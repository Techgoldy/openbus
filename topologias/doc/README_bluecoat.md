#Bluecoat

**proxyLocation.properties**
```
#Configuración acceso HDFS
HDFS_URL=hdfs://180.133.240.175:8020
HDFS_USER=hdfs
HDFS_OUTPUT_DIR=/user/hdfs/input/proxy
HDFS_OUTPUT_FILENAME=TridentProxyLocationTopology
HDFS_OUTPUT_FILE_EXTENSION=.txt
INPUT_ORIGIN=kafka
INPUT_FILE=/home/rvachet/salida_bluecoat.log

#Configuración de KAFKA
KAFKA_ZOOKEEPER_LIST=180.133.240.174:2181,180.133.240.175:2181,180.133.240.176:2181
KAFAKA_BROKER_ID=1
KAFKA_TOPIC=ob_src_bluecoat
KAFKA_FROM_BEGINNING=false

#Configuración ElasticSearch
ELASTICSEARCH_HOST=180.133.240.42
ELASTICSEARCH_PORT=9300
ELASTICSEARCH_NAME=elasticsearch
ELASTICSEARCH_CACHE_SEARCH=true

#Configuración STORM
STORM_CLUSTER=server
STORM_BATCH_MILLIS=700
STORM_NUM_WORKERS=1
STORM_MAX_SPOUT_PENDING=1
STORM_TOPOLOGY_NAME=ob_src_bluecoat

#Propiedades de la rotación
SIZE_ROTATION_UNIT=MB
SIZE_ROTATION_VALUE=64
TIME_ROTATION_UNIT=HOUR
TIME_ROTATION_VALUE=1

#Propiedades de la sincronización/escritura a HDFS
SYNC_MILLIS_PERIOD=10000
```

**ProxyLocationParser.java**
```
package com.produban.openbus.topologies;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.client.Client;
import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

public class ProxyLocationParser extends BaseFunction {

    private static final long serialVersionUID = 1L;
    public static final char SEPARADOR = '\001'; // caracter SOH
    public static Pattern pattern = Pattern
	    .compile("((?<eventTimeStamp>(.{19})) (?<timeTaken>(\\d+)) (?<clientIP>(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})) (?<User>(.*?)) (?<Group>(.*?)) (?<Exception>(.*?)) (?<filterResult>(\\S+)) (?<category>\"?([^\"]*)\"?) (?<referer>(\\S+))\\s+(?<responseCode>(\\d+)) (?<action>(\\S+)) (?<method>(\\S+)) (?<contentType>(\\S+)) (?<protocol>(\\S+)) (?<requestDomain>(\\S+)) (?<requestPort>(\\d+)) (?<requestPath>(\\S+)) (?<requestQuery>(.*?)) (?<requestURIExtension>(\\S+)) (?<userAgent>\"?([^\"]*)\"?) (?<serverIP>(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})) (?<scBytes>(\\d+)) (?<csBytes>(\\d+)) (?<virusID>(\\S+)) (\\S+) (\\S+) (?<destinationIP>(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}))?(.*))");
    private String elasticSearchHost, elasticSearchCluster;
    private int elasticSearchPort;
    private Client client;
    private Boolean useCache;
    private LocationStore ubicacion;

    public ProxyLocationParser(String EShost, int ESPort, String clusterName, Boolean useCache) {

	this.elasticSearchHost = EShost;
	this.elasticSearchPort = ESPort;
	this.elasticSearchCluster = clusterName;
	this.useCache = useCache;

    }

    @Override
    public void prepare(@SuppressWarnings("rawtypes") Map conf, TridentOperationContext context) {
	ubicacion = new LocationStore(this.elasticSearchHost, this.elasticSearchPort, this.elasticSearchCluster, this.useCache);
	super.prepare(conf, context);
    }

    @Override
    public void cleanup() {
	client.close();
    }

    @Override
    public void execute(TridentTuple tupla, TridentCollector colector) {
	String ip;
	Localizacion location;
	Matcher matcher = pattern.matcher("");
	List<Object> objetos = tupla.getValues();

	if (objetos.get(0) instanceof String) {
	    matcher = pattern.matcher(tupla.getString(0));
	}
	else if (objetos.get(0) instanceof byte[]) {
	    matcher = pattern.matcher(new String((byte[]) tupla.toArray()[0]));
	}

	if (matcher.find()) {
	    ip = matcher.group("clientIP");
	    location = ubicacion.getLocationRangos(ip);
	    colector.emit(new Values(matcher.group("eventTimeStamp"), matcher.group("timeTaken"), matcher.group("clientIP"), matcher.group("User"), matcher.group("Group"), matcher
		    .group("Exception"), matcher.group("filterResult"), matcher.group("category"), matcher.group("referer"), matcher.group("responseCode"),
		    matcher.group("action"), matcher.group("method"), matcher.group("contentType"), matcher.group("protocol"), matcher.group("requestDomain"), matcher
			    .group("requestPort"), matcher.group("requestPath"), matcher.group("requestQuery"), matcher.group("requestURIExtension"), matcher.group("userAgent"),
		    matcher.group("serverIP"), matcher.group("scBytes"), matcher.group("csBytes"), matcher.group("virusID"), matcher.group("destinationIP"), location
			    .getCoordsString(), location.getCity(), location.getPostalCode(), location.getAreaCode(), location.getMetroCode(), location.getRegion(), location
			    .getCountry()));
	}
    }

}

```

