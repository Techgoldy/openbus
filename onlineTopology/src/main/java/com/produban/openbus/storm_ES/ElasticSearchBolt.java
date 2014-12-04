package com.produban.openbus.storm_ES;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import static org.elasticsearch.node.NodeBuilder.*;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@SuppressWarnings("serial")
/**
 * Abstract <code>IRichBolt</code> implementation capable of indexing data from tuples.
 * Tuples are mapped into documents via a <code>TupleMapper</code>.
 * 
 * The bolt expects the following in the StormConfig:
 *      elastic.search.cluster (ES cluster name)
 *      elastic.search.host (ES host)
 *      elastic.search.port (ES port)
 *      
 * Also, the bolt supports a local mode, which is handy for testing.  Setting the following
 * config to <code>true</code> will cause the bolt to start a local elastic search.
 * 
 * @author boneill42
 * @author ptgoetz
 * 
 */public class ElasticSearchBolt extends BaseRichBolt {
    public static final String ELASTIC_LOCAL_MODE = "localMode";

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchBolt.class);
    private OutputCollector collector;
    private Client client;

    protected TupleMapper tupleMapper;

    public ElasticSearchBolt(TupleMapper tupleMapper) {
        this.tupleMapper = tupleMapper;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        String elasticSearchHost = (String) stormConf.get(StormElasticSearchConstants.ES_HOST);
        Integer elasticSearchPort = ((Long) stormConf.get(StormElasticSearchConstants.ES_PORT)).intValue();
        String elasticSearchCluster = (String) stormConf.get(StormElasticSearchConstants.ES_CLUSTER_NAME);
        Boolean localMode = (Boolean) stormConf.get(ELASTIC_LOCAL_MODE);

        if (localMode != null && localMode) {
            client = nodeBuilder().local(true).node().client();            
        } else {
            Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", elasticSearchCluster).build();
            client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(
                    elasticSearchHost, elasticSearchPort));
        // SI existe el indice lo borramos
        IndicesExistsResponse res =  client.admin().indices().prepareExists("indiceprueba").execute().actionGet();
        if (res.isExists()) {
            client.admin().indices().prepareDelete("indiceprueba").
            execute().actionGet();
            
        }  

        
        // MAPPING GOES HERE
        //Crear el mapping
      
       /* XContentBuilder mappingBuilder;
		try {
			mappingBuilder = jsonBuilder().startObject()
					.startObject("typeprueba")
					.startObject("properties")
			        		.startObject("id")
			        			.field("type", "string")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		.startObject("MSGID")
			        			.field("type", "string")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		.startObject("DSN")
			        			.field("type", "string")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		.startObject("tamano")
			        			.field("type", "long")
			        		.endObject()
			        .endObject()
			        .endObject()
			        .endObject();

			System.out.println(mappingBuilder.string());
		        
        final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate("indiceprueba");
        createIndexRequestBuilder.addMapping("typeprueba", mappingBuilder);

        // MAPPING DONE
        createIndexRequestBuilder.execute().actionGet();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    */

            
            
		
		        }
    }

    @Override
    public void execute(Tuple tuple) {
        String id = null;
        String indexName = null;
        String type = null;
        String document = null;
        try {
            id = this.tupleMapper.mapToId(tuple);
            indexName = this.tupleMapper.mapToIndex(tuple);
            type = this.tupleMapper.mapToType(tuple);
            document = this.tupleMapper.mapToDocument(tuple);
            byte[] byteBuffer = document.getBytes();
            IndexResponse response;
            if(id!=null && !id.equals("")){
            	 response = this.client.prepareIndex(indexName, type, id).setSource(byteBuffer).execute()
                    .actionGet();
            }else{
            	//No hay indice definido
            	 response = this.client.prepareIndex(indexName, type).setSource(byteBuffer).execute()
                        .actionGet();
            }
            LOG.debug("Indexed Document[ " + id + "], Type[" + type + "], Index[" + indexName + "], Version ["
                    + response.getVersion() + "]");
            collector.ack(tuple);
        } catch (Exception e) {
            LOG.error("Unable to index Document[ " + id + "], Type[" + type + "], Index[" + indexName + "]", e);
            collector.ack(tuple);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // Not generating any output from this bolt.
    }

}
