package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.model.VerificationToken;
import com.svsbrains.svscrypto.repository.VerificationTokenRepository;
import com.svsbrains.svscrypto.service.VerificationTokenService;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenServiceImpl(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public VerificationToken save(VerificationToken verificationToken) {
        VerificationToken existing = findByUser(verificationToken.getUser());
        if (existing != null) {
            verificationTokenRepository.delete(existing);
        }
        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken findByUser(User user) {
        return verificationTokenRepository.findByUser(user).orElse(null);
    }

    @Override
    public void removeTokenFromUser(User user) {
        VerificationToken token = findByUser(user);
        if(token == null) return;
        verificationTokenRepository.delete(token);
    }
}
