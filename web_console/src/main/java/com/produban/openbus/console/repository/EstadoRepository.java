package com.produban.openbus.console.repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.produban.openbus.console.domain.Estado;

@RooJpaRepository(domainType = Estado.class)
public interface EstadoRepository {
    @Query("select e from Estado as e where e.code = :code")
    @Transactional(readOnly = true)
    List<Estado> findEstadoByCode(@Param("code") String code);
}
