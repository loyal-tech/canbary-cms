package com.adopt.apigw.modules.Area.controller;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.MessageConstants;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.Area.mapper.AreaMapper;
import com.adopt.apigw.modules.Area.repository.AreaRepository;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.AreaMessage;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.modules.Area.model.AreaDTO;
import com.adopt.apigw.modules.Area.service.AreaService;
import com.adopt.apigw.modules.Pincode.model.PincodeDTO;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.AREA)
public class AreaController {  //extends ExBaseAbstractController<AreaDTO> {

    @Autowired
    private AreaRepository areaRepository;

//
//    @Autowired
//    AuditLogService auditLogService;
//    private static String MODULE = " [AreaController] ";
//    @Autowired
//    private AreaService areaService;
//
//
//    @Autowired
//    private MessageSender messageSender;
//
//
//    @Autowired
//    CreateDataSharedService createDataSharedService;
//
//
//    @Autowired
//    AreaMapper areaMapper;
//
//    private static final Logger logger= LoggerFactory.getLogger(AreaController.class);
//    public AreaController(AreaService service) {
//        super(service);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AREA_ALL + "\",\"" + AclConstants.OPERATION_AREA_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//        return super.getAll(requestDTO);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AREA_ALL + "\",\"" + AclConstants.OPERATION_AREA_VIEW + "\")")
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Fetch");
//
//        GenericDataDTO dataDTO = super.getEntityById(id, req);
//        AreaDTO areaDTO = (AreaDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_AREA,
//                AclConstants.OPERATION_AREA_VIEW, req.getRemoteAddr(), null, areaDTO.getId(), areaDTO.getName());
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        return super.getAllWithoutPagination();
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AREA_ALL + "\",\"" + AclConstants.OPERATION_AREA_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody AreaDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Save");
//
//        if(getMvnoIdFromCurrentStaff() != null) {
//    		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//    	}
//    	boolean flag = areaService.duplicateVerifyAtSave(entityDTO.getName(),entityDTO.getCountryId(),entityDTO.getStateId(),entityDTO.getCityId(),entityDTO.getPincodeId());
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        if (flag) {
//        	dataDTO = super.save(entityDTO, result, authentication, req);
//            AreaDTO areaDTO = (AreaDTO) dataDTO.getData();
//            //RabbitMq
//            AreaMessage areaMessage = new AreaMessage(areaDTO);
//            this.messageSender.send(areaMessage, RabbitMqConstants.QUEUE_AREA);
//            //
//            Area area = areaMapper.dtoToDomain(areaDTO,new CycleAvoidingMappingContext());
//            createDataSharedService.sendEntitySaveDataForAllMicroService(area);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_AREA,
//                    AclConstants.OPERATION_AREA_ADD, req.getRemoteAddr(), null, areaDTO.getId(), areaDTO.getName());
//            logger.info("Area Saved Suuccessfully With name "+areaDTO.getName()+" :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//
//        } else {
//        	dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
//            logger.error("Unable to Add Area"+entityDTO.getName()+":  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE,APIConstants.ERROR_MESSAGE);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AREA_ALL + "\",\"" + AclConstants.OPERATION_AREA_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AREA_ALL + "\",\"" + AclConstants.OPERATION_AREA_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody AreaDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//    	if(getMvnoIdFromCurrentStaff() != null) {
//    		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//    	}
//        String oldname=areaService.getById(entityDTO.getId()).getName();
//        MDC.put("type", "Update");
//    	GenericDataDTO dataDTO = new GenericDataDTO();
//    	//boolean flag = areaService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId());
//        boolean flag = areaService.duplicateVerifyAtEdit(entityDTO.getName(),entityDTO.getId(),entityDTO.getCountryId(),entityDTO.getStateId(),entityDTO.getCityId(),entityDTO.getPincodeId());
//        if (flag) {
//        	dataDTO = super.update(entityDTO, result, authentication, req);
//            AreaDTO areaDTO = (AreaDTO) dataDTO.getData();
//            //RabbitMq
//            AreaMessage areaMessage = new AreaMessage(areaDTO);
//            this.messageSender.send(areaMessage, RabbitMqConstants.QUEUE_AREA);
//            //
//            Area area = areaMapper.dtoToDomain(areaDTO,new CycleAvoidingMappingContext());
//            createDataSharedService.sendEntitySaveDataForAllMicroService(area);
//            if(areaDTO != null) {
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_AREA,
//                        AclConstants.OPERATION_AREA_EDIT, req.getRemoteAddr(), null, areaDTO.getId(), areaDTO.getName());
//            }
//          //  logger.info("Area with old name : " + oldname +  " is updated to : "+entityDTO.getName() +" updated Successfully; request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//        } else {
//        	dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
//      //      logger.info("Unable to Update Area with old name : " + oldname +  " is updated to : "+entityDTO.getName() +" ; request: { From : {}}; Response : {{}};", req.getHeader("requestFrom"),APIConstants.FAIL);
//        }
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_AREA_ALL + "\",\"" + AclConstants.OPERATION_AREA_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody AreaDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        //GenericDataDTO dataDTO = super.delete(entityDTO, authentication, req);
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        boolean flag = areaService.deleteVerification(entityDTO.getId().intValue());
//        if(flag){
//            dataDTO = super.delete(entityDTO, authentication, req);
//            AreaDTO areaDTO = (AreaDTO) dataDTO.getData();
//            //RabbitMq
//            AreaMessage areaMessage = new AreaMessage(areaDTO);
//            areaMessage.setIsDeleted(true);
//            this.messageSender.send(areaMessage, RabbitMqConstants.QUEUE_AREA);
//            //
//            Area area = areaMapper.dtoToDomain(areaDTO,new CycleAvoidingMappingContext());
//            area.setIsDeleted(true);
//            createDataSharedService.sendEntitySaveDataForAllMicroService(area);
//            if(areaDTO != null) {
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_AREA,
//                        AclConstants.OPERATION_AREA_DELETE, req.getRemoteAddr(), null, areaDTO.getId(), areaDTO.getName());
//                logger.info("Area With name "+ entityDTO.getName()+" Is Deleted Ducessfully  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//            }
//        } else {
//            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//            dataDTO.setResponseMessage(DeleteContant.AREA_DELETE_EXIST);
//
//            logger.error("Unable to Delete Area With name "+entityDTO.getName()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),DeleteContant.AREA_DELETE_EXIST,dataDTO.getResponseMessage());
//        }
//        //AreaDTO areaDTO = (AreaDTO) dataDTO.getData();
//        //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_AREA,
//          //      AclConstants.OPERATION_AREA_DELETE, req.getRemoteAddr(), null, areaDTO.getId(), areaDTO.getName());
//        return dataDTO;
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[AreaController]";
//    }

    @GetMapping(value = "/areaId/{areaName}")
    public Integer getWardIdByName(@PathVariable String areaName, HttpServletRequest req) throws Exception {
        return areaRepository.getWardIdByName(areaName);
    }

}
