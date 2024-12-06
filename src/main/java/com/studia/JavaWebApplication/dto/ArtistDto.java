package com.studia.JavaWebApplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ArtistDto {
    private Long id;

    @NotBlank(message = "Nazwa artysty nie może być pusta")
    @Size(min = 2, max = 50, message = "Nazwa artysty musi mieć od 2 do 50 znaków")
    private String name;

    public ArtistDto() {}

    public ArtistDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
