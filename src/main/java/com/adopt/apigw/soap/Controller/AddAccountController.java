package com.adopt.apigw.soap.Controller;


import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.CustIpMapping;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.modules.Alert.smsScheduler.service.SmsSchedulerService;
import com.adopt.apigw.modules.CustomerMacMgmt.Service.CustMacMgmtService;
import com.adopt.apigw.modules.Customers.Services.CustIpMgmtService;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.repository.PopManagementRepository;
import com.adopt.apigw.modules.LocationMaster.domain.CustomerLocationMapping;
import com.adopt.apigw.modules.LocationMaster.domain.LocationMaster;
import com.adopt.apigw.modules.LocationMaster.repository.CustomerLocationRepository;
import com.adopt.apigw.modules.LocationMaster.repository.LocationMasterRepository;
import com.adopt.apigw.modules.NetworkDevices.repository.NetworkDeviceRepository;
import com.adopt.apigw.modules.Notification.service.NotificationService;
import com.adopt.apigw.modules.Pincode.repository.PincodeRepository;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.planUpdate.repository.CustomerPackageRepository;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.modules.subscriber.model.*;
import com.adopt.apigw.modules.subscriber.service.ReceiptThread;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.message.CustomMessage;
import com.adopt.apigw.rabbitMq.message.CustomerMessageIn;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.*;
import com.adopt.apigw.repository.LeadMasterRepository;
import com.adopt.apigw.repository.common.CustIpMappingRepo;
import com.adopt.apigw.repository.common.CustQuotaRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.schedulers.QuotaResetScheduler;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.soap.Dto.*;
import com.adopt.apigw.soap.Services.ChangePlanService;
import com.adopt.apigw.soap.Services.IntegrationDataService;
import com.adopt.apigw.soap.SoapConstant.SoapConstant;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.google.gson.Gson;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;


@Slf4j
@RestController
@RequestMapping(UrlConstants.BASE_API_URL + SoapConstant.SOAP_URL)
public class AddAccountController extends ApiBaseController {
    private final Logger log = LoggerFactory.getLogger(AddAccountController.class);
    private static String MODULE = " [AddAccountController] ";

    @Autowired
    private Tracer tracer;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private SmsSchedulerService smsSchedulerService;

    @Autowired
    LocationMasterRepository locationMasterRepository;

    @Autowired
    private CustomerLocationRepository customerLocationRepository;

    @Autowired
    private CustomerLocationMapper customerLocationMapper;

    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private NetworkDeviceRepository networkDeviceRepository;

    @Autowired
    private PopManagementRepository popManagementRepository;

    @Autowired
    private LeadMasterRepository leadMasterRepository;
    @Autowired
    CustomersRepository customerRepository;
    @Autowired
    CustQuotaRepository custQuotaRepository;
    @Autowired
    CustMacMapppingRepository custMacMapppingRepository;

    @Autowired
    CustIpMappingRepo custIpMappingRepo;

    private final Logger logger = LoggerFactory.getLogger(AddAccountController.class);
    @Autowired
    private CustomerPackageRepository customerPackageRepository;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private QuotaResetScheduler quotaResetScheduler;
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private ServiceAreaService serviceAreaService;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CreateDataSharedService createDataSharedService;
    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private PostpaidPlanService postpaidPlanService;
    @Autowired
    private CustChargeService custChargeService;
    @Autowired
    private BillRunService billRunService;
    @Autowired
    private DebitDocService debitDocService;
    @Autowired
    private IntegrationDataService integrationDataService;

    @Autowired
    NotificationTemplateRepository templateRepository;
    @Autowired
    private CustPlanMappingService custPlanMappingService;
    @Autowired
    private CreditDocRepository creditDocRepository;
    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    ChangePlanService changeplanservice;

    @Autowired
    private CustMacMgmtService custMacMgmtService;

    @Autowired
    private CustIpMgmtService custIpMgmtService;

//    private static final Logger log = LoggerFactory.getLogger(AddAccountController.class);

    //    @PreAutho rize("validatePermission(\"" + MenuConstants.SoapApi.AddAccount "\")")
    @PostMapping("/customers")
    public ResponseEntity<?> createCustomers(@Valid @RequestBody wsAddAccount request,
                                             @RequestParam(name = "serviceArea", required = false) Long serviceareaId,
                                             @RequestParam(name = "mvnoid", required = false) Long mvnoid,
                                             @RequestParam(name = "plan", required = false) String plan, HttpServletRequest req) throws Exception {
        long startTime = System.currentTimeMillis();
//        TraceContext traceContext = tracer.currentSpan().context();
        LoggedInUser loggedInUser = getLoggedInUser();
        MDC.put("type", "Create");
        MDC.put("userName", loggedInUser.getUsername());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        String requestFrom = "bss";
        CustomersPojo pojo = new CustomersPojo();

        try {
//            PostpaidPlan postpaidPlancheck = postpaidPlanRepo.findPostpaidPlanBynameAndMvnoId(request.getServiceId(), Math.toIntExact(mvnoid));
//            if(Objects.isNull(postpaidPlancheck) || !postpaidPlancheck.getStatus().equalsIgnoreCase("Active")){
//                RESP_CODE = 404;
//                response.put(APIConstants.ERROR_TAG, "Package not Available");
//                return apiResponse(RESP_CODE, response);
//            }
            pojo.setUsername(request.getUserName());
            String cui = request.getUserName();
            Boolean UsernameExist = customerRepository.customerUsernameIsAlreadyExists(request.getUserName(), mvnoid);
            if (UsernameExist) {
                response.put("message", "username is already exist");
                log.error(LogConstants.USERNAME_ALREADY_EXIST + request.getUserName().trim());
                return apiResponse(SoapConstant.SUCCESS_CODE, response);
            }
            pojo.setPassword(request.getPassword());
            pojo.setPlanName(request.getServiceId());
            for (Item item : request.getItem()) {
                if (item.getKey().equalsIgnoreCase(SoapConstant.CUSTOMERSTATUS)) {
                    String customerStatus = item.getValue();
                    if ("y".equalsIgnoreCase(customerStatus)) {
                        pojo.setStatus("Active");
                    } else if ("n".equalsIgnoreCase(customerStatus)) {
                        pojo.setStatus("Inactive");
                    } else if ("suspend".equalsIgnoreCase(customerStatus)) {
                        pojo.setStatus("Suspend");
                    }
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.PARAM2)) {
                    String subNetMast = item.getValue();
                    pojo.setFramedIPNetmask(subNetMast);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.CONCURRENTLOGINPOLICY)) {
                    if (!item.getValue().isEmpty() && item.getValue() != null) {
                        Integer concurrancy = Integer.valueOf(item.getValue());
                        pojo.setMaxconcurrentsession(concurrancy);
                    } else {
                        pojo.setMaxconcurrentsession(1);
                    }
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.ADDITIONALPOLICY)) {
                    if (item.getValue() != null && !item.getValue().isEmpty()) {
                        Integer billDay = Integer.valueOf(item.getValue());
                        pojo.setBillday(billDay);
                    }
                    pojo.setCusttype("Postpaid");
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.PARAM3)) {
                    String frameRoute = item.getValue();
                    pojo.setFramedroute(frameRoute);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.PARAM4)) {
                    String nasIp = item.getValue();
                    if (!nasIp.isEmpty() && nasIp != null) {
                        String regex = "^(0:92=\")\\[(.*)\\]\"$";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(nasIp);
                        if (matcher.find()) {
                            if (matcher.groupCount() >= 2) { // Ensure there are at least two groups
                                String captureNumeric = matcher.group(1); // Get the first capturing group (0:92=")
                                String extractedData = matcher.group(2); // Get the second capturing group (content inside the brackets)
                                pojo.setNasPortId(extractedData);
                            } else {
                                response.put("message", "invalid location lock");
                                return apiResponse(SoapConstant.SUCCESS_CODE, response);
                            }
                        } else {
                            response.put("message", "invalid location lock");
                            return apiResponse(SoapConstant.SUCCESS_CODE, response);
                        }
                    } else {
                        pojo.setNasPortId(null);
                    }
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.PARAM6)) {
                    String gatewayIp = item.getValue();
                    pojo.setGatewayIP(gatewayIp);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.PARAM7)) {
                    boolean dnd = item.getValue().equals("1");
                    pojo.setIsNotificationEnable(!dnd);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.GROUPNAME)) {
                    String framedIpv6 = item.getValue();
                    pojo.setFramedIpv6Address(framedIpv6);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.CUSTOMERREPLYITEM)) {
                    String delegatedPrefix = "";
                    if (!item.getValue().isEmpty() && item.getValue() != null) {
                        delegatedPrefix = item.getValue().substring(item.getValue().indexOf('=') + 1);
                    }
                    pojo.setDelegatedprefix(delegatedPrefix);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.CALLINGSTATIONID) && item.getValue() != null && item.getValue().trim().length() > 0) {
                    String callingStationId = item.getValue();
                    boolean isMacValid = isMacValid(callingStationId);
                    if (isMacValid && custMacMapppingRepository.existsByMacAddressInAndIsDeletedIsFalse(Collections.singletonList(callingStationId))) {
                        RESP_CODE = 412;
                        response.put(APIConstants.ERROR_TAG, SoapConstant.CALLINGSTATIONID + " is already available and in-use, please use alternate mac: " + callingStationId);
                        return apiResponse(RESP_CODE, response);
                    } else if (isIpValid(callingStationId)) {
                        if (custIpMgmtService.isIpExists(callingStationId, null)) {
                            RESP_CODE = 412;
                            response.put(APIConstants.ERROR_TAG, "Param1 already exists for another account:" + callingStationId);
                            return apiResponse(RESP_CODE, response);
                        }
                    }
                    if (isMacValid)
                        pojo.setMACADDRESS(callingStationId);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.PARAM1) && item.getValue() != null && item.getValue().trim().length() > 0) {
                    String fraIp = item.getValue();
                    if (custIpMgmtService.isIpExists(fraIp, null)) {
                        RESP_CODE = 412;
                        response.put(APIConstants.ERROR_TAG, "Param1 already exists for another account: " + fraIp);
                        return apiResponse(RESP_CODE, response);
                    }
                    pojo.setFramedIp(fraIp);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.CUI) && item.getValue() != null && item.getValue().trim().length() > 0) {
                    cui = item.getValue();
                    pojo.setAcctno(cui);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.CUSTOMERALTEMAILID) && item.getValue() != null && item.getValue().trim().length() > 0) {
                    String email = item.getValue();
                    pojo.setEmail(email);
                    pojo.setAltemail(email);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.MSISDN) && item.getValue() != null && item.getValue().trim().length() > 0) {
                    String mobile = item.getValue();
                    pojo.setMobile(mobile);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.GEOLOCATION)) {
                    String vlan = item.getValue();
                    pojo.setVlan_id(vlan);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.CREATEDATE)) {
                    String date = item.getValue();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
                    pojo.setCreatedate(dateTime);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.PRIMARY_DNS)) {
                    String dns = item.getValue();
                    pojo.setPrimaryDNS(dns);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.PRIMARY_IPV6_DNS)) {
                    String ipv6_dns = item.getValue();
                    pojo.setPrimaryIPv6DNS(ipv6_dns);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.SECONDARY_DNS)) {
                    String secondary_dns = item.getValue();
                    pojo.setSecondaryDNS(secondary_dns);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.SECONDARY_IPV6_DNS)) {
                    String secondary_ipv6_dns = item.getValue();
                    pojo.setSecondaryIPv6DNS(secondary_ipv6_dns);
                }
                if (pojo.getEmail() == null) {
                    pojo.setEmail("default@default.com");
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.MACVALIDATION)) {
                    if (!item.getValue().isEmpty() && item.getValue().equalsIgnoreCase("y")) {
                        pojo.setMac_provision(true);
                        pojo.setMac_auth_enable(true);
                    } else if (!item.getValue().isEmpty() && item.getValue().equalsIgnoreCase("n")) {
                        pojo.setMac_provision(false);
                        pojo.setMac_auth_enable(false);
                    }
                }

            }
            pojo.setTitle("Mr");
            pojo.setFirstname(request.getUserName());
            pojo.setLastname(request.getUserName());
