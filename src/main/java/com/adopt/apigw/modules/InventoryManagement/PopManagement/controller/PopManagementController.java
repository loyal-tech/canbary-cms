package com.adopt.apigw.modules.InventoryManagement.PopManagement.controller;


import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.model.PopManagementDTO;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.service.PopManagementService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.PopManagementMessage;
import com.adopt.apigw.utils.APIConstants;
import com.netflix.discovery.converters.Auto;

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
import javax.validation.Valid;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.POP_MANAGEMENT)
public class PopManagementController extends ExBaseAbstractController<PopManagementDTO> {

    @Autowired
    AuditLogService auditLogService;
    
    @Autowired
    private MessageSender messageSender;

    @Autowired
    PopManagementService popManagementService;
    private static final Logger logger= LoggerFactory.getLogger(PopManagementController.class);

    public PopManagementController(PopManagementService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PopManagementController]";
    }

    //Get All PopManagement With Pagination
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POP_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_POP_MANAGEMENT_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO){
//        return super.getAll(requestDTO);
//    }

    //Save Pop Management
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POP_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_POP_MANAGEMENT_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody PopManagementDTO popManagementDTO, BindingResult result, Authentication authentication, HttpServletRequest request) throws Exception {
//        if(getMvnoIdFromCurrentStaff() != null) {
//            popManagementDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        MDC.put("type", "Create");
//        boolean flag = popManagementService.duplicateVerifyAtSave(popManagementDTO.getName());
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        if (flag){
//            dataDTO = super.save(popManagementDTO, result, authentication, request);
//            PopManagementDTO popManagementDTO1 = (PopManagementDTO) dataDTO.getData();
//            //send message
//            PopManagementMessage popManagementMessage = new PopManagementMessage(popManagementDTO1);
//            this.messageSender.send(popManagementMessage, RabbitMqConstants.QUEUE_APIGW_SEND_POP_MANAGEMENT);
//            logger.info("Pop created Successfully With name "+ popManagementDTO1.getName()+"  :  request: { From : {}, Request Url : {}}; Response : {{}}", request.getHeader("requestFrom"),request.getRequestURL(), APIConstants.SUCCESS);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_POP_MANAGEMENT,AclConstants.OPERATION_POP_MANAGEMENT_ADD, request.getRemoteAddr(), null, popManagementDTO1.getId(), popManagementDTO1.getName());
//        }
//        else{
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.POP_MANAGEMENT_NAME_EXITS);
//            logger.error("Unable to Create Pop Management with pop name " + popManagementDTO.getName() +" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"), request.getRequestURL(),HttpStatus.NOT_ACCEPTABLE);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }

    //Search Pop Management
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POP_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_POP_MANAGEMENT_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }

    // Get Entyty By Id
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POP_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_POP_MANAGEMENT_VIEW + "\")")
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = super.getEntityById(id, req);
//        PopManagementDTO popManagementDTO = (PopManagementDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH, AclConstants.OPERATION_BRANCH_VIEW, req.getRemoteAddr(), null, popManagementDTO.getId(), popManagementDTO.getName());
//        return dataDTO;
//    }

    //Get All without pagination
    @Override
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        genericDataDTO.setDataList(popManagementService.getAllWithoutPagination());
        return genericDataDTO;
    }

    //Update POP Management
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POP_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_POP_MANAGEMENT_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody PopManagementDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Update");
//        boolean flag = popManagementService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId());
//        if (flag) {
//            if(getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            dataDTO = super.update(entityDTO, result, authentication, req);
//            PopManagementDTO popManagementDTO = (PopManagementDTO) dataDTO.getData();
//            //send message
//            PopManagementMessage popManagementMessage = new PopManagementMessage(popManagementDTO);
//            this.messageSender.send(popManagementMessage, RabbitMqConstants.QUEUE_APIGW_SEND_POP_MANAGEMENT);
//            if(popManagementDTO != null) {
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_POP_MANAGEMENT, AclConstants.OPERATION_POP_MANAGEMENT_EDIT, req.getRemoteAddr(), null, entityDTO.getId(), entityDTO.getName());
//            }
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.POP_MANAGEMENT_NAME_EXITS);
//        }
//        return dataDTO;
//    }

    //Delete POP management
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_POP_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_POP_MANAGEMENT_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody PopManagementDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Delete");
//        try {
//            popManagementService.validatePOP(entityDTO);
//            boolean flag = popManagementService.deleteVerification(entityDTO.getId().intValue());
//            if (flag) {
//                dataDTO = super.delete(entityDTO, authentication, req);
//                PopManagementDTO popManagementDTO = (PopManagementDTO) dataDTO.getData();
//                if (popManagementDTO != null) {
//                    //send message
//                    PopManagementMessage popManagementMessage = new PopManagementMessage(popManagementDTO);
//                    popManagementMessage.setIsDeleted(true);
//                    this.messageSender.send(popManagementMessage, RabbitMqConstants.QUEUE_APIGW_SEND_POP_MANAGEMENT);
//                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_POP_MANAGEMENT, AclConstants.OPERATION_POP_MANAGEMENT_EDIT, req.getRemoteAddr(), null, entityDTO.getId(), entityDTO.getName());
//                }
//            } else {
//                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//                dataDTO.setResponseMessage(DeleteContant.POP_MANAGMENET_DELETE_EXIST);
//            }
//        } catch (CustomValidationException e) {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(e.getMessage());
//        }
//        return dataDTO;
//    }
}
