package com.produban.openbus.trident;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;


public class ParseProxy extends BaseFunction {
	
	private static final Logger log = LoggerFactory.getLogger(ParseProxy.class);
	public static final char SEPARADOR='\001'; //caracter SOH
	public static Pattern pattern = Pattern.compile("(?<cabecera>(.{15}) (.*) ([^:]*)): \\s+((?<eventTimeStamp>(.{19})) (?<timeTaken>(\\d+)) (?<clientIP>(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})) (?<User>(.*?)) (?<Group>(.*?)) (?<Exception>(.*?)) (?<filterResult>(\\S+)) (?<category>\"?([^\"]*)\"?) (?<referer>(\\S+))\\s+(?<responseCode>(\\d+)) (?<action>(\\S+)) (?<method>(\\S+)) (?<contentType>(\\S+)) (?<protocol>(\\S+)) (?<requestDomain>(\\S+)) (?<requestPort>(\\d+)) (?<requestPath>(\\S+)) (?<requestQuery>(.*?)) (?<requestURIExtension>(\\S+)) (?<userAgent>\"?([^\"]*)\"?) (?<serverIP>(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})) (?<scBytes>(\\d+)) (?<csBytes>(\\d+)) (?<virusID>(\\S+)) (\\S+) (\\S+) (?<destinationIP>(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}))?(.*))");
	
	@Override
	public void execute(TridentTuple tupla, TridentCollector colector) {
		Matcher matcher = pattern.matcher(tupla.getString(0));
		String resultado;
		if (matcher.find()) {
				
		//colector.emit(new Values(resultado));
			colector.emit(new Values(matcher.group("eventTimeStamp"),
					matcher.group("timeTaken"),
					matcher.group("clientIP"),
					matcher.group("User"),
					matcher.group("Group"),
					matcher.group("Exception"),
					matcher.group("filterResult"),
					matcher.group("category"),
					matcher.group("referer"),
					matcher.group("responseCode"),
					matcher.group("action"),
					matcher.group("method"),
					matcher.group("contentType"),
					matcher.group("protocol"),
					matcher.group("requestDomain"),
					matcher.group("requestPort"),
					matcher.group("requestPath"),
					matcher.group("requestQuery"),
					matcher.group("requestURIExtension"),
					matcher.group("userAgent"),
					matcher.group("serverIP"),
					matcher.group("scBytes"),
					matcher.group("csBytes"),
					matcher.group("virusID"),
					matcher.group("destinationIP")));
		}
	}

}

