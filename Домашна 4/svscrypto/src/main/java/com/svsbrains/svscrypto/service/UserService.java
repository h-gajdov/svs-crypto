package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    public User signUpUser(String username, String first_name, String last_name, String email, String password);
    public User logInUserByUsername(String username, String password);
    public User addCoinToList(String username, String c);
    public User deleteCoinFromList(String username, String c);
    public User addCoinToSearchHistory(String username, String symbol);
    public List<DailyData> getSearchHistory(String username);
    public User findByEmail(String email);
    public User save(User user);
    public User findByUsername(String username);

    void sendVerificationMail(User user);
}
