package com.studia.JavaWebApplication.model;
public enum OrderStatus {
    PENDING("Przyjęte do realizacji"),
    IN_PROGRESS("W realizacji"),
    COMPLETED("Zrealizowane"),
    CANCELLED("Anulowane");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}