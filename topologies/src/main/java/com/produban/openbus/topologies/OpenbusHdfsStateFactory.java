package com.produban.openbus.topologies;

import java.util.Map;

import backtype.storm.task.IMetricsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.state.State;
import storm.trident.state.StateFactory;

import java.util.Map;

public class OpenbusHdfsStateFactory implements StateFactory {
	 private static final Logger LOG = LoggerFactory.getLogger(OpenbusHdfsStateFactory.class);
	    private OpenbusHdfsState.Options options;

	    public OpenbusHdfsStateFactory(){}

	    public OpenbusHdfsStateFactory withOptions(OpenbusHdfsState.Options options){
	        this.options = options;
	        return this;
	    }

	    @Override
	    public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
	        LOG.info("makeState(partitonIndex={}, numpartitions={}", partitionIndex, numPartitions);
	        OpenbusHdfsState state = new OpenbusHdfsState(this.options);
	        state.prepare(conf, metrics, partitionIndex, numPartitions);
	        return state;
	    }
}
