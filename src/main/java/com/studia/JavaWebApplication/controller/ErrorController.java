package com.studia.JavaWebApplication.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "errors/access-denied"; // Nazwa widoku (np. Thymeleaf)
    }
}