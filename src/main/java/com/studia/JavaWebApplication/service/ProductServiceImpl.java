package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.ProductDTO;
import com.studia.JavaWebApplication.model.Product;
import com.studia.JavaWebApplication.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ProductDTO> findAll() {
        List<ProductDTO> productDTOList = new ArrayList<>();
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setMusicCategory(product.getMusicCategory());
            productDTO.setTitle(product.getTitle());
            productDTO.setReleaseDate(product.getReleaseDate());
            productDTO.setDescription(product.getDescription());
            productDTO.setPrice(product.getPrice());
            productDTO.setStockQuantity(product.getStockQuantity());
            productDTO.setAddedDateTime(product.getAddedDateTime());
            productDTO.setMediaType(product.getMediaType());
            productDTO.setArtist(product.getArtist());
            productDTOList.add(productDTO);
        }
        return productDTOList;
    }

//    @Override
//    public Product save(ProductDTO productDTO) {
//        Product product = new Product();
//        product.setMusicCategory(productDTO.getMusicCategory());
//        product.setTitle(productDTO.getTitle());
//        product.setReleaseDate(productDTO.getReleaseDate());
//        product.setDescription(productDTO.getDescription());
//        product.setPrice(productDTO.getPrice());
//        product.setStockQuantity(productDTO.getStockQuantity());
//        return productRepository.save(product);
//    }

    @Override
    public Product save(ProductDTO productDTO) {
        Product product = new Product(
                productDTO.getTitle(),
                productDTO.getDescription(),
                productDTO.getReleaseDate(),
                productDTO.getPrice(),
                productDTO.getStockQuantity(),
                productDTO.getMusicCategory(),
                productDTO.getMediaType(),
                productDTO.getArtist()
        );
        product.setAddedDateTime(LocalDateTime.now());
        return productRepository.save(product);
    }
    @Override
    public Product update(ProductDTO productDTO) {
        return null;
    }

    @Override
    public Product findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
