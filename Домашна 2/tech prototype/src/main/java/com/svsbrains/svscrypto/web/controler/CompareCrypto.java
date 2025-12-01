package com.svsbrains.svscrypto.web.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/compare-crypto")
public class CompareCrypto {

    @GetMapping
    public String getCompareCrypto(Model model) {
        model.addAttribute("bodyContent", "compare-crypto");
        return "master-template";
    }
}
