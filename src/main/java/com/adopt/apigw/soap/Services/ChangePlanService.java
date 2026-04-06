package com.adopt.apigw.soap.Services;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.modules.subscriber.controller.SubscriberController;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqDTO;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqDTOList;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqModel;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.repository.common.CustQuotaRepository;
import com.adopt.apigw.repository.postpaid.DebitDocRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.DebitDocService;
import com.adopt.apigw.soap.Dto.ChangeServiceRequest;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChangePlanService {

    private static String MODULE = " [changePlanService] ";

    private final Logger logger = LoggerFactory.getLogger(SubscriberController.class);

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private CustQuotaRepository custQuotaRepository;

    public ResponseEntity<?> changePlan(@RequestBody ChangeServiceRequest request, @RequestParam(name = "mvnoid", required = false) Long mvnoid, HttpServletRequest req) {
        MDC.put("type", "Create");
        Integer RESP_CODE = APIConstants.FAIL;
        String requestFrom = "bss";
        HashMap<String, Object> response = new HashMap<>();
        DeactivatePlanReqDTOList requestDTOs = new DeactivatePlanReqDTOList();
        Thread invoiceThread = null;
        String overrideResponse = "Failed to Override";
        try {
            Long starttime = System.currentTimeMillis();
//            Customers customersData = customersRepository.findByUserName(request.getUserName());
            Integer custId = customersRepository.findCustIdByUserName(request.getUserName());

            if (custId == null) {
                response.put(APIConstants.MESSAGE, "customer not found");
                logger.error(LogConstants.USERNAME_NOT_FOUND + request.getUserName());
                response.put("responseCode", APIConstants.NO_CONTENT);
                RESP_CODE = APIConstants.SUCCESS;
                return apiResponse(RESP_CODE, response, null);
            } else {
                // Get quota details
                List<Integer> custQuotaDetails1 = custQuotaRepository.findIdsByCustomerId(custId);

                if (custQuotaDetails1 == null) {
                    response.put(APIConstants.MESSAGE, "QuotaDtls Not found");
                    response.put("responseCode", APIConstants.NO_CONTENT);
                    RESP_CODE = APIConstants.SUCCESS;
                    return apiResponse(RESP_CODE, response, null);
                }
            }

//            Integer custId = customersData.getId();
            double overridQuota = 0d;
//            List<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findPostpaidPlanByname(request.getServiceId());
            List<Object[]> postpaidPlan = postpaidPlanRepo.findPostpaidPlanByName(request.getServiceId());

            if (postpaidPlan == null || postpaidPlan.isEmpty()) {
                RESP_CODE = HttpStatus.NOT_FOUND.value();
                response.put(APIConstants.MESSAGE, "Please enter a valid service");
                return apiResponse(RESP_CODE, response, null);
            }else if (Objects.nonNull(postpaidPlan) && postpaidPlan.stream()
                    .anyMatch(plan -> plan[1]!= null &&
                            (plan[1].toString().equalsIgnoreCase("Bandwidthbooster") ||
                                    plan[1].toString().equalsIgnoreCase("Volume Booster")))) {

                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.MESSAGE, "Please enter a valid service");
                return apiResponse(RESP_CODE, response, null);
            }
            if(Objects.nonNull(request.getOverrides())){
                overridQuota = dataConverter(postpaidPlan.get(0), request.getOverrides());
            }
            if (!CollectionUtils.isEmpty(postpaidPlan)) {
//                List<CustomerServiceMapping> customerServiceMapping1 = customerServiceMappingRepository.findByCustId(custId);
                List<Integer> customerServiceMapping1 = customerServiceMappingRepository.custServicemappingIdByCustId(custId);
                requestDTOs.setCustId(custId.longValue());

                //set deactivatePlanReqModel
                List<DeactivatePlanReqModel> deactivatePlanReqModelsData = new ArrayList<>();
                DeactivatePlanReqModel deactivatePlanReqModel = new DeactivatePlanReqModel();
                deactivatePlanReqModel.setBillToOrg(false);
                deactivatePlanReqModel.setNewPlanId((Integer) postpaidPlan.get(0)[0]);
                deactivatePlanReqModel.setCustServiceMappingId(customerServiceMapping1.get(0));
                deactivatePlanReqModel.setRemarks(request.getServiceId());
                deactivatePlanReqModelsData.add(deactivatePlanReqModel);
                requestDTOs.setDeactivatePlanReqModels(deactivatePlanReqModelsData);

                List<DeactivatePlanReqDTO> deactivatePlanReqDTO = new ArrayList<>();
                DeactivatePlanReqDTO deactivatePlanReqDTO1 = new DeactivatePlanReqDTO();
                deactivatePlanReqDTO1.setDeactivatePlanReqModels(deactivatePlanReqModelsData);
                deactivatePlanReqDTO1.setPlanGroupChange(false);
                deactivatePlanReqDTO1.setPlanGroupFullyChanged(false);
                deactivatePlanReqDTO1.setPaymentOwner("Admin");
                deactivatePlanReqDTO1.setPaymentOwnerId(2);
                deactivatePlanReqDTO1.setBillableCustomerId(custId);
                deactivatePlanReqDTO1.setIsParent(true);
                deactivatePlanReqDTO1.setCustId(custId);
                deactivatePlanReqModel.setUpdatedUsedQuota(overridQuota);
                deactivatePlanReqDTO.add(deactivatePlanReqDTO1);
                requestDTOs.setDeactivatePlanReqDTOS(deactivatePlanReqDTO);

                requestDTOs.setRecordPayment(null);


                Optional<Integer> custIdOptional = requestDTOs.getDeactivatePlanReqDTOS().stream().filter(DeactivatePlanReqDTO::getIsParent).map(DeactivatePlanReqDTO::getCustId).findFirst();
//                List<Integer> custIds = requestDTOs.getDeactivatePlanReqDTOS().stream().filter(i -> !i.getIsParent()).map(DeactivatePlanReqDTO::getCustId).collect(Collectors.toList());
              /*  Set<Customers> customersforInvoice = new HashSet<>();
                if (custIds != null && custIds.size() > 0) {
                    customersforInvoice.addAll(customersRepository.findAllById(custIds));
                }*/
                if (custIdOptional.isPresent()) {
                    custId = custIdOptional.get();
                } else {
                    custIdOptional = requestDTOs.getDeactivatePlanReqDTOS().stream().map(DeactivatePlanReqDTO::getCustId).findFirst();
                    if (custIdOptional.isPresent()) {
                        custId = custIdOptional.get();
                    }
                }
                Customers customers = customersRepository.findById(custId).get();
                Integer currentMvnoId = subscriberService.getLoggedInMvnoId(custId);
                Integer dataMvnoId = customers.getMvnoId();

                if (currentMvnoId == 1 || dataMvnoId.equals(currentMvnoId)) {

                    if (custId == null) {
                        throw new CustomValidationException(417, "Customer id can not be null!", null);
                    }

                    if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                        if (requestFrom.equals("pw") && !getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficient(requestDTOs.getDeactivatePlanReqDTOS().get(0))) {
                            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                            response.put(APIConstants.ERROR_TAG, "Partner has Insufficient balance!");
                            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivate Plan In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            return apiResponse(RESP_CODE, response, null);
                        }

                        if (requestFrom.equals("pw") && getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficientForLCO(requestDTOs.getDeactivatePlanReqDTOS().get(0))) {
                            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                            response.put(APIConstants.ERROR_TAG, "Partner has Insufficient balance!");
                            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create deactivate Plan In Bulk" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            return apiResponse(RESP_CODE, response, null);
                        }
                    }
                    //  Customers customers = customersService.get(requestDTOs.getCustId());
                    //            Customers customers = customersService.get(custId);
//                    customersforInvoice.add(customers);
                 /*   DebitDocument debitDocuments = debitDocRepository.findTopByCustomerAndBillrunstatus(customers.getId(), "VOID");

                    if (requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate() != null && (debitDocuments != null || !requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date")) && customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
                        requestDTOs.getDeactivatePlanReqDTOS().get(0).setChangePlanDate("Today");
                    }*/
                    DeactivatePlanReqDTOList result = new DeactivatePlanReqDTOList();
                    if (customers.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVE)) {
                        if(Objects.isNull(requestDTOs.getSkipQuotaUpdate())){
                            requestDTOs.setSkipQuotaUpdate(true);
                        }
                        result = subscriberService.deActivatePlanInList(requestDTOs);
                        overrideResponse = "SUCCESS";
                    }


                    /*  this is for post paid changePLan next bill date */
                    Optional<Customers> updatedCustomer = customersRepository.findById(customers.getId());
                    LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(updatedCustomer.get(), customers.getNextBillDate());
                    if(nextQuotaReset != null) {
                        updatedCustomer.get().setNextQuotaResetDate(nextQuotaReset);
                    } else {
                        updatedCustomer.get().setNextQuotaResetDate(LocalDate.now());
                    }
                    customersRepository.save(updatedCustomer.get());
                    for (DeactivatePlanReqDTO reqDTO : requestDTOs.getDeactivatePlanReqDTOS()) {
                        List<Integer> custServiceMappingIdList = reqDTO.getDeactivatePlanReqModels().stream().map(DeactivatePlanReqModel::getCustServiceMappingId).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(custServiceMappingIdList)) {
                            List<CustomerServiceMapping> customerServiceMappings = customerServiceMappingRepository.findAllByIdIn(custServiceMappingIdList);
                            if (!CollectionUtils.isEmpty(customerServiceMappings)) {
                                customerServiceMappings = customerServiceMappings.stream().peek(customerServiceMapping -> customerServiceMapping.setCustId(reqDTO.getCustId())).collect(Collectors.toList());
                                customerServiceMappingRepository.saveAll(customerServiceMappings);
                            }
                        }
                    }
                    Long endtime = System.currentTimeMillis();
                    logger.warn("time is taken by change plan is : " +( endtime - starttime));
                    response.put("changeService", result);
                    response.put("override", overrideResponse);
                    RESP_CODE = APIConstants.SUCCESS;
                } else {
                    RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                    response.put(APIConstants.ERROR_TAG, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
                    logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Change Plan " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + Constants.MVNO_DELETE_UPDATE_ERROR_MSG + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                }
            } else {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, "Service ID Not Available");
            }
        } catch (CustomValidationException ex) {
            ex.printStackTrace();
            RESP_CODE = ex.getErrCode();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            //logger.error("Unable to change plan  for " + requestDTOs.getCustId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            //logger.error("Unable to createRecordPayment  for " + requestDTOs.getCustId() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        String SUBMODULE = MODULE + " [apiResponse()] ";
        try {
            //logger.info(new ObjectMapper().writeValueAsString(response));
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

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
            } else if (responseCode.equals(APIConstants.NO_CONTENT)) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Customer not found");
            }else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            //ApplicationLogger.logger.error(SUBMODULE + e.getStackTrace(), e);
            e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getStackTrace());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Double dataConverter(Object[] postpaidPlan, Double overrides) {
        if (postpaidPlan == null || postpaidPlan[2] == null) {
            throw new IllegalArgumentException("PostpaidPlan or QuotaUnit cannot be null");
        }
        switch (postpaidPlan[2].toString().toUpperCase()) {
            case "GB":
                overrides = overrides / (1024.0 * 1024.0 * 1024.0);
                System.out.println("Quota in GB : " + overrides);
                return overrides;
            case "MB":
                overrides = overrides / (1024.0 * 1024.0);
                System.out.println("Quota in MB : " + overrides);
                return overrides;
            default:
                throw new UnsupportedOperationException("Unsupported QuotaUnit: " + postpaidPlan[2]);
        }
    }

    public int getLoggedInUserPartnerId() {
        int partnerId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
            partnerId = -1;
        }
        return partnerId;
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
}
