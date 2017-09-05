package com.senacor.bitc.demo.rest;

import com.senacor.bitc.demo.domain.Customer;
import com.senacor.bitc.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/customer", produces = "application/json")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping(value = "/{customerId}", method = RequestMethod.GET)
    @ResponseBody
    public Customer getCustomerById(@PathVariable Integer customerId) {
        return customerService.loadCustomerById(customerId);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Customer> getCustomersByName(
            @RequestParam(value = "lastName", defaultValue = "", required = false) String lastName) {
        return customerService.findCustomersByLastName(lastName);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }

    // Note: It is arguable if the search for customers should be in the customer endpoint
    //      If there are several search implementations (lastName, firstName, ...) it would
    //      be better to add a customer-search endpoint ;)

}
