package com.produban.openbus.topologies;

import java.util.Calendar;

public class pruebaFechas {
	 public static final long 
     HOUR=3600000,
     MINUTE=60000,
     SECOND=1000,
     DAY=86400000;
	
	public static void main(String[] args){
		

		String ini="2014-12-23 12:24:40";
		String fin="2014-12-23 12:24:45";
		Calendar eventTimeStamp=Calendar.getInstance();
		Calendar ultimoRegistro=Calendar.getInstance();
		long unidad=1000;
		long periodo=4;
		long diferenciaMillisPermitida= unidad*periodo;
		
		eventTimeStamp.set(Integer.parseInt(ini.substring(0,4)), Integer.parseInt(ini.substring(5,7)),Integer.parseInt(ini.substring(8,10)), 
			Integer.parseInt(ini.substring(11,13)), Integer.parseInt(ini.substring(14,16)), Integer.parseInt(ini.substring(17,19)));
	
		System.out.println(Integer.parseInt(ini.substring(0,4))+"/"+ Integer.parseInt(ini.substring(5,7))+"/"+Integer.parseInt(ini.substring(8,10))+" "+ 
				Integer.parseInt(ini.substring(11,13))+":"+Integer.parseInt(ini.substring(14,16))+":"+ Integer.parseInt(ini.substring(17,19)));
		ultimoRegistro.set(Integer.parseInt(fin.substring(0,4)), Integer.parseInt(fin.substring(5,7)),Integer.parseInt(fin.substring(8,10)), 
			Integer.parseInt(fin.substring(11,13)), Integer.parseInt(fin.substring(14,16)), Integer.parseInt(fin.substring(17,19)));
	
		
		
		System.out.println(eventTimeStamp);
		
		if (unidad>=MINUTE){
			eventTimeStamp.set(Calendar.SECOND,00);
			
			System.out.println("CAP AT SECS");
		}
		if (unidad>=HOUR){
			eventTimeStamp.set(Calendar.MINUTE,00);
			System.out.println("CAP AT MINs");
		}
		if(unidad>=DAY){
			eventTimeStamp.set(Calendar.HOUR, 00);
			System.out.println("CAP AT HOURS");
		}
		System.out.println(eventTimeStamp);
		

		
		int anho,mes,dia,hora,minuto,segundo;
		String s_mes,s_dia,s_hora,s_minuto,s_segundo;
		
		anho=eventTimeStamp.get(Calendar.YEAR);
		mes=eventTimeStamp.get(Calendar.MONTH);
		dia=eventTimeStamp.get(Calendar.DAY_OF_MONTH);
		hora=eventTimeStamp.get(Calendar.HOUR_OF_DAY);
		minuto=eventTimeStamp.get(Calendar.MINUTE);
		segundo=eventTimeStamp.get(Calendar.SECOND);
		
		if (mes==0){
			mes=12;
			anho--;
		}
		
		s_mes=String.valueOf(mes);
		s_dia=String.valueOf(dia);
		s_hora=String.valueOf(hora);
		s_minuto=String.valueOf(minuto);
		s_segundo=String.valueOf(segundo);
		
		if (mes<10)  s_mes="0"+s_mes;
		if (dia<10)  s_dia="0"+s_dia;
		if (hora<10)  s_hora="0"+s_hora;
		if (minuto<10)  s_minuto="0"+s_minuto;
		if (segundo<10)  s_segundo="0"+s_segundo;
		
		String resulta=anho+s_mes+s_dia+"_"+s_hora+s_minuto+s_segundo;
		
		System.out.println( resulta);


		long diferencia=Math.abs(eventTimeStamp.getTimeInMillis()-ultimoRegistro.getTimeInMillis());
		boolean result=diferencia>diferenciaMillisPermitida;
		if(result)		System.out.println("nos hemos pasado");
	}
	
}
