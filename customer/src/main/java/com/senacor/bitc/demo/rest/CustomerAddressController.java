package com.senacor.bitc.demo.rest;

import com.senacor.bitc.demo.rest.dto.mapper.CustomerAddressMapper;
import com.senacor.bitc.demo.rest.dto.request.CustomerAddressRequest;
import com.senacor.bitc.demo.rest.dto.request.CustomerRequest;
import com.senacor.bitc.demo.rest.dto.response.CustomerAddressResponse;
import com.senacor.bitc.demo.rest.dto.response.CustomerResponse;
import com.senacor.bitc.demo.service.CustomerAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/{customerId}/address", produces = "application/json")
public class CustomerAddressController {

    private final CustomerAddressService customerAddressService;
    private final CustomerAddressMapper customerAddressMapper;

    @Autowired
    public CustomerAddressController(CustomerAddressService customerAddressService, CustomerAddressMapper customerAddressMapper) {
        this.customerAddressService = customerAddressService;
        this.customerAddressMapper = customerAddressMapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public CustomerAddressResponse getCustomerAddress(
            @PathVariable(value = "customerId") Integer customerId) {

        return customerAddressMapper.fromCustomerAddressToCustomerAddressResponse(
                customerAddressService.loadCustomerAddressByCustomerId(customerId));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public CustomerAddressResponse createCustomerAddress(
            @PathVariable(value = "customerId") Integer customerId,
            @RequestBody CustomerAddressRequest customerAddressRequest) {
        return customerAddressMapper.fromCustomerAddressToCustomerAddressResponse(
                customerAddressService.saveCustomerAddress(
                        customerAddressMapper.fromCustomerAddressRequestToCustomerAddress(customerAddressRequest, customerId)));
    }

}
