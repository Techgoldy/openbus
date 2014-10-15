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
package com.produban.openbus.topologies;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.kafka.Partition;
import storm.kafka.ZkHosts;
import storm.kafka.trident.GlobalPartitionInformation;
import storm.kafka.trident.OpaqueTridentKafkaSpout;
import storm.kafka.trident.TransactionalTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.spout.IOpaquePartitionedTridentSpout;
import storm.trident.spout.IPartitionedTridentSpout;

/**
 * Storm Spout for Kafka topics
 */
public class BrokerSpout {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerSpout.class);
    private TridentKafkaConfig config;
    private ZkHosts zhost;

    @SuppressWarnings({ "rawtypes", "unused" })
    private IPartitionedTridentSpout<GlobalPartitionInformation, Partition, Map> partitionedTridentSpout = null;

    public BrokerSpout(String kafkaTopic, String zookeeperHosts, String idClient, boolean forceFromStart) {

	zhost = new ZkHosts(zookeeperHosts);
	config = new TridentKafkaConfig(zhost, kafkaTopic); // 3er parametro
							    // idClient
	config.forceFromStart = forceFromStart;
	config.startOffsetTime = -2;

	LOG.info("BrokerSpout. zookeperHosts: " + zookeeperHosts + " topic: " + kafkaTopic + " idClient: " + idClient);
    }

    @SuppressWarnings("rawtypes")
    public IPartitionedTridentSpout<GlobalPartitionInformation, Partition, Map> getPartitionedTridentSpout() {
	return new TransactionalTridentKafkaSpout(config);
    }

    @SuppressWarnings("rawtypes")
    public IOpaquePartitionedTridentSpout<GlobalPartitionInformation, Partition, Map> getOpaquePartitionedTridentSpout() {
	return new OpaqueTridentKafkaSpout(config);
    }
}