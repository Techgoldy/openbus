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


import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A parser for proxy logs. This class translates proxy log records into hashmaps with relevant fields.
 * 
 */
public class ProxyLogParser {
	
	public static Pattern pattern = Pattern.compile("(?<BATCHDATE>\\w+\\s\\d\\d\\s\\d\\d:\\d\\d:\\d\\d)\\s+\\w+\\s\\w+\\s+(?<PROXYCLASS>(\\w+\\.?)+):?\\s+(?<PROXYIP>(\\d+\\.?)+)\\s+\"?(?<USER>(\\-|[^\"]+))\"?\\s+\\-\\s+\\[(?<REQUESTDATE>[^\\]]*)\\]\\s+\"(?<HTTPMETHOD>\\w+)\\s+(?<URL>[^\"]+)\"\\s+(?<HTTPSTATUS>\\d+)\\s+(?<PORT>\\d+)\\s+(?<SQUIDRESULTCODE>\\w+):(?<SQUIDHIERARCHYCODE>\\w+)\\s+\\d+\\s+(?<POLICY>[\\w|\\-]+)\\s+(?<EXTRAFIELDS>\\<[^\\>]+\\>)[\\s|\\-]+client\\-ip\\s+\"(?<CLIENTIP>[\\d|\\.]+)\"");
	
	/**
	 * Translate a log line from a proxy log into a HashMap with relevant fields.
	 * 
	 * @param logLine the raw log line to be processed
	 * @return a HashMap containing the extracted fields
	 */
	public HashMap<String,String> parseLogLine(String logLine){
		HashMap<String, String> record = new HashMap<String, String>();
		Matcher matcher = pattern.matcher(logLine);
		
		if (matcher.find()) {
			record.put("BATCHDATE", matcher.group("BATCHDATE"));
			record.put("PROXYCLASS", matcher.group("PROXYCLASS"));
			record.put("PROXYIP", matcher.group("PROXYIP"));
			record.put("USER", matcher.group("USER").replaceAll("\\n", "\\\\n")); //some users contain the literal \n
			record.put("REQUESTDATE", matcher.group("REQUESTDATE"));
			record.put("HTTPMETHOD", matcher.group("HTTPMETHOD"));
			record.put("URL", matcher.group("URL"));
			record.put("HTTPSTATUS", matcher.group("HTTPSTATUS"));
			record.put("PORT", matcher.group("PORT"));
			record.put("SQUIDRESULTCODE", matcher.group("SQUIDRESULTCODE"));
			record.put("SQUIDHIERARCHYCODE", matcher.group("SQUIDHIERARCHYCODE"));
			record.put("POLICY", matcher.group("POLICY"));
			record.put("EXTRAFIELDS", matcher.group("EXTRAFIELDS"));
			record.put("CLIENTIP", matcher.group("CLIENTIP"));
		}
		
		return record;
		
	}
}
