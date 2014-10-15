package pigconnector;

import java.io.IOException;
import java.util.Properties;

import org.apache.pig.ExecType;
import org.apache.pig.PigServer;
import org.apache.pig.backend.executionengine.ExecException;

public class PigTest {
    static final String HADOOP_USER_NAME = "hdfs";

    public static void main(String[] args) {
	PigServer pigServer;
	try {
	    Properties props = new Properties();
	    props.setProperty("fs.default.name", "hdfs://localhost.localdomain:8020");
	    props.setProperty("mapred.job.tracker", "localhost.localdomain:8021");
	    System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
	    System.setProperty("HADOOP_USER_NAME", HADOOP_USER_NAME);
	    pigServer = new PigServer(ExecType.MAPREDUCE, props);

	    pigServer.registerScript("pig-wordcount/wordcount.pig");
	}
	catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}