/*
 *
 *  * Copyright 2013 Produban
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.produban.openbus.serialization;

import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to take the content of an Avro DataFile as an array of bytes and
 * deserialize it.
 * It assumes that the avro schema is embedded at the beginning of the byte array.
 */
public class AvroDeserializer {

    public List<GenericRecord> deserialize(byte[] avroMessage) throws IOException {

        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>();
        ByteArrayInputStream is = new ByteArrayInputStream(avroMessage);
        DataFileStream<GenericRecord> dataFileReader;
        dataFileReader = new DataFileStream<>(is, datumReader);
        GenericRecord record = null;

        List<GenericRecord> readRecords = new ArrayList<>();
        while (dataFileReader.hasNext()) {
            readRecords.add(dataFileReader.next(record));
        }
        dataFileReader.close();

        return readRecords;
    }
}
