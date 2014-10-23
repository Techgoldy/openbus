


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
import org.wso2.siddhi.query.api.extension.annotation.SiddhiExtension;
import org.wso2.siddhi.query.api.query.Query;

import com.produban.openbus.siddhiCep.SumadorCondicionalConReinicioAggregatorFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;


public class SiddhiBoltMetricaOnlineRadius1 extends BaseBasicBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = 296651450802350256L;
	private transient SiddhiManager siddhiManager;
	private BasicOutputCollector collector;
	private String q;
	private int cont=1;
    
    @SuppressWarnings("rawtypes")
	public void init(){
          //SiddhiConfiguration configuration = new SiddhiConfiguration();
    	SiddhiConfiguration conf = new SiddhiConfiguration();
    	List<Class> ext = new ArrayList<Class>();
    	ext.add(SumadorCondicionalConReinicioAggregatorFactory.class);
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
    
     gestionaStream("define stream radius (ACS_Timestamp string,ID int,User_Name string,Calling_Station_ID string,Authentication_Status string,Failure_Reason string);","insert","");
     
     siddhiManager.defineTable("define table hosts (ID int, maquina string, mac string, cuenta long);");
   
     gestionaQuery(
    		 "from radius[(User_Name contains 'host/')] "+
    		 "select ID,User_Name as maquina, Calling_Station_ID as mac, count(1) as cuenta "+
    		 "insert into hosts for current-events "+
    		 ";"
    		 , "insert", "", false);
     
     gestionaQuery(
    		 "from radius[(User_Name contains 'host/')] "+
    		 "join hosts as hosts "+
    		 "on radius.Calling_Station_ID == hosts.mac and hosts.ID<radius.ID " +
    		 "select radius.ID,radius.User_Name as maquina, radius.Calling_Station_ID as mac, count(1) as cuenta "+
    		 "delete hosts for current-events "+
    		 ";"
    		 , "insert", "", false);
     
     
     gestionaQuery(
    		 "from radius[not(User_Name contains 'host/')] "+ 
    		 "select ACS_Timestamp,ID,User_Name as usuario, Calling_Station_ID as mac, Authentication_Status as status, Failure_Reason as motivo "+
    		 "insert into peticionesUsuario "+    		 
    		 ";"
    		 , "insert", "", false);
     
     gestionaQuery(
    		 "from peticionesUsuario as peticionesUsuario unidirectional "+
    		 "join "+
    		 "hosts as hosts  "+
    		 "on hosts.mac==peticionesUsuario.mac  " + 
    		 "select ACS_Timestamp,hosts.ID as idHost, peticionesUsuario.ID as ID , usuario, maquina, peticionesUsuario.mac, status, motivo "+
    		 "insert into peticionConMaquina "+
    		 ";"
    		 , "insert", "", false);
     
     gestionaQuery(
    		 "from peticionesUsuario[not(peticionesUsuario.mac==hosts.mac in hosts)]  "+ /*not(fallidas.ID==hosts.ID in hosts) and not(fallidas.ID!=hosts.ID in hosts)*/
    		 "select ACS_Timestamp,0 as idHost, ID , usuario, \"not defined\" as maquina, mac, status, motivo "+
    		 "insert into peticionConMaquina "+
    		 ";"
    		 , "insert", "", false);
     
     //Detalle de los registros de usuario (máquina)
      q=gestionaQuery(
    		 "from peticionConMaquina#window.time(20000) "+ 
    		 "select ACS_Timestamp,idHost, ID , usuario, maquina, mac, status, motivo, agregacion:sumConReset(1L,status,'Passed') as errorsConsecutivos,1 as tamano "+
    		 "group by usuario "+
    		 "insert into fallosConsecutivos for current-events "+
    		 ";"
    		 , "insert", "", true);
     
     
     //Conteo de más de 3 fallos consecutivos (aunque se salga de laventana el primer registro "el de3 errores", el total seguirá marcando 3)
    /* gestionaQuery(
    		 "from fallosConsecutivos[errorsConsecutivos>=2]#window.time(20000) "+ 
    		 "select ACS_Timestamp,idHost, ID , usuario, maquina, mac, status, motivo, errorsConsecutivos "+
    		 "insert into masDeTresFallos for current-events "+
    		 ";"
    		 , "insert", "", false);*/
  
    }
    

    public SiddhiBoltMetricaOnlineRadius1(){
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
          InputHandler inputHandler = siddhiManager.getInputHandler("radius");
          //Adaptamos los campos al formato definido por el stream
          //podríamos recuperar el formato de los campos desde la metadata
         /* if(campos[12]==null) campos[12]="null";
          if(campos[15]==null) campos[15]="null";
          if(campos[20]==null) campos[20]="null";
          if(campos[13].equals("null")) campos[13]="-1";*/
          
           try {
                       //Pasamos los valores relevantes de latupla al Stream de  WSO2
                        //inputHandler.send(new Object[]{campos[2],campos[12],campos[15],campos[20],Integer.parseInt(campos[13])});
        	   			inputHandler.send(new Object[]{campos[3],Integer.parseInt(campos[1].trim()),campos[10],campos[16],campos[32],campos[21]});
          } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                 }
           cont++;
          /* if(cont==8){
           gestionaQuery("","delete",q,false);
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
			                	/*String id=e.getData(0).toString()+"-"+e.getData(1).toString();
			                	String docum="{\"MSGID\":\""+e.getData(0).toString()+"\","+
		                				  "\"DSN\":\""+e.getData(1)+"\","+
		                				  "\"tamano\":"+Long.parseLong(e.getData(2).toString())+ "}";
			                	System.out.println("gg");
			                	collector.emit(new Values("indiceprueba","typeprueba",id,docum,e.getStreamId(), e.getData()));*/
			                	/*
			                	  eventtimestamp
			                	  idHost, 
			                	  ID , 
			                	  usuario, 
			                	  maquina, 
			                	  mac, 
			                	  status, 
			                	  motivo
			                	  agregacion:sumConReset(1L,status,'Passed') as errorsConsecutivos 
			                	*/
			                	String id=e.getData(0).toString()+"-"+e.getData(3).toString();
			                	try {
									XContentBuilder dataBuilder=jsonBuilder().startObject()
											.field("@timestamp",e.getData(0).toString())
											.field("idHost",e.getData(1))
											.field("ID",Long.parseLong(e.getData(2).toString()))
											.field("usuario",e.getData(3))
											.field("maquina",e.getData(4))
											.field("mac",e.getData(5))
											.field("status",e.getData(6))
											.field("motivo",e.getData(7))
											.field("erroresCons",Long.parseLong(e.getData(8).toString()))
											.field("tamano",Double.parseDouble(e.getData(9).toString()))
									.endObject();
									

				                	System.out.println(dataBuilder.string());
				                	collector.emit(new Values("ob_src_postfix","test2",id,dataBuilder.string()));
								} catch (IOException e1) {
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
