package com.produban.openbus.topologies;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.storm.hdfs.trident.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.trident.format.RecordFormat;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.state.StateFactory;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.tuple.Fields;

import com.produban.openbus.topologies.TimeStampRotationPolicy.Units;

public class OpenbusRadiusEntityTopology {
	public static void main(String[] args) {
		if(args.length!=1){
			System.out.println("uso: <Fichero de propiedades>");
			System.exit(1);
		}
		
		/**Creamos un Objeto de tipo Properties*/
		Properties propiedades = new Properties();
		
		try {
			propiedades
			 .load(new FileInputStream(args[0]));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Error a la hora de abrir el fichero de PROPERTIES");
			e1.printStackTrace();
		}
		
		//Definición de la Topología
		Config conf = new Config();
	 	
		BrokerSpout openbusBrokerSpout = new BrokerSpout(propiedades.getProperty("KAFKA_TOPIC"), //tópico KAFKA
				propiedades.getProperty("KAFKA_ZOOKEEPER_LIST"), //zookeeper
				propiedades.getProperty("KAFAKA_BROKER_ID"), //Client ID - Configuración del broker 
	            Boolean.parseBoolean(propiedades.getProperty("KAFKA_FROM_BEGINNING"))); //si es desde el principio
	
		//Campos que detectaremos desde el parseador.
		Fields hdfsFields = new Fields( "ID","Message_Text","TIMESTAMP_MILLIS","ACS_Timestamp","ACSView_Timestamp",
				"ACS_Server","ACS_Session_ID","Access_Service","Service_Selection_Policy","Authorization_Policy",
				"User_Name","Identity_Store","Authentication_Method","Network_Device_Name","Identity_Group",
				"Network_Device_Groups","Calling_Station_ID","NAS_Port","Service_Type","Audit_Session_ID",
				"CTS_Security_Group","Failure_Reason","Use_Case","Framed_IP_Address","NAS_Identifier","NAS_IP_Address",
				"NAS_Port_Id","Cisco_AV_Pair","AD_Domain","Response_Time","Passed","Failed","Authentication_Status",
				"Radius_Daignostic_link","Active_Session_Link","ACS_UserName","NAC_Role","NAC_Policy_Compliance",
				"NAC_Username","NAC_Posture_Token","Selected_Posture_Server","Selected_Identity_Store",
				"Authentication_Identity_Store","Authorization_Exception_Policy_Matched_Rule",
				"External_Policy_Server_Matched_Rule","Group_Mapping_Policy_Matched_Rule",
				"Identity_Policy_Matched_Rule","NAS_Port_Type","Query_Identity_Stores",
				"Selected_Authorization_Profiles","Selected_Exception_Authorization_Profiles",
				"Selected_Query_Identity_Stores","Tunnel_Details","Cisco_H323_Attributes","Cisco_SSG_Attributes",
				"Other_Attributes","More_Details","EAP_Tunnel","EAP_Authentication","Eap_Tunnel","Eap_Authentication",
				"RADIUS_User_Name","NAS_Failure","timestamp","Response","TOTAL_COLUMN_0","TOTAL_COLUMN_1");
		
		//Formato del nombre del fichero
	    OpenbusFileNameFormat fileNameFormat = new OpenbusFileNameFormat()
	            .withPath(propiedades.getProperty("HDFS_OUTPUT_DIR"))
	            .withPrefix(propiedades.getProperty("HDFS_OUTPUT_FILENAME"))
	            .withExtension(propiedades.getProperty("HDFS_OUTPUT_FILE_EXTENSION"));
	
	    //Formato de los registros. Asignamos el delimitador HIVE por defecto como separacor de campos.
	    RecordFormat recordFormat = new DelimitedRecordFormat()
	            .withFields(hdfsFields).withFieldDelimiter("\001");
	
	    StateFactory factory = new OpenbusHdfsStateFactory();
	    
	    if (propiedades.getProperty("HDFS_OUTPUT_DIR") != null){
		//Criterios de Rotación
		Units unidad_tamano=TimeStampRotationPolicy.Units.MB;
		if(propiedades.getProperty("SIZE_ROTATION_UNIT").equals("KB")){
		    unidad_tamano=TimeStampRotationPolicy.Units.KB;
		}else if(propiedades.getProperty("SIZE_ROTATION_UNIT").equals("MB")){
		    unidad_tamano=TimeStampRotationPolicy.Units.MB;
		}else if(propiedades.getProperty("SIZE_ROTATION_UNIT").equals("GB")){
		    unidad_tamano=TimeStampRotationPolicy.Units.GB;
		}else if(propiedades.getProperty("SIZE_ROTATION_UNIT").equals("TB")){
		    unidad_tamano=TimeStampRotationPolicy.Units.TB;
		}else System.out.println("Unidad de tamaño no reconocida(KB/MB/GB/TB). Se usará por defecto MB.");

		long unidad_tiempo=TimeStampRotationPolicy.MINUTE;
		if(propiedades.getProperty("TIME_ROTATION_UNIT").equals("SECOND")){
		    unidad_tiempo=TimeStampRotationPolicy.SECOND;
		}else if(propiedades.getProperty("TIME_ROTATION_UNIT").equals("MINUTE")){
		    unidad_tiempo=TimeStampRotationPolicy.MINUTE;
		}else if(propiedades.getProperty("TIME_ROTATION_UNIT").equals("HOUR")){
		    unidad_tiempo=TimeStampRotationPolicy.HOUR;
		}else if(propiedades.getProperty("TIME_ROTATION_UNIT").equals("DAY")){
		    unidad_tiempo=TimeStampRotationPolicy.DAY;
		}else System.out.println("Unidad de tiempo no reconocida(SECOND/MINUTE/HOUR/DAY). Se usará por defecto MINUTE.");


		TimeStampRotationPolicy rotationPolicy = new TimeStampRotationPolicy()
		.setTimePeriod(Integer.parseInt(propiedades.getProperty("TIME_ROTATION_VALUE")), unidad_tiempo)
		.setSizeMax(Float.parseFloat(propiedades.getProperty("SIZE_ROTATION_VALUE")), unidad_tamano);


		OpenbusHdfsState.Options options = new OpenbusHdfsState.HdfsFileOptions()
		.withFileNameFormat(fileNameFormat)
		.withRecordFormat(recordFormat)
		.withRotationPolicy(rotationPolicy)
		.withFsUrl(propiedades.getProperty("HDFS_URL"))
		.addSyncMillisPeriod(Long.parseLong(propiedades.getProperty("SYNC_MILLIS_PERIOD")));
		System.setProperty("HADOOP_USER_NAME", propiedades.getProperty("HDFS_USER"));//Necesario para que no intente entrar con el usuario que lanza el programa

		factory = new OpenbusHdfsStateFactory().withOptions(options);
	    }
	    
		TridentTopology topology = new TridentTopology();
		Stream  parseaLogs;
		String tipo=propiedades.getProperty("INPUT_ORIGIN");
		
		//Definición de la topología
		
		if(tipo.equals("kafka")){ //Si leemos desde Kafka
			parseaLogs	= topology.newStream("spout1", openbusBrokerSpout.getPartitionedTridentSpout())
		       .each(new Fields("bytes"),
		    		 new RadiusEntityParser(),
		    		 hdfsFields);
			if (propiedades.getProperty("HDFS_OUTPUT_DIR") != null){
			    parseaLogs.partitionPersist(factory, hdfsFields, new OpenbusHdfsUpdater(), new Fields());
			}
			if(propiedades.getProperty("KAFKA_OUTPUT_TOPIC")!=null && propiedades.getProperty("KAFKA_OUTPUT_TOPIC")!=""){
			    parseaLogs.partitionPersist(new KafkaState.Factory(propiedades.getProperty("KAFKA_OUTPUT_TOPIC"),
				    propiedades.getProperty("KAFKA_ZOOKEEPER_LIST"), 
				    propiedades.getProperty("KAFKA_BROKER_HOSTS"),
				    recordFormat,propiedades.getProperty("KAFKA_TOPIC")),
				    hdfsFields,
				    new KafkaState.Updater());
			}
		}
		if (tipo.equals("disco")){ //Si leemos desde un fichero de disco local
		    SimpleFileStringSpout spout1 = new SimpleFileStringSpout(propiedades.getProperty("INPUT_FILE"), "bytes");
		    parseaLogs	= topology.newStream("spout1", spout1)
			    .each(new Fields("bytes"),
				    new RadiusEntityParser(),
				    hdfsFields);
		    if (propiedades.getProperty("HDFS_OUTPUT_DIR") != null){
			parseaLogs.partitionPersist(factory, hdfsFields, new OpenbusHdfsUpdater(), new Fields());
		    }
		    if(propiedades.getProperty("KAFKA_OUTPUT_TOPIC")!=null && propiedades.getProperty("KAFKA_OUTPUT_TOPIC")!=""){
			parseaLogs.partitionPersist(new KafkaState.Factory(propiedades.getProperty("KAFKA_OUTPUT_TOPIC"),
				propiedades.getProperty("KAFKA_ZOOKEEPER_LIST"), 
				propiedades.getProperty("KAFKA_BROKER_HOSTS"),
				recordFormat,propiedades.getProperty("KAFKA_TOPIC")),
				hdfsFields,
				new KafkaState.Updater());
			}
		}
		
		//Despliegue de la topología
		 try {
			if(propiedades.getProperty("STORM_CLUSTER").equals("local")){
				// CLuster local
			   LocalCluster cluster = new LocalCluster();
			   conf.setMaxSpoutPending(Integer.parseInt(propiedades.getProperty("STORM_MAX_SPOUT_PENDING")));
			  
		       cluster.submitTopology(propiedades.getProperty("STORM_TOPOLOGY_NAME"), conf, topology.build());
			}else{
				//Cluster 
				conf.setNumWorkers(Integer.parseInt(propiedades.getProperty("STORM_NUM_WORKERS")));
				conf.setMaxSpoutPending(Integer.parseInt(propiedades.getProperty("STORM_MAX_SPOUT_PENDING")));
				StormSubmitter.submitTopology(propiedades.getProperty("STORM_TOPOLOGY_NAME"), conf, topology.build());
			}
		 } catch (NumberFormatException  |AlreadyAliveException | InvalidTopologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 }
	
	}
}
