package com.adopt.apigw.service.common;


import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.Shorter;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.model.postpaid.TrialDebitDocument;
import com.adopt.apigw.modules.MtnPayment.service.MtnPaymentService;
import com.adopt.apigw.modules.RvenueClient.RevenueClient;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.repository.common.ShorterRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.utils.CodeGenerator;
import com.adopt.apigw.utils.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ShorterService {

    @Autowired
    private ShorterRepository shorterRepository;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private RevenueClient revenueClient;

    public Shorter createShortUrl(String url, Integer custId, Double amount, String username, String planname, LocalDateTime planDueDate) {
        CodeGenerator codeGenerator = new CodeGenerator();
        String hash = codeGenerator.generate(12);
        if (url != null) {
            Shorter shorter = new Shorter();
            shorter.setHash(hash);
            shorter.setOriginalUrl(url);
            shorter.setCustId(custId);
            shorter.setAmount(amount);
            if (username != null && !username.isEmpty()) {
                shorter.setCustomerUsername(username);
            }
            if (planname != null && !planname.isEmpty()) {
                shorter.setPlanName(planname);
            }
            if (planDueDate != null) {
                shorter.setPlanDueDate(planDueDate);
            }
            // TODO: pass mvnoID manually 6/5/2025
            shorter.setMvnoId(subscriberService.getMvnoIdFromCurrentStaff(null));
            return shorterRepository.save(shorter);
        } else {
            return null;
        }
    }

    public Map<String, Object> getShorterByHash(String hash) {
        Map<String, Object> response = new HashMap<>();

        if (hash == null || hash.trim().isEmpty()) {
            response.put("status", HttpStatus.NO_CONTENT.value());
            response.put("message", "Link is expired. Please generate the link again.");
            return response;
        }
        Shorter shorter = shorterRepository.findByHash(hash).orElse(null);
        if (shorter == null) {
            response.put("status", HttpStatus.NO_CONTENT.value());
            response.put("message", "No payment details found.");
            return response;
        }
//        Optional<Customers> optionalCustomers = customersRepository.findById(shorter.getCustId());
//        if (optionalCustomers.isPresent()) {
//            Customers customers = optionalCustomers.get();
//            shorter.setCustomerUsername(customers.getUsername());
//            shorter.setMvnoId(customers.getMvnoId());
//            if (customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVE) || customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.INAVCTIVE) || customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.SUSPEND)) {
//                Object[] latestPlan = new Object[0];
//                if (shorter.getLinkType().equals(CommonConstants.RENEW_PAYMENT)) {
//                    List<Object[]> renewPlans = subscriberService.getRenewPlanListByCustomerId(shorter.getCustId());
//                    latestPlan = renewPlans.get(0);
//                    BigDecimal value = (BigDecimal) latestPlan[3];  // Extract BigDecimal
//                    Double amount = value != null ? value.doubleValue() : null;
//                    shorter.setAmount(amount);
//                } else if (shorter.getLinkType().equals(CommonConstants.CURRENT_PAYMENT)) {
//                    List<Object[]> activePlans = subscriberService.getActivePlanListByCustomerId(shorter.getCustId());
//                    latestPlan = activePlans.get(0);
//                    List<DebitDocument> debitDocuments = revenueClient.getDebitDocumentByCustId(shorter.getCustId(), shorter.getToken());
//                    DebitDocument debitDocument = subscriberService.getLatestDebitDocument(debitDocuments);
//                    Double totalAmount = Optional.ofNullable(debitDocument.getTotalamount()).orElse(0.0);
//                    Double adjustedAmount = Optional.ofNullable(debitDocument.getAdjustedAmount()).orElse(0.0);
//                    double difference = totalAmount - adjustedAmount;
//                    shorter.setAmount(Math.abs(difference) < 0.1 ? 0.0 : difference);
//                    shorter.setInvoiceId(debitDocument.getId());
//                }
//                BigInteger planId = (BigInteger) latestPlan[0];
//                shorter.setPlanId(planId.intValue());
//                shorter.setPlanName(String.valueOf(latestPlan[1]));
//                shorter.setPlanDueDate(((Timestamp) latestPlan[2]).toLocalDateTime());
//
//            }else if (customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION) || customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING)) {
//                if (shorter.getLinkType().equals(CommonConstants.CURRENT_PAYMENT)) {
//                    List<Object[]> activePlans = subscriberService.getActivePlanListByCustomerId(shorter.getCustId());
//                    Object[] latestPlan = activePlans.get(0);
//                    List<TrialDebitDocument> trialDebitDocuments = revenueClient.getTrailDebitDocumentByCustId(shorter.getCustId(), shorter.getToken());
//                    TrialDebitDocument trialDebitDocument = subscriberService.getLatestTrailDebitDocument(trialDebitDocuments);
//                    if (trialDebitDocument != null) {
//                        Double totalAmount = Optional.ofNullable(trialDebitDocument.getTotalamount()).orElse(0.0);
//                        Double adjustedAmount = Optional.ofNullable(trialDebitDocument.getAdjustedAmount()).orElse(0.0);
//                        double difference = totalAmount - adjustedAmount;
//                        shorter.setAmount(Math.abs(difference) < 0.1 ? 0.0 : difference);
//                        shorter.setCustomerUsername(customers.getUsername());
//                        shorter.setMvnoId(customers.getMvnoId());
//                        shorter.setInvoiceId(trialDebitDocument.getId());
//                        shorter.setHash(hash);
//                        shorter.setPlanName(String.valueOf(latestPlan[1]));
//                        shorter.setPlanDueDate(((Timestamp) latestPlan[2]).toLocalDateTime());
//                    }
//                }
//
//            }
//
//        } else {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer is not found by given Id", null);
//        }

        Customers customer = customersRepository.findById(shorter.getCustId())
                .orElseThrow(() -> new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Customer not found by given Id", null));

        shorter.setCustomerUsername(customer.getUsername());
        shorter.setMvnoId(customer.getMvnoId());
        String status = customer.getStatus();
        boolean isActive = status.equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVE) ||
                status.equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.INAVCTIVE) ||
                status.equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.SUSPEND);
        boolean isNewActivation = status.equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION) ||
                status.equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING);

        if (isActive || isNewActivation) {
            Object[] latestPlan = subscriberService.getActivePlanListByCustomerId(shorter.getCustId()).stream().findFirst().orElse(null);
            if (latestPlan != null) {
                shorter.setPlanId(((BigInteger) latestPlan[0]).intValue());
                shorter.setPlanName(String.valueOf(latestPlan[1]));
                shorter.setPlanDueDate(((Timestamp) latestPlan[2]).toLocalDateTime());
            }

            if (shorter.getLinkType().equals(CommonConstants.RENEW_PAYMENT) && isActive) {
                List<Object[]> renewPlans = subscriberService.getRenewPlanListByCustomerId(shorter.getCustId());
                BigDecimal value = (BigDecimal) renewPlans.get(0)[3];
                shorter.setAmount(value != null ? value.doubleValue() : 0.0);
            } else if (shorter.getLinkType().equals(CommonConstants.CURRENT_PAYMENT)) {
                if (isActive){
                    DebitDocument debitDocument = subscriberService.getLatestDebitDocument(revenueClient.getDebitDocumentByCustId(shorter.getCustId(), shorter.getToken())) ;
                    if (debitDocument != null) {
                        double difference = Optional.ofNullable(debitDocument.getTotalamount()).orElse(0.0) -
                                Optional.ofNullable(debitDocument.getAdjustedAmount()).orElse(0.0);
                        shorter.setAmount(Math.abs(difference) < 0.1 ? 0.0 : difference);
                        shorter.setInvoiceId(debitDocument.getId());
                    } else {
                        List<Object[]> renewPlans = subscriberService.getRenewPlanListByCustomerId(shorter.getCustId());
                        BigDecimal value = (BigDecimal) renewPlans.get(0)[3];
                        shorter.setAmount(value != null ? value.doubleValue() : 0.0);
                    }
                } else if(isNewActivation){
                    TrialDebitDocument debitDocument = subscriberService.getLatestTrailDebitDocument(revenueClient.getTrailDebitDocumentByCustId(shorter.getCustId(), shorter.getToken()));
                    if (debitDocument != null) {
                        double difference = Optional.ofNullable(debitDocument.getTotalamount()).orElse(0.0) -
                                Optional.ofNullable(debitDocument.getAdjustedAmount()).orElse(0.0);
                        shorter.setAmount(Math.abs(difference) < 0.1 ? 0.0 : difference);
                        shorter.setInvoiceId(debitDocument.getId());
                    } else {
                        List<Object[]> renewPlans = subscriberService.getRenewPlanListByCustomerId(shorter.getCustId());
                        BigDecimal value = (BigDecimal) renewPlans.get(0)[3];
                        shorter.setAmount(value != null ? value.doubleValue() : 0.0);
                    }
                }
            }
        }


//        if (Boolean.TRUE.equals(shorter.getIshashused())) {
//            response.put("status", HttpStatus.IM_USED.value());
//            response.put("message", "Link is already used. Please generate the link again.");
//            return response;
//        }
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Payment details found successfully.");
        response.put("paymentDetails", shorter);

        return response;
    }

    public Map<String, Object> setUsedHash(String hash) {
        Map<String, Object> response = new HashMap<>();
        Shorter shorter = shorterRepository.findByHash(hash).orElse(null);
        if (shorter != null) {
            shorter.setIshashused(true);
            shorterRepository.save(shorter);
        }
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Payment details found successfully.");
        response.put("paymentDetails", shorter);
        return response;
    }
}
