package com.senacor.bitc.demo.service;


import com.senacor.bitc.demo.domain.Customer;

import java.util.List;

public interface CustomerService {

    Customer loadCustomerById(Integer customerId);

    Customer saveCustomer(Customer customer);

    List<Customer> findCustomersByLastName(String lastName);

}
