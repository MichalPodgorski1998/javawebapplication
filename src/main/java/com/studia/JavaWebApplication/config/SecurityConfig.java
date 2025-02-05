package com.studia.JavaWebApplication.config;

import com.studia.JavaWebApplication.service.CustomSuccessHandler;
import com.studia.JavaWebApplication.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    CustomSuccessHandler customSuccessHandler;

    @Autowired
    CustomUserDetailService customUserDetailService;

    @Autowired
    CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(c -> c.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin-page", "/users/**", "/products/**","/ordersAdministration/**").hasAuthority("ADMIN")
                        .requestMatchers("/user-page").hasAuthority("USER")
                        .requestMatchers("/orders/**").authenticated()
                        .requestMatchers("/", "/img/**", "/css/**", "/cart/**", "/cart/add/**", "/shop", "/shop/details/**", "/access-denied").permitAll()
                        .requestMatchers("/registration", "/login").anonymous()
                        .anyRequest().authenticated())

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(customSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .logout(form -> form
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // Konfiguracja zarządzania sesją
                )

                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(customAccessDeniedHandler));
        return http.build();
    }

    @Autowired
    public void configure (AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailService).passwordEncoder(passwordEncoder());
    }
}

