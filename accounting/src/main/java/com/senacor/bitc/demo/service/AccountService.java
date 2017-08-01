package com.senacor.bitc.demo.service;


import com.senacor.bitc.demo.domain.Account;

import java.util.List;

public interface AccountService {

    Account loadAccountById(Integer accountId);

    Account saveAccount(Account account);

    List<Account> findAccountsByCustomerId(Integer customerId);

}
