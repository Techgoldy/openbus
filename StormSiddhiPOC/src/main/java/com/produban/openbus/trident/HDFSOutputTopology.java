package com.produban.openbus.trident;

import java.util.concurrent.TimeUnit;

import org.apache.storm.hdfs.trident.HdfsState;
import org.apache.storm.hdfs.trident.HdfsStateFactory;
import org.apache.storm.hdfs.trident.HdfsUpdater;
import org.apache.storm.hdfs.trident.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.trident.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.trident.format.FileNameFormat;
import org.apache.storm.hdfs.trident.format.RecordFormat;
import org.apache.storm.hdfs.trident.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.trident.rotation.FileSizeRotationPolicy;
import org.apache.storm.hdfs.trident.sync.CountSyncPolicy;
import org.apache.storm.hdfs.trident.sync.SyncPolicy;

import com.produban.openbus.storm.SimpleFileStringSpout;

import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.state.StateFactory;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;

public class HDFSOutputTopology {

	public static void main(String[] args) {
		
		
		//definición de la topología
		Config conf = new Config();
//      conf.put(Config.TOPOLOGY_DEBUG,true);
		String entrada="D:\\produban\\Logs\\bepxnxusrsp01\\vmwtbitarecol01\\sample_5500.txt";
		String salida="d:\\parseado_proxy.log";
		if(args.length==2){
			entrada=args[0];
			salida=args[1];
		}
      
		SimpleFileStringSpout spout1 = new SimpleFileStringSpout(entrada, "linea");
		
		Fields hdfsFields = new Fields("eventTimeStamp","timeTaken","clientIP","User","Group","Exception","filterResult","category",
			    "referer","responseCode","action","method","contentType","protocol","requestDomain",
			    "requestPort","requestPath","requestQuery","requestURIExtension","userAgent","serverIP","scBytes","csBytes",
			    "virusID","destinationIP");
		//Fields hdfsFields = new Fields("resultado");
		
	    FileNameFormat fileNameFormat = new DefaultFileNameFormat()
	            .withPath("/user/cloudera/")
	            .withPrefix("trident")
	            .withExtension(".txt");
	
	    RecordFormat recordFormat = new DelimitedRecordFormat()
	            .withFields(hdfsFields).withFieldDelimiter("\001");
	
	    FileRotationPolicy rotationPolicy = new FileSizeRotationPolicy(10.0f, FileSizeRotationPolicy.Units.MB);
	
	    
	   HdfsState.Options options = new HdfsState.HdfsFileOptions()
	           .withFileNameFormat(fileNameFormat)
	           .withRecordFormat(recordFormat)
	           .withRotationPolicy(rotationPolicy)
	           .withFsUrl("hdfs://192.168.182.129:8020");
	   System.setProperty("HADOOP_USER_NAME", "cloudera");//Necesario para que no intente entrar con el usuario que lanza el programa
	  
	    StateFactory factory = new HdfsStateFactory().withOptions(options);
	    
		TridentTopology topology = new TridentTopology();
		Stream  parseaLogs =
		     topology.newStream("spout1", spout1)
		       .each(new Fields("linea"),
		    		 new ParseProxy(),
		    		 new Fields("eventTimeStamp","timeTaken","clientIP","User","Group","Exception","filterResult","category",
		    				    "referer","responseCode","action","method","contentType","protocol","requestDomain",
		    				    "requestPort","requestPath","requestQuery","requestURIExtension","userAgent","serverIP","scBytes","csBytes",
		    				    "virusID","destinationIP"));
		    	parseaLogs.partitionPersist(factory, hdfsFields, new HdfsUpdater(), new Fields());
		       //.each(new Fields("eventTimeStamp","timeTaken","clientIP","User"),new Print("",salida));
		
		   LocalCluster cluster = new LocalCluster();
	      cluster.submitTopology("basic_primitives", conf, topology.build());
	}
}
