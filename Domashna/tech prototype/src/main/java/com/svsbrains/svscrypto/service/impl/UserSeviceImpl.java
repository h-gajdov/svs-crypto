package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.Coin;
import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.repository.UserRepository;
import com.svsbrains.svscrypto.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserSeviceImpl implements UserService {
    private final UserRepository userRepository;

    public UserSeviceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void signInUser(String username,String first_name,String last_name, String email, String password) {
        User user=new User(username,first_name,last_name,email,password);
        userRepository.save(user);
    }

    @Override
    public User logInUserByUsername(String username,String password){
        User user=userRepository.getUserByUsernameAndPassword(username,password);
        return user;
    }

    @Override
    public User addCoinToList(String username, String c) {
        User u=userRepository.getUserByUsername(username);
        u.addCoin(c);
        userRepository.save(u);
        return u;
    }
}
