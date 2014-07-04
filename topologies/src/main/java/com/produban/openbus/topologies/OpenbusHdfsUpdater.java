package com.produban.openbus.topologies;

import java.util.List;

import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.tuple.TridentTuple;

public class OpenbusHdfsUpdater  extends BaseStateUpdater<OpenbusHdfsState> {
	long ultimaAc=0;

	  @Override
	    public void updateState(OpenbusHdfsState state, List<TridentTuple> tuples, TridentCollector collector) {
	      
		  	state.updateState(tuples, collector);
		  	
	    }

}
