package com.senacor.bitc.demo.rest;

import com.senacor.bitc.demo.domain.Customer;
import com.senacor.bitc.demo.rest.dto.mapper.CustomerMapper;
import com.senacor.bitc.demo.rest.dto.request.CustomerRequest;
import com.senacor.bitc.demo.rest.dto.response.CustomerResponse;
import com.senacor.bitc.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(produces = "application/json")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
        this.customerService = customerService;
        this.customerMapper = customerMapper;
    }

    @RequestMapping(value = "/{customerId}", method = RequestMethod.GET)
    @ResponseBody
    public CustomerResponse getCustomerById(@PathVariable Integer customerId) {
        return customerMapper.fromCustomerToCustomerResponse(
                customerService.loadCustomerById(customerId));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Resources<CustomerResponse> getCustomersByName(
            @RequestParam(value = "lastName", defaultValue = "", required = false) String lastName) {
        return new Resources<>(
                customerService.findCustomersByLastName(lastName)
                .stream()
                .map(customer -> customerMapper.fromCustomerToCustomerResponse(customer))
                .collect(Collectors.toList()));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public CustomerResponse createCustomer(@RequestBody CustomerRequest customerRequest) {
        return customerMapper.fromCustomerToCustomerResponse(
                customerService.saveCustomer(
                        customerMapper.fromCustomerRequestToCustomer(customerRequest)));
    }

    // Note: It is arguable if the search for customers should be in the customer endpoint
    //      If there are several search implementations (lastName, firstName, ...) it would
    //      be better to add a customer-search endpoint ;)

}
