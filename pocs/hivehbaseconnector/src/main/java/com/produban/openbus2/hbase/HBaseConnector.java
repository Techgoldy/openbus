package com.produban.openbus2.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.produban.openbus2.util.Constant;

public class HBaseConnector {
    private static Logger LOG = LoggerFactory.getLogger(HBaseConnector.class);
    private Configuration conf = null;

    public HBaseConnector() {
	conf = HBaseConfiguration.create();
	conf.set("hbase.zookeeper.property.clientPort", Constant.HBASE_ZOOKEEPER_PORT);
	conf.set("hbase.zookeeper.quorum", Constant.HBASE_ZOOKEEPER_QUORUM);
	conf.set("hbase.master", Constant.HBASE_MASTER);
    }

    /**
     * Create a table
     */
    public void createTable(String tableName, String strFamilies) throws Exception {
	HBaseAdmin admin = new HBaseAdmin(conf);
	try {
	    String arrFamilies[] = strFamilies.split(",");
	    if (admin.tableExists(tableName)) {
		LOG.info("Table " + tableName + " already exists");
	    }
	    else {
		HTableDescriptor tableDesc = new HTableDescriptor(tableName);
		for (int i = 0; i < arrFamilies.length; i++) {
		    tableDesc.addFamily(new HColumnDescriptor(arrFamilies[i]));
		}
		admin.createTable(tableDesc);
		LOG.info("Table " + tableName + " created");
	    }
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	finally {
	    admin.close();
	}
    }

    /**
     * Delete a table
     */
    public void deleteTable(String tableName) throws Exception {
	HBaseAdmin admin = new HBaseAdmin(conf);
	try {
	    admin.disableTable(tableName);
	    admin.deleteTable(tableName);
	    LOG.info("Table " + tableName + " deleted");
	}
	catch (MasterNotRunningException e) {
	    e.printStackTrace();
	}
	catch (ZooKeeperConnectionException e) {
	    e.printStackTrace();
	}
	finally {
	    admin.close();
	}
    }

    /**
     * Put (or insert) a row
     */
    public void addRecord(String tableName, String rowKey, String family, String qualifier, String value)
	    throws Exception {
	HTable table = new HTable(conf, tableName);
	try {
	    Put put = new Put(Bytes.toBytes(rowKey));
	    put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
	    table.put(put);
	    LOG.info("Record " + rowKey + " added to table " + tableName);
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
	finally {
	    table.close();
	}
    }

    /**
     * Delete a row
     */
    public void deleteRecord(String tableName, String rowKey) throws Exception {
	HTable table = new HTable(conf, tableName);
	try {
	    List<Delete> list = new ArrayList<Delete>();
	    Delete del = new Delete(rowKey.getBytes());
	    list.add(del);
	    table.delete(list);
	    LOG.info("Record " + rowKey + " deleted");
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
	finally {
	    table.close();
	}
    }

    /**
     * Get a row
     */
    public void getOneRecord(String tableName, String rowKey) throws Exception {
	HTable table = new HTable(conf, tableName);
	try {
	    Get get = new Get(rowKey.getBytes());
	    Result rs = table.get(get);
	    for (KeyValue kv : rs.raw()) {
		LOG.info(new String(kv.getRow()) + " ");
		LOG.info(new String(kv.getFamily()) + " ");
		LOG.info(new String(kv.getQualifier()) + " ");
		LOG.info(kv.getTimestamp() + " ");
		LOG.info(new String(kv.getValue()) + " ");
		LOG.info(new String(kv.getRow()) + " ");
	    }
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
	finally {
	    table.close();
	}
    }

    /**
     * Scan (or list) a table
     */
    public void getAllRecords(String tableName) throws Exception {
	HTable table = new HTable(conf, tableName);
	Scan s = new Scan();
	ResultScanner ss = table.getScanner(s);
	try {
	    for (Result r : ss) {
		for (KeyValue kv : r.raw()) {
		    LOG.info(new String(kv.getRow()) + " ");
		    LOG.info(new String(kv.getFamily()) + " ");
		    LOG.info(new String(kv.getQualifier()) + " ");
		    LOG.info(kv.getTimestamp() + " ");
		    LOG.info(new String(kv.getValue()) + " ");
		    LOG.info(new String(kv.getRow()) + " ");
		}
	    }
	}
	finally {
	    ss.close();
	    table.close();
	}
    }

    /**
     * Check if table exists
     */
    public void tableExists(String tableName) throws Exception {
	Configuration conf = HBaseConfiguration.create();
	HBaseAdmin admin = new HBaseAdmin(conf);
	try {
	    boolean result = admin.tableExists(tableName);
	    if (result) {
		LOG.info("Table " + tableName + " exists");
	    }
	    else {
		LOG.info("Table " + tableName + " not exists");
	    }
	}
	finally {
	    admin.close();
	}
    }

    /**
     * List tables
     */
    public void listTables() throws Exception {
	Configuration conf = HBaseConfiguration.create();
	HBaseAdmin admin = new HBaseAdmin(conf);
	try {
	    HTableDescriptor[] arrDescriptor = admin.listTables();
	    for (HTableDescriptor descriptor : arrDescriptor) {
		LOG.info(descriptor.getNameAsString());
		LOG.info("Families: ", descriptor.getColumnFamilies());
	    }
	}
	finally {
	    admin.close();
	}
    }
}
