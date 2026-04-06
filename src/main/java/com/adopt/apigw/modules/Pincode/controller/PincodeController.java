package com.adopt.apigw.modules.Pincode.controller;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.Pincode.mapper.PincodeMapper;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.PincodeMessage;
import com.adopt.apigw.service.postpaid.CityService;
import com.netflix.discovery.converters.Auto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.Pincode.model.PincodeDTO;
import com.adopt.apigw.modules.Pincode.service.PincodeService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.utils.APIConstants;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PINCODE)
public class PincodeController {  //extends ExBaseAbstractController<PincodeDTO> {
//    private static String MODULE = " [PincodeController] ";
//    @Autowired
//    private AuditLogService auditLogService;
//
//    @Autowired
//    private PincodeService pincodeService;
//
//    @Autowired
//    private MessageSender messageSender;
//
//
//    @Autowired
//    private CreateDataSharedService createDataSharedService;
//
//    @Autowired
//    private PincodeMapper pincodeMapper;
//
//    private static  final Logger logger= LoggerFactory.getLogger(PincodeController.class);
//    public PincodeController(PincodeService service) {
//        super(service);
//    }
////
//    @GetMapping(value = "/getDetailsByPin/{pincode}")
//    public GenericDataDTO getDetailsByPin(@PathVariable String pincode,HttpServletRequest req) {
//        String SUBMODULE = " [getDetailsByPin()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Integer RESP_CODE = APIConstants.FAIL;
//        MDC.put("type", "Fetch");
//
//        try {
//            if (null == pincode) {
//                genericDataDTO.setResponseMessage("Please provide pincode!");
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                RESP_CODE=APIConstants.FAIL;
//                logger.error("Unable Create Pincode   "+pincode+" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),RESP_CODE,genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            }
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(pincodeService.getDetailsByPin(pincode));
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setPageRecords(1);
//            genericDataDTO.setTotalPages(1);
//            genericDataDTO.setCurrentPageNumber(1);
//        } catch (Exception ex) {
//            if (ex instanceof RuntimeException) {
//                ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//                genericDataDTO.setResponseMessage(ex.getMessage());
//                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//                logger.error("Unable to search for pincode "+pincode+" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),RESP_CODE,genericDataDTO.getResponseMessage(),ex.getMessage());
//                return genericDataDTO;
//            }
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            logger.error("Unable to search :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),RESP_CODE,genericDataDTO.getResponseMessage(),ex.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @GetMapping("/search")
//    public GenericDataDTO getPincodeBySearch(@RequestParam(name = "s", defaultValue = "") String s1,HttpServletRequest req) {
//        String SUBMODULE = getModuleNameForLog() + " [getPincodeBySearch()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//
//        try {
////            if ("".equals(s1)) {
////                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
////                genericDataDTO.setResponseMessage("Please provide search criteria!");
////                return genericDataDTO;
////            }
//
//            genericDataDTO = GenericDataDTO.getGenericDataDTO(pincodeService.getAllPincodeBySearch(s1));
//            if (null != genericDataDTO) {
//
//                if (genericDataDTO.getDataList().isEmpty())
//                {
//                    genericDataDTO = new GenericDataDTO();
//                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
//                    genericDataDTO.setResponseMessage("No Record Found!");
//                    genericDataDTO.setDataList(new ArrayList<>());
//                    genericDataDTO.setTotalRecords(0);
//                    genericDataDTO.setPageRecords(0);
//                    genericDataDTO.setCurrentPageNumber(1);
//                    genericDataDTO.setTotalPages(1);
//                    logger.info("No data found  :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.NULL_VALUE);
//
//
//                }
//                logger.info("Pincode Is "+ s1+" found :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//
//                return genericDataDTO;
//            }
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//
//            return genericDataDTO;
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PINCODE_ALL + "\",\"" + AclConstants.OPERATION_PINCODE_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//
//        return super.getAll(requestDTO);
//    }
//
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = super.getEntityById(id, req);
//        PincodeDTO picodeDTO = (PincodeDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PINCODE,
//                AclConstants.OPERATION_PINCODE_VIEW, req.getRemoteAddr(), null, picodeDTO.getPincodeid(), picodeDTO.getPincode());
//        return dataDTO;
//
//    }
//
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        return super.getAllWithoutPagination();
//    }
//
//    @Deprecated
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PINCODE_ALL + "\",\"" + AclConstants.OPERATION_PINCODE_VIEW + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PINCODE_ALL + "\",\"" + AclConstants.OPERATION_PINCODE_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody PincodeDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        MDC.put("type", "Fetch");
//
//        if(getMvnoIdFromCurrentStaff() != null) {
//    		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//    	}
//    	boolean flag = pincodeService.duplicateVerifyAtSaveWithPincodeAndCityID(entityDTO.getPincode(),entityDTO.getCityId());
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        if (flag) {
//        	dataDTO = super.save(entityDTO, result, authentication, req);
//            PincodeDTO pincodeDTO = (PincodeDTO) dataDTO.getData();
//
//            Pincode pincode = pincodeMapper.dtoToDomain(pincodeDTO,new CycleAvoidingMappingContext());
//            createDataSharedService.sendEntitySaveDataForAllMicroService(pincode);
//
//            //RabbitMq
//            PincodeMessage pincodeMessage = new PincodeMessage(pincodeDTO);
//            this.messageSender.send(pincodeMessage, RabbitMqConstants.QUEUE_PINCODE);
//            //
//            RESP_CODE=APIConstants.SUCCESS;
//            logger.info("Municipality with code "+entityDTO.getPincode()+"is Added Successfully  :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),RESP_CODE);
//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PINCODE,
//                    AclConstants.OPERATION_PINCODE_ADD, req.getRemoteAddr(), null, pincodeDTO.getPincodeid(), pincodeDTO.getPincode());
//        } else {
//            RESP_CODE=APIConstants.FAIL;
//        	dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
//            logger.error("Unable to search :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),req.getRequestURL(),RESP_CODE,MessageConstants.PINCODE_EXITS);
//        }
//    MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PINCODE_ALL + "\",\"" + AclConstants.OPERATION_PINCODE_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody PincodeDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//    	if(getMvnoIdFromCurrentStaff() != null) {
//    		entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//    	}
//        MDC.put("type", "Update");
//        String oldPINCode= pincodeService.getPincode(entityDTO.getPincodeid());
//        GenericDataDTO dataDTO = new GenericDataDTO();
//    	//boolean flag = pincodeService.duplicateVerifyAtEdit(entityDTO.getPincode(), entityDTO.getPincodeid());
//        boolean flag = pincodeService.duplicateVerifyAtEdit(entityDTO.getPincode(),entityDTO.getPincodeid(),entityDTO.getCityId());
//        if (flag) {
//            dataDTO = super.update(entityDTO, result, authentication, req);
//            PincodeDTO pincodeDTO = (PincodeDTO) dataDTO.getData();
//            //RabbitMq
//            PincodeMessage pincodeMessage = new PincodeMessage(pincodeDTO);
//            this.messageSender.send(pincodeMessage, RabbitMqConstants.QUEUE_PINCODE);
//            //
//            Pincode pincode = pincodeMapper.dtoToDomain(pincodeDTO,new CycleAvoidingMappingContext());
//            createDataSharedService.updateEntityDataForAllMicroService(pincode);
//            if (pincodeDTO != null){
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PINCODE,
//                        AclConstants.OPERATION_PINCODE_EDIT, req.getRemoteAddr(), null, pincodeDTO.getPincodeid(), pincodeDTO.getPincode());
//          //      logger.info("Pincode With Old "+oldPINCode+" value "+ entityDTO.getPincode()+" is Updated  :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//            }
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
//            logger.error("Unable to Update pincode "+entityDTO.getPincode()+" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),req.getRequestURL(),HttpStatus.NOT_ACCEPTABLE.value(),MessageConstants.PINCODE_EXITS);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PINCODE_ALL + "\",\"" + AclConstants.OPERATION_PINCODE_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody PincodeDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        MDC.put("type", "Delete");
//
//        boolean flag = pincodeService.deleteVerification(entityDTO.getPincodeid().intValue());
//        if (flag) {
//            dataDTO = super.delete(entityDTO, authentication, req);
//            PincodeDTO pincodeDTO = (PincodeDTO) dataDTO.getData();
//            //RabbitMq
//           // PincodeMessage pincodeMessage = new PincodeMessage(pincodeDTO);
//           // this.messageSender.send(pincodeMessage, RabbitMqConstants.QUEUE_PINCODE);
//            //
//            Pincode pincode = pincodeMapper.dtoToDomain(pincodeDTO,new CycleAvoidingMappingContext());
//            pincode.setIsDeleted(true);
//            createDataSharedService.updateEntityDataForAllMicroService(pincode);
//            if (pincodeDTO != null) {
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PINCODE,
//                        AclConstants.OPERATION_PINCODE_DELETE, req.getRemoteAddr(), null, pincodeDTO.getPincodeid(), pincodeDTO.getPincode());
//
//                logger.info("PINCode "+entityDTO.getPincode()+" deleted Successfully  :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//            }
//
//            PincodeMessage pincodeMessage = new PincodeMessage(pincodeDTO);
//            pincodeMessage.setIsDeleted(true);
//            this.messageSender.send(pincodeMessage, RabbitMqConstants.QUEUE_PINCODE);
//
//
//        } else {
//            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//            dataDTO.setResponseMessage(DeleteContant.PIN_CODE_DELETE_EXIST);
//            logger.error("Unable to Delete Pincode "+entityDTO.getPincode()+" :  request: { From : {}, Request Url : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),req.getRequestURL(),HttpStatus.METHOD_NOT_ALLOWED.value(),DeleteContant.PIN_CODE_DELETE_EXIST);
//        }
//    MDC.remove("type");
//        return dataDTO;
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[PincodeController]";
//    }
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PINCODE_ALL + "\",\"" + AclConstants.OPERATION_PINCODE_VIEW + "\")")
//    @PostMapping("/getPincodeListByServiceId")
//    public GenericDataDTO getPincodeListByServiceId(@Valid @RequestBody List<Long> serviceAreaIds, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Fetch");
//        String SUBMODULE = getModuleNameForLog() + " [getPincodeListByServiceId()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try{
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setDataList(pincodeService.getPincodeListByServiceId(serviceAreaIds));
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//        }
//        return genericDataDTO;
//    }
}
