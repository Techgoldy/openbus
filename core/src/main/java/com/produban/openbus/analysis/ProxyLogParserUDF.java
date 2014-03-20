package com.produban.openbus.analysis;

import java.io.IOException;
import java.util.HashMap;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.schema.Schema;


public class ProxyLogParserUDF extends EvalFunc<Tuple> {

	private ProxyLogParser parser = new ProxyLogParser();
	
	public Tuple exec(Tuple input) throws IOException {
		if (null == input || input.size() != 1) {
			return null;
		}
		String line = (String) input.get(0);
		try {
			TupleFactory tf = TupleFactory.getInstance();
			Tuple tuple = tf.newTuple();

			HashMap<String, String> parsedLine = parser.parseLogLine(line);

			tuple.append(parsedLine.get("BATCHDATE"));
			tuple.append(parsedLine.get("PROXYCLASS"));
			tuple.append(parsedLine.get("PROXYIP"));
			tuple.append(parsedLine.get("USER"));
			tuple.append(parsedLine.get("REQUESTDATE"));
			tuple.append(parsedLine.get("HTTPMETHOD"));
			tuple.append(parsedLine.get("URL"));
			tuple.append(parsedLine.get("HTTPSTATUS"));
			tuple.append(parsedLine.get("PORT"));
			tuple.append(parsedLine.get("SQUIDRESULTCODE"));
			tuple.append(parsedLine.get("SQUIDHIERARCHYCODE"));
			tuple.append(parsedLine.get("POLICY"));
			tuple.append(parsedLine.get("EXTRAFIELDS"));
			tuple.append(parsedLine.get("CLIENTIP"));

			return tuple;
		} catch (Exception e) {
			return null;
		}
	}

 public Schema outputSchema(Schema input) {
     try {
         Schema s = new Schema();

         s.add(new Schema.FieldSchema("batch_date", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("proxy_class", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("proxy_ip", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("user", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("request_date", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("http_method", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("url", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("http_status", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("port", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("squid_result_code", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("squid_hierarchy_code", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("policy", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("extra_fields", DataType.CHARARRAY));
         s.add(new Schema.FieldSchema("client_ip", DataType.CHARARRAY));

         return s;
     } catch (Exception e) {
         return null;
     }
 }
}
