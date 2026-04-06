package com.adopt.apigw.modules.tickets.controller;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.adopt.apigw.spring.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.tickets.model.TicketReasonCategoryDTO;
import com.adopt.apigw.modules.tickets.service.TicketReasonCategoryService;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.TICKET_REASON_CATEGORY)
public class TicketReasonCategoryController extends ExBaseAbstractController2<TicketReasonCategoryDTO> {

    @Autowired
    TicketReasonCategoryService ticketReasonCategoryService;

    @Autowired
    private AuditLogService auditLogService;
    private static final Logger logger = LoggerFactory.getLogger(TicketReasonCategoryController.class);
    public TicketReasonCategoryController(TicketReasonCategoryService ticketReasonCategoryService) {
        super(ticketReasonCategoryService);
    }

    @Override
    public String getModuleNameForLog() {
        return "{TicketReasonSubCategoryController}";
    }

//    @Override
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_ADD + "\")")
//    public GenericDataDTO save(@Valid @RequestBody TicketReasonCategoryDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try {
////            entityDTO.setInwardNumber(CommonUtils.getResponse("","",null,5));
////            entityDTO.unusedQty=entityDTO.getQty();
////            entityDTO.setUsedQty(0L);
//            if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
//            {
//                logger.error("Unable to create new Ticket reasone catogory With name  "+entityDTO.getCategoryName()+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode());
//                throw new CustomValidationException(APIConstants.FAIL , Constants.AVOID_SAVE_MULTIPLE_BU , null);
//            }
//            if(getMvnoIdFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() == 1)
//            {
//                entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
//            }
//
//            if(getLoggedInUser().getLco())
//                entityDTO.setLcoId(getLoggedInUser().getPartnerId());
//            else
//                entityDTO.setLcoId(null);
//
//            boolean flag = ticketReasonCategoryService.duplicateVerifyAtSave(entityDTO.getCategoryName());
//            if (flag) {
//                TicketReasonCategoryDTO ticketReasonCategoryDTO = ticketReasonCategoryService.saveEntity(entityDTO);
//                genericDataDTO.setData(ticketReasonCategoryDTO);
//                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                genericDataDTO.setResponseMessage("Success");
//                logger.info("Ticket reasone catogory With name  "+entityDTO.getCategoryName()+" is created successfully:  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode());
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, ticketReasonCategoryDTO.getId(), ticketReasonCategoryDTO.getCategoryName());
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(MessageConstants.TICKET_REASON_CATAGORY_NAME_EXITS);
//                logger.error("Unable to create new Ticket reasone catogory With name  "+entityDTO.getCategoryName()+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode());
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to create new Ticket reasone catogory With name  "+entityDTO.getCategoryName()+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @Override
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_EDIT + "\")")
//    public GenericDataDTO update(@Valid @RequestBody TicketReasonCategoryDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Fetch");
//        try{
//            boolean flag = ticketReasonCategoryService.duplicateVerifyAtEdit(entityDTO.getCategoryName(), entityDTO.getId().intValue());
//            if (flag) {
//                genericDataDTO = super.update(entityDTO, result, authentication, req);
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(MessageConstants.TICKET_REASON_CATAGORY_NAME_EXITS);
//                logger.error("Unable toUpdate ticket Reasone category with name "+entityDTO.getCategoryName()+"  :  request: { From : {}}; Response : {{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode());
//            }
//        } catch (Exception e){
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + e.getMessage(), e);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("UnaUnable toUpdate ticket Reasone category with name "+entityDTO.getCategoryName()+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(),e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_VIEW + "\")")
//    @PostMapping(value = "/searchAll")
//    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO) {
//        MDC.put("type", "Fetch");
//        Integer RESP_CODE = APIConstants.FAIL;
////        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO=ticketReasonCategoryService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
//            logger.info("Fetching all ticketing reasone categories "+paginationRequestDTO.getFilterBy()+":  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode());
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to fetch ticketing reasone categories "+paginationRequestDTO.getFilterBy()+"  :  request: { From : {},}; Response : {{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(),ce.getStackTrace());
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to fetch ticketing reasone categories "+paginationRequestDTO.getFilterBy()+"  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_VIEW + "\")")
    @GetMapping(value = "/getReasonCategoryByCustomer")
    public GenericDataDTO getReasonCategoryByCustomer(@RequestParam(name = "customerId") Integer customerId) {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(ticketReasonCategoryService.getReasonCategoryByCustomer(customerId));
            logger.info("Fetching all cases by resasone category by customer id "+customerId+" :  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode());
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error("Unable to fetch ticket category by customer   "+customerId+"  :  request: { From : {}}; Response : {{}{};Exception:{}}",getModuleNameForLog(), genericDataDTO.getResponseCode(),ce.getStackTrace());
        } catch (Exception e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error("Unable to fetch ticket category by customer   "+customerId+"  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_TICKET_REASON_CATEGORY_VIEW + "\")")
//    @GetMapping(value = "/getAllActiveReasonCatgory")
//    public GenericDataDTO getAllActiveReasonCatgory() {
//        MDC.put("type", "Fetch");
//        Integer RESP_CODE = APIConstants.FAIL;
////        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(ticketReasonCategoryService.getAllActiveReasonCategory());
//            logger.info("Fetching all active reasone categories :  request: { From : {},}; Response : {Code:{},Message{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to get all reasone categories  :  request: { From : {}}; Response : {{}{};Exception:{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),ce.getStackTrace());
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to Unable to get all reasone categories :  request: { From : {},}; Response : {{}{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    @Override
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<TicketReasonCategoryDTO> list = ticketReasonCategoryService.getAllEntities(mvnoId);
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());

            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
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
            //ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }


    @GetMapping(value = "/getActiveServiceForSubscribers")
    public GenericDataDTO getAllActiveServicesForCustomers(@RequestParam(name = "customerId") Integer customerId) {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(ticketReasonCategoryService.getActiveServiceForSubscribers(customerId));
            logger.info("Fetching all active reasone categories :  request: { From : {},}; Response : {Code:{},Message{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error("Unable to get all reasone categories  :  request: { From : {}}; Response : {{}{};Exception:{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),ce.getStackTrace());
        } catch (Exception e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error("Unable to Unable to get all reasone categories :  request: { From : {},}; Response : {{}{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }


//    @PostMapping(value = "/getReasonCategoryByActiveServices")
//    public GenericDataDTO getReasonCategoryByActiveServices(@RequestBody List<Integer> serviceLists) {
//        Integer RESP_CODE = APIConstants.FAIL;
//        MDC.put("type", "Fetch");
////        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(ticketReasonCategoryService.getReasonCategoryByActiveServices(serviceLists));
//            logger.info("Fetching all cases by resasone category by service ids "+serviceLists.toString()+" :  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode());
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Fetching all cases by resasone category by service ids   "+serviceLists.toString()+"  :  request: { From : {}}; Response : {{}{};Exception:{}}",getModuleNameForLog(), genericDataDTO.getResponseCode(),ce.getStackTrace());
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to fetch ticket catagory by service ifs   "+serviceLists.toString()+"  :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @PostMapping(value = "/delete")
//    public GenericDataDTO delete(@RequestBody TicketReasonCategoryDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//     Long reasone_cat_id=entityDTO.getId();
//     try {
//         Boolean flag=ticketReasonCategoryService.getUniqueCategory(reasone_cat_id);
//         if (!flag) {
//             super.delete(entityDTO, authentication, req);
//             genericDataDTO.setResponseCode(HttpStatus.OK.value());
//             genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//             logger.error("Category Deleted Successfully :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode());
//         }else{
//             genericDataDTO.setResponseCode(HttpStatus.NOT_MODIFIED.value());
//             genericDataDTO.setResponseMessage("Ticket reason Category Already in use");
//             logger.error("Category already linked    :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode());
//         }
//     }catch (Exception e){
//         genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//         genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//         logger.error("Unable to delete Sub reaseone    :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getStackTrace());
//     }
//        return genericDataDTO;
//    }

    @GetMapping(value = "/getAllServiceForSubscribers")
    public GenericDataDTO getAllServicesForCustomers(@RequestParam(name = "customerId") Integer customerId) {
        MDC.put("type", "Fetch");
        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(ticketReasonCategoryService.getAllServiceForSubscribers(customerId));
            logger.info("Fetching all active reasone categories :  request: { From : {},}; Response : {Code:{},Message{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (CustomValidationException ce) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error("Unable to get all reasone categories  :  request: { From : {}}; Response : {{}{};Exception:{}}",getModuleNameForLog(),genericDataDTO.getResponseCode(),ce.getStackTrace());
        } catch (Exception e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error("Unable to Unable to get all reasone categories :  request: { From : {},}; Response : {{}{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

}
