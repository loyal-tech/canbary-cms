package com.adopt.apigw.controller.common.customer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.radius.RadiusProfile;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.radius.RadiusProfileService;

@RestController
public class CustomerRestController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerRestController.class);


    CustomersService customerService;

    RadiusProfileService radiusProfileService;

    public CustomerRestController(CustomersService customerService, RadiusProfileService radiusProfileService) {
        this.customerService = customerService;
        this.radiusProfileService = radiusProfileService;
    }

    @RequestMapping(value = "/provisionCustomer", method = RequestMethod.POST)
    public ResponseEntity<Object> provisionCustomer(@RequestBody Customers customer) {
        ApplicationLogger.logger.info("provisionCustomer call " + customer.getUsername());
        try {
            if (customer.getUsername() != null && customer.getPassword() != null && customer.getStatus() != null) {
                if (customer.getQOS() != null) {
                    List<RadiusProfile> customerRadiusProfile = new ArrayList<>();
                    String[] convertedRankArray = customer.getQOS().split(",");
                    for (String strRadName : convertedRankArray) {
                        List<RadiusProfile> listAllRadiusProfile = radiusProfileService.getAllActiveEntities();
                        Iterator<RadiusProfile> itrRadiusProfile = listAllRadiusProfile.iterator();
                        while (itrRadiusProfile.hasNext()) {
                            RadiusProfile radProfile = itrRadiusProfile.next();
                            if (radProfile.getName().equalsIgnoreCase(strRadName)) {
                                customerRadiusProfile.add(radProfile);
                            }
                        }
                        //      customer.setRadiusProfiles(customerRadiusProfile);
                    }
                }

                Customers savedCustomer = customerService.save(customer);
            } else {
                return new ResponseEntity<>("Mandatory Parameter is Missing", HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Customer creation is failed", HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>("Customer is created successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/deprovisionCustomer", method = RequestMethod.POST)
    public ResponseEntity<Object> deprovisionCustomer(@RequestBody Customers customer) {
        ApplicationLogger.logger.info("deprovisionCustomer call " + customer.getUsername());
        try {
            String username = customer.getUsername().trim();
            List<Customers> listCustomer = customerService.getCustomerFromUsernameAPI(username);
            ApplicationLogger.logger.info("Customer List is " + listCustomer);
            if (customer.getUsername() != null & listCustomer != null && listCustomer.size() >= 1) {
                Customers deletedCustomer = listCustomer.get(0);
                customerService.deleteCustomer(listCustomer.get(0).getId());

     

            } else {
                return new ResponseEntity<>("Mandatory Parameter is Missing or Username Not Found", HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Customer deprovsion is failed", HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>("Customer is deprovsioned successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/getCustomer", method = RequestMethod.POST)
    public ResponseEntity<Object> getCustomer(@RequestBody Customers customer) {
        ApplicationLogger.logger.info("getCustomer call " + customer.getUsername());
        try {
            String username = customer.getUsername().trim();
            List<Customers> listCustomer = customerService.getCustomerFromUsernameAPI(username);
            if (customer.getUsername() != null & listCustomer != null && listCustomer.size() >= 1) {
                return new ResponseEntity<>(listCustomer.get(0), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Mandatory Parameter is Missing or Username Not Found", HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Customer get is failed", HttpStatus.EXPECTATION_FAILED);
        }
    }

}
	