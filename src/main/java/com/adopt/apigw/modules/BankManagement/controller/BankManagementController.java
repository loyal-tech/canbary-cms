 package com.adopt.apigw.modules.BankManagement.controller;

 import com.adopt.apigw.constants.DeleteContant;
 import com.adopt.apigw.constants.MessageConstants;
 import com.adopt.apigw.constants.UrlConstants;
 import com.adopt.apigw.core.controller.ExBaseAbstractController;
 import com.adopt.apigw.core.dto.*;
 import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
 import com.adopt.apigw.core.utillity.log.ApplicationLogger;
 import com.adopt.apigw.modules.BankManagement.domain.BankManagement;
 import com.adopt.apigw.modules.BankManagement.mapper.BankManagementMapper;
 import com.adopt.apigw.modules.BankManagement.model.BankManagementDTO;
 import com.adopt.apigw.modules.BankManagement.repository.BankManagementRepository;
 import com.adopt.apigw.modules.BankManagement.service.BankManagementService;
 import com.adopt.apigw.modules.BusinessUnit.controller.BusinessUnitController;
 import com.adopt.apigw.modules.acl.constants.AclConstants;
 import com.adopt.apigw.modules.auditLog.service.AuditLogService;
 import com.adopt.apigw.utils.APIConstants;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.slf4j.MDC;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.http.HttpStatus;
 import org.springframework.security.access.prepost.PreAuthorize;
 import org.springframework.security.core.Authentication;
 import org.springframework.util.StringUtils;
 import org.springframework.validation.BindingResult;
 import org.springframework.web.bind.annotation.*;

 import javax.print.attribute.standard.MediaSize;
 import javax.servlet.http.HttpServletRequest;
 import javax.validation.Valid;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Optional;
 import java.util.stream.Collectors;

 @RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BANK_MANAGEMENT)
