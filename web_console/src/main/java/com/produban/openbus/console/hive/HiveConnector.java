package com.produban.openbus.console.hive;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class HiveConnector {
    private static Logger LOG = Logger.getLogger(HiveConnector.class);
    private Connection con = null;

    public HiveConnector() {}

    private Connection getConnection() throws Exception {
	try {
	    Properties prop = new Properties();
	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    InputStream resourceStream = loader.getResourceAsStream("META-INF/spring/environment.properties");
	    prop.load(resourceStream);
	    Class.forName(prop.getProperty("hive.driver.name"));
	    con = DriverManager.getConnection(prop.getProperty("hive.driver.url"), prop.getProperty("hive.driver.user"), prop.getProperty("hive.driver.password"));
	    System.setProperty("HADOOP_USER_NAME", prop.getProperty("hive.driver.user"));
	}
	catch (Exception e) {
	    e.printStackTrace();
	    throw e;
	}
	return con;
    }

    public void executeQuery(String query) throws Exception {
	Statement stmt = null;
	try {
	    stmt = getConnection().createStatement();
	    LOG.info("Query " + query + " running....");	    
	    stmt.execute(query);
	    LOG.info("Query done");
	}
	catch (Exception e) {
	    e.printStackTrace();
	    throw e;
	}
	finally {
	    stmt.close();
	    con.close();
	}
    }
}
