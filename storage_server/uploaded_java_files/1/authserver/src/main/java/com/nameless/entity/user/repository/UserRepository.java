package com.nameless.entity.user.repository;

import java.util.Optional;

import com.nameless.entity.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  // Existing method for finding by email
  Optional<User> findByEmail(String email);

  // New method to find by username
  Optional<User> findByUsername(String username);  // Added this method to support username-based lookup

}
