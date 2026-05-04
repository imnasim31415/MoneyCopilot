package com.nasim.moneycopilot.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Request body for user login. */
public record LoginRequest(

    @NotBlank
    @Email
    String email,

    @NotBlank
    String password
) {}
