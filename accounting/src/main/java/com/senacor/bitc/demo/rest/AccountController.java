package com.senacor.bitc.demo.rest;

import com.senacor.bitc.demo.feign.client.demo.exception.CustomerNotFoundException;
import com.senacor.bitc.demo.rest.dto.account.AccountMapper;
import com.senacor.bitc.demo.rest.dto.account.AccountRequest;
import com.senacor.bitc.demo.rest.dto.account.AccountResponse;
import com.senacor.bitc.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping(produces = "application/hal+json")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @RequestMapping(value = "/{accountId}", method = RequestMethod.GET)
    @ResponseBody
    public Resource<AccountResponse> getAccountById(@PathVariable Integer accountId) {
        return accountMapper.fromAccountToAccountResponse(
                accountService.loadAccountById(accountId));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Resources<Resource<AccountResponse>> getAccountsByCustomerId(
            @RequestParam(value = "customerId", defaultValue = "0", required = false) Integer customerId) {
        return new Resources<>(accountService.findAccountsByCustomerId(customerId)
                .stream()
                .map(account -> accountMapper.fromAccountToAccountResponse(account))
                .collect(Collectors.toList()));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public Resource<AccountResponse> createAccount(
            @RequestBody AccountRequest accountRequest) throws CustomerNotFoundException {
        return accountMapper.fromAccountToAccountResponse(
                accountService.saveAccount(
                        accountMapper.fromAccountRequestToAccount(accountRequest)));
    }

}
