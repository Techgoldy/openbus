package com.produban.openbus.analysis;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;
import storm.trident.Stream;
import storm.trident.TridentTopology;

import java.util.ArrayList;
import java.util.List;

/**
 * Trident topology for analytics on raw logs coming from Kafka
 */
public class RawLogTopology {

    public static StormTopology buildTopology (TopologyOptions options) {

        TridentTopology topology = new TridentTopology();

        BrokerSpout kafkaSpout = new BrokerSpout(options.getKafkaTopic(),
                options.getZookeeper(),
                options.getKafkaClientID(),
                options.isForceFromStart());

        String parserClass = options.getLogParserClass();
        LogParser logParser = null;
        try {
            logParser = (LogParser) Class.forName(parserClass).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't instantiate log parser", e);
        }

        List<String> parsedFields = logParser.fieldNames();


        Stream stream = topology.newStream("spout", kafkaSpout.getPartitionedTridentSpout())
                .each(new Fields("bytes"), logParser, new Fields(parsedFields));
                //do something interesting here



        return topology.build();


    }

    public static void main(String[] args) throws Exception {

        //parse topology arguments:
        TopologyOptions appOptions = null;
        try {
            appOptions = CliFactory.parseArguments(TopologyOptions.class, args);
        }
        catch(ArgumentValidationException e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        Config stormConfig = new Config();
        stormConfig.setNumWorkers(appOptions.getStormNumWorkers());

        List<String> fields = new ArrayList<>();

        StormSubmitter.submitTopology(appOptions.getTopologyName(), stormConfig, buildTopology(appOptions));
    }


    /* Definition of topology arguments (this uses JewelCLI library) */
    public interface TopologyOptions
    {
        @Option(defaultValue = "rawLogTopology")
        String getTopologyName();

        @Option
        String getZookeeper();

        @Option
        String getKafkaTopic();

        @Option(defaultValue = "3")
        int getStormNumWorkers();

        @Option(defaultValue = "rawLogTopology")
        String getKafkaClientID();

        @Option
        String getLogParserClass();

        @Option
        boolean isForceFromStart();

        @Option(shortName = "h", helpRequest = true)
        boolean getHelp();
    }

}



