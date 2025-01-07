package com.studia.JavaWebApplication.service;
import com.studia.JavaWebApplication.dto.ProductDTO;
import com.studia.JavaWebApplication.model.Product;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAll();
    Product save(MultipartFile imageProduct, ProductDTO productDTO, BindingResult bindingResult);

    boolean isImageDuplicate(String base64Image, Long productId);

    //    Product save(ProductDTO productDTO);
//    Product update(ProductDTO productDTO);
//    Product update(MultipartFile imageProduct, ProductDTO productDTO, BindingResult bindingResult);
    Product update(ProductDTO productDTO);
    ProductDTO findById(Long id);
    void deleteById(Long id);
}