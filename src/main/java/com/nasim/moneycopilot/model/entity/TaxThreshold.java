package com.nasim.moneycopilot.model.entity;

import com.nasim.moneycopilot.model.enums.TaxpayerCategory;
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
@Table(name = "tax_thresholds")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxThreshold {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "config_id", nullable = false)
  private TaxConfiguration config;

  @Enumerated(EnumType.STRING)
  @Column(name = "taxpayer_category", nullable = false, length = 50)
  private TaxpayerCategory taxpayerCategory;

  @Column(name = "threshold_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal thresholdAmount;
}
