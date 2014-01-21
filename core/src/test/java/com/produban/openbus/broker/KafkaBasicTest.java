package com.produban.openbus.broker;

import com.produban.openbus.util.KafkaLocal;
import com.produban.openbus.util.ZooKeeperHelper;
import kafka.message.MessageAndMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Class Description
 */

public class KafkaBasicTest {

    private static KafkaLocal kafka;

    @Before
    public void setUp(){
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

    }


    @Test
    public void sendMessageToNewTopic() {

        BasicConsumer consumer = new BasicConsumer("basic_producer_test", "localhost:2181", "test_consumer_group");

        String topic = "basic_producer_test";
        BasicProducer producer = new BasicProducer("localhost:9092", true);
        //produce some messages
        producer.sendMessage(topic, "1", "first message");
        producer.sendMessage(topic, "2", "second message");

        MessageAndMetadata<byte[], byte[]> firstMessage = consumer.consumeOne();
        System.out.println("READ: "+firstMessage.toString());
        assertEquals("first message", new String(firstMessage.message()));

        MessageAndMetadata<byte[], byte[]> secondMessage = consumer.consumeOne();
        assertEquals("second message", new String(secondMessage.message()));

    }


    @After
    public void tearDown() {

        //delete zk data
        ZooKeeperHelper zkHelper = new ZooKeeperHelper();
        try {
//            zkHelper.connect("localhost:2181");
//            zkHelper.delete("/brokers/topics/basic_producer_test/partitions/0");
//            zkHelper.delete("/brokers/topics/basic_producer_test/partitions");
//            zkHelper.delete("/brokers/topics/basic_producer_test");
//            zkHelper.delete("/consumers/test_consumer_group/owners/basic_producer_test");
//            zkHelper.delete("/consumers/test_consumer_group/owners");
//            zkHelper.delete("/consumers/test_consumer_group/ids");
//            zkHelper.delete("/consumers/test_consumer_group/offsets/basic_producer_test");
//            zkHelper.delete("/consumers/test_consumer_group/offsets");
//            zkHelper.delete("/consumers/test_consumer_group");

        }
        catch (Exception e) {
            System.out.println("Error deleting test data on zookeeper");
            e.printStackTrace(System.out);
        }


        try {
            kafka.stop();
        }
        catch (IOException e) {
            System.out.println("Error stopping local kafka server");
            e.printStackTrace(System.out);
        }

    }
}
