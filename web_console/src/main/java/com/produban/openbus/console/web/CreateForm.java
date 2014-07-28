package com.produban.openbus.console.web;

public class CreateForm {

    private String id;
    private String batchMetricName;
    private String batchMetricDesc;
    private String hidModif;
    private String rdMetricType;
    private String sourceId;
    private String selSourceName;
    private String typeQuery;
    private String fromQuery;
    private String selectQuery;
    private String whereQuery;
    private String esTimestamp;    
    private String error;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }    
    public String getHidModif() {
        return hidModif;
    }
    public void setHidModif(String hidModif) {
        this.hidModif = hidModif;
    }
    public String getRdMetricType() {
        return rdMetricType;
    }
    public void setRdMetricType(String rdMetricType) {
        this.rdMetricType = rdMetricType;
    }
    public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    public String getSelSourceName() {
        return selSourceName;
    }
    public void setSelSourceName(String selSourceName) {
        this.selSourceName = selSourceName;
    }
    public String getBatchMetricName() {
        return batchMetricName;
    }
    public void setBatchMetricName(String batchMetricName) {
        this.batchMetricName = batchMetricName;
    }
    public String getBatchMetricDesc() {
        return batchMetricDesc;
    }
    public void setBatchMetricDesc(String batchMetricDesc) {
        this.batchMetricDesc = batchMetricDesc;
    }
    public String getTypeQuery() {
        return typeQuery;
    }
    public void setTypeQuery(String typeQuery) {
        this.typeQuery = typeQuery;
    }
    public String getFromQuery() {
        return fromQuery;
    }
    public void setFromQuery(String fromQuery) {
        this.fromQuery = fromQuery;
    }
    public String getSelectQuery() {
        return selectQuery;
    }
    public void setSelectQuery(String selectQuery) {
        this.selectQuery = selectQuery;
    }
    public String getWhereQuery() {
        return whereQuery;
    }
    public void setWhereQuery(String whereQuery) {
        this.whereQuery = whereQuery;
    }
    public String getEsTimestamp() {
        return esTimestamp;
    }
    public void setEsTimestamp(String esTimestamp) {
        this.esTimestamp = esTimestamp;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}
