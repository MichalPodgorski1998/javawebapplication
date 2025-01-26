package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.UserDto;
import com.studia.JavaWebApplication.dto.UserEditDto;
import com.studia.JavaWebApplication.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User save(UserDto userDto);
    Page<UserDto> findAllUsers(Pageable pageable, String loggedInEmail);

    int getUserPagePosition(User savedUser, int size, String loggedInEmail);

    void deleteUser(int id);
    UserDto findUserById(int id);
    UserDto findUserByEmail(String email);
/*
    void updateUser(UserDto userDto);
*/
    void updateUser(UserEditDto userEditDto);
    void updatePassword(int userId, String newPassword);
}
