package com.senacor.bitc.demo.rest.dto.mapper;

import com.senacor.bitc.demo.domain.Customer;
import com.senacor.bitc.demo.domain.CustomerAddress;
import com.senacor.bitc.demo.rest.dto.LinkRelations;
import com.senacor.bitc.demo.rest.dto.request.CustomerAddressRequest;
import com.senacor.bitc.demo.rest.dto.response.CustomerAddressResponse;
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerAddressMapperTest {

    @Autowired
    private CustomerAddressMapper sut;

    private static String BASE_PATH = "http://localhost";
    private static Integer CUSTOMER_ADDRESS_ID = 1;
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
    public void fromCustomerAddressRequestToCustomerAddress() {

        CustomerAddress customerAddress = sut.fromCustomerAddressRequestToCustomerAddress(
                getCustomerAddressRequest(), CUSTOMER_ID);

        assertThat(customerAddress.getCity(), is(getCustomerAddress().getCity()));
        assertThat(customerAddress.getCustomer().getId(), is(CUSTOMER_ID));
        assertThat(customerAddress.getHouseNr(), is(getCustomerAddress().getHouseNr()));
        assertThat(customerAddress.getStreet(), is(getCustomerAddress().getStreet()));
        assertThat(customerAddress.getZipCode(), is(getCustomerAddress().getZipCode()));

    }

    @Test
    public void fromCustomerAddressToCustomerAddressResponse() {

        CustomerAddressResponse customerAddressResponse = sut.fromCustomerAddressToCustomerAddressResponse(
                getCustomerAddress());

        assertThat(customerAddressResponse.getCity(), is(getCustomerAddress().getCity()));
        assertThat(customerAddressResponse.getHouseNr(), is(getCustomerAddress().getHouseNr()));
        assertThat(customerAddressResponse.getStreet(), is(getCustomerAddress().getStreet()));
        assertThat(customerAddressResponse.getZipCode(), is(getCustomerAddress().getZipCode()));

        assertThat(customerAddressResponse.getLinks().size(), is(2));
        assertThat(customerAddressResponse.getLink(Link.REL_SELF).getRel(), is(Link.REL_SELF));
        assertThat(customerAddressResponse.getLink(Link.REL_SELF).getHref(), is(BASE_PATH + "/" + CUSTOMER_ID + "/address"));
        assertThat(customerAddressResponse.getLink(LinkRelations.CUSTOMER.getName()).getRel(), is("customer"));
        assertThat(customerAddressResponse.getLink(LinkRelations.CUSTOMER.getName()).getHref(), is(BASE_PATH + "/" + CUSTOMER_ID));

    }

    private CustomerAddress getCustomerAddress() {
        return CustomerAddress.builder()
                .city("City")
                .houseNr("HouseNr")
                .street("Street")
                .zipCode("ZipCode")
                .id(CUSTOMER_ADDRESS_ID)
                .customer(Customer.builder().id(CUSTOMER_ID).build())
                .build();
    }

    private CustomerAddressRequest getCustomerAddressRequest() {
        return CustomerAddressRequest.builder()
                .city("City")
                .houseNr("HouseNr")
                .street("Street")
                .zipCode("ZipCode")
                .build();
    }
}