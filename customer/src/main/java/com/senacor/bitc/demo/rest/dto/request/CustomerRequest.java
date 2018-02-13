package com.senacor.bitc.demo.rest.dto.request;

import lombok.*;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class CustomerRequest {

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    private String comment;

}
