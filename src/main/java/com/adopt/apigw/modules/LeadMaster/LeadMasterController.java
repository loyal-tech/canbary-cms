package com.adopt.apigw.modules.LeadMaster;

import com.adopt.apigw.audit.AuditService;
import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.lead.LeadMaster;
import com.adopt.apigw.model.lead.LeadMasterPojo;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.service.LeadMasterService;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class LeadMasterController {


    private static String MODULE = " [LeadMasterController] ";
    private static final Logger logger = LoggerFactory.getLogger(APIController.class);
    @Autowired
    ClientServiceSrv clientServiceSrv;

    private static final String RESCHEDULE_FOLLOW_UP_REMARK_LIST = "rescheduleFollowupRemarkList";
    @Autowired
    AuditLogService auditLogService;

    @Autowired
    LeadMasterService leadMasterService;

    @Autowired
    APIController apiController;


   @PostMapping("/getAllLead")
    public GenericDataDTO getAllLead(@RequestBody PaginationRequestDTO paginationRequestDTO){
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = MODULE + "[getAllLead]";
        MDC.put("type","update");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try{
          //  LeadMasterService leadMasterService = SpringContext.getBean(LeadMasterService.class);
            return leadMasterService.getAllLeadList(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
        }
       catch (Exception ex) {
        ex.printStackTrace();
        RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
        genericDataDTO.setResponseMessage(ex.getMessage());
        genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        logger.error("Unable to fetch  wareHouse And ProductWiseInventories :  request: { From : {}}; Response : {{}};Error :{};Exception:{} ;", SUBMODULE,HttpStatus.NOT_FOUND, ResponseEntity.notFound(),ex.getStackTrace());
    }
       MDC.remove("type");
       return genericDataDTO;
   }

    @GetMapping("/findAll/reScheduleFollowUpRemarks")
    @ApiOperation(value = "Get list of ReScheduleFollowUpRemarks in the system")
//	    @PreAuthorize("@roleAccesses.hasPermission('concurrent','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>>  findAllReScheduleFollowUpRemarks(HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "FETCH");
        Integer responseCode = APIConstants.FAIL;
        try {
            ClientService clientService = this.clientServiceSrv.getByName(CommonConstants.RESCHEDULE_FOLLOW_UP_REMARKS);
            if (clientService == null) {
                response.put(RESCHEDULE_FOLLOW_UP_REMARK_LIST, new ArrayList<>());
                response.put(APIConstants.MESSAGE, "No Records Found!");
            } else {
                List<String> reScheduleFollowUpRemarkList = new ArrayList<String>(Arrays.asList(clientService.getValue().split(" , ")));
                response.put(RESCHEDULE_FOLLOW_UP_REMARK_LIST, reScheduleFollowUpRemarkList);
            }
            responseCode = APIConstants.SUCCESS;
            logger.info("Fetching RescheduleFollowupRemarkList :  request: { From : {}}; Response : {{}}",MODULE,responseCode,response);
        } catch (CustomValidationException e) {
            responseCode = e.getErrCode();
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            logger.error("Unable to Fetch RescheduleFollowupRemarkList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE,responseCode,response,e.getStackTrace());
        } catch (Exception e) {
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            logger.error("Unable to Fetch RescheduleFollowupRemarkList  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE,responseCode,response,e.getStackTrace());
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
        return (ResponseEntity<Map<String, Object>>) apiController.apiResponse(responseCode, response);
    }


}
