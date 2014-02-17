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

Kafka is designed as a unified platform for handling all the real-time data feeds a large company might have.

Kafka is run as a cluster of *broker* servers. *Messages* are stored in categories called *topics*. Those messages are published by *producers* and consumed and further processed by *consumers*.

It uses a custom TCP Protocol for communication between clients and servers.

A Kafka topic is splitted into one or more partitions. Partitions are distributed over the servers in the Kafka cluster. Each partition is replicated across a configurable number of servers.

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

As previously said, we are using the Avro data format to serialize all the data events we produce in our Kafka data stream.

This means that prior to send a message into Kafka, we are serializing it in Avro format, using a concrete Avro schema. This schema is embedded in the data we send to Kafka, so every future consumer of the message will be able to deserialize it.

In the Openbus code you can find `AvroSerializer` and `AvroDeserialzer` Java lasses, that can be of great help when using producing or consuming Avro messages from Kafka.

This is the current Avro schema we are using for Log messages:

.. code-block:: json

	{
	  "type": "record",
	  "name": "ApacheLog",
	  "namespace": "openbus.schema",  
	  "doc": "Apache Log Event",
	  "fields": [
	    {"name": "host", "type": "string"},
	    {"name": "log", "type": "string"},
	    {"name": "user", "type": "string"},
	    {"name": "datetime", "type": "string"},
	    {"name": "request", "type": "string"},    
	    {"name": "status", "type": "string"},
	    {"name": "size", "type": "string"},
	    {"name": "referer", "type": "string"},
	    {"name": "userAgent", "type": "string"},
	    {"name": "session", "type": "string"},
	    {"name": "responseTime", "type": "string"}
	  ]
	}


An example of producing Avro messages into Kafka is our AvroProducer class:

.. code-block:: java

	public class AvroProducer {
	        
	        private Producer<byte[], byte[]> producer;
	        private AvroSerializer serializer;
	        private String topic;
	        
	    public AvroProducer(String brokerList, String topic, String avroSchemaPath, String[] fields) {

	        this.topic=topic;
	            this.serializer = new AvroSerializer(ClassLoader.class.getResourceAsStream(avroSchemaPath), fields );

	        Properties props = new Properties();
	        props.put("metadata.broker.list", brokerList);
	        this.producer = new kafka.javaapi.producer.Producer<>(new ProducerConfig(props));
	                   
	    }
	    
	    /**
	     * Send a message 
	     * @param values Array of Avro field values to be sent to kafka
	     */
	    public void send(Object[] values) {
	            Message message = new Message(serializer.serialize(values));
	                //producer.send(new KeyedMessage<byte[], byte[]>(topic, message.buffer().array()));
	        producer.send(new KeyedMessage<byte[], byte[]>(topic, serializer.serialize(values)));
	    }

	    /**
	     * closes producer
	     */
	    public void close() {
	        producer.close();
	    }
	 
	}



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

Introduction
............

Storm is a distributed, reliable, fault-tolerant system for processing streams of data.

The work is delegated to different types of components that are each responsible for a
simple specific processing task. 

The input stream of a Storm cluster is handled by a components called Spout and Bolts.

In Storm execution units are Spouts and Bolts that unfold in typologies. Spouts are the data sources, the Bolts are the processors. 

Spouts and connections between and among Bolts Bolts have settings to indicate how the stream is divided between the "threads" of execution. In terminology Storm each "thread" is a separate instance of a parallel Bolt.

Use Cases Storm
............

Processing of Strems 

RPC (Remote Procedure Call) distributed 

Continuous Computation

.. image:: /images/UseCases.png

Concepts
............

Tuples: An ordered list of elements

.. image:: /images/Tuples.png

Stream: Stream of tuples

.. image:: /images/Stream.jpg

Spout: Producer Stream

.. image:: /images/Spout.png

Bolt: Processor and creator of new Streams

.. image:: /images/Bolt.jpg

Topologies: Map Spouts and Bolts

.. image:: /images/Topology.png

Fisic diagram 
............
.. image:: /images/DiagramStorm.png
.. image:: /images/Supervisor.png


Components of a Storm topology
............

Shuffle Grouping:

Sending the bolts tuples is random and uniform. Valid for atomic operations

Local o Shuffle:

If the destination Bolt has one or more tasks in the same work process tuples are preferably sent to these workers

Fiels Grouping:

The stream is divided by the specified fields in the cluster. 
An example would be if the tuples have the user field all tuples with the same usuaio always go to the same task

All Grouping:

Tuples are sent to all tasks of bolts

Direct Grouping:

The producer decides which task tuple consumer receive this tuple

Global Grouping:

Send all tuples intancias to a single destination

Custom Grouping:

Lets implement a custom grouping

Example Topology
............
.. image:: /images/ExampleToplogyStorm.png

Generating Realtime views: Storm Topologies
...........................................

Based `topology: <https://github.com/Produban/openbus/blob/master/openbus-realtime/src/main/java/com/produban/openbus/processor/topology/OpenbusProcessorTopology.java>`_ Kafka -> Storm -> HBase

.. code-block:: java

		stream = topology.newStream("spout", openbusBrokerSpout.getPartitionedTridentSpout());					
		stream = stream.each(new Fields("bytes"), new AvroLogDecoder(), new Fields(fieldsWebLog));		
		stream = stream.each(new Fields(fieldsWebLog), new WebServerLogFilter());
		
		stream.each(new Fields("request", "datetime"), new DatePartition(), new Fields("cq", "cf"))
				.groupBy(new Fields("request", "cq", "cf"))
				.persistentAggregate(stateRequest, new Count(), new Fields("count"))
				.newValuesStream()
				.each(new Fields("request", "cq", "cf", "count"), new LogFilter());
		
		stream.each(new Fields("user", "datetime"), new DatePartition(), new Fields("cq", "cf"))
				.groupBy(new Fields("user", "cq", "cf"))
				.persistentAggregate(stateUser, new Count(), new Fields("count"))
				.newValuesStream()
				.each(new Fields("user", "cq", "cf", "count"), new LogFilter());
		
		stream.each(new Fields("session", "datetime"), new DatePartition(), new Fields("cq", "cf"))
				.groupBy(new Fields("session", "cq", "cf"))
				.persistentAggregate(stateSession, new Count(), new Fields("count"))
				.newValuesStream()
				.each(new Fields("session", "cq", "cf", "count"), new LogFilter());		
		
		return topology.build();	

Serving Layer
-------------

HBase
.....

Hbase is the default NoSql database supplied with Hadoop. These are its main features

	- Distributed
	- Column-oriented
	- High availability
	- High performance
	- Data volumen in order of TeraBytes and PetaBytes
	- Horizontal scalability with the addition of nodes in a cluster
	- Random read/write
	
