package com.nasim.moneycopilot.controller;

import com.nasim.moneycopilot.model.dto.CategoryResponse;
import com.nasim.moneycopilot.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Read-only endpoint for expense categories. */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

  private final CategoryRepository categoryRepository;

  /** List all available categories. */
  @GetMapping
  @Operation(summary = "List all expense categories")
  public List<CategoryResponse> list() {
    return categoryRepository.findAll().stream()
        .map(CategoryResponse::from)
        .toList();
  }
}
