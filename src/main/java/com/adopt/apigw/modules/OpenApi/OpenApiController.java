package com.adopt.apigw.modules.OpenApi;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.CustomerPayment;
import com.adopt.apigw.model.common.Shorter;
import com.adopt.apigw.modules.Customers.CustomersController;
import com.adopt.apigw.modules.MtnPayment.model.MtnBuyPlanDTO;
import com.adopt.apigw.modules.MtnPayment.model.MtnPlanFetchDTO;
import com.adopt.apigw.modules.MtnPayment.model.MtnUssdDataResponse;
import com.adopt.apigw.modules.MtnPayment.model.MtnUssdResponseDTO;
import com.adopt.apigw.modules.MtnPayment.service.MtnPaymentService;
import com.adopt.apigw.modules.paymentGatewayMaster.service.PaymentGatewayService;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.api.GenerateOtpDto;
import com.adopt.apigw.pojo.api.ValidateOtpDto;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.OTPService;
import com.adopt.apigw.service.common.ShorterService;
import com.adopt.apigw.utils.APIConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(value = "OPEN API", description = "REST APIs related that will expose to intigration !!!!", tags = "OPEN")
@RestController
@RequestMapping(UrlConstants.OPEN_API)
public class OpenApiController {

    private static final Logger log = LoggerFactory.getLogger(OpenApiController.class);

    @Autowired
    private MtnPaymentService mtnPaymentService;

    @Autowired
    private Tracer tracer;

    @Autowired
    private ShorterService shorterService;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private PaymentGatewayService paymentGatewayService;
    @PostMapping(value ="/mtn/ussd/planFetch")
    public MtnUssdResponseDTO IntiateMtnUssdPlanFetchRequest(@RequestBody MtnPlanFetchDTO mtnPlanFetchDTO, HttpServletRequest req) {
        MtnUssdResponseDTO mtnUssdResponseDTO = new MtnUssdResponseDTO();
        HashMap<String, Object> response = new HashMap<>();
        try {
            log.info("Initiate request from ussd plan fetch with payload: "+mtnPlanFetchDTO);
            mtnUssdResponseDTO = mtnPaymentService.getPlanByServiceForMtnUssd(mtnPlanFetchDTO);

        }catch (CustomValidationException e) {
            log.error("Error Processing ussd request due to  : "+e.getMessage());
            mtnUssdResponseDTO.setStatusCode(e.getErrCode().toString());
            if(mtnPlanFetchDTO.getTransactionId() != null) {
                mtnUssdResponseDTO.setTransactionId(mtnPlanFetchDTO.getTransactionId());
            }
            mtnUssdResponseDTO.setStatusMessage("Failure");
            MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
            mtnUssdDataResponse.setInboundResponse("Failed To initiate request");
            mtnUssdDataResponse.setUserInputRequired(false);
            mtnUssdResponseDTO.setData(mtnUssdDataResponse);
        }
        catch (Exception e) {
            log.error("Error Processing ussd request due to: "+e.getMessage());
            mtnUssdResponseDTO.setStatusCode("1100");
            mtnUssdResponseDTO.setStatusMessage("Failure");
            MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
            mtnUssdDataResponse.setInboundResponse("Failed To initiate request for plan fetch");
            mtnUssdDataResponse.setUserInputRequired(false);
            mtnUssdResponseDTO.setData(mtnUssdDataResponse);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return mtnUssdResponseDTO;
    }

    @PostMapping(value ="/mtn/ussd/buyPlan")
    public MtnUssdResponseDTO IntiateMtnUssdBuyPlanRequest(@RequestBody MtnBuyPlanDTO mtnBuyPlanDTO, HttpServletRequest req) {
        MtnUssdResponseDTO mtnUssdResponseDTO = new MtnUssdResponseDTO();
        HashMap<String, Object> response = new HashMap<>();
        try {
            log.info("Initiate request from ussd plan buy with payload: "+mtnBuyPlanDTO);
            mtnPaymentService.generateTokenForMtnUSSD(mtnBuyPlanDTO.getUsername() , mtnBuyPlanDTO.getPassword());
            mtnPaymentService.validateOcdRequest(mtnBuyPlanDTO);
            CustomerPayment customerPayment =  mtnPaymentService.intiatePayment(mtnBuyPlanDTO);
            mtnPaymentService.SendOcsRequest(mtnBuyPlanDTO , customerPayment);
            MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
            mtnUssdDataResponse.setInboundResponse("Purchased Successfully");
            mtnUssdDataResponse.setUserInputRequired(false);
            mtnUssdResponseDTO.setData(mtnUssdDataResponse);
            mtnUssdResponseDTO.setStatusCode("0000");
            mtnUssdResponseDTO.setStatusMessage("Success");
            mtnUssdResponseDTO.setTransactionId(mtnBuyPlanDTO.getTransactionId());
        }catch (CustomValidationException e) {
            log.error("Error Processing ussd request due to  : "+e.getMessage());
            mtnUssdResponseDTO.setStatusCode(e.getErrCode().toString());
            if(mtnBuyPlanDTO.getTransactionId() != null) {
                mtnUssdResponseDTO.setTransactionId(mtnBuyPlanDTO.getTransactionId());
            }
            mtnUssdResponseDTO.setStatusMessage("Failure");
            MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
            mtnUssdDataResponse.setInboundResponse("Purchase was Unsuccessful");
            mtnUssdDataResponse.setUserInputRequired(false);
            mtnUssdResponseDTO.setData(mtnUssdDataResponse);
        }
        catch (Exception e) {
            log.error("Error Processing ussd request due to: "+e.getMessage());
            mtnUssdResponseDTO.setStatusCode("2100");
            mtnUssdResponseDTO.setStatusMessage("Failure");
            MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
            mtnUssdDataResponse.setInboundResponse("Purchase was Unsuccessful");
            mtnUssdDataResponse.setUserInputRequired(false);
            mtnUssdResponseDTO.setData(mtnUssdDataResponse);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return mtnUssdResponseDTO;
    }

    @GetMapping("/getPaymentDetailsByHash")
    public ResponseEntity<Map<String, Object>> getPaymentDetailsByHash(
            @RequestParam(name = "hash") String hash, HttpServletRequest req) {

        MDC.put("type", "Fetch");
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;

        try {
            Map<String, Object> serviceResponse = shorterService.getShorterByHash(hash);
            RESP_CODE = (Integer) serviceResponse.get("status");
            response.putAll(serviceResponse);
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "Payment Details found by hash " +
                    LogConstants.REQUEST_BY + hash +
                    LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception e) {
            RESP_CODE = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") +
                    LogConstants.REQUEST_FOR + "Payment Details found by hash " +
                    LogConstants.REQUEST_BY + hash +
                    LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +
                    LogConstants.LOG_ERROR + e.getMessage() +
                    LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return ResponseEntity.ok(response);
    }

}

