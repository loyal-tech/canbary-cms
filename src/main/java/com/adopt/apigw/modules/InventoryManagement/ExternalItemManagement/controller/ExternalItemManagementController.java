package com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.controller;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.service.ExBaseService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingDto;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain.ExternalItemManagement;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.model.ExternalItemManagementDTO;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.repository.ExternalItemManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.service.ExternalItemManagementService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.EXTERNAL_ITEM_MANAGEMENT)
public class ExternalItemManagementController extends ExBaseAbstractController<ExternalItemManagementDTO> {
    @Autowired
    AuditLogService auditLogService;

    @Autowired
    ExternalItemManagementService externalItemManagementService;

    private static final Logger logger= LoggerFactory.getLogger(ExternalItemManagementController.class);

    public ExternalItemManagementController(ExternalItemManagementService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ExternalItemManagementController]";
    }

    //Save
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody ExternalItemManagementDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Create");
//        try {
//            if(entityDTO.getOwnershipType() == null)
//                entityDTO.setOwnershipType("Subisu Owner");
//            ExternalItemManagementDTO externalItemManagementDTO = externalItemManagementService.saveEntity(entityDTO);
//            genericDataDTO.setData(externalItemManagementDTO);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_EXTERNAL_ITEM_MANAGEMENT, AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ADD, req.getRemoteAddr(), null, externalItemManagementDTO.getId(), externalItemManagementDTO.getExternalItemGroupNumber().toString());
//            logger.info("External Item Group Management controller successfully created  :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(), APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to search :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),HttpStatus.NOT_ACCEPTABLE, APIConstants.FAIL,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    //Get External Item Group Details By Product And ServiceAreaId
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getExternalItemGroupDetailsByProductAndServiceAreaId")
//    public GenericDataDTO getExternalItemGroupDetailsByProductAndServiceAreaId(@RequestParam(name = "productId")Long productId, @RequestParam(name = "serviceAreaId")Long serviceAreaId, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(externalItemManagementService.getExtrenalItemDetailsByProductAndServiceAreaId(productId, serviceAreaId));
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
//            logger.info("Get External Item Group Management Details By product and warehouse :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to get External Item Group Management product by product and warehouse :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    //Update
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody ExternalItemManagementDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Update");
//        try {
//            ExternalItemManagementDTO existingExternalItem = externalItemManagementService.getEntityById(entityDTO.getId());
//            ExternalItemManagementDTO externalItemManagementDTO = externalItemManagementService.updateEntity(entityDTO);
//            genericDataDTO.setData(externalItemManagementDTO);
//            logger.info("External Item Group Management with old number "+ entityDTO.getExternalItemGroupNumber()+" is updated to "+ entityDTO.getExternalItemGroupNumber() +" is Successfully updated :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(ce.getMessage(), ce);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to Update External Item Group Management With  "+ entityDTO.getExternalItemGroupNumber() +" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.EXPECTATION_FAILED,ce.getMessage());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to Update External Item Group Management With  "+ entityDTO.getExternalItemGroupNumber() +" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    //Get All External Item Group By Product And Staff
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_VIEW + "\")")
//    @GetMapping("/getAllExternalItemGroupByProductAndStaff")
//    public GenericDataDTO getAllExternalItemByProductAndStaff(@RequestParam(name = "productId") Long productId, @RequestParam(name = "ownerId") Long ownerId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(externalItemManagementService.getAllExternalItemByProductAndStaff(productId, ownerId));
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//
//    }

    //Delete
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_DELETE + "\")")
//    @DeleteMapping("/delete/{id}")
//    public GenericDataDTO delete(@PathVariable Long id, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Delete");
//        ExternalItemManagementDTO entityDTO = externalItemManagementService.getEntityById(id);
//        boolean flag = externalItemManagementService.deleteVerification(entityDTO.getId().intValue());
//        if (flag) {
//            genericDataDTO = super.delete(entityDTO, authentication, req);
//            ExternalItemManagementDTO externalItemManagementDTO = (ExternalItemManagementDTO) genericDataDTO.getData();
//            if(externalItemManagementDTO != null)
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_EXTERNAL_ITEM_MANAGEMENT,
//                        AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_DELETE, req.getRemoteAddr(), null, externalItemManagementDTO.getId(), externalItemManagementDTO.getExternalItemGroupNumber());
//            logger.info("Deleting External Item Group Management With External Number "+entityDTO.getExternalItemGroupNumber()+" is successfull :   Response : {{}{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        } else {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(DeleteContant.EXTERNAL_ITEM_NUMBER_DELETE_EXIST);
//            logger.error("Unable to Delete External Item Group Management With External Number "+entityDTO.getExternalItemGroupNumber()+"  : Response : {{}{};}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_EDIT + "\")")
//    @PutMapping("/externalItemApproval")
//    public GenericDataDTO externalItemGroupApproval(@Valid @RequestBody ExternalItemManagementDTO externalItemManagementDTO, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setData(externalItemManagementService.saveExternalItemGroupApproval(externalItemManagementDTO.getId(), externalItemManagementDTO.getApprovalStatus(), externalItemManagementDTO.getApprovalRemark()));
////            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, productId, wareHouseId.toString());
//            logger.info("Get External Item Group Management Details By product and warehouse :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to get External Item Group Management product by product and warehouse :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.FAIL,HttpStatus.NOT_ACCEPTABLE,ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    //Search
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }

    //Get All PopManagement With Pagination
    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, @RequestParam Integer mvnoId){
        return super.getAll(requestDTO, req,mvnoId);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_EDIT + "\")")
//    @GetMapping("/getAllItemByExternalItemBaseOnStatus")
//    public GenericDataDTO getAllExternalItemBaseOnStatus(@RequestParam(name = "ownerId") Long ownerId,@RequestParam(name = "ownershipType") String ownershipType){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setData(externalItemManagementService.getAllExtenralItemBaseOnStatus(ownerId,ownershipType));
//         } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//         }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_EXTERNAL_ITEM_MANAGEMENT_EDIT + "\")")
//    @PostMapping("/getAllCustomerBasedOnLoginStaffServiceArea")
//    public GenericDataDTO getAllCustomerBasedOnLoginStaffServiceArea( @RequestBody List<Long> serviceAreaId){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setData(externalItemManagementService.getAllCustomerBasedOnServiceArea(serviceAreaId));
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }



}
