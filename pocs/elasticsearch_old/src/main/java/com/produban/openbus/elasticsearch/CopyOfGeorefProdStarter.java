

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class CopyOfGeorefProdStarter {

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
         IndicesExistsResponse res =  client.admin().indices().prepareExists("geoip").execute().actionGet();
         if (res.isExists()) {
             client.admin().indices().prepareDelete("geoip").
             execute().actionGet();
         }
         
        
         //Crear el mapping
         
         XContentBuilder mappingBuilderRango;

         mappingBuilderRango = jsonBuilder().startObject()
					.startObject("geoip_range")
					.startObject("properties")
			        		.startObject("id")
			        			.field("type", "string")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		.startObject("location")
			        			.field("type","geo_point")
			        			.field("lat_lon", "true")
			        		.endObject()
			        		.startObject("start_ip")
			        			.field("type", "ip")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		.startObject("end_ip")
			        			.field("type", "ip")
			        			.field("index", "not_analyzed")
			        		.endObject()
				        		.startObject("start_ip_dec")
			        			.field("type", "long")
			        		.endObject()
			        		.startObject("end_ip_dec")
			        			.field("type", "long")
			        		.endObject()
			        		.startObject("city")
			        			.field("type", "string")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		.startObject("areaCode")
			        			.field("type", "string")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		.startObject("region")
			        			.field("type", "long")
			        		.endObject()
			        		.startObject("postalCode")
			        			.field("type", "string")
			        		.endObject()
			        		.startObject("metroCode")
			        			.field("type", "string")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		.startObject("country")
			        			.field("type", "string")
			        			.field("index", "not_analyzed")
			        		.endObject()
			        		
		.endObject()
		.endObject()
		.endObject();  
         
	    final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate("geoip");
	    createIndexRequestBuilder.addMapping("geoip_range", mappingBuilderRango).execute().actionGet();
	         
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
	    		
	    	
	    			//En el caso delos rangos
	    			datosIp=jsonBuilder().startObject()
		        			.field("id", valores[0])
		        			.array("location",Float.parseFloat(valores[1].split(",")[0]),Float.parseFloat(valores[1].split(",")[1]))
		        			.field("start_ip", valores[2])
		        			.field("end_ip", valores[3])
		        			.field("start_ip_dec", valores[4])	
		        			.field("end_ip_dec", valores[5])	
		        			.field("city", valores[6])
		        			.field("areaCode", valores[7])
		        			.field("region", valores[8])
		        			.field("postalCode", valores[9])
		        			.field("metroCode", valores[10])	
		        			.field("country", valores[11])
		        			.endObject();
			    	//insertamos el documento por: <index><type><id>
			    	System.out.println(datosIp.string());
			    	client.prepareIndex("geoip", "geoip_range", valores[0]).setSource(datosIp.string().getBytes()).execute()
		            .actionGet();
	    	
	    	}
	    	linea= entrada.readLine(); //siguiente línea
	    } 
	    entrada.close();
	   
    }
	
}
