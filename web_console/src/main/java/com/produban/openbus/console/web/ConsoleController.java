package com.produban.openbus.console.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.http.HttpEntity;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.produban.openbus.console.domain.CamposOrigen;
import com.produban.openbus.console.domain.MetricaBatch;
import com.produban.openbus.console.domain.OrigenEstructurado;
import com.produban.openbus.console.hive.HiveConnector;
import com.produban.openbus.console.service.MetricaBatchService;
import com.produban.openbus.console.service.OrigenEstructuradoService;
import com.produban.openbus.console.util.HttpConnector;

@RequestMapping("/console/**")
@Controller
public class ConsoleController {

    private final static String ES_MAPPING_ID = "ID";
    private final static String ESTADO_EN_EJECUCION = "Ejecuci&oacute;n";
    private final static String ESTADO_ERROR = "Error";
    private final static String ESTADO_OK = "Ok";

    private static Logger LOG = Logger.getLogger(ConsoleController.class);

    @Autowired
    private OrigenEstructuradoService origenEstructuradoService;

    @Autowired
    private MetricaBatchService metricaBatchService;

    @RequestMapping("/create")
    public String getSources(Model model) {
	List<OrigenEstructurado> lstSources = origenEstructuradoService.findAllOrigenEstructuradoes();
	model.addAttribute("lstSources", lstSources);
	model.addAttribute("metricaBatch", new MetricaBatch());
	return "/create";
    }

    @RequestMapping(value = "/getFieldsBySource")
    public String refreshFields(@RequestParam String idSource, Model model) {
	OrigenEstructurado origenEstructurado = origenEstructuradoService.findOrigenEstructurado(Long.valueOf(idSource));
	origenEstructurado.getHsCamposOrigen().size();
	Set<CamposOrigen> hsFields = origenEstructurado.getHsCamposOrigen();

	List<CamposOrigen> lstFields = new ArrayList<CamposOrigen>();
	for (CamposOrigen field : hsFields) {
	    lstFields.add(field);
	}
	Collections.sort(lstFields, new Comparator<CamposOrigen>(){
	    public int compare(CamposOrigen s1, CamposOrigen s2) {
	        return s1.getNombreCampo().compareTo(s2.getNombreCampo());
	    }
	});
	model.addAttribute("lstFields", lstFields);
	return "/create :: #selFields";
    }

    // ***************** CREATE *****************
    @RequestMapping(value = "/createMetricBBDDES", method = RequestMethod.POST)
    public @ResponseBody CreateForm createMetricBBDDES(Model model, HttpSession session, @RequestBody final CreateForm form) {
	try {
	    MetricaBatch metricaBatch = createMetricBBDD(session, form);
	    metricaBatch = createMetricES(metricaBatch);

	    String isModif = form.getHidModif();
	    metricaBatch.setFechaUltModif(new Date());
	    if (isModif.equals("0")) {
		metricaBatch.setIsCreated(true);
		metricaBatch.setFechaCreacion(new Date());
		LOG.info("SAVE BBDD running....");
		metricaBatchService.saveMetricaBatch(metricaBatch);
		LOG.info("SAVE BBDD done");
	    }
	    else {
		metricaBatch.setIsUpdated(true);
		LOG.info("UPDATE BBDD running....");
		metricaBatchService.updateMetricaBatch(metricaBatch);
		LOG.info("UPDATE BBDD done");
	    }
	    form.setId(metricaBatch.getId().toString());
	}
	catch (Exception e) {
	    form.setId("ERROR");
	    form.setError(e.toString());
	}
	return form;
    }

