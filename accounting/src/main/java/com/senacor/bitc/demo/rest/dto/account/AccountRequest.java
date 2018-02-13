package com.senacor.bitc.demo.rest.dto.account;

import com.senacor.bitc.demo.domain.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccountRequest {

    private AccountType accountType;
    private Integer customerId;

}
