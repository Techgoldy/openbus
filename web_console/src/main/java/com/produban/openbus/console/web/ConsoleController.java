package com.produban.openbus.console.web;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
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
	model.addAttribute("lstFields", lstFields);
	return "/create :: #selFields";
    }

    @RequestMapping(value = "/createMetricBBDDES", method = RequestMethod.POST)
    public @ResponseBody CreateForm createMetricBBDDES(Model model, HttpSession session, @RequestBody final CreateForm form){
	try {
	    MetricaBatch metricaBatch = createMetricBBDD(session, form);
	    metricaBatch = createMetricES(metricaBatch);

	    String isModif = form.getHidModif();
	    metricaBatch.setFechaUltModif(new Date());
	    if (isModif.equals("0")) {
		metricaBatch.setIsCreated(true);
		metricaBatch.setFechaCreacion(new Date());
		metricaBatchService.saveMetricaBatch(metricaBatch);
	    }
	    else {
		metricaBatch.setIsUpdated(true);
		metricaBatchService.updateMetricaBatch(metricaBatch);
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

	// CURL DELETE
	
	
	String dropQuery = "DROP TABLE " + metricaBatch.getEsType();
	
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
	metricaBatchService.updateMetricaBatch(metricaBatch);
	return metricaBatch;
    }

    @RequestMapping(value = "/insertIntoHive", method = RequestMethod.GET)
    public @ResponseBody String insertIntoHive(@RequestParam String idMetric) throws Exception{
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
	    metricaBatchService.updateMetricaBatch(metricaBatch);
	    throw e;
	}
	metricaBatchService.updateMetricaBatch(metricaBatch);
	return response;
    }

    @RequestMapping(value = "/updateMetricBBDDES", method = RequestMethod.POST)
    public @ResponseBody CreateForm updateMetricBBDDES(Model model, HttpSession session, @RequestBody final CreateForm form){
	try {
	    MetricaBatch metricaBatch = null;
	    metricaBatch = metricaBatchService.findMetricaBatch(new Long(form.getHidModif()));
	    metricaBatch.setFechaUltModif(new Date());
	    metricaBatch.setIsUpdated(true);
	    
	    String strSelectQuery = form.getSelectQuery();
	    String strFromQuery = form.getFromQuery();
	    String strWhereQuery = form.getWhereQuery();
	    
	    StringBuilder insertQuery = new StringBuilder();
	    insertQuery.append("INSERT OVERWRITE TABLE " + metricaBatch.getEsType() + " ");
	    insertQuery.append(strSelectQuery + " ");
	    insertQuery.append(strFromQuery + " ");
	    insertQuery.append(strWhereQuery);

    	    metricaBatch.setSelectQuery(form.getSelectQuery());
    	    metricaBatch.setWhereQuery(form.getWhereQuery());	    
	    metricaBatch.setFechaUltModif(new Date());
	    metricaBatch.setQueryCode(insertQuery.toString());
	    metricaBatch.setEstado(ESTADO_EN_EJECUCION);
	    
	    metricaBatchService.updateMetricaBatch(metricaBatch);
	}
	catch (Exception e) {
	    form.setId("ERROR");
	    form.setError(e.toString());
	}
	return form; 
    }
    
    @RequestMapping(value = "/insertIntoHiveRel", method = RequestMethod.POST)
    public @ResponseBody String insertIntoHiveRel(@RequestBody final CreateForm form) throws Exception{
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
	    metricaBatchService.updateMetricaBatch(metricaBatch);
	    throw e;
	}
	metricaBatchService.updateMetricaBatch(metricaBatch);
	return response;
    }    
    
    @RequestMapping(value = "/reLaunchMetric", method = RequestMethod.GET)
    public @ResponseBody String reLaunchMetric(@RequestParam String idMetric, Model model)  throws Exception{
	insertIntoHive(idMetric);
	return "";
    }

    @RequestMapping("/show")
    public String showMetrics(Model model) {
	List<MetricaBatch> lstMetrics = metricaBatchService.findAllMetricaBatches();
	model.addAttribute("lstMetrics", lstMetrics);
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
    public @ResponseBody String deleteMetric(@RequestParam String idMetric, Model model) throws Exception{
	MetricaBatch metricaBatch = metricaBatchService.findMetricaBatch(new Long(idMetric));
	metricaBatchService.deleteMetricaBatch(metricaBatch);
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

    @RequestMapping("/test")
    public String test() {
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
