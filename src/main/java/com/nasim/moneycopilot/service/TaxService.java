package com.nasim.moneycopilot.service;

import com.nasim.moneycopilot.model.dto.TaxCalculationResult;
import com.nasim.moneycopilot.model.entity.MinimumTaxRule;
import com.nasim.moneycopilot.model.entity.TaxConfiguration;
import com.nasim.moneycopilot.model.entity.TaxSlab;
import com.nasim.moneycopilot.model.entity.TaxThreshold;
import com.nasim.moneycopilot.model.entity.TaxpayerProfile;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.model.enums.LocationType;
import com.nasim.moneycopilot.model.enums.TaxpayerCategory;
import com.nasim.moneycopilot.repository.InvestmentRepository;
import com.nasim.moneycopilot.repository.MinimumTaxRuleRepository;
import com.nasim.moneycopilot.repository.TaxConfigurationRepository;
import com.nasim.moneycopilot.repository.TaxSlabRepository;
import com.nasim.moneycopilot.repository.TaxThresholdRepository;
import com.nasim.moneycopilot.repository.TaxpayerProfileRepository;
import com.nasim.moneycopilot.repository.IncomeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaxService {

  private final TaxConfigurationRepository configRepository;
  private final TaxThresholdRepository thresholdRepository;
  private final TaxSlabRepository slabRepository;
  private final MinimumTaxRuleRepository minimumTaxRuleRepository;
  private final TaxpayerProfileRepository profileRepository;
  private final InvestmentRepository investmentRepository;
  private final IncomeRepository incomeRepository;

  /**
   * Calculate tax for the current user based on their profile and data for the given fiscal year.
   *
   * @param fiscalYear  e.g. "2024-2025"
   * @param isNewTaxpayer whether this is the user's first return
   * @param user        the authenticated user
   */
  @Transactional(readOnly = true)
  public TaxCalculationResult calculate(String fiscalYear, boolean isNewTaxpayer, User user) {
    TaxConfiguration config = configRepository.findByActiveTrue()
        .orElseThrow(() -> new IllegalStateException("No active tax configuration found"));

    List<TaxThreshold> thresholds = thresholdRepository.findByConfigId(config.getId());
    List<TaxSlab> slabs = slabRepository.findByConfigIdOrderBySlabOrderAsc(config.getId());
    List<MinimumTaxRule> minimumRules = minimumTaxRuleRepository.findByConfigId(config.getId());

    TaxpayerProfile profile = profileRepository.findByUserId(user.getId()).orElse(null);
    TaxpayerCategory category = profile != null ? profile.getCategory() : TaxpayerCategory.GENERAL_MALE;
    LocationType locationType = profile != null ? profile.getLocationType() : LocationType.DHAKA_CHATTOGRAM;
    boolean hasDisabledChild = profile != null && profile.isHasDisabledChild();

    // Fiscal year "2024-2025" → Jul 1 2024 – Jun 30 2025
    LocalDate from = fiscalYearStart(fiscalYear);
    LocalDate to = fiscalYearEnd(fiscalYear);

    BigDecimal grossIncome = incomeRepository.sumByUserAndDateRange(user.getId(), from, to);
    BigDecimal totalInvestments = investmentRepository.sumByUserAndFiscalYear(user.getId(), fiscalYear);

    return TaxCalculationEngine.calculate(
        grossIncome,
        category,
        locationType,
        isNewTaxpayer,
        totalInvestments,
        hasDisabledChild,
        thresholds,
        slabs,
        minimumRules);
  }

  private static LocalDate fiscalYearStart(String fiscalYear) {
    int startYear = Integer.parseInt(fiscalYear.substring(0, 4));
    return LocalDate.of(startYear, 7, 1);
  }

  private static LocalDate fiscalYearEnd(String fiscalYear) {
    int endYear = Integer.parseInt(fiscalYear.substring(5, 9));
    return LocalDate.of(endYear, 6, 30);
  }
}
