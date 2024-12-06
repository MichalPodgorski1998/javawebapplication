package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.MusicCategoryDTO;
import com.studia.JavaWebApplication.model.MusicCategory;

import java.util.List;

public interface MusicCategoryService {
    List<MusicCategoryDTO> findAllMusicCategory();
    MusicCategory save(MusicCategory musicCategory);
    MusicCategory update(MusicCategory musicCategory);
    MusicCategory findById(Long id);
    void deleteById(Long id);
}
