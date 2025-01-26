package com.studia.JavaWebApplication.repositories;

import com.studia.JavaWebApplication.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    Optional<Address> findByCityAndPostalCodeAndStreetAndHouseNumber(String city, String postalCode, String street, String houseNumber);
}