package com.senacor.bitc.demo.rest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CustomerAddressRequest {

    private String street;
    private String houseNr;
    private String city;
    private String zipCode;

}
