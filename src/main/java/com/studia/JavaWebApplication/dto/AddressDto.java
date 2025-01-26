package com.studia.JavaWebApplication.dto;

import jakarta.validation.constraints.NotEmpty;

public class AddressDto {
    private int id;

    @NotEmpty(message = "Miasto jest wymagane")
    private String city;

    @NotEmpty(message = "Kod pocztowy jest wymagany")
    private String postalCode;

    @NotEmpty(message = "Ulica jest wymagana")
    private String street;

    @NotEmpty(message = "Numer domu jest wymagany")
    private String houseNumber;

    public AddressDto() {}

    public AddressDto(String city, String postalCode, String street, String houseNumber) {
        this.city = city;
        this.postalCode = postalCode;
        this.street = street;
        this.houseNumber = houseNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotEmpty(message = "Miasto jest wymagane") String getCity() {
        return city;
    }

    public void setCity(@NotEmpty(message = "Miasto jest wymagane") String city) {
        this.city = city;
    }

    public @NotEmpty(message = "Kod pocztowy jest wymagany") String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(@NotEmpty(message = "Kod pocztowy jest wymagany") String postalCode) {
        this.postalCode = postalCode;
    }

    public @NotEmpty(message = "Ulica jest wymagana") String getStreet() {
        return street;
    }

    public void setStreet(@NotEmpty(message = "Ulica jest wymagana") String street) {
        this.street = street;
    }

    public @NotEmpty(message = "Numer domu jest wymagany") String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(@NotEmpty(message = "Numer domu jest wymagany") String houseNumber) {
        this.houseNumber = houseNumber;
    }

    // Getters and setters...
}