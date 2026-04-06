package com.adopt.apigw.modules.VoucherConfiguration.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.MenuConstants;
import com.adopt.apigw.controller.postpaid.ChargeController;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Voucher.module.APIResponseController;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.modules.Voucher.module.SNMPCounters;
import com.adopt.apigw.modules.VoucherConfiguration.domain.UpdateVoucherConfigDto;
import com.adopt.apigw.modules.VoucherConfiguration.domain.VoucherConfiguration;
import com.adopt.apigw.modules.VoucherConfiguration.module.VoucherConfigDto;
import com.adopt.apigw.modules.VoucherConfiguration.service.VoucherConfigurationService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.UpdateDiffFinder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "Voucher Configuration Management", description = "REST APIs related to Voucher Configuration Entity!!!!", tags = "Voucher Configuration")
@RestController
@RequestMapping("/api/v1/cms/voucherManagement")
public class BSSVoucherConfigurationController {
    final Logger log = LoggerFactory.getLogger(BSSVoucherConfigurationController.class);
    private static final String VOUCHER_CONFIGURATION = "voucherConfiguration";
    private static final String VOUCHER_CONFIGURATION_LIST = "voucherConfigurationList";

    @Autowired
    private VoucherConfigurationService voucherConfigurationService;
    @Autowired
    private APIResponseController apiResponseController;

    @Autowired
    private Tracer tracer;

    @Autowired
    private ChargeController controller;
    private final SNMPCounters snmpCounters = new SNMPCounters();

    @ApiOperation(value = "Get list of voucher configurations in the system")
    @GetMapping("/all")
   // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_VOUCHER_CONFIG_ALL +"\",\"" + AclConstants.OPERATION_VOUCHER_CONFIG_VIEW +"\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VOUCHER +"\")")

