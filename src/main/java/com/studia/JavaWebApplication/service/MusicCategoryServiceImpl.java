package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.MusicCategoryDTO;
import com.studia.JavaWebApplication.model.MusicCategory;
import com.studia.JavaWebApplication.repositories.MusicCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicCategoryServiceImpl implements MusicCategoryService {

    @Autowired
    private MusicCategoryRepository musicCategoryRepository;

    @Override
    public List<MusicCategoryDTO> findAllMusicCategory() {
        return musicCategoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(musicCategory -> new MusicCategoryDTO(musicCategory.getId(), musicCategory.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public MusicCategory save(MusicCategory musicCategory) {
        MusicCategory musicCategorySave = new MusicCategory(
                musicCategory.getName()
        );
        return musicCategoryRepository.save(musicCategorySave);
    }

    @Override
    public MusicCategory update(MusicCategory musicCategory) {
        return musicCategoryRepository.save(musicCategory);
    }

    @Override
    public MusicCategory findById(Long id) {
        return musicCategoryRepository.findById(id).get();
    }

    @Override
    public void deleteById(Long id) {
        musicCategoryRepository.deleteById(id);
    }
}
