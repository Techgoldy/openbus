package com.produban.openbus.console.dto;

import java.io.Serializable;

import com.produban.openbus.console.domain.Estado;

public class TableDTO implements Serializable {

    private String id;
    private String tableName;
    private String tableFields;
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
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getTableFields() {
        return tableFields;
    }
    public void setTableFields(String tableFields) {
        this.tableFields = tableFields;
    }
}
