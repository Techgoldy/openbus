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

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    // private static final Logger log = (Logger)
    // LoggerFactory.getLogger(ProxyParser.class);
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
	// TODO Auto-generated method stub
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
	// TODO Auto-generated method stub
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
