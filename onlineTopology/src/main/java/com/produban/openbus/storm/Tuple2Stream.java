package com.produban.openbus.storm;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;




import com.produban.openbus.webservice.CamposOrigen;
import com.produban.openbus.webservice.MetricaOnLine;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;


public class Tuple2Stream extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<MetricaOnLine> totalMetricas=null;
	private Map <String,ArrayList<MetricaOnLine>> metricasPorOrigen=new HashMap<String,ArrayList<MetricaOnLine>>();
	private Map<String,Integer> posicionCampos=new HashMap<String,Integer>();
	private String SEPARADOR = "\001";
	private int tickFrequencyInSecs=5;
	private String urlServicioGetMetadata;
	public String metadataJson ;
	private boolean firstMetadataSinc;
	private OutputCollector collector;
	private static Logger LOG =Logger.getLogger(Tuple2Stream.class);
	
	public Tuple2Stream(int metadataSincroSecs,String urlServicioGetMetadata,String metadataJson){
		tickFrequencyInSecs=metadataSincroSecs;
		this.urlServicioGetMetadata=urlServicioGetMetadata;
		this.metadataJson=metadataJson;
		init();
	}
	
	public void init() {
    	//inicializar la lectura de metadata
		actualizaMetadata();
		firstMetadataSinc=true;
    }
	
	@Override
    public Map<String, Object> getComponentConfiguration() {
		Config conf = new Config();
		if(tickFrequencyInSecs>0){
			conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, tickFrequencyInSecs);
		}
		return conf;
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(Tuple tupla) {
		// TODO Auto-generated method stub
		if(firstMetadataSinc){
			sendMetricTuples();
			firstMetadataSinc=false;
		}
		
		if(isTickTuple(tupla)){
			//Sincronizamos la metadata con el servicio REST
			actualizaMetadata();
			sendMetricTuples();
		}else{
			LOG.debug("Registro de datos");
			//Es un registro de datos
			if(this.metricasPorOrigen !=null && !this.metricasPorOrigen.isEmpty()){
				//obtenemos el origen de la tupla
				String origen = null;
				String[] camposOrigen= null;
				
				List<Object> objetos =tupla.getValues();		

				if (objetos.get(0) instanceof String){
					camposOrigen=tupla.getString(0).split(SEPARADOR);
				}else if(objetos.get(0) instanceof byte[]){
					camposOrigen = (new String((byte[]) tupla.getValues().get(0)).split(SEPARADOR));
				}
				if(camposOrigen!= null && camposOrigen.length>2){
					origen=camposOrigen[0];
					MetricaOnLine metrica = null;
					List<MetricaOnLine> metricList = this.metricasPorOrigen.get(origen);
					if(metricList!=null){
						LOG.debug("Hay una o más de una métrica para el origen");
						Iterator<MetricaOnLine> it = metricList.iterator();
						//por cada métrica asociada al origen, generamos un a tupla con la estructura necesaria
						while(it.hasNext()){
							metrica=it.next();
							String streamID=metrica.getStreamCep().getStreamName();
							LOG.debug("Salida al stream: "+streamID);
							String[] camposMetrica = metrica.getStreamCep().getStreamFields().split(",");
							//Tamaño para el stream + todos los campos
					 		List valoresEnvio = new ArrayList();
					 		List datos = new ArrayList();
					 		List salida = new ArrayList<String>();
					 		datos.add(streamID);
					 		
				 		    for(int i=0;i<camposMetrica.length;i++){
				 		    	//obtenemos el tipo de dato para darle tratamiento adecuado
				 		    	String formato=camposMetrica[i].replace("\t", " ").trim().toUpperCase().split(" ")[1];
				 			   camposMetrica[i]=camposMetrica[i].replace("\t", " ").trim().toUpperCase().split(" ")[0];
				 			   //los campos en la METADATA tienen que empezar por 1!!!! sino añadimos un +1
				 			  String valor=camposOrigen[posicionCampos.get(origen+"-"+camposMetrica[i])];
				 			  
				 			  if ((formato.toUpperCase().equals("INT") ||
				 				  formato.toUpperCase().equals("LONG") ||
				 				  formato.toUpperCase().equals("FLOAT") ||
				 				 formato.toUpperCase().equals("DOUBLE")) && valor.toUpperCase().equals("NULL") ){
				 				  valor="0";
				 			  }
				 			  
				 			  if(formato.toUpperCase().equals("INT")) {
				 				 valoresEnvio.add(Integer.parseInt(valor));
				 			  }else if(formato.toUpperCase().equals("LONG")){
					 				 valoresEnvio.add(Long.parseLong(valor));
				 			  }else if(formato.toUpperCase().equals("FLOAT")){
					 				 valoresEnvio.add(Float.parseFloat(valor));
				 			  }else{
				 				  //como String
				 				  valoresEnvio.add(valor);
				 			  }
				 			  
				 			  //si esto da error será porque el usuario pone un campo que no existe

				 		    }
				 		    datos.add(valoresEnvio);
				 		    salida.add("DATOS");
				 		    salida.add(datos);
				 		    collector.emit(salida);		
						}   
					}
				}
			}
			collector.ack(tupla);
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		
		declarer.declare(new Fields("tipo","datos"));
		//Si el tipo es METADATA entonces datos es un objeto tipo MetricaOnLine
		//Si el tipo es DATOS entnces datos es un objeto tipo List[] con todos los campos;
	}
	
	private void actualizaMetadata(){
		
		try {
			 String json="";
			if(this.urlServicioGetMetadata!=null ){
				HttpClient httpClient = new DefaultHttpClient();
			       HttpGet requestGet = new HttpGet(this.urlServicioGetMetadata);
			       HttpResponse response;
			       metricasPorOrigen = new HashMap<String, ArrayList<MetricaOnLine>>();;
			       
			          response = httpClient.execute(requestGet);
			           HttpEntity entity = response.getEntity();
			           json = EntityUtils.toString(entity);
			}else{
				BufferedReader entrada;
			    entrada = new BufferedReader(new FileReader(this.metadataJson));
	            String linea=entrada.readLine();
	            while(linea!=null){
	            	json+=linea;
	            	linea=entrada.readLine();
	            }
	            entrada.close();
			}
		   
	    	   ObjectMapper mapper = new ObjectMapper();
	           this.totalMetricas = mapper.readValue(json,mapper.getTypeFactory().constructCollectionType(List.class, MetricaOnLine.class));
	           
	           Iterator<MetricaOnLine> it = this.totalMetricas.iterator();
	           ArrayList<MetricaOnLine> lista; 
	           //Clasificamos las métricas por origen para recorrerlas de manera más eficiente
	           MetricaOnLine metrica;
	           while(it.hasNext()){
	        	   metrica = it.next();
	        	   //organizamos los campos por origen para agilizar las búsquedas
	        	   String origen = metrica.getStreamCep().getOrigenEstructurado().getTopologyName();

	        	   if(metrica.getStreamCep().getOrigenEstructurado().getHsCamposOrigen()!=null && !metrica.getStreamCep().getOrigenEstructurado().getHsCamposOrigen().isEmpty()){
		       			Iterator<CamposOrigen> itCampo = metrica.getStreamCep().getOrigenEstructurado().getHsCamposOrigen().iterator();
		       			while(itCampo.hasNext()){
		       				CamposOrigen campo = itCampo.next();
		       				posicionCampos.put(origen+"-"+campo.getNombreCampo().toUpperCase(),campo.getOrdenEnTabla().intValue());
		       			}
		       		}
	        	   //si la lista de métricas no existe, la creamos a vacía
	        	   lista = metricasPorOrigen.get(origen);
	        	   if(lista==null){
	        		   lista= new ArrayList<MetricaOnLine>();
	        	   }
	        	   //insertamos la métrica para el origende la iteración
	        	   lista.add(metrica);
	        	   metricasPorOrigen.put(origen, lista);
	           }
	       } catch (Exception e) {
	           //e.printStackTrace();
	    	   LOG.error("Error al acceder a la metadata");
	    	   //NOTA: Mostrar mensaje de error y continuar con la lista como estaba antes de la llamada
	       } 
	}
	
	private void sendMetricTuples(){
		
		Iterator<MetricaOnLine> it = this.totalMetricas.iterator();
		MetricaOnLine met ;
		while(it.hasNext()){
			met = it.next();
			List<Object> tupla = new ArrayList<Object>();
			tupla.add("METADATA");
			tupla.add(met);
			collector.emit(tupla);
		}
	}
	
	
	 private static boolean isTickTuple(Tuple tuple) {
			return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
				&& tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
	 }

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector=collector;
	}
}
