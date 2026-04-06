package com.adopt.apigw.modules.childcustomer.implemetation;

import brave.Tracer;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.devCode.TransactionUtil;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustPlanMapppingRepository;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.childcustomer.UpdateChildCustometMessesge;
import com.adopt.apigw.modules.childcustomer.dto.CafChildCustomerApproveMessege;
import com.adopt.apigw.modules.childcustomer.dto.ChangePasswordPojo;
import com.adopt.apigw.modules.childcustomer.dto.ChildCustPojo;
import com.adopt.apigw.modules.childcustomer.entity.ChildCustomer;
import com.adopt.apigw.modules.childcustomer.entity.QChildCustomer;
import com.adopt.apigw.modules.childcustomer.parentchildmappingrel.ParentChildMappingRel;
import com.adopt.apigw.modules.childcustomer.parentchildmappingrel.ParentChildMappingRelService;
import com.adopt.apigw.modules.childcustomer.parentchildmappingrel.ParentChildMappingRepo;
import com.adopt.apigw.modules.childcustomer.repository.ChildCustomerRepo;
import com.adopt.apigw.modules.childcustomer.service.ChildCustomerService;
import com.adopt.apigw.pojo.PaginationDetails;
import com.adopt.apigw.pojo.api.LoginPojo;
import com.adopt.apigw.pojo.api.PasswordPojo;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.ChildCustomerRegistrationSuccessMsg;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service

public class ChildCustomerImpl implements ChildCustomerService {
    private static final String MODULE = " [ChildCustomerService] ";
    private final Logger log = LoggerFactory.getLogger(ChildCustomerImpl.class);
    @Autowired
    private Tracer tracer;
    @Autowired
    private ChildCustomerRepo childCustomerRepo;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private CustomersService customersService;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private ParentChildMappingRelService parentChildMappingRelService;

    @Autowired
    private ParentChildMappingRepo parentChildMappingRepo;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;

    @Autowired
    private TransactionUtil transactionUtil;

