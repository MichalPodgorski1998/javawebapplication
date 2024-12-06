package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.ArtistDto;
import com.studia.JavaWebApplication.model.Artist;

import java.util.List;

public interface ArtistService {
    List<ArtistDto> findAllArtists();
    Artist findById(Long id);
    Artist save(Artist artist);
    Artist update(Artist artist);
    void deleteById(Long id);
}
