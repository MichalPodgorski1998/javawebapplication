package com.studia.JavaWebApplication.controller;

import com.studia.JavaWebApplication.dto.UserDto;
import com.studia.JavaWebApplication.model.User;
import com.studia.JavaWebApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
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

//    @GetMapping("/users/delete/{id}")
//    public String deleteUser(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
//        userService.deleteUser(id);
//        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
//        return "redirect:/users";
//    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") int id,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             RedirectAttributes redirectAttributes) {

        // Usuwanie użytkownika
        userService.deleteUser(id);

        // Sprawdzanie liczby użytkowników na aktualnej stronie
        Page<UserDto> userPage = userService.findAllUsers(PageRequest.of(page, size));
        boolean isLastPage = userPage.getNumberOfElements() == 0 && userPage.getTotalPages() > 1;

        // Przechodzenie do poprzedniej strony, jeśli to konieczne
        int targetPage = isLastPage && page > 0 ? page - 1 : page;

        // Dodawanie wiadomości o sukcesie i przekierowywanie
        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        return "redirect:/users?page=" + targetPage + "&size=" + size;
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        // Tworzenie obiektu Pageable
        Pageable pageable = PageRequest.of(page, size);

        // Pobieranie strony użytkowników
        Page<UserDto> userPage = userService.findAllUsers(pageable);

        // Konwertowanie Page<UserDto> na mutowalną listę
        List<UserDto> users = new ArrayList<>(userPage.getContent());

        // Usuwanie zalogowanego użytkownika, jeśli jest obecny
        users.removeIf(user -> user.getEmail().equals(loggedInEmail));

        // Dodawanie listy użytkowników do modelu
        model.addAttribute("users", users);
        model.addAttribute("message", users.isEmpty() ? "Brak użytkowników do wyświetlenia." : null);

        // Dodawanie informacji o paginacji do modelu
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());

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



