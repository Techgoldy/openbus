package com.produban.openbus.webservice;

import java.io.Serializable;

public class TableCep implements Serializable {


	private static final long serialVersionUID = -3122730729347487777L;
	private long id;
	private Integer version;
	private String tableCepId;
    private String tableCepFields;
    private String tableCepFinal;
    private String tableCepName;
    private String error; 
    
    private Integer versionMetadata;
    private Estado estado;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getTableCepId() {
		return tableCepId;
	}
	public void setTableCepId(String tableCepId) {
		this.tableCepId = tableCepId;
	}
	public String getTableCepFields() {
		return tableCepFields;
	}
	public void setTableCepFields(String tableCepFields) {
		this.tableCepFields = tableCepFields;
	}
	public String getTableCepFinal() {
		return tableCepFinal;
	}
	public void setTableCepFinal(String tableCepFinal) {
		this.tableCepFinal = tableCepFinal;
	}
	public String getTableCepName() {
		return tableCepName;
	}
	public void setTableCepName(String tableCepName) {
		this.tableCepName = tableCepName;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Integer getVersionMetadata() {
		return versionMetadata;
	}
	public void setVersionMetadata(Integer versionMetadata) {
		if(this.versionMetadata==null) this.versionMetadata=0;
		this.versionMetadata = versionMetadata;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
    
    
}
