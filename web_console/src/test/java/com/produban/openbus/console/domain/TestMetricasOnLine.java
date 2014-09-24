package com.produban.openbus.console.domain;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.produban.openbus.console.service.MetricaOnLineService;
import com.produban.openbus.console.service.OrigenEstructuradoService;
import com.produban.openbus.console.service.QueryCepService;
import com.produban.openbus.console.service.StreamCepService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring/*.xml")
public class TestMetricasOnLine {

    @Autowired
    private QueryCepService queryCepService;

    @Autowired
    private MetricaOnLineService metricaOnLineService;

    @Autowired
    private OrigenEstructuradoService origenService;

    @Autowired
    private StreamCepService streamService;
    
    
    public void testCreateOrigen() {
	CamposOrigen campo = new CamposOrigen();
	campo.setNombreCampo("");
	OrigenEstructurado origen = new OrigenEstructurado();
	origen.setIsKafkaOnline(true);
	origen.setKafkaTopic("ob_src_postfix");
	origen.setTopologyName("ob_src_postfix");
    }

    
    public void testCreate() {
	String queryFinal = "from streamPostfix[DSN=='2.0.0' or DSN=='2.6.0' or DSN=='2.4.0']#window.time(20000) as resp "+
				"join "+
				"streamPostfix[ USERFROM != 'null' and SIZE >0 ]#window.time(20000) as tam "+
				"on resp.MSGID==tam.MSGID "+
				"insert all-events into respuestasOK "+
				"tam.MSGID as MSG,resp.DSN as DSN,sum(tam.SIZE) as TAMANO group by tam.MSGID,tam.DSN;";
	
	String querydef = "from streamPostfix[DSN=='2.0.0' or DSN=='2.6.0' or DSN=='2.4.0']#window.time(20000) as resp "+
				"join "+
				"streamPostfix[ USERFROM != 'null' and SIZE >0 ]#window.time(20000) as tam "+
				"on resp.MSGID==tam.MSGID ";
	String outputUser = "tam.MSGID as MSG,resp.DSN as DSN,sum(tam.SIZE) as TAMANO";
	String outputNames = "tam.MSGID,resp.DSN,sum(tam.SIZE)";
	String outputStream = "insert all-events into respuestasOK ";
	String groupBy = "group by tam.MSGID,tam.DSN;";
	
	OrigenEstructurado origen = origenService.findOrigenEstructurado(new Long(5));
	StreamCep streamCep = new StreamCep();	
	streamCep.setStreamName("prueba");
	streamCep.setOrigenEstructurado(origen);
	
	MetricaOnLine metricaOnLine = new MetricaOnLine();
	metricaOnLine.setOnLineMetricName("Prueba 1 metrica online");
	metricaOnLine.setStreamCep(streamCep);	
	
	QueryCep queryCep = new QueryCep();
	Set<QueryCep> sQueryCep = new HashSet<QueryCep>();	
	queryCep.setGroupBy(groupBy);
	queryCep.setHasCallback(false);
	queryCep.setOutputFieldNames(outputNames);
	queryCep.setOutputFieldUser(outputUser);
	queryCep.setOutputStream(outputStream);
	queryCep.setQueryCepId(null);
	queryCep.setQueryDefinition(querydef);
	queryCep.setQueryFinal(queryFinal);
	queryCep.setQueryName("prueba");
	queryCep.setToRemove(false);
	sQueryCep.add(queryCep);
	
	metricaOnLine.setHsQueryCep(sQueryCep);	
	
	//metricaOnLineService.saveMetricaOnLine(metricaOnLine);
	
    }

    
    public void testDelete() {
	QueryCep queryCep = queryCepService.findQueryCep(new Long(1));
	queryCepService.deleteQueryCep(queryCep);
    }
    
    public void testFindAllQueryCep() {
	List<QueryCep> lstQueryCep = queryCepService.findAllQueryCeps();
	for (QueryCep queryCep : lstQueryCep){
	    ObjectMapper mapper = new ObjectMapper();
	    try {
		System.out.println(mapper.writeValueAsString(queryCep));
	    }
	    catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    
    public void testFindAllMetricaOnLine() {
	List<MetricaOnLine> lstMetricaOnLine = metricaOnLineService.findAllMetricaOnLines();
	for (MetricaOnLine metricaOnLine : lstMetricaOnLine){
	    ObjectMapper mapper = new ObjectMapper();
	    try {
		System.out.println(mapper.writeValueAsString(metricaOnLine));
	    }
	    catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
    
    public void updateMetricaOnLine(){
	RestTemplate restTemplate = new RestTemplate();
	MetricaOnLine metricaOnLine = metricaOnLineService.findMetricaOnLine(new Long(3));
	restTemplate.put("http://localhost:8080//web_console/online/updateOnLineMetric", metricaOnLine, MetricaOnLine.class);
    }
    
}
