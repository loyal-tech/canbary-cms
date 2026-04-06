package com.adopt.apigw.modules.childcustomer.constrollor;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.childcustomer.dto.*;
import com.adopt.apigw.modules.childcustomer.entity.ChildCustomer;
import com.adopt.apigw.modules.childcustomer.implemetation.ChildCustomerImpl;
import com.adopt.apigw.modules.childcustomer.parentchildmappingrel.ParentChildMappingRel;
import com.adopt.apigw.modules.childcustomer.parentchildmappingrel.ParentChildMappingRelService;
import com.adopt.apigw.modules.childcustomer.service.ChildCustomerService;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.utils.APIConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL+UrlConstants.CHILD_CUSTOMER)
public class ChildCustomerControllor {
    private static String MODULE = " [ChildCustomer] ";

    @Autowired
    private ChildCustomerService childCustomerService;
    @Autowired
    private ChildCustomerImpl childCustomerSerImpl;

    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private Tracer tracer;

    @Autowired
    private ParentChildMappingRelService parentChildMappingRelService;
    private static final Logger log = LoggerFactory.getLogger(ChildCustomerControllor.class);

    @Autowired
    private CustomersRepository customersRepository;
    @PostMapping(value = "/save")
    public ResponseEntity<?> createChildCustomer(@RequestBody ChildCustPojo pojo, HttpServletRequest req) {
        HashMap<String, Object> responseDTO = new HashMap<>();
        Integer RESP_CODE=APIConstants.FAIL;
        // Set MDC (Mapped Diagnostic Context) values for structured logging
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = req.getHeader(LogConstants.TRACE_ID);
        String spanId = traceContext.spanIdString();
        String userName = childCustomerSerImpl.getLoggedInUser().getUsername();

        MDC.put("type", "Update");
        MDC.put("userName", userName);
        MDC.put(LogConstants.TRACE_ID, traceId);
        MDC.put("spanId", spanId);

        try {
            ResponseEntity<?> response = childCustomerService.create(pojo, req);

            return response;
        } catch (Exception e) {
            log.error("Error while creating child customer: {}", e.getMessage(), e);

            RESP_CODE = APIConstants.FAIL;
            responseDTO.put("responsemessage","Failed to create child customer.");
        } finally {
            MDC.clear(); // Clears all MDC values at once
        }

        return childCustomerSerImpl.apiResponse(RESP_CODE,responseDTO);
    }


