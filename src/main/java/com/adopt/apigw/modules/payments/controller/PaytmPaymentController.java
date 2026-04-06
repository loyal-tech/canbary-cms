package com.adopt.apigw.modules.payments.controller;

import brave.Tracer;

import com.adopt.apigw.OnlinePaymentAudit.Service.OnlinePayAuditService;
import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Customers.SendCustomerPaymentDTO;
import com.adopt.apigw.modules.FlutterWaveHelper.FlutterWaveService;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerPayment;
import com.adopt.apigw.modules.PartnerLedger.domain.QPartnerPayment;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerPaymentRepository;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerDetailsService;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerService;
import com.adopt.apigw.modules.PaymentConfig.model.PaymentGatewayConfigurationConstant;
import com.adopt.apigw.modules.PaymentConfig.service.PaymentConfigService;
import com.adopt.apigw.modules.Voucher.module.APIResponseController;
import com.adopt.apigw.modules.paymentGatewayMaster.domain.PaymentGatewayResponse;
import com.adopt.apigw.modules.paymentGatewayMaster.service.PaymentGatewayService;
import com.adopt.apigw.modules.payments.service.PaymentService;
import com.adopt.apigw.modules.planUpdate.service.CustomerPackageService;
import com.adopt.apigw.modules.subscriber.model.*;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.AdditionalInformationDTO;
import com.adopt.apigw.pojo.PaymentListPojo;
import com.adopt.apigw.pojo.RecordPayment;
import com.adopt.apigw.pojo.api.CustomerPaymentDto;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.pojo.api.RecordPaymentPojo;
import com.adopt.apigw.pojo.api.SearchPaymentPojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.BudPayPaymentMessage;
import com.adopt.apigw.repository.common.CustomerPaymentRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CaptivePortalCustomerService;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.adopt.apigw.utils.ValidateCrudTransactionData;
import com.paytm.pg.merchant.PaytmChecksum;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/paytm")
public class PaytmPaymentController {
    private static String MODULE = " [PaytmPaymentController] ";
    @Autowired
    private CustomersService customersService;
    @Autowired
    private CreditDocRepository creditDocRepository;
    @Autowired
    private CreditDocService creditDocService;
    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private PartnerRepository entityRepository;

    @Autowired
    private PartnerLedgerService partnerLedgerService;

    @Autowired
    private PartnerLedgerDetailsService partnerLedgerDetailsService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private PartnerPaymentRepository partnerPaymentRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    private PostpaidPlanService planService;

    @Autowired
    CustPlanMappingService custPlanMappingService;

    @Autowired
    CustomerPackageService customerPackageService;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private FlutterWaveService flutterWaveService;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;


    @Autowired
    private PaymentConfigService paymentConfigService;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @Autowired
    private APIResponseController apiResponseController;

    @Autowired
    private Tracer tracer;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;


    @Autowired
    private OnlinePayAuditService  onlinePayAuditService;

    @Autowired
    private CaptivePortalCustomerService captivePortalCustomerService;



    private static final Logger logger = LoggerFactory.getLogger(PaytmPaymentController.class);
    @GetMapping("/")
    public String home(Model model) {
        ClientService clientService = clientServiceSrv.getByName("redirectTimeInSeconds");
        Long redirectTimeInSeconds = 10L;
        if (clientService != null && ValidateCrudTransactionData.validateLongTypeFieldValue(Long.valueOf(clientService.getValue())) && Long.valueOf(clientService.getValue()) > 0)
            redirectTimeInSeconds = Long.valueOf(clientServiceSrv.getByName("redirectTimeInSeconds").getValue());
        String homeRedirectCustomerUrl = clientServiceSrv.getValueByName(ClientServiceConstant.HOME_REDIRECT_CUSTOMER_URL);
        model.addAttribute("redirectTimeInSeconds", redirectTimeInSeconds);
        model.addAttribute("homeRedirectUrl", homeRedirectCustomerUrl);
        return "Home";
    }

