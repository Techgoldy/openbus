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


import org.apache.avro.generic.GenericRecord;
import org.junit.Test;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Unit tests for Avro serialization / deserialization
 */
public class AvroSerializationTests {

    @Test
    public void  serializeSomeUsers () throws IOException {

        AvroSerializer serializer =
                new AvroSerializer(getClass().getResourceAsStream("/user.avsc"),
                        new String[] {"name", "favorite_number", "favorite_color"});

        byte[] user1 = serializer.serialize(new Object[]{"fernando", new Integer(7), "rojo"});
        byte[] user2 = serializer.serialize(new Object[]{"maría", new Integer(99), "verde"});


        //Write a file with generated records
        FileOutputStream out = new FileOutputStream("user1.avro");
        out.write(user1);
        out.close();

        out = new FileOutputStream("user2.avro");
        out.write(user2);
        out.close();

        //deserialize the users:
        AvroDeserializer deserializer = new AvroDeserializer();

        Path path = Paths.get("user1.avro");
        byte[] binaryData = Files.readAllBytes(path);

        List<GenericRecord> readRecrods = deserializer.deserialize(binaryData);
        for (GenericRecord record : readRecrods) {
            System.out.println(record);
        }

    }

}
