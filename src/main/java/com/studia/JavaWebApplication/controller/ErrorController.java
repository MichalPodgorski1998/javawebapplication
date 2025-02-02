package com.studia.JavaWebApplication.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
//public class ErrorController {
//
//    @GetMapping("/access-denied")
//    public String accessDenied() {
//        return "errors/access-denied"; // Nazwa widoku (np. Thymeleaf)
//    }
//}


@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/access-denied")
    public String handleAccessDenied() {
        return "access-denied"; // Plik access-denied.html w templates
    }
}