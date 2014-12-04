package com.produban.openbus.esdump;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.elasticsearch.hadoop.mr.EsInputFormat;
import org.elasticsearch.hadoop.mr.LinkedMapWritable;

/**
 * A MapReduce job to dump an ElasticSearch Index into HDFS files
 */
public class ESDumpDriver extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.printf("Usage: %s [generic options] <ES nodes> <ES resource> <HDFS output path>\n" +
                              "Example: %s EShost1:9200,EShost2:9200 twitter/tweet /tmp/esdump/\n\n",
                              getClass().getSimpleName(), getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err); return -1;
        }
        String esNodes = args[0];
        String esResource = args[1];
        String outputhHdfsPath = args[2];

        Configuration conf = new Configuration();
        conf.set("es.nodes", esNodes);
        conf.set("es.resource", esResource);

        Job job = new Job(conf);
        job.setNumReduceTasks(0);
        job.setJarByClass(ESDumpDriver.class);
        job.setInputFormatClass(EsInputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LinkedMapWritable.class);

        job.setMapperClass(ESDumpMapper.class);


        FileOutputFormat.setOutputPath(job, new Path(outputhHdfsPath));

        return job.waitForCompletion(true) ? 0 : 1;

    }

}
