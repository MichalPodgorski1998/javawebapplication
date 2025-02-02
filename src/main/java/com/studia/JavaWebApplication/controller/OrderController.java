package com.studia.JavaWebApplication.controller;

import com.studia.JavaWebApplication.model.Order;
import com.studia.JavaWebApplication.model.OrderItem;
import com.studia.JavaWebApplication.model.OrderStatus;
import com.studia.JavaWebApplication.model.Product;
import com.studia.JavaWebApplication.repositories.OrderRepository;
import com.studia.JavaWebApplication.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/ordersAdministration")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;


    @GetMapping("/pending")
    public String viewAllOrders(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "3") int size,
                                @RequestParam(defaultValue = "date_desc") String sortOption,
                                Model model) {
        return getOrdersByStatus(OrderStatus.PENDING, page, size, sortOption, model, "order/pendingOrders");
    }

    @GetMapping("/in-progress")
    public String viewInProgressOrders(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "3") int size,
                                       @RequestParam(defaultValue = "date_desc") String sortOption,
                                       Model model) {model.addAttribute("localizedOrderStatus", getLocalizedOrderStatus());

        return getOrdersByStatus(OrderStatus.IN_PROGRESS, page, size, sortOption, model, "order/inProgressOrders");
    }

    @GetMapping("/completed")
    public String viewCompletedOrders(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "3") int size,
                                      @RequestParam(defaultValue = "date_desc") String sortOption,
                                      Model model) {model.addAttribute("localizedOrderStatus", getLocalizedOrderStatus());

        return getOrdersByStatus(OrderStatus.COMPLETED, page, size, sortOption, model, "order/completedOrders");
    }

    @GetMapping("/cancelled")
    public String viewCancelledOrders(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "3") int size,
                                      @RequestParam(defaultValue = "date_desc") String sortOption,
                                      Model model) {model.addAttribute("localizedOrderStatus", getLocalizedOrderStatus());

        return getOrdersByStatus(OrderStatus.CANCELLED, page, size, sortOption, model, "order/cancelledOrders");
    }

    private String getOrdersByStatus(OrderStatus status, int page, int size, String sortOption, Model model, String viewName) {
        Sort sort = sortOption.equals("date_asc") ? Sort.by("orderDate").ascending() : Sort.by("orderDate").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orderPage = orderRepository.findAllByStatus(status, pageable);

        Map<String, Long> orderCounts = new HashMap<>();
        for (OrderStatus s : OrderStatus.values()) {
            long count = orderRepository.countByStatus(s);
            orderCounts.put(s.name(), count);
        }

        System.out.println("Liczba zamówień: " + orderCounts);
        model.addAttribute("localizedOrderStatus", getLocalizedOrderStatus());

        model.addAttribute("orderCounts", orderCounts);
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("sortOption", sortOption);
        return viewName;
    }

    private Map<String, String> getLocalizedOrderStatus() {
        Map<String, String> localizedStatusMap = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            localizedStatusMap.put(status.name(), status.getDisplayName());
        }
        return localizedStatusMap;
    }

    @PostMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String status,
                                    @RequestHeader(value = "referer", required = false) String referer,
                                    RedirectAttributes redirectAttributes) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono zamówienia o ID: " + id));

        OrderStatus newStatus = OrderStatus.valueOf(status);

        if (newStatus.ordinal() < order.getStatus().ordinal()) {
            redirectAttributes.addFlashAttribute("error", "Nie można zmienić statusu na wcześniejszy.");
            return "redirect:" + (referer != null ? referer : "/ordersAdministration");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {
            return "redirect:/login";
        }

        if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderItem orderItem : order.getItems()) {
                Product product = orderItem.getProduct();
                product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        redirectAttributes.addFlashAttribute("message", "Status zamówienia został zaktualizowany.");

        return "redirect:" + (referer != null ? referer : "/ordersAdministration");
    }


}
