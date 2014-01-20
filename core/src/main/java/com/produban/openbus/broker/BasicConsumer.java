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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class BasicConsumer extends Thread
{
  private final ConsumerConnector consumer;
  private ConsumerIterator<byte[], byte[]> it;

  
  public BasicConsumer(String topic, String zookeeperUrl, String groupId)
  {
    consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeperUrl, groupId));
    
    Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
    topicCountMap.put(topic, new Integer(1));
    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
    KafkaStream<byte[], byte[]> stream =  consumerMap.get(topic).get(0);
    it = stream.iterator();
  }

  private static ConsumerConfig createConsumerConfig(String zooKeeperUrl, String groupId)
  {
    Properties props = new Properties();
    props.put("zookeeper.connect", zooKeeperUrl);
    props.put("group.id", groupId);
    props.put("zookeeper.session.timeout.ms", "4000");
    props.put("zookeeper.sync.time.ms", "2000");
    props.put("auto.commit.interval.ms", "1000");

    return new ConsumerConfig(props);

  }
 
  public void run() {
    while(it.hasNext())
      System.out.println(it.next());
  }
  
  public MessageAndMetadata<byte[], byte[]> consumeOne() {
	  if (it.hasNext())
	  	return it.next();
	  else
		  return null;
  }
  
  
  public void shutdown() {
	  if (consumer != null) consumer.shutdown();
  }
}