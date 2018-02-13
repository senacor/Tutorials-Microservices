package com.senacor.bitc.demo.rest.dto.mapper;

import com.senacor.bitc.demo.domain.Customer;
import com.senacor.bitc.demo.domain.CustomerAddress;
import com.senacor.bitc.demo.rest.CustomerAddressController;
import com.senacor.bitc.demo.rest.CustomerController;
import com.senacor.bitc.demo.rest.dto.LinkRelations;
import com.senacor.bitc.demo.rest.dto.request.CustomerAddressRequest;
import com.senacor.bitc.demo.rest.dto.response.CustomerAddressResponse;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class CustomerAddressMapper {

    public CustomerAddress fromCustomerAddressRequestToCustomerAddress(CustomerAddressRequest customerAddressRequest, Integer customerId) {

        return CustomerAddress.builder()
                .customer(Customer.builder()
                        .id(customerId)
                        .build())
                .city(customerAddressRequest.getCity())
                .houseNr(customerAddressRequest.getHouseNr())
                .street(customerAddressRequest.getStreet())
                .zipCode(customerAddressRequest.getZipCode())
                .build();
    }

    public CustomerAddressResponse fromCustomerAddressToCustomerAddressResponse(CustomerAddress customerAddress) {

        return addCustomerAddressLinks(
                CustomerAddressResponse.builder()
                        .city(customerAddress.getCity())
                        .houseNr(customerAddress.getHouseNr())
                        .street(customerAddress.getStreet())
                        .zipCode(customerAddress.getZipCode())
                        .build(),
                customerAddress.getCustomer().getId());
    }

    public static CustomerAddressResponse addCustomerAddressLinks(CustomerAddressResponse customerAddressResponse, Integer customerId) {

        customerAddressResponse.add(linkTo(methodOn(CustomerAddressController.class)
                .getCustomerAddress(customerId)).withSelfRel());

        customerAddressResponse.add(linkTo(methodOn(CustomerController.class)
                .getCustomerById(customerId))
                .withRel(LinkRelations.CUSTOMER.getName()));

        return customerAddressResponse;
    }

}
