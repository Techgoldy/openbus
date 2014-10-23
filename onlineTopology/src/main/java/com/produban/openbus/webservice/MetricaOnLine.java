package com.produban.openbus.webservice;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MetricaOnLine implements Serializable {


	private long id;
	private Integer version;
	private static final long serialVersionUID = 1L;
	private String onLineMetricName;
    private String onLineMetricDesc;
    private String esIndex;
    private String esType;
    private String esCamposId;
    private Date fechaCreacion;
    private Date fechaUltModif;
    private String usuarioCreacion;
    private String usuarioModificacion;
    private Set<QueryCep> hsQueryCep = new HashSet<QueryCep>();
    private Set<TableCep> hsTableCep = new HashSet<TableCep>();
    private StreamCep streamCep;
    
    private Integer versionMetadata;
    private Estado estado;
    private String error;
    
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
	public String getEsIndex() {
		return esIndex;
	}
	public void setEsIndex(String esIndex) {
		this.esIndex = esIndex;
	}
	public String getEsType() {
		return esType;
	}
	public void setEsType(String esType) {
		this.esType = esType;
	}
	public String getEsCamposId() {
		return esCamposId;
	}
	public void setEsCamposId(String esCamposId) {
		this.esCamposId = esCamposId;
	}
	public Date getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public Date getFechaUltModif() {
		return fechaUltModif;
	}
	public void setFechaUltModif(Date fechaUltModif) {
		this.fechaUltModif = fechaUltModif;
	}
	public String getUsuarioCreacion() {
		return usuarioCreacion;
	}
	public void setUsuarioCreacion(String usuarioCreacion) {
		this.usuarioCreacion = usuarioCreacion;
	}
	public String getUsuarioModificacion() {
		return usuarioModificacion;
	}
	public void setUsuarioModificacion(String usuarioModificacion) {
		this.usuarioModificacion = usuarioModificacion;
	}
	public Set<QueryCep> getHsQueryCep() {
		return hsQueryCep;
	}
	public void setHsQueryCep(Set<QueryCep> hsQueryCep) {
		this.hsQueryCep = hsQueryCep;
	}
	public StreamCep getStreamCep() {
		return streamCep;
	}
	public void setStreamCep(StreamCep streamCep) {
		this.streamCep = streamCep;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
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
	public Set<TableCep> getHsTableCep() {
		return hsTableCep;
	}
	public void setHsTableCep(Set<TableCep> hsTableCep) {
		this.hsTableCep = hsTableCep;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

    
    
}
