package com.studia.JavaWebApplication.controller;

import com.studia.JavaWebApplication.dto.ArtistDto;
import com.studia.JavaWebApplication.dto.MusicCategoryDTO;
import com.studia.JavaWebApplication.dto.ProductDTO;
import com.studia.JavaWebApplication.model.MediaType;
import com.studia.JavaWebApplication.model.MusicCategory;
import com.studia.JavaWebApplication.model.Product;
import com.studia.JavaWebApplication.service.ArtistService;
import com.studia.JavaWebApplication.service.MusicCategoryService;
import com.studia.JavaWebApplication.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    @Autowired
    private MusicCategoryService musicCategoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ArtistService artistService;


    @GetMapping("/products/products")
    public String products(Model model) {
        List<ProductDTO> productDTOList = productService.findAll();
        model.addAttribute("products", productDTOList);
        model.addAttribute("size", productDTOList.size());
        return "products/products";
    }

    @GetMapping("/products/addProduct")
    public String addProduct(Model model) {
        List<MusicCategoryDTO> musicCategories = musicCategoryService.findAllMusicCategory();
        model.addAttribute("musicCategories", musicCategories);

        List<ArtistDto> artists = artistService.findAllArtists();
        model.addAttribute("artists", artists);

        model.addAttribute("mediaTypes", MediaType.values());
        model.addAttribute("product", new ProductDTO());
        return "products/addProduct";
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute("product")ProductDTO productDTO, RedirectAttributes redirectAttributes) {
        productService.save(productDTO);
        return "redirect:/products/products";
    }
}