    @PostMapping(value = "/pgresponse")
    public String getResponseRedirect(HttpServletRequest request, Model model) {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info("LinkResponse API Hit");

        Map<String, String[]> mapData = request.getParameterMap();
        TreeMap<String, String> parameters = new TreeMap<String, String>();
        String paytmChecksum = "";
        for (Entry<String, String[]> requestParamsEntry : mapData.entrySet()) {
            if ("CHECKSUMHASH".equalsIgnoreCase(requestParamsEntry.getKey())) {
                paytmChecksum = requestParamsEntry.getValue()[0];
            } else {
                parameters.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
            }
        }
        ApplicationLogger.logger.info("Data received as Response:-  " + parameters);
        String result = "";
        Boolean flag = false;
        Long orderId = Long.parseLong(parameters.get("ORDERID"));
        QPartnerPayment partnerPayment = QPartnerPayment.partnerPayment;
        BooleanExpression expression = partnerPayment.isNotNull();
        expression = expression.and(partnerPayment.orderid.eq(orderId.toString()));
        Optional<PartnerPayment> payment = partnerPaymentRepository.findOne(expression);
        if (payment.isPresent()) {
            try {
                partnerService.addBalance(orderId.toString(), parameters.get("RESPCODE").equals("01"), parameters.get("TXNAMOUNT"), parameters.get("TXNID"));
                flag = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Long customerOrderId = Long.parseLong(parameters.get("ORDERID"));
            QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
            CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(customerOrderId))).get();
            if(customerPayment.getIsFromCaptive() != null){
                if(customerPayment.getIsFromCaptive() == true) {
                    flag = true;
                }
            }
            RecordPayment recordPayment = new RecordPayment();
            recordPayment.setAmount(Double.parseDouble(parameters.get("TXNAMOUNT")));
            recordPayment.setPaymentdate(LocalDate.now());
            recordPayment.setBank("PayTM");
            recordPayment.setCustomerid(customerPaymentRepository.findByOrderId(orderId).getCustId().toString());
            recordPayment.setPaymode(UtilsCommon.PAYMENT_MODE_ONLINE);
            recordPayment.setRemark("Payment processed through PayTM");

            customersService.addTransactionId(orderId, parameters.get("TXNID"));
            if (customerPaymentRepository.findByOrderId(orderId).getLinkId() != null)
                customersService.expireLink(customerPaymentRepository.findByOrderId(orderId).getLinkId());

            SearchPaymentPojo searchPaymentPojo = new SearchPaymentPojo();
            searchPaymentPojo.setRemarks("Payment processed through PayTM");
            Optional<Customers> customers = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
            boolean isValideChecksum = false;
            try {
                HashMap<String , String> getPaymentGatewayParemeterforchecksum = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PAYTM , customers.get().getMvnoId());
                isValideChecksum = validateCheckSum(parameters, paytmChecksum , getPaymentGatewayParemeterforchecksum);
                if (isValideChecksum && parameters.containsKey("RESPCODE")) {
                    if (parameters.get("RESPCODE").equals("01")) {
                        result = "Payment Successful";
                        Customers customersformvno = customersRepository.findById(customerPayment.getCustId()).get();
                        /**Payment Gateway parameter started**/
                        HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PAYTM , customers.get().getMvnoId());
                        String PAYMENT_GATEWAY_NAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.PAYTM.PAYMENT_GATEWAY_NAME);
                        String STAFFUSER_USERNAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.PAYTM.STAFFUSER_USERNAME);
                        String STAFFUSER_PASSWORD = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.PAYTM.STAFFUSER_PASSWORD);
                        StaffUser staffUser = staffUserRepository.findStaffUserByUsername(STAFFUSER_USERNAME);
                        Long mvnoId = staffUser.getMvnoId().longValue();
                        String MVNO_NAME = mvnoRepository.findMvnoNameById(mvnoId);

                        //send sms from here
                        List<GrantedAuthority> role_name = new ArrayList<>();
                        role_name.add(new SimpleGrantedAuthority("ADMIN"));
                        LoggedInUser user = new LoggedInUser(STAFFUSER_USERNAME, STAFFUSER_PASSWORD, true, true, true, true, role_name, MVNO_NAME, MVNO_NAME, LocalDateTime.now(), customers.get().getCreatedById(), customers.get().getPartner().getId(), "ADMIN", null, customers.get().getMvnoId(), null, customers.get().getCreatedById(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),MVNO_NAME,null,null,null);
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findByCustId(customers.get().getId()).get(0);
                        RecordPaymentRequestDTO recordPaymentDTO = new RecordPaymentRequestDTO();
                        recordPaymentDTO.setPaymentAmount(customerPayment.getPayment());
                        recordPaymentDTO.setBankName("Paytm");
                        recordPaymentDTO.setBranch(this.clientServiceSrv.getCurrencyByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT).getValue());
                        recordPaymentDTO.setCustId(customers.get().getId());
                        recordPaymentDTO.setIsTdsDeducted(false);
                        recordPaymentDTO.setPaymentDate(LocalDate.now());
                        recordPaymentDTO.setPaymentMode(this.clientServiceSrv.getCurrencyByName(ClientServiceConstant.PAYEMNT_GATEWAY).getValue());
                        recordPaymentDTO.setReferenceNo(parameters.get("TXNID"));
                        recordPaymentDTO.setRemarks(parameters.get("TXNID"));
                        recordPaymentDTO.setType("Payment");
                        recordPaymentDTO.setMvnoId(customers.get().getMvnoId());

                        RecordPaymentPojo recordPaymentPojo
                                =  new RecordPaymentPojo();
                        recordPaymentPojo.setAmount(customerPayment.getPayment());
                        recordPaymentPojo.setBank("");
                        recordPaymentPojo.setChequedate(LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                        recordPaymentPojo.setCustomerid(customerPayment.getCustId());
                        recordPaymentPojo.setPaymode("ONLINE");
                        recordPaymentPojo.setReferenceno(customerPayment.getOrderId().toString());
                        recordPaymentPojo.setRemark("Payment through Paytm");
                        recordPaymentPojo.setReciptNo(customerPayment.getOrderId().toString());
                        recordPaymentPojo.setType("Payment");
                        recordPaymentPojo.setPaytype("");
                        recordPaymentPojo.setTdsAmount(0.0000);
                        recordPaymentPojo.setAbbsAmount(0.0000);
                        recordPaymentPojo.setInvoiceId(Collections.singletonList(0));
                        recordPaymentPojo.setOnlinesource("E_PAY");
                        PaymentListPojo paymentListPojo = new PaymentListPojo();
                        paymentListPojo.setInvoiceId(0);
                        paymentListPojo.setAmountAgainstInvoice(customerPayment.getPayment());
                        paymentListPojo.setTdsAmountAgainstInvoice(0.0000);
                        paymentListPojo.setAbbsAmountAgainstInvoice(0.0000);
                        List<PaymentListPojo> paymentListPojos =  new ArrayList<>();
                        paymentListPojos.add(paymentListPojo);
                        recordPaymentPojo.setPaymentListPojos(paymentListPojos);



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
                        requestDTO.setRemarks("Transaction ID:-" +parameters.get("TXNID"));
                        requestDTO.setIsAdvRenewal(false);
                        requestDTO.setCustId(customers.get().getId());
                        requestDTO.setIsRefund(false);
                        requestDTO.setRecordPaymentDTO(recordPaymentDTO);
                        requestDTO.setOnlinePurType("PAYTM");
                        requestDTO.setAddonStartDate(null);
                        requestDTO.setCreatedate(LocalDateTime.now());
                        requestDTO.setCreatedById(customers.get().getId());
                        requestDTO.setCreatedByName(customers.get().getCreatedByName());
                        requestDTO.setLastModifiedById(customers.get().getId());
                        requestDTO.setLastModifiedByName(customers.get().getLastModifiedByName());
                        requestDTO.setUpdatedate(LocalDateTime.now());
                        requestDTO.setPaymentOwnerId(2);
                        requestDTO.setIsParent(true);
                        requestDTO.setCustServiceMappingId(customerServiceMapping.getId());
                        logger.info("Payment For user id " + requestDTO.getCustId() + " is Successfull :  request: { From : {}}; Response : {{}}", "MTN", APIConstants.SUCCESS);
                        String number = String.valueOf(UtilsCommon.gen());

                        CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers.get(), false, 0.0, "FLutter wave", null, number,null,null);
                        CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                        try {
                            Customers customer = customersService.get(basicDetailsPojo.getId(),mvnoId.intValue());
                            CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                            Customers customers1 = customersRepository.getOne(recordPaymentDTO.getCustId());
                            customersService.sendCustPaymentSuccessMessage("Payment Success", customers.get().getUsername(), customerPayment.getPayment(), "Online", customers.get().getMvnoId(), customers.get().getCountryCode(), customers.get().getMobile(), customers.get().getEmail(), customers.get().getId(), customerPayment.getPaymentDate().toString(), recordPaymentDTO.getReferenceNo(),customers.get().getBuId(),requestDTO.getPlanId(),(long) customersService.getLoggedInStaffId(),customerPayment.getCreditDocumentId());
                            List<CreditDocument> creditDocumentList = creditDocService.getAllByCustomer_IdOrderByIdDesc(customers.get().getId());
                            if (!creditDocumentList.isEmpty()) {
                                customersPojo.setCreditDocumentId(creditDocumentList.get(0).getId().toString());
                                customersPojo.setIsFromFlutterWave("true");
                            }
                            AdditionalInformationDTO additionalInformationDTO =  new AdditionalInformationDTO();
                            String transactionNumber = "";
                            if(parameters.get("TXNID") != null && !parameters.get("TXNID").equalsIgnoreCase("")){
                                transactionNumber  = parameters.get("TXNID");
                            }
                            else{
                                transactionNumber = customerPayment.getOrderId().toString();
                            }
                            additionalInformationDTO.setTransactionNumber(String.valueOf(orderId));
                            debitDocService.createInvoice(customers1, Constants.RENEW, "PAYTM", null,additionalInformationDTO, null,false,false,null,null,null);
                            customerPayment.setPgTransactionId(parameters.get("TXNID"));
                            customerPayment.setStatus("Success");
                            customerPaymentRepository.save(customerPayment);
                        }catch (Exception e ){
                            logger.error("exception:"+e);
                            customerPayment.setStatus("Failed");
                            customerPaymentRepository.save(customerPayment);
                        }
                    }
                    else {
                        result = "Payment Failed";
                        customersService.sendCustPaymentFailedMessage("Payment Failed", customers.get().getUsername(), customerPayment.getPayment(), "Online", customers.get().getMvnoId(), customers.get().getCountryCode(), customers.get().getMobile(), customers.get().getEmail(), customers.get().getId(), customerPayment.getPaymentDate().toString(), parameters.get("ORDERID"),customers.get().getBuId(),customerPayment.getPlanId());
                        logger.error("Payment Failed For Order Id "+customerPaymentRepository.findById(orderId).get().getOrderId()+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                        customersService.updatePaymentStatus(orderId, "Failure");
                        customerPayment.setStatus("Failed");
                        customerPaymentRepository.save(customerPayment);
                    }
                } else {
                    result = "Checksum mismatched";
                }
            } catch (Exception e) {
                result = e.toString();
            }
        }


        QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
        Optional<CustomerPayment> customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(Long.parseLong(parameters.get("ORDERID")))));
        Customers customers = customersRepository.findById(customerPayment.get().getCustId()).get();
        /**Payment Gateway parameter started**/
        HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.PAYTM , customers.getMvnoId());
        String PAYMENT_GATEWAY_NAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.PAYTM.PAYMENT_GATEWAY_NAME);
        String REDIRECT_CAPTIVE_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.PAYTM.REDIRECT_CAPTIVE_URL);
        String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.PAYTM.REDIRECT_CWSC_URL);
        String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.PAYTM.REDIRECT_TIME_IN_SECONDS);
        /**Payment Gateway parameter ended**/
        model.addAttribute("result", result);
        parameters.remove("CHECKSUMHASH");
        model.addAttribute("parameters", parameters);
        model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
        if (flag) {
            String url = REDIRECT_CAPTIVE_URL;
            if(customerPayment.isPresent()) {
                logger.info("customer is found in redirect");
                url = url.replace("{userName}", customers.getUsername());
                url = url.replace("{Password}",customers.getPassword());
            }
            model.addAttribute("homeRedirectUrl", url);
        }
        else
            model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
        MDC.remove("type");
        return "report";
    }

    private boolean validateCheckSum(TreeMap<String, String> parameters, String paytmChecksum , HashMap<String ,String> paymentGatewayParameter) throws Exception {
        return PaytmChecksum.verifySignature(parameters,
                paymentGatewayParameter.get(PaymentGatewayConfigurationConstant.PAYTM.PAYTM_MERCHANT_KEY), paytmChecksum);
    }


    @PostMapping(value = "/linkResponse")
    public void getResponseFromLink(HttpServletRequest request,@RequestParam("mvnoId") Integer mvnoId) {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info("LinkResponse API Hit");
        Map<String, String[]> mapData = request.getParameterMap();
        TreeMap<String, String> parameters = new TreeMap<String, String>();
        String paytmChecksum = "";
        for (Entry<String, String[]> requestParamsEntry : mapData.entrySet()) {
            if ("CHECKSUMHASH".equalsIgnoreCase(requestParamsEntry.getKey())) {
                paytmChecksum = requestParamsEntry.getValue()[0];
            } else {
                parameters.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
            }
        }

        String result;
        Long orderId = null;
        RecordPayment recordPayment = new RecordPayment();
        recordPayment.setAmount(Double.parseDouble(parameters.get("TXNAMOUNT")));
        recordPayment.setPaymentdate(LocalDate.now());
        recordPayment.setBank("PayTM");
        String linkId = parameters.get("MERC_UNQ_REF").toString().substring(3);
        QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
        BooleanExpression expression = qCustomerPayment.isNotNull();
        expression = expression.and(qCustomerPayment.linkId.eq(linkId));
        Optional<CustomerPayment> payment = customerPaymentRepository.findOne(expression);
        if (payment.isPresent())
            orderId = payment.get().getOrderId();
        if (customerPaymentRepository.findById(orderId).get().getLinkId() != null)
            customersService.expireLink(customerPaymentRepository.findById(orderId).get().getLinkId());

        recordPayment.setCustomerid(customerPaymentRepository.findById(orderId).get().getCustId().toString());
        recordPayment.setPaymode(UtilsCommon.PAYMENT_MODE_ONLINE);
        recordPayment.setRemark("Payment processed through PayTM");
        customersService.addTransactionId(orderId, parameters.get("TXNID"));

        SearchPaymentPojo searchPaymentPojo = new SearchPaymentPojo();
        searchPaymentPojo.setRemarks("Payment processed through PayTM");
        boolean isValideChecksum = false;
        try {
            isValideChecksum = validateCheckSum(parameters, paytmChecksum , new HashMap<String,String>());
            if (isValideChecksum && parameters.containsKey("RESPCODE")) {
                if (parameters.get("RESPCODE").equals("01")) {
                    result = "Payment Successful";
                    logger.info("Get Responce from link for user "+recordPayment.getCustomer().getUsername()+" is Successfull :  request: { From : {}}; Response : {{}}", request.getHeader("requestFrom"), APIConstants.SUCCESS);
                    ApplicationLogger.logger.info("Payment Successfully done for orderId " + orderId);

                    CreditDocument doc = creditDocService.covertPaymentReqToCreditDoc(recordPayment,mvnoId);
                    doc.setReferenceno(parameters.get("TXNID"));
                    CreditDocument document = creditDocRepository.save(doc);
                    searchPaymentPojo.setIdlist(document.getId().toString());
                    creditDocService.approvePayment(searchPaymentPojo,mvnoId);
                    customersService.updatePaymentStatus(orderId, "Success");
                } else {
                    result = "Payment Failed";
                    ApplicationLogger.logger.error("Payment Failed for orderId " + orderId);
                    logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                    customersService.updatePaymentStatus(orderId, "Failure");
                }
            } else {
                result = "Checksum mismatched";
                ApplicationLogger.logger.error(result);
                logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
            }
        } catch (Exception e) {
            result = e.toString();
            ApplicationLogger.logger.error(result);
            logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", request.getHeader("requestFrom"),APIConstants.FAIL,request,e.getStackTrace());
        }
        MDC.remove("type");
    }

    @GetMapping(value = "/fwResponse")
    @Transactional
    public String getfwResponse(HttpServletRequest request, Model model) throws Exception {
        MDC.put("type", "Fetch");
        ApplicationLogger.logger.info("LinkResponse API Hit");
        Map<String, String[]> mapData = request.getParameterMap();
        TreeMap<String, String> parameters = new TreeMap<String, String>();
//        String paytmChecksum = "";
        for (Map.Entry<String, String[]> requestParamsEntry : mapData.entrySet()) {
            parameters.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
        }
        try {
            ApplicationLogger.logger.info("Data received as Response:-  " + parameters);
            String result = "";
            Boolean flag = true;
            Long orderId = Long.parseLong(parameters.get("tx_ref"));
            QPartnerPayment partnerPayment = QPartnerPayment.partnerPayment;
            QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
            BooleanExpression expression = partnerPayment.isNotNull();
            expression = expression.and(partnerPayment.orderid.eq(orderId.toString()));
            Optional<PartnerPayment> payment = partnerPaymentRepository.findOne(expression);
            CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(orderId))).orElse(null);
            if (Objects.nonNull(customerPayment)) {

                /**Payment Gateway parameter started**/
                Optional<Customers> customersformvnoId = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.FLUTTERWAVE , customersformvnoId.get().getMvnoId());
                String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.CWSC_REDIRECT_URL);
                String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.REDIRECT_TIME_IN_SECONDS);
                String FLUTTERWAVE_SECRET_KEY = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.FLUTTERWAVE_SECRET_KEY);
                String STAFFUSER_USERNAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.STAFFUSER_USERNAME);
                String STAFFUSER_PASSWORD = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.STAFFUSER_PASSWORD);
                StaffUser staffUser = staffUserRepository.findStaffUserByUsername(STAFFUSER_USERNAME);
                Long mvnoId = staffUser.getMvnoId().longValue();
                String MVNO_NAME = mvnoRepository.findMvnoNameById(mvnoId);
                List<GrantedAuthority> role_name=new ArrayList<>();
                role_name.add(new SimpleGrantedAuthority("ADMIN"));
                LoggedInUser user = new LoggedInUser(STAFFUSER_USERNAME, STAFFUSER_PASSWORD, true, true, true, true, role_name, MVNO_NAME, MVNO_NAME, LocalDateTime.now(), customersformvnoId.get().getCreatedById(), customersformvnoId.get().getPartner().getId(), "ADMIN", null, customersformvnoId.get().getMvnoId(), null, customersformvnoId.get().getCreatedById(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),MVNO_NAME,null,null,null);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);
                /**Payment Gateway parameter ended**/
                if (payment.isPresent()) {
                    try {
                        partnerService.addBalance(orderId.toString(), parameters.get("status").equals("successful"), customerPayment.getPayment().toString(), parameters.get("transaction_id"));
                        flag = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (parameters.containsKey("status")) {
                        result = parameters.get("status");
                        if (parameters.get("status").equals("successful")) {
                       //     logger.info("Payment For user "+recordPayment.getCustomer().getUsername()+" is Successfull :  request: { From : {}}; Response : {{}}", request.getHeader("requestFrom"), APIConstants.SUCCESS);
                            //send sms from here
                            Optional<Customers> customers = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                            if (customers.isPresent()) {
                                subscriberService.validateTransaction(parameters.get("transaction_id").toString() , orderId);
                                flutterWaveService.validateTransactionFromFlutterWaveWithPaymentGateway(orderId.toString() , parameters.get("transaction_id").toString(),FLUTTERWAVE_SECRET_KEY);
                                customers.get().setStatus("Active");
                                customersRepository.save(customers.get());
                                SendCustomerPaymentDTO sendCustomerPaymentDTO =  new SendCustomerPaymentDTO();
                                sendCustomerPaymentDTO.setCustId(customers.get().getId());
                                sendCustomerPaymentDTO.setAmount(customerPayment.getPayment());
                                customerPayment.setStatus(result);
                                customerPayment.setPgTransactionId(parameters.get("transaction_id").toString());
                                debitDocService.adjustCustomerLedgerPayment(sendCustomerPaymentDTO);
                                customersService.sendCustPaymentSuccessMessage("Payment Success", customers.get().getUsername(), customerPayment.getPayment(), "Online", customers.get().getMvnoId(), customers.get().getCountryCode(), customers.get().getMobile(), customers.get().getEmail(), customers.get().getId(), customerPayment.getOrderId().toString(),LocalDate.now().toString(),customers.get().getBuId(),null,(long) customersService.getLoggedInStaffId(),customerPayment.getCreditDocumentId());
                            }
                        } else {
                            result = "Payment Failed";
                            customerPayment.setStatus(result);
                            customerPayment.setPgTransactionId(parameters.get("transaction_id"));
                            logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                        }
                        customerPaymentRepository.save(customerPayment);
                    } else {
                        logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                        throw new RuntimeException("Status not present in payment");
                    }

                }
                model.addAttribute("result", result);
//        parameters.remove("CHECKSUMHASH");
                model.addAttribute("parameters", parameters);
                model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
                if (flag)
                    model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
                else
                    model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
            }
        } catch (Exception e) {
            logger.error("Payment Failed For With Exception :  request: { From : {}}; Response : {{}};Error :{} ;Exception: {}", request.getHeader("requestFrom"),APIConstants.FAIL,request,e.getStackTrace());
            ApplicationLogger.logger.info(e.getMessage());
        }
        MDC.remove("type");
        return "report";
    }

    @GetMapping(value = "/fwResponse/changePlan")
    @Transactional
    public String getfwResponseAndChangePlan(HttpServletRequest request, Model model) throws Exception {
        ApplicationLogger.logger.info("LinkResponse API Hit");
        MDC.put("type", "Fetch");
        Map<String, String[]> mapData = request.getParameterMap();
        TreeMap<String, String> parameters = new TreeMap<String, String>();
//        String paytmChecksum = "";
        for (Map.Entry<String, String[]> requestParamsEntry : mapData.entrySet()) {
            parameters.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
        }
        try {
            ApplicationLogger.logger.info("Data received as Response:-  " + parameters);

            String result = "";
            Long orderId = Long.parseLong(parameters.get("tx_ref"));
            Long transactionId = Long.parseLong(parameters.get("transaction_id"));
            QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
            CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(orderId))).orElse(null);
            Long redirectTimeInSeconds = 10L;
            if(Objects.isNull(customerPayment)){
                throw new RuntimeException("Customer not available for transaction id");
            }
            if (Objects.nonNull(customerPayment)) {
                /**Payment Gateway parameter started**/
                Optional<Customers> customersformvnoId = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.FLUTTERWAVE , customersformvnoId.get().getMvnoId());
                String PAYMENT_GATEWAY_NAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.PAYMENT_GATEWAY_NAME);
                String REDIRECT_CAPTIVE_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.CAPTIVE_REDIRECT_URL);
                String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.CWSC_REDIRECT_URL);
                String FLUTTERWAVE_SECRET_KEY = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.FLUTTERWAVE_SECRET_KEY);
                String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.REDIRECT_TIME_IN_SECONDS);
                String STAFFUSER_USERNAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.STAFFUSER_USERNAME);
                String STAFFUSER_PASSWORD = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.STAFFUSER_PASSWORD);
                StaffUser staffUser = staffUserRepository.findStaffUserByUsername(STAFFUSER_USERNAME);
                Long mvnoId = staffUser.getMvnoId().longValue();
                String MVNO_NAME = mvnoRepository.findMvnoNameById(mvnoId);
                redirectTimeInSeconds = Long.valueOf(REDIRECT_TIME_IN_SECONDS);
                List<GrantedAuthority> role_name=new ArrayList<>();
                role_name.add(new SimpleGrantedAuthority("ADMIN"));
                LoggedInUser user = new LoggedInUser(STAFFUSER_USERNAME, STAFFUSER_PASSWORD, true, true, true, true, role_name, MVNO_NAME, MVNO_NAME, LocalDateTime.now(), customersformvnoId.get().getCreatedById(), customersformvnoId.get().getPartner().getId(), "ADMIN", null, customersformvnoId.get().getMvnoId(), null, customersformvnoId.get().getCreatedById(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),MVNO_NAME,null,null,null);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(auth);
                /**Payment Gateway parameter ended**/
                if (parameters.containsKey("status")) {
                    result = parameters.get("status");
                    if (parameters.get("status").equals("successful")) {

                        subscriberService.validateTransaction(transactionId.toString() , orderId);
                        flutterWaveService.validateTransactionFromFlutterWaveWithPaymentGateway(orderId.toString() , transactionId.toString() , FLUTTERWAVE_SECRET_KEY);
//                        model.addAttribute("homeRedirectUrl", clientServiceSrv.getValueByName(ClientServiceConstant.HOME_REDIRECT_URL_CAPTIVE_PORTAL) + "?customerId=" + customerPayment.getCustId() + "&transactionId=" + customerPayment.getOrderId() + "&amount=" + customerPayment.getPayment() + "&currency=" + this.clientServiceSrv.getValueByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT));
                        Optional<Customers> customers = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                        CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findByCustId(customers.get().getId()).get(0);

                        model.addAttribute("homeRedirectUrl", REDIRECT_CAPTIVE_URL + "?username=" + customers.get().getUsername() + "&password=" + customers.get().getPassword() + "&url=http://adoptnettech.com/");
                        RecordPaymentRequestDTO recordPaymentDTO = new RecordPaymentRequestDTO();
                        recordPaymentDTO.setPaymentAmount(customerPayment.getPayment());
                        recordPaymentDTO.setBankName("FlutterWave");
                        recordPaymentDTO.setBranch(this.clientServiceSrv.getValueByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT));
                        recordPaymentDTO.setCustId(customers.get().getId());
                        recordPaymentDTO.setIsTdsDeducted(false);
                        recordPaymentDTO.setPaymentDate(LocalDate.now());
                        recordPaymentDTO.setPaymentMode(this.clientServiceSrv.getValueByName(ClientServiceConstant.PAYEMNT_GATEWAY));
                        recordPaymentDTO.setReferenceNo(String.valueOf(transactionId));
                        recordPaymentDTO.setRemarks("Transaction ID:-" + transactionId);
                        recordPaymentDTO.setType("Payment");
                        recordPaymentDTO.setMvnoId(mvnoId.intValue());

                        ChangePlanRequestDTO requestDTO = new ChangePlanRequestDTO();
                        requestDTO.setPurchaseType(CommonConstants.PLAN_GROUP_RENEW);
                        requestDTO.setPlanId(Math.toIntExact(customerPayment.getPlanId()));
                        requestDTO.setIsPaymentReceived(false);
                        requestDTO.setRemarks("Transaction ID:-" + transactionId);
                        requestDTO.setIsAdvRenewal(false);
                        requestDTO.setCustId(customers.get().getId());
                        requestDTO.setIsRefund(false);
                        requestDTO.setRecordPaymentDTO(recordPaymentDTO);
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_FROM_FLUTTERWAVE);
                        requestDTO.setAddonStartDate(LocalDateTime.now());
                        requestDTO.setCreatedate(LocalDateTime.now());
                        requestDTO.setCreatedById(customers.get().getId());
                        requestDTO.setCreatedByName(customers.get().getCreatedByName());
                        requestDTO.setLastModifiedById(customers.get().getId());
                        requestDTO.setLastModifiedByName(customers.get().getLastModifiedByName());
                        requestDTO.setUpdatedate(LocalDateTime.now());
                        requestDTO.setCustServiceMappingId(customerServiceMapping.getId());
                        logger.info("Payment For user id "+requestDTO.getCustId()+" is Successfull :  request: { From : {}}; Response : {{}}", request.getHeader("requestFrom"), APIConstants.SUCCESS);
