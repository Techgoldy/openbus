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

import static org.junit.Assert.*;
import java.util.HashMap;
import org.junit.Test;
import com.produban.openbus.analysis.ProxyLogParser;

public class ProxyLogParserTest {
	
	//Anonymized examples of proxy log lines:
	public static final String LOGLINE1 = "Jun 01 00:14:45 blah blah Bank.Bank.CommunicationSystems.Proxy.production.SAN.blah: 172.11.11.111 "
										 +"\"SI_EUROPE\n00001@Validacion_GABP.BSCH\" - [01/Jun/2013:00:00:00 +0200] \"POST http://sock7.pusher.com/pusher/498/t6b45e4l/xhr_streaming\""
										 + " 200 2088 TCP_MISS:DIRECT 155 DEFAULT_CASE_11-AUTH_Policy-DefaultGroup-NONE-NONE-NONE-DefaultGroup "
										 + "<IW_comp,-5.4,\"-\",\"-\",-,-,-,\"-\",\"-\",-,-,-,\"-\",\"-\",-,\"-\",\"-\",-,-,IW_comp,-,\"-\",\"trojan\",\"Unknown\",\"Unknown\",\"-\",\"-\",128.05,0,-,\"-\",\"-\"> "
										 + "- client-ip \"180.11.11.11\"";
	
	public static final String LOGLINE2 = "Jun 01 00:14:45 blah blah Bank.Bank.CommunicationSystems.Proxy.production.SAN.blah: 172.11.11.222 "
			                            + "- - [01/Jun/2013:00:01:26 +0200] \"GET http://publishing.kalooga.com/mod/widgetloader-20278.js\" "
			                            + "200 1679 TCP_MISS:DIRECT 32 DEFAULT_CASE_11-NO_AUTH_Policy-No_AUTH_SERVERs_Identity-NONE-NONE-NONE-DefaultGroup "
			                            + "<IW_busi,0.0,\"-\",\"-\",-,-,-,\"-\",\"-\",-,-,-,\"-\",\"-\",-,\"-\",\"-\",-,-,IW_busi,-,\"-\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",531.75,0,-,\"-\",\"-\"> "
			                            + "- client-ip \"180.11.11.11\"";
	
	public static final String LOGLINE3 = "Jun 01 05:14:45 blah blah Bank.Bank.CommunicationSystems.Proxy.production.SAN.blah: 172.11.11.111 "
			                            + "\"SI_EUROPE\n00001@Validacion_GABP.BSCH\" - [01/Jun/2013:04:26:05 +0200] \"POST http://87.248.205.81/idle/WSJmaD8irK7QiGoZ/221\" "
			                            + "200 1 TCP_CLIENT_REFRESH_MISS:DIRECT 496 DEFAULT_CASE_11-AUTH_Policy-DefaultGroup-NONE-NONE-NONE-DefaultGroup "
			                            + "<IW_srch,-3.5,\"-\",\"-\",-,-,-,\"-\",\"-\",-,-,-,\"-\",\"-\",-,\"-\",\"-\",-,-,IW_srch,-,\"-\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",4.24,0,-,\"-\",\"-\"> "
			                            + "- client-ip \"180.11.11.11\"";
	
	private ProxyLogParser parser = new ProxyLogParser();
	
	@Test
	public void parseLogLine1() {
		HashMap<String, String> record = parser.parseLogLine(LOGLINE1);
		
		assertEquals("Jun 01 00:14:45", record.get("BATCHDATE"));
		assertEquals("Bank.Bank.CommunicationSystems.Proxy.production.SAN.blah", record.get("PROXYCLASS"));
		assertEquals("172.11.11.111", record.get("PROXYIP"));
		assertEquals("SI_EUROPE\\n00001@Validacion_GABP.BSCH", record.get("USER"));
		assertEquals("01/Jun/2013:00:00:00 +0200", record.get("REQUESTDATE"));
		assertEquals("POST", record.get("HTTPMETHOD"));
		assertEquals("http://sock7.pusher.com/pusher/498/t6b45e4l/xhr_streaming", record.get("URL"));
		assertEquals("200", record.get("HTTPSTATUS"));
		assertEquals("2088", record.get("PORT"));
		assertEquals("TCP_MISS", record.get("SQUIDRESULTCODE"));
		assertEquals("DIRECT", record.get("SQUIDHIERARCHYCODE"));
		assertEquals("DEFAULT_CASE_11-AUTH_Policy-DefaultGroup-NONE-NONE-NONE-DefaultGroup", record.get("POLICY"));
		assertEquals("<IW_comp,-5.4,\"-\",\"-\",-,-,-,\"-\",\"-\",-,-,-,\"-\",\"-\",-,\"-\",\"-\",-,-,IW_comp,-,\"-\",\"trojan\",\"Unknown\",\"Unknown\",\"-\",\"-\",128.05,0,-,\"-\",\"-\">", record.get("EXTRAFIELDS"));
		assertEquals("180.11.11.11", record.get("CLIENTIP"));
	}
	
