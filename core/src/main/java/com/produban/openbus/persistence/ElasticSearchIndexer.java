package com.produban.openbus.persistence;

import backtype.storm.tuple.Values;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

/**
 * Class Description
 */
public class ElasticSearchIndexer extends BaseFunction{

    Client client;
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchIndexer.class);

    @Override
    public void prepare(java.util.Map conf, TridentOperationContext context) {
        client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
    }

    @Override
    public void execute(TridentTuple objects, TridentCollector tridentCollector) {

        //first field of the tuple is the ID
        String tweetId = objects.getString(0);

        //build json
        XContentBuilder builder = null;
        try {

            builder = buildJson(objects);

        } catch (IOException ex) {
            logger.error(ex.toString());
            logger.error("Error creating JSON object for indexing into elasticsearch");
        }

        //index
        IndexResponse response = client.prepareIndex("twitter", "tweet", tweetId)
                .setSource(builder)
                .execute()
                .actionGet();


        tridentCollector.emit(new Values(true));
    }

    @Override
    public void cleanup() {
        client.close();
    }

    private XContentBuilder buildJson(TridentTuple tridentTuple) throws IOException {
        XContentBuilder builder = jsonBuilder()
                .startObject()
                .field("tweetId", tridentTuple.getString(0))
                .field("rawDate", tridentTuple.getString(1))
                .field("text", tridentTuple.getString(2))
                .field("retweetCount", tridentTuple.getInteger(3))
                .field("longitude", tridentTuple.getDouble(4))
                .field("latitude", tridentTuple.getDouble(5))
                .field("userFollowerCount", tridentTuple.getInteger(6))
                .field("userLocation", tridentTuple.getString(7))
                .field("userName", tridentTuple.getString(8))
                .field("userImgUrl", tridentTuple.getString(9))
                .array("urls", tridentTuple.getString(10).split("\\|"))
                .array("mentionedUsers", tridentTuple.getString(11).split("\\|"))
                .array("hashtags", tridentTuple.getString(12).split("\\|"))
                .endObject();

        return builder;
    }
}
