package com.adopt.apigw.modules.paymentGatewayMaster.service;


import com.adopt.apigw.OnlinePaymentAudit.Service.OnlinePayAuditService;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.modules.RvenueClient.RevenueClient;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqDTOList;
import com.adopt.apigw.pojo.BudPay.BudPayPojo;
import com.adopt.apigw.pojo.BudPay.BudPayResponse;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.adopt.apigw.modules.PaymentConfig.service.PaymentConfigService;
import com.adopt.apigw.modules.paymentGatewayMaster.domain.PaymentGatewayResponse;
import com.adopt.apigw.modules.subscriber.model.ChangePlanRequestDTO;
import com.adopt.apigw.modules.subscriber.model.CustomChangePlanDTO;
import com.adopt.apigw.modules.subscriber.model.CustomersBasicDetailsPojo;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.AdditionalInformationDTO;
import com.adopt.apigw.pojo.CustomPeriodInvoiceDTO;
import com.adopt.apigw.pojo.PaginationDetails;
import com.adopt.apigw.pojo.api.CustomerPaymentDto;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.rabbitMq.message.BudPayPaymentMessage;
import com.adopt.apigw.rabbitMq.message.CustCwscOnlinePaymentDTO;
import com.adopt.apigw.rabbitMq.message.CustPayDTOMessage;
import com.adopt.apigw.repository.common.CustomerPaymentRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.postpaid.CustomerChargeHistoryRepo;
import com.adopt.apigw.repository.postpaid.DebitDocRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CaptivePortalCustomerService;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.DebitDocService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.adopt.apigw.utils.StatusConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.auditLog.model.AuditForResponseModel;
import com.adopt.apigw.modules.paymentGatewayMaster.domain.PaymentGateWay;
import com.adopt.apigw.modules.paymentGatewayMaster.dto.PaymentGatewayDTO;
import com.adopt.apigw.modules.paymentGatewayMaster.mapper.PaymentGatewayMapper;
import com.adopt.apigw.modules.paymentGatewayMaster.repository.PaymentGatewayRepository;
import com.adopt.apigw.utils.CommonConstants;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentGatewayService extends ExBaseAbstractService<PaymentGatewayDTO, PaymentGateWay, Long> {

    @Autowired
    private PaymentGatewayRepository paymentGatewayRepository;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    private PostpaidPlanService planService;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private PaymentConfigService paymentConfigService;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private OnlinePayAuditService onlinePayAuditService;

    @Autowired
    private CustomerChargeHistoryRepo customerChargeHistoryRepo;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private CaptivePortalCustomerService captivePortalCustomerService;
    @Autowired
    CustomerDocDetailsService customerDocDetailsService;

    @Autowired
    private RevenueClient revenueClient;

    @Autowired
    private StaffUserService staffUserService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentGatewayService(PaymentGatewayRepository repository, PaymentGatewayMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return " [PaymentGatewayService] ";
    }

    public PaymentGatewayDTO getPGByName(String name) {
        String SUBMODULE = getModuleNameForLog() + " [getPGByName()] ";
        try {
            List<PaymentGateWay> transactionModeList = paymentGatewayRepository.findAllByNameAndIsDeletedIsFalse(name);
            return (null != transactionModeList && 0 < transactionModeList.size())
                    ? getMapper().domainToDTO(transactionModeList.get(0), new CycleAvoidingMappingContext()) : null;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public PaymentGatewayDTO getByIdAndStatus(Long id) {
        return getMapper().domainToDTO(paymentGatewayRepository.findByIdAndStatus(id, CommonConstants.ACTIVE_STATUS), new CycleAvoidingMappingContext());
    }

    public List<PaymentGatewayDTO> getPGForUsers() {
        return this.paymentGatewayRepository.findByUserenableflagAndStatusAndIsDeletedIsFalse(true, CommonConstants.ACTIVE_STATUS)
                .stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<PaymentGatewayDTO> getPGForPartner() {
        return this.paymentGatewayRepository.findByPartnerenableflagAndStatusAndIsDeletedIsFalse(true, CommonConstants.ACTIVE_STATUS)
                .stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<PaymentGatewayDTO> getPGForBoth() {
        return this.paymentGatewayRepository.findByUserenableflagAndPartnerenableflagAndStatusAndIsDeletedIsFalse(true, true, CommonConstants.ACTIVE_STATUS)
                .stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<PaymentGatewayDTO> getAllByStatus() {
        return this.paymentGatewayRepository.findAllByStatusAndIsDeletedIsFalse(CommonConstants.ACTIVE_STATUS)
                .stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    public List<AuditForResponseModel> getPGListForAuditFor() {
        String SUBMODULE = getModuleNameForLog() + " [getPGListForAuditFor()] ";
        List<AuditForResponseModel> responseList = new ArrayList<>();
        try {
            List<PaymentGatewayDTO> pgList = getAllByStatus();
            if (null != pgList && 0 < pgList.size()) {
                for (PaymentGatewayDTO pgDTO : pgList) {
                    AuditForResponseModel responseModel = new AuditForResponseModel();
                    responseModel.setId(pgDTO.getId().intValue());
                    responseModel.setName(pgDTO.getName());
                    responseList.add(responseModel);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return responseList;
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
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

            //    e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}",APIConstants.FAIL, e.getStackTrace());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response, Page page) {
        try {
            //logger.info(new ObjectMapper().writeValueAsString(response));
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
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {

            //    e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            ApplicationLogger.logger.error("Error error{}exception{}",APIConstants.FAIL, e.getStackTrace());
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

    public ResponseEntity<?> apiResponses(Integer responseCode, HashMap<String, Object> response) {
        return apiResponse(responseCode, response, null);
    }


//    public DebitDocSearchPojo getInvoiceDetails(Integer invoiceId, Integer custId) {
//
//        QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
//        BooleanExpression exp = qDebitDocument.isNotNull();
//        exp = exp.and(qDebitDocument.id.eq(invoiceId)).and(qDebitDocument.customer.id.eq(custId));
//
//        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
//
//        List<DebitDocSearchPojo> queryResults =  queryFactory
//                .select(Projections.constructor(
//                        DebitDocSearchPojo.class,
//                        qDebitDocument.customer.title.concat(" ").concat(qDebitDocument.customer.firstname.concat(" ").concat(qDebitDocument.customer.lastname)),
//                        qDebitDocument.billrunstatus,
//                        qDebitDocument.createdate,
//                        qDebitDocument.totalamount,
//                        qDebitDocument.docnumber,
//                        qDebitDocument.billdate,
//                        qDebitDocument.billrunid,
//                        qDebitDocument.amountinwords,
//                        qDebitDocument.discount,
//                        qDebitDocument.latepaymentdate,
//                        qDebitDocument.startdate,
//                        qDebitDocument.endate,
//                        qDebitDocument.tax))
//                .from(qDebitDocument)
//                .where(exp)
//                .fetch();
//
//        return  queryResults.get(0);
//
//    }

     public PaymentGatewayResponse getRazorpayResponse(Long orderId , String pgTransactionId) throws Exception {
        PaymentGatewayResponse paymentGatewayResponse =  new PaymentGatewayResponse();
        paymentGatewayResponse.setOrderId(orderId);
        paymentGatewayResponse.setPgTransactionId(pgTransactionId);
         QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
         CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(orderId))).orElse(null);
         Long redirectTimeInSeconds = 10L;
         if(Objects.isNull(customerPayment)){
             throw new RuntimeException("Customer not available for transaction id");
         }
         if (Objects.nonNull(customerPayment)) {

             {
                 subscriberService.validateTransaction(pgTransactionId , orderId);
                 /**Payment Gateway parameter started**/
                 Optional<Customers> customersformvnoId = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                 HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.RAZORPAY , customersformvnoId.get().getMvnoId());
                 String REDIRECT_CAPTIVE_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.CAPTIVE_REDIRECT_URL);
                 String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.CWSC_REDIRECT_URL);
                 String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.REDIRECT_TIME_IN_SECONDS);
                 String STAFFUSER_USERNAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.STAFFUSER_USERNAME);
                 String STAFFUSER_PASSWORD = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.STAFFUSER_PASSWORD);
                 StaffUser staffUser = staffUserRepository.findStaffUserByUsername(STAFFUSER_USERNAME);
                 Long mvnoId = staffUser.getMvnoId().longValue();
                 String MVNO_NAME = mvnoRepository.findMvnoNameById(mvnoId);
                 redirectTimeInSeconds = Long.valueOf(REDIRECT_TIME_IN_SECONDS);
                 paymentGatewayResponse.setRedirectTimeInSecond(Integer.valueOf(REDIRECT_TIME_IN_SECONDS));
                 /**Payment Gateway parameter ended**/
                 List<GrantedAuthority> role_name=new ArrayList<>();
                 role_name.add(new SimpleGrantedAuthority("ADMIN"));
                 LoggedInUser user = new LoggedInUser(STAFFUSER_USERNAME, STAFFUSER_PASSWORD, true, true, true, true, role_name, MVNO_NAME, MVNO_NAME, LocalDateTime.now(), customersformvnoId.get().getCreatedById(), customersformvnoId.get().getPartner().getId(), "ADMIN", null, customersformvnoId.get().getMvnoId(), null, customersformvnoId.get().getCreatedById(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),MVNO_NAME,null,null,null);
                 UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                 SecurityContextHolder.getContext().setAuthentication(auth);
                 SecurityContextHolder.getContext().setAuthentication(auth);
                 // TODO: pass mvnoID manually 6/5/2025
                 Integer testMvno = getMvnoIdFromCurrentStaff(null);
//                        model.addAttribute("homeRedirectUrl", clientServiceSrv.getValueByName(ClientServiceConstant.HOME_REDIRECT_URL_CAPTIVE_PORTAL) + "?customerId=" + customerPayment.getCustId() + "&transactionId=" + customerPayment.getOrderId() + "&amount=" + customerPayment.getPayment() + "&currency=" + this.clientServiceSrv.getValueByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT));
                 Optional<Customers> customers = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                 if(customerPayment.getIsFromCaptive()){
                     String url = REDIRECT_CAPTIVE_URL;
                     url = url.replace("{userName}",customers.get().getUsername());
                     url = url.replace("{Password}",customers.get().getPassword());
                     paymentGatewayResponse.setRedirecturl(url);
                 }
                 else {
                     paymentGatewayResponse.setRedirecturl(REDIRECT_CWSC_URL);
                 }

                 ChangePlanRequestDTO requestDTO = new ChangePlanRequestDTO();
                 PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(Math.toIntExact(customerPayment.getPlanId())).get();
                 if(postpaidPlan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_RENEW) || postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL)) {
                     requestDTO.setPurchaseType(CommonConstants.PLAN_GROUP_RENEW);
                 }
                 else{
                     requestDTO.setPurchaseType("Addon");
                 }
                 requestDTO.setPlanId(Math.toIntExact(customerPayment.getPlanId()));
                 requestDTO.setIsPaymentReceived(false);
                 requestDTO.setRemarks("Transaction ID:-" + pgTransactionId);
                 requestDTO.setIsAdvRenewal(false);
                 requestDTO.setCustId(customers.get().getId());
                 requestDTO.setIsRefund(false);
                 requestDTO.setRecordPaymentDTO(null);
                 requestDTO.setOnlinePurType("RENEW");
                 requestDTO.setAddonStartDate(null);
                 requestDTO.setBillableCustomerId(null);
                 requestDTO.setIsParent(true);
                 requestDTO.setDiscount(0.0000);
                 requestDTO.setNewPlanList(null);
                 requestDTO.setPlanMappingList(null);
                 requestDTO.setPaymentOwnerId(customers.get().getCreatedById());
                 requestDTO.setIsTriggerCoaDm(true);
                 if(customerPayment.getLinkId() != null  && !customerPayment.getLinkId().isEmpty()) {
                     requestDTO.setCustServiceMappingId(Integer.parseInt(customerPayment.getLinkId()));
                 }
                 else{
                     List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findByCustId(customerPayment.getCustId());
                     if(!customerServiceMappingList.isEmpty()){
                         CustomerServiceMapping customerServiceMapping = customerServiceMappingList.get(customerServiceMappingList.size() -1);
                         requestDTO.setCustServiceMappingId(customerServiceMapping.getId());
                     }
                 }
                 String number=String.valueOf(UtilsCommon.gen());
                 CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers.get(), false, 0.0, "FLutter wave", null, number,null,null);
                 CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                 try {
                     Customers customer =  customersRepository.findById(basicDetailsPojo.getId()).get();
                     customer.setBillRunCustPackageRelId(customChangePlanDTO.getCustpackagerelid());
                     CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                     List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerIdAndPlanId(customersformvnoId.get().getId() , requestDTO.getPlanId());
                     custPlanMappping = custPlanMappping.stream().filter(custPlanMappping1 -> custPlanMappping1.getOfferPrice() >0).collect(Collectors.toList());
                     Customers customers1 = customersRepository.getOne(customersformvnoId.get().getId());
                     AdditionalInformationDTO additionalInformationDTO =  new AdditionalInformationDTO();
                     String transactionNumber = "";
                     transactionNumber = customerPayment.getOrderId().toString();
                     additionalInformationDTO.setTransactionNumber(transactionNumber);
                     debitDocService.createInvoice(customers1 , Constants.RENEW,"RAZORPAY", null,additionalInformationDTO, null,false,false,null,null,null);
                 } catch (Exception e) {
                     ApplicationLogger.logger.error("" + e.getMessage(), e);
                     e.printStackTrace();
                 }
                 if (customers.isPresent()) {
                     customersService.sendCustPaymentSuccessMessage("Payment Success", customers.get().getUsername(), customerPayment.getPayment(), "Online", customers.get().getMvnoId(), customers.get().getCountryCode(), customers.get().getMobile(), customers.get().getEmail(), customers.get().getId(),orderId.toString(), customerPayment.getPaymentDate().toString(),customers.get().getBuId(),null, (long) customersService.getLoggedInStaffId(),null);

                 }
                 customerPayment.setStatus("Successful");
                 customerPayment.setPgTransactionId(pgTransactionId);
             }
             customerPaymentRepository.save(customerPayment);


         }
       return paymentGatewayResponse;
     }

    public GenericDataDTO getResponseFromBudPay(BudPayPojo budPayPojo, String secretKey, String requestUrl){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        OnlinePayAudit onlinePayAudit = new OnlinePayAudit();
        try{

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(secretKey);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(budPayPojo);

            HttpEntity<String> entity = new HttpEntity<>(requestBody,httpHeaders);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String>  response = restTemplate.exchange(requestUrl,HttpMethod.POST,entity,String.class);

            if(response.getStatusCode() == HttpStatus.OK){
                genericDataDTO.setResponseMessage("Transaction initiated successfully");
                genericDataDTO.setResponseCode(response.getStatusCode().value());
                genericDataDTO.setData(response.getBody());
                // Create an ObjectMapper instance
                ObjectMapper respMapper = new ObjectMapper();
                // Parse JSON string to JsonNode
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                // Access data field
                JsonNode dataNode = rootNode.get("data");
                BudPayResponse budPayResponse = new BudPayResponse();
                budPayResponse.setAuthorizationUrl(dataNode.get("authorization_url").asText());
                budPayResponse.setReference(dataNode.get("reference").asText());
                budPayResponse.setAccessCode(dataNode.get("access_code").asText());
                budPayResponse.setPayerId(dataNode.get("payer_id").asText());
                genericDataDTO.setData(budPayResponse);

                CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.parseLong(dataNode.get("reference").asText()));
                customerPayment.setPaymentLink(dataNode.get("authorization_url").asText());
                customerPayment = customerPaymentRepository.save(customerPayment);
                CustomerPaymentDto customerPaymentDto = new CustomerPaymentDto(customerPayment);
                kafkaMessageSender.send(new KafkaMessageData(customerPaymentDto,customerPaymentDto.getClass().getSimpleName(),"BudPayPayment"));

            }else {
                genericDataDTO.setResponseMessage("Transaction initiation failed");
                genericDataDTO.setResponseCode(response.getStatusCode().value());
                genericDataDTO.setData(response.getBody());

            }
        }catch (Exception e){
            ApplicationLogger.logger.error(e.getMessage());
        }
        return genericDataDTO;
    }

    public void InitiateAddonOrRenewAfterPaymentSuccess(CustomerPayment customerPayment) {
        try {
            Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerPayment.getCustId());
            Customers customersformvnoId = customersRepository.findByIdAndIsDeletedIsFalse(customerPayment.getCustId());
            HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PHONEPE , customerPayment.getMvnoid());
            String STAFFUSER_USERNAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.STAFFUSER_USERNAME);
            String STAFFUSER_PASSWORD = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.STAFFUSER_PASSWORD);
            StaffUser staffUser = staffUserRepository.findStaffUserByUsername(STAFFUSER_USERNAME);
            Long mvnoId = staffUser.getMvnoId().longValue();
            String type = "";
            String MVNO_NAME = mvnoRepository.findMvnoNameById(mvnoId);
            /**Payment Gateway parameter ended**/
            List<GrantedAuthority> role_name=new ArrayList<>();
            role_name.add(new SimpleGrantedAuthority("ADMIN"));
            LoggedInUser user = new LoggedInUser(STAFFUSER_USERNAME, STAFFUSER_PASSWORD, true, true, true, true, role_name, MVNO_NAME, MVNO_NAME, LocalDateTime.now(), customersformvnoId.getCreatedById(), customersformvnoId.getPartner().getId(), "ADMIN", null, customersformvnoId.getMvnoId(), null, customersformvnoId.getCreatedById(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),MVNO_NAME,null,null,null);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            SecurityContextHolder.getContext().setAuthentication(auth);
            // TODO: pass mvnoID manually 6/5/2025
            Integer testMvno = getMvnoIdFromCurrentStaff(null);
            if(customers!=null){
                ChangePlanRequestDTO requestDTO = new ChangePlanRequestDTO();
                PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(Math.toIntExact(customerPayment.getPlanId())).get();
                if(postpaidPlan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_RENEW) || postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL)) {
                    requestDTO.setPurchaseType(CommonConstants.PLAN_GROUP_RENEW);
                }
                else{
                    requestDTO.setPurchaseType("Addon");
                }
                requestDTO.setPlanId(Math.toIntExact(customerPayment.getPlanId()));
                requestDTO.setIsPaymentReceived(false);
                requestDTO.setRemarks("Transaction ID:-" + customerPayment.getPgTransactionId());
                requestDTO.setIsAdvRenewal(false);
                requestDTO.setCustId(customers.getId());
                requestDTO.setIsRefund(false);
                requestDTO.setRecordPaymentDTO(null);
                requestDTO.setOnlinePurType("RENEW");
                requestDTO.setAddonStartDate(null);
                requestDTO.setBillableCustomerId(null);
                requestDTO.setIsParent(true);
                requestDTO.setDiscount(0.0000);
                requestDTO.setNewPlanList(null);
                requestDTO.setPlanMappingList(null);
                requestDTO.setPaymentOwnerId(customers.getCreatedById());
                if(customerPayment.getLinkId() != null  && !customerPayment.getLinkId().isEmpty()) {
                    requestDTO.setCustServiceMappingId(Integer.parseInt(customerPayment.getLinkId()));
                }
                else{
                    List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findByCustId(customerPayment.getCustId());
                    if(!customerServiceMappingList.isEmpty()){
                        CustomerServiceMapping customerServiceMapping = customerServiceMappingList.get(customerServiceMappingList.size() -1);
                        requestDTO.setCustServiceMappingId(customerServiceMapping.getId());
                    }
                }
                String number=String.valueOf(UtilsCommon.gen());
                CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers, false, 0.0, "FLutter wave", null, number,null,null);
                CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                try {
                    Customers customer =  customersRepository.findById(basicDetailsPojo.getId()).get();
                    customer.setBillRunCustPackageRelId(customChangePlanDTO.getCustpackagerelid());
                    CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                    List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerIdAndPlanId(customersformvnoId.getId() , requestDTO.getPlanId());
                    custPlanMappping = custPlanMappping.stream().filter(custPlanMappping1 -> custPlanMappping1.getOfferPrice() >0).collect(Collectors.toList());
                    Customers customers1 = customersRepository.findByIdAndIsDeletedIsFalse(customersformvnoId.getId());
                    AdditionalInformationDTO additionalInformationDTO =  new AdditionalInformationDTO();
                    String transactionNumber = "";
                    transactionNumber = customerPayment.getOrderId().toString();
                    additionalInformationDTO.setTransactionNumber(transactionNumber);
                    String plangroup = postpaidPlanRepo.findPlangroupById(requestDTO.getPlanId());
                    if (plangroup.equalsIgnoreCase("Registration and Renewal" ) || plangroup.equalsIgnoreCase("Renewal")){
                        type = Constants.RENEW;
                    }else {
                        type = Constants.ADD_ON;
                    }
                    debitDocService.createInvoice(customers1 ,type ,"PHONEPE", null,additionalInformationDTO, null,false,false,null,null,null);
                } catch (Exception e) {
                    ApplicationLogger.logger.error("" + e.getMessage(), e);
                    e.printStackTrace();
                }
                if (customers!=null) {
                    customersService.sendCustPaymentSuccessMessage("Payment Success", customers.getUsername(), customerPayment.getPayment(), "Online", customers.getMvnoId(), customers.getCountryCode(), customers.getMobile(), customers.getEmail(), customers.getId(),customerPayment.getOrderId().toString(), customerPayment.getPaymentDate().toString(),customers.getBuId(),null,(long)customersService.getLoggedInStaffId(),null);

                }
            }



        }catch (Exception e){
            ApplicationLogger.logger.error("Something went wrong request for plan addon/renew failed due to : "+e.getMessage());
        }
    }

    public PaymentGatewayResponse getCommonPaymentGatewayResponse(Long orderId , String pgTransactionId , String paymentGatewayName) throws Exception {
        PaymentGatewayResponse paymentGatewayResponse =  new PaymentGatewayResponse();
        paymentGatewayResponse.setOrderId(orderId);
        paymentGatewayResponse.setPgTransactionId(pgTransactionId);
        QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
        CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(orderId))).orElse(null);
        Long redirectTimeInSeconds = 10L;
        if(Objects.isNull(customerPayment)){
            throw new RuntimeException("Customer not available for transaction id");
        }
        if (Objects.nonNull(customerPayment)) {

            {
                subscriberService.validateTransaction(pgTransactionId , orderId);
                /**Payment Gateway parameter started**/
                Optional<Customers> customersformvnoId = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(paymentGatewayName, customersformvnoId.get().getMvnoId());
                String REDIRECT_CAPTIVE_URL = null;
                String REDIRECT_CWSC_URL = null;
                String REDIRECT_TIME_IN_SECONDS = null;
                String STAFFUSER_USERNAME = null;
                String STAFFUSER_PASSWORD = null;
                if(paymentGatewayName.equalsIgnoreCase(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.MOMO_PAY)) {
                    REDIRECT_CAPTIVE_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.CAPTIVE_REDIRECT_URL);
                    REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.CWSC_REDIRECT_URL);
                }
                STAFFUSER_USERNAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.STAFFUSER_USERNAME);
                STAFFUSER_PASSWORD = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.STAFFUSER_PASSWORD);
                REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.RAZORPAY.REDIRECT_TIME_IN_SECONDS);
                StaffUser staffUser = staffUserRepository.findStaffUserByUsername(STAFFUSER_USERNAME);
                Long mvnoId = staffUser.getMvnoId().longValue();
                String MVNO_NAME = mvnoRepository.findMvnoNameById(mvnoId);
                redirectTimeInSeconds = Long.valueOf(REDIRECT_TIME_IN_SECONDS);
                paymentGatewayResponse.setRedirectTimeInSecond(Integer.valueOf(REDIRECT_TIME_IN_SECONDS));
                /**Payment Gateway parameter ended**/
                List<GrantedAuthority> role_name=new ArrayList<>();
                role_name.add(new SimpleGrantedAuthority("ADMIN"));
                LoggedInUser user = new LoggedInUser(STAFFUSER_USERNAME, STAFFUSER_PASSWORD, true, true, true, true, role_name, MVNO_NAME, MVNO_NAME, LocalDateTime.now(), customersformvnoId.get().getCreatedById(), customersformvnoId.get().getPartner().getId(), "ADMIN", null, customersformvnoId.get().getMvnoId(), null, customersformvnoId.get().getCreatedById(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),MVNO_NAME,null,null,null);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);
                SecurityContextHolder.getContext().setAuthentication(auth);
                // TODO: pass mvnoID manually 6/5/2025
                Integer testMvno = getMvnoIdFromCurrentStaff(null);
