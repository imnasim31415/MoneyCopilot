package com.nasim.moneycopilot.model.dto;

/** Response body returned after successful authentication. */
public record AuthResponse(
    String accessToken,
    String refreshToken
) {}