**OpenbusProxyLocationTopology.java**
```
package com.produban.openbus.topologies;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class OpenbusProxyLocationTopology {

    private static Logger LOG = Logger.getLogger(OpenbusProxyLocationTopology.class);    
    public static void main(String[] args) {
	if (args.length != 1) {
	    LOG.debug("uso: <Fichero de propiedades>");
	    System.exit(1);
	}
	
	Properties propiedades = new Properties();
	try {
	    propiedades.load(new FileInputStream(args[0]));
    	    Config conf = new Config();
        
            BrokerSpout openbusBrokerSpout = new BrokerSpout(
            	propiedades.getProperty("KAFKA_TOPIC"),
            	propiedades.getProperty("KAFKA_ZOOKEEPER_LIST"),
            	propiedades.getProperty("KAFAKA_BROKER_ID"),
            	Boolean.getBoolean(propiedades.getProperty("KAFKA_FROM_BEGINNING")));
            
            Fields hdfsFields = new Fields("eventTimeStamp", "timeTaken", "clientIP", "User", "Group", "Exception", "filterResult", "category", "referer", "responseCode", "action",
            	"method", "contentType", "protocol", "requestDomain", "requestPort", "requestPath", "requestQuery", "requestURIExtension", "userAgent", "serverIP", "scBytes",
            	"csBytes", "virusID", "destinationIP", "coords", "city", "postalCode", "areaCode", "metroCode", "region", "country");
            
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
            
            TimeStampRotationPolicy rotationPolicy = new TimeStampRotationPolicy().setTimePeriod(Integer.parseInt(propiedades.getProperty("TIME_ROTATION_VALUE")), unidad_tiempo)
            .setSizeMax(Float.parseFloat(propiedades.getProperty("SIZE_ROTATION_VALUE")), unidad_tamano);
            
            OpenbusHdfsState.Options options = new OpenbusHdfsState.HdfsFileOptions().withFileNameFormat(fileNameFormat).withRecordFormat(recordFormat)
            .withRotationPolicy(rotationPolicy).withFsUrl(propiedades.getProperty("HDFS_URL"))
            .addSyncMillisPeriod(Long.parseLong(propiedades.getProperty("SYNC_MILLIS_PERIOD")));
            System.setProperty("HADOOP_USER_NAME", propiedades.getProperty("HDFS_USER"));
            
            StateFactory factory = new OpenbusHdfsStateFactory().withOptions(options);
            
            TridentTopology topology = new TridentTopology();
            Stream parseaLogs;
            String tipo = propiedades.getProperty("INPUT_ORIGIN");
            
            
            if (tipo.equals("kafka")) { // Si leemos desde Kafka
                parseaLogs = topology.newStream("spout1", openbusBrokerSpout.getPartitionedTridentSpout()).each(
            	    new Fields("bytes"),
            	    new ProxyLocationParser(propiedades.getProperty("ELASTICSEARCH_HOST"), Integer.parseInt(propiedades.getProperty("ELASTICSEARCH_PORT")), propiedades
            		    .getProperty("ELASTICSEARCH_NAME"), Boolean.parseBoolean(propiedades.getProperty("ELASTICSEARCH_CACHE_SEARCH"))), hdfsFields);
                parseaLogs.partitionPersist(factory, hdfsFields, new OpenbusHdfsUpdater(), new Fields());
                if (propiedades.getProperty("KAFKA_OUTPUT_TOPIC") != null && propiedades.getProperty("KAFKA_OUTPUT_TOPIC") != "") {
            	parseaLogs.partitionPersist(
            		new KafkaState.Factory(propiedades.getProperty("KAFKA_OUTPUT_TOPIC"), propiedades.getProperty("KAFKA_ZOOKEEPER_LIST"), 
            			propiedades.getProperty("KAFKA_BROKER_HOSTS"), recordFormat, propiedades.getProperty("KAFKA_TOPIC")), hdfsFields, new KafkaState.Updater());
                }
            }
            
            if (tipo.equals("disco")) { // Si leemos desde un fichero de disco local
                SimpleFileStringSpout spout1 = new SimpleFileStringSpout(propiedades.getProperty("INPUT_FILE"), "bytes");
                parseaLogs = topology.newStream("spout1", spout1).each(
            	    new Fields("bytes"),
            	    new ProxyLocationParser(propiedades.getProperty("ELASTICSEARCH_HOST"), Integer.parseInt(propiedades.getProperty("ELASTICSEARCH_PORT")), 
            		    propiedades.getProperty("ELASTICSEARCH_NAME"), Boolean.parseBoolean(propiedades.getProperty("ELASTICSEARCH_CACHE_SEARCH"))), hdfsFields);
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
	catch (FileNotFoundException e) {
	    LOG.error(e);
	}
	catch (IOException e) {
	    LOG.error(e);
	}
	catch (AlreadyAliveException | InvalidTopologyException e) {
	    LOG.error(e);
	}
    }
}
```
