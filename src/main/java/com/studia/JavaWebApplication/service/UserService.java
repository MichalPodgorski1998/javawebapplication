package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.UserDto;
import com.studia.JavaWebApplication.model.User;

import java.util.List;

public interface UserService {
    User save(UserDto userDto);
    List<UserDto> findAllUsers();
    void deleteUser(int id);

}
