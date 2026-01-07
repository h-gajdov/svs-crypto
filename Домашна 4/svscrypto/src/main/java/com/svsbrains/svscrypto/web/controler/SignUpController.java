package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.model.VerificationToken;
import com.svsbrains.svscrypto.service.CoinService;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.VerificationTokenService;
import com.svsbrains.svscrypto.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Controller for handling user registration and email verification.
 * <p>
 * This controller manages the signup flow, including:
 * <ul>
 *     <li>Displaying the signup form.</li>
 *     <li>Processing user registration.</li>
 *     <li>Sending verification emails.</li>
 *     <li>Displaying the verification form.</li>
 *     <li>Verifying the user's PIN and enabling the account.</li>
 * </ul>
 * </p>
 * <p>
 * It delegates all business logic to {@link UserService} and {@link VerificationTokenService}.
 * The controller ensures proper redirection for successful registration or verification failure.
 * </p>
 */
@Controller
public class SignUpController {
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final DailyDataService dailyDataService;
    private final CoinService coinService;

    public SignUpController(UserService userService, VerificationTokenService verificationTokenService, DailyDataService dailyDataService, CoinService coinService) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.dailyDataService = dailyDataService;
        this.coinService = coinService;
    }

    @GetMapping("/signup")
    public String signUpForm(Model model) {
        model.addAttribute("symbols", dailyDataService.getCoinsBySymbols(coinService.getAllSymbols()));
        model.addAttribute("bodyContent", "sign-up-form");
        model.addAttribute("pageTitle", "Register");
        return "master-template";
    }

    /**
     * Handles submission of the signup form.
     * <p>
     * This method:
     * <ul>
     *     <li>Registers a new user with the given information.</li>
     *     <li>Sends a verification email to the user.</li>
     *     <li>Redirects the user to the verification page with their email as a request parameter.</li>
     * </ul>
     * </p>
     *
     * @param firstname user's first name
     * @param lastname  user's last name
     * @param email     user's email
     * @param username  desired username
     * @param password  desired password
     * @param model     the Spring MVC model
     * @return a redirect to the verification page for the registered email
     */
    @PostMapping("/signup")
    public String handleSignUpSubmission(@RequestParam String firstname, @RequestParam String lastname,
                             @RequestParam String email, @RequestParam String username,
                             @RequestParam String password, Model model) {
        User user = userService.signUpUser(username, firstname, lastname, email, password);
        userService.sendVerificationMail(user);
        return "redirect:/verify?email=" + user.getEmail();
    }

    /**
     * Displays the email verification form.
     * <p>
     * If the email parameter is missing or empty, the user is redirected back to the signup page.
     * Otherwise, the verification form is rendered.
     * </p>
     *
     * @param email the email of the user to verify
     * @param model the Spring MVC model
     * @return the name of the master template rendering the verification form, or redirect to signup
     */
    @GetMapping("/verify")
    public String showVerifyPage(@RequestParam(required = false) String email, Model model) {
        if (email == null || email.isEmpty()) {
            return "redirect:/signup";
        }

        model.addAttribute("symbols", dailyDataService.getCoinsBySymbols(coinService.getAllSymbols()));
        model.addAttribute("email", email);
        model.addAttribute("bodyContent", "verification-form");
        model.addAttribute("pageTitle", "Verify Mail");
        return "master-template";
    }

    /**
     * Handles verification of the user's PIN.
     * <p>
     * This method:
     * <ul>
     *     <li>Validates the provided PIN against the user's verification token.</li>
     *     <li>If valid, enables the user account and clears any session state.</li>
     *     <li>If invalid, redisplays the verification form with an error message.</li>
     * </ul>
     * </p>
     *
     * @param email         the email of the user being verified
     * @param pin           the verification PIN entered by the user
     * @param model         the Spring MVC model
     * @param sessionStatus used to mark session completion if verification succeeds
     * @return redirect to login if verification succeeds, or redisplay the verification form on failure
     */
    @PostMapping("/verify")
    public String verifyUser(@RequestParam("email") String email,
                             @RequestParam String pin,
                             Model model,
                             SessionStatus sessionStatus) {

        User user = userService.findByEmail(email);
        VerificationToken tokenEntity = verificationTokenService.findByUser(user);

        if (tokenEntity == null || !tokenEntity.getToken().equals(pin)) {
            model.addAttribute("error", "Invalid PIN. Please try again.");
            model.addAttribute("bodyContent", "verification-form");
            model.addAttribute("pageTitle", "Verify Mail");
            return "master-template";
        }

        userService.enableUser(user);
        sessionStatus.setComplete();

        return "redirect:/login";
    }
}
