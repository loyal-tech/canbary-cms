package com.adopt.apigw.modules.auditLog.controller;


import com.adopt.apigw.constants.MenuConstants;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.domain.AuditLogEntry;
import com.adopt.apigw.modules.auditLog.repository.AuditLogRepository;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.AuditLogConstants;
import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.model.AuditLogEntryDTO;
import com.adopt.apigw.modules.auditLog.model.AuditLogSearchRequestDTO;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.paymentGatewayMaster.service.PaymentGatewayService;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.PartnerService;

import java.util.List;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.AUDIT_LOG)
public class AuditLogController extends ExBaseAbstractController<AuditLogEntryDTO> {
    private static String MODULE = " [AuditLogController] ";
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private CustomersService customersService;
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private PaymentGatewayService pgService;
    @Autowired
    private ClientServiceSrv clientServiceSrv;

    public AuditLogController(AuditLogService service,
                              AuditLogRepository auditLogRepository) {
        super(service);
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public String getModuleNameForLog() {
        return " [AuditLogController] ";
    }


    private static final Logger logger= LoggerFactory.getLogger(AuditLogController.class);
    private final AuditLogRepository auditLogRepository;

    //   @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AUDIT_ALL + "\")")
    @PostMapping("/searchAudit")
    public GenericDataDTO getAuditLogByParam(@RequestBody AuditLogSearchRequestDTO reqDTO) {

        String SUBMODULE = getModuleNameForLog() + " [getAuditLogByParam()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        try {
            if (null == reqDTO) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Request!");
                logger.info("Unable to fetch Auditlog for "+reqDTO.getAuditFor()+" :  request: { Response : {{}}", APIConstants.NULL_VALUE);
                return genericDataDTO;
            }

            PaginationRequestDTO paginationRequestDTO = setDefaultPaginationValues(new PaginationRequestDTO());
            if (null == reqDTO.getPage())
                reqDTO.setPage(paginationRequestDTO.getPage());
            if (null == reqDTO.getPageSize())
                reqDTO.setPageSize(paginationRequestDTO.getPageSize());
            if (null == reqDTO.getSortOrder())
                reqDTO.setSortOrder(paginationRequestDTO.getSortOrder());
            if (null == reqDTO.getSortBy())
                reqDTO.setSortBy(paginationRequestDTO.getSortBy());
            if (null != reqDTO.getPageSize() && reqDTO.getPageSize() > MAX_PAGE_SIZE)
                reqDTO.setPageSize(MAX_PAGE_SIZE);
            logger.info("fetch Auditlog for "+reqDTO.getAuditFor()+" :  request: { Response : {{}}", APIConstants.SUCCESS);
            return auditLogService.getAuditHistoryByRequestParam(reqDTO);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);

            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to fetch Auditlog for"+reqDTO.getAuditFor()+":  request: { Response : {{}};Error :{} ;Exception:{};",HttpStatus.NOT_ACCEPTABLE,APIConstants.ERROR_MESSAGE,ex.getStackTrace());
           return genericDataDTO;
        }
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AUDIT_ALL + "\")")
    @GetMapping("/getListByAuditFor/{auditFor}")
    public GenericDataDTO getAuditForList(@PathVariable String auditFor) {
        String SUBMODULE = getModuleNameForLog() + " [getAuditForList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == auditFor) {
                genericDataDTO.setResponseMessage("Please Provide AuditFor!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.info("Unable to fetch Auditlog for "+auditFor+" :  request: { Response : {{}}", APIConstants.NULL_VALUE);
                return genericDataDTO;
            }
            if (null != auditFor) {
                if (auditFor.equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_CUSTOMER))
                    return GenericDataDTO.getGenericDataDTO(customersService.getCustomerListForAuditFor());
                if (auditFor.equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_EMPLOYEE))
                    return GenericDataDTO.getGenericDataDTO(staffUserService.getStaffListForAuditFor());
                if (auditFor.equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_PARTNER))
                    return GenericDataDTO.getGenericDataDTO(partnerService.getPartnerListForAuditFor());
                if (auditFor.equalsIgnoreCase(AuditLogConstants.AUDIT_FOR_PAYMENT_GATEWAY))
                    return GenericDataDTO.getGenericDataDTO(pgService.getPGListForAuditFor());
                logger.info("fetch Auditlog for "+auditFor+" :  request: { Response : {{}}", APIConstants.SUCCESS);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to fetch Auditlog for"+auditFor+":  request: { Response : {{}};Error :{} ;Exception:{};",HttpStatus.NOT_ACCEPTABLE,APIConstants.ERROR_MESSAGE,ex.getStackTrace());
        }
        return genericDataDTO;
    }

    @PostMapping("/getAuditList/{entity_id}")
//    @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_SHIFT_LOCATION + "\",\""
//            + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_SHIFT_LOCATION+ "\")")
    public GenericDataDTO getAuditForList(@PathVariable Long entity_id,@RequestBody PaginationRequestDTO paginationRequestDTO) {
        String SUBMODULE = getModuleNameForLog() + " [getAuditForList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            if (null == entity_id) {
                genericDataDTO.setResponseMessage("Please Provide entity reference id!");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                logger.info("Unable to fetch Auditlog for "+entity_id+" :  request: { Response : {{}}", APIConstants.NULL_VALUE);
                return genericDataDTO;
            }
           genericDataDTO = auditLogService.getAllEntitiesbyEntityrefId(entity_id, paginationRequestDTO);


        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to fetch Auditlog for"+entity_id+":  request: { Response : {{}};Error :{} ;Exception:{};",HttpStatus.NOT_ACCEPTABLE,APIConstants.ERROR_MESSAGE,ex.getStackTrace());
        }
        return genericDataDTO;
    }

    @PostMapping("/getSearchAudit/{entityIds}")
    public GenericDataDTO getSearchAudit(@PathVariable Long entityIds, @RequestBody PaginationRequestDTO paginationRequestDTO) {
        String SUBMODULE = getModuleNameForLog() + " [getSearchAudit()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (entityIds == null) {
                genericDataDTO.setResponseMessage("Please provide at least one entity reference ID.");
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                return genericDataDTO;
            }
            genericDataDTO = auditLogService.searchAuditLogs(entityIds, paginationRequestDTO);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to fetch audit log data.");
        }
        return genericDataDTO;
    }

}
