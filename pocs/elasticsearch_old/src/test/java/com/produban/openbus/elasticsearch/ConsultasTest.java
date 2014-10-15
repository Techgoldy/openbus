package com.produban.openbus.elasticsearch;

import java.io.IOException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;

public class ConsultasTest {
	 
		public static void main( String[] args ) throws IOException, InterruptedException
	    {
	    	 String elasticSearchHost = "localhost";
	         Integer elasticSearchPort = 9300;
	         String elasticSearchCluster = "elasticsearch";
	                  
	         Client client;	
	         String ip ="180.243.255.187";
	         
	         Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", elasticSearchCluster).build();
	         client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(
	                 elasticSearchHost, elasticSearchPort));
	         
	         
	         SearchResponse responseRango = client.prepareSearch("georef") //uno o más indices
	    		        .setTypes("ip_rango")
	    		        .setQuery(
	                    QueryBuilders.boolQuery()       // Your query
	                            .must(QueryBuilders.rangeQuery("ip_fin").gte(ip))
	                            .must(QueryBuilders.rangeQuery("ip_inicio").lte(ip))
	                    )
	        			.execute()
	        			.actionGet();
	         
	         System.out.println(responseRango);
	    }
}
