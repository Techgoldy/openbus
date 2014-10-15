package com.produban.openbus.topologies;

import kafka.javaapi.producer.Producer;
//import kafka.javaapi.producer.ProducerData;
import kafka.message.Message;
import kafka.serializer.Encoder;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import backtype.storm.task.IMetricsContext;
import storm.trident.operation.TridentCollector;
import storm.trident.state.BaseStateUpdater;
import storm.trident.state.State;
import storm.trident.state.StateFactory;
import storm.trident.tuple.TridentTuple;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.storm.hdfs.trident.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.trident.format.RecordFormat;

public class KafkaState implements State {
    private static final String SEPARADOR = "\001";
    protected String kafkaTopic;
    protected String zookeeperHosts;
    protected Producer<String, String> producer;
    protected String brokerList;
    protected String origen;
    protected RecordFormat format;

    public static class Factory implements StateFactory {
	protected String kafkaTopic;
	protected String zookeeperHosts;
	protected String brokerList;
	protected String origen;
	protected RecordFormat format;

	public Factory(String kafkaTopic, String zookeeperHosts, String brokerList, RecordFormat format, String origen) {
	    this.kafkaTopic = kafkaTopic;
	    this.zookeeperHosts = zookeeperHosts;
	    this.brokerList = brokerList;
	    this.format = format;
	    this.origen = origen;
	}

	public State makeState(Map conf, int partitionIndex, int numPartitions) {
	    return makeState(conf, null, partitionIndex, numPartitions);
	}

	public State makeState(Map conf, IMetricsContext context, int partitionIndex, int numPartitions) {
	    return new KafkaState(kafkaTopic, zookeeperHosts, brokerList, format, origen);
	}
    }

    public static class Updater extends BaseStateUpdater<KafkaState> {
	@Override
	public void updateState(KafkaState state, List<TridentTuple> tuples, TridentCollector collector) {
	    state.setBulk(tuples);
	}
    }

    public KafkaState(String kafkaTopic, String zookeeperHosts, String brokerList, RecordFormat format, String origen) {
	this.kafkaTopic = kafkaTopic;
	this.zookeeperHosts = zookeeperHosts;
	this.brokerList = brokerList;
	this.format = format;
	this.origen = origen;
    }

    @Override
    public void beginCommit(Long txid) {

    }

    @Override
    public void commit(Long txid) {

    }

    public void setBulk(List<TridentTuple> tuples) {
	Properties props = new Properties();
	props.put("metadata.broker.list", brokerList);
	props.put("zk.connect", zookeeperHosts);
	// props.put("serializer.class",
	// "com.produban.openbus.topologies.KafkaState$UTF8Encoder");
	props.put("serializer.class", "kafka.serializer.StringEncoder");
	System.out.println("zk.connect: " + props.get("zk.connect"));

	ProducerConfig config = new ProducerConfig(props);
	producer = new Producer<String, String>(config);

	KeyedMessage<String, String> producerData;
	List<KeyedMessage<String, String>> batchData = new ArrayList<KeyedMessage<String, String>>();

	for (TridentTuple tuple : tuples) {

	    producerData = new KeyedMessage<String, String>(kafkaTopic, origen + SEPARADOR + new String(format.format(tuple)));
	    batchData.add(producerData);
	}
	producer.send(batchData);
    }

    // ----------------------------------------------------------------------------

    public static class UTF8Encoder implements Encoder<String> {
	public Message toMessage(String event) {
	    return new Message(event.getBytes(Charset.forName("UTF-8")));
	}

	@Override
	public byte[] toBytes(String arg0) {
	    // TODO Auto-generated method stub
	    return arg0.getBytes();
	}
    };
}
