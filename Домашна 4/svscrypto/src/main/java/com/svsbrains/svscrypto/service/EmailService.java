package com.svsbrains.svscrypto.service;

/**
 * Service interface for sending simple emails.
 * <p>
 * Implementations of this interface provide functionality to send plain-text emails
 * to a specified recipient with a given subject and message body.
 * </p>
 */
public interface EmailService {

    /**
     * Sends a simple plain-text email.
     *
     * @param to      the recipient's email address
     * @param subject the subject of the email
     * @param body    the content of the email
     * @throws IllegalArgumentException if any of the parameters are null or empty (optional for implementation)
     * @throws RuntimeException         if the email could not be sent due to a mail server error
     */
    void sendSimpleMail(String to, String subject, String body);
}
