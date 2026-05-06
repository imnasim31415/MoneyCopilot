package com.nasim.moneycopilot.repository;

import com.nasim.moneycopilot.model.entity.TaxpayerProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxpayerProfileRepository extends JpaRepository<TaxpayerProfile, UUID> {

  Optional<TaxpayerProfile> findByUserId(UUID userId);
}
