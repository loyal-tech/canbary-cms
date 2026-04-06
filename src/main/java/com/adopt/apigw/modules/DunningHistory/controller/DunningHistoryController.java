package com.adopt.apigw.modules.DunningHistory.controller;


import com.adopt.apigw.constants.MenuConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.modules.DunningHistory.domain.DunningHistory;
import com.adopt.apigw.modules.DunningHistory.service.DunningHistoryService;
import com.adopt.apigw.modules.Voucher.module.APIResponseController;
import com.adopt.apigw.modules.VoucherBatch.module.VoucherBatchInfoDto;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.utils.APIConstants;
import com.netflix.discovery.converters.Auto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/cms/dunnninghistory")
public class DunningHistoryController {

   @Autowired
   private DunningHistoryService dunningHistoryService;

   @Autowired
   private APIResponseController apiResponseController;

    private static final Logger logger = LoggerFactory.getLogger(DunningHistoryController.class);


    @ApiOperation(value = "Get list of  all dunning history")
    @PostMapping("/findAll")
    //@PreAuthorize("@roleAccesses.hasPermission('voucherbatch','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<?> findAllDunningHistory(@RequestBody PaginationRequestDTO requestDTO) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            Page<DunningHistory> getAllCustomerDunningHistory = dunningHistoryService.findAllDunningHistory(requestDTO);
            Integer responseCode = APIConstants.SUCCESS;
            response.put("customerDunningHistory", getAllCustomerDunningHistory);
            logger.debug("All dunning History fetch successfully");
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            logger.error("Error while fetch DunningHistory: " + e.getMessage());
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @ApiOperation(value = "Get list of  all dunning history")
    @PostMapping("/findByPartnerOrCustomerId")
    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_DUNNUNG_MANAGEMENT + "\",\""
            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_DUNNUNG_MANAGEMENT + "\")")
    public ResponseEntity<?> findAllByPartnerOrCustomer(@RequestBody PaginationRequestDTO requestDTO) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            Page<DunningHistory> getAllCustomerDunningHistory = dunningHistoryService.findAllByPartnerOrCustomerDunningHistory(requestDTO);
            Integer responseCode = APIConstants.SUCCESS;
            response.put("customerDunningHistory", getAllCustomerDunningHistory);
            logger.debug("All dunning History fetch successfully");
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            logger.error("Error while fetch DunningHistory: " + e.getMessage());
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }
}
