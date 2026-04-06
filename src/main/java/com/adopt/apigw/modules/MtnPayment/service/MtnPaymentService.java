package com.adopt.apigw.modules.MtnPayment.service;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Customers.LightPostpaidPlanDTO;
import com.adopt.apigw.modules.MtnPayment.model.*;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.payments.controller.PaytmPaymentController;
import com.adopt.apigw.modules.subscriber.model.ChangePlanRequestDTO;
import com.adopt.apigw.modules.subscriber.model.CustomChangePlanDTO;
import com.adopt.apigw.modules.subscriber.model.CustomersBasicDetailsPojo;
import com.adopt.apigw.modules.subscriber.model.RecordPaymentRequestDTO;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.AdditionalInformationDTO;
import com.adopt.apigw.pojo.PaymentListPojo;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.CustomMessage;
import com.adopt.apigw.repository.common.BranchServiceAreaMappingRepository;
import com.adopt.apigw.repository.common.CustomerPaymentRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.postpaid.PlanServiceRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.SendRestAPIService;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.service.postpaid.CustPlanMappingService;
import com.adopt.apigw.service.postpaid.DebitDocService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.adopt.apigw.core.utillity.log.ApplicationLogger.logger;

@Service
public class MtnPaymentService {

    @Autowired
    CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private PaytmPaymentController paytmPaymentController;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private URL sendurl;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;

    @Autowired
    private NumberSequenceUtil numberSequenceUtil;

    @Autowired
    private MTNPaymentRequestService mtnPaymentRequestService;

    @Autowired
    private SendRestAPIService sendRestAPIService;

    @Autowired
    private PlanServiceRepository planServiceRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private CustPlanMappingService custPlanMappingService;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private MvnoRepository mvnoRepository;


    private static final Logger log = LoggerFactory.getLogger(MtnPaymentService.class);

    //   String subscriptionKey = "f4f2da18c0db4033b897644dc8ef1fec";

    //make constant for this
    //make url constant and also put it into the application propeerties
    //createApiUser
    public int getCreateUserApiResponce(String subscriptionKey, String contentType, String UUID) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n  \"providerCallbackHost\": \"https://webhook.site/1adc9d11-e64c-4e1f-b06f-e1520245f3fe\"\n}");
        Request request = new Request.Builder()
                .url("https://sandbox.momodeveloper.mtn.com/v1_0/apiuser")
                .method("POST", body)
                .addHeader("X-Reference-Id", UUID)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return response.code();

    }


    public String getCreatedUserApiResponce(String subscriptionKey, String contentType, String UUID) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://sandbox.momodeveloper.mtn.com/v1_0/apiuser/".concat(UUID))
                .method("GET", null)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Content-Type", contentType)
                .build();
        Response response = client.newCall(request).execute();
        JSONObject object = new JSONObject(response.body().string());
        String providerCallbackHost = object.getString("providerCallbackHost");
        String targetEnvironment = object.getString("targetEnvironment");
        return targetEnvironment;
    }

    public String getGeneratingApiKey(String subscriptionKey, String contentType, String UUID) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://sandbox.momodeveloper.mtn.com/v1_0/apiuser/" + UUID + "/apikey")
                .method("POST", body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .build();
        Response response = client.newCall(request).execute();

        String tmp = response.body().string();

        JSONObject object = new JSONObject(tmp);
        String API_KEY = object.getString("apiKey");
        return API_KEY;

    }

    public String getGeneratingApiToken(String subscriptionKey, String authKey) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://sandbox.momodeveloper.mtn.com/collection/token/")
                .method("POST", body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Authorization", "Basic ".concat(authKey).concat(""))
                .build();
        Response response = client.newCall(request).execute();
        String tmp = response.body().string();
        JSONObject object = new JSONObject(tmp);
        String TOKEN = object.getString("access_token");
        String token_type = object.getString("token_type");
       // String expires_in = object.getString("expires_in");
        String status = String.valueOf(response.code());
        if (status.equals("200"))
            return TOKEN;
        if (status.equals("401"))
            return "UnAuthorized";
        if (status.equals("500"))
            return "INTERNAL_SERVER_ERROR";
        return "ERROR";
    }

    public static int getRequestToPay(String subscriptionKey, String contentType, String UUID, String userToken, String targetEnvironment, String amount, String phone) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse(contentType);
        RequestBody body = RequestBody.create(mediaType,
                "{\r\n  \"amount\": \""
                        + amount + "\",\r\n  \"currency\": \"EUR\",\r\n  \"externalId\": \"15977684\",\r\n  \"payer\": {\r\n    \"partyIdType\": \"MSISDN\",\r\n    \"partyId\": \""
                        + phone + "\"\r\n  },\r\n  \"payerMessage\": \"Please Note\",\r\n  \"payeeNote\": \"Confirm To Pay\"\r\n}");
        Request request = new Request.Builder()
                .url("https://sandbox.momodeveloper.mtn.com/collection/v1_0/requesttopay")
                .method("POST", body)
                .addHeader("Authorization", "Bearer ".concat(userToken))
                .addHeader("X-Reference-Id", UUID)
                .addHeader("X-Target-Environment", targetEnvironment)
                .addHeader("Content-Type", contentType)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .build();

        Response response = client.newCall(request).execute();

        //Get returned status code
        String status2 = String.valueOf(response.code());

        if (response.code() == 409) {
            JSONObject object = new JSONObject(response.body().string());

        }
        return response.code();
    }


    public static String getStatus(String PRIMARY_KEY, String CONTENT_TYPE, String TARGET_ENVIRONMENT, String UUID, String USER_TOKEN) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://sandbox.momodeveloper.mtn.com/collection/v1_0/requesttopay/".concat(UUID))
                .method("GET", null)
                .addHeader("Authorization", "Bearer ".concat(USER_TOKEN))
                .addHeader("X-Target-Environment", TARGET_ENVIRONMENT)
                .addHeader("Content-Type", CONTENT_TYPE)
                .addHeader("Ocp-Apim-Subscription-Key", PRIMARY_KEY)
                .build();
        Response response = client.newCall(request).execute();

        JSONObject object = new JSONObject(response.body().string());
        //Get attributes from response body
        String externalId = object.getString("externalId");
        String amount = object.getString("amount");
        String currency = object.getString("currency");
        String payerMessage = object.getString("payerMessage");
        String payeeNote = object.getString("payeeNote");
        String status = object.getString("status");
        //String payer = object.getString("payer");


        if (object.has("reason")) {
            String reason = object.getString("reason");
            System.out.println("reason = " + reason);
        }


        return ("Response Code = " + status + "externalId = " + externalId + "amount = " + amount + "currency = " + currency + "payer = " );
    }

    public Response getAccountBalance(String PRIMARY_KEY,String USER_TOKEN,String TARGET_ENVIRONMENT, String CONTENT_TYPE) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
            MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://sandbox.momodeveloper.mtn.com/collection/v1_0/account/balance")
                .method("GET", null)
                .addHeader("Content-Type", CONTENT_TYPE)
                .addHeader("Ocp-Apim-Subscription-Key", PRIMARY_KEY)
                .addHeader("X-Target-Environment", TARGET_ENVIRONMENT)
                .addHeader("Authorization", "Bearer ".concat(USER_TOKEN))
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public Response CheckIfUserIsRegisteredAndActive(String PRIMARY_KEY,String USER_TOKEN,String TARGET_ENVIRONMENT,String CONTENT_TYPE,String phone) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://sandbox.momodeveloper.mtn.com/collection/v1_0/accountholder/"+"msisdn/" +phone+"/active")
                .method("GET", null)
                .addHeader("X-Target-Environment", TARGET_ENVIRONMENT)
                .addHeader("Ocp-Apim-Subscription-Key", PRIMARY_KEY)
                .addHeader("Authorization", "Bearer ".concat(USER_TOKEN))
                .addHeader("Content-Type", CONTENT_TYPE)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

