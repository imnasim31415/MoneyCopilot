package com.nasim.moneycopilot.model.dto;

import com.nasim.moneycopilot.model.entity.Category;

/** Response body for a single category. */
public record CategoryResponse(
    Integer id,
    String name,
    String icon
) {

  /** Map a {@link Category} entity to this response record. */
  public static CategoryResponse from(Category category) {
    return new CategoryResponse(category.getId(), category.getName(), category.getIcon());
  }
}
