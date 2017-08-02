package com.senacor.bitc.demo.service;


import com.senacor.bitc.demo.domain.Account;
import com.senacor.bitc.demo.feign.client.demo.exception.CustomerNotFoundException;

import java.util.List;

public interface AccountService {

    Account loadAccountById(Integer accountId);

    Account saveAccount(Account account) throws CustomerNotFoundException;

    List<Account> findAccountsByCustomerId(Integer customerId);

}
