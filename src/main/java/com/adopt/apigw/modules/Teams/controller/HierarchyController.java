package com.adopt.apigw.modules.Teams.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.dto.ValidationData;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.lead.LeadChangeAssigneePojo;
import com.adopt.apigw.model.lead.LeadQuotationChangeAssigneePojo;
import com.adopt.apigw.modules.Teams.domain.Hierarchy;
import com.adopt.apigw.modules.Teams.model.HierarchyDTO;
import com.adopt.apigw.modules.Teams.repository.HierarchyRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.tickets.service.CaseService;
import com.adopt.apigw.pojo.api.LeadMgmtWfDTO;
import com.adopt.apigw.pojo.api.LeadReasonMgmtWfDTO;
import com.adopt.apigw.rabbitMq.message.LeadQuotationWfDTO;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;


@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.TEAMS_HIERARCHY)
public class HierarchyController extends ExBaseAbstractController2<HierarchyDTO> {

    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    ClientServiceSrv clientServiceSrv;

//    @Autowired
//    private CaseService caseService;

    @Autowired
    HierarchyRepository hierarchyRepository;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    private Tracer tracer;
    @Autowired
    private CustomersRepository customersRepository;


    public HierarchyController(HierarchyService service) {
        super(service);
    }


    @Override
    public String getModuleNameForLog() {
        return "[Teams Hierarchy]";
    }

    private static final Logger logger = LoggerFactory.getLogger(HierarchyController.class);


//    public GenericDataDTO save(@Valid @RequestBody HierarchyDTO entityDTO, BindingResult bindingResult, Authentication authentication, HttpServletRequest httpServletRequest)throws Exception{
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//       try{
//        HierarchyDTO hierarchyDTO = hierarchyService.saveEntity(entityDTO);
//        genericDataDTO.setData(hierarchyDTO);
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");}
//       catch (Exception ex){
//           ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//           genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//           genericDataDTO.setResponseMessage(ex.getMessage());
//       }
//       return genericDataDTO;
//    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_HIERARCHY_ALL + "\",\"" + AclConstants.OPERATION_HIERARCHY_VIEW + "\")")


//    @PreAuthorize("validatePermission(\"" + MenuConstants.WORKFLOW_LIST + "\")")
    @GetMapping("/hierarchy/all")
    public GenericDataDTO getAllHierarchy(HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        String SUBMODULE = " [getAllHierarchy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(hierarchyService.getAllHierarchy());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching All Hierarchy" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetching All Hierarchy" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }


    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_HIERARCHY_ALL + "\",\"" + AclConstants.OPERATION_HIERARCHY_ADD + "\")")

    @PreAuthorize("validatePermission(\"" + MenuConstants.WORKFLOW_CREATE + "\")")
    @Override
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO save(@Valid @RequestBody HierarchyDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() > 1) {
            throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
        }

        if (getLoggedInUser().getLco())
            entityDTO.setLcoId(getLoggedInUser().getPartnerId());
        else
            entityDTO.setLcoId(null);

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            if (result.hasErrors()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Heirarchy" + LogConstants.LOG_BY_NAME + entityDTO.getHierarchyName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            ValidationData validation = validateSave(entityDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Heirarchy" + LogConstants.LOG_BY_NAME + entityDTO.getHierarchyName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //           ApplicationLogger.logger.info(getModuleNameForLog() + " entityDto : " + entityDTO);

            boolean flag = hierarchyService.duplicateVerifyAtSave(entityDTO.getEventName(),mvnoId);
            if (!flag) {
                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
                    throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
                HierarchyDTO dtoData = hierarchyService.saveEntity(entityDTO);
                genericDataDTO.setData(dtoData);
                genericDataDTO.setTotalRecords(1);
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Heirarchy" + LogConstants.LOG_BY_NAME + entityDTO.getHierarchyName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                //response.put(APIConstants.ERROR_TAG, MessageConstants.TAX_NAME_EXITS);
                if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                    genericDataDTO.setResponseMessage("User with multiple BU access is restricted from SAVE operations !!");
                } else {
                    genericDataDTO.setResponseMessage("Duplicate Entry already Exist!!");
                }
                //return apiResponse(RESP_CODE, response, null);
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Heirarchy" + LogConstants.LOG_BY_NAME + entityDTO.getHierarchyName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "charge with same name already exist" + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to save data. Please try after some time");
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Heirarchy" + LogConstants.LOG_BY_NAME + entityDTO.getHierarchyName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    //add edit constant for
    @PreAuthorize("validatePermission(\"" + MenuConstants.WORKFLOW_EDIT + "\")")
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO update(@Valid @RequestBody HierarchyDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            if (result.hasErrors()) {
//               ApplicationLogger.logger.debug("Base Controller Error"+result.getFieldErrors());
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
                return genericDataDTO;
            }
            ValidationData validation = validateUpdate(entityDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                return genericDataDTO;
            }

//            HierarchyDTO dtoData = hierarchyService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());

//            entityDTO.setMvnoId(dtoData.getMvnoId());
            genericDataDTO.setData(hierarchyService.updateEntity(entityDTO));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setTotalRecords(1);
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
            } else if (ex instanceof CustomValidationException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
            } else {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
            }
        }
        return genericDataDTO;
    }


    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_HIERARCHY_ALL + "\",\"" + AclConstants.OPERATION_HIERARCHY_VIEW + "\")")

