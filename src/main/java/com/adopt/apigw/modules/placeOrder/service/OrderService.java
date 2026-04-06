package com.adopt.apigw.modules.placeOrder.service;

import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.PGConstants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.PGException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.paymentGatewayMaster.dto.PaymentGatewayDTO;
import com.adopt.apigw.modules.paymentGatewayMaster.service.PaymentGatewayService;
import com.adopt.apigw.modules.payments.util.CCAvenueHelper;
import com.adopt.apigw.modules.payments.util.PGHelper;
import com.adopt.apigw.modules.payments.util.PayuPGHelper;
import com.adopt.apigw.modules.placeOrder.domain.Order;
import com.adopt.apigw.modules.placeOrder.mapper.OrderMapper;
import com.adopt.apigw.modules.placeOrder.model.OrderDTO;
import com.adopt.apigw.modules.placeOrder.model.OrderResponseModel;
import com.adopt.apigw.modules.placeOrder.repository.OrderRepository;
import com.adopt.apigw.modules.purchaseDetails.model.PGResponseModel;
import com.adopt.apigw.modules.purchaseDetails.model.PaymentGatewayResponseDTO;
import com.adopt.apigw.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.adopt.apigw.modules.purchaseDetails.service.PaymentGatewayResponseService;
import com.adopt.apigw.modules.purchaseDetails.service.PurchaseDetailsService;
import com.adopt.apigw.modules.subscriber.model.ChangePlanRequestDTO;
import com.adopt.apigw.modules.subscriber.model.CustomChangePlanDTO;
import com.adopt.apigw.modules.subscriber.model.CustomerPlansModel;
import com.adopt.apigw.modules.subscriber.service.InvoiceThread;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.ClientServicePojo;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.pojo.api.PartnerPojo;
import com.adopt.apigw.pojo.api.PostpaidPlanPojo;
import com.adopt.apigw.pojo.api.TaxDetailCountReqDTO;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.adopt.apigw.utils.PropertyReaderUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class OrderService extends ExBaseAbstractService<OrderDTO, Order, Long> {

    @Autowired
    private OrderRepository repository;
    @Autowired
    public PostpaidPlanService planService;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;
    @Autowired
    private CustomersService customersService;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private PostpaidPlanMapper postpaidPlanMapper;
    @Autowired
    private PaymentGatewayService pgService;
    @Autowired
    private PurchaseDetailsService purchaseService;
    @Autowired
    private PaymentGatewayResponseService paymentGatewayResService;
    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private CustomerLedgerService customerLedgerService;
    @Autowired
    private CustomerLedgerDtlsService customerLedgerDtlsService;
    @Autowired
    private TaxService taxService;
    @Autowired
    private ClientServiceSrv clientServiceSrv;
    @Autowired
    private AuditLogService auditLogService;

    public OrderService(OrderRepository repository, OrderMapper mapper) {
        super(repository, mapper);
    }

    @Transactional
    public OrderResponseModel placeOrder(OrderDTO orderDTO,String requestFrom ,HttpServletRequest request) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [placeOrder()] ";
        OrderResponseModel responseModel = new OrderResponseModel();
        try {
            PurchaseDetailsDTO purchaseDTO = new PurchaseDetailsDTO();
            purchaseDTO.setPurchasedate(LocalDateTime.now());
            purchaseDTO.setPgid(orderDTO.getPgid());
            purchaseDTO.setTransid(this.generateUUID());
            CustomersPojo customersPojo = null;
            PartnerPojo partnerPojo = null;
            Customers customers = null;
            if (orderDTO != null) {
                PaymentGatewayDTO pgDTO = pgService.getByIdAndStatus(orderDTO.getPgid());
                if (orderDTO.getCustId() != null && orderDTO.getCustId() != 0) {
                    if ((pgDTO != null && pgDTO.getUserenableflag())) {
                        customersPojo = customerMapper.domainToDTO(customersRepository.findById(orderDTO.getCustId().intValue()).get()
                                , new CycleAvoidingMappingContext());
                        customers = customerMapper.dtoToDomain(customersPojo, new CycleAvoidingMappingContext());
                        if (customersPojo != null) {
                            if (orderDTO.getOrdertype().equalsIgnoreCase(PGConstants.ORDER_TYPE_PLAN)) {
                                PostpaidPlan plan = planService.get(orderDTO.getEntityid().intValue(),customers.getMvnoId());
                                PostpaidPlanPojo planPojo = postpaidPlanMapper.domainToDTO(plan
                                        , new CycleAvoidingMappingContext());
                                TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO(orderDTO.getEntityid().intValue()
                                        , null, customers.getId(), null);
                                double planPrice = planPojo.getOfferprice() + taxService.taxCalculationByPlan(taxDetailCountReqDTO, plan.getChargeList());
                                double payableAmount = 0.0;
                                double usedCustBalance = 0.0;
                                if (orderDTO.getIs_balance_used()) {
                                    double getCustBalance = customersPojo.getOutstanding();
                                    if (getCustBalance > 0) {
                                        if (getCustBalance >= planPrice) {
                                            usedCustBalance = planPrice;
                                            payableAmount = 0.0;
                                        } else {
                                            usedCustBalance = getCustBalance;
                                            payableAmount = planPrice - getCustBalance;
                                        }
                                    } else {
                                        payableAmount = planPrice;
                                    }
                                } else {
                                    payableAmount = planPrice;
                                }
                                orderDTO.setFinalamount(planPrice);
                                orderDTO.setBalanced_used(usedCustBalance);
                                purchaseDTO.setAmount(payableAmount);
                                purchaseDTO.setCustid(customersPojo.getId());
                            }
                        }
                    } else {
                        throw new PGException("Payment gateway is not exist or it is not used for customer");
                    }
                } else if (orderDTO.getPartnerId() != null) {

                    if ((pgDTO != null && pgDTO.getPartnerenableflag())) {
                        partnerPojo = partnerService.convertPartnerModelToPartnerPojo(partnerService.get(orderDTO.getPartnerId().intValue(),customers.getMvnoId()));
                        if (partnerPojo != null) {
                            if (orderDTO.getOrdertype().equalsIgnoreCase(PGConstants.ORDER_TYPE_PARTNER_ADD_BALANCE)) {
//                                customersPojo = new CustomersPojo();
//                                customersPojo.setFirstname(partnerPojo.getName());
//                                customersPojo.setPhone(partnerPojo.getMobile());
//                                customersPojo.setEmail(partnerPojo.getEmail());
                                purchaseDTO.setAmount(orderDTO.getFinalamount());
                                purchaseDTO.setPartnerid(partnerPojo.getId());
                            }
                        }
                    } else {
                        throw new PGException("Payment gateway is not exist or it is not used for partner");
                    }
                }

                if (purchaseDTO.getAmount() != 0.0 && orderDTO.getBalanced_used() > 0 && orderDTO.getIs_balance_used()) {
                    CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
                    ledgerDtls.setCustomer(customers);
                    ledgerDtls.setDebitdocid(null);
                    ledgerDtls.setAmount(orderDTO.getBalanced_used());
                    ledgerDtls.setDescription("Used balance against plan purchase");
                    ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
                    ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
                    ledgerDtls = customerLedgerDtlsService.save(ledgerDtls);

                    CustomerLedger customerLedger = this.customerLedgerService.getCustomerLeger(customers).get(0);
                    customerLedger.setTotaldue(customerLedger.getTotaldue() + orderDTO.getBalanced_used());
                    customerLedger.setCustomer(customers);
                    customerLedger.setUpdatedate(LocalDateTime.now());
                    customerLedgerService.save(customerLedger);
                    orderDTO.setLedger_details_id(ledgerDtls.getId().longValue());
                    orderDTO.setIs_settled(false);
                }

                orderDTO = this.saveEntity(orderDTO);
                // auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PLACE,
                //        request.getRemoteAddr(), null, orderDTO.getId());

                if (purchaseDTO != null) {
                    if (purchaseDTO.getAmount() != null) {
                        purchaseDTO.setOrderid(orderDTO.getId());
                        purchaseDTO.setPaymentstatus(UtilsCommon.getPaymentStatus().get(PGConstants.PENDING_STATUS));
                        purchaseDTO.setPurchaseStatus(UtilsCommon.getPaymentStatus().get(PGConstants.PENDING_STATUS));
                        purchaseDTO.setPgResStatus(null);
                    }
                    purchaseDTO = purchaseService.saveEntity(purchaseDTO);
                    // auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PURCHASE,
                    //        request.getRemoteAddr(), null, purchaseDTO.getId());
                }

                if (purchaseDTO.getAmount() == 0.0 && orderDTO.getIs_balance_used() && orderDTO.getBalanced_used() > 0) {
                    ChangePlanRequestDTO changePlanRequestDTO = new ChangePlanRequestDTO(orderDTO.getCustId().intValue(), orderDTO.getEntityid().intValue(), orderDTO.getPurchase_type(), true, false, "Online purchase", orderDTO.getBalanced_used(), purchaseDTO.getId(), SubscriberConstants.PURCHASE_FROM_CUSTPORTAL, SubscriberConstants.PURCHASE_TYPE_RENEW,null,null);
                    changePlanRequestDTO.setIsPaymentReceived(false);
                    CustomChangePlanDTO customChangePlanDTO = subscriberService.changePlan(changePlanRequestDTO, customerMapper.dtoToDomain(customersPojo, new CycleAvoidingMappingContext()), false, 0.0,requestFrom, null);
                    // auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PAYMENT,
                    //        request.getRemoteAddr(), null, purchaseDTO.getId());
                    try {
                        Customers customer =  customersRepository.findById(customers.getId()).get();
                        customer.setBillRunCustPackageRelId(customChangePlanDTO.getCustpackagerelid());

                        Runnable invoiceRunnable = new InvoiceThread(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), customer, customersService,"",null,null);
                        Thread invoiceThread = new Thread(invoiceRunnable);
                        invoiceThread.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    purchaseDTO.setPurchaseStatus(PGConstants.SUCCESSFUL_STATUS);
                    purchaseDTO.setPaymentstatus(PGConstants.SUCCESSFUL_STATUS);
                    purchaseDTO = purchaseService.saveEntity(purchaseDTO);
                }

                Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);

                //Make Object Dynamically
                Class cls;
                String gateWayClassName = pgDTO.getPrefix() + PGConstants.PG_CONFIG_CLASS_MAME;
                cls = Class.forName(properties.getProperty(gateWayClassName));

                if (cls != null) {
                    PGHelper pgHelper = (PGHelper) cls.getConstructor().newInstance();
                    if (pgHelper != null && pgHelper.getClass().getName().equalsIgnoreCase(properties.getProperty(gateWayClassName))) {
                        responseModel = pgHelper.generateFormData(customersPojo, purchaseDTO, partnerPojo);
                    }
                }
            } else {
                throw new PGException("Order can not be null");
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
        return responseModel;
    }

    @Transactional
    public PurchaseDetailsDTO processPayment(Map<String, Object> response, HttpServletRequest request) throws Exception {

        PurchaseDetailsDTO purchaseDetailsDTO = null;
        PGResponseModel responseModel;
        PGHelper pgHelper;
        String pgStatus;
        String pgPayId;
        String sysTxnId;
        String sysStatus;

        ApplicationLogger.logger.info("MAP PG RES :: " + response);
        //For Payu
        if (response.containsKey(PGConstants.PAYU_RESPONSE_TXNID)) {
            pgHelper = new PayuPGHelper();
            responseModel = pgHelper.generatePGResponse(response);
        }

        //For CCAvenue
        else if (response.containsKey(PGConstants.CCAVENUE_RESPONSE_ORDER_NO)) {
            pgHelper = new CCAvenueHelper();
            responseModel = pgHelper.generatePGResponse(response);
        } else {
            throw new PGException("Payment gateway not identified!!");
        }
        if (null != responseModel) {
            pgStatus = responseModel.getPgStatus();
            pgPayId = responseModel.getPgPayId();
            sysTxnId = responseModel.getSysTxnId();
            if (pgStatus.equalsIgnoreCase(PGConstants.SUCCESS_STATUS)) {
                sysStatus = UtilsCommon.getPaymentStatus().get(PGConstants.SUCCESSFUL_STATUS);
            } else if (pgStatus.equalsIgnoreCase(PGConstants.PENDING_STATUS)
                    || pgStatus.equalsIgnoreCase(PGConstants.ABORT_STATUS)
                    || pgStatus.equalsIgnoreCase(PGConstants.INVALID_STATUS)
                    || pgStatus.equalsIgnoreCase(PGConstants.TIMEOUT_STATUS)) {
                sysStatus = UtilsCommon.getPaymentStatus().get(PGConstants.PENDING_STATUS);
            } else {
                sysStatus = UtilsCommon.getPaymentStatus().get(PGConstants.FAILED_STATUS);
            }

            if (sysTxnId != null && sysTxnId.trim().length() > 0) {
                purchaseDetailsDTO = purchaseService.getPurchaseBYTxnId(sysTxnId);
                if (null != purchaseDetailsDTO.getPaymentstatus()
                        && purchaseDetailsDTO.getPaymentstatus().equalsIgnoreCase(PGConstants.PENDING_STATUS)) {
                    if (purchaseDetailsDTO.getTransid() != null && purchaseDetailsDTO.getTransid().trim().length() > 0) {
                        purchaseDetailsDTO.setPaymentstatus(sysStatus);
                        purchaseDetailsDTO.setPurchaseStatus(UtilsCommon.getPaymentStatus().get(PGConstants.PENDING_STATUS));
                        purchaseDetailsDTO.setPgtransid(pgPayId);
                        purchaseDetailsDTO.setTransResDate(LocalDateTime.now());
                        purchaseDetailsDTO.setPgResStatus(pgStatus);
                        purchaseDetailsDTO = purchaseService.saveEntity(purchaseDetailsDTO);

                        //auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PAYMENT,
                        //        request.getRemoteAddr(), null, purchaseDetailsDTO.getId());

                        PaymentGatewayResponseDTO paymentGatewayResponse = new PaymentGatewayResponseDTO();
                        paymentGatewayResponse.setPgId(purchaseDetailsDTO.getPgid());
                        paymentGatewayResponse.setPurchaseId(purchaseDetailsDTO.getId());
                        paymentGatewayResponse.setResponseDate(LocalDateTime.now());
                        paymentGatewayResponse.setResponse(response.toString());
                        paymentGatewayResService.saveEntity(paymentGatewayResponse);
                    } else {
                        throw new PGException("Purchase not found!!");
                    }
                } else {
                    throw new PGException("Payment Is Already Succeed");
                }
            } else {
                throw new PGException("Transaction id must not be empty or null");
            }
        }

        return purchaseDetailsDTO;
    }

    public String generateUUID() {
//        UUID uuid = UUID.randomUUID();
//        return uuid.toString();

        String alphabetsInUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String alphabetsInLowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        // create a super set of all characters
        String allCharacters = alphabetsInLowerCase + alphabetsInUpperCase + numbers;
        // initialize a string to hold result
        StringBuffer randomString = new StringBuffer();
        // loop for 10 times
        for (int i = 0; i < 9; i++) {
            // generate a random number between 0 and length of all characters
            int randomIndex = (int) (Math.random() * allCharacters.length());
            // retrieve character at index and add it to result
            randomString.append(allCharacters.charAt(randomIndex));
        }

        //APPEND UNIX EPOCH FOR UNIQUE STRING
        randomString.append("-" + Instant.now().getEpochSecond());

        return randomString.toString();
    }

    public Boolean validatePurchaseAddon(OrderDTO requestDTO) {
        String SUBMODULE = " [validate Addon Request()] ";
        try {
            ClientServicePojo clientServicePojo = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CONVERT_VOL_BOOST_TOPUP).get(0);
            if (clientServicePojo != null) {
                if (clientServicePojo.getValue().equalsIgnoreCase("1")) {
                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(requestDTO.getEntityid().intValue()).get();
                    if (postpaidPlan.getPlanGroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_ADDON)) {
                        List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(requestDTO.getCustId().intValue(),false).stream()
                                .filter(data -> data.getService().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA)
                                        && data.getPlangroup().equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_RENEW)
                                        && !data.getVolTotalQuota().equalsIgnoreCase(SubscriberConstants.UNLIMITED_QUOTA)
                                        && (data.getQuotaType().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA) || data.getQuotaType().equalsIgnoreCase(CommonConstants.BOTH_QUOTA_TYPE))).collect(Collectors.toList());

                        if (currentPlanList == null || currentPlanList.size() <= 0) {
                            throw new RuntimeException("Something went wrong.");
                        }
                    } else {
                        throw new RuntimeException("Selected plan is not for add on.");
                    }
                    return true;
                }
                return true;
            } else {
                throw new RuntimeException("Client service for convert addon can't be null");
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public String getModuleNameForLog() {
        return "[OrderService]";
    }
}
