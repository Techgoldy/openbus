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

			HashMap<String, String> parsedLine = parser.parse(line);

			tuple.append(parsedLine.get(ProxyLogParser.BATCHDATE));
			tuple.append(parsedLine.get(ProxyLogParser.PROXYCLASS));
			tuple.append(parsedLine.get(ProxyLogParser.PROXYIP));
			tuple.append(parsedLine.get(ProxyLogParser.USER));
			tuple.append(parsedLine.get(ProxyLogParser.REQUESTDATE));
			tuple.append(parsedLine.get(ProxyLogParser.HTTPMETHOD));
			tuple.append(parsedLine.get(ProxyLogParser.URL));
			tuple.append(parsedLine.get(ProxyLogParser.HTTPSTATUS));
			tuple.append(parsedLine.get(ProxyLogParser.PORT));
			tuple.append(parsedLine.get(ProxyLogParser.SQUIDRESULTCODE));
			tuple.append(parsedLine.get(ProxyLogParser.SQUIDHIERARCHYCODE));
			tuple.append(parsedLine.get(ProxyLogParser.POLICY));
			tuple.append(parsedLine.get(ProxyLogParser.EXTRAFIELDS));
			tuple.append(parsedLine.get(ProxyLogParser.CLIENTIP));

			return tuple;
		} catch (Exception e) {
			return null;
		}
	}

 public Schema outputSchema(Schema input) {
     try {
         Schema s = new Schema();

         s.add(new Schema.FieldSchema(ProxyLogParser.BATCHDATE, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.PROXYCLASS, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.PROXYIP, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.USER, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.REQUESTDATE, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.HTTPMETHOD, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.URL, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.HTTPSTATUS, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.PORT, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.SQUIDRESULTCODE, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.SQUIDHIERARCHYCODE, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.POLICY, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.EXTRAFIELDS, DataType.CHARARRAY));
         s.add(new Schema.FieldSchema(ProxyLogParser.CLIENTIP, DataType.CHARARRAY));

         return s;
     } catch (Exception e) {
         return null;
     }
 }
}
