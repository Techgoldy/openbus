package com.produban.openbus.console.dto;

public class CreateForm {

    private String id;
    private String batchMetricName;
    private String batchMetricDesc;
    private String hidModif;
    private String rdMetricType;
    private String sourceId;
    private String selSourceName;
    private String typeQuery;
    private String fromQuery;
    private String selectQuery;
    private String whereQuery;
    private String esTimestamp;
    private String error;
    private String planificacion;
    private String esId;
    
    public String getEsId() {
        return esId;
    }

    public void setEsId(String esId) {
        this.esId = esId;
    }

    public String getPlanificacion()
    {
      return this.planificacion;
    }
    
    public void setPlanificacion(String planificacion)
    {
      this.planificacion = planificacion;
    }
    
    public String getId()
    {
      return this.id;
    }
    
    public void setId(String id)
    {
      this.id = id;
    }
    
    public String getHidModif()
    {
      return this.hidModif;
    }
    
    public void setHidModif(String hidModif)
    {
      this.hidModif = hidModif;
    }
    
    public String getRdMetricType()
    {
      return this.rdMetricType;
    }
    
    public void setRdMetricType(String rdMetricType)
    {
      this.rdMetricType = rdMetricType;
    }
    
    public String getSourceId()
    {
      return this.sourceId;
    }
    
    public void setSourceId(String sourceId)
    {
      this.sourceId = sourceId;
    }
    
    public String getSelSourceName()
    {
      return this.selSourceName;
    }
    
    public void setSelSourceName(String selSourceName)
    {
      this.selSourceName = selSourceName;
    }
    
    public String getBatchMetricName()
    {
      return this.batchMetricName;
    }
    
    public void setBatchMetricName(String batchMetricName)
    {
      this.batchMetricName = batchMetricName;
    }
    
    public String getBatchMetricDesc()
    {
      return this.batchMetricDesc;
    }
    
    public void setBatchMetricDesc(String batchMetricDesc)
    {
      this.batchMetricDesc = batchMetricDesc;
    }
    
    public String getTypeQuery()
    {
      return this.typeQuery;
    }
    
    public void setTypeQuery(String typeQuery)
    {
      this.typeQuery = typeQuery;
    }
    
    public String getFromQuery()
    {
      return this.fromQuery;
    }
    
    public void setFromQuery(String fromQuery)
    {
      this.fromQuery = fromQuery;
    }
    
    public String getSelectQuery()
    {
      return this.selectQuery;
    }
    
    public void setSelectQuery(String selectQuery)
    {
      this.selectQuery = selectQuery;
    }
    
    public String getWhereQuery()
    {
      return this.whereQuery;
    }
    
    public void setWhereQuery(String whereQuery)
    {
      this.whereQuery = whereQuery;
    }
    
    public String getEsTimestamp()
    {
      return this.esTimestamp;
    }
    
    public void setEsTimestamp(String esTimestamp)
    {
      this.esTimestamp = esTimestamp;
    }
    
    public String getError()
    {
      return this.error;
    }
    
    public void setError(String error)
    {
      this.error = error;
    }}
