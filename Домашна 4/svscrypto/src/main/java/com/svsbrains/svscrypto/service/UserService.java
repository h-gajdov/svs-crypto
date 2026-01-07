package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Service responsible for managing {@link User} domain logic.
 * <p>
 * This service handles:
 * <ul>
 *     <li>User registration and authentication</li>
 *     <li>User persistence and lookup</li>
 *     <li>User activity such as watchlist and search history</li>
 *     <li>Verification email dispatch</li>
 * </ul>
 *
 * <p>
 * It also extends {@link UserDetailsService} to integrate with Spring Security
 * for authentication and authorization.
 */
public interface UserService extends UserDetailsService {

    User signUpUser(String username, String firstName, String lastName, String email, String password);

    User logInUserByUsername(String username, String password);

    User addCoinToWatchlist(String username, String c);

    User removeCoinFromWatchlist(String username, String c);

    User addCoinToSearchHistory(String username, String symbol);

    List<DailyData> getSearchHistory(String username);

    User findByEmail(String email);

    User save(User user);

    User findByUsername(String username);

    User getCurrentUser(UserDetails userDetails);

    /**
     * Enables the given user account and removes any existing verification token associated with the user.
     * <p>
     * This method is typically called after the user successfully verifies their email or PIN.
     * It performs two actions:
     * <ol>
     *     <li>Sets the {@code enabled} flag of the {@link User} to {@code true}, allowing the user to log in.</li>
     *     <li>Removes the existing verification token from the user via {@link VerificationTokenService},
     *         ensuring the token cannot be reused.</li>
     * </ol>
     *
     * @param user the {@link User} to enable
     * @return the updated {@link User} with {@code enabled = true} and token removed
     */
    User enableUser(User user);

    /**
     * Sends a verification email containing a one-time PIN
     * to the user's registered email address.
     *
     * @param user the user to verify
     */
    void sendVerificationMail(User user);
}
