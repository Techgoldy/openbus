package com.produban.openbus.console.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ConsoleInterceptor extends HandlerInterceptorAdapter{

    final static String REMOVE_SESSION_URL_1 = "/console/menu";
    final static String REMOVE_SESSION_URL_2 = "/console/showbatch";
    final static String REMOVE_SESSION_URL_3 = "/console/showonline";
    final static String REMOVE_SESSION_URL_4 = "/console/createbatch";
    final static String REMOVE_SESSION_URL_5 = "/console/createonline";
    final static String QUERIES_SESSION_NAME = "queriesSession";
    final static String TABLES_SESSION_NAME = "tablesSession";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	if (request.getServletPath().equalsIgnoreCase(REMOVE_SESSION_URL_1) ||
	    request.getServletPath().equalsIgnoreCase(REMOVE_SESSION_URL_2) ||
	    request.getServletPath().equalsIgnoreCase(REMOVE_SESSION_URL_3) ||
	    request.getServletPath().equalsIgnoreCase(REMOVE_SESSION_URL_4) ||
	    request.getServletPath().equalsIgnoreCase(REMOVE_SESSION_URL_5)){
	    request.getSession().removeAttribute(QUERIES_SESSION_NAME);
	    request.getSession().removeAttribute(TABLES_SESSION_NAME);
	}
	return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	// TODO Auto-generated method stub
	super.postHandle(request, response, handler, modelAndView);
    }

    
}