//                        List<CustPlanMapppingPojo> custPlanMapppingPojoList = custPlanMappingService.findAllByCustomersId(customers.get().getId());
//                        if (custPlanMapppingPojoList.size() == 1) {
////                            customerPackageDTOS.get(0).getPlanId();
//                            if (planService.get(custPlanMapppingPojoList.get(0).getPlanId().intValue()).getOfferprice() == 0) {
//                                requestDTO.setAddonStartDate(LocalDate.now());
//                            }
//                        }
                        String number=String.valueOf(UtilsCommon.gen());
                        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustomerId(customers.get().getId());
                        custPlanMapppingList = custPlanMapppingList.stream().filter(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase("Active")).collect(Collectors.toList());
                        custPlanMapppingList = custPlanMapppingList.stream().filter(custPlanMappping -> !custPlanMappping.getPlanId().equals(14)).collect(Collectors.toList());
                        if(!custPlanMapppingList.isEmpty()){
                            for(CustPlanMappping custPlanMappping:custPlanMapppingList){
                                custPlanMappping.setExpiryDate(LocalDateTime.now().minusMinutes(1));
                                custPlanMappping.setEndDate(LocalDateTime.now().minusMinutes(1));
                                custPlanMappping.setCustPlanStatus("STOP");
                                custPlanMappingRepository.save(custPlanMappping);
                                custPlanMappingService.updateCustPlanEndDateInRadius(custPlanMappping ,"CHANGE_PLAN");
                            }
                        }
                        CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers.get(), false, 0.0, "FLutter wave", null, number,null,null);
                        CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                        try {
                            Customers customer = customersService.get(basicDetailsPojo.getId(),mvnoId.intValue());
                            CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                            List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerIdAndPlanId(recordPaymentDTO.getCustId() , requestDTO.getPlanId());
                            custPlanMappping = custPlanMappping.stream().filter(custPlanMappping1 -> custPlanMappping1.getOfferPrice() >0).collect(Collectors.toList());
                            Customers customers1 = customersRepository.getOne(recordPaymentDTO.getCustId());
                            List<CreditDocument> creditDocumentList = creditDocService.getAllByCustomer_IdOrderByIdDesc(customers.get().getId());
                            if(!creditDocumentList.isEmpty()) {
                                customersPojo.setCreditDocumentId(creditDocumentList.get(0).getId().toString());
                                customersPojo.setIsFromFlutterWave("true");
                            }
                            AdditionalInformationDTO additionalInformationDTO = new AdditionalInformationDTO();
                            String transactionNumber = "";
                            if(parameters.get("transaction_id") != null && !parameters.get("transaction_id").equalsIgnoreCase("")){
                                transactionNumber  = parameters.get("transaction_id");
                            }
                            else{
                                transactionNumber = customerPayment.getOrderId().toString();
                            }
                            additionalInformationDTO.setTransactionNumber(transactionNumber);
                            debitDocService.createInvoice(customers1 , Constants.RENEW,"FLUTTERWAVE", null,additionalInformationDTO, null,false,false,null,null,null);
                        } catch (Exception e) {
                            logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                            ApplicationLogger.logger.error("" + e.getMessage(), e);
                            e.printStackTrace();
                        }
                        if (customers.isPresent()) {
                            customersService.sendCustPaymentSuccessMessage("Payment Success", customers.get().getUsername(), customerPayment.getPayment(), "Online", customers.get().getMvnoId(), customers.get().getCountryCode(), customers.get().getMobile(), customers.get().getEmail(),customers.get().getId(),customerPayment.getPaymentDate().toString(), recordPaymentDTO.getReferenceNo(),customers.get().getBuId(),null,(long) customersService.getLoggedInStaffId(),customerPayment.getCreditDocumentId());

                        }
                        customerPayment.setStatus("Successful");
                        customerPayment.setPgTransactionId(parameters.get("transaction_id"));
                    } else {
                        result = "Payment Failed";
                        customerPayment.setStatus(result);
                        customerPayment.setPgTransactionId(parameters.get("transaction_id"));
                        logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                    }
                    customerPaymentRepository.save(customerPayment);
                } else {
                    logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                    throw new RuntimeException("Status not present in payment");
                }
                redirectTimeInSeconds = Long.valueOf(REDIRECT_TIME_IN_SECONDS);
            }
                model.addAttribute("result", result);
                model.addAttribute("parameters", parameters);
                model.addAttribute("redirectTimeInSeconds", redirectTimeInSeconds);


        } catch (Exception e) {
            logger.error("Payment Failed For Order Id "+parameters.get("transaction_id")+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", request.getHeader("requestFrom"),APIConstants.FAIL,request,e.getStackTrace());
            ApplicationLogger.logger.info(e.getMessage());
        }
        MDC.remove("type");
        return "report";
    }
    @GetMapping(value = "/fwResponse/renewPlanForCWSC")
    @Transactional
    public String getfwResponseAndRenewPlanForCWSC(HttpServletRequest request, Model model) throws Exception {
        ApplicationLogger.logger.info("LinkResponse API Hit");
        MDC.put("type", "Fetch");
        Map<String, String[]> mapData = request.getParameterMap();
        TreeMap<String, String> parameters = new TreeMap<String, String>();
//        String paytmChecksum = "";
        for (Map.Entry<String, String[]> requestParamsEntry : mapData.entrySet()) {
            parameters.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
        }
        try {
            ApplicationLogger.logger.info("Data received as Response:-  " + parameters);


           // Integer testMvno =  getMvnoIdFromCurrentStaff();
           // System.out.println("....,Mvno: "+testMvno);
            String result = "";
            Long orderId = Long.parseLong(parameters.get("tx_ref"));
            Long transactionId = Long.parseLong(parameters.get("transaction_id"));
            QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
            CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(orderId))).orElse(null);
            ClientService clientService = clientServiceSrv.getByName("redirectTimeInSeconds");
            Long redirectTimeInSeconds = 10L;
            if (clientService != null && ValidateCrudTransactionData.validateLongTypeFieldValue(Long.valueOf(clientService.getValue())) && Long.valueOf(clientService.getValue()) > 0)
                redirectTimeInSeconds = Long.valueOf(clientServiceSrv.getByName("redirectTimeInSeconds").getValue());
            if(Objects.isNull(customerPayment)){
                throw new RuntimeException("Customer not available for transaction id");
            }
            if (Objects.nonNull(customerPayment)) {
                if (parameters.containsKey("status")) {
                    result = parameters.get("status");
                    if (parameters.get("status").equals("successful")) {
                        /**Payment Gateway parameter started**/
                        Optional<Customers> customersformvnoId = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                        HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.FLUTTERWAVE , customersformvnoId.get().getMvnoId());
                        String PAYMENT_GATEWAY_NAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.PAYMENT_GATEWAY_NAME);
                        String REDIRECT_CAPTIVE_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.CAPTIVE_REDIRECT_URL);
                        String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.CWSC_REDIRECT_URL);
                        String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.REDIRECT_TIME_IN_SECONDS);
                        String FLUTTERWAVE_SECRET_KEY = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.FLUTTERWAVE_SECRET_KEY);
                        String FLUTTERWAVE_CURRENCY = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.FLUTTERWAVE_CURRENCY);
                        String STAFFUSER_USERNAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.STAFFUSER_USERNAME);
                        String STAFFUSER_PASSWORD = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.STAFFUSER_PASSWORD);
                        StaffUser staffUser = staffUserRepository.findStaffUserByUsername(STAFFUSER_USERNAME);
                        Long mvnoId = staffUser.getMvnoId().longValue();
                        String MVNO_NAME = mvnoRepository.findMvnoNameById(mvnoId);
                        redirectTimeInSeconds = Long.valueOf(REDIRECT_TIME_IN_SECONDS);
                        /**Payment Gateway parameter ended**/
                        List<GrantedAuthority> role_name=new ArrayList<>();
                        role_name.add(new SimpleGrantedAuthority("ADMIN"));
                        LoggedInUser user = new LoggedInUser(STAFFUSER_USERNAME, STAFFUSER_PASSWORD, true, true, true, true, role_name, MVNO_NAME, MVNO_NAME, LocalDateTime.now(), customersformvnoId.get().getCreatedById(), customersformvnoId.get().getPartner().getId(), "ADMIN", null, customersformvnoId.get().getMvnoId(), null, customersformvnoId.get().getCreatedById(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),MVNO_NAME,null,null,null);
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        // TODO: pass mvnoID manually 6/5/2025
                        Integer testMvno = getMvnoIdFromCurrentStaff(null);
                        subscriberService.validateTransaction(transactionId.toString() , orderId);
                        flutterWaveService.validateTransactionFromFlutterWaveWithPaymentGateway(orderId.toString() , parameters.get("transaction_id").toString() ,FLUTTERWAVE_SECRET_KEY );
