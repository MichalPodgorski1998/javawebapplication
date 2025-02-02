package com.studia.JavaWebApplication.controller;

import com.studia.JavaWebApplication.dto.ProductDTO;
import com.studia.JavaWebApplication.dto.UserDto;
import com.studia.JavaWebApplication.model.*;
import com.studia.JavaWebApplication.repositories.AddressRepository;
import com.studia.JavaWebApplication.repositories.OrderRepository;
import com.studia.JavaWebApplication.repositories.ProductRepository;
import com.studia.JavaWebApplication.service.ProductService;
import com.studia.JavaWebApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDto user = null;
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof UserDetails) {
            String loggedInEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
            user = userService.findUserByEmail(loggedInEmail);
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }

        model.addAttribute("user", user);
        model.addAttribute("cart", cart);

        return "shop/cart";
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

                int cartTotalItems = cart.getTotalItems();
                Map<String, Object> productDetails = new HashMap<>();
                productDetails.put("title", productDTO.getTitle());
                productDetails.put("artist", productDTO.getArtist().getName());
                productDetails.put("mediaType", productDTO.getMediaType().name());
                productDetails.put("image", productDTO.getImageUrl());
                productDetails.put("price", productDTO.getPrice());
                productDetails.put("formattedDetails", productDTO.getArtist().getName() + " - " + productDTO.getTitle() + " " + productDTO.getMediaType().name());
                productDetails.put("cartTotalItems", cartTotalItems);

                return ResponseEntity.ok(productDetails);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Nie można dodać więcej produktów niż dostępne w magazynie"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Produkt nie został znaleziony"));
        }
    }

    @PostMapping("/order/submit")
    @ResponseBody
    public ResponseEntity<?> submitOrder(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Koszyk jest pusty.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Użytkownik niezalogowany.");
        }

        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        UserDto userDto = userService.findUserByEmail(email);
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono użytkownika.");
        }

        User user = userService.mapToUserEntity(userDto);

        if (user.getAddress() == null) {
            return ResponseEntity.badRequest().body("Użytkownik nie ma przypisanego adresu.");
        }

        Address address = addressRepository.findByCityAndPostalCodeAndStreetAndHouseNumber(
                        user.getAddress().getCity(),
                        user.getAddress().getPostalCode(),
                        user.getAddress().getStreet(),
                        user.getAddress().getHouseNumber())
                .orElseThrow(() -> new RuntimeException("Adres użytkownika nie istnieje w bazie danych."));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setAddress(address);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Produkt nie znaleziony: " + cartItem.getProduct().getId()));

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                return ResponseEntity.badRequest().body("Brak wystarczającej ilości produktu: " + product.getTitle());
            }
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);
        orderRepository.save(order);
        session.removeAttribute("cart");
        return ResponseEntity.ok("Zamówienie zostało zapisane.");
    }

    @GetMapping("/orders")
    public String viewMyOrders(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestParam(defaultValue = "date_desc") String sortOption,
                               Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String loggedInEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        UserDto userDto = userService.findUserByEmail(loggedInEmail);

        if (userDto == null) {
            model.addAttribute("error", "Nie znaleziono użytkownika w bazie.");
            return "redirect:/";
        }

        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setAddress(addressRepository.findById(userDto.getAddress().getId()).orElse(null));

        Sort sort = Sort.by("orderDate").descending();
        if ("date_asc".equals(sortOption)) {
            sort = Sort.by("orderDate").ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orderPage = orderRepository.findAllByUser(user, pageable);

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("sortOption", sortOption);

        return "order/myOrders";
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