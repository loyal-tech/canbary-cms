package com.adopt.apigw.soap.Services;

import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.CustQuotaDetails;
import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.qosPolicy.model.QOSPolicyDTO;
import com.adopt.apigw.modules.subscriber.model.CustomerPlansModel;
import com.adopt.apigw.modules.subscriber.model.DateOverrideDto;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqModel;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.utils.CommonConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class IntegrationDataService {

  /*  @Transactional
    public CustPlanMappping saveCustPlanMapping(CustomersPojo subscriber, PostpaidPlan postpaidPlanObj, Customers customers, Integer custServiceMappingId) {
        try {
            CustPlanMappping custPlanMappping = null;

            CustPlanMapppingPojo planMapppingPojo = new CustPlanMapppingPojo();
            Integer validity = customersService.calculatePlanValidityDays(subscriber, postpaidPlanObj, null, LocalDateTime.now());
            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            LocalDateTime expDate = null;

            PostpaidPlanPojo plan = postpaidPlanMapper.domainToDTO(postpaidPlanObj, new CycleAvoidingMappingContext());
            planMapppingPojo.setCustomer(subscriber);
            planMapppingPojo.setCustid(subscriber.getId());
            planMapppingPojo.setDiscount(model.getDiscount());

            if(subscriber.getAddressList().isEmpty())
            {
                List<CustomerAddressPojo> customerAddresses = new ArrayList<>();
                CustomerAddressPojo customerAddress1 = new CustomerAddressPojo();
                customerAddress1.setAddressType("Present");
                customerAddress1.setStateId(1);
                customerAddress1.setCustomer(subscriber);
                customerAddresses.add(customerAddress1);
                subscriber.setAddressList(customerAddresses);


            }

            planMapppingPojo.setBillableCustomerId(subscriber.getBillableCustomerId());

            subscriber.setLastBillDate(LocalDate.now());
            subscriber.setNextBillDate(LocalDate.now().plusDays(validity));


            // Set Expiry Date of Plan
            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
                planMapppingPojo.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_ADMIN);
            } else {
                planMapppingPojo.setPurchaseFrom(SubscriberConstants.PURCHASE_FROM_PARTNER);
            }
            planMapppingPojo.setPurchaseType(SubscriberConstants.PURCHASE_TYPE_NEW);
            String planName = postpaidPlanObj.getName();
            TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO(postpaidPlanObj.getId(), subscriber.getAddressList().stream().filter(data -> data.getAddressType().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT)).findAny().orElse(null).getStateId(), null, null);
            Double taxAmount = taxService.taxCalculationByPlan(taxDetailCountReqDTO);


            if (dateOverrideDtos != null) {
                boolean dateOverrideFlag = dateOverrideDtos.isDateOverrideFlag();
                if(dateOverrideFlag)
                {
                    startDate=dateOverrideDtos.getChangePlanStartDate();
                    expDate=dateOverrideDtos.getChangePlanEndDate();
                    validity = Math.toIntExact(ChronoUnit.DAYS.between(startDate, expDate));

                }
            }
            else {
                startDate=LocalDateTime.now();
                expDate=customersService.calculateExpiryDate(subscriber, null, postpaidPlanObj, Long.valueOf(validity));;
            }

            planMapppingPojo.setPlanValidityDays(validity);
            Optional<CustomerServiceMapping> customerServiceMapping = customerServiceMappingRepository.findById(custServiceMappingId);
            if (null != postpaidPlanObj && planMapppingPojo.getExpiryDate() == null) {
                planMapppingPojo.setStartDate(startDate);
                if (changePlanDate){
                    planMapppingPojo.setStartDate(customers.getNextBillDate().atStartOfDay().minusSeconds(1l));
                }
//                expDate = customersService.calculateExpiryDate(subscriber, null, postpaidPlanObj, Long.valueOf(validity));

                Integer parentCustomerId = null;
                if (Objects.nonNull(customers.getParentCustomers())) {
                    if (customerServiceMapping.isPresent()) {
                        if (customerServiceMapping.get().getInvoiceType().equalsIgnoreCase("Group"))
                            parentCustomerId = customers.getParentCustomers().getId();
                    }
                }
                List<CustomerPlansModel> parentActiveCustPlanModelList = null;
                List<CustomerPlansModel> activeCustPlanModelList = this.subscriberService.getActivePlanList(customers.getId(), false);
                if (parentCustomerId != null) {
                    parentActiveCustPlanModelList = this.subscriberService.getActivePlanList(parentCustomerId, false);
                    //parent customer expiray date
                    Customers parentCustomer = this.subscriberService.get(parentCustomerId);
                    LocalDateTime parentMaxExpiryDate = getParentMaxExpiryDateByService(plan.getServiceId(), parentCustomer);
                    startDate = planMapppingPojo.getStartDate();
                    endDate = planMapppingPojo.getEndDate();
                    if (changePlanDate){
                        startDate = customers.getNextBillDate().atStartOfDay().minusSeconds(1l);
                    }
                    *//* if parent and child have same service *//*
                    if (parentActiveCustPlanModelList.size() > 0) {
                        if (parentActiveCustPlanModelList.get(0).getServiceId().equals(postpaidPlanObj.getServiceId())) {
                            if (parentMaxExpiryDate != null) {
                                *//*  checking start date of childs plan with parents end date of a plan  *//*
                                if (startDate.compareTo(parentMaxExpiryDate) < 0) {
                                    if (parentMaxExpiryDate.compareTo(expDate) > 0) {
                                        expDate = calculateExpiryDate(subscriber, postpaidPlanObj, startDate);
                                    } else if (parentMaxExpiryDate.compareTo(expDate) <= 0) {
                                        expDate = parentMaxExpiryDate;
                                    }
                                }
                            }
                        }
                    }
                }
                if (!postpaidPlanObj.getUnitsOfValidity().equalsIgnoreCase("Hours")) {
                    expDate = LocalDateTime.of(expDate.toLocalDate(), LocalTime.now());
                }
                planMapppingPojo.setEndDate(expDate);
                planMapppingPojo.setExpiryDate(expDate);
                subscriber.setLastBillDate(LocalDate.now());
                subscriber.setNextBillDate(expDate.toLocalDate());

                Long prorate_validity = ChronoUnit.DAYS.between(planMapppingPojo.getStartDate(), planMapppingPojo.getEndDate());
                planMapppingPojo.setValidity(prorate_validity.doubleValue());
                if (subscriber.getInvoiceType() != null)
                    planMapppingPojo.setOfferPrice(subscriber.getInvoiceType().equals(CommonConstants.INVOICE_TYPE_GROUP) ? (postpaidPlanObj.getOfferprice() / planMapppingPojo.getPlanValidityDays()) * prorate_validity : postpaidPlanObj.getOfferprice());
                else
                    planMapppingPojo.setOfferPrice(postpaidPlanObj.getOfferprice());

                planMapppingPojo.setTaxAmount(taxAmount);
                if (null != postpaidPlanObj.getQospolicy()) {
                    planMapppingPojo.setQospolicyId(postpaidPlanObj.getQospolicy().getId());
                }
            }

            if (model.isBillToOrg() && model.getNewAmount() != null) {
                planMapppingPojo.setNewAmount(model.getNewAmount());
                planMapppingPojo.setIsInvoiceToOrg(model.isBillToOrg());
                planMapppingPojo.setBillTo(com.adopt.apigw.constants.Constants.ORGANIZATION);
            }

            Optional<PlanService> services = planServiceRepository.findById(plan.getServiceId());
            if (services.isPresent()) {
                if (services.get().getExpiry() != null) {
                    if (services.get().getExpiry().equalsIgnoreCase(com.adopt.apigw.constants.Constants.AT_MIDNIGHT)) {
                        planMapppingPojo.setExpiryDate(planMapppingPojo.getExpiryDate().toLocalDate().atTime(LocalTime.MAX).minusSeconds(1));
                        planMapppingPojo.setEndDate(planMapppingPojo.getEndDate().toLocalDate().atTime(LocalTime.MAX).minusSeconds(1));
                    }
                }
                planMapppingPojo.setService(services.get().getName());
            }
            QOSPolicyDTO qosPolicyDTO = null;
            if (null != plan && null != plan.getQuotatype()) {

                if (!plan.getQuotatype().equalsIgnoreCase(CommonConstants.DID_QUOTA_TYPE) && !plan.getQuotatype().equalsIgnoreCase(CommonConstants.INTERCOM_QUOTA_TYPE) && !plan.getQuotatype().equalsIgnoreCase(CommonConstants.VOICE__BOTH_QUOTA_TYPE)) {
                    Double totalQuotaForSeconds = 0.0;
                    Double totalQuotaForKB = 0.0;
                    if (null != plan.getQuotaunittime() && plan.getQuotaunittime().equalsIgnoreCase(com.adopt.apigw.constants.Constants.MINUTE)) {
                        totalQuotaForSeconds = plan.getQuotatime() * 60;
                    }
                    if (null != plan.getQuotaunittime() && plan.getQuotaunittime().equalsIgnoreCase(com.adopt.apigw.constants.Constants.HOUR)) {
                        totalQuotaForSeconds = plan.getQuotatime() * 60 * 60;
                    }
                    if (null != plan.getQuotaUnit() && plan.getQuotaUnit().equalsIgnoreCase(com.adopt.apigw.constants.Constants.MB)) {
                        totalQuotaForKB = (double) plan.getQuota() * 1024;
                    }
                    if (null != plan.getQuotaUnit() && plan.getQuotaUnit().equalsIgnoreCase(com.adopt.apigw.constants.Constants.GB)) {
                        totalQuotaForKB = (double) plan.getQuota() * 1024 * 1024;
                    }
                    CustQuotaDtlsPojo quotaDetails = null;
                    if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.TIME_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), 0.0, 0.0, null, plan.getQuotatime(), 0.0, plan.getQuotaunittime(), 0.0, 0.0, 0.0, totalQuotaForSeconds, subscriber, plan.getName(), plan.getPlanGroup());
                    }
                    if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.DATA_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), (double) plan.getQuota(), 0.0, plan.getQuotaUnit(), 0.0, 0.0, null, totalQuotaForKB, 0.0, 0.0, 0.0, subscriber, plan.getName(), plan.getPlanGroup());
                    }
                    if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.BOTH_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), (double) plan.getQuota(), 0.0, plan.getQuotaUnit(), plan.getQuotatime(), 0.0, plan.getQuotaunittime(), totalQuotaForKB, 0.0, 0.0, totalQuotaForSeconds, subscriber, plan.getName(), plan.getPlanGroup());
                    }
                    if (plan.getQospolicyid() != null) {
                        QOSPolicyDTO qosPolicy = qosPolicyService.getEntityForUpdateAndDelete(plan.getQospolicyid());
                        if (Objects.nonNull(qosPolicyDTO) && qosPolicyDTO.getDownstreamprofileuid() != null) {
                            quotaDetails.setDownstreamprofileuid(qosPolicyDTO.getDownstreamprofileuid());
                            quotaDetails.setUpstreamprofileuid(qosPolicyDTO.getUpstreamprofileuid());
                        }
                    }
                    quotaDetails.setPlanId(postpaidPlanObj.getId());
                    quotaDetails.setLastQuotaReset(LocalDateTime.now());
                    if (plan.isUseQuota()) {
                        quotaDetails.setChunkAvailable(plan.isUseQuota());
                    } else {
                        quotaDetails.setChunkAvailable(false);
                    }
                    if (plan.getChunk() != null) {
                        quotaDetails.setReservedQuotaInPer(plan.getChunk());
                    } else {
                        quotaDetails.setReservedQuotaInPer(0.0);
                    }
                    if(postpaidPlanObj.getUsageQuotaType() != null) {
                        quotaDetails.setUsageQuotaType(postpaidPlanObj.getUsageQuotaType());
                    } else {
                        quotaDetails.setUsageQuotaType("TOTAL");
                    }
                    planMapppingPojo.setQuotaList(Collections.singletonList(quotaDetails));
//                    planMapppingPojo.setPlanValidityDays(customersService.calculatePlanValidityDays(subscriber, postpaidPlanObj, Long.valueOf(validity), LocalDateTime.now()));
                    //planMapppingPojo.setService(postpaidPlanObj.getServiceName());
                    planMapppingPojo.setPlanId(plan.getId());
                    if (subscriber.getIsinvoicestop()) {
                        planMapppingPojo.setIsInvoiceCreated(false);
                        planMapppingPojo.setIsinvoicestop(true);
                    }
                    if (custServiceMappingId == null) {
                        CustomerServiceMapping mapping = new CustomerServiceMapping();
                        mapping.setServiceId(Long.valueOf(postpaidPlanObj.getServiceId()));
                        mapping.setCustId(subscriber.getId());
//                        mapping = customersService.generateConnectionNumber(mapping);
                        Boolean isLCO = customers.getLcoId() != null ? true :false;
                        String connectionNo = numberSequenceUtil.getConnectionNumber(isLCO, customers.getLcoId(), customers.getMvnoId());
                        mapping.setConnectionNo(connectionNo);
                        planMapppingPojo.setCustServiceMappingId(customerServiceMapping.get().getId());
                    } else {
                        if (customerServiceMapping.isPresent()) {
                            if (customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION))
                                customerServiceMapping.get().setStatus(CommonConstants.CUSTOMER_STATUS_NEW_ACTIVATION);
                            else
                                customerServiceMapping.get().setStatus(CommonConstants.ACTIVE_STATUS);
                            customerServiceMapping.get().setCustId(planMapppingPojo.getCustid());
                            planMapppingPojo.setCustServiceMappingId(customerServiceMapping.get().getId());
                            customerServiceMappingRepository.save(customerServiceMapping.get());
                        }
                    }
//                    if (Objects.isNull(plangroupId)) {
//                        if (model.getPlanId() != null) {
//                            String invoiceType = customerServiceMappingRepository.findInvoiceTypeByCustServiceId(model.getCustServiceMappingId());
//                            if (invoiceType != null) {
//                                planMapppingPojo.setInvoiceType(invoiceType);
//                            }
//                        }
//                    } else {
//                        if (custPlanMappingRepository.findAllByCustServiceMappingId(custServiceMappingId).get(0).getInvoiceType() != null) {
//                            planMapppingPojo.setInvoiceType(custPlanMappingRepository.findAllByCustServiceMappingId(custServiceMappingId).get(0).getInvoiceType());
//                        }
//                    }
                    String invoiceType = customerServiceMappingRepository.findInvoiceTypeByCustServiceId(model.getCustServiceMappingId());
                    if (invoiceType != null) {
                        planMapppingPojo.setInvoiceType(invoiceType);
                    }
                    if (null != plan && plan.getQospolicyid() != null) {
                        qosPolicyDTO = qosPolicyService.getEntityForUpdateAndDelete(plan.getQospolicyid());
                        if (Objects.nonNull(qosPolicyDTO) && qosPolicyDTO.getDownstreamprofileuid() != null) {
                            quotaDetails.setDownstreamprofileuid(qosPolicyDTO.getDownstreamprofileuid());
                            quotaDetails.setUpstreamprofileuid(qosPolicyDTO.getUpstreamprofileuid());
                        }
                    }
                    planMapppingPojo.setRenewalId(renewalId);
                    if(changePlanDate)
                        event = CommonConstants.EVENTCONSTANTS.RENEW_PLAN;
                    custPlanMappping = custPlanMappingService.save(custPlanMappingService.convertDTOToDomain(planMapppingPojo), event);
                    planMapppingPojo.setId(custPlanMappping.getId());
                    subscriber.setCustPackageId(custPlanMappping.getId());
                    if (plangroupId != null)
                        customersService.saveCustomerChargeHistory(plan, subscriber, custPlanMappping, plangroupId, false,changePlanDate,null);
                    else customersService.saveCustomerChargeHistory(plan, subscriber, custPlanMappping, null, false,changePlanDate,null);

                } else {
                    CustQuotaDtlsPojo quotaDetails = null;
                    if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.DID_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), plan.getQuotadid(), 0.0, 0.0, 0.0, subscriber, plan.getQuotaunitdid(), null);
                    } else if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.INTERCOM_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), 0.0, 0.0, plan.getQuotaintercom(), 0.0, subscriber, null, plan.getQuotaunitintercom());
                    } else if (plan.getQuotatype().equalsIgnoreCase(CommonConstants.VOICE__BOTH_QUOTA_TYPE)) {
                        quotaDetails = new CustQuotaDtlsPojo(planMapppingPojo, plan.getId(), plan.getQuotatype(), plan.getQuotadid(), 0.0, plan.getQuotaintercom(), 0.0, subscriber, plan.getQuotaunitdid(), plan.getQuotaunitintercom());
                    }
                    quotaDetails.setPlanId(postpaidPlanObj.getId());
                    quotaDetails.setLastQuotaReset(LocalDateTime.now());
                    if(postpaidPlanObj.getUsageQuotaType() != null) {
                        quotaDetails.setUsageQuotaType(postpaidPlanObj.getUsageQuotaType());
                    } else {
                        quotaDetails.setUsageQuotaType("TOTAL");
                    }
                    planMapppingPojo.setQuotaList(Collections.singletonList(quotaDetails));
//                    planMapppingPojo.setPlanValidityDays(customersService.calculatePlanValidityDays(subscriber, postpaidPlanObj, Long.valueOf(validity), LocalDateTime.now()));
                    planMapppingPojo.setPlangroupid(plangroupId);
                    planMapppingPojo.setCustServiceMappingId(custServiceMappingId);
                    planMapppingPojo.setRenewalId(renewalId);
                    custPlanMappping = custPlanMappingService.save(custPlanMappingService.convertDTOToDomain(planMapppingPojo), "");
                    planMapppingPojo.setId(custPlanMappping.getId());
                    subscriber.setCustPackageId(custPlanMappping.getId());
                    custPlanMappping.setStatus("1");
                    if (plangroupId != null)
                        customersService.saveCustomerChargeHistory(plan, subscriber, custPlanMappping, plangroupId, false, changePlanDate,null);
                    else customersService.saveCustomerChargeHistory(plan, subscriber, custPlanMappping, null, false, changePlanDate,null);
                }
            }
            if (!CollectionUtils.isEmpty(custPlanMappping.getQuotaList())) {
                List<CustQuotaDetails> list = custPlanMappping.getQuotaList();
                if (!CollectionUtils.isEmpty(list)) {
                    CustPlanMappping finalCustPlanMappping = custPlanMappping;
                    list.forEach(x -> x.setLastQuotaReset(LocalDateTime.now()));
                    list.forEach(custQuotaDetails -> custQuotaDetails.setCustPlanMappping(finalCustPlanMappping));
                    custQuotaRepository.saveAll(list);
                }
            }
            if (!CollectionUtils.isEmpty(custPlanMappping.getQuotaList())) {
                List<CustQuotaDetails> list = custPlanMappping.getQuotaList();
                if (!CollectionUtils.isEmpty(list)) {
                    CustPlanMappping finalCustPlanMappping = custPlanMappping;
                    list.forEach(x -> x.setLastQuotaReset(LocalDateTime.now()));
                    list.forEach(custQuotaDetails -> custQuotaDetails.setCustPlanMappping(finalCustPlanMappping));
                    custQuotaRepository.saveAll(list);
                }
            }
            customerServiceMapping.get().setCustId(custPlanMappping.getCustomer().getId());
            customerServiceMappingRepository.save(customerServiceMapping.get());
            return custPlanMappping;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (Exception ex) {
            throw new RuntimeException("Exception on change plan: " + ex.getMessage());
        }
    }
*/
}
