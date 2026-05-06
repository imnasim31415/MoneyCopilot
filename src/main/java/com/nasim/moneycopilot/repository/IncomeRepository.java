package com.nasim.moneycopilot.repository;

import com.nasim.moneycopilot.model.entity.Income;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Repository for {@link Income} entities. */
public interface IncomeRepository extends JpaRepository<Income, UUID> {

  /** Find all non-deleted incomes for a user, with optional source and date range filters. */
  @Query("""
      SELECT i FROM Income i
      WHERE i.user.id = :userId
        AND i.deletedAt IS NULL
        AND (:sourceId IS NULL OR i.source.id = :sourceId)
        AND (:from IS NULL OR i.incomeDate >= :from)
        AND (:to IS NULL OR i.incomeDate <= :to)
      """)
  Page<Income> findAllByFilters(
      @Param("userId") UUID userId,
      @Param("sourceId") Integer sourceId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      Pageable pageable);

  /** Find a single non-deleted income by id and owner. */
  Optional<Income> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

  /** Sum of all non-deleted income for a user in a specific year/month. */
  @Query("""
      SELECT COALESCE(SUM(i.amount), 0) FROM Income i
      WHERE i.user.id = :userId
        AND i.deletedAt IS NULL
        AND YEAR(i.incomeDate) = :year
        AND MONTH(i.incomeDate) = :month
      """)
  BigDecimal sumByUserAndYearMonth(
      @Param("userId") UUID userId,
      @Param("year") int year,
      @Param("month") int month);

  /** Income grouped by source for a user in a specific year/month. */
  @Query("""
      SELECT i.source.id, i.source.name, SUM(i.amount)
      FROM Income i
      WHERE i.user.id = :userId
        AND i.deletedAt IS NULL
        AND YEAR(i.incomeDate) = :year
        AND MONTH(i.incomeDate) = :month
      GROUP BY i.source.id, i.source.name
      ORDER BY SUM(i.amount) DESC
      """)
  List<Object[]> sumBySourceForUserAndYearMonth(
      @Param("userId") UUID userId,
      @Param("year") int year,
      @Param("month") int month);

  /** Monthly income totals for a user from a start date onward. */
  @Query("""
      SELECT YEAR(i.incomeDate), MONTH(i.incomeDate), SUM(i.amount)
      FROM Income i
      WHERE i.user.id = :userId
        AND i.deletedAt IS NULL
        AND i.incomeDate >= :startDate
      GROUP BY YEAR(i.incomeDate), MONTH(i.incomeDate)
      ORDER BY YEAR(i.incomeDate), MONTH(i.incomeDate)
      """)
  List<Object[]> monthlyTotals(
      @Param("userId") UUID userId,
      @Param("startDate") LocalDate startDate);
}
