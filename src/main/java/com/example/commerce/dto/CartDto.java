package com.example.commerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CartDto {
    private Long id;
    private Long customerId;
    private Set<CartItemDto> items;
    private Double totalPrice;
}
