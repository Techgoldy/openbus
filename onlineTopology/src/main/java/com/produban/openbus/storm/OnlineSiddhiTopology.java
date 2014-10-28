package com.produban.openbus.storm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import kafka.javaapi.consumer.SimpleConsumer;
import storm.kafka.KafkaSpout;
import storm.kafka.KafkaUtils;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;

import com.produban.openbus.storm_ES.DefaultTupleMapper;
import com.produban.openbus.storm_ES.ElasticSearchBolt;
import com.produban.openbus.storm_ES.StormElasticSearchConstants;
import com.produban.openbus.trident.EchoBolt;

public class OnlineSiddhiTopology {
	 public static void main(String[] args) {

		 if(args.length!=1){
			 System.out.println("USO: <fichero de parámetros>");
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
		IRichSpout spout=null;
		//comprobamos el tipo de entrada
		String tipo=propiedades.getProperty("INPUT_ORIGIN");
		if (tipo.equals("kafka")){
			//Configuramos el KafkaSpout
			ZkHosts zooHosts = new ZkHosts(propiedades.getProperty("KAFKA_ZOOKEEPER_LIST"));
		    SpoutConfig spoutConfig = new SpoutConfig(zooHosts, propiedades.getProperty("KAFKA_TOPIC"), "", "STORM-ID");
		    boolean fromBeginning=false;
		    if(propiedades.getProperty("KAFKA_FROM_BEGINNING")!=null){
		    	fromBeginning=Boolean.parseBoolean(propiedades.getProperty("KAFKA_FROM_BEGINNING"));
		    }else{
		    	fromBeginning=false;
		    }
		    //spoutConfig.startOffsetTime=-1;
		   spoutConfig.forceFromStart = fromBeginning;
		    if(!fromBeginning){
		    	spoutConfig.startOffsetTime=-1;
		    }
			spout =  new KafkaSpout(spoutConfig);
			
		}
		if (tipo.equals("disco")){
			spout = new SimpleFileStringSpout(propiedades.getProperty("INPUT_FILE"), "linea");
		}
	 
		Config conf = new Config();
		conf.put(StormElasticSearchConstants.ES_CLUSTER_NAME,propiedades.getProperty("ES_CLUSTER_NAME"));
		conf.put(StormElasticSearchConstants.ES_HOST,propiedades.getProperty("ES_HOST"));
		conf.put(StormElasticSearchConstants.ES_PORT,Integer.parseInt(propiedades.getProperty("ES_PORT")));
		//Se crea la topología
		TopologyBuilder builder = new TopologyBuilder();

	    builder.setSpout("source", spout, 1);
		builder.setBolt("streamer", new Tuple2Stream(Integer.parseInt(propiedades.getProperty("METADATA_SINCRO_SECS")),propiedades.getProperty("GET_METADATA_SERVICE_URL"),propiedades.getProperty("METADATA_FILE_JSON")), 1).shuffleGrouping("source");
		
		builder.setBolt("SiddhiBolt", new SiddhiBolt(propiedades.getProperty("PUT_METRICA_SERVICE_URL"),
													propiedades.getProperty("DELETE_METRICA_SERVICE_URL"),
													propiedades.getProperty("ELASTICSEARCH_OUTPUT").toLowerCase().equals("true"))
						, 1).shuffleGrouping("streamer");
		
		if(propiedades.getProperty("ELASTICSEARCH_OUTPUT").toLowerCase().equals("true")){
			builder.setBolt("ESBolt", new ElasticSearchBolt(new DefaultTupleMapper()),1).shuffleGrouping("SiddhiBolt");
		}
		 if(propiedades.getProperty("ECHO_OUTPUT").toLowerCase().equals("true")){
			builder.setBolt("echo", new EchoBolt(), 1).shuffleGrouping("SiddhiBolt","echo");
		 }

		//Despliegue de la topología
		 try {
			if(propiedades.getProperty("STORM_CLUSTER").equals("local")){
				// local
			   LocalCluster cluster = new LocalCluster();
			   conf.setMaxSpoutPending(Integer.parseInt(propiedades.getProperty("STORM_MAX_SPOUT_PENDING")));
		       cluster.submitTopology(propiedades.getProperty("STORM_TOPOLOGY_NAME"), conf, builder.createTopology());
		  
			}else{
				//Server cluster 
				conf.setNumWorkers(Integer.parseInt(propiedades.getProperty("STORM_NUM_WORKERS")));
				conf.setMaxSpoutPending(Integer.parseInt(propiedades.getProperty("STORM_MAX_SPOUT_PENDING")));
				StormSubmitter.submitTopology(propiedades.getProperty("STORM_TOPOLOGY_NAME"), conf, builder.createTopology());
			}
		 } catch (NumberFormatException  |AlreadyAliveException | InvalidTopologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 }
    }
}
