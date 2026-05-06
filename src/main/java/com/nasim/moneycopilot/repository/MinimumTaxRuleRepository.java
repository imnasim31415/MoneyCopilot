package com.nasim.moneycopilot.repository;

import com.nasim.moneycopilot.model.entity.MinimumTaxRule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MinimumTaxRuleRepository extends JpaRepository<MinimumTaxRule, Integer> {

  List<MinimumTaxRule> findByConfigId(Integer configId);
}