//                        model.addAttribute("homeRedirectUrl", clientServiceSrv.getValueByName(ClientServiceConstant.HOME_REDIRECT_URL_CAPTIVE_PORTAL) + "?customerId=" + customerPayment.getCustId() + "&transactionId=" + customerPayment.getOrderId() + "&amount=" + customerPayment.getPayment() + "&currency=" + this.clientServiceSrv.getValueByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT));
                        Optional<Customers> customers = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                        if(customerPayment.getIsFromCaptive()){
                            String url = REDIRECT_CAPTIVE_URL;
                            url = url.replace("{userName}",customers.get().getUsername());
                            url = url.replace("{Password}",customers.get().getPassword());
                            model.addAttribute("homeRedirectUrl", url);
                        }
                        else {
                            model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
                        }
                        RecordPaymentRequestDTO recordPaymentDTO = new RecordPaymentRequestDTO();
                        recordPaymentDTO.setPaymentAmount(customerPayment.getPayment());
                        recordPaymentDTO.setBankName("FlutterWave");
                        recordPaymentDTO.setBranch("");
                        recordPaymentDTO.setCustId(customers.get().getId());
                        recordPaymentDTO.setIsTdsDeducted(false);
                        recordPaymentDTO.setPaymentDate(LocalDate.now());
                        recordPaymentDTO.setPaymentMode("FLUTTERWAVE");
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
                        requestDTO.setOnlinePurType("RENEW");
                        requestDTO.setAddonStartDate(null);
                        requestDTO.setBillableCustomerId(null);
                        requestDTO.setIsParent(true);
                        requestDTO.setDiscount(0.0000);
                        requestDTO.setNewPlanList(null);
                        requestDTO.setPlanMappingList(null);
                        requestDTO.setPaymentOwnerId(customers.get().getCreatedById());
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
                        logger.info("Payment For user id "+requestDTO.getCustId()+" is Successfull :  request: { From : {}}; Response : {{}}", request.getHeader("requestFrom"), APIConstants.SUCCESS);
                        String number=String.valueOf(UtilsCommon.gen());
                        CustomChangePlanDTO customChangePlanDTO = subscriberService.renewCustomer(requestDTO, customers.get(), false, 0.0, "FLutter wave", null, number,null,null);
                        CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                        try {
                            Customers customer = customersService.get(basicDetailsPojo.getId(),mvnoId.intValue());
                            customer.setBillRunCustPackageRelId(customChangePlanDTO.getCustpackagerelid());
                            CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                            List<CustPlanMappping> custPlanMappping = custPlanMappingRepository.findAllByCustomerIdAndPlanId(recordPaymentDTO.getCustId() , requestDTO.getPlanId());
                            custPlanMappping = custPlanMappping.stream().filter(custPlanMappping1 -> custPlanMappping1.getOfferPrice() >0).collect(Collectors.toList());
                            Customers customers1 = customersRepository.getOne(recordPaymentDTO.getCustId());
                            List<CreditDocument> creditDocumentList = creditDocService.getAllByCustomer_IdOrderByIdDesc(customers.get().getId());
                            if(!creditDocumentList.isEmpty()) {
                                customersPojo.setCreditDocumentId(creditDocumentList.get(0).getId().toString());
                                customersPojo.setIsFromFlutterWave("true");
                            }
                            AdditionalInformationDTO additionalInformationDTO =  new AdditionalInformationDTO();
                            String transactionNumber = "";
                            if(parameters.get("transaction_id") != null && !parameters.get("transaction_id").equalsIgnoreCase("")){
                                transactionNumber  = parameters.get("transaction_id");
                            }
                            else{
                                transactionNumber = customerPayment.getOrderId().toString();
                            }
                            additionalInformationDTO.setTransactionNumber(transactionNumber);
                            debitDocService.createInvoice(customers1 , Constants.RENEW,"FLUTTERWAVE", null,additionalInformationDTO, null,false,false,null,null,null);
                        } catch (Exception e) {
                            logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                            ApplicationLogger.logger.error("" + e.getMessage(), e);
                            e.printStackTrace();
                        }
                        if (customers.isPresent()) {
                            customersService.sendCustPaymentSuccessMessage("Payment Success", customers.get().getUsername(), customerPayment.getPayment(), "Online", customers.get().getMvnoId(), customers.get().getCountryCode(), customers.get().getMobile(), customers.get().getEmail(), customers.get().getId(), recordPaymentDTO.getReferenceNo(), customerPayment.getPaymentDate().toString(),customers.get().getBuId(),null,(long) customersService.getLoggedInStaffId(),customerPayment.getCreditDocumentId());

                        }
                        customerPayment.setStatus("Successful");
                        customerPayment.setPgTransactionId(parameters.get("transaction_id"));
                    } else {
                        result = "Payment Failed";
                        customerPayment.setStatus(result);
                        customerPayment.setPgTransactionId(parameters.get("transaction_id"));
                        logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                    }
                    customerPaymentRepository.save(customerPayment);
                } else {
                    logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                    throw new RuntimeException("Status not present in payment");
                }

            }

                model.addAttribute("result", result);
                model.addAttribute("parameters", parameters);
                model.addAttribute("redirectTimeInSeconds", redirectTimeInSeconds);


        } catch (Exception e) {
            logger.error("Payment Failed For Order Id "+parameters.get("transaction_id")+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", request.getHeader("requestFrom"),APIConstants.FAIL,request,e.getStackTrace());
            ApplicationLogger.logger.info(e.getMessage());
        }
        MDC.remove("type");
        return "report";
    }
    @GetMapping(value = "/fwResponse/changePlanForCWSC")
    @Transactional
    public String getfwResponseAndChangePlanForCWSC(HttpServletRequest request, Model model) throws Exception {
        ApplicationLogger.logger.info("LinkResponse API Hit");
        MDC.put("type", "Fetch");
        Map<String, String[]> mapData = request.getParameterMap();
        TreeMap<String, String> parameters = new TreeMap<String, String>();
//        String paytmChecksum = "";
        for (Map.Entry<String, String[]> requestParamsEntry : mapData.entrySet()) {
            parameters.put(requestParamsEntry.getKey(), requestParamsEntry.getValue()[0]);
        }
        try {
            ApplicationLogger.logger.info("Data received as Response:-  " + parameters);

            List<GrantedAuthority> role_name=new ArrayList<>();
            role_name.add(new SimpleGrantedAuthority("ADMIN"));
            LoggedInUser user = new LoggedInUser("admin", "admin@123", true, true, true, true, role_name, "admin", "admin", LocalDateTime.now(), 2, 1, "ADMIN", null, 2, null, 2, new ArrayList<Long>(),false , new ArrayList<String>(), new ArrayList<Long>(),"admin",null,null,null);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            // TODO: pass mvnoID manually 6/5/2025
            Integer testMvno =  getMvnoIdFromCurrentStaff(null);
            System.out.println("....,Mvno: "+testMvno);
            String result = "";
            Long orderId = Long.parseLong(parameters.get("tx_ref"));
            Long transactionId = Long.parseLong(parameters.get("transaction_id"));
            QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
            CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.isNotNull().and(qCustomerPayment.orderId.eq(orderId))).orElse(null);
            ClientService clientService = clientServiceSrv.getByName("redirectTimeInSeconds");
            Long redirectTimeInSeconds = 10L;
            if (clientService != null && ValidateCrudTransactionData.validateLongTypeFieldValue(Long.valueOf(clientService.getValue())) && Long.valueOf(clientService.getValue()) > 0) {
                redirectTimeInSeconds = Long.valueOf(clientServiceSrv.getByName("redirectTimeInSeconds").getValue());
            }
            if(Objects.isNull(customerPayment)){
                throw new RuntimeException("Customer not available for transaction id");
            }
            if (Objects.nonNull(customerPayment)) {
                if (parameters.containsKey("status")) {
                    result = parameters.get("status");
                    if (parameters.get("status").equals("successful")) {
                        /**Payment Gateway parameter started**/
                        Optional<Customers> customersformvnoId = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                        HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.FLUTTERWAVE , customersformvnoId.get().getMvnoId());
                        String PAYMENT_GATEWAY_NAME = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.PAYMENT_GATEWAY_NAME);
                        String REDIRECT_CAPTIVE_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.CAPTIVE_REDIRECT_URL);
                        String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.CWSC_REDIRECT_URL);
                        String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.REDIRECT_TIME_IN_SECONDS);
                        String FLUTTERWAVE_SECRET_KEY = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.FLUTTER_WAVE.FLUTTERWAVE_SECRET_KEY);
                        redirectTimeInSeconds = Long.valueOf(REDIRECT_TIME_IN_SECONDS);
                        /**Payment Gateway parameter ended**/


                        subscriberService.validateTransaction(transactionId.toString() , orderId);
                        flutterWaveService.validateTransactionFromFlutterWaveWithPaymentGateway(orderId.toString() , transactionId.toString() , FLUTTERWAVE_SECRET_KEY);
