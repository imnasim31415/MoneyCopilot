package com.nasim.moneycopilot.service;

import com.nasim.moneycopilot.model.dto.CategoryBreakdownResponse;
import com.nasim.moneycopilot.model.dto.CategoryBreakdownResponse.CategoryBreakdownItem;
import com.nasim.moneycopilot.model.dto.IncomeBreakdownResponse;
import com.nasim.moneycopilot.model.dto.IncomeBreakdownResponse.IncomeBreakdownItem;
import com.nasim.moneycopilot.model.dto.MonthlySummaryResponse;
import com.nasim.moneycopilot.model.dto.TrendResponse;
import com.nasim.moneycopilot.model.dto.TrendResponse.MonthlyTrendItem;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.repository.ExpenseRepository;
import com.nasim.moneycopilot.repository.IncomeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Aggregation queries backing the financial dashboard. */
@Service
@RequiredArgsConstructor
public class DashboardService {

  private final ExpenseRepository expenseRepository;
  private final IncomeRepository incomeRepository;

  /** Monthly summary: total income, expenses, net savings, savings rate. */
  @Transactional(readOnly = true)
  public MonthlySummaryResponse monthlySummary(User currentUser, int year, int month) {
    BigDecimal totalIncome = incomeRepository
        .sumByUserAndYearMonth(currentUser.getId(), year, month);
    BigDecimal totalExpenses = expenseRepository
        .sumByUserAndYearMonth(currentUser.getId(), year, month);

    BigDecimal netSavings = totalIncome.subtract(totalExpenses);
    BigDecimal savingsRate = totalIncome.compareTo(BigDecimal.ZERO) > 0
        ? netSavings.divide(totalIncome, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
        : BigDecimal.ZERO;

    return new MonthlySummaryResponse(year, month, totalIncome, totalExpenses,
        netSavings, savingsRate);
  }

  /** Expense breakdown by category for a given month. */
  @Transactional(readOnly = true)
  public CategoryBreakdownResponse categoryBreakdown(User currentUser, int year, int month) {
    List<Object[]> rows = expenseRepository
        .sumByCategoryForUserAndYearMonth(currentUser.getId(), year, month);

    BigDecimal total = rows.stream()
        .map(r -> (BigDecimal) r[2])
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    List<CategoryBreakdownItem> items = rows.stream()
        .map(r -> new CategoryBreakdownItem(
            (Integer) r[0],
            (String) r[1],
            (BigDecimal) r[2],
            percentage((BigDecimal) r[2], total)
        ))
        .toList();

    return new CategoryBreakdownResponse(year, month, total, items);
  }

  /** Income breakdown by source for a given month. */
  @Transactional(readOnly = true)
  public IncomeBreakdownResponse incomeBreakdown(User currentUser, int year, int month) {
    List<Object[]> rows = incomeRepository
        .sumBySourceForUserAndYearMonth(currentUser.getId(), year, month);

    BigDecimal total = rows.stream()
        .map(r -> (BigDecimal) r[2])
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    List<IncomeBreakdownItem> items = rows.stream()
        .map(r -> new IncomeBreakdownItem(
            (Integer) r[0],
            (String) r[1],
            (BigDecimal) r[2],
            percentage((BigDecimal) r[2], total)
        ))
        .toList();

    return new IncomeBreakdownResponse(year, month, total, items);
  }

  /** Monthly income/expense/savings trend for the last {@code months} months. */
  @Transactional(readOnly = true)
  public TrendResponse trends(User currentUser, int months) {
    LocalDate startDate = YearMonth.now().minusMonths(months - 1L).atDay(1);

    List<Object[]> incomeRows = incomeRepository.monthlyTotals(currentUser.getId(), startDate);
    List<Object[]> expenseRows = expenseRepository.monthlyTotals(currentUser.getId(), startDate);

    Map<String, BigDecimal> incomeMap = toYearMonthMap(incomeRows);
    Map<String, BigDecimal> expenseMap = toYearMonthMap(expenseRows);

    List<MonthlyTrendItem> items = new ArrayList<>();
    YearMonth current = YearMonth.now();
    YearMonth start = YearMonth.now().minusMonths(months - 1L);

    for (YearMonth ym = start; !ym.isAfter(current); ym = ym.plusMonths(1)) {
      String key = ym.getYear() + "-" + ym.getMonthValue();
      BigDecimal income = incomeMap.getOrDefault(key, BigDecimal.ZERO);
      BigDecimal expense = expenseMap.getOrDefault(key, BigDecimal.ZERO);
      items.add(new MonthlyTrendItem(ym.getYear(), ym.getMonthValue(), income, expense,
          income.subtract(expense)));
    }

    return new TrendResponse(items);
  }

  private BigDecimal percentage(BigDecimal part, BigDecimal total) {
    if (total.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    return part.divide(total, 4, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
  }

  private Map<String, BigDecimal> toYearMonthMap(List<Object[]> rows) {
    Map<String, BigDecimal> map = new HashMap<>();
    for (Object[] row : rows) {
      int yr = ((Number) row[0]).intValue();
      int mo = ((Number) row[1]).intValue();
      map.put(yr + "-" + mo, (BigDecimal) row[2]);
    }
    return map;
  }
}
