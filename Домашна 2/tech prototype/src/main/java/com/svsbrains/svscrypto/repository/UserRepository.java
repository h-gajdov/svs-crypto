package com.svsbrains.svscrypto.repository;

import com.svsbrains.svscrypto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
