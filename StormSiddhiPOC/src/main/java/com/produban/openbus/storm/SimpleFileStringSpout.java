package com.produban.openbus.storm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

/**
 * Spout File
 */
public class SimpleFileStringSpout extends BaseRichSpout {
    private static final long serialVersionUID = 4438467494562792399L;
    private static Logger LOG = Logger.getLogger(SimpleFileStringSpout.class);
    private final String emittedTupleName;

    private static BufferedReader br;
    private SpoutOutputCollector collector;
    private boolean cycle = false;
    private String fileName;

    private Calendar inicio=null;
    
    public void setCycle(boolean cycle) {
	this.cycle = cycle;
    }

    public SimpleFileStringSpout(String sourceFileName, String emittedTupleName) {
	super();
	
	this.emittedTupleName = emittedTupleName;
	try {
	    br = new BufferedReader(new FileReader(new File(sourceFileName)));
	    fileName = sourceFileName;
	}
	catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @SuppressWarnings("rawtypes")
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
	this.collector = collector;
    }

    public void close() {
	if (br != null) {
	    try {
		br.close();
		br = null;
	    }
	    catch (IOException e) {
		LOG.error(e);
	    }
	}
    }

    public void nextTuple() {
	try {
	    String rawEvent = br.readLine();
	    if (rawEvent != null) {
		try {
		    String messageId = UUID.randomUUID().toString();
		    if(inicio==null) inicio=Calendar.getInstance();
		    LOG.info("inicio:"+inicio.get(Calendar.MINUTE) +":"+inicio.get(Calendar.SECOND)+"-FIN:"+ Calendar.getInstance().get(Calendar.MINUTE)+":"+ Calendar.getInstance().get(Calendar.SECOND));
		    //Thread.sleep(1000);
		    collector.emit(new Values(rawEvent), messageId);
		}
		catch (Exception e) {
		    LOG.error(e);
		}
	    }
	    else if (cycle) {
		br.close();
		br = new BufferedReader(new FileReader(new File(fileName)));
	    }
	    else {
		Utils.sleep(1);
	    }

	}
	catch (IOException e) {
	    throw new RuntimeException(e);
	}

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
	declarer.declare(new Fields(emittedTupleName));
    }

    @Override
    public void ack(Object msgId) {    }

    @Override
    public void fail(Object msgId) {
	LOG.error(msgId);
    }
}