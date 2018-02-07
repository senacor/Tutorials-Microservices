package com.senacor.bitc.demo.rest.dto.account;

import com.senacor.bitc.demo.domain.Account;
import com.senacor.bitc.demo.domain.AccountType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountMapperTest {


    @Autowired
    private AccountMapper sut;

    private static String BASE_PATH = "http://localhost";
    private static Integer CUSTOMER_ID = 1;
    private static Integer ACCOUNT_ID = 1;

    // note: the setup and teardown of the mock request is needed because the link-builder of HATEOAS
    //       depends on the current request. if no request is present an exception will be thrown.

    @Before
    public void setup() {
        HttpServletRequest mockRequest = new MockHttpServletRequest();
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    }

    @After
    public void teardown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void fromAccountRequestToAccount() {

        Account account = sut.fromAccountRequestToAccount(getAccountRequest());

        assertThat(account.getAccountType(), is(getAccountRequest().getAccountType()));
        assertThat(account.getCustomerId(), is(getAccountRequest().getCustomerId()));
        assertNull(account.getId());
    }

    @Test
    public void fromAccountToAccountResponse() {
        Resource<AccountResponse> accountResponse = sut.fromAccountToAccountResponse(getAccount());

        assertThat(accountResponse.getContent().getAccountType(), is(getAccount().getAccountType()));
        assertThat(accountResponse.getContent().getCustomerId(), is(getAccount().getCustomerId()));
        assertThat(accountResponse.getLink(Link.REL_SELF).getHref(), is(BASE_PATH + "/" + getAccount().getId()));
        assertThat(accountResponse.getLink(Link.REL_SELF).getRel(), is(Link.REL_SELF));
    }

    private Account getAccount() {
        return Account.builder()
                .accountType(AccountType.GIRO)
                .customerId(CUSTOMER_ID)
                .id(ACCOUNT_ID)
                .build();
    }

    private AccountRequest getAccountRequest() {
        return AccountRequest.builder()
                .accountType(AccountType.GIRO)
                .customerId(CUSTOMER_ID)
                .build();
    }
}