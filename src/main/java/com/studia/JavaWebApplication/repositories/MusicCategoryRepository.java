package com.studia.JavaWebApplication.repositories;

import com.studia.JavaWebApplication.model.MusicCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicCategoryRepository extends JpaRepository<MusicCategory, Long> {

}
