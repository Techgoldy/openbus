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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping(value = "/createBatchMetric")
    public String createBatchMetric(@Valid MetricaBatch metricaBatch, BindingResult bindingResult, Model model, HttpServletRequest request, HttpSession session) {
	if (!bindingResult.hasErrors()) {
	    metricaBatch.setCreateCode("");
	    metricaBatch.setEsCamposId(ES_MAPPING_ID);
	    metricaBatch.setEsIndex(request.getParameter("selSourceName"));
	    metricaBatch.setEsType(metricaBatch.getBatchMetricName());
	    metricaBatch.setFechaCreacion(new Date());
	    metricaBatch.setFechaUltModif(new Date());
	    metricaBatch.setIsCreated(true);
	    metricaBatch.setIsUpdated(false);
	    metricaBatch.setPlanificacion("");
	    metricaBatch.setUsuarioCreacion((String) session.getAttribute("username"));
	    try {
		runMetricAtHive(metricaBatch, request);
		metricaBatchService.saveMetricaBatch(metricaBatch);
		model.addAttribute("errorCreateBatchMetric", false);
	    }
	    catch (Exception e) {
		model.addAttribute("errorCreateBatchMetric", true);
	    }
	}
	else {
	    model.addAttribute("errorCreateBatchMetric", true);
	}
	return "/menu";
    }

    @RequestMapping(value = "/reLaunchMetric")
    public String reLaunchMetric(@RequestParam String idMetric, Model model) {
	MetricaBatch metricaBatch = metricaBatchService.findMetricaBatch(new Long(idMetric));
	try {
	    metricaBatch.setFechaUltModif(new Date());
	    metricaBatch.setIsCreated(false);
	    metricaBatch.setIsUpdated(true);
	    runMetricAtHive(metricaBatch, null);
	    metricaBatchService.saveMetricaBatch(metricaBatch);
	    model.addAttribute("errorCreateBatchMetric", false);
	}
	catch (Exception e) {
	    model.addAttribute("errorCreateBatchMetric", true);
	}
	List<MetricaBatch> lstMetrics = metricaBatchService.findAllMetricaBatches();
	model.addAttribute("lstMetrics", lstMetrics);
	return "/show :: #divShow";
    }

    @RequestMapping("/show")
    public String showMetrics(Model model) {
	List<MetricaBatch> lstMetrics = metricaBatchService.findAllMetricaBatches();
	model.addAttribute("lstMetrics", lstMetrics);
	return "/show";
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

    private void runMetricAtHive(MetricaBatch metricaBatch, HttpServletRequest request) throws Exception {
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

    private String buildCreateExternal(MetricaBatch metricaBatch, HttpServletRequest request) throws Exception {
	Properties prop = new Properties();
	ClassLoader loader = Thread.currentThread().getContextClassLoader();
	InputStream resourceStream = loader.getResourceAsStream("META-INF/spring/environment.properties");
	prop.load(resourceStream);

	/*
	String strQuerySelect = (String) request.getParameter("txtQuerySelect");
	Map<String, String> hmSelectFields = new LinkedHashMap<String, String>();
	Map<String, String> hmSelectFieldsModif = new LinkedHashMap<String, String>();
	strQuerySelect = strQuerySelect.substring(strQuerySelect.indexOf("ID,") + 3, strQuerySelect.length());
	String key = null;
	String value = null;
	for (String firstCharacter : strQuerySelect.split(",")) {
	    for (String secondCharacter : firstCharacter.split(" as ")) {
		if (key != null && value != null) {
		    hmSelectFields.put(key, value);
		    key = null;
		    value = null;
		}
		if (key == null) {
		    key = secondCharacter;
		}
		else if (value == null) {
		    value = secondCharacter;
		}
	    }
	}
	hmSelectFields.put(key, value);
	for (Map.Entry entry : hmSelectFields.entrySet()) {
	    String keyModif = entry.getKey().toString();
	    String valueModif = entry.getValue().toString();
	    if (keyModif.indexOf("(") != -1) {
		keyModif = keyModif.substring(0, keyModif.indexOf("("));
	    }
	    hmSelectFieldsModif.put(keyModif, valueModif);
	}

	StringBuilder externalQuery = new StringBuilder();
	externalQuery.append("CREATE EXTERNAL TABLE ");
	externalQuery.append(metricaBatch.getEsType());
	externalQuery.append("(");
	externalQuery.append(metricaBatch.getEsCamposId() + " STRING,");
	String type = null;
	int cont = 0;
	for (Map.Entry entry : hmSelectFieldsModif.entrySet()) {
	    cont++;
	    String keyModif = entry.getKey().toString().trim();
	    switch (keyModif) {
	    case "MAX":
		type = "STRING";
		break;
	    case "MIN":
		type = "STRING";
		break;
	    case "YEAR":
		type = "BIGINT";
		break;
	    case "MONTH":
		type = "BIGINT";
		break;
	    case "SUM":
		type = "BIGINT";
		break;
	    case "COUNT":
		type = "BIGINT";
		break;
	    default:
		type = "STRING";
		break;
	    }
	    if (hmSelectFieldsModif.size() != cont) {
		externalQuery.append(entry.getValue().toString() + " " + type + ",");
	    }
	    else {
		externalQuery.append(entry.getValue().toString() + " " + type);
	    }
	}
	
	*/
	String strQueryType = (String) request.getParameter("txtQueryType");
	String strTimestamp = (String) request.getParameter("txtTimestamp");
	String strQuerySelect = (String) request.getParameter("txtQuerySelect");
	StringBuilder externalQuery = new StringBuilder();
	externalQuery.append("CREATE EXTERNAL TABLE ");
	externalQuery.append(metricaBatch.getEsType());
	externalQuery.append("(");
	externalQuery.append(strQueryType);
	externalQuery.append(") ");
	externalQuery.append("STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler' TBLPROPERTIES('es.resource' = '");
	externalQuery.append(metricaBatch.getEsIndex() + "/" + metricaBatch.getEsType());
	if (strQuerySelect.indexOf(" as ID") != -1){
	    externalQuery.append("', 'es.mapping.id' = '");
	    externalQuery.append(metricaBatch.getEsCamposId());
	    externalQuery.append("', 'es.id.field' = '");
	    externalQuery.append(metricaBatch.getEsCamposId());
	}	
	externalQuery.append("', 'es.index.auto.create' = 'true','es.nodes' = '");
	externalQuery.append(prop.getProperty("elastic.url.datanode1") + "," + prop.getProperty("elastic.url.datanode2") + "," + prop.getProperty("elastic.url.datanode3"));
	externalQuery.append("', 'es.port' = '" + prop.getProperty("elastic.port.datanodes"));
	if (strTimestamp != null && (! strTimestamp.equals(""))){
		externalQuery.append("', 'es.mapping.names' = '" + request.getParameter("txtTimestamp") + ":@timestamp");
	}
	externalQuery.append("')");
	
	return externalQuery.toString();
    }

    private String buildInsert(MetricaBatch metricaBatch, HttpServletRequest request) throws Exception {
	String strQuerySelect = (String) request.getParameter("txtQuerySelect");
	String strQueryFrom = (String) request.getParameter("txtQueryFrom");
	String strQueryWhere = (String) request.getParameter("txtQueryWhere");

	StringBuilder insertQuery = new StringBuilder();
	insertQuery.append("INSERT OVERWRITE TABLE " + metricaBatch.getEsType() + " ");
	insertQuery.append(strQuerySelect + " ");
	insertQuery.append(strQueryFrom + " ");
	insertQuery.append(strQueryWhere);

	return insertQuery.toString();
    }
}
