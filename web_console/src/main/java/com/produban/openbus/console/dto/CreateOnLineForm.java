package com.produban.openbus.console.dto;

import java.util.List;

public class CreateOnLineForm {

    private String id;
    private String onLineMetricName;
    private String onLineMetricDesc;
    private String hidModif;
    private String sourceId;
    private String selSourceName;
    private String streamName;
    private String streamFields;
    private List<QueryDTO> queries;
    private List<TableDTO> tables;
    private String error;

    public List<TableDTO> getTables() {
        return tables;
    }

    public void setTables(List<TableDTO> tables) {
        this.tables = tables;
    }

    public List<QueryDTO> getQueries() {
	return queries;
    }

    public void setQueries(List<QueryDTO> queries) {
        this.queries = queries;
    }

    public String getStreamFields() {
        return streamFields;
    }

    public void setStreamFields(String streamFields) {
        this.streamFields = streamFields;
    }

    public String getError() {
	return error;
    }

    public void setError(String error) {
	this.error = error;
    }

    public String getStreamName() {
	return streamName;
    }

    public void setStreamName(String streamName) {
	this.streamName = streamName;
    }

    public String getId() {
	return this.id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getHidModif() {
	return this.hidModif;
    }

    public void setHidModif(String hidModif) {
	this.hidModif = hidModif;
    }

    public String getSourceId() {
	return this.sourceId;
    }

    public void setSourceId(String sourceId) {
	this.sourceId = sourceId;
    }

    public String getSelSourceName() {
	return this.selSourceName;
    }

    public void setSelSourceName(String selSourceName) {
	this.selSourceName = selSourceName;
    }

    public String getOnLineMetricName() {
	return onLineMetricName;
    }

    public void setOnLineMetricName(String onLineMetricName) {
	this.onLineMetricName = onLineMetricName;
    }

    public String getOnLineMetricDesc() {
	return onLineMetricDesc;
    }

    public void setOnLineMetricDesc(String onLineMetricDesc) {
	this.onLineMetricDesc = onLineMetricDesc;
    }
}
