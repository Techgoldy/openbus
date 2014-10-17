package com.produban.openbus.topologies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

public class LocationStore {

    private Map<String, Localizacion> locationCache;

    private String elasticSearchHost, elasticSearchCluster;
    private int elasticSearchPort;
    private Client client;
    private boolean useCache;

    @SuppressWarnings("resource")
    public LocationStore(String ESHost, int ESPort, String ESClusterName, boolean useCache) {
	this.elasticSearchHost = ESHost;
	this.elasticSearchPort = ESPort;
	this.elasticSearchCluster = ESClusterName;
	this.useCache = useCache;

	if (useCache) {
	    locationCache = new HashMap<String, Localizacion>();
	}

	client = new TransportClient(ImmutableSettings.settingsBuilder().put("cluster.name", elasticSearchCluster).build()).addTransportAddress(new InetSocketTransportAddress(
		elasticSearchHost, elasticSearchPort));
    }

    @SuppressWarnings("unchecked")
    public Localizacion getLocationRangos(String ip) {

	// Buscamos en la cache a ver si existe

	Localizacion locCache = null;
	Localizacion loc = new Localizacion(Localizacion.DIA);
	if (useCache) {
	    locCache = locationCache.get(ip);
	}
	// System.out.println("IP a analizar: "+ip);
	// si no está en cache o ha pasado el tiempo máximo se consulta
	// ElasticSearch
	if (locCache == null || !locCache.isValid()) {

	    SearchResponse responseRango = client.prepareSearch("geoip")
		    // uno o más indices
		    .setTypes("geoip_range").setQuery(QueryBuilders.boolQuery() // Your
										// query
			    .must(QueryBuilders.rangeQuery("end_ip").gte(ip)).must(QueryBuilders.rangeQuery("start_ip").lte(ip))).setFrom(0).setSize(60).setExplain(true).execute()
		    .actionGet();

	    Iterator<SearchHit> it = responseRango.getHits().iterator();
	    Boolean fin = false;
	    // System.out.println("  Tenemos "+responseRango.getHits().hits().length+" HITS");
	    while (it.hasNext() && !fin) {
		SearchHit s = it.next();
		loc.update(ip, s.getSource().get("city").toString(), (ArrayList<Double>) s.getSource().get("location"), Localizacion.DIA, s.getSource().get("postalCode")
			.toString(), s.getSource().get("areaCode").toString(), s.getSource().get("metroCode").toString(), s.getSource().get("region").toString(), s.getSource()
			.get("country").toString());
		if (s.getSource().get("start_ip").toString().equals(s.getSource().get("end_ip").toString()) || s.getSource().get("end_ip").toString() == null) {
		    // Es una IP única y tiene prioridad sobre el resto, así que
		    // rompemos bucle
		    fin = true;
		    // System.out.println("    ES UNA FIJA:"+ip);
		}/*
		  * else{ System.out.println("    ES UN RANGO:"+ip); }
		  */
	    }

	    if (useCache) {
		// añadimos/actualizamos la localización a memoria
		locationCache.put(ip, loc);
	    }
	}
	else {
	    loc = locCache;
	}

	return loc;
    }

    @SuppressWarnings("unchecked")
    public Localizacion getLocation(String ip) {

	// Buscamos en la cache a ver si existe

	Localizacion locCache = null;
	Localizacion loc = new Localizacion(Localizacion.DIA);
	if (useCache) {
	    locCache = locationCache.get(ip);
	}

	// si no está en cache o ha pasado el tiempo máximo se consulta
	// ElasticSearch
	if (locCache == null || !locCache.isValid()) {
	    SearchResponse response = client.prepareSearch("geoip") // uno o más
								    // indices
		    .setTypes("geoip_range").setPostFilter(FilterBuilders.termFilter("ip", ip)) // Filter
		    .setFrom(0).setSize(60).setExplain(true).execute().actionGet();

	    Iterator<SearchHit> it = response.getHits().iterator();

	    if (it.hasNext()) {
		// si hay respuestas
		while (it.hasNext()) {
		    SearchHit s = it.next();
		    loc.update(s.getSource().get("ip").toString(), s.getSource().get("city").toString(), (ArrayList<Double>) s.getSource().get("coord"), Localizacion.DIA, s
			    .getSource().get("postalCode").toString(), s.getSource().get("areaCode").toString(), s.getSource().get("metroCode").toString(),
			    s.getSource().get("region").toString(), s.getSource().get("country").toString());
		}
	    }
	    else {
		// Si no hay respuestas para las IP fijas, pasamos a comprobar
		// los rangos
		SearchResponse responseRango = client.prepareSearch("geoip")
			// uno o más indices
			.setTypes("geoip_range").setQuery(QueryBuilders.boolQuery() // Your
										    // query
				.must(QueryBuilders.rangeQuery("ip_fin").gte(ip)).must(QueryBuilders.rangeQuery("ip_inicio").lte(ip))).setFrom(0).setSize(60).setExplain(true)
			.execute().actionGet();

		it = responseRango.getHits().iterator();

		while (it.hasNext()) {
		    SearchHit s = it.next();
		    loc.update(ip, s.getSource().get("city").toString(), (ArrayList<Double>) s.getSource().get("coord"), Localizacion.DIA, s.getSource().get("postalCode")
			    .toString(), s.getSource().get("areaCode").toString(), s.getSource().get("metroCode").toString(), s.getSource().get("region").toString(), s.getSource()
			    .get("country").toString());
		}
	    }

	    if (useCache) {
		locationCache.put(ip, loc);
	    }
	}
	else {
	    loc = locCache;
	}

	return loc;
    }
}
