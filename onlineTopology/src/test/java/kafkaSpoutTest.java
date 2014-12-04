import java.util.List;

import storm.kafka.Broker;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

import com.produban.openbus.storm_ES.DefaultTupleMapper;
import com.produban.openbus.storm_ES.ElasticSearchBolt;
import com.produban.openbus.storm_ES.StormElasticSearchConstants;
import com.produban.openbus.trident.EchoBolt;


public class kafkaSpoutTest {

	public static void main(String[] args) {
		
		
		Config conf = new Config();
		// conf.put(Config.TOPOLOGY_DEBUG,true);
		conf.put(StormElasticSearchConstants.ES_CLUSTER_NAME,"elasticsearch");
		conf.put(StormElasticSearchConstants.ES_HOST,"localhost");
		conf.put(StormElasticSearchConstants.ES_PORT,9300);
		
		ZkHosts zooHosts = new ZkHosts("localhost:50003");
	    TopologyBuilder builder = new TopologyBuilder();
	    SpoutConfig spoutConfig = new SpoutConfig(zooHosts, "test", "", "STORM-ID");
	
	    //spoutConfig.scheme=new StringScheme();
	   // spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
	    KafkaSpout spout1 =  new KafkaSpout(spoutConfig);
	    builder.setSpout("source", spout1, 1);
		builder.setBolt("echo", new EchoBolt(), 1).shuffleGrouping("source");
	
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("basic_primitives", conf,
				builder.createTopology());
	}
}
