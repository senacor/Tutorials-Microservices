package com.senacor.bitc.demo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senacor.bitc.demo.domain.Account;
import com.senacor.bitc.demo.domain.AccountType;
import com.senacor.bitc.demo.feign.client.demo.domain.CustomerResponse;
import com.senacor.bitc.demo.rest.dto.account.AccountMapper;
import com.senacor.bitc.demo.rest.dto.account.AccountRequest;
import com.senacor.bitc.demo.rest.dto.account.AccountResponse;
import com.senacor.bitc.demo.service.AccountService;
import com.senacor.bitc.demo.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountMapper accountMapper;

    @Autowired
    private ObjectMapper mapper;

    private JacksonTester<Account> accountJsonTester;

    private static String BASE_PATH = "http://localhost";
    private static Integer CUSTOMER_ID = 1;
    private static Integer ACCOUNT_ID = 1;
    private static AccountType ACCOUNT_TYPE = AccountType.GIRO;

    @Before
    public void setUp() throws Exception {
        JacksonTester.initFields(this, mapper);
    }

    @Test
    public void getAccountById() throws Exception {

        given(this.accountService.loadAccountById(1))
                .willReturn(getAccountWithId());

        given(this.accountMapper.fromAccountToAccountResponse(getAccountWithId()))
                .willReturn(getAccountResponse());

        verifyJson(
                mockMvc.perform(get("/1"))
                        .andExpect(status().isOk()), false);
    }

    @Test
    public void getAccountsByCustomerId() throws Exception {
        given(this.accountService.findAccountsByCustomerId(CUSTOMER_ID))
                .willReturn(Collections.singletonList(getAccountWithId()));

        given(this.accountMapper.fromAccountToAccountResponse(getAccountWithId()))
                .willReturn(getAccountResponse());

        verifyJson(
                mockMvc.perform(get("?customerId=" + CUSTOMER_ID))
                        .andExpect(status().isOk()),
                true);

    }

    @Test
    public void createAccount() throws Exception {

        given(this.accountService.saveAccount(getAccountWithoutId()))
                .willReturn(getAccountWithId());

        AccountRequest accountRequest = getAccountRequest();

        given(this.accountMapper.fromAccountToAccountResponse(getAccountWithId()))
                .willReturn(getAccountResponse());
        given(this.accountMapper.fromAccountRequestToAccount(accountRequest))
                .willReturn(getAccountWithoutId());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("");
        request.contentType(TestUtil.APPLICATION_JSON_UTF8);

        request.content(mapper.writeValueAsString(accountRequest));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        verifyJson(
                mockMvc.perform(request)
                        .andExpect(status().isCreated()), false);
    }

    private ResultActions verifyJson(final ResultActions actions, boolean isArray) throws Exception {
        actions
                .andExpect(content().contentType(TestUtil.HAL_JSON_UTF8))
                .andExpect(jsonPath((isArray ? "$._embedded.accounts[0]" : "$") + ".accountType", is(getAccountWithId().getAccountType().toString())))
                .andExpect(jsonPath((isArray ? "$._embedded.accounts[0]" : "$") + ".customerId", is(getAccountWithId().getCustomerId())))
                .andExpect(jsonPath((isArray ? "$._embedded.accounts[0]" : "$") + "._links.self.href", is(BASE_PATH + "/" + getAccountWithId().getId())));

        return actions;
    }

    private AccountRequest getAccountRequest() {
        return AccountRequest.builder()
                .accountType(ACCOUNT_TYPE)
                .customerId(CUSTOMER_ID)
                .build();
    }

    private Resource<AccountResponse> getAccountResponse() {

        AccountResponse accountResponse = AccountResponse.builder()
                .accountType(ACCOUNT_TYPE)
                .customerId(CUSTOMER_ID)
                .build();

        Resource<AccountResponse> accountResponseResource = new Resource<>(accountResponse);

        accountResponseResource.add(new Link(BASE_PATH + "/" + ACCOUNT_ID, Link.REL_SELF));

        return accountResponseResource;
    }

    private Account getAccountWithId() {
        return getAccount(ACCOUNT_ID);
    }

    private Account getAccountWithoutId() {
        return getAccount(null);
    }

    private Account getAccount(Integer id) {

        Account.AccountBuilder builder = Account.builder();

        if (id != null) {
            builder.id(id);
        }

        builder.accountType(ACCOUNT_TYPE)
                .customerId(CUSTOMER_ID);

        return builder.build();
    }

}