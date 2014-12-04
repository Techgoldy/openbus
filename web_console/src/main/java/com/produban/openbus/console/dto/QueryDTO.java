package com.produban.openbus.console.dto;

import java.io.Serializable;

import com.produban.openbus.console.domain.Estado;

public class QueryDTO implements Serializable, Comparable<QueryDTO> {

    private String id;
    private String rdCallback;
    private String queryName;
    private String queryFrom;
    private String queryInto;
    private String queryAs;
    private String queryId;
    private String outputFieldFormat;
    private String esTTL;
    private String esType;
    private Integer queryOrder;
    private Estado estado;
    private Integer versionMetadata;    
    
    public Integer getVersionMetadata() {
        return versionMetadata;
    }
    public void setVersionMetadata(Integer versionMetadata) {
        this.versionMetadata = versionMetadata;
    }
    public Estado getEstado() {
        return estado;
    }
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    public Integer getQueryOrder() {
        return queryOrder;
    }
    public void setQueryOrder(Integer queryOrder) {
        this.queryOrder = queryOrder;
    }
    public String getEsTTL() {
        return esTTL;
    }
    public void setEsTTL(String esTTL) {
        this.esTTL = esTTL;
    }
    public String getEsType() {
        return esType;
    }
    public void setEsType(String esType) {
        this.esType = esType;
    }
    public String getOutputFieldFormat() {
        return outputFieldFormat;
    }
    public void setOutputFieldFormat(String outputFieldFormat) {
        this.outputFieldFormat = outputFieldFormat;
    }
    public String getQueryId() {
        return queryId;
    }
    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }
    public String getRdCallback() {
        return rdCallback;
    }
    public void setRdCallback(String rdCallback) {
        this.rdCallback = rdCallback;
    }
    private String queryGroupBy;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getQueryName() {
        return queryName;
    }
    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }
    public String getQueryFrom() {
        return queryFrom;
    }
    public void setQueryFrom(String queryFrom) {
        this.queryFrom = queryFrom;
    }
    public String getQueryInto() {
        return queryInto;
    }
    public void setQueryInto(String queryInto) {
        this.queryInto = queryInto;
    }
    public String getQueryAs() {
        return queryAs;
    }
    public void setQueryAs(String queryAs) {
        this.queryAs = queryAs;
    }
    public String getQueryGroupBy() {
        return queryGroupBy;
    }
    public void setQueryGroupBy(String queryGroupBy) {
        this.queryGroupBy = queryGroupBy;
    }
    
    @Override
    public int compareTo(QueryDTO o) {
	int comp=this.getQueryOrder().compareTo(o.getQueryOrder());
        if (comp==0) comp=1;
        return comp;
    }
}
