package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.ArtistDto;
import com.studia.JavaWebApplication.dto.MusicCategoryDTO;
import com.studia.JavaWebApplication.model.Artist;
import com.studia.JavaWebApplication.repositories.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistServiceImpl implements ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Override
    public List<ArtistDto> findAllArtists() {
        return artistRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(artist -> new ArtistDto(artist.getId(), artist.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Artist save(Artist artist) {
        Artist artistSave = new Artist(
                artist.getName()
        );
        return artistRepository.save(artistSave);
    }

    @Override
    public Artist update(Artist artist) {
        return artistRepository.save(artist);
    }

    @Override
    public Artist findById(Long id) {
        return artistRepository.findById(id).get();
    }
    @Override
    public void deleteById(Long id) {
        artistRepository.deleteById(id);
    }
}
