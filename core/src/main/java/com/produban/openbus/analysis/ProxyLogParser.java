/*
 * Copyright 2013 Produban
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.produban.openbus.analysis;


import backtype.storm.tuple.Values;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.produban.openbus.util.Common.join;


/**
 * A parser for proxy logs. This class translates proxy log records into hashmaps with relevant fields.
 * 
 */
public class ProxyLogParser implements LogParser {

    //Fields:
    public static final String BATCHDATE = "BATCHDATE";
    public static final String PROXYCLASS = "PROXYCLASS";
    public static final String PROXYIP = "PROXYIP";
    public static final String USER = "USER";
    public static final String REQUESTDATE = "REQUESTDATE";
    public static final String HTTPMETHOD = "HTTPMETHOD";
    public static final String URL = "URL";
    public static final String HTTPSTATUS = "HTTPSTATUS";
    public static final String PORT = "PORT";
    public static final String SQUIDRESULTCODE = "SQUIDRESULTCODE";
    public static final String SQUIDHIERARCHYCODE = "SQUIDHIERARCHYCODE";
    public static final String POLICY = "POLICY";
    public static final String EXTRAFIELDS = "EXTRAFIELDS";
    public static final String CLIENTIP = "CLIENTIP";

    //Regex:
    public static Pattern pattern = Pattern.compile("(?<"+BATCHDATE+">\\w+\\s\\d\\d\\s\\d\\d:\\d\\d:\\d\\d)\\s+\\w+\\s\\w+\\s+(?<"+PROXYCLASS+">(\\w+\\.?)+):?\\s+(?<"+PROXYIP+">(\\d+\\.?)+)\\s+\"?(?<"+USER+">(\\-|[^\"]+))\"?\\s+\\-\\s+\\[(?<"+REQUESTDATE+">[^\\]]*)\\]\\s+\"(?<"+HTTPMETHOD+">\\w+)\\s+(?<"+URL+">[^\"]+)\"\\s+(?<"+HTTPSTATUS+">\\d+)\\s+(?<"+PORT+">\\d+)\\s+(?<"+SQUIDRESULTCODE+">\\w+):(?<"+SQUIDHIERARCHYCODE+">\\w+)\\s+\\d+\\s+(?<"+POLICY+">[\\w|\\-]+)\\s+(?<"+EXTRAFIELDS+">\\<[^\\>]+\\>)[\\s|\\-]+client\\-ip\\s+\"(?<"+CLIENTIP+">[\\d|\\.]+)\"");

    private HashMap<String, String> record = new HashMap<String, String>();


	/**
	 * Translate a log line from a proxy log into a HashMap with relevant fields.
	 * 
	 * @param logLine the raw log line to be processed
	 * @return a HashMap containing the extracted fields
	 */
	public HashMap<String,String> parse(String logLine){

		Matcher matcher = pattern.matcher(logLine);
		
		if (matcher.find()) {
			record.put(BATCHDATE, matcher.group(BATCHDATE));
			record.put(PROXYCLASS, matcher.group(PROXYCLASS));
			record.put(PROXYIP, matcher.group(PROXYIP));
			record.put(USER, matcher.group(USER).replaceAll("\\n", "\\\\n")); //some users contain the literal \n
			record.put(REQUESTDATE, matcher.group(REQUESTDATE));
			record.put(HTTPMETHOD, matcher.group(HTTPMETHOD));
			record.put(URL, matcher.group(URL));
			record.put(HTTPSTATUS, matcher.group(HTTPSTATUS));
			record.put(PORT, matcher.group(PORT));
			record.put(SQUIDRESULTCODE, matcher.group(SQUIDRESULTCODE));
			record.put(SQUIDHIERARCHYCODE, matcher.group(SQUIDHIERARCHYCODE));
			record.put(POLICY, matcher.group(POLICY));
			record.put(EXTRAFIELDS, matcher.group(EXTRAFIELDS));
			record.put(CLIENTIP, matcher.group(CLIENTIP));
		}

		return record;
	}

    @Override
    public List<String> fieldNames() {
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add(BATCHDATE);
        fieldNames.add(PROXYCLASS);
        fieldNames.add(PROXYIP);
        fieldNames.add(USER);
        fieldNames.add(REQUESTDATE);
        fieldNames.add(HTTPMETHOD);
        fieldNames.add(URL);
        fieldNames.add(HTTPSTATUS);
        fieldNames.add(PORT);
        fieldNames.add(SQUIDRESULTCODE);
        fieldNames.add(SQUIDHIERARCHYCODE);
        fieldNames.add(POLICY);
        fieldNames.add(EXTRAFIELDS);
        fieldNames.add(CLIENTIP);

        return fieldNames;
    }

    @Override
    public void execute(TridentTuple objects, TridentCollector tridentCollector) {
        String logLine = new String(objects.getBinary(0));

        HashMap<String, String> parsedFields = this.parse(logLine);

        Values tridentValues = new Values();
        tridentValues.add(parsedFields.get(BATCHDATE));
        tridentValues.add(parsedFields.get(PROXYCLASS));
        tridentValues.add(parsedFields.get(PROXYIP));
        tridentValues.add(parsedFields.get(USER));
        tridentValues.add(parsedFields.get(REQUESTDATE));
        tridentValues.add(parsedFields.get(HTTPMETHOD));
        tridentValues.add(parsedFields.get(URL));
        tridentValues.add(parsedFields.get(HTTPSTATUS));
        tridentValues.add(parsedFields.get(PORT));
        tridentValues.add(parsedFields.get(SQUIDRESULTCODE));
        tridentValues.add(parsedFields.get(SQUIDHIERARCHYCODE));
        tridentValues.add(parsedFields.get(POLICY));
        tridentValues.add(parsedFields.get(EXTRAFIELDS));
        tridentValues.add(parsedFields.get(CLIENTIP));

        tridentCollector.emit(tridentValues);
    }

    @Override
    public void prepare(Map map, TridentOperationContext tridentOperationContext) {

    }

    @Override
    public void cleanup() {

    }
}
