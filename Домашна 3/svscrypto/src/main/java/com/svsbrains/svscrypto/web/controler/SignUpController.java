package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.model.VerificationToken;
import com.svsbrains.svscrypto.service.VerificationTokenService;
import com.svsbrains.svscrypto.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@SessionAttributes("email")
@Controller
public class SignUpController {
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;

    public SignUpController(UserService userService, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
    }

    @GetMapping("/signin")
    public String signIn(Model model) {
        model.addAttribute("bodyContent", "sign-in-form");
        model.addAttribute("pageTitle", "Register");
        return "master-template";
    }

    @PostMapping("/signin")
    public String signInUser(@RequestParam String firstname, @RequestParam String lastname,
                             @RequestParam String email, @RequestParam String username,
                             @RequestParam String password, Model model) {
        User user = userService.signInUser(username, firstname, lastname, email, password);
        userService.sendVerificationMail(user);
        model.addAttribute("email", user.getEmail());
        return "redirect:/verify";
    }

    @GetMapping("/verify")
    public String showVerifyPage(Model model) {
        if (!model.containsAttribute("email")) {
            return "redirect:/signin";
        }

        model.addAttribute("bodyContent", "verification-form");
        model.addAttribute("pageTitle", "Verify Mail");
        return "master-template";
    }

    @PostMapping("/verify")
    public String verifyUser(@ModelAttribute("email") String email,
                             @RequestParam String pin,
                             Model model,
                             SessionStatus sessionStatus) {

        User user = userService.findByEmail(email);
        VerificationToken tokenEntity = verificationTokenService.findByUser(user);

        if (tokenEntity == null || !tokenEntity.getToken().equals(pin)) {
            model.addAttribute("error", "Invalid PIN. Please try again.");
            model.addAttribute("bodyContent", "verification-form");
            return "master-template";
        }

        user.setEnabled(true);
        userService.save(user);

        sessionStatus.setComplete();

        return "redirect:/login";
    }
}
