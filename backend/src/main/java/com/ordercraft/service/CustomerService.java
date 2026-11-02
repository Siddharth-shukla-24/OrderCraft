package com.ordercraft.service;

import com.ordercraft.dto.CustomerDto;
import com.ordercraft.entity.Customer;
import com.ordercraft.exception.ResourceNotFoundException;
import com.ordercraft.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDto> getAllCustomers(String status) {
        List<Customer> list = (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status))
                ? customerRepository.findByStatus(status.toUpperCase())
                : customerRepository.findAll();

        return list.stream().map(this::toDto).toList();
    }

    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return toDto(customer);
    }

    private CustomerDto toDto(Customer c) {
        return new CustomerDto(
                c.getCustomerId(),
                c.getCustomerName(),
                c.getEmail(),
                c.getPhone(),
                c.getBillingAddress(),
                c.getStatus()
        );
    }
}
