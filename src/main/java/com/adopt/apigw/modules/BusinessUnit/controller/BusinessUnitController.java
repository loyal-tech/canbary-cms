package com.adopt.apigw.modules.BusinessUnit.controller;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.StaffUserBusinessUnitMapping;
import com.adopt.apigw.model.common.StaffUserServiceAreaMapping;
import com.adopt.apigw.modules.Area.model.AreaDTO;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.BusinessUnit.mapper.BusinessUnitMapper;
import com.adopt.apigw.modules.BusinessUnit.model.BusinessUnitDTO;
import com.adopt.apigw.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.adopt.apigw.modules.BusinessUnit.service.BusinessUnitService;
import com.adopt.apigw.modules.InvestmentCode.service.InvestmentCodeService;
import com.adopt.apigw.modules.Region.model.RegionDTO;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.BranchMessage;
import com.adopt.apigw.rabbitMq.message.BusinessUnitMessage;
import com.adopt.apigw.repository.common.StaffUserBusinessUnitMappingRepository;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BUSINESS_UNIT)
public class BusinessUnitController{ //extends ExBaseAbstractController<BusinessUnitDTO> {
//    @Autowired
//    AuditLogService auditLogService;
//    private static String MODULE = " [BusinessUnitController] ";
//    @Autowired
//    BusinessUnitService businessUnitService;
//
//    @Autowired
//    private StaffUserBusinessUnitMappingRepository staffUserBusinessUnitMappingRepository;
//
//    @Autowired
//    private MessageSender messageSender;
//
//    @Autowired
//    BusinessUnitRepository businessUnitRepository;
//
//    @Autowired
//    InvestmentCodeService investmentCodeService;
//
//    @Autowired
//    BusinessUnitMapper businessUnitMapper;
//
//    @Autowired
//    CreateDataSharedService createDataSharedService;
//
//    public BusinessUnitController(BusinessUnitService service) {
//        super(service);
//    }
//    private static final Logger logger= LoggerFactory.getLogger(BusinessUnitController.class);
//    @Override
//    public String getModuleNameForLog() {
//        return "[BusinessUnitController]";
//    }
//
//    //Get All Business Unit with Pagination
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//        return super.getAll(requestDTO);
//    }
//
//    //Save Business Unit
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody BusinessUnitDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if(getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        MDC.put("type", "Create");
//        boolean flag = businessUnitService.duplicateVerifyAtSave(entityDTO.getBuname());
//        boolean flagforUcode = businessUnitService.duplicateVerifyAtSaveUcode(entityDTO.getBucode());
//        GenericDataDTO dataDTO = new GenericDataDTO();
//
//        if (flag && flagforUcode) {
//
//            dataDTO = super.save(entityDTO, result, authentication, req);
//            BusinessUnitDTO businessUnitDTO = (BusinessUnitDTO) dataDTO.getData();
//            dataDTO.setResponseMessage("Successfully Created");
//            logger.info("BusinessUnit created Successfully With name "+ entityDTO.getBuname()+"  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//
//            //send message
//            BusinessUnitMessage message = new BusinessUnitMessage();
//            message.setId(businessUnitDTO.getId());
//            message.setBuname(businessUnitDTO.getBuname());
//            message.setBucode(businessUnitDTO.getBucode());
//            message.setStatus(businessUnitDTO.getStatus());
//            message.setIsDeleted(businessUnitDTO.getIsDeleted());
//            message.setMvnoId(businessUnitDTO.getMvnoId());
//            message.setPlanBindingType(businessUnitDTO.getPlanBindingType());
//
//            messageSender.send(message,RabbitMqConstants.QUEUE_BUSINESS_UNIT,RabbitMqConstants.QUEUE_BUSINESS_UNIT_SEND_TASK_MGMT_SUCCESS,RabbitMqConstants.QUEUE_BUSINESS_UNIT_KPI);
//            messageSender.send(message,RabbitMqConstants.QUEUE_BUSINESS_UNIT_SUCCESS);
//            BusinessUnit businessUnit = businessUnitMapper.dtoToDomain(businessUnitDTO, new CycleAvoidingMappingContext());
//            createDataSharedService.sendEntitySaveDataForAllMicroService(businessUnit);
//
//            //send message
////            BusinessUnitMessage message = new BusinessUnitMessage();
////            message.setId(businessUnitDTO.getId());
////            message.setBuname(businessUnitDTO.getBuname());
////            message.setBucode(businessUnitDTO.getBucode());
////            message.setStatus(businessUnitDTO.getStatus());
////            message.setIsDeleted(businessUnitDTO.getIsDeleted());
////            message.setMvnoId(businessUnitDTO.getMvnoId());
////            messageSender.send(message, RabbitMqConstants.QUEUE_BUSINESS_UNIT);
////            List<StaffUserBusinessUnitMapping> staffUserBusinessUnitMappingList = new ArrayList<>();
////
////            StaffUserBusinessUnitMapping staffUserBusinessUnitMapping = new StaffUserBusinessUnitMapping();
////            //staffUserBusinessUnitMapping.setBusinessunitId(businessUnitDTO.getId().intValue());
////            staffUserBusinessUnitMapping.setStaffId(businessUnitService.getLoggedInUserId());
////            staffUserBusinessUnitMapping.setCreatedOn(LocalDateTime.now());
////            staffUserBusinessUnitMapping.setLastmodifiedOn(LocalDateTime.now());
////            staffUserBusinessUnitMappingList.add(staffUserBusinessUnitMapping);
//
////            if (businessUnitService.getLoggedInUserId() != 1) {
////                StaffUserBusinessUnitMapping staffUserBusinessUnitMapping1 = new StaffUserBusinessUnitMapping();
////                staffUserBusinessUnitMapping1 = new StaffUserBusinessUnitMapping();
////                staffUserBusinessUnitMapping1.setBusinessunitId(businessUnitDTO.getId().intValue());
////                staffUserBusinessUnitMapping1.setStaffId(1);
////                staffUserBusinessUnitMapping1.setCreatedOn(LocalDateTime.now());
////                staffUserBusinessUnitMapping1.setLastmodifiedOn(LocalDateTime.now());
////                staffUserBusinessUnitMappingList.add(staffUserBusinessUnitMapping);
////            }
////            staffUserBusinessUnitMappingRepository.saveAll(staffUserBusinessUnitMappingList);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_UNIT,
//                    AclConstants.OPERATION_BUSINESS_UNIT_ADD, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getBuname());
//        } else if(!flagforUcode){
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.BUSINESS_UNIT_CODE_EXITS);
//            logger.error("Unable to Create Business with busness Code " +entityDTO.getBucode()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(),MessageConstants.BUSINESS_UNIT_CODE_EXITS);
//        }else{
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.BUSINESS_UNIT_NAME_EXITS);
//            logger.error("Unable to Create Business with busness name " +entityDTO.getBuname()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    //Update Business Unit
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody BusinessUnitDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Update");
//        if(getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        String oldname=businessUnitService.getById(entityDTO.getId()).getBuname();
//        String oldID=businessUnitService.getById(entityDTO.getId()).getBucode();
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        boolean flag = businessUnitService.duplicateVerifyAtEdit(entityDTO.getBuname(), entityDTO.getId());
//        boolean flagforUcode = businessUnitService.duplicateVerifyUcodeAtEdit(entityDTO.getBucode(), entityDTO.getId());
//
//        if (flag && flagforUcode) {
//            dataDTO = super.update(entityDTO, result, authentication, req);
//            BusinessUnitDTO businessUnitDTO = (BusinessUnitDTO) dataDTO.getData();
//            if(businessUnitDTO != null) {
//            	 //send message
//                BusinessUnitMessage message = new BusinessUnitMessage();
//                message.setId(businessUnitDTO.getId());
//                message.setBuname(businessUnitDTO.getBuname());
//                message.setBucode(businessUnitDTO.getBucode());
//                message.setStatus(businessUnitDTO.getStatus());
//                message.setIsDeleted(businessUnitDTO.getIsDeleted());
//                message.setMvnoId(businessUnitDTO.getMvnoId());
//                message.setPlanBindingType(businessUnitDTO.getPlanBindingType());
//
//                messageSender.send(message, RabbitMqConstants.QUEUE_BUSINESS_UNIT,RabbitMqConstants.QUEUE_BUSINESS_UNIT_SEND_TASK_MGMT_SUCCESS,RabbitMqConstants.QUEUE_BUSINESS_UNIT_KPI);
//                BusinessUnit businessUnit = businessUnitMapper.dtoToDomain(businessUnitDTO, new CycleAvoidingMappingContext());
//                createDataSharedService.updateEntityDataForAllMicroService(businessUnit);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_UNIT,
//                        AclConstants.OPERATION_BUSINESS_UNIT_EDIT, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getBuname());
//                dataDTO.setResponseMessage("Successfully Updated");
//                //     logger.info("Business With old BuID "+oldID+" with new Id "+entityDTO.getBucode()+"  and Oldname "+oldname+" to new name "+entityDTO.getBuname()+" :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//            }
//        } else if(!flagforUcode){
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.BUSINESS_UNIT_CODE_EXITS);
//
//            logger.error("Unable to Update business With BUCode "+ entityDTO.getBucode()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(),MessageConstants.BUSINESS_UNIT_CODE_EXITS);
//        }else{
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.BUSINESS_UNIT_NAME_EXITS);
//            logger.error("Unable to Update business With BuName "+ entityDTO.getBuname()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(),MessageConstants.BUSINESS_UNIT_CODE_EXITS);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    //Delete Business Unit
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody BusinessUnitDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Delete");
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        boolean flag = businessUnitService.deleteVerification(entityDTO.getId().intValue());
//        boolean flag2 = businessUnitService.deleteVerificationForSubBusinessunit(entityDTO.getId().intValue());
//        if(flag && flag2){
//            dataDTO = super.delete(entityDTO, authentication, req);
//            BusinessUnitDTO businessUnitDTO = (BusinessUnitDTO) dataDTO.getData();
//
//            if(businessUnitDTO != null) {
//            	 //send message
//                businessUnitService.deleteIcNameBumapping(businessUnitDTO.getId());
//                BusinessUnitMessage message = new BusinessUnitMessage();
//                message.setId(businessUnitDTO.getId());
//                message.setBuname(businessUnitDTO.getBuname());
//                message.setBucode(businessUnitDTO.getBucode());
//                message.setStatus(businessUnitDTO.getStatus());
//                message.setIsDeleted(true);
//                message.setMvnoId(businessUnitDTO.getMvnoId());
//                messageSender.send(message, RabbitMqConstants.QUEUE_BUSINESS_UNIT, RabbitMqConstants.QUEUE_BUSINESS_UNIT_SEND_TASK_MGMT_SUCCESS,RabbitMqConstants.QUEUE_BUSINESS_UNIT_KPI);
//                BusinessUnit businessUnit = businessUnitMapper.dtoToDomain(businessUnitDTO, new CycleAvoidingMappingContext());
//                businessUnit.setIsDeleted(true);
//                createDataSharedService.deleteEntityDataForAllMicroService(businessUnit);
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_UNIT,
//                        AclConstants.OPERATION_BUSINESS_UNIT_DELETE, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getBuname());
//                dataDTO.setResponseMessage("Successfully Deleted");
//                logger.info("Business Uint With name "+ entityDTO.getBuname()+" is Deleted Successsfully :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//            }
//        } else {
//            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//            dataDTO.setResponseMessage(DeleteContant.BUSINESS_UNIT_EXIST);
//            logger.error("Unable to Delete Business Unit With name "+entityDTO.getBuname()+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.METHOD_NOT_ALLOWED.value(),DeleteContant.BUSINESS_UNIT_EXIST);
//        }
//        MDC.remove("tyqpe");
//        return dataDTO;
//    }
//
//    //Search Business Unit
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    //Get Entity By Id
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_VIEW + "\")")
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Fetch");
//        GenericDataDTO dataDTO = super.getEntityById(id, req);
//        BusinessUnitDTO businessUnitDTO = (BusinessUnitDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_UNIT,
//                AclConstants.OPERATION_BUSINESS_UNIT_VIEW, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getBuname());
//       // logger.info("Country Search Successfull  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),RESP_CODE);
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    //Get all without Pagination
////    @Override
////    public GenericDataDTO getAllWithoutPagination() {
////        return super.getAllWithoutPagination();
////    }
//
//    @GetMapping("/BusinessUnit/{id}")
//    public HashMap<String, Object> getBusinessUnitById(@PathVariable Long id, HttpServletRequest req) throws Exception {
//        Integer RESP_CODE=APIConstants.FAIL;
//        MDC.put("type", "Fetch");
//        HashMap<String, Object> response = new HashMap<>();
//        try{
//            if (id==null){
//                RESP_CODE=APIConstants.NOT_FOUND;
//                logger.error("Unable to search charge with name  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), RESP_CODE, response);
//            }else {
//                Optional<BusinessUnit> businessUnit = businessUnitRepository.findById(id);
//                if (businessUnit.isPresent()){
//                    List<String> icnames=investmentCodeService.getIcnameListByBuId(id);
//                    BusinessUnitDTO pojo=businessUnitService.convertBumodeltoPojo(businessUnit);
//                    pojo.setIcnames(icnames);
//                    response.put("BuById",pojo);
//                    RESP_CODE = APIConstants.SUCCESS;
//                }else {
//                    RESP_CODE = APIConstants.NOT_FOUND;
//                    logger.error("Unable to search charge with name request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), RESP_CODE, response);
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        MDC.remove("type");
//        return  response;
//    }
//
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BUSINESS_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BUSINESS_UNIT_VIEW + "\")")
//    @GetMapping(value = "/getBUFromStaff")
//    public GenericDataDTO getBUFromStaff(HttpServletRequest req) throws Exception {
//        MDC.put("type", "Fetch");
//        GenericDataDTO dataDTO = businessUnitService.getBUFromStaff();
////        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_UNIT,
////                AclConstants.OPERATION_BUSINESS_UNIT_VIEW, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getBuname());
//        // logger.info("Country Search Successfull  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),RESP_CODE);
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @GetMapping(value = "/getBUFromCurrentStaff")
//    public GenericDataDTO getBUFromCurrentStaff(HttpServletRequest req) throws Exception {
//        MDC.put("type", "Fetch");
//        GenericDataDTO dataDTO = businessUnitService.getBUFromCurrentStaff();
//        return dataDTO;
//    }
//
//    @Override
//    public GenericDataDTO getAllWithoutPagination () {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        try {
//            List<BusinessUnitDTO> list = businessUnitService.getAllEntities().stream().filter(x -> !x.getIsDeleted() && x.getStatus().equalsIgnoreCase("ACTIVE")).collect(Collectors.toList());
//            genericDataDTO.setDataList(list);
//            genericDataDTO.setTotalRecords(list.size());
//            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage("Failed to load data");
//            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//
//        }
//
//        return genericDataDTO;
//
//    }
}
