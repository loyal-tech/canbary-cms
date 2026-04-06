package com.adopt.apigw.service.common;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Cas.Domain.CasMaster;
import com.adopt.apigw.modules.Cas.Domain.CasMasterRepository;
import com.adopt.apigw.modules.Cas.Domain.CasParameterMapping;
import com.adopt.apigw.modules.Cas.Repository.CasePackageRepository;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import com.adopt.apigw.modules.InventoryManagement.item.ItemRepository;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.postpaid.CustPlanMappingService;
import com.adopt.apigw.service.postpaid.PlanServiceService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.ezbill.entity.*;
import com.adopt.ezbill.service.EZBillService;
import com.adopt.ezbill.service.EZBillServiceImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Component
public class EzBillServiceUtility {
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    PlanServiceRepository planServiceRepository;

    @Autowired
    CasePackageRepository casePackageRepository;

    @Autowired
    CustomerAddressRepository customerAddressRepository;

    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private PlanCasMappingRepository planCasMappingRepository;

    private final EZBillService ezBillService = new EZBillServiceImpl();

    String authToken = "5a4c89b6321cd3.84561630";

    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;
    @Autowired
    private CasMasterRepository casMasterRepository;
    @Autowired
    private ItemRepository itemRepository;


    DeactivateReasonInfoResponse getDeactiveReasons(DeactivateReasonInfo deactivateReasonInfo) {
        deactivateReasonInfo.setAuthToken("authtoken");
        String endpointURL = "";
        return ezBillService.getDeactivateReason(deactivateReasonInfo, endpointURL);

    }

