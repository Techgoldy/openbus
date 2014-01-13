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


package com.produban.openbus.persistence;


import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.produban.openbus.util.FormatUtil;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;


/**
 * Function Storm/Trident persistence in HDFS   
 */
public class HDFSPersistence extends BaseFunction {
	private static final long serialVersionUID = -3967587213225365514L;
	private static Logger LOG = LoggerFactory.getLogger(HDFSPersistence.class);    
	    
	private HDFSStore hDFSStore;

	@Override
	@SuppressWarnings("rawtypes") 
	public void prepare(Map conf, TridentOperationContext context) {
		hDFSStore = new HDFSStore();
	}
    
    @Override
    public final void execute(final TridentTuple tuple, final TridentCollector collector) {   
    	String json = null;
        try {            	
            json = (String)tuple.getStringByField("json");
        	String datetimeInHour = FormatUtil.getDateFormat(tuple.getStringByField("datetime"), 
        			FormatUtil.DATE_FORMAT_WEBSERVER, FormatUtil.DATE_FORMAT_HOUR);   
                      			
    		hDFSStore.writeFile2HDFS(datetimeInHour + File.separator + "wslog" + "_" + "_" + System.nanoTime(), json);
    		
    		collector.emit(new Values(json));
        } catch (Exception e) {
        	LOG.error("Persistence HDFS: " + json, e);         	
		} 
    }
}