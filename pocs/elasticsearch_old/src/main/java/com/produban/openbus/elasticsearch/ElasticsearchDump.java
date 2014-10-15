package com.produban.openbus.elasticsearch;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;


import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;

public class ElasticsearchDump {
	
	static String SEPARADOR = "\001";
	
	 public static void main( String[] args ) throws IOException, InterruptedException
	    {
		 
		 
		 if(args.length!=7){
			 System.out.println("USO: ElasticsearchDump <server> <port> <cluster_name> <index> <type> <cantidad_registros> <Output_file>");
			 System.exit(1);
		 }
		 
	 String elasticSearchHost = args[0];
     int elasticSearchPort = Integer.parseInt(args[1]);
     String elasticSearchCluster = args[2];
     String index=args[3];
     String type =args[4];
     int registros=Integer.parseInt(args[5]);
	
     System.out.println("Cluster: "+elasticSearchCluster+"-->"+elasticSearchHost+":"+elasticSearchPort+"/"+index+"/"+type);
     
     Client client;	
     

		BufferedWriter salida;
		salida = new BufferedWriter(new FileWriter (args[6]));
     
     Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", elasticSearchCluster).build();
     client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(
             elasticSearchHost, elasticSearchPort));
     
     SearchResponse response;
     if(registros!=0)
     {
    	  response = client.prepareSearch(index) //uno o más indices
	        .setTypes(type) //un type o varios
	        .setFrom(0).setSize(registros).setExplain(true)
	        .execute()
	        .actionGet();
     }else{
    	 response = client.prepareSearch(index) //uno o más indices
    		        .setTypes(type) //un type o varios
    		        .setFrom(0).setExplain(true)
    		        .execute()
    		        .actionGet();
     }
     	
     	
	    
     	salida.write(index+"\n");
     	salida.write(type+"\n");
     	

     	ClusterState clusterState = client.admin().cluster().prepareState().execute().actionGet().getState();
        IndexMetaData inMetaData = clusterState.getMetaData().index(index);
        MappingMetaData metad = inMetaData.mapping(type);

        salida.write(metad.source().toString()+"\n");
     	
		Iterator<SearchHit> it = response.getHits().iterator();
		while(it.hasNext()){
			SearchHit s = it.next();
			salida.write(s.getId()+SEPARADOR+s.getSourceAsString()+"\n");
		}
		salida.flush();
    	salida.close();
	}
}
