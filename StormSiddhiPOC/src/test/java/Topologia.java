
import java.io.IOException;

import com.produban.openbus.storm.SimpleFileStringSpout;
import com.produban.openbus.trident.HDFSStore;
import com.produban.openbus.trident.ParseProxy;
import com.produban.openbus.trident.Print;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.testing.FixedBatchSpout;


public class Topologia {

	public static void main(String[] args) {
		
		Config conf = new Config();
//      conf.put(Config.TOPOLOGY_DEBUG,true);
		String entrada="/home/cloudera/main_sample.log";
		String salida="/home/cloudera/salida_proxy_soh.log";
		if(args.length==2){
			entrada=args[0];
			salida=args[1];
		}
      
		SimpleFileStringSpout spout1 = new SimpleFileStringSpout(entrada, "linea");

		TridentTopology topology = new TridentTopology();
		Stream  parseaLogs =
		     topology.newStream("spout1", spout1)
		       .each(new Fields("linea"), new ParseProxy(), new Fields("parseado"))
		       .each(new Fields("parseado"),new Print("",salida));
		 
		
		LocalCluster cluster = new LocalCluster();
	      cluster.submitTopology("basic_primitives", conf, topology.build());
		
		/*HDFSStore hdfsOutput =new HDFSStore();
		try {
			System.out.println("hey man!");
			hdfsOutput.writeFile2HDFS("prueba1", "hola esto va de prueba");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);*/
	      
	}
	
	
	

}
