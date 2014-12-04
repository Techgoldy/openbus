package com.produban.openbus.webservice;

import java.io.Serializable;
import java.util.Set;

public class OrigenEstructurado implements Serializable {


	private long id;
	private Integer version;
	private static final long serialVersionUID = 1L;
	private String topologyName;
    private String kafkaTopic;
    private Boolean isKafkaOnline;
    private Set<CamposOrigen> hsCamposOrigen;
	public String getTopologyName() {
		return topologyName;
	}
	public void setTopologyName(String topologyName) {
		this.topologyName = topologyName;
	}
	public String getKafkaTopic() {
		return kafkaTopic;
	}
	public void setKafkaTopic(String kafkaTopic) {
		this.kafkaTopic = kafkaTopic;
	}
	public Boolean getIsKafkaOnline() {
		return isKafkaOnline;
	}
	public void setIsKafkaOnline(Boolean isKafkaOnline) {
		this.isKafkaOnline = isKafkaOnline;
	}
	public Set<CamposOrigen> getHsCamposOrigen() {
		return hsCamposOrigen;
	}
	public void setHsCamposOrigen(Set<CamposOrigen> hsCamposOrigen) {
		this.hsCamposOrigen = hsCamposOrigen;
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
    
    
}
