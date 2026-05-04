package com.nasim.moneycopilot.repository;

import com.nasim.moneycopilot.model.entity.Expense;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Repository for {@link Expense} entities. */
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

  /** Find all non-deleted expenses for a user, with optional category and date range filters. */
  @Query("""
      SELECT e FROM Expense e
      WHERE e.user.id = :userId
        AND e.deletedAt IS NULL
        AND (:categoryId IS NULL OR e.category.id = :categoryId)
        AND (:from IS NULL OR e.expenseDate >= :from)
        AND (:to IS NULL OR e.expenseDate <= :to)
      """)
  Page<Expense> findAllByFilters(
      @Param("userId") UUID userId,
      @Param("categoryId") Integer categoryId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      Pageable pageable);

  /** Find a single non-deleted expense by id and owner. */
  Optional<Expense> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);
}
