package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.AddressDto;
import com.studia.JavaWebApplication.model.Address;
import com.studia.JavaWebApplication.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class CustomUserDetail implements UserDetails {

    private User user;

    public CustomUserDetail(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> user.getRole());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }
    public String getPhoneNumber() {
        return user.getPhoneNumber();
    }

    public Address getAddress() { // Dodaj getter dla adresu
        return user.getAddress();
    }

    public String getRole() {
        return user.getRole();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
