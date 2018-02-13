package com.senacor.bitc.demo.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CustomerAddressResponse extends ResourceSupport {

    private String street;
    private String houseNr;
    private String city;
    private String zipCode;

}
