package com.studia.JavaWebApplication.dto;

import com.studia.JavaWebApplication.model.User;
import com.studia.JavaWebApplication.repositories.UserRepository;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@UserEditDto.UniqueEmail
@UserEditDto.UniquePhoneNumber
public class UserEditDto {

    private int id;

    @NotEmpty(message = "Email jest wymagany")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Podaj prawidłowy adres email")
    @Size(min = 5, max = 254, message = "Email może zawierać maksymalnie 254 znaki")
    private String email;

    @NotEmpty(message = "Imię jest wymagane")
    @Size(min = 2, max = 30, message = "Imię musi mieć od 2 do 30 znaków")
    @Pattern(regexp = "^[A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+$", message = "Imię może zawierać tylko litery, bez spacji i znaków specjalnych.")
    private String firstName;

    @NotEmpty(message = "Nazwisko jest wymagane")
    @Size(min = 2, max = 50, message = "Nazwisko musi mieć od 2 do 50 znaków")
    @Pattern(regexp = "^[A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+$", message = "Nazwisko może zawierać tylko litery, bez spacji i znaków specjalnych.")
    private String lastName;

    @NotEmpty(message = "Numer telefonu jest wymagany")
    @Pattern(regexp = "\\d{9}", message = "Proszę wprowadzić 9-cyfrowy numer telefonu bez spacji i znaków specjalnych.")
    private String phoneNumber;

    // Gettery i settery
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

    // Definicja adnotacji dla unikalnego emaila na poziomie klasy
    @Constraint(validatedBy = UniqueEmailValidator.class)
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UniqueEmail {
        String message() default "Email już istnieje w bazie danych";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    // Walidator dla unikalnego emaila
    @Component
    public static class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, UserEditDto> {

        private final UserRepository userRepository;

        @Autowired
        public UniqueEmailValidator(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public boolean isValid(UserEditDto userEditDto, ConstraintValidatorContext context) {
            if (userEditDto == null || userRepository == null) {
                return true; // Pomijamy walidację, jeśli obiekt DTO jest null lub repozytorium nie jest dostępne
            }
            String email = userEditDto.getEmail();
            int userId = userEditDto.getId();

            // Sprawdź, czy email istnieje dla innego użytkownika
            User userWithEmail = userRepository.findByEmail(email);
            if (userWithEmail != null && userWithEmail.getId() != userId) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Email już istnieje w bazie danych")
                        .addPropertyNode("email")
                        .addConstraintViolation();
                return false; // Email jest już używany przez innego użytkownika
            }
            return true;
        }
    }

    // Definicja adnotacji dla unikalnego numeru telefonu na poziomie klasy
    @Constraint(validatedBy = UniquePhoneNumberValidator.class)
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UniquePhoneNumber {
        String message() default "Numer telefonu już istnieje w bazie danych";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    // Walidator dla unikalnego numeru telefonu
    @Component
    public static class UniquePhoneNumberValidator implements ConstraintValidator<UniquePhoneNumber, UserEditDto> {

        private final UserRepository userRepository;

        @Autowired
        public UniquePhoneNumberValidator(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public boolean isValid(UserEditDto userEditDto, ConstraintValidatorContext context) {
            if (userEditDto == null || userRepository == null) {
                return true; // Pomijamy walidację, jeśli obiekt DTO jest null lub repozytorium nie jest dostępne
            }
            String phoneNumber = userEditDto.getPhoneNumber();
            int userId = userEditDto.getId();

            // Sprawdź, czy numer telefonu istnieje dla innego użytkownika
            User userWithPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);
            if (userWithPhoneNumber != null && userWithPhoneNumber.getId() != userId) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Numer telefonu już istnieje w bazie danych")
                        .addPropertyNode("phoneNumber")
                        .addConstraintViolation();
                return false; // Numer telefonu jest już używany przez innego użytkownika
            }
            return true;
        }
    }
}
