package com.senacor.bitc.demo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senacor.bitc.demo.domain.Customer;
import com.senacor.bitc.demo.service.CustomerService;
import com.senacor.bitc.demo.util.TestUtil;
import com.senacor.bitc.demo.rest.CustomerController;
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

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * References for further info on Spring Testing with MockMvc, MockBean (...):
 * http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-testing-spring-boot-applications-mocking-beans
 * https://www.petrikainulainen.net/programming/spring-framework/integration-testing-of-spring-mvc-applications-write-clean-assertions-with-jsonpath/
 */
@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper mapper;

    private JacksonTester<Customer> customerJsonTester;

    @Before
    public void setUp() throws Exception {
        JacksonTester.initFields(this, mapper);
    }

    @Test
    public void getCustomerById() throws Exception {

        given(this.customerService.loadCustomerById(1))
                .willReturn(getCustomerWithId());

        mockMvc.perform(get("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().json(customerJsonTester.write(getCustomerWithId()).getJson()));

    }

    @Test
    public void getCustomersByName() throws Exception {
        given(this.customerService.findCustomersByLastName("Last"))
                .willReturn(Collections.singletonList(getCustomerWithId()));

        mockMvc.perform(get("/customer?lastName=Last"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(getCustomerWithId().getId())))
                .andExpect(jsonPath("$[0].firstName", is(getCustomerWithId().getFirstName())))
                .andExpect(jsonPath("$[0].lastName", is(getCustomerWithId().getLastName())))
                .andExpect(jsonPath("$[0].birthDate", is(getCustomerWithId().getBirthDate().toString())))
                .andExpect(jsonPath("$[0].comment", is(getCustomerWithId().getComment())));

    }

    @Test
    public void createCustomer() throws Exception {

        given(this.customerService.saveCustomer(getCustomerWithoutId()))
                .willReturn(getCustomerWithId());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/customer");
        request.contentType(TestUtil.APPLICATION_JSON_UTF8);

        request.content(mapper.writeValueAsString(getCustomerWithoutId()));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(getCustomerWithId().getId())))
                .andExpect(jsonPath("$.firstName", is(getCustomerWithId().getFirstName())))
                .andExpect(jsonPath("$.lastName", is(getCustomerWithId().getLastName())))
                .andExpect(jsonPath("$.birthDate", is(getCustomerWithId().getBirthDate().toString())))
                .andExpect(jsonPath("$.comment", is(getCustomerWithId().getComment())));
    }

    private Customer getCustomerWithId() {
        return getCustomer(1);
    }

    private Customer getCustomerWithoutId() {
        return getCustomer(null);
    }

    private Customer getCustomer(Integer id) {
        Customer.CustomerBuilder builder = Customer.builder();

        if (id != null) {
            builder.id(id);
        }

        builder.firstName("First")
                .lastName("Last")
                .birthDate(LocalDate.of(2000, 1, 1))
                .comment("comment");

        return builder.build();
    }

}