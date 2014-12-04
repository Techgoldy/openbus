import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.produban.openbus.siddhiCep.MediaCondicionadaAggregatorFactory;
import com.produban.openbus.siddhiCep.MediaCondicionadaAnteriorAggregatorFactory;
import com.produban.openbus.siddhiCep.SumadorCondicionalConReinicioAggregatorFactory;


public class SiddhiBoltMetricaOnlineRadius4 extends BaseBasicBolt  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 296651450802350256L;
	private transient SiddhiManager siddhiManager;
	private BasicOutputCollector collector;
    
    @SuppressWarnings("rawtypes")
	public void init(){
          //SiddhiConfiguration configuration = new SiddhiConfiguration();
    	SiddhiConfiguration conf = new SiddhiConfiguration();
    	List<Class> ext = new ArrayList<Class>();
    	ext.add(SumadorCondicionalConReinicioAggregatorFactory.class);
    	ext.add(MediaCondicionadaAggregatorFactory.class);
    	ext.add(MediaCondicionadaAnteriorAggregatorFactory.class);
    	conf.setSiddhiExtensions(ext);
     siddhiManager = new SiddhiManager(conf);
     
     
   /*  gestionaStream("define stream streamPostfix (MSGID string, USERFROM string, TOUSER string,DSN string, SIZE int);","insert","");
     
     String query1ID=gestionaQuery(
    		 "from streamPostfix[DSN=='2.0.0' or DSN=='2.6.0' or DSN=='2.4.0']#window.time(20000) as resp "+
    		 "join "+
    		 "streamPostfix[ USERFROM != 'null' and SIZE >0 ]#window.time(20000) as tam "+
    		 "on resp.MSGID==tam.MSGID "+
    		 "insert all-events into respuestasOK "+
    		 "tam.MSGID,resp.DSN,sum(tam.SIZE) as TAMANO group by tam.MSGID,tam.DSN;"
    		 , "insert", "", true);*/
    
     gestionaStream("define stream radius2 (Authentication_Status string,timestamp string, User_Name string);","insert","");
     
   
     gestionaQuery(
    		 "from radius2[not(User_Name contains 'host/')]#window.time(20000) "+
    		 "select timestamp,agregacion:mediaCondIntPorcentaje(Authentication_Status,'Failed',10000) as media, agregacion:mediaCondAnteriorIntPorcentaje(Authentication_Status,'Failed',10000) as mediaAnterior " +
    		 "insert into mediaFallos for all-events "+
    		 ";"
    		 , "insert", "", false);
     
     gestionaQuery(
    		 "from mediaFallos#window.time(20000) "+
    		 "select timestamp,media, media - mediaAnterior as incrementoMedia " +
    		 "insert into mediaIncremento for all-events "+
    		 ";"
    		 , "insert", "", true);
    
  
    }
    

    public SiddhiBoltMetricaOnlineRadius4(){
          init();
    }


    @Override
    public void execute(Tuple arg0, BasicOutputCollector arg1) {
          // TODO Auto-generated method stub
    	collector=arg1;
          String[] campos=arg0.getString(0).split("¬");

          if(siddhiManager==null){
                 init();
          }
          //recuperamos el Stream para pasarle la tupla
          InputHandler inputHandler = siddhiManager.getInputHandler("radius2");
          //Adaptamos los campos al formato definido por el stream
          
           try {
                       //Pasamos los valores relevantes de latupla al Stream de  WSO2
        	   if (campos[32]==null) campos[32]="";
               inputHandler.send(new Object[]{campos[32],campos[3],campos[10]});
          } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                 }
        /*  try {
                 Thread.sleep(1000);
          } catch (InterruptedException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
          }*/
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
          // TODO Auto-generated method stub
    	//declarer.declare(new Fields("streamID", "datos"));
    	declarer.declare(new Fields("index","type","id","document"));
    }
    
    @Override
	public void prepare(Map stormConf, TopologyContext context) {
		super.prepare(stormConf, context);
	}
    
    /**
	 * <p>Método para añadir o eliminar Streams del Siddhi-CEP</p>
	 * @param streamDefinition Definición del STREAM a crear/eliminar
	 * @param opcion Valores:
	 *<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- insert: Crea el Stream indicado</p>
	 *<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- delete: Elimina el Stream indicado</p>
	 * @param streamID Nombre del STREAM si ha si ha sido creado. Si streamID no está informado, se permite la creación.
	 * Si streamID está informado se permite el borrado.
	 * 
	 */
	public void gestionaStream(String streamDefinition, String opcion,String streamID){
		
		if(streamID!=null ){
			if(opcion.equals("insert")){
				//creamos
				siddhiManager.defineStream(streamDefinition);
			}
		}else{
			System.out.println("El STREAM: "+streamID+" no existe");
			if(opcion.equals("delete")){
				//eliminamos
				siddhiManager.removeStream(streamID);
			}
		}
	}
	
	/**
	 * <p>Método para añadir o eliminar Queries del Siddhi-CEP</p>
	 * <p>En la creación/actualización además se generarán las funciones callback.
	 * </p>
	 * @param queryDef Nombre de la QUERY a crear/eliminar
	 * @param opcion Valores:
	 *<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- insert: Crea la QUERY indicada</p>
	 *<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- delete: Elimina la QUERY indicada</p>
	 * @param queryID Identificador de la query insertada en el Siddhi-CEP. 
	 * Si el ID no está especificado solo se permite la inserción.
	 * Si está especificada permitimos el borrado y la inserción pasa a ser una actualización.
	 * @param callback 
	 * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- true: Genera la salida de los eventos al siguiente BOLT</p>
	 * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- false: No se genera callback</p>
	 */
	public String gestionaQuery(String queryDef, String opcion,String queryID,boolean callback){
		
		String queryReference=queryID;
		if(opcion.equals("delete")){
			
			//borramos la query del ID especificado
			siddhiManager.removeQuery(queryID);
			/*¿Como tratamos las excepciones quepuedan llegar?
				- QueryID nulo
				- QueryID inválido
			*/
		}
		else if(opcion.equals("insert")){
			if(queryID!=null){
				//queryAntigua=siddhiManager.getQuery(queryID);
				//Si existe el ID y es válido, borramos la regla
				//if (queryAntigua!=null){
					siddhiManager.removeQuery(queryID);
					/*¿Como tratamos las excepciones quepuedan llegar?
					- QueryID nulo
					- QueryID inválido
					 */
				//}
			}
			//añadimos la nueva Query
			 queryReference=siddhiManager.addQuery(queryDef);
			
			/*¿Como tratamos las excepciones quepuedan llegar?
				- queryDef nulo
				- queryDef inválido
			 */
			
			//actualizamos en la metadatael queryID devuelto por la creación
			
			//Generamos callback si es necesario
			if(callback){
				siddhiManager.addCallback(queryReference, new QueryCallback() {
		            @Override
		            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
		            	
		               // EventPrinter.print(timeStamp, inEvents, removeEvents);
		                //generar la emisión de la tupla
		                /*habría que generarun formato común de sailda a todas las reglas 
		                para pasar al Bolt de escrituraa ElasticSearch */
		                
		                if(inEvents!=null){
			                for(Event e:inEvents){
			                	System.out.println("++"+Arrays.toString(e.getData()));
			                	//collector.emit(new Values(e.getStreamId(), Arrays.toString(e.getData())));
			                	String id=e.getData(0).toString();
			                				                	
			                	try {
									XContentBuilder dataBuilder=jsonBuilder().startObject()
											.field("@timestamp",e.getData(0).toString())
											.field("media",((int)e.getData(1)))
											.field("incrementoMedia",(e.getData(2)))
									.endObject();
									
				                	collector.emit(new Values("ob_src_postfix","media",id,dataBuilder.string()));
				                	
								} catch (NumberFormatException | IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
			                	
				                }
		                }
		                if(removeEvents!=null){
			                for(Event e:removeEvents){
			                	System.out.println("--"+Arrays.toString(e.getData()));
			                	//collector.emit(new Values(e.getStreamId(), Arrays.toString(e.getData())));
			                	//----collector.emit(new Values(e.getStreamId(), e.getData()));
			                	//para la poc
			     
			                	/*String id=e.getData(0).toString()+"-"+e.getData(1).toString();
			                	String docum="{\"MSGID\":\""+e.getData(0).toString()+"\","+
			                				  "\"DSN\":\""+e.getData(1)+"\","+
			                				  "\"tamano\":"+Long.parseLong(e.getData(2).toString())+ "}";
			                	System.out.println("gg");
			                	collector.emit(new Values("indiceprueba","typeprueba",id,docum,e.getStreamId(), e.getData()));
				                */
			                }
		                }
		            }
		        });
			}
		}
		return queryReference;
	}
		

}