    @GetMapping(value = "/getAll")
    public GenericDataDTO getAll(@RequestParam Integer page,@RequestParam("pagesize") Integer pagesize, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) {
        GenericDataDTO responseDTO = new GenericDataDTO();

        // Extract current trace context and set MDC values for logging
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = req.getHeader(LogConstants.TRACE_ID);
        String spanId = traceContext.spanIdString();
        String userName = childCustomerSerImpl.getLoggedInUser().getUsername();

        MDC.put("type", "Update");
        MDC.put("userName", userName);
        MDC.put(LogConstants.TRACE_ID, traceId);
        MDC.put("spanId", spanId);

        try {
            Page<ChildCustomer> childCustomers = childCustomerService.getAllChildCustomer(page, pagesize,mvnoId);

            responseDTO.setResponseCode(APIConstants.SUCCESS);
            responseDTO.setResponseMessage(APIConstants.SUCCESS_STATUS);
            responseDTO.setDataList(childCustomers.getContent());
            responseDTO.setTotalRecords(childCustomers.getTotalElements());
            responseDTO.setTotalPages(childCustomers.getTotalPages());
            responseDTO.setPageRecords(childCustomers.getNumberOfElements()); // Number of records on current page

        } catch (Exception e) {
            // Log the error message
            log.error("Error while fetching child customers", e);
            responseDTO.setResponseCode(APIConstants.FAIL);
            responseDTO.setResponseMessage("Failed to fetch child customer data.");
        } finally {
            MDC.clear();
        }
        return responseDTO;
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> deleteChildCustomer(@RequestParam Long id, HttpServletRequest req) {
        HashMap<String, Object> response = new HashMap<>();
        int responseCode = APIConstants.FAIL;
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = req.getHeader(LogConstants.TRACE_ID);
        String spanId = traceContext.spanIdString();
        String userName = childCustomerSerImpl.getLoggedInUser().getUsername();
        MDC.put("type", "Update");
        MDC.put("userName", userName);
        MDC.put(LogConstants.TRACE_ID, traceId);
        MDC.put("spanId", spanId);
        try {
            log.info("Request received to delete child customer with ID: {}", id);

            childCustomerService.delete(id, req);

            response.put("responseMessage", "Child customer deleted successfully.");
            responseCode = APIConstants.SUCCESS;

            log.info("Child customer deleted successfully. ID: {}", id);
            return childCustomerSerImpl.apiResponse(responseCode, response);

        } catch (Exception e) {
            log.error("Error deleting child customer with ID {}: {}", id, e.getMessage(), e);

            response.put("responseMessage", "Failed to delete child customer.");
            return childCustomerSerImpl.apiResponse(responseCode, response);
        }finally {
            MDC.clear();
        }
    }
    @GetMapping(value = "/getChildByParentID")
    public GenericDataDTO getChildByParentID(@RequestParam Long parentId, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = request.getHeader(LogConstants.TRACE_ID);
        String spanId = traceContext.spanIdString();
        String userName = childCustomerSerImpl.getLoggedInUser().getUsername();

        MDC.put("type", "Fetch");
        MDC.put("userName", userName);
        MDC.put(LogConstants.TRACE_ID, traceId);
        MDC.put("spanId", spanId);

        try {
            log.info("Request received to fetch child customers for parent ID: {}", parentId);
            return childCustomerService.getchildCustByParentID(parentId);
        } catch (Exception e) {
            log.error("Failed to fetch child customers for parent ID {}: {}", parentId, e.getMessage(), e);

            GenericDataDTO errorResponse = new GenericDataDTO();
            errorResponse.setResponseCode(APIConstants.FAIL);
            errorResponse.setResponseMessage("Error occurred while fetching child customers.");
            return errorResponse;
        } finally {
            MDC.clear();
        }
    }
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> update(@RequestBody ChildCustPojo pojo,@PathVariable Long id ,HttpServletRequest request){
        HashMap<String,Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = request.getHeader(LogConstants.TRACE_ID);
        String spanId = traceContext.spanIdString();
        String userName = childCustomerSerImpl.getLoggedInUser().getUsername();

        MDC.put("type", "Fetch");
        MDC.put("userName", userName);
        MDC.put(LogConstants.TRACE_ID, traceId);
        MDC.put("spanId", spanId);

        try {
            log.info("Request received to fetch child customers for parent ID: {}");
            pojo.setId(id);
            ResponseEntity<?> responseEntity = childCustomerService.updatechildCustByParentID(pojo);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CHILD_CUSTOMER, AclConstants.OPERATION_CHILDCUSTOMER_EDIT, request.getRemoteAddr(), null, pojo.getParentCustId(), pojo.getFirstName());
            return responseEntity;
        } catch (Exception e) {
            log.error("Failed to fetch child customers for parent ID {}: {}",pojo.getUserName(), e.getMessage(), e);
            response.put("responseMessege","Error occurred while fetching child customers.");
        } finally {
            MDC.clear();
        }
        return childCustomerSerImpl.apiResponse(APIConstants.FAIL,response);
    }

    @GetMapping(value = "/getChildById")
    public GenericDataDTO getChildByID(@RequestParam Long Id, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = request.getHeader(LogConstants.TRACE_ID);
        String spanId = traceContext.spanIdString();
        GenericDataDTO response = new GenericDataDTO();
        String userName = childCustomerSerImpl.getLoggedInUser().getUsername();

        MDC.put("type", "Fetch");
        MDC.put("userName", userName);
        MDC.put(LogConstants.TRACE_ID, traceId);
        MDC.put("spanId", spanId);

        try {
            log.info("Request received to fetch ChildCustomer with ID: {}", Id);
            ChildCustomer childCustomer = childCustomerService.getchildCustByID(Id);
            if (childCustomer != null) {
                response.setResponseCode(APIConstants.SUCCESS);
                response.setResponseMessage(APIConstants.SUCCESS_STATUS);
                response.setData(childCustomer);
            } else {
                log.warn("No ChildCustomer found with ID: {}", Id);
                response.setResponseCode(APIConstants.NO_CONTENT_FOUND);
                response.setResponseMessage("No ChildCustomer found for the provided ID.");
            }
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch ChildCustomer with ID {}: {}", Id, e.getMessage(), e);
            response.setResponseCode(APIConstants.FAIL);
            response.setResponseMessage("Error occurred while fetching ChildCustomer.");
            return response;
        } finally {
            MDC.clear();
        }
    }
    @PostMapping(value = "/search")
    public ResponseEntity<?> getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = request.getHeader(LogConstants.TRACE_ID);
        String spanId = traceContext.spanIdString();
//        GenericDataDTO response = ne/w GenericDataDTO();
        String loggedInuserName = childCustomerSerImpl.getLoggedInUser().getUsername();
        MDC.put("type", "Fetch");
        MDC.put("userName", loggedInuserName);
        MDC.put(LogConstants.TRACE_ID, traceId);
        MDC.put("spanId", spanId);
        int responseCode = APIConstants.FAIL;
        HashMap<String,Object> response = new HashMap<>();

