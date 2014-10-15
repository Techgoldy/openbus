package com.produban.openbus2.client;

import java.sql.SQLException;

public class HiveHBaseJdbcClient {
    /*
     * String regExMulti = "([a-z]*)"; String strHiveCreateMulti =
     * "CREATE TABLE " + tableName + " (key string, value string) ROW FORMAT " +
     * "SERDE 'org.apache.hadoop.hive.contrib.serde2.RegexSerDe' WITH SERDEPROPERTIES "
     * + "(\"input.regex\" = \""+ regExMulti +"\") STORED AS TEXTFILE";
     */

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
	try {/*
	      * HiveConnector hiveConnector = new HiveConnector();
	      * 
	      * hiveConnector.createTable(Constant.HIVE_TABLENAME_PROXY,Constant.
	      * HIVE_LOCATION, Constant.HIVE_FIELDS_PROXY_LOG);
	      * hiveConnector.selectByUser
	      * (Constant.HIVE_TABLENAME_PROXY,"n34613");
	      * hiveConnector.executeCreateQuery("METRICA7",
	      * Constant.HIVE_CREATE_TABLE_METRICA7);
	      * hiveConnector.selectAll(Constant.HIVE_TABLENAME_PROXY);
	      * 
	      * HBaseConnector hBaseConnector = new HBaseConnector();
	      * 
	      * hBaseConnector.createTable("Metrica1", Constant.HBASE_FAMILIES);
	      * 
	      * hBaseConnector.addRecord("Metrica1", "1", "create_syntax",
	      * "create_syntax", Constant.HIVE_CREATE_TABLE_METRICA1);
	      * hBaseConnector.addRecord("Metrica2", "2", "create_syntax",
	      * "create_syntax", Constant.HIVE_CREATE_TABLE_METRICA2);
	      * hBaseConnector.addRecord("Metrica3", "3", "create_syntax",
	      * "create_syntax", Constant.HIVE_CREATE_TABLE_METRICA3);
	      * 
	      * hBaseConnector.addRecord("Metrica1", "1", "table_name",
	      * "table_name", "metrica1"); hBaseConnector.addRecord("Metrica2",
	      * "2", "table_name", "table_name", "metrica2");
	      * hBaseConnector.addRecord("Metrica3", "3", "table_name",
	      * "table_name", "metrica3");
	      * 
	      * hBaseConnector.addRecord("Metrica1", "1", "metric_name",
	      * "metric_name", "Accesos por USUARIO y URL");
	      * hBaseConnector.addRecord("Metrica2", "2", "metric_name",
	      * "metric_name", "Accesos por USUARIO");
	      * hBaseConnector.addRecord("Metrica3", "3", "metric_name",
	      * "metric_name", "Accesos por URL");
	      * 
	      * hBaseConnector.addRecord("Metrica1", "1", "info", "info", "");
	      * hBaseConnector.addRecord("Metrica2", "2", "info", "info", "");
	      * hBaseConnector.addRecord("Metrica3", "3", "info", "info", "");
	      * 
	      * hBaseConnector.getAllRecords("Metrica1");
	      */
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
    }
}