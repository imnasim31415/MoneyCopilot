package com.nasim.moneycopilot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nasim.moneycopilot.exception.ResourceNotFoundException;
import com.nasim.moneycopilot.model.dto.CreateExpenseRequest;
import com.nasim.moneycopilot.model.dto.ExpenseResponse;
import com.nasim.moneycopilot.model.entity.Category;
import com.nasim.moneycopilot.model.entity.Expense;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.repository.CategoryRepository;
import com.nasim.moneycopilot.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

  @Mock
  private ExpenseRepository expenseRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private ExpenseService expenseService;

  private User user;
  private Category category;

  @BeforeEach
  void setUp() {
    user = User.builder().id(UUID.randomUUID()).email("user@example.com").build();
    category = new Category(1, "FOOD", "utensils", true);
  }

  @Test
  void should_createExpense_when_requestIsValid() {
    CreateExpenseRequest request = new CreateExpenseRequest(
        new BigDecimal("100.00"), 1, LocalDate.now(), "Lunch");

    when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
    when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> {
      Expense saved = invocation.getArgument(0);
      saved.setId(UUID.randomUUID());
      return saved;
    });

    ExpenseResponse response = expenseService.create(request, user);

    assertThat(response.amount()).isEqualByComparingTo("100.00");
    assertThat(response.categoryName()).isEqualTo("FOOD");
    assertThat(response.description()).isEqualTo("Lunch");
  }

  @Test
  void should_throwException_when_categoryNotFound() {
    CreateExpenseRequest request = new CreateExpenseRequest(
        new BigDecimal("50.00"), 999, LocalDate.now(), null);

    when(categoryRepository.findById(999)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> expenseService.create(request, user))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void should_throwException_when_expenseNotFound() {
    UUID id = UUID.randomUUID();

    when(expenseRepository.findByIdAndUserIdAndDeletedAtIsNull(id, user.getId()))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> expenseService.getById(id, user))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void should_softDelete_when_expenseExists() {
    UUID id = UUID.randomUUID();
    Expense expense = Expense.builder().id(id).user(user).category(category).build();

    when(expenseRepository.findByIdAndUserIdAndDeletedAtIsNull(id, user.getId()))
        .thenReturn(Optional.of(expense));
    when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

    expenseService.delete(id, user);

    assertThat(expense.getDeletedAt()).isNotNull();
    verify(expenseRepository).save(expense);
  }
}
