package com.senacor.bitc.demo.rest;

import com.senacor.bitc.demo.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.InetAddress;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(IpAddressController.class)
public class IpAddressControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void address_RunLocal_ShouldReturnIpAddress() throws Exception {
        String ipAddress = InetAddress.getLocalHost().getHostAddress();

        mockMvc.perform(get("/address"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.address", is(ipAddress)));
    }

}