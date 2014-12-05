package com.produban.openbus.topologies;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class IronportParser extends BaseFunction {

    private static final long serialVersionUID = 1L;
    public static final char SEPARADOR = '\001'; // caracter SOH
    public static Pattern pattern = Pattern
	    .compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+<\\d+>(?<eventTimeStamp>(.{15}))\\s+(\\S+):(\\s+(?:Info:|Warning:))?(( (Bounced\\s+by\\s+destination\\s+server\\s+with\\s+response:\\s+(?<DSNBOUNCE>(\\d+\\.\\d+\\.\\d+))\\s+-\\s+(?<BOUNCEDESC>(.*))|Delayed:(\\s+)DCID(\\s+)(?<DCIDDELAY>(\\d+))(\\s+)MID(\\s+)(?<MIDDELAY>(\\d+))(\\s+)to(\\s+)RID(\\s+)(?<RIDDELAY>(\\d+))(\\s+)-(\\s+)(?<DSNDELAY>(\\d+\\.\\d+\\.\\d+))(\\s+)-(\\s+)(?<DELAYDESC>(.*))|Dropped(\\s)by(\\s)(?<SPAMCASE>CASE)|MID\\s+(?<MID>(\\d+))(\\s+antivirus\\s+(?<ANTIVIRUS>(\\S+)))?|[^t]+(\\s)using(\\s)engine:(\\s)CASE(\\s)(?<MARKETINGCASE>marketing)|RID\\s+\\[?(?<RID>((\\d+)(,\\s\\d+)*))\\]?|ICID\\s+(?<ICID>(\\d+))|DCID\\s+(?<DCID>(\\d+))|Subject (?<SUBJECT>(?:'|)(.*?)(?:'|))$|Message-ID (?:'|)(.*?)(?:'|)|(?:From:|from) <(?<FROM>(.*?))>$|To: <(?<TO>(.*?))>$|Response (?<RESPONSE>'(.*?)')|ready (?<BYTES>(.*?)) bytes|interface (?<INTERFACE>(\\d+\\.\\d+\\.\\d+\\.\\d+))|port (?<PORT>(\\d+))|\\((?<INTERFACEIP>(\\d+\\.\\d+\\.\\d+\\.\\d+))\\)(\\s)address(\\s)(?<HOSTIP>(\\d+\\.\\d+\\.\\d+\\.\\d+))(\\s)reverse(\\s)dns(\\s)host(\\s+)(?<HOSTNAME>(\\S+))(\\s)verified(\\s)(?<HOSTVERIFIED>(\\S+))$|((REJECT|TCPREFUSE) SG (?<REPUTATION>(\\S+))(\\s)match(\\s)sbrs(?<RANGO>\\[(\\S+)\\])(\\s)SBRS(\\s)(?<SCORE>(\\S+)))|\\(content filter:(?<FILTROCONTENIDO>(\\S+))\\)|(?<RESTO>(.*?))))*)$");

    private String origen;

    public IronportParser(String origen) {
	this.origen = origen;
    }

    @Override
    public void execute(TridentTuple tupla, TridentCollector colector) {

	Matcher matcher;
	Matcher matcherSubPat;
	String amavisId = null;

	if (origen.equals("disco")) {
	    matcher = pattern.matcher(tupla.getString(0));
	}
	else {
	    matcher = pattern.matcher(new String((byte[]) tupla.toArray()[0]));
	}

	// el ID de AMAVIS está dentro de la expresión de STATUSDESC

	if (matcher.find()) {
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
				.group("REPUTATION"), matcher.group("RANGO"), matcher.group("SCORE"), matcher.group("FILTROCONTENIDO"), matcher.group("MARKETINGCASE")));
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
