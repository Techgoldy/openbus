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


import com.produban.openbus.util.KafkaLocal;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.Properties;


/**
 * Unit test for schema encoded avro message to kafka.
 */
public class ApacheLogProducerTest  extends TestCase  {

    private static KafkaLocal kafka;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ApacheLogProducerTest( String testName )  {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()  {
        return new TestSuite( ApacheLogProducerTest.class );
    }

    @Before
    public void setUp(){
        Properties kafkaProperties = new Properties();

        try {
            //start kafka
            kafkaProperties.load(Class.class.getResourceAsStream("/kafkalocal.properties"));
            kafka = new KafkaLocal(kafkaProperties);
            Thread.sleep(5000);
        } catch (Exception e){
            e.printStackTrace(System.out);
            fail("Error running local Kafka broker");
            e.printStackTrace(System.out);
        }

    }
	
	/**
	 *  send 1000 messages, with 5 users, 10 sessions and 10 requests
	 * 
	 */
	public void testApacheLogProducer() {
		ApacheLogProducerSample aps = new ApacheLogProducerSample("/kafka-test.properties","testtopic1", 0);
		aps.apacheLogProducerHelper(100,5,10,10);		
        assertTrue( true );
	}
	
	/**
	 *  send 1 messages, with 1 users, 1 sessions and 1 requests
	 * 
	 */
	public void testApacheLogProducerMin() {
		ApacheLogProducerSample aps = new ApacheLogProducerSample("/kafka-test.properties","webserverlog",0);
		aps.apacheLogProducerHelper(1,1,1,1);		
        assertTrue( true );
	}
	
	/**
	 *  send 1 messages, with 1 users, 1 sessions and 1 requests
	 * 
	 */
	public void testApacheLogProducer2Users() {
		ApacheLogProducerSample aps = new ApacheLogProducerSample("/kafka-test.properties","webserverlog",0);
		aps.apacheLogProducerHelper(100,2,4,4);		
        assertTrue( true );
    }

    @After
    public void tearDown() {
        try {
            kafka.stop();
        }
        catch (IOException e) {
            System.out.println("Error stopping local kafka server");
            e.printStackTrace(System.out);
        }

    }
   
}
