


import com.produban.openbus.storm.SimpleFileStringSpout;
import com.produban.openbus.storm_ES.DefaultTupleMapper;
import com.produban.openbus.storm_ES.ElasticSearchBolt;
import com.produban.openbus.storm_ES.StormElasticSearchConstants;
import com.produban.openbus.trident.EchoBolt;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

public class SiddhiTopologyAux {
	public static void main(String[] args) {
		double a=0D,b=0D;
		System.out.println(a/b);
		Config conf = new Config();
		// conf.put(Config.TOPOLOGY_DEBUG,true);
		conf.put(StormElasticSearchConstants.ES_CLUSTER_NAME,"elasticsearch");
		conf.put(StormElasticSearchConstants.ES_HOST,"localhost");
		conf.put(StormElasticSearchConstants.ES_PORT,9300);
		String entrada = "D:\\test3.txt";
		//String entrada= "D:\\input_postfix_online.txt";
		if (args.length == 2) {
			entrada = args[0];
		}

		SimpleFileStringSpout spout1 = new SimpleFileStringSpout(entrada,
				"linea");

		
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("source", spout1, 1);
		builder.setBolt("node", new SiddhiBoltMetricaOnlineRadius1(), 1).shuffleGrouping("source");
		builder.setBolt("ESBolt", new ElasticSearchBolt(new DefaultTupleMapper())).shuffleGrouping("node");
		//builder.setBolt("echo", new EchoBolt(), 1).shuffleGrouping("node");

		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("basic_primitives", conf,
				builder.createTopology());
	}

}
