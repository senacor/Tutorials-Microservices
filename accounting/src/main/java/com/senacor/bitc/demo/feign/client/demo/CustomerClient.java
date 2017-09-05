package com.senacor.bitc.demo.feign.client.demo;

import com.senacor.bitc.demo.feign.client.demo.domain.Customer;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("customer")
@RequestMapping(value = "/customer", produces = "application/json")
public interface CustomerClient {

    @RequestMapping(value = "/{customerId}", method = RequestMethod.GET)
    @ResponseBody
    Customer getCustomerById(@PathVariable(name = "customerId") Integer customerId);

}
