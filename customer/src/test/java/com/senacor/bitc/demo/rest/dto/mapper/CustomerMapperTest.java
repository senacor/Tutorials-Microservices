package com.senacor.bitc.demo.rest.dto.mapper;

import com.senacor.bitc.demo.domain.Customer;
import com.senacor.bitc.demo.domain.CustomerAddress;
import com.senacor.bitc.demo.rest.dto.LinkRelations;
import com.senacor.bitc.demo.rest.dto.request.CustomerRequest;
import com.senacor.bitc.demo.rest.dto.response.CustomerResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerMapperTest {

    @Autowired
    private CustomerMapper sut;

    private static String BASE_PATH = "http://localhost";
    private static Integer CUSTOMER_ID = 1;

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
    public void fromCustomerRequestToCustomer() {
        Customer customer = sut.fromCustomerRequestToCustomer(getCustomerRequest());

        assertThat(customer.getBirthDate(), is(getCustomerRequest().getBirthDate()));
        assertThat(customer.getComment(), is(getCustomerRequest().getComment()));
        assertThat(customer.getFirstName(), is(getCustomerRequest().getFirstName()));
        assertThat(customer.getLastName(), is(getCustomerRequest().getLastName()));

        assertNull(customer.getId());
    }

    @Test
    public void fromCustomerToCustomerResponse() {

        CustomerResponse customerResponse = sut.fromCustomerToCustomerResponse(getCustomer());

        verifyCustomerResponse(customerResponse);
        assertThat(customerResponse.getLinks().size(), is(1));
    }

    @Test
    public void fromCustomerWithAddressToCustomerResponse() {
        CustomerResponse customerResponse = sut.fromCustomerToCustomerResponse(getCustomerWithAddress());

        verifyCustomerResponse(customerResponse);

        assertThat(customerResponse.getLinks().size(), is(2));
        assertThat(customerResponse.getLink(LinkRelations.ADDRESS.getName()).getRel(), is("address"));
        assertThat(customerResponse.getLink(LinkRelations.ADDRESS.getName()).getHref(),
                is(BASE_PATH + "/" + getCustomerWithAddress().getId() + "/address"));
    }

    private void verifyCustomerResponse(CustomerResponse customerResponse) {
        assertThat(customerResponse.getKey(), is(getCustomer().getId()));
        assertThat(customerResponse.getBirthDate(), is(getCustomer().getBirthDate()));
        assertThat(customerResponse.getComment(), is(getCustomer().getComment()));
        assertThat(customerResponse.getFirstName(), is(getCustomer().getFirstName()));
        assertThat(customerResponse.getLastName(), is(getCustomer().getLastName()));

        assertThat(customerResponse.getLink(Link.REL_SELF).getRel(), is(Link.REL_SELF));
        assertThat(customerResponse.getLink(Link.REL_SELF).getHref(), is(BASE_PATH + "/" + getCustomer().getId()));
    }

    private Customer getCustomer() {
        return Customer.builder()
                .firstName("First")
                .lastName("Last")
                .birthDate(LocalDate.of(2000, 1, 1))
                .comment("comment")
                .id(CUSTOMER_ID)
                .build();
    }

    private Customer getCustomerWithAddress() {

        Customer customer = getCustomer();
        customer.setCustomerAddress(
                CustomerAddress.builder().build());

        return customer;
    }

    private CustomerRequest getCustomerRequest() {
        return CustomerRequest.builder()
                .firstName("First")
                .lastName("Last")
                .birthDate(LocalDate.of(2000, 1, 1))
                .comment("comment")
                .build();
    }
}