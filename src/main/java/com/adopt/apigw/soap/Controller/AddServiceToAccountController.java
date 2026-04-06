package com.adopt.apigw.soap.Controller;

import com.adopt.apigw.constants.*;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.subscriber.controller.SubscriberController;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqDTO;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqDTOList;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqModel;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.repository.common.CustQuotaRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.schedulers.QuotaResetScheduler;
import com.adopt.apigw.schedulers.UpdateCustomerQuotaDto;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.soap.Dto.AddServiceToAccountRequest;
import com.adopt.apigw.soap.Dto.ChangeServiceRequest;
import com.adopt.apigw.soap.Dto.ChangeServiceSubRequest;
import com.adopt.apigw.soap.Dto.Override;
import com.adopt.apigw.soap.Services.ChangePlanService;
import com.adopt.apigw.soap.SoapConstant.SoapConstant;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(UrlConstants.BASE_API_URL + SoapConstant.SOAP_URL)
public class AddServiceToAccountController {


    private static String MODULE = " [AddServiceToAccountController] ";
    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private CustomersService customersService;
    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private CustomersRepository customersRepository;

    private final Logger logger = LoggerFactory.getLogger(SubscriberController.class);
    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;
    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;
    @Autowired
    CustQuotaRepository custQuotaRepository;
    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    private QuotaResetScheduler quotaResetScheduler;
    @Autowired
    CustomersRepository customerRepository;
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    CustMacMapppingRepository custMacMapppingRepository;

    @Autowired
    ChangePlanService changeplanservice;

