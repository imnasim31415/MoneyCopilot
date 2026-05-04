package com.nasim.moneycopilot.controller;

import com.nasim.moneycopilot.model.dto.AuthResponse;
import com.nasim.moneycopilot.model.dto.LoginRequest;
import com.nasim.moneycopilot.model.dto.RefreshRequest;
import com.nasim.moneycopilot.model.dto.RegisterRequest;
import com.nasim.moneycopilot.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Handles user registration, login, and token refresh. */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

  private final AuthService authService;

  /** Register a new user account. */
  @PostMapping("/register")
  @Operation(summary = "Register a new user")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
  }

  /** Login with email and password to receive tokens. */
  @PostMapping("/login")
  @Operation(summary = "Login and receive JWT tokens")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  /** Exchange a refresh token for a new access token. */
  @PostMapping("/refresh")
  @Operation(summary = "Refresh access token")
  public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
    return ResponseEntity.ok(authService.refresh(request));
  }
}
