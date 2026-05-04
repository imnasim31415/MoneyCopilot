package com.nasim.moneycopilot.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Request body for updating an existing expense. */
public record UpdateExpenseRequest(

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 10, fraction = 2)
    BigDecimal amount,

    @NotNull
    Integer categoryId,

    @NotNull
    @PastOrPresent
    LocalDate expenseDate,

    @Size(max = 255)
    String description
) {}
