package com.studia.JavaWebApplication.repositories;

import com.studia.JavaWebApplication.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p.title) = LOWER(:keyword) " +
            "OR LOWER(p.musicCategory.name) = LOWER(:keyword) " +
            "OR LOWER(p.artist.name) = LOWER(:keyword) " +
            "OR LOWER(p.mediaType) = LOWER(:keyword)")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
