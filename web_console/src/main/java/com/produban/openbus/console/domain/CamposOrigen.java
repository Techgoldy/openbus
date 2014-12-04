package com.produban.openbus.console.domain;
import java.io.Serializable;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RooJavaBean
@RooToString
@RooJpaEntity
@RooSerializable
@JsonIgnoreProperties("origenEstructurado")
public class CamposOrigen implements Serializable {

    /**
     */
    private String nombreCampo;

    /**
     */
    private String tipoCampo;

    /**
     */
    private Long ordenEnTabla;
    
    /**
     */
    @ManyToOne(fetch=FetchType.LAZY)
    private OrigenEstructurado origenEstructurado;
}
