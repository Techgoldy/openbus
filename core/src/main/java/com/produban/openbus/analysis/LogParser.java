package com.produban.openbus.analysis;

import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for raw log parsers.
 */
public interface LogParser extends Function {

    /**
     *
     * @param logLine String log line
     * @return HashMap<String, String> containing Name, Value of parsed fields
     */
    public HashMap<String,String> parse(String logLine);

    public List<String> fieldNames();

    @Override
    void execute(TridentTuple objects, TridentCollector tridentCollector);

    @Override
    void prepare(Map map, TridentOperationContext tridentOperationContext);

    @Override
    void cleanup();
}
