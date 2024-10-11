package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.UserDto;
import com.studia.JavaWebApplication.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User save(UserDto userDto);
    Page<UserDto> findAllUsers(Pageable pageable);
    void deleteUser(int id);
    UserDto findUserById(int id);
    void updateUser(UserDto userDto);
}