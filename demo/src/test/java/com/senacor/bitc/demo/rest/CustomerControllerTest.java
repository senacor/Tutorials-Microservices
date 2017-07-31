package com.senacor.bitc.demo.rest;

import com.senacor.bitc.demo.domain.Customer;
import com.senacor.bitc.demo.service.CustomerService;
import com.senacor.bitc.demo.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    @Test
    public void getCustomerById() throws Exception {

        given(this.customerService.loadCustomerById(1L))
                .willReturn(getCustomer());

        mockMvc.perform(get("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(getCustomer().getId())))
                .andExpect(jsonPath("$.firstName", is(getCustomer().getFirstName())))
                .andExpect(jsonPath("$.lastName", is(getCustomer().getLastName())))
                .andExpect(jsonPath("$.birthDate", is(getCustomer().getBirthDate().toString())))
                .andExpect(jsonPath("$.comment", is(getCustomer().getComment())));
    }

    @Test
    public void getCustomersByName() throws Exception {
        given(this.customerService.findCustomersByLastName("Last"))
                .willReturn(Stream.of(getCustomer()).collect(Collectors.toList()));

        mockMvc.perform(get("/customer?lastName=Last"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(getCustomer().getId())))
                .andExpect(jsonPath("$[0].firstName", is(getCustomer().getFirstName())))
                .andExpect(jsonPath("$[0].lastName", is(getCustomer().getLastName())))
                .andExpect(jsonPath("$[0].birthDate", is(getCustomer().getBirthDate().toString())))
                .andExpect(jsonPath("$[0].comment", is(getCustomer().getComment())));

    }

    @Test
    public void createCustomer() throws Exception {

        given(this.customerService.saveCustomer(any(Customer.class)))
                .willReturn(getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/customer");
        request.contentType(TestUtil.APPLICATION_JSON_UTF8);

        request.content("{ " +
                        "\"firstName\": \"First\", " +
                        "\"lastName\": \"Last\", " +
                        "\"birthDate\": \"2000-01-01\", " +
                        "\"comment\": \"nothing\"" +
                        "}");

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(getCustomer().getId())))
                .andExpect(jsonPath("$.firstName", is(getCustomer().getFirstName())))
                .andExpect(jsonPath("$.lastName", is(getCustomer().getLastName())))
                .andExpect(jsonPath("$.birthDate", is(getCustomer().getBirthDate().toString())))
                .andExpect(jsonPath("$.comment", is(getCustomer().getComment())));
    }

    private Customer getCustomer() {

        return Customer.builder()
                .id(1)
                .firstName("First")
                .lastName("Last")
                .birthDate(LocalDate.of(2000,1,1))
                .comment("comment")
                .build();
    }

}