package com.adopt.apigw.modules.tickets.controller;
//
//
//import com.adopt.apigw.constants.Constants;
//import com.adopt.apigw.constants.MessageConstants;
//import com.adopt.apigw.constants.UrlConstants;
//import com.adopt.apigw.core.controller.ExBaseAbstractController;
//import com.adopt.apigw.core.controller.ExBaseAbstractController2;
//import com.adopt.apigw.core.dto.GenericDataDTO;
//import com.adopt.apigw.core.dto.PaginationRequestDTO;
//import com.adopt.apigw.core.utillity.log.ApplicationLogger;
//import com.adopt.apigw.exception.CustomValidationException;
//import com.adopt.apigw.modules.acl.constants.AclConstants;
//import com.adopt.apigw.modules.auditLog.service.AuditLogService;
//import com.adopt.apigw.modules.tickets.domain.TicketSubCategoryTatMapping;
//import com.adopt.apigw.modules.tickets.model.TicketReasonCategoryDTO;
//import com.adopt.apigw.modules.tickets.model.TicketReasonSubCategoryDTO;
//import com.adopt.apigw.modules.tickets.service.TicketReasonSubCategoryService;
//import com.adopt.apigw.spring.LoggedInUser;
//import com.adopt.apigw.utils.APIConstants;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;
//
//@RestController
//@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.TICKET_REASON_SUB_CATEGORY)
public class TicketReasonSubCategoryController  {
//
//    @Autowired
//    TicketReasonSubCategoryService ticketReasonSubCategoryService;
//
//    @Autowired
//    private AuditLogService auditLogService;
//
//
//
//    public TicketReasonSubCategoryController(TicketReasonSubCategoryService ticketReasonSubCategoryService) {
//        super(ticketReasonSubCategoryService);
//    }
//    private static final Logger logger = LoggerFactory.getLogger(TicketReasonSubCategoryController.class);
//    @Override
//    public String getModuleNameForLog() {
//        return "[TicketReasonSubCategoryController]";
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_REASON_SUB_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_CASE_REASON_SUB_CATEGORY_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody TicketReasonSubCategoryDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Create");
//        try {
////            entityDTO.setInwardNumber(CommonUtils.getResponse("","",null,5));
////            entityDTO.unusedQty=entityDTO.getQty();
////            entityDTO.setUsedQty(0L);
//
//            if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
//            {
//                logger.error("Unable to create new Ticket reasone sub catogory With name  "+entityDTO.getSubCategoryName()+"  :  request: { From : {}}; Response : {{};}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
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
//            boolean flag = ticketReasonSubCategoryService.duplicateVerifyAtSave(entityDTO.getSubCategoryName());
//            if (flag) {
//                TicketReasonSubCategoryDTO ticketReasonSubCategoryDTO = ticketReasonSubCategoryService.saveEntity(entityDTO);
//                genericDataDTO.setData(ticketReasonSubCategoryDTO);
//                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                genericDataDTO.setResponseMessage("Success");
//                logger.info("Ticket reasone sub catogory With name  "+entityDTO.getSubCategoryName()+" is created successfully:  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_PRODUCT_MANAGEMENT, AclConstants.OPERATION_PRODUCT_MANAGEMENT_ADD, req.getRemoteAddr(), null, ticketReasonSubCategoryDTO.getId(), ticketReasonSubCategoryDTO.getSubCategoryName());
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(MessageConstants.TICKET_REASON_SUB_CATAGORY_NAME_EXITS);
//                logger.error("Unable to create new Ticket reasone sub catogory With name  "+entityDTO.getSubCategoryName()+"  :  request: { From : {}}; Response : {{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            }
//        }
//        catch(CustomValidationException e)
//        {
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to create new Ticket reasone sub catogory With name  "+entityDTO.getSubCategoryName()+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
//        }
//        catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to create new Ticket reasone sub catogory With name  "+entityDTO.getSubCategoryName()+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_REASON_SUB_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_CASE_REASON_SUB_CATEGORY_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody TicketReasonSubCategoryDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        MDC.put("type", "Update");
//        try{
//            boolean flag = ticketReasonSubCategoryService.duplicateVerifyAtEdit(entityDTO.getSubCategoryName(), entityDTO.getId().intValue());
//            if (flag) {
//
//                genericDataDTO = super.update(entityDTO, result, authentication, req);
//                Iterable<TicketSubCategoryTatMapping> list= ticketReasonSubCategoryService.updateStatus(entityDTO);
//                //ticketReasonSubCategoryService.UpdateStatus(entityDTO);
//
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(MessageConstants.TICKET_REASON_CATAGORY_NAME_EXITS);
//                logger.error("Unable to Update ticket reasone sub catogory With name  "+entityDTO.getSubCategoryName()+"  :  request: { From : {}}; Response : {{}{};}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            }
//        } catch (Exception e){
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + e.getMessage(), e);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to Update ticket reasone sub catogory With name  "+entityDTO.getSubCategoryName()+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_REASON_SUB_CATEGORY_ALL + "\",\"" + AclConstants.OPERATION_CASE_REASON_SUB_CATEGORY_VIEW + "\")")
//    @PostMapping(value = "/searchAll")
//    public GenericDataDTO search(@RequestBody PaginationRequestDTO paginationRequestDTO) {
//        Integer RESP_CODE = APIConstants.FAIL;
//        MDC.put("type", "Fetch");
////        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO = ticketReasonSubCategoryService.search(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
//            logger.info("Fetching Ticket reason sub catogory With name  "+paginationRequestDTO.getFilterBy()+" is created successfully:  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to fetch ticket reason  sub catogory With name "+paginationRequestDTO.getFilterBy()+"  :  request: { From : {}}; Response : {{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ce.getStackTrace());
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to fetch ticket reason  sub catogory With name "+paginationRequestDTO.getFilterBy()+"  :  request: { From : {}}; Response : {{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
//
//        }
//        MDC.remove("type");
//        return genericDataDTO;
////    }
//
//    @GetMapping(value = "/getSubCategoryReasons")
//    public GenericDataDTO getSubCategoryReasons(@RequestParam("parentCategoryId") Long parentCategoryId) {
//        Integer RESP_CODE = APIConstants.FAIL;
//        MDC.put("type", "Fetch");
////        HashMap<String, Object> response = new HashMap<>();
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(ticketReasonSubCategoryService.getSubCategoryReasons(parentCategoryId));
//            logger.info("Ticket reason catogory With name  "+parentCategoryId+" :  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        } catch (CustomValidationException ce) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ce.getMessage(), ce);
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable fetch ticket reasone with category id  "+parentCategoryId+"  :  request: { From : {}}; Response : {{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ce.getStackTrace());
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + e.getMessage(), e);
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable fetch ticket reasone with category id  "+parentCategoryId+"  :  request: { From : {}}; Response : {{};Exception:{}}",  getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    public LoggedInUser getLoggedInUser() {
//        LoggedInUser loggedInUser = null;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
//            }
//        } catch (Exception e) {
//            //ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
//        }
//        return loggedInUser;
//    }

//    @PostMapping(value = "/delete")
//    public GenericDataDTO delete(@RequestBody TicketReasonSubCategoryDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Long reasone_cat_id=entityDTO.getId();
//        try {
//            Boolean flag=ticketReasonSubCategoryService.getUniqueSubCategory(reasone_cat_id);
//            if (!flag) {
//                super.delete(entityDTO, authentication, req);
//                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                logger.error("Category Deleted Successfully :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode());
//            }else{
//                genericDataDTO.setResponseCode(HttpStatus.NOT_MODIFIED.value());
//                genericDataDTO.setResponseMessage("Ticket reason Subcategory Already in use");
//                logger.error("Category already linked    :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode());
//            }
//        }catch (Exception e){
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to delete Sub reaseone    :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(),e.getStackTrace());
//        }
//        return genericDataDTO;
//    }
}