    /*  @PreAuthorize("validatePermission(\"" + MenuConstants.PrepaidCustomers.PREPAID_CUSTOMER_CHANGE_PLAN + "\"," +
              "\"" + MenuConstants.PostpaidCustomers.POSTPAID_CUSTOMER_CHANGE_PLAN + "\"," +
              "\"" + MenuConstants.PrepaidCustomerCAF.PREPAID_CAF_CUSTOMER_CHANGE_PLAN + "\"," +
              "\"" + MenuConstants.PostpaidCustomerCAF.POSTPAID_CAF_CUSTOMER_CHANGE_PLAN + "\")")*/
    @PostMapping(value = "/addServiceToAccount")
    public ResponseEntity<?> deactivatePlanInBulk(@RequestBody AddServiceToAccountRequest request, @RequestParam(name = "mvnoid", required = false) Long mvnoid, HttpServletRequest req) {
        logger.debug("In addServiceToAccount : " + request.getServiceId().trim() + " " + request.getUserName());
        MDC.put("type", "Create");
        Integer RESP_CODE = APIConstants.FAIL;
        String requestFrom = "bss";
        HashMap<String, Object> response = new HashMap<>();
        DeactivatePlanReqDTOList requestDTOs = new DeactivatePlanReqDTOList();
        Thread invoiceThread = null;
        String serviceid = request.getServiceId().trim();
        try {
//            Customers customersData = customersRepository.findByUserName(request.getUserName().trim());
            Integer custId = customersRepository.findCustIdByUserName(request.getUserName().trim());
            if (custId == null) {
                response.put("message", "Username Not available");
                return apiResponse(SoapConstant.SUCCESS_CODE, response, null);
            }
//            List<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findPostpaidPlanByname(serviceid);
            List<Object[]> postpaidPlan = postpaidPlanRepo.findPostpaidPlanByName(request.getServiceId());
            if (postpaidPlan == null || postpaidPlan.isEmpty()) {
                response.put("message", "ServiceId Not available");
                return apiResponse(SoapConstant.SUCCESS_CODE, response, null);
            }

            if (Objects.nonNull(postpaidPlan) && postpaidPlan.stream()
                    .anyMatch(plan -> plan[1] != null && (plan[1].toString().equalsIgnoreCase("Bandwidthbooster") || plan[1].toString().equalsIgnoreCase("Volume Booster")))) {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.MESSAGE, "Please enter a valid service");
                return apiResponse(RESP_CODE, response, null);
            }
//            Integer custId = customersData.getId();
//            List<CustomerServiceMapping> customerServiceMapping1 = customerServiceMappingRepository.findByCustId(custId);
            List<Integer> customerServiceMapping1 = customerServiceMappingRepository.custServicemappingIdByCustId(custId);
            requestDTOs.setCustId(custId.longValue());

            //set deactivatePlanReqModel
            List<DeactivatePlanReqModel> deactivatePlanReqModelsData = new ArrayList<>();
            DeactivatePlanReqModel deactivatePlanReqModel = new DeactivatePlanReqModel();
            deactivatePlanReqModel.setBillToOrg(false);
            deactivatePlanReqModel.setNewPlanId((Integer) postpaidPlan.get(0)[0]);
            deactivatePlanReqModel.setCustServiceMappingId(customerServiceMapping1.get(0));
            deactivatePlanReqModel.setRemarks(serviceid);
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
            deactivatePlanReqDTO.add(deactivatePlanReqDTO1);
            requestDTOs.setDeactivatePlanReqDTOS(deactivatePlanReqDTO);

            requestDTOs.setRecordPayment(null);


            Optional<Integer> custIdOptional = requestDTOs.getDeactivatePlanReqDTOS().stream().filter(DeactivatePlanReqDTO::getIsParent).map(DeactivatePlanReqDTO::getCustId).findFirst();
            List<Integer> custIds = requestDTOs.getDeactivatePlanReqDTOS().stream().filter(i -> !i.getIsParent()).map(DeactivatePlanReqDTO::getCustId).collect(Collectors.toList());
//            Set<Customers> customersforInvoice = new HashSet<>();
//            if (custIds != null && custIds.size() > 0) {
//                customersforInvoice.addAll(customersRepository.findAllById(custIds));
//            }
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
//                customersforInvoice.add(customers);
//                DebitDocument debitDocuments = debitDocRepository.findTopByCustomerAndBillrunstatus(customers.getId(), "VOID");

//                if (requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate() != null && (debitDocuments != null || !requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date")) && customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
//                    requestDTOs.getDeactivatePlanReqDTOS().get(0).setChangePlanDate("Today");
//                }
                DeactivatePlanReqDTOList result = new DeactivatePlanReqDTOList();
                if (customers.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVE)) {
                    if (Objects.isNull(requestDTOs.getSkipQuotaUpdate())) {
                        requestDTOs.setSkipQuotaUpdate(true);
                    }
                    result = subscriberService.deActivatePlanInList(requestDTOs);
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

                response.put("deActivateResponse", result);
                RESP_CODE = APIConstants.SUCCESS;
                //  logger.info("createRecordPayment  for " + custId + ":  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, response);
            } else {
                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.ERROR_TAG, Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Unable to Change Plan " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + Constants.MVNO_DELETE_UPDATE_ERROR_MSG + LogConstants.LOG_STATUS_CODE + RESP_CODE);

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
            } else {
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

    @PostMapping(value = "/changeService")
    public ResponseEntity<?> changeService(@RequestBody ChangeServiceRequest request, @RequestParam(name = "mvnoid", required = false) Long mvnoid, HttpServletRequest req) {
        logger.debug("In changeService : " + request.getServiceId().trim());
        return changeplanservice.changePlan(request, mvnoid, req);

    }

    public String overrideResetQuota(ChangeServiceRequest request, Long mvnoId) {
        logger.debug("In overrideResetQuota :" + request.getUserName());
        LocalDateTime todayDate = LocalDateTime.now();
        HashMap<String, Object> response = new HashMap<>();
        Double qouta = request.getOverrides();
        String responsemessage = "Failure";
        try {
            Customers customers = customerRepository.findByUsernameAndMvnoId(request.getUserName().toLowerCase().trim(), Math.toIntExact(mvnoId)).
                    orElseThrow(() -> new EntityNotFoundException("Customer not found"));
            List<PostpaidPlan> plan = postpaidPlanRepo.findPostpaidPlanByname(request.getServiceId());
            List<CustPlanMappping> planMapping = custPlanMappingRepository.findByPostpaidPlanId(plan.get(0).getId());
            if (plan == null || plan.isEmpty() || planMapping.isEmpty()) {
                responsemessage = "ServiceId Not available";
                return responsemessage;
            }
            int lastIndex = planMapping.size() - 1;
            Integer lastPlanMappingId = planMapping.get(lastIndex).getId();
            CustQuotaDetails custQuotaDetails = custQuotaRepository.findCustQuotaDetailsByCustPackId(lastPlanMappingId);

//            PostpaidPlan postpaidPlan = custQuotaDetails.getPostpaidPlan();
            List<Object[]> postpaidPlan = postpaidPlanRepo.findPostpaidPlanByName(request.getServiceId());
            UpdateCustomerQuotaDto updateCustomerDto = new UpdateCustomerQuotaDto();
            updateCustomerDto.setCustId(customers.getId());
            updateCustomerDto.setMvnoId(mvnoId);
            updateCustomerDto.setSkipQuotaUpdate(custQuotaDetails.getSkipQuotaUpdate());
            Double volumeBasedTotalQuota = custQuotaDetails.getTotalQuotaKB();
            boolean isUpdated = false;
            Double convertedQuota = dataConverter(postpaidPlan.get(0), request.getOverrides());
            if (custQuotaDetails.getPostpaidPlan().getQuotatype().equals("Data")) {

                custQuotaDetails.setTotalQuotaKB(volumeBasedTotalQuota);
                custQuotaDetails.setUsedQuotaKB(request.getOverrides() / 1024);
                custQuotaDetails.setUsedQuota(convertedQuota);
                updateCustomerDto.setUserName(customers.getUsername());
                updateCustomerDto.setCustId(customers.getId());
                updateCustomerDto.setQuotaDetailId(custQuotaDetails.getId());
                updateCustomerDto.setUsedQuota(convertedQuota);
                updateCustomerDto.setUsedQuotaKB(request.getOverrides() / 1024);
                custQuotaDetails.setLastQuotaReset(todayDate);
                quotaResetScheduler.updateCustomerInRadius(updateCustomerDto);
                custQuotaRepository.save(custQuotaDetails);
                return "SUCCESS";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : " + e.getMessage());
        }
        return responsemessage;
    }

    private Double dataConverter(Object[] postpaidPlan, Double overrides) {
        logger.debug("In DataConverter Method: " + postpaidPlan);
        if (postpaidPlan == null ||postpaidPlan[2] == null) {
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
                throw new UnsupportedOperationException("Unsupported QuotaUnit: " +  postpaidPlan[2]);
        }
    }

    @PostMapping(value = "/applyServicesToSubAcct")
    public ResponseEntity<?> changeAndApplyService(@RequestBody ChangeServiceSubRequest request, @RequestParam(name = "mvnoid", required = false) Long mvnoid, HttpServletRequest req) {
        logger.debug("In changeAndApplyService Controller: " + request.getServiceId().trim() + " " + request.getUserName().trim());
        MDC.put("type", "Create");
        Integer RESP_CODE = APIConstants.FAIL;
        String requestFrom = "bss";
        HashMap<String, Object> response = new HashMap<>();
        DeactivatePlanReqDTOList requestDTOs = new DeactivatePlanReqDTOList();
        Thread invoiceThread = null;
        String overrideResponse = "Failed to Override";
        try {
            Customers customersData = customersRepository.findByUserName(request.getUserName());

            Integer custId = customersData.getId();
            if (custId == null) {
                response.put(APIConstants.MESSAGE, "customer not found");
                logger.error(LogConstants.USERNAME_NOT_FOUND + request.getUserName());
                response.put("responseCode", APIConstants.NO_CONTENT);
                RESP_CODE = APIConstants.SUCCESS;
                return apiResponse(RESP_CODE, response, null);
            } else {
                // Get quota details
                Long startQuota = System.currentTimeMillis();
                List<Integer> custQuotaDetails1 = custQuotaRepository.findIdsByCustomerId(custId);
                Long endtQuota = System.currentTimeMillis();
                logger.warn("time is taken by find cust quota is : " +( endtQuota - startQuota));

                if (custQuotaDetails1 == null) {
                    response.put(APIConstants.MESSAGE, "QuotaDtls Not found");
                    response.put("responseCode", APIConstants.NO_CONTENT);
                    RESP_CODE = APIConstants.SUCCESS;
                    return apiResponse(RESP_CODE, response, null);
                }
            }

            Double overridQuota = 0.0;
//            List<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findPostpaidPlanByname(request.getServiceId());
            List<Object[]> postpaidPlan = postpaidPlanRepo.findPostpaidPlanByName(request.getServiceId());

            if (Objects.nonNull(request.getOverrides()) && request.getOverrides().size() > 0 && !request.getOverrides().get(0).getValue().isEmpty()) {
                overridQuota = Math.abs(Double.valueOf(request.getOverrides().get(0).getValue()));
                overridQuota = dataConverter(postpaidPlan.get(0), overridQuota);
            }
            if (Objects.nonNull(postpaidPlan) && postpaidPlan.stream()
                    .anyMatch(plan ->plan[1] != null &&
                            (plan[1].toString().equalsIgnoreCase("Bandwidthbooster") ||
                                    plan[1].toString().equalsIgnoreCase("Volume Booster")))) {

                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
                response.put(APIConstants.MESSAGE, "Please enter a valid service");
                return apiResponse(RESP_CODE, response, null);
            }
            if (!CollectionUtils.isEmpty(postpaidPlan)) {
                List<CustomerServiceMapping> customerServiceMapping1 = customerServiceMappingRepository.findByCustId(custId);

                requestDTOs.setCustId(customersData.getId().longValue());

                //set deactivatePlanReqModel
                List<DeactivatePlanReqModel> deactivatePlanReqModelsData = new ArrayList<>();
                DeactivatePlanReqModel deactivatePlanReqModel = new DeactivatePlanReqModel();
                deactivatePlanReqModel.setBillToOrg(false);
                deactivatePlanReqModel.setNewPlanId((Integer) postpaidPlan.get(0)[0]);
                deactivatePlanReqModel.setCustServiceMappingId(customerServiceMapping1.get(0).getId());
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
                deactivatePlanReqModel.setUpdatedUsedQuota(overridQuota);
                deactivatePlanReqDTO1.setIsParent(true);
                deactivatePlanReqDTO1.setCustId(custId);
                deactivatePlanReqDTO.add(deactivatePlanReqDTO1);
                requestDTOs.setDeactivatePlanReqDTOS(deactivatePlanReqDTO);


                requestDTOs.setRecordPayment(null);


                Optional<Integer> custIdOptional = requestDTOs.getDeactivatePlanReqDTOS().stream().filter(DeactivatePlanReqDTO::getIsParent).map(DeactivatePlanReqDTO::getCustId).findFirst();
                List<Integer> custIds = requestDTOs.getDeactivatePlanReqDTOS().stream().filter(i -> !i.getIsParent()).map(DeactivatePlanReqDTO::getCustId).collect(Collectors.toList());
               /* Set<Customers> customersforInvoice = new HashSet<>();
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
                Customers customers =customersRepository.findById(custId).get();
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
//                    DebitDocument debitDocuments = debitDocRepository.findTopByCustomerAndBillrunstatus(customers.getId(), "VOID");

//                    if (requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate() != null && (debitDocuments != null || !requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date")) && customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
//                        requestDTOs.getDeactivatePlanReqDTOS().get(0).setChangePlanDate("Today");
//                    }
                    DeactivatePlanReqDTOList result = new DeactivatePlanReqDTOList();
                    if (customers.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVE)) {
                        if (Objects.isNull(requestDTOs.getSkipQuotaUpdate())) {
                            requestDTOs.setSkipQuotaUpdate(true);
                        }
                        result = subscriberService.deActivatePlanInList(requestDTOs);
                        overrideResponse = "SUCCESS";
                    }
                    List<DeactivatePlanReqDTO> list = result.getDeactivatePlanReqDTOS();
                    if (!CollectionUtils.isEmpty(list)) {
                        List<List<Integer>> lists = new ArrayList<>();
                        for (DeactivatePlanReqDTO model : list) {
                            List<DeactivatePlanReqModel> deactivatePlanReqModels = model.getDeactivatePlanReqModels();
                            List<List<Integer>> list1 = deactivatePlanReqModels.stream().map(DeactivatePlanReqModel::getDebitDocIds).collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(list1))
                                lists.addAll(list1);
                        }

                        List<Integer> debitDocIds = lists.stream().filter(Objects::nonNull).flatMap(List::stream).collect(Collectors.toList());
                    }
//                    String paymentOwner = requestDTOs.getDeactivatePlanReqDTOS().stream().map(DeactivatePlanReqDTO::getPaymentOwner).findFirst().orElse(null);
//                    Integer paymentOwnerId = requestDTOs.getDeactivatePlanReqDTOS().stream().map(DeactivatePlanReqDTO::getPaymentOwnerId).findFirst().orElse(null);
                    //            debitDocService.createInvoice(customers, null, 200, new HashSet<Integer>(debitDocIds), null, paymentOwner, paymentOwnerId, false, CommonConstants.INVOICE_TYPE.CHANGE_PLAN);

                    /*  this is for post paid changePLan next bill date */
                    boolean changePlanNextBillDate = false;
                    if (requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate() != null && requestDTOs.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date") && customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_POSTPAID)) {
                        changePlanNextBillDate = true;
                    }
               /*     if (customersforInvoice.size() > 1) {
                        Integer parentId = custIdOptional.get();
                        List<Integer> childIds = custIds;
                        childIds.removeIf(i -> i.equals(parentId));
                        debitDocService.createInvoice(customersforInvoice, CommonConstants.INVOICE_TYPE.CHANGE_PLAN, parentId, childIds, requestDTOs.getRecordPayment(), null, changePlanNextBillDate,false,null);
                    } else {
                        debitDocService.createInvoice(customers, CommonConstants.INVOICE_TYPE.CHANGE_PLAN, "", requestDTOs.getRecordPayment(), null, null, changePlanNextBillDate);
                    }*/
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
                    //TODO: added timer to don't conflict bor bill to subisu invoice, need to find solution
              /*      for (DeactivatePlanReqDTO reqDTO : requestDTOs.getDeactivatePlanReqDTOS()) {
                        if (reqDTO.getDeactivatePlanReqModels().get(0).isBillToOrg()) {
                            Thread.sleep(2000);
                            subscriberService.orgCustInvoiceForChangePlan(customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext()), reqDTO);
                        }
                    }*/

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

    public String overrideResetQuota(ChangeServiceSubRequest request, Long mvnoId) {
        logger.debug("OverrideResetQuota method :" + request.getUserName());
        LocalDateTime todayDate = LocalDateTime.now();
        HashMap<String, Object> response = new HashMap<>();
        List<Override> qouta = request.getOverrides();
        String responsemessage = "Failure";
        try {
            Customers customers = customerRepository.findByUsernameAndMvnoId(request.getUserName().toLowerCase().trim(), Math.toIntExact(mvnoId))
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

            List<PostpaidPlan> plan = postpaidPlanRepo.findPostpaidPlanByname(request.getServiceId());

            List<CustPlanMappping> planMapping = custPlanMappingRepository.findByPostpaidPlanId(plan.get(0).getId());

            if (plan == null || plan.isEmpty() || planMapping.isEmpty()) {
                responsemessage = "ServiceId Not available";
                return responsemessage;
            }

            int lastIndex = planMapping.size() - 1;
            Integer lastPlanMappingId = planMapping.get(lastIndex).getId();
            CustQuotaDetails custQuotaDetails = custQuotaRepository.findCustQuotaDetailsByCustPackId(lastPlanMappingId);

            PostpaidPlan postpaidPlan = custQuotaDetails.getPostpaidPlan();
            UpdateCustomerQuotaDto updateCustomerDto = new UpdateCustomerQuotaDto();
            updateCustomerDto.setCustId(customers.getId());
            updateCustomerDto.setMvnoId(mvnoId);
            updateCustomerDto.setSkipQuotaUpdate(custQuotaDetails.getSkipQuotaUpdate());

            Double volumeBasedTotalQuota = custQuotaDetails.getTotalQuotaKB();
            boolean isUpdated = false;

            // Assuming the first override contains the quota value
            if (!request.getOverrides().isEmpty()) {
                String overrideValue = request.getOverrides().get(0).getValue();
                Double quotaValue = Double.parseDouble(overrideValue);

                //    List<Override> convertedQuota = quotaConverter(postpaidPlan, request.getOverrides());

                if (custQuotaDetails.getPostpaidPlan().getQuotatype().equals("Data")) {
                    List<Override> convertedQuota = quotaConverter(postpaidPlan, request.getOverrides());

                    // Extract the first converted value
                    Double usedQuotaValue = convertedQuota.isEmpty()
                            ? 0.0
                            : Double.parseDouble(convertedQuota.get(0).getValue());

                    custQuotaDetails.setTotalQuotaKB(volumeBasedTotalQuota);
                    custQuotaDetails.setUsedQuotaKB(usedQuotaValue / 1024);
                    custQuotaDetails.setUsedQuota(usedQuotaValue);

                    updateCustomerDto.setUserName(customers.getUsername());
                    updateCustomerDto.setCustId(customers.getId());
                    updateCustomerDto.setQuotaDetailId(custQuotaDetails.getId());
                    updateCustomerDto.setUsedQuotaKB(usedQuotaValue / 1024);

                    custQuotaDetails.setLastQuotaReset(todayDate);
                    custQuotaRepository.save(custQuotaDetails);
                    quotaResetScheduler.updateCustomerInRadius(updateCustomerDto);
                    return "SUCCESS";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error message : " + e.getMessage());
        }
        return responsemessage;
    }

    private List<Override> quotaConverter(PostpaidPlan postpaidPlan, List<Override> overrides) {
        logger.debug("In QuotaConverter method:" + postpaidPlan);
        if (postpaidPlan == null || postpaidPlan.getQuotaUnit() == null) {
            throw new IllegalArgumentException("PostpaidPlan or QuotaUnit cannot be null");
        }

        List<Override> convertedOverrides = new ArrayList<>();

        for (Override override : overrides) {
            Double originalValue = Double.parseDouble(override.getValue());
            Double convertedValue;

            switch (postpaidPlan.getQuotaUnit().toUpperCase()) {
                case "GB":
                    convertedValue = originalValue / (1024.0 * 1024.0 * 1024.0);
                    break;
                case "MB":
                    convertedValue = originalValue / (1024.0 * 1024.0);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported QuotaUnit: " + postpaidPlan.getQuotaUnit());
            }

            Override convertedOverride = new Override();
            convertedOverride.setName(override.getName()); // This will set the overrideName
            convertedOverride.setValue(convertedValue.toString()); // This will set the overrideValue
            convertedOverrides.add(convertedOverride);
        }

        return convertedOverrides;
    }

}
