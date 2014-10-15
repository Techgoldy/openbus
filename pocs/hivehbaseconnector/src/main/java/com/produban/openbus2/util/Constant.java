package com.produban.openbus2.util;

public interface Constant {

    // Hive Configuration
    public static final String HIVE_DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";
    public static final String HIVE_JDBC_URI = "jdbc:hive2://localhost:10000/default";
    public static final String HIVE_JDBC_USER = "hive";
    public static final String HIVE_JDBC_PASSWORD = "";

    // Hive Queries
    public static final String HIVE_SELECT_ALL = "SELECT * FROM %s";
    public static final String HIVE_SELECT_BY_USERCODE = "SELECT * FROM %s WHERE USERCODE = '%s'";
    public static final String HIVE_DROP_TABLE = "DROP TABLE IF EXISTS %s";
    public static final String HIVE_CREATE_TABLE = "CREATE EXTERNAL TABLE %s (%s) ROW FORMAT DELIMITED FIELDS TERMINATED BY '¬' LINES TERMINATED BY '\n' LOCATION '%s'";
    public static final String HIVE_CREATE_TABLE_METRICA1 = "CREATE TABLE metrica1 AS SELECT usercode,to_date(eventTimeStamp) as DIA,count(1) "
	    + "as CUENTA FROM logs_proxy GROUP BY usercode, to_date(eventTimeStamp)";
    public static final String HIVE_CREATE_TABLE_METRICA2 = "CREATE TABLE metrica2 AS SELECT requestDomain,usercode,to_date(eventTimeStamp) as DIA,count(1) "
	    + "as CUENTA FROM logs_proxy GROUP BY requestDomain,usercode,to_date(eventTimeStamp)";
    public static final String HIVE_CREATE_TABLE_METRICA3 = "CREATE TABLE metrica3 AS SELECT clientIP,usercode,to_date(eventTimeStamp) as DIA,count(1) "
	    + "as CUENTA FROM logs_proxy GROUP BY clientIP,usercode,to_date(eventTimeStamp)";
    public static final String HIVE_CREATE_TABLE_METRICA4 = "CREATE TABLE metrica4 AS SELECT requestDomain,to_date(eventTimeStamp) as DIA, count(1) "
	    + "as CUENTA,sum(csBytes)/1024 as TAMANO FROM logs_proxy WHERE requestURIExtension not in('html','htm','-') "
	    + "GROUP BY requestDomain,to_date(eventTimeStamp)";
    public static final String HIVE_CREATE_TABLE_METRICA5 = "CREATE TABLE metrica5 AS SELECT usercode,requestDomain,to_date(eventTimeStamp) as DIA,count(1) "
	    + "as CUENTA FROM logs_proxy WHERE requestURIExtension in('exe','pl','js') or contentType in "
	    + "('application/x-msdos-program', 'application/x-javascript','application/javascript') "
	    + "GROUP BY usercode,requestDomain,to_date(eventTimeStamp)";
    public static final String HIVE_CREATE_TABLE_METRICA6 = "CREATE TABLE metrica6 AS SELECT usercode,requestDomain,to_date(eventTimeStamp) as DIA,count(1) "
	    + "as CUENTA FROM logs_proxy WHERE requestURIExtension in('rar','zip','tar','gz','Z','tgz','gzip','ace')"
	    + "GROUP BY usercode,requestDomain,to_date(eventTimeStamp)";
    public static final String HIVE_CREATE_TABLE_METRICA7 = "CREATE TABLE metrica7 AS SELECT usercode,requestDomain,to_date(eventTimeStamp) as DIA,count(1) "
	    + "as CUENTA FROM logs_proxy WHERE requestURIExtension in('flv','mpeg','avi','f4m','bootstrap','mp4') or "
	    + "contentType in ('video/mp4','video/f4f','video/x-flv','video/f4m','video/abst','video/webm','video/x-ms-asf') "
	    + "GROUP BY usercode,requestDomain,to_date(eventTimeStamp)";

    // Hive Parameters
    public static final String HIVE_LOCATION = "/user/hive/warehouse/logs/";
    public static final String HIVE_TABLENAME_PROXY = "logs_proxy";
    public static final String HIVE_FIELDS_PROXY_LOG = "eventTimeStamp Timestamp,timeTaken int,clientIP String,userCode String,"
	    + "userGroup String,Exception String,filterResult String,category String,referer String,responseCode int,"
	    + "action String,method String,contentType String, protocol String,requestDomain String,requestPort int ,"
	    + "requestPath String,requestQuery String,requestURIExtension String,userAgent String,serverIP String,"
	    + "scBytes int,csBytes int,virusID String,destinationIP String";
    // HBase Configuration
    public static final String HBASE_MASTER = "localhost:60000";
    public static final String HBASE_ZOOKEEPER_PORT = "2181";
    public static final String HBASE_ZOOKEEPER_QUORUM = "localhost";
    public static final String HBASE_FAMILIES = "create_syntax,table_name,metric_name,info";

}
