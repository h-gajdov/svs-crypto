package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.Coin;
import com.svsbrains.svscrypto.model.User;

public interface UserService{

    public void signInUser(String username,String first_name,String last_name, String email, String password);
    public User logInUserByUsername(String username, String password);
    public void addCoinToList(String username, Coin c);

}