//            pojo.setMobile("1234567898");
            pojo.setContactperson("");
            pojo.setCustcategory("");
            pojo.setDunningCategory("Platinum");
            pojo.setContactperson(request.getUserName());
            pojo.setServiceareaid(serviceareaId);
            if (pojo.getMobile() == null) {
                pojo.setMobile("1234567890");
            }
            if (pojo.getBillday() == null) {
                LocalDate date = LocalDate.now();
                pojo.setBillday(date.getDayOfMonth());
            }
            if (pojo.getMac_provision() == null) {
                pojo.setMac_provision(true);
                pojo.setMac_auth_enable(true);
            }
            if (pojo.getMaxconcurrentsession() == null) {
                pojo.setMaxconcurrentsession(1);
            }
            CustPlanMapppingPojo custPlanMappping;
            List<CustPlanMapppingPojo> planMappingList = new ArrayList<>();
            List<Object[]> postpaidPlan = new ArrayList<>();
            if (request.getServiceId() != null && !request.getServiceId().isEmpty()) {
                postpaidPlan = postpaidPlanRepo.findPostpaidPlansByname(request.getServiceId());
                if (postpaidPlan == null || postpaidPlan.isEmpty()) {
                    response.put("message", "Service not available");
                    return apiResponse(SoapConstant.SUCCESS_CODE, response);
                }
            } else {
                postpaidPlan = postpaidPlanRepo.findPostpaidPlansByname(plan);
            }
            for (Object[] postpaidPlan1 : postpaidPlan) {
                custPlanMappping = new CustPlanMapppingPojo();
                custPlanMappping.setPlanId((Integer) postpaidPlan1[0]);
//                String serviceName = serviceRepository.findServiceNameById((Integer) postpaidPlan1[1]);
                String serviceName = customersService.getServiceName((Integer) postpaidPlan1[1]);
                custPlanMappping.setService(serviceName != null ? serviceName : "");
                custPlanMappping.setValidity((Double) postpaidPlan1[2]);
                custPlanMappping.setServiceId((Integer) postpaidPlan1[1]);
                custPlanMappping.setDiscount(0.0);
                custPlanMappping.setSkipQuotaUpdate(true);
                custPlanMappping.setBillTo("CUSTOMER");
                custPlanMappping.setDiscountType("One-time");
                custPlanMappping.setDiscountExpiryDate(null);
                custPlanMappping.setIsInvoiceToOrg(false);

                planMappingList.add(custPlanMappping);
            }

            pojo.setPlanMappingList(planMappingList);
            pojo.setCalendarType("English");
            if (String.valueOf(postpaidPlan.get(0)[3]).equalsIgnoreCase("prepaid")) {
                pojo.setCusttype("Prepaid");
            } else {
                pojo.setCusttype("Postpaid");
            }
            pojo.setCustlabel("customer");

            //set address liss
            CustomerAddressPojo customerAddressPojo = new CustomerAddressPojo();
            customerAddressPojo.setAddressType("Present");
            customerAddressPojo.setStateId(2);
            customerAddressPojo.setAreaId(2);
            customerAddressPojo.setCityId(2);
            customerAddressPojo.setCountryId(2);
            customerAddressPojo.setLandmark("HYD");
            customerAddressPojo.setPincodeId(2);
            customerAddressPojo.setVersion("new");

            pojo.setAddressList(Collections.singletonList(customerAddressPojo));

            customersService.validateMendatoryFields(pojo);
            CustomersService customersService = SpringContext.getBean(CustomersService.class);

            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            CustomersPojo customersPojo = pojo;
            pojo.setDunningCategory(pojo.getDunningCategory());
            pojo.setDunningSector(pojo.getCustomerSector());
            pojo.setCustcategory(pojo.getDunningCategory());
            pojo.setDunningType(pojo.getCustomerType());
            pojo.setCustomerSector(pojo.getDunningSector());
            if (pojo.getEarlybillday() == null) {
                pojo.setEarlybillday(0);
            } else {
                pojo.setEarlybillday(pojo.getEarlybillday());
            }
            if (pojo.getEarlybilldays() == null) {
                pojo.setEarlybilldays(0);
            } else {
                pojo.setEarlybilldays(pojo.getEarlybilldays());
            }
            pojo.setPartnerid(CommonConstants.DEFAULT_PARTNER_ID);
            if (cui != null && !cui.isEmpty()) {
                pojo.setAcctno(cui);
            } else {
                pojo.setUsername(request.getUserName());
            }
            if (pojo.getCreatedById() == null) {
                pojo.setCreatedById(loggedInUser.getUserId());
            }

            if (pojo.getCreatedByName() == null) {
                pojo.setCreatedByName(loggedInUser.getFullName());
            }
            pojo.setMacRetentionUnit(null);
            pojo.setMacRetentionPeriod(null);
            // Save Customer
            pojo = customersService.save(pojo, requestFrom, false);


            customersPojo.setId(pojo.getId());

            List<Integer> mappingIds = customerServiceMappingRepository.custServicemappingIdByCustomerId(pojo.getId());

            //save customer for all microservice
            if (pojo != null) {
                if (customersPojo.getPaymentDetails() != null) {
                    pojo.setPaymentDetails(customersPojo.getPaymentDetails());
                }
                pojo.setCustomerCreated(true);
                customersService.sharedCustomerData(pojo, false);
            }

//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_ADD, req.getRemoteAddr(), null, pojo.getId().longValue(), "");

            response.put("customer", pojo);
            if (pojo.getOltid() != null) {
                String networkDeviceName = networkDeviceRepository.findDeviceNameById(pojo.getOltid());
                if (networkDeviceName != null && !networkDeviceName.isEmpty()) {
                    pojo.setOltName(networkDeviceName);
                }
            }
            if (pojo.getPopid() != 0) {
                String popName = popManagementRepository.findPopNameById(pojo.getPopid());
                if (popName != null && !popName.isEmpty()) {
                    pojo.setOltName(popName);
                }
            }
            CustomerMessageIn customerMessageIn = new CustomerMessageIn(pojo);
            kafkaMessageSender.send(new KafkaMessageData(customerMessageIn, CustomerMessageIn.class.getSimpleName()));

            for (Item item : request.getItem()) {
                if (item.getKey().equalsIgnoreCase(SoapConstant.CALLINGSTATIONID) && item.getValue() != null && !item.getValue().isEmpty()) {
                    String callingStationId = item.getValue().toLowerCase();
                    boolean isMacValid = isMacValid(callingStationId);
                    if (isMacValid) {
                        CustMacMappping custMacMappping = new CustMacMappping();
                        custMacMappping.setMacAddress(callingStationId);
                        custMacMappping.setCustomer(customerMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext()));
                        if (!CollectionUtils.isEmpty(mappingIds))
                            custMacMappping.setCustsermappingid(mappingIds.get(0));
                        custMacMgmtService.saveMacMapping(Collections.singletonList(custMacMappping));
                    }
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.PARAM1) && item.getValue() != null && !item.getValue().isEmpty()) {
                    String fraIp = item.getValue();
                    CustIpMapping custIpMapping = new CustIpMapping();
                    custIpMapping.setIpAddress(fraIp);
                    custIpMapping.setIpType("Ipv4");
                    custIpMapping.setCustid(pojo.getId());
                    if (!CollectionUtils.isEmpty(mappingIds))
                        custIpMapping.setCustsermappingid(mappingIds.get(0));
                    custIpMgmtService.save(Collections.singletonList(custIpMapping));
                }
            }

            long endTime = System.currentTimeMillis();
            log.warn("::::::::: Total customer creation time ::::::::: "+ (endTime - startTime));
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (ConstraintViolationException exception) {
            RESP_CODE = 404;
            response.put(APIConstants.ERROR_TAG, "username is already exist");
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return apiResponse(RESP_CODE,response);
        }catch (DataIntegrityViolationException ex) {
            RESP_CODE = 404;
            response.put(APIConstants.ERROR_TAG, "username is already exist");
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            return apiResponse(RESP_CODE,response);
        }catch (RuntimeException exception) {
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, exception.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/UpdateAccount")
    public ResponseEntity<?> updateCustomers(@Valid @RequestBody WsUpdateAccountRequest request,
                                             @RequestParam(name = "mvnoid", required = false) Long mvnoid,
                                             HttpServletRequest req) {
        log.debug("In Customer Update: " + request.getUserName().trim());
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        String requestFrom = "bss";
        CustomersPojo pojo = new CustomersPojo();
        //  Customers customers = customersRepository.findByUsernameAndMvnoId(request.getUserName(),Math.toIntExact(mvnoid)).get();

        try {
            Customers customers = customersRepository.findByUsernameAndMvnoIdAndStatusNot(
                    request.getUserName().trim(), Math.toIntExact(mvnoid), "Terminate"
            ).orElseThrow(() -> new CustomValidationException(404, "User not found ", null));
            log.error(LogConstants.USERNAME_NOT_FOUND + request.getUserName().trim());
            String acctNo = customers.getAcctno();
            String currentStatus = customers.getStatus();
//            boolean userName = request.getUserName().equalsIgnoreCase(customers.getUsername());
//            if(userName){
//                pojo.setUsername(customers.getUsername());
//            }
            pojo.setUsername(customers.getUsername());
            pojo.setMac_provision(customers.getMac_provision());
            pojo.setMac_auth_enable(customers.getMac_auth_enable());
            if (request.getPassword() != null) {
                pojo.setPassword(request.getPassword());
            } else {
                pojo.setPassword(customers.getPassword());
            }
            for (WsUpdateAccountRequest.Item item : request.getItem()) {
                if (item.getKey().equalsIgnoreCase(SoapConstant.CUSTOMERSTATUS)) {
                    String customerStatus = item.getValue();
                    if ("y".equalsIgnoreCase(customerStatus)) {
                        pojo.setStatus("Active");
                    } else if ("n".equalsIgnoreCase(customerStatus)) {
                        pojo.setStatus("Inactive");
                    } else if ("suspend".equalsIgnoreCase(customerStatus)) {
                        pojo.setStatus("Suspend");
                    }
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.CALLINGSTATIONID)) {
                    String callingStationId = item.getValue();
                    boolean isMacValid = isMacValid(callingStationId);
                    if (isMacValid && (custMacMgmtService.isMacExistsExceptCustomer(callingStationId, customers.getId()))) {
                        RESP_CODE = 412;
                        response.put(APIConstants.ERROR_TAG, SoapConstant.CALLINGSTATIONID + " is already available and in-use, please use alternate mac: " + callingStationId);
                        return apiResponse(RESP_CODE, response);
                    } else if (isIpValid(callingStationId)) {
                        if (custIpMgmtService.isIpExists(callingStationId, customers.getId())) {
                            RESP_CODE = 412;
                            response.put(APIConstants.ERROR_TAG, "CALLINGSTATIONID already exists for another account: " + callingStationId);
                            return apiResponse(RESP_CODE, response);
                        }
                    }
                    if (isMacValid)
                        pojo.setMACADDRESS(callingStationId);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.PARAM1)) {
                    String fraIp = item.getValue();
                    if (!fraIp.isEmpty() && custIpMgmtService.isIpExists(fraIp, customers.getId())) {
                        RESP_CODE = 412;
                        response.put(APIConstants.ERROR_TAG, "Param1 already exists for another account: " + fraIp);
                        return apiResponse(RESP_CODE, response);
                    }
                    pojo.setFramedIp(fraIp);
                }
                if (item.getKey().equalsIgnoreCase(SoapConstant.CREATEDATE)) {
                    String date = item.getValue();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
                    pojo.setCreatedate(dateTime);
                }
//                if (item.getKey().equalsIgnoreCase(SoapConstant.PARAM7)) {
//                    boolean dnd = item.getValue().equals("1");
//                    pojo.setIsNotificationEnable(!dnd);
//                }
            }

            if (!customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.PARAM1)) {
                pojo.setFramedIp(customers.getFramedIp());
            }

            // Set Email
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.CUSTOMERALTEMAILID)) {
                String email = customersService.getValueForKey(request.getItem(), SoapConstant.CUSTOMERALTEMAILID);
                if (!email.isEmpty() && email != null) {
                    pojo.setEmail(email);
                    pojo.setAltemail(email);
                } else {
                    pojo.setEmail("default@default.com");
                    pojo.setAltemail("default@default.com");
                }
            } else {
                pojo.setEmail(customers.getEmail());
                pojo.setAltemail(customers.getAltemail());
            }

            //Set Mobile
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.MSISDN)) {
                String mobile = customersService.getValueForKey(request.getItem(), SoapConstant.MSISDN);
                if (!mobile.isEmpty() && mobile != null) {
                    pojo.setMobile(mobile);
                } else {
                    pojo.setMobile("1234567890");
                }
            } else {
                pojo.setMobile(customers.getMobile());
            }

            //Set Max Concurrent Session
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.CONCURRENTLOGINPOLICY)) {
                String concurrancy = customersService.getValueForKey(request.getItem(), SoapConstant.CONCURRENTLOGINPOLICY);
                if (!concurrancy.isEmpty() && concurrancy != null) {
                    pojo.setMaxconcurrentsession(Integer.valueOf(concurrancy));
                } else {
                    pojo.setMaxconcurrentsession(1);
                }
            } else {
                pojo.setMaxconcurrentsession(customers.getMaxconcurrentsession());
            }

            //Set Framed Ip NetMask
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.PARAM2)) {
                String subNetMast = customersService.getValueForKey(request.getItem(), SoapConstant.PARAM2);
                if (!subNetMast.isEmpty() && subNetMast != null) {
                    pojo.setFramedIPNetmask(subNetMast);
                } else {
                    pojo.setFramedIPNetmask(null);
                }
            } else {
                pojo.setFramedIPNetmask(customers.getFramedIPNetmask());
            }

            //Set Framed Route
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.PARAM3)) {
                String frameRoute = customersService.getValueForKey(request.getItem(), SoapConstant.PARAM3);
                if (!frameRoute.isEmpty() && frameRoute != null) {
                    pojo.setFramedroute(frameRoute);
                } else {
                    pojo.setFramedroute(null);
                }
            } else {
                pojo.setFramedroute(customers.getFramedroute());
            }

            //Set Notification Enabled
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.PARAM7)) {
                String dndValue = customersService.getValueForKey(request.getItem(), SoapConstant.PARAM7);
                if (!dndValue.isEmpty() && dndValue != null) {
                    boolean dnd = dndValue.equals("1");
                    pojo.setIsNotificationEnable(!dnd);
                } else {
                    pojo.setIsNotificationEnable(true);
                }
            } else {
                pojo.setIsNotificationEnable(false);
            }

            //Set nas PortId
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.PARAM4)) {
                String nasIpValue = customersService.getValueForKey(request.getItem(), SoapConstant.PARAM4);
                if (!nasIpValue.isEmpty() && nasIpValue != null) {
                    String regex = "^(0:92=\")\\[(.*)\\]\"$";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(nasIpValue);
                    if (matcher.find()) {
                        if (matcher.groupCount() >= 2) {
                            String captureNumeric = matcher.group(1);
                            String extractedData = matcher.group(2);
                            pojo.setNasPortId(extractedData);
                        } else {
                            response.put("message", "invalid location lock");
                            return apiResponse(SoapConstant.SUCCESS_CODE, response);
                        }
                    } else {
                        response.put("message", "invalid location lock");
                        return apiResponse(SoapConstant.SUCCESS_CODE, response);
                    }
                }
            } else {
                pojo.setNasPortId(customers.getNasPortId());
            }

            //Set GatewayIp
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.PARAM6)) {
                String gatewayIp = customersService.getValueForKey(request.getItem(), SoapConstant.PARAM6);
                if (!gatewayIp.isEmpty() && gatewayIp != null) {
                    pojo.setGatewayIP(gatewayIp);
                } else {
                    pojo.setGatewayIP(null);
                }
            } else {
                pojo.setGatewayIP(customers.getGatewayIP());
            }

            //Set FramedIpIpv6 Address
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.GROUPNAME)) {
                String framedIpv6 = customersService.getValueForKey(request.getItem(), SoapConstant.GROUPNAME);
                if (!framedIpv6.isEmpty() && framedIpv6 != null) {
                    pojo.setFramedIpv6Address(framedIpv6);
                } else {
                    pojo.setFramedIpv6Address(null);
                }
            } else {
                pojo.setFramedIpv6Address(customers.getFramedIpv6Address());
            }

            //Set Delegated Prefix
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.CUSTOMERREPLYITEM)) {
                String delegatedPrefix = customersService.getValueForKey(request.getItem(), SoapConstant.CUSTOMERREPLYITEM);
                if (!delegatedPrefix.isEmpty() && delegatedPrefix != null) {
                    delegatedPrefix = delegatedPrefix.substring(delegatedPrefix.indexOf('=') + 1);
                    pojo.setDelegatedprefix(delegatedPrefix);
                } else {
                    pojo.setDelegatedprefix(null);
                }
            } else {
                pojo.setDelegatedprefix(customers.getDelegatedprefix());
            }

            //Set Account no
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.CUI)) {
                String cui = customersService.getValueForKey(request.getItem(), SoapConstant.CUI);
                if (!cui.isEmpty() && cui != null) {
                    pojo.setAcctno(cui);
                } else {
                    pojo.setAcctno(customers.getUsername().trim());
                }
            } else {
                pojo.setAcctno(customers.getAcctno());
            }

            //Set Vlan
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.GEOLOCATION)) {
                String vlan = customersService.getValueForKey(request.getItem(), SoapConstant.GEOLOCATION);
                if (!vlan.isEmpty() && vlan != null) {
                    pojo.setVlan_id(vlan);
                } else {
                    pojo.setVlan_id(null);
                }
            } else {
                pojo.setVlan_id(customers.getVlan_id());
            }

            //Set DNS
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.PRIMARY_DNS)) {
                String dns = customersService.getValueForKey(request.getItem(), SoapConstant.PRIMARY_DNS);
                if (!dns.isEmpty() && dns != null) {
                    pojo.setPrimaryDNS(dns);
                } else {
                    pojo.setPrimaryDNS(null);
                }
            } else {
                pojo.setPrimaryDNS(customers.getPrimaryDNS());
            }

