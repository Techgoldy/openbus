package com.produban.openbus.trident;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;




/**
 * Store HDFS    
 */
public class HDFSStore {
	//private final static Logger LOG = LoggerFactory.getLogger(HDFSStore.class);
	
    FileSystem fileSystem;	
    Configuration configuration;

    public HDFSStore() {   
    	configuration = new Configuration();     	
	    configuration.set("fs.defaultFS","hdfs://localhost:8020");       //puerto 8020?             
	    configuration.set("hadoop.job.ugi", "cloudera");	    	    
	    System.setProperty("HADOOP_USER_NAME", "cloudera");
	      
	    fileSystem = HDFSUtils.getFS("hdfs://localhost:8020", configuration);
    }

    public HDFSStore(String hdfsDir, String hdfsUser) {   
    	configuration = new Configuration();     	
	    configuration.set("fs.defaultFS", hdfsDir);                    	    
	    System.setProperty("HADOOP_USER_NAME", hdfsUser);
	      
	    fileSystem = HDFSUtils.getFS("hdfs://192.168.182.128:8020", configuration);
    }
        
	public void copyFromLocalFile(String pathLocal, String pathHDFS) throws IOException {
		try {						
			fileSystem.copyFromLocalFile(new Path(pathLocal), new Path("/user/cloudera" + '/'/*File.pathSeparator*/ + pathHDFS));						
		} catch (IOException e) {
			System.err.println("Error writing to hdfs: " + pathHDFS + " " + e);
			throw new RuntimeException(e);
		}
	}     

    public void writeFile2HDFS(String path, String toWrite) throws IOException {
        String tmp ="/user/cloudera/output" + File.separator + path + ".tmp";
        FSDataOutputStream os = fileSystem.create(new Path(tmp), true);
        os.writeUTF(toWrite);
        os.close();
        fileSystem.rename(new Path(tmp), new Path("/user/cloudera/output" + '/'/*File.pathSeparator*/+ path));
    }
}