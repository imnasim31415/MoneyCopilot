package com.nasim.moneycopilot.model.dto;

import com.nasim.moneycopilot.model.enums.LocationType;
import com.nasim.moneycopilot.model.enums.TaxpayerCategory;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateTaxpayerProfileRequest(
    @NotNull TaxpayerCategory category,
    @NotNull LocationType locationType,
    LocalDate dateOfBirth,
    boolean hasDisabledChild
) {}
