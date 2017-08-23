package com.senacor.bitc.demo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senacor.bitc.demo.domain.Account;
import com.senacor.bitc.demo.domain.AccountType;
import com.senacor.bitc.demo.service.AccountService;
import com.senacor.bitc.demo.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
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

    @Autowired
    private ObjectMapper mapper;

    private JacksonTester<Account> accountJsonTester;

    @Before
    public void setUp() throws Exception {
        JacksonTester.initFields(this, mapper);
    }

    @Test
    public void getAccountById() throws Exception {

        given(this.accountService.loadAccountById(1))
                .willReturn(getAccountWithId());

        mockMvc.perform(get("/account/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().json(accountJsonTester.write(getAccountWithId()).getJson()));
    }

    @Test
    public void getAccountsByCustomerId() throws Exception {
        given(this.accountService.findAccountsByCustomerId(1))
                .willReturn(Collections.singletonList(getAccountWithId()));

        mockMvc.perform(get("/account?customerId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(getAccountWithId().getId())))
                .andExpect(jsonPath("$[0].accountType", is(getAccountWithId().getAccountType().toString())))
                .andExpect(jsonPath("$[0].customerId", is(getAccountWithId().getCustomerId())));

    }

    @Test
    public void createAccount() throws Exception {

        given(this.accountService.saveAccount(getAccountWithoutId()))
                .willReturn(getAccountWithId());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/account");
        request.contentType(TestUtil.APPLICATION_JSON_UTF8);

        request.content(mapper.writeValueAsString(getAccountWithoutId()));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(getAccountWithId().getId())))
                .andExpect(jsonPath("$.accountType", is(getAccountWithId().getAccountType().toString())))
                .andExpect(jsonPath("$.customerId", is(getAccountWithId().getCustomerId())));
    }

    private Account getAccountWithId() {
        return getAccount(1);
    }

    private Account getAccountWithoutId() {
        return getAccount(null);
    }

    private Account getAccount(Integer id) {

        Account.AccountBuilder builder = Account.builder();

        if (id != null) {
            builder.id(id);
        }

        builder.accountType(AccountType.CREDIT_CARD)
                .customerId(1);

        return builder.build();
    }

}