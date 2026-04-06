package com.adopt.apigw.modules.childcustomer.parentchildmappingrel;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.modules.childcustomer.dto.CustomPageResponse;
import com.adopt.apigw.modules.childcustomer.entity.ChildCustomer;
import com.adopt.apigw.modules.childcustomer.implemetation.ChildCustomerImpl;
import com.adopt.apigw.modules.childcustomer.service.ChildCustomerService;
import com.adopt.apigw.pojo.api.LoginPojo;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL+UrlConstants.PARENT_CHILD_MAPPING)
public class ParentChildMappingControllor {
    @Autowired
    private ParentChildMappingRelService parentChildMappingRelService;
    @Autowired
    private ParentChildMappingRepo parentChildMappingRepo;

    private final Logger logger = LoggerFactory.getLogger(ParentChildMappingControllor.class);

    @Autowired
    private Tracer tracer;

    @Autowired
    private ChildCustomerImpl childCustomerSerImpl;


    @Autowired
    private ChildCustomerImpl childCustomerservice;

    @GetMapping(value = "/getAllParent/{username}")
    public ResponseEntity<?> getParentChildMapping(@PathVariable String username,@RequestParam("mvnoId") Integer mvnoId){
        return parentChildMappingRelService.getParentChildRel(username,mvnoId);
    }


    @GetMapping(value = "/getAllParentChildmapById")
    public ResponseEntity<?> getParentChildMappingbyparentId(@RequestParam Integer parentId){
        return parentChildMappingRelService.getParentChildRelBYParentId(parentId.longValue());
    }

    @PostMapping(value = "/updateChild")
    public ResponseEntity<?> getParentChildMappingbyparentId(@RequestBody UpdateChildDTO updateChildDTO){
        return parentChildMappingRelService.getChildUpdate(updateChildDTO);
    }

    @DeleteMapping(value = "/deleteChild")
    public ResponseEntity<?> deleteChildById(@RequestParam  Long childId){
        return parentChildMappingRelService.getChildDelete(childId);
    }
    @PutMapping(value = "/reactivatechild")
    public ResponseEntity<?> reActivateChildCustomer(@RequestParam  Long childId){
        return parentChildMappingRelService.reactivateChildCustomer(childId);
    }

    @GetMapping(value = "/getChildById")
    public ResponseEntity<?> getChildById(@RequestParam  Long childId){
        return parentChildMappingRelService.getChildById(childId);
    }

    @PostMapping(value = "/search")
    public ResponseEntity<?> search(@RequestBody PaginationRequestDTO requestDTO,@RequestParam Integer parentId, HttpServletRequest request) {
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
            Page<ParentChildMappingRel> childCustomer = parentChildMappingRelService.getchildSearch(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getStatus(),parentId.longValue());
            CustomPageResponse<ParentChildMappingRel> result = new CustomPageResponse<>();
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


    @PostMapping(value = "/verifyChildCustomer")
    public ResponseEntity<?> verifyChildCustomer(@RequestBody VerifyChildDTO verifyChildDTO){
        return parentChildMappingRelService.verifyChildCustomer(verifyChildDTO);
    }

    @GetMapping(value = "/getParentChildByMobile/{mobilenumber}")
    public ResponseEntity<?> getParentChildByNumber(@PathVariable String mobilenumber,@RequestParam("mvnoId") Integer mvnoId){
        HashMap<String, Object>
                responseMap = new HashMap<>();
        int responseCode = APIConstants.INTERNAL_SERVER_ERROR;
        try {
            logger.info("Fetching Parent-Child mapping for mobilenumber: {}", mobilenumber);
                List<ParentChildMappingRel> mappings = parentChildMappingRelService.getParentChildMappingList(mobilenumber,mvnoId);
                if (!mappings.isEmpty()) {
                    responseMap.put("responseMessage", APIConstants.SUCCESS_STATUS);
                    responseMap.put("parentChildMappingRel", mappings);
                    responseCode = APIConstants.SUCCESS;
                    logger.info("Successfully fetched {} records for username {}", mappings.size(), mobilenumber);
                } else {
                    responseMap.put("responseMessage", "No child found.");
                    responseCode = APIConstants.NO_CONTENT_FOUND;
                    logger.warn("No Parent-Child mappings found for username: {}", mobilenumber);
                }
        } catch (Exception e) {
            responseMap.put("responseMessage", APIConstants.FAIL);
            responseCode = APIConstants.FAIL;
            logger.error("Error occurred while fetching Parent-Child mappings for username: {}", mobilenumber, e);
        }

        return childCustomerservice.apiResponse(responseCode, responseMap);
    }
    @GetMapping(value = "/getParentDetails")
    public ResponseEntity<?> getParentDetails(@RequestParam Integer custId,
                                              @RequestParam("mvnoId") Integer mvnoId) {

        HashMap<String, Object> responseMap = new HashMap<>();
        int responseCode = APIConstants.INTERNAL_SERVER_ERROR;
        try {
            logger.info("Fetching parent details for customerId={} and mvnoId={}", custId, mvnoId);

            ParentChildMappingRel mapping =
                    parentChildMappingRepo.findLatestParentMapping(custId, mvnoId).orElse(null);

            if (mapping != null) {

                responseMap.put("responseMessage", APIConstants.SUCCESS_STATUS);
                responseMap.put("data", mapping);
                responseCode = APIConstants.SUCCESS;

                logger.info("Parent details fetched successfully for customerId={}", custId);

            } else {

                responseMap.put("responseMessage", "No child found.");
                responseCode = APIConstants.NO_CONTENT_FOUND;

                logger.warn("No parent-child mapping found for customerId={} and mvnoId={}", custId, mvnoId);
            }

        } catch (Exception e) {

            responseMap.put("responseMessage", APIConstants.FAIL);
            responseCode = APIConstants.FAIL;

            logger.error("Exception while fetching parent details for customerId={} and mvnoId={}",
                    custId, mvnoId, e);
        }

        return childCustomerservice.apiResponse(responseCode, responseMap);
    }

}