    @Override
    public ResponseEntity<?> create(ChildCustPojo pojo, HttpServletRequest req) {
        long startTime = System.currentTimeMillis();
//        TraceContext traceContext = tracer.currentSpan().context();
        LoggedInUser loggedInUser = getLoggedInUser();

        // Set MDC values for traceability in logs
//        MDC.put("type", "Create");
//        MDC.put("userName", loggedInUser.getUsername());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());

        HashMap<String, Object> response = new HashMap<>();
        int responseCode = HttpStatus.INTERNAL_SERVER_ERROR.value();

        try {
            Boolean thresholdFlag = thresholdValidation(pojo);
            if (!thresholdFlag) {
                response.put("responseMessage", "Threshold limit exceeded");
                responseCode = APIConstants.EXPECTATION_FAILED;
                return apiResponse(responseCode, response);
            }
            validateMendatoryFields(pojo);
            boolean isDuplicate = duplicateVerifyAtSave(pojo.getUserName(),pojo.getMvnoId());
//            if(!isDuplicate){
//                List<ParentChildMappingRel> parentChildMappingRel = parentChildMappingRepo.findByChildUsernameAndMvno(pojo.getUserName(), getLoggedInUser().getMvnoId().longValue());
//                for (ParentChildMappingRel childMappingRel : parentChildMappingRel) {
//                    Customers customers = childMappingRel.getParentCustomer();
//                    if(childMappingRel.getChildUsername().equalsIgnoreCase(pojo.getUserName()) && customers.getId()!=pojo.getParentCustId().intValue()){
//                        parentChildMappingRelService.saveParentChildRel(pojo);
//                    }
//                }
//            }
            if (isDuplicate) {
                if (!checkDuplicateByPhoneNo(pojo)) {
                    response.put("responseMessage", "customer with this Phone number is bind with another parent customer");
                    responseCode = APIConstants.ALREADY_EXIST;
                    return apiResponse(responseCode, response);
                }
                ChildCustomer childCustomer = new ChildCustomer(pojo);
                childCustomer.setCreateDateTime(LocalDateTime.now());
                childCustomer.setModifyDateTime(LocalDateTime.now());
                childCustomer.setUpdatedByName(getLoggedInUser().getFirstName());
                childCustomer.setCreatedByName(getLoggedInUser().getFirstName());
                childCustomer.setMvnoId(Long.valueOf(pojo.getMvnoId()));
                childCustomer.setParentCustId(pojo.getParentCustId());
                childCustomer.setCreateByStaffId(Long.valueOf(getLoggedInUser().getStaffId()));
                Object[] customers = customersRepository.findBasicCustomerInfoById(childCustomer.getParentCustId().intValue());
//                Optional<Customers> optionalCustomer = customersRepository.findById(pojo.getParentCustId().intValue());
                if (pojo.getIsParent() == null) {
                    childCustomer.setIsParent(false);
                } else {
                    childCustomer.setIsParent(pojo.getIsParent());
                }
                List<Long> buIdsList = getLoggedInUser().getBuIds();
                if (Objects.isNull(buIdsList) || buIdsList.isEmpty()) {
                    childCustomer.setBuId(null);
                } else if (buIdsList.size() > 1) {
                    response.put("responseMessage", "Multiple buIds bound to user.");
                    responseCode = HttpStatus.MULTI_STATUS.value();
                } else {
                    childCustomer.setBuId(buIdsList.get(0).intValue());
                }
                if (pojo.getAccountNumber() != null && !pojo.getAccountNumber().isEmpty()) {
                    childCustomer.setParentAccountNumber(pojo.getAccountNumber());
                } else {
                    String parentAcctNo = customersRepository.findAcctnoById(pojo.getParentCustId().intValue());
//                    Optional<Customers> optionalCustomer = customersRepository.findCustomerById(pojo.getParentCustId().intValue());
                    //                        Customers customers = optionalCustomer.get();
                    childCustomer.setParentAccountNumber(parentAcctNo);
                }

                childCustomerRepo.saveAndFlush(childCustomer);
                parentChildMappingRelService.saveParentChildRel(pojo, customers);
                kafkaMessageSender.send(new KafkaMessageData(childCustomer, childCustomer.getClass().getSimpleName()));
//               if(optionalCustomer.isPresent()) {
//                   StringBuilder planNamesBuilder = new StringBuilder();
//                   if (optionalCustomer.get().getPlanMappingList() != null) {
//                       for (CustPlanMappping mappping : optionalCustomer.get().getPlanMappingList()) {
//                           String planname = postpaidPlanRepo.findNameById(mappping.getPlanId());
//                           if (!planNamesBuilder.toString().contains(planname)) {
//                               planNamesBuilder.append(planname).append(", ");
//                           }
//                       }
//                   }
//                   List<String> planNames = custPlanMapppingRepository.findPlanNameByCustId(pojo.getParentCustId().intValue());
                if (customers != null && customers.length > 0) {
                    Object[] innerArray = (Object[]) customers[0];
                    Long staffId = ((Integer) innerArray[7]).longValue();
                    ChildCustomerRegistrationSuccessMsg childCustomerRegistrationSuccessMsg = new ChildCustomerRegistrationSuccessMsg(LocalDateTime.now().toString(), null, childCustomer.getUserName(), childCustomer.getPassword(), innerArray[6].toString(), childCustomer.getMobileNumber(), childCustomer.getEmail(), childCustomer.getMvnoId(), "Registration Succes", RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,innerArray[9].toString(), childCustomer.getBuId(), childCustomer.getParentAccountNumber(), (String) innerArray[1], staffId,childCustomer.getId(), (String) innerArray[8]);
                    kafkaMessageSender.send(new KafkaMessageData(childCustomerRegistrationSuccessMsg, childCustomerRegistrationSuccessMsg.getClass().getSimpleName()));
                }
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CHILD_CUSTOMER, AclConstants.OPERATION_CHILDCUSTOMER_ADD, req.getRemoteAddr(), null, pojo.getParentCustId(), pojo.getFirstName());
                log.info("Child customer created successfully: {}", pojo.getUserName());
                response.put("responseMessage", APIConstants.SUCCESS_STATUS);
                responseCode = APIConstants.SUCCESS;
            } else {
                List<Long> parentCustId = new ArrayList<>();
//                List<ParentChildMappingRel> parentChildMappingRel = parentChildMappingRepo.findByChildUsernameAndParentCustomerAndMvnoAndIsDeleteIsFalse(pojo.getUserName(), pojo.getParentCustId(), getLoggedInUser().getMvnoId().longValue());
                Long count = parentChildMappingRepo.countByChildUsernameAndParentCustomerAndMvnoAndIsDeleteIsFalse(pojo.getUserName(), pojo.getParentCustId(), pojo.getMvnoId().longValue());
             //   Boolean mobileCount = checkCustomerMobileAndUsername(pojo.getUserName() , pojo.getMobileNumber(),pojo.getMvnoId());
                if (count <= 0 ) {
                    Object[] customers = customersRepository.findBasicCustomerInfoById(pojo.getParentCustId().intValue());
                    parentChildMappingRelService.saveParentChildRel(pojo, customers);
                    response.put("responseMessage", APIConstants.SUCCESS_STATUS);
                    responseCode = APIConstants.SUCCESS;
                }
//                }else if(mobileCount){
//                    log.warn("Attempted to create duplicate user: {}", pojo.getUserName());
//                    response.put("responseMessage", "User with different mobile number is already exist.");
//                    responseCode = APIConstants.ALREADY_EXIST;
//                }
                else {
                    log.warn("Attempted to create duplicate user: {}", pojo.getUserName());
                    response.put("responseMessage", "User already exists");
                    responseCode = APIConstants.ALREADY_EXIST;
                }
            }
            log.warn(":::::::::::::::: Time Take by Child customer creation :::::::::::::::: {}", (System.currentTimeMillis() - startTime));
            return apiResponse(responseCode, response);
        } catch (CustomValidationException e) {
            log.error("Error while creating child customer for username '{}': {}", pojo.getUserName(), e.getMessage(), e);
            response.put("responseMessage", e.getMessage());
            return apiResponse(responseCode, response);
        } catch (Exception e) {
            log.error("Error while creating child customer for username '{}': {}", pojo.getUserName(), e.getMessage(), e);
            response.put("responseMessage", e.getMessage());
            return apiResponse(responseCode, response);

        } finally {
            MDC.clear(); // Clean up MDC
        }
    }

    private Boolean thresholdValidation(ChildCustPojo pojo) {
        try {
            // TODO: pass mvnoID manually 6/5/2025
            Long mvnoId = pojo.getMvnoId().longValue();
            long childCustomersCount = childCustomerRepo.countByParentCustIdAndMvnoId(pojo.getParentCustId(), mvnoId);
            log.debug("Found {} child customers for parentCustId: {} and mvnoId: {}", childCustomersCount, pojo.getParentCustId(), mvnoId);

//            Optional<Mvno> optionalMvno = mvnoRepository.findById(mvnoId);
            Long mvnoThreshold = mvnoRepository.findThresholdById(mvnoId);
            if (mvnoThreshold != null) {
//                Mvno mvno = optionalMvno.get();
                Integer threshold = Math.toIntExact(mvnoThreshold + 1);
                log.debug("Threshold for mvnoId {} is {}", mvnoId, threshold);

                if (childCustomersCount < threshold) {
                    log.info("Threshold validation passed for parentCustId: {}", pojo.getParentCustId());
                    return true;
                } else {
                    log.warn("Threshold exceeded: {} >= {}", childCustomersCount, threshold);
                    return false;
                }
            } else {
                log.warn("MVNO not found with ID: {}", mvnoId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error during threshold validation for parentCustId: {} - {}", pojo.getParentCustId(), e.getMessage(), e);
        }
        return false;
    }


    @Override
    public List<ChildCustomer> getChildCustomer() {
        // TODO: pass mvnoID manually 6/5/2025
        List<Long> mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null).longValue(), 1L);
        return childCustomerRepo.findAllByMvnoIdIn(mvnoIds);

    }

    @Override
    public void delete(Long id, HttpServletRequest req) {
        try {
            Optional<ChildCustomer> optionalCustomer = childCustomerRepo.findById(id);

            if (optionalCustomer.isPresent()) {
                ChildCustomer childCustomer = optionalCustomer.get();
                childCustomer.setIsdeleted(true);
                childCustomerRepo.save(childCustomer);
                log.info("Child customer soft-deleted successfully. ID: {}", id);
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CHILD_CUSTOMER, AclConstants.OPERATION_CHILDCUSTOMER_DELETE, req.getRemoteAddr(), null, optionalCustomer.get().getParentCustId(), optionalCustomer.get().getFirstName());
            } else {
                log.warn("Attempt to delete non-existing child customer. ID: {}", id);
            }
        } catch (Exception e) {
            log.error("Error while deleting child customer with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete child customer with ID: " + id);
        }
    }

    @Override
    public GenericDataDTO getchildCustByParentID(Long parentId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            // TODO: pass mvnoID manually 6/5/2025
            Long mvnoID = getMvnoIdFromCurrentStaff(null).longValue();
            List<ChildCustomer> childCustomers = childCustomerRepo.findByParentCustIdAndMvnoId(parentId, mvnoID);

            if (childCustomers != null && !childCustomers.isEmpty()) {
                genericDataDTO.setResponseMessage(APIConstants.SUCCESS_STATUS);
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setDataList(childCustomers);

                log.info("Fetched {} child customers for parent ID: {}", childCustomers.size(), parentId);
            } else {
                genericDataDTO.setResponseMessage("No child customers found.");
                genericDataDTO.setResponseCode(APIConstants.SUCCESS); // Still success, just empty list

                log.warn("No child customers found for parent ID: {}", parentId);
            }

        } catch (Exception e) {
            log.error("Error fetching child customers for parent ID {}: {}", parentId, e.getMessage(), e);

            genericDataDTO.setResponseMessage("Failed to fetch child customers.");
            genericDataDTO.setResponseCode(APIConstants.FAIL);
        }

        return genericDataDTO;
    }

    @Override
    public ResponseEntity<?> updatechildCustByParentID(ChildCustPojo pojo) {
        log.info("Received request to update child customer with data: {}", pojo);

        HashMap<String, Object> response = new HashMap<>();
        int respCode = APIConstants.FAIL;

        try {
            // TODO: pass mvnoID manually 6/5/2025
            Optional<ChildCustomer> childCustByPArentIdAndUsername = childCustomerRepo.findByParentCustIdAndUserNameAndMvnoId(pojo.getId(), pojo.getUserName(), pojo.getMvnoId().longValue());
            // TODO: pass mvnoID manually 6/5/2025
            Optional<ChildCustomer> optionalChild = childCustomerRepo.findByIdAndMvnoId(pojo.getId(), pojo.getMvnoId().longValue());
            if (optionalChild.isPresent() || childCustByPArentIdAndUsername.isPresent()) {
                ChildCustomer childCustomer = optionalChild.orElse(childCustByPArentIdAndUsername.get());
                log.debug("Existing child customer found for update: {}", childCustomer);

                // Perform field updates
                childCustomer.setFirstName(pojo.getFirstName());
                childCustomer.setLastName(pojo.getLastName());
                childCustomer.setUpdatedByName(getLoggedInUser().getFirstName());
                childCustomer.setStatus(pojo.getStatus());
                childCustomer.setMobileNumber(pojo.getMobileNumber());
              //  childCustomer.setPassword(pojo.getPassword());
                childCustomer.setEmail(pojo.getEmail());
                childCustomer.setWallet(pojo.getWallet());
                childCustomer.setModifyDateTime(LocalDateTime.now());
                shareChildCustomerInRevenue(pojo);
                childCustomerRepo.save(childCustomer);

                log.info("Child customer updated successfully. ID: {}", childCustomer.getId());
                response.put("responseMessage", "Child customer updated successfully.");
                respCode = APIConstants.SUCCESS;
                parentChildMappingRelService.updateParentChildRel(pojo);
            } else {
                log.warn("Child customer not found with ID: {}", pojo.getId());
                response.put("responseMessage", "Child customer not found.");
                respCode = APIConstants.NOT_FOUND;
            }
        } catch (Exception e) {
            log.error("Exception occurred while updating child customer. Error: {}", e.getMessage(), e);
            response.put("responseMessage", "Internal server error occurred while updating child customer.");
            respCode = APIConstants.INTERNAL_SERVER_ERROR;
        }

        return apiResponse(respCode, response);
    }

    private void shareChildCustomerInRevenue(ChildCustPojo pojo) {
        UpdateChildCustometMessesge updateChildCustometMessesge = new UpdateChildCustometMessesge();
        updateChildCustometMessesge.setFirstName(pojo.getFirstName());
        updateChildCustometMessesge.setLastName(pojo.getLastName());
        updateChildCustometMessesge.setMobileNumber(pojo.getMobileNumber());
        updateChildCustometMessesge.setParentCustId(pojo.getParentCustId());
        updateChildCustometMessesge.setIsParent(pojo.getIsParent());
        updateChildCustometMessesge.setEmail(pojo.getEmail());
      //  updateChildCustometMessesge.setPassword(pojo.getPassword());
        updateChildCustometMessesge.setStatus(pojo.getStatus());
        updateChildCustometMessesge.setUserName(pojo.getUserName());
        updateChildCustometMessesge.setId(pojo.getId());
        kafkaMessageSender.send(new KafkaMessageData(updateChildCustometMessesge, updateChildCustometMessesge.getClass().getSimpleName()));
    }


    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if (securityContext.getAuthentication().getPrincipal() != null)
                    mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }
    public Integer getMvnoIdFromCurrentStaff(Integer custId) {
        //TODO: Change once API work on live BSS server
        Integer mvnoId = null;
        try {
            if(custId!=null){
                mvnoId = customersRepository.getCustomerMvnoIdByCustId(custId);

            }
//            else {
//                SecurityContext securityContext = SecurityContextHolder.getContext();
//                if (null != securityContext.getAuthentication()) {
//                    if(securityContext.getAuthentication().getPrincipal() != null)
//                        mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
//                }
//            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }


    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

    /*public boolean duplicateVerifyAtSave(String userName, LoggedInUser loggedInUser) {
        boolean flag = false;
        if (userName != null) {
            userName = userName.trim();
            Integer count;
            if (loggedInUser.getMvnoId() == 1) count = childCustomerRepo.duplicateVerifyAtSave(userName);
            else
                count = childCustomerRepo.duplicateVerifyAtSave(userName, Arrays.asList(loggedInUser.getMvnoId().longValue(), 1L));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }*/
    public boolean duplicateVerifyAtSave(String userName,Integer mvnoId) {
        boolean flag = false;
        if (userName != null) {
            userName = userName.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) count = childCustomerRepo.duplicateVerifyAtSave(userName);
            else
                // TODO: pass mvnoID manually 6/5/2025
                count = childCustomerRepo.duplicateVerifyAtSave(userName, Arrays.asList(mvnoId.longValue(), 1L));
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }


    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response, null);
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        try {
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            if (null != page) {
                response.put("pageDetails", setPaginationDetails(page));
            }

            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else if (responseCode.equals(APIConstants.NOT_FOUND)) {
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            } else if (responseCode.equals(HttpStatus.UNAUTHORIZED.value())) {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else if (responseCode.equals(HttpStatus.CONFLICT.value())) {
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            } else if (responseCode.equals(HttpStatus.EXPECTATION_FAILED.value())) {
                return new ResponseEntity<>(response, HttpStatus.EXPECTATION_FAILED);
            } else if (responseCode.equals(HttpStatus.NO_CONTENT.value())) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {

            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}", APIConstants.FAIL, e.getStackTrace());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public PaginationDetails setPaginationDetails(Page page) {
        PaginationDetails pageDetails = new PaginationDetails();
        pageDetails.setTotalPages(page.getTotalPages());
        pageDetails.setTotalRecords(page.getTotalElements());
        pageDetails.setTotalRecordsPerPage(page.getNumberOfElements());
        pageDetails.setCurrentPageNumber(page.getNumber() + 1);
        return pageDetails;
    }

    public void validateMendatoryFields(ChildCustPojo pojo) throws Exception {

        if (pojo.getFirstName() == null) {
            throw new CustomValidationException(APIConstants.FAIL, "FirstName field is mandatory, Please add FirstName", null);
        }

        if (pojo.getLastName() == null) {
            throw new CustomValidationException(APIConstants.FAIL, "LastName field is mandatory, Please add LastName", null);
        }

        if (pojo.getEmail() == null) {
            throw new CustomValidationException(APIConstants.FAIL, "Email field is mandatory, Please add Email", null);
        }

        if (pojo.getUserName() == null) {
            throw new CustomValidationException(APIConstants.FAIL, "Username field is mandatory, Please add UserName", null);
        }

        if (pojo.getParentCustId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, "Parent Id field is mandatory, Please add Parent", null);
        }
//            ChildCustomer customers = childCustomerRepo.findByUserNameAndMvnoId(
//                    pojo.getUserName(), getMvnoIdFromCurrentStaff().longValue()).orElse(null);
//            List<ParentChildMappingRel> parentChildMappingRel = parentChildMappingRepo.findAllById(customers.getId());
//            if (customers != null) {
//                throw new CustomValidationException(APIConstants.FAIL, "Username already exists", null);
//            }
    }

    public List<java.lang.Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = new ArrayList<java.lang.Long>();
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }

    public HashMap<String, Object> childLogin(LoginPojo pojo, Optional<ChildCustomer> childCustomerList) {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (childCustomerList == null || !childCustomerList.isPresent()) {
                log.warn("Login failed: No child customer found for username '{}'", pojo.getUsername());
                response.put(CommonConstants.RESPONSE_MESSAGE, "Invalid username or password");
                return response;
            }
            ChildCustomer childCustomer = childCustomerList.get(); // Assuming username is unique
            ApplicationLogger.logger.info("Entered Password: " + pojo.getPassword() + " | In DB Password: " + childCustomer.getPassword());
            log.debug("Attempting login for child customer username '{}'", pojo.getUsername());
            if (pojo.getPassword().equals(childCustomer.getPassword())) {
                Optional<Customers> customers = customersRepository.findById(childCustomer.getParentCustId().intValue());
                log.info("Child login successful for username '{}'", pojo.getUsername());
                response.put("lastName", childCustomer.getLastName());
                response.put("parentId", childCustomer.getParentCustId());
                response.put("mvnoId", childCustomer.getMvnoId());
                response.put("fistName", childCustomer.getFirstName());
                response.put("userId", childCustomer.getId());
                response.put("userName", childCustomer.getUserName());
                response.put("partnerId", getLoggedInUser().getPartnerId());
                response.put("accountNumber", customers.get().getAcctno());
                response.put(CommonConstants.RESPONSE_MESSAGE, "Child login success");
                response.put("status", "SUCCESS");
            } else {
                log.warn("Child login failed: Invalid password for username '{}'", pojo.getUsername());
                response.put("status", "FAIL");
                response.put(CommonConstants.RESPONSE_MESSAGE, "Invalid username or password");
            }
        } catch (Exception e) {
            log.error("Exception occurred during child login for username '{}'. Error: {}", pojo.getUsername(), e.getMessage(), e);
            response.put(CommonConstants.RESPONSE_MESSAGE, "Internal server error during login");
        }
        return response;
    }

    public List<ChildCustomer> getCustomerByUsername(String username) {
        return childCustomerRepo.findByUserName(username);
    }

    @Override
    public Page<ChildCustomer> getAllChildCustomer(Integer pageNumber, Integer pageSize, Integer mvnoId) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        // TODO: pass mvnoID manually 6/5/2025
        return childCustomerRepo.findAllByMvnoIdAndIsdeletedOrderByIdDesc(
                mvnoId.longValue(), false, pageRequest);
    }

    @Override
    public ChildCustomer getchildCustByID(Long id) {
        return childCustomerRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("ChildCustomer not found with ID: " + id));
    }

    @Override
    public List<ChildCustomer> getchildCustByMobileNumber(String mobileNumber,Integer parentId,Long mvnoId) {
        List<ChildCustomer> childCustomersList = childCustomerRepo.findChildrenByMobileNumberAndMvnoId(mobileNumber, mvnoId);
        List<ChildCustomer> stream = childCustomersList.stream().filter(child -> !Objects.equals(child.getParentCustId(), parentId.longValue())).collect(Collectors.toList());
        if(stream.isEmpty()){
            throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "No account found by mobile number",null);
        }
        return childCustomersList;
    }

    @Override
    public Page<ChildCustomer> getchildCustByID(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder, String status) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        log.info("Fetching child customers with filters: {}, page: {}, pageSize: {}, sortBy: {}, sortOrder: {}, status: {}", filters, page, pageSize, sortBy, sortOrder, status);

        try {
            QChildCustomer qCustomer = QChildCustomer.childCustomer;
            BooleanExpression booleanExpression = qCustomer.isNotNull().and(qCustomer.isdeleted.eq(false));

            for (GenericSearchModel searchModel : filters) {
                String column = searchModel.getFilterColumn();
                String value = searchModel.getFilterValue();

                if (SearchConstants.CUST_USERNAME.equalsIgnoreCase(column) && value != null) {
                    booleanExpression = booleanExpression.and(qCustomer.userName.containsIgnoreCase(value));
                    log.debug("Filtering by userName contains: {}", value);
                } else if (SearchConstants.ANY.equalsIgnoreCase(column) && value != null && !value.isEmpty()) {
                    booleanExpression = booleanExpression.and(qCustomer.userName.likeIgnoreCase("%" + value + "%").or(qCustomer.firstName.likeIgnoreCase("%" + value + "%")).or(qCustomer.mobileNumber.contains(value)));
                    log.debug("Filtering by ANY field (username/firstName/mobileNumber) contains: {}", value);
                } else {
                    log.warn("Unsupported filter column: {}", column);
                }
            }

            // Build the query
            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
            QueryResults<ChildCustomer> queryResults = queryFactory.select(Projections.constructor(ChildCustomer.class, qCustomer.id, qCustomer.userName, qCustomer.isdeleted, qCustomer.createdByName, qCustomer.email, qCustomer.lastName, qCustomer.mobileNumber, qCustomer.firstName, qCustomer.wallet, qCustomer.isParent, qCustomer.createByStaffId, qCustomer.buId, qCustomer.createDateTime, qCustomer.parentCustId, qCustomer.status)).from(qCustomer).where(booleanExpression).orderBy(qCustomer.id.desc()) // Consider dynamic sort if needed
                    .offset(pageRequest.getOffset()).limit(pageRequest.getPageSize()).fetchResults();

            log.info("Query executed successfully. Total records: {}", queryResults.getTotal());

            return new PageImpl<>(queryResults.getResults(), pageRequest, queryResults.getTotal());

        } catch (Exception e) {
            log.error("Error occurred while fetching child customers", e);
            return Page.empty(); // Safer than returning null
        }
    }


    @Override
    public Page<ChildCustomer> getChildByParentCustId(Long id, Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize);
        log.info("Fetching child customers with filters: {}, page: {}, pageSize: {}, sortBy: {}, sortOrder: {}, status: {}", page, pageSize);

        try {
            QChildCustomer qCustomer = QChildCustomer.childCustomer;
            BooleanExpression booleanExpression = qCustomer.isNotNull().and(qCustomer.isdeleted.eq(false));

            booleanExpression = booleanExpression.and(qCustomer.parentCustId.eq(id));
            // Build the query
            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
            QueryResults<ChildCustomer> queryResults = queryFactory.select(Projections.constructor(ChildCustomer.class, qCustomer.id, qCustomer.userName, qCustomer.isdeleted, qCustomer.createdByName, qCustomer.email, qCustomer.lastName, qCustomer.mobileNumber, qCustomer.firstName, qCustomer.wallet, qCustomer.isParent, qCustomer.createByStaffId, qCustomer.buId, qCustomer.createDateTime, qCustomer.parentCustId, qCustomer.status)).from(qCustomer).where(booleanExpression).orderBy(qCustomer.id.desc()) // Consider dynamic sort if needed
                    .offset(pageRequest.getOffset()).limit(pageRequest.getPageSize()).fetchResults();

            log.info("Query executed successfully. Total records: {}", queryResults.getTotal());

            return new PageImpl<>(queryResults.getResults(), pageRequest, queryResults.getTotal());

        } catch (Exception e) {
            log.error("Error occurred while fetching child customers", e);
            return Page.empty(); // Safer than returning null
        }
    }

    @Override
    public ResponseEntity<?> updateChildPassword(ChangePasswordPojo pojo, Integer mvnoId) {
        int responseCode = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();

        try {
            if (pojo == null || pojo.getUserName() == null || pojo.getOldPassword() == null || pojo.getNewPassword() == null) {
                response.put("message", "Invalid request payload");
                return apiResponse(APIConstants.FAIL, response);
            }
//            Long mvnoId = getMvnoIdFromCurrentStaff().longValue();
            // TODO: pass mvnoID manually 6/5/2025
            Optional<ChildCustomer> optionalCustomer = childCustomerRepo.findByUserNameAndMvnoIdInAndIsdeleted(pojo.getUserName(), Arrays.asList(mvnoId.longValue(), 1L),false);

            if (optionalCustomer.isPresent()) {
                ChildCustomer customer = optionalCustomer.get();
                if (pojo.getOldPassword().equals(customer.getPassword())) {
                    customer.setPassword(pojo.getNewPassword());
                    childCustomerRepo.saveAndFlush(customer);
                    List<ParentChildMappingRel> parentChildMappingRelList = parentChildMappingRepo.findAllByChildCustomerAndIsDelete(customer.getId(),false);
//                    Customers customers = customersRepository.findByUsernameAndMvnoId(pojo.getUserName(), mvnoId).orElse(null);
//                    if(customers != null){
//                        customers.set(pojo.getNewPassword());
//                        customersRepository.save(customers);
//                    }

                    if (!parentChildMappingRelList.isEmpty()) {
                        parentChildMappingRelList.stream().forEach(parentChildMappingRel -> parentChildMappingRel.setChildPassword(customer.getPassword()));
                        parentChildMappingRepo.saveAll(parentChildMappingRelList);
                    }
                    response.put("message", APIConstants.SUCCESS_STATUS);
                    responseCode = APIConstants.SUCCESS;
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CHILD_CUSTOMER, AclConstants.OPERATION_CHILDCUSTOMER_CHANGE_PASSWORD, null, null, optionalCustomer.get().getParentCustId(), optionalCustomer.get().getFirstName());
                } else {
                    response.put("message", "Old password does not match");
                    responseCode = HttpStatus.EXPECTATION_FAILED.value();
                }
            } else {
                response.put("message", "Child not found");
                responseCode = APIConstants.NO_CONTENT_FOUND;
            }

        } catch (Exception e) {
            log.error("Error updating child password for user: {}", pojo.getUserName(), e);
            response.put("message", "Internal server error");
            responseCode = APIConstants.FAIL;
        }

        return apiResponse(responseCode, response);
    }
