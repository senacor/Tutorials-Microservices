package com.senacor.bitc.demo.feign.client.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends Exception {

    @Override
    public String getMessage() {
        return "Customer not found!";
    }
}
