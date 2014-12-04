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
    private String streamName;

    /**
     */
    @ManyToOne
    private OrigenEstructurado origenEstructurado;

    /**
     */
    @ManyToOne
    private Estado estado;

    /**
     */
    private Integer versionMetadata;

    /**
     */
    @Column(columnDefinition = "LONGBLOB")
    private String error;
}
