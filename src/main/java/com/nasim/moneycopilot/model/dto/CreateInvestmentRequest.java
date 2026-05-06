package com.nasim.moneycopilot.model.dto;

import com.nasim.moneycopilot.model.enums.InvestmentCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record CreateInvestmentRequest(
    @NotNull InvestmentCategory category,
    @NotNull @DecimalMin("0.01") BigDecimal amount,
    @NotBlank @Pattern(regexp = "\\d{4}-\\d{4}") String fiscalYear,
    String description
) {}
