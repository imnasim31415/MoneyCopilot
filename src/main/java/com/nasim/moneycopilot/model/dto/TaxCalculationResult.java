package com.nasim.moneycopilot.model.dto;

import java.math.BigDecimal;
import java.util.List;

public record TaxCalculationResult(
    BigDecimal grossIncome,
    BigDecimal taxFreeThreshold,
    BigDecimal taxableIncome,
    List<SlabBreakdown> slabBreakdowns,
    BigDecimal grossTax,
    BigDecimal allowableInvestment,
    BigDecimal investmentRebate,
    BigDecimal taxAfterRebate,
    BigDecimal minimumTax,
    BigDecimal finalTax
) {

  public static TaxCalculationResult zero(BigDecimal grossIncome, BigDecimal threshold) {
    return new TaxCalculationResult(
        grossIncome,
        threshold,
        BigDecimal.ZERO,
        List.of(),
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO);
  }
}
