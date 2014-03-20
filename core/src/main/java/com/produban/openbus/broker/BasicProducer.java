/*
 * Copyright 2013 Produban
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.produban.openbus.broker;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * A basic kafka producer class that can be used as a starting point for more complex java producers.
 * 
 */
public class BasicProducer {

	Producer<String, String> producer;
	
	/**
	 * Creates a new BasicProducer from a set of properties
	 * 
	 * @param props properties of the producer. See http://kafka.apache.org/documentation.html#producerconfigs for all possible config fields
	 */
	public BasicProducer(Properties props){
		producer = new Producer<String, String>(new ProducerConfig(props));
	}
	
	/**
	 * Instantiates a new BasicProducer using the provided broker list and a set of default properties.
	 * This constructor is useful if we want to avoid connecting directly with zookeeper.
	 * 
	 * @param brokerList URL to retrieve the kafka broker list. 
	 */
	public BasicProducer(String brokerList, boolean requiredAcks) {
		Properties props = new Properties();
		props.put("metadata.broker.list", brokerList);
		props.put("request.required.acks", requiredAcks ? "1" : "0");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		
		producer = new Producer<String, String>(new ProducerConfig(props));
	}
	
	/**
	 * Sends an arbitrary message to a kafka topic. The message must have String types as both key and value.
	 * 
	 * @param topic the topic where the message will be sent
	 * @param key the message key
	 * @param value the message value
	 */
	public void sendMessage(String topic, String key, String value){
		KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, key, value);
		producer.send(data);
	}
	
	/**
	 * Creates a simulated random log event and sends it to a kafka topic.
	 * 
	 * @param topic topic where the message will be sent
	 */
	public void sendRandomLogEvent(String topic){
		//Build random IP message
		Random rnd = new Random();
		long runtime = new Date().getTime();
		String ip = "192.168.2." + rnd.nextInt(255);
		String msg = runtime + ", www.example.com, "+ ip;
		
		//Send the message to the broker
		this.sendMessage(topic, ip, msg);
	}
}
