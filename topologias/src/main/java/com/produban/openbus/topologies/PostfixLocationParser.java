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
	    //LOG.info(location.getCoordsString()+location.getCity()+location.getPostalCode()+location.getAreaCode()+location.getMetroCode()+location.getRegion()+location.getCountry());
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
