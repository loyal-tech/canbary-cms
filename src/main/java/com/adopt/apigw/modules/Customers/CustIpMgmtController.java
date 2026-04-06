package com.adopt.apigw.modules.Customers;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.CustIpMapping;
import com.adopt.apigw.modules.Customers.Services.CustIpMgmtService;
import com.adopt.apigw.pojo.PaginationDetails;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL+UrlConstants.CUST_IP_MGMT)
public class CustIpMgmtController extends ApiBaseController {

    @Autowired
    private Tracer tracer;


    @Autowired
    private CustIpMgmtService custIpMgmtService;

    private static  final Logger LOGGER = LoggerFactory.getLogger(CustomersController.class);

    @ApiOperation(value = "This API will fetch customer with only required data")
    @GetMapping("getIpsByCustId")
    public ResponseEntity<?> getIpLists(@RequestParam(name = "custId") Integer custId, HttpServletRequest req) throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        MDC.put("userName",getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            List<CustIpMapping> custIpMappingList = custIpMgmtService.getCustomerIps(custId);
            Integer responseCode = APIConstants.SUCCESS;
            response.put("customerIps", custIpMappingList);
            response.put("msg", "Customer Ips fetch successfully");
            RESP_CODE=APIConstants.SUCCESS;
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch all Customer IPs"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE+RESP_CODE);

            return apiResponse(responseCode, response);
        } catch (CustomValidationException ce) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all Customer Ips"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, ce.getMessage());
            return apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all Customere Ips"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+LogConstants.LOG_STATUS_CODE+RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponse(responseCode, response);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }


    }

    @ApiOperation("This API is for updating the IP Addresses of the customers")
    @PostMapping("/save")
    public GenericDataDTO saveCustomers(@RequestBody List<CustIpMapping> custIpMappingList, HttpServletRequest request) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<CustIpMapping> UpdatedcustIpMappingList = custIpMgmtService.save(custIpMappingList);
            genericDataDTO.setDataList(UpdatedcustIpMappingList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");

        } catch (CustomValidationException e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }
        return genericDataDTO;
    }


    @ApiOperation("This API is for updating the IP Addresses of the customers")
    @PostMapping("/update")
    public GenericDataDTO updatCustomerIps(@RequestBody List<CustIpMapping> custIpMappingList, HttpServletRequest request) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<CustIpMapping> UpdatedcustIpMappingList = custIpMgmtService.update(custIpMappingList);
            genericDataDTO.setDataList(UpdatedcustIpMappingList);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");

        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }
        return genericDataDTO;
    }


    @ApiOperation("This API is for updating the IP Addresses of the customers")
    @DeleteMapping("/delete")
    public GenericDataDTO deleteCustomers(@RequestParam Integer id, HttpServletRequest request) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            custIpMgmtService.delete(id);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");

        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }
        return genericDataDTO;
    }



    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }





}
