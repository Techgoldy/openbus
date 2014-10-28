package com.produban.openbus.console.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;

import com.produban.openbus.console.domain.CamposOrigen;
import com.produban.openbus.console.domain.Estado;
import com.produban.openbus.console.domain.MetricaBatch;
import com.produban.openbus.console.domain.MetricaOnLine;
import com.produban.openbus.console.domain.OrigenEstructurado;
import com.produban.openbus.console.domain.QueryCep;
import com.produban.openbus.console.domain.StreamCep;
import com.produban.openbus.console.domain.TableCep;
import com.produban.openbus.console.dto.CreateOnLineForm;
import com.produban.openbus.console.dto.QueryDTO;
import com.produban.openbus.console.dto.TableDTO;
import com.produban.openbus.console.hive.HiveConnector;
import com.produban.openbus.console.repository.EstadoRepository;
import com.produban.openbus.console.service.EstadoService;
import com.produban.openbus.console.service.MetricaBatchService;
import com.produban.openbus.console.service.MetricaOnLineService;
import com.produban.openbus.console.service.OrigenEstructuradoService;
import com.produban.openbus.console.service.QueryCepService;
import com.produban.openbus.console.service.StreamCepService;
import com.produban.openbus.console.service.TableCepService;
import com.produban.openbus.console.util.HttpConnector;
import com.produban.openbus.siddhiCep.MediaCondicionadaAggregatorFactory;
import com.produban.openbus.siddhiCep.SumadorCondicionalConReinicioAggregatorFactory;

@RequestMapping("/console/**")
@Controller
public class ConsoleController {

    private final static String ES_MAPPING_ID = "ID";
    private final static String ESTADO_EN_EJECUCION = "Ejecuci&oacute;n";
    private final static String ESTADO_ERROR = "Error";
    private final static String ESTADO_OK = "Ok";

    private final static String ESTADO_ONLINE_METRICA_EN_CREACION = "201";
    private final static String ESTADO_ONLINE_STREAM_EN_CREACION = "1";
    private final static String ESTADO_ONLINE_QUERY_EN_CREACION = "101";
    private final static String ESTADO_ONLINE_METRICA_EN_ACTUALIZACION = "210";
    private final static String ESTADO_ONLINE_STREAM_EN_ACTUALIZACION = "4";
    private final static String ESTADO_ONLINE_QUERY_EN_ACTUALIZACION = "105";
    private final static String ESTADO_ONLINE_METRICA_EN_BORRADO = "211";
    private final static String ESTADO_ONLINE_STREAM_EN_BORRADO = "5";
    private final static String ESTADO_ONLINE_QUERY_EN_BORRADO = "106";
    private final static String ESTADO_ONLINE_TABLE_EN_CREACION = "301";
    private final static String ESTADO_ONLINE_TABLE_EN_ACTUALIZACION = "304";
    private final static String ESTADO_ONLINE_TABLE_EN_BORRADO = "305";

    
    private static Logger LOG = Logger.getLogger(ConsoleController.class);

    @Autowired
    private OrigenEstructuradoService origenEstructuradoService;

    @Autowired
    private MetricaBatchService metricaBatchService;

    @Autowired
    private MetricaOnLineService metricaOnLineService;

    @Autowired
    private QueryCepService queryCepService;

    @Autowired
    private StreamCepService streamCepService;
    
    @Autowired
    private EstadoService estadoService;

    @Autowired
    private EstadoRepository estadoRepository;
    
    @Autowired
    private TableCepService tableCepService;
    
    
    // ***************** CREATE BATCH *****************