    private MetricaBatch createMetricBBDD(HttpSession session, CreateForm form) throws Exception {
	MetricaBatch metricaBatch = new MetricaBatch();
	String isBatch = form.getRdMetricType();
	metricaBatch.setBatchMetricName(form.getBatchMetricName());
	metricaBatch.setBatchMetricDesc(form.getBatchMetricDesc());
	metricaBatch.setSourceId(form.getSourceId());
	metricaBatch.setEsCamposId(ES_MAPPING_ID);
	metricaBatch.setEsIndex(form.getSelSourceName());
	metricaBatch.setEsTimestamp(form.getEsTimestamp());
	metricaBatch.setEsType(form.getBatchMetricName());
	metricaBatch.setFechaUltModif(new Date());
	metricaBatch.setUsuarioCreacion((String) session.getAttribute("username"));
	metricaBatch.setTypeQuery(form.getTypeQuery());
	metricaBatch.setFromQuery(form.getFromQuery());
	metricaBatch.setSelectQuery(form.getSelectQuery());
	metricaBatch.setWhereQuery(form.getWhereQuery());

	String strSelectQuery = metricaBatch.getSelectQuery();
	String strFromQuery = metricaBatch.getFromQuery();
	String strWhereQuery = metricaBatch.getWhereQuery();

	StringBuilder insertQuery = new StringBuilder();
	insertQuery.append("INSERT OVERWRITE TABLE " + metricaBatch.getEsType() + " ");
	insertQuery.append(strSelectQuery + " ");
	insertQuery.append(strFromQuery + " ");
	insertQuery.append(strWhereQuery);

	metricaBatch.setQueryCode(insertQuery.toString());
	metricaBatch.setEstado(ESTADO_EN_EJECUCION);

	if (isBatch.equals("1")) {
	    metricaBatch.setIsBatch(true);
	    metricaBatch.setPlanificacion("");
	}
	else {
	    metricaBatch.setIsBatch(false);
	    metricaBatch.setPlanificacion("");
	}
	return metricaBatch;
    }

    private MetricaBatch createMetricES(MetricaBatch metricaBatch) throws Exception {
	Properties prop = new Properties();
	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	InputStream resourceStream = loader.getResourceAsStream("META-INF/spring/environment.properties");
	prop.load(resourceStream);

	String strTypeQuery = metricaBatch.getTypeQuery();
	String strTimestamp = metricaBatch.getEsTimestamp();
	String strQuerySelect = metricaBatch.getSelectQuery();

	// Se crea el indice en elasticsearch
	createESIndex(metricaBatch.getEsIndex(), metricaBatch.getEsType(), strTypeQuery, prop, strTimestamp);

	String dropQuery = "DROP TABLE IF EXISTS " + metricaBatch.getEsType();

	StringBuilder externalQuery = new StringBuilder();
	externalQuery.append("CREATE EXTERNAL TABLE ");
	externalQuery.append(metricaBatch.getEsType());
	externalQuery.append("(");
	externalQuery.append(strTypeQuery);
	externalQuery.append(") ");
	externalQuery.append("STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler' TBLPROPERTIES('es.resource' = '");
	externalQuery.append(metricaBatch.getEsIndex() + "/" + metricaBatch.getEsType());
	if (strQuerySelect.indexOf(" as ID") != -1) {
	    externalQuery.append("', 'es.mapping.id' = '");
	    externalQuery.append(metricaBatch.getEsCamposId());
	    externalQuery.append("', 'es.id.field' = '");
	    externalQuery.append(metricaBatch.getEsCamposId());
	}
	externalQuery.append("', 'es.index.auto.create' = 'true','es.nodes' = '");
	externalQuery.append(prop.getProperty("elastic.url.datanode1") + "," + prop.getProperty("elastic.url.datanode2") + "," + prop.getProperty("elastic.url.datanode3"));
	externalQuery.append("', 'es.port' = '" + prop.getProperty("elastic.port.datanodes"));
	if (strTimestamp != null && (!strTimestamp.equals(""))) {
	    externalQuery.append("', 'es.mapping.names' = '" + strTimestamp + ":@timestamp");
	}
	externalQuery.append("')");
	HiveConnector hiveConnector = new HiveConnector();
	hiveConnector.executeQuery(dropQuery);
	hiveConnector.executeQuery(externalQuery.toString());
	metricaBatch.setCreateCode(externalQuery.toString());
	LOG.info("UPDATE BBDD running....");
	metricaBatchService.updateMetricaBatch(metricaBatch);
	LOG.info("UPDATE BBDD done");
	return metricaBatch;
    }

