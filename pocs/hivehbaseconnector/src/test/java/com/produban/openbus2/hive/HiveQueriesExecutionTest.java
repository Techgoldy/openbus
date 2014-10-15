package com.produban.openbus2.hive;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HiveQueriesExecutionTest {

    String tableName = "logs";
    String tableNameAux = "metrica1";
    String location = "/user/hive/warehouse/logs/";
    String fields = "eventTimeStamp Timestamp,timeTaken int,clientIP String,userCode String,"
	    + "userGroup String,Exception String,filterResult String,category String,referer String,responseCode int,"
	    + "action String,method String,contentType String, protocol String,requestDomain String,requestPort int ,"
	    + "requestPath String,requestQuery String,requestURIExtension String,userAgent String,serverIP String,"
	    + "scBytes int,csBytes int,virusID String,destinationIP String";
    String userCode = "n34613";
    String query = "CREATE EXTERNAL TABLE tabla1 (eventTimeStamp Timestamp, username String, number int) ROW FORMAT DELIMITED FIELDS TERMINATED BY '¬' LINES TERMINATED BY '\n' LOCATION '/user/hive/warehouse/metricas/'";
    String query2 = "CREATE TABLE metrica1 AS SELECT usercode,to_date(eventTimeStamp) as DIA,count(1) as CUENTA FROM logs GROUP BY usercode, to_date(eventTimeStamp)";

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void executeQueryTest() {
	try {
	    HiveConnector hiveConnector = new HiveConnector();
	    hiveConnector.executeQuery(this.query);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }

    @Test
    public void createTableTest() {
	try {
	    HiveConnector hiveConnector = new HiveConnector();
	    hiveConnector.createTable(this.tableName, this.location, this.fields);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }

    @Test
    public void executeCreateQueryTest() {
	try {
	    HiveConnector hiveConnector = new HiveConnector();
	    hiveConnector.executeCreateQuery(this.tableNameAux, this.query2);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }

    @Test
    public void selectAllTest() {
	try {
	    HiveConnector hiveConnector = new HiveConnector();
	    hiveConnector.selectAll(this.tableName);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }

    @Test
    public void selectByUserTest() {
	try {
	    HiveConnector hiveConnector = new HiveConnector();
	    hiveConnector.selectByUser(this.tableName, this.userCode);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    assertTrue(false);
	}
	assertTrue(true);
    }

}
