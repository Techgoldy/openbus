package com.produban.openbus.analysis;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;
import com.produban.openbus.persistence.ElasticSearchIndexer;
import storm.trident.Stream;
import storm.trident.TridentTopology;

import java.util.ArrayList;
import java.util.List;

/**
 * A Trident topology for processing a stream of Tweets
 */
public class TweetsTopology {

    public static StormTopology buildTopology (TweetsTopologyOptions options){

        TridentTopology topology = new TridentTopology();

        BrokerSpout kafkaTweetSpout = new BrokerSpout( options.getKafkaTopic(),
                                                       options.getZookeeper(),
                                                       options.getKafkaClientID());
        List<String> tweetFields = new ArrayList<>();
        tweetFields.add("tweetId");
        tweetFields.add("rawDate");
        tweetFields.add("text");
        tweetFields.add("retweetCount");
        tweetFields.add("longitude");
        tweetFields.add("latitude");
        tweetFields.add("userFollowerCount");
        tweetFields.add("userLocation");
        tweetFields.add("userName");
        tweetFields.add("userImgUrl");
        tweetFields.add("urls");
        tweetFields.add("mentionedUsers");
        tweetFields.add("hashtags");

        Stream stream = topology.newStream("spout", kafkaTweetSpout.getPartitionedTridentSpout())
                .each(new Fields("bytes"), new TweetJsonDecoder(), new Fields(tweetFields))
                .each(new Fields("text"), new TweetFilter(options.getFilterKeyWords()))
                //do something interesting here
                .each(new ElasticSearchIndexer(), new Fields("indexed"));

        return topology.build();
    }


    public static void main(String[] args) throws Exception {

        //parse topology arguments:
        TweetsTopologyOptions appOptions = null;
        try {
            appOptions = CliFactory.parseArguments(TweetsTopologyOptions.class, args);
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
    public interface TweetsTopologyOptions
    {
        @Option(defaultValue = "tweetsTopology")
        String getTopologyName();

        @Option
        String getZookeeper();

        @Option
        String getKafkaTopic();

        @Option(defaultValue = "3")
        int getStormNumWorkers();

        @Option(defaultValue = "tweetsTopology")
        String getKafkaClientID();

        @Option
        String[] getFilterKeyWords();

        @Option(shortName = "h", helpRequest = true)
        boolean getHelp();
    }
}
