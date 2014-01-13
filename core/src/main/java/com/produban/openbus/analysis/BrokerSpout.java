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
package com.produban.openbus.analysis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.produban.openbus.util.Conf;

import storm.kafka.BrokerHosts;
import storm.kafka.HostPort;
import storm.kafka.Partition;
import storm.kafka.StaticHosts;
import storm.kafka.ZkHosts;
import storm.kafka.trident.GlobalPartitionInformation;
import storm.kafka.trident.TransactionalTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.spout.IPartitionedTridentSpout;


/**
 * Kafka Broker Openbus
 */
public class BrokerSpout {		
	private static final Logger LOG = LoggerFactory.getLogger(BrokerSpout.class);
	private final static String KAFKA_TOPIC = "webserverlog";		
	private final static String KAFKA_IDCLIENT = "idOpenbus";	
	private TridentKafkaConfig config = null;
	private ZkHosts zhost = null;
	
	@SuppressWarnings("rawtypes")
	private IPartitionedTridentSpout<GlobalPartitionInformation, Partition, Map> partitionedTridentSpout = null;

	public BrokerSpout() {
    	zhost = new ZkHosts(Conf.ZOOKEEPER_HOST + ":" + Conf.ZOOKEEPER_PORT, Conf.ZOOKEEPER_BROKER);        
        config = new TridentKafkaConfig(zhost, KAFKA_TOPIC, KAFKA_IDCLIENT);      
	}
	
	public BrokerSpout(String kafkaTopic) {    	
    	zhost = new ZkHosts(Conf.ZOOKEEPER_HOST, Conf.ZOOKEEPER_BROKER);    	
        config = new TridentKafkaConfig(zhost, kafkaTopic, KAFKA_IDCLIENT);                        
	}
	
	public BrokerSpout(String kafkaTopic, String zookeperHost, String zookeperBroker) {    	
    	zhost = new ZkHosts(zookeperHost, zookeperBroker);    	    	    	
        config = new TridentKafkaConfig(zhost, kafkaTopic, KAFKA_IDCLIENT);                        
	}
		
	public BrokerSpout(String kafkaTopic, String staticHosts, int port, String idClient) {
		int i = 0;
		GlobalPartitionInformation hostsAndPartitions = new GlobalPartitionInformation();		
		String[] hosts = staticHosts.split(",");
        for (String host : hosts) {   
        	LOG.info("BrokerSpout. Host: " + host + " port: " + port + " topic: " + kafkaTopic + " idClient: " + idClient);
        	hostsAndPartitions.addPartition(i, new HostPort(host, port));        	
        	i++;
        }
        BrokerHosts brokerHosts = new StaticHosts(hostsAndPartitions);    	
        config = new TridentKafkaConfig(brokerHosts, kafkaTopic, idClient);                        
	}
	
	public BrokerSpout(String kafkaTopic, String zookeperHost, String zookeperBroker, String idClient) {    	
    	zhost = new ZkHosts(zookeperHost, zookeperBroker);    	
        config = new TridentKafkaConfig(zhost, kafkaTopic, idClient);                        
        LOG.info("BrokerSpout. zookeperHost: " + zookeperHost + " zookeperBroker: " + zookeperBroker + " topic: " + kafkaTopic + " idClient: " + idClient);
	}
	
	@SuppressWarnings("rawtypes")
	public IPartitionedTridentSpout<GlobalPartitionInformation, Partition, Map> getPartitionedTridentSpout() {		
		partitionedTridentSpout = new TransactionalTridentKafkaSpout(config);
		
		return partitionedTridentSpout;
	}
}