    private void createESIndex(String index, String type, String strTypeQuery, Properties prop, String timestamp) throws Exception {
	HttpConnector httpConnector = new HttpConnector();

	Map<String, Map> mapPost = new HashMap<String, Map>();
	Map<String, Map> mapPut = new HashMap<String, Map>();
	Map<String, Map> map2 = new HashMap<String, Map>();
	Map<String, Map> map3 = new HashMap<String, Map>();

	ObjectMapper objectMapper = new ObjectMapper();

	String existsUrl = "http://" + prop.getProperty("elastic.url.datanode1") + ":" + prop.getProperty("elastic.port.datanodes") + "/_stats/_indexes?pretty";
	HttpEntity entity = httpConnector.launchHttp(existsUrl, "GET", null);

	JSONParser parser = new JSONParser();
	Object obj = parser.parse(new BufferedReader(new InputStreamReader(entity.getContent())));
	JSONObject jsonObject = (JSONObject) obj;
	jsonObject = (JSONObject) jsonObject.get("indices");
	String json = null;

	mapPost.put("mappings", mapPut);
	mapPut.put(type, map2);
	map2.put("properties", map3);
	String[] array1 = strTypeQuery.split(",");
	Map<String, String> valuesMap = null;
	for (int i = 0; i < array1.length; i++) {
	    valuesMap = new HashMap<String, String>();
	    String[] array2 = array1[i].split(" ");
	    array2[1] = array2[1].toLowerCase();
	    array2[1] = array2[1].replaceAll("\n", "");
	    array2[0] = array2[0].replaceAll("\n", "");

	    if ("bigint".equals(array2[1]) || "int".equals(array2[1])) {
		array2[1] = "long";
	    }
	    valuesMap.clear();
	    valuesMap.put("type", array2[1]);
	    if ("string".equals(array2[1])) {
		valuesMap.put("index", "not_analyzed");
	    }
	    map3.put(array2[0], valuesMap);
	}

	if (timestamp != null && (!timestamp.equals(""))) {
	    valuesMap = new HashMap<String, String>();
	    valuesMap.put("type", "date");
	    valuesMap.put("format", "dateOptionalTime");
	    map3.put("@timestamp", valuesMap);
	}

	if (jsonObject.get(index) != null) { // Existe el indice, se lanza PUT
	    String putUrl = "http://" + prop.getProperty("elastic.url.datanode1") + ":" + prop.getProperty("elastic.port.datanodes") + "/" + index + "/" + type + "/_mapping";
	    json = objectMapper.writeValueAsString(mapPut);

	    entity = httpConnector.launchHttp(putUrl, "PUT", json);
	}
	else { // No existe el indice, se lanza POST
	    String postUrl = "http://" + prop.getProperty("elastic.url.datanode1") + ":" + prop.getProperty("elastic.port.datanodes") + "/" + index + "/";
	    json = objectMapper.writeValueAsString(mapPost);

	    entity = httpConnector.launchHttp(postUrl, "POST", json);
	}
    }

    @RequestMapping(value = "/insertIntoHive", method = RequestMethod.GET)
    public @ResponseBody String insertIntoHive(@RequestParam String idMetric) throws Exception {
	String response = "";
	MetricaBatch metricaBatch = null;
	try {
	    metricaBatch = metricaBatchService.findMetricaBatch(new Long(idMetric));
	    HiveConnector hiveConnector = new HiveConnector();
	    hiveConnector.executeQuery(metricaBatch.getQueryCode());
	    metricaBatch.setFechaUltModif(new Date());
	    metricaBatch.setEstado(ESTADO_OK);
	}
	catch (Exception e) {
	    metricaBatch.setEstado(ESTADO_ERROR);
	    metricaBatch.setError(e.toString());
	    response = "Error al insertar en Hive : " + e.toString();
	    LOG.info("UPDATE BBDD running....");
	    metricaBatchService.updateMetricaBatch(metricaBatch);
	    LOG.info("UPDATE BBDD done");
	    throw e;
	}
	LOG.info("UPDATE BBDD running....");
	metricaBatchService.updateMetricaBatch(metricaBatch);
	LOG.info("UPDATE BBDD done");
	return response;
    }

