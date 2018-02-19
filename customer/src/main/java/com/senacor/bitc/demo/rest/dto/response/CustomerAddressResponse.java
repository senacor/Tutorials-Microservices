package com.senacor.bitc.demo.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Relation(value = "address", collectionRelation = "addresses")
public class CustomerAddressResponse extends ResourceSupport {

    private String street;
    private String houseNr;
    private String city;
    private String zipCode;

}
