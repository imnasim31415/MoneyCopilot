package com.nasim.moneycopilot.controller;

import com.nasim.moneycopilot.model.dto.CreateInvestmentRequest;
import com.nasim.moneycopilot.model.dto.InvestmentResponse;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.service.InvestmentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/investments")
@RequiredArgsConstructor
public class InvestmentController {

  private final InvestmentService investmentService;

  @PostMapping
  public ResponseEntity<InvestmentResponse> create(
      @Valid @RequestBody CreateInvestmentRequest request,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(investmentService.create(request, user));
  }

  @GetMapping
  public ResponseEntity<List<InvestmentResponse>> list(
      @RequestParam String fiscalYear,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(investmentService.listByFiscalYear(fiscalYear, user));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID id,
      @AuthenticationPrincipal User user) {
    investmentService.delete(id, user);
    return ResponseEntity.noContent().build();
  }
}
