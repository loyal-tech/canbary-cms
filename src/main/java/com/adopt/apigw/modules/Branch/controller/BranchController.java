package com.adopt.apigw.modules.Branch.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.BranchServiceAreaMapping;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.mapper.BranchMapper;
import com.adopt.apigw.modules.BranchService.model.BranchServiceMappingEntity;
import com.adopt.apigw.modules.BranchService.repository.BranchServiceMappingRepository;
import com.adopt.apigw.rabbitMq.message.BranchMessageIn;
import com.adopt.apigw.repository.common.BranchServiceAreaMappingRepository;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.modules.Branch.model.BranchDTO;
import com.adopt.apigw.modules.Branch.service.BranchService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.BranchMessage;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BRANCH_MANAGEMENT)
public class BranchController{ // extends ExBaseAbstractController<BranchDTO> {

//
//    private static final Logger logger = LoggerFactory.getLogger(BranchController.class);
//    @Autowired
//    AuditLogService auditLogService;
//    private static String MODULE = " [BranchController] ";
//    @Autowired
//    BranchService branchService;
//
//    @Autowired
//    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;
//
//    @Autowired
//    private MessageSender messageSender;
//
//    @Autowired
//    private BranchServiceMappingRepository branchServiceMappingRepository;
//
//    @Autowired
//    private CreateDataSharedService createDataSharedService;
//
//    @Autowired
//    private BranchMapper branchMapper;
//    public BranchController(BranchService service) {
//        super(service);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[BranchController]";
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BRANCH_ALL + "\",\"" + AclConstants.OPERATION_BRANCH_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//        return super.getAll(requestDTO);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BRANCH_ALL + "\",\"" + AclConstants.OPERATION_BRANCH_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody BranchDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        String SUBMODULE = getModuleNameForLog() + " [save()] ";
//        GenericDataDTO dataDTO = new GenericDataDTO();
//
//
//            try {
//                if(entityDTO.getSharing_percentage()!=null) {
//                    double  number = entityDTO.getSharing_percentage();
//
//                    if (number % 1 != 0) {
//                        dataDTO.setResponseMessage("Fraction Value not allowed");
//                        dataDTO = new GenericDataDTO();
//                        dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//                        dataDTO.setResponseMessage(MessageConstants.Fraction_Value);
//                    }
//
//                    if (entityDTO.getRevenue_sharing() && (entityDTO.getSharing_percentage() < 0 || entityDTO.getSharing_percentage() > 100)) {
//                        dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        //dataDTO.setResponseMessage("Revenue sharing percentage must be less than 100 or greater than 0");
//                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Revenue sharing percentage must be less than 100 or greater than 0", null);
//                    }
//                }
//                    if (getMvnoIdFromCurrentStaff() != null) {
//                        entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//                    }
//                    boolean flag = branchService.duplicateVerifyAtSave(entityDTO.getName());
//
//                    if (flag) {
//                        dataDTO = super.save(entityDTO, result, authentication, req);
//                        BranchDTO branchDTO = (BranchDTO) dataDTO.getData();
//
//                        //send message
//                        BranchMessage branchMessage = new BranchMessage(branchDTO.getId(), branchDTO.getName(), branchDTO.getStatus(), branchDTO.getIsDeleted());
//                        this.messageSender.send(branchMessage, RabbitMqConstants.QUEUE_APIGW_SEND_BRANCH,RabbitMqConstants.QUEUE_APIGW_BRANCH_KPI);
//                        BranchMessageIn branchMessageIn = new BranchMessageIn(branchDTO.getId(), branchDTO.getName(), branchDTO.getStatus(),branchDTO.getBranch_code(), branchDTO.getIsDeleted(), branchDTO.getMvnoId());
//                        this.messageSender.send(branchMessageIn, RabbitMqConstants.QUEUE_BRANCH_SUCCESS);
//
//                        //Common micoroservice data share call
//                        Branch branch =branchMapper.dtoToDomain(branchDTO,new CycleAvoidingMappingContext());
//                        createDataSharedService.sendEntitySaveDataForAllMicroService(branch);
//                        logger.info("Branch with name " + entityDTO.getName() + "  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//                        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH,
//                                AclConstants.OPERATION_BRANCH_ADD, req.getRemoteAddr(), null, branchDTO.getId(), branchDTO.getName());
//                    } else {
//                        dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                        dataDTO.setResponseMessage(MessageConstants.BRANCH_NAME_EXITS);
//                        logger.error("Unable to Create branch with name " + entityDTO.getName() + " :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), dataDTO.getResponseCode(), dataDTO.getResponseMessage());
//                    }
//
//                    return dataDTO;
//                } catch(Exception ex){
//                    dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//                    dataDTO.setResponseMessage(ex.getMessage());
//                }
//
//         return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BRANCH_ALL + "\",\"" + AclConstants.OPERATION_BRANCH_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody BranchDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//
//        if (getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        String Oldname = branchService.getById(entityDTO.getId()).getName();
//        boolean flag = branchService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId());
//        if (entityDTO.getRevenue_sharing()) {
//            List<BranchServiceMappingEntity> branchServiceMappingEntityList = entityDTO.getBranchServiceMappingEntityList();
//            branchServiceMappingEntityList.stream().forEach(branchServiceMapping -> {
//
//            if ((branchServiceMapping.getRevenueShareper() < 0) || (branchServiceMapping.getRevenueShareper()) > 100) {
//               throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),"Revenue sharing percentage must be less than or equal to 100",null);
////                dataDTO.setResponseMessage("Revenue sharing percentage must be less than or equal to 100");
//            }
//            });
////            else {
//                if (flag) {
//                    BranchDTO branchDTO = entityDTO;
//                    dataDTO = super.update(entityDTO, result, authentication, req);
//                    if (branchDTO != null) {
//                        //send message
//                        BranchMessage branchMessage = new BranchMessage(branchDTO.getId(), branchDTO.getName(), branchDTO.getStatus(), branchDTO.getIsDeleted());
//                        this.messageSender.send(branchMessage, RabbitMqConstants.QUEUE_APIGW_SEND_BRANCH,RabbitMqConstants.QUEUE_APIGW_BRANCH_KPI);
//                        //Common micoroservice data share call
//                        Branch branch =branchMapper.dtoToDomain(branchDTO,new CycleAvoidingMappingContext());
//                        createDataSharedService.updateEntityDataForAllMicroService(branch);
//                        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH,
//                                AclConstants.OPERATION_BRANCH_EDIT, req.getRemoteAddr(), null, branchDTO.getId(), branchDTO.getName());
//                    }
//                    //  logger.info("Branch With oldname "+Oldname+" is changed to "+entityDTO.getName()+" :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),dataDTO.getResponseCode());
//                } else {
//                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    dataDTO.setResponseMessage(MessageConstants.BRANCH_NAME_EXITS);
//                    //     logger.error("Unable to Update branch with oldname" +Oldname+" to "+entityDTO.getName()+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(),MessageConstants.BRANCH_NAME_EXITS);
//                }
////            }
//
//        }
//        else {
//            if (flag) {
//                BranchDTO branchDTO = entityDTO;
//                dataDTO = super.update(entityDTO, result, authentication, req);
//                if (branchDTO != null) {
//                    //send message
//                    BranchMessage branchMessage = new BranchMessage(branchDTO.getId(), branchDTO.getName(), branchDTO.getStatus(), branchDTO.getIsDeleted());
//                    this.messageSender.send(branchMessage, RabbitMqConstants.QUEUE_APIGW_SEND_BRANCH,RabbitMqConstants.QUEUE_APIGW_BRANCH_KPI);
//                    Branch branch =branchMapper.dtoToDomain(branchDTO,new CycleAvoidingMappingContext());
//                    createDataSharedService.updateEntityDataForAllMicroService(branch);
//                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH,
//                            AclConstants.OPERATION_BRANCH_EDIT, req.getRemoteAddr(), null, branchDTO.getId(), branchDTO.getName());
//                }else {
//                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    dataDTO.setResponseMessage(MessageConstants.BRANCH_NAME_EXITS);
//                    //     logger.error("Unable to Update branch with oldname" +Oldname+" to "+entityDTO.getName()+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(),MessageConstants.BRANCH_NAME_EXITS);
//                }
//            }else{
//                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                dataDTO.setResponseMessage(MessageConstants.BRANCH_NAME_EXITS);
//            }
//
//        }
//            return dataDTO;
//    }
//
//
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BRANCH_ALL + "\",\"" + AclConstants.OPERATION_BRANCH_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody BranchDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        boolean flag = branchService.deleteVerification(entityDTO.getId().intValue());
//        boolean flag2 = branchService.deleteVerificationForRegion(entityDTO.getId().intValue());
//        if(flag && flag2) {
//            dataDTO = super.delete(entityDTO, authentication, req);
//            List<BranchServiceAreaMapping> branchServiceAreaMappingList =branchServiceAreaMappingRepository.findAllByBranchId(entityDTO.getId().intValue());
//            branchServiceAreaMappingRepository.deleteAll(branchServiceAreaMappingList);
//            List<BranchServiceMappingEntity> branchServiceMappingEntityList = branchServiceMappingRepository.findAllByBranchId(entityDTO.getId());
//         //   branchServiceMappingRepository.deleteAll(branchServiceMappingEntityList);
//            BranchDTO branchDTO = (BranchDTO) dataDTO.getData();
//            if(branchDTO != null) {
//            	 //send message
//                BranchMessage branchMessage = new BranchMessage(branchDTO.getId(),branchDTO.getName(),branchDTO.getStatus(),true);
//                this.messageSender.send(branchMessage, RabbitMqConstants.QUEUE_APIGW_SEND_BRANCH,RabbitMqConstants.QUEUE_APIGW_BRANCH_KPI);
//
//                //Common micoroservice data share call
//                Branch branch =branchMapper.dtoToDomain(branchDTO,new CycleAvoidingMappingContext());
//                branch.setIsDeleted(true);
//                createDataSharedService.deleteEntityDataForAllMicroService(branch);
//
//                logger.info("Banch With id "+branchDTO.getName()+"  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),branchDTO.getStatus());
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH,
//                        AclConstants.OPERATION_BRANCH_DELETE, req.getRemoteAddr(), null, branchDTO.getId(), branchDTO.getName());
//            }
//            if (!branchServiceMappingEntityList.isEmpty()) {
//                branchServiceMappingEntityList.stream().forEach(branchServiceMapping -> branchServiceMapping.setIsDeleted(true));
//                branchServiceMappingRepository.saveAll(branchServiceMappingEntityList);
//            }
//        } else {
//            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//            dataDTO.setResponseMessage(DeleteContant.BRANCH_DELETE_EXIST);
//            logger.error("Unable to Delete Branch With Name "+entityDTO.getName()+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.METHOD_NOT_ALLOWED.value(),DeleteContant.BRANCH_DELETE_EXIST);
//        }
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BRANCH_ALL + "\",\"" + AclConstants.OPERATION_BRANCH_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BRANCH_ALL + "\",\"" + AclConstants.OPERATION_BRANCH_VIEW + "\")")
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = super.getEntityById(id, req);
//        BranchDTO branchDTO = (BranchDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BRANCH,
//                AclConstants.OPERATION_BRANCH_VIEW, req.getRemoteAddr(), null, branchDTO.getId(), branchDTO.getName());
//        return dataDTO;
//    }
//
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        return super.getAllWithoutPagination();
//    }
//
//    //Get BranchIds by ServiceAreas
//    @GetMapping("/getBranchByServiceArea")
//    public GenericDataDTO getBranchByServiceArea() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            BranchService branchService = SpringContext.getBean(BranchService.class);
//            genericDataDTO.setDataList(branchService
//                    .getBranchByServiceArea());
//            genericDataDTO.setTotalRecords(branchService
//                    .getBranchByServiceArea().size());
//            logger.info("Fetching All Warehouse Without pagination  :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        }
//        catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Fetch all without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
//        }
//        return genericDataDTO;
//    }
//
//    // Get All Service Area List By UserStaff
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @GetMapping("/getAllServiceAreaByBranchId/{branchId}")
//    public GenericDataDTO getAllServiceAreaByBranchId(@PathVariable Integer branchId, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            BranchService branchService = SpringContext.getBean(BranchService.class);
//            genericDataDTO.setDataList(branchService.getAllServiceAreaByBranchId(branchId));
//            logger.info("Fetching Service area list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Service area list :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }
//
//
//
//
//    // Get All Service Area List By UserStaff
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @PostMapping("/getAllBranchesByServiceAreaId")
//    public GenericDataDTO getAllBranachesByServiceAreaID(@RequestBody List<Integer> serviceAreaId, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            BranchService branchService = SpringContext.getBean(BranchService.class);
//            genericDataDTO.setDataList(branchService.getAllBranchesByServieAreaId(serviceAreaId));
//            logger.info("Fetching Branch list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Branch list :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @GetMapping("/getAllBranchesByServiceArea/{serviceAreaId}")
//    public GenericDataDTO getAllBranachesByServiceArea(@PathVariable Integer serviceAreaId, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            BranchService branchService = SpringContext.getBean(BranchService.class);
//            genericDataDTO.setDataList(branchService.getAllBranchesByServieAreaId(Arrays.asList(serviceAreaId)));
//            logger.info("Fetching Branch list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Branch list :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }
//
//    @PostMapping("/getAllBranchesByforPartnerServiceAreaId")
//    public GenericDataDTO getAllBranachesforPartnerByServiceAreaID(@RequestBody List<Integer> serviceAreaId, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            BranchService branchService = SpringContext.getBean(BranchService.class);
//            genericDataDTO.setDataList(branchService.getAllBranachesforPartnerByServiceAreaID(serviceAreaId));
//            logger.info("Fetching Branch list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Branch list :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }

}
