package com.nasim.moneycopilot.model.dto;

import jakarta.validation.constraints.NotBlank;

/** Request body for token refresh. */
public record RefreshRequest(

    @NotBlank
    String refreshToken
) {}
