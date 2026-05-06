package com.nasim.moneycopilot.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "tax_slabs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxSlab {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "config_id", nullable = false)
  private TaxConfiguration config;

  @Column(name = "slab_order", nullable = false)
  private Integer slabOrder;

  @Column(name = "income_up_to", precision = 14, scale = 2)
  private BigDecimal incomeUpTo;

  @Column(name = "rate_percentage", nullable = false, precision = 5, scale = 2)
  private BigDecimal ratePercentage;
}
