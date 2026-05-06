package com.nasim.moneycopilot.model.dto;

import com.nasim.moneycopilot.model.entity.Income;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Response body for a single income record. */
public record IncomeResponse(
    UUID id,
    BigDecimal amount,
    Integer sourceId,
    String sourceName,
    LocalDate incomeDate,
    String description,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {

  /** Map an {@link Income} entity to this response record. */
  public static IncomeResponse from(Income income) {
    return new IncomeResponse(
        income.getId(),
        income.getAmount(),
        income.getSource().getId(),
        income.getSource().getName(),
        income.getIncomeDate(),
        income.getDescription(),
        income.getCreatedAt(),
        income.getUpdatedAt()
    );
  }
}
