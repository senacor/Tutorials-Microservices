package com.senacor.bitc.demo.rest;

import com.senacor.bitc.demo.domain.IpAddress;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
public class IpAddressController {

    @RequestMapping(value = "/address", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public IpAddress address() throws UnknownHostException {
        IpAddress ipAddress = new IpAddress(retrieveServerIpAddress());
        return ipAddress;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({UnknownHostException.class})
    public void handleException() {

    }

    private String retrieveServerIpAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

}
