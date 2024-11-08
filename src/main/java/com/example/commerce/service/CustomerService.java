package com.example.commerce.service;

import com.example.commerce.model.Cart;
import com.example.commerce.model.Customer;
import com.example.commerce.repository.CustomerRepository;
import com.example.commerce.repository.CartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Customer addCustomer(Customer customer) {

        Customer savedCustomer = customerRepository.save(customer);
        Cart cart = new Cart();
        cart.setCustomer(savedCustomer);
        cartRepository.save(cart);
        savedCustomer.setCart(cart);

        return savedCustomer;
    }


    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