    public static String getAuthTokenFromCAS(CasMaster casMaster, String authToken) {
        if (casMaster != null) {
            if (casMaster.getCasParameterMappings().size() > 0) {
                List<CasParameterMapping> casParameterMappings = casMaster.getCasParameterMappings().stream().filter(casParamaterMapping -> casParamaterMapping.getParamName().equalsIgnoreCase(CommonConstants.CAS_PARAMS.AUTH_TOKEN_EZ_BILL)).collect(Collectors.toList());
                if (casParameterMappings.size() > 0) {
                    authToken = casParameterMappings.get(0).getParamValue();
                } else {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Auth token parameter not set in this CAS.", null);
                }
            }
        } else {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "CAS not found", null);
        }
        return authToken;
    }

    public void createCustomer(Customers customer, String authToken, String boxNumber, CasMaster casMaster, String connectionNumber, Integer custID, CustomerInventoryMappingDto customerInventoryMappingDto) {
        if (customer.getParentCustomers() != null) {
            custID = Integer.valueOf(customer.getParentCustomers().getEzyBillCustomersId());
            customer.setEzyBillCustomersId(String.valueOf(custID));
        }
        Integer customerID=custID;
        CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findByConnectionNo(connectionNumber);
        if (customerServiceMapping != null) {
            QCustPlanMappping qCustPlanMappping=QCustPlanMappping.custPlanMappping;
            BooleanExpression booleanExpression=qCustPlanMappping.isNotNull().and(qCustPlanMappping.custServiceMappingId.eq(customerServiceMapping.getId())).and(qCustPlanMappping.custPlanStatus.eq("Active"));
            List<CustPlanMappping> custPlanMapppings= IterableUtils.toList(custPlanMappingRepository.findAll(booleanExpression));

            custPlanMapppings.stream().forEach(custPlanMappping->{
                String planType=custPlanMappping.getPurchaseType();
                if(!planType.equals(CommonConstants.PLAN_GROUP_DTV_ADDON)){
                    if (custPlanMappping != null) {
                        // CustPlanMappping custPlanMappping = planMapppingList.get(0);
                        PostpaidPlan postpaidPlans = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                        if (postpaidPlans != null) {
                            CustomerAddress customerAddress = customerAddressRepository.findByAddressTypeAndCustomerAndVersion("Present", customer, "NEW");
                            PlanService planService = planServiceRepository.findById(postpaidPlans.getServiceId()).orElse(null);
                            List<PlanCasMapping> planCasMappings = planCasMappingRepository.findAllByPlanId(custPlanMappping.getPlanId().longValue());
                            if (planCasMappings.size() > 0 && planService != null) {
                                if (planService.getIs_dtv() != null && planService.getIs_dtv()) {
                                    int packageIdCAS = planCasMappings.stream().filter(planCasMapping -> planCasMapping.getCasId().equals(casMaster.getId())).findFirst().get().getPackageId().intValue();
                                    if (packageIdCAS != 0) {
                                        Map<String, String> customerDetails = new HashMap<>();
                                        SaveCustomerInfoResponse saveCustomerInfoResponse = new SaveCustomerInfoResponse();
                                        SaveCustomerInfo saveCustomerInfo = new SaveCustomerInfo();
                                        if (customerID != null) {
                                            saveCustomerInfo.setCustomerId(customerID);
                                        }
                                        saveCustomerInfo.setFirstName(customer.getFirstname());
                                        saveCustomerInfo.setCountry("Nepal");
                                        saveCustomerInfo.setState("Bagmati");
                                        saveCustomerInfo.setDistrict("Kathmandu");
                                        saveCustomerInfo.setCity("1");
                                        saveCustomerInfo.setPin(123456);
                                        saveCustomerInfo.setAddress(customerAddress.getAddress1());
                                        saveCustomerInfo.setInstallationAddress(customerAddress.getAddress1());
                                        saveCustomerInfo.setBoxint(boxNumber);
                                        saveCustomerInfo.setAuthToken(authToken);
                                        saveCustomerInfo.setCafint(customer.getCafno());
                                        saveCustomerInfo.setLastName(customer.getLastname());
                                        saveCustomerInfo.setMobile(customer.getMobile());
                                        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                        saveCustomerInfo.setPackageEndDate(custPlanMappping.getExpiryDate().format(format));
                                        saveCustomerInfo.setPackageId(packageIdCAS);
                                        saveCustomerInfoResponse = ezBillService.createCustomer(saveCustomerInfo, casMaster.getEndpoint());
                                        if (saveCustomerInfoResponse.getStatusCode().equalsIgnoreCase("0")) {
                                            customerDetails = getDataFromString(saveCustomerInfoResponse.getStatusMessage());
                                            if (customerID == null) {
                                                customer.setEzyBillCustomersId(customerDetails.get(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.CUSTOMER_ID).trim());
                                                customer.setEzyBillAccountNumber(customerDetails.get(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.account_number).trim());
//                                        customer.setEzyBillStockId(customerDetails.get(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.stock_id).trim());
                                                customersRepository.save(customer);
                                                custPlanMappping.setCasId(String.valueOf(casMaster.getId()));
                                                custPlanMappping.setEzBillPackageId(String.valueOf(packageIdCAS));
                                                custPlanMappping.setEzyBillServiceId(customerDetails.get(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.customer_service_id).trim());
                                                customerInventoryMappingDto.setEzyBillStockId(Long.valueOf(customerDetails.get(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.stock_id).trim()));
                                            } else {
                                                CustomerPacakgesInfoResponse customerPacakgesInfoResponse = getCustomerPackageInfoResponse(authToken, customer, casMaster, boxNumber);
                                                List<CustomerpackageList> customerpackageList = customerPacakgesInfoResponse.getCustomerpackageList().stream().filter(item -> item.getPackageId() == packageIdCAS).collect(Collectors.toList());
                                                if (customerpackageList.size() > 0) {
//                                            if (customer.getParentCustomers() != null) {
                                                    customerDetails = getDataFromString(saveCustomerInfoResponse.getStatusMessage());
                                                    customer.setEzyBillCustomersId(customerDetails.get(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.CUSTOMER_ID).trim());
                                                    customer.setEzyBillAccountNumber(customerDetails.get(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.account_number).trim());
                                                    GetParticularStockRequest getParticularStockRequest = new GetParticularStockRequest();
                                                    getParticularStockRequest.setSerialint(boxNumber);
                                                    getParticularStockRequest.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                                                    GetParticularStockResponse getParticularStockResponse = ezBillService.getParticularStockRequest(getParticularStockRequest, casMaster.getEndpoint());
                                                    if (getParticularStockResponse.getStatusCode().equalsIgnoreCase("0")) {
//                                                    customer.setEzyBillStockId(String.valueOf(getParticularStockResponse.getStockId()));
                                                        customerInventoryMappingDto.setEzyBillStockId(Long.valueOf(getParticularStockResponse.getStockId()));
                                                    }
//                                                customer.setEzyBillStockId(customerDetails.get(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.stock_id).trim());
                                                    customersRepository.save(customer);
//                                            }
                                                    custPlanMappping.setCasId(String.valueOf(casMaster.getId()));
                                                    custPlanMappping.setEzBillPackageId(String.valueOf(packageIdCAS));
                                                    custPlanMappping.setEzyBillServiceId(String.valueOf(customerpackageList.get(0).getServiceId()));
//                                            custPlanMappingRepository.save(custPlanMapping);
                                                }
                                            }
                                        } else {
                                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), saveCustomerInfoResponse.getStatusMessage(), null);
                                        }

                                    } else {
                                        ApplicationLogger.logger.info("Plan is not mapped with available CAS.");
                                    }
                                } else {
                                    ApplicationLogger.logger.info("Service is not DTV service.");
                                }
                            } else {
                                ApplicationLogger.logger.info("Plan with this service not found");
                            }
                        } else {
                            ApplicationLogger.logger.info("plan not found");
                        }
                        custPlanMappingRepository.save(custPlanMappping);
                        CustomerInventoryMappingService customerInventoryMappingService = SpringContext.getBean(CustomerInventoryMappingService.class);
                        try {
                            customerInventoryMappingService.saveEntity(customerInventoryMappingDto);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                }else{
                    // For Add-On Plan
                    activateServiceForAddon(connectionNumber, customer, boxNumber, casMaster, customerInventoryMappingDto,custPlanMappping);
                }

            });


//            List<CustPlanMappping> planMapppingList = custPlanMappingRepository.findAllByCustServiceMappingId(customerServiceMapping.getId());

        }


    }

    public void activateServiceForAddon(String connectionNumber, Customers customers, String boxNumber, CasMaster casMaster, CustomerInventoryMappingDto customerInventoryMappingDto, CustPlanMappping custPlanMapping) {
        try {
            ActivateServiceResponse activateServiceResponse = new ActivateServiceResponse();
            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findByConnectionNo(connectionNumber);
            if (customerServiceMapping != null) {
                    ActivateService activateService = new ActivateService();
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapping.getPlanId()).orElse(null);
                    if (postpaidPlan != null) {
                        PlanService planService = planServiceRepository.findById(postpaidPlan.getServiceId()).orElse(null);
                        if (planService != null && planService.getIs_dtv()) {
                            if (customers.getEzyBillCustomersId() != null) {
                                List<PlanCasMapping> planCasMappings = postpaidPlan.getPlanCasMappingList().stream().filter(planCasMapping -> planCasMapping.getCasId().equals(casMaster.getId())).collect(Collectors.toList());

                                if (casMaster != null) {
                                    CustomerPacakgesInfoResponse customerPacakgesInfoResponse = getCustomerPackageInfoResponse(getAuthTokenFromCAS(casMaster, authToken), customers, casMaster, boxNumber);
                                    if (customerPacakgesInfoResponse.getStatusCode().equalsIgnoreCase("1") && customerPacakgesInfoResponse.getStatusMessage().contains("No records found")) {
                                        createCustomer(customers, getAuthTokenFromCAS(casMaster, authToken), boxNumber, casMaster, connectionNumber, Integer.valueOf(customers.getEzyBillCustomersId()), customerInventoryMappingDto);
                                    } else {
                                        if (planCasMappings.size() > 0) {
                                            activateService.setProductId(String.valueOf(planCasMappings.get(0).getPackageId()));
                                        }
                                        activateService.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                                        activateService.setCustomerId(Integer.parseInt(custPlanMapping.getCustomer().getEzyBillCustomersId()));
                                        activateService.setStockId(Math.toIntExact(customerInventoryMappingDto.getEzyBillStockId()));
                                        activateService.setValidityDays(custPlanMapping.getPlanValidityDays());
                                        activateService.setPackageEndDate(format.format(custPlanMapping.getEndDate()));
                                        activateService.setPackageStartDate(format.format(custPlanMapping.getStartDate()));
                                        activateServiceResponse = ezBillService.activateService(activateService, casMaster.getEndpoint());
                                        customerPacakgesInfoResponse = getCustomerPackageInfoResponse(getAuthTokenFromCAS(casMaster, authToken), customers, casMaster, boxNumber);
                                        if (activateServiceResponse.getStatusCode().equalsIgnoreCase("1") && !activateServiceResponse.getStatusMessage().contains("Service added successfully")) {
                                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), activateServiceResponse.getStatusMessage(), null);
                                        } else {
                                            List<CustomerpackageList> customerpackageList = customerPacakgesInfoResponse.getCustomerpackageList().stream().filter(item -> item.getPackageId() == planCasMappings.get(0).getPackageId().intValue()).collect(Collectors.toList());
                                            if (customerpackageList.size() > 0) {
                                                custPlanMapping.setCasId(String.valueOf(casMaster.getId()));
                                                custPlanMapping.setEzBillPackageId(String.valueOf(planCasMappings.get(0).getPackageId()));
                                                custPlanMapping.setEzyBillServiceId(String.valueOf(customerpackageList.get(0).getServiceId()));
                                                custPlanMappingRepository.save(custPlanMapping);
                                            }
                                        }
                                    }
                                } else {
                                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "CAS not found with this plan", null);
                                }
                            } else {
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer is not provisioned in EZ Bill.", null);
                            }
                        } else {
                            ApplicationLogger.logger.info("Plan doest not have DTV service");
                        }
                    }
            }
        } catch (Exception customValidationException) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), customValidationException.getMessage(), null);
        }
    }

    public DeactivateServiceResponse deactivateService(List<CustPlanMappping> custPlanMappings, int reasonID) {

        DeactivateService deactivateService = new DeactivateService();
        DeactivateServiceResponse deactivateServiceResponse = new DeactivateServiceResponse();
        deactivateService.setReasonId(reasonID);
        for (CustPlanMappping custPlanMappping : custPlanMappings) {
            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(custPlanMappping.getCustServiceMappingId()).orElse(null);
            List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(customerServiceMapping.getConnectionNo(), customerServiceMapping.getCustId());
            for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                if (!Objects.isNull(customerInventoryMapping.getProduct())) {
                    if (customerInventoryMapping.getProduct().getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
                        PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                        if (postpaidPlan != null) {
                            PlanService planService = planServiceRepository.findById(postpaidPlan.getServiceId()).orElse(null);
                            if (planService != null && planService.getIs_dtv()) {
                                if (custPlanMappping.getCustomer().getEzyBillCustomersId() != null && custPlanMappping.getEzyBillServiceId() != null) {
                                    deactivateService.setCustomerId(Integer.parseInt(custPlanMappping.getCustomer().getEzyBillCustomersId()));
                                    deactivateService.setServiceId(Integer.parseInt(custPlanMappping.getEzyBillServiceId()));
                                    deactivateService.setRemarks(custPlanMappping.getRemarks());
                                }
                                if (custPlanMappping.getCasId() != null) {
                                    CasMaster casMaster = casePackageRepository.findById(Long.valueOf(custPlanMappping.getCasId())).orElse(null);
                                    if (casMaster != null) {
                                        deactivateService.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                                        deactivateServiceResponse = ezBillService.deactivateService(deactivateService, casMaster.getEndpoint());
                                        if (deactivateServiceResponse.getStatusCode().equalsIgnoreCase("1")) {
                                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), deactivateServiceResponse.getStatusMessage(), null);
                                        }
                                    }
                                    //return deactivateServiceResponse;
                                }
                            } else {
                                ApplicationLogger.logger.info("Plan doest not have DTV service");
                            }
                        }
                    }
                }
            }
        }
        return deactivateServiceResponse;
    }


    public void getUnPairedInfoResponse(CasMaster casMaster, String serialNumber) {
        try {
            GetUnPairedInfo getUnPairedInfo = new GetUnPairedInfo();
            GetUnPairedInfoResponse getUnPairedInfoResponse = new GetUnPairedInfoResponse();
            getUnPairedInfo.setSerialint(serialNumber);
            getUnPairedInfo.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
            getUnPairedInfoResponse = ezBillService.unpairSTB(getUnPairedInfo, casMaster.getEndpoint());
            if (getUnPairedInfoResponse.getStatusCode().equalsIgnoreCase("1")) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), getUnPairedInfoResponse.getStatusMessage(), null);
            }
        } catch (CustomValidationException customValidationException) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), customValidationException.getMessage(), null);
        }

    }

    public Map<String, String> getDataFromString(String s) {
        Map<String, String> customerDetails = new HashMap<>();
        if (s.contains("@")) {
            String[] customerId = s.split("@");
            for (String value : customerId) {
                if (value.split(":")[0].contains(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.CUSTOMER_ID)) {
                    customerDetails.put(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.CUSTOMER_ID, value.split(":")[1]);
                } else if (value.split(":")[0].contains(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.account_number)) {
                    customerDetails.put(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.account_number, value.split(":")[1]);
                } else if (value.split(":")[0].contains(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.customer_service_id)) {
                    customerDetails.put(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.customer_service_id, value.split(":")[1]);
                } else if (value.split(":")[0].contains(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.stock_id)) {
                    customerDetails.put(CommonConstants.SAVE_CUSTOMER_RESPONSE_PARAMS_EZ.stock_id, value.split(":")[1]);
                }
            }
        }
        return customerDetails;
    }

    public void getPairedInfoResponse(String boxNumber, String cardNumber, String connectionNumber, Customers customers, CasMaster casMaster, CustomerInventoryMappingDto customerInventoryMappingDto) {
    try{
        if (cardNumber != null) {
            GetPairedInfoResponse getPairedInfoResponse = new GetPairedInfoResponse();
            GetPairedInfo getPairedInfo = new GetPairedInfo();
            getPairedInfo.setSerialint(boxNumber);
            getPairedInfo.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
            getPairedInfo.setVcint(cardNumber);
            getPairedInfoResponse = ezBillService.pairSTB(getPairedInfo, casMaster.getEndpoint());
            if (getPairedInfoResponse.getStatusCode().equalsIgnoreCase("1")) {
                getUnPairedInfoResponse(casMaster,boxNumber);
                ezBillService.pairSTB(getPairedInfo, casMaster.getEndpoint());
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), getPairedInfoResponse.getStatusMessage(), null);
            }
        }
        if (customers.getEzyBillCustomersId() == null) {
            createCustomer(customers, getAuthTokenFromCAS(casMaster, authToken), boxNumber, casMaster, connectionNumber, null, customerInventoryMappingDto);
        } else {
            activateService(connectionNumber, customers, boxNumber, casMaster, customerInventoryMappingDto);
        }
         } catch (CustomValidationException customValidationException) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), customValidationException.getMessage(), null);
        }
    }

    public void pairSTB(String boxNumber, String cardNumber, CasMaster casMaster, CustomerInventoryMappingDto customerInventoryMappingDto) {

        if (cardNumber != null) {
            GetPairedInfoResponse getPairedInfoResponse = new GetPairedInfoResponse();
            GetPairedInfo getPairedInfo = new GetPairedInfo();
            getPairedInfo.setSerialint(boxNumber);
            getPairedInfo.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
            getPairedInfo.setVcint(cardNumber);
            getPairedInfoResponse = ezBillService.pairSTB(getPairedInfo, casMaster.getEndpoint());
            if (getPairedInfoResponse.getStatusCode().equalsIgnoreCase("1")) {
                getUnPairedInfoResponse(casMaster,boxNumber);
                getPairedInfoResponse = ezBillService.pairSTB(getPairedInfo, casMaster.getEndpoint());
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), getPairedInfoResponse.getStatusMessage(), null);
            }

            // Get Package Information

            CustomerPacakgesInfoResponse customerPacakgesInfoResponse = getCustomerPackageInfoResponseByCustIDandBoxNumber(getAuthTokenFromCAS(casMaster, authToken), customerInventoryMappingDto.getCustomerId(), casMaster, boxNumber);

            List<CustPlanMappping> custPlanMapppingList = getCustPlanMapppingbyConnectionNo(customerInventoryMappingDto.getConnectionNo());

            if(!customerPacakgesInfoResponse.getStatusCode().equals("1")&&!customerPacakgesInfoResponse.getStatusMessage().equals("No records found"))
            custPlanMapppingList.stream().forEach(custPlanMapping -> {
                List<CustomerpackageList> customerpackageList = customerPacakgesInfoResponse.getCustomerpackageList().stream().filter(entity -> {
                    return entity.getPackageId() == Integer.parseInt(custPlanMapping.getEzBillPackageId());
                }).collect(Collectors.toList());

                if (customerpackageList.size()>0){
                    custPlanMapping.setEzyBillServiceId(String.valueOf(customerpackageList.get(0).getServiceId()));
                    custPlanMappingRepository.save(custPlanMapping);
                }

            });
        }
    }

    List<CustPlanMappping>  getCustPlanMapppingbyConnectionNo(String connectionNo){
        CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findByConnectionNo(connectionNo);
        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
        BooleanExpression exp1 = qCustPlanMappping.isNotNull().and(qCustPlanMappping.isDelete.eq(false).and(qCustPlanMappping.custServiceMappingId.eq(customerServiceMapping.getId()))).and(qCustPlanMappping.custPlanStatus.eq("Active"));
        return IterableUtils.toList(custPlanMappingRepository.findAll(exp1));
    }

    CustomerPacakgesInfoResponse getCustomerPackageInfoResponseByCustIDandBoxNumber(String authToken, Integer customerId, CasMaster casMaster, String boxNumber){

        Customers customer=customersRepository.getOne(customerId);
        CustomerPacakgesInfo customerPacakgesInfo = new CustomerPacakgesInfo();
        customerPacakgesInfo.setAuthToken(authToken);
        customerPacakgesInfo.setCustomerId(Integer.parseInt(customer.getEzyBillCustomersId()));
        customerPacakgesInfo.setBoxint(boxNumber);
        return ezBillService.customerPacakgesInfo(customerPacakgesInfo, casMaster.getEndpoint());

    }
