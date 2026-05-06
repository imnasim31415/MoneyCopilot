package com.nasim.moneycopilot.service;

import com.nasim.moneycopilot.model.dto.TaxpayerProfileResponse;
import com.nasim.moneycopilot.model.dto.UpdateTaxpayerProfileRequest;
import com.nasim.moneycopilot.model.entity.TaxpayerProfile;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.model.enums.LocationType;
import com.nasim.moneycopilot.model.enums.TaxpayerCategory;
import com.nasim.moneycopilot.repository.TaxpayerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaxpayerProfileService {

  private final TaxpayerProfileRepository profileRepository;

  @Transactional(readOnly = true)
  public TaxpayerProfileResponse getOrDefault(User user) {
    return profileRepository.findByUserId(user.getId())
        .map(this::toResponse)
        .orElse(defaultProfile());
  }

  @Transactional
  public TaxpayerProfileResponse upsert(UpdateTaxpayerProfileRequest request, User user) {
    TaxpayerProfile profile = profileRepository.findByUserId(user.getId())
        .orElseGet(() -> TaxpayerProfile.builder().user(user).build());

    profile.setCategory(request.category());
    profile.setLocationType(request.locationType());
    profile.setDateOfBirth(request.dateOfBirth());
    profile.setHasDisabledChild(request.hasDisabledChild());

    return toResponse(profileRepository.save(profile));
  }

  private TaxpayerProfileResponse toResponse(TaxpayerProfile p) {
    return new TaxpayerProfileResponse(
        p.getId(),
        p.getCategory(),
        p.getLocationType(),
        p.getDateOfBirth(),
        p.isHasDisabledChild());
  }

  private TaxpayerProfileResponse defaultProfile() {
    return new TaxpayerProfileResponse(
        null,
        TaxpayerCategory.GENERAL_MALE,
        LocationType.DHAKA_CHATTOGRAM,
        null,
        false);
  }
}
