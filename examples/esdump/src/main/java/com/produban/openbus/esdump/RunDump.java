package com.produban.openbus.esdump;

import org.apache.hadoop.util.ToolRunner;

/**
 * Class Description
 */
public class RunDump {

    public static void main (String[] args) throws Exception {
            ToolRunner.run(new ESDumpDriver(), args);
    }
}

