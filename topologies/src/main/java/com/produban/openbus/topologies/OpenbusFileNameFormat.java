package com.produban.openbus.topologies;

import org.apache.storm.hdfs.trident.format.FileNameFormat;
import java.util.Map;

public class OpenbusFileNameFormat implements FileNameFormat {
	 private int partitionIndex;
	    private String path = "/storm";
	    private String prefix = "";
	    private String extension = ".txt";

	    /**
	     * Overrides the default prefix.
	     *
	     * @param prefix
	     * @return
	     */
	    public OpenbusFileNameFormat withPrefix(String prefix){
	        this.prefix = prefix;
	        return this;
	    }

	    /**
	     * Overrides the default file extension.
	     *
	     * @param extension
	     * @return
	     */
	    public OpenbusFileNameFormat withExtension(String extension){
	        this.extension = extension;
	        return this;
	    }

	    public OpenbusFileNameFormat withPath(String path){
	        this.path = path;
	        return this;
	    }

	    @Override
	    public void prepare(Map conf, int partitionIndex, int numPartitions) {
	        this.partitionIndex = partitionIndex;

	    }

	    @Override
	    public String getName(long rotation, long timeStamp) {
	        return this.prefix + "-" + this.partitionIndex +  "-" + rotation + "-" + timeStamp + this.extension;
	    }
	    
	    
	    public String getName(long rotation, long timeStamp,String timeStampDato) {
	        return this.prefix+"-"+timeStampDato+"-" + this.partitionIndex +  "-" + rotation + "-" + timeStamp + this.extension;
	    }
	    
	    public String getPath(){
	        return this.path;
	    }
}
