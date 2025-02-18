package com.studia.JavaWebApplication.repositories;

import com.studia.JavaWebApplication.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Page<User> findAllByEmailNot(String email, Pageable pageable);
    User findByEmail (String email);
    User findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Page<User> findAll(Pageable pageable);
    boolean existsByAddress_Id(int addressId);
    long countByIdLessThanAndEmailNot(int id, String email);
}
