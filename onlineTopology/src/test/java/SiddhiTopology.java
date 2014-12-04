

import com.produban.openbus.storm.SimpleFileStringSpout;
import com.produban.openbus.trident.EchoBolt;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

public class SiddhiTopology {

    public static void main(String[] args) {
	Config conf = new Config();
	String entrada = "D:\\Produban\\workspace\\StormPOC\\input_proxy.txt";
	SimpleFileStringSpout spout1 = new SimpleFileStringSpout(entrada, "linea");

	TopologyBuilder builder = new TopologyBuilder();

	builder.setSpout("source", spout1, 1);
	builder.setBolt("node", new SiddhiBoltTickTuple(), 1).shuffleGrouping("source");
	builder.setBolt("echo", new EchoBolt(), 1).shuffleGrouping("node");

	LocalCluster cluster = new LocalCluster();
	cluster.submitTopology("basic_primitives", conf, builder.createTopology());
    }
}
