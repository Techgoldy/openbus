#Postfix

**postfixLocation.properties**
```
#Configuración acceso HDFS
HDFS_URL=hdfs://180.133.240.175:8020
HDFS_USER=hdfs
HDFS_OUTPUT_DIR=/user/hdfs/input/postfix
HDFS_OUTPUT_FILENAME=TridentPostfixLocationTopology
HDFS_OUTPUT_FILE_EXTENSION=.txt
INPUT_ORIGIN=kafka

#Configuración de KAFKA
KAFKA_ZOOKEEPER_LIST=180.133.240.174:2181,180.133.240.175:2181,180.133.240.176:2181
KAFAKA_BROKER_ID=1
KAFKA_TOPIC=ob_src_postfix
KAFKA_FROM_BEGINNING=false

#Configuración STORM
STORM_CLUSTER=servidor
STORM_BATCH_MILLIS=700
STORM_NUM_WORKERS=1
STORM_MAX_SPOUT_PENDING=1
STORM_TOPOLOGY_NAME=ob_src_postfix

#Propiedades de la rotación
SIZE_ROTATION_UNIT=MB
SIZE_ROTATION_VALUE=64
TIME_ROTATION_UNIT=HOUR
TIME_ROTATION_VALUE=1

#Propiedades de la sincronización/escritura a HDFS
SYNC_MILLIS_PERIOD=10000
#Configuración ElasticSearch
ELASTICSEARCH_HOST=180.133.240.42
ELASTICSEARCH_PORT=9300
ELASTICSEARCH_NAME=elasticsearch
ELASTICSEARCH_CACHE_SEARCH=true
```
**PostfixLocationParser.java**
```
package com.produban.openbus.topologies;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class PostfixLocationParser extends BaseFunction {
    
    private static final long serialVersionUID = 1L;
    private static Logger LOG = Logger.getLogger(PostfixLocationParser.class);
    public static final char SEPARADOR = '\001';
    public static Pattern pattern = Pattern.compile("(?<EVENTTIMESTAMP>(.{15}))(\\s)(\\S+)(\\s)postfix/(smtpd\\[(?<SMTPDID>(\\d+))\\]|cleanup\\[(?<CLEANUPID>(\\d+))\\]|qmgr\\[(?<QMGRID>(\\d+))\\]|smtp\\[(?<SMTPID>(\\d+))\\]|error\\[(?<ERRORID>(\\d+))\\]):((\\s+)(?<MSGID>(\\S+)):)?(\\s+)(client=(?<CLIENTE>(\\S+))\\[(?<CLIENTEIP>(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+))\\]|(?<ACCION>(connect(\\s)from(\\s)|disconnect(\\s)from(\\s)))(?<SERVER>(\\S+))\\[(?<SERVERIP>(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+))\\]|message-id=<(?<MESSAGEID>(\\S+))>|from=<(?<FROM>([^>]*)?)>,(\\s)size=(?<SIZE>(\\d+)),(\\s)nrcpt=(?<NRCPT>(\\d+))(\\s)(?<NRCPTDEC>((\\S+)(\\s)?)*)$|to=<(?<TO>(\\S+))>,(\\s)relay=(?<TOSERVERNAME>(\\S+)?)\\[(?<TOSERVERIP>(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+))\\]:(?<TOSERVERPORT>(\\d+))(,(\\s)conn_use=(\\d*))?,(\\s)delay=(?<DELAY>(\\d+)[.,]?(\\d*)),(\\s+)delays=((\\d+)[.,]?(\\d*))/((\\d+)[.,]?(\\d*))/((\\d+)[.,]?(\\d*))/((\\d+)[.,]?(\\d*)),(\\s)dsn=(?<DSN>(\\S+)),(\\s)status=(?<STATUS>(\\S+))(\\s)(?<STATUSDESC>\\((.*)\\)$))");
    public static Pattern subpattern = Pattern.compile("((\\s)queued(\\s)as(\\s)(?<AMAVISID>(\\S+))\\))");

    private String origen;
    private String elasticSearchHost;
    private String elasticSearchCluster;
    private int elasticSearchPort;
    private Boolean useCache;
    
    private Client client;
    private LocationStore ubicacion;
    
    public PostfixLocationParser(String EShost, int ESPort, String clusterName, Boolean useCache) {
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
	Matcher matcherSubPat;
	String amavisId = null;
	String ip;
	Localizacion location;
	

	List<Object> objetos = tupla.getValues();
	Matcher matcher = pattern.matcher("");
	
	if (objetos.get(0) instanceof String) {
	    matcher = pattern.matcher(tupla.getString(0));
	}
	else if (objetos.get(0) instanceof byte[]) {
	    matcher = pattern.matcher(new String((byte[]) tupla.toArray()[0]));
	}

	if (matcher.find()) {
	    ip = matcher.group("CLIENTEIP");
	    location = ubicacion.getLocationRangos(ip);
	    
	    if (matcher.group("STATUSDESC") != null) {
		matcherSubPat = subpattern.matcher(matcher.group("STATUSDESC"));
		if (matcherSubPat.find()) {
		    amavisId = matcherSubPat.group("AMAVISID");
		}
	    }
	    colector.emit(new Values(fechaFormato(matcher.group("EVENTTIMESTAMP")), matcher.group("SMTPDID"), matcher.group("MSGID"), matcher.group("CLEANUPID"), 
		    matcher.group("QMGRID"), matcher.group("SMTPID"), matcher.group("ERRORID"), matcher.group("CLIENTE"), matcher.group("CLIENTEIP"), 
		    matcher.group("ACCION"), matcher.group("SERVER"), matcher.group("SERVERIP"), matcher.group("MESSAGEID"), matcher.group("FROM"), 
		    matcher.group("SIZE"), matcher.group("NRCPT"), matcher.group("TO"), matcher.group("TOSERVERNAME"), matcher.group("TOSERVERIP"), 
		    matcher.group("TOSERVERPORT"), matcher.group("DELAY"), matcher.group("DSN"), matcher.group("STATUS"), matcher.group("STATUSDESC"), 
		    amavisId, location.getCoordsString(), location.getCity(), location.getPostalCode(),location.getAreaCode(), location.getMetroCode(), 
		    location.getRegion(), location.getCountry() 
	    ));
	    LOG.info(location.getCoordsString()+location.getCity()+location.getPostalCode()+location.getAreaCode()+location.getMetroCode()+location.getRegion()+location.getCountry());
	}
    }

    public String fechaFormato(String entrada) {
	String salida = entrada;
	if (entrada != null && entrada.length() == 15) {
	    Calendar c = Calendar.getInstance();
	    String mesString = entrada.substring(0, 3);
	    String mes = "00";
	    if (mesString.toLowerCase().equals("jan"))
		mes = "01";
	    else if (mesString.toLowerCase().equals("feb"))
		mes = "02";
	    else if (mesString.toLowerCase().equals("mar"))
		mes = "03";
	    else if (mesString.toLowerCase().equals("apr"))
		mes = "04";
	    else if (mesString.toLowerCase().equals("may"))
		mes = "05";
	    else if (mesString.toLowerCase().equals("jun"))
		mes = "06";
	    else if (mesString.toLowerCase().equals("jul"))
		mes = "07";
	    else if (mesString.toLowerCase().equals("aug"))
		mes = "08";
	    else if (mesString.toLowerCase().equals("sep"))
		mes = "09";
	    else if (mesString.toLowerCase().equals("oct"))
		mes = "10";
	    else if (mesString.toLowerCase().equals("nov"))
		mes = "11";
	    else if (mesString.toLowerCase().equals("dec"))
		mes = "12";
	    String dia = entrada.substring(4, 6);
	    int anho = c.get(Calendar.YEAR);
	    if (mes.equals("12")) { // diciembre es el mes 0 del año siguiente
		anho--;
	    }
	    char decena = entrada.charAt(4);
	    if (decena == ' ')
		dia = "0" + entrada.charAt(5);
	    salida = anho + "-" + mes + "-" + dia + " " + entrada.substring(7, 9) + ":" + entrada.substring(10, 12) + ":" + entrada.substring(13, 15);
	}
	return salida;
    }
}

```
**OpenbusPostfixLocationTopology**

```
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
	    	Boolean.getBoolean(propiedades.getProperty("KAFKA_FROM_BEGINNING")));
	    
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
```
