package com.adopt.apigw.service.common;


import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.Area.repository.AreaRepository;
import com.adopt.apigw.modules.Branch.model.BranchDTO;
import com.adopt.apigw.modules.Branch.service.BranchService;
import com.adopt.apigw.modules.Customers.CaptiveCustomerResponseDTO;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.custAccountProfile.CustAccountProfile;
import com.adopt.apigw.modules.custAccountProfile.CustAccountProfileRepository;
import com.adopt.apigw.modules.subscriber.model.*;
import com.adopt.apigw.rabbitMq.message.BudpayChangePlanMessage;
import com.adopt.apigw.rabbitMq.message.CustPackageRelMessage;
import com.adopt.apigw.repository.common.BranchServiceAreaMappingRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.CityRepository;
import com.adopt.apigw.repository.postpaid.CustomerAddressRepository;
import com.adopt.apigw.service.postpaid.PartnerService;

import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.google.gson.Gson;
import org.slf4j.LoggerFactory;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.LocationMaster.domain.CustomerLocationMapping;
import com.adopt.apigw.modules.LocationMaster.domain.LocationMaster;
import com.adopt.apigw.modules.LocationMaster.domain.LocationMasterMapping;
import com.adopt.apigw.modules.LocationMaster.repository.CustomerLocationRepository;
import com.adopt.apigw.modules.LocationMaster.repository.LocationMasterMappingRepository;
import com.adopt.apigw.modules.LocationMaster.repository.LocationMasterRepository;
import com.adopt.apigw.modules.Voucher.domain.Voucher;
import com.adopt.apigw.modules.Voucher.module.ValidateCrudTransactionData;
import com.adopt.apigw.modules.Voucher.module.VoucherStatus;
import com.adopt.apigw.modules.Voucher.repository.VoucherRepository;
import com.adopt.apigw.modules.Voucher.service.VoucherService;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.CustomMessage;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.postpaid.CustPlanMappingService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.utils.CommonConstants;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.adopt.apigw.modules.Reseller.mapper.WifiUtils.validateEmailAddress;

@Service
public class CaptivePortalCustomerService {

    private static final Logger log = LoggerFactory.getLogger(CaptivePortalCustomerService.class);

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private LocationMasterRepository locationMasterRepository;

    @Autowired
    private CustomerLocationRepository customerLocationRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private CustPlanMappingService custPlanMappingService;

    @Autowired
    private LocationMasterMappingRepository locationMasterMappingRepository;

    @Autowired
    private CustomersRepository customersRepository;


    @Autowired
    private CustomerLocationMapper customerLocationMapper;

    @Autowired
    private ClientServiceRepository clientServiceRepository;

    @Autowired
    private BranchService branchService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;
    @Autowired
    private ServiceAreaRepository serviceAreaRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;

    @Autowired
    private HierarchyService hierarchyService;
    @Autowired
    private MvnoRepository mvnoRepository;
    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    private CustAccountProfileRepository custAccountProfileRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private AcctNumCreationService acctNumCreationService;

    //    @Transactional
    public Customers saveCustomer(CustomersPojo customerDto, Integer mvnoId) throws Exception {
        validateEmailAddress(customerDto.getEmail());
        customerDto.setMvnoId(mvnoId);
        try {
            PostpaidPlan plan = new PostpaidPlan();
            Voucher voucher = new Voucher();
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerDto.getPlanName())) {
                customerDto.setVoucherCode(null);
                plan = postpaidPlanService.getPlanByName(customerDto.getPlanName(), mvnoId);
            } else if (ValidateCrudTransactionData.validateStringTypeFieldValue(customerDto.getVoucherCode())) {
                customerDto.setPlanName(null);
                voucher = voucherService.getVoucher(customerDto.getVoucherCode(), mvnoId);
                voucher.setStatus(VoucherStatus.USED);
                voucherRepository.save(voucher);
                if (voucher.getVoucherBatch() != null && voucher.getVoucherBatch().getPlan() != null) {
                    plan = voucher.getVoucherBatch().getPlan();
                }
            }
            customerDto.setServiceareaid(plan.getServiceAreaNameList().get(0).getId());
            customerDto.setCreatedByName("Admin Admin");
            customerDto.setCreatedById(2);
            customerDto.setLast_password_change(LocalDateTime.now());
//            String encryptPassword =  passwordGenerator.encryptPassword(customerDto.getPassword());
            customerDto.setPassword(customerDto.getPassword());
            customerDto.setContactperson("Admin Admin");
            customerDto.setFirstname(customerDto.getUsername());
            customerDto.setLastname(customerDto.getUsername());
            customerDto.setCustname(customerDto.getUsername());
            customerDto.setIsDeleted(false);
            customerDto.setPartnerid(1);
            customerDto.setTitle("Mr");
            CustPlanMapppingPojo customerPlanDetail = saveCustomerPlanDetail(plan, voucher, customerDto, false, customerDto.getValidUpto(), customerDto.getValidFrom());
            customerDto.setPlanMappingList(Collections.singletonList(customerPlanDetail));
            customerDto.setCusttype(CommonConstants.CUST_TYPE_PREPAID);
            customerDto.setCalendarType(CommonConstants.CAL_TYPE_ENGLISH);
            customerDto.setCustlabel("customer");
            Customers customer = customerMapper.dtoToDomain(customerDto, new CycleAvoidingMappingContext());
//            customer.setNextBillDate(customersService.getNextBillDate(customer));
            customerDto = customersService.saveSubscriber(customerDto, "pw", false);
            customerDto.setId(customerDto.getId());
            if(customerDto != null) {
                customersService.sharedCustomerData(customerDto,false);
                CustomMessage customMessage = new CustomMessage(customerDto);
                //messageSender.send(customMessage, RabbitMqConstants.QUEUE_APIGW_CUSTOMER);
                kafkaMessageSender.send(new KafkaMessageData(customMessage,customMessage.getClass().getSimpleName(),"CUSTOMER_CREATE"));
//                messageSender.send(customMessage, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_NOTIFICATION);
                custPlanMappingService.sendCustPlanMapping(customerDto.getId());
            }
            List<CustomerLocationMapping> customerLocationMappings = new ArrayList<>();
            if(!CollectionUtils.isEmpty(customerDto.getLocations())) {
                customerLocationMappings = saveCustomerLocationMapping(customer, customerDto, true);
                List<CustomerLocationMappingDto> locationMappingDtos = customerLocationMapper.domainToDTO(customerLocationMappings, new CycleAvoidingMappingContext());
                customerDto.setCustomerLocations(locationMappingDtos);
            }
            log.info("Customer has been created successfully: " + customer.getUsername());
            return customer;
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<CustomerLocationMapping> saveCustomerLocationMapping(Customers customer, CustomersPojo customerDto, boolean isGroovyData) {
        List<CustomerLocationMapping> customerLocationMappings = new ArrayList<>();
        if(!CollectionUtils.isEmpty(customerDto.getLocations())) {
            customerDto.getLocations().forEach(l -> {
                Optional<LocationMaster> locationMaster = locationMasterRepository.findById(l);
                if(locationMaster.isPresent()) {
                    CustomerLocationMapping customerLocationMapping = new CustomerLocationMapping();
                    customerLocationMapping.setCustId(Long.valueOf(customer.getId()));
                    customerLocationMapping.setLocationId(locationMaster.get().getLocationMasterId());
                    customerLocationMapping.setLocationName(locationMaster.get().getName());
                    customerLocationMappings.add(customerLocationMapping);
                }
            });
        }
        if(!CollectionUtils.isEmpty(customerLocationMappings)) {
            if(isGroovyData)
                removeOldCustomerLocationMapping(Long.valueOf(customer.getId()));
            new HashSet<>(customerLocationRepository.saveAll(customerLocationMappings));
        }
        return customerLocationRepository.findByCustId(Long.valueOf(customer.getId()));
    }

