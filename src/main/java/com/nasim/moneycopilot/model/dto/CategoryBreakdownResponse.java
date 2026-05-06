package com.nasim.moneycopilot.model.dto;

import java.math.BigDecimal;
import java.util.List;

/** Expense breakdown by category for a given month. */
public record CategoryBreakdownResponse(
    int year,
    int month,
    BigDecimal totalExpenses,
    List<CategoryBreakdownItem> items
) {

  /** Single category entry in the breakdown. */
  public record CategoryBreakdownItem(
      int categoryId,
      String categoryName,
      BigDecimal amount,
      BigDecimal percentage
  ) {}
}