public class BankManagementController{ //  extends ExBaseAbstractController<BankManagementDTO> {
//     private static final Logger logger= LoggerFactory.getLogger(BankManagementController.class);
//     @Autowired
//     AuditLogService auditLogService;
//     private static String MODULE = " [BankManagementController] ";
//     @Autowired
//     BankManagementService bankManagementService;
//
//     @Autowired
//     private BankManagementRepository bankManagementRepository;
//
//     @Autowired
//     private BankManagementMapper bankManagementMapper;
//
//
//     public BankManagementController(BankManagementService service) {
//         super(service);
//     }
//
//     @Override
//     public String getModuleNameForLog() {
//         return null;
//     }
//
//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_VIEW + "\")")
//     @Override
//     public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//         return super.getAll(requestDTO);
//     }
//
//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_VIEW + "\")")
//     @Override
//     public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//         GenericDataDTO dataDTO = super.getEntityById(id, req);
//         BankManagementDTO bankManagementDTO = (BankManagementDTO) dataDTO.getData();
//
//         auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BANK_UNIT,
//                 AclConstants.OPERATION_BANK_UNIT_VIEW, req.getRemoteAddr(), null, bankManagementDTO.getId(), bankManagementDTO.getBankname());
//         return dataDTO;
//
//     }
//
//     @Override
//     public GenericDataDTO getAllWithoutPagination() {
//         return super.getAllWithoutPagination();
//     }
//
//
//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_ADD + "\")")
//     @Override
//     public GenericDataDTO save(@Valid @RequestBody BankManagementDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//         if (getMvnoIdFromCurrentStaff() != null) {
//             entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//         }
//         MDC.put("type", "Create");
//         boolean flag = bankManagementService.duplicateVerifyAtSave(entityDTO.getAccountnum());
//         if(entityDTO.getBanktype().equals("other")){
//             flag = true;
//         }
//         GenericDataDTO dataDTO = new GenericDataDTO();
//         if (flag) {
//
//             dataDTO = super.save(entityDTO, result, authentication, req);
//             BankManagementDTO bankManagementDTO = (BankManagementDTO) dataDTO.getData();
//             auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BANK_UNIT,
//                     AclConstants.OPERATION_BANK_UNIT_ADD, req.getRemoteAddr(), null, bankManagementDTO.getId(), bankManagementDTO.getAccountnum());
//             logger.info("Bank is created Successfully With name "+entityDTO.getBankname()+"  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//         } else {
//             dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//             dataDTO.setResponseMessage(MessageConstants.BANK_NAME_EXITS);
//             logger.error("Bank With name "+entityDTO.getBankname()+":  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(),MessageConstants.BANK_NAME_EXITS);
//         }
//         MDC.remove("type");
//         return dataDTO;
//     }
//
//
//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_EDIT + "\")")
//     @Override
//     public GenericDataDTO update(@Valid @RequestBody BankManagementDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//         if (getMvnoIdFromCurrentStaff() != null) {
//             entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//         }
//         MDC.put("type", "Update");
//         Optional<BankManagement> bankManagement = bankManagementRepository.findById(entityDTO.getId());
//            String oldname=bankManagementRepository.findById(entityDTO.getId()).get().getBankname();
//         GenericDataDTO dataDTO = new GenericDataDTO();
//         boolean flag = true;
//         if(entityDTO.getAccountnum() != null && !entityDTO.getAccountnum().equals(""))
//             flag  = bankManagementService.duplicateVerifyAtEdit(entityDTO.getAccountnum(), entityDTO.getId(),entityDTO.getBanktype());
//         boolean flag2 = bankManagementService.deleteVerify(entityDTO.getId());
//         if(entityDTO.getId().equals(bankManagement.get().getId()) && entityDTO.getAccountnum() == null)
//         {
//             entityDTO.setAccountnum(bankManagement.get().getAccountnum());
//         }
//         if(entityDTO.getId().equals(bankManagement.get().getId()) && entityDTO.getBankname() == null)
//         {
//             entityDTO.setBankname(bankManagement.get().getBankname());
//         }
//         if(entityDTO.getId().equals(bankManagement.get().getId()) && entityDTO.getBankholdername() == null)
//         {
//             entityDTO.setBankholdername(bankManagement.get().getBankholdername());
//         }
//         if(entityDTO.getId().equals(bankManagement.get().getId()) && entityDTO.getIfsccode() == null)
//         {
//             entityDTO.setIfsccode(bankManagement.get().getIfsccode());
//         }
//         if(entityDTO.getId().equals(bankManagement.get().getId()) && entityDTO.getBanktype() == null)
//         {
//             entityDTO.setBanktype(bankManagement.get().getBanktype());
//         }
//
//         if (flag && flag2) {
//             dataDTO = super.update(entityDTO, result, authentication, req);
//             BankManagementDTO bankManagementDTO = (BankManagementDTO) dataDTO.getData();
//             if (bankManagementDTO != null) {
//                 auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BANK_UNIT,
//                         AclConstants.OPERATION_BANK_UNIT_EDIT, req.getRemoteAddr(), null, bankManagementDTO.getId(), bankManagementDTO.getAccountnum());
//                 logger.info("Bank With old name "+oldname+" is Updated to "+entityDTO.getBankname()+"   :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//             }
//         }else if(!flag2){
//             Optional<BankManagement> bankManagement1 = bankManagementRepository.findById(entityDTO.getId());
//             entityDTO.setAccountnum(bankManagement1.get().getAccountnum());
//             entityDTO.setBankname(entityDTO.getBankname());
//             dataDTO = super.update(entityDTO, result, authentication, req);
//         }
//         else {
//             dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//             dataDTO.setResponseMessage(MessageConstants.BANK_NAME_EXITS);
//             logger.error("Unable to Update Bank With name: "+entityDTO.getBankname() +"  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value());
//         }
//         MDC.remove("type");
//         return dataDTO;
//     }
//
//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_DELETE + "\")")
//     @Override
//     public GenericDataDTO delete(@RequestBody BankManagementDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//         MDC.put("type", "Delete");
//         GenericDataDTO dataDTO = new GenericDataDTO();
//         boolean flag = bankManagementService.deleteVerify(entityDTO.getId());
//         if (flag) {
//             dataDTO = super.delete(entityDTO, authentication, req);
//             BankManagementDTO bankManagementDTO = (BankManagementDTO) dataDTO.getData();
//             if (bankManagementDTO != null) {
//                 auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BANK_UNIT,
//                         AclConstants.OPERATION_BANK_UNIT_DELETE, req.getRemoteAddr(), null, bankManagementDTO.getId(), bankManagementDTO.getBankname());
//                 logger.info("Bannk  With name "+entityDTO.getBankname()+" is deleted Successfully  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),APIConstants.SUCCESS);
//             }
//         } else {
//             dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//             dataDTO.setResponseMessage(DeleteContant.BANK_DELETE_EXIST);
//             logger.error("Unable to Delete Bank With name: "+entityDTO.getBankname() +"  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value());
//         }
//         MDC.remove("type");
//         return dataDTO;
//     }
//     @GetMapping("/searchByStatus")
//     public GenericDataDTO getAllByStatus(HttpServletRequest req, @RequestParam(name="banktype",required=false) String banktype) {
//         String SUBMODULE = getModuleNameForLog() + " [getALlByStatus] ";
//         MDC.put("type", "Fetch");
//         GenericDataDTO genericDataDTO = new GenericDataDTO();
//         try {
//             genericDataDTO = GenericDataDTO.getGenericDataDTO(bankManagementRepository.findAllByStatus());
//             if(banktype!=null){
//             if (null != genericDataDTO) {
//
//                 if (genericDataDTO.getDataList().isEmpty()) {
//                     genericDataDTO = new GenericDataDTO();
//                     genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
//                     genericDataDTO.setResponseMessage("No Record Found!");
//                     genericDataDTO.setDataList(new ArrayList<>());
//                     genericDataDTO.setTotalRecords(0);
//                     genericDataDTO.setPageRecords(0);
//                     genericDataDTO.setCurrentPageNumber(1);
//                     genericDataDTO.setTotalPages(1);
//                 }
//                 List<BankManagement> banksList = bankManagementRepository.findAllByStatus();
//                 List<BankManagementDTO> banks = new ArrayList<>();
//                         banksList.stream().forEach(bankManagement -> {
//                     BankManagementDTO bankManagementDTO = bankManagementMapper.domainToDTO(bankManagement, new CycleAvoidingMappingContext());
//                     banks.add(bankManagementDTO);
//                 });
//
//
//                 if (banktype.equalsIgnoreCase("other")) {
//                     List l1 = banks.stream().filter(bank -> (StringUtils.isEmpty(bank.getBanktype()) || bank.getBanktype().equals("other"))).collect(Collectors.toList());
//                     genericDataDTO.setDataList(l1);
//                 }
//                 if (banktype.equalsIgnoreCase("operator")) {
//                     List l2 = banks.stream().filter(bank -> (StringUtils.isEmpty(bank.getBanktype()) || bank.getBanktype().equals("operator"))).collect(Collectors.toList());
//                     genericDataDTO.setDataList(l2);
//                 }
//                 logger.info("No data Found  :  request: { From : {}}; Response : {{}};}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode());
//             }
//             return genericDataDTO;
//             }
//
//         } catch (Exception ex) {
//             ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//             genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//             genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//             logger.error("Unable to Search data  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.EXPECTATION_FAILED.value(),HttpStatus.EXPECTATION_FAILED.getReasonPhrase(),ex.getStackTrace());
//             return genericDataDTO;
//         }
//         MDC.remove("type");
//         return genericDataDTO;
//     }
//
//     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_VIEW + "\")")
//     @Override
//     public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//             , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//             , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//             , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//         return super.search(page, pageSize, sortOrder, sortBy, filter);
//     }
//     public ValidationData validateSearchCriteria(List<GenericSearchModel> filterList) {
//         ValidationData validationData = new ValidationData();
//         if (null == filterList || 0 < filterList.size()) {
//             validationData.setValid(false);
//             validationData.setMessage("Please Provide Search Criteria");
//             return validationData;
//         }
//         validationData.setValid(true);
//         return validationData;
//     }
//     int i =0;
//
//
//
//
////     public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
////         return apiResponse(responseCode, response, null);
////     }
////     @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BANK_UNIT_ALL + "\",\"" + AclConstants.OPERATION_BANK_UNIT_VIEW + "\")")
////     @PostMapping("/bankByName")
////     public ResponseEntity<?> searchParentCustomer(@RequestBody PaginationRequestDTO requestDTO) {
////         Integer RESP_CODE = APIConstants.FAIL;
////         HashMap<String, Object> response = new HashMap<>();
////         Page<BankManagement> bankList = null;
////         try {
////             requestDTO = setDefaultPaginationValues(requestDTO);
////             ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
////             if (validationData.isValid()) {
////                 RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
////                 response.put(APIConstants.ERROR_TAG, validationData.getStackTrace());
////                 return apiResponse(RESP_CODE, response);
////             }
////             CustomersService subscriberService = SpringContext.getBean(CustomersService.class);
////             parentCustomersList = subscriberService.searchParentCustomersByCustomerType(requestDTO.getFilters(),
////                     requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(),
////                     type, requestDTO.getStatus());
////             Integer Response = 0;
////             if (parentCustomersList.isEmpty()) {
////                 Response = APIConstants.NULL_VALUE;
////                 response.put(APIConstants.MESSAGE, "No Records Found!");
////                 return apiResponse(Response, response);
////
////             }
////             if (null != parentCustomersList && 0 < parentCustomersList.getSize()) {
////                 response.put("parentCustomerList", parentCustomersList.getContent().stream().map(data -> {
////                     try {
////                         return subscriberMapper.domainToDTO(data, new CycleAvoidingMappingContext());
////                     } catch (NoSuchFieldException e) {
////                         e.printStackTrace();
////                     }
////                     return null;
////                 }).collect(Collectors.toList()));
////             } else {
////                 response.put("parentCustomerList", new ArrayList<>());
////             }
////             RESP_CODE = APIConstants.SUCCESS;
////         } catch (CustomValidationException ce) {
////             ce.printStackTrace();
////             RESP_CODE = ce.getErrCode();
////             response.put(APIConstants.ERROR_TAG, ce.getStackTrace());
////         } catch (RuntimeException re) {
////             re.printStackTrace();
////             RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
////             response.put(APIConstants.ERROR_TAG, re.getStackTrace());
////         } catch (Exception e) {
////             e.printStackTrace();
////             RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
////             response.put(APIConstants.ERROR_TAG, e.getStackTrace());
////         }
////         return apiResponse(RESP_CODE, response, parentCustomersList);
////     }

 }