	@Test
	public void parseLogLine2() {
		HashMap<String, String> record = parser.parseLogLine(LOGLINE2);
		
		assertEquals("Jun 01 00:14:45", record.get("BATCHDATE"));
		assertEquals("Bank.Bank.CommunicationSystems.Proxy.production.SAN.blah", record.get("PROXYCLASS"));
		assertEquals("172.11.11.222", record.get("PROXYIP"));
		assertEquals("-", record.get("USER"));
		assertEquals("01/Jun/2013:00:01:26 +0200", record.get("REQUESTDATE"));
		assertEquals("GET", record.get("HTTPMETHOD"));
		assertEquals("http://publishing.kalooga.com/mod/widgetloader-20278.js", record.get("URL"));
		assertEquals("200", record.get("HTTPSTATUS"));
		assertEquals("1679", record.get("PORT"));
		assertEquals("TCP_MISS", record.get("SQUIDRESULTCODE"));
		assertEquals("DIRECT", record.get("SQUIDHIERARCHYCODE"));
		assertEquals("DEFAULT_CASE_11-NO_AUTH_Policy-No_AUTH_SERVERs_Identity-NONE-NONE-NONE-DefaultGroup", record.get("POLICY"));
		assertEquals("<IW_busi,0.0,\"-\",\"-\",-,-,-,\"-\",\"-\",-,-,-,\"-\",\"-\",-,\"-\",\"-\",-,-,IW_busi,-,\"-\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",531.75,0,-,\"-\",\"-\">", record.get("EXTRAFIELDS"));
		assertEquals("180.11.11.11", record.get("CLIENTIP"));
	}
	
	@Test
	public void parseLogLine3() {
		HashMap<String, String> record = parser.parseLogLine(LOGLINE3);
		
		assertEquals("Jun 01 05:14:45", record.get("BATCHDATE"));
		assertEquals("Bank.Bank.CommunicationSystems.Proxy.production.SAN.blah", record.get("PROXYCLASS"));
		assertEquals("172.11.11.111", record.get("PROXYIP"));
		assertEquals("SI_EUROPE\\n00001@Validacion_GABP.BSCH", record.get("USER"));
		assertEquals("01/Jun/2013:04:26:05 +0200", record.get("REQUESTDATE"));
		assertEquals("POST", record.get("HTTPMETHOD"));
		assertEquals("http://87.248.205.81/idle/WSJmaD8irK7QiGoZ/221", record.get("URL"));
		assertEquals("200", record.get("HTTPSTATUS"));
		assertEquals("1", record.get("PORT"));
		assertEquals("TCP_CLIENT_REFRESH_MISS", record.get("SQUIDRESULTCODE"));
		assertEquals("DIRECT", record.get("SQUIDHIERARCHYCODE"));
		assertEquals("DEFAULT_CASE_11-AUTH_Policy-DefaultGroup-NONE-NONE-NONE-DefaultGroup", record.get("POLICY"));
		assertEquals("<IW_srch,-3.5,\"-\",\"-\",-,-,-,\"-\",\"-\",-,-,-,\"-\",\"-\",-,\"-\",\"-\",-,-,IW_srch,-,\"-\",\"-\",\"Unknown\",\"Unknown\",\"-\",\"-\",4.24,0,-,\"-\",\"-\">", record.get("EXTRAFIELDS"));
		assertEquals("180.11.11.11", record.get("CLIENTIP"));
	}
}
