package com.studia.JavaWebApplication.validate;

import com.studia.JavaWebApplication.dto.ProductDTO;
import com.studia.JavaWebApplication.service.ProductService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class UniqueImageValidator implements ConstraintValidator<UniqueImage, ProductDTO> {

    @Autowired
    private ProductService productService;

    @Override
    public boolean isValid(ProductDTO productDTO, ConstraintValidatorContext context) {
        MultipartFile imageProduct = productDTO.getImageProduct();
        if (imageProduct != null && !imageProduct.isEmpty()) {
            try {
                String base64Image = Base64.getEncoder().encodeToString(imageProduct.getBytes());
                List<ProductDTO> allProducts = productService.findAll();

                for (ProductDTO existingProduct : allProducts) {
                    if (!existingProduct.getId().equals(productDTO.getId()) &&
                            base64Image.equals(existingProduct.getImage())) {
                        // Dodaj komunikat walidacyjny
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate("Ten sam obraz jest już przypisany do innego produktu.")
                                .addPropertyNode("imageProduct")
                                .addConstraintViolation();
                        return false;
                    }
                }
            } catch (IOException e) {
                return false; // Błąd przetwarzania
            }
        }
        return true; // Wszystko w porządku
    }
}