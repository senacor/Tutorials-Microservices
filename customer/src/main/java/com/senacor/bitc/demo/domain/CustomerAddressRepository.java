package com.senacor.bitc.demo.domain;

import org.springframework.data.repository.CrudRepository;

public interface CustomerAddressRepository extends CrudRepository<CustomerAddress, Integer> {

    CustomerAddress findByCustomerId(Integer customerId);

}
