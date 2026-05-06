package com.nasim.moneycopilot.model.dto;

import java.math.BigDecimal;

/** Monthly financial summary: income, expenses, savings, and savings rate. */
public record MonthlySummaryResponse(
    int year,
    int month,
    BigDecimal totalIncome,
    BigDecimal totalExpenses,
    BigDecimal netSavings,
    BigDecimal savingsRate
) {}
