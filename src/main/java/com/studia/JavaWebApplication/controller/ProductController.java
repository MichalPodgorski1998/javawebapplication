package com.studia.JavaWebApplication.controller;
import com.studia.JavaWebApplication.dto.ArtistDto;
import com.studia.JavaWebApplication.dto.MusicCategoryDTO;
import com.studia.JavaWebApplication.dto.ProductDTO;
import com.studia.JavaWebApplication.model.MediaType;
import com.studia.JavaWebApplication.service.ArtistService;
import com.studia.JavaWebApplication.service.MusicCategoryService;
import com.studia.JavaWebApplication.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private MusicCategoryService musicCategoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ArtistService artistService;

    @GetMapping("/products")
    public String products(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "default") String sortOption, // Połączone kryterium i kierunek
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(required = false) List<String> mediaTypes,
            @RequestParam(required = false) List<Long> artistIds,
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
                    minStock != null && minStock > 0 ? minStock : null,
                    maxStock != null && maxStock > 0 ? maxStock : null,
                    mediaTypes != null && !mediaTypes.isEmpty() ? mediaTypes : null,
                    artistIds != null && !artistIds.isEmpty() ? artistIds : null,
                    pageable);
        }

        if (page >= productPage.getTotalPages() && productPage.getTotalPages() > 0) {
            page = productPage.getTotalPages() - 1;
            pageable = PageRequest.of(page, size, Sort.by("id").ascending());
            productPage = productService.filterProducts(
                    search, categories, minPrice, maxPrice, minStock, maxStock, mediaTypes, artistIds, pageable);
        }

        if (productPage.isEmpty()) {
            model.addAttribute("noMatches", "Brak dopasowań dla podanych kryteriów.");
        }

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
        model.addAttribute("minStock", minStock);
        model.addAttribute("maxStock", maxStock);
        model.addAttribute("selectedMediaTypes", mediaTypes);
        model.addAttribute("selectedArtistIds", artistIds);

        return "products/products";
    }


    @GetMapping("/products/addProduct")
    public String addProduct(Model model) {

        ProductDTO productDTO = new ProductDTO();
        String imageUrl = productDTO.getImage() != null
                ? "data:image/jpeg;base64," + productDTO.getImage()
                : "https://demofree.sirv.com/nope-not-here.jpg";
        model.addAttribute("imageUrl", imageUrl);

        List<MusicCategoryDTO> musicCategories = musicCategoryService.findAllMusicCategory();
        List<ArtistDto> artists = artistService.findAllArtists();
        model.addAttribute("musicCategories", musicCategories);
        model.addAttribute("artists", artists);
        model.addAttribute("mediaTypes", MediaType.values());
        model.addAttribute("product", new ProductDTO());
        return "products/addProduct";
    }

    @PostMapping("/saveProduct")
    public String saveProduct(
            @Valid @ModelAttribute("product") ProductDTO productDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (productDTO.getImageProduct() != null && !productDTO.getImageProduct().isEmpty()) {
            try {
                String base64Image = Base64.getEncoder().encodeToString(productDTO.getImageProduct().getBytes());

                if (productService.isImageDuplicate(base64Image, productDTO.getId())) {
                    productDTO.setImage(null);
                } else {
                    productDTO.setImage(base64Image);
                }
            } catch (IOException e) {
                productDTO.setImage(null);
            }
        } else {
            productDTO.setImage(null);
        }

        if (bindingResult.hasErrors()) {
            handleValidationErrors(productDTO, model);
            return "products/addProduct";
        }

        productService.save(productDTO.getImageProduct(), productDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            handleValidationErrors(productDTO, model);
            return "products/addProduct";
        }

        redirectAttributes.addFlashAttribute("success", "Produkt został pomyślnie dodany.");
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String getEditProductPage(@PathVariable("id") Long id, Model model) {
        ProductDTO productDTO = productService.findById(id);

        String imageUrl = productDTO.getImage() != null
                ? "data:image/jpeg;base64," + productDTO.getImage()
                : "https://demofree.sirv.com/nope-not-here.jpg";
        model.addAttribute("imageUrl", imageUrl);

        List<MusicCategoryDTO> musicCategories = musicCategoryService.findAllMusicCategory();
        List<ArtistDto> artists = artistService.findAllArtists();
        System.out.println("Release Date: " + productDTO.getReleaseDate());
        model.addAttribute("musicCategories", musicCategories);
        model.addAttribute("mediaTypes", MediaType.values());
        model.addAttribute("artists", artists);
        model.addAttribute("product", productDTO);
        model.addAttribute("categories", musicCategoryService.findAllMusicCategory());
        return "products/editProduct"; // Nazwa widoku HTML
    }

    @PostMapping("/products/edit")
    public String editProduct(
            @Valid @ModelAttribute("product") ProductDTO productDTO,
            BindingResult bindingResult,
            Model model,
            @RequestParam(value = "imageProduct", required = false) MultipartFile imageProduct) {

        if (bindingResult.hasErrors()) {
            handleValidationErrors(productDTO, model);
            return "products/editProduct";
        }
        productDTO.setEditDateTime(LocalDateTime.now());
        productDTO.setImageProduct(imageProduct);
        productService.update(productDTO);

        return "redirect:/products";
    }

    private void handleValidationErrors(ProductDTO productDTO, Model model) {
        if (productDTO.getImage() == null && productDTO.getId() != null) {
            ProductDTO existingProduct = productService.findById(productDTO.getId());
            productDTO.setImage(existingProduct.getImage());
        }
        String imageUrl = (productDTO.getImage() != null && !productDTO.getImage().isEmpty())
                ? "data:image/jpeg;base64," + productDTO.getImage()
                : "https://demofree.sirv.com/nope-not-here.jpg";
        model.addAttribute("imageUrl", imageUrl);

        List<MusicCategoryDTO> musicCategories = musicCategoryService.findAllMusicCategory();
        List<ArtistDto> artists = artistService.findAllArtists();
        model.addAttribute("musicCategories", musicCategories);
        model.addAttribute("artists", artists);
        model.addAttribute("mediaTypes", MediaType.values());
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                RedirectAttributes redirectAttributes) {

        productService.deleteById(id);

        Page<ProductDTO> productPage = productService.findAllProducts(PageRequest.of(page, size));

        boolean isLastPage = productPage.getNumberOfElements() == 0 && productPage.getTotalPages() > 1;

        int targetPage = isLastPage && page > 0 ? page - 1 : page;

        redirectAttributes.addFlashAttribute("success", "Produkt został pomyślnie usunięty!");
        return "redirect:/products?page=" + targetPage + "&size=" + size;
    }


}
