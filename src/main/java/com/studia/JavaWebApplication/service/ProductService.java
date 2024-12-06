package com.studia.JavaWebApplication.service;
import com.studia.JavaWebApplication.dto.ProductDTO;
import com.studia.JavaWebApplication.model.Product;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAll();
    Product save(ProductDTO productDTO);
    Product update(ProductDTO productDTO);
    Product findById(Long id);
    void deleteById(Long id);
}