    // ***************** UPDATE *****************

    @RequestMapping(value = "/updateMetricBBDDES", method = RequestMethod.POST)
    public @ResponseBody CreateForm updateMetricBBDDES(Model model, HttpSession session, @RequestBody final CreateForm form) {
	try {
	    MetricaBatch metricaBatch = null;
	    metricaBatch = metricaBatchService.findMetricaBatch(new Long(form.getHidModif()));

	    Properties prop = new Properties();
	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    InputStream resourceStream = loader.getResourceAsStream("META-INF/spring/environment.properties");
	    prop.load(resourceStream);

	    // Se borra el indice de elasticsearch si existe
	    HttpConnector httpConnector = new HttpConnector();
	    String url = "http://" + prop.getProperty("elastic.url.datanode1") + ":" + prop.getProperty("elastic.port.datanodes") + "/" + metricaBatch.getEsIndex() + "/"
			+ metricaBatch.getEsType();	    
	    try {
		httpConnector.launchHttp(url, "DELETE", null);
	    }
	    catch (Exception e) {
		LOG.warn("Index not found in elasticsearch");
	    }	    

	    try {
		url = "http://" + prop.getProperty("elastic.url.datanode1") + ":" + prop.getProperty("elastic.port.datanodes") + "/" + metricaBatch.getEsIndex() + "/"
				+ form.getBatchMetricName();	    
		httpConnector.launchHttp(url, "DELETE", null);
	    }
	    catch (Exception e) {
		LOG.warn("Index not found in elasticsearch");
	    }	    
	    
	    // Se crea el indice en elasticsearch
	    createESIndex(metricaBatch.getEsIndex(), form.getBatchMetricName(), form.getTypeQuery(), prop, form.getEsTimestamp());

	    // Se borra la tabla de hive antigua
	    String dropQueryOld = "DROP TABLE IF EXISTS " + metricaBatch.getEsType();
	    // Se borra la tabla de hive antigua
	    String dropQueryNew = "DROP TABLE IF EXISTS " + form.getBatchMetricName();

	    
	    // Se crea la tabla de hive
	    StringBuilder externalQuery = new StringBuilder();
	    externalQuery.append("CREATE EXTERNAL TABLE ");
	    externalQuery.append(form.getBatchMetricName());
	    externalQuery.append("(");
	    externalQuery.append(form.getTypeQuery());
	    externalQuery.append(") ");
	    externalQuery.append("STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler' TBLPROPERTIES('es.resource' = '");
	    externalQuery.append(metricaBatch.getEsIndex() + "/" + form.getBatchMetricName());
	    if (form.getSelectQuery().indexOf(" as ID") != -1) {
		externalQuery.append("', 'es.mapping.id' = '");
		externalQuery.append(metricaBatch.getEsCamposId());
		externalQuery.append("', 'es.id.field' = '");
		externalQuery.append(metricaBatch.getEsCamposId());
	    }
	    externalQuery.append("', 'es.index.auto.create' = 'true','es.nodes' = '");
	    externalQuery.append(prop.getProperty("elastic.url.datanode1") + "," + prop.getProperty("elastic.url.datanode2") + "," + prop.getProperty("elastic.url.datanode3"));
	    externalQuery.append("', 'es.port' = '" + prop.getProperty("elastic.port.datanodes"));
	    if (form.getEsTimestamp() != null && (!form.getEsTimestamp().equals(""))) {
		externalQuery.append("', 'es.mapping.names' = '" + form.getEsTimestamp() + ":@timestamp");
	    }
	    externalQuery.append("')");
	    HiveConnector hiveConnector = new HiveConnector();
	    hiveConnector.executeQuery(dropQueryOld);
	    hiveConnector.executeQuery(dropQueryNew);
	    hiveConnector.executeQuery(externalQuery.toString());

	    // Se actualiza en MYSQL
	    String strSelectQuery = form.getSelectQuery();
	    String strFromQuery = form.getFromQuery();
	    String strWhereQuery = form.getWhereQuery();

	    StringBuilder insertQuery = new StringBuilder();
	    insertQuery.append("INSERT OVERWRITE TABLE " + form.getBatchMetricName() + " ");
	    insertQuery.append(strSelectQuery + " ");
	    insertQuery.append(strFromQuery + " ");
	    insertQuery.append(strWhereQuery);
	    metricaBatch.setFechaUltModif(new Date());
	    metricaBatch.setIsUpdated(true);
	    metricaBatch.setSelectQuery(form.getSelectQuery());
	    metricaBatch.setWhereQuery(form.getWhereQuery());
	    metricaBatch.setFechaUltModif(new Date());
	    metricaBatch.setQueryCode(insertQuery.toString());
	    metricaBatch.setTypeQuery(form.getTypeQuery());
	    metricaBatch.setEstado(ESTADO_EN_EJECUCION);
	    metricaBatch.setBatchMetricDesc(form.getBatchMetricDesc());
	    metricaBatch.setBatchMetricName(form.getBatchMetricName());
	    metricaBatch.setEsTimestamp(form.getEsTimestamp());

	    LOG.info("UPDATE BBDD running....");
	    metricaBatchService.updateMetricaBatch(metricaBatch);
	    LOG.info("UPDATE BBDD done");
	}
	catch (Exception e) {
	    form.setId("ERROR");
	    form.setError(e.toString());
	}
	return form;
    }

