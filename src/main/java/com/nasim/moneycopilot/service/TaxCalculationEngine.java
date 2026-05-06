package com.nasim.moneycopilot.service;

import com.nasim.moneycopilot.model.dto.SlabBreakdown;
import com.nasim.moneycopilot.model.dto.TaxCalculationResult;
import com.nasim.moneycopilot.model.entity.MinimumTaxRule;
import com.nasim.moneycopilot.model.entity.TaxSlab;
import com.nasim.moneycopilot.model.entity.TaxThreshold;
import com.nasim.moneycopilot.model.enums.LocationType;
import com.nasim.moneycopilot.model.enums.TaxpayerCategory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Pure, stateless tax calculation engine for Bangladesh income tax.
 * No Spring dependencies — all inputs are explicit parameters.
 */
public final class TaxCalculationEngine {

  private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

  // Investment rebate: 15% of (the lesser of actual investment, 25% of AIT, or 10M BDT ceiling)
  private static final BigDecimal REBATE_RATE = new BigDecimal("0.15");
  private static final BigDecimal INVESTMENT_ALLOWANCE_RATE = new BigDecimal("0.25");
  private static final BigDecimal INVESTMENT_CEILING = new BigDecimal("10000000");

  // Disabled-child additional allowance per child (simplified: flat BDT 50,000 per profile)
  private static final BigDecimal DISABLED_CHILD_ALLOWANCE = new BigDecimal("50000");

  private TaxCalculationEngine() {}

  /**
   * Calculate tax liability.
   *
   * @param grossIncome       total annual income
   * @param category          taxpayer category
   * @param locationType      residential location
   * @param isNewTaxpayer     whether this is first-time taxpayer
   * @param totalInvestments  sum of eligible investment amounts
   * @param hasDisabledChild  whether taxpayer has a disabled child
   * @param thresholds        list of TaxThreshold rows for the active config
   * @param slabs             list of TaxSlab rows ordered by slab_order ASC
   * @param minimumRules      list of MinimumTaxRule rows for the active config
   */
  public static TaxCalculationResult calculate(
      BigDecimal grossIncome,
      TaxpayerCategory category,
      LocationType locationType,
      boolean isNewTaxpayer,
      BigDecimal totalInvestments,
      boolean hasDisabledChild,
      List<TaxThreshold> thresholds,
      List<TaxSlab> slabs,
      List<MinimumTaxRule> minimumRules) {

    // 1. Resolve tax-free threshold
    Map<TaxpayerCategory, BigDecimal> thresholdMap = thresholds.stream()
        .collect(Collectors.toMap(TaxThreshold::getTaxpayerCategory, TaxThreshold::getThresholdAmount));

    BigDecimal threshold = thresholdMap.getOrDefault(category, BigDecimal.ZERO);

    // Disabled-child allowance stacks on top of the threshold
    if (hasDisabledChild) {
      threshold = threshold.add(DISABLED_CHILD_ALLOWANCE);
    }

    BigDecimal taxableIncome = grossIncome.subtract(threshold);
    if (taxableIncome.compareTo(BigDecimal.ZERO) <= 0) {
      return TaxCalculationResult.zero(grossIncome, threshold);
    }

    // 2. Apply progressive slabs
    List<SlabBreakdown> breakdowns = new ArrayList<>();
    BigDecimal remaining = taxableIncome;
    BigDecimal grossTax = BigDecimal.ZERO;

    for (TaxSlab slab : slabs) {
      if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

      BigDecimal slabSize = slab.getIncomeUpTo(); // null means unbounded
      BigDecimal chunk = (slabSize == null) ? remaining : remaining.min(slabSize);

      BigDecimal slabTax = chunk
          .multiply(slab.getRatePercentage())
          .divide(HUNDRED, 2, RoundingMode.HALF_UP);

      breakdowns.add(new SlabBreakdown(
          slab.getSlabOrder(),
          slab.getRatePercentage(),
          chunk,
          slabTax));

      grossTax = grossTax.add(slabTax);
      remaining = remaining.subtract(chunk);
    }

    // 3. Investment tax rebate
    BigDecimal investmentBase = grossIncome.multiply(INVESTMENT_ALLOWANCE_RATE);
    BigDecimal allowableInvestment = totalInvestments
        .min(investmentBase)
        .min(INVESTMENT_CEILING);
    BigDecimal rebate = allowableInvestment.multiply(REBATE_RATE).setScale(2, RoundingMode.HALF_UP);

    BigDecimal taxAfterRebate = grossTax.subtract(rebate).max(BigDecimal.ZERO);

    // 4. Minimum tax rule
    BigDecimal minimumTax = resolveMinimumTax(locationType, isNewTaxpayer, minimumRules);

    // If income > threshold, minimum tax is a floor on the final liability
    BigDecimal finalTax = taxAfterRebate.max(minimumTax).setScale(2, RoundingMode.HALF_UP);

    return new TaxCalculationResult(
        grossIncome,
        threshold,
        taxableIncome.setScale(2, RoundingMode.HALF_UP),
        breakdowns,
        grossTax.setScale(2, RoundingMode.HALF_UP),
        allowableInvestment.setScale(2, RoundingMode.HALF_UP),
        rebate,
        taxAfterRebate.setScale(2, RoundingMode.HALF_UP),
        minimumTax,
        finalTax);
  }

  private static BigDecimal resolveMinimumTax(
      LocationType locationType,
      boolean isNewTaxpayer,
      List<MinimumTaxRule> rules) {

    return rules.stream()
        .filter(r -> r.getLocationType() == locationType && r.isNewTaxpayer() == isNewTaxpayer)
        .map(MinimumTaxRule::getMinimumAmount)
        .findFirst()
        .orElse(BigDecimal.ZERO);
  }
}
