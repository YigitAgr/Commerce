package com.example.commerce.model;

import com.example.commerce.dto.ProductDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {

    private String name;
    private Double price;
    private Integer stock;

    // Constructor that accepts ProductDto
    public Product(ProductDto productDto) {
        this.id = productDto.getId();  // Assuming `BaseEntity` has an `id` field
        this.name = productDto.getName();
        this.price = productDto.getPrice();
        this.stock = productDto.getStock();
    }



}
