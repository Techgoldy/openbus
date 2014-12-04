package com.produban.openbus.trident;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RawLocalFileSystem;
import org.apache.log4j.Logger;

/**
 * HDFS utils     
 */
public class HDFSUtils {
    public static final Logger LOG = Logger.getLogger(HDFSUtils.class);
    public static final String URI_CONFIG = "file://192.168.182.128:8020/";
    
    public static FileSystem getFS(String path, Configuration conf) {
        try {        	
            FileSystem ret = new Path(path).getFileSystem(conf);

            if(ret instanceof LocalFileSystem) {
                LOG.info("Using local filesystem and disabling checksums");
                ret = new RawLocalFileSystem();
                
                try {
                    ((RawLocalFileSystem) ret).initialize(new URI(URI_CONFIG), new Configuration());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
            	LOG.info("No local filesystem " + conf.getStrings("fs.defaultFS"));
            }
            
            return ret;
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }   
}