    @RequestMapping(value = "/insertIntoHiveRel", method = RequestMethod.POST)
    public @ResponseBody String insertIntoHiveRel(@RequestBody final CreateForm form) throws Exception {
	String response = "";
	MetricaBatch metricaBatch = null;
	try {
	    metricaBatch = metricaBatchService.findMetricaBatch(new Long(form.getHidModif()));
	    HiveConnector hiveConnector = new HiveConnector();
	    hiveConnector.executeQuery(metricaBatch.getQueryCode());
	    metricaBatch.setFechaUltModif(new Date());
	    metricaBatch.setEstado(ESTADO_OK);
	}
	catch (Exception e) {
	    metricaBatch.setEstado(ESTADO_ERROR);
	    metricaBatch.setError(e.toString());
	    response = "Error al insertar en Hive : " + e.toString();
	    LOG.info("UPDATE BBDD running....");
	    metricaBatchService.updateMetricaBatch(metricaBatch);
	    LOG.info("UPDATE BBDD done");
	    throw e;
	}
	LOG.info("UPDATE BBDD running....");
	metricaBatchService.updateMetricaBatch(metricaBatch);
	LOG.info("UPDATE BBDD done");
	return response;
    }

    @RequestMapping(value = "/reLaunchMetric", method = RequestMethod.GET)
    public @ResponseBody String reLaunchMetric(@RequestParam String idMetric, Model model) throws Exception {
	MetricaBatch metricaBatch = null;
	try {
	    metricaBatch = metricaBatchService.findMetricaBatch(new Long(idMetric));
	    metricaBatch.setFechaUltModif(new Date());
	    metricaBatch.setEstado(ESTADO_EN_EJECUCION);
	}
	catch (Exception e) {
	    metricaBatch.setEstado(ESTADO_ERROR);
	    metricaBatch.setError(e.toString());
	    LOG.info("UPDATE BBDD running....");
	    metricaBatchService.updateMetricaBatch(metricaBatch);
	    LOG.info("UPDATE BBDD done");
	    throw e;
	}
	LOG.info("UPDATE BBDD running....");
	metricaBatchService.updateMetricaBatch(metricaBatch);
	LOG.info("UPDATE BBDD done");
	return "";
    }

