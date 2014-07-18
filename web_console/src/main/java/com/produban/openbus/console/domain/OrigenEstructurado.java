package com.produban.openbus.console.domain;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity
@RooSerializable
public class OrigenEstructurado {

    /**
     */
    private String topologyName;

    /**
     */
    private String kafkaTopic;

    /**
     */
    @Column(columnDefinition = "BIT")
    private Boolean isKafkaOnline;

    /**
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<CamposOrigen> hsCamposOrigen = new HashSet<CamposOrigen>();
}
