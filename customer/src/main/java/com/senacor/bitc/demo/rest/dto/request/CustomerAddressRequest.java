package com.senacor.bitc.demo.rest.dto.request;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class CustomerAddressRequest {

    private String street;
    private String houseNr;
    private String city;
    private String zipCode;

}
