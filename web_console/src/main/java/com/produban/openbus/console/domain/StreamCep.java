package com.produban.openbus.console.domain;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.ManyToMany;

@RooJavaBean
@RooToString
@RooJpaEntity
@RooSerializable
public class StreamCep implements Serializable {

    /**
     */
    private String streamCepId;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String streamFields;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String streamFinal;

    /**
     */
    @Column(columnDefinition = "BIT")
    private Boolean toRemove;

    /**
     */
    private String streamName;

    /**
     */
    @ManyToOne
    private OrigenEstructurado origenEstructurado;
    
    @Column(columnDefinition = "BIT")
    private Boolean toUpdateCep;
    
    @Column(columnDefinition = "LONGBLOB")
    private String error; 

 }
