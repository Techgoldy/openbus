package com.produban.openbus.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;



/**
 * Hello world!
 *
 */
public class WriteReadToElasticsearch 
{
	


    public static void main( String[] args ) throws IOException, InterruptedException
    {
    	 String elasticSearchHost = "localhost";
         Integer elasticSearchPort = 9300;
         String elasticSearchCluster = "elasticsearch";
         Boolean localMode = false;
         
         Client client;	

         if (localMode != null && localMode) {
             client = nodeBuilder().local(true).node().client();            
         } else {
             Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", elasticSearchCluster).build();
             client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(
                     elasticSearchHost, elasticSearchPort));
             
	         // SI existe el indice lo borramos
	         IndicesExistsResponse res =  client.admin().indices().prepareExists("georef").execute().actionGet();
	         if (res.isExists()) {
	             client.admin().indices().prepareDelete("georef").
	             execute().actionGet();
	         }
	         
	        
	         //Crear el mapping
	       
	         XContentBuilder mappingBuilderIP,mappingBuilderRango;
	 		
	         mappingBuilderIP = jsonBuilder().startObject()
	 					.startObject("ip_fijas")
	 					.startObject("properties")
	 			        		.startObject("id")
	 			        			.field("type", "string")
	 			        			.field("index", "not_analyzed")
	 			        		.endObject()
	 			        		.startObject("ip")
	 			        			.field("type", "ip")
	 			        			.field("index", "not_analyzed")
	 			        		.endObject()
	 			        		.startObject("long")
	 			        			.field("type", "float")
	 			        		.endObject()
	 			        		.startObject("lat")
	 			        			.field("type", "float")
	 			        		.endObject()
	 			        		.startObject("coord")
 			        				.field("type","string")
 			        			.endObject()
	 			        .endObject()
	 			        .endObject()
	 			        .endObject();
	 			
	 			mappingBuilderRango= jsonBuilder().startObject()
	 					.startObject("ip_rangos")
	 					.startObject("properties")
	 			        		.startObject("id")
	 			        			.field("type", "string")
	 			        			.field("index", "not_analyzed")
	 			        		.endObject()
	 			        		.startObject("ip_inicio")
	 			        			.field("type", "ip")
	 			        			.field("index", "not_analyzed")
	 			        		.endObject()
	 			        		.startObject("ip_fin")
	 			        			.field("type", "ip")
	 			        			.field("index", "not_analyzed")
	 			        		.endObject()
	 			        		.startObject("long")
	 			        			.field("type", "float")
	 			        		.endObject()
	 			        		.startObject("lat")
	 			        			.field("type", "float")
	 			        		.endObject()
	 			        		
	 			        .endObject()
	 			        .endObject()
	 			        .endObject();
	
	 	
		         final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate("georef");
		         createIndexRequestBuilder.addMapping("ip_fijas", mappingBuilderIP).addMapping("ip_rangos", mappingBuilderRango).execute().actionGet();

	
	 		
	 		//Creacióndelos datos
	 		XContentBuilder datosIpFija=jsonBuilder().startObject()
		        			.field("id", "180.127.133.39")
		        			.field("ip", "180.127.133.39")
		        			.field("long", -3.802439)
		        			.field("lat", 40.419308)
		        			.endObject();

            client.prepareIndex("georef", "ip_fijas", "180.127.133.39").setSource(datosIpFija.string().getBytes()).execute()
                    .actionGet();
            
            datosIpFija=jsonBuilder().startObject()
        			.field("id", "200.17.13.99")
        			.field("ip", "200.17.13.99")
        			.field("long", -3.803232)
        			.field("lat", 43.462089)
        			.field("coord","[-3.803232,43.462089]")
        			.endObject();

            client.prepareIndex("georef", "ip_fijas", "200.17.13.99").setSource(datosIpFija.string().getBytes()).execute()
            .actionGet();
            
            System.out.println(datosIpFija.string());
    
    		datosIpFija=jsonBuilder().startObject()
			.field("id", "10.10.14.10")
			.field("ip", "10.10.14.10")
			.field("long", -118.4047309)
			.field("lat", 33.9828847)
			.field("coord","[-118.4047309,33.9828847]")
			.endObject();

    		client.prepareIndex("georef", "ip_fijas", "10.10.14.10").setSource(datosIpFija.string().getBytes()).execute()
    		.actionGet();
    		
    		XContentBuilder datosIpRango=jsonBuilder().startObject()
    				.field("id", "1")
    				.field("ip_inicio", "20.200.0.0")
    				.field("ip_fin", "20.205.255.255")
    				.field("long", 4)
    				.field("lat", 4)
    				.field("coord","[4,4]")
    				.endObject();

    	    		client.prepareIndex("georef", "ip_rangos", "1").setSource(datosIpRango.string().getBytes()).execute()
    	    		.actionGet();
	 		
    	    		
    	    		 datosIpRango=jsonBuilder().startObject()
    	     				.field("id", "2")
    	     				.field("ip_inicio", "20.19.0.0")
    	     				.field("ip_fin", "20.20.55.255")
    	     				.field("long", 6)
    	     				.field("lat", 6)
    	     				.field("coord","[6,6]")
    	     				.endObject();

    	     	    		client.prepareIndex("georef", "ip_rangos", "2").setSource(datosIpRango.string().getBytes()).execute()
    	     	    		.actionGet();
    	    		
    		Thread.sleep(5000);
    		
    		//Hacer una consulta para obtener la localizacion
    		SearchResponse response = client.prepareSearch("georef") //uno o más indices
    		        .setTypes("ip_fijas") //un type o varios
    		       //.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
    		       // .setQuery(QueryBuilders.termQuery("ip","10.10.14.10"))             // Query
    		        .setPostFilter(FilterBuilders.termFilter("ip", "10.10.14.10"))   // Filter
    		        .setFrom(0).setSize(60).setExplain(true)
    		        .execute()
    		        .actionGet();
    		
    		SearchRequestBuilder ser = client.prepareSearch("georef") //uno o más indices
	        .setTypes("ip_fijas") //un type o varios
	       //.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
	       // .setQuery(QueryBuilders.termQuery("ip","10.10.14.10"))             // Query
	        .setPostFilter(FilterBuilders.termFilter("ip", "10.10.14.10"))   // Filter
	        .setFrom(0).setSize(60).setExplain(true);
    		
    		System.out.println(ser.toString());
    		
    		Iterator<SearchHit> it = response.getHits().iterator();
    		while(it.hasNext()){
    			SearchHit s = it.next();
    			System.out.println(s.getSourceAsString());
    			System.out.println("Las coordenadas de la IP son: "+ s.getSource().get("coord"));
    		}
    		
    		//consulta de rangos:
    		SearchRequestBuilder	ser1 = client.prepareSearch("georef") //uno o más indices
    		        .setTypes("ip_rangos")
    		        .setQuery(
                    QueryBuilders.boolQuery()       // Your query
                            .must(QueryBuilders.rangeQuery("ip_fin").gte("20.20.2.43"))
                            .must(QueryBuilders.rangeQuery("ip_inicio").lte("20.20.2.43"))
                            );

    		System.out.println(ser1.toString());
    	    		
    		SearchResponse resp2=ser1.execute().actionGet();
    		
    		System.out.println(resp2.toString());
    		
    		 it = resp2.getHits().iterator();
    		while(it.hasNext()){
    			SearchHit s = it.next();
    			System.out.println(s.getSourceAsString());
    			System.out.println("Las coordenadas de la IP son: "+ s.getSource().get("coord"));
    		}
	
         }//fin else de si es localhost o no
    }
}
    
