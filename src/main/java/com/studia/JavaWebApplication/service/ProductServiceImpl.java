package com.studia.JavaWebApplication.service;

import com.studia.JavaWebApplication.dto.ProductDTO;
import com.studia.JavaWebApplication.model.Product;
import com.studia.JavaWebApplication.repositories.ProductRepository;
import com.studia.JavaWebApplication.utils.ImageUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageUpload imageUpload;



    @Override
    public Page<ProductDTO> filterProducts(String search, List<Long> categories, Double minPrice, Double maxPrice,
                                           Integer minStock, Integer maxStock, List<String> mediaTypes,
                                           List<Long> artistIds, Pageable pageable) {
        Page<Product> productPage = productRepository.filterProducts(search, categories, minPrice, maxPrice,
                minStock, maxStock, mediaTypes, artistIds, pageable);

        List<ProductDTO> productDTOList = productPage.getContent().stream()
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getTitle(),
                        product.getDescription(),
                        product.getReleaseDate(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getMusicCategory(),
                        product.getAddedDateTime(),
                        product.getMediaType(),
                        product.getArtist(),
                        product.getImage()))
                .toList();

        return new PageImpl<>(productDTOList, pageable, productPage.getTotalElements());
    }

    @Override
    public List<ProductDTO> findAllProducts() {
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
            productDTO.setImage(product.getImage());
            productDTOList.add(productDTO);
        }
        return productDTOList;
    }

    @Override
    public Page<ProductDTO> findAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductDTO> productDTOList = productPage.getContent().stream()
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getTitle(),
                        product.getDescription(),
                        product.getReleaseDate(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getMusicCategory(),
                        product.getAddedDateTime(),
                        product.getMediaType(),
                        product.getArtist(),
                        product.getImage()))
                .toList();

        return new PageImpl<>(productDTOList, pageable, productPage.getTotalElements());
    }

    @Override
    public Page<ProductDTO> searchProducts(String search, Pageable pageable) {

        Page<Product> productPage = productRepository.searchByKeyword(search, pageable);

        List<ProductDTO> productDTOList = productPage.getContent().stream()
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getTitle(),
                        product.getDescription(),
                        product.getReleaseDate(),
                        product.getPrice(),
                        product.getStockQuantity(),
                        product.getMusicCategory(),
                        product.getAddedDateTime(),
                        product.getMediaType(),
                        product.getArtist(),
                        product.getImage()))
                .toList();

        return new PageImpl<>(productDTOList, pageable, productPage.getTotalElements());
    }

    @Override
    public Product save(MultipartFile imageProduct, ProductDTO productDTO, BindingResult bindingResult) {
        Product product = new Product(
                productDTO.getTitle(),
                productDTO.getDescription(),
                productDTO.getReleaseDate(),
                productDTO.getPrice(),
                productDTO.getStockQuantity(),
                productDTO.getMusicCategory(),
                productDTO.getMediaType(),
                productDTO.getArtist(),
                null // Domyślna wartość obrazu
        );
        product.setAddedDateTime(LocalDateTime.now());

        if (imageProduct != null && !imageProduct.isEmpty()) {
            try {
                String base64Image = Base64.getEncoder().encodeToString(imageProduct.getBytes());

                // Sprawdź, czy zdjęcie jest unikalne
                if (!isImageDuplicate(base64Image, productDTO.getId())) {
                    product.setImage(base64Image); // Zapisz zdjęcie, jeśli jest unikalne
                }
            } catch (IOException e) {
                bindingResult.rejectValue("imageProduct", "error.product", "Błąd podczas przetwarzania obrazu.");
            }
        }

        return productRepository.save(product);
    }

    @Override
    public boolean isImageDuplicate(String base64Image, Long productId) {
        return productRepository.findAll().stream()
                .anyMatch(existingProduct ->
                        !existingProduct.getId().equals(productId) &&
                                base64Image.equals(existingProduct.getImage()));
    }


    @Override
    public Product update(ProductDTO productDTO) {
        Product product = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new RuntimeException("Produkt nie został znaleziony."));

        // Ustaw dane produktu
        product.setTitle(productDTO.getTitle());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setMusicCategory(productDTO.getMusicCategory());
        product.setMediaType(productDTO.getMediaType());
        product.setArtist(productDTO.getArtist());

        // Obsługa obrazu
        MultipartFile imageProduct = productDTO.getImageProduct();
        if (imageProduct != null && !imageProduct.isEmpty()) {
            try {
                // Jeśli przesłano nowe zdjęcie, zamień stare
                imageUpload.uploadFile(imageProduct);
                String base64Image = Base64.getEncoder().encodeToString(imageProduct.getBytes());
                product.setImage(base64Image);
            } catch (Exception e) {
                throw new RuntimeException("Błąd podczas przetwarzania obrazu: " + e.getMessage());
            }
        } else {
            // Zachowaj istniejące zdjęcie
            product.setImage(product.getImage());
        }

        return productRepository.save(product);
    }

    @Override
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produkt o podanym ID nie został znaleziony"));

        return new ProductDTO(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getReleaseDate(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getMusicCategory(),
                product.getAddedDateTime(),
                product.getMediaType(),
                product.getArtist(),
                product.getImage()
        );
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
