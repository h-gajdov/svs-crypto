package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    public User signUpUser(String username, String first_name, String last_name, String email, String password);
    public User logInUserByUsername(String username, String password);
    public User addCoinToList(String username, String c);
    public User deleteCoinFromList(String username, String c);
    public User addHistoryCoin(String username,String s);
    public User findByEmail(String email);
    public User save(User user);
    public User findByUsername(String username);

    void sendVerificationMail(User user);
}
