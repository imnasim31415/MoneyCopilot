package com.nasim.moneycopilot.controller;

import com.nasim.moneycopilot.model.dto.CategoryBreakdownResponse;
import com.nasim.moneycopilot.model.dto.IncomeBreakdownResponse;
import com.nasim.moneycopilot.model.dto.MonthlySummaryResponse;
import com.nasim.moneycopilot.model.dto.TrendResponse;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Financial dashboard aggregation endpoints. */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

  private final DashboardService dashboardService;

  /** Monthly income, expenses, savings, and savings rate. */
  @GetMapping("/monthly-summary")
  @Operation(summary = "Monthly financial summary")
  public MonthlySummaryResponse monthlySummary(
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month,
      @AuthenticationPrincipal User currentUser) {
    LocalDate now = LocalDate.now();
    return dashboardService.monthlySummary(
        currentUser,
        year != null ? year : now.getYear(),
        month != null ? month : now.getMonthValue());
  }

  /** Expense totals grouped by category for a given month. */
  @GetMapping("/category-breakdown")
  @Operation(summary = "Expense breakdown by category")
  public CategoryBreakdownResponse categoryBreakdown(
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month,
      @AuthenticationPrincipal User currentUser) {
    LocalDate now = LocalDate.now();
    return dashboardService.categoryBreakdown(
        currentUser,
        year != null ? year : now.getYear(),
        month != null ? month : now.getMonthValue());
  }

  /** Income totals grouped by source for a given month. */
  @GetMapping("/income-breakdown")
  @Operation(summary = "Income breakdown by source")
  public IncomeBreakdownResponse incomeBreakdown(
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month,
      @AuthenticationPrincipal User currentUser) {
    LocalDate now = LocalDate.now();
    return dashboardService.incomeBreakdown(
        currentUser,
        year != null ? year : now.getYear(),
        month != null ? month : now.getMonthValue());
  }

  /** Monthly income/expense/savings trend over the last N months. */
  @GetMapping("/trends")
  @Operation(summary = "Monthly trend data")
  public TrendResponse trends(
      @RequestParam(defaultValue = "12") int months,
      @AuthenticationPrincipal User currentUser) {
    return dashboardService.trends(currentUser, months);
  }
}