    public ResponseEntity<Map<String, Object>> getAll(PaginationDTO paginationDTO, @RequestParam(name = "name", required = false) String name, @RequestParam(name = "locationId", required = false) Long locationId,@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            PageableResponse<VoucherConfiguration> page = voucherConfigurationService.getAll(Long.valueOf(mvnoId), name, locationId, paginationDTO);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(page.getData())) {
                responseCode = APIConstants.NULL_VALUE;
                if (!StringUtils.isEmpty(name)) {
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch voucher configurations"+LogConstants.LOG_BY_NAME+ name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
                    snmpCounters.incrementSearchVoucherProfileByNameFailure();
                } else {
                    response.put("status" ,APIConstants.NO_CONTENT_FOUND);
                    response.put("message","No Records Found");
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch voucher configurations"+LogConstants.LOG_BY_NAME+ name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);
                    snmpCounters.incrementGetVoucherProfileListFailure();
                }
                return apiResponseController.apiResponse(HttpStatus.NO_CONTENT.value(),response);
            } else {
                responseCode = APIConstants.SUCCESS;
                RESP_CODE = APIConstants.SUCCESS;
                response.put(VOUCHER_CONFIGURATION_LIST, page);
                if (!StringUtils.isEmpty(name)) {
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"Search voucher configurations"+LogConstants.LOG_BY_NAME+name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    snmpCounters.incrementSearchVoucherProfileByNameSuccess();
                } else {
                    RESP_CODE = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR+"fetch voucher configurations"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS +  LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    snmpCounters.incrementGetVoucherProfileListSuccess();
                }
            }
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch voucher configurations " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            apiResponseController.buildErrorMessageForResponse(response, e);
            if (!StringUtils.isBlank(name)) snmpCounters.incrementSearchVoucherProfileByNameFailure();
            else snmpCounters.incrementGetVoucherProfileListFailure();
            return apiResponseController.apiResponse(APIConstants.FAIL, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Add new voucher configuration")
    @PostMapping("/addVoucherConfig")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_VOUCHER_CONFIG_ALL +"\",\"" + AclConstants.OPERATION_VOUCHER_CONFIG_ADD +"\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VOUCHER_CREATE +  "\")")
    public ResponseEntity<Map<String, Object>> create(@RequestBody VoucherConfigDto voucherConfigDto,@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        Integer respCode = APIConstants.FAIL;

        try {
//            // TODO: pass mvnoID manually 6/5/2025
//            Integer mvnoId = apiResponseController.getMvnoIdFromCurrentStaff(null);
            response.put(VOUCHER_CONFIGURATION, voucherConfigurationService.save(voucherConfigDto, Long.valueOf(mvnoId)));
            respCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create Voucher Configuration"+LogConstants.LOG_BY_NAME + voucherConfigDto.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
            response.put(APIConstants.MESSAGE, "Voucher Configuration has been added successfully.");
            snmpCounters.incrementCreateVoucherProfileSuccess();
            return apiResponseController.apiResponse(APIConstants.SUCCESS, response);
        } catch (Exception e) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create Voucher Configuration"+LogConstants.LOG_BY_NAME + voucherConfigDto.getName()+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
           // log.error("Error while add new voucher configuration: " + voucherConfigDto.getName() +" " + e.getMessage());
            apiResponseController.buildErrorMessageForResponse(response, e);
            snmpCounters.incrementCreateVoucherProfileFailure();
            return apiResponseController.apiResponse(APIConstants.FAIL, response);
        }  finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Update existing voucher configuration")
    @PutMapping("/update")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_VOUCHER_CONFIG_ALL +"\",\"" + AclConstants.OPERATION_VOUCHER_CONFIG_EDIT +"\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VOUCHER_EDIT +  "\")")

    public ResponseEntity<Map<String, Object>> update(@RequestBody UpdateVoucherConfigDto voucherConfigDto,@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;

        try {
//            // TODO: pass mvnoID manually 6/5/2025
//            Integer mvnoId =apiResponseController.getMvnoIdFromCurrentStaff(null);
            VoucherConfiguration oldname = voucherConfigurationService.getByID(voucherConfigDto.getId());
            VoucherConfiguration oldClone = new VoucherConfiguration(oldname);
            VoucherConfiguration updatedVoucher = voucherConfigurationService.update(voucherConfigDto, Long.valueOf(mvnoId));
            response.put(VOUCHER_CONFIGURATION, voucherConfigurationService.update(voucherConfigDto, Long.valueOf(mvnoId)));
            response.put(APIConstants.MESSAGE, "Voucher Configuration has been updated successfully.");
            snmpCounters.incrementUpdateVoucherProfileSuccess();
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom") +LogConstants.REQUEST_FOR+"Update Voucher Configuration"+ voucherConfigDto.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+" , Update Voucher Configuration Details "+ updatedVoucher+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return apiResponseController.apiResponse(APIConstants.SUCCESS, response);
        } catch(CustomValidationException ce){
            response.put(APIConstants.ERROR_TAG,ce.getMessage());
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            return apiResponseController.apiResponse(HttpStatus.EXPECTATION_FAILED.value(), response);
        }catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Update Voucher Configuration"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            apiResponseController.buildErrorMessageForResponse(response, e);
            snmpCounters.incrementUpdateVoucherProfileFailure();
            return apiResponseController.apiResponse(APIConstants.FAIL, response);
        }  finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Delete existing voucher configuration based on the given config id")
    @DeleteMapping("/delete")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_VOUCHER_CONFIG_ALL +"\",\"" + AclConstants.OPERATION_VOUCHER_CONFIG_DELETE +"\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VOUCHER_DELETE +  "\")")
    public ResponseEntity<Map<String, Object>> delete(@RequestParam(name = "configId", required = true) Long configId,@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        VoucherConfiguration voucherConfiguration = new VoucherConfiguration();
        try {
            voucherConfigurationService.deleteById(configId, Long.valueOf(mvnoId));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"Delete Voucher Configuration" + voucherConfiguration.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.MESSAGE, "Voucher Configuration has been deleted successfully.");
            snmpCounters.incrementDeleteVoucherProfileSuccess();
            return apiResponseController.apiResponse(APIConstants.SUCCESS, response);
        } catch(IllegalArgumentException ce){
            response.put(APIConstants.ERROR_TAG,ce.getMessage());
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            return apiResponseController.apiResponse(HttpStatus.EXPECTATION_FAILED.value(), response);
        }catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            snmpCounters.incrementDeleteVoucherProfileFailure();
            return apiResponseController.apiResponse(APIConstants.FAIL, response);
        }  finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Get voucher configuration based on the given config id")
    @GetMapping("/findById")
   // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_VOUCHER_CONFIG_ALL +"\",\"" + AclConstants.OPERATION_VOUCHER_CONFIG_VIEW +"\")")
    public ResponseEntity<Map<String, Object>> get(@RequestParam(name = "configId", required = true) Long configId,@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
          TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;

        try {
            response.put(VOUCHER_CONFIGURATION, voucherConfigurationService.findById(configId, Long.valueOf(mvnoId)));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch voucher by id: "+ configId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            snmpCounters.incrementSearchVoucherProfileByIdSuccess();
            return apiResponseController.apiResponse(APIConstants.SUCCESS, response);
        } catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch voucher by id: " + configId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            apiResponseController.buildErrorMessageForResponse(response, e);
            snmpCounters.incrementSearchVoucherProfileByIdFailure();
            return apiResponseController.apiResponse(APIConstants.FAIL, response);
        }  finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Get voucher configuration based on the given configuration name")
    @GetMapping("/name/{name}")
 //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_VOUCHER_CONFIG_ALL +"\",\"" + AclConstants.OPERATION_VOUCHER_CONFIG_VIEW +"\")")
    public ResponseEntity<Map<String, Object>> getByName(PaginationDTO paginationDTO, @RequestParam(name = "name", required = false) String name,@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            if (paginationDTO.getSize() < 1) {
                response.put(APIConstants.ERROR_MESSAGE, "Page size must not be less than one!");
                return apiResponseController.apiResponse(APIConstants.FAIL, response);
            }
            Page<VoucherConfiguration> page = voucherConfigurationService.findByName(name, Long.valueOf(mvnoId), paginationDTO);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(page.getContent())) {
                responseCode = APIConstants.NULL_VALUE;
                response.put(APIConstants.ERROR_MESSAGE, "No Records Found!");
                if (!StringUtils.isEmpty(name)) {
                    responseCode = APIConstants.SUCCESS;
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"Fetch voucher configuration based on configuration name: "+ name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
                    snmpCounters.incrementSearchVoucherProfileByNameFailure();
                } else {
                    responseCode = APIConstants.NULL_VALUE;
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch voucher configuration based on configuration name: "+ name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);
                    snmpCounters.incrementGetVoucherProfileListFailure();
                }
            } else {
                responseCode = APIConstants.SUCCESS;
                response.put(VOUCHER_CONFIGURATION, page);
                if (!StringUtils.isEmpty(name)) {
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"Fetch voucher configuration based on configuration name "+ name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
                    snmpCounters.incrementSearchVoucherProfileByNameSuccess();
                } else {
                    responseCode = HttpStatus.EXPECTATION_FAILED.value();
                    log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Fetch voucher configuration based on configuration name "+ name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + responseCode);
                    snmpCounters.incrementGetVoucherProfileListSuccess();
                }
            }
            // response.put(VOUCHER_CONFIGURATION, voucherConfigurationService.findByName(name,mvnoId));
            log.debug("Request For Fetch voucher by name: " + name);
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Fetch voucher configuration based on configuration name "+ name + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            apiResponseController.buildErrorMessageForResponse(response, e);
            if (!StringUtils.isEmpty(name)) snmpCounters.incrementSearchVoucherProfileByNameSuccess();
            else snmpCounters.incrementGetVoucherProfileListSuccess();
            return apiResponseController.apiResponse(APIConstants.FAIL, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Update voucher configuration status based on the config id and status value")
    @GetMapping("/updateStatus")
    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_VOUCHER_CONFIG_ALL +"\",\"" + AclConstants.OPERATION_VOUCHER_CONFIG_VIEW +"\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.VOUCHER +"\")")

    public ResponseEntity<Map<String, Object>> updateVoucherConfigStatus(@RequestParam(name = "id", required = true) Long id, @RequestParam(name = "status", required = true) String status,@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            String message = voucherConfigurationService.updateVoucherConfigStatus(id, status, Long.valueOf(mvnoId));
            Integer responseCode = APIConstants.SUCCESS;
            response.put(APIConstants.MESSAGE, message);
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update voucher status"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            snmpCounters.incrementChangeStatusVoucherProfileSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch(CustomValidationException ce){
            response.put(APIConstants.ERROR_TAG,ce.getMessage());
            response.put(APIConstants.MESSAGE, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
            return apiResponseController.apiResponse(HttpStatus.EXPECTATION_FAILED.value(), response);
        }catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update voucher status"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            apiResponseController.buildErrorMessageForResponse(response, e);
            snmpCounters.incrementChangeStatusVoucherProfileFailure();
            return apiResponseController.apiResponse(APIConstants.FAIL, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Get list of voucher configurations based on the name and type value")
    @GetMapping("/findVoucher")
 //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_VOUCHER_CONFIG_ALL +"\",\"" + AclConstants.OPERATION_VOUCHER_CONFIG_VIEW +"\")")
    public ResponseEntity<Map<String, Object>> findVoucher(@RequestParam(name = "voucherName", required = false) String voucherName, @RequestParam(name = "voucherCodeFormat", required = false) String voucherCodeFormat,@RequestParam(name = "mvnoId", required = true) Integer mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            List<VoucherConfiguration> voucherConfigurationList = voucherConfigurationService.findVoucher(voucherName, voucherCodeFormat, Long.valueOf(mvnoId));
            Integer responseCode = 0;
            if (voucherConfigurationList.isEmpty()) {
                responseCode = APIConstants.NULL_VALUE;
                response.put(APIConstants.ERROR_MESSAGE, "No Records Found!");
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Voucher profile"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);
                snmpCounters.incrementSearchVoucherProfileByNameFailure();
            } else {
                responseCode = APIConstants.SUCCESS;
                response.put(VOUCHER_CONFIGURATION_LIST, voucherConfigurationList);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+LogConstants.REQUEST_FOR +"fetch Voucher profile"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
                snmpCounters.incrementSearchVoucherProfileByNameSuccess();
            }
            response.put(VOUCHER_CONFIGURATION_LIST, voucherConfigurationList);
//		    log.debug("Request For Fetch vouchers by name or code Format: " + voucherName +" " + voucherCodeFormat);
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Voucher profile"+ voucherName + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_FAILED  + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            apiResponseController.buildErrorMessageForResponse(response, e);
            snmpCounters.incrementSearchVoucherProfileByNameSuccess();
            return apiResponseController.apiResponse(APIConstants.FAIL, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
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
