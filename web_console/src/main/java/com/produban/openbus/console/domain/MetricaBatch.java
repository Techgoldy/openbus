package com.produban.openbus.console.domain;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;
import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@RooJpaEntity
@RooSerializable
public class MetricaBatch {

    /**
     */
    private String batchMetricName;

    /**
     */
    private String batchMetricDesc;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String queryCode;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String createCode;

    /**
     */
    private String planificacion;

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
    private String typeQuery;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String selectQuery;

    /**
     */
    private String FromQuery;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String WhereQuery;

    /**
     */
    private String sourceId;

    /**
     */
    private String esTimestamp;

    /**
     */
    @Column(columnDefinition = "BIT")
    private Boolean isBatch;

    /**
     */
    private String estado;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String error;

    /**
     */
    private String esId;
}
