package com.produban.openbus.topologies;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;



import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.Constants;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;


public class PostfixParser extends BaseFunction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private static final Logger log = LoggerFactory.getLogger(PostfixParser.class);
	public static final char SEPARADOR='\001'; //caracter SOH
	//public static Pattern pattern = Pattern.compile("(?<EVENTTIMESTAMP>(.{15}))(\\s)(\\S+)(\\s)postfix/(smtpd\\[(?<SMTPDID>(\\d+))\\]|cleanup\\[(?<CLEANUPID>(\\d+))\\]|qmgr\\[(?<QMGRID>(\\d+))\\]|smtp\\[(?<SMTPID>(\\d+))\\]|error\\[(?<ERRORID>(\\d+))\\]):((\\s+)(?<MSGID>(\\S+)):)?(\\s+)(client=(?<CLIENTE>(\\S+))\\[(?<CLIENTEIP>(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+))\\]|(?<ACCION>(connect(\\s)from(\\s)|disconnect(\\s)from(\\s)))(?<SERVER>(\\S+))\\[(?<SERVERIP>(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+))\\]|message-id=<(?<MESSAGEID>(\\S+))>|from=<(?<FROM>(\\S+)?)>,(\\s)size=(?<SIZE>(\\d+)),(\\s)nrcpt=(?<NRCPT>(\\d+))(\\s)(?<NRCPTDEC>((\\S+)(\\s)?)*)$|to=<(?<TO>(\\S+))>,(\\s)relay=(?<TOSERVERNAME>(\\S+)?)\\[(?<TOSERVERIP>(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+))\\]:(?<TOSERVERPORT>(\\d+)),(\\s)delay=(?<DELAY>(\\d+)[.,]?(\\d*)),(\\s+)delays=((\\d+)[.,]?(\\d*))/((\\d+)[.,]?(\\d*))/((\\d+)[.,]?(\\d*))/((\\d+)[.,]?(\\d*)),(\\s)dsn=(?<DSN>(\\S+)),(\\s)status=(?<STATUS>(\\S+))(\\s)(?<STATUSDESC>\\((.*)((\\s)queued(\\s)as(\\s)(?<AMAVISID>(\\S+)))?\\)))");
	public static Pattern pattern = Pattern.compile("(?<EVENTTIMESTAMP>(.{15}))(\\s)(\\S+)(\\s)postfix/(smtpd\\[(?<SMTPDID>(\\d+))\\]|cleanup\\[(?<CLEANUPID>(\\d+))\\]|qmgr\\[(?<QMGRID>(\\d+))\\]|smtp\\[(?<SMTPID>(\\d+))\\]|error\\[(?<ERRORID>(\\d+))\\]):((\\s+)(?<MSGID>(\\S+)):)?(\\s+)(client=(?<CLIENTE>(\\S+))\\[(?<CLIENTEIP>(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+))\\]|(?<ACCION>(connect(\\s)from(\\s)|disconnect(\\s)from(\\s)))(?<SERVER>(\\S+))\\[(?<SERVERIP>(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+))\\]|message-id=<(?<MESSAGEID>(\\S+))>|from=<(?<FROM>([^>]*)?)>,(\\s)size=(?<SIZE>(\\d+)),(\\s)nrcpt=(?<NRCPT>(\\d+))(\\s)(?<NRCPTDEC>((\\S+)(\\s)?)*)$|to=<(?<TO>(\\S+))>,(\\s)relay=(?<TOSERVERNAME>(\\S+)?)\\[(?<TOSERVERIP>(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+))\\]:(?<TOSERVERPORT>(\\d+))(,(\\s)conn_use=(\\d*))?,(\\s)delay=(?<DELAY>(\\d+)[.,]?(\\d*)),(\\s+)delays=((\\d+)[.,]?(\\d*))/((\\d+)[.,]?(\\d*))/((\\d+)[.,]?(\\d*))/((\\d+)[.,]?(\\d*)),(\\s)dsn=(?<DSN>(\\S+)),(\\s)status=(?<STATUS>(\\S+))(\\s)(?<STATUSDESC>\\((.*)\\)$))");
	public static Pattern subpattern = Pattern.compile("((\\s)queued(\\s)as(\\s)(?<AMAVISID>(\\S+))\\))");
	
	private String origen;
	
	public PostfixParser(String origen){
		this.origen=origen;
		
	}
	
	@Override
	public void execute(TridentTuple tupla, TridentCollector colector) {
		
		Matcher matcher;
		Matcher matcherSubPat;
		String amavisId=null;
			
		if(origen.equals("disco")){
			matcher = pattern.matcher(tupla.getString(0));
		}else{
			matcher = pattern.matcher(new String((byte[]) tupla.toArray()[0]));
		}
		
		// el ID de AMAVIS está dentro de la expresión de STATUSDESC
				
		if (matcher.find()) {
			if(matcher.group("STATUSDESC")!=null){
				matcherSubPat=subpattern.matcher(matcher.group("STATUSDESC"));
				if(matcherSubPat.find()){
					amavisId=matcherSubPat.group("AMAVISID");
				}
			}
			colector.emit(new Values(
					fechaFormato(matcher.group("EVENTTIMESTAMP"))
					,matcher.group("SMTPDID")
					,matcher.group("MSGID")
					,matcher.group("CLEANUPID")
					,matcher.group("QMGRID")
					,matcher.group("SMTPID")
					,matcher.group("ERRORID")
					,matcher.group("CLIENTE")
					,matcher.group("CLIENTEIP")
					,matcher.group("ACCION")
					,matcher.group("SERVER")
					,matcher.group("SERVERIP")
					,matcher.group("MESSAGEID")
					,matcher.group("FROM")
					,matcher.group("SIZE")
					,matcher.group("NRCPT")
					,matcher.group("TO")
					,matcher.group("TOSERVERNAME")
					,matcher.group("TOSERVERIP")
					,matcher.group("TOSERVERPORT")
					,matcher.group("DELAY")
					,matcher.group("DSN")
					,matcher.group("STATUS")
					,matcher.group("STATUSDESC")
					,amavisId  //	,matcher.group("AMAVISID")
					));
		}
	}
	
	public String fechaFormato(String entrada){
		String salida=entrada;
		if(entrada!=null && entrada.length()==15){
			Calendar c =  Calendar.getInstance();
			String mesString =entrada.substring(0,3);
			String mes="00";
			if (mesString.toLowerCase().equals("jan")) mes="01";
			else if (mesString.toLowerCase().equals("feb")) mes="02";
			else if (mesString.toLowerCase().equals("mar")) mes="03";
			else if (mesString.toLowerCase().equals("apr")) mes="04";
			else if (mesString.toLowerCase().equals("may")) mes="05";
			else if (mesString.toLowerCase().equals("jun")) mes="06";
			else if (mesString.toLowerCase().equals("jul")) mes="07";
			else if (mesString.toLowerCase().equals("aug")) mes="08";
			else if (mesString.toLowerCase().equals("sep")) mes="09";
			else if (mesString.toLowerCase().equals("oct")) mes="10";
			else if (mesString.toLowerCase().equals("nov")) mes="11";
			else if (mesString.toLowerCase().equals("dec")) mes="12";
			String dia=entrada.substring(4,6);
			int anho=c.get(Calendar.YEAR);
			if(mes.equals("12")){ //diciembre es el mes 0 del año siguiente
				anho--;
			}
			char decena=entrada.charAt(4);
			if (decena==' ') dia="0"+entrada.charAt(5);
			salida=anho+"-"+mes+"-"+dia+" "+entrada.substring(7,9)+":"+entrada.substring(10,12)+":"+ entrada.substring(13,15);
			//c.set(c.get(Calendar.YEAR), mes, Integer.parseInt(entrada.substring(4,6)), Integer.parseInt(entrada.substring(7,9)), Integer.parseInt(entrada.substring(10,12)), Integer.parseInt(entrada.substring(13,15)));
		}

		return salida;
	}
	
	private static boolean isTickTuple(Tuple tuple) {
		return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
			&& tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
	    }

	
}
