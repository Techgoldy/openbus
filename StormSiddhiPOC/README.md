#Openbus

Big data applications for ingestion and analysis of massive amounts of events generated by a banking IT Infraestructure.

The objective of this POC is to test Storm integration with Siddhi CEP.

#Dependences

Deploying this POC in your environment involves the following dependences:

- Hadoop 2.2.0 or higher version
- Storm 0.9.1 or higher version
- Siddhi pluggins

```xml
<dependency>
	<groupId>org.wso2.siddhi</groupId>
	<artifactId>siddhi-api</artifactId>
	<version>2.0.0-wso2v1</version>
</dependency>
<dependency>
	<groupId>org.wso2.siddhi</groupId>
	<artifactId>siddhi-core</artifactId>
	<version>2.0.0-wso2v1</version>
</dependency>
<dependency>
	<groupId>org.wso2.siddhi</groupId>
	<artifactId>siddhi-query</artifactId>
	<version>2.0.0-wso2v1</version>
</dependency>
```

#Running examples

In this POC there are 2 main classes:

1. SiddhiTopology. This POC will read de CEP rules from a JSON located in a HTTP Server in localhost. Rules will be sent to Siddhi CEP every certain amount of time. Tick tuples will be used for this purpose: 

```java
@Override
public Map<String, Object> getComponentConfiguration() {
	Config conf = new Config();
	int tickFrequencyInSeconds = 10;
	conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, tickFrequencyInSeconds);
	return conf;
}
```


2. SiddhiTopologyAux. This POC will have all the CEP rules inserted from the beginning.


