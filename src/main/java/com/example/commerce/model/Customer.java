package com.example.commerce.model;

import com.example.commerce.dto.CustomerDto;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer extends BaseEntity {

    private String name;
    private String email;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Cart cart;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Order> orders;

    public Customer(CustomerDto customerDto) {
        this.id = customerDto.getId();  // Assuming BaseEntity has an id field
        this.name = customerDto.getName();
        this.email = customerDto.getEmail();
    }


}
