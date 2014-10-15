package com.produban.openbus.elasticsearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ElasticsearchRestore {

	static String SEPARADOR = "\001";	 
     
     public static void main( String[] args ) throws IOException, InterruptedException
	 {
    	 

		 if(args.length!=4){
			 System.out.println("USO: ElasticsearchRestore: <server> <port> <cluster_name> <Input_file>");
			 System.exit(1);
		 }
		 
    	 String elasticSearchHost = args[0];
         Integer elasticSearchPort = Integer.parseInt(args[1]);
         String elasticSearchCluster = args[2];
         String index="restore";
         String type ="ip_fijas";
         String _id;
         String json;
         String mapping;
    	
         Client client;	
	     
	     Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", elasticSearchCluster).build();
	     client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(
	             elasticSearchHost, elasticSearchPort));
	     
	     //Leer de un fichero
	     BufferedReader entrada;
	     entrada = new BufferedReader(new FileReader(args[3]));
	     
	     
	     //primera línea es el index
	     index=entrada.readLine();
	     //segunda línea type
	     type=entrada.readLine();
	     
	     //tercera linea es el mapping
	     mapping=entrada.readLine();
	     
	     
	   //ver si existe el indice
	     IndicesExistsResponse res =  client.admin().indices().prepareExists(index).execute().actionGet();
         if (res.isExists()) {
             //ver si existe el type:
 
        	 String []ssss=client.admin().indices().prepareTypesExists(index).setTypes(type).request().types();
        	System.out.println("www");
        	if(client.admin().indices().prepareTypesExists(index).setTypes(type).execute().actionGet().isExists()){
        		System.out.println("BORRAMOS");
        		//borramos el type
        		DeleteMappingResponse actionGet = client.admin().indices().prepareDeleteMapping(index).setType(type).execute().actionGet();
        	}
    	     //creramos el mapping del type
    	     client.admin().indices().preparePutMapping(index).setType(type).setSource(mapping).execute().actionGet();

         }else{
        	 //El ínice no existe, creramos el índice y el mapping
        	 client.admin().indices().prepareCreate(index).addMapping(type,mapping).execute().actionGet();
         }
	     
	     String linea=entrada.readLine();
	     while(linea!=null){
	    	 //procesamos la línea
	    	 _id=linea.substring(0,linea.indexOf(SEPARADOR));
	    	 json=linea.substring(linea.indexOf(SEPARADOR)+1,linea.length());
	    	 
	    	 //Creamos el objeto para insertarlo en ElasticSearch
	    	 
	    	 client.prepareIndex(index, type, _id).setSource(json).execute()
	    		.actionGet();
	    	 
	    	 //leemos el siguiente registro
	    	 linea=entrada.readLine();
	     }
	     entrada.close();
	     
	 }
}
