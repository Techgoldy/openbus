package com.produban.openbus.console.domain;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity
@RooSerializable
public class TableCep implements Serializable {

    /**
     */
    private String tableCepId;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String tableCepFields;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String tableCepFinal;

    /**
     */
    private String tableCepName;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String error;

    /**
     */
    private Integer versionMetadata;

    /**
     */
    @ManyToOne
    private Estado estado;

}
