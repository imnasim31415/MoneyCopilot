package com.nasim.moneycopilot.service;

import com.nasim.moneycopilot.exception.ResourceNotFoundException;
import com.nasim.moneycopilot.model.dto.CreateExpenseRequest;
import com.nasim.moneycopilot.model.dto.ExpenseResponse;
import com.nasim.moneycopilot.model.dto.UpdateExpenseRequest;
import com.nasim.moneycopilot.model.entity.Category;
import com.nasim.moneycopilot.model.entity.Expense;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.repository.CategoryRepository;
import com.nasim.moneycopilot.repository.ExpenseRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business logic for expense management. */
@Service
@RequiredArgsConstructor
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final CategoryRepository categoryRepository;

  /** Create a new expense for the authenticated user. */
  @Transactional
  public ExpenseResponse create(CreateExpenseRequest request, User currentUser) {
    Category category = findCategory(request.categoryId());

    Expense expense = Expense.builder()
        .user(currentUser)
        .amount(request.amount())
        .category(category)
        .expenseDate(request.expenseDate())
        .description(request.description())
        .build();

    return ExpenseResponse.from(expenseRepository.save(expense));
  }

  /** List expenses for the authenticated user with optional filters. */
  @Transactional(readOnly = true)
  public Page<ExpenseResponse> list(
      User currentUser,
      Integer categoryId,
      LocalDate from,
      LocalDate to,
      Pageable pageable) {

    return expenseRepository
        .findAllByFilters(currentUser.getId(), categoryId, from, to, pageable)
        .map(ExpenseResponse::from);
  }

  /** Get a single expense by id for the authenticated user. */
  @Transactional(readOnly = true)
  public ExpenseResponse getById(UUID id, User currentUser) {
    return ExpenseResponse.from(findExpense(id, currentUser));
  }

  /** Update an existing expense owned by the authenticated user. */
  @Transactional
  public ExpenseResponse update(UUID id, UpdateExpenseRequest request, User currentUser) {
    Expense expense = findExpense(id, currentUser);
    Category category = findCategory(request.categoryId());

    expense.setAmount(request.amount());
    expense.setCategory(category);
    expense.setExpenseDate(request.expenseDate());
    expense.setDescription(request.description());

    return ExpenseResponse.from(expenseRepository.save(expense));
  }

  /** Soft-delete an expense owned by the authenticated user. */
  @Transactional
  public void delete(UUID id, User currentUser) {
    Expense expense = findExpense(id, currentUser);
    expense.setDeletedAt(OffsetDateTime.now());
    expenseRepository.save(expense);
  }

  private Expense findExpense(UUID id, User currentUser) {
    return expenseRepository
        .findByIdAndUserIdAndDeletedAtIsNull(id, currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Expense", id));
  }

  private Category findCategory(Integer categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
  }
}
