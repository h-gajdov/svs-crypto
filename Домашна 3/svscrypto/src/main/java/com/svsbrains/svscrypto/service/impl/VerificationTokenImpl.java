package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.model.VerificationToken;
import com.svsbrains.svscrypto.repository.VerificationTokenRepository;
import com.svsbrains.svscrypto.service.VerificationTokenService;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenImpl implements VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenImpl(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public VerificationToken save(VerificationToken verificationToken) {
        if (verificationTokenRepository.findByUser(verificationToken.getUser()) != null) {
            verificationTokenRepository.delete(verificationTokenRepository.findByUser(verificationToken.getUser()));
            verificationTokenRepository.flush();
        }
        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken findByUser(User user) {
        return verificationTokenRepository.findByUser(user);
    }
}
