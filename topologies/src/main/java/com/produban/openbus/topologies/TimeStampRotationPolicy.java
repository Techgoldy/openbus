package com.produban.openbus.topologies;

import java.util.Calendar;
import java.util.List;

import org.apache.storm.hdfs.trident.rotation.FileRotationPolicy;

import storm.trident.tuple.TridentTuple;

public class TimeStampRotationPolicy implements FileRotationPolicy  {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public static enum Units {

        KB((long)Math.pow(2, 10)),
        MB((long)Math.pow(2, 20)),
        GB((long)Math.pow(2, 30)),
        TB((long)Math.pow(2, 40));

        private long byteCount;

        private Units(long byteCount){
            this.byteCount = byteCount;
        }

        public long getByteCount(){
            return byteCount;
        }
    }

    private long maxBytes;

    private long lastOffset = 0;
    private long currentBytesWritten = 0;

	//milisegundos que tiene cada unidad
	 public static final long 
	        HOUR=3600000,
	        MINUTE=60000,
	        SECOND=1000,
	        DAY=86400000;
	        

	 private Calendar limiteTimeStamp=null;
	 private Calendar ultimoRegistro=Calendar.getInstance();
	 private long unidad;
	 private long periodo;
	 private long diferenciaMillisPermitida;
	
	public TimeStampRotationPolicy setTimePeriod(int periodo,long unidad){
		this.unidad=unidad;
		this.periodo=periodo;
		this.diferenciaMillisPermitida=this.unidad*this.periodo;
		resetTimePeriod();
		return this;
	}
	
	public TimeStampRotationPolicy setSizeMax(float count, Units units){
	        this.maxBytes = (long)(count * units.getByteCount());
	        return this;
	}
	
	
	
	public boolean markTimePeriod(TridentTuple tupla) {
		// TODO Auto-generated method stub
		boolean result=false;
		long diferencia;
		List objetos =tupla.getValues();
		
		if (objetos.get(0) instanceof String){
			ultimoRegistro=getCalendarTimestamp(tupla.getString(0));
		}else if(objetos.get(0) instanceof byte[]){
			ultimoRegistro=getCalendarTimestamp(new String((byte[]) tupla.toArray()[0]));
		}
		
		//ultimoRegistro=getCalendarTimestamp(tupla.getString(0));
		if (limiteTimeStamp==null){
			//inicializamos
			result=true;
		}else{
			//comprobar si pasamos del periodo en la unidad
			diferencia=Math.abs(limiteTimeStamp.getTimeInMillis()-ultimoRegistro.getTimeInMillis());
			result=diferencia>=diferenciaMillisPermitida;
		}
		return result;
	}

	
	public void resetTimePeriod() {
		//truncamos a la unidad seleccionada
		
		if (unidad>=SECOND){
			ultimoRegistro.set(Calendar.MILLISECOND,0);
		}
		if (unidad>=MINUTE){
			ultimoRegistro.set(Calendar.SECOND,0);
		}
		if (unidad>=HOUR){
			ultimoRegistro.set(Calendar.MINUTE,0);
		}
		if(unidad>=DAY){
			ultimoRegistro.set(Calendar.HOUR, 0);
		}
	
		limiteTimeStamp=ultimoRegistro;
	}
	
	
	
		@Override
	    public boolean mark(TridentTuple tuple, long offset) {
	        long diff = offset - this.lastOffset;
	        this.currentBytesWritten += diff;
	        this.lastOffset = offset;
	        return this.currentBytesWritten >= this.maxBytes;
	    }

	    @Override
	    public void reset() {
	        this.currentBytesWritten = 0;
	        this.lastOffset = 0;
	    }
	
	
	public String getBaseTimestamp(){
		
		String result="";
		
		int anho,mes,dia,hora,minuto,segundo;
		String s_mes,s_dia,s_hora,s_minuto,s_segundo;
		
		if (limiteTimeStamp==null){
			ultimoRegistro=Calendar.getInstance();
			resetTimePeriod();
		}
			anho=limiteTimeStamp.get(Calendar.YEAR);
			mes=limiteTimeStamp.get(Calendar.MONTH);
			dia=limiteTimeStamp.get(Calendar.DAY_OF_MONTH);
			hora=limiteTimeStamp.get(Calendar.HOUR_OF_DAY);
			minuto=limiteTimeStamp.get(Calendar.MINUTE);
			segundo=limiteTimeStamp.get(Calendar.SECOND);
			
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
			
			result=anho+s_mes+s_dia+"_"+s_hora+s_minuto+s_segundo;
		
		return result;
	}
	
	private Calendar getCalendarTimestamp(String timestamp){
		Calendar resultado=Calendar.getInstance();
		resultado.set(Integer.parseInt(timestamp.substring(0,4)), Integer.parseInt(timestamp.substring(5,7)),Integer.parseInt(timestamp.substring(8,10)), 
				Integer.parseInt(timestamp.substring(11,13)), Integer.parseInt(timestamp.substring(14,16)), Integer.parseInt(timestamp.substring(17,19)));
		return resultado;
	}

}
