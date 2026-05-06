package com.nasim.moneycopilot.controller;

import com.nasim.moneycopilot.model.dto.IncomeSourceResponse;
import com.nasim.moneycopilot.repository.IncomeSourceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Read-only endpoint for predefined income sources. */
@RestController
@RequestMapping("/api/v1/income-sources")
@RequiredArgsConstructor
@Tag(name = "Income Sources")
@SecurityRequirement(name = "bearerAuth")
public class IncomeSourceController {

  private final IncomeSourceRepository incomeSourceRepository;

  /** List all predefined income sources. */
  @GetMapping
  @Operation(summary = "List all income sources")
  public List<IncomeSourceResponse> list() {
    return incomeSourceRepository.findAll().stream()
        .map(IncomeSourceResponse::from)
        .toList();
  }
}
