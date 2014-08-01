package com.produban.openbus.storm;

import com.produban.openbus.trident.EchoBolt;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

public class SiddhiTopologyAux {
	public static void main(String[] args) {

		Config conf = new Config();
		// conf.put(Config.TOPOLOGY_DEBUG,true);
		String entrada = "D:\\Produban\\workspace\\StormPOC\\input.txt";
		if (args.length == 2) {
			entrada = args[0];
		}

		SimpleFileStringSpout spout1 = new SimpleFileStringSpout(entrada,
				"linea");

		
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("source", spout1, 1);
		builder.setBolt("node", new SiddhiBoltAux(), 1).shuffleGrouping("source");
		builder.setBolt("echo", new EchoBolt(), 1).shuffleGrouping("node");

		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("basic_primitives", conf,
				builder.createTopology());
	}

}
