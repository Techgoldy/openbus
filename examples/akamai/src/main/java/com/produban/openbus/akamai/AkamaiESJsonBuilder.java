package com.produban.openbus.akamai;

import backtype.storm.tuple.Values;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * This Trident function takes a tuple as input and generates
 * three fields needed for indexing data in ElasticSearch:
 */
public class AkamaiESJsonBuilder extends BaseFunction {

    private static final Logger logger = LoggerFactory.getLogger(AkamaiESJsonBuilder.class);
    private List<String> fieldNames;

    public AkamaiESJsonBuilder(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {

        String id = tridentTuple.getString(0);
        XContentBuilder builder = null;

        try {

            builder = buildJson(tridentTuple);

        } catch (IOException ex) {
            logger.error(ex.toString());
            logger.error("Error creating JSON object for indexing into elasticsearch");
        }

        Values tridentValues = new Values();
        tridentValues.add(0, builder);
        tridentValues.add(1, id);

        tridentCollector.emit(tridentValues);
    }

    private XContentBuilder buildJson(TridentTuple tridentTuple) throws IOException {
        XContentBuilder builder = jsonBuilder()
                .startObject();

        for (int i = 0; i < fieldNames.size(); i ++) {
            builder.field(fieldNames.get(i), tridentTuple.get(i));
        }

        builder.endObject();

        return builder;
    }
}
