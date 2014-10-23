package com.produban.openbus.webservice;

import java.io.Serializable;


public class StreamCep implements Serializable {

	/**
	 * 
	 */
	private long id;
	private Integer version;
	private static final long serialVersionUID = 1L;
	private String streamCepId;
    private String streamFields;
    private String streamFinal;
    private String streamName;
    private OrigenEstructurado origenEstructurado;
    private String error; 
    
    private Integer versionMetadata;
    private Estado estado;
    
   
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public String getStreamCepId() {
		return streamCepId;
	}
	public void setStreamCepId(String streamCepId) {
		this.streamCepId = streamCepId;
	}
	public String getStreamFields() {
		return streamFields;
	}
	public void setStreamFields(String streamFields) {
		this.streamFields = streamFields;
	}
	public String getStreamFinal() {
		return streamFinal;
	}
	public void setStreamFinal(String streamFinal) {
		this.streamFinal = streamFinal;
	}
	public String getStreamName() {
		return streamName.trim();
	}
	public void setStreamName(String streamName) {
		this.streamName = streamName;
	}
	public OrigenEstructurado getOrigenEstructurado() {
		return origenEstructurado;
	}
	public void setOrigenEstructurado(OrigenEstructurado origenEstructurado) {
		this.origenEstructurado = origenEstructurado;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
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
	public Integer getVersionMetadata() {
		if(this.versionMetadata==null) this.versionMetadata=0;
		return versionMetadata;
	}
	public void setVersionMetadata(Integer versionMetadata) {
		this.versionMetadata = versionMetadata;
	}
	

 }
