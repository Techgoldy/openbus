package com.produban.openbus.console.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.produban.openbus.console.domain.MetricaOnLine;
import com.produban.openbus.console.domain.QueryCep;
import com.produban.openbus.console.service.MetricaOnLineService;
import com.produban.openbus.console.service.OrigenEstructuradoService;

@RequestMapping("/online/**")
@Controller
public class ServiceOnLineController {

    private static Logger LOG = Logger.getLogger(ServiceOnLineController.class);

    @Autowired
    private OrigenEstructuradoService origenEstructuradoService;

    @Autowired
    private MetricaOnLineService metricaOnLineService;
    
    @RequestMapping(value = "/findAllOnlineMetrics", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody List<MetricaOnLine> findAllOnlineMetrics(Model model) {
	return metricaOnLineService.findAllMetricaOnLines();
    }

    @RequestMapping(value = "/findStreamNullOnlineMetrics", method = RequestMethod.GET)
    public @ResponseBody List<MetricaOnLine> findStreamNullOnlineMetrics(Model model) {
	List<MetricaOnLine> lstMetrics = new ArrayList<MetricaOnLine>();
	for (MetricaOnLine metricaOnLine : metricaOnLineService.findAllMetricaOnLines()){
	    if (metricaOnLine.getStreamCep().getStreamCepId() == null){
		lstMetrics.add(metricaOnLine);
	    }
	}
	return lstMetrics;
    }

    @RequestMapping(value = "/findToRemoveOnlineMetrics", method = RequestMethod.GET)
    public @ResponseBody List<MetricaOnLine> findToRemoveOnlineMetrics(Model model) {
	List<MetricaOnLine> lstMetrics = new ArrayList<MetricaOnLine>();
	for (MetricaOnLine metricaOnLine : metricaOnLineService.findAllMetricaOnLines()){
	    boolean toRemove = false;
	    for(QueryCep queryCep : metricaOnLine.getHsQueryCep()){
		if (queryCep.getToRemove()){
		    toRemove = true;
		    break;
		}
	    }
	    if(toRemove){
		lstMetrics.add(metricaOnLine);
	    }
	}
	return lstMetrics;
    }
    
    @RequestMapping(value = "/updateOnLineMetric", method = RequestMethod.PUT, consumes="application/json")
    public @ResponseBody MetricaOnLine updateOnLineMetric(@RequestBody final MetricaOnLine metricaOnLine) {
	metricaOnLineService.updateMetricaOnLine(metricaOnLine);
	return metricaOnLine;
    }

}