    @RequestMapping("/show")
    public String showMetrics(Model model) {
	List<MetricaBatch> lstMetrics = metricaBatchService.findAllMetricaBatches();
	model.addAttribute("lstMetrics", lstMetrics);
	return "/show";
    }

    @RequestMapping("/refresh")
    public String refreshMetrics(@RequestParam String search, Model model) {
	List<MetricaBatch> lstMetrics = metricaBatchService.findAllMetricaBatches();
	model.addAttribute("lstMetrics", lstMetrics);
	model.addAttribute("search", search);
	return "/show";
    }

    @RequestMapping("/updateMetric")
    public String updateMetric(@RequestParam String idMetric, Model model) {
	List<OrigenEstructurado> lstSources = origenEstructuradoService.findAllOrigenEstructuradoes();
	model.addAttribute("lstSources", lstSources);
	MetricaBatch metricaBatch = metricaBatchService.findMetricaBatch(new Long(idMetric));
	model.addAttribute("metricaBatch", metricaBatch);
	return "/create";
    }

    @RequestMapping(value = "/deleteMetric", method = RequestMethod.GET)
    public @ResponseBody String deleteMetric(@RequestParam String idMetric, Model model) throws Exception {
	Properties prop = new Properties();
	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	InputStream resourceStream = loader.getResourceAsStream("META-INF/spring/environment.properties");
	prop.load(resourceStream);

	MetricaBatch metricaBatch = metricaBatchService.findMetricaBatch(new Long(idMetric));
	HttpConnector httpConnector = new HttpConnector();
	String url = "http://" + prop.getProperty("elastic.url.datanode1") + ":" + prop.getProperty("elastic.port.datanodes") + "/" + metricaBatch.getEsIndex() + "/"
		+ metricaBatch.getEsType();
	try {
	    httpConnector.launchHttp(url, "DELETE", null);
	}
	catch (Exception e) {
	    LOG.warn("Index not found in elasticsearch");
	}	    

	HiveConnector hiveConnector = new HiveConnector();
	hiveConnector.executeQuery("DROP TABLE " + metricaBatch.getEsType());
	LOG.info("DELETE BBDD running....");
	metricaBatchService.deleteMetricaBatch(metricaBatch);
	LOG.info("DELETE BBDD done");
	return "";
    }

    @RequestMapping("/menu")
    public String menu(Model model, HttpSession session) {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	String name = auth.getName();
	session.setAttribute("username", name);
	return "/menu";
    }

