package com.produban.openbus.console.repository;
import com.produban.openbus.console.domain.QueryCep;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = QueryCep.class)
public interface QueryCepRepository {
}
