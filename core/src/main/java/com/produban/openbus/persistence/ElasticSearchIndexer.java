package com.produban.openbus.persistence;

import backtype.storm.tuple.Values;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

/**
 * Class Description
 */
public class ElasticSearchIndexer extends BaseFunction{

    Client client;
    String indexName;
    String docType;
    String clusterName;
    List<String> esNodes;

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchIndexer.class);

    public ElasticSearchIndexer(String clusterName, String indexName, String docType, List<String> elasticSearchNodes) {
        this.indexName = indexName;
        this.docType = docType;
        this.clusterName = clusterName;
        this.esNodes = elasticSearchNodes;
    }

    @Override
    public void prepare(java.util.Map conf, TridentOperationContext context) {

        //instantiate ES Transport Client:
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
        TransportClient transportClient = new TransportClient(settings);
        for (String esNode : esNodes) {
            String[] hostAndPort = esNode.split(":");
            transportClient.addTransportAddress(new InetSocketTransportAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
        }
        client = transportClient;
    }

    @Override
    public void execute(TridentTuple tuple, TridentCollector tridentCollector) {

        //first field of the tuple is the ID
        //String tweetId = objects.getString(0);

        //build json
        /*XContentBuilder builder = null;
        try {

            builder = buildJson(objects);

        } catch (IOException ex) {
            logger.error(ex.toString());
            logger.error("Error creating JSON object for indexing into elasticsearch");
        }*/

        XContentBuilder builder = (XContentBuilder) tuple.get(0);
        String docId = tuple.getString(1);

        //index
        IndexResponse response = client.prepareIndex(this.indexName, this.docType, docId)
                .setSource(builder)
                .execute()
                .actionGet();



        tridentCollector.emit(new Values(true));
    }

    @Override
    public void cleanup() {
        client.close();
    }

    /**
     *
     * @param tridentTuple Trident-Storm tuple containing fields from a Trident topology
     * @param fieldNames
     * @return
     * @throws IOException
     */
    /*private XContentBuilder buildJson(TridentTuple tridentTuple, List<String> fieldNames) throws IOException {
        XContentBuilder builder = jsonBuilder()
                .startObject();

        for (int i = 0; i < fieldNames.size(); i ++) {
            builder.field(fieldNames.get(i), tridentTuple.get(i));
        }

        builder.endObject();

        return builder;
    }*/

    /*private XContentBuilder buildJson(TridentTuple tridentTuple) throws IOException {

        String tweetId = tridentTuple.getString(0);
        String rawDate = tridentTuple.getString(1);
        Object date = tridentTuple.get(2);
        String text = tridentTuple.getString(3);
        String lang = tridentTuple.getString(4);
        int retweetCount = tridentTuple.getInteger(5);
        double longitude = tridentTuple.getDouble(6);
        double latitude = tridentTuple.getDouble(7);
        int userFollowerCount = tridentTuple.getInteger(8);
        String userLocation = tridentTuple.getString(9);
        String userName = tridentTuple.getString(10);
        String userId = tridentTuple.getString(11);
        String userImgUrl = tridentTuple.getString(12);
        String[] urls = tridentTuple.getString(13).split("\\|");
        String[] mentionedUsers = tridentTuple.getString(14).split("\\|");
        String[] hashtags = tridentTuple.getString(15).split("\\|");

        XContentBuilder builder = jsonBuilder()
                .startObject()
                .field("tweetId", tweetId)
                .field("rawDate", rawDate)
                .field("date", date)
                .field("text", text)
                .field("lang", lang)
                .field("retweetCount", retweetCount);

        if (longitude != 0 || latitude != 0) {
            builder.field("longitude", longitude)
                   .field("latitude", latitude)
                   .array("location", new String[] {Double.toString(longitude), Double.toString(latitude)});
        }

        builder.field("userFollowerCount", userFollowerCount)
                .field("userLocation", userLocation)
                .field("userName", userName)
                .field("userId", userId)
                .field("userImgUrl", userImgUrl)
                .array("urls", urls)
                .array("mentionedUsers", mentionedUsers)
                .array("hashtags", hashtags)
                .endObject();

        return builder;
    }*/
}