//    public CustomerPayment putUuidString(String uuid){
//    if(uuid!= null){
 //       CustomerPayment customerPayment = new CustomerPayment();
//        customerPayment.setOrderId(1l);
//        customerPayment.setUuidId(uuid);
//        CustomerPayment save = customerPaymentRepository.save(customerPayment);
//        return save;
//    }
//    return null;
//    }

    public CustomerPayment putStatusString(String status,CustomerPayment customerPayment,Long orderId,Integer planId) throws Exception {
        if(status!= null && customerPayment != null){

            customerPayment.setOrderId(orderId);
            customerPayment.setStatus(status);
            CustomerPayment save = customerPaymentRepository.save(customerPayment);
            return save;
        }
        else if(status!= null && customerPayment == null){
            CustomerPayment customerPaymentwhileInitiate= new CustomerPayment();
            customerPaymentwhileInitiate.setOrderId(orderId);
            customerPaymentwhileInitiate.setStatus(status);
            Optional<Customers> customers = customersRepository.findById(customerPaymentRepository.findById(orderId).get().getCustId());

            Integer testMvno =  paytmPaymentController.getMvnoIdFromCurrentStaff(customers.get().getId());

            RecordPaymentRequestDTO recordPaymentDTO = new RecordPaymentRequestDTO();
            recordPaymentDTO.setPaymentAmount(customerPayment.getPayment());
            recordPaymentDTO.setBankName("Flutter Wave");
            recordPaymentDTO.setBranch(this.clientServiceSrv.getValueByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT));
            recordPaymentDTO.setCustId(customers.get().getId());
            recordPaymentDTO.setIsTdsDeducted(false);
            recordPaymentDTO.setPaymentDate(LocalDate.now());
            recordPaymentDTO.setPaymentMode(this.clientServiceSrv.getValueByName(ClientServiceConstant.PAYEMNT_GATEWAY));
            //transection id get from responce
            //recordPaymentDTO.setReferenceNo(String.valueOf(transactionId));
           // recordPaymentDTO.setRemarks("Transaction ID:-" + transactionId);
            recordPaymentDTO.setType("Payment");
            recordPaymentDTO.setMvnoId(testMvno);

            ChangePlanRequestDTO requestDTO = new ChangePlanRequestDTO();
            requestDTO.setPurchaseType(CommonConstants.PLAN_GROUP_RENEW);
            requestDTO.setPlanId(planId);
            requestDTO.setIsPaymentReceived(true);
            //transection id get from responce
           // requestDTO.setRemarks("Transaction ID:-" + transactionId);
            requestDTO.setIsAdvRenewal(false);
            requestDTO.setIsAdvRenewal(false);
            requestDTO.setCustId(customers.get().getId());
            requestDTO.setIsRefund(false);
            requestDTO.setRecordPaymentDTO(recordPaymentDTO);
            requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_RENEW);
            requestDTO.setAddonStartDate(null);
            requestDTO.setCreatedate(LocalDateTime.now());
            requestDTO.setCreatedById(customers.get().getId());
            requestDTO.setCreatedByName(customers.get().getCreatedByName());
            requestDTO.setLastModifiedById(customers.get().getId());
            requestDTO.setLastModifiedByName(customers.get().getLastModifiedByName());
            requestDTO.setUpdatedate(LocalDateTime.now());
            String number=String.valueOf(UtilsCommon.gen());
            CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers.get(), false, 0.0, "FLutter wave", null, number,null,null);
            return customerPaymentRepository.save(customerPaymentwhileInitiate);
        }
        else{
            throw new RuntimeException("Please add status and customerPayment");
        }

    }

    public Integer getMvnoIdFromCurrentStaff() {
        //TODO: Change once API work on live BSS server
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if(securityContext.getAuthentication().getPrincipal() != null)
                    mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
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



    @Transactional
    public String debitcompleted(String externaltransactionid , String transactionid,String status) throws Exception {
        String apiresponse = "start";
        try {
            List<GrantedAuthority> role_name = new ArrayList<>();
            role_name.add(new SimpleGrantedAuthority("ADMIN"));
            LoggedInUser user = new LoggedInUser("admin", "admin@123", true, true, true, true, role_name, "admin", "admin", LocalDateTime.now(), 2, 1, "ADMIN", null, 2, null, 2, new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),"admin",null,null,null);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            // TODO: pass mvnoID manually 6/5/2025
            Integer testMvno = getMvnoIdFromCurrentStaff(null);
            System.out.println("....,Mvno: " + testMvno);
            String result = "";
            Long orderId = Long.parseLong(externaltransactionid);
            Long transactionId = Long.parseLong(transactionid);
            QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
            CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(orderId)).and(qCustomerPayment.pgTransactionId.eq(transactionid))).orElse(null);
            if (Objects.isNull(customerPayment)) {
                apiresponse = "customernotfound";
                throw new RuntimeException("Customer not available for transaction id");

            }
            if (customerPayment.getStatus().equalsIgnoreCase("Initiate")) {
                apiresponse = "IntiatePayment";
                throw new RuntimeException("Payment is in intiate state");
            }
            if(customerPayment.getStatus().equalsIgnoreCase("Successful")){
                apiresponse = "paymentdonewithsuccess";
                throw new RuntimeException("Payment is already done with transaction id and external transaction id(order id)");
            }
            if (Objects.nonNull(customerPayment)) {
                if(customerPayment.getStatus().equalsIgnoreCase("pending")) {
                    if (status.equalsIgnoreCase("SUCCESSFUL")) {


//                        model.addAttribute("homeRedirectUrl", clientServiceSrv.getValueByName(ClientServiceConstant.HOME_REDIRECT_URL_CAPTIVE_PORTAL) + "?customerId=" + customerPayment.getCustId() + "&transactionId=" + customerPayment.getOrderId() + "&amount=" + customerPayment.getPayment() + "&currency=" + this.clientServiceSrv.getValueByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT));
                        Optional<Customers> customers = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                        CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findByCustId(customers.get().getId()).get(0);
                        RecordPaymentRequestDTO recordPaymentDTO = new RecordPaymentRequestDTO();
                        recordPaymentDTO.setPaymentAmount(customerPayment.getPayment());
                        recordPaymentDTO.setBankName("MTN");
                        recordPaymentDTO.setBranch(this.clientServiceSrv.getValueByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT));
                        recordPaymentDTO.setCustId(customers.get().getId());
                        recordPaymentDTO.setIsTdsDeducted(false);
                        recordPaymentDTO.setPaymentDate(LocalDate.now());
                        recordPaymentDTO.setPaymentMode(this.clientServiceSrv.getValueByName(ClientServiceConstant.PAYEMNT_GATEWAY));
                        recordPaymentDTO.setReferenceNo(String.valueOf(transactionId));
                        recordPaymentDTO.setRemarks("Transaction ID:-" + transactionId);
                        recordPaymentDTO.setType("Payment");
                        recordPaymentDTO.setMvnoId(testMvno);

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
                        requestDTO.setRemarks("Transaction ID:-" + transactionId);
                        requestDTO.setIsAdvRenewal(false);
                        requestDTO.setCustId(customers.get().getId());
                        requestDTO.setIsRefund(false);
                        requestDTO.setRecordPaymentDTO(recordPaymentDTO);
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_FROM_MTN);
                        requestDTO.setAddonStartDate(LocalDateTime.now());
                        requestDTO.setCreatedate(LocalDateTime.now());
                        requestDTO.setCreatedById(customers.get().getId());
                        requestDTO.setCreatedByName(customers.get().getCreatedByName());
                        requestDTO.setLastModifiedById(customers.get().getId());
                        requestDTO.setLastModifiedByName(customers.get().getLastModifiedByName());
                        requestDTO.setUpdatedate(LocalDateTime.now());
                        requestDTO.setCustServiceMappingId(customerServiceMapping.getId());
                        logger.info("Payment For user id " + requestDTO.getCustId() + " is Successfull :  request: { From : {}}; Response : {{}}", "MTN", APIConstants.SUCCESS);
//                        List<CustPlanMapppingPojo> custPlanMapppingPojoList = custPlanMappingService.findAllByCustomersId(customers.get().getId());
//                        if (custPlanMapppingPojoList.size() == 1) {
////                            customerPackageDTOS.get(0).getPlanId();
//                            if (planService.get(custPlanMapppingPojoList.get(0).getPlanId().intValue()).getOfferprice() == 0) {
//                                requestDTO.setAddonStartDate(LocalDate.now());
//                            }
//                        }
                        String number = String.valueOf(UtilsCommon.gen());

                        CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers.get(), false, 0.0, "FLutter wave", null, number,null,null);
                        CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                        Integer id = null;
                        try {
                            Customers customer = customersRepository.findById(basicDetailsPojo.getId()).get();
                            CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                            List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerIdAndPlanId(recordPaymentDTO.getCustId(), requestDTO.getPlanId());
                            custPlanMappping = custPlanMappping.stream().filter(custPlanMappping1 -> custPlanMappping1.getOfferPrice() > 0).collect(Collectors.toList());
                            Customers customers1 = customersRepository.getOne(recordPaymentDTO.getCustId());
                            List<CreditDocument> creditDocumentList = creditDocService.getAllByCustomer_IdOrderByIdDesc(customers.get().getId());
                            if (!creditDocumentList.isEmpty()) {
                                customersPojo.setCreditDocumentId(creditDocumentList.get(0).getId().toString());
                                customersPojo.setIsFromFlutterWave("true");
                                id = creditDocumentList.get(0).getId();
                            }
//                            Runnable chargeRunnable = new InvoiceCreationThread(customersPojo, customersService, null, false,null,CommonConstants.INVOICE_TYPE.RENEW);
//                            Thread billchargeThread = new Thread(chargeRunnable);
//                            billchargeThread.start();
                            AdditionalInformationDTO additionalInformationDTO = new AdditionalInformationDTO();
                            additionalInformationDTO.setTransactionNumber(customerPayment.getOrderId().toString());
                            if(postpaidPlan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_RENEW) || postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL)) {
                                debitDocService.createInvoice(customers1, Constants.RENEW, this.clientServiceSrv.getValueByName(ClientServiceConstant.PAYEMNT_GATEWAY) + "  " + recordPaymentDTO.getReferenceNo(), null, additionalInformationDTO, null, false,false,null,null,null);
                            }
                            else{
                                debitDocService.createInvoice(customers1, Constants.ADD_ON, this.clientServiceSrv.getValueByName(ClientServiceConstant.PAYEMNT_GATEWAY) + "  " + recordPaymentDTO.getReferenceNo(), null, additionalInformationDTO, null, false,false,null,null,null);
                            }

                        } catch (Exception e) {
                            logger.error("Payment Failed For Order Id " + orderId + " :  request: { From : {}}; Response : {{}};Error :{} ;", "MTN", APIConstants.FAIL);
                            logger.error("" + e.getMessage(), e);
                            e.printStackTrace();
                        }
                        if (customers.isPresent()) {
                            customersService.sendCustPaymentSuccessMessage("Payment Success", customers.get().getUsername(), customerPayment.getPayment(), "Online", customers.get().getMvnoId(), customers.get().getCountryCode(), customers.get().getMobile(), customers.get().getEmail(), customers.get().getId(), customerPayment.getPaymentDate().toString(), recordPaymentDTO.getReferenceNo(),customers.get().getBuId(),requestDTO.getPlanId(),(long) customersService.getLoggedInStaffId(),id);

                        }
                        customerPayment.setStatus("Success");
                        customerPayment.setPgTransactionId(transactionId.toString());
                        apiresponse = "Success";

                    }
                }
                else {
                    result = "Payment Failed";
                    customerPayment.setStatus(result);
                    customerPayment.setPgTransactionId(transactionId.toString());
                    logger.error("Payment Failed For Order Id " + orderId + " :  request: { From : {}}; Response : {{}};Error :{} ;", "MTN", APIConstants.FAIL);
                }
                customerPaymentRepository.save(customerPayment);

            }
            ClientService clientService = clientServiceSrv.getByName("redirectTimeInSeconds");
            Long redirectTimeInSeconds = 10L;
