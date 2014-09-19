package com.produban.openbus.console.domain;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity
@RooSerializable
public class MetricaOnLine implements Serializable{

    /**
     */
    private String onLineMetricName;

    /**
     */
    private String onLineMetricDesc;

    /**
     */
    private String esIndex;

    /**
     */
    private String esType;

    /**
     */
    private String esCamposId;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fechaCreacion;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fechaUltModif;

    /**
     */
    private String usuarioCreacion;

    /**
     */
    private String usuarioModificacion;

    /**
     */
    @Column(columnDefinition = "BIT")
    private Boolean isCreated;

    /**
     */
    @Column(columnDefinition = "BIT")
    private Boolean isUpdated;

    /**
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<QueryCep> hsQueryCep = new HashSet<QueryCep>();

    /**
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private StreamCep streamCep;
}