    @RequestMapping("/createbatch")
    public String getSources(Model model, HttpServletRequest request) {
	List<OrigenEstructurado> lstSources = origenEstructuradoService.findAllOrigenEstructuradoes();
	model.addAttribute("lstSources", lstSources);
	model.addAttribute("metricaBatch", new MetricaBatch());
	if (request.getParameter("lang") != null){
	    model.addAttribute("lang", request.getParameter("lang"));
	    request.getSession().setAttribute("lang", request.getParameter("lang"));
	}
	else if (request.getSession().getAttribute("lang") != null){
	    model.addAttribute("lang", request.getSession().getAttribute("lang"));
	}
	
	return "/console/createbatch";
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
	return "/console/createbatch :: #selFields";
    }

    
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
	metricaBatch.setPlanificacion(form.getPlanificacion());
	metricaBatch.setEsId(form.getEsId());

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
	}
	else {
	    metricaBatch.setIsBatch(false);
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

	LOG.debug("ES Index created");
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
	if (metricaBatch.getEsId() != null){
	    externalQuery.append("', 'es.mapping.id' = '");
	    externalQuery.append(metricaBatch.getEsId());
	    externalQuery.append("', 'es.id.field' = '");
	    externalQuery.append(metricaBatch.getEsId());
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
	boolean bTimeStamp = false;

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
	if (timestamp == null){
	    timestamp = "";
	}
	for (int i = 0; i < array1.length; i++) {
	    valuesMap = new HashMap<String, String>();
	    String[] array2 = array1[i].split(" ");
	    array2[1] = array2[1].toLowerCase();
	    array2[0] = array2[0].toLowerCase();
	    array2[1] = array2[1].replaceAll("\n", "");
	    array2[0] = array2[0].replaceAll("\n", "");

	    if ("string".equals(array2[1])) {
		valuesMap.put("type", "string");
		valuesMap.put("index", "not_analyzed");
		map3.put(array2[0], valuesMap);		
	    }
	    else if ("bigint".equals(array2[1]) || "int".equals(array2[1])) {
		valuesMap.put("type", "long");
		map3.put(array2[0], valuesMap);
	    }
	    else if ("timestamp".equals(array2[1])){ 
		valuesMap.put("type", "date");
		valuesMap.put("format", "dateOptionalTime");
		if(timestamp.equals(array2[1])){
		    bTimeStamp = true;
		    map3.put("@timestamp", valuesMap);
		}
		else{
		    map3.put(array2[0], valuesMap);
		}
	    }
	}

	if ((!bTimeStamp) && (!timestamp.equals(""))) {
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

    // ***************** UPDATE BATCH *****************

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
		//httpConnector.launchHttp(url, "DELETE", null);
	    }
	    catch (Exception e) {
		LOG.warn("Index not found in elasticsearch");
	    }	    

	    try {
		url = "http://" + prop.getProperty("elastic.url.datanode1") + ":" + prop.getProperty("elastic.port.datanodes") + "/" + metricaBatch.getEsIndex() + "/"
				+ form.getBatchMetricName();	    
		//httpConnector.launchHttp(url, "DELETE", null);
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
	    boolean esID = false;
	    if (form.getEsId() != null && (!form.getEsId().equals(""))){
		externalQuery.append("', 'es.mapping.id' = '");
		externalQuery.append(form.getEsId());
		externalQuery.append("', 'es.id.field' = '");
		externalQuery.append(form.getEsId());
		esID = true;
	    }
	    if (! esID){
        	    if (form.getSelectQuery().indexOf(" as ID") != -1) {
        		externalQuery.append("', 'es.mapping.id' = '");
        		externalQuery.append(metricaBatch.getEsCamposId());
        		externalQuery.append("', 'es.id.field' = '");
        		externalQuery.append(metricaBatch.getEsCamposId());
        	    }
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
	    metricaBatch.setPlanificacion(form.getPlanificacion());
	    metricaBatch.setUsuarioModificacion((String) session.getAttribute("username"));
	    metricaBatch.setEsId(form.getEsId());
	    String isBatch = form.getRdMetricType();
	    if (isBatch.equals("1")) {
		metricaBatch.setIsBatch(true);
	    }
	    else {
		metricaBatch.setIsBatch(false);
	    }
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

    @RequestMapping("/showbatch")
    public String showMetrics(Model model, HttpServletRequest request) {
	List<MetricaBatch> lstMetrics = metricaBatchService.findAllMetricaBatches();
	model.addAttribute("lstMetrics", lstMetrics);
	model.addAttribute("search", request.getParameter("hidSearch"));
	if (request.getParameter("lang") != null){
	    model.addAttribute("lang", request.getParameter("lang"));
	    request.getSession().setAttribute("lang", request.getParameter("lang"));
	}
	else if (request.getSession().getAttribute("lang") != null){
	    model.addAttribute("lang", request.getSession().getAttribute("lang"));
	}
	return "/console/showbatch";
    }

    @RequestMapping("/refresh")
    public String refreshMetrics(@RequestParam String search, Model model) {
	List<MetricaBatch> lstMetrics = metricaBatchService.findAllMetricaBatches();
	model.addAttribute("lstMetrics", lstMetrics);
	model.addAttribute("search", search);
	return "/console/showbatch";
    }

    @RequestMapping("/updateMetric")
    public String updateMetric(@RequestParam String idMetric, Model model) {
	List<OrigenEstructurado> lstSources = origenEstructuradoService.findAllOrigenEstructuradoes();
	model.addAttribute("lstSources", lstSources);
	MetricaBatch metricaBatch = metricaBatchService.findMetricaBatch(new Long(idMetric));
	model.addAttribute("metricaBatch", metricaBatch);
	return "/console/createbatch";
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
	return "/console/menu";
    }

 // ***************** CREATE ONLINE *****************    
    @RequestMapping("/createonline")
    public String getSourcesOnLine(Model model, HttpServletRequest request) {
	List<OrigenEstructurado> lstSources = origenEstructuradoService.findAllOrigenEstructuradoes();
	model.addAttribute("lstSources", lstSources);
	model.addAttribute("metricaOnLine", new MetricaOnLine());
	if (request.getParameter("lang") != null){
	    model.addAttribute("lang", request.getParameter("lang"));
	    request.getSession().setAttribute("lang", request.getParameter("lang"));
	}
	else if (request.getSession().getAttribute("lang") != null){
	    model.addAttribute("lang", request.getSession().getAttribute("lang"));
	}
	
	return "/console/createonline";
    }

    @RequestMapping(value = "/getFieldsBySourceOnLine")
    public String refreshFieldsOnLine(@RequestParam String idSource, Model model) {
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
	return "/console/createonline :: #selFields";
    }
    
    @RequestMapping(value = "/saveTable", method = RequestMethod.POST)
    public @ResponseBody TableDTO saveTable(HttpServletRequest request, @RequestBody final TableDTO tableDTO, Model model) {
	List<TableDTO> tablesSession = (List<TableDTO>) request.getSession().getAttribute("tablesSession");
	int idTable = 1;
	if (tablesSession == null){
	    tablesSession = new ArrayList<TableDTO>();
	}
	else if(tablesSession.size() > 0){
	    List lstIds = new ArrayList();
	    for (TableDTO tableDTOAux : tablesSession){
		lstIds.add(tableDTOAux.getId());
	    }
	    Collections.sort(lstIds);
	    String lastId = (String) lstIds.get(lstIds.size() - 1);
	    idTable = Integer.valueOf(lastId) + 1 ;	    
	}
	tableDTO.setId(new Integer(idTable).toString());
	try {
	    tableDTO.setEstado(getEstadoByCode(tableDTO.getEstado().getCode()));
	}
	catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	tablesSession.add(tableDTO);
	request.getSession().setAttribute("tablesSession", tablesSession);
	model.addAttribute("tablesSession", request.getSession().getAttribute("tablesSession"));
	return tableDTO;
    }
    
    @RequestMapping(value = "/updateTable", method = RequestMethod.POST)
    public @ResponseBody TableDTO updateTable(HttpServletRequest request, @RequestBody final TableDTO tableDTO, Model model) {
	List<TableDTO> tablesSession = (List<TableDTO>) request.getSession().getAttribute("tablesSession");
	for (TableDTO tableDTOAux : tablesSession){
	    if(tableDTOAux.getId().equals(tableDTO.getId())){
		tableDTOAux.setTableFields(tableDTO.getTableFields());
		tableDTOAux.setTableName(tableDTO.getTableName());
		tableDTOAux.setVersionMetadata(tableDTO.getVersionMetadata());
		try {
		    tableDTOAux.setEstado(getEstadoByCode(tableDTO.getEstado().getCode()));
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	request.getSession().setAttribute("tablesSession", tablesSession);
	model.addAttribute("tablesSession", request.getSession().getAttribute("tablesSession"));
	return tableDTO;
    }

    @RequestMapping(value = "/findTableById", method = RequestMethod.POST)
    public @ResponseBody TableDTO findTableById(@RequestParam String idTable, HttpServletRequest request) {
	TableDTO tableDTO = null;
	List<TableDTO> tablesSession = (List<TableDTO>) request.getSession().getAttribute("tablesSession");
	for (TableDTO tableDTOAux : tablesSession){
	    if(tableDTOAux.getId().equals(idTable)){
		tableDTO = tableDTOAux;
		break;
	    }
	}
	return tableDTO;
    }
    
    @RequestMapping(value= "/deleteTable")
    public String deleteTable(@RequestParam String idTable, HttpServletRequest request, Model model) {
	List<TableDTO> tablesSession = (List<TableDTO>) request.getSession().getAttribute("tablesSession");
	int index = 0;
	for (TableDTO tableDTOAux : tablesSession){
	    if(tableDTOAux.getId().equals(idTable)){
		break;
	    }
	    index++;
	}
	tablesSession.remove(index);
	request.getSession().setAttribute("tablesSession", tablesSession);
	model.addAttribute("tablesSession", request.getSession().getAttribute("tablesSession"));
	return "/console/createonline :: #divTableTable";
    }

    @RequestMapping("/getTables")
    public String getTables(Model model, HttpServletRequest request) {
	model.addAttribute("tablesSession", request.getSession().getAttribute("tablesSession"));
	return "/console/createonline :: #divTableTable";
    }
    
    @RequestMapping(value = "/saveQuery", method = RequestMethod.POST)
    public @ResponseBody QueryDTO saveQuery(HttpServletRequest request, @RequestBody final QueryDTO queryDTO, Model model) {
	List<QueryDTO> queriesSession = (List<QueryDTO>) request.getSession().getAttribute("queriesSession");
	int idQuery = 1;
	if (queriesSession == null){
	    queriesSession = new ArrayList<QueryDTO>();
	}
	else if(queriesSession.size() > 0){
	    List lstIds = new ArrayList();
	    for (QueryDTO queryDTOAux : queriesSession){
		lstIds.add(queryDTOAux.getId());
	    }
	    Collections.sort(lstIds);
	    String lastId = (String) lstIds.get(lstIds.size() - 1);
	    idQuery = Integer.valueOf(lastId) + 1 ;	    
	}
	try {
	    queryDTO.setEstado(getEstadoByCode(queryDTO.getEstado().getCode()));
	}
	catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	queryDTO.setId(new Integer(idQuery).toString());
	queriesSession.add(queryDTO);
	request.getSession().setAttribute("queriesSession", queriesSession);
	model.addAttribute("queriesSession", request.getSession().getAttribute("queriesSession"));
	return queryDTO;
    }

    @RequestMapping(value = "/updateQuery", method = RequestMethod.POST)
    public @ResponseBody QueryDTO updateQuery(HttpServletRequest request, @RequestBody final QueryDTO queryDTO, Model model) {
	List<QueryDTO> queriesSession = (List<QueryDTO>) request.getSession().getAttribute("queriesSession");
	for (QueryDTO queryDTOAux : queriesSession){
	    if(queryDTOAux.getId().equals(queryDTO.getId())){
		queryDTOAux.setQueryAs(queryDTO.getQueryAs());
		queryDTOAux.setQueryFrom(queryDTO.getQueryFrom());
		queryDTOAux.setQueryGroupBy(queryDTO.getQueryGroupBy());
		queryDTOAux.setQueryInto(queryDTO.getQueryInto());
		queryDTOAux.setQueryName(queryDTO.getQueryName());
		queryDTOAux.setQueryId(queryDTO.getQueryId());
		queryDTOAux.setOutputFieldFormat(queryDTO.getOutputFieldFormat());
		queryDTOAux.setRdCallback(queryDTO.getRdCallback());
		queryDTOAux.setEsTTL(queryDTO.getEsTTL());
		queryDTOAux.setEsType(queryDTO.getEsType());
		queryDTOAux.setQueryOrder(queryDTO.getQueryOrder());
		queryDTOAux.setEstado(queryDTO.getEstado());
		try {
		    queryDTOAux.setEstado(getEstadoByCode(queryDTO.getEstado().getCode()));
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	request.getSession().setAttribute("queriesSession", queriesSession);
	model.addAttribute("queriesSession", request.getSession().getAttribute("queriesSession"));
	return queryDTO;
    }

    @RequestMapping(value = "/findQueryById", method = RequestMethod.POST)
    public @ResponseBody QueryDTO findQueryById(@RequestParam String idQuery, HttpServletRequest request) {
	QueryDTO queryDTO = null;
	List<QueryDTO> queriesSession = (List<QueryDTO>) request.getSession().getAttribute("queriesSession");
	for (QueryDTO queryDTOAux : queriesSession){
	    if(queryDTOAux.getId().equals(idQuery)){
		queryDTO = queryDTOAux;
		break;
	    }
	}
	return queryDTO;
    }
    
    @RequestMapping(value= "/deleteQuery")
    public String deleteQuery(@RequestParam String idQuery, HttpServletRequest request, Model model) {
	List<QueryDTO> queriesSession = (List<QueryDTO>) request.getSession().getAttribute("queriesSession");
	int index = 0;
	for (QueryDTO queryDTOAux : queriesSession){
	    if(queryDTOAux.getId().equals(idQuery)){
		break;
	    }
	    index++;
	}
	queriesSession.remove(index);
	request.getSession().setAttribute("queriesSession", queriesSession);
	model.addAttribute("queriesSession", request.getSession().getAttribute("queriesSession"));
	return "/console/createonline :: #divTable";
    }

    @RequestMapping("/getQueries")
    public String getQueries(Model model, HttpServletRequest request) {
	model.addAttribute("queriesSession", request.getSession().getAttribute("queriesSession"));
	return "/console/createonline :: #divTable";
    }
    
    @RequestMapping(value = "/updateQueryToRemove")
    public String updateQueryToRemove(@RequestParam String idMetric, @RequestParam String idQuery, HttpServletRequest request, Model model) throws Exception {
	QueryCep queryCep = queryCepService.findQueryCep(new Long(idQuery));
	List<QueryDTO> queriesSession = (List<QueryDTO>) request.getSession().getAttribute("queriesSession");
	for (QueryDTO queryDTOAux : queriesSession){
	    if(queryDTOAux.getId().equals(queryCep.getId().toString())){
		try {
		    int versionMetaData = queryCep.getVersionMetadata().intValue();
		    queryDTOAux.setVersionMetadata(new Integer(versionMetaData) + 1);
		    queryDTOAux.setEstado(getEstadoByCode(ESTADO_ONLINE_QUERY_EN_BORRADO));
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	request.getSession().setAttribute("queriesSession", queriesSession);
	model.addAttribute("queriesSession", request.getSession().getAttribute("queriesSession"));
	
	return "/console/createonline :: #divTable";
    }
    
    @RequestMapping(value = "/updateTableToRemove")
    public String updateTableToRemove(@RequestParam String idMetric, @RequestParam String idTable, HttpServletRequest request, Model model) throws Exception {
	TableCep tableCep = tableCepService.findTableCep(new Long(idTable));	
	List<TableDTO> tablesSession = (List<TableDTO>) request.getSession().getAttribute("tablesSession");
	for (TableDTO tableDTOAux : tablesSession){
	    if(tableDTOAux.getId().equals(tableCep.getId().toString())){
		try {
		    int versionMetaData = tableCep.getVersionMetadata().intValue();
		    tableDTOAux.setVersionMetadata(new Integer(versionMetaData) + 1);
		    tableDTOAux.setEstado(getEstadoByCode(ESTADO_ONLINE_TABLE_EN_BORRADO));
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
	request.getSession().setAttribute("tablesSession", tablesSession);
	model.addAttribute("tablesSession", request.getSession().getAttribute("tablesSession"));
	
	return "/console/createonline :: #divTableTable";
    }
    
    
    
    @RequestMapping(value = "/createOnLineMetric", method = RequestMethod.POST)
    public @ResponseBody CreateOnLineForm createOnLineMetric(Model model, HttpServletRequest request, @RequestBody final CreateOnLineForm form) {
	try {
	    boolean enCreacionStream = true;
	    boolean enCreacionQuery = true;
	    boolean enCreacionTable = true;
	    boolean enCreacionMetrica = true;
	    SiddhiConfiguration configuration = new SiddhiConfiguration();
	    List<Class> ext = new ArrayList<Class>();
	    ext.add(SumadorCondicionalConReinicioAggregatorFactory.class);
	    ext.add(MediaCondicionadaAggregatorFactory.class);
	    configuration.setSiddhiExtensions(ext);
	    SiddhiManager siddhiManager = new SiddhiManager(configuration);
	    String isModif = form.getHidModif();
	    
	    // STREAMCEP
	    MetricaOnLine metricaOnLine = null;
	    StreamCep streamCep = null;
	    int versionMetaData = 0;
	    if (isModif.equals("0")) {
		metricaOnLine = new MetricaOnLine();
		streamCep = new StreamCep();
		streamCep.setEstado(getEstadoByCode(ESTADO_ONLINE_STREAM_EN_CREACION));
		streamCep.setVersionMetadata(new Integer(0));
		streamCep.setStreamCepId(null);		
	    }
	    else{
		metricaOnLine = metricaOnLineService.findMetricaOnLine(new Long(isModif));
		streamCep = metricaOnLine.getStreamCep();
		if (! form.getStreamName().equalsIgnoreCase(streamCep.getStreamName())){
		    enCreacionStream = false;
		}
		else if (! form.getStreamFields().equalsIgnoreCase(streamCep.getStreamFields())){
		    enCreacionStream = false;
		}
		
		if (! enCreacionStream){
		    streamCep.setEstado(getEstadoByCode(ESTADO_ONLINE_STREAM_EN_ACTUALIZACION));
		    versionMetaData = streamCep.getVersionMetadata().intValue();
		    streamCep.setVersionMetadata(new Integer(versionMetaData) + 1);
		    enCreacionMetrica = false;
		}
	    }
	    OrigenEstructurado origen = origenEstructuradoService.findOrigenEstructurado(new Long(form.getSourceId()));
	    streamCep.setOrigenEstructurado(origen);
	    streamCep.setStreamName(form.getStreamName());
	    streamCep.setStreamFields(form.getStreamFields());
	    streamCep.setStreamFinal("define stream " + streamCep.getStreamName() + " (" + streamCep.getStreamFields() + ");");
	    
	    try {
		siddhiManager.defineStream(streamCep.getStreamFinal());
	    }
	    catch (Exception e) {
		form.setId("ERROR");
		form.setError("<b>Stream Cep</b>: " +e.toString());
		e.printStackTrace();
		return form;
	    }

	    // TABLECEP	    
	    TableCep tableCep = null;
	    Set<TableCep> sTableCep = new HashSet<TableCep>();

	    List<TableDTO> tablesSession = (List<TableDTO>) request.getSession().getAttribute("tablesSession");
	    Map<String,TableCep> hmTablesBBDD = new HashMap<String, TableCep>();
	    if (tablesSession != null){
		Map<String,TableDTO> hmTablesDTO = new HashMap<String, TableDTO>();
		
		for (TableCep table :metricaOnLine.getHsTableCep()){ 
		    hmTablesBBDD.put(table.getId().toString(), table); // Map with database values
		}
		
		for (TableDTO tableDTO : tablesSession) {
		    enCreacionTable = true;
		    hmTablesDTO.put(tableDTO.getId(), tableDTO); // Map with form values
		    tableCep = new TableCep();
		    tableCep.setTableCepName(tableDTO.getTableName());
		    tableCep.setTableCepFields(tableDTO.getTableFields());
		    tableCep.setTableCepFinal("define table " + tableCep.getTableCepName() + " (" + tableCep.getTableCepFields() + ");");
		    if (hmTablesBBDD.containsKey(tableDTO.getId())){ // Check if it changed
			TableCep tableBBDD = hmTablesBBDD.get(tableDTO.getId());
			if (isModif.equals("0")) {		    
			    tableCep.setTableCepId(null);
			}
			else{
			    tableCep.setTableCepId(tableBBDD.getTableCepId());
			}
			
			if (! tableDTO.getTableName().equalsIgnoreCase(tableBBDD.getTableCepName())){
			    enCreacionTable = false;
			}
			else if (! tableDTO.getTableFields().equalsIgnoreCase(tableBBDD.getTableCepFields())){
			    enCreacionTable = false;
			}
			else if (! tableDTO.getEstado().getId().toString().equals(tableBBDD.getId().toString())){
			    tableCep.setEstado(tableDTO.getEstado());
			    tableCep.setVersionMetadata(tableDTO.getVersionMetadata());
			    enCreacionMetrica = false;
			}
			else{
			    tableCep.setVersionMetadata(tableBBDD.getVersionMetadata());
			    tableCep.setEstado(tableBBDD.getEstado());
			}
			if (! enCreacionTable){
			    tableCep.setEstado(getEstadoByCode(ESTADO_ONLINE_TABLE_EN_ACTUALIZACION));
			    versionMetaData = tableBBDD.getVersionMetadata().intValue();
			    tableCep.setVersionMetadata(new Integer(versionMetaData) + 1);
			    enCreacionMetrica = false;
			}
		    }
		    else{
			enCreacionTable = false;
			tableCep.setVersionMetadata(new Integer(0));
			tableCep.setEstado(getEstadoByCode(ESTADO_ONLINE_TABLE_EN_CREACION));
		    }
		    
		    try {
			if (! tableCep.getEstado().getCode().equals(ESTADO_ONLINE_TABLE_EN_BORRADO)){
			    siddhiManager.defineTable(tableCep.getTableCepFinal());
			}
		    }
		    catch (Exception  e) {
			form.setId("ERROR");
			form.setError("<b>Table Cep</b>: " + e.toString());
			e.printStackTrace();
			return form;
		    }
		    sTableCep.add(tableCep);
		}
		if (hmTablesBBDD.size() != hmTablesDTO.size()){
		    enCreacionMetrica = false;
		}
	    }
	    
	    
	    // QUERYCEP	    
	    QueryCep queryCep = null;
	    String queryFinal = null;
	    String outputFieldNames = null;
	    Set<QueryCep> sQueryCep = new HashSet<QueryCep>();

	    List<QueryDTO> queriesSession = (List<QueryDTO>) request.getSession().getAttribute("queriesSession");
	    Collections.sort(queriesSession);
	    form.setQueries(queriesSession);
	    
	    Map<String,QueryCep> hmQueriesBBDD = new HashMap<String, QueryCep>();
	    if (queriesSession != null){
		Map<String,QueryDTO> hmQueriesDTO = new HashMap<String, QueryDTO>();
		
		for (QueryCep query :metricaOnLine.getHsQueryCep()){ 
		    hmQueriesBBDD.put(query.getId().toString(), query); // Map with database values
		}
		
		for (QueryDTO queryDTO : queriesSession) {
		    enCreacionQuery = true;
		    hmQueriesDTO.put(queryDTO.getId(), queryDTO); // Map with form values
		    queryCep = new QueryCep();
		    outputFieldNames = queryDTO.getQueryAs();		    
		    outputFieldNames = outputFieldNames.toLowerCase();
		    String [] arrOutputFieldNames = outputFieldNames.split(" as ");
		    String strOutputFieldNames = "";
		    for(int i=1;i<arrOutputFieldNames.length;i++){
			if (i == arrOutputFieldNames.length){
			    strOutputFieldNames = strOutputFieldNames.concat(arrOutputFieldNames[i]);
			}
			else{
			    String [] cc = arrOutputFieldNames[i].split(",");
			    strOutputFieldNames = strOutputFieldNames.concat(cc[0]) + ",";
			}
		    }
		    strOutputFieldNames = strOutputFieldNames.trim().substring(0,strOutputFieldNames.length()-1);
		    queryCep.setOutputFieldNames(strOutputFieldNames);

		    if (queryDTO.getRdCallback() == null){
			queryCep.setHasCallback(false);
		    }
		    else{
			if (queryDTO.getRdCallback().equals("") || queryDTO.getRdCallback().equals("0")){
			    queryCep.setHasCallback(false);
			}
			else{
			    queryCep.setHasCallback(true);
			}
		    }

		    queryCep.setGroupBy(queryDTO.getQueryGroupBy());
		    queryCep.setOutputFieldUser(queryDTO.getQueryAs());
		    queryCep.setOutputStream(queryDTO.getQueryInto());
		    queryCep.setQueryDefinition(queryDTO.getQueryFrom());
		    queryCep.setQueryName(queryDTO.getQueryName());
		    if (queryDTO.getQueryId() != null){
			queryCep.setEsId(queryDTO.getQueryId());
		    }
		    queryCep.setOutputFieldFormat(queryDTO.getOutputFieldFormat());
		    queryCep.setEsTTL(queryDTO.getEsTTL());
		    queryCep.setEsType(queryDTO.getEsType());
		    queryFinal = queryDTO.getQueryFrom() + " " + queryDTO.getQueryAs() + " " + queryDTO.getQueryGroupBy() + " " + queryDTO.getQueryInto() ;
		    queryCep.setQueryFinal(queryFinal);
		    queryCep.setQueryOrder(queryDTO.getQueryOrder());
		    
		    if (hmQueriesBBDD.containsKey(queryDTO.getId())){ // Check if it changed
			QueryCep queryBBDD = hmQueriesBBDD.get(queryDTO.getId());
			if (isModif.equals("0")) {		    
			    queryCep.setQueryCepId(null);
			}
			else{
			    queryCep.setQueryCepId(queryBBDD.getQueryCepId());
			}
			
			if (! queryDTO.getQueryGroupBy().equalsIgnoreCase(queryBBDD.getGroupBy())){
			    enCreacionQuery = false;
			}
			else if (! queryDTO.getQueryAs().equalsIgnoreCase(queryBBDD.getOutputFieldUser())){
			    enCreacionQuery = false;
			}
			else if (! queryDTO.getQueryInto().equalsIgnoreCase(queryBBDD.getOutputStream())){
			    enCreacionQuery = false;
			}
			else if (! queryDTO.getQueryFrom().equalsIgnoreCase(queryBBDD.getQueryDefinition())){
			    enCreacionQuery = false;
			}			
			else if (! queryDTO.getQueryName().equalsIgnoreCase(queryBBDD.getQueryName())){
			    enCreacionQuery = false;			    
			}
			else if (! queryDTO.getOutputFieldFormat().equalsIgnoreCase(queryBBDD.getOutputFieldFormat())){
			    enCreacionQuery = false;			    
			}
			else if (! queryDTO.getEsTTL().equalsIgnoreCase(queryBBDD.getEsTTL())){
			    enCreacionQuery = false;			    
			}			
			else if (! queryDTO.getQueryId().equalsIgnoreCase(queryBBDD.getEsId())){
			    enCreacionQuery = false;			    
			}			
			else if (queryBBDD.getHasCallback() && queryDTO.getRdCallback().equals("0")){
			    enCreacionQuery = false;			    
			}			
			else if ((! queryBBDD.getHasCallback()) && queryDTO.getRdCallback().equals("1")){
			    enCreacionQuery = false;			    
			}
			else if (! queryDTO.getEstado().getId().toString().equals(queryBBDD.getId().toString())){
			    queryCep.setEstado(queryDTO.getEstado());
			    queryCep.setVersionMetadata(queryDTO.getVersionMetadata());
			}
			else{
			    queryCep.setVersionMetadata(queryBBDD.getVersionMetadata());
			    queryCep.setEstado(queryBBDD.getEstado());
			    enCreacionMetrica = false;
			}
			if(! enCreacionQuery){
			    queryCep.setEstado(getEstadoByCode(ESTADO_ONLINE_QUERY_EN_ACTUALIZACION));
			    versionMetaData = queryBBDD.getVersionMetadata().intValue();
			    queryCep.setVersionMetadata(new Integer(versionMetaData) + 1);
			    enCreacionMetrica = false;
			}
		    }
		    else{
			enCreacionQuery = false;
			queryCep.setVersionMetadata(new Integer(0));
			queryCep.setEstado(getEstadoByCode(ESTADO_ONLINE_QUERY_EN_CREACION));
		    }
		    
		    try {
			if (! queryCep.getEstado().getCode().equals(ESTADO_ONLINE_QUERY_EN_BORRADO)){
			    siddhiManager.addQuery(queryCep.getQueryFinal());
			}
		    }
		    catch (Exception  e) {
			form.setId("ERROR");
			form.setError("<b>Query Cep -> " + queryDTO.getQueryName() + "</b>: " + e.toString());
			e.printStackTrace();
			return form;
		    }
		    sQueryCep.add(queryCep);
		}
		if (hmQueriesBBDD.size() != hmQueriesDTO.size()){
		    enCreacionMetrica = false;
		}
	    }	    
	    
	    metricaOnLine.setHsTableCep(sTableCep);
	    metricaOnLine.setHsQueryCep(sQueryCep);
	    metricaOnLine.setStreamCep(streamCep);
	    metricaOnLine.setOnLineMetricDesc(form.getOnLineMetricDesc());
	    metricaOnLine.setOnLineMetricName(form.getOnLineMetricName());
	    metricaOnLine.setUsuarioCreacion((String) request.getSession().getAttribute("username"));
	    metricaOnLine.setEsCamposId(ES_MAPPING_ID);
	    metricaOnLine.setEsIndex(form.getSelSourceName());
	    metricaOnLine.setEsType(form.getOnLineMetricName());
	    metricaOnLine.setFechaUltModif(new Date());
	    metricaOnLine.setError(null);
	    
	    if (isModif.equals("0")) {
		metricaOnLine.setFechaCreacion(new Date());
		metricaOnLine.setVersionMetadata(new Integer(0));
		metricaOnLine.setEstado(getEstadoByCode(ESTADO_ONLINE_METRICA_EN_CREACION));
		LOG.info("SAVE BBDD running....");
		metricaOnLineService.saveMetricaOnLine(metricaOnLine);
		LOG.info("SAVE BBDD done");
	    }
	    else {
		if (! enCreacionMetrica){
		    metricaOnLine.setEstado(getEstadoByCode(ESTADO_ONLINE_METRICA_EN_ACTUALIZACION));
		    versionMetaData = metricaOnLine.getVersionMetadata().intValue();
		    metricaOnLine.setVersionMetadata(new Integer(versionMetaData) + 1);
		}
		LOG.info("UPDATE BBDD running....");
		metricaOnLineService.updateMetricaOnLine(metricaOnLine);
		LOG.info("UPDATE BBDD done");
	    }
	    form.setId(metricaOnLine.getId().toString());
	    
	    for (TableCep tableBBDD : hmTablesBBDD.values()){
		tableCepService.deleteTableCep(tableBBDD);
	    }
	    for (QueryCep queryBBDD : hmQueriesBBDD.values()){
		queryCepService.deleteQueryCep(queryBBDD);
	    }
	}
	catch (Exception e) {
	    form.setId("ERROR");
	    form.setError("<b>General</b>: " + e.toString());
	    e.printStackTrace();
	}
	return form;
    }    
    
    private Estado getEstadoByCode(String code) throws Exception{
	List<Estado> lstEstado = estadoRepository.findEstadoByCode(code);
	return lstEstado.get(0);
    }
    
    @RequestMapping("/showonline")
    public String showOnLine(Model model, HttpServletRequest request) {
	List<MetricaOnLine> lstMetrics = metricaOnLineService.findAllMetricaOnLines();
	model.addAttribute("lstMetrics", lstMetrics);
	model.addAttribute("search", request.getParameter("hidSearch"));
	model.addAttribute("lang", request.getParameter("lang"));
	if (request.getParameter("lang") != null){
	    model.addAttribute("lang", request.getParameter("lang"));
	    request.getSession().setAttribute("lang", request.getParameter("lang"));
	}
	else if (request.getSession().getAttribute("lang") != null){
	    model.addAttribute("lang", request.getSession().getAttribute("lang"));
	}
	return "/console/showonline";
    }

    @RequestMapping("/refreshOnLine")
    public String refreshOnLineMetrics(@RequestParam String search, Model model) {
	List<MetricaOnLine> lstMetrics = metricaOnLineService.findAllMetricaOnLines();
	model.addAttribute("lstMetrics", lstMetrics);
	model.addAttribute("search", search);
	return "/console/showonline";
    }

    @RequestMapping("/updateOnLineMetric")
    public String updateOnLineMetric(@RequestParam String idMetric, Model model, HttpServletRequest request) {
	List<TableDTO> tablesBBDD;
	List<QueryDTO> queriesBBDD;
	try {
	    List<OrigenEstructurado> lstSources = origenEstructuradoService.findAllOrigenEstructuradoes();
	    model.addAttribute("lstSources", lstSources);
	    MetricaOnLine metricaOnLine = metricaOnLineService.findMetricaOnLine(new Long(idMetric));
	    model.addAttribute("metricaOnLine", metricaOnLine);

	    tablesBBDD = new ArrayList<TableDTO>();
	    TableDTO tableDTO = null;
	    if (metricaOnLine.getHsTableCep() != null){
		for (TableCep tableCep : metricaOnLine.getHsTableCep()){
		    tableDTO = new TableDTO();
		    tableDTO.setId(tableCep.getId().toString());
		    tableDTO.setTableFields(tableCep.getTableCepFields());
		    tableDTO.setTableName(tableCep.getTableCepName());
		    tableDTO.setEstado(tableCep.getEstado());
		    tableDTO.setVersionMetadata(tableCep.getVersionMetadata());
		    tablesBBDD.add(tableDTO);
		}
	    }

	    queriesBBDD = new ArrayList<QueryDTO>();
	    QueryDTO queryDTO = null;
	    if (metricaOnLine.getHsQueryCep() != null){
		for (QueryCep queryCep : metricaOnLine.getHsQueryCep()){
		    queryDTO = new QueryDTO();
		    queryDTO.setId(queryCep.getId().toString());
		    queryDTO.setQueryAs(queryCep.getOutputFieldUser());
		    queryDTO.setQueryFrom(queryCep.getQueryDefinition());
		    queryDTO.setQueryGroupBy(queryCep.getGroupBy());
		    queryDTO.setQueryInto(queryCep.getOutputStream());
		    queryDTO.setQueryName(queryCep.getQueryName());
		    queryDTO.setQueryId(queryCep.getEsId());
		    queryDTO.setOutputFieldFormat(queryCep.getOutputFieldFormat());
		    queryDTO.setEsTTL(queryCep.getEsTTL());
		    queryDTO.setEsType(queryCep.getEsType());
		    queryDTO.setQueryOrder(queryCep.getQueryOrder());
		    queryDTO.setEstado(queryCep.getEstado());
		    queryDTO.setVersionMetadata(queryCep.getVersionMetadata());
		    if (queryCep.getHasCallback()){
			queryDTO.setRdCallback("1");
		    }
		    else{
			queryDTO.setRdCallback("0");
		    }
		    queriesBBDD.add(queryDTO);
		}
	    }
	    model.addAttribute("tablesSession", tablesBBDD);
	    request.getSession().setAttribute("tablesSession", tablesBBDD);
	    model.addAttribute("queriesSession", queriesBBDD);
	    request.getSession().setAttribute("queriesSession", queriesBBDD);
	    if (request.getParameter("lang") != null){
		model.addAttribute("lang", request.getParameter("lang"));
		request.getSession().setAttribute("lang", request.getParameter("lang"));
	    }
	    else if (request.getSession().getAttribute("lang") != null){
		model.addAttribute("lang", request.getSession().getAttribute("lang"));
	    }
	}
	catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	return "/console/createonline";
    }

    @RequestMapping(value = "/updateToRemove", method = RequestMethod.GET)
    public @ResponseBody String updateToRemove(@RequestParam String idMetric, Model model) throws Exception {
	MetricaOnLine metricaOnLine = metricaOnLineService.findMetricaOnLine(new Long(idMetric));
	metricaOnLine.setEstado(getEstadoByCode(ESTADO_ONLINE_METRICA_EN_BORRADO));
	int versionMetaData = metricaOnLine.getVersionMetadata().intValue();
	metricaOnLine.setVersionMetadata(new Integer(versionMetaData) + 1);
	
	metricaOnLineService.updateMetricaOnLine(metricaOnLine);
	StreamCep streamCep = metricaOnLine.getStreamCep();
	streamCep.setEstado(getEstadoByCode(ESTADO_ONLINE_STREAM_EN_BORRADO));
	versionMetaData = streamCep.getVersionMetadata().intValue();
	streamCep.setVersionMetadata(new Integer(versionMetaData) + 1);
	streamCepService.updateStreamCep(streamCep);
	
	for (TableCep tableCep : metricaOnLine.getHsTableCep()){
	    LOG.info("UPDATE BBDD running....");
	    tableCep.setEstado(getEstadoByCode(ESTADO_ONLINE_TABLE_EN_BORRADO));
	    versionMetaData = tableCep.getVersionMetadata().intValue();
	    tableCep.setVersionMetadata(new Integer(versionMetaData) + 1);
	    tableCepService.updateTableCep(tableCep);
	    LOG.info("UPDATE BBDD done");
	}
	
	for (QueryCep queryCep : metricaOnLine.getHsQueryCep()){
	    LOG.info("UPDATE BBDD running....");
	    queryCep.setEstado(getEstadoByCode(ESTADO_ONLINE_QUERY_EN_BORRADO));
	    versionMetaData = queryCep.getVersionMetadata().intValue();
	    queryCep.setVersionMetadata(new Integer(versionMetaData) + 1);
	    queryCepService.updateQueryCep(queryCep);
	    LOG.info("UPDATE BBDD done");
	}
	
	return "";
    }

    @RequestMapping(value = "/deleteOnLineMetric", method = RequestMethod.GET)
    public @ResponseBody String deleteOnLineMetric(@RequestParam String idMetric, Model model) throws Exception {
	MetricaOnLine metricaOnLine = metricaOnLineService.findMetricaOnLine(new Long(idMetric));
	LOG.info("DELETE BBDD running....");
	metricaOnLineService.deleteMetricaOnLine(metricaOnLine);
	LOG.info("DELETE BBDD done");
	return "";
    }
    
    // ***************** TEST *****************
    
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(Model model) {
	List<MetricaOnLine> lstMetricaOnLine = null;
	try {/*
	    JobDetail job = new JobDetail();
	    	job.setName("dummyJobName");
	    	job.setJobClass(com.produban.openbus.console.util.ScheduledJob.class);
	 
	    	CronTrigger trigger = new CronTrigger();
	    	trigger.setName("dummyTriggerName");
	    	trigger.setCronExpression("0/30 * * * * ?");
	 
	    	//schedule it
	    	Scheduler scheduler = new StdSchedulerFactory().getScheduler();
	    	scheduler.start();
	    	scheduler.scheduleJob(job, trigger);
	    	*/
		lstMetricaOnLine = metricaOnLineService.findAllMetricaOnLines();
		for (MetricaOnLine metricaOnLine : lstMetricaOnLine){
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			model.addAttribute("json",mapper.writeValueAsString(metricaOnLine));
			MetricaOnLine obj = mapper.readValue(mapper.writeValueAsString(metricaOnLine), MetricaOnLine.class);
			System.out.println(obj);
		    }
		    catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	}
	catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return "/test";
    }
    
    @RequestMapping(value = "/test2", method = RequestMethod.GET)
    public @ResponseBody List<MetricaOnLine> test2(Model model) {
	List<MetricaOnLine> lstMetricaOnLine = null;
	try {
		lstMetricaOnLine = metricaOnLineService.findAllMetricaOnLines();
		for (MetricaOnLine metricaOnLine : lstMetricaOnLine){
		    ObjectMapper mapper = new ObjectMapper();
		    try {
			model.addAttribute("json",mapper.writeValueAsString(metricaOnLine));
			MetricaOnLine obj = mapper.readValue(mapper.writeValueAsString(metricaOnLine), MetricaOnLine.class);
			System.out.println(obj);
		    }
		    catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	}
	catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return lstMetricaOnLine;
    }
}
