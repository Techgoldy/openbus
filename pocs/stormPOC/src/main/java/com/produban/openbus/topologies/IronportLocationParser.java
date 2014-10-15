package com.produban.openbus.topologies;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

public class IronportLocationParser extends BaseFunction {

    private static final long serialVersionUID = 1L;
    private static Logger LOG = Logger.getLogger(IronportLocationParser.class);
    // private static final Logger log =
    // LoggerFactory.getLogger(PostfixParser.class);
    public static final char SEPARADOR = '\001'; // caracter SOH
    public static Pattern pattern = Pattern
	    .compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+<\\d+>(?<eventTimeStamp>(.{15}))\\s+(\\S+):(\\s+(?:Info:|Warning:))?(( (Bounced\\s+by\\s+destination\\s+server\\s+with\\s+response:\\s+(?<DSNBOUNCE>(\\d+\\.\\d+\\.\\d+))\\s+-\\s+(?<BOUNCEDESC>(.*))|Delayed:(\\s+)DCID(\\s+)(?<DCIDDELAY>(\\d+))(\\s+)MID(\\s+)(?<MIDDELAY>(\\d+))(\\s+)to(\\s+)RID(\\s+)(?<RIDDELAY>(\\d+))(\\s+)-(\\s+)(?<DSNDELAY>(\\d+\\.\\d+\\.\\d+))(\\s+)-(\\s+)(?<DELAYDESC>(.*))|Dropped(\\s)by(\\s)(?<SPAMCASE>CASE)|MID\\s+(?<MID>(\\d+))(\\s+antivirus\\s+(?<ANTIVIRUS>(\\S+)))?|[^t]+(\\s)using(\\s)engine:(\\s)CASE(\\s)(?<MARKETINGCASE>marketing)|RID\\s+\\[?(?<RID>((\\d+)(,\\s\\d+)*))\\]?|ICID\\s+(?<ICID>(\\d+))|DCID\\s+(?<DCID>(\\d+))|Subject (?<SUBJECT>(?:'|)(.*?)(?:'|))$|Message-ID (?:'|)(.*?)(?:'|)|(?:From:|from) <(?<FROM>(.*?))>$|To: <(?<TO>(.*?))>$|Response (?<RESPONSE>'(.*?)')|ready (?<BYTES>(.*?)) bytes|interface (?<INTERFACE>(\\d+\\.\\d+\\.\\d+\\.\\d+))|port (?<PORT>(\\d+))|\\((?<INTERFACEIP>(\\d+\\.\\d+\\.\\d+\\.\\d+))\\)(\\s)address(\\s)(?<HOSTIP>(\\d+\\.\\d+\\.\\d+\\.\\d+))(\\s)reverse(\\s)dns(\\s)host(\\s+)(?<HOSTNAME>(\\S+))(\\s)verified(\\s)(?<HOSTVERIFIED>(\\S+))$|((REJECT|TCPREFUSE) SG (?<REPUTATION>(\\S+))(\\s)match(\\s)sbrs(?<RANGO>\\[(\\S+)\\])(\\s)SBRS(\\s)(?<SCORE>(\\S+)))|\\(content filter:(?<FILTROCONTENIDO>(\\S+))\\)|(?<RESTO>(.*?))))*)$");

    private String elasticSearchHost;
    private String elasticSearchCluster;
    private int elasticSearchPort;
    private Boolean useCache;
    
    private Client client;
    private LocationStore ubicacion;
    
    public IronportLocationParser(String EShost, int ESPort, String clusterName, Boolean useCache) {
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
	List<Object> objetos = tupla.getValues();
	Matcher matcher = pattern.matcher("");
	Localizacion location;
	String ip;
	
	if (objetos.get(0) instanceof String) {
	    matcher = pattern.matcher(tupla.getString(0));
	}
	else if (objetos.get(0) instanceof byte[]) {
	    matcher = pattern.matcher(new String((byte[]) tupla.toArray()[0]));
	}

	// el ID de AMAVIS está dentro de la expresión de STATUSDESC

	if (matcher.find()) {
	    ip = matcher.group("INTERFACEIP");
	    location = ubicacion.getLocationRangos(ip);
	    
	    String[] destin;
	    if (matcher.group("RID") != null) {
		destin = matcher.group("RID").split(", ");
	    }
	    else {
		destin = new String[1];
		destin[0] = null;
	    }
	    for (int i = 0; i < destin.length; i++) {
		colector.emit(new Values(fechaFormato(matcher.group("eventTimeStamp")), matcher.group("ICID"), matcher.group("MID"), destin[i] // RID
			, matcher.group("DCID"), matcher.group("SUBJECT"), matcher.group("FROM"), matcher.group("TO"), matcher.group("RESPONSE"), matcher.group("BYTES"), matcher
				.group("INTERFACE"), matcher.group("PORT"), matcher.group("INTERFACEIP"), matcher.group("HOSTIP"), matcher.group("HOSTNAME"), matcher
				.group("HOSTVERIFIED"), matcher.group("DSNBOUNCE"), matcher.group("BOUNCEDESC"), matcher.group("SPAMCASE"), matcher.group("DCIDDELAY"), matcher
				.group("MIDDELAY"), matcher.group("RIDDELAY"), matcher.group("DSNDELAY"), matcher.group("DELAYDESC"), matcher.group("ANTIVIRUS"), matcher
				.group("REPUTATION"), matcher.group("RANGO"), matcher.group("SCORE"), matcher.group("FILTROCONTENIDO"), matcher.group("MARKETINGCASE"),
				location.getCoordsString(), location.getCity(), location.getPostalCode(),location.getAreaCode(), location.getMetroCode(), 
				    location.getRegion(), location.getCountry()));
		LOG.debug(location.getCoordsString()+location.getCity()+location.getPostalCode()+location.getAreaCode()+location.getMetroCode()+location.getRegion()+location.getCountry());		
	    }
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