//                if (clientService != null && ValidateCrudTransactionData.validateLongTypeFieldValue(Long.valueOf(clientService.getValue())) && Long.valueOf(clientService.getValue()) > 0) {
//                    redirectTimeInSeconds = Long.valueOf(clientServiceSrv.getByName("redirectTimeInSeconds").getValue());
//                    model.addAttribute("result", result);
//                    model.addAttribute("parameters", mapping);
//                    model.addAttribute("redirectTimeInSeconds", redirectTimeInSeconds);
//                }

        } catch (Exception e) {
            logger.error("Payment Failed For Order Id  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "MTN", APIConstants.FAIL, e.getStackTrace());
            logger.info(e.getMessage());
        }
        return apiresponse;
    }

    public Page<CustomerPayment> findAllPaymentHistoryByCustomer(PaginationRequestDTO requestDTO){
        QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
        BooleanExpression booleanExpression = qCustomerPayment.isNotNull();
        if(requestDTO.getPage() > 0){
            requestDTO.setPage(requestDTO.getPage()-1);
        }
        if(requestDTO.getFilters().get(0).getFilterColumn().length() > 0){
            if(requestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("pending")){
                booleanExpression = booleanExpression.and(qCustomerPayment.status.equalsIgnoreCase("pending"));
            }
            if(requestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("Initiate")){
                booleanExpression = booleanExpression.and(qCustomerPayment.status.equalsIgnoreCase("Initiate"));
            }
            if(requestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("Successful")){
                booleanExpression = booleanExpression.and(qCustomerPayment.status.equalsIgnoreCase("Successful"));
            }
        }
        booleanExpression =booleanExpression.and(qCustomerPayment.custId.eq(Integer.parseInt(requestDTO.getFilters().get(0).getFilterValue())));
        Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getPageSize());
        Page<CustomerPayment> customerPayments =  customerPaymentRepository.findAll(booleanExpression ,pageable);
        return  customerPayments;
    }

    public Boolean IsTransactionStatusSuccess(String transactionid , String externaltransactionid){
        Boolean status = false;
        Long orderId = Long.parseLong(externaltransactionid);
        Long transactionId = Long.parseLong(transactionid);
        QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
        CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(orderId)).and(qCustomerPayment.pgTransactionId.eq(transactionid))).orElse(null);
        if(Objects.nonNull(customerPayment)){
            if(customerPayment.getStatus().equalsIgnoreCase("Success")){
                status = true;
            }
        }
        return status;
    }


    public void validateOcdRequest(MtnBuyPlanDTO mtnBuyPlanDTO){
        if(mtnBuyPlanDTO.getPlanId() == null){
            throw new CustomValidationException(2200,"Plan Id can't be empty",null);
        }
        if(mtnBuyPlanDTO.getPlanId() != null){
            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(mtnBuyPlanDTO.getPlanId()).orElse(null);
            if(postpaidPlan == null) {
                throw new CustomValidationException(2300, "Plan is not found by given Id", null);
            }
            else{
                if(postpaidPlan.getParam1() == null || postpaidPlan.getParam1().equalsIgnoreCase("") || postpaidPlan.getParam1().length() == 0){
                    throw new CustomValidationException(2400 , "Given plan does not have an offeringId. Please contact administrator",null);
                }
            }
        }
        if(mtnBuyPlanDTO.getMobileNumber() == null || mtnBuyPlanDTO.getMobileNumber().length() == 0 || mtnBuyPlanDTO.getMobileNumber().equalsIgnoreCase("")){
            throw new CustomValidationException(2500, "Mobile number can't be empty", null);
        }
    }

    public CustomerPayment intiatePayment(MtnBuyPlanDTO mtnBuyPlanDTO){
        PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(mtnBuyPlanDTO.getPlanId()).orElse(null);
        CustomerPaymentDto customerPaymentDto =  new CustomerPaymentDto();
        customerPaymentDto.setPlanId(mtnBuyPlanDTO.getPlanId().longValue());
        customerPaymentDto.setPayment(postpaidPlan.getOfferprice());
        customerPaymentDto.setCustId(null);
        customerPaymentDto.setStatus("Intiate");
        customerPaymentDto.setPgTransactionId(mtnBuyPlanDTO.getTransactionId());
        customerPaymentDto.setCustomerUsername(mtnBuyPlanDTO.getMobileNumber());
        customerPaymentDto.setMerchantName("USSD(OCS)");
        customerPaymentDto.setTransactionDate(LocalDateTime.now());
        CustomerPayment customerPayment =   setCustomerPayment(customerPaymentDto);
        return customerPayment;
    }

    public CustomerPayment setCustomerPayment(CustomerPaymentDto customerPaymentDto){
        CustomerPayment customerPayment = new CustomerPayment();
        if(customerPaymentDto.getOrderId() == null) {
            customerPayment.setOrderId(UtilsCommon.generateId(Long.parseLong(String.valueOf(UtilsCommon.gen()))));
        }
        else{
            customerPayment.setOrderId(Long.parseLong(customerPaymentDto.getOrderId()));
        }
        customerPayment.setStatus(customerPaymentDto.getStatus());
        customerPayment.setPayment(customerPaymentDto.getPayment());
        if(customerPaymentDto.getCustId() != null){
            customerPayment.setCustId(customerPaymentDto.getCustId());
        }
        customerPayment.setPlanId(Math.toIntExact(customerPaymentDto.getPlanId()));
        customerPayment.setPaymentDate(LocalDateTime.now());
        customerPayment.setIsFromCaptive(false);
        if(customerPaymentDto.getCustomerUsername() != null){
            customerPayment.setCustomerUsername(customerPaymentDto.getCustomerUsername());
        }
        if(customerPaymentDto.getMerchantName() != null){
            customerPayment.setMerchantName(customerPaymentDto.getMerchantName());
        }
        if(customerPaymentDto.getAccountNumber()!=null){
            customerPayment.setAccountNumber(customerPaymentDto.getAccountNumber());
        }
        if(customerPaymentDto.getPgTransactionId() != null){
            customerPayment.setPgTransactionId(customerPaymentDto.getPgTransactionId());
        }
        customerPayment.setTransactionDate(customerPaymentDto.getTransactionDate());
        customerPayment = customerPaymentRepository.save(customerPayment);
        return customerPayment;
    }

    public void SendOcsRequest(MtnBuyPlanDTO mtnBuyPlanDTO,CustomerPayment customerPayment) throws Exception {
        PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(mtnBuyPlanDTO.getPlanId()).orElse(null);
        String response = sendCustomerdebitRequestToUSSD(mtnBuyPlanDTO , customerPayment , postpaidPlan.getParam1());
        if(response.contains("Connection timed out")){
            customerPayment.setStatus("Failed");
            customerPaymentRepository.save(customerPayment);
            throw new CustomValidationException(2600,"OCS API CONNECTION TIMEOUT",null);
        }
        JSONObject jsonObject = new JSONObject(response);
        JSONObject dataArea = jsonObject.getJSONObject("dataArea");
        JSONObject changeOfferingResponse = dataArea.getJSONObject("changeOfferingResponse");
        int i = changeOfferingResponse.getInt("resultCode");
        if (i == 0) {
            createCustomerOrAddPlan(mtnBuyPlanDTO, customerPayment , postpaidPlan);
        }  else {
            customerPayment.setStatus("Failed");
            customerPaymentRepository.save(customerPayment);
            throw new CustomValidationException(2700,"OCS EXCEPETION: "+changeOfferingResponse.get("resultDesc").toString(),null);
        }

    }

    public String sendCustomerdebitRequestToUSSD(MtnBuyPlanDTO mtnBuyPlanDTO , CustomerPayment customerPayment,String offeringId){
        String response = "";
        String payloadwithurl = mtnPaymentRequestService.requestToGetMTNUSSDPAY(mtnBuyPlanDTO.getMobileNumber(),offeringId ,customerPayment.getOrderId().toString());
        String[] strings = payloadwithurl.split("\\|\\|");
        String url = strings[0];
        String payload = strings[1];
        response = sendRestAPIService.sendPatchRequest(payload , url);
        return  response;
    }

    public void createCustomerOrAddPlan(MtnBuyPlanDTO mtnBuyPlanDTO , CustomerPayment customerPayment ,PostpaidPlan newpostpaidPlan) throws Exception {
        String mobileNumber = mtnBuyPlanDTO.getMobileNumber();
        if (mobileNumber.startsWith("211") && mobileNumber.length() - 3 == 9) {
            mobileNumber =  mtnBuyPlanDTO.getMobileNumber().substring(3); // Remove the "211" prefix
        }
        // TODO: pass mvnoID manually 6/5/2025
        Customers customers = customersRepository.findByUsernameAndMvnoId(mobileNumber , getMvnoIdFromCurrentStaff(null)).orElse(null);
        customerPayment.setStatus("Success");
        if(newpostpaidPlan == null)
        {
            throw new RuntimeException("No plan found by given plan id");
        }
        if(customers != null){
            log.info("Customer with "+customers.getUsername()+" already created going to renew or addon");
            customerPayment.setCustId(customers.getId());
            renewOrAddonPlan(customers , newpostpaidPlan);
        }
        else{
            log.info("Customer with "+mtnBuyPlanDTO.getMobileNumber()+" is not found creating new customer");
            mobileNumber = mtnBuyPlanDTO.getMobileNumber();
            if (mobileNumber.startsWith("211") && mobileNumber.length() - 3 == 9) {
                mobileNumber =  mtnBuyPlanDTO.getMobileNumber().substring(3); // Remove the "211" prefix
            }
            CustomersPojo customersPojo = createMTNCustomers(mobileNumber , newpostpaidPlan);
            CustomersPojo newcustomersPojo = customersService.save(customersPojo, "bss", false);
            if(newcustomersPojo != null) {
                if(customersPojo.getPaymentDetails() != null){
                    newcustomersPojo.setPaymentDetails(customersPojo.getPaymentDetails());
                }
                customersService.sharedCustomerData(newcustomersPojo , false);
                CustomMessage customMessage = new CustomMessage(newcustomersPojo);
                kafkaMessageSender.send(new KafkaMessageData(customMessage, CustomMessage.class.getSimpleName()));
//                messageSender.send(customMessage, RabbitMqConstants.QUEUE_APIGW_CUSTOMER);
//                messageSender.send(customMessage, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_NOTIFICATION);
                custPlanMappingService.sendCustPlanMapping(newcustomersPojo.getId());
                customerPayment.setCustId(newcustomersPojo.getId());
                customersService.sendCustPaymentSuccessMessage("Payment Success", newcustomersPojo.getUsername(), newpostpaidPlan.getOfferprice(), "Online", newcustomersPojo.getMvnoId(), newcustomersPojo.getCountryCode(), newcustomersPojo.getMobile(), newcustomersPojo.getEmail(), newcustomersPojo.getId(), "3839323923", LocalDate.now().toString(),newcustomersPojo.getBuId(), newpostpaidPlan.getId(),(long) customersService.getLoggedInStaffId(),null);
            }
        }
        customerPaymentRepository.save(customerPayment);
    }

    public void renewOrAddonPlan(Customers customers , PostpaidPlan plan) throws Exception {
        log.info("request is created and now enter to function");
        ChangePlanRequestDTO changePlanRequestDTO = createAddPlanRequestDTO(customers , plan);
        String number = String.valueOf(UtilsCommon.gen());
        CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(changePlanRequestDTO, customers, false, 0.0, "OCS", null, number,null,null);
        CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
        Integer id = null;
        try {
            List<CreditDocument> creditDocumentList = creditDocService.getAllByCustomer_IdOrderByIdDesc(customers.getId());
            id = creditDocumentList.get(0).getId();
            if(plan.getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_RENEW) || plan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL)) {
                debitDocService.createInvoice(customers, Constants.RENEW, "OCS", null, null, null, false,false,null,null,null);
            }
            else{
                debitDocService.createInvoice(customers, Constants.ADD_ON, "OCS", null, null, null, false,false,null,null,null);
            }
            customersService.sendCustPaymentSuccessMessage("Payment Success", customers.getUsername(), plan.getOfferprice(), "Online", customers.getMvnoId(), customers.getCountryCode(), customers.getMobile(), customers.getEmail(), customers.getId(), "test2992299", LocalDate.now().toString(),customers.getBuId(),plan.getId(),customers.getCreatedById().longValue(),id);

        }catch (Exception e ){
            logger.error("exception:"+e);
        }
    }

    public CustomersPojo createMTNCustomers(String username,PostpaidPlan plan){
        log.info("come in mtn customer creation");
        // TODO: pass mvnoID manually 6/5/2025
        ClientService basePlanId = clientServiceSrv.getByNameAndMvnoIdEquals("BASE_PLAN_ID" , getMvnoIdFromCurrentStaff(null));
        if(basePlanId == null){
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR , "Please configure base plan in setting",null);
        }
        PostpaidPlan basePlan = postpaidPlanRepo.findById(Integer.parseInt(basePlanId.getValue())).orElse(null);
        PlanService basePlanService = planServiceRepository.findById(basePlan.getServiceId()).orElse(null);
        ServiceArea serviceArea = plan.getServiceAreaNameList().get(0);
        PlanService service = planServiceRepository.findById(plan.getServiceId()).orElse(null);
        List<BranchServiceAreaMapping> branchServiceAreaMapping = branchServiceAreaMappingRepository.findAllByServiceareaIdIn(plan.getServiceAreaNameList().stream().map(serviceArea1 -> Math.toIntExact(serviceArea1.getId())).collect(Collectors.toList()));
        CustomersPojo customers = new CustomersPojo();
        customers.setUsername(username);
        customers.setPassword(String.valueOf(UtilsCommon.gen6Digit()));
        customers.setBillableCustomerId(null);
        customers.setBillday(null);
        List<CustPlanMapppingPojo> planMappingList = new ArrayList<>();
        CustPlanMapppingPojo custPlanMapppingPojo =  new CustPlanMapppingPojo();
        custPlanMapppingPojo.setPlanId(plan.getId());
        custPlanMapppingPojo.setServiceId(plan.getServiceId());
        custPlanMapppingPojo.setBillTo("CUSTOMER");
        custPlanMapppingPojo.setService(service.getName());
        custPlanMapppingPojo.setValidity(1.0000);
        custPlanMapppingPojo.setNewAmount(null);
        custPlanMapppingPojo.setDiscount(0.0000);
        custPlanMapppingPojo.setOfferPrice(plan.getOfferprice());
        custPlanMapppingPojo.setIsInvoiceToOrg(false);
        custPlanMapppingPojo.setIstrialplan(null);
        custPlanMapppingPojo.setCustomer(customers);
        CustPlanMapppingPojo basePlanMappingPojo =  new CustPlanMapppingPojo();
        basePlanMappingPojo.setPlanId(basePlan.getId());
        basePlanMappingPojo.setServiceId(basePlan.getServiceId());
        basePlanMappingPojo.setBillTo("CUSTOMER");
        basePlanMappingPojo.setService(basePlanService.getName());
        basePlanMappingPojo.setValidity(1.0000);
        basePlanMappingPojo.setNewAmount(null);
        basePlanMappingPojo.setDiscount(0.0000);
        basePlanMappingPojo.setOfferPrice(basePlan.getOfferprice());
        basePlanMappingPojo.setIsInvoiceToOrg(false);
        basePlanMappingPojo.setIstrialplan(null);
        basePlanMappingPojo.setCustomer(customers);
        planMappingList.add(basePlanMappingPojo);
        planMappingList.add(custPlanMapppingPojo);
        customers.setPlanMappingList(planMappingList);
        customers.setCustMacMapppingList(new ArrayList<>());
        customers.setFramedIp("");
        customers.setFramedIpBind("");
        customers.setInvoiceType("");
        customers.setIpPoolNameBind("");
        customers.setIstrialplan(false);
        customers.setLatitude("");
        customers.setLongitude("");
        customers.setLocations(null);
        customers.setMasterdbid(null);
        customers.setMasterdbid(null);
        customers.setNasPort("");
        customers.setOverChargeList(new ArrayList<>());
        customers.setPlangroupid(null);
        customers.setSplitterid(null);
        customers.setStaffId(null);
        customers.setVoicesrvtype("");
        customers.setFirstname("CF");
        customers.setLastname("CF");
        customers.setCustname("CF");
        customers.setEmail("CF@gmail.com");
        customers.setTitle("Mr");
        customers.setPan("");
        customers.setGst("");
        customers.setAadhar("");
        customers.setPassportNo("");
        customers.setTinNo("");
        customers.setContactperson("FROM SMS");
        customers.setFailcount(0);
        customers.setCusttype("Prepaid");
        customers.setCustlabel("CUSTOMER");
        customers.setPhone("");
        customers.setMobile(username);
        customers.setAltemail("");
        customers.setFax("");
        customers.setCountryCode("+211");
        customers.setCustomerType("");
        customers.setCustomerSubType("");
        customers.setCustomerSubSector("");
        customers.setCustomerSector("");
        customers.setCafno("");
        customers.setVoicesrvtype("");
        customers.setDidno("");
        customers.setCalendarType("");
        customers.setPartnerid(1);
        customers.setSalesremark("");
        customers.setServicetype("");
        customers.setServiceareaid(serviceArea.getId());
        customers.setStatus("Active");
        customers.setLatitude("");
        customers.setLongitude("");
        customers.setIstrialplan(false);
        customers.setDiscount(0.0000);
        customers.setFlatAmount(0.0000);
        customers.setDiscountType("");
        CustomerAddressPojo customerAddressPojo =  new CustomerAddressPojo();
        customerAddressPojo.setAddressType("Present");
        customerAddressPojo.setLandmark("Test");
        customerAddressPojo.setAreaId(Math.toIntExact(serviceArea.getPincodeList().get(0).getAreaList().get(0).getId()));
        customerAddressPojo.setPincodeId(Math.toIntExact(serviceArea.getPincodeList().get(0).getId()));
        customerAddressPojo.setCityId(Math.toIntExact(serviceArea.getCityid()));
        customerAddressPojo.setStateId(serviceArea.getPincodeList().get(0).getStateId());
        customerAddressPojo.setCountryId(serviceArea.getPincodeList().get(0).getCountryId());
        customerAddressPojo.setFullAddress(serviceArea.getPincodeList().get(0).getAreaList().get(0).getName()+serviceArea.getPincodeList().get(0).getPincode());
        customerAddressPojo.setLandmark1("");
        customerAddressPojo.setVersion("NEW");
        customerAddressPojo.setCustomerId(null);
        customerAddressPojo.setCustomer(customers);
        List<CustomerAddressPojo> customerAddressPojos = new ArrayList<>();
        customerAddressPojos.add(customerAddressPojo);
        customers.setAddressList(customerAddressPojos);
        customers.setBranch(branchServiceAreaMapping.size() > 0 ? branchServiceAreaMapping.get(0).getBranchId() : 1L);
        customers.setIsCustCaf("no");
        customers.setDunningCategory("Silver");
        RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
        recordPaymentPojo.setAmount(plan.getOfferprice());
        List<PaymentListPojo> paymentListPojoList =  new ArrayList<>();
        PaymentListPojo paymentListPojo = new PaymentListPojo();
        paymentListPojo.setTdsAmountAgainstInvoice(0.0000);
        paymentListPojo.setAbbsAmountAgainstInvoice(0.0000);
        paymentListPojo.setAmountAgainstInvoice(plan.getOfferprice() + basePlan.getOfferprice());
        paymentListPojo.setInvoiceId(0);
        paymentListPojoList.add(paymentListPojo);
        recordPaymentPojo.setPaymentListPojos(paymentListPojoList);
        recordPaymentPojo.setReferenceno("88999999888");
        recordPaymentPojo.setChequedate(LocalDate.now());
        recordPaymentPojo.setIsAdjusted(true);
        recordPaymentPojo.setCustomerid(0);
        recordPaymentPojo.setPaymode("OCS");
        recordPaymentPojo.setRemark("Payment from OCS");
        recordPaymentPojo.setOnlinesource("");
        recordPaymentPojo.setType("Payment");
        customers.setPaymentDetails(recordPaymentPojo);
        customers.setValleyType("");
        customers.setCustomerArea("");
        customers.setCalendarType(CommonConstants.CAL_TYPE_ENGLISH);
        customers.setCreatedByName("Admin Admin");
        customers.setCreatedById(2);
        customers.setLast_password_change(LocalDateTime.now());
        log.info("customers "+customers);
        return customers;

    }

    public ChangePlanRequestDTO createAddPlanRequestDTO(Customers customers,PostpaidPlan plan){
        log.info("enter in create changeplanRequestDTO");
        PlanService service = planServiceRepository.findById(plan.getServiceId()).orElse(null);
        ChangePlanRequestDTO changePlanRequestDTO = new ChangePlanRequestDTO();
        changePlanRequestDTO.setCustId(customers.getId());
        changePlanRequestDTO.setPlanId(plan.getId());
        Integer custServiceMappingId = null;
        if(customers.getPlanMappingList().stream().filter(custPlanMappping -> custPlanMappping.getService().equalsIgnoreCase(service.getName())).collect(Collectors.toList()).size() > 0){
            custServiceMappingId = customers.getPlanMappingList().stream().filter(custPlanMappping -> custPlanMappping.getService().equalsIgnoreCase(service.getName())).collect(Collectors.toList()).get(0).getCustServiceMappingId();
        }
        if(plan.getPlanGroup().equalsIgnoreCase("Registration and Renewal") || plan.getPlanGroup().equalsIgnoreCase("Renew")){
            changePlanRequestDTO.setPurchaseType("Renew");
        }
        if (plan.getPlanGroup().equalsIgnoreCase("Volume Booster") || plan.getPlanGroup().equalsIgnoreCase("Bandwidth Booster"))  {
            changePlanRequestDTO.setPurchaseType("Addon");

        }
        if(custServiceMappingId == null) {
            CustomerServiceMapping mapping = new CustomerServiceMapping();
            mapping.setServiceId(Long.valueOf(plan.getServiceId()));
            mapping.setCustId(customers.getId());
            mapping.setStatus("Active");
//                    mapping = customersService.generateConnectionNumber(mapping);
            Boolean isLCO = customers.getLcoId() != null ? true : false;
            String connectionNo = numberSequenceUtil.getConnectionNumber(isLCO, customers.getLcoId(), customers.getMvnoId());
            mapping.setConnectionNo(connectionNo);
            CustomerServiceMapping savedCustomerServiceMapping = new CustomerServiceMapping();
            savedCustomerServiceMapping = customerServiceMappingRepository.save(mapping);
            custServiceMappingId = savedCustomerServiceMapping.getId();
        }
        changePlanRequestDTO.setCustServiceMappingId(custServiceMappingId);
        changePlanRequestDTO.setPaymentOwnerId(2);
        changePlanRequestDTO.setPaymentOwner("Admin");
        changePlanRequestDTO.setBillableCustomerId(customers.getId());
        changePlanRequestDTO.setAddonStartDate(null);
        changePlanRequestDTO.setIsAdvRenewal(false);
        changePlanRequestDTO.setIsRefund(false);
        changePlanRequestDTO.setIsParent(true);
        changePlanRequestDTO.setOnlinePurType("");
        RecordPaymentRequestDTO recordPaymentDTO = new RecordPaymentRequestDTO();
        recordPaymentDTO.setPaymentAmount(plan.getOfferprice());
        recordPaymentDTO.setBankName("OCS");
        recordPaymentDTO.setBranch(this.clientServiceSrv.getCurrencyByNameAadMvnoId(ClientServiceConstant.CURRENCY_FOR_PAYMENT,customers.getMvnoId()).getValue());
        recordPaymentDTO.setCustId(customers.getId());
        recordPaymentDTO.setIsTdsDeducted(false);
        recordPaymentDTO.setPaymentDate(LocalDate.now());
        recordPaymentDTO.setPaymentMode(this.clientServiceSrv.getCurrencyByNameAadMvnoId(ClientServiceConstant.PAYEMNT_GATEWAY,customers.getMvnoId()).getValue());
        recordPaymentDTO.setReferenceNo("test919191");
        recordPaymentDTO.setRemarks("Payment from ocs");
        recordPaymentDTO.setType("Payment");
        recordPaymentDTO.setMvnoId(getMvnoIdFromCurrentStaff(customers.getId()));
        changePlanRequestDTO.setRecordPaymentDTO(recordPaymentDTO);
        log.info("changePlanRequestDTO : "+changePlanRequestDTO);
        return changePlanRequestDTO;
    }

    public MtnUssdResponseDTO getPlanByServiceForMtnUssd(MtnPlanFetchDTO mtnPlanFetchDTO){
        MtnUssdResponseDTO mtnUssdResponseDTO = null;
        generateTokenForMtnUSSD(mtnPlanFetchDTO.getUsername() , mtnPlanFetchDTO.getPassword());
        // TODO: pass mvnoID manually 6/5/2025
        Integer testMvno = getMvnoIdFromCurrentStaff(null);
        log.info("Mvno: " + testMvno);
        PlanService planService = planServiceRepository.findAllByNameEqualsAndMvnoIdIn(mtnPlanFetchDTO.getService() , Arrays.asList(testMvno));
        if(planService != null) {
            log.info("The plan list search started with parameter service id: "+planService.getId()+" and planType: "+StatusConstants.ADDON_PLAN.VOLUME_BOOSTER+" and status is Active and is delete false and param1 is not null and param2 is not null");
            List<LightPostpaidPlanDTO> planDTOList = postpaidPlanRepo.findAllByService(Arrays.asList(planService.getId()), Arrays.asList(StatusConstants.ADDON_PLAN.VOLUME_BOOSTER));
            if(planDTOList != null && !planDTOList.isEmpty()){
                mtnUssdResponseDTO = convertPlanListToResponseDTO(planDTOList , mtnPlanFetchDTO.getTransactionId());
                return mtnUssdResponseDTO;
            }
            else{
                throw new CustomValidationException(1300 , "Plan list is found empty by given search parameter",null);
            }
        }
        else{
            throw new CustomValidationException(1200 ,"The service is not found given name",null);
        }
    }
    /**
     * This Function give plan list based on parameters
     * Be in mind that,
     * This USSD specific api doesn't work for common method
     * **/
    public MtnUssdResponseDTO convertPlanListToResponseDTO(List<LightPostpaidPlanDTO> planDTOList , String transactionId){
        MtnUssdResponseDTO mtnUssdResponseDTO = new MtnUssdResponseDTO();
        mtnUssdResponseDTO.setStatusCode("0000");
        mtnUssdResponseDTO.setStatusMessage("Success");
        mtnUssdResponseDTO.setTransactionId(transactionId);
        MtnUssdDataResponse mtnUssdDataResponse = new MtnUssdDataResponse();
        mtnUssdDataResponse.setUserInputRequired(true);
        mtnUssdDataResponse.setInboundResponse(convertPlanDTOToResponseString(planDTOList));
        mtnUssdResponseDTO.setData(mtnUssdDataResponse);
        log.info("MtnUssdResponseDTO : "+mtnUssdResponseDTO);
        return mtnUssdResponseDTO;
    }
    /**
     * This is just format data object to given string
     * **/
    public String convertPlanDTOToResponseString(List<LightPostpaidPlanDTO> planDTOList){
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < planDTOList.size(); i++) {
            formatted.append(planDTOList.get(i).getId())
                    .append(") ")
                    .append(planDTOList.get(i).getParam2());
            if (i < planDTOList.size() - 1) {
                formatted.append("\n");
            }

        }
        return formatted.toString();

    }

    public void generateTokenForMtnUSSD(String username , String password){
        StaffUser staffUser = staffUserRepository.findStaffUserByUsername(username);
        Long mvnoId = staffUser.getMvnoId().longValue();
        String MVNO_NAME = mvnoRepository.findMvnoNameById(mvnoId);
        List<GrantedAuthority> role_name=new ArrayList<>();
        role_name.add(new SimpleGrantedAuthority("ADMIN"));
        LoggedInUser user = new LoggedInUser(username, password, true, true, true, true, role_name, MVNO_NAME, MVNO_NAME, LocalDateTime.now(), staffUser.getId(), staffUser.getPartnerid(), "ADMIN", null, staffUser.getMvnoId(), null, staffUser.getId(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),MVNO_NAME,null,null,null);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }








    protected String getURL(){
        return sendurl.getCURL_URL();
    }

    protected  String getGroovyPath(){
        return sendurl.getGroovyFilPath();
    }



}
