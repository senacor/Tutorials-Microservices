package com.senacor.bitc.demo.rest;

import com.senacor.bitc.demo.domain.Account;
import com.senacor.bitc.demo.feign.client.demo.exception.CustomerNotFoundException;
import com.senacor.bitc.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(produces = "application/json")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(value = "/{accountId}", method = RequestMethod.GET)
    @ResponseBody
    public Account getAccountById(@PathVariable Integer accountId) {
        return accountService.loadAccountById(accountId);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Account> getAccountsByCustomerId(
            @RequestParam(value = "customerId", defaultValue = "0", required = false) Integer customerId) {
        return accountService.findAccountsByCustomerId(customerId);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public Account createAccount(@RequestBody Account account) throws CustomerNotFoundException {
        return accountService.saveAccount(account);
    }

}
