package com.senacor.bitc.demo.service;

import com.senacor.bitc.demo.domain.CustomerAddress;
import com.senacor.bitc.demo.domain.CustomerAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerAddressServiceImpl implements CustomerAddressService {

    private final CustomerAddressRepository customerAddressRepository;

    @Autowired
    public CustomerAddressServiceImpl(CustomerAddressRepository customerAddressRepository) {
        this.customerAddressRepository = customerAddressRepository;
    }

    @Override
    public CustomerAddress loadCustomerAddressById(Integer id) {
        return customerAddressRepository.findOne(id);
    }

    @Override
    public CustomerAddress loadCustomerAddressByCustomerId(Integer customerId) {
        return customerAddressRepository.findByCustomerId(customerId);
    }

    @Override
    public CustomerAddress saveCustomerAddress(CustomerAddress customerAddress) {
        return customerAddressRepository.save(customerAddress);
    }
}
