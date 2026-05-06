package com.nasim.moneycopilot.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Request body for creating a new income record. */
public record CreateIncomeRequest(

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 10, fraction = 2)
    BigDecimal amount,

    @NotNull
    Integer sourceId,

    @NotNull
    @PastOrPresent
    LocalDate incomeDate,

    @Size(max = 255)
    String description
) {}
