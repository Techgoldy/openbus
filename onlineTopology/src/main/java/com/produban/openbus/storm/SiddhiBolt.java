package com.produban.openbus.storm;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.net.URI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.query.compiler.exception.SiddhiParserException;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.produban.openbus.siddhiCep.MediaCondicionadaAggregatorFactory;
import com.produban.openbus.siddhiCep.SumadorCondicionalConReinicioAggregatorFactory;
import com.produban.openbus.storm_ES.StormElasticSearchConstants;
import com.produban.openbus.webservice.HttpEntityEnclosingDeleteRequest;
import com.produban.openbus.webservice.MetricaOnLine;
import com.produban.openbus.webservice.QueryCep;
import com.produban.openbus.webservice.StreamCep;
import com.produban.openbus.webservice.TableCep;

public class SiddhiBolt extends BaseRichBolt {
	
	private static final long serialVersionUID = 1L;
    private static Logger LOG = Logger.getLogger(SiddhiBolt.class);

    private transient SiddhiManager siddhiManager;

    private OutputCollector collector;
   

    private Client client;

    
   // private final static String SEPARADOR = "\001";
    private Map<String,Integer> queriesVersionHistory=new HashMap<String,Integer>();
    private Map<String, Integer> metricasVersionHistory=new HashMap<String,Integer>();
    private Map<String,Integer> streamsVersionHistory=new HashMap<String,Integer>();
    private Map<String,Integer> tablesVersionHistory=new HashMap<String,Integer>();
	private String urlServicioPutMetrica;
	private String urlServicioDeleteMetrica;
	private boolean salidaElasticSearch;
    
    
    public SiddhiBolt(String urlServicioPutMetrica, String urlServicioDeleteMetrica, boolean salidaElasticSearch) {
    	init();
    	this.urlServicioDeleteMetrica=urlServicioDeleteMetrica;
    	this.urlServicioPutMetrica=urlServicioPutMetrica;
    	this.salidaElasticSearch=salidaElasticSearch;
    }
    
    @SuppressWarnings("rawtypes")
	public void init() {
    	SiddhiConfiguration conf = new SiddhiConfiguration();
    	List<Class> ext = new ArrayList<Class>();
    	ext.add(SumadorCondicionalConReinicioAggregatorFactory.class);
    	ext.add(MediaCondicionadaAggregatorFactory.class);
    	conf.setSiddhiExtensions(ext);
    	siddhiManager = new SiddhiManager(conf);
    }
    
