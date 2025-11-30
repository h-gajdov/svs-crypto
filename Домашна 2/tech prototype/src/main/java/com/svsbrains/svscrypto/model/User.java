package com.svsbrains.svscrypto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class User {
    @Id
    @Column(unique = true)
    @NotNull
    private String username;

    private String first_name;
    private String last_name;

    @Email
    private String email;

    @Lob //Za da ne go ogranicit br na karakteri
    @NotNull
    private String password;

    @ManyToMany
    private List<Coin> coins;
}
