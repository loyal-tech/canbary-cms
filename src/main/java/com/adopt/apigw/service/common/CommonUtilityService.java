package com.adopt.apigw.service.common;


import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import com.adopt.apigw.modules.InventoryManagement.item.ItemRepository;
import com.adopt.apigw.modules.InventoryManagement.item.QItem;
import com.adopt.apigw.pojo.CommonUtility.CustomerCredentials;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.utils.APIConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonUtilityService {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;


    public CustomerCredentials getCustomerCredentials(String deviceSerialNumber) throws Exception {
        CustomerCredentials customerCredentials = new CustomerCredentials();
        try {
            List<Item> itemList = itemRepository.findAllItemBySerialNumberAndIsDeletedFalse(deviceSerialNumber);
            if (itemList.size() >= 1) {

                /*
                If in case of, serialNumber is bind with the multiple items,
                the first value in the list will be selected as default unit value
                */

                Item item = itemList.get(0);
                CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findByItemIdAndIsDeletedFalse(item.getId());
                if (customerInventoryMapping != null) {
                    Customers customer = customerInventoryMapping.getCustomer();
                    customerCredentials.setDeviceSerialNumber(item.getSerialNumber());
                    customerCredentials.setCustomerPassword(customer.getPassword());
                    customerCredentials.setCustomerUserName(customer.getUsername());
                    ApplicationLogger.logger.info("credentials with device serial number : " + deviceSerialNumber + " fetch successfully !!");

                    return customerCredentials;
                } else {
                    ApplicationLogger.logger.error("Customer with the device serial number : " + deviceSerialNumber + " not found");
                    throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Customer with the device serial number : " + deviceSerialNumber + " not found", null);
                }
            } else {
                ApplicationLogger.logger.error("Item with the device serial number : " + deviceSerialNumber + " not found");
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Item with the device serial number : " + deviceSerialNumber + " not found", null);
            }

        } catch (Exception e) {
            ApplicationLogger.logger.error("Unable to customers fetch the credentials : " + e.getMessage());
        }
        return null;
    }
}
