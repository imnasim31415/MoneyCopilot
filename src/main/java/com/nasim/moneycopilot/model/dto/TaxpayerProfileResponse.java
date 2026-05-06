package com.nasim.moneycopilot.model.dto;

import com.nasim.moneycopilot.model.enums.LocationType;
import com.nasim.moneycopilot.model.enums.TaxpayerCategory;
import java.time.LocalDate;
import java.util.UUID;

public record TaxpayerProfileResponse(
    UUID id,
    TaxpayerCategory category,
    LocationType locationType,
    LocalDate dateOfBirth,
    boolean hasDisabledChild
) {}