    private void removeOldCustomerLocationMapping(Long custId) {
        List<CustomerLocationMapping> list = customerLocationRepository.findByCustId(custId);
        if(!CollectionUtils.isEmpty(list))
            customerLocationRepository.deleteInBatch(list);
    }

    private CustPlanMapppingPojo saveCustomerPlanDetail(PostpaidPlan plan, Voucher voucher, CustomersPojo customer, Boolean isRenew, String validUpto, String validFrom) {
        try {
//            QOSPolicy qosPolicy = plan.getQospolicy();

            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime expiryDate = subscriberService.calculateExpiryDate(customer, plan, startDate);
            Double taxAmount = 0.0;
            LocalDateTime endDate = null;
            Integer planValidityDays = 0;

            Services service = serviceRepository.findById(plan.getServiceId().longValue()).get();
            CustPlanMapppingPojo customerPlanDetailVo = new CustPlanMapppingPojo(plan.getId(), customer.getId(), customer,
                    startDate, expiryDate,null, plan.getUploadQOS(), plan.getDownloadQOS(), plan.getUploadTs(), plan.getDownloadTs()
                    , service.getServiceName(), plan.getOfferprice(), taxAmount, endDate, plan.getValidity(), planValidityDays, null, null, customer.getUsername());//(customer.getCustomerId(), qosPolicyName, voucher, plan);
            if(ValidateCrudTransactionData.validateStringTypeFieldValue(validUpto)){
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = sdf.parse(validUpto);
                LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                customerPlanDetailVo.setEndDate(dateTime);
            } else {
                customerPlanDetailVo.setEndDate(LocalDateTime.now().plusDays(plan.getValidity().longValue()));
            }
            customerPlanDetailVo.setCreatedate(LocalDateTime.now());
            customerPlanDetailVo.setCreatedByName("Admin Admin");
            customerPlanDetailVo.setDiscount(0.0000);
            customerPlanDetailVo.setDiscountType("One-time");
            return customerPlanDetailVo;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public CustAccountProfile fetchSubscriberAccountProfileDetail(Long profileId){
        CustAccountProfile custAccountProfile = custAccountProfileRepository.findByProfileId(profileId).orElse(null);
        return custAccountProfile;
    }

    @Transactional
    public Customers createCustomerFrom(String mobileno, String mac, String email, String countryCode,String data , Customers parentCustomers , CustomerLocationMapping customerLocationMapping) throws Exception{
        Customers customers = new Customers();
        customers.setUsername(data);
        customers.setPassword(mobileno.length() > 0 ? mobileno : data);
        customers.setIsDeleted(false);
        customers.setCustname(data);
        customers.setBillableCustomerId(null);
        customers.setBillday(null);
        customers.setPlanMappingList(new ArrayList<>());
        customers.setCustMacMapppingList(new ArrayList<>());
        customers.setFramedIp("");
        customers.setFramedIpBind("");
        customers.setInvoiceType("");
        customers.setIpPoolNameBind("");
        customers.setIstrialplan(false);
        Long profileId = mvnoRepository.findProfileIdByMvnoId(Long.valueOf(parentCustomers.getMvnoId())).orElse(null);
        if(profileId!=null) {
            CustAccountProfile custAccountProfile = fetchSubscriberAccountProfileDetail(profileId);
            if (customers.getAcctno() == null || customers.getAcctno().trim().isEmpty()) {
                customers.setAcctno(acctNumCreationService.getNewCustomerAccountNo(custAccountProfile, parentCustomers.getMvnoId()));
            }
        }
        customers.setLatitude("");
        customers.setLongitude("");
//        customers.setLocations(null);
        customers.setMasterdbid(null);
        customers.setMasterdbid(null);
        customers.setNasPort("");
        customers.setOverChargeList(new ArrayList<>());
        customers.setParentExperience("Actual");
//        customers.setAcctno(CommonUtils.getNewCustomerAccountNo());
//        customers.setPlangroupid(null);
        customers.setSplitterid(null);
        customers.setStaffId(null);
        customers.setVoicesrvtype("");
        customers.setFirstname("CF");
        customers.setLastname("CF");
        customers.setEmail(email.length() > 0 ? email : "CF@gmail.com");
        customers.setTitle("Mr");
        customers.setPan("");
        customers.setGst("");
        customers.setAadhar("");
        customers.setPassportNo("");
        customers.setTinNo("");
        customers.setContactperson(data);
        customers.setFailcount(0);
        customers.setCusttype(parentCustomers.getCusttype());
        customers.setCustlabel("organization");
        customers.setPhone("");
        customers.setMobile(mobileno.length() > 0 ? mobileno : "9898989898");
        customers.setAltemail("");
        customers.setFax("");
        customers.setCountryCode(countryCode.length() > 0 ? countryCode : "+91");
        customers.setCustomerType("");
        customers.setCustomerSubType("");
        customers.setCustomerSubSector("");
        customers.setCustomerSector("");
        customers.setCafno("");
        customers.setVoicesrvtype("");
        customers.setDidno("");
        customers.setCalendarType("");
        if(isServiceAreaPartnerBind(Math.toIntExact(parentCustomers.getServicearea().getId()),parentCustomers.getMvnoId())) {
            customers.setPartner(parentCustomers.getPartner());
        }
        else{
            customers.setPartner(parentCustomers.getPartner());
        }
        customers.setSalesremark("");
        customers.setServicetype("");
        customers.setServicearea(parentCustomers.getServicearea());
        customers.setStatus("Active");
        customers.setParentCustomers(parentCustomers);
        customers.setLatitude("");
        customers.setLongitude("");
        customers.setIstrialplan(false);
//        customers.setDi(0.0000);
//        customers.setFlatAmount(0.0000);
//        customers.setDiscountType("");
//        CustomerAddress customerAddressPojo =  new CustomerAddress();
//  customerAddressPojo.setAddressType("Present");
//        customerAddressPojo.setLandmark("Test");
//        customerAddressPojo.setAreaId(Math.toIntExact(parentCustomers.getServicearea().getPincodeList().get(0).getAreaList().get(0).getId()));
//        customerAddressPojo.setPincodeId(Math.toIntExact(parentCustomers.getServicearea().getPincodeList().get(0).getId()));
//        customerAddressPojo.setCityId(Math.toIntExact(parentCustomers.getServicearea().getPincodeList().get(0).getCityId()));
//        customerAddressPojo.setStateId(parentCustomers.getServicearea().getPincodeList().get(0).getStateId());
//        customerAddressPojo.setCountryId(parentCustomers.getServicearea().getPincodeList().get(0).getCountryId());
//        customerAddressPojo.setLandmark1("");
//        customerAddressPojo.setVersion("NEW");
//        customerAddressPojo.setIsDelete(false);
//        customerAddressPojo.setCustomer(customers);
//        customerAddressPojo.setCustomer(customers);

//        List<CustomerAddress> customerAddressPojos = parentCustomers.getAddressList();
//        customerAddressPojos.get(0).setCustomer(customers);
////        customerAddressPojos.add(customerAddressPojo);
//        customers.setAddressList(customerAddressPojos);
        if(isServiceAreaBranchBind(Math.toIntExact(parentCustomers.getServicearea().getId()))) {
            customers.setBranch(parentCustomers.getBranch());
        }
        else{
            customers.setBranch(null);
        }
        customers.setCafno("no");
        customers.setDunningCategory("Silver");
//        RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
//        recordPaymentPojo.setAmount(0.0000);
//        recordPaymentPojo.setPaymode("");
//        recordPaymentPojo.setReferenceno("");
//        customers.setPaymentDetails(recordPaymentPojo);
        customers.setValleyType("");
        customers.setCustomerArea("");
        customers.setCalendarType(CommonConstants.CAL_TYPE_ENGLISH);
        customers.setCreatedByName("Admin Admin");
        customers.setCreatedById(2);
        customers.setLast_password_change(LocalDateTime.now());
//        CustomerLocationMapping customerLocationMappingDto =  new CustomerLocationMapping();
//        customerLocationMappingDto.setLocationId(customerLocationMapping.getLocationId());
//        customerLocationMappingDto.setMac(mac);
//        customerLocationMappingDto.setIsParentLocation(false);
//        customerLocationMappingDto.setLocationName(customerLocationMapping.getLocationName());
//        customerLocationMappingDto.setIsActive(true);
//        customerLocationMappingDto.setIsDelete(false);
//        customerLocationMappingDto.setMvnoId(parentCustomers.getMvnoId());
//        List<CustomerLocationMapping> customerLocationMappingDtos = new ArrayList<>();
//        customerLocationMappingDtos.add(customerLocationMappingDto);
//        customers.setCustomerLocations(customerLocationMappingDtos);
        customers.setNextBillDate(LocalDate.now());
        LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, customers.getNextBillDate());
        if(nextQuotaReset != null) {
            customers.setNextQuotaResetDate(nextQuotaReset);
        } else {
            customers.setNextQuotaResetDate(LocalDate.now());
        }
        customers.setMvnoId(parentCustomers.getMvnoId());
        log.info("customers"+customers);
        return customers;
    }

    public CustomerListPojo getCustomerByUsernameAndMac(String username , String mac , String countryCode) throws Exception {
        CustomerListPojo customerListPojos = null;
        // TODO: pass mvnoID manually 6/5/2025
        Integer globalmvnoId = subscriberService.getMvnoIdFromCurrentStaff(null);
        if(Objects.nonNull(mac) && mac.length() > 0){
            log.info("mac is found find org cust");
            System.out.println("******API STARTTED TIME*****"+LocalDateTime.now());
            /**find location by mac **/
            List<LocationMasterMapping> locationMasterMappingsList = locationMasterMappingRepository.findAllByMac(mac);
            if(!locationMasterMappingsList.isEmpty()){
                log.info("location mac found");
                System.out.println("******Location TIME*****"+LocalDateTime.now());
                    log.info("location master found");
                    List<CustomerLocationMapping> customerLocationMappingList = customerLocationRepository.findByLocationIdAndIsActiveAndIsParentLocationAndIsDeleteAndMac(locationMasterMappingsList.get(0).getLocationMasterId() ,true ,true,false, mac);
                    if(!customerLocationMappingList.isEmpty()){
                        log.info("parent customer is found");
                        if(customerLocationMappingList.get(0).getCustId() != null) {
                            System.out.println("******Before Is available*****" + LocalDateTime.now());
                            Integer mvnoId = null;
                            if (isQuotaAvailable(customerLocationMappingList.get(0).getCustId().intValue())) {
                                System.out.println("******After quota available*****" + LocalDateTime.now());
                                log.info("quota is available");
                                Pattern pattern = Pattern.compile("^(\\d{8,12})@.+$");
                                Matcher matcher = pattern.matcher(username);

                                if (matcher.find()) {
                                    username = matcher.group(1);
                                }
                                String orgCustName = username + "@" + customerLocationMappingList.get(0).getLocationName();
                                System.out.println("******Before parent available*****" + LocalDateTime.now());
                                Customers parentCustomer = customersRepository.findById(customerLocationMappingList.get(0).getCustId().intValue()).get();
                                mvnoId = customersService.getMvnoIdFromCurrentStaff(parentCustomer.getId());
                                System.out.println("******Before Customer find available*****" + LocalDateTime.now());
                                Customers customers = customersRepository.findByUsernameAndMvnoId(orgCustName, mvnoId).orElse(null);
                                System.out.println("******Before parent available*****" + LocalDateTime.now());
                                if (customers != null) {
                                    log.info("Orginization Customer Found");
                                    if (!Objects.equals(parentCustomer.getId(), customers.getParentCustomers().getId())) {
                                        System.out.println("******After Parent available*****" + LocalDateTime.now());
                                        log.info("parent customer id is in change");
                                        customers.setParentCustomers(parentCustomer);
                                        customers.setParentCustomersId(parentCustomer.getId());
                                        customersRepository.save(customers);
                                        System.out.println("******After customer created*****" + LocalDateTime.now());
                                        CustomersPojo newcustomersPojo = customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext());
                                        System.out.println("******After parent available*****" + LocalDateTime.now());
                                        System.out.println("******Before Search *****" + LocalDateTime.now());
                                    }
                                    CustomerListPojo paidCustomer =  getByUsername(username , subscriberService.getMvnoIdFromCurrentStaff(parentCustomer.getId()));
                                    if(paidCustomer != null){
                                        if(!Objects.equals(paidCustomer.getPassword() , customers.getPassword())){
                                            log.info("Paid user found in for child customer change password to paid user password");
                                            customers.setPassword(paidCustomer.getPassword());
                                            customersRepository.save(customers);
                                            CustomersPojo customersPojo = customerMapper.domainToDTO(customers , new CycleAvoidingMappingContext());
                                            sendCustMessage(customersPojo);
                                        }
                                    }
                                    return getByUsername(customers.getUsername() , customers.getMvnoId());

                                } else {
                                    System.out.println("$$$$Before child customer create $$$$$$ :" + LocalDateTime.now());
                                    log.info("Orginizion Customer is not found make a new Customer");
                                    Customers saveCustomer = createCustomerFrom(username, mac, "", countryCode, orgCustName, parentCustomer, customerLocationMappingList.get(0));
                                    CustomerListPojo paidCustomer = getByUsername(username , subscriberService.getMvnoIdFromCurrentStaff());
                                    if(paidCustomer != null ){
                                        log.info("Paid customer found, Set child customer password to paid customer password");
                                        saveCustomer.setPassword(paidCustomer.getPassword());
                                    }
                                    System.out.println("Before customer create :" + LocalDateTime.now());
                                    List<CustomerAddress> customerAddress =  parentCustomer.getAddressList();
                                    Customers customers1 = customersRepository.save(saveCustomer);
                                    List<CustomerAddress> childCustomerAddressList = new ArrayList<>();
                                    CustomerAddress customerAddress1 = new CustomerAddress();
                                    customerAddress1.setCustomer(customers1);
                                    customerAddress1.setPincodeId(customerAddress.get(0).getPincodeId());
                                    customerAddress1.setArea(customerAddress.get(0).getArea());
                                    customerAddress1.setAreaId(customerAddress.get(0).getAreaId());
                                    customerAddress1.setPincode(customerAddress.get(0).getPincode());
                                    customerAddress1.setPincodeId(customerAddress.get(0).getPincodeId());
                                    customerAddress1.setCity(customerAddress.get(0).getCity());
                                    customerAddress1.setCityId(customerAddress.get(0).getCityId());
                                    customerAddress1.setAddressType("New");
                                    customerAddress1.setIsDelete(false);
                                   CustomerAddress childcustomeraddress =  customerAddressRepository.save(customerAddress1);
                                    childCustomerAddressList.add(childcustomeraddress);
                                    List<CustomerLocationMapping> customerLocationMappings = parentCustomer.getCustomerLocations();
                                    List<CustomerLocationMapping> newCustomerLocationMappings =  new ArrayList<>();
                                    CustomerLocationMapping customerLocationMapping = new CustomerLocationMapping();
                                    customerLocationMapping.setMac(customerLocationMappings.get(0).getMac());
                                    customerLocationMapping.setIsParentLocation(false);
                                    customerLocationMapping.setLocationName(customerLocationMappings.get(0).getLocationName());
                                    customerLocationMapping.setMvnoId(customerLocationMappings.get(0).getMvnoId());
                                    customerLocationMapping.setIsActive(true);
                                    customerLocationMapping.setLocationId(customerLocationMappings.get(0).getLocationId());
                                    customerLocationMapping.setCustId(customers1.getId().longValue());
                                    customerLocationMapping.setIsDelete(false);
                                    customerLocationRepository.save(customerLocationMapping);
                                    newCustomerLocationMappings.add(customerLocationMapping);
                                    customers1.setCustomerLocations(newCustomerLocationMappings);
                                    customers1.setAddressList(childCustomerAddressList);
                                    CustomersPojo customersPojo = customerMapper.domainToDTO(customers1,new CycleAvoidingMappingContext());
                                    customersPojo.setCustomerCreated(true);
                                    System.out.println("After customer create :" + LocalDateTime.now());
                                    //save customer for all microservice
                                    if (customersPojo != null) {
                                            sendCustMessage(customersPojo);
                                    }
                                    System.out.println("$$$$After child customer create $$$$$$ : " + LocalDateTime.now());
                                    System.out.println("****BEFORE SEARCH: " + LocalDateTime.now());
                                    return getByUsername(customersPojo.getUsername(), mvnoId);

                                }


                            } else {
                                log.info("quota is not available finding indiviudal cust");
                                return getByUsername(username, globalmvnoId);
                            }
                        }
                        else{
                            log.error("customer id not found in customer location mapping");
                            return getByUsername(username,globalmvnoId);
                        }
                    }
                    else{
                        log.info("parent customer is not found finding individual cust");
                        return getByUsername(username, globalmvnoId);
                    }

                }

            else{
                return getByUsername(username, globalmvnoId);
            }

        }
        else{
            log.info("can not find mac get into find normal cust");
            return getByUsername(username, globalmvnoId);
        }



    }


