package com.produban.openbus.topologies;

import org.apache.storm.hdfs.trident.HdfsState;
import org.apache.storm.hdfs.trident.HdfsStateFactory;
import org.apache.storm.hdfs.trident.HdfsUpdater;
import org.apache.storm.hdfs.trident.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.trident.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.trident.format.FileNameFormat;
import org.apache.storm.hdfs.trident.format.RecordFormat;
import org.apache.storm.hdfs.trident.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.trident.rotation.FileSizeRotationPolicy;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.state.StateFactory;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.tuple.Fields;

public class PostfixTopology {
public static void main(String[] args) {
		
		if(args.length!=9){
			System.out.println("uso: <HDFSURL> <usuarioHDFS> <DirSalidaHDFS> <KafkaZookeeper> <BrokerID> <Topic> <disco/kafka> <segundos> <Cluster>");
			System.exit(1);
		}
		//definiciÃ³n de la topologÃ­a
		Config conf = new Config();
//      conf.put(Config.TOPOLOGY_DEBUG,true);
     	
		BrokerSpout openbusBrokerSpout = new BrokerSpout(args[5], //topico
                  args[3], //zookeeper
                  args[4], //Client ID - Configuración del broker ./config/server.properties
                  false); //si es desde el principio

		
		Fields hdfsFields = new Fields("EVENTTIMESTAMP","SMTPDID","MSGID","CLEANUPID"
				,"QMGRID","SMTPID","ERRORID","CLIENTE","CLIENTEIP","ACCION","SERVER"
				,"SERVERIP","MESSAGEID","FROM","SIZE","NRCPT","TO","TOSERVERNAME"
				,"TOSERVERIP","TOSERVERPORT","DELAY","DSN","STATUS","STATUSDESC"
				,"AMAVISID");
		//Fields hdfsFields = new Fields("resultado");
		
	    FileNameFormat fileNameFormat = new DefaultFileNameFormat()
	            .withPath(args[2])
	            .withPrefix("PostfixParseadoTrident")
	            .withExtension(".txt");
	
	    RecordFormat recordFormat = new DelimitedRecordFormat()
	            .withFields(hdfsFields).withFieldDelimiter("\001");
	
	    FileRotationPolicy rotationPolicy = new FileSizeRotationPolicy(10.0f, FileSizeRotationPolicy.Units.MB);
	
	    
	   HdfsState.Options options = new HdfsState.HdfsFileOptions()
	           .withFileNameFormat(fileNameFormat)
	           .withRecordFormat(recordFormat)
	           .withRotationPolicy(rotationPolicy)
	           .withFsUrl(args[0]);
	   System.setProperty("HADOOP_USER_NAME", args[1]);//Necesario para que no intente entrar con el usuario que lanza el programa
	  
	    StateFactory factory = new HdfsStateFactory().withOptions(options);
	    
		TridentTopology topology = new TridentTopology();
		Stream  parseaLogs;
		String tipo=args[6];
		if(tipo.equals("kafka")){
		parseaLogs	= topology.newStream("spout1", openbusBrokerSpout.getPartitionedTridentSpout())
		       .each(new Fields("bytes"),
		    		 new PostfixParser(tipo),
		    		 new Fields("EVENTTIMESTAMP","SMTPDID","MSGID","CLEANUPID"
		    					,"QMGRID","SMTPID","ERRORID","CLIENTE","CLIENTEIP","ACCION","SERVER"
		    					,"SERVERIP","MESSAGEID","FROM","SIZE","NRCPT","TO","TOSERVERNAME"
		    					,"TOSERVERIP","TOSERVERPORT","DELAY","DSN","STATUS","STATUSDESC"
		    					,"AMAVISID"));
		    	parseaLogs.partitionPersist(factory, hdfsFields, new HdfsUpdater(), new Fields());
		}
		if (tipo.equals("disco")){
			
			//String entrada="D:\\sample_kafka.log";
			String entrada="/home/rvachet/postfix.log";
			SimpleFileStringSpout spout1 = new SimpleFileStringSpout(entrada, "bytes");
			parseaLogs	= topology.newStream("spout1", spout1)
				       .each(new Fields("bytes"),
				    		 new PostfixParser(tipo),
				    		 new Fields("EVENTTIMESTAMP","SMTPDID","MSGID","CLEANUPID"
				    					,"QMGRID","SMTPID","ERRORID","CLIENTE","CLIENTEIP","ACCION","SERVER"
				    					,"SERVERIP","MESSAGEID","FROM","SIZE","NRCPT","TO","TOSERVERNAME"
				    					,"TOSERVERIP","TOSERVERPORT","DELAY","DSN","STATUS","STATUSDESC"
				    					,"AMAVISID"));
			parseaLogs.partitionPersist(factory, hdfsFields, new HdfsUpdater(), new Fields());
		}
		 try {
			if(args[8].equals("local")){
				// CLuster local
			   LocalCluster cluster = new LocalCluster();
			   conf.setMaxSpoutPending(1);
		      cluster.submitTopology("parseaPostfix", conf, topology.build());
		      Thread.sleep(Integer.parseInt(args[7]));
				cluster.killTopology("parseaPostfix");
				cluster.shutdown();
			}else{
				conf.setNumWorkers(10);
				conf.setMaxSpoutPending(1);
				StormSubmitter.submitTopology("parseaPostfix", conf, topology.build());
			}
		 } catch (InterruptedException | AlreadyAliveException | InvalidTopologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 }
		     
	     
	      System.exit(0);
	}
}
