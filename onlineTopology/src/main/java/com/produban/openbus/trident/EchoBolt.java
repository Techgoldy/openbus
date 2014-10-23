package com.produban.openbus.trident;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.produban.openbus.webservice.MetricaOnLine;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

public class EchoBolt extends BaseBasicBolt {

    private static Logger LOG = Logger.getLogger(EchoBolt.class);

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
    	

		List<Object> objetos =tuple.getValues();		
		
		/*if (objetos.get(0) instanceof String){
			System.out.println("Es String: "+ tuple.getString(0)+" "+tuple.getString(0));
		}else if(objetos.get(0) instanceof byte[]){
			System.out.println("Es byte[]: "+new String((byte[]) tuple.getValues().get(0))+"--"+new String((byte[]) tuple.getValues().get(1)));
		}*/
		
		/*if (tuple.getString(0).equals("DATOS")){
			System.out.println(tuple.getValueByField("datos").getClass().getName());
			String[] datos = (String[]) tuple.getValueByField("datos");
			
			String contenido="";
			for(int i=1;i<datos.length;i++){
				contenido+=datos[i]+",";
			}
			
			System.out.println("Stream destino: "+datos[0] +" con datos: "+contenido);
		}else{
			MetricaOnLine metrica = (MetricaOnLine) tuple.getValueByField("datos");
			System.out.println("metrica recibida: "+metrica.getOnLineMetricName());
		}*/
		
		
		System.out.println("Destino (index/type/id):"+tuple.getValueByField("index")+"/"+tuple.getValueByField("type")+"/"+
							tuple.getValueByField("id")+
							"Datos: "+tuple.getValueByField("document"));
		

    }
}