    public  PaginationRequestDTO getPaginationRequestFromUsername(String username){
        PaginationRequestDTO paginationRequestDTO =  new PaginationRequestDTO();
        GenericSearchModel genericSearchModel = new GenericSearchModel();
        genericSearchModel.setFilterColumn("usernameequalto");
        genericSearchModel.setFilterCondition("and");
        genericSearchModel.setFilterOperator("equalto");
        genericSearchModel.setFilterDataType("string");
        genericSearchModel.setFilterValue(username);
        List<GenericSearchModel> genericSearchModelList =  new ArrayList<>();
        genericSearchModelList.add(genericSearchModel);
        paginationRequestDTO.setFilters(genericSearchModelList);
        paginationRequestDTO.setPage(1);
        paginationRequestDTO.setPageSize(5);
        return  paginationRequestDTO;
    }

    public Boolean isQuotaAvailable(Integer custId){
        List<CustomerPlansModel>  customerPlansModelList =  subscriberService.getActivePlanQuota(custId);
        if(customerPlansModelList.stream().anyMatch(customerPlansModel -> customerPlansModel.getIsAllowOverUsage() == true)){
            log.info("Allow over usage true found in one of plan so quota is available");
            return true;
        }
        log.info("Allow over usage not found going for normal flow");
        customerPlansModelList = customerPlansModelList.stream().filter(customerPlansModel -> Double.parseDouble(customerPlansModel.getVolTotalQuota()) - Double.parseDouble(customerPlansModel.getVolUsedQuota()) - customerPlansModel.getTotalReserve() > 0 && Objects.equals(customerPlansModel.getPlanstage(), "ACTIVE")).collect(Collectors.toList());
        if(!customerPlansModelList.isEmpty()){
            return true;
        }
        else{
            return false;
        }

    }

