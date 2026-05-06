package com.nasim.moneycopilot.model.dto;

import java.math.BigDecimal;
import java.util.List;

/** Income breakdown by source for a given month. */
public record IncomeBreakdownResponse(
    int year,
    int month,
    BigDecimal totalIncome,
    List<IncomeBreakdownItem> items
) {

  /** Single income-source entry in the breakdown. */
  public record IncomeBreakdownItem(
      int sourceId,
      String sourceName,
      BigDecimal amount,
      BigDecimal percentage
  ) {}
}
