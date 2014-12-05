package com.produban.openbus.topologies;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;


public class RadiusEntityParser extends BaseFunction{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5370134728957703065L;

	public static final char SEPARADOR='\001';//'\001'; //caracter SOH
	public static Pattern pattern = Pattern.compile(
			"(?<ID>\"(\\d+),(\\d+)\"),"
			+"(?<MESSAGETEXT>[^,]*),"
			+"(?<ACSTIMESTAMP>(\\d+)/(\\d+)/(\\d+)(\\s)(\\d+):(\\d+):(\\d+)(\\s)(AM|PM)),"
			+"(?<ACSVIEWTIMESTAMP>(\\d+)/(\\d+)/(\\d+)(\\s)(\\d+):(\\d+):(\\d+)(\\s)(AM|PM)),"
			+"(?<ACSSERVER>[^,]*),"
			+"(?<ACSSESSIONID>[^,]*),"
			+"(?<ACCESSSERVICE>[^,]*),"
			+"(?<SERVICESELECTIONPOLICY>[^,]*),"
			+"(?<AUTHORIZATIONPOLICY>[^,]*),"
			+"(?<USERNAME>[^,]*),"
			+"(?<IDENTITYSTORE>[^,]*),"
			+"(?<AUTHENTICATIONMETHOD>[^,]*),"
			+"(?<NETWORKDEVICENAME>[^,]*),"
			+"(?<IDENTITYGROUP>[^,]*),"
			+"(?<NETWORKDEVICEGROUPS>\"[^\".]*\"),"
			+"(?<CALLINGSTATIONID>((\\S{2}-\\S{2}-\\S{2}-\\S{2}-\\S{2}-\\S{2})|(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}))*),"
			+"(?<NASPORT>(\\d+)),"
			+"(?<SERVICETYPE>[^,]*),"
			+"(?<AUDITSESSIONID>[^,]*),"
			+"(?<CTSSECURITYGROUP>[^,]*),"
			+"(?<FAILUREREASON>[^,]*),"
			+"(?<USECASE>[^,]*),"
			+"(?<FRAMEDIPADDRESS>[^,]*),"
			+"(?<NASIDENTIFIER>[^,]*),"
			+"(?<NASIPADDRESS>(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})*),"
			+"(?<NASPORTID>[^,]*),"
			+"(?<CISCOAVPAIR>[^,]*),"
			+"(?<ADDOMAIN>[^,]*),"
			+"(?<RESPONSETIME>((\\d*)|\"(\\d+),(\\d+)\")),"
			+"(?<PASSED>(\\d*)),"
			+"(?<FAILED>(\\d*)),"
			+"(?<AUTHENTICATIONSTATUS>[^,]*),"
			+"(?<RADIUSDAIGNOSTICLINK>[^,]*),"
			+"(?<ACTIVESESSIONLINK>[^,]*),"
			+"(?<ACSUSERNAME>[^,]*),"
			+"(?<NACROLE>[^,]*),"
			+"(?<NACPOLICYCOMPLIANCE>[^,]*),"
			+"(?<NACUSERNAME>[^,]*),"
			+"(?<NACPOSTURETOKEN>[^,]*),"
			+"(?<SELECTEDPOSTURESERVER>[^,]*),"
			+"(?<SELECTEDIDENTITYSTORE>((\"[^\".]*\")|([^,.]*))),"
			+"(?<AUTHENTICATIONIDENTITYSTORE>[^,]*),"
			+"(?<AUTHORIZATIONEXCEPTIONPOLICYMATCHEDRULE>[^,]*),"
			+"(?<EXTERNALPOLICYSERVERMATCHEDRULE>[^,]*),"
			+"(?<GROUPMAPPINGPOLICYMATCHEDRULE>[^,]*),"
			+"(?<IDENTITYPOLICYMATCHEDRULE>[^,]*),"
			+"(?<NASPORTTYPE>[^,]*),"
			+"(?<QUERYIDENTITYSTORES>[^,]*),"
			+"(?<SELECTEDAUTHORIZATIONPROFILES>[^,]*),"
			+"(?<SELECTEDEXCEPTIONAUTHORIZATIONPROFILES>[^,]*),"
			+"(?<SELECTEDQUERYIDENTITYSTORES>[^,]*),"
			+"(?<TUNNELDETAILS>(\"[^\"]*\"|[^,]*)),"
			+"(?<CISCOH323ATTRIBUTES>[^,]*),"
			+"(?<CISCOSSGATTRIBUTES>[^,]*),"
			+"(?<OTHERATTRIBUTES>(\"[^\"]*\"|[^,]*)),"
			+"(?<MOREDETAILS>[^,]*),"
			+"(?<EAPTUNNEL>[^,]*),"
			+"(?<EAPAUTHENTICATION>[^,]*),"
			+"(?<EAPTUNNEL2>[^,]*),"
			+"(?<EAPAUTHENTICATION2>[^,]*),"
			+"(?<RADIUSUSERNAME>[^,]*),"
			+"(?<NASFAILURE>[^,]*),"
			+"(?<TIMESTAMP>(\\d+)-(\\d+)-(\\d+)(\\s)(\\d+)\\:(\\d+)\\:(\\d+)\\.(\\d+)),"
			+"(?<RESPONSE>(\\{[^\\}]*\\})*),"
			+"(?<TOTALCOLUMN0>[^,]*),"
			+"(?<TOTALCOLUMN1>[^,]*)"
			);
	