//            Set Ipv6 DNS
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.PRIMARY_IPV6_DNS)) {
                String ipv6_dns = customersService.getValueForKey(request.getItem(), SoapConstant.PRIMARY_IPV6_DNS);
                if (!ipv6_dns.isEmpty() && ipv6_dns != null) {
                    pojo.setPrimaryIPv6DNS(ipv6_dns);
                } else {
                    pojo.setPrimaryIPv6DNS(null);
                }
            } else {
                pojo.setPrimaryIPv6DNS(customers.getPrimaryIPv6DNS());
            }


            // Set Secondary DNS
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.SECONDARY_DNS)) {
                String secondary_dns = customersService.getValueForKey(request.getItem(), SoapConstant.SECONDARY_DNS);
                if (!secondary_dns.isEmpty() && secondary_dns != null) {
                    pojo.setSecondaryDNS(secondary_dns);
                } else {
                    pojo.setSecondaryDNS(null);
                }
            } else {
                pojo.setSecondaryDNS(customers.getSecondaryDNS());
            }

            //Set Secondary Ipv6 DNS
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.SECONDARY_IPV6_DNS)) {
                String secondary_ipv6_dns = customersService.getValueForKey(request.getItem(), SoapConstant.SECONDARY_IPV6_DNS);
                if (!secondary_ipv6_dns.isEmpty() && secondary_ipv6_dns != null) {
                    pojo.setSecondaryIPv6DNS(secondary_ipv6_dns);
                } else {
                    pojo.setSecondaryIPv6DNS(null);
                }
            } else {
                pojo.setSecondaryIPv6DNS(customers.getSecondaryIPv6DNS());
            }

            // Set MAC Validation
            if (customersService.checkItemPresentOrNot(request.getItem(), SoapConstant.MACVALIDATION)) {
                String macValidation = customersService.getValueForKey(request.getItem(), SoapConstant.MACVALIDATION);
                if (!macValidation.isEmpty() && macValidation != null && macValidation.equalsIgnoreCase("y")) {
                    pojo.setMac_provision(true);
                    pojo.setMac_auth_enable(true);
                } else if (!macValidation.isEmpty() && macValidation != null && macValidation.equalsIgnoreCase("n")) {
                    pojo.setMac_provision(false);
                    pojo.setMac_auth_enable(false);
                    customersService.removeCustomerMac(customers);
                } else {
                    pojo.setMac_provision(true);
                    pojo.setMac_auth_enable(true);
                }
            } else {
                pojo.setMac_provision(pojo.getMac_provision());
                pojo.setMac_auth_enable(pojo.getMac_auth_enable());
            }


            pojo.setTitle(customers.getTitle());
            pojo.setFirstname(customers.getFirstname());
            pojo.setLastname(customers.getLastname());
