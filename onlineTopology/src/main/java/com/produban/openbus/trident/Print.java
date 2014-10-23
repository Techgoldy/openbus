package com.produban.openbus.trident;

import storm.trident.operation.BaseFilter;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author Enno Shioji (enno.shioji@peerindex.com)
 */
public class Print extends BaseFilter {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private int partitionIndex;
    private int numPartitions;
    private final String name;
    private String ficheroSalida;
    private BufferedWriter salida;

    public Print() {
	name = "";
    }

    public Print(String name, String ficheroSalida) {
	this.name = name;
	this.ficheroSalida = ficheroSalida;
    }

    @Override
    public void prepare(Map conf, TridentOperationContext context) {
	this.partitionIndex = context.getPartitionIndex();
	this.numPartitions = context.numPartitions();
    }

    @Override
    public boolean isKeep(TridentTuple tuple) {
	System.err.println(String.format("%s::Partition idx: %s out of %s partitions got %s", name, partitionIndex,
		numPartitions, tuple.toString()));
	if (!ficheroSalida.equals(null)) {
	    try {
		salida = new BufferedWriter(new FileWriter(ficheroSalida, true));
		salida.write(tuple.toString().substring(1, tuple.toString().length() - 1) + "\n");
		salida.close();
	    }
	    catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	}
	return true;
    }
}