package com.example.commerce.service;

import com.example.commerce.dto.CustomerDto;
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
    // Add a new customer
    public CustomerDto addCustomer(CustomerDto customerDto) {
        // Convert CustomerDto to Customer entity
        Customer customer = new Customer();
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        // Add other fields as needed

        // Save the customer to the database
        Customer savedCustomer = customerRepository.save(customer);

        // Convert saved Customer entity back to CustomerDto
        CustomerDto savedCustomerDto = new CustomerDto();
        savedCustomerDto.setId(savedCustomer.getId());
        savedCustomerDto.setName(savedCustomer.getName());
        savedCustomerDto.setEmail(savedCustomer.getEmail());

        return savedCustomerDto;
    }

    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(this::convertCustomerToDto)
                .toList();
    }

    // Utility method to convert Customer entity to CustomerDto
    private CustomerDto convertCustomerToDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customer.getId());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        return customerDto;
    }
}
