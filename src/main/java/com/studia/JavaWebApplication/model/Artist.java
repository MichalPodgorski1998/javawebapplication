package com.studia.JavaWebApplication.model;

import jakarta.persistence.*;

@Entity
@Table(name = "artists", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    public Artist() {}

    public Artist(String name) {
        this.name = name;
    }

    // Gettery i Settery
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
