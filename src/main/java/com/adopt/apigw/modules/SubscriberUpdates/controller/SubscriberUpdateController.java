package com.adopt.apigw.modules.SubscriberUpdates.controller;

import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.repository.radius.CustomersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.SubscriberUpdates.model.SubscriberUpdateDTO;
import com.adopt.apigw.modules.SubscriberUpdates.model.SubscriberUpdateSearchDTO;
import com.adopt.apigw.modules.SubscriberUpdates.service.SubscriberUpdateService;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.utils.APIConstants;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SUBSCRIBER_UPDATE)
public class SubscriberUpdateController extends ExBaseAbstractController<SubscriberUpdateDTO> {
    public SubscriberUpdateController(SubscriberUpdateService service) {
        super(service);
    }
    private static String MODULE = " [SubscriberUpdateController] ";
    @Autowired
    SubscriberUpdateService service;
    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomersService customersService;
    private static final Logger logger = LoggerFactory.getLogger(SubscriberUpdateController.class);
    @GetMapping("/byCustomerId/{custId}")
    public GenericDataDTO getEntityByCustId(@PathVariable Integer custId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        MDC.put("type", "Fetch");
        genericDataDTO.setResponseMessage("Success");
        if (custId == null) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setDataList(null);
            logger.error("Unable to Subscriber Update by "+custId+":  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        } else {
            try {
                logger.info("Fetching All Subscriber DEtails by customer Id"+service.getAllByCustomer(custId)+" :  request: { From : {}}; Response : {{}{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return GenericDataDTO.getGenericDataDTO(service.getAllByCustomer(custId));

            } catch (Exception e) {
                ApplicationLogger.logger.error(e.getMessage(), e);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                if (e instanceof DataNotFoundException) {
                    genericDataDTO.setResponseMessage("Data Not Found");
                } else {
                    genericDataDTO.setResponseMessage(e.getMessage());
                }
                logger.error("Unable to fetch all subscriber by customer id "+custId+":  request: { From : {}, Request Url : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setDataList(null);
            }
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @PostMapping("/byCustomerAndTime")
    public GenericDataDTO getSubscriberByTime(@Valid @RequestBody SubscriberUpdateSearchDTO pojo) throws Exception {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (pojo.getCustomer_id() == null) {
                genericDataDTO.setResponseMessage("Please Enter Customer !");
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to fetch Subscriber By time "+pojo.getSTART_DATE()+"  request: { From : {}, Request Url : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            } else {
                Customers customers =  customersRepository.findById(pojo.getCustomer_id()).get();
                if (customers != null) {
                    List<SubscriberUpdateDTO> subscriberUpdateDTOList = service.getCustomerByTime(pojo);
                    genericDataDTO.setDataList(subscriberUpdateDTOList);
                    genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                    logger.info("Fetching All Subscribers With time "+pojo.getSTART_DATE()+" :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                    genericDataDTO.setResponseMessage("Customer Not Found !");
                    genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                    logger.error("Unable to fetch all Subscribers With time "+pojo.getSTART_DATE()+" :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                }
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseMessage("Data Not Found");
            } else {
                genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            }
            logger.error("Unable to fetch all Subscribers With time "+pojo.getSTART_DATE()+" :  request: { From : {}, Request Url : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "[SubscriberUpdate Controller]";
    }
}
