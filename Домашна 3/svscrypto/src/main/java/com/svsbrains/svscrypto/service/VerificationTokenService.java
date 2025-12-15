package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.model.VerificationToken;

public interface VerificationTokenService {
    VerificationToken save(VerificationToken verificationToken);
    VerificationToken findByUser(User user);
}
