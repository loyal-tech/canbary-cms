package com.adopt.apigw.modules.Voucher.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.MenuConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.Voucher.domain.Voucher;
import com.adopt.apigw.modules.Voucher.module.APIResponseController;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.modules.Voucher.module.SNMPCounters;
import com.adopt.apigw.modules.Voucher.service.VoucherService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "Voucher Management", description = "REST APIs related to Voucher Entity!!!!", tags = "Voucher")
@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.VOUCHER)
public class BSSVoucherController {
    private static String MODULE = " [BSSVoucherController] ";
    private final Logger log = LoggerFactory.getLogger(BSSVoucherController.class);
    private static final String VOUCHER_LIST = "voucherList";

    @Autowired
    private VoucherService voucherService;
    @Autowired
    private APIResponseController responseController;
    @Autowired
    private Tracer tracer;

    private final SNMPCounters snmpCounters = new SNMPCounters();

    @ApiOperation(value = "validate already generated voucher based on the given planId and code value")
    @PostMapping("/validate")
    //@PreAuthorize("@roleAccesses.hasPermission('voucher','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> validate(@RequestParam(name = "code", required = true) String code, HttpServletRequest request, @RequestParam("mvnoId") Integer mvnoId) {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        try {
            // TODO: pass mvnoID manually 6/5/2025
            //Integer mvnoId = responseController.getMvnoIdFromCurrentStaff(null);
            snmpCounters.incrementValidateVoucherSuccess();
            Integer responseCode = APIConstants.SUCCESS;
            responseEntity = responseController.apiResponse(responseCode, response);
            log.debug("Request : { From : " + request.getHeader("requestFrom") + " ,Request URL : " + request.getRequestURL() + ", Request For : " + code + " Response : " + responseEntity.getBody() + " }");
            return voucherService.validateVoucher(code, Long.valueOf(mvnoId));
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            responseEntity = responseController.apiResponse(responseCode, response);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementValidateVoucherFailure();
            log.debug("Request : { From : " + request.getHeader("requestFrom") + " ,Request URL : " + request.getRequestURL() + ", Request For : " + code + " Response : " + responseEntity.getBody() + " }");
            return responseController.apiResponse(responseCode, response);
        }
    }

    @ApiOperation(value = "Get list of vouchers in the system")
    @GetMapping("/all")
    @PreAuthorize("validatePermission(\"" + MenuConstants.SHOW_MANAGE_VOUCHERS + "\")")
    // @PreAuthorize("@roleAccesses.hasPermission('voucher','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getAllVouchers(PaginationDTO paginationDTO, @RequestParam(name = "resellerId", required = false) Long resellerId, HttpServletRequest request,@RequestParam Integer mvnoId) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            // TODO: pass mvnoID manually 6/5/2025
//            Integer mvnoId = responseController.getMvnoIdFromCurrentStaff(null);
            Page<Voucher> voucherList = voucherService.getAllVouchers(Long.valueOf(mvnoId), paginationDTO, resellerId);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(voucherList.getContent())) {
                responseCode = APIConstants.NO_CONTENT_FOUND;
                response.put(APIConstants.ERROR_MESSAGE, "No Records Found!");
                snmpCounters.incrementGetAllVouchersListFailure();
            } else {
                responseCode = APIConstants.SUCCESS;
                response.put("voucher", voucherList);
                snmpCounters.incrementGetAllVouchersListSuccess();
            }
            // response.put(VOUCHER_LIST, voucherList);