//    public StbReplacementResponse stbReplacementRequest() {
//        StbReplacementRequest stbReplacementRequest = new StbReplacementRequest();
//        stbReplacementRequest.setReplacementReason(1);
//        stbReplacementRequest.setAuthToken("");
//        stbReplacementRequest.setNewSerialint("");
//        stbReplacementRequest.setOldSerialint("");
//        String endpointURL = "";
//        return ezBillService.stbReplacement(stbReplacementRequest, endpointURL);
//
//    }

    public ReplacementReasonInfoResponse replacementReasonInfoResponse() {
        ReplacementReasonInfo replacementReasonInfo = new ReplacementReasonInfo();
        String endpointURL = "";
        return ezBillService.replacementreason(replacementReasonInfo, endpointURL);
    }

    public void serviceExtensionResponse(CustPlanMappping custPlanMappping, LocalDateTime endDateTime) {

        PostpaidPlan postpaidPlans = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);

        if (postpaidPlans != null) {
            PlanService planService = planServiceRepository.findById(postpaidPlans.getServiceId()).orElse(null);
            if (planService != null && planService.getIs_dtv() && custPlanMappping.getCasId() != null) {
                CasMaster casMaster = casMasterRepository.findById(Long.valueOf(custPlanMappping.getCasId())).orElse(null);
                if (casMaster != null) {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(custPlanMappping.getCustServiceMappingId()).orElse(null);
                    List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(customerServiceMapping.getConnectionNo(), customerServiceMapping.getCustId());
                    for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                        if (customerInventoryMapping.getProduct().getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
                            ServiceExtensionRequest serviceExtensionRequest = new ServiceExtensionRequest();
                            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            serviceExtensionRequest.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                            serviceExtensionRequest.setProductId(Integer.parseInt(custPlanMappping.getEzBillPackageId()));
                            serviceExtensionRequest.setSerialint(itemRepository.findById(customerInventoryMapping.getItemId()).get().getSerialNumber());
                            serviceExtensionRequest.setExtendDate(format.format(endDateTime));
                            ServiceExtensionResponse serviceExtensionResponse = ezBillService.serviceExtension(serviceExtensionRequest, casMaster.getEndpoint());
                            if (serviceExtensionResponse.getStatusCode().equalsIgnoreCase("1")) {
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), serviceExtensionResponse.getStatusMessage(), null);
                            }
                        }
                    }
                }
            }
        }

