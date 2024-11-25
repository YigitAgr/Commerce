package com.example.commerce.service;

import com.example.commerce.converters.CustomerConventer;
import com.example.commerce.dto.CustomerDto;
import com.example.commerce.model.Customer;
import com.example.commerce.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerConventer customerConventer;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerConventer customerConventer) {
        this.customerRepository = customerRepository;
        this.customerConventer = customerConventer;
    }

    @Transactional
    // Add a new customer
    public CustomerDto addCustomer(CustomerDto customerDto) {
        Customer customer = customerConventer.convertDtoToCustomer(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return customerConventer.convertCustomerToDto(savedCustomer);
    }

    // Get all customers
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customerConventer::convertCustomerToDto)
                .toList();
    }

    // Get a customer by ID (optional but can be added)
    public CustomerDto getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));
        return customerConventer.convertCustomerToDto(customer);
    }
}
