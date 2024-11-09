package com.studia.JavaWebApplication.dto;

import com.studia.JavaWebApplication.repositories.UserRepository;
import jakarta.persistence.Column;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class UserDto {

    private int id;

    @NotEmpty(message = "Email jest wymagany")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Podaj prawidłowy adres email")
    @Size(min =5, max = 254, message = "Email może zawierać maxymalnie 254 znaki")
    @UniqueEmail(message = "Email już istnieje w bazie danych")
    private String email;

    @NotEmpty(message = "Hasło jest wymagane")
    @Size(min = 3, max = 32, message = "Hasło musi mieć od 8 do 32 znaków")
    @Pattern(regexp = "^[A-Za-z0-9@$!%*?&]+$", message = "Hasło może zawierać tylko litery (bez polskich znaków), cyfry i znaki @$!%*?&")
    private String password;

    private String role;

    @NotEmpty(message = "Imię jest wymagane")
    @Size(min =2, max = 30, message = "Imię musi mieć od 2 do 30 znaków")
    @Pattern(regexp = "^[A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+$", message = "Imię może zawierać tylko litery, bez spacji i znaków specjalnych.")
    private String firstName;

    @NotEmpty(message = "Nazwisko jest wymagane")
    @Size(min = 2, max = 50, message = "Nazwisko musi mieć od 2 do 50 znaków")
    @Pattern(regexp = "^[A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+$", message = "Nazwisko może zawierać tylko litery, bez spacji i znaków specjalnych.")
    private String lastName;

    @NotEmpty(message = "Numer telefonu jest wymagany")
    @Pattern(regexp = "\\d{9}", message = "Proszę wprowadzić 9-cyfrowy numer telefonu bez spacji i znaków specjalnych.")
    @UniquePhoneNumber(message = "Numer telefonu już istnieje w bazie danych")
    private String phoneNumber;

    public UserDto() {
    }

    public UserDto(int id, String email, String password, String role, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    // Definicja niestandardowej adnotacji dla unikalnego emaila
    @Constraint(validatedBy = UniqueEmailValidator.class)
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UniqueEmail {
        String message() default "Email już istnieje w bazie danych";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    // Walidator dla unikalnego emaila
    public static class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

        @Autowired
        private UserRepository userRepository;

        @Override
        public boolean isValid(String email, ConstraintValidatorContext context) {
            if (email == null || userRepository == null) {
                return true; // Pomijamy walidację, jeśli email jest null lub repozytorium nie jest dostępne
            }
            return !userRepository.existsByEmail(email);
        }
    }

    // Definicja niestandardowej adnotacji dla unikalnego numeru telefonu
    @Constraint(validatedBy = UniquePhoneNumberValidator.class)
    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UniquePhoneNumber {
        String message() default "Numer telefonu już istnieje w bazie danych";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    // Walidator dla unikalnego numeru telefonu
    public static class UniquePhoneNumberValidator implements ConstraintValidator<UniquePhoneNumber, String> {

        @Autowired
        private UserRepository userRepository;

        @Override
        public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
            if (phoneNumber == null || userRepository == null) {
                return true; // Pomijamy walidację, jeśli numer telefonu jest null lub repozytorium nie jest dostępne
            }
            return !userRepository.existsByPhoneNumber(phoneNumber);
        }
    }
}
