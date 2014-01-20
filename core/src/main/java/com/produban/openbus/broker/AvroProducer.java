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

package com.produban.openbus.broker;

import java.util.Properties;

import com.produban.openbus.serialization.AvroSerializer;
import kafka.javaapi.producer.Producer;
import kafka.message.Message;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.log4j.Logger;


/**
 * 
 * A Kafka producer that send Avro encoded messages.
 * Writes specified fields from Avro schema to a specific topic
 * Uses AvroSerializer for embedding schema with message (Each sent message will have the  Avro schema embedded)
 */

public class AvroProducer {
	static final Logger logger = Logger.getLogger(AvroProducer.class);
	
	private Producer<byte[], byte[]> producer;
	private AvroSerializer serializer;
	private String topic;
	
	/**
	 * Constructor
	 * @param brokerList kafka broker list. It has not to be complete, it's used to retrieve cluster info.
	 * @param topic target topic where the producer will send the messages.
	 * @param avroSchemaPath path of the file with the Avro schema
	 * @param fields list of Avro field names
	 */
    public AvroProducer(String brokerList, String topic, String avroSchemaPath, String[] fields) {

        this.topic=topic;
    	this.serializer = new AvroSerializer(ClassLoader.class.getResourceAsStream(avroSchemaPath), fields );

        Properties props = new Properties();
        props.put("metadata.broker.list", brokerList);
        this.producer = new kafka.javaapi.producer.Producer<>(new ProducerConfig(props));
        	   
    }
    
    /**
     * Send a message 
     * @param values Array of Avro field values to be sent to kafka
     */
    public void send(Object[] values) {
    	Message message = new Message(serializer.serialize(values));
		//producer.send(new KeyedMessage<byte[], byte[]>(topic, message.buffer().array()));
        producer.send(new KeyedMessage<byte[], byte[]>(topic, serializer.serialize(values)));
    }

	/**
	 * closes producer
	 */
	public void close() {
		producer.close();
	}
              
    
}