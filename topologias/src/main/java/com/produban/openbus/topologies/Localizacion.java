package com.produban.openbus.topologies;

import java.util.ArrayList;
import java.util.Calendar;

public class Localizacion {

    private String ip;
    private String city;
    private String areaCode;
    private String region;
    private String postalCode;
    private String metroCode;
    private String country;

    private ArrayList<Double> coord;

    private long loadTime;
    private long refreshInterval;

    public static long DIA = 86400000;
    public static long HORA = 3600000;
    public static long MINUTO = 60000;

    public Localizacion(long refreshMillis) {
	this.loadTime = Calendar.getInstance().getTimeInMillis();
	this.refreshInterval = refreshMillis;
    }

    public Localizacion(String ip, String city, ArrayList<Double> coord, long refreshMillis) {
	this.loadTime = Calendar.getInstance().getTimeInMillis();
	this.ip = ip;
	this.city = city;
	this.coord = coord;
	this.refreshInterval = refreshMillis;
    }

    public void update(String ip, String city, ArrayList<Double> coord, long refreshMillis, String postalCode, String areaCode, String metroCode, String region, String country) {
	this.loadTime = Calendar.getInstance().getTimeInMillis();
	this.ip = ip;
	this.city = city;
	this.coord = coord;
	this.refreshInterval = refreshMillis;
	this.postalCode = postalCode;
	this.areaCode = areaCode;
	this.metroCode = metroCode;
	this.region = region;
	this.country = country;
    }

    public boolean isValid() {
	long now = Calendar.getInstance().getTimeInMillis();
	if (now - loadTime < refreshInterval) {
	    return true;
	}
	return false;
    }

    public String getIp() {
	return ip;
    }

    public String getCity() {
	return city;
    }

    public ArrayList<Double> getCoords() {
	if (coord == null) {
	    return new ArrayList<Double>();
	}
	return coord;
    }

    public String getCoordsString() {
	if (coord == null) {
	    return "";
	}
	return coord.get(0).toString() + "," + coord.get(1).toString();
    }

    public String getPostalCode() {
	return this.postalCode;
    }

    public String getAreaCode() {
	return this.areaCode;
    }

    public String getMetroCode() {
	return this.metroCode;
    }

    public String getRegion() {
	return this.region;
    }

    public String getCountry() {
	// TODO Auto-generated method stub
	return this.country;
    }
}
