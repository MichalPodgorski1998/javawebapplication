package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.UserDto;
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
                userDto.getEmail(),                                 // email
                passwordEncoder.encode(userDto.getPassword()),      // password
                userDto.getRole(),                                  // role
                userDto.getFirstName(),                             // firstName
                userDto.getLastName(),                              // lastName
                userDto.getPhoneNumber()                            // phoneNumber
        );
        return userRepository.save(user);
    }

    @Override
    public Page<UserDto> findAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserDto> userDtoList = transferData(userPage.getContent());
        return new PageImpl<>(userDtoList, pageable, userPage.getTotalElements());
    }

    @Override
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
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
