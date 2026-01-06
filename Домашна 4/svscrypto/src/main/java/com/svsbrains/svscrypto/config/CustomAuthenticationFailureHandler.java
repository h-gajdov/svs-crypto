package com.svsbrains.svscrypto.config;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private final UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");

        if (exception instanceof DisabledException) {
            User user = userService.findByUsername(username);
            if (user != null) {
                userService.sendVerificationMail(user);

                String targetUrl = "/verify?email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8);
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
                return;
            }
        }

        setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
