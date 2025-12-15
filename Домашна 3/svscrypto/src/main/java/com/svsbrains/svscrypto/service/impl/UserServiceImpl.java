package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.model.VerificationToken;
import com.svsbrains.svscrypto.repository.UserRepository;
import com.svsbrains.svscrypto.service.EmailService;
import com.svsbrains.svscrypto.service.UserService;
import com.svsbrains.svscrypto.service.VerificationTokenService;
import com.svsbrains.svscrypto.util.PinGenerator;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenService verificationTokenService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
    }

    @Override
    public User signInUser(String username, String first_name, String last_name, String email, String password) {
        if(userRepository.findByEmail(email) != null){
            throw new UsernameNotFoundException("Email already in use");
        }
        User user = new User(username, first_name, last_name, email, passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    @Override
    public User logInUserByUsername(String username, String password) {
        User user = userRepository.getUserByUsername(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            //TODO: Change exception
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return user;
    }

    @Override
    public User addCoinToList(String username, String c) {
        User u = userRepository.getUserByUsername(username);
        u.addCoin(c);
        return u;
    }

    @Override
    public User deleteCoinFromList(String username, String c) {
        User u = userRepository.getUserByUsername(username);
        u.removeCoin(c);
        return u;
    }

    @Override
    public User addHistoryCoin(String username, String s) {
        User u = userRepository.getUserByUsername(username);
        u.addHistoryCoin(s);
        return u;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void sendVerificationMail(User user) {
        String pin = PinGenerator.generatePin();
        VerificationToken verificationToken = new VerificationToken(user, pin);
        verificationTokenService.save(verificationToken);

        String subject = "Your Verification PIN";
        String body = "Your verification code is: " + pin + "\n" +
                "This code expires in 15 minutes.";

        emailService.sendSimpleMail(user.getEmail(), subject, body);
    }
}
