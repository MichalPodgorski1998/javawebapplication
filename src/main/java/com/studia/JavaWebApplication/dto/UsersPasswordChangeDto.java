package com.studia.JavaWebApplication.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UsersPasswordChangeDto {

    @NotEmpty(message = "Nowe hasło jest wymagane")
    @Size(min = 8, message = "Nowe hasło musi mieć co najmniej 8 znaków")
    private String newPassword;

    @NotEmpty(message = "Potwierdzenie nowego hasła jest wymagane")
    private String confirmNewPassword;

    @AssertTrue(message = "Nowe hasło i potwierdzenie muszą się zgadzać")
    public boolean isPasswordMatching() {
        // Zwraca true, jeśli oba pola są null lub gdy są równe
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }

    // Getters and setters
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
