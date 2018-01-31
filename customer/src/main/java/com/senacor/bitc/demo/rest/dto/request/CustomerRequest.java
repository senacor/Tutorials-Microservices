package com.senacor.bitc.demo.rest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class CustomerRequest extends ResourceSupport {

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    private String comment;

}
