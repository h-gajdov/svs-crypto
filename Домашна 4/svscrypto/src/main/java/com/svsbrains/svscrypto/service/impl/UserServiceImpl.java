package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.model.VerificationToken;
import com.svsbrains.svscrypto.model.exceptions.EmailAlreadyUsedException;
import com.svsbrains.svscrypto.model.exceptions.EmailNotFoundException;
import com.svsbrains.svscrypto.model.exceptions.InvalidCredentialsException;
import com.svsbrains.svscrypto.repository.UserRepository;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.EmailService;
import com.svsbrains.svscrypto.service.UserService;
import com.svsbrains.svscrypto.service.VerificationTokenService;
import com.svsbrains.svscrypto.util.PinGenerator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final DailyDataService dailyDataService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenService verificationTokenService, EmailService emailService, DailyDataService dailyDataService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
        this.dailyDataService = dailyDataService;
    }

    @Override
    public User signUpUser(String username, String firstName, String lastName, String email, String password) {
        if(userRepository.findByEmail(email).isPresent()){
            throw new EmailAlreadyUsedException(email);
        }
        User user = new User(username, firstName, lastName, email, passwordEncoder.encode(password));
        userRepository.save(user);
        return user;
    }

    @Override
    public User logInUserByUsername(String username, String password) {
        User user = findByUsername(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return user;
    }

    @Override
    public User addCoinToWatchlist(String username, String symbol) {
        User user = findByUsername(username);
        user.addCoin(symbol);
        return save(user);
    }

    @Override
    public User removeCoinFromWatchlist(String username, String symbol) {
        User user = findByUsername(username);
        user.removeCoin(symbol);
        return save(user);
    }

    @Override
    public User addCoinToSearchHistory(String username, String symbol) {
        User user = findByUsername(username);
        user.addHistoryCoin(symbol);
        return save(user);
    }

    @Override
    public List<DailyData> getSearchHistory(String username) {
        User user = findByUsername(username);
        return user.getHistoryCoins().stream().map(dailyDataService::getBySymbol).toList();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EmailNotFoundException(email));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Override
    public User getCurrentUser(UserDetails userDetails) {
        if(userDetails == null) return null;
        return findByUsername(userDetails.getUsername());
    }

    @Override
    public User enableUser(User user) {
        user.setEnabled(true);
        verificationTokenService.removeTokenFromUser(user);
        return user;
    }

    @Override
    public void sendVerificationMail(User user) {
        verificationTokenService.removeTokenFromUser(user); // if there was a token previously remove it

        String pin = PinGenerator.generatePin();
        VerificationToken verificationToken = new VerificationToken(user, pin);
        verificationTokenService.save(verificationToken);

        String subject = "Your Verification PIN";
        String body = "Your verification code is: " + pin + "\n" +
                "This code expires in 15 minutes.";

        emailService.sendSimpleMail(user.getEmail(), subject, body);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }
}
