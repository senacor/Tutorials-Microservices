package com.senacor.bitc.demo.feign.client.demo;

import com.senacor.bitc.demo.feign.client.demo.domain.Customer;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient("demo")
public interface CustomerClient {

    @RequestMapping(value = "/customer/{customerId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    Customer getCustomerById(@PathVariable(name = "customerId") Integer customerId);

}
