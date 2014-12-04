package com.produban.openbus.esdump;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.elasticsearch.hadoop.mr.LinkedMapWritable;

import java.io.IOException;

/**
 * Class Description
 */
public class ESDumpMapper extends Mapper<Text, LinkedMapWritable, Text, Text> {

    @Override
    protected void map(Text key, LinkedMapWritable value, Context context) throws IOException, InterruptedException {
        Text docId = key;
        LinkedMapWritable doc = value;

        context.write(docId, new Text(doc.toString()));
    }
}
