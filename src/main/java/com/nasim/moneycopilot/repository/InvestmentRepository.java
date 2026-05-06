package com.nasim.moneycopilot.repository;

import com.nasim.moneycopilot.model.entity.Investment;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvestmentRepository extends JpaRepository<Investment, UUID> {

  List<Investment> findByUserIdAndFiscalYearAndDeletedAtIsNull(UUID userId, String fiscalYear);

  Optional<Investment> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

  @Query("""
      SELECT COALESCE(SUM(i.amount), 0) FROM Investment i
      WHERE i.user.id = :userId
        AND i.fiscalYear = :fiscalYear
        AND i.deletedAt IS NULL
      """)
  BigDecimal sumByUserAndFiscalYear(
      @Param("userId") UUID userId,
      @Param("fiscalYear") String fiscalYear);
}
