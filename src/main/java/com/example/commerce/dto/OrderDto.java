package com.example.commerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class OrderDto {

    private Long id;
    private Long customerId;
    private Set<OrderItemDto> items;
    private Double totalPrice;   // Corrected to totalPrice
    private String status;       // Corrected to status
    private LocalDateTime orderDate;  // Added orderDate for consistency with Order

    public void setOrderStatus(String orderStatus) {
        this.status = orderStatus;
    }

}
