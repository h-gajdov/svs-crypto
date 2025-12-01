package com.svsbrains.svscrypto.web.controler;

import com.svsbrains.svscrypto.model.User;
import com.svsbrains.svscrypto.service.UserService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String logIn(Model model){
        model.addAttribute("bodyContent","log-in-form");
        return "master-template";
    }
    @PostMapping("/login")
    public String logInUser(@RequestParam String username,@RequestParam String password,Model model){
        User user=userService.logInUserByUsername(username,password);
        if(user==null) return "redirect:/login?error";
        model.addAttribute("user",user);
        return "redirect:/dashboard";
    }
    @GetMapping("/signin")
    public String signIn(Model model){
        model.addAttribute("bodyContent","sign-in-form");
        return "master-template";
    }
    @PostMapping("/signin")
    public String signInUser(@RequestParam String firstname,@RequestParam String lastname,
                             @RequestParam String email,@RequestParam String username,
                             @RequestParam String password, Model model){
        userService.signInUser(username,firstname,lastname,email,password);
        return "redirect:/login";
    }
}
