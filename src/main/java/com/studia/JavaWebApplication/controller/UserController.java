package com.studia.JavaWebApplication.controller;

import com.studia.JavaWebApplication.dto.PasswordChangeDto;
import com.studia.JavaWebApplication.dto.UserDto;
import com.studia.JavaWebApplication.dto.UserEditDto;
import com.studia.JavaWebApplication.dto.UsersPasswordChangeDto;
import com.studia.JavaWebApplication.model.User;
import com.studia.JavaWebApplication.service.CustomUserDetail;
import com.studia.JavaWebApplication.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model, RedirectAttributes redirectAttributes) {
        if (error != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nieprawidłowy adres e-mail lub hasło!");
            return "redirect:/login"; // przekierowanie z atrybutem flash
        }
        if (logout != null) {
            redirectAttributes.addFlashAttribute("logoutMessage", "Zostałeś wylogowany pomyślnie!");
            return "redirect:/login"; // przekierowanie z atrybutem flash
        }
        return "login";
    }

    @GetMapping("/registration")
    public String getRegistrationPage(@ModelAttribute("user") UserDto userDto) {
        return "register";
    }


    @PostMapping("/registration")
    public String saveUser(@ModelAttribute("user") @Valid UserDto userDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "register";  // Zwróć formularz rejestracyjny z błędami
        }

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
    public String addUser(@ModelAttribute("user") @Valid UserDto userDto,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "add-user";  // Zwraca formularz z błędami walidacji
        }

        userService.save(userDto);
        redirectAttributes.addFlashAttribute("message", "Użytkownik dodany pomyślnie!");
        return "redirect:/users";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/edit/{id}")
    public String getEditUserPage(@PathVariable("id") int id, Model model) {
        UserDto userDto = userService.findUserById(id);
        model.addAttribute("user", userDto);
        model.addAttribute("passwordForm", new PasswordChangeDto());
        return "edit-user";
    }

    @PostMapping("/users/edit")
    public String editUser(@ModelAttribute("user") @Valid UserEditDto UserEditDto,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // Przekazujemy obiekt userDto z błędami do widoku
            model.addAttribute("user", UserEditDto);
            model.addAttribute("passwordForm", new PasswordChangeDto());
            // Ponownie wyświetlamy formularz edycji z błędami walidacji
            return "edit-user";
        }

        userService.updateUser(UserEditDto);
        redirectAttributes.addFlashAttribute("message", "Użytkownik zaktualizowany pomyślnie!");
        return "redirect:/users";
    }

    @PostMapping("/users/update-password")
    public String updatePassword(
            @ModelAttribute("passwordForm") @Valid UsersPasswordChangeDto usersPasswordChangeDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @RequestParam("userId") int userId,
            Model model) {
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("passwordMatching")) {
                // Pobierz komunikat błędu z walidacji
                String errorMessage = bindingResult.getFieldError("passwordMatching").getDefaultMessage();
                model.addAttribute("errorMessage", errorMessage);
            }
            // Pobierz dane użytkownika i przekaż do modelu
            UserDto userDto = userService.findUserById(userId);
            model.addAttribute("user", userDto);
            model.addAttribute("passwordForm", usersPasswordChangeDto);
            // Zwróć widok formularza z błędami
            return "edit-user"; // Upewnij się, że nazwa widoku jest poprawna
        }

        // Aktualizacja hasła po pomyślnej walidacji
        userService.updatePassword(userId, usersPasswordChangeDto.getNewPassword());
        redirectAttributes.addFlashAttribute("message", "Hasło zostało zmienione pomyślnie!");
        return "redirect:/users";
    }

    @GetMapping("/profile-edit")
    public String editProfile(Model model, @AuthenticationPrincipal CustomUserDetail userDetail) {
        // Pobierz dane użytkownika na podstawie zalogowanego użytkownika
        UserDto userDto = userService.findUserByEmail(userDetail.getUsername());
        model.addAttribute("user", userDto);
        model.addAttribute("passwordForm", new PasswordChangeDto());
        return "profile-edit";
    }


    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") @Valid UserEditDto userEditDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "profile-edit";  // Zwróć widok formularza edycji profilu z błędami walidacji
        }

        userService.updateUser(userEditDto);

        // Aktualizuj szczegóły uwierzytelnienia użytkownika
        UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(userEditDto.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                updatedUserDetails,
                updatedUserDetails.getPassword(),
                updatedUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        redirectAttributes.addFlashAttribute("message", "Profil zaktualizowany pomyślnie!");
        return "redirect:/profile";
    }

    @GetMapping("/profile/change-password")
    public String getPasswordChangePage(Model model) {
        model.addAttribute("passwordForm", new PasswordChangeDto());
        return "password-update"; // Zwróć widok formularza zmiany hasła
    }

    @PostMapping("/profile/update-password")
    public String updatePassword(@ModelAttribute("passwordForm") @Valid PasswordChangeDto passwordChangeDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 @AuthenticationPrincipal CustomUserDetail userDetail) {
        if (bindingResult.hasErrors()) {
            return "password-update"; // Zwróć formularz, jeśli wystąpiły błędy walidacji
        }

        // Pobierz dane użytkownika na podstawie emaila z userDetail
        UserDto userDto = userService.findUserByEmail(userDetail.getUsername());

        // Sprawdź, czy bieżące hasło użytkownika jest poprawne
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), userDto.getPassword())) {
            bindingResult.rejectValue("currentPassword", "error.passwordForm", "Aktualne hasło jest nieprawidłowe.");
            return "password-update"; // Zwróć formularz, jeśli bieżące hasło jest niepoprawne
        }

        // Zaktualizuj hasło, kodując je przed zapisem
        userService.updatePassword(userDto.getId(), passwordChangeDto.getNewPassword());

        // Przekierowanie z komunikatem o sukcesie
        redirectAttributes.addFlashAttribute("message", "Hasło zostało zmienione pomyślnie!");
        return "redirect:/profile"; // Przekierowanie na stronę profilu po udanej zmianie hasła
    }
    @GetMapping("/users/details/{id}")
    public String userDetails(@PathVariable("id") int id, Model model) {
        // Zakładamy, że użytkownik zawsze istnieje
        UserDto userDto = userService.findUserById(id);
        model.addAttribute("user", userDto);
        return "userDetails"; // Nazwa szablonu Thymeleaf
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

    @GetMapping("/profile")
    public String userProfile(Model model, @AuthenticationPrincipal CustomUserDetail userDetail) {
        model.addAttribute("user", userDetail);
        return "profile";
    }
}



