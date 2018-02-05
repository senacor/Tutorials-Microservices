package com.senacor.bitc.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor // needed for JPA
@AllArgsConstructor // needed for builder (because of NoArgsConstructor)
@Entity
@Table(name = "customer_address")
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "street")
    private String street;

    @Column(name = "house_nr")
    public String houseNr;

    @Column(name = "city")
    private String city;

    @Column(name = "zip")
    private String zipCode;

    @OneToOne(optional=false)
    @JoinColumn(name = "customer_id")
    Customer customer;

}
