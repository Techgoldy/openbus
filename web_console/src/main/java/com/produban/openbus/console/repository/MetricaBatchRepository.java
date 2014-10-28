package com.produban.openbus.console.repository;
import com.produban.openbus.console.domain.MetricaBatch;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = MetricaBatch.class)
public interface MetricaBatchRepository {
}
