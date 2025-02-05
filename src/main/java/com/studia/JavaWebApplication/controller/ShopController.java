package com.studia.JavaWebApplication.controller;

import com.studia.JavaWebApplication.dto.ProductDTO;
import com.studia.JavaWebApplication.model.Cart;
import com.studia.JavaWebApplication.model.MediaType;
import com.studia.JavaWebApplication.service.ArtistService;
import com.studia.JavaWebApplication.service.MusicCategoryService;
import com.studia.JavaWebApplication.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ShopController {
    @Autowired
    private ProductService productService;

    @Autowired
    private MusicCategoryService musicCategoryService;

    @Autowired
    private ArtistService artistService;


    @GetMapping({"/shop", "/"})
    public String shopPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "default") String sortOption, // Połączone kryterium i kierunek
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> mediaTypes,
            @RequestParam(required = false) List<Long> artistIds,
            HttpSession session, // Dodano sesję
            Model model) {

        Pageable pageable;

        if ("default".equals(sortOption)) {
            pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        } else {
            String[] sortParams = sortOption.split("_");
            String sort = sortParams[0];
            String direction = sortParams[1];
            pageable = PageRequest.of(page, size,
                    direction.equalsIgnoreCase("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending());
        }

        Page<ProductDTO> productPage;

        if (search != null && !search.isBlank()) {
            productPage = productService.searchProducts(search, pageable);
        } else {
            productPage = productService.filterProducts(
                    null,
                    categories != null && !categories.isEmpty() ? categories : null,
                    minPrice != null && minPrice > 0 ? minPrice : null,
                    maxPrice != null && maxPrice > 0 ? maxPrice : null,
                    null,
                    null,
                    mediaTypes != null && !mediaTypes.isEmpty() ? mediaTypes : null,
                    artistIds != null && !artistIds.isEmpty() ? artistIds : null,
                    pageable);
        }

        if (page >= productPage.getTotalPages() && productPage.getTotalPages() > 0) {
            page = productPage.getTotalPages() - 1;
            pageable = PageRequest.of(page, size, Sort.by("id").ascending());
            productPage = productService.filterProducts(
                    search, categories, minPrice, maxPrice, null, null, mediaTypes, artistIds, pageable);
        }

        if (productPage.isEmpty()) {
            model.addAttribute("noMatches", "Brak dopasowań dla podanych kryteriów.");
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        model.addAttribute("cart", cart);

        model.addAttribute("pageTitle", "Strona główna");

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        model.addAttribute("sort", sortOption.equals("default") ? "id" : sortOption.split("_")[0]);
        model.addAttribute("direction", sortOption.equals("default") ? "asc" : sortOption.split("_")[1]);

        model.addAttribute("categories", musicCategoryService.findAllMusicCategory());
        model.addAttribute("mediaTypes", MediaType.values());
        model.addAttribute("artists", artistService.findAllArtists());
        model.addAttribute("selectedCategories", categories);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("selectedMediaTypes", mediaTypes);
        model.addAttribute("selectedArtistIds", artistIds);

        return "shop/shop"; // Nazwa widoku
    }
    @GetMapping("/shop/details/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        ProductDTO product = productService.findById(id);
        model.addAttribute("product", product);
        return "shop/product-details"; // Ścieżka do widoku szczegółów produktu
    }

}




