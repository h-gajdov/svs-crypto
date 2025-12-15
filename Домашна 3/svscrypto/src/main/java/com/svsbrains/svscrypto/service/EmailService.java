package com.svsbrains.svscrypto.service;

public interface EmailService {
    void sendSimpleMail(String to, String subject, String body);
}
