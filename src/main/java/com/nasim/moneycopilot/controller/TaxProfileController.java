package com.nasim.moneycopilot.controller;

import com.nasim.moneycopilot.model.dto.TaxpayerProfileResponse;
import com.nasim.moneycopilot.model.dto.UpdateTaxpayerProfileRequest;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.service.TaxpayerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tax/profile")
@RequiredArgsConstructor
public class TaxProfileController {

  private final TaxpayerProfileService profileService;

  @GetMapping
  public ResponseEntity<TaxpayerProfileResponse> get(
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(profileService.getOrDefault(user));
  }

  @PutMapping
  public ResponseEntity<TaxpayerProfileResponse> upsert(
      @Valid @RequestBody UpdateTaxpayerProfileRequest request,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(profileService.upsert(request, user));
  }
}
