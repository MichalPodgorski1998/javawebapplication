package com.studia.JavaWebApplication.model;

import jakarta.persistence.*;

@Entity
@Table(name = "music_category", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class MusicCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_category_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    public MusicCategory(String name) {
        this.name = name;
    }

    public MusicCategory() {

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
