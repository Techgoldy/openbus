Introduction
============

The objective of Openbus is to define an architecture able to process the massive amount of events that occur in a banking IT Infraestructure.
Those events are of different types, from a variety of sources and with different formats.

Depending on that, we would like to define if the processing to be done should be batch-oriented or near-realtime.

To achieve this flexibility and big capability, we have defined Openbus as a concrete implementation of the so called Lambda Architecture for Big Data systems.

Lambda Architecture defines three main layers for the processing of data streams: Batch layer, Speed layer and Serving layer.

.. image:: /images/lambda_architecture.png

In our case, Openbus is comprised of a set of technologies that interact between them to implement these layers.

Those technologies are the following:

  - Apache Kafka: this is our data stream.
  - HDFS
  - Apache Storm
  - MapReduce
  - Hive
  - HBase

An open source framework for centralized logging and event management (batch and realtime).

Use Cases
---------

Some use cases where openbus could be applied are:

  - Web analytics
  - Social Network Analysis
  - Security Information and Event Management

Installation
------------

To deploy openbus on your environment, you need to first install all dependencies and then the openbus code itself.

Installing dependencies
.......................

  - Install Hadoop
  - [Install Kafka] (https://github.com/Produban/openbus/wiki/Deploying-Kafka-in-RHEL-6.4)
  - [Install Storm] (https://github.com/Produban/openbus/wiki/Install-Storm-cluster)
  - [Install Camus] (https://github.com/Produban/openbus/wiki/Installing-Camus)


Building openbus
................

Clone the project from github::

    #> git clone https://github.com/Produban/openbus.git

Build the project using maven::

    #> cd openbus
    #> mvn compile

Executing examples
------------------

Submitting events to the Kafka broker
.....................................

Running batch ETL processes from Kafka to Hadoop
................................................

Running real time analysis with Storm topologies
................................................

Visualizing data
................

