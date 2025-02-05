package com.studia.JavaWebApplication.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController {

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied"; // Musi być w templates/access-denied.html
    }
}