//    public boolean duplicateVerifyAtSave(String username, Long parentCustId, Long mvnoId) {
//        List<ParentChildMappingRel> existingMappings =
//                parentChildMappingRepo.findByChildUsernameAndParentCustomerAndMvnoAndIsDeleteIsFalse(
//                        username, parentCustId, mvnoId);
//        return existingMappings != null && !existingMappings.isEmpty();
//    }

    public boolean duplicateVerifyAtSave(String username, Long parentCustId, String mobileNumber, Long mvnoId) {
        List<ParentChildMappingRel> existingMappings = parentChildMappingRepo.findByChildUsernameAndParentCustomerAndMvnoAndIsDeleteIsFalse(username, parentCustId, mvnoId);

        if (existingMappings == null || existingMappings.isEmpty()) {
            return false;
        }
        for (ParentChildMappingRel rel : existingMappings) {
            if (rel.getChildMobile() != null && rel.getChildMobile().equals(mobileNumber)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasMultipleParents(String username, Long mvnoId) {
        Long count = parentChildMappingRepo.countDistinctParentsForUser(username, mvnoId);
        return count != null && count > 1;
    }


    @Override
    public GenericDataDTO getChildCustomerByMobileNumberAndUserName(String username, String mobileNumber) {
        GenericDataDTO dto = new GenericDataDTO();

        log.info("Fetching ChildCustomer for username: {}, mobileNumber: {}", username, mobileNumber);

        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            log.warn("Mobile number is empty or null");
            dto.setResponseMessage("Mobile number is empty or null");
            dto.setResponseCode(APIConstants.FAIL);
            return dto;
        }

        if (username == null || username.trim().isEmpty()) {
            log.warn("Username is empty or null");
            dto.setResponseMessage("Username is empty or null");
            dto.setResponseCode(APIConstants.FAIL);
            return dto;
        }

        try {
            // TODO: pass mvnoID manually 6/5/2025
            Long mvnoId = getMvnoIdFromCurrentStaff(null).longValue();
            log.debug("Resolved mvnoId for current staff: {}", mvnoId);

            List<ChildCustomer> childCustomer = childCustomerRepo.findAllByUserNameAndMobileNumberAndMvnoId(username, mobileNumber, mvnoId);

            if (!childCustomer.isEmpty()) {
                log.info("Found {} customer(s) for username: {} and mobileNumber: {}", childCustomer.size(), username, mobileNumber);
                dto.setResponseCode(APIConstants.SUCCESS);
                dto.setResponseMessage(APIConstants.SUCCESS_STATUS);
                dto.setDataList(childCustomer);
            } else {
                log.info("No customer found for username: {} and mobileNumber: {}", username, mobileNumber);
                dto.setResponseCode(APIConstants.NO_CONTENT_FOUND);
                dto.setResponseMessage("Customer not found");
            }
        } catch (Exception e) {
            log.error("Error fetching child customer by username and mobile number", e);
            dto.setResponseCode(APIConstants.FAIL);
            dto.setResponseMessage("Something went wrong while fetching customer.");
        }

        return dto;
    }

    public void approveCafStatus(String status, Customers customersCaf) {
        Long parentCustId = customersCaf.getId().longValue();
        String username = customersCaf.getUsername();
        Long mvnoId = getMvnoIdFromCurrentStaff(customersCaf.getId()).longValue();

        log.info("Approving CAF status | ParentCustId: {}, Username: {}, Status: {}, MvnoId: {}", parentCustId, username, status, mvnoId);

        try {
            Optional<ChildCustomer> optionalChildCustomer = childCustomerRepo.findByParentCustIdAndUserNameAndMvnoId(parentCustId, username, mvnoId);

            if (optionalChildCustomer.isPresent()) {
                ChildCustomer childCustomer = optionalChildCustomer.get();
                log.info("ChildCustomer found | ID: {}", childCustomer.getId());

                List<ParentChildMappingRel> parentChildMappingRelList = parentChildMappingRepo.findByChildUsernameAndParentCustomerAndMvnoAndIsDeleteIsFalse(childCustomer.getUserName(), childCustomer.getParentCustId(), mvnoId);

                if (parentChildMappingRelList != null && !parentChildMappingRelList.isEmpty()) {
                    for (ParentChildMappingRel rel : parentChildMappingRelList) {
                        parentChildMappingRepo.findById(rel.getId()).ifPresent(mapping -> {
                            mapping.setStatus(status);
                            parentChildMappingRepo.saveAndFlush(mapping);
                            log.info("ParentChildMappingRel updated | ID: {}, New Status: {}", mapping.getId(), status);
                        });
                    }
                }

                childCustomer.setStatus(status);
                childCustomerRepo.saveAndFlush(childCustomer);
                log.info("ChildCustomer status updated | ID: {}, Status: {}", childCustomer.getId(), status);

                if (childCustomer.getStatus() != null) {
                    CafChildCustomerApproveMessege approveMessage = new CafChildCustomerApproveMessege();
                    approveMessage.setStatus(childCustomer.getStatus());
                    approveMessage.setCustomerId(childCustomer.getId().intValue());
                    approveMessage.setLoggedInUser(customersService.getLoggedInUserId());

                    kafkaMessageSender.send(new KafkaMessageData(approveMessage, approveMessage.getClass().getSimpleName()));
                    log.info("Kafka message sent | Customer ID: {}", childCustomer.getId());
                } else {
                    log.warn("Updated status is null for ChildCustomer ID: {}", childCustomer.getId());
                }

            } else {
                log.warn("ChildCustomer not found | ParentCustId: {}, Username: {}, MvnoId: {}", parentCustId, username, mvnoId);
            }
        } catch (Exception e) {
            log.error("Error while approving CAF status | ParentCustId: {}, Username: {}", parentCustId, username, e);
        }
    }

    public void updateChildPasswordAdmin(PasswordPojo passwordPojo){
        transactionUtil.updateChildCustomerPassword(passwordPojo.getNewpassword(),passwordPojo.getCustId().longValue());
    }

    public void changeStatusChildCustByParentID(Integer customerId, String status) {
        try {
            Long parentId = customerId.longValue();
            Long mvnoId = getMvnoIdFromCurrentStaff().longValue();

            List<ChildCustomer> childCustomerList = childCustomerRepo.findByParentCustIdAndMvnoId(parentId, mvnoId);
            List<ParentChildMappingRel> parentChildMappingRelList = parentChildMappingRepo.findByparentCustomer(parentId);

            if (!childCustomerList.isEmpty() && !parentChildMappingRelList.isEmpty()) {
                childCustomerList.forEach(child -> child.setStatus(status));
                childCustomerRepo.saveAll(childCustomerList);

                parentChildMappingRelList.forEach(rel -> rel.setStatus(status));
                parentChildMappingRepo.saveAll(parentChildMappingRelList);

                log.info("Child-customer status update completed for parent ID: {}", customerId);
            } else {
                log.info("No child customers or relationships found for parent ID: {}", customerId);
            }
        } catch (Exception e) {
            log.error("Error while updating child customer status for parent ID: {}", customerId, e);
        }
    }
    private Boolean checkDuplicateByPhoneNo(ChildCustPojo pojo) {
        try {
            Object[] parentChildRelObject = parentChildMappingRepo.findBasicParentChildInfoByUserNameAndMvnoIdAndIsParent(pojo.getUserName(), getMvnoIdFromCurrentStaff().longValue(), false);
            if(parentChildRelObject!=null && parentChildRelObject.length>0){
                Object[] parentChild = (Object[]) parentChildRelObject[0];
                String childMobile = (String) parentChild[3]; // child mobile number
                Long parentId = (Long) parentChild[6];  // parent cust id
                if(parentId == null){
                    throw new CustomValidationException(APIConstants.EXPECTATION_FAILED,"Parent Cust Not found",null);
                }
                if(childMobile.equals(pojo.getMobileNumber())){
                    if(parentId == pojo.getParentCustId().longValue()){
                        log.info("child mobile is same in db and in pojo; Child username: {}",pojo.getUserName());
                        return true;
                    }else {
                        return false;
                    }
                }else return true;
            }else return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error while checking is child duplicate by Phone No: {}", pojo.getMobileNumber(), e);
        }
        return false;
    }

    @Override
    public GenericDataDTO getChildCustomerByUserName(String username,Integer mvnoId) {
        GenericDataDTO dto = new GenericDataDTO();

        log.info("Fetching ChildCustomer for username: {}, mobileNumber: {}", username);


        if (username == null || username.trim().isEmpty()) {
            log.warn("Username is empty or null");
            dto.setResponseMessage("Username is empty or null");
            dto.setResponseCode(APIConstants.FAIL);
            return dto;
        }

        try {
            boolean isChildExist = parentChildMappingRelService.isChildUserExist(username,mvnoId.longValue());
            dto.setData(isChildExist);
            dto.setResponseCode(HttpStatus.OK.value());
            if(isChildExist) {
                dto.setResponseMessage("username already exist.");
            }
            else{
                dto.setResponseMessage("valid username.");
            }
            log.debug("Resolved mvnoId for current staff: {}", mvnoId);


        } catch (Exception e) {
            log.error("Error fetching child customer by username and mobile number", e);
            dto.setResponseCode(APIConstants.FAIL);
            dto.setResponseMessage("Something went wrong while fetching customer.");
        }

        return dto;
    }

    public Boolean checkCustomerMobileAndUsername(String username , String mobileNumber , Integer mvnoId){
        List<ParentChildMappingRel> parentChildMappingRelList = parentChildMappingRepo.findByChildUsernameAndMvno(username,mvnoId.longValue());
        for (ParentChildMappingRel rel : parentChildMappingRelList) {
            if (mobileNumber.equals(rel.getChildMobile())) {
                return true;
            }
        }
        return false;
    }


}