//                        model.addAttribute("homeRedirectUrl", clientServiceSrv.getValueByName(ClientServiceConstant.HOME_REDIRECT_URL_CAPTIVE_PORTAL) + "?customerId=" + customerPayment.getCustId() + "&transactionId=" + customerPayment.getOrderId() + "&amount=" + customerPayment.getPayment() + "&currency=" + this.clientServiceSrv.getValueByName(ClientServiceConstant.CURRENCY_FOR_PAYMENT));
                        Optional<Customers> customers = customersRepository.findById(customerPaymentRepository.findByOrderId(orderId).getCustId());
                        model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
                        RecordPaymentRequestDTO recordPaymentDTO = new RecordPaymentRequestDTO();
                        recordPaymentDTO.setPaymentAmount(customerPayment.getPayment());
                        recordPaymentDTO.setBankName("Flutter Wave");
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
                        requestDTO.setOnlinePurType(SubscriberConstants.PURCHASE_TYPE_RENEW);
                        requestDTO.setAddonStartDate(null);
                        requestDTO.setCreatedate(LocalDateTime.now());
                        requestDTO.setCreatedById(customers.get().getId());
                        requestDTO.setCreatedByName(customers.get().getCreatedByName());
                        requestDTO.setLastModifiedById(customers.get().getId());
                        requestDTO.setLastModifiedByName(customers.get().getLastModifiedByName());
                        requestDTO.setUpdatedate(LocalDateTime.now());

                        logger.info("Payment For user id "+requestDTO.getCustId()+" is Successfull :  request: { From : {}}; Response : {{}}", request.getHeader("requestFrom"), APIConstants.SUCCESS);
