package com.produban.openbus.webservice;

import java.io.Serializable;

public class CamposOrigen implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nombreCampo;
    private String tipoCampo;
    private Long ordenEnTabla;
    private OrigenEstructurado origenEstructurado;
    private long id;
	private Integer version;
	
	
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
	public String getNombreCampo() {
		return nombreCampo;
	}
	public void setNombreCampo(String nombreCampo) {
		this.nombreCampo = nombreCampo;
	}
	public String getTipoCampo() {
		return tipoCampo;
	}
	public void setTipoCampo(String tipoCampo) {
		this.tipoCampo = tipoCampo;
	}
	public Long getOrdenEnTabla() {
		return ordenEnTabla;
	}
	public void setOrdenEnTabla(Long ordenEnTabla) {
		this.ordenEnTabla = ordenEnTabla;
	}
	public OrigenEstructurado getOrigenEstructurado() {
		return origenEstructurado;
	}
	public void setOrigenEstructurado(OrigenEstructurado origenEstructurado) {
		this.origenEstructurado = origenEstructurado;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
