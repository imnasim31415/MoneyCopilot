package com.nasim.moneycopilot.model.dto;

import java.math.BigDecimal;
import java.util.List;

/** Monthly trend data over the last N months. */
public record TrendResponse(List<MonthlyTrendItem> months) {

  /** Income, expenses, and savings for a single month. */
  public record MonthlyTrendItem(
      int year,
      int month,
      BigDecimal income,
      BigDecimal expenses,
      BigDecimal savings
  ) {}
}
