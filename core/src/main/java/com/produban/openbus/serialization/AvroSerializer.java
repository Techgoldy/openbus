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

package com.produban.openbus.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.log4j.Logger;


/**
 * A Kafka Serializer class that encode a Kafka Message into Avro binary format with the schema embedded
 */
public class AvroSerializer  {

	static final Logger logger = Logger.getLogger(AvroSerializer.class);

    private Schema schema;
    private String[] fields;    
    
	/**
	 * Create a new Serializer from an byte stream representing an Avro schema and a list of Avro field names.
	 * @param schemaIs Input stream containing an Avro schema that will be embedded.
	 * @param fields list of field names that will be included in the Avro format. Those have to be present in the schema
	 */
    public AvroSerializer(InputStream schemaIs, String[] fields){
    	try {
            this.schema = new Schema.Parser().parse(schemaIs);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not parse Avro schema from provided InputStream", e);
        }

    	this.fields = fields;
	}

    /**
     * Create a new Serializer from an Avro schema and a list of Avro field names.
     * @param schema Avro schema that will be embedded
     * @param fields list of field names that will be included in the Avro message. Those have to be present in the schema
     */
    public AvroSerializer(Schema schema, String[] fields) {
			this.schema = schema;
	    	this.fields = fields;
	}

    /**
     * Takes an array of Avro field values and encodes it in Avro binary format, according to the defined
     * field list and schema.
     * @param values an array of field values that are to be persisted
     * @return an array of bytes that represents the encoded field values and has the Avro schema embedded.
     */
	public byte[] serialize(Object[] values) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(writer);

        try {
            dataFileWriter.create(schema, os);

            GenericRecord datum = new GenericData.Record(schema);
            //String[] values = str.split(delimiter);
            int i=0;
            for(Object value : values){
                datum.put(this.fields[i++], value);
            }

            dataFileWriter.append(datum);
            dataFileWriter.close();

            logger.debug("encoded string: " + os.toString());
            os.close();
        } catch (IOException e) {
            throw new RuntimeException("Error trying to encode provided data into Avro format with provided schema", e);
        }
              
        logger.debug("serialized byte array size: " + os.size());
        return os.toByteArray();	
        
    }

}
