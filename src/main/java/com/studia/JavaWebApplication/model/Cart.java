package com.studia.JavaWebApplication.model;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Cart implements Serializable { //pozwala na przechowywanie obiektu w sesji

    //Lista items przechowuje CartItem, czyli produkty w koszyku
    private List<CartItem> items = new ArrayList<>();


    public void addItem(Product product, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
    }


    public void updateItem(Product product, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(quantity); // Aktualizuj ilość produktu
                return;
            }
        }
    }

    public int getItemQuantity(Long productId) {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public double getTotalPrice() {
        return items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }

}