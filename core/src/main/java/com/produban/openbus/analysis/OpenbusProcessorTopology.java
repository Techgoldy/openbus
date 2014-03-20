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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.produban.openbus.util.DatePartition;
import com.produban.openbus.util.LogFilter;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.state.StateFactory;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.contrib.hbase.trident.HBaseAggregateState;
import backtype.storm.contrib.hbase.utils.TridentConfig;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;

/**
 * Trident topology for basic counts on a stream of apache web logs consumed from Kafka.
 * 
 */
public class OpenbusProcessorTopology {	
	private static final Logger LOG = LoggerFactory.getLogger(OpenbusProcessorTopology.class);
	    	
	public static StormTopology buildTopology(AvroWebLogTopologyOptions options) throws IOException {

		TridentConfig configRequest = new TridentConfig(options.getHbaseRequestTable(),
                                                        options.getHbaseRequestTableRowId());
		StateFactory stateRequest = HBaseAggregateState.transactional(configRequest);

	    TridentConfig configUser = new TridentConfig(options.getHbaseUserTable(),
                                                     options.getHbaseUserTableRowId());
	    StateFactory stateUser = HBaseAggregateState.transactional(configUser);

	    TridentConfig configSession = new TridentConfig(options.getHbaseSessionTable(),
                                                        options.getHbaseSessionTableRowId());
	    StateFactory stateSession = HBaseAggregateState.transactional(configSession);
	    	    
	    BrokerSpout openbusBrokerSpout = new BrokerSpout( options.getKafkaTopic(),
                                                          options.getZookeeper(),
                                                          options.getKafkaClientID());

        //We need to know what fields will be produced after Avro messages decoding.
        //We use the avro schema for that (even when we dont need the schema to decode
        //the messages, because is embedded into the messages)
        Schema avroSchema = new Schema.Parser().parse(new File(options.getAvroSchema()));
        List<String> avroFieldNames = new ArrayList<>();
        for (Schema.Field avroField : avroSchema.getFields()) {
            avroFieldNames.add(avroField.name());
        }

        TridentTopology topology = new TridentTopology();
		Stream stream = topology.newStream("spout", openbusBrokerSpout.getPartitionedTridentSpout())
		         .each(new Fields("bytes"), new AvroLogDecoder(), new Fields(avroFieldNames))
                 .each(new Fields("datetime"), new DateTimeTransformation(), new Fields("timestamp"))
                 //this step adds a "timestamp" field:
		         .each(new Fields(avroFieldNames), new WebServerLogFilter());
		
		stream.each(new Fields("request", "datetime"), new DatePartition(), new Fields("cq", "cf"))
				.groupBy(new Fields("request", "cq", "cf"))
				.persistentAggregate(stateRequest, new Count(), new Fields("count"))
				//.persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"))	// Test				
				.newValuesStream()
				.each(new Fields("request", "cq", "cf", "count"), new LogFilter());
		
		stream.each(new Fields("user", "datetime"), new DatePartition(), new Fields("cq", "cf"))
				.groupBy(new Fields("user", "cq", "cf"))
				.persistentAggregate(stateUser, new Count(), new Fields("count"))
				//.persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"))	// Test				
				.newValuesStream()
				.each(new Fields("user", "cq", "cf", "count"), new LogFilter());
		
		stream.each(new Fields("session", "datetime"), new DatePartition(), new Fields("cq", "cf"))
				.groupBy(new Fields("session", "cq", "cf"))
				.persistentAggregate(stateSession, new Count(), new Fields("count"))
				//.persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"))	// Test				
				.newValuesStream()
				.each(new Fields("session", "cq", "cf", "count"), new LogFilter());
		
		return topology.build();				
	}

	public static void main(String[] args) throws Exception {	

        //parse topology arguments:
        AvroWebLogTopologyOptions appOptions = null;
        try {
            appOptions = CliFactory.parseArguments(AvroWebLogTopologyOptions.class, args);
        }
        catch(ArgumentValidationException e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        System.out.println("appOptions.toString() = " + appOptions.toString());
        Config stormConfig = new Config();
        stormConfig.setNumWorkers(appOptions.getStormNumWorkers());

		StormSubmitter.submitTopology(appOptions.getTopologyName(), stormConfig, buildTopology(appOptions));
	}

    /*
        Definition of topology arguments (this uses JewelCLI library)
     */
    public interface AvroWebLogTopologyOptions
    {
        @Option(defaultValue = "avroWebLogTopology")
        String getTopologyName();

        @Option
        String getZookeeper();

        @Option
        String getKafkaTopic();

        @Option
        String getAvroSchema();

        @Option(defaultValue = "3")
        int getStormNumWorkers();

        @Option(defaultValue = "AvroWebLogTopology")
        String getKafkaClientID();

        @Option(defaultValue =  "wslog_user")
        String getHbaseUserTable();

        @Option(defaultValue =  "user")
        String getHbaseUserTableRowId();

        @Option(defaultValue =  "wslog_request")
        String getHbaseRequestTable();

        @Option(defaultValue =  "request")
        String getHbaseRequestTableRowId();

        @Option(defaultValue =  "wslog_session")
        String getHbaseSessionTable();

        @Option(defaultValue =  "session")
        String getHbaseSessionTableRowId();

        @Option(shortName = "h", helpRequest = true)
        boolean getHelp();
    }
}