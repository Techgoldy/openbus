package com.produban.openbus2.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.produban.openbus2.util.Constant;

public class HiveConnector {
    private static Logger LOG = LoggerFactory.getLogger(HiveConnector.class);
    private Connection con = null;

    public HiveConnector() {
    }

    private Connection getConnection() throws Exception {
	try {
	    Class.forName(Constant.HIVE_DRIVER_NAME);
	    con = DriverManager.getConnection(Constant.HIVE_JDBC_URI, Constant.HIVE_JDBC_USER,
		    Constant.HIVE_JDBC_PASSWORD);
	}
	catch (Exception e) {
	    LOG.error(e.getMessage());
	}
	return con;
    }

    public void executeQuery(String query) throws Exception {
	try {
	    Statement stmt = getConnection().createStatement();
	    stmt.execute(query);
	    LOG.info("Query -- " + query + " -- done");
	}
	finally {
	    con.close();
	}
    }

    public void createTable(String tableName, String location, String fields) throws Exception {
	try {
	    Statement stmt = getConnection().createStatement();
	    stmt.execute(String.format(Constant.HIVE_DROP_TABLE, tableName));
	    LOG.info("Drop table " + tableName + " done");
	    Object[] parameters = { tableName, fields, location };
	    stmt.execute(String.format(Constant.HIVE_CREATE_TABLE, parameters));
	    LOG.info("Create table " + tableName + " done");
	}
	finally {
	    con.close();
	}
    }

    public void executeCreateQuery(String tableName, String query) throws Exception {
	try {
	    Statement stmt = getConnection().createStatement();
	    stmt.execute(String.format(Constant.HIVE_DROP_TABLE, tableName));
	    LOG.info("Drop table " + tableName + " done");
	    stmt.execute(query);
	    LOG.info("Create table " + tableName + " done");
	}
	finally {
	    con.close();
	}
    }

    public void selectAll(String tableName) throws Exception {
	try {
	    Statement stmt = getConnection().createStatement();
	    ResultSet res = stmt.executeQuery(String.format(Constant.HIVE_SELECT_ALL, tableName));
	    String logData = null;
	    while (res.next()) {
		for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
		    switch (res.getMetaData().getColumnType(i)) {
		    case (Types.VARCHAR):
			logData = res.getMetaData().getColumnName(i) + ": " + res.getString(i) + "\t";
			break;
		    case (Types.INTEGER):
			logData = res.getMetaData().getColumnName(i) + ": " + res.getInt(i) + "\t";
			break;
		    case (Types.DATE):
			logData = res.getMetaData().getColumnName(i) + ": " + res.getDate(i) + "\t";
			break;
		    case (Types.TIMESTAMP):
			logData = res.getMetaData().getColumnName(i) + ": " + res.getTimestamp(i) + "\t";
			break;
		    default:
			logData = res.getMetaData().getColumnName(i) + ": " + res.getString(i) + "\t";
		    }
		    LOG.info(logData);
		}
	    }
	}
	finally {
	    con.close();
	}
    }

    public void selectByUser(String tableName, String userCode) throws Exception {
	try {
	    Statement stmt = getConnection().createStatement();
	    Object[] parameters = { tableName, userCode };
	    ResultSet res = stmt.executeQuery(String.format(Constant.HIVE_SELECT_BY_USERCODE, parameters));

	    String logData = null;
	    while (res.next()) {
		for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
		    switch (res.getMetaData().getColumnType(i)) {
		    case (Types.VARCHAR):
			logData = res.getMetaData().getColumnName(i) + ": " + res.getString(i) + "\t";
			break;
		    case (Types.INTEGER):
			logData = res.getMetaData().getColumnName(i) + ": " + res.getInt(i) + "\t";
			break;
		    case (Types.DATE):
			logData = res.getMetaData().getColumnName(i) + ": " + res.getDate(i) + "\t";
			break;
		    case (Types.TIMESTAMP):
			logData = res.getMetaData().getColumnName(i) + ": " + res.getTimestamp(i) + "\t";
			break;
		    default:
			logData = res.getMetaData().getColumnName(i) + ": " + res.getString(i) + "\t";
		    }
		    LOG.info(logData);
		}
	    }
	}
	finally {
	    con.close();
	}
    }
}
