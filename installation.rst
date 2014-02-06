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

Arguments are kafka topic, number of requests, number of users, number of user sessions, number of session requests, date simulation offset (0 for today).


Running batch ETL processes from Kafka to Hadoop
................................................

Launch Camus ETL::

    #> cd $camusHome
    #> hadoop jar camus-example-0.1.0-SNAPSHOT-shaded.jar com.linkedin.camus.etl.kafka.CamusJob -P <camus.properties>

where <camus.properties> is a file path pointing to camus configuration as described in https://github.com/Produban/camus under configuration section.


Running real time analysis with Storm topologies
................................................

Launch Openbus Topology::

    #> cd $openBusRealTimeHome
    #> storm jar target/openbus-realtime-0.0.1-shaded.jar com.produban.openbus.processor.topology.OpenbusProcessorTopology openbus -zookepperHost vmlbcnimbusl01:2181 -topic wslog -staticHost vmlbcbrokerl01,vmlbcbrokerl02

Arguments are topology, kafka topic, zookeeper host and kafka broker list.


Visualizing data
................

View Hits per Day/Month/Week::

    #> cd $openBusRealTimeHome/hbase/queryscripts
    #> ./hitsPer<Period>.sh <date> [requestId]

where <Period> can be Day, Month or Week.
First arguments is the date in format "yyyyMMdd" for a day of a year, "yyyyMM" for a month of a year and "yyyyWW" for a week of a year.
Second argument is optional for filtering with an specific request.


View Users per Day/Month/Week::

    #> cd $openBusRealTimeHome/hbase/queryscripts
    #> ./usersPer<Period>.sh <date> [userId]

where <Period> can be Day, Month or Week.
First arguments is the date in format "yyyyMMdd" for a day of a year, "yyyyMM" for a month of a year and "yyyyWW" for a week of a year.
Second argument is optional for filtering with an specific user.


View Sessions per Day/Month/Week::

    #> cd $openBusRealTimeHome/hbase/queryscripts
    #> ./sessionsPer<Period>.sh <date> [userId]

where <Period> can be Day, Month or Week.
First arguments is the date in format "yyyyMMdd" for a day of a year, "yyyyMM" for a month of a year and "yyyyWW" for a week of a year.
Second argument is optional for filtering with an specific session.

