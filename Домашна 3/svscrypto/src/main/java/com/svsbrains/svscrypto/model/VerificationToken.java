package com.svsbrains.svscrypto.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    public VerificationToken() {}

    public VerificationToken(User user, String token) {
        this.user = user;
        this.token = token;
        // Set expiry to 15 minutes from now
        this.expiryDate = new Date(System.currentTimeMillis() + (1000 * 60 * 15));
    }

}
