package com.nasim.moneycopilot.controller;

import com.nasim.moneycopilot.model.dto.CreateExpenseRequest;
import com.nasim.moneycopilot.model.dto.ExpenseResponse;
import com.nasim.moneycopilot.model.dto.UpdateExpenseRequest;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** CRUD endpoints for expenses. */
@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

  private final ExpenseService expenseService;

  /** Create a new expense. */
  @PostMapping
  @Operation(summary = "Create a new expense")
  public ResponseEntity<ExpenseResponse> create(
      @Valid @RequestBody CreateExpenseRequest request,
      @AuthenticationPrincipal User currentUser) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(expenseService.create(request, currentUser));
  }

  /** List expenses with optional filters. */
  @GetMapping
  @Operation(summary = "List expenses (paginated)")
  public Page<ExpenseResponse> list(
      @RequestParam(required = false) Integer categoryId,
      @RequestParam(required = false) LocalDate from,
      @RequestParam(required = false) LocalDate to,
      @PageableDefault(size = 20, sort = "expenseDate", direction = Sort.Direction.DESC)
          Pageable pageable,
      @AuthenticationPrincipal User currentUser) {
    return expenseService.list(currentUser, categoryId, from, to, pageable);
  }

  /** Get a single expense by id. */
  @GetMapping("/{id}")
  @Operation(summary = "Get expense by id")
  public ExpenseResponse getById(
      @PathVariable UUID id,
      @AuthenticationPrincipal User currentUser) {
    return expenseService.getById(id, currentUser);
  }

  /** Update an existing expense. */
  @PutMapping("/{id}")
  @Operation(summary = "Update an expense")
  public ExpenseResponse update(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateExpenseRequest request,
      @AuthenticationPrincipal User currentUser) {
    return expenseService.update(id, request, currentUser);
  }

  /** Soft-delete an expense. */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete an expense")
  public void delete(
      @PathVariable UUID id,
      @AuthenticationPrincipal User currentUser) {
    expenseService.delete(id, currentUser);
  }
}
