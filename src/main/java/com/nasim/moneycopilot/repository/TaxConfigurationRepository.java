package com.nasim.moneycopilot.repository;

import com.nasim.moneycopilot.model.entity.TaxConfiguration;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxConfigurationRepository extends JpaRepository<TaxConfiguration, Integer> {

  Optional<TaxConfiguration> findByActiveTrue();
}
