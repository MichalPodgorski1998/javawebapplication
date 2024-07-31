package com.studia.JavaWebApplication.repositories;

import com.studia.JavaWebApplication.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail (String email);
    Page<User> findAll(Pageable pageable);
}
