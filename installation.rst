Installation
------------

Deploying the Openbus architecture in your environment involves the following steps:

  - Install dependencies
  - Build Openbus code
  - Run examples

We have tested Openbus in a Red Hat Enterprise Linux 6.4 environment

Installing dependencies
.......................

  - Install Hadoop
  - `Install Kafka <https://github.com/Produban/openbus/wiki/Deploying-Kafka-in-RHEL-6.4>`_
  - `Install Storm <https://github.com/Produban/openbus/wiki/Install-Storm-cluster>`_
  - `Install Camus <https://github.com/Produban/openbus/wiki/Installing-Camus>`_

Building openbus
................

Clone the project from github::

    #> git clone https://github.com/Produban/openbus.git

Build the project using maven::

    #> cd openbus
    #> mvn compile

Running examples
------------------

Submitting events to the Kafka broker
.....................................

Launch javaAvroKafka sample::

    #> cd $javaAvroKafkaHome
    #> java -jar avroKafka-1.0-SNAPSHOT-shaded.jar wslog 50 2 3 3 -90

Arguments are topic, number of request, number of users, number of user sessions, number of session requests, date simulation offset (0 for today)


Running batch ETL processes from Kafka to Hadoop
................................................

Running real time analysis with Storm topologies
................................................

Visualizing data
................
