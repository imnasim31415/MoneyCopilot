package com.nasim.moneycopilot.controller;

import com.nasim.moneycopilot.model.dto.CreateIncomeRequest;
import com.nasim.moneycopilot.model.dto.IncomeResponse;
import com.nasim.moneycopilot.model.dto.UpdateIncomeRequest;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.service.IncomeService;
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

/** CRUD endpoints for income records. */
@RestController
@RequestMapping("/api/v1/incomes")
@RequiredArgsConstructor
@Tag(name = "Incomes")
@SecurityRequirement(name = "bearerAuth")
public class IncomeController {

  private final IncomeService incomeService;

  /** Create a new income record. */
  @PostMapping
  @Operation(summary = "Create a new income record")
  public ResponseEntity<IncomeResponse> create(
      @Valid @RequestBody CreateIncomeRequest request,
      @AuthenticationPrincipal User currentUser) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(incomeService.create(request, currentUser));
  }

  /** List income records with optional filters. */
  @GetMapping
  @Operation(summary = "List income records (paginated)")
  public Page<IncomeResponse> list(
      @RequestParam(required = false) Integer sourceId,
      @RequestParam(required = false) LocalDate from,
      @RequestParam(required = false) LocalDate to,
      @PageableDefault(size = 20, sort = "incomeDate", direction = Sort.Direction.DESC)
          Pageable pageable,
      @AuthenticationPrincipal User currentUser) {
    return incomeService.list(currentUser, sourceId, from, to, pageable);
  }

  /** Get a single income record by id. */
  @GetMapping("/{id}")
  @Operation(summary = "Get income record by id")
  public IncomeResponse getById(
      @PathVariable UUID id,
      @AuthenticationPrincipal User currentUser) {
    return incomeService.getById(id, currentUser);
  }

  /** Update an existing income record. */
  @PutMapping("/{id}")
  @Operation(summary = "Update an income record")
  public IncomeResponse update(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateIncomeRequest request,
      @AuthenticationPrincipal User currentUser) {
    return incomeService.update(id, request, currentUser);
  }

  /** Soft-delete an income record. */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete an income record")
  public void delete(
      @PathVariable UUID id,
      @AuthenticationPrincipal User currentUser) {
    incomeService.delete(id, currentUser);
  }
}
