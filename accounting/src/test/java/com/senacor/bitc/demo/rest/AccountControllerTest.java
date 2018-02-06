package com.senacor.bitc.demo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senacor.bitc.demo.domain.Account;
import com.senacor.bitc.demo.domain.AccountType;
import com.senacor.bitc.demo.rest.dto.account.AccountMapper;
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

    @Autowired
    private AccountController controllerUnderTest;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountMapper accountMapper;

    @Autowired
    private ObjectMapper mapper;

    private JacksonTester<Account> accountJsonTester;

    private static String BASE_PATH = "http://localhost";

    @Before
    public void setUp() throws Exception {
        JacksonTester.initFields(this, mapper);

        // inject actual instance of AccountMapper instead of mock (note: the mock variable is still needed)
        ReflectionTestUtils.setField(controllerUnderTest, "accountMapper", new AccountMapper());
    }

    @Test
    public void getAccountById() throws Exception {

        given(this.accountService.loadAccountById(1))
                .willReturn(getAccountWithId());

        verifyJson(
                mockMvc.perform(get("/1"))
                        .andExpect(status().isOk()), false);
    }

    @Test
    public void getAccountsByCustomerId() throws Exception {
        given(this.accountService.findAccountsByCustomerId(1))
                .willReturn(Collections.singletonList(getAccountWithId()));

        verifyJson(
                mockMvc.perform(get("?customerId=1"))
                        .andExpect(status().isOk()),
                true);

    }

    @Test
    public void createAccount() throws Exception {

        given(this.accountService.saveAccount(getAccountWithoutId()))
                .willReturn(getAccountWithId());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("");
        request.contentType(TestUtil.APPLICATION_JSON_UTF8);

        request.content(mapper.writeValueAsString(getAccountWithoutId()));

        verifyJson(
                mockMvc.perform(request)
                        .andExpect(status().isCreated()), false);
    }

    private ResultActions verifyJson(final ResultActions actions, boolean isArray) throws Exception {
        actions
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath((isArray ? "$[0]" : "$") + ".accountType", is(getAccountWithId().getAccountType().toString())))
                .andExpect(jsonPath((isArray ? "$[0]" : "$") + ".customerId", is(getAccountWithId().getCustomerId())))
                .andExpect(jsonPath((isArray ? "$[0]" : "$") + ".links[0].href", is(BASE_PATH + "/" + getAccountWithId().getId())))
                .andExpect(jsonPath((isArray ? "$[0]" : "$") + ".links[0].rel", is(Link.REL_SELF)));

        return actions;
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