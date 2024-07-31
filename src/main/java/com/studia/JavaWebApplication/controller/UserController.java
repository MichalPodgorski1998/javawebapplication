package com.studia.JavaWebApplication.controller;

import com.studia.JavaWebApplication.dto.UserDto;
import com.studia.JavaWebApplication.model.User;
import com.studia.JavaWebApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(Principal principal) {
        return "index";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        return "redirect:/users";
    }

//    @GetMapping("/users")
//    public String users(Model model) {
//        List<UserDto> userList = userService.findAllUsers(); // Metoda do pobierania wszystkich użytkowników
//        model.addAttribute("users", userList);
//        return "users"; // Widok Thymeleaf dla listy użytkowników
//    }

//    @GetMapping("/users")
//    public String users(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails loggedInUser = (UserDetails) authentication.getPrincipal();
//        String loggedInUsername = loggedInUser.getUsername();
//
//        List<UserDto> users = userService.findAllUsers();
//        model.addAttribute("loggedInUser", loggedInUsername);
//        model.addAttribute("users", users);
//        return "users";
//    }

    @GetMapping("/users")
    public String users(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        // Pobieranie listy użytkowników
        List<UserDto> users = userService.findAllUsers();

        // Usunięcie zalogowanego użytkownika, jeśli jest obecny
        users.removeIf(user -> user.getEmail().equals(loggedInEmail));

        // Dodawanie listy użytkowników do modelu
        model.addAttribute("users", users);
        model.addAttribute("message", users.isEmpty() ? "Brak użytkowników do wyświetlenia." : null);

        return "users";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Widok logowania
    }

    @GetMapping("/registration")
    public String getRegistrationPage(@ModelAttribute("user") UserDto userDto) {
        return "register";
    }

    @PostMapping("/registration")
    public String saveUser(@ModelAttribute("user") UserDto userDto, RedirectAttributes redirectAttributes) {
        userService.save(userDto);
        redirectAttributes.addFlashAttribute("message", "Registered Successfully!");
        return "redirect:/registration";
    }

    @GetMapping("user-page")
    public String userPage (Model model, Principal principal) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        model.addAttribute("user", userDetails);
        return "user";
    }

    @GetMapping("admin-page")
    public String adminPage (Model model, Principal principal) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
        model.addAttribute("user", userDetails);
        return "admin";
    }

}



