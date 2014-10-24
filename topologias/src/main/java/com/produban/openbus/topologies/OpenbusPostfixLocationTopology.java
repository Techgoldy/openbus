package com.produban.openbus.topologies;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
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

public class OpenbusPostfixLocationTopology {

    private static Logger LOG = Logger.getLogger(OpenbusPostfixLocationTopology.class);
    public static void main(String[] args) {
	if (args.length != 1) {
	    LOG.debug("uso: <Fichero de propiedades>");
	    System.exit(1);
	}
	Properties propiedades = new Properties();
	try {
	    propiedades.load(new FileInputStream(args[0]));

	    Config conf = new Config();

	    BrokerSpout openbusBrokerSpout = new BrokerSpout(propiedades.getProperty("KAFKA_TOPIC"), 
	    	propiedades.getProperty("KAFKA_ZOOKEEPER_LIST"), 
	    	propiedades.getProperty("KAFAKA_BROKER_ID"), 
	    	Boolean.parseBoolean(propiedades.getProperty("KAFKA_FROM_BEGINNING")));
	    
	    Fields hdfsFields = new Fields("EVENTTIMESTAMP", "SMTPDID", "MSGID", "CLEANUPID", "QMGRID", "SMTPID", "ERRORID", "CLIENTE", "CLIENTEIP", "ACCION", "SERVER", "SERVERIP",
	    	"MESSAGEID", "FROM", "SIZE", "NRCPT", "TO", "TOSERVERNAME", "TOSERVERIP", "TOSERVERPORT", "DELAY", "DSN", "STATUS", "STATUSDESC", "AMAVISID",
	    	 "coords", "city", "postalCode", "areaCode", "metroCode", "region", "country");

	    // Formato del nombre del fichero
	    OpenbusFileNameFormat fileNameFormat = new OpenbusFileNameFormat().withPath(propiedades.getProperty("HDFS_OUTPUT_DIR"))
	    	.withPrefix(propiedades.getProperty("HDFS_OUTPUT_FILENAME")).withExtension(propiedades.getProperty("HDFS_OUTPUT_FILE_EXTENSION"));

	    // Formato de los registros. Asignamos el delimitador HIVE por defecto como separacor de campos.
	    RecordFormat recordFormat = new DelimitedRecordFormat().withFields(hdfsFields).withFieldDelimiter("\001");

	    // Criterios de Rotación
	    Units unidad_tamano = TimeStampRotationPolicy.Units.MB;
	    if (propiedades.getProperty("SIZE_ROTATION_UNIT").equals("KB")) {
	        unidad_tamano = TimeStampRotationPolicy.Units.KB;
	    }
	    else if (propiedades.getProperty("SIZE_ROTATION_UNIT").equals("MB")) {
	        unidad_tamano = TimeStampRotationPolicy.Units.MB;
	    }
	    else if (propiedades.getProperty("SIZE_ROTATION_UNIT").equals("GB")) {
	        unidad_tamano = TimeStampRotationPolicy.Units.GB;
	    }
	    else if (propiedades.getProperty("SIZE_ROTATION_UNIT").equals("TB")) {
	        unidad_tamano = TimeStampRotationPolicy.Units.TB;
	    }
	    else{
	        LOG.debug("Unidad de tamaño no reconocida(KB/MB/GB/TB). Se usará por defecto MB.");
	    }

	    long unidad_tiempo = TimeStampRotationPolicy.MINUTE;
	    if (propiedades.getProperty("TIME_ROTATION_UNIT").equals("SECOND")) {
	        unidad_tiempo = TimeStampRotationPolicy.SECOND;
	    }
	    else if (propiedades.getProperty("TIME_ROTATION_UNIT").equals("MINUTE")) {
	        unidad_tiempo = TimeStampRotationPolicy.MINUTE;
	    }
	    else if (propiedades.getProperty("TIME_ROTATION_UNIT").equals("HOUR")) {
	        unidad_tiempo = TimeStampRotationPolicy.HOUR;
	    }
	    else if (propiedades.getProperty("TIME_ROTATION_UNIT").equals("DAY")) {
	        unidad_tiempo = TimeStampRotationPolicy.DAY;
	    }
	    else{
	        LOG.debug("Unidad de tiempo no reconocida(SECOND/MINUTE/HOUR/DAY). Se usará por defecto MINUTE.");
	    }

	    TimeStampRotationPolicy rotationPolicy = new TimeStampRotationPolicy().setTimePeriod(Integer.parseInt(propiedades.getProperty("TIME_ROTATION_VALUE")), 
		    unidad_tiempo).setSizeMax(Float.parseFloat(propiedades.getProperty("SIZE_ROTATION_VALUE")), unidad_tamano);

	    OpenbusHdfsState.Options options = new OpenbusHdfsState.HdfsFileOptions().withFileNameFormat(fileNameFormat).withRecordFormat(recordFormat)
	    	.withRotationPolicy(rotationPolicy).withFsUrl(propiedades.getProperty("HDFS_URL"))
	    	.addSyncMillisPeriod(Long.parseLong(propiedades.getProperty("SYNC_MILLIS_PERIOD")));
	    
	    System.setProperty("HADOOP_USER_NAME", propiedades.getProperty("HDFS_USER"));
	    StateFactory factory = new OpenbusHdfsStateFactory().withOptions(options);

	    TridentTopology topology = new TridentTopology();
	    Stream parseaLogs;
	    String tipo = propiedades.getProperty("INPUT_ORIGIN");

	    if (tipo.equals("kafka")) { // Si leemos desde Kafka
	        parseaLogs = topology.newStream("spout1", openbusBrokerSpout.getPartitionedTridentSpout()).each(new Fields("bytes"), 
	        	new PostfixLocationParser(propiedades.getProperty("ELASTICSEARCH_HOST"), Integer.parseInt(propiedades.getProperty("ELASTICSEARCH_PORT")), 
	        	propiedades.getProperty("ELASTICSEARCH_NAME"), Boolean.parseBoolean(propiedades.getProperty("ELASTICSEARCH_CACHE_SEARCH"))), hdfsFields);
	        parseaLogs.partitionPersist(factory, hdfsFields, new OpenbusHdfsUpdater(), new Fields());
	        if (propiedades.getProperty("KAFKA_OUTPUT_TOPIC") != null && propiedades.getProperty("KAFKA_OUTPUT_TOPIC") != "") {
	    		parseaLogs.partitionPersist(new KafkaState.Factory(propiedades.getProperty("KAFKA_OUTPUT_TOPIC"), propiedades.getProperty("KAFKA_ZOOKEEPER_LIST"),
	    		propiedades.getProperty("KAFKA_BROKER_HOSTS"), recordFormat, propiedades.getProperty("KAFKA_TOPIC")), hdfsFields, new KafkaState.Updater());
	        }
	    }
	    
	    if (tipo.equals("disco")) { // Si leemos desde un fichero de disco local
	        SimpleFileStringSpout spout1 = new SimpleFileStringSpout(propiedades.getProperty("INPUT_FILE"), "bytes");
	        parseaLogs = topology.newStream("spout1", spout1).each(new Fields("bytes"), new PostfixLocationParser(propiedades.getProperty("ELASTICSEARCH_HOST"), 
        	Integer.parseInt(propiedades.getProperty("ELASTICSEARCH_PORT")), propiedades.getProperty("ELASTICSEARCH_NAME"), 
        	Boolean.parseBoolean(propiedades.getProperty("ELASTICSEARCH_CACHE_SEARCH"))), hdfsFields);
	        parseaLogs.partitionPersist(factory, hdfsFields, new OpenbusHdfsUpdater(), new Fields());
	        if (propiedades.getProperty("KAFKA_OUTPUT_TOPIC") != null && propiedades.getProperty("KAFKA_OUTPUT_TOPIC") != "") {
	    		parseaLogs.partitionPersist(new KafkaState.Factory(propiedades.getProperty("KAFKA_OUTPUT_TOPIC"), 
    			propiedades.getProperty("KAFKA_ZOOKEEPER_LIST"), propiedades.getProperty("KAFKA_BROKER_HOSTS"), 
    			recordFormat, propiedades.getProperty("KAFKA_TOPIC")), hdfsFields, new KafkaState.Updater());
	        }
	    }

	    if (propiedades.getProperty("STORM_CLUSTER").equals("local")) {
	        LocalCluster cluster = new LocalCluster();
	        conf.setMaxSpoutPending(Integer.parseInt(propiedades.getProperty("STORM_MAX_SPOUT_PENDING")));
	        cluster.submitTopology(propiedades.getProperty("STORM_TOPOLOGY_NAME"), conf, topology.build());
	    }
	    else {
	        conf.setNumWorkers(Integer.parseInt(propiedades.getProperty("STORM_NUM_WORKERS")));
	        conf.setMaxSpoutPending(Integer.parseInt(propiedades.getProperty("STORM_MAX_SPOUT_PENDING")));
	        StormSubmitter.submitTopology(propiedades.getProperty("STORM_TOPOLOGY_NAME"), conf, topology.build());
	    }
	}
	catch (NumberFormatException | IOException | AlreadyAliveException | InvalidTopologyException e) {
	    LOG.error(e);
	}
    }
}
