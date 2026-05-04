package com.nasim.moneycopilot.repository;

import com.nasim.moneycopilot.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for {@link User} entities. */
public interface UserRepository extends JpaRepository<User, UUID> {

  /** Find a user by email address. */
  Optional<User> findByEmail(String email);

  /** Check whether an email is already registered. */
  boolean existsByEmail(String email);
}
