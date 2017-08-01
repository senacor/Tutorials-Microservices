package com.senacor.bitc.demo.rest;

import com.senacor.bitc.demo.domain.Account;
import com.senacor.bitc.demo.domain.AccountType;
import com.senacor.bitc.demo.service.AccountService;
import com.senacor.bitc.demo.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    @Test
    public void getAccountById() throws Exception {

        given(this.accountService.loadAccountById(1))
                .willReturn(getAccount());

        mockMvc.perform(get("/account/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(getAccount().getId())))
                .andExpect(jsonPath("$.accountType", is(getAccount().getAccountType().toString())))
                .andExpect(jsonPath("$.customerId", is(getAccount().getCustomerId())));
    }

    @Test
    public void getAccountsByCustomerId() throws Exception {
        given(this.accountService.findAccountsByCustomerId(1))
                .willReturn(Stream.of(getAccount()).collect(Collectors.toList()));

        mockMvc.perform(get("/account?customerId=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(getAccount().getId())))
                .andExpect(jsonPath("$[0].accountType", is(getAccount().getAccountType().toString())))
                .andExpect(jsonPath("$[0].customerId", is(getAccount().getCustomerId())));

    }

    @Test
    public void createAccount() throws Exception {

        given(this.accountService.saveAccount(any(Account.class)))
                .willReturn(getAccount());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/account");
        request.contentType(TestUtil.APPLICATION_JSON_UTF8);

        request.content("{ " +
                        "\"accountType\": \"CREDIT_CARD\", " +
                        "\"customerId\": 1" +
                        "}");

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(getAccount().getId())))
                .andExpect(jsonPath("$.accountType", is(getAccount().getAccountType().toString())))
                .andExpect(jsonPath("$.customerId", is(getAccount().getCustomerId())));
    }

    private Account getAccount() {

        return Account.builder()
                .id(1)
                .accountType(AccountType.CREDIT_CARD)
                .customerId(1)
                .build();
    }

}