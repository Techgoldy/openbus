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


package com.produban.openbus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;


/**
 * Formatting of the date of the tuple. He sent many tuples as date formats interested 
 * 
 */
public class DatePartition extends BaseFunction {
	private static final long serialVersionUID = -2085404907352931677L;
	private static final Logger LOG = LoggerFactory.getLogger(DatePartition.class);
			
	private static final String CF_HOURLY = "hourly";
	private static final String CF_DAILY = "daily";
	private static final String CF_WEEKLY = "weekly";
	private static final String CF_MONTHLY = "monthly";

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        try {
        	String datetimeInHour = FormatUtil.getDateFormat(tuple.getString(1), 
        			FormatUtil.DATE_FORMAT_WEBSERVER, FormatUtil.DATE_FORMAT_HOUR);   
        	String daily = FormatUtil.getDateFormat(tuple.getString(1), 
        			FormatUtil.DATE_FORMAT_WEBSERVER, FormatUtil.DATE_FORMAT_DAY);
        	String monthly = FormatUtil.getDateFormat(tuple.getString(1), 
        			FormatUtil.DATE_FORMAT_WEBSERVER, FormatUtil.DATE_FORMAT_MONTH);
        	String weekly = FormatUtil.getDateFormat(tuple.getString(1), 
        			FormatUtil.DATE_FORMAT_WEBSERVER, FormatUtil.DATE_FORMAT_YEARWEEK);

        	if (LOG.isDebugEnabled()) {          		
        		LOG.info("Date format. Hour: " + datetimeInHour + " day: " + daily 
        				+ " week: "+ weekly + " month: " + monthly);
        	}
      
        	collector.emit(new Values(CF_HOURLY, datetimeInHour));
        	collector.emit(new Values(CF_DAILY,daily));
        	collector.emit(new Values(CF_WEEKLY, weekly));
        	collector.emit(new Values(CF_MONTHLY, monthly));        	
        } catch (Exception e) {
        	LOG.error("Format timestamp hour: " + tuple.getString(0) + " " + tuple.getString(1) + " ", e); 
		} 
    }
}