//		    log.debug("Request to fetch all vouchers");
            return responseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            log.error("Error while fetch all vouchers: " + e.getMessage());
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetAllVouchersListFailure();
            return responseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @ApiOperation(value = "Get list of vouchers based on the given batch name")
    @GetMapping("/findVouchersByBatchId")

    //  @PreAuthorize("@roleAccesses.hasPermission('voucher','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findVouchersByBatchName(PaginationDTO paginationDTO, @RequestParam(name = "batchId", required = true) Long batchId, HttpServletRequest request,@RequestParam Integer mvnoId) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Voucher voucher = new Voucher();
        try {
            // TODO: pass mvnoID manually 6/5/2025
//            Integer mvnoId = responseController.getMvnoIdFromCurrentStaff(null);
//            List<Voucher> voucherList = voucherService.findVouchersByBatchId(batchId, mvnoId);
//            response.put(VOUCHER_LIST, voucherList);
//            snmpCounters.incrementFindVouchersByBatchIdSuccess();
//            return responseController.apiResponse(APIConstants.SUCCESS, response);

            Page<Voucher> voucherList = voucherService.findVouchersByBatchId(batchId, Long.valueOf(mvnoId), paginationDTO);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(voucherList.getContent())) {
                responseCode = APIConstants.NULL_VALUE;
                response.put(APIConstants.ERROR_MESSAGE, "No Records Found!");
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch voucher" + LogConstants.LOG_BY_NAME + voucher.getBatchName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);
                snmpCounters.incrementGetAllVouchersListFailure();
            } else {
                responseCode = APIConstants.SUCCESS;
                response.put("voucher", voucherList);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch voucher" + LogConstants.LOG_BY_NAME + voucher.getBatchName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
                snmpCounters.incrementFindVouchersByBatchIdSuccess();
            }
            return responseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch voucher" + LogConstants.LOG_BY_NAME + voucher.getBatchName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementFindVouchersByBatchIdFailure();
            return responseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }


    @ApiOperation(value = "Get list of vouchers based on the given batch name and status value")
    @GetMapping("/findVouchers")
    @PreAuthorize("validatePermission(\"" + MenuConstants.SHOW_MANAGE_VOUCHERS + "\")")
    // @PreAuthorize("@roleAccesses.hasPermission('voucher','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findVouchers(PaginationDTO paginationDTO, @RequestParam(name = "batchName", required = false) String batchName, @RequestParam(name = "status", required = true) String status, HttpServletRequest request,@RequestParam Integer mvnoId) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            // TODO: pass mvnoID manually 6/5/2025
//            Integer mvnoId = responseController.getMvnoIdFromCurrentStaff(null);
            Page<Voucher> voucherList = voucherService.findVouchers(batchName, status, Long.valueOf(mvnoId), paginationDTO);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(voucherList.getContent())) {
                responseCode = APIConstants.NO_CONTENT_FOUND;
                response.put(APIConstants.MESSAGE, "No Records Found!");
                snmpCounters.incrementFindVouchersFailure();
            } else {
                responseCode = APIConstants.SUCCESS;
                response.put("voucher", voucherList);
                snmpCounters.incrementFindVouchersSuccess();
            }


//		    log.debug("Request to fetch all voucher by batch name: " + batchName + " and status: " + status);
            return responseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            log.error("Error while fetch voucher by batch name: " + batchName + " and status: " + status + " " + e.getMessage());
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementFindVouchersFailure();
            return responseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @ApiOperation(value = "Add voucher id in list")
    @GetMapping("/addVoucherId")
    // @PreAuthorize("@roleAccesses.hasPermission('voucher','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> addVoucherId(@RequestParam(name = "id", required = true) Long id, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            voucherService.addVoucherId(id);
            snmpCounters.incrementCreateVoucherIdSuccess();
            return responseController.apiResponse(APIConstants.SUCCESS, response);
        } catch (Exception e) {
            log.error("Error while add new voucher: " + e.getMessage());
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementCreateVoucherIdFailure();
            return responseController.apiResponse(responseCode, response);
        }
    }

    @ApiOperation(value = "Change vouchers status to Active based on the given Voucher Id value")
    @GetMapping("/changeStatusToActive")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VOUCHER_ACTIVE + "\")")
    //  @PreAuthorize("@roleAccesses.hasPermission('voucher','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> changeStatusToActive(@RequestParam(name = "voucherIdList", required = true) List<Long> voucherIdList,@RequestParam Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            // TODO: pass mvnoID manually 6/5/2025
//            Integer mvnoId = responseController.getMvnoIdFromCurrentStaff(null);
            String message = voucherService.changeStatusToActive(voucherIdList, Long.valueOf(mvnoId));