//                        List<CustPlanMapppingPojo> custPlanMapppingPojoList = custPlanMappingService.findAllByCustomersId(customers.get().getId());
//                        if (custPlanMapppingPojoList.size() == 1) {
////                            customerPackageDTOS.get(0).getPlanId();
//                            if (planService.get(custPlanMapppingPojoList.get(0).getPlanId().intValue()).getOfferprice() == 0) {
//                                requestDTO.setAddonStartDate(LocalDate.now());
//                            }
//                        }
                       // CustomChangePlanDTO customChangePlanDTO = subscriberService.changePlan01(requestDTO, customers.get(), false, 0.0, "FLutter wave", null, number);

                        //CustomersBasicDetailsPojo basicDetailsPojo = customChangePlanDTO.getCustomersBasicDetailsPojo();
                        try {
                            subscriberService.recordPayment(recordPaymentDTO , customers.get());
                            DeactivatePlanReqDTO deactivatePlanReqDTO =  new DeactivatePlanReqDTO();
                            deactivatePlanReqDTO.setCustId(customers.get().getId());
                            deactivatePlanReqDTO.setPlanGroupChange(false);
                            deactivatePlanReqDTO.setPlanGroupFullyChanged(false);
                            List<DeactivatePlanReqModel> deactivatePlanReqModelList = new ArrayList<>();
                            DeactivatePlanReqModel deactivatePlanReqModel = new DeactivatePlanReqModel();
                            deactivatePlanReqModel.setPlanId(Math.toIntExact(customerPayment.getPlanId()));
                            Integer newPlanId = Integer.parseInt(customerPayment.getLinkId());
                            deactivatePlanReqModel.setNewPlanId(newPlanId);
                            deactivatePlanReqModel.setIsFromFlutterWave("true");
                            deactivatePlanReqModelList.add(deactivatePlanReqModel);
                            deactivatePlanReqDTO.setDeactivatePlanReqModels(deactivatePlanReqModelList);


                            String number=String.valueOf(UtilsCommon.gen());
                            // CustomChangePlanDTO customChangePlanDTO = subscriberService.changePlan01(requestDTO, customers.get(), false, 0.0, "FLutter wave", null, number);
                            List<CustomerPlansModel> activeCustPlanModelList = subscriberService.getActivePlanList(recordPaymentDTO.getCustId() , false);
                            List<CustomerPlansModel> list = new LinkedList<>();

                            list = activeCustPlanModelList.stream().filter(customerPlansModel -> !customerPlansModel.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER)
                                    && !customerPlansModel.getPlangroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_BANDWIDTH_BOOSTER)).collect(Collectors.toList());
                            list = list.stream().filter(customerPlansModel -> Objects.equals(customerPlansModel.getPlanId(), deactivatePlanReqDTO.getDeactivatePlanReqModels().get(0).getPlanId())).collect(Collectors.toList());

                            CustPlanMappping custPlanMappping = null;
                            Customers customers1 =  customersRepository.findById(requestDTO.getCustId()).get();
                            if(!CollectionUtils.isEmpty(list)) {
                                custPlanMappping = custPlanMappingRepository.findById(list.get(0).getPlanmapid());
                            }
                            Thread.sleep(2000);
                            if(Objects.nonNull(custPlanMappping)) {
                                Random rnd = new Random();
                                int renewalId = rnd.nextInt(999999);
                                subscriberService.deactivateCustPlanMapping(custPlanMappping, deactivatePlanReqDTO.getDeactivatePlanReqModels().get(0).getNewPlanId(), null, deactivatePlanReqModel, customers1,"",null, renewalId,false,null);
                            }
                            else
                            {
                                System.out.println("**plan list is empty or price is 0");
                            }
