package com.example.commerce.service;

import com.example.commerce.converters.CustomerConventer;
import com.example.commerce.dto.CustomerDto;
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
    private CustomerConventer customerConventer;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    // Add a new customer
    public CustomerDto addCustomer(CustomerDto customerDto) {
        Customer customer = customerConventer.convertDtoToCustomer(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return customerConventer.convertCustomerToDto(savedCustomer);

    }

    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customerConventer::convertCustomerToDto)
                .toList();
    }

}