    @SuppressWarnings("rawtypes")
	@Override	
    public void execute(Tuple tupla) {
		// TODO Auto-generated method stub
		
		
		if (siddhiManager == null) {
	    	init();
	    }
		
		//Two type of tuples might be received (MetadataTuple or DataTuple)
		if(tupla.getValueByField("tipo").equals("METADATA")){
			//Whenever a MEtric is received, Siddhi rules must be checked with the metadata in MySQL
			//Any change detected will be updated into Siddhi-CEP
			final MetricaOnLine metrica = (MetricaOnLine) tupla.getValueByField("datos");
			//We have a Metric
			//if that METRIC version has not been processed 
			boolean cambios = false;
			boolean error = false;
			if(metricasVersionHistory.get(metrica.getOnLineMetricName())== null || metricasVersionHistory.get(metrica.getOnLineMetricName())<metrica.getVersionMetadata()){
				//GET Stream

				StreamCep stream = metrica.getStreamCep();
				//if that STREAM version has not been processed before
				if(streamsVersionHistory.get(metrica.getOnLineMetricName()+"-"+stream.getStreamName())==null || streamsVersionHistory.get(metrica.getOnLineMetricName()+"-"+stream.getStreamName())<stream.getVersionMetadata()){
					LOG.debug("Recibimos el Stream "+stream.getStreamName());
					//Test if EXISTS
					Integer estadoStream=stream.getEstado().getCode();
					
					switch(estadoStream){
					case (1): //CREATE
						//El Stream no ha sido creado
						//if((stream.getStreamCepId()==null || stream.getStreamCepId().equals(""))){
							try{
								siddhiManager.defineStream(stream.getStreamFinal());
								stream.setStreamCepId(stream.getStreamName());
								stream.getEstado().setCode(0);
								stream.setError("Creación correcta del Stream");
							}catch(SiddhiParserException e){
								stream.setError(e.getMessage());
								stream.getEstado().setCode(3);
								metrica.getEstado().setCode(202);
								metrica.setError(e.getMessage());
								error=true;
							}
							stream.setVersionMetadata(stream.getVersionMetadata()+1);
							this.streamsVersionHistory.put(metrica.getOnLineMetricName()+"-"+stream.getStreamName(), stream.getVersionMetadata());
							cambios=true;
						//}
						break;
					case(4): //UPDATE
						try{
							siddhiManager.removeStream(stream.getStreamCepId());
						}catch(Exception e){
							// TODO: De momento ignoramos los borrados que fallen
						}
							try{
								siddhiManager.defineStream(stream.getStreamFinal());
								stream.setStreamCepId(stream.getStreamName());
								stream.setError("Update correcto del Stream");
								stream.getEstado().setCode(6);
							}catch(SiddhiParserException e){
								stream.setError(e.getMessage());
								stream.getEstado().setCode(7);
								metrica.getEstado().setCode(204);
								metrica.setError(e.getMessage());
								error=true;
							}
						
						stream.setVersionMetadata(stream.getVersionMetadata()+1);
						this.streamsVersionHistory.put(metrica.getOnLineMetricName()+"-"+stream.getStreamName(), stream.getVersionMetadata());
						cambios=true;
						break;
					case(5): //DELETE
						try{
							siddhiManager.removeStream(stream.getStreamCepId());
							
						}catch(Exception e){
							// TODO: De momento ignoramos los borrados que fallen
						}
						stream.setVersionMetadata(stream.getVersionMetadata()+1);
						this.streamsVersionHistory.remove(metrica.getOnLineMetricName()+"-"+stream.getStreamName());
						stream.getEstado().setCode(8);
						cambios=true;
						break;
					
					}

				}//La versión ha sido procesada
				//GET all Tables
				Set<TableCep> tables = metrica.getHsTableCep();
				if(!error){
					for(TableCep t: tables){
						if(this.tablesVersionHistory.get(metrica.getOnLineMetricName()+"-"+t.getTableCepName())==null || this.tablesVersionHistory.get(metrica.getOnLineMetricName()+"-"+t.getTableCepName())<t.getVersionMetadata()){
							Integer estadoTable=t.getEstado().getCode();
							
							switch(estadoTable){
							case (301)://CREATE
								try{
									siddhiManager.defineTable(t.getTableCepFinal());
									t.setTableCepId(t.getTableCepId());
									t.getEstado().setCode(300);
									t.setError("Creación correcta de la Tabla");
								}catch(Exception e){
									t.setError(e.getMessage());
									t.getEstado().setCode(303);
									metrica.getEstado().setCode(212);
									metrica.setError(e.getMessage());
									error=true;
								}
								cambios=true;
								t.setVersionMetadata(t.getVersionMetadata()+1);
								this.tablesVersionHistory.put(metrica.getOnLineMetricName()+"-"+t.getTableCepName(), t.getVersionMetadata());
								break;
							case (304): //UPDATE
								try{
									siddhiManager.removeTable(t.getTableCepName());
									siddhiManager.removeStream(t.getTableCepName());
								}catch(Exception e){
									// TODO: A priori ignoramos los fallos de borrado
								}
								try{
									siddhiManager.defineTable(t.getTableCepFinal());
									t.setTableCepId(t.getTableCepId());
									t.getEstado().setCode(306);
									t.setError("Actualización correcta de la Tabla");
								}catch(Exception e){
									t.setError(e.getMessage());
									t.getEstado().setCode(307);
									metrica.getEstado().setCode(213);
									metrica.setError(e.getMessage());
									error=true;
								}
								cambios=true;
								t.setVersionMetadata(t.getVersionMetadata()+1);
								this.tablesVersionHistory.put(metrica.getOnLineMetricName()+"-"+t.getTableCepName(), t.getVersionMetadata());
								break;
							case(305): //DELETE
								try{
									siddhiManager.removeTable(t.getTableCepName());
								}catch(Exception e){
									// TODO: A priori ignoramos los fallos de borrado
								}
								cambios=true;
								t.getEstado().setCode(308);
								t.setError("Borrado correcto de la Tabla");
								t.setVersionMetadata(t.getVersionMetadata()+1);
								this.tablesVersionHistory.remove(metrica.getOnLineMetricName()+"-"+t.getTableCepName());
								break;
							}
						}
						
					}
				}
				//GET all Queries
				Set<QueryCep> queries = metrica.getHsQueryCep();
				//ordenamos las queries
				Set<QueryCep> queriesSorted = new TreeSet<QueryCep>(queries);
				
				for(final QueryCep query: queriesSorted){
					//if that QUERY version has not been processed before
					if(!error && (this.queriesVersionHistory.get(metrica.getOnLineMetricName()+"-"+query.getQueryName())==null || this.queriesVersionHistory.get(metrica.getOnLineMetricName()+"-"+query.getQueryName())<query.getVersionMetadata())){
						Integer estadoQuery=query.getEstado().getCode();
						
						switch(estadoQuery){
						case(101): //CREATE
							try{
								String idQuery = siddhiManager.addQuery(query.getQueryFinal());
								query.setQueryCepId(idQuery);
								//Crear el _mapping de salida a ElasticSearch para asegurar el tipo de cada campo
								if(query.getHasCallback() && salidaElasticSearch){
								try {
									creaEsMapping(metrica.getEsIndex(),query.getEsType(),query);
									//query.setError("Query creada con éxito");
									try{
										//LLamada a la generación del CALLBACK
										//Generamos el CALLBACK de SIddhi para el ElasticSearchBolt
										siddhiManager.addCallback(idQuery, new QueryCallback() {
								            @Override
								            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
								            	
								                //generar la emisión de la tupla
								                /*generar un formato común de sailda a todas las reglas 
								                para pasar al Bolt de escrituraa ElasticSearch */

								                XContentBuilder dataBuilder;
								                String valorIdEs = "";
								                //Mapeamos los campos
								                String[] campos =query.getOutputFieldNames().split(",");
												String[] formatos =query.getOutputFieldFormat().split(",");
												//Obtenemos los campos para el ID
												String[] camposIdEs = query.getEsId().split(",");
								                Map<String,Boolean> mapCamposIdEs = new HashMap<String,Boolean>();
						                		if(camposIdEs!=null && camposIdEs.length>0){					                		
							                		for(String campo: camposIdEs){
														mapCamposIdEs.put(campo.trim().toUpperCase(), true);
													}
						                		}
								                if(inEvents!=null){
									                for(Event e:inEvents){
									                	try {
									                		valorIdEs="";									                		
									                		dataBuilder=jsonBuilder().startObject();
															
															for(int i =0;i<campos.length;i++){
																if(formatos[i].trim().equals("geo_point")){
																	String [] coords = ((String)e.getData(i)).split(",");
																	dataBuilder.array(campos[i], Float.parseFloat(coords[0].trim()),Float.parseFloat(coords[1].trim()));
																}else{
																	dataBuilder.field(campos[i], e.getData(i));
																	if(mapCamposIdEs.get(campos[i].trim().toUpperCase())!=null){
																		valorIdEs+=e.getData(i)+"-";
																	}
																}
															}
															dataBuilder.endObject();
															String docum=dataBuilder.string();
															LOG.debug("gg+ indice: "+valorIdEs+ " doc: "+docum);
										                	collector.emit(new Values(metrica.getEsIndex(),query.getEsType(),valorIdEs,docum));
										                	collector.emit("echo", new Values(metrica.getEsIndex(),query.getEsType(),valorIdEs,docum));
														} catch (IOException e1) {
															// TODO Auto-generated catch block
															e1.printStackTrace();
														}
									                	
										            }
								                }
								                if(removeEvents!=null){
									                for(Event e:removeEvents){

									                	try {
									                		valorIdEs="";
									                		dataBuilder=jsonBuilder().startObject();
															
															for(int i =0;i<campos.length;i++){
																if(formatos[i].trim().equals("geo_point")){
																	String [] coords = ((String)e.getData(i)).split(",");
																	dataBuilder.array(campos[i], Float.parseFloat(coords[0].trim()),Float.parseFloat(coords[1].trim()));
																}else{
																	dataBuilder.field(campos[i], e.getData(i));
																	if(mapCamposIdEs.get(campos[i].trim().toUpperCase())!=null){
																		valorIdEs+=e.getData(i)+"-";
																	}
																}
															}
															dataBuilder.endObject();
															String docum=dataBuilder.string();
															LOG.debug("gg- indice: "+valorIdEs+ " doc: "+docum);
										                	collector.emit(new Values(metrica.getEsIndex(),query.getEsType(),valorIdEs,docum));
										                	collector.emit("echo", new Values(metrica.getEsIndex(),query.getEsType(),valorIdEs,docum));
									                	} catch (IOException e1) {
															// TODO Auto-generated catch block
															e1.printStackTrace();
														}
									                }
								                }
								            }
								        });
									}catch(Exception e3){ //Error en el callback
										query.setError(e3.getMessage());
										query.getEstado().setCode(104);
										metrica.getEstado().setCode(203);
										metrica.setError(e3.getMessage());
										error=true;
									}
									query.getEstado().setCode(100);
								} catch (Exception e2) {
									// TODO Auto-generated catch block
									LOG.warn("Error al crear el mapping definido");
									query.setError(e2.getMessage());
									query.getEstado().setCode(109);
									metrica.getEstado().setCode(203);
									metrica.setError(e2.getMessage());
									error=true;
								}
								}else{
									query.getEstado().setCode(100);
								}
							}catch(SiddhiParserException e){
								query.setError(e.getMessage());
								query.getEstado().setCode(103);
								metrica.getEstado().setCode(203);
								metrica.setError(e.getMessage());
								error=true;
							}
							query.setVersionMetadata(query.getVersionMetadata()+1);
							this.queriesVersionHistory.put(metrica.getOnLineMetricName()+"-"+query.getQueryName(), query.getVersionMetadata());
							cambios=true;
							break;
						case(105): //UPDATE
							//BORRADO
							try{
								siddhiManager.removeQuery(query.getQueryCepId());
								//siddhiManager.removeStream(query.outputStreramName());
							}catch(Exception e){
								// TODO: De momento los fallos en borrado los damos por buenos.
								query.setError("La query fue borrada o el ID indicado no es correcto");
							}
							//CREACIÓN
							try{
								String idQuery = siddhiManager.addQuery(query.getQueryFinal());
								query.setQueryCepId(idQuery);
								if(query.getHasCallback() && salidaElasticSearch){
								//Crear el _mapping de salida a ElasticSearch para asegurar el tipo de cada campo
								try {
									creaEsMapping(metrica.getEsIndex(),query.getEsType(),query);
									stream.setError("Query creada con éxito");
									try{
										//LLamada a la generación del CALLBACK
										//Generamos el CALLBACK de SIddhi para el ElasticSearchBolt
										siddhiManager.addCallback(idQuery, new QueryCallback() {
								            @Override
								            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
								            	
								                //generar la emisión de la tupla
								                /*generar un formato común de sailda a todas las reglas 
								                para pasar al Bolt de escrituraa ElasticSearch */
	
								                XContentBuilder dataBuilder;
								                String valorIdEs = "";
								                //Mapeamos los campos
								                String[] campos =query.getOutputFieldNames().split(",");
												String[] formatos =query.getOutputFieldFormat().split(",");
												//Obtenemos los campos para el ID
												String[] camposIdEs = query.getEsId().split(",");
								                Map<String,Boolean> mapCamposIdEs = new HashMap<String,Boolean>();
						                		if(camposIdEs!=null && camposIdEs.length>0){					                		
							                		for(String campo: camposIdEs){
														mapCamposIdEs.put(campo.trim().toUpperCase(), true);
													}
						                		}
								                if(inEvents!=null){
									                for(Event e:inEvents){
									                	try {
									                		valorIdEs="";									                		
									                		dataBuilder=jsonBuilder().startObject();
															
															for(int i =0;i<campos.length;i++){
																if(formatos[i].trim().equals("geo_point")){
																	String [] coords = ((String)e.getData(i)).split(",");
																	dataBuilder.array(campos[i], Float.parseFloat(coords[0].trim()),Float.parseFloat(coords[1].trim()));
																}else{
																	dataBuilder.field(campos[i], e.getData(i));
																	if(mapCamposIdEs.get(campos[i].trim().toUpperCase())!=null){
																		valorIdEs+=e.getData(i)+"-";
																	}
																}
															}
															dataBuilder.endObject();
															String docum=dataBuilder.string();
															LOG.debug("gg+ indice: "+valorIdEs+ " doc: "+docum);
										                	collector.emit(new Values(metrica.getEsIndex(),query.getEsType(),valorIdEs,docum));
										                	collector.emit("echo", new Values(metrica.getEsIndex(),query.getEsType(),valorIdEs,docum));
														} catch (IOException e1) {
															// TODO Auto-generated catch block
															e1.printStackTrace();
														}
									                	
										            }
								                }
								                if(removeEvents!=null){
									                for(Event e:removeEvents){
	
									                	try {
									                		valorIdEs="";
									                		dataBuilder=jsonBuilder().startObject();
															
															for(int i =0;i<campos.length;i++){
																if(formatos[i].trim().equals("geo_point")){
																	String [] coords = ((String)e.getData(i)).split(",");
																	dataBuilder.array(campos[i], Float.parseFloat(coords[0].trim()),Float.parseFloat(coords[1].trim()));
																}else{
																	dataBuilder.field(campos[i], e.getData(i));
																	if(mapCamposIdEs.get(campos[i].trim().toUpperCase())!=null){
																		valorIdEs+=e.getData(i)+"-";
																	}
																}
															}
															dataBuilder.endObject();
															String docum=dataBuilder.string();
															LOG.debug("gg- indice: "+valorIdEs+ " doc: "+docum);
										                	collector.emit(new Values(metrica.getEsIndex(),query.getEsType(),valorIdEs,docum));
										                	collector.emit("echo", new Values(metrica.getEsIndex(),query.getEsType(),valorIdEs,docum));
									                	} catch (IOException e1) {
															// TODO Auto-generated catch block
															e1.printStackTrace();
														}
									                }
								                }
								            }
								        });
									}catch(Exception e3){ //Error en el callback
										query.setError(e3.getMessage());
										query.getEstado().setCode(114);
										metrica.getEstado().setCode(205);
										metrica.setError(e3.getMessage());
										error=true;
									}
									query.getEstado().setCode(107);
								} catch (Exception e2) {
									// TODO Auto-generated catch block
									LOG.warn("Error al crear el mapping definido");
									query.setError(e2.getMessage());
									query.getEstado().setCode(119);
									metrica.getEstado().setCode(205);
									metrica.setError(e2.getMessage());
									error=true;
								}
								}else{
									query.getEstado().setCode(100);
								}
							}catch(Exception e){
								query.setError(e.getMessage());
								query.getEstado().setCode(113);
								metrica.getEstado().setCode(205);
								metrica.setError(e.getMessage());
								error=true;
							}
							query.setVersionMetadata(query.getVersionMetadata()+1);
							this.queriesVersionHistory.put(metrica.getOnLineMetricName()+"-"+query.getQueryName(), query.getVersionMetadata());
							cambios=true;
							break;
						case(106): //DELETE
							try{
								siddhiManager.removeQuery(query.getQueryCepId());
								//siddhiManager.removeStream(query.outputStreramName());
								query.setError("Borrado correcto de la query");
							}catch(Exception e){
								// TODO: De momento los fallos en borrado los damos por buenos.
								//query.setError("La query fue borrada o el ID indicado no es correcto");
							}
							query.getEstado().setCode(108);
							query.setVersionMetadata(query.getVersionMetadata()+1);
							this.queriesVersionHistory.remove(metrica.getOnLineMetricName()+"-"+query.getQueryName());
							cambios=true;
							break;
						
						}
					}
				}
				
				if(cambios){
					//UPDATE/CREATE o DELETE de la metrica según corresponda

					Integer estadoMetrica = metrica.getEstado().getCode();
					metrica.setVersionMetadata(metrica.getVersionMetadata()+1);
					metricasVersionHistory.put(metrica.getOnLineMetricName(), metrica.getVersionMetadata());
					
					switch(estadoMetrica){
					//si los códigos no han cambiado, no ha habido erroeres
					
						case(201): //CREATE
							metrica.getEstado().setCode(200);
							//LLAMADA AL SERVICIO REST --> PUT
							sendRESTService("PUT",metrica);
							break;
						/*case(210)*/default: //UPDATE y/o fallos
							if(metrica.getEstado().getCode()==210)
								metrica.getEstado().setCode(208);
							//LLAMADA AL SERVICIO REST --> PUT
							sendRESTService("PUT",metrica);
							break;
						case(211): //DELETE
							metrica.getEstado().setCode(209);
							//LLAMADA AL SERVICIO REST --> DELETE
							sendRESTService("DELETE",metrica);
							metricasVersionHistory.remove(metrica.getOnLineMetricName());
							break;
					}	
					
				}// FIN de la deteccion de cambios en la métrica				
			}//La métrica ha sido procesada
			
		}//Tupla de datos
		else{
			//Received format is("String[] fields") where [0] is the Stream and the rest are the fields
			List datos = (List) tupla.getValueByField("datos");
			String streamID=(String) datos.get(0);

			List valores = (List) datos.get(1);
			LOG.debug("SIDDHIBOLT - Sacamos la tupla por el stream: "+streamID);
			InputHandler inputHandler = siddhiManager.getInputHandler(streamID);

			if (inputHandler != null) {
			    try {
					inputHandler.send(valores.toArray());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
			    throw new RuntimeException("Input handler for stream " + streamID + " not found");
			}
		}
		collector.ack(tupla);
	}

    

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// Fields needed to insert data into Elasticsearch
		declarer.declare(new Fields("index","type","id","document"));
		declarer.declareStream("echo",new Fields("index","type","id","document"));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector=collector;
		
		 String elasticSearchHost = (String) stormConf.get(StormElasticSearchConstants.ES_HOST);
	     Integer elasticSearchPort = ((Long) stormConf.get(StormElasticSearchConstants.ES_PORT)).intValue();
	     String elasticSearchCluster = (String) stormConf.get(StormElasticSearchConstants.ES_CLUSTER_NAME);
	        
	     Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", elasticSearchCluster).build();
         client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(elasticSearchHost, elasticSearchPort));
	}
	
	private void creaEsMapping(String index, String type,QueryCep query) throws Exception{
		//Preparamos los campos geo_point y timestamp

		Map<String,String> mapCamposFormato = new HashMap<String,String>();
		String[] camposNombre = query.getOutputFieldNames().split(",");
		String[] camposFormato = query.getOutputFieldFormat().split(",");
		String lastField="";
		if(camposNombre.length==camposFormato.length){
			for(int i = 0;i<camposNombre.length;i++){
				String []part =camposFormato[i].trim().replace('\n',' ').replace('\t',' ').replace("  ", " ").replace('\n',' ').split(" "); 
				mapCamposFormato.put(part[0].trim().toUpperCase(),part[1].trim());
			}
		}else{
			//Lanzar excepcion (Creaarla)
			throw new Exception("ERROR en QUERY: "+query.getQueryName()+"\\nLa cantidad de Campos de la query y de la salida a ElasticSearch no coinciden");
		}
		LOG.debug("Formatos Obtenidos");
		//Generamos el JSON del Mapping
		 XContentBuilder mappingBuilder;

         mappingBuilder = jsonBuilder().startObject()
					.startObject(type)
					.startObject("properties");
         
         //por cada campo, lo añadimos con su formato
         for(int i = 0;i<camposNombre.length;i++){
        	  mappingBuilder.startObject(camposNombre[i].trim())
        	  	.field("type",mapCamposFormato.get(camposNombre[i].trim().toUpperCase()));
        	  if(mapCamposFormato.get(camposNombre[i].trim().toUpperCase())==null){
      			throw new Exception("ERROR en QUERY: "+query.getQueryName()+".\nEl campo: \""+camposNombre[i].trim()+"\" definido en \"QUERY AS\" no coincide con los definidos en \"FORMATO ES\" .");
        	  }
        	  
        	  if(mapCamposFormato.get(camposNombre[i].trim().toUpperCase()).toLowerCase().equals("string")){
        		  mappingBuilder.field("index", "not_analyzed");
        	  }
        	  if(mapCamposFormato.get(camposNombre[i].trim().toUpperCase()).toLowerCase().equals("date")){
        		  mappingBuilder.field("format","yyyy-MM-dd HH:mm:ss");
        	  }
        	  mappingBuilder.endObject();
         }
         
         
         mappingBuilder.endObject();  
         //Añadir el TTL si está indicado, por defecto ponemos 60s
         String ttl=query.getEsTTL();
         if(ttl!= null && !ttl.equals("")){
        	 mappingBuilder.startObject("_ttl").field("enabled", "true").field("default", ttl).endObject();
         }else{
        	 mappingBuilder.startObject("_ttl").field("enabled", "true").field("default", "60s").endObject();
         }
         mappingBuilder.endObject(); 
         LOG.info(mappingBuilder.string());
		//Comprobar si existe el indice
		IndicesExistsResponse resp = this.client.admin().indices().prepareExists(index).execute().actionGet();
		if(resp.isExists()){
			//Comprobar si existe el type
			TypesExistsResponse existeType = this.client.admin().indices().prepareTypesExists(index).setTypes(type).execute().actionGet();
			
			if(existeType.isExists()){
				//Si existe el type, lo borramos
				this.client.admin().indices().prepareDeleteMapping(index).setType(type).execute().actionGet();
			}
			//Crear el nuevo mapping
			this.client.admin().indices().preparePutMapping(index).setType(type).setSource(mappingBuilder).execute().actionGet();
		}else{
			//Crear e mapping desde cero con el index
			client.admin().indices().prepareCreate(index).addMapping(type, mappingBuilder).execute().actionGet();
		}
		
		
		
	}
	
	private void sendRESTService(String tipo,MetricaOnLine metrica){
		 try {
			 if(tipo.equals("PUT")){
		        RestTemplate restTemplate = new RestTemplate();
		        restTemplate.put(this.urlServicioPutMetrica, metrica, MetricaOnLine.class);  
			 }
			 if(tipo.equals("DELETE")){
				 RestTemplate restTemplate = new RestTemplate();
				 restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory() {
				        @Override
				        protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
				            if (HttpMethod.DELETE == httpMethod) {
				                return new HttpEntityEnclosingDeleteRequest(uri);
				            }
				            return super.createHttpUriRequest(httpMethod, uri);
				        }
				    });
				 	
			    ResponseEntity<MetricaOnLine> exchange = restTemplate.exchange(
			            this.urlServicioDeleteMetrica,
			            HttpMethod.DELETE,
			            new HttpEntity<MetricaOnLine>(metrica),
			            MetricaOnLine.class);
			  
			 }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	

}
