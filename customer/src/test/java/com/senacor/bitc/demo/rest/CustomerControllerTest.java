package com.senacor.bitc.demo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senacor.bitc.demo.domain.Customer;
import com.senacor.bitc.demo.rest.dto.LinkRelations;
import com.senacor.bitc.demo.rest.dto.mapper.CustomerMapper;
import com.senacor.bitc.demo.rest.dto.request.CustomerRequest;
import com.senacor.bitc.demo.rest.dto.response.CustomerResponse;
import com.senacor.bitc.demo.service.CustomerService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html
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

    @MockBean
    private CustomerMapper customerMapper;

    @Autowired
    private ObjectMapper mapper;

    private JacksonTester<CustomerResponse> customerResponseJsonTester;

    // Note: set without "/customer" here because the controller does not specify this, is globally set
    private static String BASE_PATH = "http://localhost";
    private static String ADDRESS_PATH = "/address";

    private static Integer CUSTOMER_ID = 1;

    @Before
    public void setUp() throws Exception {
        JacksonTester.initFields(this, mapper);
    }

    @Test
    public void getCustomerById() throws Exception {

        given(this.customerService.loadCustomerById(1))
                .willReturn(getCustomerWithId());

        given(this.customerMapper.fromCustomerToCustomerResponse(getCustomerWithId()))
                .willReturn(getCustomerResponse());

        verifyJsonCustomer(mockMvc.perform(get( "/1"))
                .andExpect(status().isOk()), false);

    }

    @Test
    public void getCustomerWithAddressById() throws Exception {

        given(this.customerService.loadCustomerById(1))
                .willReturn(getCustomerWithId());

        given(this.customerMapper.fromCustomerToCustomerResponse(getCustomerWithId()))
                .willReturn(getCustomerResponseWithAddressLink());

        verifyJsonAddressLink(
        verifyJsonCustomer(mockMvc.perform(get( "/1"))
                .andExpect(status().isOk()), false));

    }

    @Test
    public void getCustomersByName() throws Exception {
        given(this.customerService.findCustomersByLastName("Last"))
                .willReturn(Collections.singletonList(getCustomerWithId()));

        given(this.customerMapper.fromCustomerToCustomerResponse(getCustomerWithId()))
                .willReturn(getCustomerResponse());

        verifyJsonCustomer(mockMvc.perform(get("/search?lastName=Last"))
                .andExpect(status().isOk()), true);

    }

    @Test
    public void createCustomer() throws Exception {

        given(this.customerService.saveCustomer(getCustomerWithoutId()))
                .willReturn(getCustomerWithId());

        given(this.customerMapper.fromCustomerToCustomerResponse(getCustomerWithId()))
                .willReturn(getCustomerResponse());
        given(this.customerMapper.fromCustomerRequestToCustomer(getCustomerRequest()))
                .willReturn(getCustomerWithoutId());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/");
        request.contentType(TestUtil.APPLICATION_JSON_UTF8);
        request.content(mapper.writeValueAsString(getCustomerRequest()));

        verifyJsonCustomer(
                mockMvc.perform(request).andExpect(status().isCreated()), false);
    }

    private ResultActions verifyJsonCustomer(final ResultActions actions, boolean isArray) throws Exception {
        actions
                .andExpect(content().contentType(TestUtil.HAL_JSON_UTF8))
                .andExpect(jsonPath((isArray ? "$._embedded.customers[0]" : "$") + ".key", is(getCustomerWithId().getId())))
                .andExpect(jsonPath((isArray ? "$._embedded.customers[0]" : "$") + ".firstName", is(getCustomerWithId().getFirstName())))
                .andExpect(jsonPath((isArray ? "$._embedded.customers[0]" : "$") + ".lastName", is(getCustomerWithId().getLastName())))
                .andExpect(jsonPath((isArray ? "$._embedded.customers[0]" : "$") + ".birthDate", is(getCustomerWithId().getBirthDate().toString())))
                .andExpect(jsonPath((isArray ? "$._embedded.customers[0]" : "$") + ".comment", is(getCustomerWithId().getComment())))
                .andExpect(jsonPath((isArray ? "$._embedded.customers[0]" : "$") + "._links.self.href", is(BASE_PATH + "/" + getCustomerWithId().getId())));

        return actions;
    }

    private ResultActions verifyJsonAddressLink(final ResultActions actions) throws Exception {
        actions
                .andExpect(jsonPath("$._links.address.href",
                        is(BASE_PATH + "/" + getCustomerWithId().getId() + ADDRESS_PATH)));

        return actions;
    }

    private CustomerResponse getCustomerResponseWithAddressLink() {

        CustomerResponse customerResponse = getCustomerResponse();
        customerResponse.add(new Link(BASE_PATH + "/" + CUSTOMER_ID + ADDRESS_PATH, LinkRelations.ADDRESS.getName()));

        return customerResponse;
    }

    private CustomerResponse getCustomerResponse() {

        CustomerResponse customerResponse = CustomerResponse.builder()
                .firstName("First")
                .lastName("Last")
                .birthDate(LocalDate.of(2000, 1, 1))
                .comment("comment")
                .key(CUSTOMER_ID)
                .build();

        customerResponse.add(new Link(BASE_PATH + "/" + CUSTOMER_ID, Link.REL_SELF));

        return customerResponse;
    }

    private CustomerRequest getCustomerRequest() {
        return CustomerRequest.builder()
                .firstName("First")
                .lastName("Last")
                .birthDate(LocalDate.of(2000, 1, 1))
                .comment("comment")
                .build();
    }

    private Customer getCustomerWithId() {
        return getCustomer(CUSTOMER_ID);
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