package com.produban.openbus.storm;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SiddhiBolt extends BaseBasicBolt {
    private static final long serialVersionUID = 1L;
    private static Logger LOG = Logger.getLogger(SiddhiBolt.class);

    private transient SiddhiManager siddhiManager;
    private BasicOutputCollector collector;
    private boolean useDefaultAsStreamName = true;
    private String componentID;
    
    private final static String SEPARADOR = "\001";

    @Override
    public Map<String, Object> getComponentConfiguration() {
	Config conf = new Config();
	int tickFrequencyInSeconds = 10;
	conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, tickFrequencyInSeconds);
	return conf;
    }

    public void init() {
	SiddhiConfiguration configuration = new SiddhiConfiguration();
	this.siddhiManager = new SiddhiManager(configuration);
    }

    public SiddhiBolt() {
	init();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collectorAux) {
	try {
	    this.collector = collectorAux;
	    if (siddhiManager == null) {
	    	init();
	    }
	    String queryID = null;
	    InputHandler inputHandler = null;
	    JSONObject jsonObject = getJson();
	    String operation = (String) jsonObject.get("operation");
	    LOG.info(operation);
	    String define = (String) jsonObject.get("define");
	    LOG.info(define);
	    final String streamID = define.split(" ")[2];

	    if (isTickTuple(tuple)) {
		if (streamID != null) {
		    if (operation.equals("insert")) {
			if (siddhiManager.getStreamDefinition(streamID) == null) {
			    siddhiManager.defineStream(define);
			}
		    }
		}
		else {
		    if (operation.equals("delete")) {
			siddhiManager.removeStream(streamID);
		    }
		}

		String query = (String) jsonObject.get("query");
		LOG.info(query);

		if (operation.equals("delete")) {
		    siddhiManager.removeQuery(query);
		}
		else if (operation.equals("insert")) {
		    if (query != null) {
			siddhiManager.removeQuery(query);
		    }
		    queryID = siddhiManager.addQuery(query);
		}

		siddhiManager.addCallback(queryID, new QueryCallback() {
		    @Override
		    public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
			if (inEvents != null) {
			    for (Event e : inEvents) {
				collector.emit(new Values(e.getStreamId(), e.getData()));
			    }
			}
			if (removeEvents != null) {
			    for (Event e : removeEvents) {
				collector.emit(new Values(e.getStreamId(), e.getData()));
			    }
			}
		    }
		});
	    }
	    else {
		inputHandler = siddhiManager.getInputHandler(streamID);

		if (inputHandler != null) {
		    inputHandler.send(tuple.toString().split(this.SEPARADOR));
		}
		else {
		    throw new RuntimeException("Input handler for stream " + streamID + " not found");
		}
	    }
	}
	catch (InterruptedException e) {
	    LOG.error(e);
	}
	catch (Exception e) {
	    LOG.error(e);
	}

    }

    private static boolean isTickTuple(Tuple tuple) {
	return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)
		&& tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }

    private InputStreamReader callRestClient() throws ClientProtocolException, IOException {
	HttpClient client = new DefaultHttpClient();
	HttpGet request = new HttpGet("http://localhost");
	HttpResponse response = client.execute(request);
	return new InputStreamReader(response.getEntity().getContent());
    }

    private JSONObject getJson() throws Exception {
	JSONParser parser = new JSONParser();
	Object obj = parser.parse(callRestClient());
	JSONObject jsonObject = (JSONObject) obj;
	return jsonObject;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
	declarer.declare(new Fields("streamID", "datos"));
    }

    public BasicOutputCollector getCollector() {
	return collector;
    }

    public void setUseDefaultAsStreamName(boolean useDefaultAsStreamName) {
	this.useDefaultAsStreamName = useDefaultAsStreamName;
    }

    public boolean isUseDefaultAsStreamName() {
	return useDefaultAsStreamName;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
	super.prepare(stormConf, context);
	this.componentID = context.getThisComponentId();
    }
}
