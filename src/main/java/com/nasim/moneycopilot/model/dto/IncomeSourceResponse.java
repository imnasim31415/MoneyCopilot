package com.nasim.moneycopilot.model.dto;

import com.nasim.moneycopilot.model.entity.IncomeSource;

/** Response body for a single income source. */
public record IncomeSourceResponse(Integer id, String name, String icon) {

  /** Map an {@link IncomeSource} entity to this response record. */
  public static IncomeSourceResponse from(IncomeSource source) {
    return new IncomeSourceResponse(source.getId(), source.getName(), source.getIcon());
  }
}
