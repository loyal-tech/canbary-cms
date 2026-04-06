package com.adopt.apigw.modules.CronJobs;

import com.adopt.apigw.repository.postpaid.CustomerLedgerDtlsRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.schedulerAudit.SchedulerAudit;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.service.SchedulerLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.core.repository.CustomRepository;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustomerLedger;
import com.adopt.apigw.model.postpaid.CustomerLedgerDtls;
import com.adopt.apigw.modules.placeOrder.model.OrderDTO;
import com.adopt.apigw.modules.placeOrder.service.OrderService;
import com.adopt.apigw.modules.purchaseDetails.model.CustomPurchase;
import com.adopt.apigw.modules.purchaseDetails.repository.PurchaseDetailsRepo;
import com.adopt.apigw.modules.subscriber.queryScript.PlanQueryScript;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.CustomerLedgerDtlsService;
import com.adopt.apigw.service.postpaid.CustomerLedgerService;
import com.adopt.apigw.utils.CommonConstants;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class RefundSettlementJob {

    @Autowired
    private PurchaseDetailsRepo purchaseDetailsRepo;

    @Autowired
    private CustomRepository<CustomPurchase> customRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerLedgerDtlsService customerLedgerDtlsService;

    @Autowired
    private CustomerLedgerService customerLedgerService;

    @Autowired
    private CustomersService customersService;
    @Autowired
    private CustomerLedgerDtlsRepository customerLedgerDtlsRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;
    @Autowired
    private CustomersRepository customersRepository;

    private String cronRefundPermission = "No";


    @Scheduled(cron = "${cronjobtimeforrefundrelease}}")
    public void cronJob() {
        log.info("XXXXXXXXXXXX----------REFUND RELEASE Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.REFUND_RELEASE_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.REFUND_RELEASE)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.REFUND_RELEASE);
            try {
                cronRefundPermission = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CRON_PERMISSION_FOR_REFUND).get(0).getValue();
                if (cronRefundPermission.equalsIgnoreCase("yes")) {
                    ApplicationLogger.logger.info("CRON PERMISSION FOR REFUND SETTLE IS STARTED");

                    List<CustomPurchase> purchaseList = customRepository.getResultOfQuery(PlanQueryScript.getRefundableOrders(), CustomPurchase.class);
                    if (purchaseList.size() > 0) {
                        purchaseList.forEach(data -> {
                            try {
                                OrderDTO orderDTO = orderService.getEntityById(data.getOrderid(), customerLedgerDtlsService.getMvnoIdFromCurrentStaff(data.getCustid()));
                                Customers customers =  customersRepository.findById(data.getCustid()).get();
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        });
                        schedulerAudit.setEndTime(LocalDateTime.now());
                        schedulerAudit.setDescription("CRON PERMISSION FOR REFUND SETTLE IS TRUE");
                        schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                        schedulerAudit.setTotalCount(purchaseList.size());
                    }
                } else {
                    ApplicationLogger.logger.info("CRON PERMISSION FOR REFUND SETTLE IS FALSE");
                    schedulerAudit.setEndTime(LocalDateTime.now());
                    schedulerAudit.setDescription("CRON PERMISSION FOR REFUND SETTLE IS FALSE");
                    schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                    schedulerAudit.setTotalCount(null);
                }
                ApplicationLogger.logger.info("CRON JOB FOR Refund settle is ENDED : " + LocalDateTime.now());
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.REFUND_RELEASE);
                log.info("XXXXXXXXXXXX---------- REFUND RELEASE Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("REFUND RELEASE Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------REFUND RELEASE Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }
}
