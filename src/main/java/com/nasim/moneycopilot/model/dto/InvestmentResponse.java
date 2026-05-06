package com.nasim.moneycopilot.model.dto;

import com.nasim.moneycopilot.model.enums.InvestmentCategory;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record InvestmentResponse(
    UUID id,
    InvestmentCategory category,
    BigDecimal amount,
    String fiscalYear,
    String description,
    OffsetDateTime createdAt
) {}
