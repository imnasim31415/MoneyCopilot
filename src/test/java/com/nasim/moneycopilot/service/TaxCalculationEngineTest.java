package com.nasim.moneycopilot.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.nasim.moneycopilot.model.dto.TaxCalculationResult;
import com.nasim.moneycopilot.model.entity.MinimumTaxRule;
import com.nasim.moneycopilot.model.entity.TaxConfiguration;
import com.nasim.moneycopilot.model.entity.TaxSlab;
import com.nasim.moneycopilot.model.entity.TaxThreshold;
import com.nasim.moneycopilot.model.enums.LocationType;
import com.nasim.moneycopilot.model.enums.TaxpayerCategory;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TaxCalculationEngineTest {

  // AY 2025-2026 config fixture
  private static TaxConfiguration config;
  private static List<TaxThreshold> thresholds;
  private static List<TaxSlab> slabs;
  private static List<MinimumTaxRule> minimumRules;

  @BeforeAll
  static void setUpConfig() {
    config = TaxConfiguration.builder().id(1).assessmentYear("2025-2026").active(true).build();

    thresholds = List.of(
        threshold(TaxpayerCategory.GENERAL_MALE,    "350000"),
        threshold(TaxpayerCategory.FEMALE,          "400000"),
        threshold(TaxpayerCategory.SENIOR_CITIZEN,  "400000"),
        threshold(TaxpayerCategory.DISABLED,        "475000"),
        threshold(TaxpayerCategory.THIRD_GENDER,    "475000"),
        threshold(TaxpayerCategory.FREEDOM_FIGHTER, "500000")
    );

    slabs = List.of(
        slab(1, "100000",  "5"),
        slab(2, "400000", "10"),
        slab(3, "500000", "15"),
        slab(4, "500000", "20"),
        slab(5, "2000000","25"),
        slab(6, null,     "30")
    );

    minimumRules = List.of(
        minRule(LocationType.DHAKA_CHATTOGRAM, "5000", false),
        minRule(LocationType.OTHER_CITY_CORP,  "4000", false),
        minRule(LocationType.NON_CITY,         "3000", false)
    );
  }

  @Test
  void should_returnZeroTax_when_incomeAtOrBelowThreshold() {
    TaxCalculationResult result = calculate("350000", TaxpayerCategory.GENERAL_MALE);

    assertThat(result.finalTax()).isEqualByComparingTo("0.00");
    assertThat(result.taxableIncome()).isEqualByComparingTo("0.00");
  }

  @Test
  void should_applyFirstSlabOnly_when_taxableIncomeWithinFirstSlab() {
    // taxable = 450000 - 350000 = 100000 → 5% → 5000
    TaxCalculationResult result = calculate("450000", TaxpayerCategory.GENERAL_MALE);

    assertThat(result.taxableIncome()).isEqualByComparingTo("100000.00");
    assertThat(result.grossTax()).isEqualByComparingTo("5000.00");
    assertThat(result.slabBreakdowns()).hasSize(1);
    assertThat(result.slabBreakdowns().get(0).ratePercentage()).isEqualByComparingTo("5");
  }

  @Test
  void should_applyTwoSlabs_when_taxableIncomeSpansFirstTwoSlabs() {
    // taxable = 800000 - 350000 = 450000
    // slab1: 100000 @ 5% = 5000
    // slab2: 350000 @ 10% = 35000
    // gross = 40000
    TaxCalculationResult result = calculate("800000", TaxpayerCategory.GENERAL_MALE);

    assertThat(result.grossTax()).isEqualByComparingTo("40000.00");
    assertThat(result.slabBreakdowns()).hasSize(2);
  }

  @Test
  void should_applyHigherThreshold_forFemale() {
    // Female threshold = 400000, same income 450000 → taxable = 50000 → 5% → 2500
    TaxCalculationResult result = calculate("450000", TaxpayerCategory.FEMALE);

    assertThat(result.taxFreeThreshold()).isEqualByComparingTo("400000.00");
    assertThat(result.taxableIncome()).isEqualByComparingTo("50000.00");
    assertThat(result.grossTax()).isEqualByComparingTo("2500.00");
  }

  @Test
  void should_addDisabledChildAllowance_on_topOfThreshold() {
    // GENERAL_MALE: 350000 + 50000 disabled-child = 400000 threshold
    // income 450000 → taxable = 50000 → 5% = 2500
    TaxCalculationResult result = TaxCalculationEngine.calculate(
        new BigDecimal("450000"),
        TaxpayerCategory.GENERAL_MALE,
        LocationType.DHAKA_CHATTOGRAM,
        false,
        BigDecimal.ZERO,
        true,
        thresholds, slabs, minimumRules);

    assertThat(result.taxFreeThreshold()).isEqualByComparingTo("400000.00");
    assertThat(result.grossTax()).isEqualByComparingTo("2500.00");
  }

  @Test
  void should_applyInvestmentRebate_correctly() {
    // Income 1000000, no threshold overshoot for GENERAL_MALE
    // taxable = 650000; gross tax = 100000*5% + 400000*10% + 150000*15% = 5000+40000+22500 = 67500
    // investment = 200000
    // allowance = min(200000, 1000000*25%=250000, ceiling 10M) = 200000
    // rebate = 200000 * 15% = 30000
    // after rebate = 67500 - 30000 = 37500
    // minimum tax (Dhaka, not new) = 5000 → final = max(37500, 5000) = 37500
    TaxCalculationResult result = TaxCalculationEngine.calculate(
        new BigDecimal("1000000"),
        TaxpayerCategory.GENERAL_MALE,
        LocationType.DHAKA_CHATTOGRAM,
        false,
        new BigDecimal("200000"),
        false,
        thresholds, slabs, minimumRules);

    assertThat(result.allowableInvestment()).isEqualByComparingTo("200000.00");
    assertThat(result.investmentRebate()).isEqualByComparingTo("30000.00");
    assertThat(result.taxAfterRebate()).isEqualByComparingTo("37500.00");
    assertThat(result.finalTax()).isEqualByComparingTo("37500.00");
  }

  @Test
  void should_capInvestmentAllowance_at25PercentOfIncome() {
    // Income 400000; investment claimed = 200000
    // allowance = min(200000, 400000*25%=100000, 10M) = 100000
    // taxable = 50000 → gross = 2500
    // rebate = 100000*15% = 15000 → but gross is only 2500 → taxAfterRebate = 0
    // min tax Dhaka = 5000 → final = max(0, 5000) = 5000
    TaxCalculationResult result = TaxCalculationEngine.calculate(
        new BigDecimal("400000"),
        TaxpayerCategory.GENERAL_MALE,
        LocationType.DHAKA_CHATTOGRAM,
        false,
        new BigDecimal("200000"),
        false,
        thresholds, slabs, minimumRules);

    assertThat(result.allowableInvestment()).isEqualByComparingTo("100000.00");
    assertThat(result.taxAfterRebate()).isEqualByComparingTo("0.00");
    assertThat(result.finalTax()).isEqualByComparingTo("5000.00");
  }

  @Test
  void should_applyMinimumTax_when_calculatedTaxIsLower() {
    // Just barely above threshold → tiny tax → minimum tax kicks in
    // income 360000, taxable 10000 → gross 500 → rebate 0 → 500 < 5000 min → final = 5000
    TaxCalculationResult result = calculate("360000", TaxpayerCategory.GENERAL_MALE);

    assertThat(result.minimumTax()).isEqualByComparingTo("5000.00");
    assertThat(result.finalTax()).isEqualByComparingTo("5000.00");
  }

  @Test
  void should_useLowerMinimumTax_forNonCityLocation() {
    // NON_CITY minimum = 3000
    TaxCalculationResult result = TaxCalculationEngine.calculate(
        new BigDecimal("360000"),
        TaxpayerCategory.GENERAL_MALE,
        LocationType.NON_CITY,
        false,
        BigDecimal.ZERO,
        false,
        thresholds, slabs, minimumRules);

    assertThat(result.minimumTax()).isEqualByComparingTo("3000.00");
    assertThat(result.finalTax()).isEqualByComparingTo("3000.00");
  }

  @Test
  void should_spanAllSlabs_forHighIncome() {
    // Income 10,000,000 for GENERAL_MALE
    // taxable = 9,650,000
    // slab1: 100000*5% = 5000
    // slab2: 400000*10% = 40000
    // slab3: 500000*15% = 75000
    // slab4: 500000*20% = 100000
    // slab5: 2000000*25% = 500000
    // slab6: (9650000-3500000)=6150000*30% = 1845000
    // gross = 5000+40000+75000+100000+500000+1845000 = 2565000
    TaxCalculationResult result = calculate("10000000", TaxpayerCategory.GENERAL_MALE);

    assertThat(result.slabBreakdowns()).hasSize(6);
    assertThat(result.grossTax()).isEqualByComparingTo("2565000.00");
  }

  @Test
  void should_returnZeroTax_forFreedomFighterAtThreshold() {
    TaxCalculationResult result = calculate("500000", TaxpayerCategory.FREEDOM_FIGHTER);
    assertThat(result.finalTax()).isEqualByComparingTo("0.00");
  }

  // ── helpers ──────────────────────────────────────────────────────────────

  private static TaxCalculationResult calculate(String income, TaxpayerCategory category) {
    return TaxCalculationEngine.calculate(
        new BigDecimal(income),
        category,
        LocationType.DHAKA_CHATTOGRAM,
        false,
        BigDecimal.ZERO,
        false,
        thresholds, slabs, minimumRules);
  }

  private static TaxThreshold threshold(TaxpayerCategory cat, String amount) {
    return TaxThreshold.builder()
        .config(config)
        .taxpayerCategory(cat)
        .thresholdAmount(new BigDecimal(amount))
        .build();
  }

  private static TaxSlab slab(int order, String upTo, String rate) {
    return TaxSlab.builder()
        .config(config)
        .slabOrder(order)
        .incomeUpTo(upTo == null ? null : new BigDecimal(upTo))
        .ratePercentage(new BigDecimal(rate))
        .build();
  }

  private static MinimumTaxRule minRule(LocationType loc, String amount, boolean isNew) {
    return MinimumTaxRule.builder()
        .config(config)
        .locationType(loc)
        .minimumAmount(new BigDecimal(amount))
        .newTaxpayer(isNew)
        .build();
  }
}
