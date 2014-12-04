package com.produban.openbus.webservice;


import java.io.Serializable;

public class QueryCep implements Serializable, Comparable<QueryCep>{

	private static final long serialVersionUID = 1L;
	private String queryFinal;
    private String queryDefinition;
    private String outputFieldUser;
    private String outputFieldNames;
    private String outputStream;
    private String groupBy;
    private String queryCepId;
    private String queryName;
    private Boolean hasCallback;
    private String error;
    private long id;
	private Integer version;
	private String esId;
	private String outputFieldFormat;
	
	private Integer versionMetadata;
	private Estado estado;
	private String esTTL;
	private String esType;
	private Integer queryOrder;
	
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
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
	public String getQueryFinal() {
		return queryFinal;
	}
	public void setQueryFinal(String queryFinal) {
		this.queryFinal = queryFinal;
	}
	public String getQueryDefinition() {
		return queryDefinition;
	}
	public void setQueryDefinition(String queryDefinition) {
		this.queryDefinition = queryDefinition;
	}
	public String getOutputFieldUser() {
		return outputFieldUser;
	}
	public void setOutputFieldUser(String outputFieldUser) {
		this.outputFieldUser = outputFieldUser;
	}
	public String getOutputFieldNames() {
		return outputFieldNames;
	}
	public void setOutputFieldNames(String outputFieldNames) {
		this.outputFieldNames = outputFieldNames;
	}
	public String getOutputStream() {
		return outputStream;
	}
	public void setOutputStream(String outputStream) {
		this.outputStream = outputStream;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	public String getQueryCepId() {
		if(queryCepId==null) queryCepId="";
		return queryCepId;
	}
	public void setQueryCepId(String queryCepId) {
		this.queryCepId = queryCepId;
	}
	public String getQueryName() {
		return queryName;
	}
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}
	public Boolean getHasCallback() {
		if(hasCallback==null) this.hasCallback=false;
		return hasCallback;
	}
	public void setHasCallback(Boolean hasCallback) {
		this.hasCallback = hasCallback;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getEsId() {
		if(esId==null) esId="";
		return esId;
	}
	public void setEsId(String eSID) {
		esId = eSID;
	}
	public String getOutputFieldFormat() {
		return outputFieldFormat;
	}
	public void setOutputFieldFormat(String outputFieldFormat) {
		this.outputFieldFormat = outputFieldFormat;
	} 
	public Integer getVersionMetadata() {
		if(this.versionMetadata==null) this.versionMetadata=0;
		return versionMetadata;
	}
	public void setVersionMetadata(Integer versionMetadata) {
		this.versionMetadata = versionMetadata;
	}

	public String getEsType() {
		return esType;
	}
	public void setEsType(String esType) {
		this.esType = esType;
	}
	public String getEsTTL() {
		return esTTL;
	}
	public void setEsTTL(String esTTL) {
		this.esTTL = esTTL;
	}

	public Integer getQueryOrder() {
		if(queryOrder==null) queryOrder=0;
		return queryOrder;
	}
	public void setQueryOrder(Integer queryOrder) {
		this.queryOrder = queryOrder;
	}
	@Override	
	public int compareTo(QueryCep o) {
		// TODO Auto-generated method stub
		int comp=this.getQueryOrder().compareTo(o.getQueryOrder());
		if (comp==0) comp=1;
		return comp;
	}
	
	
	public String outputStreramName(){
		String into = this.outputStream.toLowerCase();
		
        if (into.indexOf("insert into ") != -1){
               return outputStream.split(" ")[2].trim(); 
        }
        else if (into.indexOf("delete ") != -1){
        	return outputStream.split(" ")[1].trim(); 
        }
        else if (into.indexOf("update ") != -1){
        	return outputStream.split(" ")[1].trim();
        }
       
		return "";
	}

}
