package com.senacor.bitc.customer.service;


import com.senacor.bitc.customer.domain.Customer;

import java.util.List;

public interface CustomerService {

    Customer loadCustomerById(Integer customerId);

    Customer saveCustomer(Customer customer);

    List<Customer> findCustomersByLastName(String lastName);

}
