package com.nasim.moneycopilot.model.dto;

import com.nasim.moneycopilot.model.entity.Expense;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Response body for a single expense. */
public record ExpenseResponse(
    UUID id,
    BigDecimal amount,
    Integer categoryId,
    String categoryName,
    LocalDate expenseDate,
    String description,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {

  /** Map an {@link Expense} entity to this response record. */
  public static ExpenseResponse from(Expense expense) {
    return new ExpenseResponse(
        expense.getId(),
        expense.getAmount(),
        expense.getCategory().getId(),
        expense.getCategory().getName(),
        expense.getExpenseDate(),
        expense.getDescription(),
        expense.getCreatedAt(),
        expense.getUpdatedAt()
    );
  }
}
