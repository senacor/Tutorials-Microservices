package com.senacor.bitc.demo.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Integer> {

    List<Account> findByCustomerId(Integer customerId);

}
