package com.studia.JavaWebApplication.repositories;

import com.studia.JavaWebApplication.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p .mediaType) = LOWER(:keyword) " +
            "OR LOWER(p.musicCategory.name) = LOWER(:keyword) " +
            "OR LOWER(p.artist.name) = LOWER(:keyword) " +
            "OR LOWER(p.title) = LOWER(:keyword)")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:categories IS NULL OR p.musicCategory.id IN :categories) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:minStock IS NULL OR p.stockQuantity >= :minStock) " +
            "AND (:maxStock IS NULL OR p.stockQuantity <= :maxStock) " +
            "AND (:mediaTypes IS NULL OR p.mediaType IN :mediaTypes) " +
            "AND (:artistIds IS NULL OR p.artist.id IN :artistIds)")
    Page<Product> filterProducts(
            @Param("search") String search,
            @Param("categories") List<Long> categories,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minStock") Integer minStock,
            @Param("maxStock") Integer maxStock,
            @Param("mediaTypes") List<String> mediaTypes,
            @Param("artistIds") List<Long> artistIds,
            Pageable pageable);
}
