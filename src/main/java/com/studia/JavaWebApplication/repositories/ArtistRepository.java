package com.studia.JavaWebApplication.repositories;

import com.studia.JavaWebApplication.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