//    @PreAuthorize("validatePermission(\"" + MenuConstants.WORKFLOW_LIST + "\")")
    @PostMapping(value = "/search")
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter, HttpServletRequest req,@RequestParam Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        try {

            if (null == filter || null == filter.getFilter() || 0 == filter.getFilter().size()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please provide search criteria!");
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search hierarchy using keyword " + filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            if (null != pageSize && pageSize > MAX_PAGE_SIZE)
                pageSize = MAX_PAGE_SIZE;
            genericDataDTO = hierarchyService.search(filter.getFilter(), page, pageSize, sortBy, sortOrder,mvnoId);

            if (null != genericDataDTO) {

                if (genericDataDTO.getDataList().isEmpty()) {
                    genericDataDTO = new GenericDataDTO();
                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                    genericDataDTO.setResponseMessage("No Record Found!");
                    genericDataDTO.setDataList(new ArrayList<>());
                    genericDataDTO.setTotalRecords(0);
                    genericDataDTO.setPageRecords(0);
                    genericDataDTO.setCurrentPageNumber(1);
                    genericDataDTO.setTotalPages(1);
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search hierarchy keyword " + filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

                }
                return genericDataDTO;

            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                genericDataDTO.setResponseMessage("No Record Found!");
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);

            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search hierarchy using keyword" + filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }
    public Integer getMvnoIdFromCurrentStaff(Integer custId) {
        //TODO: Change once API work on live BSS server
        Integer mvnoId = null;
        try {
            if(custId!=null){
                mvnoId = customersRepository.getCustomerMvnoIdByCustId(custId);

            }
//            else {
//                SecurityContext securityContext = SecurityContextHolder.getContext();
//                if (null != securityContext.getAuthentication()) {
//                    if(securityContext.getAuthentication().getPrincipal() != null)
//                        mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
//                }
//            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }

    //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_HIERARCHY_ALL + "\",\"" + AclConstants.OPERATION_HIERARCHY_VIEW + "\")")

    @PreAuthorize("validatePermission(\"" + MenuConstants.WORKFLOW_LIST + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = super.getEntityById(id, req,mvnoId);
        HierarchyDTO hierarchyDTO = (HierarchyDTO) genericDataDTO.getData();
        return genericDataDTO;
    }


    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_HIERARCHY_ALL + "\",\"" + AclConstants.OPERATION_HIERARCHY_VIEW + "\")")
    @GetMapping("/assignFromStaffList")
    public GenericDataDTO assignFromStaffList(@RequestParam(name = "nextAssignStaff") Integer nextAssignStaff, @RequestParam(name = "eventName") String eventName, @RequestParam(name = "entityId") Integer entityId, @RequestParam(name = "isApproveRequest") boolean isApproveRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            genericDataDTO.setResponseMessage("Assigned to next staff");
            hierarchyService.assignFromStaffList(nextAssignStaff, eventName, entityId, isApproveRequest);
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setPageRecords(0);
            genericDataDTO.setCurrentPageNumber(1);
            genericDataDTO.setTotalPages(1);
        } catch (CustomValidationException e) {
            genericDataDTO.setResponseMessage(e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
        }
        return genericDataDTO;
    }

    @PutMapping("/approveLead")
    public GenericDataDTO approveLead(@RequestBody LeadReasonMgmtWfDTO leadReasonMgmtWfDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            genericDataDTO.setResponseMessage("Assigned to next staff");
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setPageRecords(0);
            genericDataDTO.setCurrentPageNumber(1);
            genericDataDTO.setTotalPages(1);
            LeadMgmtWfDTO leadMgmtWfDTO = hierarchyService.convertLeadReasonMgmtWfDTOToLeadMgmtWfDTO(leadReasonMgmtWfDTO);
            return hierarchyService.approveLead(leadMgmtWfDTO, genericDataDTO);


        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
        }
        return genericDataDTO;
    }

    @PostMapping("/assignFromStaffListForLead")
    public GenericDataDTO assignFromStaffListForLead(@RequestParam(name = "nextAssignStaff") Integer nextAssignStaff, @RequestParam(name = "eventName") String eventName, @RequestBody LeadMgmtWfDTO leadMgmtWfDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            genericDataDTO.setResponseMessage("Assigned to next staff");
            genericDataDTO.setData(hierarchyService.assignFromStaffListForLead(nextAssignStaff, eventName, leadMgmtWfDTO));
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setPageRecords(0);
            genericDataDTO.setCurrentPageNumber(1);
            genericDataDTO.setTotalPages(1);


        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setTotalRecords(0);
        }
        return genericDataDTO;
    }


    @GetMapping("/getApprovalProgress")
    public GenericDataDTO getApprovalProgress(@RequestParam(name = "entityId") Long entityId, @RequestParam(name = "eventName") String eventName, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId", traceContext.traceIdString());
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
//        String staffname=hierarchyService.getApproveProgress(eventName,entityId);
        try {
            genericDataDTO.setDataList(hierarchyService.getApproveProgress(eventName, entityId));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Approval Progress" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Approval Progress" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }


    @GetMapping("/getApprovalProgressForLead")
    public GenericDataDTO getApprovalProgressForLead(@RequestParam(name = "mvnoId") Integer mvnoId, @RequestParam(name = "buId") Long buId, @RequestParam(name = "nextTeamHierarchyMappingId") Integer nextTeamHierarchyMappingId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
//        String staffname=hierarchyService.getApproveProgress(eventName,entityId);
        try {
            genericDataDTO.setDataList(hierarchyService.getApprovalProgressForLead(mvnoId, buId, nextTeamHierarchyMappingId));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Approval Progress for lead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, outwardDto.getId(), outwardDto.getOutwardNumber());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Approval Progress for lead" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
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
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getStackTrace(), e);
        }
        return loggedInUser;
    }


    @GetMapping("/assignEveryStaff")
    public GenericDataDTO assignEveryStaff(@RequestParam(name = "entityId") Integer entityId, @RequestParam(name = "eventName") String eventName, @RequestParam(name = "isApproveRequest") Boolean isApproveRequest, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Assign Every Staff from by id : " + entityId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return hierarchyService.assignEveryStaff(entityId, eventName, isApproveRequest);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Assign Every Staff from by id : " + entityId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    @GetMapping("/reassignlead")
//    public GenericDataDTO reassignLead(@RequestParam(name = "caseId") Long caseId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        MDC.put("type", "Fetch");
//        try {
//            logger.info("Getting Lead Reassigned from  with id  " + caseId + "  is Successfull:  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//            return caseService.reassignLead(caseId);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Reassign Lead  with " + caseId + ":  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.REASSIGN_LEAD + "\",\"" + MenuConstants.REASSIGN_ENTERPRISE_LEAD + "\")")
    @GetMapping("/reassignLead")
    public GenericDataDTO reassignTicket(@RequestParam(name = "leadMasterId") Long leadMasterId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Lead Reassigned from  with id : " + leadMasterId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return hierarchyService.reassignLead(leadMasterId);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Lead Reassigned from  with id : " + leadMasterId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @PutMapping("/" +
            "updateLeadAssignee")
    public GenericDataDTO updateLeadAssignee(@RequestBody LeadChangeAssigneePojo leadChangeAssigneePojo, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Lead Reassigned from  with id  " + leadChangeAssigneePojo.getLeadMasterId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return hierarchyService.updateLeadAssignee(leadChangeAssigneePojo);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Lead Reassigned from  with id  " + leadChangeAssigneePojo.getLeadMasterId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    @GetMapping("/reassignWorkflowGetStaffList")
    public GenericDataDTO reassignWorkflowFetchDataList(@RequestParam(name = "entityId") Integer entityId, @RequestParam(name = "eventName") String eventName, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ReAssign Staff from  with id : " + entityId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return hierarchyService.reassignWorkflowGetStaffList(entityId, eventName);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ReAssign Staff from  with id : " + entityId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/reassignWorkflow")
    public GenericDataDTO reassignWorkflow(@RequestParam(name = "entityId") Integer entityId, @RequestParam(name = "eventName") String eventName, @RequestParam(name = "assignToStaffId") Integer assignToStaffId, @RequestParam(name = "remark", required = false) String remark, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ReAssign Staff from  with id  " + entityId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return hierarchyService.reassignWorkflow(entityId, eventName, assignToStaffId, remark);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch ReAssign Staff from  with id  " + entityId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PutMapping("/approveLeadQuotation")
    public GenericDataDTO approveLeadQuotation(@RequestBody LeadQuotationWfDTO leadQuotationWfDTO,@RequestParam("mvnoId") Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            genericDataDTO.setResponseMessage("Assigned to next staff");
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setPageRecords(0);
            genericDataDTO.setCurrentPageNumber(1);
            genericDataDTO.setTotalPages(1);
            return hierarchyService.approveLeadQuotation(leadQuotationWfDTO, genericDataDTO,mvnoId);


        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
        }
        return genericDataDTO;
    }

    @PostMapping("/assignFromStaffListForLeadQuotation")
    public GenericDataDTO assignFromStaffListForLeadQuotation(@RequestParam(name = "nextAssignStaff") Integer nextAssignStaff, @RequestParam(name = "eventName") String eventName, @RequestBody LeadQuotationWfDTO leadQuotationWfDTO,@RequestParam("mvnoId") Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            genericDataDTO.setResponseMessage("Assigned to next staff");
            genericDataDTO.setData(hierarchyService.assignFromStaffListForLeadQuotation(nextAssignStaff, eventName, leadQuotationWfDTO,mvnoId));
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setPageRecords(0);
            genericDataDTO.setCurrentPageNumber(1);
            genericDataDTO.setTotalPages(1);


        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setTotalRecords(0);
        }
        return genericDataDTO;
    }

    @GetMapping("/getApprovalProgressForLeadQuotation")
    public GenericDataDTO getApprovalProgressForLeadQuotation(@RequestParam(name = "mvnoId") Integer mvnoId, @RequestParam(name = "buId") Long buId, @RequestParam(name = "nextTeamHierarchyMappingId") Integer nextTeamHierarchyMappingId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            genericDataDTO.setDataList(hierarchyService.getApprovalProgressForLeadQuotation(mvnoId, buId, nextTeamHierarchyMappingId));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Approval Progress" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Approval Progress" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_LEAD_ALL + "\",\"" + AclConstants.OPERATION_LEAD_EDIT + "\")")
    @GetMapping("/reassignLeadQuotation")
    public GenericDataDTO reassignLeadQuotation(@RequestParam(name = "leadQuotationId") Long leadQuotationId, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Lead Reassigned from  with id  " + leadQuotationId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return hierarchyService.reassignLeadQuotation(leadQuotationId);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Lead Reassigned from  with id  " + leadQuotationId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
    @PutMapping("/updateLeadQuotationAssignee")
    public GenericDataDTO updateLeadQuotationAssignee(@RequestBody LeadQuotationChangeAssigneePojo leadQuotationChangeAssigneePojo, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        try {
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Lead Reassigned from  with id  " + leadQuotationChangeAssigneePojo.getLeadQuotationDetailId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return hierarchyService.updateLeadQuotationAssignee(leadQuotationChangeAssigneePojo);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Lead Reassigned from  with id  " + leadQuotationChangeAssigneePojo.getLeadQuotationDetailId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @Override

    @PreAuthorize("validatePermission(\"" + MenuConstants.WORKFLOW_DELETE + "\")")
    public GenericDataDTO delete(@RequestBody HierarchyDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = super.delete(entityDTO, authentication, req);
            if (genericDataDTO.getData() != null) {
                Hierarchy hierarchy = hierarchyRepository.findById(entityDTO.getId()).orElse(null);
                if (hierarchy != null) {
                    createDataSharedService.deleteEntityDataForAllMicroService(hierarchy);
                    hierarchyService.sharedTeamHierarchyData(hierarchy, CommonConstants.OPERATION_DELETE);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }


    @GetMapping("/getCurrentTeamActionName")
    public GenericDataDTO getCurrentTeamActionName(@RequestParam(name = "custId") Integer custId, @RequestParam(name = "eventName") String eventName, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            genericDataDTO=hierarchyService.getCurrentTeamAction(custId,eventName);
            RESP_CODE = APIConstants.SUCCESS;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch current team action name " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch current team action name" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
}
