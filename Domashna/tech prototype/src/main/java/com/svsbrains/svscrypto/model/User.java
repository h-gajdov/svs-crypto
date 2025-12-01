package com.svsbrains.svscrypto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class User {

    public User(String username, String firstName, String lastName, String email, String password) {
        this.username=username;
        this.first_name=firstName;
        this.last_name=lastName;
        this.email=email;
        this.password=password;
        this.coins=new ArrayList<>();
    }

    public void addCoin(Coin c){
        this.coins.add(c);
    }

    public @NotNull String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public @Email String getEmail() {
        return email;
    }

    public void setEmail(@Email String email) {
        this.email = email;
    }

    public @NotNull String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public List<Coin> getCoins() {
        return coins;
    }

    public void setCoins(List<Coin> coins) {
        this.coins = coins;
    }

    @Id
    @Column(unique = true)
    @NotNull
    private String username;
    private String first_name;
    private String last_name;

    @Email
    private String email;

    @NotNull
    private String password;

    @ManyToMany
    private List<Coin> coins;
}
