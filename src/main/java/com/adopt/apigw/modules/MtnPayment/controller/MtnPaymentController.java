package com.adopt.apigw.modules.MtnPayment.controller;


import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.CustomerMapper;
import com.adopt.apigw.modules.MtnPayment.model.PaymentRequest;
import com.adopt.apigw.modules.MtnPayment.service.MTNPaymentRequestService;
import com.adopt.apigw.modules.MtnPayment.service.MtnPaymentService;

import com.adopt.apigw.modules.paymentGatewayMaster.service.PaymentGatewayService;
import com.adopt.apigw.modules.subscriber.model.ChangePlanRequestDTO;
import com.adopt.apigw.modules.subscriber.model.CustomChangePlanDTO;
import com.adopt.apigw.modules.subscriber.model.CustomersBasicDetailsPojo;
import com.adopt.apigw.modules.subscriber.model.RecordPaymentRequestDTO;
import com.adopt.apigw.modules.subscriber.service.InvoiceCreationThread;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.repository.common.CustomerPaymentRepository;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class MtnPaymentController {


    private static String PRIMARY_KEY = "f4f2da18c0db4033b897644dc8ef1fec";
    private static String CONTENT_TYPE = "application/json";
    private static String UUID;
    private int CREATION_RESPONSE_CODE;
    private String TARGET_ENVIRONMENT;
    // private static getUserApiKey getUserApiKey = new getUserApiKey();
    private String PHONE_NUMBER = "9265796638";
    private String AMOUNT = "10";
    private String API_KEY, USER_TOKEN, AUTH_KEY;
    public String tmp;

    private static final Logger logger = LoggerFactory.getLogger(MtnPaymentController.class);

    @Autowired
    private MtnPaymentService mtnPaymentService;

    @Autowired
    private MTNPaymentRequestService mtnPaymentRequestService;

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private CustomersRepository customersRepository;
    
    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private ClientServiceSrv clientServiceSrv;
    
    @Autowired
    private CustomersService customersService;
    
    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;
    
    @GetMapping("/getMtnResponceForUser")
    public Object getMtnResponce() throws IOException {
        try {
            UUID = java.util.UUID.randomUUID().toString();
            //  CustomerPayment customerPayment = mtnPaymentService.putUuidString(UUID);
            CREATION_RESPONSE_CODE = mtnPaymentService.getCreateUserApiResponce(PRIMARY_KEY, CONTENT_TYPE, UUID);
            if (CREATION_RESPONSE_CODE == 400) {
                return "INVALID_DATA";
            } else if (CREATION_RESPONSE_CODE == 500) {
                return "INTERNAL_SERVER_ERROR";
            } else if (CREATION_RESPONSE_CODE == 201 || CREATION_RESPONSE_CODE == 409) {
                TARGET_ENVIRONMENT = mtnPaymentService.getCreatedUserApiResponce(PRIMARY_KEY, CONTENT_TYPE, UUID);
                API_KEY = mtnPaymentService.getGeneratingApiKey(PRIMARY_KEY, CONTENT_TYPE, UUID);
                // AUTH_KEY = Base64.encodeToString((UUID.concat(":").concat(API_KEY)).getBytes(), Base64.NO_WRAP);
                AUTH_KEY = Base64.getEncoder().encodeToString((UUID.concat(":").concat(API_KEY)).getBytes());
                USER_TOKEN = mtnPaymentService.getGeneratingApiToken(PRIMARY_KEY, AUTH_KEY);
                if (!USER_TOKEN.equals("INTERNAL_SERVER_ERROR") && !USER_TOKEN.equals("ERROR") && !USER_TOKEN.equals("UnAuthorized")) {
                    mtnPaymentService.getRequestToPay(PRIMARY_KEY, CONTENT_TYPE, UUID, USER_TOKEN, TARGET_ENVIRONMENT, AMOUNT, PHONE_NUMBER);
                    tmp = mtnPaymentService.getStatus(PRIMARY_KEY, CONTENT_TYPE, TARGET_ENVIRONMENT, UUID, USER_TOKEN);
                    Response accountBalance = mtnPaymentService.getAccountBalance(PRIMARY_KEY, USER_TOKEN, TARGET_ENVIRONMENT, CONTENT_TYPE);
                    mtnPaymentService.CheckIfUserIsRegisteredAndActive(PRIMARY_KEY, USER_TOKEN, TARGET_ENVIRONMENT, CONTENT_TYPE, PHONE_NUMBER);


                    while (true) {

                        Thread.sleep(60000);
                        mtnPaymentService.getRequestToPay(PRIMARY_KEY, CONTENT_TYPE, UUID, USER_TOKEN, TARGET_ENVIRONMENT, AMOUNT, PHONE_NUMBER);
                        tmp = mtnPaymentService.getStatus(PRIMARY_KEY, CONTENT_TYPE, TARGET_ENVIRONMENT, UUID, USER_TOKEN);
                        mtnPaymentService.CheckIfUserIsRegisteredAndActive(PRIMARY_KEY, USER_TOKEN, TARGET_ENVIRONMENT, CONTENT_TYPE, PHONE_NUMBER);
                    }
                }
            }


        } catch (Exception e) {


        }

        return "done";
    }



    @PostMapping("/debit")
    public ResponseEntity<?> sendDebitRequest(@RequestBody PaymentRequest paymentRequest) throws Exception {
        try {
            // Convert the DebitRequest object to XML

            String jsonresponse = mtnPaymentRequestService.requestToGroovy(paymentRequest.getAmount() ,paymentRequest.getTransactionId() , paymentRequest.getCurrency() , paymentRequest.getFromfri());
            JSONObject object = new JSONObject(jsonresponse);
            String pgtransactionid = object.getJSONObject("ns8:debitresponse").get("transactionid").toString();
            String status = object.getJSONObject("ns8:debitresponse").get("status").toString();
            Long orderId = Long.valueOf(paymentRequest.getTransactionId());
            CustomerPayment customerPayment = customerPaymentRepository.findByOrderId(orderId);
            customerPayment.setPgTransactionId(pgtransactionid);
            customerPayment.setStatus(status);
            customerPaymentRepository.save(customerPayment);
            Integer RESP_CODE = APIConstants.SUCCESS;
            HashMap<String, Object> response = new HashMap<>();
            response.put("status" , status);
            response.put("externaltransactionId",pgtransactionid);
            response.put("orderid",orderId);
            return paymentGatewayService.apiResponse(RESP_CODE,response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing debit request.");
        }
    }

    @GetMapping("/getbalance")
    public ResponseEntity<?> sendDebitRequest(String fri) throws Exception {
        try {
            Integer RESP_CODE = APIConstants.SUCCESS;
            HashMap<String, Object> response = new HashMap<>();
            String jsonresponse = mtnPaymentRequestService.requestToGroovyForBalance(fri);
            JSONObject object = new JSONObject(jsonresponse);
            response.put("balanceresponse",object.getJSONObject("ns8:getbalanceresponse").getJSONObject("balance").get("amount"));
            return paymentGatewayService.apiResponse(RESP_CODE , response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing debit request.");
        }
    }


    @PostMapping("/getDebitCompletedRequestrest")
    public ResponseEntity<?> sendDebitCompletedRequest(@RequestBody String request) throws Exception {
        try {
            // Convert the DebitRequest object to XML
            JSONObject json = XML.toJSONObject(request);
            String jsonString = json.toString(4);
            ;
            Map<String, LinkedHashMap<String , Object>> mapData = new ObjectMapper().readValue(jsonString, HashMap.class);
            LinkedHashMap<String, Object> mapping = mapData.get("ns0:debitcompletedrequest");
//        String paytmChecksum = "";
            try {
                ApplicationLogger.logger.info("Data received as Response:-  " + mapping);

                List<GrantedAuthority> role_name=new ArrayList<>();
                role_name.add(new SimpleGrantedAuthority("ADMIN"));
                LoggedInUser user = new LoggedInUser("admin", "admin@123", true, true, true, true, role_name, "admin", "admin", LocalDateTime.now(), 2, 1, "ADMIN", null, 2, null, 2, new ArrayList<Long>(),false,new ArrayList<String>(), new ArrayList<Long>(),"adimin",new ArrayList<Long>(),null, null);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);// TODO: pass mvnoID manually 6/5/2025
                Integer testMvno = mtnPaymentService.getMvnoIdFromCurrentStaff(null);
                System.out.println("....,Mvno: "+testMvno);
                String result = "";
                Long orderId = Long.parseLong(mapping.get("externaltransactionid").toString());
                Long transactionId = Long.parseLong(mapping.get("transactionid").toString());
                QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
                CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(orderId))).orElse(null);
                if(Objects.isNull(customerPayment)){
                    throw new RuntimeException("Customer not available for transaction id");
                }
                if (Objects.nonNull(customerPayment)) {
                    if (mapping.containsKey("status")) {
                        result = mapping.get("status").toString();
                        if (mapping.get("status").equals("SUCCESSFUL")) {

                            
//                        model.addAttribute("homeRedirectUrl", clientServiceSrv.getValueByName(ClientServiceConstant.HOME_REDIRECT_URL_CAPTIVE_PORTAL) + "?customerId=" + customerPayment.getCustId() + "&transactionId=" + customerPayment.getOrderId() + "&amount=" + customerPayment.getPayment() + "&currency=" + this.clientServiceSrv.getValueByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT));
                            Optional<Customers> customers = customersRepository.findById(customerPaymentRepository.findById(orderId).get().getCustId());
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
                            requestDTO.setPurchaseType(CommonConstants.PLAN_GROUP_RENEW);
                            requestDTO.setPlanId(Math.toIntExact(customerPayment.getPlanId()));
                            requestDTO.setIsPaymentReceived(true);
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
                            logger.info("Payment For user id "+requestDTO.getCustId()+" is Successfull :  request: { From : {}}; Response : {{}}","MTN", APIConstants.SUCCESS);
//                        List<CustPlanMapppingPojo> custPlanMapppingPojoList = custPlanMappingService.findAllByCustomersId(customers.get().getId());
//                        if (custPlanMapppingPojoList.size() == 1) {
////                            customerPackageDTOS.get(0).getPlanId();
//                            if (planService.get(custPlanMapppingPojoList.get(0).getPlanId().intValue()).getOfferprice() == 0) {
//                                requestDTO.setAddonStartDate(LocalDate.now());
//                            }
//                        }
                            String number=String.valueOf(UtilsCommon.gen());
                            
                            CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers.get(), false, 0.0, "FLutter wave", null, number,null,null);
                            CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                            Integer id = null;
                            try {
                                Customers customer = customersRepository.findById(basicDetailsPojo.getId()).get();
                                CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                                List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerIdAndPlanId(recordPaymentDTO.getCustId() , requestDTO.getPlanId());
                                custPlanMappping = custPlanMappping.stream().filter(custPlanMappping1 -> custPlanMappping1.getOfferPrice() >0).collect(Collectors.toList());
                                Customers customers1 = customersRepository.getOne(recordPaymentDTO.getCustId());
                                List<CreditDocument> creditDocumentList = creditDocService.getAllByCustomer_IdOrderByIdDesc(customers.get().getId());
                                if(!creditDocumentList.isEmpty()) {
                                    customersPojo.setCreditDocumentId(creditDocumentList.get(0).getId().toString());
                                    customersPojo.setIsFromFlutterWave("true");
                                    id = creditDocumentList.get(0).getId();
                                }
                                Runnable chargeRunnable = new InvoiceCreationThread(customersPojo, customersService,null,false,null, CommonConstants.INVOICE_TYPE.RENEW);
                                Thread billchargeThread = new Thread(chargeRunnable);
                                billchargeThread.start();
                            } catch (Exception e) {
                                logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", "MTN",APIConstants.FAIL,request);
                                ApplicationLogger.logger.error("" + e.getMessage(), e);
                                e.printStackTrace();
                            }
                            if (customers.isPresent()) {
                                customersService.sendCustPaymentSuccessMessage("Payment Success", customers.get().getUsername(), customerPayment.getPayment(), "Online", customers.get().getMvnoId(), customers.get().getCountryCode(), customers.get().getMobile(), customers.get().getEmail(),customers.get().getId(),customerPayment.getPaymentDate().toString(), recordPaymentDTO.getReferenceNo(),customers.get().getBuId(),null, (long) customersService.getLoggedInStaffId(),id);

                            }
                            customerPayment.setStatus("Successful");
                            customerPayment.setPgTransactionId(transactionId.toString());
                        } else {
                            result = "Payment Failed";
                            customerPayment.setStatus(result);
                            customerPayment.setPgTransactionId(transactionId.toString());
                            logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", "MTN",APIConstants.FAIL,request);
                        }
                        customerPaymentRepository.save(customerPayment);
                    } else {
                        logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", "MTN",APIConstants.FAIL,request);
                        throw new RuntimeException("Status not present in payment");
                    }

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
                logger.error("Payment Failed For Order Id "+mapping.get("transaction_id")+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "MTN",APIConstants.FAIL,request,e.getStackTrace());
                ApplicationLogger.logger.info(e.getMessage());
            }
            Integer RESP_CODE = APIConstants.SUCCESS;
            HashMap<String, Object> response = new HashMap<>();
            response.put("hashmap",mapping);
            return paymentGatewayService.apiResponse(RESP_CODE,response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing debit request.");
        }
    }

//    @PostMapping("/mtn/registerCustomer")
//    @ApiModelProperty("An api for call ocd soap apis")
//    public ResponseEntity<?> createOrCheckMtnCustomer(@RequestBody OcdRequest ocdRequest) {
//        MDC.put("type", "crete");
//        String SUBMODULE = " [RegisterCustomer()] ";
//        HashMap<String, Object> response = new HashMap<>();
//        Integer RESP_CODE = APIConstants.FAIL;
//        try {
//            mtnPaymentService.validateOcdRequest(ocdRequest);
//            CustomerPayment customerPayment =  mtnPaymentService.intiatePayment(ocdRequest);
//            mtnPaymentService.SendOcsRequest(ocdRequest , customerPayment);
//            response.put("response" , "Customer has been successfully register");
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        } catch (Exception ex) {
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, ex.getMessage());
//        }
//        MDC.remove("type");
//        return paymentGatewayService.apiResponse(RESP_CODE, response);
//    }

//    @PostMapping("/debit1")
//    public ResponseEntity<String> sendDebitRequest1(@RequestBody DebitRequest debitRequest) throws Exception {
//        CustomerPayment initate = mtnPaymentService.putStatusString("initate", null, 1l, 1);
//        mtnPaymentService.putStatusString("su",initate,1l,1);
//        return null;
//    }
}