//		    if(count>0) {
//		    	response.put(APIConstants.MESSAGE, "Voucher Status has been change to Active."+count+"voucher status is not changed because Only Generated voucher can be Active");
//		    }else {
//		    response.put(APIConstants.MESSAGE, "Voucher Status has been change to Active");
//		    }
            response.put(APIConstants.MESSAGE, message);
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change status for active" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
            snmpCounters.incrementChangeVoucherStatusToActiveSuccess();
            return responseController.apiResponse(APIConstants.SUCCESS, response);
        } catch (CustomValidationException ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            responseCode = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change status for active" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            response.put(APIConstants.ERROR_MESSAGE, ex.getMessage());
            snmpCounters.incrementChangeVoucherStatusToActiveFailure();
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change status for active" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementChangeVoucherStatusToActiveFailure();
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return responseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Change vouchers status to Block based on the given Voucher Id value")
    @GetMapping("/changeStatusToBlock")

    @PreAuthorize("validatePermission(\"" + MenuConstants.VOUCHER_BLOCK + "\")")
    // @PreAuthorize("@roleAccesses.hasPermission('voucher','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> changeStatusToBlock(@RequestParam(name = "voucherIdList", required = true) List<Long> voucherIdList ,@RequestParam Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            // TODO: pass mvnoID manually 6/5/2025
//            Integer mvnoId = responseController.getMvnoIdFromCurrentStaff(null);
            String message = voucherService.changeStatusToBlock(voucherIdList, Long.valueOf(mvnoId));
//		    if(count>0) {
//		    	response.put(APIConstants.MESSAGE, "Voucher Status has been change to Blocked."+count+"voucher status is not changed because Only Generated and Active voucher can be Block.");
//		    }
//		    else {
//		    response.put(APIConstants.MESSAGE, "Voucher Status has been change to Blocked");
//		    }
            response.put(APIConstants.MESSAGE, message);
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change status for blocked" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
            snmpCounters.incrementChangeVoucherStatusToBlockSuccess();
            return responseController.apiResponse(APIConstants.SUCCESS, response);
        } catch (CustomValidationException ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            responseCode = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change status for blocked" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            response.put(APIConstants.ERROR_MESSAGE, ex.getMessage());
            snmpCounters.incrementChangeVoucherStatusToUnblockFailure();
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change status for blocked" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementChangeVoucherStatusToBlockFailure();
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return responseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Change vouchers status to Unblock based on the given Voucher Id value")
    @GetMapping("/changeStatusToUnblock")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VOUCHER_UNBLOCK + "\")")

    //   @PreAuthorize("@roleAccesses.hasPermission('voucher','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> changeStatusToUnblock(@RequestParam(name = "voucherIdList", required = true) List<Long> voucherIdList,@RequestParam Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            // TODO: pass mvnoID manually 6/5/2025
//            Integer mvnoId = responseController.getMvnoIdFromCurrentStaff(null);
            String message = voucherService.changeStatusToUnblock(voucherIdList, Long.valueOf(mvnoId));
//		    if(count>0) {
//		    	response.put(APIConstants.MESSAGE, "Voucher Status has been change to Active.\"+count+\"voucher status is not changed because Only Blocked voucher can be Unblock.");
//		    }
//		    else {
//		    response.put(APIConstants.MESSAGE, "Voucher Status has been change to Active");
//		    }
            response.put(APIConstants.MESSAGE, message);
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change status for unblocked" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
            snmpCounters.incrementChangeVoucherStatusToUnblockSuccess();
            return responseController.apiResponse(APIConstants.SUCCESS, response);
        } catch (CustomValidationException ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            responseCode = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change status for unblocked" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            response.put(APIConstants.ERROR_MESSAGE, ex.getMessage());
            snmpCounters.incrementChangeVoucherStatusToUnblockFailure();
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change status for unblocked" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementChangeVoucherStatusToUnblockFailure();
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return responseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Change vouchers status to Scrap based on the given Voucher Id value")
    @GetMapping("/changeStatusToScrap")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VOUCHER_SCRAP + "\")")

    //  @PreAuthorize("@roleAccesses.hasPermission('voucher','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> changeStatusToScrap(@RequestParam(name = "voucherIdList", required = true) List<Long> voucherIdList,@RequestParam Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer responseCode = APIConstants.FAIL;
        try {
            // TODO: pass mvnoID manually 6/5/2025
//            Integer mvnoId = responseController.getMvnoIdFromCurrentStaff(null);
            String message = voucherService.changeStatusToScrap(voucherIdList, Long.valueOf(mvnoId));
//		    if(count>0) {
//		    	response.put(APIConstants.MESSAGE, "Voucher Status has been change to Scrapped.\"+count+\"voucher status is not changed because Only Used voucher can not be Scrap..");
//		    }
//		    else {
//		    response.put(APIConstants.MESSAGE, "Voucher Status has been change to Scrapped");
//		    }
            response.put(APIConstants.MESSAGE, message);
            responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change status for Scrap" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
            snmpCounters.incrementChangeVoucherStatusToScrapSuccess();
            return responseController.apiResponse(APIConstants.SUCCESS, response);
        } catch (CustomValidationException ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            responseCode = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change status for Scrap" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            response.put(APIConstants.ERROR_MESSAGE, ex.getMessage());
            snmpCounters.incrementChangeVoucherStatusToUnblockFailure();
        } catch (Exception e) {
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change status for Scrap" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementChangeVoucherStatusToScrapFailure();
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return responseController.apiResponse(responseCode, response);
    }

    @ApiOperation(value = "Send sms")
    @PostMapping("/sendSms")
    @PreAuthorize("validatePermission(\"" + MenuConstants.SEND_SMS_MANAGE_VOUCHERS + "\")")
    //@PreAuthorize("@roleAccesses.hasPermission('voucher','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> sendSms(@RequestParam(name = "code", required = true) String code, @RequestParam(name = "id", required = true) Long id, @RequestParam(name = "countryCode", required = true) String countryCode,@RequestParam Integer mvnoId, @RequestParam(name = "mobileNo", required = true) String mobileNo, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity responseEntity = null;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            // TODO: pass mvnoID manually 6/5/2025
