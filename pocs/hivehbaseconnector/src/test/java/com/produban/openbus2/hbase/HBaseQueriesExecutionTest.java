package com.produban.openbus2.hbase;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.produban.openbus2.util.Constant;

public class HBaseQueriesExecutionTest {

    String tableName = "Metrica1";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createTableTest() {
	HBaseConnector hBaseConnector = new HBaseConnector();
	try {
	    hBaseConnector.createTable(this.tableName, Constant.HBASE_FAMILIES);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }

    @Test
    public void tableExistsTest() {
	HBaseConnector hBaseConnector = new HBaseConnector();
	try {
	    hBaseConnector.tableExists(this.tableName);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }

    @Test
    public void listTablesTest() {
	HBaseConnector hBaseConnector = new HBaseConnector();
	try {
	    hBaseConnector.listTables();
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }

    @Test
    public void addRecordTest() {
	HBaseConnector hBaseConnector = new HBaseConnector();
	try {
	    hBaseConnector.addRecord(this.tableName, "1", "create_syntax", "create_syntax",
		    Constant.HIVE_CREATE_TABLE_METRICA1);
	    hBaseConnector.addRecord(this.tableName, "1", "table_name", "table_name", "metrica1");
	    hBaseConnector.addRecord(this.tableName, "1", "metric_name", "metric_name", "Accesos por USUARIO y URL");
	    hBaseConnector.addRecord(this.tableName, "1", "info", "info", "");
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }

    @Test
    public void getOneRecordTest() {
	HBaseConnector hBaseConnector = new HBaseConnector();
	try {
	    hBaseConnector.getOneRecord(this.tableName, "1");
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }

    @Test
    public void deleteRecordTest() {
	HBaseConnector hBaseConnector = new HBaseConnector();
	try {
	    hBaseConnector.deleteRecord(this.tableName, "1");
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }

    @Test
    public void getAllRecords() {
	HBaseConnector hBaseConnector = new HBaseConnector();
	try {
	    hBaseConnector.getAllRecords(this.tableName);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }

    @Test
    public void deleteTableTest() {
	HBaseConnector hBaseConnector = new HBaseConnector();
	try {
	    hBaseConnector.deleteTable(this.tableName);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }
}
