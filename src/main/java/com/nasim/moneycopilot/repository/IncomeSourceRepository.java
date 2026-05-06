package com.nasim.moneycopilot.repository;

import com.nasim.moneycopilot.model.entity.IncomeSource;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for {@link IncomeSource} reference data. */
public interface IncomeSourceRepository extends JpaRepository<IncomeSource, Integer> {}
