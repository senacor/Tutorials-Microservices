package com.senacor.bitc.demo.service;

import com.senacor.bitc.demo.domain.Customer;
import com.senacor.bitc.demo.domain.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MySQLCustomerService implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public MySQLCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    @Override
    public Customer loadCustomerById(Integer customerId) {
        return customerRepository.findOne(customerId);
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> findCustomersByLastName(String lastName) {
        return customerRepository.findByLastName(lastName);
    }
}