//        ServiceExtensionRequest serviceExtensionRequest = new ServiceExtensionRequest();
//        serviceExtensionRequest.setProductId(2);
//        serviceExtensionRequest.setAuthToken("");
//        serviceExtensionRequest.setSerialint("");
//        serviceExtensionRequest.setExtendDate("");
//        String endpointURL = "";
//        return ezBillService.serviceExtension(serviceExtensionRequest, endpointURL);
    }

    public ReactivateBoxResponse reactivateBoxResponse(CasMaster casMaster, String boxNumber) {
        ReactivateBox reactivateBox = new ReactivateBox();
        reactivateBox.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
        reactivateBox.setSerialint(boxNumber);
        return ezBillService.reactivateBox(reactivateBox, casMaster.getEndpoint());

    }

    public void activatebyConnectionNumber(List<CustPlanMappping> custPlanMappings, String connectionNumber) {

        try {
            ActivateServiceResponse activateServiceResponse = new ActivateServiceResponse();

            for (CustPlanMappping custPlanMapping : custPlanMappings) {
                CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(custPlanMapping.getCustServiceMappingId()).orElse(null);
                Customers customers=customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(customerServiceMapping.getConnectionNo(), customerServiceMapping.getCustId());
                for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                    if (customerInventoryMapping.getProduct().getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
                        Item item=itemRepository.findById(customerInventoryMapping.getItemId()).orElse(null);
                        String boxNumber=item.getSerialNumber();
                        String stockId=customerInventoryMapping.getEzyBillStockId();
                        ActivateService activateService = new ActivateService();
                        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapping.getPlanId()).orElse(null);
                        if (postpaidPlan != null) {
                            PlanService planService = planServiceRepository.findById(postpaidPlan.getServiceId()).orElse(null);
                            if (planService != null && planService.getIs_dtv()) {
                                if (customers.getEzyBillCustomersId() != null) {
                                    Product product = customerInventoryMapping.getProduct();
                                    CasMaster casMaster = casMasterRepository.findById(product.getCaseId()).get();
                                    List<PlanCasMapping> planCasMappings = postpaidPlan.getPlanCasMappingList().stream().filter(planCasMapping -> planCasMapping.getCasId().equals(casMaster.getId())).collect(Collectors.toList());

                                    if (casMaster != null) {
                                        CustomerPacakgesInfoResponse customerPacakgesInfoResponse = getCustomerPackageInfoResponse(getAuthTokenFromCAS(casMaster, authToken), customers, casMaster, boxNumber);
                                        if (planCasMappings.size() > 0) {
                                            activateService.setProductId(String.valueOf(planCasMappings.get(0).getPackageId()));
                                        }
                                        activateService.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                                        activateService.setCustomerId(Integer.parseInt(custPlanMapping.getCustomer().getEzyBillCustomersId()));
                                        activateService.setStockId(Integer.parseInt(stockId));
                                        activateService.setValidityDays(custPlanMapping.getPlanValidityDays());
                                        activateService.setPackageEndDate(format.format(custPlanMapping.getEndDate()));
                                        activateService.setPackageStartDate(format.format(custPlanMapping.getStartDate()));
                                        activateServiceResponse = ezBillService.activateService(activateService, casMaster.getEndpoint());
                                        customerPacakgesInfoResponse = getCustomerPackageInfoResponse(getAuthTokenFromCAS(casMaster, authToken), customers, casMaster, boxNumber);
                                        if (activateServiceResponse.getStatusCode().equalsIgnoreCase("1") && !activateServiceResponse.getStatusMessage().contains("Service added successfully")) {
                                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), activateServiceResponse.getStatusMessage(), null);
                                        } else {
                                            List<CustomerpackageList> customerpackageList = customerPacakgesInfoResponse.getCustomerpackageList().stream().filter(entity -> entity.getPackageId() == planCasMappings.get(0).getPackageId().intValue()).collect(Collectors.toList());
                                            if (customerpackageList.size() > 0) {
                                                custPlanMapping.setEzyBillServiceId(String.valueOf(customerpackageList.get(0).getServiceId()));
                                                custPlanMapping.setEzBillPackageId(String.valueOf(customerpackageList.get(0).getPackageId()));
                                                custPlanMappingRepository.save(custPlanMapping);
                                            }
                                        }
                                    } else {
                                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "CAS not found with this plan", null);
                                    }
                                } else {
                                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer is not provisioned in EZ Bill.", null);
                                }
                            } else {
                                ApplicationLogger.logger.info("Plan doest not have DTV service");
                            }
                        }

                    }
                }
            }
        } catch (Exception customValidationException) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), customValidationException.getMessage(), null);
        }
    }

    public void activateService(String connectionNumber, Customers customers, String boxNumber, CasMaster casMaster, CustomerInventoryMappingDto customerInventoryMappingDto) {
        try {
            ActivateServiceResponse activateServiceResponse = new ActivateServiceResponse();
            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findByConnectionNo(connectionNumber);
            if (customerServiceMapping != null) {
                QCustPlanMappping qCustPlanMappping=QCustPlanMappping.custPlanMappping;
                BooleanExpression booleanExpression=qCustPlanMappping.isNotNull().and(qCustPlanMappping.custServiceMappingId.eq(customerServiceMapping.getId())).and(qCustPlanMappping.custPlanStatus.eq("Active"));
                List<CustPlanMappping> planMapppingList= IterableUtils.toList(custPlanMappingRepository.findAll(booleanExpression));
//                List<CustPlanMappping> planMapppingList = custPlanMappingRepository.findAllByCustServiceMappingId(customerServiceMapping.getId());
                if (planMapppingList.size() > 0) {
                    for (CustPlanMappping custPlanMapping : planMapppingList) {
                        ActivateService activateService = new ActivateService();
                        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapping.getPlanId()).orElse(null);
                        if (postpaidPlan != null) {
                            PlanService planService = planServiceRepository.findById(postpaidPlan.getServiceId()).orElse(null);
                            if (planService != null && planService.getIs_dtv()) {
                                if (customers.getEzyBillCustomersId() != null) {
                                    List<PlanCasMapping> planCasMappings = postpaidPlan.getPlanCasMappingList().stream().filter(planCasMapping -> planCasMapping.getCasId().equals(casMaster.getId())).collect(Collectors.toList());

                                    if (casMaster != null) {
                                        CustomerPacakgesInfoResponse customerPacakgesInfoResponse = getCustomerPackageInfoResponse(getAuthTokenFromCAS(casMaster, authToken), customers, casMaster, boxNumber);
                                        if (customers.getEzyBillCustomersId()==null && customerPacakgesInfoResponse.getStatusCode().equalsIgnoreCase("1") && customerPacakgesInfoResponse.getStatusMessage().contains("No records found")) {
                                            createCustomer(customers, getAuthTokenFromCAS(casMaster, authToken), boxNumber, casMaster, connectionNumber, Integer.valueOf(customers.getEzyBillCustomersId()), customerInventoryMappingDto);
                                        }else if (customerInventoryMappingDto.getEzyBillStockId()==null && customerPacakgesInfoResponse.getStatusCode().equalsIgnoreCase("1") && customerPacakgesInfoResponse.getStatusMessage().contains("No records found")) {
                                            createCustomer(customers, getAuthTokenFromCAS(casMaster, authToken), boxNumber, casMaster, connectionNumber, Integer.valueOf(customers.getEzyBillCustomersId()), customerInventoryMappingDto);
                                        }else {
                                            if (planCasMappings.size() > 0) {
                                                activateService.setProductId(String.valueOf(planCasMappings.get(0).getPackageId()));
                                            }
                                            activateService.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                                            activateService.setCustomerId(Integer.parseInt(custPlanMapping.getCustomer().getEzyBillCustomersId()));
                                            activateService.setStockId(Math.toIntExact(customerInventoryMappingDto.getEzyBillStockId()));
                                            activateService.setValidityDays(custPlanMapping.getPlanValidityDays());
                                            activateService.setPackageEndDate(format.format(custPlanMapping.getEndDate()));
                                            activateService.setPackageStartDate(format.format(custPlanMapping.getStartDate()));
                                            activateServiceResponse = ezBillService.activateService(activateService, casMaster.getEndpoint());
                                            customerPacakgesInfoResponse = getCustomerPackageInfoResponse(getAuthTokenFromCAS(casMaster, authToken), customers, casMaster, boxNumber);
                                            if (activateServiceResponse.getStatusCode().equalsIgnoreCase("1") && (activateServiceResponse.getStatusMessage().contains("Duplicate Products Exists in (Bundles or Products) which are in Active State")|| activateServiceResponse.getStatusMessage().contains("Duplicate Base package exist in the selected packages"))) {

                                                List<CustomerpackageList> customerpackageList = customerPacakgesInfoResponse.getCustomerpackageList().stream().filter(item -> item.getPackageId() == planCasMappings.get(0).getPackageId().intValue()).collect(Collectors.toList());
                                                if (customerpackageList.size() > 0) {
                                                    custPlanMapping.setEzyBillServiceId(String.valueOf(customerpackageList.get(0).getServiceId()));
                                                    custPlanMappingRepository.save(custPlanMapping);
                                                }

                                            }else if(activateServiceResponse.getStatusCode().equalsIgnoreCase("1") && !activateServiceResponse.getStatusMessage().contains("Service added successfully"))
                                            {
                                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), activateServiceResponse.getStatusMessage(), null);
                                            }
                                            else {
                                                List<CustomerpackageList> customerpackageList = customerPacakgesInfoResponse.getCustomerpackageList().stream().filter(item -> item.getPackageId() == planCasMappings.get(0).getPackageId().intValue()).collect(Collectors.toList());
                                                if (customerpackageList.size() > 0) {
                                                    custPlanMapping.setEzyBillServiceId(String.valueOf(customerpackageList.get(0).getServiceId()));
                                                    custPlanMappingRepository.save(custPlanMapping);
                                                }
                                            }
                                        }
                                    } else {
                                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "CAS not found with this plan", null);
                                    }
                                } else {
                                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer is not provisioned in EZ Bill.", null);
                                }
                            } else {
                                ApplicationLogger.logger.info("Plan doest not have DTV service");
                            }
                        }
                    }
                }
            }
        } catch (Exception customValidationException) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), customValidationException.getMessage(), null);
        }
    }

    CustomerPacakgesInfoResponse getCustomerPackageInfoResponse(String authToken, Customers customers, CasMaster casMaster, String boxNumber) {
        CustomerPacakgesInfo customerPacakgesInfo = new CustomerPacakgesInfo();
        customerPacakgesInfo.setAuthToken(authToken);
        customerPacakgesInfo.setCustomerId(Integer.parseInt(customers.getEzyBillCustomersId()));
        customerPacakgesInfo.setBoxint(boxNumber);
        return ezBillService.customerPacakgesInfo(customerPacakgesInfo, casMaster.getEndpoint());
    }

    public void extendExpiryDateInEZBill(CustPlanMappping custPlanMapping, LocalDateTime endDateTime) {
        PostpaidPlan postpaidPlans = postpaidPlanRepo.findById(custPlanMapping.getPlanId()).orElse(null);
        if (postpaidPlans != null) {
            PlanService planService = planServiceRepository.findById(postpaidPlans.getServiceId()).orElse(null);
            if (planService != null && planService.getIs_dtv() && custPlanMapping.getCasId() != null) {
                CasMaster casMaster = casMasterRepository.findById(Long.valueOf(custPlanMapping.getCasId())).orElse(null);
                if (casMaster != null) {
                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(custPlanMapping.getCustServiceMappingId()).orElse(null);
                    List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(customerServiceMapping.getConnectionNo(), customerServiceMapping.getCustId());
                    for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                        if (customerInventoryMapping.getProduct().getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
                            ActivateServiceResponse activateServiceResponse = new ActivateServiceResponse();
                            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            ActivateService activateService = new ActivateService();
                            activateService.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                            activateService.setCustomerId(Integer.parseInt(custPlanMapping.getCustomer().getEzyBillCustomersId()));
                            activateService.setProductId(String.valueOf(custPlanMapping.getEzBillPackageId()));
                            activateService.setStockId(Integer.parseInt(customerInventoryMapping.getEzyBillStockId()));
                            activateService.setPackageEndDate(format.format(endDateTime));
                            activateServiceResponse = ezBillService.activateService(activateService, casMaster.getEndpoint());
                            if (activateServiceResponse.getStatusCode().equalsIgnoreCase("1") && !activateServiceResponse.getStatusMessage().contains("Service added successfully")) {
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), activateServiceResponse.getStatusMessage(), null);
                            }
                            CustomerPacakgesInfoResponse customerPacakgesInfoResponse = getCustomerPackageInfoResponse(authToken, custPlanMapping.getCustomer(), casMaster, itemRepository.findById(customerInventoryMapping.getItemId()).get().getSerialNumber());
                            List<CustomerpackageList> customerpackageList = customerPacakgesInfoResponse.getCustomerpackageList().stream().filter(customerpackageList1 -> customerpackageList1.getPackageId() == Integer.parseInt(custPlanMapping.getEzBillPackageId())).collect(Collectors.toList());
                            custPlanMapping.setEzyBillServiceId(String.valueOf(customerpackageList.get(0).getServiceId()));
                            custPlanMappingRepository.save(custPlanMapping);
                        }
                    }
                }
            }
        }
    }

    public void renewPlanInEzBill(CustPlanMappping newCPR, Integer oldCPRId, String addOn) {
        CustPlanMappping oldCPM = custPlanMappingRepository.findById(oldCPRId);
            if ((oldCPM.getEndDate().isBefore(LocalDateTime.now()) && (newCPR.getStartDate().isBefore(LocalDateTime.now()) || newCPR.getStartDate().isEqual(LocalDateTime.now()))) || (!addOn.isEmpty() && addOn.equals("Addon"))) {
            if (oldCPM != null) {
                if (oldCPM.getCasId() != null) {
                    CasMaster casMaster = casMasterRepository.findById(Long.valueOf(oldCPM.getCasId())).orElse(null);
                    if (casMaster != null) {
                        PostpaidPlan postpaidPlans = postpaidPlanRepo.findById(newCPR.getPlanId()).orElse(null);
                        if (postpaidPlans != null) {
                            List<PlanCasMapping> planCasMappings = postpaidPlans.getPlanCasMappingList().stream().filter(planCasMapping -> planCasMapping.getCasId().equals(casMaster.getId())).collect(Collectors.toList());
                            if (planCasMappings.size() > 0) {
                                PlanService planService = planServiceRepository.findById(postpaidPlans.getServiceId()).orElse(null);
                                if (planService != null && planService.getIs_dtv()) {
                                    CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(newCPR.getCustServiceMappingId()).orElse(null);
                                    List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(customerServiceMapping.getConnectionNo(), customerServiceMapping.getCustId());
                                    for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                                        if (customerInventoryMapping.getProduct().getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
                                            ActivateServiceResponse activateServiceResponse = new ActivateServiceResponse();
                                            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                            ActivateService activateService = new ActivateService();
                                            activateService.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                                            activateService.setCustomerId(Integer.parseInt(newCPR.getCustomer().getEzyBillCustomersId()));
                                            activateService.setProductId(String.valueOf(planCasMappings.get(0).getPackageId()));
                                            activateService.setStockId(Integer.parseInt(customerInventoryMapping.getEzyBillStockId()));
                                            activateService.setPackageEndDate(format.format(newCPR.getEndDate()));
                                            activateServiceResponse = ezBillService.activateService(activateService, casMaster.getEndpoint());
                                            if (activateServiceResponse.getStatusCode().equalsIgnoreCase("1") && !activateServiceResponse.getStatusMessage().contains("Service added successfully")) {
                                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), activateServiceResponse.getStatusMessage(), null);
                                            }
                                            Item item= itemRepository.findById(customerInventoryMapping.getItemId()).orElse(null);
                                            CustomerPacakgesInfoResponse customerPacakgesInfoResponse = getCustomerPackageInfoResponse(authToken, newCPR.getCustomer(), casMaster, item.getSerialNumber());
                                            List<CustomerpackageList> customerpackageList = customerPacakgesInfoResponse.getCustomerpackageList().stream().filter(entity -> entity.getPackageId() == planCasMappings.get(0).getPackageId().intValue()).collect(Collectors.toList());
                                            if (customerpackageList.size()>0){
                                                newCPR.setEzBillPackageId(String.valueOf(customerpackageList.get(0).getPackageId()));
                                                newCPR.setEzyBillServiceId(String.valueOf(customerpackageList.get(0).getServiceId()));
                                                newCPR.setCasId(oldCPM.getCasId());
                                                custPlanMappingRepository.save(newCPR);
                                            }

                                        }
//                                        else {
//                                            throw new RuntimeException("No STB Assigned to renew.Please assign STB for using this service.");
//                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (!addOn.equals("Addon")) {
                    PostpaidPlan postpaidPlans = postpaidPlanRepo.findById(newCPR.getPlanId()).orElse(null);
                    if (postpaidPlans != null) {
                            PlanService planService = planServiceRepository.findById(postpaidPlans.getServiceId()).orElse(null);
                            if (planService != null && planService.getIs_dtv()) {
                                CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(newCPR.getCustServiceMappingId()).orElse(null);
                                List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(customerServiceMapping.getConnectionNo(), customerServiceMapping.getCustId());
                                for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                                    if (customerInventoryMapping.getProduct().getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
                                        CasMaster casMaster= casMasterRepository.findById(Long.valueOf(customerInventoryMapping.getProduct().getCaseId())).orElse(null);
                                        List<PlanCasMapping> planCasMappings = postpaidPlans.getPlanCasMappingList().stream().filter(planCasMapping -> planCasMapping.getCasId().equals(casMaster.getId())).collect(Collectors.toList());
                                        ActivateServiceResponse activateServiceResponse = new ActivateServiceResponse();
                                        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                        ActivateService activateService = new ActivateService();
                                        activateService.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                                        activateService.setCustomerId(Integer.parseInt(newCPR.getCustomer().getEzyBillCustomersId()));
                                        activateService.setProductId(String.valueOf(planCasMappings.get(0).getPackageId()));
                                        activateService.setStockId(Integer.parseInt(customerInventoryMapping.getEzyBillStockId()));
                                        activateService.setPackageEndDate(format.format(newCPR.getEndDate()));
                                        activateServiceResponse = ezBillService.activateService(activateService, casMaster.getEndpoint());
                                        if (activateServiceResponse.getStatusCode().equalsIgnoreCase("1") && !activateServiceResponse.getStatusMessage().contains("Service added successfully")) {
                                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), activateServiceResponse.getStatusMessage(), null);
                                        }
                                        Item item= itemRepository.findById(customerInventoryMapping.getItemId()).orElse(null);
                                        CustomerPacakgesInfoResponse customerPacakgesInfoResponse = getCustomerPackageInfoResponse(authToken, newCPR.getCustomer(), casMaster, item.getSerialNumber());
                                        List<CustomerpackageList> customerpackageList = customerPacakgesInfoResponse.getCustomerpackageList().stream().filter(entity -> entity.getPackageId() == planCasMappings.get(0).getPackageId().intValue()).collect(Collectors.toList());
                                        if (customerpackageList.size()>0){
                                            newCPR.setEzBillPackageId(String.valueOf(customerpackageList.get(0).getPackageId()));
                                            newCPR.setEzyBillServiceId(String.valueOf(customerpackageList.get(0).getServiceId()));
                                            newCPR.setCasId(String.valueOf(casMaster.getId()));
                                            custPlanMappingRepository.save(newCPR);
                                        }

                                    }
//                                        else {
//                                            throw new RuntimeException("No STB Assigned to renew.Please assign STB for using this service.");
//                                        }
                                }
                            }

                    }
        }

    }

    public void changePlan(CustPlanMappping oldCPR, CustPlanMappping newCPR) {
        deactivateService(Arrays.asList(oldCPR), 13);
        Item item = null;

        //Ignore Add-On Plan
        //if (newCPR != null && !oldCPR.getPurchaseType().equals(CommonConstants.PLAN_GROUP_DTV_ADDON) ) {
            if (oldCPR.getCasId() != null) {
                CasMaster casMaster = casMasterRepository.findById(Long.valueOf(oldCPR.getCasId())).orElse(null);
                if (casMaster != null) {
                    PostpaidPlan postpaidPlans = postpaidPlanRepo.findById(newCPR.getPlanId()).orElse(null);
                    if (postpaidPlans != null) {
                        List<PlanCasMapping> planCasMappings = postpaidPlans.getPlanCasMappingList().stream().filter(planCasMapping -> planCasMapping.getCasId().equals(casMaster.getId())).collect(Collectors.toList());
                        if (planCasMappings.size() > 0) {
                            PlanService planService = planServiceRepository.findById(postpaidPlans.getServiceId()).orElse(null);
                            if (planService != null && planService.getIs_dtv()) {

                                CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(newCPR.getCustServiceMappingId()).orElse(null);
                                List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(customerServiceMapping.getConnectionNo(), customerServiceMapping.getCustId());
                                for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                                    if (customerInventoryMapping.getProduct().getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
                                        item = itemRepository.findById(customerInventoryMapping.getItemId()).orElse(null);
                                        ActivateServiceResponse activateServiceResponse = new ActivateServiceResponse();
                                        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                        ActivateService activateService = new ActivateService();
                                        activateService.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                                        activateService.setCustomerId(Integer.parseInt(newCPR.getCustomer().getEzyBillCustomersId()));
                                        activateService.setProductId(String.valueOf(planCasMappings.get(0).getPackageId()));
                                        activateService.setStockId(Integer.parseInt(customerInventoryMapping.getEzyBillStockId()));
                                        activateService.setPackageEndDate(format.format(newCPR.getEndDate()));
                                        activateServiceResponse = ezBillService.activateService(activateService, casMaster.getEndpoint());
                                        if (activateServiceResponse.getStatusCode().equalsIgnoreCase("1") && !activateServiceResponse.getStatusMessage().contains("Service added successfully")) {
                                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), activateServiceResponse.getStatusMessage(), null);
                                        }

                                        CustomerPacakgesInfoResponse customerPacakgesInfoResponse = getCustomerPackageInfoResponse(authToken, newCPR.getCustomer(), casMaster, item.getSerialNumber());
                                        List<CustomerpackageList> customerpackageList = customerPacakgesInfoResponse.getCustomerpackageList().stream().filter(customerpackageList1 -> customerpackageList1.getPackageId() == planCasMappings.get(0).getPackageId()).collect(Collectors.toList());
                                        newCPR.setEzBillPackageId(String.valueOf(planCasMappings.get(0).getPackageId()));
                                        newCPR.setEzyBillServiceId(String.valueOf(customerpackageList.get(0).getServiceId()));
                                        newCPR.setCasId(oldCPR.getCasId());
                                        custPlanMappingRepository.save(newCPR);
                                    }
                                }

                            }
                        }
                    }
                }
            }
