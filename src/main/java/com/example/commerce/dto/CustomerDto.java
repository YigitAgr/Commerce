package com.example.commerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CustomerDto {
    private Long id;
    private String name;
    private String email;
    private Long cartId;
    private Set<OrderDto> orders;
}
