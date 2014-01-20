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

package com.produban.openbus.broker;

import com.produban.openbus.serialization.AvroDeserializer;
import kafka.message.MessageAndMetadata;
import org.apache.avro.generic.GenericRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Class Description
 */

public class KafkaAvroMessagesTest {

    private static KafkaLocal kafka;

    private AvroProducer userProducer;
    private AvroProducer logProducer;
    private String[] userFields;
    private String[] logFields;
    private String userTopic = "test_users";
    private String logTopic = "test_logs";
    private BasicConsumer userConsumer;
    private BasicConsumer logConsumer;
    private AvroDeserializer deserializer;

    @Before
    public void setUp() {
        //Start local kafka instance
        Properties kafkaProperties = new Properties();
        try {
            //start kafka
            kafkaProperties.load(Class.class.getResourceAsStream("/kafkalocal.properties"));
            kafka = new KafkaLocal(kafkaProperties);
            Thread.sleep(5000);
        } catch (Exception e){
            e.printStackTrace(System.out);
            fail("Error running local Kafka broker");
            e.printStackTrace(System.out);
        }

        //producers
        this.userFields = new String[] {"name", "favorite_number", "favorite_color"};
        this.userProducer = new AvroProducer("localhost:9092", userTopic, "/user.avsc", this.userFields);

        this.logFields = new String[] {"host", "log", "user", "datetime", "request", "status", "size",
                                       "referer", "userAgent", "session", "responseTime"};
        this.logProducer = new AvroProducer("localhost:9092", logTopic, "/apacheLog.avsc", this.logFields );


        //consumers
        this.userConsumer = new BasicConsumer(userTopic, "localhost:2181", "groupTestUsers");
        this.logConsumer = new BasicConsumer(logTopic, "localhost:2181", "groupTestLogs");

        //common
        this.deserializer = new AvroDeserializer();
    }

    @Test
    public void testUsers() throws IOException {

        //Data we will send
        Object[][] sentData = {   new Object[] {"fernando", 77, "rojo"},
                                  new Object[] {"María", 150, "verde"},
                                  new Object[] {"Ramón Ramirez", 123, "azul"}};

        //produce user messages
        for (Object[] dataItem : sentData) {
            userProducer.send(dataItem);
        }

        //consume user messages
        for (Object[] sentItem : sentData) {
            MessageAndMetadata<byte[], byte[]> data = userConsumer.consumeOne();
            byte[] avroMessage = data.message();

            List<GenericRecord> records = deserializer.deserialize(avroMessage);
            GenericRecord record = records.get(0); //always the first record

            assertEquals(record.get("name").toString(), sentItem[0]);
            assertEquals(record.get("favorite_number"), sentItem[1]);
            assertEquals(record.get("favorite_color").toString(), sentItem[2]);
        }

    }

    //@Test
    public void testLogs() throws IOException {

        //Data we will send
        String HOSTREMOTO="85.155.188.198";
        String NOMBRELOGREMOTO="-";
        String USUARIOREMOTO="user";
        String TIEMPOEJECPETICION="[17/Sep/2012:19:01:24+0200]";
        String LINEAPETICION="index";
        String ESTADOPETICION="200";
        String TAMANORESPUESTA="3117";
        String REFERER="-";
        String USERAGENT="Chrome/21.0.1180.89";
        String IDSESION="0000z2ur1hruUUG-MhpsITK9JY_:16vnisqka";
        String TIEMPORESPUESTA="";

        Object[][] sentData = {
                    new Object[] {HOSTREMOTO, NOMBRELOGREMOTO, USUARIOREMOTO, TIEMPOEJECPETICION, LINEAPETICION,
                                  ESTADOPETICION, TAMANORESPUESTA, REFERER, USERAGENT, IDSESION, TIEMPORESPUESTA}
        };

        //produce log messages
        for (Object[] dataItem : sentData) {
            logProducer.send(dataItem);
        }

        //consume log messages
        for (Object[] sentItem : sentData) {
            MessageAndMetadata<byte[], byte[]> data = logConsumer.consumeOne();
            byte[] avroMessage = data.message();

            List<GenericRecord> records = deserializer.deserialize(avroMessage);
            GenericRecord record = records.get(0); //always the first record
            assertEquals(record.get("host").toString(), sentItem[0]);
            assertEquals(record.get("log").toString(), sentItem[1]);
            assertEquals(record.get("user").toString(), sentItem[2]);
            assertEquals(record.get("datetime").toString(), sentItem[3]);
            assertEquals(record.get("request").toString(), sentItem[4]);
            assertEquals(record.get("status").toString(), sentItem[5]);
            assertEquals(record.get("size").toString(), sentItem[6]);
            assertEquals(record.get("referer").toString(), sentItem[7]);
            assertEquals(record.get("userAgent").toString(), sentItem[8]);
            assertEquals(record.get("session").toString(), sentItem[9]);
            assertEquals(record.get("responseTime").toString(), sentItem[10]);
        }
    }

    @After
    public void tearDown() {
        try {
            kafka.stop();
        }
        catch (IOException e) {
            System.out.println("Error stopping local kafka server");
            e.printStackTrace(System.out);
        }

    }



}
