package com.svsbrains.svscrypto.repository;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByUser(User user);
}
