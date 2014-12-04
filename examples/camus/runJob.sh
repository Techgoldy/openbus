#!/bin/bash
#
# ~/camus/
#		camus.properties
#		log4j.xml
#		openbus.camus-0.0.1-SNAPSHOT-shaded.jar
#		runJob.sh
#		camus/schemas:
#			openbus-model-0.0.1-SNAPSHOT.jar
#

export HADOOP_CLASSPATH=$(pwd)/schemas/openbus-model-0.0.1-SNAPSHOT.jar
echo HADOOP_CLASSPATH = $HADOOP_CLASSPATH

hadoop jar openbus.camus-0.0.1-SNAPSHOT-shaded.jar com.linkedin.camus.etl.kafka.CamusJob -P camus.properties

