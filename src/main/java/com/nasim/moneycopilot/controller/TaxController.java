package com.nasim.moneycopilot.controller;

import com.nasim.moneycopilot.model.dto.TaxCalculationResult;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tax")
@RequiredArgsConstructor
public class TaxController {

  private final TaxService taxService;

  @GetMapping("/calculate")
  public ResponseEntity<TaxCalculationResult> calculate(
      @RequestParam String fiscalYear,
      @RequestParam(required = false, defaultValue = "false") boolean newTaxpayer,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(taxService.calculate(fiscalYear, newTaxpayer, user));
  }
}
