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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
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


//    @GetMapping("/products/products")
//    public String products(Model model) {
//        List<ProductDTO> productDTOList = productService.findAllProducts();
//        model.addAttribute("products", productDTOList);
//        model.addAttribute("size", productDTOList.size());
//        return "products/products";
//    }

//    @GetMapping("/products/products")
//    public String products(@RequestParam(defaultValue = "0") int page,
//                           @RequestParam(defaultValue = "5") int size,
//                           Model model) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<ProductDTO> productPage = productService.findAllProducts(pageable);
//
//        model.addAttribute("products", productPage.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", productPage.getTotalPages());
//        model.addAttribute("totalItems", productPage.getTotalElements());
//        model.addAttribute("size", size);
//
//        return "products/products";
//    }
@GetMapping("/products/products")
public String products(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
    Pageable pageable = PageRequest.of(page, size);
    Page<ProductDTO> productPage = productService.findAllProducts(pageable);

    // Jeśli bieżąca strona jest większa niż dostępne strony, ustaw ją na ostatnią dostępną
    if (page >= productPage.getTotalPages() && productPage.getTotalPages() > 0) {
        page = productPage.getTotalPages() - 1; // Ustaw na ostatnią stronę
        pageable = PageRequest.of(page, size);
        productPage = productService.findAllProducts(pageable);
    }

    model.addAttribute("products", productPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", productPage.getTotalPages());
    model.addAttribute("totalItems", productPage.getTotalElements());
    model.addAttribute("size", size);

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

        // Obsługa obrazu
        if (productDTO.getImageProduct() != null && !productDTO.getImageProduct().isEmpty()) {
            try {
                // Konwersja obrazu na Base64
                String base64Image = Base64.getEncoder().encodeToString(productDTO.getImageProduct().getBytes());

                // Sprawdzanie, czy obraz jest unikalny
                if (productService.isImageDuplicate(base64Image, productDTO.getId())) {
                    // Jeśli obraz jest zduplikowany, ustaw komunikat błędu i pokaż obraz domyślny
//                    bindingResult.rejectValue("imageProduct", "error.product", "Zdjęcie jest już przypisane do innego produktu.");
                    productDTO.setImage(null); // Ustaw brak obrazu
                } else {
                    // Jeśli obraz jest unikalny, ustaw go w DTO
                    productDTO.setImage(base64Image);
                }
            } catch (IOException e) {
//                bindingResult.rejectValue("imageProduct", "error.product", "Błąd podczas przetwarzania obrazu.");
                productDTO.setImage(null); // Ustaw brak obrazu w przypadku błędu
            }
        } else {
            // Jeśli brak obrazu, ustaw wartość null
            productDTO.setImage(null);
        }

        // Obsługa błędów walidacji
        if (bindingResult.hasErrors()) {
            handleValidationErrors(productDTO, model);
            return "products/addProduct";
        }

        // Zapis produktu
        productService.save(productDTO.getImageProduct(), productDTO, bindingResult);

        // Ponowne sprawdzenie błędów po zapisaniu
        if (bindingResult.hasErrors()) {
            handleValidationErrors(productDTO, model);
            return "products/addProduct";
        }

        // Przekierowanie po sukcesie
        redirectAttributes.addFlashAttribute("success", "Produkt został pomyślnie dodany.");
        return "redirect:/products/products";
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

        // Jeśli występują błędy walidacji
        if (bindingResult.hasErrors()) {
            handleValidationErrors(productDTO, model);
            return "products/editProduct";
        }

        // Aktualizuj produkt
        productDTO.setImageProduct(imageProduct);
        productService.update(productDTO);

        // Przekierowanie w przypadku sukcesu
        return "redirect:/products/products";
    }

    // Metoda pomocnicza do obsługi błędów i ładowania modelu
    private void handleValidationErrors(ProductDTO productDTO, Model model) {
        if (productDTO.getImage() == null && productDTO.getId() != null) {
            ProductDTO existingProduct = productService.findById(productDTO.getId());
            productDTO.setImage(existingProduct.getImage());
        }
        // Ustaw Base64 URL obrazu w modelu
        String imageUrl = (productDTO.getImage() != null && !productDTO.getImage().isEmpty())
                ? "data:image/jpeg;base64," + productDTO.getImage()
                : "https://demofree.sirv.com/nope-not-here.jpg";
        model.addAttribute("imageUrl", imageUrl);

        // Załaduj dane pomocnicze
        List<MusicCategoryDTO> musicCategories = musicCategoryService.findAllMusicCategory();
        List<ArtistDto> artists = artistService.findAllArtists();
        model.addAttribute("musicCategories", musicCategories);
        model.addAttribute("artists", artists);
        model.addAttribute("mediaTypes", MediaType.values());
    }


//    @GetMapping("/products/delete/{id}")
//    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
//        try {
//            productService.deleteById(id);
//            redirectAttributes.addFlashAttribute("success", "Kategoria muzyczna została pomyślnie usunięta.");
//        } catch (DataIntegrityViolationException e) {
//            redirectAttributes.addFlashAttribute("failed", "Nie można usunąć kategorii muzycznej, ponieważ są z nim powiązane inne produkty.");
//        }  catch (Exception e) {
//            redirectAttributes.addFlashAttribute("failed", "Wystąpił błąd podczas usuwania kategorii muzycznej.");
//        }
//        return "redirect:/products/products";
//    }


    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "5") int size,
                                RedirectAttributes redirectAttributes) {

        // Usuń produkt
        productService.deleteById(id);

        // Pobierz listę produktów
        Page<ProductDTO> productPage = productService.findAllProducts(PageRequest.of(page, size));

        // Sprawdź, czy aktualna strona jest pusta i czy istnieje poprzednia strona
        boolean isLastPage = productPage.getNumberOfElements() == 0 && productPage.getTotalPages() > 1;

        // Przełącz na poprzednią stronę, jeśli aktualna jest pusta
        int targetPage = isLastPage && page > 0 ? page - 1 : page;

        // Dodaj wiadomość o sukcesie i przekieruj
        redirectAttributes.addFlashAttribute("success", "Produkt został pomyślnie usunięty!");
        return "redirect:/products/products?page=" + targetPage + "&size=" + size;
    }


}
