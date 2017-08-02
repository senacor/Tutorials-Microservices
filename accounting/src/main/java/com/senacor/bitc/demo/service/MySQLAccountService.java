package com.senacor.bitc.demo.service;

import com.senacor.bitc.demo.domain.Account;
import com.senacor.bitc.demo.domain.AccountRepository;
import com.senacor.bitc.demo.feign.client.demo.CustomerClient;
import com.senacor.bitc.demo.feign.client.demo.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MySQLAccountService implements AccountService {

    private final CustomerClient customerClient;
    private final AccountRepository accountRepository;

    @Autowired
    public MySQLAccountService(
            AccountRepository accountRepository,
            CustomerClient customerClient) {
        this.accountRepository = accountRepository;
        this.customerClient = customerClient;
    }


    @Override
    public Account loadAccountById(Integer accountId) {
        return accountRepository.findOne(accountId);
    }

    @Override
    public Account saveAccount(Account account) throws CustomerNotFoundException {

        Integer customerId = account.customerId;

        if (customerId == null ||
                customerClient.getCustomerById(customerId) == null)
        {
            throw new CustomerNotFoundException();
        }

        return accountRepository.save(account);
    }

    @Override
    public List<Account> findAccountsByCustomerId(Integer customerId) {
        return accountRepository.findByCustomerId(customerId);
    }
}