	private String formatFecha(String entrada){
		
		String[] partes = entrada.split(" ");
		if(partes.length==3){
			String fecha = partes[0];
			String hora = partes[1];
			partes=fecha.split("/");
			if(partes.length==3){
				String dia=partes[0];
				String mes=partes[1];
				String anho=partes[2];
				
				return anho+"-"+String.format("%02d",Integer.parseInt(mes))+"-"+String.format("%02d",Integer.parseInt(dia))+" "+hora;
			}
			
		}
		return null;
	}
	
	private long timeStampMillis(String timestamp){
		Calendar c = Calendar.getInstance();
		
		String[] partes = timestamp.split(" ");
		if(partes.length==3){
			String fecha = partes[0];
			String horaMinSec = partes[1];
			partes=fecha.split("/");
			if(partes.length==3){
				String dia=partes[0];
				String mes=partes[1];
				String anho=partes[2];
				partes=horaMinSec.split(":");
				if(partes.length==3){
					c.set(Integer.parseInt(anho), Integer.parseInt(mes), Integer.parseInt(dia),Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
					return c.getTimeInMillis();
				}
			}
			
		}
		
		
		return 0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(TridentTuple tupla, TridentCollector colector) {
		// TODO Auto-generated method stub
		Matcher matcher=pattern.matcher("");

		List objetos =tupla.getValues();		
		
		if (objetos.get(0) instanceof String){
			matcher = pattern.matcher(tupla.getString(0));
		}else if(objetos.get(0) instanceof byte[]){
			matcher = pattern.matcher(new String((byte[]) tupla.toArray()[0]));
		}
		
		if(matcher.find()){
			
			colector.emit(new Values(
			matcher.group("ID").replace(",", "").replace("\"", "")
			,matcher.group("MESSAGETEXT")
			,String.valueOf(timeStampMillis(matcher.group("ACSTIMESTAMP"))) //Campo de timestamp como millis
			,formatFecha(matcher.group("ACSTIMESTAMP"))
			,formatFecha(matcher.group("ACSVIEWTIMESTAMP"))
			,matcher.group("ACSSERVER")
			,matcher.group("ACSSESSIONID")
			,matcher.group("ACCESSSERVICE")
			,matcher.group("SERVICESELECTIONPOLICY")
			,matcher.group("AUTHORIZATIONPOLICY")
			,matcher.group("USERNAME")
			,matcher.group("IDENTITYSTORE")
			,matcher.group("AUTHENTICATIONMETHOD")
			,matcher.group("NETWORKDEVICENAME")
			,matcher.group("IDENTITYGROUP")
			,matcher.group("NETWORKDEVICEGROUPS")
			,matcher.group("CALLINGSTATIONID")
			,matcher.group("NASPORT")
			,matcher.group("SERVICETYPE")
			,matcher.group("AUDITSESSIONID")
			,matcher.group("CTSSECURITYGROUP")
			,matcher.group("FAILUREREASON")
			,matcher.group("USECASE")
			,matcher.group("FRAMEDIPADDRESS")
			,matcher.group("NASIDENTIFIER")
			,matcher.group("NASIPADDRESS")
			,matcher.group("NASPORTID")
			,matcher.group("CISCOAVPAIR")
			,matcher.group("ADDOMAIN")
			,matcher.group("RESPONSETIME").replace(",","").replace("\"", "") //quitamos el separador de miles si existe
			,matcher.group("PASSED")
			,matcher.group("FAILED")
			,matcher.group("AUTHENTICATIONSTATUS")
			,matcher.group("RADIUSDAIGNOSTICLINK")
			,matcher.group("ACTIVESESSIONLINK")
			,matcher.group("ACSUSERNAME")
			,matcher.group("NACROLE")
			,matcher.group("NACPOLICYCOMPLIANCE")
			,matcher.group("NACUSERNAME")
			,matcher.group("NACPOSTURETOKEN")
			,matcher.group("SELECTEDPOSTURESERVER")
			,matcher.group("SELECTEDIDENTITYSTORE")
			,matcher.group("AUTHENTICATIONIDENTITYSTORE")
			,matcher.group("AUTHORIZATIONEXCEPTIONPOLICYMATCHEDRULE")
			,matcher.group("EXTERNALPOLICYSERVERMATCHEDRULE")
			,matcher.group("GROUPMAPPINGPOLICYMATCHEDRULE")
			,matcher.group("IDENTITYPOLICYMATCHEDRULE")
			,matcher.group("NASPORTTYPE")
			,matcher.group("QUERYIDENTITYSTORES")
			,matcher.group("SELECTEDAUTHORIZATIONPROFILES")
			,matcher.group("SELECTEDEXCEPTIONAUTHORIZATIONPROFILES")
			,matcher.group("SELECTEDQUERYIDENTITYSTORES")
			,matcher.group("TUNNELDETAILS")
			,matcher.group("CISCOH323ATTRIBUTES")
			,matcher.group("CISCOSSGATTRIBUTES")
			,matcher.group("OTHERATTRIBUTES")
			,matcher.group("MOREDETAILS")
			,matcher.group("EAPTUNNEL")
			,matcher.group("EAPAUTHENTICATION")
			,matcher.group("EAPTUNNEL2")
			,matcher.group("EAPAUTHENTICATION2")
			,matcher.group("RADIUSUSERNAME")
			,matcher.group("NASFAILURE")
			,matcher.group("TIMESTAMP")
			,matcher.group("RESPONSE")
			,matcher.group("TOTALCOLUMN0")
			,matcher.group("TOTALCOLUMN1")));
		}
	}
}
