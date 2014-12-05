package com.produban.openbus.akamai;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;
import com.produban.openbus.analysis.BrokerSpout;
import com.produban.openbus.persistence.ElasticSearchIndexer;
import com.produban.openbus.util.Common;
import storm.trident.Stream;
import storm.trident.TridentTopology;

import java.util.ArrayList;
import java.util.List;

/**
 * A Trident topology for processing a stream of Akamai CloudMonitor messages
 */
public class AkamaiTopology {

    public static StormTopology buildTopology (AkamaiTopologyOptions options){

        TridentTopology topology = new TridentTopology();

        BrokerSpout kafkaAkamaiSpout = new BrokerSpout( options.getKafkaTopic(),
                Common.join(options.getZookeeper(), ","),
                options.getKafkaClientID(),
                options.isForceFromStart());

        ElasticSearchIndexer esIndexer = new ElasticSearchIndexer(
                options.getElasticSearchClusterName(),
                options.getElasticSearchIndex(),
                options.getElasticSearchDocType(),
                options.getElasticSearchNodes());

        List<String> dataFields = new ArrayList<>();
        dataFields.add("id");
        dataFields.add("rawDate");
        dataFields.add("date");
        dataFields.add("proto");
        dataFields.add("protoVer");
        dataFields.add("status");
        dataFields.add("cliIp");
        dataFields.add("reqMethod");
        dataFields.add("reqPath");
        dataFields.add("respLen");
        dataFields.add("UA");
        dataFields.add("referer");

        Stream stream = topology.newStream("spout", kafkaAkamaiSpout.getOpaquePartitionedTridentSpout())
                .each(new Fields("bytes"), new AkamaiJsonDecoder(), new Fields(dataFields))
                .each(new Fields(dataFields), new AkamaiESJsonBuilder(dataFields), new Fields ("ESbuilder", "docId"))
                        //do something interesting here!
                .each(new Fields("ESbuilder", "docId"), esIndexer, new Fields("indexed"));

        return topology.build();
    }


    public static void main(String[] args) throws Exception {

        //parse topology arguments:
        AkamaiTopologyOptions appOptions = null;
        try {
            appOptions = CliFactory.parseArguments(AkamaiTopologyOptions.class, args);
        }
        catch(ArgumentValidationException e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        Config stormConfig = new Config();
        stormConfig.setNumWorkers(appOptions.getStormNumWorkers());

        StormSubmitter.submitTopology(appOptions.getTopologyName(), stormConfig, buildTopology(appOptions));
    }


    /*
     Definition of topology arguments (this uses JewelCLI library)
    */
    public interface AkamaiTopologyOptions
    {
        @Option(defaultValue = "akamaiTopology")
        String getTopologyName();

        @Option
        List<String> getZookeeper();

        @Option
        List<String> getElasticSearchNodes();

        @Option
        String getElasticSearchClusterName();

        @Option
        String getElasticSearchIndex();

        @Option
        String getElasticSearchDocType();

        @Option
        String getKafkaTopic();

        @Option(defaultValue = "2")
        int getStormNumWorkers();

        @Option(defaultValue = "akamaiTopology")
        String getKafkaClientID();

        @Option
        List<String> getFilterKeyWords();

        @Option
        boolean isForceFromStart();

        @Option(shortName = "h", helpRequest = true)
        boolean getHelp();
    }
}
