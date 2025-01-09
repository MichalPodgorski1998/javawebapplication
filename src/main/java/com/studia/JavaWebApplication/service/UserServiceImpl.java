package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.UserDto;
import com.studia.JavaWebApplication.dto.UserEditDto;
import com.studia.JavaWebApplication.model.User;
import com.studia.JavaWebApplication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public User save(UserDto userDto) {

        User user = new User(
                userDto.getEmail(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getRole(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getPhoneNumber()
        );
        return userRepository.save(user);
    }

//    @Override
//    public Page<UserDto> findAllUsers(Pageable pageable) {
//        Page<User> userPage = userRepository.findAll(pageable);
//        List<UserDto> userDtoList = transferData(userPage.getContent());
//        return new PageImpl<>(userDtoList, pageable, userPage.getTotalElements());
//    }

    @Override
    public Page<UserDto> findAllUsers(Pageable pageable, String loggedInEmail) {
        Page<User> userPage = userRepository.findAllByEmailNot(loggedInEmail, pageable);
        List<UserDto> userDtoList = transferData(userPage.getContent());
        return new PageImpl<>(userDtoList, pageable, userPage.getTotalElements());
    }


    @Override
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto findUserById(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDto(user.getId(),
                           user.getEmail(),
                           user.getPassword(),
                           user.getRole(),
                           user.getFirstName(),
                           user.getLastName(),
                           user.getPhoneNumber());
    }


    @Override
    public UserDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return new UserDto(user.getId(), user.getEmail(), user.getPassword(), user.getRole(), user.getFirstName(), user.getLastName(), user.getPhoneNumber());
    }

//    @Override
//    public void updateUser(UserDto userDto) {
//        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new RuntimeException("User not found"));
//        user.setFirstName(userDto.getFirstName());
//        user.setLastName(userDto.getLastName());
//        user.setEmail(userDto.getEmail());
//        user.setPhoneNumber(userDto.getPhoneNumber());
//        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
//            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        }
//        userRepository.save(user);
//    }
    @Override
    public void updateUser(UserEditDto UserEditDto) {
        User user = userRepository.findById(UserEditDto.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(UserEditDto.getFirstName());
        user.setLastName(UserEditDto.getLastName());
        user.setEmail(UserEditDto.getEmail());
        user.setPhoneNumber(UserEditDto.getPhoneNumber());
        userRepository.save(user);
    }

    public void updatePassword(int userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private List<UserDto> transferData(List<User> users) {
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user: users) {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setEmail(user.getEmail());
            userDto.setPhoneNumber(user.getPhoneNumber());
            userDto.setRole(user.getRole());
            userDtoList.add(userDto);
        }
        return userDtoList;
    }
}
