package com.produban.openbus.console.web;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.produban.openbus.console.domain.Estado;
import com.produban.openbus.console.domain.MetricaOnLine;
import com.produban.openbus.console.domain.QueryCep;
import com.produban.openbus.console.domain.TableCep;
import com.produban.openbus.console.repository.EstadoRepository;
import com.produban.openbus.console.service.MetricaOnLineService;
import com.produban.openbus.console.service.OrigenEstructuradoService;
import com.produban.openbus.console.service.QueryCepService;
import com.produban.openbus.console.service.TableCepService;

@RequestMapping("/online/**")
@Controller
public class ServiceOnLineController {
    private static Logger LOG = Logger.getLogger(ServiceOnLineController.class);

    @Autowired
    private OrigenEstructuradoService origenEstructuradoService;

    @Autowired
    private MetricaOnLineService metricaOnLineService;

    @Autowired
    private QueryCepService queryCepService;

    @Autowired
    private TableCepService tableCepService;
    
    @Autowired
    private EstadoRepository estadoRepository;

    private final static String ESTADO_ONLINE_QUERY_BORRADA = "108";
    private final static String ESTADO_ONLINE_TABLA_BORRADA = "308";
    
    @RequestMapping(value = "/findAllOnlineMetrics", method = RequestMethod.GET)
    public @ResponseBody List<MetricaOnLine> findAllOnlineMetrics(Model model) {
	return metricaOnLineService.findAllMetricaOnLines();
    }

    @RequestMapping(value = "/updateOnLineMetric", method = RequestMethod.PUT, consumes="application/json")
    public @ResponseBody MetricaOnLine updateOnLineMetric(@RequestBody final MetricaOnLine metricaOnLine) {
	LOG.info("UPDATE BBDD running....");
	List<Estado> lstEstado = estadoRepository.findEstadoByCode(metricaOnLine.getEstado().getCode());
	metricaOnLine.setEstado(lstEstado.get(0));
	Set<QueryCep> sQueryCep = new HashSet<QueryCep>();
	Set<TableCep> sTableCep = new HashSet<TableCep>();
	for (TableCep tableCep : metricaOnLine.getHsTableCep()){
	    lstEstado = estadoRepository.findEstadoByCode(tableCep.getEstado().getCode());
	    tableCep.setEstado(lstEstado.get(0));	    
	    if (! tableCep.getEstado().getCode().equals(ESTADO_ONLINE_TABLA_BORRADA)){
		sTableCep.add(tableCep);
	    }
	}
	metricaOnLine.setHsTableCep(sTableCep);
	for (QueryCep queryCep : metricaOnLine.getHsQueryCep()){
	    lstEstado = estadoRepository.findEstadoByCode(queryCep.getEstado().getCode());
	    queryCep.setEstado(lstEstado.get(0));
	    if (! queryCep.getEstado().getCode().equals(ESTADO_ONLINE_QUERY_BORRADA)){
		sQueryCep.add(queryCep);
	    }
	}
	metricaOnLine.setHsQueryCep(sQueryCep);
	lstEstado = estadoRepository.findEstadoByCode(metricaOnLine.getStreamCep().getEstado().getCode());
	metricaOnLine.getStreamCep().setEstado(lstEstado.get(0));
	metricaOnLineService.updateMetricaOnLine(metricaOnLine);
	LOG.info("UPDATE BBDD done");
	return metricaOnLine;
    }

    @RequestMapping(value = "/deleteOnLineMetric", method = RequestMethod.DELETE, consumes="application/json")
    public @ResponseBody MetricaOnLine deleteOnlineMetric(@RequestBody final MetricaOnLine metricaOnLine) {
	LOG.info("DELETE BBDD running....");
	metricaOnLineService.deleteMetricaOnLine(metricaOnLine);
	LOG.info("DELETE BBDD done");
	return null;
    }
    
}
