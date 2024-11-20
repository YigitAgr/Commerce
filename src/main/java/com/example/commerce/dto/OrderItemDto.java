package com.example.commerce.dto;

public class OrderItemDto {

    private Long productId;
    private int quantity;
    private Double priceAtTimeOfPurchase;

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getPriceAtTimeOfPurchase() {
        return priceAtTimeOfPurchase;
    }

    public void setPriceAtTimeOfPurchase(Double priceAtTimeOfPurchase) {
        this.priceAtTimeOfPurchase = priceAtTimeOfPurchase;
    }
}
