package com.produban.openbus.examples;

/*
* Copyright 2013 Produban
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import com.produban.openbus.broker.AvroProducer;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.lang.Math;

import org.apache.log4j.Logger;

/**
 * 
 * A simple ApacheLog producer example with avro encoding and schema embedding.
 *
 */
public class ApacheLogProducerSample {

	static final Logger logger = Logger.getLogger(ApacheLogProducerSample.class);
	
    /**
     * avro fields
     */   
	private final String HOSTREMOTO="host";
	private final String NOMBRELOGREMOTO="log";
	private final String USUARIOREMOTO="user";
	private final String TIEMPOEJECPETICION="datetime";
	private final String LINEAPETICION="request";
	private final String ESTADOPETICION="status";
	private final String TAMANORESPUESTA="size";
	private final String REFERER="referer";
	private final String USERAGENT="userAgent";
	private final String IDSESION="session";
	private final String TIEMPORESPUESTA="responseTime";
	
	
	private  final String[] FIELDS ={
			HOSTREMOTO,
			NOMBRELOGREMOTO,
			USUARIOREMOTO,
			TIEMPOEJECPETICION,
			LINEAPETICION,
			ESTADOPETICION,
			TAMANORESPUESTA,
			REFERER,
			USERAGENT,
			IDSESION,
			TIEMPORESPUESTA	
	};
	

	private String resource;
	private String topic;	
	private int offset;
	
	/**
	 * 
	 * @param resource properties resource path with brokerList, target topic and day offset from current date for generating different datetimes for ApacheLog messages
	 * 		kafka.brokerList=localhost:9092
	 * 		kafka.topic=apacheLogAvro20131022
	 * 		producer.dateOffset=-1 #yesterday
	 * @param topic override the topic specified in the above resource
	 */
    public ApacheLogProducerSample(String resource, String topic, int offset) {

    	this.resource=resource;
    	this.topic=topic;    	
    	this.offset=offset; 
	}

    /**
     * 
     * @param args: topic, total messages, users, sessions per user, request per session  
     *         topic is mandatory when supplying the others
     */
	public static void main(String[] args) {
    	
    	int nMessages=100000, nUsers=5, nSessions=10, nRequests=100;
    	String topic=null;
    	int offset=0;
    	if(args.length>0) {
    		topic=args[0];
    		nMessages=Integer.parseInt(args[1]);
    		nUsers=Integer.parseInt(args[2]);
    		nSessions=Integer.parseInt(args[3]);
    		nRequests=Integer.parseInt(args[4]);    		
    		if (args[5] != null)  offset=Integer.parseInt(args[5]);
    	}
    	
    	ApacheLogProducerSample aps = new ApacheLogProducerSample("/kafka.properties",topic, offset);
    	aps.apacheLogProducerHelper(nMessages, nUsers, nSessions, nRequests);
    }
	
	/**
	 * 
	 * Uses AvroProducer for sending massive messages in ApacheLog avro format specified in resource /apacheLog.avsc
	 * 
	 * @param nMessages total messages number
	 * @param nUsers concurrent users number
	 * @param nSessions sessions per user number
	 * @param nRequests requests per session number
	 */
	public void apacheLogProducerHelper(int nMessages, int nUsers, int nSessions, int nRequests)   {
    	 
	    	Properties kafkaProps = new Properties();
	    	try {
				kafkaProps.load(ClassLoader.class.getResourceAsStream(resource));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    	
	    	
	    	int dateOffset=offset;
	    	if (offset==0){
	    		dateOffset=Integer.parseInt(kafkaProps.get("producer.dateOffset").toString());
	    	}
	    	if(topic==null) kafkaProps.getProperty("kafka.topic");
	    	AvroProducer ap = new AvroProducer(kafkaProps.getProperty("kafka.brokerList"), topic, "/apacheLog.avsc", FIELDS);
	    	
			String[] HOSTREMOTO={ "85.155.188.198","85.155.188.199","85.155.188.197","85.155.188.196","85.155.188.195","85.155.188.190"};
			String NOMBRELOGREMOTO="-";
			String USUARIOREMOTO="user";
			String TIEMPOEJECPETICION="[17/Sep/2012:19:01:24+0200]";
			String LINEAPETICION="index";
			String ESTADOPETICION="200";
			String TAMANORESPUESTA="3117";
			String REFERER="-";
			String USERAGENT="Chrome/21.0.1180.89";
			String IDSESION="0000z2ur1hruUUG-MhpsITK9JY_:16vnisqka";
			String TIEMPORESPUESTA="";
			
			
			Date datetime= new Date();
			Date lasttime=new Date();
			Calendar cal = new GregorianCalendar();		
			logger.info("starting at " + datetime + ", " + datetime.getTime()/1000);
			
			int count=0;
			for(int i=0;i<nMessages/*/nUsers/nRequests/nSessions*/;i++) {
				
				for(int j=0;j<nSessions;j++) {
						cal.setTime(datetime);
						cal.add(Calendar.DAY_OF_MONTH, dateOffset);

						
					for(int k=0;k<nRequests;k++) { 
						

						for(int m=0;m<nUsers;m++) {					
							
							int randomNum =  (int)(Math.random()*(m+1)); 
							if (randomNum>=(nUsers/2))  continue;
							
							if(cal.getTime().getTime()>lasttime.getTime()) lasttime=cal.getTime();
							
							IDSESION="0000z2ur1hruUUG-MhpsITK9JY_:" + k;
							LINEAPETICION="page" + j%(2*(m+1));
							TIEMPOEJECPETICION=cal.getTime().toString();
							USUARIOREMOTO="user"+m;
                            TIEMPORESPUESTA = String.valueOf(m*100%10000);
							Object[] payload = new Object[] {HOSTREMOTO[k%5], NOMBRELOGREMOTO, USUARIOREMOTO,
                                                            TIEMPOEJECPETICION, LINEAPETICION, ESTADOPETICION,
                                                            TAMANORESPUESTA, REFERER, USERAGENT, IDSESION,
                                                            TIEMPORESPUESTA};

					
							ap.send(payload);
							count++;
							cal.add(Calendar.SECOND,5);
						}		
					}
					cal.add(Calendar.MINUTE,5);
				}
			}
			
			logger.info("ending  at " + lasttime + ", " + lasttime.getTime()/1000);
			logger.info(count + " registros enviados a kafka");
			ap.close();

	    }
	
}