//            Integer mvnoId = responseController.getMvnoIdFromCurrentStaff(null);
            voucherService.sendSms(id, countryCode, mobileNo, code, Long.valueOf(mvnoId));
            Integer responseCode = APIConstants.SUCCESS;
            response.put(APIConstants.MESSAGE, "Voucher Code has been Sent successfully.");
            snmpCounters.incrementSendSMSForVoucherSuccess();
            System.out.println("Response" + request.getAuthType());
            //log.info("Request:"+request.getAuthType());
            //log.info("Response"+response);
            responseEntity = responseController.apiResponse(responseCode, response);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch voucher sms for: " + mobileNo + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return responseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementSendSMSForVoucherFailure();
            responseEntity = responseController.apiResponse(responseCode, response);
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch voucher sms for: " + mobileNo + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return responseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Get list of vouchers based on the given batch name and status value")
    @GetMapping("/exportToCSV")

    @PreAuthorize("validatePermission(\"" + MenuConstants.DOWNLOAD_VOUCHER + "\")")
    //  @PreAuthorize("@roleAccesses.hasPermission('voucher','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findVouchers(@RequestParam(name = "batchName", required = false) String batchName, @RequestParam(name = "status", required = true) String status,@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            // TODO: pass mvnoID manually 6/5/2025
            List<Map<String, String>> dataToExport = voucherService.dataToExport(batchName, status,(long) mvnoId);
            response.put("dataToExport", dataToExport);
            snmpCounters.incrementFindVouchersSuccess();
            return responseController.apiResponse(APIConstants.SUCCESS, response);
        }
        catch (CustomValidationException ce) {
            log.error("Error while fetch voucher by batch name: " + batchName + " and status: " + status + " " + ce.getMessage());
            response.put(APIConstants.ERROR_MESSAGE, ce.getMessage());
            response.put("status", ce.getErrCode());
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            snmpCounters.incrementFindVouchersFailure();
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            log.error("Error while fetch voucher by batch name: " + batchName + " and status: " + status + " " + e.getMessage());
            Integer responseCode = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementFindVouchersFailure();
            return responseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @ApiOperation(value = "validate already generated voucher based on the given planId and code value only verify")
    @PostMapping("/verify")
    //@PreAuthorize("@roleAccesses.hasPermission('voucher','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> verify(@RequestParam(name = "code", required = true) String code,@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        try {
            // TODO: pass mvnoID manually 6/5/2025
            snmpCounters.incrementValidateVoucherSuccess();
            Integer responseCode = APIConstants.SUCCESS;
            responseEntity = responseController.apiResponse(responseCode, response);
            log.debug("Request : { From : " + request.getHeader("requestFrom") + " ,Request URL : " + request.getRequestURL() + ", Request For : " + code + " Response : " + responseEntity.getBody() + " }");
            return voucherService.verifyVoucher(code, Long.valueOf(mvnoId));
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            responseEntity = responseController.apiResponse(responseCode, response);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementValidateVoucherFailure();
            log.debug("Request : { From : " + request.getHeader("requestFrom") + " ,Request URL : " + request.getRequestURL() + ", Request For : " + code + " Response : " + responseEntity.getBody() + " }");
            return responseController.apiResponse(responseCode, response);
        }
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }


    @ApiOperation(value = "validate already generated voucher based on the given planId and code value only verify")
    @GetMapping("/changeStatus")
    //@PreAuthorize("@roleAccesses.hasPermission('voucher','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> changeStatus(@RequestParam(name = "voucherId", required = true) Long voucherId,@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        try {
            // TODO: pass mvnoID manually 6/5/2025
            snmpCounters.incrementValidateVoucherSuccess();
            Integer responseCode = APIConstants.SUCCESS;
            responseEntity = responseController.apiResponse(responseCode, response);
            log.debug("Request : { From : " + request.getHeader("requestFrom") + " ,Request URL : " + request.getRequestURL() + ", Request For : " + voucherId + " Response : " + responseEntity.getBody() + " }");
            return voucherService.changeStatus(voucherId, Long.valueOf(mvnoId));
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            responseEntity = responseController.apiResponse(responseCode, response);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementValidateVoucherFailure();
            log.debug("Request : { From : " + request.getHeader("requestFrom") + " ,Request URL : " + request.getRequestURL() + ", Request For : " + voucherId + " Response : " + responseEntity.getBody() + " }");
            return responseController.apiResponse(responseCode, response);
        }
    }
}