//            pojo.setEmail(customers.getEmail());
            pojo.setContactperson(customers.getContactperson());
            pojo.setCustcategory(customers.getCustcategory());
            pojo.setServiceareaid(Long.valueOf(1));

            pojo.setCalendarType("English");
            pojo.setCusttype(customers.getCusttype());
            pojo.setCustlabel("customer");


            for (WsUpdateAccountRequest.Item item : request.getItem()) {
                if (item.getKey().equalsIgnoreCase(SoapConstant.ADDITIONALPOLICY)) {
                    if (item.getValue() != null && !item.getValue().isEmpty()) {
                        Integer billDay = Integer.valueOf(item.getValue());
                        pojo.setBillday(billDay);
                    } else {
                        pojo.setBillday(customers.getBillday());
                    }
                }
            }

            CustomersService customersService = SpringContext.getBean(CustomersService.class);
            pojo.setId(customers.getId());
            Customers old1 = customersRepository.findById(customers.getId()).get();
            if (old1.getLeadId() != null) {
                pojo.setLeadId(old1.getLeadId());
            }
            if (old1.getLeadNo() != null) {
                pojo.setLeadNo(old1.getLeadNo());
            }
            if (pojo.getStatus() != null) {
                customers.setStatus(pojo.getStatus());
            }
            Customers customers22 = new Customers(old1, customers.getId());
            customersService.getEntityForUpdateAndDelete(customers.getId(),customers.getMvnoId());
            Customers updatedCust = customersService.updateCustForSoapUpdate(pojo, customers.getId());

            if(pojo.getBillday() == null){
                pojo.setBillday(updatedCust.getBillday());
            }

            for (WsUpdateAccountRequest.Item item : request.getItem()) {
                if (item.getKey().equalsIgnoreCase(SoapConstant.ADDITIONALPOLICY)) {
                    if (item.getValue() != null && !item.getValue().isEmpty()) {
                        Integer billDay = Integer.valueOf(item.getValue());
                        updatedCust.setBillday(billDay);
                        LocalDate nextBilldate = customersService.getNextBillDate(updatedCust);
                        LocalDate quotaResetDate = customersService.getNextQuotaResetDate(updatedCust, nextBilldate);
                        pojo.setNextBillDate(nextBilldate);
                        pojo.setNextQuotaResetDate(quotaResetDate);
                        customerRepository.updateNextBillDateAndBilldayAndNextQuotaResetDate(updatedCust.getId(), nextBilldate, quotaResetDate, billDay);
                    }
                }
            }

            response.put("customer", pojo);
            RESP_CODE = APIConstants.SUCCESS;
            Customers newcust = customersRepository.findById(customers.getId()).get();
            //Customers cust = customerRepository.findAllById(pojo.getId());
            List<CustomerServiceMapping> mappings = customerServiceMappingRepository.findAllByCustId(pojo.getId());
            List<CustMacMappping> custMacMappingList = new ArrayList<>();
            for (WsUpdateAccountRequest.Item item : request.getItem()) {
                if (item.getKey().equalsIgnoreCase(SoapConstant.CALLINGSTATIONID)) {
                    String callingStationId = item.getValue();
                    if (!callingStationId.isEmpty()) {
                        if (isMacValid(callingStationId)) {
                            CustMacMappping custMacMappping = new CustMacMappping();
                            custMacMappping.setMacAddress(callingStationId.toLowerCase());
                            custMacMappping.setCustomer(updatedCust);
                            if (!CollectionUtils.isEmpty(mappings))
                                custMacMappping.setCustsermappingid(mappings.get(0).getId());
                            GenericDataDTO reseponse = custMacMgmtService.saveMacMapping(Collections.singletonList(custMacMappping));
                            if (reseponse.getResponseCode() == HttpStatus.OK.value() && !CollectionUtils.isEmpty(reseponse.getDataList())) {
                                custMacMappingList = reseponse.getDataList();
                            }
                        }
                    } else {
                        custMacMapppingRepository.deleteByCustomerid(updatedCust.getId().longValue());
//                        custMacMappingList = custMacMapppingRepository.findByCustomerId(updatedCust.getId());
//                        if (!custMacMappingList.isEmpty() && custMacMappingList != null) {
//                            custMacMappingList.stream()
//                                    .forEach(mac -> mac.setDeleteFlag(true));

                            CustMacMappingMessage message = new CustMacMappingMessage(updatedCust);
                            kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
//                            custMacMapppingRepository.saveAll(custMacMappingList);
//                        }
                    }
                }

                if (item.getKey().equalsIgnoreCase(SoapConstant.PARAM1)) {
                    String fraIp = item.getValue();
                    if (!fraIp.isEmpty()) {
                        if (!custIpMgmtService.isIpExists(fraIp, customers.getId())) {
                            CustIpMapping custIpMapping = new CustIpMapping();
                            custIpMapping.setIpAddress(fraIp);
                            custIpMapping.setIpType("Ipv4");
                            custIpMapping.setCustid(updatedCust.getId());
                            if (!CollectionUtils.isEmpty(mappings))
                                custIpMapping.setCustsermappingid(mappings.get(0).getId());
                            custIpMgmtService.save(Collections.singletonList(custIpMapping));
                        }
                    } else {
                        List<CustIpMapping> custIpMappingList = new ArrayList<>();
                        custIpMappingList = custIpMappingRepo.getAllByCustid(updatedCust.getId());
                        if (!custIpMappingList.isEmpty() && custIpMappingList != null) {
                            custIpMappingRepo.deleteInBatch(custIpMappingList);
                            CustIPMessage custIPMessage = new CustIPMessage(custIpMappingList, true);
                            kafkaMessageSender.send(new KafkaMessageData(custIPMessage, custIPMessage.getClass().getSimpleName(), "CUSTOMER_IP_TO_DELETE_RADIUS"));
                        }
                    }
                }

            }

            customerRepository.save(updatedCust);
            if (!CollectionUtils.isEmpty(custMacMappingList))
                updatedCust.setCustMacMapppingList(custMacMappingList);
            CustomersPojo customersPojo = customerMapper.domainToDTO(updatedCust, new CycleAvoidingMappingContext());
            customersPojo.setCustomerCreated(false);
            if (!currentStatus.equalsIgnoreCase(updatedCust.getStatus())) {
                customersPojo.setCustomerCreated(true);
            }
            CustomMessage customMessage = new CustomMessage(customersPojo);

            List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustomerId(customers.getId());
            List<CustomerServiceMapping> customerServiceMappinList = customerServiceMappingRepository.findAllByCustId(pojo.getId());
            createDataSharedService.updateCustomerEntityForAllMicroServce(customerMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext()), custPlanMapppingList, customerServiceMappinList);
            //Customers cust = customerRepository.findAllById(pojo.getId());
            for (WsUpdateAccountRequest.Item item : request.getItem()) {
                if (item.getKey().equalsIgnoreCase(SoapConstant.CALLINGSTATIONID)) {
                    String callingStationId = item.getValue().toLowerCase();
                    boolean isMacValid = isMacValid(callingStationId);
                    if (isMacValid) {
                        CustMacMappping custMacMappping = new CustMacMappping();
                        custMacMappping.setMacAddress(callingStationId);
                        custMacMappping.setCustomer(newcust);
//                        List<CustomerServiceMapping> mappings = customerServiceMappingRepository.findAllByCustId(pojo.getId());
                        if (!CollectionUtils.isEmpty(mappings))
                            custMacMappping.setCustsermappingid(mappings.get(0).getId());
                        custMacMgmtService.saveMacMapping(Collections.singletonList(custMacMappping));
                    }
//                    custMacMapppingRepository.save(custmac);
                }
            }
            String updatedValues = UtilsCommon.getUpdatedDiff(customers22, newcust);
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Customer" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + customersService.getLoggedInUser().getUsername() + " , updated Customer Details " + updatedValues + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_EDIT, req.getRemoteAddr(), updatedValues, pojo.getId().longValue(), "");
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update customer" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update customer" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
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

    public CustomersPojo getLocationmapping(CustomersPojo subscriber) {
        log.debug("In Locationmapping : {}", subscriber);
        if (!org.springframework.util.CollectionUtils.isEmpty(subscriber.getLocations())) {
            List<Long> locations = subscriber.getLocations();
            List<LocationMaster> locationMasters = locationMasterRepository.findAllByLocationMasterIdIn(locations);
            if (!org.springframework.util.CollectionUtils.isEmpty(locationMasters)) {
                List<CustomerLocationMapping> list = new ArrayList<>();
                for (LocationMaster location : locationMasters) {
                    CustomerLocationMapping customerLocationMapping = new CustomerLocationMapping();
                    customerLocationMapping.setLocationId(location.getLocationMasterId());
                    customerLocationMapping.setLocationName(location.getName());
                    customerLocationMapping.setCustId(Long.valueOf(subscriber.getId()));
                    list.add(customerLocationMapping);
                }
                if (!org.springframework.util.CollectionUtils.isEmpty(list)) {
                    list = customerLocationRepository.saveAll(list);
                    List<CustomerLocationMappingDto> locationMappingDtos = customerLocationMapper.domainToDTO(list, new CycleAvoidingMappingContext());
                    subscriber.setCustomerLocations(locationMappingDtos);
                }
            }
        }
        return subscriber;
    }

    @PutMapping("/removeAccount/changeStatus")
    // change status of customer for SOAP API
    public ResponseEntity<?> changeStatusCustomers(@RequestBody RemoveAccountRequest request, @RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) throws Exception {
        log.debug("In removeAccount : " + request.getUserName().trim());
        HashMap<String, Object> response = new HashMap<>();
        Boolean message = false;
        Integer RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
        try {
            CustomersService customersService = SpringContext.getBean(CustomersService.class);
            message = customersService.changeCustomerStatus(request, Math.toIntExact(mvnoId));
            if (message) {
                response.put("terminationCheck", "Success");
                RESP_CODE = HttpStatus.OK.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change Status" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + HttpStatus.OK);
            } else {
                response.put("terminationCheck", "User Already Terminated");
                RESP_CODE = HttpStatus.OK.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Customer is already terminated " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + HttpStatus.OK);
            }
        } catch (Exception ex) {
            response.put("terminationCheck", "Exception occure during termination");
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_MESSAGE, ex.getMessage());
            log.debug(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change Status" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/resetUsegeForAccount")
    public GenericDataDTO resetUsageForAccount(@RequestParam String userName, @RequestParam Long mvnoId, HttpServletRequest req) {
        log.debug("In ResetUsage : " + userName);
        LocalDateTime todayDate = LocalDateTime.now();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Map<String, Object> response = new HashMap<>();
        Integer responsecode = HttpStatus.EXPECTATION_FAILED.value();
        String responseMessage = LogConstants.LOG_FAILED;
        try {
            Customers customers = customerRepository.findByUsernameAndMvnoIdAndStatusNot(userName, Math.toIntExact(mvnoId), "Terminate")
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
            if(customers == null) {
                log.error(LogConstants.USERNAME_NOT_FOUND + userName);
            }
            List<CustQuotaDetails> custQuotaDetailsList = custQuotaRepository.findByCustomerId(customers.getId());

            boolean isCustomerUpdated = false;

            for (CustQuotaDetails quotaDetails : custQuotaDetailsList) {
                CustPlanMappping planMapping = quotaDetails.getCustPlanMappping();
                PostpaidPlan postpaidPlan = quotaDetails.getPostpaidPlan();
                // Skip if not a "New" purchase type
                if (!"New".equalsIgnoreCase(planMapping.getPurchaseType())) {
                    continue;
                }
                LocalDate endDate = postpaidPlan.getEndDate();
                if (endDate.isAfter(ChronoLocalDate.from(todayDate))) {
                    if (!isCustomerUpdated) {
                        List<CustMacMappping> addressMappings = custMacMapppingRepository.findByCustomerId(customers.getId());
                        customers.setCustMacMapppingList(addressMappings);
                        isCustomerUpdated = true;
                    }
                    quotaResetScheduler.resetQuota(quotaDetails, customers, 1L, todayDate);
                    responsecode = HttpStatus.OK.value();
                    responseMessage = LogConstants.LOG_SUCCESS;
                } else if (endDate.isBefore(LocalDate.now()) && CommonConstants.ACTIVE_STATUS.equalsIgnoreCase(customers.getStatus())) {
                    quotaResetScheduler.inActivateCustomer(customers);
                }
            }
            genericDataDTO.setResponseCode(responsecode);
            genericDataDTO.setResponseMessage(responseMessage);
            return genericDataDTO;
        } catch (EntityNotFoundException e) {
            genericDataDTO.setResponseCode(responsecode);
            genericDataDTO.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            genericDataDTO.setResponseCode(responsecode);
            genericDataDTO.setResponseMessage(e.getMessage());
        }
        return genericDataDTO;
    }


    @PostMapping("/removeService")
    public ResponseEntity<?> stopServiceInBulk(@Valid @RequestBody RemoveServiceRequest request, HttpServletRequest req) throws Exception {
        log.debug("In remove service: " + request.getString1());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        DeactivatePlanReqDTOList planReqDTOList = new DeactivatePlanReqDTOList();
        try {

            //set required value manually
            String userName = request.getString1().trim().toLowerCase();
            Customers customers = customersRepository.findByUsernameAndMvnoId(userName, 2)
                    .orElseThrow(() -> new NoSuchElementException("Customer not found with username: " + request.getString1()));
            if(customers == null) {
                log.error(LogConstants.USERNAME_NOT_FOUND + userName);
            }
            if (customers == null) {
                response.put("Failure", "not available");
                RESP_CODE = APIConstants.SUCCESS;
                return apiResponse(RESP_CODE, response, null);
            } else if (customers.getStatus().equalsIgnoreCase("Suspend")) {
                if (!customers.getStatus().equalsIgnoreCase(SubscriberConstants.TERMINATE)) {
                    customers.setStatus("Active");
                    customersRepository.save(customers);
                    CustomerUpdateMessage message = new CustomerUpdateMessage(customers);
                    kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
//                    customersService.removeAllMacFromCustomer(customers);
//                    customersService.removeIpFromCustomer(customers);
                    response.put("msg", "Success");
                    RESP_CODE = APIConstants.SUCCESS;
                    return apiResponse(RESP_CODE, response, null);
                }
            } else {
                response.put("Failure", "not available");
                RESP_CODE = APIConstants.SUCCESS;
                return apiResponse(RESP_CODE, response, null);
            }
        } catch (CustomValidationException ce) {
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Service stopped" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (RuntimeException exception) {
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Service stopped" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, exception.getMessage());
        } catch (Exception ex) {
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Service stopped" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response, null);
    }


    @PutMapping("/removeSubscriberAccount/changeStatus")
    // change status of customer for SOAP API
    public ResponseEntity<?> removeSubscriberAccount(@RequestBody String username, @RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) throws Exception {
        log.debug("In removeSubscriberAccount ChangeStatus: " + username);
        HashMap<String, Object> response = new HashMap<>();
        Boolean message = false;
        Integer RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
        try {
            CustomersService customersService = SpringContext.getBean(CustomersService.class);
            message = customersService.changeSubscriberCustomerStatus(username, Math.toIntExact(mvnoId));
            if (message) {
                response.put("terminationCheck", "Success");
                RESP_CODE = HttpStatus.OK.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change Status" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + HttpStatus.OK);
            } else {
                response.put("terminationCheck", "failure");
                RESP_CODE = HttpStatus.OK.value();
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Customer is already terminated " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + HttpStatus.OK);
            }
        } catch (Exception ex) {
            response.put("terminationCheck", "failure");
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_MESSAGE, ex.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update change Status" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.EXPECTATION_FAILED);
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping(value = "/subscribeAddon")
    public GenericDataDTO changePlan01(@RequestBody WsSubscribeAddOnRequest request, @RequestParam(name = "mvnoid", required = false) Long mvnoid, HttpServletRequest req) throws Exception {
        log.debug("In ChangePlan: " + request.getSubscriberId());
        String SUBMODULE = MODULE + " [changePlan()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        String requestFrom = "bss";
        Boolean flag = true;
        Thread invoiceThread = null;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        ChangePlanRequestDTOList requestDTOs = new ChangePlanRequestDTOList();
        Integer customerId = null;
        String custName = null;
        String username = null;
        Boolean isInvoiceStop = false;
        String fullName = null;
        Object[] customer = null;

        //set data in dto

        Object[] customersData = customerRepository.findCustomersByUserNameActiveOnly(request.getSubscriberId().trim());
        if (Objects.isNull(customersData) ||customersData.length == 0) {
            genericDataDTO.setResponseMessage("Customer not available");
            log.error(LogConstants.USERNAME_NOT_FOUND + username);
            RESP_CODE = APIConstants.SUCCESS;
            return genericDataDTO;
        }

            customer = (Object[]) customersData[0];
            customerId = ((BigInteger) customer[0]).intValue();             //custid
            custName = (String) customer[1];               // custname
            username = (String) customer[2];               // username
            isInvoiceStop = (Boolean) customer[3];        // isinvoicestop

        List<Object[]> postpaidPlanData = postpaidPlanRepo.findPostpaidPlanDetailsByName(request.getAddOnPackageName());

        if (postpaidPlanData.isEmpty()) {
            genericDataDTO.setResponseMessage("planData not available");
            log.debug("planData not available IN: " + request.getAddOnPackageName());
            RESP_CODE = APIConstants.SUCCESS;
            return genericDataDTO;
        } else if (!postpaidPlanData.get(0)[1].toString().equalsIgnoreCase("Bandwidthbooster")) {
            genericDataDTO.setResponseMessage("planData not available");
            log.debug("Add on plan happend only Bandwidthbooster  this plan Not Bandwidthbooster plan : " + request.getAddOnPackageName());
            RESP_CODE = APIConstants.SUCCESS;
            return genericDataDTO;
        } else if (!postpaidPlanData.get(0)[2].toString().equalsIgnoreCase("Active")) {
            genericDataDTO.setResponseMessage("planData not available");
            log.debug(" Thia Plan Is Not Active : " + request.getAddOnPackageName());
            RESP_CODE = APIConstants.SUCCESS;
            return genericDataDTO;
        }
        List<Integer> custServiceMappingId = customerServiceMappingRepository.custServicemappingIdByServiceIdAndCustomerId( ((Integer) postpaidPlanData.get(0)[3]).longValue(), customerId);
        List<ChangePlanRequestDTO> requestDTOList = new ArrayList<>();
        ChangePlanRequestDTO changePlanRequestDTOData = new ChangePlanRequestDTO();
        changePlanRequestDTOData.setPurchaseType("Addon");
        changePlanRequestDTOData.setPlanId(request.getAddOnPackageId());
        changePlanRequestDTOData.setRemarks("");
        changePlanRequestDTOData.setPaymentOwnerId(mvnoid.intValue());
        changePlanRequestDTOData.setBillableCustomerId(customerId);

        String startTime = request.getStartTime();
        LocalDateTime localStartDate = (startTime != null && !startTime.isEmpty())
                ? Instant.ofEpochMilli(Long.parseLong(startTime))
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                : LocalDateTime.now();
        changePlanRequestDTOData.setAddonStartDate(localStartDate);


        String endTime = request.getEndTime();
        LocalDateTime localEndDate = (endTime != null && !endTime.isEmpty())
                ? Instant.ofEpochMilli(Long.parseLong(endTime))
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                : localStartDate.plusDays(1);
        changePlanRequestDTOData.setAddonEndDate(localEndDate);
        if (localStartDate.isAfter(localEndDate)) {
            genericDataDTO.setResponseMessage("Expiry date can not be less than start date!");
            log.debug("Expiry date can not be less than start date : " + localEndDate);
            return genericDataDTO;
        }
        changePlanRequestDTOData.setIsAdvRenewal(false);
        changePlanRequestDTOData.setCustId(customerId);
        changePlanRequestDTOData.setCustServiceMappingId(custServiceMappingId.get(0));
        changePlanRequestDTOData.setIsParent(true);

        requestDTOList.add(changePlanRequestDTOData);
        requestDTOs.setChangePlanRequestDTOList(requestDTOList);

        if (requestDTOs != null) {
//            Customers customers = customersService.get(requestDTOs.getChangePlanRequestDTOList().get(0).getCustId());
            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                if (requestFrom.equals("pw") && !getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficient(requestDTOs, getLoggedInUserPartnerId(),getMvnoIdFromCurrentStaff(customerId))) {
                    genericDataDTO.setResponseMessage("Partner has Insufficient balance!");
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    // log.error("Unable to customers with name " + customers.getCustname() + "  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                    return genericDataDTO;
                }

                if (requestFrom.equals("pw") && getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficientForLCO(requestDTOs, getLoggedInUserPartnerId(),getMvnoIdFromCurrentStaff(customerId))) {
                    genericDataDTO.setResponseMessage("Partner has Insufficient balance!");
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "customers with name " + custName + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }
            }
        }

        try {
            if (requestDTOs != null) {
                Integer custId = null;
                String number = String.valueOf(UtilsCommon.gen());
                List<ChangePlanRequestDTO> changePlanRequestDTOS = requestDTOs.getChangePlanRequestDTOList();
                changePlanRequestDTOS.removeIf(changePlanRequestDTO -> changePlanRequestDTO.getPlanId() == null);

                Optional<Integer> custIdOptional = requestDTOs.getChangePlanRequestDTOList().stream().filter(ChangePlanRequestDTO::getIsParent).map(ChangePlanRequestDTO::getCustId).findFirst();
                Set<Integer> custIdsWithoutDuplicates = requestDTOs.getChangePlanRequestDTOList().stream().filter(i -> !i.getIsParent()).map(ChangePlanRequestDTO::getCustId).collect(Collectors.toSet());
                List<Integer> custIds = new ArrayList<>();
                custIds.addAll(custIdsWithoutDuplicates);

                //TODO invoice code
                Set<Customers> customersforInvoice = new HashSet<>();
                if (custIds != null && custIds.size() > 0) {
                    customersforInvoice.addAll(customersRepository.findAllById(custIds));
                }
                if (custIdOptional.isPresent()) {
                    custId = custIdOptional.get();
                } else {
                    custIdOptional = requestDTOs.getChangePlanRequestDTOList().stream().map(ChangePlanRequestDTO::getCustId).findFirst();
                    if (custIdOptional.isPresent()) {
                        custId = custIdOptional.get();
                    }
                }

                //TODO
//                Customers parentCustomers = customersService.get(custId);
                Customers customers = customersRepository.findById(customerId).get();
                customersforInvoice.add(customers);//remove
                //TODO
                List<CustChargeOverrideDTO> custChargeDetailsList = requestDTOs.getCustChargeDetailsList();
                List<CustChargeOverrideDTO> custChargeOverrideDTOS = new ArrayList<>();
                for (ChangePlanRequestDTO requestDTO : requestDTOs.getChangePlanRequestDTOList()) {
                    if (!CollectionUtils.isEmpty(postpaidPlanData)) {
                        requestDTO.setPlanId((Integer) postpaidPlanData.get(0)[0]);
                    }
                    if (null == requestDTO.getCustId()) {
                        genericDataDTO.setResponseMessage("Please provide customer id!");
                        RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                        log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        return genericDataDTO;
                    }
                    if (requestDTO.getPaymentOwner() == null) {
                        requestDTO.setPaymentOwner("");
                    }
//                    Customers customers = customersService.get(requestDTO.getCustId());
                    //TODO not need
                    String updatedValues = UtilsCommon.getUpdatedDiff(customers, requestDTO);
                    if (null == customers) {
                        genericDataDTO.setResponseMessage("Customer not found!");
                        RESP_CODE = APIConstants.NOT_FOUND;
                        genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                        log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        return genericDataDTO;
                    }

                    if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                        List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(customers.getId(), false);
                        if (currentPlanList.size() <= 0) {
                            genericDataDTO.setResponseMessage("Subscriber must have any active plan for AddOn Purchase");
                            RESP_CODE = APIConstants.NOT_FOUND;
                            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            return genericDataDTO;
                        }
                    }

                    if (subscriberService.getCustomerPlanList(customers.getId(), false).size() <= 0) {
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_NEW);
                    } else {
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_RENEW);
                    }
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Plan For customer " + updatedValues + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    List<CustomersBasicDetailsPojo> custBasicDetailsPojoList = new ArrayList<CustomersBasicDetailsPojo>();
                    CustomersBasicDetailsPojo basicDetailsPojo = null;
                    if (requestDTO.getPlanGroupId() != null && requestDTO.getPlanGroupId() != 0 && requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)) {//TODO not in use
//                        if (requestDTO.getNewPlanList() != null) {
                        if (CollectionUtils.isEmpty(requestDTO.getNewPlanList())) {
                            List<Integer> newPlanList = requestDTO.getPlanBindWithOldPlans().stream().filter(newPlanBindWithOldPlan -> newPlanBindWithOldPlan.getNewPlanId() != null).map(NewPlanBindWithOldPlan::getNewPlanId).collect(Collectors.toList());
                            //Double maxValidity = requestDTO.getPlanList().stream().map(PostpaidPlan::getValidity).max(Double::compare).get();
                            requestDTO.setNewPlanList(newPlanList);
                        }
                        CustomChangePlanDTO customChangePlanDTO = null;
                        for (NewPlanBindWithOldPlan newPlanBindWithOldPlan : requestDTO.getPlanBindWithOldPlans()) {
                            if (newPlanBindWithOldPlan.getNewPlanId() != null) {
                                requestDTO.setPlanId(newPlanBindWithOldPlan.getNewPlanId());
                                requestDTO.setCustServiceMappingId(newPlanBindWithOldPlan.getCustServiceMappingId());
                                requestDTO.setIsTriggerCoaDm(true);
                                customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers, false, 0.0, requestFrom, null, number, requestDTOs.getDateOverrideDtos(),null);
                                Thread.sleep(1000);//Added bcuz sometimes in billing engine get conflict data.., need solution for this
                                if (customChangePlanDTO.getRecordpaymentResponseDTO() != null) {
                                    if (customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument().size() > 0 && !customers.getIsinvoicestop()) {
                                        List<CreditDocument> creditDocumentList = customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument();
                                        Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                                        Thread receiptThread = new Thread(receiptRunnable);
                                        receiptThread.start();
                                    }
                                }
                                basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                                if (!CollectionUtils.isEmpty(custChargeDetailsList)) {
                                    List<CustChargeOverrideDTO> custChargeOverrideDTOs = custChargeDetailsList.stream()
                                            .filter(custCharge -> custCharge.getParentId().equals(customers.getId()))
                                            .collect(Collectors.toList());

                                    for (CustChargeOverrideDTO custChargeOverrideDTO : custChargeOverrideDTOs) {
                                        custChargeOverrideDTO.setIsRenew(true);
                                        CustChargeOverrideDTO chargeOverrideDTO = custChargeService.createCustChargeOverride(custChargeOverrideDTO);
                                        basicDetailsPojo.setCustChargeOverride(chargeOverrideDTO);
                                        custChargeOverrideDTOS.add(chargeOverrideDTO);
                                    }
                                }
                                custBasicDetailsPojoList.add(basicDetailsPojo);
                            }
                        }

                        genericDataDTO.setData(custBasicDetailsPojoList);
                    } else {
//                        if (Objects.nonNull(requestDTOs.getIsTriggerCoaDm())) {
                        requestDTO.setIsTriggerCoaDm(true);
//                        }
                        CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers, false, 0.0, requestFrom, null, number, requestDTOs.getDateOverrideDtos(),null);
                        if (customChangePlanDTO.getRecordpaymentResponseDTO() != null) { //TODO not in use
                            if (customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument().size() > 0 && !customers.getIsinvoicestop()) {
                                List<CreditDocument> creditDocumentList = customChangePlanDTO.getRecordpaymentResponseDTO()
                                        .getCreditDocument();
                                Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                                Thread receiptThread = new Thread(receiptRunnable);
                                receiptThread.start();
                            }
                        }
                        basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                        basicDetailsPojo.setCustPackagId(customChangePlanDTO.getCustpackagerelid());
                        if (!CollectionUtils.isEmpty(custChargeDetailsList)) {
                            for (CustChargeOverrideDTO custChargeOverrideDTO : custChargeDetailsList) {
                                if (custChargeOverrideDTO.getCustid().equals(customers.getId())) {
                                    List<CustChargeDetailsPojo> custChargeDetailsPojoList = custChargeOverrideDTO.getCustChargeDetailsPojoList().stream().peek(custChargeDetailsPojo -> {
                                        custChargeDetailsPojo.setStartdate(customChangePlanDTO.getStartdate());
                                        custChargeDetailsPojo.setEnddate(customChangePlanDTO.getEnddate());
                                        custChargeDetailsPojo.setExpiry(customChangePlanDTO.getEnddate());
                                    }).collect(Collectors.toList());
                                    custChargeOverrideDTO.setCustChargeDetailsPojoList(custChargeDetailsPojoList);
                                    custChargeOverrideDTO.setIsRenew(true);
                                    custChargeOverrideDTO.setCustid(customers.getId());
                                    //                                if (custChargeOverrideDTO.getParentId() != null)
                                    //                                    custChargeOverrideDTO.setCustid(custChargeOverrideDTO.getParentId());
                                    CustChargeOverrideDTO chargeOverrideDTO = custChargeService.createCustChargeOverride(custChargeOverrideDTO);
                                    basicDetailsPojo.setCustChargeOverride(chargeOverrideDTO);
                                    custChargeOverrideDTOS.add(chargeOverrideDTO);

                                }
                            }
                        }
                        genericDataDTO.setData(basicDetailsPojo);

                    }


                    genericDataDTO.setTotalRecords(1);
                    genericDataDTO.setPageRecords(1);
                    genericDataDTO.setTotalPages(1);
                    genericDataDTO.setCurrentPageNumber(1);
                    String auditChangePlan = null;
                    if (requestDTO.getPurchaseType() != null) {
                        if (requestDTO.getPlanId() != null) {
//                            PostpaidPlan postpaidPlan = postpaidPlanService.get(requestDTO.getPlanId());
                            if (postpaidPlanData != null) {
                                auditChangePlan = customers.getFullName() + "change plan type is" + requestDTO.getPurchaseType()
                                        + "..and plan is :- " +postpaidPlanData.get(0)[4];
//                                log.info("Fetching All Hierarchy :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                            }
                        }


                    }
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW_PLANS,
                            req.getRemoteAddr(), auditChangePlan, customers.getId().longValue(), customers.getFullName());

                }

                try {
                    if (requestDTOs.getChangePlanRequestDTOList().get(0).getPurchaseType().equalsIgnoreCase("Addon")) {
                        if (customersforInvoice.size() > 1) {
                            Integer parentId = custIdOptional.get();
                            //this is because only when more than one child ia renewing plan, in revenue we need atleast one id as parent id
                            custIds.removeIf(i -> i.equals(parentId));
                            List<Integer> childIds = custIds;
                            debitDocService.createInvoice(customersforInvoice, Constants.ADD_ON, parentId, childIds, requestDTOs.getRecordPayment(), null, false, false, null,null);
                        } else {
                            debitDocService.createInvoice(customers, Constants.ADD_ON, "", requestDTOs.getRecordPayment(), null, null, false,false,null,null,null);
                        }
                    } else {
                        if (customersforInvoice.size() > 1) {
                            Integer parentId = custIdOptional.get();
                            //this is because only when more than one child ia renewing plan, in revenue we need atleast one id as parent id
                            custIds.removeIf(i -> i.equals(parentId));
                            List<Integer> childIds = custIds;
                            debitDocService.createInvoice(customersforInvoice, Constants.RENEW, parentId, childIds, requestDTOs.getRecordPayment(), null, false, false, null,null);
                        } else {
                            debitDocService.createInvoice(customers, Constants.RENEW, "", requestDTOs.getRecordPayment(), null, null, false,false,null,null,null);
                        }
                    }
//                            if (requestDTOs.getChangePlanRequestDTOList().get(0).getPurchaseType().equalsIgnoreCase("Addon")){
//                                debitDocService.createInvoice(customers,Constants.ADD_ON);
//                            }else {
//                                debitDocService.createInvoice(customers,Constants.RENEW);
//                            }
//                            flag = false;


                } catch (Exception e) {
                    log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for user " + customers.getId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    //ApplicationLogger.log.error(SUBMODULE + e.getStackTrace(), e);
                    e.printStackTrace();
                }

            }
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.log.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for user " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.log.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for user " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        return genericDataDTO;
    }


    @PostMapping(value = "/subscribeTopUp")
    public GenericDataDTO changePlanTopUp(@RequestBody WsSubscribeTopUp request, @RequestParam(name = "mvnoid", required = false) Long mvnoid, HttpServletRequest req) throws Exception {
        log.debug("In change plan TopUp :" + request.getSubscriberId());
        String SUBMODULE = MODULE + " [changePlan()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.FAIL;
        String requestFrom = "bss";
        Boolean flag = true;
        Thread invoiceThread = null;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        ChangePlanRequestDTOList requestDTOs = new ChangePlanRequestDTOList();
        Integer customerId = null;
        String custName = null;
        String username = null;
        Boolean isInvoiceStop = false;
        String fullName = null;
        Object[] customer = null;

        //set data in dto

        Object[] customersData = customerRepository.findCustomersByUserNameActiveOnly(request.getSubscriberId().trim());
        if (Objects.isNull(customersData) || customersData.length == 0) {
            genericDataDTO.setResponseMessage("Customer not available");
            RESP_CODE = APIConstants.SUCCESS;
            log.error(LogConstants.USERNAME_NOT_FOUND + username);
            return genericDataDTO;
        }
            customer = (Object[]) customersData[0];
            customerId = ((BigInteger) customer[0]).intValue();             //custid
            custName = (String) customer[1];               // custname
            username = (String) customer[2];               // username
            isInvoiceStop = (Boolean) customer[3];        // isinvoicestop


        List<Object[]> postpaidPlanData = postpaidPlanRepo.findPostpaidPlanDetailsByName(request.getTopUpPackageName());

        if (postpaidPlanData.isEmpty()) {
            genericDataDTO.setResponseMessage("planData not available");
            RESP_CODE = APIConstants.SUCCESS;
            log.debug("Postpaid Plan Not available :" + request.getTopUpPackageName());
            return genericDataDTO;
        } else if (!postpaidPlanData.get(0)[1].toString().equalsIgnoreCase("Volume Booster")) {
            genericDataDTO.setResponseMessage("planData not available");
            RESP_CODE = APIConstants.SUCCESS;
            log.debug("Thia Plan Is Not Volume Booster,Top-Up Not Happen : {}", request.getTopUpPackageName());
            return genericDataDTO;
        } else if (!postpaidPlanData.get(0)[2].toString().equalsIgnoreCase("Active")) {
            genericDataDTO.setResponseMessage("planData not available");
            RESP_CODE = APIConstants.SUCCESS;
            log.debug("Thia Plan Is Not Active :" + request.getTopUpPackageName());
            return genericDataDTO;
        }
        List<Integer> custServiceMappingId = customerServiceMappingRepository.custServicemappingIdByServiceIdAndCustomerId( ((Integer) postpaidPlanData.get(0)[3]).longValue(), customerId);
        List<ChangePlanRequestDTO> requestDTOList = new ArrayList<>();
        ChangePlanRequestDTO changePlanRequestDTOData = new ChangePlanRequestDTO();
        changePlanRequestDTOData.setPurchaseType("Addon");
        changePlanRequestDTOData.setPlanId((Integer) postpaidPlanData.get(0)[0]);
        changePlanRequestDTOData.setRemarks("");
        changePlanRequestDTOData.setPaymentOwnerId(mvnoid.intValue());
        changePlanRequestDTOData.setBillableCustomerId(customerId);
        // Check if startTime is null and set to current date if it is
        LocalDateTime localStartDate = (request.getStartTime() == 0)
                ? LocalDateTime.now() // Default to current date
                : Instant.ofEpochMilli(request.getStartTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        changePlanRequestDTOData.setAddonStartDate(localStartDate);
        if (request.getUpdateAction() == 1)
            changePlanRequestDTOData.setIsTriggerCoaDm(true);
        else if (request.getUpdateAction() == 0)
            changePlanRequestDTOData.setIsTriggerCoaDm(false);
        if (request.getUpdateAction() == 2)
            changePlanRequestDTOData.setIsTriggerCoaDm(true);


        LocalDateTime localEndDate = (request.getEndTime() == 0)
                ? LocalDateTime.now().plusDays(1) // Default to 1 days from now
                : Instant.ofEpochMilli(request.getEndTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        changePlanRequestDTOData.setAddonEndDate(localEndDate);
        if (localStartDate.isAfter(localEndDate)) {
            genericDataDTO.setResponseMessage("Expiry date can not be less than start date!");
            return genericDataDTO;
        }
        changePlanRequestDTOData.setIsAdvRenewal(false);
        changePlanRequestDTOData.setCustId(customerId);
        changePlanRequestDTOData.setCustServiceMappingId(custServiceMappingId.get(0));
        changePlanRequestDTOData.setIsParent(true);

        requestDTOList.add(changePlanRequestDTOData);
        requestDTOs.setChangePlanRequestDTOList(requestDTOList);

        if (requestDTOs != null) {
            Customers customers = customersRepository.findById(requestDTOs.getChangePlanRequestDTOList().get(0).getCustId()).get();
            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                if (requestFrom.equals("pw") && !getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficient(requestDTOs, getLoggedInUserPartnerId(),getMvnoIdFromCurrentStaff(customerId))) {
                    genericDataDTO.setResponseMessage("Partner has Insufficient balance!");
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    // log.error("Unable to customers with name " + customers.getCustname() + "  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                    return genericDataDTO;
                }

                if (requestFrom.equals("pw") && getLoggedInUser().getLco() && customersService.isPartnerBalanceInsufficientForLCO(requestDTOs, getLoggedInUserPartnerId(),getMvnoIdFromCurrentStaff(customerId))) {
                    genericDataDTO.setResponseMessage("Partner has Insufficient balance!");
                    RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                    log.debug(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "customers with name " + customers.getCustname() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    return genericDataDTO;
                }
            }
        }

        try {
            if (requestDTOs != null) {
                Integer custId = null;
                String number = String.valueOf(UtilsCommon.gen());
                List<ChangePlanRequestDTO> changePlanRequestDTOS = requestDTOs.getChangePlanRequestDTOList();
                changePlanRequestDTOS.removeIf(changePlanRequestDTO -> changePlanRequestDTO.getPlanId() == null);

                Optional<Integer> custIdOptional = requestDTOs.getChangePlanRequestDTOList().stream().filter(ChangePlanRequestDTO::getIsParent).map(ChangePlanRequestDTO::getCustId).findFirst();
                Set<Integer> custIdsWithoutDuplicates = requestDTOs.getChangePlanRequestDTOList().stream().filter(i -> !i.getIsParent()).map(ChangePlanRequestDTO::getCustId).collect(Collectors.toSet());
                List<Integer> custIds = new ArrayList<>();
                custIds.addAll(custIdsWithoutDuplicates);
                Set<Customers> customersforInvoice = new HashSet<>();
                if (custIds != null && custIds.size() > 0) {
                    customersforInvoice.addAll(customersRepository.findAllById(custIds));
                }
                if (custIdOptional.isPresent()) {
                    custId = custIdOptional.get();
                } else {
                    custIdOptional = requestDTOs.getChangePlanRequestDTOList().stream().map(ChangePlanRequestDTO::getCustId).findFirst();
                    if (custIdOptional.isPresent()) {
                        custId = custIdOptional.get();
                    }
                }

                //TODO
//                Customers parentCustomers = customersService.get(custId);
                Customers customers = customersRepository.findById(customerId).get();
                customersforInvoice.add(customers);//remove
                //TODO
                List<CustChargeOverrideDTO> custChargeDetailsList = requestDTOs.getCustChargeDetailsList();
                List<CustChargeOverrideDTO> custChargeOverrideDTOS = new ArrayList<>();
                for (ChangePlanRequestDTO requestDTO : requestDTOs.getChangePlanRequestDTOList()) {
                    if (null == requestDTO.getCustId()) {
                        genericDataDTO.setResponseMessage("Please provide customer id!");
                        RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
                        log.debug(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        return genericDataDTO;
                    }
                    if (requestDTO.getPaymentOwner() == null) {
                        requestDTO.setPaymentOwner("");
                    }
//                    Customers customers = customersService.get(requestDTO.getCustId());
                    String updatedValues = UtilsCommon.getUpdatedDiff(customers, requestDTO);
                    if (null == customers) {
                        genericDataDTO.setResponseMessage("Customer not found!");
                        RESP_CODE = APIConstants.NOT_FOUND;
                        genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                        log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                        return genericDataDTO;
                    }

                    if (requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_ADDON)) {
                        List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(customers.getId(), false);
                        if (currentPlanList.size() <= 0) {
                            genericDataDTO.setResponseMessage("Subscriber must have any active plan for AddOn Purchase");
                            RESP_CODE = APIConstants.NOT_FOUND;
                            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for customer " + customers.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                            return genericDataDTO;
                        }
                    }

                    if (subscriberService.getCustomerPlanList(customers.getId(), false).size() <= 0) {
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_NEW);
                    } else {
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_RENEW);
                    }
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Plan For customer " + updatedValues + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    List<CustomersBasicDetailsPojo> custBasicDetailsPojoList = new ArrayList<CustomersBasicDetailsPojo>();
                    CustomersBasicDetailsPojo basicDetailsPojo = null;
                    if (requestDTO.getPlanGroupId() != null && requestDTO.getPlanGroupId() != 0 && requestDTO.getPurchaseType().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)) {
//                        if (requestDTO.getNewPlanList() != null) {
                        if (CollectionUtils.isEmpty(requestDTO.getNewPlanList())) {
                            List<Integer> newPlanList = requestDTO.getPlanBindWithOldPlans().stream().filter(newPlanBindWithOldPlan -> newPlanBindWithOldPlan.getNewPlanId() != null).map(NewPlanBindWithOldPlan::getNewPlanId).collect(Collectors.toList());
                            //Double maxValidity = requestDTO.getPlanList().stream().map(PostpaidPlan::getValidity).max(Double::compare).get();
                            requestDTO.setNewPlanList(newPlanList);
                        }
                        CustomChangePlanDTO customChangePlanDTO = null;
                        for (NewPlanBindWithOldPlan newPlanBindWithOldPlan : requestDTO.getPlanBindWithOldPlans()) {
                            if (newPlanBindWithOldPlan.getNewPlanId() != null) {
                                requestDTO.setPlanId(newPlanBindWithOldPlan.getNewPlanId());
                                requestDTO.setCustServiceMappingId(newPlanBindWithOldPlan.getCustServiceMappingId());

                                if (request.getUpdateAction() == 1)
                                    requestDTO.setIsTriggerCoaDm(true);
                                else if (request.getUpdateAction() == 0)
                                    requestDTO.setIsTriggerCoaDm(false);
                                if (request.getUpdateAction() == 2)
                                    requestDTO.setIsTriggerCoaDm(true);

                                customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers, false, 0.0, requestFrom, null, number, requestDTOs.getDateOverrideDtos(),null);
                                if (customChangePlanDTO.getRecordpaymentResponseDTO() != null) {
                                    if (customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument().size() > 0 && !customers.getIsinvoicestop()) {
                                        List<CreditDocument> creditDocumentList = customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument();
                                        Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                                        Thread receiptThread = new Thread(receiptRunnable);
                                        receiptThread.start();
                                    }
                                }
                                basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                                if (!CollectionUtils.isEmpty(custChargeDetailsList)) {
                                    List<CustChargeOverrideDTO> custChargeOverrideDTOs = custChargeDetailsList.stream()
                                            .filter(custCharge -> custCharge.getParentId().equals(customers.getId()))
                                            .collect(Collectors.toList());

                                    for (CustChargeOverrideDTO custChargeOverrideDTO : custChargeOverrideDTOs) {
                                        custChargeOverrideDTO.setIsRenew(true);
                                        CustChargeOverrideDTO chargeOverrideDTO = custChargeService.createCustChargeOverride(custChargeOverrideDTO);
                                        basicDetailsPojo.setCustChargeOverride(chargeOverrideDTO);
                                        custChargeOverrideDTOS.add(chargeOverrideDTO);
                                    }
                                }
                                custBasicDetailsPojoList.add(basicDetailsPojo);
                            }
                        }

                        genericDataDTO.setData(custBasicDetailsPojoList);
                    } else {
                        if (request.getUpdateAction() == 1)
                            requestDTO.setIsTriggerCoaDm(true);
                        else if (request.getUpdateAction() == 0)
                            requestDTO.setIsTriggerCoaDm(false);

                        CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers, false, 0.0, requestFrom, null, number, requestDTOs.getDateOverrideDtos(),null);
                        if (customChangePlanDTO.getRecordpaymentResponseDTO() != null) {
                            if (customChangePlanDTO.getRecordpaymentResponseDTO().getCreditDocument().size() > 0 && !customers.getIsinvoicestop()) {
                                List<CreditDocument> creditDocumentList = customChangePlanDTO.getRecordpaymentResponseDTO()
                                        .getCreditDocument();
                                Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                                Thread receiptThread = new Thread(receiptRunnable);
                                receiptThread.start();
                            }
                        }
                        basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                        basicDetailsPojo.setCustPackagId(customChangePlanDTO.getCustpackagerelid());
                        if (!CollectionUtils.isEmpty(custChargeDetailsList)) {
                            for (CustChargeOverrideDTO custChargeOverrideDTO : custChargeDetailsList) {
                                if (custChargeOverrideDTO.getCustid().equals(customers.getId())) {
                                    List<CustChargeDetailsPojo> custChargeDetailsPojoList = custChargeOverrideDTO.getCustChargeDetailsPojoList().stream().peek(custChargeDetailsPojo -> {
                                        custChargeDetailsPojo.setStartdate(customChangePlanDTO.getStartdate());
                                        custChargeDetailsPojo.setEnddate(customChangePlanDTO.getEnddate());
                                        custChargeDetailsPojo.setExpiry(customChangePlanDTO.getEnddate());
                                    }).collect(Collectors.toList());
                                    custChargeOverrideDTO.setCustChargeDetailsPojoList(custChargeDetailsPojoList);
                                    custChargeOverrideDTO.setIsRenew(true);
                                    custChargeOverrideDTO.setCustid(customers.getId());
                                    //                                if (custChargeOverrideDTO.getParentId() != null)
                                    //                                    custChargeOverrideDTO.setCustid(custChargeOverrideDTO.getParentId());
                                    CustChargeOverrideDTO chargeOverrideDTO = custChargeService.createCustChargeOverride(custChargeOverrideDTO);
                                    basicDetailsPojo.setCustChargeOverride(chargeOverrideDTO);
                                    custChargeOverrideDTOS.add(chargeOverrideDTO);

                                }
                            }
                        }
                        genericDataDTO.setData(basicDetailsPojo);

                    }


                    genericDataDTO.setTotalRecords(1);
                    genericDataDTO.setPageRecords(1);
                    genericDataDTO.setTotalPages(1);
                    genericDataDTO.setCurrentPageNumber(1);
                    String auditChangePlan = null;
                    if (requestDTO.getPurchaseType() != null) {
                        if (requestDTO.getPlanId() != null) {
                            PostpaidPlan postpaidPlan = postpaidPlanService.get(requestDTO.getPlanId(),mvnoid.intValue());
                            if (postpaidPlan != null) {
                                auditChangePlan = customers.getFullName() + "change plan type is" + requestDTO.getPurchaseType()
                                        + "..and plan is :- " +postpaidPlanData.get(0)[4];
//                                log.info("Fetching All Hierarchy :  request: { From : {}}; Response : {{} code:{}}", req.getHeader("requestFrom"), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                            }
                        }

                    }
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_VIEW_PLANS,
                            req.getRemoteAddr(), auditChangePlan, customers.getId().longValue(), customers.getFullName());

                }

                try {
                    if (requestDTOs.getChangePlanRequestDTOList().get(0).getPurchaseType().equalsIgnoreCase("Addon")) {
                        if (customersforInvoice.size() > 1) {
                            Integer parentId = custIdOptional.get();
                            //this is because only when more than one child ia renewing plan, in revenue we need atleast one id as parent id
                            custIds.removeIf(i -> i.equals(parentId));
                            List<Integer> childIds = custIds;
                            debitDocService.createInvoice(customersforInvoice, Constants.ADD_ON, parentId, childIds, requestDTOs.getRecordPayment(), null, false, false, null,null);
                        } else {
                            debitDocService.createInvoice(customers, Constants.ADD_ON, "", requestDTOs.getRecordPayment(), null, null, false, false, null,null,null);
                        }
                    } else {
                        if (customersforInvoice.size() > 1) {
                            Integer parentId = custIdOptional.get();
                            //this is because only when more than one child ia renewing plan, in revenue we need atleast one id as parent id
                            custIds.removeIf(i -> i.equals(parentId));
                            List<Integer> childIds = custIds;
                            debitDocService.createInvoice(customersforInvoice, Constants.RENEW, parentId, childIds, requestDTOs.getRecordPayment(), null, false, false, null,null);
                        } else {
                            debitDocService.createInvoice(customers, Constants.RENEW, "", requestDTOs.getRecordPayment(), null, null, false, false, null,null,null);
                        }
                    }
//                            if (requestDTOs.getChangePlanRequestDTOList().get(0).getPurchaseType().equalsIgnoreCase("Addon")){
//                                debitDocService.createInvoice(customers,Constants.ADD_ON);
//                            }else {
//                                debitDocService.createInvoice(customers,Constants.RENEW);
//                            }
//                            flag = false;


                } catch (Exception e) {
                    log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for user " + customers.getId() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    //ApplicationLogger.log.error(SUBMODULE + e.getStackTrace(), e);
                    e.printStackTrace();
                }

            }
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                //ApplicationLogger.log.error(SUBMODULE + ex.getStackTrace(), ex);
                genericDataDTO.setResponseMessage(ex.getMessage());
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for user " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                return genericDataDTO;
            }
            //ApplicationLogger.log.error(SUBMODULE + ex.getStackTrace(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch change plan for user " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        }
        return genericDataDTO;
    }

    @PostMapping("/addSubscriberAcctXML")
    public ResponseEntity<?> addSubscriberAcctXML(@Valid @RequestBody SubscriberAccount request,
                                                  @RequestParam(name = "serviceArea", required = false) Long serviceAreaId,
                                                  @RequestParam(name = "mvnoid", required = false) Long mvnoid,
                                                  @RequestParam(name = "plan", required = false) String plan, HttpServletRequest req) throws Exception {
        log.debug("In addSubscriberAcctXML : " + request.getName());
        TraceContext traceContext = tracer.currentSpan().context();
        LoggedInUser loggedInUser = getLoggedInUser();
        MDC.put("type", "Create");
        MDC.put("userName", loggedInUser.getUsername());
//        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
//        MDC.put("spanId", traceContext.spanIdString());
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        String requestFrom = "bss";
        CustomersPojo pojo = new CustomersPojo();
        String userName = request.getName().trim();
        String password = request.getPassword().trim();

        try {

            pojo.setUsername(userName);
            Boolean UsernameExist = customerRepository.customerUsernameIsAlreadyExists(userName, mvnoid);
            if (UsernameExist) {
                response.put("message", "username is already exist");
                log.error(LogConstants.USERNAME_ALREADY_EXIST + userName);
                return apiResponse(SoapConstant.SUCCESS_CODE, response);
            }
            pojo.setPassword(password);
            pojo.setTitle("Mr");
            pojo.setFirstname(userName);
            pojo.setLastname(userName);
            pojo.setMobile("1234567890");
            pojo.setEmail("default@default.com");
            pojo.setContactperson("");
            pojo.setCustcategory("");
            pojo.setDunningCategory("Platinum");
            pojo.setContactperson(userName);
            pojo.setServiceareaid(serviceAreaId);
            pojo.setStatus(request.getActivated());
            LocalDate date = LocalDate.now();
            pojo.setBillday(date.getDayOfMonth());
            pojo.setMaxconcurrentsession(1);
            pojo.setAcctno(userName);
            pojo.setMac_provision(true);
            pojo.setMac_auth_enable(true);
            if ("y".equalsIgnoreCase(request.getActivated())) {
                pojo.setStatus("Active");
            } else if ("n".equalsIgnoreCase(request.getActivated())) {
                pojo.setStatus("Inactive");
            } else if ("suspend".equalsIgnoreCase(request.getActivated())) {
                pojo.setStatus("Suspend");
            }
            if (!request.getLocationLock().isEmpty() && request.getLocationLock() != null) {
                String regex = "^(0:92=\")\\[(.*)\\]\"$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(request.getLocationLock());
                if (matcher.find()) {
                    if (matcher.groupCount() >= 2) { // Ensure there are at least two groups
                        String captureNumeric = matcher.group(1); // Get the first capturing group (0:92=")
                        String extractedData = matcher.group(2); // Get the second capturing group (content inside the brackets)
                        pojo.setNasPortId(extractedData);
                    } else {
                        response.put("message", "Invalid Location Lock Value");
                        return apiResponse(SoapConstant.SUCCESS_CODE, response);
                    }
                } else {
                    response.put("message", "Invalid Location Lock Value ");
                    return apiResponse(SoapConstant.SUCCESS_CODE, response);
                }
            }

            CustPlanMapppingPojo custPlanMappping;
            List<CustPlanMapppingPojo> planMappingList = new ArrayList<>();
            PostpaidPlan postpaidPlan = null;
            if (!request.getServiceSubscriptions().isEmpty() && !request.getServiceSubscriptions().get(0).serviceId.isEmpty()) {
                List<PostpaidPlan> postpaidPlans = postpaidPlanRepo.findPostpaidPlanByname(request.getServiceSubscriptions().get(0).serviceId);
                if (!postpaidPlans.isEmpty()) {
                    postpaidPlan = postpaidPlans.get(0);
                }
            } else {
//                Optional<PostpaidPlan> optionalPlan = postpaidPlanRepo.findById(planId);
                List<PostpaidPlan> optionalPlan = postpaidPlanRepo.findPostpaidPlanByname(plan);
                if (!optionalPlan.isEmpty()) {
                    postpaidPlan = optionalPlan.get(0);
                }
            }
            if (postpaidPlan == null) {
                response.put("message", "Service not available");
                return apiResponse(SoapConstant.SUCCESS_CODE, response);
            }
            pojo.setPlanName(postpaidPlan.getName());
            custPlanMappping = new CustPlanMapppingPojo();
            custPlanMappping.setPlanId(postpaidPlan.getId());
            Services services = serviceRepository.findById(Long.valueOf(postpaidPlan.getServiceId())).get();
            custPlanMappping.setService(String.valueOf(services.getServiceName()));
            custPlanMappping.setValidity(postpaidPlan.getValidity());
            custPlanMappping.setServiceId(postpaidPlan.getServiceId());
            custPlanMappping.setDiscount(0.0);
            custPlanMappping.setBillTo("CUSTOMER");
            custPlanMappping.setDiscountType("One-time");
            custPlanMappping.setDiscountExpiryDate(null);
            custPlanMappping.setIsInvoiceToOrg(false);
            custPlanMappping.setSkipQuotaUpdate(true);

            planMappingList.add(custPlanMappping);

            pojo.setPlanMappingList(planMappingList);
            pojo.setCalendarType("English");
            if (postpaidPlan.getPlantype().equalsIgnoreCase("prepaid")) {
                pojo.setCusttype("Prepaid");
                pojo.setCustomerType("Prepaid");
            } else {
                pojo.setCusttype("Postpaid");
                pojo.setCustomerType("Postpaid");
            }
            pojo.setCustlabel("customer");

            //set address liss
            CustomerAddressPojo customerAddressPojo = new CustomerAddressPojo();
            customerAddressPojo.setAddressType("Present");
            customerAddressPojo.setStateId(2);
            customerAddressPojo.setAreaId(2);
            customerAddressPojo.setCityId(2);
            customerAddressPojo.setCountryId(2);
            customerAddressPojo.setLandmark("HYD");
            customerAddressPojo.setPincodeId(2);
            customerAddressPojo.setVersion("new");

            pojo.setAddressList(Collections.singletonList(customerAddressPojo));

            RecordPaymentPojo recordPaymentPojo = pojo.getPaymentDetails();
            customersService.validateMendatoryFields(pojo);
            CustomersService customersService = SpringContext.getBean(CustomersService.class);

            BillRunService billRunService = SpringContext.getBean(BillRunService.class);
//     ***       customersService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1)
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            CustomersPojo customersPojo = pojo;
            pojo.setDunningCategory(pojo.getDunningCategory());
            pojo.setDunningSector(pojo.getCustomerSector());
            pojo.setCustcategory(pojo.getDunningCategory());
            pojo.setDunningType(pojo.getCustomerType());
            pojo.setCustomerSector(pojo.getDunningSector());
            if (pojo.getEarlybillday() == null) {
                pojo.setEarlybillday(0);
            } else {
                pojo.setEarlybillday(pojo.getEarlybillday());
            }
            if (pojo.getEarlybilldays() == null) {
                pojo.setEarlybilldays(0);
            } else {
                pojo.setEarlybilldays(pojo.getEarlybilldays());
            }
            pojo.setPartnerid(CommonConstants.DEFAULT_PARTNER_ID);

            // Save customer
            pojo = customersService.save(pojo, requestFrom, false);
            if (pojo.getCreatedById() == null) {
                pojo.setCreatedById(loggedInUser.getUserId());
            }

            if (pojo.getCreatedByName() == null) {
                pojo.setCreatedByName(loggedInUser.getFullName());
            }
            customersPojo.setId(pojo.getId());
            //save customer for all microservice
            if (pojo != null) {
                if (customersPojo.getPaymentDetails() != null) {
                    pojo.setPaymentDetails(customersPojo.getPaymentDetails());
                }
                pojo.setCustomerCreated(true);
                customersService.sharedCustomerData(pojo, false);
            }
//            customersService.saveDataForOrganizationCustomer(pojo, pojo.getDiscount(), 0L, pojo.getPlanMappingList().get(0).getPlangroupid(), pojo.getCusttype());
            boolean isInvoiceCreated = false;
/*            try {
                if ((pojo.getCusttype() != null & !"".equals(pojo.getCusttype()) && pojo.getCusttype().equalsIgnoreCase("Prepaid")) && (recordPaymentPojo == null || (recordPaymentPojo != null && recordPaymentPojo.getAmount() <= 0))) {
                    //   log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"create customers"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                } else {
                    // Generate Invoice
                    if (null != pojo.getPlanMappingList() && 0 < pojo.getPlanMappingList().size() && !pojo.getIstrialplan() && !pojo.getIsinvoicestop()) {
                        isInvoiceCreated = true;
                    }
                    // Generate Receipt
                    Customers customers = customersService.savePaymentXMLDocument(customerMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext()));
                    if (null != customers) {
                        Runnable receiptRunnable = new ReceiptThread(billRunService, customers.getCreditDocuments());
                        Thread receiptThread = new Thread(receiptRunnable);
                        receiptThread.start();
                    }

                    customers = customerMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext());
                    getLocationmapping(pojo);
                    // Generate Charge Invoice
                    if (null != customers && null != customers.getOverChargeList() && 0 < customers.getOverChargeList().size() && !pojo.getIstrialplan() && !pojo.getIsinvoicestop()) {
                        List<Integer> custChargeIdList = new ArrayList<>();
                        customers.getOverChargeList().forEach(data -> custChargeIdList.add(data.getId()));
                        Runnable chargeRunnable = new ChargeThread(customers.getId(), custChargeIdList, customersService, 0L, "", null);
                        Thread billchargeThread = new Thread(chargeRunnable);
                        billchargeThread.start();
                        RESP_CODE = APIConstants.SUCCESS;
                        log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
                    }
                }
            } catch (Exception e) {
                log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }*/

//            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_CUSTOMER, AclConstants.OPERATION_CUSTOMER_ADD, req.getRemoteAddr(), null, pojo.getId().longValue(), "");
            String SUBMODULE = MODULE + " [save()] ";

            response.put("customer", pojo);
         /*   if (pojo.getOltid() != null) {
                NetworkDevices networkDevices = networkDeviceRepository.findById(pojo.getOltid()).orElse(null);
                if (networkDevices != null) {
                    pojo.setOltName(networkDevices.getName());
                }
            }*/
         /*   if (pojo.getPopid() != 0) {
                PopManagement popManagement = popManagementRepository.findById(pojo.getPopid()).orElse(null);
                if (popManagement != null) {
                    pojo.setOltName(popManagement.getPopName());
                }
            }*/
            CustomerMessageIn customerMessageIn = new CustomerMessageIn(pojo);
//            messageSender.send(customerMessageIn, RabbitMqConstants.QUEUE_CUSTOMER_SUCCESS);
            kafkaMessageSender.send(new KafkaMessageData(customerMessageIn, CustomerMessageIn.class.getSimpleName()));
          /*  if (pojo.getLeadId() != null) {
                LeadMaster leadMaster = leadMasterRepository.findById(pojo.getLeadId()).get();
                leadMaster.setNextApproveStaffId(null);
                leadMaster.setNextTeamMappingId(null);
                leadMasterRepository.save(leadMaster);
                LeadMgmtWfDTO leadMgmtWfDTO = new LeadMgmtWfDTO(leadMaster);
                SendApproverForLeadMsg sendApproverForLeadMsg = new SendApproverForLeadMsg(leadMgmtWfDTO);
                kafkaMessageSender.send(new KafkaMessageData(sendApproverForLeadMsg, SendApproverForLeadMsg.class.getSimpleName()));
//                messageSender.send(sendApproverForLeadMsg, RabbitMqConstants.QUEUE_SEND_APPROVER_DETAIL);
            }*/

            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (RuntimeException exception) {
            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
            response.put(APIConstants.ERROR_TAG, exception.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + exception.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create customers" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + loggedInUser.getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/UpdateSubscriberAccount")
    public ResponseEntity<?> updateSubscriberAccount(@Valid @RequestBody SubscriberAccountDTO request, @RequestParam(name = "mvnoid", required = false) Long mvnoid, HttpServletRequest req) {
        log.debug("In Update Subscriber Account :" + request.getName().trim());
        String msg = "";
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        String requestFrom = "bss";
        CustomersPojo pojo = new CustomersPojo();
        String username = request.getName().toLowerCase().trim();
        String status = request.getActivated();

        try {
            Customers customers = customersRepository.findByUsernameAndMvnoId(username, Math.toIntExact(mvnoid))
                    .orElseThrow(() -> new CustomValidationException(204, "Username is not available in SPR", null));
            if(customers == null) {
                log.error(LogConstants.USERNAME_NOT_FOUND + username);
            }
            GenericDataDTO customerStatus = changeSubscriberAccountStatus(customers.getId(), status, req, request.getPassword(), request.getLocationLock());
            RESP_CODE = HttpStatus.OK.value();
            response.put("data", customerStatus);
            response.put("responseMessage", customerStatus.getResponseMessage());
        } catch (SQLException ce) {
            ce.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update customer" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (CustomValidationException ce) {
            //		ApplicationLogger.log.error(MODULE + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put("responseMessage", ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update customer" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            //		ApplicationLogger.log.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update customer" + LogConstants.LOG_BY_NAME + pojo.getUsername() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return apiResponse(RESP_CODE, response);
    }


    public void sendCustStatusInActiveMessage(String username, String mobileNumber, String emailId, String status, Integer mvnoId,Long staffId) {
        try {
            log.debug("In sendCustStatusInActiveMessage : {}", username);
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_STATUS_INACTIVATE_TEMPLATE);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    Long buId = null;
                    if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                        buId = getBUIdsFromCurrentStaff().get(0);
                    }
                    CustomerStatusInActiveMessage customerStatusInActiveMessage = new CustomerStatusInActiveMessage(username, mobileNumber, emailId, status, mvnoId, RabbitMqConstants.CUSTOMER_STATUS_INACTIVATE_EVENT, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, buId,staffId);
                    Gson gson = new Gson();
                    gson.toJson(customerStatusInActiveMessage);
                    kafkaMessageSender.send(new KafkaMessageData(customerStatusInActiveMessage, CustomerStatusInActiveMessage.class.getSimpleName()));
//                    messageSender.send(customerStatusInActiveMessage, RabbitMqConstants.QUEUE_CUSTOMER_STATUS_INACTIVATE_NOTIFICATION);
                }
            } else {
                System.out.println("Message of Customer Status InActive is not sent because template is not present.");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private GenericDataDTO changeSubscriberAccountStatus(Integer id, String status, HttpServletRequest req, String newPassword, String locationeLock) throws Exception {
        log.debug("In changeSubscriberAccountStatus Method: {}", id);
        String msg = "";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Customers customers = customersService.getEntityForUpdateAndDelete(id,getMvnoIdFromCurrentStaff(id));
//        List<CreditDocument> creditDocuments = creditDocRepository.findAllByCustId(id);
        Integer RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
        SubscriberAccountDTO subscriberAccountDTO = new SubscriberAccountDTO();
        try {
          /*  if (creditDocuments.size() > 0 && status.equalsIgnoreCase("Terminate")) {
                Boolean paymentStatus = creditDocuments.stream().anyMatch(i -> i.getStatus().equalsIgnoreCase("pending"));
                if (paymentStatus) {
                    throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Termination not allowed as Payment is Pending for Approval!", null);
                }
            }*/
            if (customers.getStatus() != null && customers.getStatus().equalsIgnoreCase("Terminate")) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer is Already terminated.", null);
            }
            if (status != null && status.equalsIgnoreCase("Terminate")) {
                msg = "Customer " + customers.getCustname() + " status has been assing to approval to Process";
            } else {
                if (status != null && (status.equalsIgnoreCase("Y") || status.equalsIgnoreCase("N") || status.equalsIgnoreCase("SUSPEND"))) {
                    status = changeStatusValue(status);
                }
                if (status != null && !status.isEmpty())
                    customers.setStatus(status);
                if (newPassword != null && !newPassword.isEmpty())
                    customers.setPassword(newPassword);
                if (locationeLock != null && !locationeLock.isEmpty()) {
                    String regex = "^(0:92=\")\\[(.*)\\]\"$";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(locationeLock);
                    if (matcher.find()) {
                        if (matcher.groupCount() >= 2) { // Ensure there are at least two groups
                            String captureNumeric = matcher.group(1); // Get the first capturing group (0:92=")
                            String extractedData = matcher.group(2); // Get the second capturing group (content inside the brackets)
                            customers.setNasPortId(extractedData);
                        } else {
                            genericDataDTO.setResponseMessage("invalid location lock");
                            genericDataDTO.setResponseCode(SoapConstant.SUCCESS_CODE);
                            return genericDataDTO;
                        }
                    } else {
                        genericDataDTO.setResponseMessage("invalid location lock");
                        genericDataDTO.setResponseCode(SoapConstant.SUCCESS_CODE);
                        return genericDataDTO;
                    }
                }

                msg = "Customer " + customers.getCustname() + " status has been changed.";
                RESP_CODE = HttpStatus.OK.value();
                subscriberAccountDTO.setPassword(customers.getPassword());
                subscriberAccountDTO.setActivated(customers.getStatus());
                genericDataDTO.setResponseMessage(msg);
            }

            customersRepository.save(customers);

            CustomerUpdateMessage message = new CustomerUpdateMessage(customers);
            kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));

//            if (customers.getStatus() != null && customers.getStatus().equalsIgnoreCase("Active"))
//                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));

//            if (customers.getStatus() != null && customers.getStatus().equalsIgnoreCase("Suspend"))
//                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));

            //Notification for customer status Inactive.
//            if (customers.getStatus() != null && customers.getStatus().equalsIgnoreCase("InActive")) {
//                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
//                sendCustStatusInActiveMessage(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getStatus(), customers.getMvnoId());
//            }
            genericDataDTO.setData(subscriberAccountDTO);
            return genericDataDTO;
        } catch (Exception e) {
            throw e;
        }
//        return subscriberAccountDTO;
    }

    public List<Customers> changeCustomerStatusAndPassword(List<Customers> customers, String status, String password, String locationLock) {
        log.debug("changeCustomerStatusAndPassword : {}", customers);
        try {
            customers = customers.stream()
                    .peek(customer -> {
                        if (status != null) {
                            customer.setStatus(status);
                        }
                        if (password != null) {
                            customer.setPassword(password);
                        }
                        if (locationLock != null) {
                            customer.setNasPortId(locationLock);// Assuming `password` is a field in Customers.
                        }
                    })
                    .collect(Collectors.toList());

            customersRepository.saveAll(customers);
        } catch (Exception ex) {
            throw new CustomValidationException(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Exception while updating customer status and password: " + ex.getMessage(),
                    null
            );
        }
        return customers;
    }

    @PostMapping(value = "/changeAddonSubscription")
    public GenericDataDTO changeAddonSubscription(@RequestBody WsChangeAddOnSubscription request, @RequestParam(name = "mvnoid", required = false) Long mvnoid, HttpServletRequest req) throws Exception {
        log.debug("In changeAddonSubscription :" + request.getSubscriberId().trim());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        HashMap<String, Object> response = new HashMap<>();
        try {

//        Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(request.getAddOnName());

            Optional<DebitDocument> debitDocument = Optional.empty();
            Customers customers = customersRepository.findByUsernameAndMvnoIdAndStatusNot(request.getSubscriberId(), Math.toIntExact(mvnoid), "Terminate").orElse(null);
            if (customers == null) {
                genericDataDTO.setResponseMessage("Username is not available in SPR Table");
                log.error(LogConstants.USERNAME_NOT_FOUND + request.getSubscriberId().trim());
                return genericDataDTO;
            }
            CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(request.getAddOnSubscriptionId());
            if (custPlanMappping == null) {
                genericDataDTO.setResponseMessage("Invalid subscription status received");
                return genericDataDTO;
            } else if (!custPlanMappping.getCustPlanStatus().equalsIgnoreCase("Active")) {
                genericDataDTO.setResponseMessage("Plan already expired");
                return genericDataDTO;
            }
            Optional<CustomerServiceMapping> customerServiceMapping = customerServiceMappingRepository.findById(custPlanMappping.getCustServiceMappingId());

            Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId());
            if (!postpaidPlan.isPresent()) {
                throw new RuntimeException("New Plan not available!");
            }

            if (!postpaidPlan.get().getName().equalsIgnoreCase(request.getAddOnName()) || (!custPlanMappping.getPurchaseType().equalsIgnoreCase("Volume Booster") && !custPlanMappping.getPurchaseType().equalsIgnoreCase("Bandwidthbooster"))) {
                genericDataDTO.setResponseMessage("AddOn subcription not found by subscriberId: " + request.getSubscriberId());
                return genericDataDTO;
            }
            LocalDateTime expDate = null;
            LocalDateTime endDate = null;


            if (custPlanMappping != null) {
                if (custPlanMappping.getCustomer().getId() != customers.getId()) {
                    WsChangeAddOnSubscriptionResponse wsChangeAddOnSubscriptionResponse = new WsChangeAddOnSubscriptionResponse();
                    genericDataDTO.setResponseMessage("Invalid addon-SubscriptionId");
                    genericDataDTO.setData(wsChangeAddOnSubscriptionResponse);
                    return genericDataDTO;
                }
                custPlanMappping.setEndDate(LocalDateTime.now().minusSeconds(1));
                custPlanMappping.setExpiryDate(LocalDateTime.now().minusSeconds(1));
                custPlanMappping.setIsVoid(Boolean.TRUE);
                custPlanMappping.setCustPlanStatus("STOP");

                if (customers.getIstrialplan()) {
                    custPlanMappping.setIstrialplan(false);
                }
                custPlanMappping.setCustServiceMappingId(customerServiceMapping.get().getId());
                custPlanMappingRepository.save(custPlanMappping);
                if (request.getUpdateAction() != null && request.getUpdateAction() == 1)
                    custPlanMappping.setSkipQuotaUpdate(true);
                else if (request.getUpdateAction() != null && request.getUpdateAction() == 0)
                    custPlanMappping.setSkipQuotaUpdate(false);
                custPlanMappingService.updateCustPlanEndDateInRadius(custPlanMappping, CommonConstants.EVENTCONSTANTS.QUOTA_BOOSTER_EXPIRE);
            }
            if (customerServiceMapping != null) {
                customerServiceMapping.get().setCustId(custPlanMappping.getCustomer().getId());
                customerServiceMapping.get().setStatus("STOP");
                customerServiceMappingRepository.save(customerServiceMapping.get());
            }

            WsChangeAddOnSubscriptionResponse wsChangeAddOnSubscriptionResponse = new WsChangeAddOnSubscriptionResponse();
            wsChangeAddOnSubscriptionResponse.setAddOnId(postpaidPlan.get().getId());
            wsChangeAddOnSubscriptionResponse.setAddOnName(postpaidPlan.get().getName());
            wsChangeAddOnSubscriptionResponse.setAddOnStatus(postpaidPlan.get().getStatus());
            wsChangeAddOnSubscriptionResponse.setAddonSubscriptionId(custPlanMappping.getId());
            wsChangeAddOnSubscriptionResponse.setEndTime(custPlanMappping.getEndDate());
            wsChangeAddOnSubscriptionResponse.setParameter1("");
            wsChangeAddOnSubscriptionResponse.setParameter2("");
            wsChangeAddOnSubscriptionResponse.setUsageResetTime(custPlanMappping.getEndDate());
            wsChangeAddOnSubscriptionResponse.setSubscriberIdentity(customers.getUsername());

            TerminateAddOnMessage terminateAddOnMessage = new TerminateAddOnMessage();
            terminateAddOnMessage.setCustId(customers.getId());
            terminateAddOnMessage.setStatus("terminate");
            terminateAddOnMessage.setGenerateCreditnote(true);
            terminateAddOnMessage.setCustpackrelid(request.getAddOnSubscriptionId());
//                messageSender.send(cutomerCustomerTerminationMessage, SharedDataConstants.QUEUE_CUSTOMER_TERMINATION_DATA_REVENUE);
            kafkaMessageSender.send(new KafkaMessageData(terminateAddOnMessage, TerminateAddOnMessage.class.getSimpleName()));


            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setData(wsChangeAddOnSubscriptionResponse);
            return genericDataDTO;
        } catch (Exception ex) {
            throw new CustomValidationException(
                    HttpStatus.EXPECTATION_FAILED.value(),
                    "Exception while updating customer status and password: " + ex.getMessage(),
                    null
            );
        }
    }


    @PostMapping(value = "/changeTopUpSubscription")
    public GenericDataDTO changeTopUpSubscription(@RequestBody WsChangeTopUpSubscription request, @RequestParam(name = "mvnoid", required = false) Long mvnoid, HttpServletRequest req) throws Exception {
        //cpr
        //username
        //planname
        log.debug("In changeTopUpSubscription :" + request.getSubscriberId().trim());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(request.getAddOnName());

        Optional<DebitDocument> debitDocument = Optional.empty();
        Customers customers = customersRepository.findByUsernameAndMvnoIdAndStatusNot(request.getSubscriberId(), Math.toIntExact(mvnoid), "Terminate").orElse(null);
        if (customers == null) {
            genericDataDTO.setResponseMessage("Username is not available in SPR Table");
            log.error(LogConstants.USERNAME_NOT_FOUND + request.getSubscriberId().trim());
            return genericDataDTO;
        }
        CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(request.getTopUpSubscriptionId());
        if (custPlanMappping == null) {
            genericDataDTO.setResponseMessage("Invalid subscription status received");
            return genericDataDTO;
        } else if (!custPlanMappping.getCustPlanStatus().equalsIgnoreCase("Active")) {
            genericDataDTO.setResponseMessage("Plan already expired");
            return genericDataDTO;
        }
        Optional<CustomerServiceMapping> customerServiceMapping = customerServiceMappingRepository.findById(custPlanMappping.getCustServiceMappingId());

        Optional<PostpaidPlan> postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId());
        if (!postpaidPlan.isPresent()) {
            throw new RuntimeException("New Plan not available!");
        }
        LocalDateTime expDate = null;


        if (custPlanMappping != null) {
            if (custPlanMappping.getCustomer().getId() != customers.getId()) {
                WsChangeTopUpSubscriptionResponse wsChangeTopUpSubscriptionResponse = new WsChangeTopUpSubscriptionResponse();
                genericDataDTO.setResponseMessage("Invalid top-Up-SubscriptionId");
                genericDataDTO.setData(wsChangeTopUpSubscriptionResponse);
                return genericDataDTO;
            }
            custPlanMappping.setEndDate(LocalDateTime.now().minusSeconds(1));
            custPlanMappping.setExpiryDate(LocalDateTime.now().minusSeconds(1));
            custPlanMappping.setIsVoid(Boolean.TRUE);
            custPlanMappping.setCustPlanStatus("STOP");
            if (customers.getIstrialplan()) {
                custPlanMappping.setIstrialplan(false);
            }
            custPlanMappingRepository.save(custPlanMappping);

            custPlanMappping.setCustServiceMappingId(customerServiceMapping.get().getId());
            if (request.getUpdateAction() != null && request.getUpdateAction() == 1) {
                custPlanMappping.setSkipQuotaUpdate(true);
            } else if (request.getUpdateAction() != null && request.getUpdateAction() == 0) {
                custPlanMappping.setSkipQuotaUpdate(false);
            }

            custPlanMappingService.updateCustPlanEndDateInRadius(custPlanMappping, CommonConstants.EVENTCONSTANTS.VOLUME_BOOSTER_EXPIRE);

        }
        if (customerServiceMapping != null) {
            customerServiceMapping.get().setCustId(custPlanMappping.getCustomer().getId());
            customerServiceMapping.get().setStatus("STOP");
            customerServiceMappingRepository.save(customerServiceMapping.get());
        }


        WsChangeTopUpSubscriptionResponse wsChangeTopUpSubscriptionResponse = new WsChangeTopUpSubscriptionResponse();
        wsChangeTopUpSubscriptionResponse.setEndTime(custPlanMappping.getEndDate());
        wsChangeTopUpSubscriptionResponse.setSubscriberIdentity(customers.getUsername());
        wsChangeTopUpSubscriptionResponse.setTopUpId(postpaidPlan.get().getId());
        wsChangeTopUpSubscriptionResponse.setTopUpName(postpaidPlan.get().getName());
        wsChangeTopUpSubscriptionResponse.setTopUpStatus(postpaidPlan.get().getStatus());
        wsChangeTopUpSubscriptionResponse.setTopUpSubscriptionId(custPlanMappping.getId());
        wsChangeTopUpSubscriptionResponse.setUsageResetTime(custPlanMappping.getEndDate());

        TerminateAddOnMessage terminateAddOnMessage = new TerminateAddOnMessage();
        terminateAddOnMessage.setCustId(customers.getId());
        terminateAddOnMessage.setStatus("terminate");
        terminateAddOnMessage.setGenerateCreditnote(true);
        terminateAddOnMessage.setCustpackrelid(request.getTopUpSubscriptionId());
