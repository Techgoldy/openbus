package com.produban.openbus.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class GeorefESStarter {


    
	public static void main( String[] args ) throws IOException, InterruptedException
    {
    	 String elasticSearchHost = "localhost";
         Integer elasticSearchPort = 9300;
         String elasticSearchCluster = "elasticsearch";
                  
         Client client;	
         
         if(args.length!=4){
        	 System.out.println("USO: <ElasticSearchHost> <ElasticSearchPort> <ElasticSearchClusterName> <Fichero CSVdatos>");
        	System.exit(1);
         }
         elasticSearchHost=args[0];
    	 elasticSearchPort=Integer.parseInt(args[1]);
    	 elasticSearchCluster=args[2];
         
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
 					.startObject("ip_fija")
 					.startObject("properties")
 			        		.startObject("id")
 			        			.field("type", "string")
 			        			.field("index", "not_analyzed")
 			        		.endObject()
 			        		.startObject("ip")
 			        			.field("type", "ip")
 			        			.field("index", "not_analyzed")
 			        		.endObject()
 			        		.startObject("city")
 			        			.field("type", "string")
 			        			.field("index", "not_analyzed")
 			        		.endObject()
 			        		.startObject("areacode")
 			        			.field("type", "string")
 			        			.field("index", "not_analyzed")
 			        		.endObject()
 			        		.startObject("region")
 			        			.field("type", "long")
 			        		.endObject()
 			        		.startObject("postalcode")
 			        			.field("type", "long")
 			        		.endObject()
 			        		.startObject("metrocode")
 			        			.field("type", "string")
 			        			.field("index", "not_analyzed")
 			        		.endObject()
 			        		.startObject("country")
 			        			.field("type", "string")
 			        			.field("index", "not_analyzed")
 			        		.endObject()
 			        		.startObject("lon")
 			        			.field("type", "float")
 			        		.endObject()
 			        		.startObject("lat")
 			        			.field("type", "float")
 			        		.endObject()
 			        		.startObject("coord")
			        				.field("type","geo_point")
			        				.field("lat_lon",true)
			        			.endObject()
 		.endObject()
 		.endObject()
 		.endObject();
 			
         
         mappingBuilderRango = jsonBuilder().startObject()
					.startObject("ip_rango")
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
			        		.startObject("city")
			        			.field("type", "string")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		.startObject("areacode")
			        			.field("type", "string")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		.startObject("region")
			        			.field("type", "long")
			        		.endObject()
			        		.startObject("postalcode")
			        			.field("type", "long")
			        		.endObject()
			        		.startObject("metrocode")
			        			.field("type", "string")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		.startObject("country")
			        			.field("type", "string")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		.startObject("lon")
			        			.field("type", "float")
			        		.endObject()
			        		.startObject("lat")
			        			.field("type", "float")
			        		.endObject()
			        		.startObject("coord")
			        				.field("type","geo_point")
			        			.endObject()
		.endObject()
		.endObject()
		.endObject();  
         
	    final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate("georef");
	    createIndexRequestBuilder.addMapping("ip_fija", mappingBuilderIP).addMapping("ip_rango", mappingBuilderRango).execute().actionGet();
	         
	    //leemos el fichero CSV
	    BufferedReader entrada;
	    entrada = new BufferedReader(new FileReader(args[3]));
	    
	    //la primera linea la saltamos ya que es la cabecera:
	    String linea = entrada.readLine();
	    linea= entrada.readLine();		//primera línea de datos
	    XContentBuilder datosIp;	//Objeto para construir el JSON
	    String[] valores;
	    
	    while(linea!=null){
	    	valores=linea.split(";");
	    	
	    	if(valores.length==12){
	    		
	    		if(valores[2]==null || valores[1].equals(valores[2])){
	    			//en este caso es una IP fija
			    	//Separamos los campos y preparamos el JSON a cargar
			    	datosIp=jsonBuilder().startObject()
		        			.field("id", valores[0])
		        			.field("ip", valores[1])
		        			.field("city", valores[3])
		        			.field("areaCode", valores[4])
		        			.field("region", valores[5])
		        			.field("postalcode", Integer.parseInt(valores[6]))
		        			.field("metrocode", valores[7])
		        			.field("country", valores[8])		
		        			.field("long", Float.parseFloat(valores[9]))
		        			.field("lat", Float.parseFloat(valores[10]))
		        			.array("coord",Float.parseFloat(valores[9]),Float.parseFloat(valores[10]))
		        			.endObject();
			    	//insertamos el documento por: <index><type><id>
			    	System.out.println(datosIp.string());
			    	client.prepareIndex("georef", "ip_fija", valores[0]).setSource(datosIp.string().getBytes()).execute()
		            .actionGet();
	    		}else{
	    			//En el caso delos rangos
	    			datosIp=jsonBuilder().startObject()
		        			.field("id", valores[0])
		        			.field("ip_inicio", valores[1])
		        			.field("ip_fin", valores[2])
		        			.field("city", valores[3])
		        			.field("areaCode", valores[4])
		        			.field("region", valores[5])
		        			.field("postalcode", Integer.parseInt(valores[6]))
		        			.field("metrocode", valores[7])
		        			.field("country", valores[8])		
		        			.field("long", Float.parseFloat(valores[9]))
		        			.field("lat", Float.parseFloat(valores[10]))
		        			.array("coord",Float.parseFloat(valores[9]),Float.parseFloat(valores[10]))
		        			.endObject();
			    	//insertamos el documento por: <index><type><id>
			    	System.out.println(datosIp.string());
			    	client.prepareIndex("georef", "ip_rango", valores[0]).setSource(datosIp.string().getBytes()).execute()
		            .actionGet();
	    		}
	    	
	    	}
	    	linea= entrada.readLine(); //siguiente línea
	    } 
	    entrada.close();
	   
    }
	
	
}
