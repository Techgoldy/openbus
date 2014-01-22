Architecture
============

High level architecture
-----------------------

.. image:: /images/lambda.002.png

Data stream: Apache Kafka
-------------------------

We use Apache kafka as a central hub for collecting different types of events.

Multiple systems will be publishing events into Kafka topics.

At the moment we are using Avro format for all the published events.

Introduction
............

Producing
.........

Kafka uses a custom TCP based `protocol <https://cwiki.apache.org/confluence/display/KAFKA/A+Guide+To+The+Kafka+Protocol>`_ to expose its API. Apart from a JVM client maintained in its own codebase, there are client libraries for the following languages:

	- Python
	- Go
	- C
	- C++
	- Clojure
	- Ruby
	- NodeJS
	- Storm
	- Scala DSL
	- JRuby

Becoming a publisher in Kafka is not very difficult. You will need a partial list of your Kafka brokers (it doesn't have to be exhaustive, since the client uses those endpoints to query about the topic leaders) and a topic name.

This is an example of a very simple Kafka producer with Java:

.. code-block:: java

   	import java.util.Date;
	import java.util.Properties;
	import java.util.Random;

	import kafka.javaapi.producer.Producer;
	import kafka.producer.KeyedMessage;
	import kafka.producer.ProducerConfig;

	public class BasicProducer {

        Producer<String, String> producer;
        
        public BasicProducer(String brokerList, boolean requiredAcks) {
                Properties props = new Properties();
                props.put("metadata.broker.list", brokerList);
                props.put("request.required.acks", requiredAcks ? "1" : "0");
                props.put("serializer.class", "kafka.serializer.StringEncoder");
                
                producer = new Producer<String, String>(new ProducerConfig(props));
        }
        
        public void sendMessage(String topic, String key, String value){
                KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, key, value);
                producer.send(data);
        }
        
        /**
         * Creates a simulated random log event and sends it to a kafka topic.
         * 
         * @param topic topic where the message will be sent
         */
        public void sendRandomLogEvent(String topic){
                //Build random IP message
                Random rnd = new Random();
                long runtime = new Date().getTime();
                String ip = "192.168.2." + rnd.nextInt(255);
                String msg = runtime + ", www.example.com, "+ ip;
                
                //Send the message to the broker
                this.sendMessage(topic, ip, msg);
        }
	}


Avro
....


Consuming
.........


Batch Layer: Hadoop
-------------------

All Data: HDFS
..............

Generating Batch views: Mapreduce
.................................

Speed Layer: Storm
------------------

Generating Realtime views: Storm Topologies
...........................................

Serving Layer
-------------

HBase
.....

