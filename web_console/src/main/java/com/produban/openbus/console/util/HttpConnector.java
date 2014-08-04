package com.produban.openbus.console.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

public class HttpConnector {

    private static Logger LOG = Logger.getLogger(HttpConnector.class);

    public HttpEntity launchHttp(String url, String action, String json) throws Exception {
	try {
	    HttpClient httpClient = new DefaultHttpClient();

	    HttpPut requestPut = null;
	    HttpPost requestPost = null;
	    HttpDelete requestDelete = null;
	    HttpGet requestGet = null;
	    HttpResponse response = null;
	    StringEntity input = null;
	    if (json != null) {
		input = new StringEntity(json);
		input.setContentType("application/json");
	    }

	    LOG.info("URL = " + url);
	    LOG.info("json = " + json);
	    
	    LOG.info(action + " running....");
	    if (action.equals("PUT")) {
		requestPut = new HttpPut(url);
		requestPut.setHeader("Accept", "application/json");
		requestPut.setHeader("Content-type", "application/json");
		requestPut.setEntity(input);
		response = httpClient.execute(requestPut);
	    }
	    else if (action.equals("POST")) {
		requestPost = new HttpPost(url);
		requestPost.setHeader("Accept", "application/json");
		requestPost.setHeader("Content-type", "application/json");
		requestPost.setEntity(input);
		response = httpClient.execute(requestPost);
	    }
	    else if (action.equals("DELETE")) {
		requestDelete = new HttpDelete(url);
		response = httpClient.execute(requestDelete);
	    }
	    else if (action.equals("GET")) {
		requestGet = new HttpGet(url);
		response = httpClient.execute(requestGet);
	    }
	    else {
		throw new Exception("No existe ninguna accion");
	    }

	    LOG.info(action + " done");
	    if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 400) {
		throw new IOException("Error code = " + response.getStatusLine().getStatusCode());

	    }
	    return response.getEntity();
	}
	catch (Exception e) {
	    LOG.error(e);
	    throw e;
	}
    }
}
