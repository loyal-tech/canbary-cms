package com.adopt.apigw.controller.api;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.postpaid.Tax;
import com.adopt.apigw.pojo.BudPay.BudPayPojo;
import com.adopt.apigw.modules.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.adopt.apigw.modules.PaymentConfig.service.PaymentConfigService;
import com.adopt.apigw.modules.Voucher.module.APIResponseController;
import com.adopt.apigw.modules.paymentGatewayMaster.service.PaymentGatewayService;
import com.adopt.apigw.pojo.api.TaxPojo;
import com.adopt.apigw.service.postpaid.DebitDocService;
import com.adopt.apigw.service.postpaid.TaxService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class BudPayPaymentController {

    @Autowired
    private APIResponseController apiResponseController;

    @Autowired
    private Tracer tracer;

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @Autowired
    private PaymentConfigService paymentConfigService;

    @Autowired
    private DebitDocService debitDocService;

    private static final Logger logger = LoggerFactory.getLogger(BudPayPaymentController.class);


    @PostMapping(value = "/BudPayResponse")
    @Transactional
    public GenericDataDTO getBudPayResponse (@RequestBody BudPayPojo budPayPojo , HttpServletRequest request){
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.BUDPAY,budPayPojo.getMvnoid());
            /**All BudPay configuration fetch started**/
            String BUDPAY_SECRET_KEY = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.BUDPAY.BUDPAY_SECRET_KEY);
            String BUDPAY_CALLBACK_URL = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.BUDPAY.BUDPAY_CALLBACK_URL);
            String BUDPAY_REQUEST_URL = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.BUDPAY.BUDPAY_REQUEST_URL);
            budPayPojo.setCallback(BUDPAY_CALLBACK_URL);
            /**All BudPay configuration fetch ended**/
            genericDataDTO = paymentGatewayService.getResponseFromBudPay(budPayPojo,BUDPAY_SECRET_KEY,BUDPAY_REQUEST_URL);

            return genericDataDTO;
        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseMessage(ce.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/BudPayResponseFromCWSC")
    @Transactional
    public GenericDataDTO getBudPayResponseFromCWSC (@RequestBody BudPayPojo budPayPojo , HttpServletRequest request){
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
            HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.BUDPAY,budPayPojo.getMvnoid());
            /**All BudPay configuration fetch started**/
            String BUDPAY_SECRET_KEY = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.BUDPAY.BUDPAY_SECRET_KEY);
            String BUDPAY_CALLBACK_URL = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.BUDPAY.BUDPAY_CALLBACK_CWSC_BUY_URL);
            String BUDPAY_REQUEST_URL = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.BUDPAY.BUDPAY_REQUEST_URL);
            budPayPojo.setCallback(BUDPAY_CALLBACK_URL);
            /**All BudPay configuration fetch ended**/
            genericDataDTO = paymentGatewayService.getResponseFromBudPay(budPayPojo,BUDPAY_SECRET_KEY,BUDPAY_REQUEST_URL);

            return genericDataDTO;
        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseMessage(ce.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        }
        return genericDataDTO;
    }

    @GetMapping("/invoice")
    public GenericDataDTO generateIspInvoice(HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        logger.info("*********cronjobtimeforispautobill**************");
        debitDocService.ispInvoiceGenerate(null);

        return genericDataDTO;
    }


    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getStackTrace().toString(), e);
        }
        return loggedInUser;
    }

    @GetMapping("/verifyBudPayPayment")
    public GenericDataDTO verifyBudPayOnlinePayment(@RequestParam("reference") String reference){
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            dataDTO = paymentGatewayService.verifyBudPayPayment(reference);
        } catch (Exception e) {
            logger.error("error while verifying budpay payment; Error", e);
            e.printStackTrace();
        }
        return dataDTO;
    }
}
