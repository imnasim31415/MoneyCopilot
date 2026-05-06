package com.nasim.moneycopilot.model.entity;

import com.nasim.moneycopilot.model.enums.LocationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "minimum_tax_rules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MinimumTaxRule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "config_id", nullable = false)
  private TaxConfiguration config;

  @Enumerated(EnumType.STRING)
  @Column(name = "location_type", nullable = false, length = 50)
  private LocationType locationType;

  @Column(name = "minimum_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal minimumAmount;

  @Column(name = "is_new_taxpayer", nullable = false)
  private boolean newTaxpayer;
}
