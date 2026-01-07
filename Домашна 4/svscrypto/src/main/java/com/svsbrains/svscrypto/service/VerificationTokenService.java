package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.model.VerificationToken;

/**
 * Service interface for managing verification tokens.
 * <p>
 * Verification tokens are typically used for user account verification,
 * such as email confirmation or password reset functionality.
 * This service provides methods to save a token and retrieve a token associated with a specific user.
 * </p>
 */
public interface VerificationTokenService {

    /**
     * Saves a verification token to the persistent storage.
     *
     * @param verificationToken the {@link VerificationToken} object to be saved
     * @return the saved {@link VerificationToken} with any updates (e.g., generated ID)
     */
    VerificationToken save(VerificationToken verificationToken);

    /**
     * Finds a verification token associated with a specific user.
     *
     * @param user the {@link User} for whom to find the verification token
     * @return the {@link VerificationToken} associated with the given user,
     *         or {@code null} if no token exists
     */
    VerificationToken findByUser(User user);

    /**
     * Removes the verification token associated with the given user.
     * <p>
     * This is usually called after the user successfully verifies their account
     * to ensure the token cannot be reused.
     *
     * @param user the {@link User} whose verification token should be removed
     */
    void removeTokenFromUser(User user);
}