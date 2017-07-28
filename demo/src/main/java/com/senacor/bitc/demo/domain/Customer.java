package com.senacor.bitc.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor // needed for JPA
@AllArgsConstructor // needed for builder (because of NoArgsConstructor)
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "first_name", nullable = false)
    public String firstName;
    @Column(name = "last_name", nullable = false)
    public String lastName;
    @Column(name = "birth_date", nullable = false)
    public Date birthDate;
    public String comment;

}
