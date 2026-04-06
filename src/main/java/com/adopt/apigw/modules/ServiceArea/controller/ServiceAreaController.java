package com.adopt.apigw.modules.ServiceArea.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.spring.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.utils.APIConstants;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SERVICE_AREA)
public class ServiceAreaController{ // extends ExBaseAbstractController<ServiceAreaDTO> {
//
//    public ServiceAreaController(ServiceAreaService service) {
//        super(service);
//    }
//
    private static String MODULE = " [ServiceAreaController] ";
    private static final Logger logger = LoggerFactory.getLogger(ServiceAreaController.class);
    @Autowired
    private ServiceAreaService serviceAreaService;

    @Autowired
    private Tracer tracer;

    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }
//    @Autowired
//    private StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
//
//    @Autowired
//    private AuditLogService auditLogService;
//    @Autowired
//    private MessageSender messageSender;
//
//    @Autowired
//    private CreateDataSharedService createDataSharedService;
//
//    @Autowired
//    ServiceAreaMapper serviceAreaMapper;
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[ServiceAreaController]";
//    }
//
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(serviceAreaService.getAllEntities());
//            genericDataDTO.setTotalRecords(serviceAreaService.getAllEntities().size());
//            logger.info("Fetching Sevice area list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to fetch data without pagination:  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @GetMapping("/all/byreasonconfig/{caseReasonId}")
//    public GenericDataDTO getAllServiceAreaForCaseReasonConfig(@PathVariable Long caseReasonId,HttpServletRequest req) {
//        String SUBMODULE = getModuleNameForLog() + " [getAllServiceAreaForCaseReasonConfig()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return GenericDataDTO.getGenericDataDTO(serviceAreaService.getAllServiceAreaForCaseReasonConfig(caseReasonId));
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//
//        }
//        return genericDataDTO;
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody ServiceAreaDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Delete");
//        try {
//            serviceAreaService.validateServiceAreaInventory(entityDTO);
//            boolean flag = serviceAreaService.deleteVerification(entityDTO.getId().intValue());
//            if (flag) {
//                dataDTO = super.delete(entityDTO, authentication, req);
//                ServiceAreaDTO serviceArea = (ServiceAreaDTO) dataDTO.getData();
//                if (serviceArea != null) {
//                    //send message
//                    ServiceareaMessage serviceAreaMessage = new ServiceareaMessage();
//                    serviceAreaMessage.setId(serviceArea.getId());
//                    serviceAreaMessage.setName(serviceArea.getName());
//                    serviceAreaMessage.setStatus(serviceArea.getStatus());
//                    serviceAreaMessage.setIsDeleted(true);
//                    serviceAreaMessage.setMvnoId(serviceArea.getMvnoId());
//                    serviceAreaMessage.setLatitude(serviceArea.getLatitude());
//                    serviceAreaMessage.setLongitude(serviceArea.getLongitude());
//                    serviceAreaMessage.setAreaId(serviceArea.getAreaid());
//                    this.messageSender.send(serviceAreaMessage, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA);
//                    ServiceArea deletedServiceArea = serviceAreaMapper.dtoToDomain(serviceArea,new CycleAvoidingMappingContext());
//                    createDataSharedService.deleteEntityDataForAllMicroService(deletedServiceArea);
//                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_SERVICE_AREA, AclConstants.OPERATION_SERVICE_AREA_DELETE, req.getRemoteAddr(), null, serviceArea.getId(), serviceArea.getName());
//                }
//                dataDTO.setResponseMessage("Successfully Deleted");
//                logger.info("Service Area With name " + serviceArea.getName() + " Deleted  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//
//            } else {
//                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//                dataDTO.setResponseMessage(DeleteContant.SERVICE_AREA_DELETE_EXIST);
//
//                logger.error("Unable to Delete Service Area With name " + entityDTO.getName() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), HttpStatus.METHOD_NOT_ALLOWED.value(), DeleteContant.SERVICE_AREA_DELETE_EXIST);
//
//            }
//        } catch (CustomValidationException e) {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(e.getMessage());
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody ServiceAreaDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Update");
//        boolean flag = serviceAreaService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId().intValue());
//        ServiceArea oldname=serviceAreaService.getByID(entityDTO.getId());
//        if (flag) {
//            if (getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            String updatedValues = CommonUtils.getUpdatedDiff(oldname,entityDTO);
//            dataDTO = super.update(entityDTO, result, authentication, req);
//            ServiceAreaDTO serviceArea = (ServiceAreaDTO) dataDTO.getData();
//            if(serviceArea != null)
//            {
//            	 //send message
//            	  ServiceareaMessage serviceAreaMessage = new ServiceareaMessage();
//                  serviceAreaMessage.setId(serviceArea.getId());
//                  serviceAreaMessage.setName(serviceArea.getName());
//                  serviceAreaMessage.setStatus(serviceArea.getStatus());
//                  serviceAreaMessage.setIsDeleted(serviceArea.getIsDeleted());
//                  serviceAreaMessage.setMvnoId(serviceArea.getMvnoId());
//                  serviceAreaMessage.setLatitude(serviceArea.getLatitude());
//                  serviceAreaMessage.setLongitude(serviceArea.getLongitude());
//                  serviceAreaMessage.setAreaId(serviceArea.getAreaid());
//                  this.messageSender.send(serviceAreaMessage, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA);
//                  ServiceArea updatedServiceArea = serviceAreaMapper.dtoToDomain(serviceArea,new CycleAvoidingMappingContext());
//                  createDataSharedService.updateEntityDataForAllMicroService(updatedServiceArea);
//            	auditLogService.addAuditEntry(AclConstants.ACL_CLASS_SERVICE_AREA, AclConstants.OPERATION_SERVICE_AREA_EDIT, req.getRemoteAddr(), null, serviceArea.getId(), serviceArea.getName());
//            }
//            dataDTO.setResponseMessage("Successfully Updated");
//            logger.info("Sevice Area With oldname "+updatedValues+" :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//        } else {
//            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            dataDTO.setResponseMessage(MessageConstants.SERVICE_AREA_NAME_EXITS);
//            logger.error("Unable to Update Service Area With oldname "+oldname+":  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(),MessageConstants.SERVICE_AREA_NAME_EXITS);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody ServiceAreaDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Create");
//        boolean flag = serviceAreaService.duplicateVerifyAtSave(entityDTO.getName());
//        if (flag) {
//            if (getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            dataDTO = super.save(entityDTO, result, authentication, req);
//            ServiceAreaDTO serviceArea = (ServiceAreaDTO) dataDTO.getData();
//
//            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingList = new ArrayList<>();
//
//            StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
//            staffUserServiceAreaMapping.setServiceId(serviceArea.getId().intValue());
//            staffUserServiceAreaMapping.setStaffId(serviceAreaService.getLoggedInUserId());
//            staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
//            staffUserServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
//            staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping);
//
//            if (serviceAreaService.getLoggedInUserId() != 1) {
//                StaffUserServiceAreaMapping staffUserServiceAreaMapping1 = new StaffUserServiceAreaMapping();
//                staffUserServiceAreaMapping1 = new StaffUserServiceAreaMapping();
//                staffUserServiceAreaMapping1.setServiceId(serviceArea.getId().intValue());
//                staffUserServiceAreaMapping1.setStaffId(1);
//                staffUserServiceAreaMapping1.setCreatedOn(LocalDateTime.now());
//                staffUserServiceAreaMapping1.setLastmodifiedOn(LocalDateTime.now());
//                staffUserServiceAreaMappingList.add(staffUserServiceAreaMapping1);
//            }
//            staffUserServiceAreaMappingRepository.saveAll(staffUserServiceAreaMappingList);
//
//            //send message
//            ServiceareaMessage serviceAreaMessage = new ServiceareaMessage();
//            serviceAreaMessage.setId(serviceArea.getId());
//            serviceAreaMessage.setName(serviceArea.getName());
//            serviceAreaMessage.setStatus(serviceArea.getStatus());
//            serviceAreaMessage.setIsDeleted(serviceArea.getIsDeleted());
//            serviceAreaMessage.setMvnoId(serviceArea.getMvnoId());
//            serviceAreaMessage.setLatitude(serviceArea.getLatitude());
//            serviceAreaMessage.setLongitude(serviceArea.getLongitude());
//            serviceAreaMessage.setAreaId(serviceArea.getAreaid());
//            this.messageSender.send(serviceAreaMessage, RabbitMqConstants.QUEUE_APIGW_SEND_SERVICE_AREA);
//            ServiceArea serviceAreaEntity = new ServiceArea();
//            serviceAreaEntity = serviceAreaMapper.dtoToDomain(serviceArea,new CycleAvoidingMappingContext());
//            createDataSharedService.sendEntitySaveDataForAllMicroService(serviceAreaEntity);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_SERVICE_AREA, AclConstants.OPERATION_SERVICE_AREA_ADD, req.getRemoteAddr(), null, serviceArea.getId(), serviceArea.getName());
//            dataDTO.setResponseMessage("Successfully Created");
//            logger.info("Service Area is created with name "+ entityDTO.getName()+"  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//        } else {
//            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            dataDTO.setResponseMessage(MessageConstants.SERVICE_AREA_NAME_EXITS);
//            logger.error("Unable to Create Service Area With name " +entityDTO.getName() +" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(),MessageConstants.SERVICE_AREA_NAME_EXITS);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = super.getEntityById(id, req);
//        MDC.put("type", "Fetch");
//        ServiceAreaDTO serviceArea = (ServiceAreaDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_SERVICE_AREA, AclConstants.OPERATION_SERVICE_AREA_VIEW, req.getRemoteAddr(), null, serviceArea.getId(), serviceArea.getName());
//        logger.info("Service  Search with Name "+serviceArea.getName()+"  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//        MDC.remove("type");
//        return dataDTO;
//
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//        return super.getAll(requestDTO);
//    }
//
//    // Get All Service Area List By UserStaff
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SERVICE_AREA_ALL + "\",\"" + AclConstants.OPERATION_SERVICE_AREA_VIEW + "\")")
//    @GetMapping("/getAllServiceAreaByStaff")
//    public GenericDataDTO getAllServiceAreaByStaff(){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(serviceAreaService.getAllServiceAreaByStaffId());
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
//    //Get StaffIds by ServiceAreas
//    @GetMapping("/getStaffUserByServiceArea")
//    public GenericDataDTO getStaffUserByServiceArea() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
//            genericDataDTO.setDataList(staffUserService
//                    .getStaffUserByServiceArea());
//            genericDataDTO.setTotalRecords(staffUserService
//                    .getStaffUserByServiceArea().size());
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
//    @GetMapping("/viewStaffUserByServiceArea")
//    public GenericDataDTO viewStaffUserByServiceArea() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
//            genericDataDTO.setDataList(staffUserService
//                    .viewStaffUserByServiceArea());
//            genericDataDTO.setTotalRecords(staffUserService
//                    .viewStaffUserByServiceArea().size());
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
//
//    @GetMapping("/getPincodefromCity")
//    public GenericDataDTO getpincodefromcity(@RequestParam("id") Integer id) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(serviceAreaService.getPincodefromcity(id));
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
//
//@PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_SERVICE_MANAGEMENT + "\",\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_SERVICE_MANAGEMENT + "\",\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_SERVICE_MANAGEMENT + "\",\""
//        + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_SERVICE_MANAGEMENT+ "\")")
    @PostMapping("/getAllServicesByServiceAreaId")
    public GenericDataDTO getAllServicesByServiceAreaId(@RequestBody List<Integer> serviceAreaId, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setDataList(serviceAreaService.getAllServicebyServiceAreaId(serviceAreaId,false,mvnoId));
            RESP_CODE = APIConstants.NOT_FOUND;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Service area list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error("ServiceAreaController" + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Service area list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }



    @PostMapping("/getAllServicesByServiceAreaIdForCWSC")
    public GenericDataDTO getAllServicesByServiceAreaIdForCWSC(@RequestBody List<Integer> serviceAreaId, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put("traceId",traceContext.traceIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setDataList(serviceAreaService.getAllServicebyServiceAreaId(serviceAreaId,true,mvnoId));
            RESP_CODE = APIConstants.NOT_FOUND;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Service area list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error("ServiceAreaController" + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Service area list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
//    @GetMapping("/serviceAreaListWhereBranchIsNotBind")
//    public GenericDataDTO getAllserviceAreaListWhereBranchIsNotBind(){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(serviceAreaService.serviceAreaIdListWhereBranchIsNotBind());
//            genericDataDTO.setTotalRecords(serviceAreaService.serviceAreaIdListWhereBranchIsNotBind().size());
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



    @GetMapping("/getAllRemainingPlanForServiceArea/{serviceAreaId}")
    public GenericDataDTO getAllRemainingPlanForServiceArea(@PathVariable Integer serviceAreaId, HttpServletRequest req){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        org.slf4j.MDC.put("type", "Fetch");
        org.slf4j.MDC.put("userName", getLoggedInUser().getUsername());
        org.slf4j.MDC.put("traceId",traceContext.traceIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setDataList(serviceAreaService.getAllRemainingPlansForServiceArea(serviceAreaId));
            RESP_CODE = APIConstants.NOT_FOUND;
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Service area list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error("ServiceAreaController" + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Service area list" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }
}