//        }


    }

    public void startService(CustPlanMappping custPlanMappping) {
        if (custPlanMappping != null) {
            CasMaster casMaster = casMasterRepository.findById(Long.valueOf(custPlanMappping.getCasId())).orElse(null);
            if (casMaster != null) {
                PostpaidPlan postpaidPlans = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                if (postpaidPlans != null) {
                    List<PlanCasMapping> planCasMappings = postpaidPlans.getPlanCasMappingList().stream().filter(planCasMapping -> planCasMapping.getCasId().equals(casMaster.getId())).collect(Collectors.toList());
                    if (planCasMappings.size() > 0) {
                        PlanService planService = planServiceRepository.findById(postpaidPlans.getServiceId()).orElse(null);
                        if (planService != null && planService.getIs_dtv()) {
                            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(custPlanMappping.getCustServiceMappingId()).orElse(null);
                            List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(customerServiceMapping.getConnectionNo(), customerServiceMapping.getCustId());
                            for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                                if (customerInventoryMapping.getProduct().getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
                                    ActivateServiceResponse activateServiceResponse = new ActivateServiceResponse();
                                    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    ActivateService activateService = new ActivateService();
                                    activateService.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                                    activateService.setCustomerId(Integer.parseInt(custPlanMappping.getCustomer().getEzyBillCustomersId()));
                                    activateService.setProductId(String.valueOf(planCasMappings.get(0).getPackageId()));
                                    activateService.setStockId(Integer.parseInt(customerInventoryMapping.getEzyBillStockId()));
                                    activateService.setPackageEndDate(format.format(custPlanMappping.getEndDate()));
                                    activateServiceResponse = ezBillService.activateService(activateService, casMaster.getEndpoint());
                                    if (activateServiceResponse.getStatusCode().equalsIgnoreCase("1") && !activateServiceResponse.getStatusMessage().contains("Service added successfully")) {
                                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), activateServiceResponse.getStatusMessage(), null);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    public void replaceSetupBox(CasMaster casMaster, String newSerialNumber, String oldSerialNumber, int replacementReasonId, String connectionNumber, CustomerInventoryMapping customerInventoryMapping) {
        try {
            CustPlanMappingService custPlanMappingService = SpringContext.getBean(CustPlanMappingService.class);
            PostpaidPlanService postpaidPlanService = SpringContext.getBean(PostpaidPlanService.class);
            PlanServiceService planServiceService = SpringContext.getBean(PlanServiceService.class);
            List<CustPlanMappping> custPlanMappings = custPlanMappingService.getAllWithConnectionNumberAndForDTVService(connectionNumber);
            for (CustPlanMappping custPlanMappping : custPlanMappings) {
                //Need to remove this as we already fetch custplan mapping which is not in STOP status.
                if (!custPlanMappping.getCustPlanStatus().equalsIgnoreCase("STOP")) {
                    StbReplacementRequest stbReplacementRequest = new StbReplacementRequest();
                    StbReplacementResponse stbReplacementResponse = new StbReplacementResponse();
                    stbReplacementRequest.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                    stbReplacementRequest.setNewSerialint(newSerialNumber);
                    stbReplacementRequest.setOldSerialint(oldSerialNumber);
                    stbReplacementRequest.setReplacementReason(replacementReasonId);
                    stbReplacementResponse = ezBillService.stbReplacement(stbReplacementRequest, casMaster.getEndpoint());
                    if (stbReplacementResponse.getStatusCode().equalsIgnoreCase("1")) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), stbReplacementResponse.getStatusMessage(), null);
                    } else if (stbReplacementResponse.getStatusCode().equalsIgnoreCase("0")) {
                        {
                            if (custPlanMappping.getPlanId() != null) {
                                PostpaidPlan postpaidPlans = postpaidPlanService.findById(custPlanMappping.getPlanId());
                                if (postpaidPlans != null) {
                                    PlanService planService = planServiceRepository.findById(postpaidPlans.getServiceId()).get();
                                    if (planService != null && planService.getIs_dtv()) {
                                        for (ServicePackageList servicePackageList : stbReplacementResponse.getServicePackageList()) {
                                            if (servicePackageList.getPackageId() == Integer.parseInt(custPlanMappping.getEzBillPackageId())) {
                                                custPlanMappping.setEzyBillServiceId(String.valueOf(servicePackageList.getServiceId()));
                                                custPlanMappingService.save(custPlanMappping);
                                                customerInventoryMapping.setEzyBillStockId(stbReplacementResponse.getStockId());
                                                CustomerInventoryMappingService customerInventoryMappingService = SpringContext.getBean(CustomerInventoryMappingService.class);
                                                customerInventoryMappingService.saveEntity(customerInventoryMappingService.getMapper().domainToDTO(customerInventoryMapping, new CycleAvoidingMappingContext()));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Renewal can not  be done as connection is not active .", null);
                }
            }
        } catch (CustomValidationException customValidationException) {
            StbReplacementRequest stbReplacementRequest = new StbReplacementRequest();
            stbReplacementRequest.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
            stbReplacementRequest.setOldSerialint(newSerialNumber);
            stbReplacementRequest.setReplacementReason(4);
            ezBillService.stbReplacement(stbReplacementRequest, casMaster.getEndpoint());
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), customValidationException.getMessage(), null);
        } catch (RuntimeException e) {
            StbReplacementRequest stbReplacementRequest = new StbReplacementRequest();
            stbReplacementRequest.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
            stbReplacementRequest.setOldSerialint(newSerialNumber);
            stbReplacementRequest.setReplacementReason(4);
            ezBillService.stbReplacement(stbReplacementRequest, casMaster.getEndpoint());
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        } catch (Exception e) {
            StbReplacementRequest stbReplacementRequest = new StbReplacementRequest();
            stbReplacementRequest.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
            stbReplacementRequest.setOldSerialint(newSerialNumber);
            stbReplacementRequest.setReplacementReason(4);
            ezBillService.stbReplacement(stbReplacementRequest, casMaster.getEndpoint());
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }

    }


    public void getPairedInfo(CasMaster casMaster, String serialNumber, String vcNumber) {
        try {
            GetPairedInfo getPairedInfo = new GetPairedInfo();
            GetPairedInfoResponse getPairedInfoResponse = new GetPairedInfoResponse();
            getPairedInfo.setVcint(vcNumber);
            getPairedInfo.setSerialint(serialNumber);
            getPairedInfo.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
            getPairedInfoResponse = ezBillService.pairSTB(getPairedInfo, casMaster.getEndpoint());
            if (getPairedInfoResponse.getStatusCode().equalsIgnoreCase("1")) {
                getUnPairedInfoResponse(casMaster,serialNumber);
                ezBillService.pairSTB(getPairedInfo, casMaster.getEndpoint());
                //throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), getPairedInfoResponse.getStatusMessage(), null);
            }
        } catch (CustomValidationException customValidationException) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), customValidationException.getMessage(), null);
        }

    }

    public void manuallyActivate(CustPlanMappping custPlanMapping) {
        try {
            if (custPlanMapping.getEzyBillServiceId() != null) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                ActivateService activateService = new ActivateService();
                ActivateServiceResponse activateServiceResponse = new ActivateServiceResponse();
                CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(custPlanMapping.getCustServiceMappingId()).orElse(null);
                if (customerServiceMapping != null) {
                    List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerId(customerServiceMapping.getConnectionNo(), customerServiceMapping.getCustId());
                    for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                        if (customerInventoryMapping.getProduct().getProductCategory().getDtvCategory().equalsIgnoreCase("STB") && customerInventoryMapping.getConnectionNo().equalsIgnoreCase(customerServiceMapping.getConnectionNo())) {
                            CasMaster casMaster = casMasterRepository.findById(Long.valueOf(custPlanMapping.getCasId())).orElse(null);
                            Item item = itemRepository.findById(customerInventoryMapping.getItemId()).orElse(null);
                            activateService.setProductId(custPlanMapping.getEzBillPackageId());
                            activateService.setAuthToken(getAuthTokenFromCAS(casMaster, authToken));
                            activateService.setCustomerId(Integer.parseInt(custPlanMapping.getCustomer().getEzyBillCustomersId()));
                            activateService.setStockId(Integer.parseInt(customerInventoryMapping.getEzyBillStockId()));
                            activateService.setValidityDays(custPlanMapping.getPlanValidityDays());
                            activateService.setPackageEndDate(format.format(custPlanMapping.getEndDate()));
                            activateService.setPackageStartDate(format.format(custPlanMapping.getStartDate()));
                            activateServiceResponse = ezBillService.activateService(activateService, casMaster.getEndpoint());
                            if (activateServiceResponse.getStatusCode().equalsIgnoreCase("1")) {
                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), activateServiceResponse.getStatusMessage(), null);
                            }
                            CustomerPacakgesInfoResponse customerPacakgesInfoResponse = getCustomerPackageInfoResponse(authToken, custPlanMapping.getCustomer(), casMaster, item.getSerialNumber());
                            List<CustomerpackageList> customerpackageList = customerPacakgesInfoResponse.getCustomerpackageList().stream().filter(customerpackageList1 -> customerpackageList1.getPackageId() == Integer.parseInt(custPlanMapping.getEzBillPackageId())).collect(Collectors.toList());
                            custPlanMapping.setEzyBillServiceId(String.valueOf(customerpackageList.get(0).getServiceId()));
                            custPlanMappingRepository.save(custPlanMapping);
                        }
                    }
                }
            }
        } catch (CustomValidationException customValidationException) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), customValidationException.getMessage(), null);
        }

    }
}
