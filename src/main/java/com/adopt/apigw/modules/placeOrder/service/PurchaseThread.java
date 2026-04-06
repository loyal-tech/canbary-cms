package com.adopt.apigw.modules.placeOrder.service;

import com.adopt.apigw.repository.postpaid.CustomerLedgerDtlsRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.adopt.apigw.constants.PGConstants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.exception.PGException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustomerLedger;
import com.adopt.apigw.model.postpaid.CustomerLedgerDtls;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerLedgerBalanceDTO;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerDetailsService;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerService;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerPaymentService;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.placeOrder.model.OrderDTO;
import com.adopt.apigw.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.adopt.apigw.modules.purchaseDetails.service.PurchaseDetailsService;
import com.adopt.apigw.modules.subscriber.model.ChangePlanRequestDTO;
import com.adopt.apigw.modules.subscriber.model.CustomChangePlanDTO;
import com.adopt.apigw.modules.subscriber.service.InvoiceThread;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.BillRunService;
import com.adopt.apigw.service.postpaid.CustomerLedgerDtlsService;
import com.adopt.apigw.service.postpaid.CustomerLedgerService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PurchaseThread implements Runnable {
    private PurchaseDetailsDTO purchaseDetailsDTO;
    private PurchaseDetailsService purchaseDetailsService;
    private OrderService orderService;
    private SubscriberService subscriberService;
    private CustomersService customersService;
    private PartnerLedgerDetailsService partnerLedgerDetailsService;
    private PartnerLedgerService partnerLedgerService;
    private PartnerPaymentService partnerPaymentService;
    private CustomerLedgerService customerLedgerService;
    private CustomerLedgerDtlsService customerLedgerDtlsService;
    private BillRunService billRunService;
    private HttpServletRequest request;
    private SecurityContext securityContext;
    @Autowired
    private CustomerLedgerDtlsRepository customerLedgerDtlsRepository;
    @Autowired
    private CustomersRepository customersRepository;


    public PurchaseThread(PurchaseDetailsDTO dto, PurchaseDetailsService service, SubscriberService subscriberService, CustomersService customersService, OrderService orderService, PartnerLedgerDetailsService partnerLedgerDetailsService, PartnerLedgerService partnerLedgerService, PartnerPaymentService partnerPaymentService, CustomerLedgerService customerLedgerService, CustomerLedgerDtlsService customerLedgerDtlsService, HttpServletRequest request) {
        this.purchaseDetailsDTO = dto;
        this.purchaseDetailsService = service;
        this.customersService = customersService;
        this.subscriberService = subscriberService;
        this.orderService = orderService;
        this.partnerLedgerDetailsService = partnerLedgerDetailsService;
        this.partnerLedgerService = partnerLedgerService;
        this.partnerPaymentService = partnerPaymentService;
        this.customerLedgerService = customerLedgerService;
        this.customerLedgerDtlsService = customerLedgerDtlsService;
        this.request = request;
        this.securityContext = SecurityContextHolder.getContext();
    }

    @SneakyThrows
    @Override
    public void run() {
        this.billRunService = SpringContext.getBean(BillRunService.class);
        purchaseProcess(purchaseDetailsDTO);
    }

    @Transactional
    public void purchaseProcess(PurchaseDetailsDTO dto) throws Exception {
        AuditLogService auditLogService = SpringContext.getBean(AuditLogService.class);

        purchaseDetailsDTO = dto;
        purchaseDetailsDTO = this.purchaseDetailsService.getPurchaseBYTxnId(purchaseDetailsDTO.getTransid());
        OrderDTO orderDTO = orderService.getEntityById(purchaseDetailsDTO.getOrderid(),dto.getMvnoId());
        if (purchaseDetailsDTO.getPaymentstatus().equalsIgnoreCase(UtilsCommon.getPaymentStatus().get(PGConstants.FAILED_STATUS))) {
            purchaseDetailsDTO.setPurchaseStatus(PGConstants.FAILED_STATUS);
            purchaseDetailsService.saveEntity(purchaseDetailsDTO);

//            auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PLACE,
//                    request.getRemoteAddr(), null, orderDTO.getId());
//            auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PAYMENT,
//                    request.getRemoteAddr(), null, purchaseDetailsDTO.getId());
//            auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PURCHASE,
//                    request.getRemoteAddr(), null, purchaseDetailsDTO.getId());

            //Settle ledger if balance used
            if (orderDTO.getIs_balance_used() && orderDTO.getBalanced_used() > 0 && orderDTO.getLedger_details_id() != null) {
//                Double settleAmount = this.customerLedgerDtlsService.get(orderDTO.getLedger_details_id().intValue()).getAmount();
                Double settleAmount = customerLedgerDtlsRepository.findById(orderDTO.getLedger_details_id().intValue()).get().getAmount();
                Customers customers =  customersRepository.findById(purchaseDetailsDTO.getCustid()).get();
                if (settleAmount != null && settleAmount > 0) {
                    CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
                    ledgerDtls.setCustomer(customers);
                    ledgerDtls.setDebitdocid(null);
                    ledgerDtls.setAmount(settleAmount);
                    ledgerDtls.setDescription("Settle balance against used amount in plan purchase");
                    ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                    ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_WALLET_ADJUST);
                    ledgerDtls = customerLedgerDtlsService.save(ledgerDtls);

                    CustomerLedger customerLedger = this.customerLedgerService.getCustomerLeger(customers).get(0);
                    customerLedger.setTotalpaid(customerLedger.getTotalpaid() + settleAmount);
                    customerLedger.setCustomer(customers);
                    customerLedger.setUpdatedate(LocalDateTime.now());
                    customerLedger = customerLedgerService.save(customerLedger);

                    orderDTO.setIs_settled(true);
                    orderDTO = orderService.saveEntity(orderDTO);
                }
            }

            throw new PGException("Payment is failed due to cancellation");
        } else if (purchaseDetailsDTO.getPaymentstatus().equalsIgnoreCase(UtilsCommon.getPaymentStatus().get(PGConstants.PENDING_STATUS))) {
            purchaseDetailsDTO.setPurchaseStatus(PGConstants.PENDING_STATUS);
            purchaseDetailsService.saveEntity(purchaseDetailsDTO);

//            auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PLACE,
//                    request.getRemoteAddr(), null, orderDTO.getId());
//            auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PAYMENT,
//                    request.getRemoteAddr(), null, purchaseDetailsDTO.getId());
//            auditLogService.addAuditForPlaceOrder(AclConstants.ACL_CLASS_ORDER_PURCHASE,
//                    request.getRemoteAddr(), null, purchaseDetailsDTO.getId());
            throw new PGException("Payment is pending");
        }
        try {
            if (purchaseDetailsDTO.getCustid() != null) {
                if (orderDTO != null) {
                    if (orderDTO.getEntityid() != null) {
                        if (orderDTO.getOrdertype().equalsIgnoreCase(PGConstants.ORDER_TYPE_PLAN)) {
                            ChangePlanRequestDTO changePlanRequestDTO = new ChangePlanRequestDTO(orderDTO.getCustId().intValue()
                                    , orderDTO.getEntityid().intValue(), orderDTO.getPurchase_type(), true, false, "Online purchase",
                                    orderDTO.getBalanced_used(), purchaseDetailsDTO.getId(), SubscriberConstants.PURCHASE_FROM_CUSTPORTAL, SubscriberConstants.PURCHASE_TYPE_RENEW,null,null);
                            Customers customers =  customersRepository.findById(orderDTO.getCustId().intValue()).get();

                            //Payment record
                            /*RecordPaymentRequestDTO recordPaymentDto = new RecordPaymentRequestDTO(CommonUtils.PAYMENT_MODE_ONLINE
                                    , LocalDateTime.now().toLocalDate(), purchaseDetailsDTO.getAmount(), false, "", customers.getId());
                            RecordpaymentResponseDTO recordpaymentResponseDTO = subscriberService.recordPayment(recordPaymentDto, customers);

                            //Invoke Receipt therad
                            if (null != customers) {
                                List<CreditDocument> creditDocumentList = recordpaymentResponseDTO.getCreditDocument();
                                Runnable receiptRunnable = new ReceiptThread(billRunService, creditDocumentList);
                                Thread receiptThread = new Thread(receiptRunnable);
                                receiptThread.start();
                            }*/

                            //Settle ledger if balance used
                            if (orderDTO.getIs_balance_used() && orderDTO.getBalanced_used() > 0 && orderDTO.getLedger_details_id() != null) {
                                Double settleAmount = customerLedgerDtlsRepository.findById(orderDTO.getLedger_details_id().intValue()).get().getAmount();
                                if (settleAmount != null && settleAmount > 0) {
                                    CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
                                    ledgerDtls.setCustomer(customers);
                                    ledgerDtls.setDebitdocid(null);
                                    ledgerDtls.setAmount(settleAmount);
                                    ledgerDtls.setDescription("Settle balance against used amount in plan purchase");
                                    ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                                    ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_WALLET_ADJUST);
                                    ledgerDtls = customerLedgerDtlsService.save(ledgerDtls);

                                    CustomerLedger customerLedger = this.customerLedgerService.getCustomerLeger(customers).get(0);
                                    customerLedger.setTotalpaid(customerLedger.getTotalpaid() + settleAmount);
                                    customerLedger.setCustomer(customers);
                                    customerLedger.setUpdatedate(LocalDateTime.now());
                                    customerLedger = customerLedgerService.save(customerLedger);

                                    orderDTO.setIs_settled(true);
                                    orderDTO = orderService.saveEntity(orderDTO);
                                }
                            }

                            changePlanRequestDTO.setIsPaymentReceived(false);
                            CustomChangePlanDTO customChangePlanDTO = subscriberService.changePlan(changePlanRequestDTO, customers, true, purchaseDetailsDTO.getAmount(),"", null);

                            // Invoke billing engine

                            try {
                                Customers customer =  customersRepository.findById(customers.getId()).get();
                                customer.setBillRunCustPackageRelId(customChangePlanDTO.getCustpackagerelid());

                                Runnable invoiceRunnable = new InvoiceThread(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), customer, customersService,"",null,null);
                                Thread invoiceThread = new Thread(invoiceRunnable);
                                invoiceThread.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            purchaseDetailsDTO.setPurchaseStatus(PGConstants.SUCCESSFUL_STATUS);
                            purchaseDetailsService.saveEntity(purchaseDetailsDTO);
                        }
                    }
                }
            } else if (purchaseDetailsDTO.getPartnerid() != null) {
                if (orderDTO.getOrdertype().equalsIgnoreCase(PGConstants.ORDER_TYPE_PARTNER_ADD_BALANCE)) {
                    PartnerLedgerBalanceDTO balanceDTO = new PartnerLedgerBalanceDTO(orderDTO.getFinalamount(), orderDTO.getPartnerId().intValue()
                            , "Add balance", UtilsCommon.PAYMENT_MODE_ONLINE, purchaseDetailsDTO.getTransid(), LocalDateTime.now().toLocalDate());
                    //add balance in ledgerdetails
                    partnerLedgerDetailsService.addBalance(balanceDTO);
                    //add balance in ledger
                    partnerLedgerService.addBalance(balanceDTO);
                    //add balance in partnerpayment
                    partnerPaymentService.addBalance(balanceDTO);

                    purchaseDetailsDTO.setPurchaseStatus(PGConstants.SUCCESSFUL_STATUS);
                    purchaseDetailsService.saveEntity(purchaseDetailsDTO);
                }
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }
}