//                        model.addAttribute("homeRedirectUrl", clientServiceSrv.getValueByName(ClientServiceConstant.HOME_REDIRECT_URL_CAPTIVE_PORTAL) + "?customerId=" + customerPayment.getCustId() + "&transactionId=" + customerPayment.getOrderId() + "&amount=" + customerPayment.getPayment() + "&currency=" + this.clientServiceSrv.getValueByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT));
                Optional<Customers> customers = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                if(customerPayment.getIsFromCaptive()){
                    String url = REDIRECT_CAPTIVE_URL;
                    url = url.replace("{userName}",customers.get().getUsername());
                    url = url.replace("{Password}",customers.get().getPassword());
                    paymentGatewayResponse.setRedirecturl(url);
                }
                else {
                    paymentGatewayResponse.setRedirecturl(REDIRECT_CWSC_URL);
                }

                ChangePlanRequestDTO requestDTO = new ChangePlanRequestDTO();
                PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(Math.toIntExact(customerPayment.getPlanId())).get();
                if(postpaidPlan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_RENEW) || postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL)) {
                    requestDTO.setPurchaseType(CommonConstants.PLAN_GROUP_RENEW);
                }
                else{
                    requestDTO.setPurchaseType("Addon");
                }
                requestDTO.setPlanId(Math.toIntExact(customerPayment.getPlanId()));
                requestDTO.setIsPaymentReceived(false);
                requestDTO.setRemarks("Transaction ID:-" + pgTransactionId);
                requestDTO.setIsAdvRenewal(false);
                requestDTO.setCustId(customers.get().getId());
                requestDTO.setIsRefund(false);
                requestDTO.setRecordPaymentDTO(null);
                requestDTO.setOnlinePurType("RENEW");
                requestDTO.setAddonStartDate(null);
                requestDTO.setBillableCustomerId(null);
                requestDTO.setIsParent(true);
                requestDTO.setDiscount(0.0000);
                requestDTO.setNewPlanList(null);
                requestDTO.setPlanMappingList(null);
                requestDTO.setPaymentOwnerId(customers.get().getCreatedById());
                requestDTO.setIsTriggerCoaDm(true);
                if(customerPayment.getLinkId() != null  && !customerPayment.getLinkId().isEmpty()) {
                    requestDTO.setCustServiceMappingId(Integer.parseInt(customerPayment.getLinkId()));
                }
                else{
                    List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findByCustId(customerPayment.getCustId());
                    if(!customerServiceMappingList.isEmpty()){
                        CustomerServiceMapping customerServiceMapping = customerServiceMappingList.get(customerServiceMappingList.size() -1);
                        requestDTO.setCustServiceMappingId(customerServiceMapping.getId());
                    }
                }
                String number=String.valueOf(UtilsCommon.gen());
                CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers.get(), false, 0.0, "FLutter wave", null, number,null,null);
                CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                try {
                    Customers customer =  customersRepository.findById(basicDetailsPojo.getId()).get();
                    customer.setBillRunCustPackageRelId(customChangePlanDTO.getCustpackagerelid());
                    CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                    List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerIdAndPlanId(customersformvnoId.get().getId() , requestDTO.getPlanId());
                    custPlanMappping = custPlanMappping.stream().filter(custPlanMappping1 -> custPlanMappping1.getOfferPrice() >0).collect(Collectors.toList());
                    Customers customers1 = customersRepository.getOne(customersformvnoId.get().getId());
                    AdditionalInformationDTO additionalInformationDTO =  new AdditionalInformationDTO();
                    String transactionNumber = "";
                    transactionNumber = customerPayment.getOrderId().toString();
                    additionalInformationDTO.setTransactionNumber(transactionNumber);
                    additionalInformationDTO.setAmount(customerPayment.getPayment());
                    if(postpaidPlan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_RENEW) || postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL)) {
                        debitDocService.createInvoice(customers1 , Constants.RENEW,paymentGatewayName, null,additionalInformationDTO, null,false,false,null,null,null);
                    }
                    else{
                        debitDocService.createInvoice(customers1 , Constants.ADD_ON,paymentGatewayName, null,additionalInformationDTO, null,false,false,null,null,null);
                    }
                } catch (Exception e) {
                    ApplicationLogger.logger.error("" + e.getMessage(), e);
                    e.printStackTrace();
                }
                if (customers.isPresent()) {
                    customersService.sendCustPaymentSuccessMessage("Payment Success", customers.get().getUsername(), customerPayment.getPayment(), "Online", customers.get().getMvnoId(), customers.get().getCountryCode(), customers.get().getMobile(), customers.get().getEmail(), customers.get().getId(),orderId.toString(), customerPayment.getPaymentDate().toString(),customers.get().getBuId(),null,(long) customersService.getLoggedInStaffId(),null);

                }
                customerPayment.setStatus("Successful");
                customerPayment.setPgTransactionId(pgTransactionId);
                customerPayment.setTransactionDate(LocalDateTime.now());
            }
            auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUYPLAN, AclConstants.OPERATION_BUYPLAN_BUY, null,null , Long.valueOf(customerPayment.getCustId()),customerPayment.getCustomerUsername());
            customerPaymentRepository.save(customerPayment);


        }
        return paymentGatewayResponse;
    }

    public void addCustomerPayment(CustPayDTOMessage message){
        CustomerLedgerDtlsPojo customerLedgerDtlsPojo=new CustomerLedgerDtlsPojo();
        CustomerPayment customerPayment = null;
        customerPayment = customerPaymentRepository.findByOrderId(message.getOrderId());
        if(Objects.isNull(customerPayment)) {
            customerPayment = new CustomerPayment();
            customerPayment.setOrderId(message.getOrderId());
            customerPayment.setPayment(message.getPayment());
            customerPayment.setStatus(message.getStatus());
            customerPayment.setCustId(message.getCustId());
            customerPayment.setCustomerUsername(message.getCustomerUsername());
            customerPayment.setPlanId(message.getPlanId());
            customerPayment.setMvnoid(message.getMvnoid());
            if (message.getAccountNumber() != null) {
                customerPayment.setAccountNumber(message.getAccountNumber());
            }
            customerPayment.setBuid(message.getBuid());
            if (message.getMerchantName() != null) {
                customerPayment.setMerchantName(message.getMerchantName());
            }
            if (message.getPgTransactionId() != null) {
                customerPayment.setPgTransactionId(message.getPgTransactionId());
            }
            if (message.getPaymentLink() != null) {
                customerPayment.setPaymentLink(message.getPaymentLink());
            }
            if (message.getPartnerPaymentId() != null) {
                customerPayment.setPartnerPaymentId(message.getPartnerPaymentId());
            }
            if (message.getIsFromCaptive() != null) {
                customerPayment.setIsFromCaptive(message.getIsFromCaptive());
            }
            if (message.getChecksum() != null) {
                customerPayment.setChecksum(message.getChecksum());
            }
            if (message.getCreditDocumentId() != null) {
                customerPayment.setCreditDocumentId(message.getCreditDocumentId());
            }
            if (message.getCustServiceMappingId() != null) {
                customerPayment.setLinkId(message.getCustServiceMappingId().toString());
            }
            if (message.getTransactionDate() != null && !message.getTransactionDate().trim().isEmpty()) {
                customerPayment.setTransactionDate(LocalDateTime.parse(message.getTransactionDate()));
            }
            customerPayment.setPaymentDate(LocalDateTime.parse(message.getPaymentDate()));
            customerPaymentRepository.save(customerPayment);
        }
        else{
            customerPayment.setStatus(message.getStatus());
            customerPayment.setTransactionDate(LocalDateTime.now());
            customerPayment.setMerchantName(message.getMerchantName());
            if(message.getPgTransactionId() != null){
                customerPayment.setPgTransactionId(message.getPgTransactionId());
            }
            customerPaymentRepository.save(customerPayment);
        }
        Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerPayment.getCustId());
        List<DebitDocument> debitDocuments = debitDocRepository.findAllByCustomerAndBillrunstatusIsNot(customers, "VOID");
        customerLedgerDtlsPojo.setCustId(customers.getId());
        Object[] data =(Object[]) staffUserRepository.findUserPassByStaffId(customers.getCreatedById())[0];
        ResponseEntity<Map<String, Object>> walletPojo= revenueClient.getWalletAmount(customerLedgerDtlsPojo, staffUserService.generateToken((String) data[0],(String) data[1]));
        if (walletPojo.getStatusCode().is2xxSuccessful() && walletPojo.getBody() != null) {
            Map<String, Object> responseBody = walletPojo.getBody();
            if (responseBody.containsKey("customerWalletDetails")) {
                Double customerWalletDetails = Double.parseDouble(responseBody.get("customerWalletDetails").toString());
                if(customerWalletDetails>0 || customerWalletDetails==0){
                    for(DebitDocument document:debitDocuments) {
                        List<CustPlanMappping> custPlanMapppingList1 = custPlanMappingRepository.findAllByCustomerId(customerPayment.getCustId());
                        List<Integer> cprIds = custPlanMapppingList1.stream().filter(list -> list.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE)).map(mappping -> mappping.getId()).collect(Collectors.toList());
                        customerDocDetailsService.changeStatusDisableToActive(cprIds);
                        String status = custPlanMappingRepository.getCustPlanStatusByCustomer_Id(customerPayment.getCustId());
                        if(status.equalsIgnoreCase("Active")) {
                            try {
                                createInvoiceForConvertedActivePlan(customerPayment.getCustId(), document.getStartdate(), document.getEndate(), CommonConstants.INVOICE_TYPE.CHANGE_PLAN, data,StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE);
                            } catch (Exception e) {
                                log.error("Failed to create invoice for latest debit document (start=" + document.getStartdate() + ")", e);
                            }
                        }
                    }
                }
            }
        } else {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Failed to fetch customer wallet details", null);
        }

    }


    public void changePlanAndCreatInvoiceFromCustomer(CustCwscOnlinePaymentDTO dataMessage){
        QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
        try {
            CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.orderId.eq(dataMessage.getReference())).orElse(null);
            Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerPayment.getCustId());
            if(customers!=null){
                Mvno mvno = mvnoRepository.findById(Long.valueOf(customers.getMvnoId())).orElse(null);
                List<StaffUser> staffUser = staffUserRepository.findAllStaffByMvnoIds(Collections.singletonList(mvno.getId()));
                List<GrantedAuthority> role_name = new ArrayList<>();
                role_name.add(new SimpleGrantedAuthority("ADMIN"));
                LoggedInUser user = new LoggedInUser(staffUser.get(0).getUsername(), mvno.getName(), true, true, true, true, role_name, mvno.getName(), mvno.getName(), LocalDateTime.now(), staffUser.get(0).getId(), customers.getPartner().getId(), "ADMIN", null, customers.getMvnoId(), null, staffUser.get(0).getId(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),mvno.getName(),null,null,null);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);
                DeactivatePlanReqDTOList changePlanPojoFromCustomer = captivePortalCustomerService.CreateChangePlanPojoForBudpay(customers.getId(), customerPayment.getPlanId(), customers.getCustomerServiceMappingList().get(0).getId(), dataMessage.getPaymentOwnerId());
                subscriberService.deActivatePlanInList(changePlanPojoFromCustomer);
                List<DebitDocument> debitDocuments = debitDocRepository.findAllByCustomerAndBillrunstatusIsNot(customers, "VOID");
                if (debitDocuments.size() == 0) {
                    changePlanPojoFromCustomer.getDeactivatePlanReqDTOS().get(0).setChangePlanDate("Today");
                }
                boolean changePlanNextBillDate = false;
                if (changePlanPojoFromCustomer.getDeactivatePlanReqDTOS().get(0).getChangePlanDate() != null && changePlanPojoFromCustomer.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date") && debitDocuments.size() > 0) {
                    changePlanNextBillDate = true;
                }
                debitDocService.createInvoice(customers, CommonConstants.INVOICE_TYPE.CHANGE_PLAN, "", changePlanPojoFromCustomer.getRecordPayment(), null, null, changePlanNextBillDate, false, null,null,null);
                customerPaymentRepository.save(customerPayment);
                 captivePortalCustomerService.sendBudPayChangePlanMessageToRevenue(customerPayment.getCustId(), customerPayment.getPlanId(), customerPayment.getOrderId().toString(), dataMessage.getPaymentStatus(), customerPayment.getPayment(), dataMessage.getPaymentOwnerId());
               }
        }catch (Exception e){
            e.printStackTrace();
            ApplicationLogger.logger.error("Exception  customer change plan and creating invoice ");
        }
    }

    public GenericDataDTO verifyBudPayPayment(String reference) {
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(Long.parseLong(reference));

            if (customerPayment != null) {
                if (!customerPayment.getStatus().equalsIgnoreCase("success")) {
                    HashMap<String, String> paymentGatewayParameter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.BUDPAY, getMvnoIdFromCurrentStaff());
                    String secretKey = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.BUDPAY.BUDPAY_SECRET_KEY);
                    String verifyUrl = paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.BUDPAY.BUDPAY_VERIFY_STATUS_URL);
                    if (verifyUrl.endsWith("/")) {
                        verifyUrl = verifyUrl.substring(0, verifyUrl.length() - 1);
                    }
                    HttpGet httpGet = new HttpGet(verifyUrl + "/" + reference);
                    httpGet.setHeader("Authorization", "Bearer " + secretKey);

                    CloseableHttpClient httpClient = HttpClients.createDefault();
                    CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
                    org.apache.http.HttpEntity responseEntity = httpResponse.getEntity();
                    String responseText = EntityUtils.toString(responseEntity, "UTF-8");
                    JsonNode rootNode = objectMapper.readTree(responseText);
                    String apiResponseMsg = rootNode.path("message").asText();// message of the budpay verify trasnaction api
                    if (rootNode.path("status").asBoolean()) {
                        JsonNode data = rootNode.path("data"); // data node that has payment data
                        String transactionStatus = data.path("status").asText(); // transaction status of payment
                        String transactionDate = data.path("transaction_date").asText(); // transaction date of payment
                        if (transactionDate != null && !transactionDate.equalsIgnoreCase("null")) {
                            customerPayment.setPaymentDate(LocalDateTime.parse(transactionDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        }else customerPayment.setPaymentDate(LocalDateTime.now());

                        if (transactionStatus.equalsIgnoreCase("success")) {
                            customerPayment.setStatus(transactionStatus);
                            if (customerPayment.getPlanId() == null) {
                                log.info("customer payment for orderId: {}, plan id is found null",reference);
                                customerPaymentRepository.save(customerPayment);
                                Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerPayment.getCustId());
                                if (customers != null) {
                                    BudPayPaymentMessage budPayPaymentMessage = new BudPayPaymentMessage(customers.getId(), transactionStatus, reference);
                                    kafkaMessageSender.send(new KafkaMessageData(budPayPaymentMessage, BudPayPaymentMessage.class.getSimpleName()));
                                }
                            } else {
                                log.info("customer payment for orderId: {} found for change plan with planId: {};",reference,customerPayment.getPlanId());
                                Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerPayment.getCustId());
                                Mvno mvno = mvnoRepository.findById(Long.valueOf(customers.getMvnoId())).orElse(null);
                                List<StaffUser> staffUser = staffUserRepository.findAllStaffByMvnoIds(Collections.singletonList(mvno.getId()));
                                List<GrantedAuthority> role_name = new ArrayList<>();
                                role_name.add(new SimpleGrantedAuthority("ADMIN"));
                                LoggedInUser user = new LoggedInUser(staffUser.get(0).getUsername(), mvno.getName(), true, true, true, true, role_name, mvno.getName(), mvno.getName(), LocalDateTime.now(), staffUser.get(0).getId(), customers.getPartner().getId(), "ADMIN", null, customers.getMvnoId(), null, staffUser.get(0).getId(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(), mvno.getName(), null, null, null);
                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                                SecurityContextHolder.getContext().setAuthentication(auth);
                                if (customers != null) {
                                    DeactivatePlanReqDTOList changePlanPojoFromCustomer = captivePortalCustomerService.CreateChangePlanPojoForBudpay(customers.getId(), customerPayment.getPlanId(), customers.getCustomerServiceMappingList().get(0).getId(), staffUser.get(0).getId());
                                    subscriberService.deActivatePlanInList(changePlanPojoFromCustomer);
                                    List<DebitDocument> debitDocuments = debitDocRepository.findAllByCustomerAndBillrunstatusIsNot(customers, "VOID");
                                    if (debitDocuments.size() == 0) {
                                        changePlanPojoFromCustomer.getDeactivatePlanReqDTOS().get(0).setChangePlanDate("Today");
                                    }
                                    boolean changePlanNextBillDate = false;
                                    if (changePlanPojoFromCustomer.getDeactivatePlanReqDTOS().get(0).getChangePlanDate() != null && changePlanPojoFromCustomer.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date") && debitDocuments.size() > 0) {
                                        changePlanNextBillDate = true;
                                    }
                                    debitDocService.createInvoice(customers, CommonConstants.INVOICE_TYPE.CHANGE_PLAN, "", changePlanPojoFromCustomer.getRecordPayment(), null, null, changePlanNextBillDate, false, null, null,null);
                                    customerPayment = customerPaymentRepository.save(customerPayment);
                                    captivePortalCustomerService.sendBudPayChangePlanMessageToRevenue(customerPayment.getCustId(), customerPayment.getPlanId(), customerPayment.getOrderId().toString(), transactionStatus, customerPayment.getPayment(), staffUser.get(0).getId());

                                }
                            }
                        }else {
                            customerPayment.setStatus(transactionStatus);
                            customerPaymentRepository.save(customerPayment);
                            log.info("budpay payment for orderId: {}, status: {}; Message: {}",reference,transactionStatus,apiResponseMsg);
                        }
                    }else {
                        log.info("some issue occurred while check status for budpay payment orderId: {}; Message: {}",reference,apiResponseMsg);
                        dataDTO.setResponseCode(APIConstants.EXPECTATION_FAILED);
                        dataDTO.setResponseMessage("Reference No does not belongs to Budpay");
                        return dataDTO;
                    }
                    dataDTO.setResponseCode(APIConstants.SUCCESS);
                    dataDTO.setResponseMessage("Payment verified successfully");
                }else {
                    dataDTO.setResponseCode(APIConstants.SUCCESS);
                    dataDTO.setResponseMessage("Payment Already found Success");
                    log.info("budpay payment for orderId: {}, is already found success",reference);
                }
            }else {
                log.info("no customer payment found for orderId: {}",reference);
                dataDTO.setResponseCode(APIConstants.SUCCESS);
                dataDTO.setResponseMessage("customer payment not found for order Id: "+reference);
            }
        } catch (Exception e) {
            log.error("error while verifying budpay online payment status for orderID: {}; Error : {};",reference,e.getMessage());
            dataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            dataDTO.setResponseMessage("Some error occured");
            e.printStackTrace();
        }
        return dataDTO;
    }


    public void createInvoiceForConvertedActivePlan(Integer custId, LocalDateTime startDate, LocalDateTime endDate, String planType, Object[] data, String status){
        System.out.println("----------------------Entering in createInvoiceForConvertedActivePlan----------------------");
        CustomPeriodInvoiceDTO customPeriodInvoiceDTO=new CustomPeriodInvoiceDTO();
        LocalDate lastDayOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        customPeriodInvoiceDTO.setCustomStartDate(startDate.plusMonths(1).toLocalDate());
        customPeriodInvoiceDTO.setCustomEndDate(lastDayOfMonth);
        customPeriodInvoiceDTO.setCustomerId(custId);
        customPeriodInvoiceDTO.setInvoiceType(planType);
        System.out.println("---------------------------------------- (startDate plus 1=" + startDate.plusMonths(1).toLocalDate() + ") and (lastDayOfMonth=" + lastDayOfMonth + ")------------------------------");
        ResponseEntity<?> response=revenueClient.createPostpaidInvoiceForCustomPeriod(customPeriodInvoiceDTO,status,staffUserService.generateToken((String) data[0],(String) data[1]));
        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("----------------------Invoice Generated Successfully For Converted Active Plan----------------------");
        } else {
            log.error("Failed to create invoice for latest debit document (startDate=" + startDate + ") and (endDate=" + endDate + ")");
        }

    }
}
