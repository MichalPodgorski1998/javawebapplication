package com.studia.JavaWebApplication.controller;

import com.studia.JavaWebApplication.dto.ProductDTO;
import com.studia.JavaWebApplication.model.Cart;
import com.studia.JavaWebApplication.model.Product;
import com.studia.JavaWebApplication.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CartController {

    @Autowired
    private ProductService productService;

    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
            System.out.println("Koszyk nie istnieje w sesji, utworzono nowy.");
        } else {
            System.out.println("Znaleziono koszyk w sesji. Liczba produktów: " + cart.getItems().size());
            cart.getItems().forEach(item -> {
                System.out.println("Produkt: " + item.getProduct().getTitle() + ", Ilość: " + item.getQuantity());
            });
        }
        model.addAttribute("cart", cart);
        return "shop/cart"; // Widok koszyka
    }

    @PostMapping("/cart/add/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@PathVariable("id") Long productId,
                                                         @RequestParam(defaultValue = "1") int quantity,
                                                         HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        ProductDTO productDTO = productService.findById(productId);
        if (productDTO != null) {
            int stockQuantity = productDTO.getStockQuantity();
            int currentQuantityInCart = cart.getItemQuantity(productId);

            int availableToAdd = stockQuantity - currentQuantityInCart;

            if (currentQuantityInCart >= stockQuantity) {
                return ResponseEntity.badRequest().body(Map.of("error", "Osiągnięto maksymalną ilość tego produktu w koszyku."));
            }

            if (quantity > availableToAdd) {
                quantity = availableToAdd;
            }

            if (quantity > 0) {
                cart.addItem(productDTO.toProduct(), quantity);

                // Przygotuj dane do zwrócenia w JSON
                Map<String, Object> productDetails = new HashMap<>();
                productDetails.put("title", productDTO.getTitle());
                productDetails.put("artist", productDTO.getArtist().getName());
                productDetails.put("mediaType", productDTO.getMediaType().name()); // Pobranie wartości enum
                productDetails.put("image", productDTO.getImageUrl());
                productDetails.put("price", productDTO.getPrice());
                productDetails.put("formattedDetails", productDTO.getArtist().getName() + " - " + productDTO.getTitle() + " " + productDTO.getMediaType().name());

                return ResponseEntity.ok(productDetails);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Nie można dodać więcej produktów niż dostępne w magazynie"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Produkt nie został znaleziony"));
        }
    }

    @PostMapping("/cart/update/{id}")
    public String updateCartQuantity(
            @PathVariable("id") Long productId,
            @RequestParam("quantity") int quantity,
            HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart != null) {
            ProductDTO productDTO = productService.findById(productId);
            if (productDTO != null) {
                int stockQuantity = productDTO.getStockQuantity();

                // Sprawdź, czy żądana ilość nie przekracza stanu magazynowego
                if (quantity > stockQuantity) {
                    quantity = stockQuantity;
                }

                // Zaktualizuj ilość w koszyku
                cart.updateItem(productDTO.toProduct(), quantity);
                System.out.println("Zaktualizowano ilość produktu w koszyku: " + quantity);
            } else {
                System.out.println("Produkt o ID " + productId + " nie został znaleziony.");
            }
        }

        return "redirect:/cart";
    }


    @PostMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            cart.removeItem(id);
        }
        return "redirect:/cart";
    }


}