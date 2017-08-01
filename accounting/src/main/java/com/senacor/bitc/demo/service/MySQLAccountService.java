package com.senacor.bitc.demo.service;

import com.senacor.bitc.demo.domain.Account;
import com.senacor.bitc.demo.domain.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MySQLAccountService implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public MySQLAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public Account loadAccountById(Integer accountId) {
        return accountRepository.findOne(accountId);
    }

    @Override
    public Account saveAccount(Account account) {

        // TODO: Call the customer service and check it the customer with the given ID exists!

        return accountRepository.save(account);
    }

    @Override
    public List<Account> findAccountsByCustomerId(Integer customerId) {
        return accountRepository.findByCustomerId(customerId);
    }
}
