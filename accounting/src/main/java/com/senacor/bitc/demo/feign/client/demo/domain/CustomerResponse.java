package com.senacor.bitc.demo.feign.client.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CustomerResponse extends ResourceSupport {

    // Since the customer already exists and is only identified by its
    // id in the database, we have to expose the id to consumers through this.
    // Usually it is better to add a UNIQUE functional key to the database
    // that can be exposed to consumers instead of a technical identifier.
    // For a user a user-name or email could be a functional identifier.
    private Integer key;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    private String comment;

}
