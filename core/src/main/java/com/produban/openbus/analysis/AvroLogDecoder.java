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
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

import com.produban.openbus.serialization.AvroDeserializer;

/**
 * Trident BaseFunction that decodes Avro messages
 * 
 */
public class AvroLogDecoder extends BaseFunction {	
	private static Logger LOG = LoggerFactory.getLogger(AvroLogDecoder.class);
	private static final long serialVersionUID = -8661808311962745765L;

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
	}
    
    @Override
    public final void execute(final TridentTuple tuple, final TridentCollector collector) {

        LOG.info("HELLO");

    	AvroDeserializer deserializer = new AvroDeserializer();
        byte[] rawAvroMessage = tuple.getBinary(0);
        GenericRecord record;
        try {
            //We only send one record per kafka message, so we fetch only the first position:
            record = deserializer.deserialize(rawAvroMessage).get(0);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deserializing kafka message in Trident topology"+rawAvroMessage, e);
        }

        LOG.info("Deserializing Record:\n" + record.toString());

        Values values = new Values();
        for (Schema.Field avroField : record.getSchema().getFields()) {
            values.add(record.get(avroField.name()).toString());
        }
        collector.emit(values);
    }
}
