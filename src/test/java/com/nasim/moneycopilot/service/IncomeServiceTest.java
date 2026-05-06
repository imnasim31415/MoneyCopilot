package com.nasim.moneycopilot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nasim.moneycopilot.exception.ResourceNotFoundException;
import com.nasim.moneycopilot.model.dto.CreateIncomeRequest;
import com.nasim.moneycopilot.model.dto.IncomeResponse;
import com.nasim.moneycopilot.model.entity.Income;
import com.nasim.moneycopilot.model.entity.IncomeSource;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.repository.IncomeRepository;
import com.nasim.moneycopilot.repository.IncomeSourceRepository;
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
class IncomeServiceTest {

  @Mock
  private IncomeRepository incomeRepository;

  @Mock
  private IncomeSourceRepository incomeSourceRepository;

  @InjectMocks
  private IncomeService incomeService;

  private User user;
  private IncomeSource source;

  @BeforeEach
  void setUp() {
    user = User.builder().id(UUID.randomUUID()).email("user@example.com").build();
    source = new IncomeSource(1, "SALARY", "briefcase", true);
  }

  @Test
  void should_createIncome_when_requestIsValid() {
    CreateIncomeRequest request = new CreateIncomeRequest(
        new BigDecimal("50000.00"), 1, LocalDate.now(), "Monthly salary");

    when(incomeSourceRepository.findById(1)).thenReturn(Optional.of(source));
    when(incomeRepository.save(any(Income.class))).thenAnswer(invocation -> {
      Income saved = invocation.getArgument(0);
      saved.setId(UUID.randomUUID());
      return saved;
    });

    IncomeResponse response = incomeService.create(request, user);

    assertThat(response.amount()).isEqualByComparingTo("50000.00");
    assertThat(response.sourceName()).isEqualTo("SALARY");
    assertThat(response.description()).isEqualTo("Monthly salary");
  }

  @Test
  void should_throwException_when_sourceNotFound() {
    CreateIncomeRequest request = new CreateIncomeRequest(
        new BigDecimal("1000.00"), 999, LocalDate.now(), null);

    when(incomeSourceRepository.findById(999)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> incomeService.create(request, user))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void should_throwException_when_incomeNotFound() {
    UUID id = UUID.randomUUID();

    when(incomeRepository.findByIdAndUserIdAndDeletedAtIsNull(id, user.getId()))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> incomeService.getById(id, user))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void should_softDelete_when_incomeExists() {
    UUID id = UUID.randomUUID();
    Income income = Income.builder().id(id).user(user).source(source).build();

    when(incomeRepository.findByIdAndUserIdAndDeletedAtIsNull(id, user.getId()))
        .thenReturn(Optional.of(income));
    when(incomeRepository.save(any(Income.class))).thenReturn(income);

    incomeService.delete(id, user);

    assertThat(income.getDeletedAt()).isNotNull();
    verify(incomeRepository).save(income);
  }
}
