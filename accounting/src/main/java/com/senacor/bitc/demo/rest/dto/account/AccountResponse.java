package com.senacor.bitc.demo.rest.dto.account;

import com.senacor.bitc.demo.domain.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.core.Relation;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Relation(value = "account", collectionRelation = "accounts")
public class AccountResponse {

    private AccountType accountType;
    private Integer customerId;

}
