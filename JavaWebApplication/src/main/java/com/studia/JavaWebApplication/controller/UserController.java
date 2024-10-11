package com.studia.JavaWebApplication.controller;

import com.studia.JavaWebApplication.dto.UserDto;
import com.studia.JavaWebApplication.model.User;
import com.studia.JavaWebApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/registration")
    public String getRegistrationPage(@ModelAttribute("user") UserDto userDto) {
        return "register";
    }

    @PostMapping("/registration")
    public String saveUser(@ModelAttribute("user") UserDto userDto, RedirectAttributes redirectAttributes) {
        userService.save(userDto);
        redirectAttributes.addFlashAttribute("message", "Zarejestrowano pomyśnie!");
        return "redirect:/registration";
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = ((UserDetails) authentication.getPrincipal()).getUsername();

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDto> userPage = userService.findAllUsers(pageable);

        List<UserDto> users = new ArrayList<>(userPage.getContent());

        users.removeIf(user -> user.getEmail().equals(loggedInEmail));

        model.addAttribute("users", users);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("user", new UserDto());
        return "users";
    }

    @GetMapping("/users/add")
    public String getAddUserPage(@ModelAttribute("user") UserDto userDto) {
        return "add-user";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute("user") UserDto userDto,
                          RedirectAttributes redirectAttributes) {
        userService.save(userDto);

        redirectAttributes.addFlashAttribute("message", "Użytkownik dodany pomyślnie!");

        return "redirect:/users";
    }

    //DO MODALNEGO OKNA EDYCJI
    @GetMapping("/users/{id}")
    @ResponseBody
    public UserDto getUserById(@PathVariable("id") int id) {
        return userService.findUserById(id);
    }

    @GetMapping("/users/edit/{id}")
    public String getEditUserPage(@PathVariable("id") int id, Model model) {
        UserDto userDto = userService.findUserById(id);
        model.addAttribute("user", userDto);
        return "edit-user";
    }

    @PostMapping("/users/edit")
    public String editUser(@ModelAttribute("user") UserDto userDto,
                           RedirectAttributes redirectAttributes) {

        //MODAL USER EDIT - ŻEBY NIE USTAWIAŁ ROLI NA NULL
        UserDto existingUser = userService.findUserById(userDto.getId());
        if (userDto.getRole() == null || userDto.getRole().isEmpty()) {
            userDto.setRole(existingUser.getRole());
        }

        userService.updateUser(userDto);
        redirectAttributes.addFlashAttribute("message", "Użytkownik zaktualizowany pomyślnie!");
        return "redirect:/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") int id,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             RedirectAttributes redirectAttributes) {

        userService.deleteUser(id);

        Page<UserDto> userPage = userService.findAllUsers(PageRequest.of(page, size));
        boolean isLastPage = userPage.getNumberOfElements() == 0 && userPage.getTotalPages() > 1;

        int targetPage = isLastPage && page > 0 ? page - 1 : page;

        redirectAttributes.addFlashAttribute("message", "Użytkownik usunięty pomyślnie!");
        return "redirect:/users?page=" + targetPage + "&size=" + size;
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



