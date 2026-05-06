package com.nasim.moneycopilot.repository;

import com.nasim.moneycopilot.model.entity.TaxThreshold;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxThresholdRepository extends JpaRepository<TaxThreshold, Integer> {

  List<TaxThreshold> findByConfigId(Integer configId);
}
