package com.senacor.bitc.demo.rest.dto.account;

import com.senacor.bitc.demo.domain.AccountType;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class AccountRequest {

    private AccountType accountType;
    private Integer customerId;

}
