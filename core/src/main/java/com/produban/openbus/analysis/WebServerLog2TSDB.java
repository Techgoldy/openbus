/*
* Copyright 2013 Produban
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.produban.openbus.analysis;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.Aggregator;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

import com.produban.openbus.util.Conf;
import com.produban.openbus.persistence.OpenTSDBRecoder;
import com.produban.openbus.persistence.RemoteRecoder;


import com.produban.openbus.analysis.WebServerLog2TSDB.CountState;

/** 
 * Count the requests of the logs and send them to TSDB
 */
public class WebServerLog2TSDB implements Aggregator<CountState> {
	private static final long serialVersionUID = 6457457778660543357L;
	private static Logger LOG = LoggerFactory.getLogger(WebServerLog2TSDB.class);
 	
    class CountState {
        long count = 0;
        String user;
        String request;
        String datetime;
        String host;
        String log;
        String status;
        String size;
        String referer;
        String userAgent;
        String session;
        String responseTime;
        String timestamp;        
        RemoteRecoder recoder;
    }

    public CountState init(Object batchId, TridentCollector collector) {        
    	RemoteRecoder recorder = new OpenTSDBRecoder();
    	
        try {
			recorder.init(Conf.HOST_OPENTSDB, Conf.PORT_OPENTSDB);
		} catch (UnknownHostException e) {
			LOG.error("OpenTSDB no host exception " + Conf.HOST_OPENTSDB + " : " + Conf.PORT_OPENTSDB, e);
		} catch (IOException e) {
			LOG.error("OpenTSDB IOexception " + Conf.HOST_OPENTSDB + " : " + Conf.PORT_OPENTSDB, e);
		}

        CountState cs = new CountState();
        cs.recoder=recorder;
        
        return cs;
    }

    public void aggregate(CountState state, TridentTuple tuple, TridentCollector collector) {       	
        state.count+=1;
        state.user = tuple.getStringByField("user");
        state.request = tuple.getStringByField("request");
        state.host = tuple.getStringByField("host");
        state.datetime = tuple.getStringByField("datetime");
        state.status = tuple.getStringByField("status");
        state.size = tuple.getStringByField("size");
        state.referer = tuple.getStringByField("referer");
        state.userAgent = tuple.getStringByField("userAgent");
        state.session = tuple.getStringByField("session");
        state.responseTime = tuple.getStringByField("responseTime");
        state.timestamp = tuple.getStringByField("timestamp");                
    }

    public void complete(CountState state, TridentCollector collector) {
    	if(state.count > 0) {
    		String metric = "put apache.requests " + state.timestamp + " " +  state.count + " user=" + state.user 
    				+ " request=" + state.request + " host=" + state.host + " userAgent=" + state.userAgent 
    				+ " session=" + state.session + " \n"; 
    		    		
    		try {
				state.recoder.send(metric);
		        state.recoder.close();
			} catch (IOException e) {
				LOG.error("OpenTSDB IOExcep " + Conf.HOST_OPENTSDB + " : " + Conf.PORT_OPENTSDB, e);
			}
  
    		if (LOG.isDebugEnabled())  LOG.debug(metric);
    		
//TODO: Remove    		
LOG.info(metric);

    		collector.emit(new Values(state.count));
    	}
    }
    
    @Override
    @SuppressWarnings("rawtypes")	
	public void prepare(Map conf, TridentOperationContext context) {				
	}

	@Override
	public void cleanup() {
	}
}