    @RequestMapping("/")
    public String login(Model model) {
	return "/login";
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(@RequestParam String url, @RequestParam String action) {
	HttpConnector httpConnector = new HttpConnector();
	try {
	    Properties prop = new Properties();
	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
	    InputStream resourceStream = loader.getResourceAsStream("META-INF/spring/environment.properties");
	    prop.load(resourceStream);

	    // String urlIndexExists = "http://" +
	    // prop.getProperty("elastic.url.datanode1") + ":"
	    // +prop.getProperty("elastic.port.datanodes") +
	    // "/_stats/_indexes?pretty";
	    String urlIndexExists = "http://" + "localhost" + ":" + "9200" + "/_stats/_indexes?pretty";
	    LOG.info("HTTP Action = " + urlIndexExists);
	    HttpEntity entity = httpConnector.launchHttp(urlIndexExists, "GET", null);

	    JSONParser parser = new JSONParser();
	    Object obj = parser.parse(new BufferedReader(new InputStreamReader(entity.getContent())));
	    JSONObject jsonObject = (JSONObject) obj;
	    jsonObject = (JSONObject) jsonObject.get("indices");
	    if (jsonObject.get("new2") == null) {
		LOG.info("NOl = " + jsonObject.get("indices"));
	    }
	    else {
		LOG.info("SIL = " + jsonObject.get("new2"));
		LOG.info("SIL = " + jsonObject.get("indices"));
	    }
	}
	catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return "/test";
    }

    // METODOS ANTIGUOS
    @RequestMapping(value = "/createBatchMetric")
    public String createBatchMetric(@Valid MetricaBatch metricaBatch, BindingResult bindingResult, Model model, HttpServletRequest request, HttpSession session) {
	if (!bindingResult.hasErrors()) {
	    try {
		String isModif = request.getParameter("hidModif");
		String isBatch = request.getParameter("rdMetricType");
		metricaBatch.setCreateCode("");
		metricaBatch.setEsCamposId(ES_MAPPING_ID);
		metricaBatch.setEsIndex(request.getParameter("selSourceName"));
		metricaBatch.setEsType(metricaBatch.getBatchMetricName());
		metricaBatch.setFechaUltModif(new Date());
		metricaBatch.setUsuarioCreacion((String) session.getAttribute("username"));
		if (isBatch.equals("1")) {
		    metricaBatch.setIsBatch(true);
		    metricaBatch.setPlanificacion("");
		}
		else {
		    metricaBatch.setIsBatch(false);
		    metricaBatch.setPlanificacion("");
		}
		if (isModif.equals("0")) {
		    metricaBatch.setFechaCreacion(new Date());
		    metricaBatch.setIsCreated(true); // Esto ira cuando se lance
						     // la metrica
		    metricaBatch.setIsUpdated(false);
		    runMetricAtHive(metricaBatch, request);
		    metricaBatchService.saveMetricaBatch(metricaBatch);
		}
		else {
		    metricaBatch.setIsCreated(false);
		    metricaBatch.setIsUpdated(true); // Esto ira cuando se lance
						     // la metrica
		    runMetricAtHive(metricaBatch, request);
		    metricaBatchService.updateMetricaBatch(metricaBatch);
		}
		model.addAttribute("errorCreateBatchMetric", false);
	    }
	    catch (Exception e) {
		model.addAttribute("messageError", e.getMessage());
		model.addAttribute("errorCreateBatchMetric", true);
	    }
	}
	else {
	    model.addAttribute("messageError", "Error de bind: " + bindingResult);
	    model.addAttribute("errorCreateBatchMetric", true);
	}
	return "/menu";
    }

    private void runMetricAtHive(MetricaBatch metricaBatch, HttpServletRequest request) throws Exception {
	try {
	    String externalQuery = null;
	    String insertQuery = null;
	    if (metricaBatch.getIsCreated()) {
		externalQuery = buildCreateExternal(metricaBatch, request);
		insertQuery = buildInsert(metricaBatch, request);
		metricaBatch.setCreateCode(externalQuery);
		metricaBatch.setQueryCode(insertQuery);
	    }
	    else {
		externalQuery = metricaBatch.getCreateCode();
		insertQuery = metricaBatch.getQueryCode();
	    }
	    String dropQuery = "DROP TABLE " + metricaBatch.getEsType();

	    HiveConnector hiveConnector = new HiveConnector();
	    hiveConnector.executeQuery(dropQuery);
	    hiveConnector.executeQuery(externalQuery);
	    hiveConnector.executeQuery(insertQuery);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    throw e;
	}
    }

    private String buildCreateExternal(MetricaBatch metricaBatch, HttpServletRequest request) throws Exception {
	Properties prop = new Properties();
	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	InputStream resourceStream = loader.getResourceAsStream("META-INF/spring/environment.properties");
	prop.load(resourceStream);

	/*
	 * String strQuerySelect = (String) request.getParameter("selectQuery");
	 * Map<String, String> hmSelectFields = new LinkedHashMap<String,
	 * String>(); Map<String, String> hmSelectFieldsModif = new
	 * LinkedHashMap<String, String>(); strQuerySelect =
	 * strQuerySelect.substring(strQuerySelect.indexOf("ID,") + 3,
	 * strQuerySelect.length()); String key = null; String value = null; for
	 * (String firstCharacter : strQuerySelect.split(",")) { for (String
	 * secondCharacter : firstCharacter.split(" as ")) { if (key != null &&
	 * value != null) { hmSelectFields.put(key, value); key = null; value =
	 * null; } if (key == null) { key = secondCharacter; } else if (value ==
	 * null) { value = secondCharacter; } } } hmSelectFields.put(key,
	 * value); for (Map.Entry entry : hmSelectFields.entrySet()) { String
	 * keyModif = entry.getKey().toString(); String valueModif =
	 * entry.getValue().toString(); if (keyModif.indexOf("(") != -1) {
	 * keyModif = keyModif.substring(0, keyModif.indexOf("(")); }
	 * hmSelectFieldsModif.put(keyModif, valueModif); }
	 * 
	 * StringBuilder externalQuery = new StringBuilder();
	 * externalQuery.append("CREATE EXTERNAL TABLE ");
	 * externalQuery.append(metricaBatch.getEsType());
	 * externalQuery.append("(");
	 * externalQuery.append(metricaBatch.getEsCamposId() + " STRING,");
	 * String type = null; int cont = 0; for (Map.Entry entry :
	 * hmSelectFieldsModif.entrySet()) { cont++; String keyModif =
	 * entry.getKey().toString().trim(); switch (keyModif) { case "MAX":
	 * type = "STRING"; break; case "MIN": type = "STRING"; break; case
	 * "YEAR": type = "BIGINT"; break; case "MONTH": type = "BIGINT"; break;
	 * case "SUM": type = "BIGINT"; break; case "COUNT": type = "BIGINT";
	 * break; default: type = "STRING"; break; } if
	 * (hmSelectFieldsModif.size() != cont) {
	 * externalQuery.append(entry.getValue().toString() + " " + type + ",");
	 * } else { externalQuery.append(entry.getValue().toString() + " " +
	 * type); } }
	 */
	String strQueryType = (String) request.getParameter("typeQuery");
	String strTimestamp = (String) request.getParameter("esTimestamp");
	String strQuerySelect = (String) request.getParameter("selectQuery");
	StringBuilder externalQuery = new StringBuilder();
	externalQuery.append("CREATE EXTERNAL TABLE ");
	externalQuery.append(metricaBatch.getEsType());
	externalQuery.append("(");
	externalQuery.append(strQueryType);
	externalQuery.append(") ");
	externalQuery.append("STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler' TBLPROPERTIES('es.resource' = '");
	externalQuery.append(metricaBatch.getEsIndex() + "/" + metricaBatch.getEsType());
	if (strQuerySelect.indexOf(" as ID") != -1) {
	    externalQuery.append("', 'es.mapping.id' = '");
	    externalQuery.append(metricaBatch.getEsCamposId());
	    externalQuery.append("', 'es.id.field' = '");
	    externalQuery.append(metricaBatch.getEsCamposId());
	}
	externalQuery.append("', 'es.index.auto.create' = 'true','es.nodes' = '");
	externalQuery.append(prop.getProperty("elastic.url.datanode1") + "," + prop.getProperty("elastic.url.datanode2") + "," + prop.getProperty("elastic.url.datanode3"));
	externalQuery.append("', 'es.port' = '" + prop.getProperty("elastic.port.datanodes"));
	if (strTimestamp != null && (!strTimestamp.equals(""))) {
	    externalQuery.append("', 'es.mapping.names' = '" + request.getParameter("txtTimestamp") + ":@timestamp");
	}
	externalQuery.append("')");

	return externalQuery.toString();
    }

    private String buildInsert(MetricaBatch metricaBatch, HttpServletRequest request) throws Exception {
	String strQuerySelect = (String) request.getParameter("selectQuery");
	String strQueryFrom = (String) request.getParameter("fromQuery");
	String strQueryWhere = (String) request.getParameter("whereQuery");

	StringBuilder insertQuery = new StringBuilder();
	insertQuery.append("INSERT OVERWRITE TABLE " + metricaBatch.getEsType() + " ");
	insertQuery.append(strQuerySelect + " ");
	insertQuery.append(strQueryFrom + " ");
	insertQuery.append(strQueryWhere);

	return insertQuery.toString();
    }
}
