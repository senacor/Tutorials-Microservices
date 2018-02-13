package com.senacor.bitc.demo.service;

import com.senacor.bitc.demo.domain.CustomerAddress;

public interface CustomerAddressService {

    CustomerAddress loadCustomerAddressById(Integer id);

    CustomerAddress loadCustomerAddressByCustomerId(Integer customerId);

    CustomerAddress saveCustomerAddress(CustomerAddress customerAddress);

}
