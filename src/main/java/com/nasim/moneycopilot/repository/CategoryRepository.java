package com.nasim.moneycopilot.repository;

import com.nasim.moneycopilot.model.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for {@link Category} entities. */
public interface CategoryRepository extends JpaRepository<Category, Integer> {

  /** Find a category by its name. */
  Optional<Category> findByName(String name);
}
