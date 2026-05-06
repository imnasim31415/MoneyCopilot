package com.nasim.moneycopilot.model.dto;

import java.math.BigDecimal;

public record SlabBreakdown(
    int slabOrder,
    BigDecimal ratePercentage,
    BigDecimal incomeChunk,
    BigDecimal taxAmount
) {}
