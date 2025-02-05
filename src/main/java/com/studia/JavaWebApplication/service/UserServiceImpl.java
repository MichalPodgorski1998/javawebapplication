package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.AddressDto;
import com.studia.JavaWebApplication.dto.UserDto;
import com.studia.JavaWebApplication.dto.UserEditDto;
import com.studia.JavaWebApplication.model.Address;
import com.studia.JavaWebApplication.model.User;
import com.studia.JavaWebApplication.repositories.AddressRepository;
import com.studia.JavaWebApplication.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public User save(UserDto userDto) {

        Address address = null;
        // Sprawdź, czy adres już istnieje w bazie danych
        if (userDto.getAddress() != null) {
            Optional<Address> existingAddress = addressRepository.findByCityAndPostalCodeAndStreetAndHouseNumber(
                    userDto.getAddress().getCity(),
                    userDto.getAddress().getPostalCode(),
                    userDto.getAddress().getStreet(),
                    userDto.getAddress().getHouseNumber()
            );

            // Jeśli adres istnieje, użyj go, w przeciwnym razie zapisz nowy
            if (existingAddress.isPresent()) {
                address = existingAddress.get();
            } else {
                address = new Address(
                        userDto.getAddress().getCity(),
                        userDto.getAddress().getPostalCode(),
                        userDto.getAddress().getStreet(),
                        userDto.getAddress().getHouseNumber()
                );
                address = addressRepository.save(address);
            }
        }
        // Utwórz użytkownika i przypisz adres
        User user = new User(
                userDto.getId(),
                userDto.getEmail(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getRole(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPhoneNumber(),
                address
        );
        return userRepository.save(user);
    }

    @Override
    public Page<UserDto> findAllUsers(Pageable pageable, String loggedInEmail) {
        Page<User> userPage = userRepository.findAllByEmailNot(loggedInEmail, pageable);
        List<UserDto> userDtoList = transferData(userPage.getContent());
        return new PageImpl<>(userDtoList, pageable, userPage.getTotalElements());
    }

    @Override
    public int getUserPagePosition(User savedUser, int pageSize, String loggedInEmail) {
        long position = userRepository.countByIdLessThanAndEmailNot(savedUser.getId(), loggedInEmail);
        return (int) (position / pageSize);
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        // Pobierz użytkownika, aby uzyskać jego adres
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Przechowaj ID adresu przypisanego do użytkownika
        Address address = user.getAddress();

        // Usuń użytkownika
        userRepository.delete(user);

        // Jeśli adres istnieje i nie jest używany przez żadnego użytkownika, usuń go
        if (address != null && !userRepository.existsByAddress_Id(address.getId())) {
            addressRepository.delete(address);
        }
    }

    @Override
    public UserDto findUserById(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        // Konwersja Address na AddressDto
        AddressDto addressDto = null;
        if (user.getAddress() != null) {
            addressDto = new AddressDto(
                    user.getAddress().getCity(),
                    user.getAddress().getPostalCode(),
                    user.getAddress().getStreet(),
                    user.getAddress().getHouseNumber()
            );
        }

        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                addressDto // Przekazanie AddressDto
        );
    }


    @Override
    public UserDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        // Konwersja Address na AddressDto
        AddressDto addressDto = null;
        if (user.getAddress() != null) {
            addressDto = new AddressDto(
                    user.getAddress().getCity(),
                    user.getAddress().getPostalCode(),
                    user.getAddress().getStreet(),
                    user.getAddress().getHouseNumber()
            );
        }

        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                addressDto // Przekazanie AddressDto
        );
    }

    @Override
    public User mapToUserEntity(UserDto userDto) {
        Address address = null;
        if (userDto.getAddress() != null) {
            address = new Address(
                    userDto.getAddress().getCity(),
                    userDto.getAddress().getPostalCode(),
                    userDto.getAddress().getStreet(),
                    userDto.getAddress().getHouseNumber()
            );
        }

        return new User(
                userDto.getId(),
                userDto.getEmail(),
                userDto.getPassword(),
                userDto.getRole(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPhoneNumber(),
                address
        );
    }

    @Override
    public void updateUser(UserEditDto userEditDto) {
        User user = userRepository.findById(userEditDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(userEditDto.getFirstName());
        user.setLastName(userEditDto.getLastName());
        user.setEmail(userEditDto.getEmail());
        user.setPhoneNumber(userEditDto.getPhoneNumber());

        Address oldAddress = user.getAddress();

        // Obsługa aktualizacji adresu
        if (userEditDto.getAddress() != null) {
            Address address = null;

            // Sprawdź, czy adres istnieje w bazie
            Optional<Address> existingAddress = addressRepository.findByCityAndPostalCodeAndStreetAndHouseNumber(
                    userEditDto.getAddress().getCity(),
                    userEditDto.getAddress().getPostalCode(),
                    userEditDto.getAddress().getStreet(),
                    userEditDto.getAddress().getHouseNumber()
            );

            // Jeśli adres istnieje, użyj go, w przeciwnym razie zapisz nowy
            if (existingAddress.isPresent()) {
                address = existingAddress.get();
            } else {
                address = new Address(
                        userEditDto.getAddress().getCity(),
                        userEditDto.getAddress().getPostalCode(),
                        userEditDto.getAddress().getStreet(),
                        userEditDto.getAddress().getHouseNumber()
                );
                address = addressRepository.save(address);
            }

            user.setAddress(address);
        } else {
            // Jeśli w edytowanym DTO adres jest pusty, usuwamy przypisanie
            user.setAddress(null);
        }

        userRepository.save(user);

        if (oldAddress != null && !userRepository.existsByAddress_Id(oldAddress.getId())) {
            addressRepository.delete(oldAddress);
        }
    }

    public void updatePassword(int userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    private List<UserDto> transferData(List<User> users) {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : users) {
            AddressDto addressDto = null;
            if (user.getAddress() != null) {
                addressDto = new AddressDto(
                        user.getAddress().getCity(),
                        user.getAddress().getPostalCode(),
                        user.getAddress().getStreet(),
                        user.getAddress().getHouseNumber()
                );
            }
            UserDto userDto = new UserDto(
                    user.getId(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getRole(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumber(),
                    addressDto // Przekazanie AddressDto
            );
            userDtoList.add(userDto);
        }
        return userDtoList;
    }
}