//                            CreditDebitMappingPojo creditDebitMappingPojo = new CreditDebitMappingPojo();
//                            List<DebitDocument>debitDocumentList = debitDocRepository.getAllByCustomer_Id(customers.get().getId());
//                            creditDebitMappingPojo.setInvoiceId(debitDocumentList.get(debitDocumentList.size()-1).getId());
//                            CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
//                            creditDebitDataPojo.setAmount(customerPayment.getPayment());
//                            List<CreditDocument> creditDocumentList = creditDocService.getAllByCustomer_IdOrderByIdDesc(customers.get().getId());
//                            creditDebitDataPojo.setId(creditDocumentList.get(0).getId());
//                            List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
//                            creditDebitDataPojoList.add(creditDebitDataPojo);
//                            creditDebitMappingPojo.setCreditDocumentList(creditDebitDataPojoList);
//                            debitDocService.InvoicePaymentDone(creditDebitMappingPojo);
////                            CustomerPackage customerPackage = customerPackageService.findAllByCustomersId(customChangePlanDTO.getCustId());
//                            customer.setBillRunCustPackageRelId(customChangePlanDTO.get);
//                            Runnable invoiceRunnable = new InvoiceThread(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
//                                    , customer, customersService);
//                            Thread invoiceThread = new Thread(invoiceRunnable);
//                            invoiceThread.start();
                        } catch (Exception e) {
                            logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                            ApplicationLogger.logger.error("" + e.getMessage(), e);
                            e.printStackTrace();
                        }
                        if (customers.isPresent()) {
                            customersService.sendCustPaymentSuccessMessage("Payment Success", customers.get().getUsername(),customerPayment.getPayment(), "Online", customers.get().getMvnoId(), customers.get().getCountryCode(), customers.get().getMobile(), customers.get().getEmail(),customers.get().getId(), recordPaymentDTO.getReferenceNo(), customerPayment.getPaymentDate().toString(),customers.get().getBuId(),null,(long) customersService.getLoggedInStaffId(),customerPayment.getCreditDocumentId());

                        }
                        customerPayment.setStatus("Successful");
                        customerPayment.setPgTransactionId(parameters.get("transaction_id"));
                    } else {
                        result = "Payment Failed";
                        customerPayment.setStatus(result);
                        customerPayment.setPgTransactionId(parameters.get("transaction_id"));
                        logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                    }
                    customerPaymentRepository.save(customerPayment);
                } else {
                    logger.error("Payment Failed For Order Id "+orderId+" :  request: { From : {}}; Response : {{}};Error :{} ;", request.getHeader("requestFrom"),APIConstants.FAIL,request);
                    throw new RuntimeException("Status not present in payment");
                }

            }
                model.addAttribute("result", result);
                model.addAttribute("parameters", parameters);
                model.addAttribute("redirectTimeInSeconds", redirectTimeInSeconds);


        } catch (Exception e) {
            logger.error("Payment Failed For Order Id "+parameters.get("transaction_id")+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", request.getHeader("requestFrom"),APIConstants.FAIL,request,e.getStackTrace());
            ApplicationLogger.logger.info(e.getMessage());
        }
        MDC.remove("type");
        return "report";
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

    @PostMapping(value = "/rpResponse")
    @Transactional
    public ResponseEntity<?> getrpResponse(@RequestBody CustomerPayment responseCustomerPayment, HttpServletRequest request, Model model) throws Exception {
        ApplicationLogger.logger.info("Razorpay Response API Hit");
        MDC.put("type", "Fetch");
        HashMap<String, Object> response = new HashMap<>();
        Integer RESP_CODE = APIConstants.FAIL;
        try {
            Long orderId = responseCustomerPayment.getOrderId();
            String pgTrasactionId = responseCustomerPayment.getPgTransactionId();
            PaymentGatewayResponse paymentGatewayResponse = paymentGatewayService.getRazorpayResponse(orderId , pgTrasactionId);
            response.put("callbackResponse", paymentGatewayResponse);
            response.put("message", "Plan buy successfully");
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, ex.getMessage());
        }
        MDC.remove("type");
        return apiResponseController.apiResponse(RESP_CODE, response);
    }

    @GetMapping("/budpayCallBackUrl")
    public String getBudPayCallBack(@RequestParam(name = "reference") String reference, @RequestParam(name = "status") String status, Model model) {

        BooleanExpression booleanExpression = null;
        QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
        TreeMap<String, String> parameters = new TreeMap<String, String>();

//        OnlinePayAudit onlinePayAudit = new OnlinePayAudit();
        try{
            if (status.equalsIgnoreCase("success")) {
                CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.orderId.eq(Long.parseLong(reference)).and(qCustomerPayment.status.equalsIgnoreCase("Initiate").or(qCustomerPayment.status.equalsIgnoreCase("cancelled")))).orElse(null);

                if (customerPayment != null) {
                    Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerPayment.getCustId());

                    HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.BUDPAY , customers.getMvnoId());

                    String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.BUDPAY.BUDPAY_REDIRECT_URL);
                    String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.BUDPAY.REDIRECT_TIME_IN_SECONDS);

                    if (customers != null) {
                        BudPayPaymentMessage budPayPaymentMessage = new BudPayPaymentMessage(customers.getId(), status, reference);
//                        messageSender.send(budPayPaymentMessage, RabbitMqConstants.QUEUE_SEND_BUDPAY_PAYMENT_SUCCESS);
                        kafkaMessageSender.send(new KafkaMessageData(budPayPaymentMessage, BudPayPaymentMessage.class.getSimpleName()));
                        model.addAttribute("result", "Payment Success!!");
                        parameters.put("referenece",reference);
                        parameters.put("status", status);
                        parameters.put("amount", String.valueOf(customerPayment.getPayment()));
                        customerPayment.setStatus(status);
                        customerPayment.setPaymentDate(LocalDateTime.now());

                        customerPayment = customerPaymentRepository.save(customerPayment);
                        CustomerPaymentDto customerPaymentDto = new CustomerPaymentDto(customerPayment);
                        kafkaMessageSender.send(new KafkaMessageData(customerPaymentDto,customerPaymentDto.getClass().getSimpleName(),"BudPayPayment"));
                        model.addAttribute("parameters", parameters);
                        model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
                        model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);

                        return "report";
                    }


                }
            }else {
                CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.orderId.eq(Long.parseLong(reference)).and(qCustomerPayment.status.equalsIgnoreCase("Initiate"))).orElse(null);
                customerPayment.setPaymentDate(LocalDateTime.now());
                customerPayment.setStatus(status);
                customerPayment = customerPaymentRepository.save(customerPayment);
                CustomerPaymentDto customerPaymentDto = new CustomerPaymentDto(customerPayment);
                kafkaMessageSender.send(new KafkaMessageData(customerPaymentDto,customerPaymentDto.getClass().getSimpleName(),"BudPayPayment"));

                Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerPayment.getCustId());

                HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.BUDPAY , customers.getMvnoId());

                String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.BUDPAY.BUDPAY_REDIRECT_URL);
                String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.BUDPAY.REDIRECT_TIME_IN_SECONDS);

                model.addAttribute("result", "Payment Cancelled!!");
                parameters.put("referenece",reference);
                parameters.put("status", status);
                parameters.put("amount", String.valueOf(customerPayment.getPayment()));

                model.addAttribute("parameters", parameters);
                model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
                model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
            }
            return "report";
        }catch (Exception e){
            e.printStackTrace();
            ApplicationLogger.logger.error(e.getMessage());
        }
        return "report";
    }

    @GetMapping("/budpayCallBackUrlCwsc")
    @Transactional
    public String getBudpayCallBackUrlCwsc(@RequestParam(name = "reference") String reference, @RequestParam(name = "status") String status, Model model) {

        BooleanExpression booleanExpression = null;
        QCustomerPayment qCustomerPayment = QCustomerPayment.customerPayment;
        TreeMap<String, String> parameters = new TreeMap<String, String>();

//        OnlinePayAudit onlinePayAudit = new OnlinePayAudit();
        try{
            if (status.equalsIgnoreCase("success")) {
                CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.orderId.eq(Long.parseLong(reference)).and(qCustomerPayment.status.equalsIgnoreCase("Initiate").or(qCustomerPayment.status.equalsIgnoreCase("cancelled")))).orElse(null);

                if (customerPayment != null) {
                    Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerPayment.getCustId());

                    HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.BUDPAY , customers.getMvnoId());

                    String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.BUDPAY.BUDPAY_REDIRECT_URL);
                    String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.BUDPAY.REDIRECT_TIME_IN_SECONDS);
                    Mvno mvno = mvnoRepository.findById(Long.valueOf(customers.getMvnoId())).orElse(null);
                    List<StaffUser> staffUser = staffUserRepository.findAllStaffByMvnoIds(Collections.singletonList(mvno.getId()));
                    List<GrantedAuthority> role_name = new ArrayList<>();
                    role_name.add(new SimpleGrantedAuthority("ADMIN"));
                    LoggedInUser user = new LoggedInUser(staffUser.get(0).getUsername(), mvno.getName(), true, true, true, true, role_name, mvno.getName(), mvno.getName(), LocalDateTime.now(), staffUser.get(0).getId(), customers.getPartner().getId(), "ADMIN", null, customers.getMvnoId(), null, staffUser.get(0).getId(), new ArrayList<Long>(), false, new ArrayList<String>(), new ArrayList<Long>(),mvno.getName(),null,null,null);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    if (customers != null) {
                        DeactivatePlanReqDTOList changePlanPojoFromCustomer = captivePortalCustomerService.CreateChangePlanPojoForBudpay(customers.getId() , customerPayment.getPlanId() ,customers.getCustomerServiceMappingList().get(0).getId(), staffUser.get(0).getId());
                        subscriberService.deActivatePlanInList(changePlanPojoFromCustomer);
                        List<DebitDocument> debitDocuments = debitDocRepository.findAllByCustomerAndBillrunstatusIsNot(customers,"VOID");
                        if (debitDocuments.size()==0){
                            changePlanPojoFromCustomer.getDeactivatePlanReqDTOS().get(0).setChangePlanDate("Today");
                        }
                        boolean changePlanNextBillDate = false;
                        if(changePlanPojoFromCustomer.getDeactivatePlanReqDTOS().get(0).getChangePlanDate()!=null && changePlanPojoFromCustomer.getDeactivatePlanReqDTOS().get(0).getChangePlanDate().equalsIgnoreCase("Next Bill Date") && debitDocuments.size()>0){
                            changePlanNextBillDate = true;
                        }
                        debitDocService.createInvoice(customers, CommonConstants.INVOICE_TYPE.CHANGE_PLAN, "", changePlanPojoFromCustomer.getRecordPayment(),null, null,changePlanNextBillDate,false,null,null,null);
                        model.addAttribute("result", "Payment Success!!");
                        parameters.put("referenece",reference);
                        parameters.put("status", status);
                        parameters.put("amount", String.valueOf(customerPayment.getPayment()));
                        customerPayment.setStatus(status);
                        customerPayment.setPaymentDate(LocalDateTime.now());
                        customerPayment = customerPaymentRepository.save(customerPayment);
                        captivePortalCustomerService.sendBudPayChangePlanMessageToRevenue(customerPayment.getCustId() , customerPayment.getPlanId() , customerPayment.getOrderId().toString() , status , customerPayment.getPayment() , staffUser.get(0).getId());
                        CustomerPaymentDto customerPaymentDto = new CustomerPaymentDto(customerPayment);
                        kafkaMessageSender.send(new KafkaMessageData(customerPaymentDto,customerPaymentDto.getClass().getSimpleName(),"BudPayPayment"));

                        model.addAttribute("parameters", parameters);
                        model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
                        model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);

                        return "report";
                    }


                }
            }else {
                CustomerPayment customerPayment = customerPaymentRepository.findOne(qCustomerPayment.orderId.eq(Long.parseLong(reference)).and(qCustomerPayment.status.equalsIgnoreCase("Initiate"))).orElse(null);
                customerPayment.setPaymentDate(LocalDateTime.now());
                customerPayment.setStatus(status);
                customerPayment =  customerPaymentRepository.save(customerPayment);
                CustomerPaymentDto customerPaymentDto = new CustomerPaymentDto(customerPayment);
                kafkaMessageSender.send(new KafkaMessageData(customerPaymentDto,customerPaymentDto.getClass().getSimpleName(),"BudPayPayment"));

                Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerPayment.getCustId());

                HashMap<String , String> getPaymentGatewayParemeter = paymentConfigService.getPaymentGatewayParameter(PaymentGatewayConfigurationConstant.PAYMENT_GATEWAY_LIST.BUDPAY , customers.getMvnoId());

                String REDIRECT_CWSC_URL = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.BUDPAY.BUDPAY_REDIRECT_URL);
                String REDIRECT_TIME_IN_SECONDS = getPaymentGatewayParemeter.get(PaymentGatewayConfigurationConstant.BUDPAY.REDIRECT_TIME_IN_SECONDS);

                model.addAttribute("result", "Payment Cancelled!!");
                parameters.put("referenece",reference);
                parameters.put("status", status);
                parameters.put("amount", String.valueOf(customerPayment.getPayment()));

                model.addAttribute("parameters", parameters);
                model.addAttribute("redirectTimeInSeconds", REDIRECT_TIME_IN_SECONDS);
                model.addAttribute("homeRedirectUrl", REDIRECT_CWSC_URL);
            }
            return "report";
        }catch (Exception e){
            ApplicationLogger.logger.error(e.getMessage());
        }
        return "report";
    }

}

