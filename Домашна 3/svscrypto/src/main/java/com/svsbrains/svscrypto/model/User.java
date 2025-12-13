package com.svsbrains.svscrypto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class User {
    public List<String> getCoins() {
        return coins;
    }

    public void setCoins(List<String> coins) {
        this.coins = coins;
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

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> coins;

    public User() {
        this.username = "";
        this.first_name = "";
        this.last_name = "";
        this.email = "";
        this.password = "";
        this.coins = new LinkedList<>();
    }

    public User(String username, String firstName, String lastName, String email, String password) {
        this.username = username;
        this.first_name = firstName;
        this.last_name = lastName;
        this.email = email;
        this.password = password;
        this.coins = new LinkedList<>();
    }

    public void removeCoin(String c) {
        this.coins.remove(c);
    }

    public void addCoin(String c) {
        this.coins.add(c);
    }


    public boolean hasCoin(String symbol) {
        if (coins == null) return false;
        return coins.stream().anyMatch(c -> c.equals(symbol));
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

}
