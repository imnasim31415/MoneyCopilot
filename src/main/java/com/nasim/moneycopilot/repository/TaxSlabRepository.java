package com.nasim.moneycopilot.repository;

import com.nasim.moneycopilot.model.entity.TaxSlab;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxSlabRepository extends JpaRepository<TaxSlab, Integer> {

  List<TaxSlab> findByConfigIdOrderBySlabOrderAsc(Integer configId);
}
