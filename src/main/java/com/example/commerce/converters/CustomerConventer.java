package com.example.commerce.converters;
import com.example.commerce.dto.CustomerDto;
import com.example.commerce.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerConventer {

    // Convert Customer entity to CustomerDto
    public CustomerDto convertCustomerToDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customer.getId());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        return customerDto;
    }

    // Convert CustomerDto to Customer entity
    public Customer convertDtoToCustomer(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        return customer;
    }
}
