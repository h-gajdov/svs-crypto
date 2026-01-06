package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.service.EmailService;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class JavaMailEmailService implements EmailService {
    private final JavaMailSender javaMailSender;

    public JavaMailEmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendSimpleMail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Recipient email cannot be empty");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
        } catch (MailException e) {
            System.err.println("Failed to send email to " + to + " error: " + e.getMessage());
        }
    }
}
