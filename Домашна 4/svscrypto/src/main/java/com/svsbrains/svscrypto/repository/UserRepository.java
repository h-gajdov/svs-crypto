package com.svsbrains.svscrypto.repository;

import com.svsbrains.svscrypto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> getUserByUsernameAndPassword(String username, String password);

    Optional<User> findByEmail(String email);

    Optional<User> findUserByUsername(String username);
}
