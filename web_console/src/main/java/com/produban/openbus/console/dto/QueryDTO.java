package com.produban.openbus.console.dto;

import java.io.Serializable;

public class QueryDTO implements Serializable {

    private String id;
    private String rdCallback;
    private String queryName;
    private String queryFrom;
    private String queryInto;
    private String queryAs;
    
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
}
