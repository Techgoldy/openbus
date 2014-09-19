package com.produban.openbus.console.domain;
import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.Transient;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity
@RooSerializable
public class QueryCep implements Serializable{

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String queryFinal;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String queryDefinition;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String outputFieldUser;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String outputFieldNames;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String outputStream;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String groupBy;

    /**
     */
    private String queryCepId;

    /**
     */
    private String queryName;

    /**
     */
    @Column(columnDefinition = "BIT")
    private Boolean toRemove;

    /**
     */
    @Column(columnDefinition = "BIT")
    private Boolean hasCallback;
    
}
