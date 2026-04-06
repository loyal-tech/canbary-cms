package com.adopt.apigw.modules.PartnerLedger.CustomerLedger.controller;

import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.postpaid.CustomerLedgerAllInfoPojo;
import com.adopt.apigw.model.postpaid.CustomerLedgerDtlsPojo;
import com.adopt.apigw.model.postpaid.CustomerLedgerInfoPojo;
import com.adopt.apigw.service.postpaid.CustomerLedgerDtlsService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("api/v1")
public class CustomerLedgerController extends ApiBaseController {

    private static final Logger logger = LoggerFactory.getLogger(APIController.class);

    private static String MODULE = " [APIController] ";
    private static final String CUSTOMER_PAYMENT = "CustomerPayment";
    private static final String OTP = "otp";

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    @PostMapping("/wallet")
    public ResponseEntity<?> getWalletAmount(@Valid @RequestBody CustomerLedgerDtlsPojo pojo) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomerLedgerDtlsService customerLedgerDtlsService = SpringContext
                    .getBean(CustomerLedgerDtlsService.class);
            CustomerLedgerInfoPojo infoPojo = customerLedgerDtlsService.getByTime(pojo);
            CustomerLedgerAllInfoPojo ledgerAllInfoPojo = customerLedgerDtlsService.custInfoBytime(pojo.getCustId(),
                    infoPojo);
            if (infoPojo == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
            } else {
                response.put("customerWalletDetails",-ledgerAllInfoPojo.getCustomerLedgerInfoPojo().getClosingBalance());
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(MODULE + ce.getMessage(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + ex.getMessage(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return apiResponse(RESP_CODE, response);
    }
}
