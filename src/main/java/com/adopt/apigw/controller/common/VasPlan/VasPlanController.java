package com.adopt.apigw.controller.common.VasPlan;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.dto.ValidationData;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.BooleanWithMessage;
import com.adopt.apigw.model.common.VasPlan;
import com.adopt.apigw.model.postpaid.Charge;
import com.adopt.apigw.modules.CustomerFeedback.model.CustomerFeedbackDTO;
import com.adopt.apigw.pojo.api.PlanPojo;
import com.adopt.apigw.pojo.api.VasPlanPojo;
//import com.adopt.apigw.service.common.VasPlanService;
import com.adopt.apigw.service.common.VasPlanService;
import com.adopt.apigw.service.postpaid.PlanServiceService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping(UrlConstants.BASE_API_URL)
public class VasPlanController extends ApiBaseController {
    private static final String MODULE = " [VasPlanController] ";
    private static final Logger log = LoggerFactory.getLogger(APIController.class);
    @Autowired
    VasPlanService vasPlanService;

    @Autowired
    private Tracer tracer;


    @PostMapping("/vasplan")
    public ResponseEntity<?> createVasPlan(@Valid @RequestBody VasPlanPojo pojo, HttpServletRequest req) throws Exception {
        int RESP_CODE = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Create");
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("spanId",traceContext.spanIdString());
        try {
            VasPlanService vasPlanService = SpringContext.getBean(VasPlanService.class);
           // vasPlanService.vasPlanValidation(pojo); /**Vas validation commited**/
            boolean flag = vasPlanService.duplicateVerifyAtSave(pojo.getName());
            boolean defualtVas = vasPlanService.checkDefualtVas(pojo);
            if (pojo.getIsdefault() && defualtVas) {
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_DEFUALT_VASPLAN_MANAGEMENT);
                RESP_CODE = HttpStatus.ALREADY_REPORTED.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create VasPlan Service" + LogConstants.LOG_BY_NAME + pojo.getName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return apiResponse(RESP_CODE, response, null);
            }
            if (flag) {
                pojo = vasPlanService.save(pojo);
                response.put("Vasplan", pojo);
                response.put(APIConstants.MESSAGE, "Successfully Created");
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create VasPlan Service" + LogConstants.LOG_BY_NAME + pojo.getName() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_VASPLAN_MANAGEMENT);
                log.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create country" +LogConstants.LOG_BY_NAME+pojo.getName()+LogConstants.REQUEST_BY +  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "VasPlan name already exist "+ LogConstants.LOG_STATUS_CODE+ RESP_CODE);
                return apiResponse(RESP_CODE, response, null);

            }

        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create VasPlan Service" + LogConstants.LOG_BY_NAME + pojo.getName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);

        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());

            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create VasPlan Service" + LogConstants.LOG_BY_NAME + pojo.getName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED
                    + LogConstants.LOG_ERROR + ex.getMessage()
                    + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.clear();
        }

        return apiResponse(RESP_CODE, response, null);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody VasPlanPojo vasPlanPojo, @PathVariable Integer id, HttpServletRequest req) {
        Integer respCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());

        try {
            VasPlanService vasPlanService = SpringContext.getBean(VasPlanService.class);
            vasPlanPojo.setId(id);
            boolean duplicateCheck = vasPlanService.duplicateVerifyAtEdit(vasPlanPojo.getName(), id);
            if (duplicateCheck) {
                vasPlanPojo = vasPlanService.updateCustomerVASplan(vasPlanPojo,req);
                response.put("VAS plan", vasPlanPojo);
                response.put(APIConstants.MESSAGE, "VAS plan updated successfully");
                respCode = APIConstants.SUCCESS;
            } else {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
//                LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update VAS plan" + LogConstants.LOG_BY_NAME + vasPlanPojo.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
                return apiResponse(respCode, response, null);
            }

        } catch (CustomValidationException ce) {
//            LOGGER.error(ce.getMessage(), ce);
            respCode = ce.getErrCode() != null ? ce.getErrCode() : HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.MESSAGE, Constants.VAS_UPDATE_ERROR_MSG);
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//            LOGGER.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update VAS plan" + LogConstants.LOG_BY_NAME + vasPlanPojo.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);

        } catch (Exception ex) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return apiResponse(respCode, response);
    }


    @DeleteMapping("/vasplan/{id}")
    public ResponseEntity<?> deleteVasPlan(@PathVariable Integer id) {
        HashMap<String, Object> response = new HashMap<>();
        Integer respCode = APIConstants.FAIL;

        try {
            vasPlanService.deleteVasPlan(id);
            respCode = APIConstants.SUCCESS;
            response.put(APIConstants.MESSAGE, "VAS Plan deleted successfully");
        } catch (CustomValidationException e) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
        } catch (Exception e) {
            respCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.put(APIConstants.ERROR_TAG, "Error deleting VAS Plan");
        }

        return apiResponse(respCode, response);
    }


    @GetMapping("/vasplans")
    public ResponseEntity<?> getAllVasPlans() {
        HashMap<String, Object> response = new HashMap<>();
        Integer respCode = APIConstants.FAIL;

        try {
            List<VasPlan> vasPlans = vasPlanService.getAllActiveVasPlans();
            response.put("vasPlans", vasPlans);
            respCode = APIConstants.SUCCESS;
        } catch (Exception e) {
            response.put(APIConstants.ERROR_TAG, "Failed to fetch VAS Plans");
            respCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        return apiResponse(respCode , response);
    }

    @GetMapping("/currencybasevasplans")
    public ResponseEntity<?> getAllVasPlansBasedCurrency(@RequestParam(value = "currency") String currency ,@RequestParam(value = "mvnoId") Integer mvnoId) {
        HashMap<String, Object> response = new HashMap<>();
        Integer respCode = APIConstants.FAIL;

        try {
            List<VasPlan> vasPlans = vasPlanService.getAllActiveVasPlansBasedCurrency(currency,mvnoId);
            response.put("vasPlans", vasPlans);
            respCode = APIConstants.SUCCESS;
        } catch (Exception e) {
            response.put(APIConstants.ERROR_TAG, "Failed to fetch VAS Plans");
            respCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        return apiResponse(respCode , response);
    }

    @ResponseBody
    @PostMapping("/vasplans/findall")
    public GenericDataDTO getallwithpagination(@RequestBody PaginationRequestDTO paginationRequestDTO,@RequestParam(value = "mvnoId") Integer mvnoId) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Page<VasPlan> vasPlanPage = vasPlanService.getAllActiveVasPlansWithPagination(paginationRequestDTO,mvnoId);

            if (vasPlanPage != null && !vasPlanPage.isEmpty()) {
                // Set success response with data and pagination info
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);  // Assuming you have a success code constant
                genericDataDTO.setResponseMessage("Records Found");
                genericDataDTO.setDataList(vasPlanPage.getContent());
                genericDataDTO.setTotalRecords(vasPlanPage.getTotalElements());
                genericDataDTO.setPageRecords(vasPlanPage.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(vasPlanPage.getNumber() + 1);
                genericDataDTO.setTotalPages(vasPlanPage.getTotalPages());
            } else {
                genericDataDTO.setResponseCode(APIConstants.NOT_FOUND);
                genericDataDTO.setResponseMessage("No Record Found!");
            }
        } catch (Exception e) {
            // Handle exception and set error response
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }

    @PostMapping("/vasPlan/search")
    public ResponseEntity<?> searchVasPlan(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<VasPlan> vasPlanList = null;
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            requestDTO = setDefaultPaginationValues(requestDTO);
            ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
            if (validationData.isValid()) {
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                response.put(APIConstants.ERROR_TAG, validationData.getMessage());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Vasplan using keyword"+requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + validationData.getMessage() + LogConstants.LOG_STATUS_CODE +respCode);
                return apiResponse(respCode, response);
            }
            VasPlanService vasPlanService = SpringContext.getBean(VasPlanService.class);
            vasPlanList = vasPlanService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder());
            Integer Response = 0;
            if (vasPlanList.isEmpty()) {
                Response = APIConstants.NULL_VALUE;
                response.put(APIConstants.MESSAGE, "No Records Found!");
                response.put("vasPlanList", new ArrayList<>());
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Vasplan using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE +Response);
                return apiResponse(Response, response, vasPlanList);
            }
            if (null != vasPlanList && 0 < vasPlanList.getSize()) {
                response.put("vasPlanList", vasPlanList.getContent());
            } else {
                response.put("vasPlanList", new ArrayList<>());
            }
            respCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Vasplan using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +respCode);

        } catch (CustomValidationException ce) {
            log.error(ce.getMessage(),ce);
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search Vasplan using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE +respCode);
        } catch (RuntimeException re) {
            log.error(re.getMessage(),re);
            respCode = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, re.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Vasplan using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + re.getMessage()+ LogConstants.LOG_STATUS_CODE +respCode);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"search Vasplan using keyword : " +requestDTO.getFilters().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response, vasPlanList);
    }

    @GetMapping("/vasplan/{id}")
    public ResponseEntity<?> getVasplanById(@PathVariable Integer id,@RequestParam(value = "mvnoId") Integer mvnoId, HttpServletRequest req) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        VasPlanPojo pojo = new VasPlanPojo();
        try {
            VasPlanService vasPlanService = SpringContext.getBean(VasPlanService.class);
            VasPlan vasPlan = vasPlanService.get(id,mvnoId);
            if (vasPlan == null) {
                respCode = APIConstants.NOT_FOUND;
                response.put(APIConstants.ERROR_TAG, "VasPlan Not Found!");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch VasPlan "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+respCode);
                return apiResponse(respCode, response);
            } else {
                response.put("VasPlanData", vasPlanService.convertVasPlanModelToVasPlanPojo(vasPlan));
                respCode = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch VasPlan"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+respCode);
            }

        } catch (CustomValidationException ce) {
            log.error(ce.getMessage(),ce);
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "fetch VasPlan"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            log.error(ex.getMessage(),ex);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch VasPlan"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response);
    }

    @GetMapping("/getAllbychargetype")
    public ResponseEntity<GenericDataDTO> getAllVasCharge(@RequestParam(value = "mvnoId") Integer mvnoId) {
        GenericDataDTO response = new GenericDataDTO();

        try {
            List<Charge> result = vasPlanService.getByListType(mvnoId);

            if (result == null || result.isEmpty()) {
                response.setResponseMessage("No data found for the specified charge type.");
                response.setResponseCode(HttpStatus.NO_CONTENT.value());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }

            response.setResponseCode(HttpStatus.OK.value());
            response.setResponseMessage("Data retrieved successfully.");
            response.setDataList(result);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/vasplan/updateVas")
    public ResponseEntity<?> updateVas(@RequestBody VasPlanUpdateDTO vasPlanUpdateDTO, HttpServletRequest req) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            VasPlanService vasPlanService = SpringContext.getBean(VasPlanService.class);
            vasPlanService.vasPlanUpdate(vasPlanUpdateDTO);
                respCode = APIConstants.SUCCESS;
                response.put(APIConstants.MESSAGE, "VasPlan update successfully");
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"update VasPlan "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+respCode);
                return apiResponse(respCode, response);
        } catch (CustomValidationException ce) {
            log.error(ce.getMessage(),ce);
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "fetch VasPlan"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            log.error(ex.getMessage(),ex);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch VasPlan"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response);
    }


    @GetMapping("/vasplan/getCustVasPlan")
    public ResponseEntity<?> getCustVasPlan(@RequestParam(value = "custId") Integer custId,@RequestParam(value = "mvnoId") Integer mvnoId, HttpServletRequest req) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            VasPlanService vasPlanService = SpringContext.getBean(VasPlanService.class);
            VasPlan vasPlan =  vasPlanService.getCustVasPlan(custId);
            if(vasPlan != null) {
                respCode = APIConstants.SUCCESS;
                response.put("vasPlanList", Collections.singletonList(vasPlan));
                response.put(APIConstants.MESSAGE, "VasPlan fetch successfully");
            }
            else{
                respCode = APIConstants.NO_CONTENT;
                response.put("vasPlanList",new ArrayList<>());
                response.put(APIConstants.MESSAGE , "No attached vas found");
            }
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"update VasPlan "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+respCode);
            return apiResponse(respCode, response);
        } catch (CustomValidationException ce) {
            log.error(ce.getMessage(),ce);
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "fetch VasPlan"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            log.error(ex.getMessage(),ex);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch VasPlan"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response);
    }

    @GetMapping("/vasplan/checkShiftLocation")
    public ResponseEntity<?> checkShiftLocation(@RequestParam(value = "custId") Integer custId, HttpServletRequest req) throws Exception {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "FETCH");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            VasPlanService vasPlanService = SpringContext.getBean(VasPlanService.class);
            BooleanWithMessage booleanWithMessage = vasPlanService.checkShiftLocation(custId);
            respCode = APIConstants.SUCCESS;
            response.put("isAllowed", booleanWithMessage.isAllowed());
            response.put(APIConstants.MESSAGE, booleanWithMessage.getMessage());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"update VasPlan "+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+respCode);
            return apiResponse(respCode, response);
        } catch (CustomValidationException ce) {
            log.error(ce.getMessage(),ce);
            respCode = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR + "fetch VasPlan"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        } catch (Exception ex) {
            log.error(ex.getMessage(),ex);
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch VasPlan"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response);
    }
    @GetMapping("/vasplan/getVasPlanByCustId")
    public ResponseEntity<?> getVasPlanByCustId(@RequestParam Integer custId, HttpServletRequest req) {
        Integer respCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, (String) req.getAttribute(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());
        try {
            VasPlanService vasPlanService = SpringContext.getBean(VasPlanService.class);
            List<VasPlanResponseDTO> vasPlans = vasPlanService.getCustVasDetails(custId);
            if (vasPlans == null || vasPlans.isEmpty()) {
                respCode = APIConstants.NO_CONTENT;
                response.put("vasPlanList", Collections.emptyList());
                response.put(APIConstants.MESSAGE, "There is no active value added services are available for this customer.");
            } else {
                respCode = APIConstants.SUCCESS;
                response.put("vasPlanList", vasPlans);
                response.put(APIConstants.MESSAGE, "VAS plans fetched successfully");
            }
        } catch (Exception ex) {
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR, ex.getMessage());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(respCode, response);
    }

}
