package com.nasim.moneycopilot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.nasim.moneycopilot.model.dto.CategoryBreakdownResponse;
import com.nasim.moneycopilot.model.dto.MonthlySummaryResponse;
import com.nasim.moneycopilot.model.dto.TrendResponse;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.repository.ExpenseRepository;
import com.nasim.moneycopilot.repository.IncomeRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

  @Mock
  private ExpenseRepository expenseRepository;

  @Mock
  private IncomeRepository incomeRepository;

  @InjectMocks
  private DashboardService dashboardService;

  private User user;

  @BeforeEach
  void setUp() {
    user = User.builder().id(UUID.randomUUID()).email("user@example.com").build();
  }

  @Test
  void should_calculateMonthlySummary_when_dataExists() {
    when(incomeRepository.sumByUserAndYearMonth(user.getId(), 2026, 4))
        .thenReturn(new BigDecimal("80000.00"));
    when(expenseRepository.sumByUserAndYearMonth(user.getId(), 2026, 4))
        .thenReturn(new BigDecimal("50000.00"));

    MonthlySummaryResponse result = dashboardService.monthlySummary(user, 2026, 4);

    assertThat(result.totalIncome()).isEqualByComparingTo("80000.00");
    assertThat(result.totalExpenses()).isEqualByComparingTo("50000.00");
    assertThat(result.netSavings()).isEqualByComparingTo("30000.00");
    assertThat(result.savingsRate()).isEqualByComparingTo("37.50");
  }

  @Test
  void should_returnZeroSavingsRate_when_incomeIsZero() {
    when(incomeRepository.sumByUserAndYearMonth(user.getId(), 2026, 4))
        .thenReturn(BigDecimal.ZERO);
    when(expenseRepository.sumByUserAndYearMonth(user.getId(), 2026, 4))
        .thenReturn(new BigDecimal("10000.00"));

    MonthlySummaryResponse result = dashboardService.monthlySummary(user, 2026, 4);

    assertThat(result.savingsRate()).isEqualByComparingTo("0");
    assertThat(result.netSavings()).isEqualByComparingTo("-10000.00");
  }

  @Test
  void should_calculateCategoryBreakdown_with_percentages() {
    List<Object[]> rows = List.of(
        new Object[]{1, "FOOD", new BigDecimal("30000.00")},
        new Object[]{2, "TRANSPORT", new BigDecimal("10000.00")}
    );

    when(expenseRepository.sumByCategoryForUserAndYearMonth(user.getId(), 2026, 4))
        .thenReturn(rows);

    CategoryBreakdownResponse result = dashboardService.categoryBreakdown(user, 2026, 4);

    assertThat(result.totalExpenses()).isEqualByComparingTo("40000.00");
    assertThat(result.items()).hasSize(2);
    assertThat(result.items().get(0).percentage()).isEqualByComparingTo("75.00");
    assertThat(result.items().get(1).percentage()).isEqualByComparingTo("25.00");
  }

  @Test
  void should_returnEmptyBreakdown_when_noData() {
    when(expenseRepository.sumByCategoryForUserAndYearMonth(user.getId(), 2026, 4))
        .thenReturn(List.of());

    CategoryBreakdownResponse result = dashboardService.categoryBreakdown(user, 2026, 4);

    assertThat(result.items()).isEmpty();
    assertThat(result.totalExpenses()).isEqualByComparingTo("0");
  }

  @Test
  void should_returnTrendWithAllMonths_when_noGaps() {
    LocalDate startDate = LocalDate.now().withDayOfMonth(1);
    List<Object[]> incomeRows = Collections.singletonList(
        new Object[]{startDate.getYear(), startDate.getMonthValue(), new BigDecimal("50000.00")});
    List<Object[]> expenseRows = Collections.singletonList(
        new Object[]{startDate.getYear(), startDate.getMonthValue(), new BigDecimal("30000.00")});

    when(incomeRepository.monthlyTotals(user.getId(), startDate)).thenReturn(incomeRows);
    when(expenseRepository.monthlyTotals(user.getId(), startDate)).thenReturn(expenseRows);

    TrendResponse result = dashboardService.trends(user, 1);

    assertThat(result.months()).hasSize(1);
    assertThat(result.months().get(0).income()).isEqualByComparingTo("50000.00");
    assertThat(result.months().get(0).expenses()).isEqualByComparingTo("30000.00");
    assertThat(result.months().get(0).savings()).isEqualByComparingTo("20000.00");
  }
}
