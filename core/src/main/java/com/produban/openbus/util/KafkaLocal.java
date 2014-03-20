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

package com.produban.openbus.util;

import java.io.IOException;
import java.util.Properties;

import com.produban.openbus.util.ZookeeperLocal;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;


public class KafkaLocal {

	public KafkaServerStartable kafka;
	public ZookeeperLocal zookeeper;
	
	public KafkaLocal(Properties kafkaProperties) throws IOException, InterruptedException{
		KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
		
		//start local zookeeper
		System.out.println("starting local zookeeper...");
		zookeeper = new ZookeeperLocal();
        try {
            zookeeper.startzkServer(2181);
        }
        catch (Exception e) {
            throw new RuntimeException("Error starting local zookeeper server", e);
        }

		System.out.println("done");
		
		//start local kafka broker
		kafka = new KafkaServerStartable(kafkaConfig);
		System.out.println("starting local kafka broker...");
		kafka.startup();
		System.out.println("done");


	}
	
	
	public void stop() throws IOException {
		//stop kafka broker
		System.out.println("stopping kafka...");
		kafka.shutdown();
		System.out.println("done");

        //stop zookeeper local server:
        zookeeper.stopZkServer();
	}


    public static void main (String[] args) {
        Properties props = new Properties();
        String hostname = args.length > 0 ? args[0] : "localhost";
        String port = args.length > 1 ? args[1] : "9092";
        String brokerId = args.length > 2 ? args[2] : "0";
        String logDir = args.length > 3 ? args[3] : "/tmp/kafkalocal";
        String zookeeperConnect = args.length > 4 ? args[4] : "localhost:2181";

        props.setProperty("host.name", hostname);
        props.setProperty("port", port);
        props.setProperty("broker.id", brokerId);
        props.setProperty("log.dir", logDir);
        props.setProperty("zookeeper.connect", zookeeperConnect);

        try {
            KafkaLocal kafkaServer = new KafkaLocal(props);
        }
        catch (Exception e) {
            System.out.println("Error running Kafka server process");
            e.printStackTrace();
        }

    }


}