        try {
            Page<ChildCustomer> childCustomer = childCustomerService.getchildCustByID(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getStatus());
            CustomPageResponse<ChildCustomer> result = new CustomPageResponse<>();
            if (childCustomer != null) {
                result.setContent(childCustomer.getContent());
                response.put("messege",APIConstants.SUCCESS_STATUS);
                response.put("childCustomer",result);
                result.setCurrentPage(childCustomer.getNumber());
                result.setTotalPages(childCustomer.getTotalPages());
                result.setTotalElements(childCustomer.getTotalElements());
                responseCode =APIConstants.SUCCESS;

            } else {
                responseCode =APIConstants.NO_CONTENT_FOUND;
                response.put("messege","No ChildCustomer found for the provided ID.");
            }
            return childCustomerSerImpl.apiResponse(responseCode,response);
        } catch (Exception e) {
            responseCode = APIConstants.FAIL;
            response.put("messege","Error occurred while fetching ChildCustomer.");
            return childCustomerSerImpl.apiResponse(responseCode,response);
        } finally {
            MDC.clear();
        }
    }

    @PostMapping(value = "/getAllChildByParent")
    public ResponseEntity<?> getChildByParent(@RequestBody ChildByParentDTO requestDTO, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = request.getHeader(LogConstants.TRACE_ID);
        String spanId = traceContext.spanIdString();
//        GenericDataDTO response = ne/w GenericDataDTO();
        String loggedInuserName = childCustomerSerImpl.getLoggedInUser().getUsername();
        MDC.put("type", "Fetch");
        MDC.put("userName", loggedInuserName);
        MDC.put(LogConstants.TRACE_ID, traceId);
        MDC.put("spanId", spanId);
        int responseCode = APIConstants.FAIL;
        HashMap<String,Object> response = new HashMap<>();

        try {
            Page<ParentChildMappingRel> childCustomer = parentChildMappingRelService.getParentChildByParentCustId(requestDTO.getId(), requestDTO.getPage(), requestDTO.getPageSize());
            CustomPageResponse<ParentChildMappingRel> result = new CustomPageResponse<>();
            if (childCustomer != null) {
                result.setContent(childCustomer.getContent());
                response.put("messege",APIConstants.SUCCESS_STATUS);
                response.put("childCustomer",result);
                result.setCurrentPage(childCustomer.getNumber());
                result.setTotalPages(childCustomer.getTotalPages());
                result.setTotalElements(childCustomer.getTotalElements());
                responseCode =APIConstants.SUCCESS;
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CHILD_CUSTOMER, AclConstants.OPERATION_CHILDCUSTOMER_FETCH, request.getRemoteAddr(), null, requestDTO.getId(), "Fetched " + childCustomer.getTotalElements() + " child customers");
            } else {
                responseCode =APIConstants.NO_CONTENT_FOUND;
                response.put("messege","No ChildCustomer found for the provided ID.");
            }
            return childCustomerSerImpl.apiResponse(responseCode,response);
        } catch (Exception e) {
            responseCode = APIConstants.FAIL;
            response.put("messege","Error occurred while fetching ChildCustomer.");
            return childCustomerSerImpl.apiResponse(responseCode,response);
        } finally {
            MDC.clear();
        }
    }
    @PutMapping(value = "/updatepassword")
    public ResponseEntity<?> updateChildPassword(@RequestBody ChangePasswordPojo pojo,
                                                 HttpServletRequest request,@RequestParam("mvnoId") Integer mvnoId) {
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = request.getHeader(LogConstants.TRACE_ID);
        String spanId = traceContext.spanIdString();
        HashMap<String, Object> response = new HashMap<>();

        String loggedInUserName = childCustomerSerImpl.getLoggedInUser().getUsername();
        MDC.put("type", "Fetch");
        MDC.put("userName", loggedInUserName);
        MDC.put(LogConstants.TRACE_ID, traceId);
        MDC.put("spanId", spanId);

        try {
            return childCustomerService.updateChildPassword(pojo,mvnoId);
        } catch (Exception e) {
            log.error("Error while updating child password for user: {}", pojo.getUserName(), e);
            response.put("message", "An unexpected error occurred.");
            return childCustomerSerImpl.apiResponse(APIConstants.FAIL, response);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping(value = "/getChildByMobileNumber")
    public GenericDataDTO getChildByMobileNumber(@RequestParam String mobileNumber,Integer parentId, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = request.getHeader(LogConstants.TRACE_ID);
        String spanId = traceContext.spanIdString();
        GenericDataDTO response = new GenericDataDTO();
        String userName = childCustomerSerImpl.getLoggedInUser().getUsername();

        MDC.put("type", "Fetch");
        MDC.put("userName", userName);
        MDC.put(LogConstants.TRACE_ID, traceId);
        MDC.put("spanId", spanId);

        try {
            log.info("Request received to fetch ChildCustomer with ID: {}", mobileNumber);
            List<ChildCustomer> childCustomer = childCustomerService.getchildCustByMobileNumber(mobileNumber,parentId, Long.valueOf(childCustomerSerImpl.getMvnoIdFromCurrentStaff(parentId)));
            if (!childCustomer.isEmpty()) {
                response.setResponseCode(APIConstants.SUCCESS);
                response.setResponseMessage(APIConstants.SUCCESS_STATUS);
                response.setData(childCustomer);
            } else {
                log.warn("No ChildCustomer found with ID: {}", mobileNumber);
                response.setResponseCode(APIConstants.NO_CONTENT_FOUND);
                response.setResponseMessage("No ChildCustomer found for the provided ID.");
            }
            return response;
        }catch (CustomValidationException ex){
            response.setResponseCode(ex.getErrCode());
            response.setResponseMessage(ex.getMessage());
            return response;
        }catch (Exception e) {
            log.error("Failed to fetch ChildCustomer with ID {}: {}", mobileNumber, e.getMessage(), e);
            response.setResponseCode(APIConstants.FAIL);
            response.setResponseMessage("Error occurred while fetching ChildCustomer.");
            return response;
        } finally {
            MDC.clear();
        }
    }
    @GetMapping("/fetchchildcustomer")
    public GenericDataDTO getChildCustomerByMobileAndUsername(@RequestParam String username, @RequestParam String mobileNumber, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = request.getHeader(LogConstants.TRACE_ID);
        String spanId = traceContext.spanIdString();
        GenericDataDTO response = new GenericDataDTO();
        String userName = childCustomerSerImpl.getLoggedInUser().getUsername();

        MDC.put("type", "Fetch");
        MDC.put("userName", userName);
        MDC.put(LogConstants.TRACE_ID, traceId);
        MDC.put("spanId", spanId);

        log.info("Received request to fetch child customer. Requestor: {}, Username param: {}, MobileNumber param: {}",
                userName, username, mobileNumber);

        try {
            GenericDataDTO result = childCustomerService.getChildCustomerByMobileNumberAndUserName(username, mobileNumber);
            log.info("Fetch child customer completed successfully");
            return result;
        } catch (Exception e) {
            log.error("Error occurred while fetching child customer for username: {}, mobileNumber: {}", username, mobileNumber, e);
            response.setResponseCode(APIConstants.FAIL);
            response.setResponseMessage("Something went wrong while fetching customer.");
            return response;
        } finally {
            MDC.clear();
        }
    }


    @GetMapping("/isChildUserExist")
    public GenericDataDTO isChildUserExist(@RequestParam String username,@RequestParam Integer mvnoId, HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        String traceId = request.getHeader(LogConstants.TRACE_ID);
        String spanId = traceContext.spanIdString();
        GenericDataDTO response = new GenericDataDTO();
        String userName = childCustomerSerImpl.getLoggedInUser().getUsername();

        MDC.put("type", "Fetch");
        MDC.put("userName", userName);
        MDC.put(LogConstants.TRACE_ID, traceId);
        MDC.put("spanId", spanId);

        log.info("Received request to fetch child customer. Requestor: {}, Username param: {}, MobileNumber param: {}",
                userName);

        try {
            GenericDataDTO result = childCustomerService.getChildCustomerByUserName(username, mvnoId);
            log.info("Fetch child customer completed successfully");
            return result;
        } catch (Exception e) {
            log.error("Error occurred while fetching child customer for username: {}, mobileNumber: {}", username, mvnoId, e);
            response.setResponseCode(APIConstants.FAIL);
            response.setResponseMessage("Something went wrong while fetching customer.");
            return response;
        } finally {
            MDC.clear();
        }
    }



}
