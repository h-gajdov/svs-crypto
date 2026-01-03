package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.service.EmailService;
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
        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("your-email@gmail.com"); // Optional: explicitly set the sender
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }
}
