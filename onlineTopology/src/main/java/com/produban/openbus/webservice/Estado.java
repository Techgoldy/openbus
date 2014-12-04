package com.produban.openbus.webservice;

import java.io.Serializable;

public class Estado implements Serializable {

	
	private static final long serialVersionUID = 8338086847889775945L;
	private Integer code;
	private String description;
	private Integer id;
	private Integer version;
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
}
