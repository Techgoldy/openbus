package com.produban.openbus.analysis;

import java.util.HashMap;
import java.util.List;

/**
 * Interface for raw log parsers.
 */
public interface RawLogParser {

    /**
     *
     * @param logLine String log line
     * @return HashMap<String, String> containing Name, Value of parsed fields
     */
    public HashMap<String,String> parseLogLine(String logLine);

    public List<String> fieldNames();
}