    public Boolean IsCustOrgCust(Integer custId){
        Customers customers = customersRepository.findById(custId).get();
        String username  = customers.getUsername();
        if(username.contains("@") && Objects.nonNull(customers.getCustlabel())  && customers.getCustlabel().equalsIgnoreCase("organization")){
            return  true;
        }
        return false;
    }

    public Boolean IsIndividualCust(String username){
        // TODO: pass mvnoID manually 6/5/2025
        Integer mvnoId = customersService.getMvnoIdFromCurrentStaff(null);
       Customers customers = customersRepository.findByUsernameAndMvnoId(username , mvnoId).orElse(null);
       if(customers != null){
           return  true;
       }
       else{
           String iscustomercreate = clientServiceRepository.findValueByNameandMvnoId("ISCUSTOMERCREATE",mvnoId);
           if(iscustomercreate != null) {
               if(iscustomercreate.equalsIgnoreCase("1") || iscustomercreate.equalsIgnoreCase("true")) {
                   return false;
               }
               else{
                   return  true;
               }
           }
           else{
               return true;
           }
       }
    }

    public CustomersPojo getLocationmapping(CustomersPojo subscriber , String mac) {
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
                    if(mac != null && mac.length() > 0){
                        customerLocationMapping.setMac(mac);
                    }
                    list.add(customerLocationMapping);
                }
                if (!org.springframework.util.CollectionUtils.isEmpty(list)) {
                    customerLocationRepository.saveAll(list);
                    List<CustomerLocationMappingDto> locationMappingDtos = customerLocationMapper.domainToDTO(list, new CycleAvoidingMappingContext());
                    subscriber.setCustomerLocations(locationMappingDtos);
                }
            }
        }
        return subscriber;
    }

    public Boolean IsQuotaAvailableForIndividualCust(String username){
        // TODO: pass mvnoID manually 6/5/2025
        Integer mvnoId = customersService.getMvnoIdFromCurrentStaff(null);
        Customers customers = customersRepository.findByUsernameAndMvnoId(username , mvnoId).orElse(null);
        if(customers != null){
            List<CustomerPlansModel> customerPlansModelList = new ArrayList<>();
            customerPlansModelList = subscriberService.getActivePlanQuota(customers.getId());
            if(customerPlansModelList.stream().anyMatch(customerPlansModel -> customerPlansModel.getIsAllowOverUsage() == true)){
                log.info("Allow over usage true found in one of plan for indipendent cust so quota is available");
                return true;
            }
            log.info("Allow over usage not found for indipendent cust going for normal flow");
            customerPlansModelList = customerPlansModelList.stream()
                    .filter(customerPlansModel -> {
                        double volRemainingQuota = Double.parseDouble(customerPlansModel.getVolTotalQuota()) - Double.parseDouble(customerPlansModel.getVolUsedQuota()) - customerPlansModel.getTotalReserve();
                        double timeRemainingQuota = Double.parseDouble(customerPlansModel.getTimeTotalQuota()) - Double.parseDouble(customerPlansModel.getTimeUsedQuota());
                        return (volRemainingQuota > 0 || timeRemainingQuota > 0)
                                && Objects.equals(customerPlansModel.getPlanstage(), "ACTIVE");
                    })
                    .collect(Collectors.toList());
            if(!customerPlansModelList.isEmpty()){
                return  true;
            }
            else{
                return  false;
            }
        }
        else{
            return  false;
        }
    }

    public boolean IsQuotaAvalibleOnMac(String mac)  throws Exception{
        Boolean flag = false;
        if (Objects.nonNull(mac) && mac.length() > 0) {
            log.info("mac is found find org cust");
            /**find location by mac **/
            List<LocationMasterMapping> locationMasterMappingsList = locationMasterMappingRepository.findAllByMac(mac);
            if (!locationMasterMappingsList.isEmpty()) {
                log.info("location mac found");
                Optional<LocationMaster> locationMaster = locationMasterRepository.findById(locationMasterMappingsList.get(0).getLocationMasterId());
                if (locationMaster.isPresent()) {
                    log.info("location master found");
                    List<CustomerLocationMapping> customerLocationMappingList = customerLocationRepository.findByLocationIdAndIsActiveAndIsParentLocationAndIsDeleteAndMac(locationMaster.get().getLocationMasterId(), true, true, false,mac);
                    if (!customerLocationMappingList.isEmpty()) {
                        log.info("parent customer is found");
                        if (customerLocationMappingList.get(0).getCustId() != null) {
                           log.info("parent cust id is found");
                           if(isQuotaAvailable(customerLocationMappingList.get(0).getCustId().intValue())){
                               flag = true;
                           }
                        }
                    }
                }
            }
        }

        return flag;
    }

    public CaptiveCustomerResponseDTO getCaptiveCustResponse(String mac)  throws Exception{
        Integer partner = 1;
        CaptiveCustomerResponseDTO captiveCustomerResponseDTO =  new CaptiveCustomerResponseDTO();
        captiveCustomerResponseDTO.setIsParentCustAvailable(false);
        captiveCustomerResponseDTO.setIsPartnerBindWithServiceArea(false);
        captiveCustomerResponseDTO.setIsBranchBindWithServiceArea(true);
        if (Objects.nonNull(mac) && mac.length() > 0) {
            log.info("mac is found find org cust");
            /**find location by mac **/
            List<LocationMasterMapping> locationMasterMappingsList = locationMasterMappingRepository.findAllByMac(mac);
            if (!locationMasterMappingsList.isEmpty()) {
                log.info("location mac found");
                Optional<LocationMaster> locationMaster = locationMasterRepository.findById(locationMasterMappingsList.get(0).getLocationMasterId());
                if (locationMaster.isPresent()) {
                    log.info("location master found");
                    List<CustomerLocationMapping> customerLocationMappingList = customerLocationRepository.findByLocationIdAndIsActiveAndIsParentLocationAndIsDeleteAndMac(locationMaster.get().getLocationMasterId(), true, true, false,mac);
                    if (!customerLocationMappingList.isEmpty()) {
                        log.info("parent customer mapping list is found");
                        if (customerLocationMappingList.get(0).getCustId() != null) {
                            log.info("parent cust id is found");
                            Customers parentCustomer = customersRepository.findById(customerLocationMappingList.get(0).getCustId().intValue()).orElse(null);
                            if(parentCustomer != null){
                                log.info("parent customer found");
                                CustomersPojo customersPojo = customerMapper.domainToDTO(parentCustomer , new CycleAvoidingMappingContext());
                               captiveCustomerResponseDTO.setParentCustomers(customersPojo);
                               captiveCustomerResponseDTO.setIsParentCustAvailable(true);
                               captiveCustomerResponseDTO.setIsBranchBindWithServiceArea(isServiceAreaBranchBind(Math.toIntExact(parentCustomer.getServicearea().getId())));
                               captiveCustomerResponseDTO.setIsPartnerBindWithServiceArea(isServiceAreaPartnerBind(Math.toIntExact(parentCustomer.getServicearea().getId()),parentCustomer.getMvnoId()));
                            }
                        }
                    }
                }
            }
        }

        return captiveCustomerResponseDTO;
    }

    public CustomersPojo saveCustomerWithLimitedData(CustomersPojo customersPojo,String requestFrom) {
        try {
            customersPojo = addDefaultValue(customersPojo);
            Integer mvnoId = customersPojo.getMvnoId();
            customersPojo = customersService.save(customersPojo, requestFrom, false);
            Customers customers = customersRepository.findById(customersPojo.getId()).get();
            customers.setMvnoId(mvnoId);
            Mvno staffMvno = mvnoRepository.findById(Long.valueOf(mvnoId)).get();
            StaffUser staffUser = staffUserRepository.findByUsernameAndMvnoId(staffMvno.getUsername(),mvnoId);
            customers.setCurrentAssigneeId(staffUser.getId());
            customersRepository.save(customers);

            hierarchyService.assignEveryStaff(customers.getId(), CommonConstants.WORKFLOW_EVENT_NAME.CAF, true);
//            customersRepository.save(customerMapper.dtoToDomain(customersPojo, new CycleAvoidingMappingContext()));
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(ex.getErrCode(), ex.getMessage(), null);
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return customersPojo;
    }
    public CustomersPojo addDefaultValue(CustomersPojo pojo) {
        if(pojo.getPartnerid() == null) {
            pojo.setPartnerid(1);
        }
        if(pojo.getServiceareaid() != null) {
            List<CustomerAddressPojo> addressList = new ArrayList<>();
            CustomerAddressPojo customerAddressPojo = new CustomerAddressPojo();
            Optional<ServiceArea> serviceArea = serviceAreaRepository.findById(pojo.getServiceareaid());
            if(serviceArea.isPresent()) {
                customerAddressPojo.setAddressType(UtilsCommon.ADDR_TYPE_PRESENT);
                City city = cityRepository.getOne(serviceArea.get().getCityid().intValue());
                if(city != null) {
                    customerAddressPojo.setCityId(city.getId());
                    if(city.getState() != null)
                        customerAddressPojo.setStateId(city.getState().getId());
                    if(city.getCountryId() != null)
                        customerAddressPojo.setCountryId(city.getCountryId());
                    if(city.getState() != null && city.getCountryId() != null) {
                        Optional<Area> area = areaRepository.getFirstByCityIdAndCountryIdAndStateIdAndMvnoId(city.getId(), city.getCountryId(), city.getState().getId(), city.getMvnoId());
                        if(area.isPresent()) {
                            Long areaId = area.get().getId();
                            if(area.get().getPincode() != null) {
                                Long pincodeId = area.get().getPincode().getId();
                                customerAddressPojo.setPincodeId(pincodeId.intValue());
                            }
                            customerAddressPojo.setAreaId(areaId.intValue());
                        }
                    }
                }
                customerAddressPojo.setVersion("New");
                customerAddressPojo.setLandmark(pojo.getAddressList().get(0).getLandmark());
                customerAddressPojo.setCustomer(pojo);
                addressList.add(customerAddressPojo);
                pojo.setAddressList(addressList);
                List<Integer> branches = branchServiceAreaMappingRepository.findBranchByServiceareaId(serviceArea.get().getId().intValue());
                if(!CollectionUtils.isEmpty(branches) && pojo.getBranch() == null) {
                    pojo.setBranch(Long.valueOf(branches.get(0)));
                }
                pojo.setCustcategory(UtilsCommon.CREDIT_CLASS_GOLD);
                pojo.setCustcategory(UtilsCommon.CREDIT_CLASS_GOLD);
                pojo.setLeadSource(customersService.getLoggedInUser().getMvnoName());
                pojo.setContactperson(customersService.getLoggedInUser().getMvnoName());
                if (pojo.getMvnoId()==null) {
                    pojo.setMvnoId(serviceArea.get().getMvnoId());
                }
            } else {
                throw new CustomValidationException(APIConstants.NOT_FOUND,"Given Service Area Not Available",null);
            }
        }
        return pojo;
    }
    public Boolean isServiceAreaBranchBind(Integer serviceArea){
        log.info("enter in branch for serviceare");
        Boolean flag = true;
        log.info("flag intial set to true");
        List<Integer> serviceareaids = new ArrayList<>();
        serviceareaids.add(serviceArea);
        List<BranchDTO> branchDTOList = branchService.getAllBranchesByServieAreaId(serviceareaids);
        if(branchDTOList.isEmpty()){
            log.info("no branch found bind with servicearea");
            flag = false;
        }
        else{
            flag = true;
        }
        log.info("final flag for branch "+flag);
       return flag;

    }

    public Boolean isServiceAreaPartnerBind(Integer serviceArea,Integer mvnoId){
        log.info("enter in partner for serviceare");
        Boolean flag = false;
        log.info("flag intial set to false");
        List<Integer> serviceareaids = new ArrayList<>();
        serviceareaids.add(serviceArea);
        List<Partner> partnerList = partnerService.getPartnersByServiceAreaId(serviceareaids,mvnoId);
        partnerList = partnerList.stream().filter(partner -> partner.getId() != 1).collect(Collectors.toList());
        if(partnerList.isEmpty()){
            log.info("no partner found bind with servicearea");
            flag = false;
        }
        else{
            flag = true;
        }
        log.info("final flag for partner "+flag);
        return flag;

    }

    public void sendCustMessage(CustomersPojo pojo) {
        customersService.sharedCustomerData(pojo,false);
        LocalDateTime localDateTime = LocalDateTime.now();
        StringBuilder planNamesBuilder = new StringBuilder();
        if (pojo.getPlanMappingList() != null) {
            for (CustPlanMapppingPojo mappping : pojo.getPlanMappingList()) {
                String planname = postpaidPlanService.findNameById(mappping.getPlanId());
                if(!planNamesBuilder.toString().contains(planname)){
                    planNamesBuilder.append(planname).append(", ");
                }
                CustPackageRelMessage message = new CustPackageRelMessage(mappping, "");
                message.setCustomerCreated(pojo.isCustomerCreated());
                kafkaMessageSender.send(new KafkaMessageData(message, CustPackageRelMessage.class.getSimpleName()));
//                messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_PACKAGE_REL);
            }
        }
        String finalUsername = convertToMobileNumber(pojo.getUsername());
        if(finalUsername != null){
            CustomerListPojo paidUser = getByUsername(finalUsername,  pojo.getMvnoId());
            if(paidUser != null){
                log.info("Paid customer find with mobile number, Send original username and password");
                customersService.sendCustApprovalRegistrationMessage(paidUser.getId(),localDateTime.toString(), planNamesBuilder.toString(), paidUser.getUsername(), paidUser.getPassword(), pojo.getCountryCode(), pojo.getMobile(), pojo.getEmail(), pojo.getMvnoId(), pojo.getStatus(), pojo.getAcctno(), (long) customersService.getLoggedInStaffId(),pojo.getLoginUsername(),pojo.getLoginPassword());
            }
            else{
                log.info("Paid customer not find with mobile number, Send child customer username and password");
                customersService.sendCustApprovalRegistrationMessage(pojo.getId(),localDateTime.toString(), planNamesBuilder.toString(), pojo.getUsername(), pojo.getPassword(), pojo.getCountryCode(), pojo.getMobile(), pojo.getEmail(), pojo.getMvnoId(), pojo.getStatus(), pojo.getAcctno(), (long) customersService.getLoggedInStaffId(),pojo.getLoginUsername(),pojo.getLoginPassword());
            }
        }
        else{
            log.info("FinalUsername got null, Send child customer username and password");
            customersService.sendCustApprovalRegistrationMessage(pojo.getId(),localDateTime.toString(), planNamesBuilder.toString(), pojo.getUsername(), pojo.getPassword(), pojo.getCountryCode(), pojo.getMobile(), pojo.getEmail(), pojo.getMvnoId(), pojo.getStatus(), pojo.getAcctno(),(long) customersService.getLoggedInStaffId(),pojo.getLoginUsername(),pojo.getLoginPassword());
        }

        CustomMessage customMessage = new CustomMessage(pojo);
        //messageSender.send(customMessage, RabbitMqConstants.QUEUE_APIGW_CUSTOMER);
        kafkaMessageSender.send(new KafkaMessageData(customMessage,customMessage.getClass().getSimpleName(),"CUSTOMER_CREATE"));
//        messageSender.send(customMessage, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_NOTIFICATION);
    }

    public CustomerListPojo  getByUsername(String username,Integer mvnoId){
     List<Object[]> customerList = customersRepository.findCustomerObjectUsingUsername(username,mvnoId);
     if(!customerList.isEmpty()) {
         Object[] customer = customerList.get(0);
         Integer id = (Integer) customer[0];
         String name = (String) customer[2] + ' ' + (String) customer[3] + ' ' + (String) customer[4];
         String serviceAreaname = (String) customer[5];
         String mobile = (String) customer[6];
         String acctno = (String) customer[7];
         String status = (String) customer[8];
         Integer nextTeamHierchyMapping = (Integer) customer[9];
         Long staffId = (Long) customer[10];
         String password = (String)customer[11];
         CustomerListPojo customerListPojo = new CustomerListPojo(id, name, username , password, mobile, serviceAreaname, acctno, status, nextTeamHierchyMapping, staffId);
       //  System.out.println("$$$$$After Search$$$$$  : " + LocalDateTime.now());
         return customerListPojo;
     }
     else{
         return null;
     }

    }

    public DeactivatePlanReqDTOList CreateChangePlanPojoForBudpay(Integer custId,Integer planId,Integer custServiceId,Integer paymentOwnerId){
        DeactivatePlanReqDTOList deactivatePlanReqDTOList = new DeactivatePlanReqDTOList();
        List<DeactivatePlanReqDTO> deactivatePlanReqDTOS = new ArrayList<>();
        DeactivatePlanReqDTO deactivatePlanReqDTO = new DeactivatePlanReqDTO();
        List<DeactivatePlanReqModel> deactivatePlanReqModelList = new ArrayList<>();
        DeactivatePlanReqModel deactivatePlanReqModel = new DeactivatePlanReqModel();
        deactivatePlanReqModel.setBillToOrg(false);
        deactivatePlanReqModel.setNewPlanGroupId(null);
        deactivatePlanReqModel.setPlanGroupId(null);
        deactivatePlanReqModel.setNewPlanId(planId);
        deactivatePlanReqModel.setCustServiceMappingId(custServiceId);
        deactivatePlanReqModel.setDiscount(0.0000);
        deactivatePlanReqModelList.add(deactivatePlanReqModel);
        deactivatePlanReqDTO.setDeactivatePlanReqModels(deactivatePlanReqModelList);
        deactivatePlanReqDTO.setBillableCustomerId(custId);
        deactivatePlanReqDTO.setIsParent(true);
        deactivatePlanReqDTO.setPaymentOwner("admin");
        deactivatePlanReqDTO.setCustId(custId);
        deactivatePlanReqDTO.setPlanGroupChange(false);
        deactivatePlanReqDTO.setPlanGroupFullyChanged(false);
       deactivatePlanReqDTO.setChangePlanDate("Next Bill Date");
   //     deactivatePlanReqDTO.setChangePlanDate("Today");
        deactivatePlanReqDTOS.add(deactivatePlanReqDTO);
        deactivatePlanReqDTOList.setDeactivatePlanReqDTOS(deactivatePlanReqDTOS);
        return deactivatePlanReqDTOList;
    }

    public void sendBudPayChangePlanMessageToRevenue(Integer custId , Integer planId , String referenceNumber , String status , Double amount , Integer staffId){
        BudpayChangePlanMessage budpayChangePlanMessage = new BudpayChangePlanMessage();
        budpayChangePlanMessage.setCustomerId(custId);
        budpayChangePlanMessage.setPlanId(planId);
        budpayChangePlanMessage.setPaymentStatus(status);
        budpayChangePlanMessage.setReferenceNumber(referenceNumber);
        budpayChangePlanMessage.setAmount(amount);
        budpayChangePlanMessage.setStaffId(staffId);
        Gson gson = new Gson();
        gson.toJson(budpayChangePlanMessage);
        kafkaMessageSender.send(new KafkaMessageData(budpayChangePlanMessage,BudpayChangePlanMessage.class.getSimpleName()));
//        messageSender.send(budpayChangePlanMessage , RabbitMqConstants.QUEUE_SEND_BUDPAY_CUSTOMER_CWSC_CHANGE_PLAN_TO_REVENUE);
    }

    public String convertToMobileNumber(String username){
        String mobilenumber = null;
        mobilenumber = username.replaceAll("^\\d+", "");
        // If the result is empty, use the original input
        if (mobilenumber.isEmpty()) {
            mobilenumber = username;
        } else {
            mobilenumber = username.substring(0, username.length() - mobilenumber.length());
        }
        return mobilenumber;
    }

    public void saveCustomerLocationByCustId(Long locationId, Integer custId){
        Optional<LocationMaster> locationMaster = locationMasterRepository.findById(locationId);
        if(locationMaster.isPresent()) {
            CustomerLocationMapping customerLocationMapping = new CustomerLocationMapping();
            customerLocationMapping.setCustId(Long.valueOf(custId));
            customerLocationMapping.setLocationId(locationMaster.get().getLocationMasterId());
            customerLocationMapping.setLocationName(locationMaster.get().getName());
            customerLocationRepository.save(customerLocationMapping);
        }
    }
}
