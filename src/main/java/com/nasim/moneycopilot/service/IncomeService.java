package com.nasim.moneycopilot.service;

import com.nasim.moneycopilot.exception.ResourceNotFoundException;
import com.nasim.moneycopilot.model.dto.CreateIncomeRequest;
import com.nasim.moneycopilot.model.dto.IncomeResponse;
import com.nasim.moneycopilot.model.dto.UpdateIncomeRequest;
import com.nasim.moneycopilot.model.entity.Income;
import com.nasim.moneycopilot.model.entity.IncomeSource;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.repository.IncomeRepository;
import com.nasim.moneycopilot.repository.IncomeSourceRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business logic for income management. */
@Service
@RequiredArgsConstructor
public class IncomeService {

  private final IncomeRepository incomeRepository;
  private final IncomeSourceRepository incomeSourceRepository;

  /** Create a new income record for the authenticated user. */
  @Transactional
  public IncomeResponse create(CreateIncomeRequest request, User currentUser) {
    IncomeSource source = findSource(request.sourceId());

    Income income = Income.builder()
        .user(currentUser)
        .amount(request.amount())
        .source(source)
        .incomeDate(request.incomeDate())
        .description(request.description())
        .build();

    return IncomeResponse.from(incomeRepository.save(income));
  }

  /** List incomes for the authenticated user with optional filters. */
  @Transactional(readOnly = true)
  public Page<IncomeResponse> list(
      User currentUser,
      Integer sourceId,
      LocalDate from,
      LocalDate to,
      Pageable pageable) {

    return incomeRepository
        .findAllByFilters(currentUser.getId(), sourceId, from, to, pageable)
        .map(IncomeResponse::from);
  }

  /** Get a single income record by id for the authenticated user. */
  @Transactional(readOnly = true)
  public IncomeResponse getById(UUID id, User currentUser) {
    return IncomeResponse.from(findIncome(id, currentUser));
  }

  /** Update an existing income record owned by the authenticated user. */
  @Transactional
  public IncomeResponse update(UUID id, UpdateIncomeRequest request, User currentUser) {
    Income income = findIncome(id, currentUser);
    IncomeSource source = findSource(request.sourceId());

    income.setAmount(request.amount());
    income.setSource(source);
    income.setIncomeDate(request.incomeDate());
    income.setDescription(request.description());

    return IncomeResponse.from(incomeRepository.save(income));
  }

  /** Soft-delete an income record owned by the authenticated user. */
  @Transactional
  public void delete(UUID id, User currentUser) {
    Income income = findIncome(id, currentUser);
    income.setDeletedAt(OffsetDateTime.now());
    incomeRepository.save(income);
  }

  private Income findIncome(UUID id, User currentUser) {
    return incomeRepository
        .findByIdAndUserIdAndDeletedAtIsNull(id, currentUser.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Income", id));
  }

  private IncomeSource findSource(Integer sourceId) {
    return incomeSourceRepository.findById(sourceId)
        .orElseThrow(() -> new ResourceNotFoundException("IncomeSource", sourceId));
  }
}
