package com.produban.openbus.akamai;

import backtype.storm.tuple.Values;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class Description
 */
public class AkamaiJsonDecoder extends BaseFunction {

    private static final Logger logger = LoggerFactory.getLogger(AkamaiJsonDecoder.class);

    @Override
    public void execute(TridentTuple objects, TridentCollector tridentCollector) {

        byte[] jsonBytes = objects.getBinary(0);
        String jsonString = new String(jsonBytes);

        if (jsonBytes.length == 0) {
            logger.warn("EMPTY json bytes received!");
            return;
        }

        logger.info("DECODING JSON:");
        logger.info(jsonString);

        JSONObject jsonObj = new JSONObject(jsonString.trim());

        String id = jsonObj.getString("id");
        String proto = jsonObj.getJSONObject("message").getString("proto");
        String protoVer = jsonObj.getJSONObject("message").getString("protoVer");
        String status = jsonObj.getJSONObject("message").getString("status");
        String cliIp = jsonObj.getJSONObject("message").getString("cliIp");
        String reqMethod = jsonObj.getJSONObject("message").getString("reqMethod");
        String reqPath = jsonObj.getJSONObject("message").getString("reqPath");
        String respLen = jsonObj.getJSONObject("message").getString("respLen");
        String UA = jsonObj.getJSONObject("message").getString("UA");
        String referer = jsonObj.getJSONObject("reqHdr").getString("referer");

        String rawDate = jsonObj.getJSONObject("reqHdr").getString("date");
        String akamaiDateFormat = "EEE, dd MMM yyyy HH:mm:ss Z";
        Date date = null;
        try {
            date = new SimpleDateFormat(akamaiDateFormat).parse(rawDate);
        }
        catch (ParseException e) {
            logger.error("Error parsing akamai date: "+rawDate);
            logger.error(e.toString());
        }

        Values tridentValues = new Values();
        tridentValues.add(id);
        tridentValues.add(rawDate);
        tridentValues.add(date);
        tridentValues.add(proto);
        tridentValues.add(protoVer);
        tridentValues.add(status);
        tridentValues.add(cliIp);
        tridentValues.add(reqMethod);
        tridentValues.add(reqPath);
        tridentValues.add(respLen);
        tridentValues.add(UA);
        tridentValues.add(referer);

        tridentCollector.emit(tridentValues);
    }
}
