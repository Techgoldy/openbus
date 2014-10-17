package com.produban.openbus.storm;

import java.util.Arrays;

import org.apache.log4j.Logger;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

public class EchoBolt extends BaseBasicBolt {

    private static Logger LOG = Logger.getLogger(EchoBolt.class);

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
	LOG.info("Result -> " + tuple.getString(0) + " "+Arrays.toString((Object[]) tuple.getValues().get(1)));
    }
}
