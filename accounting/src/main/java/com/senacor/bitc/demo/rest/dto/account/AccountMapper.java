package com.senacor.bitc.demo.rest.dto.account;

import com.senacor.bitc.demo.domain.Account;
import com.senacor.bitc.demo.rest.AccountController;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@Component
public class AccountMapper {

    public Account fromAccountRequestToAccount(AccountRequest accountRequest) {
        return Account.builder()
                .accountType(accountRequest.getAccountType())
                .customerId(accountRequest.getCustomerId())
                .build();
    }

    public Resource<AccountResponse> fromAccountToAccountResponse(Account account) {

        AccountResponse response = AccountResponse.builder()
                .accountType(account.getAccountType())
                .customerId(account.getCustomerId())
                .build();

        return addAccountLinks(new Resource<>(response), account.getId());
    }

    public static Resource<AccountResponse> addAccountLinks(Resource<AccountResponse> accountResponseResource, Integer accountId) {

        accountResponseResource.add(
                linkTo(methodOn(AccountController.class).getAccountById(accountId))
                .withSelfRel());

        return accountResponseResource;

    }

}