//                messageSender.send(cutomerCustomerTerminationMessage, SharedDataConstants.QUEUE_CUSTOMER_TERMINATION_DATA_REVENUE);
        kafkaMessageSender.send(new KafkaMessageData(terminateAddOnMessage, TerminateAddOnMessage.class.getSimpleName()));


        genericDataDTO.setResponseMessage("Success");
        genericDataDTO.setData(wsChangeTopUpSubscriptionResponse);
        return genericDataDTO;
    }


    public boolean isMacValid(String mac) {
        String macRegex = "^([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}$|" +  // Colon or hyphen-separated
                "^([0-9A-Fa-f]{4}\\.){2}[0-9A-Fa-f]{4}$";     // Dot-separated
//        String macRegex = "([0-9A-Fa-f]{2}([:.\\\\-])){5}[0-9A-Fa-f]{2}";
        Pattern macPattern = Pattern.compile(macRegex);
        Matcher macMatcher = macPattern.matcher(mac);
        return macMatcher.matches();
    }

    public boolean isIpValid(String IP) {
        String macRegex = "([0-9A-Fa-f]{1,4}([:.\\-])?){6}";
        Pattern macPattern = Pattern.compile(macRegex);
        Matcher macMatcher = macPattern.matcher(IP);
        return macMatcher.matches();
    }

    public String changeStatusValue(String status) {
        String changedStatus = "";
        if (status != null && status.equalsIgnoreCase("Y")) {
            changedStatus = "Active";
            return changedStatus;
        } else if (status != null && status.equalsIgnoreCase("N")) {
            changedStatus = "InActive";
            return changedStatus;
        }
        return status;
    }
}
