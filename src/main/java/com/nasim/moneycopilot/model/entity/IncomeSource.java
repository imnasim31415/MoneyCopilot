package com.nasim.moneycopilot.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Reference entity for predefined income sources. */
@Entity
@Table(name = "income_sources")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IncomeSource {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @Column(length = 50)
  private String icon;

  @Column(name = "is_system", nullable = false)
  private boolean system;
}
