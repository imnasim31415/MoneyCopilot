package com.nasim.moneycopilot.service;

import com.nasim.moneycopilot.exception.ResourceNotFoundException;
import com.nasim.moneycopilot.model.dto.CreateInvestmentRequest;
import com.nasim.moneycopilot.model.dto.InvestmentResponse;
import com.nasim.moneycopilot.model.entity.Investment;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.repository.InvestmentRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvestmentService {

  private final InvestmentRepository investmentRepository;

  @Transactional
  public InvestmentResponse create(CreateInvestmentRequest request, User user) {
    Investment investment = Investment.builder()
        .user(user)
        .fiscalYear(request.fiscalYear())
        .category(request.category())
        .amount(request.amount())
        .description(request.description())
        .build();
    return toResponse(investmentRepository.save(investment));
  }

  @Transactional(readOnly = true)
  public List<InvestmentResponse> listByFiscalYear(String fiscalYear, User user) {
    return investmentRepository
        .findByUserIdAndFiscalYearAndDeletedAtIsNull(user.getId(), fiscalYear)
        .stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  public void delete(UUID id, User user) {
    Investment investment = investmentRepository
        .findByIdAndUserIdAndDeletedAtIsNull(id, user.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Investment", id));
    investment.setDeletedAt(OffsetDateTime.now());
    investmentRepository.save(investment);
  }

  private InvestmentResponse toResponse(Investment i) {
    return new InvestmentResponse(
        i.getId(),
        i.getCategory(),
        i.getAmount(),
        i.getFiscalYear(),
        i.getDescription(),
        i.getCreatedAt());
  }
}
