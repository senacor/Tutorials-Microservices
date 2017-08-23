package com.senacor.bitc.demo.feign.client.demo.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    public Integer id;

    public String firstName;

    public String lastName;

    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate birthDate;

    public String comment;

}
