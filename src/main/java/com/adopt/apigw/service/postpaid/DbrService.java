package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.QCustomers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.CronJobs.CronjobConst;
import com.adopt.apigw.modules.CustomerDBR.domain.*;
import com.adopt.apigw.modules.CustomerDBR.model.CustomDailyRevenue;
import com.adopt.apigw.modules.CustomerDBR.model.CustomMonthlyRevenue;
import com.adopt.apigw.modules.CustomerDBR.model.QCustomDailyRevenue;
import com.adopt.apigw.modules.CustomerDBR.model.QCustomMonthlyRevenue;
import com.adopt.apigw.modules.CustomerDBR.repository.*;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedgerDetails;
import com.adopt.apigw.modules.PartnerLedger.domain.QPartnerLedgerDetails;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerLedgerDetailsRepository;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.modules.subscriber.model.DeactivatePlanReqModel;
import com.adopt.apigw.nepaliCalendarUtils.model.NepaliDateDTO;
import com.adopt.apigw.nepaliCalendarUtils.service.DateConverterService;
import com.adopt.apigw.pojo.AggregateCount;
import com.adopt.apigw.pojo.api.CustChargeDetailsPojo;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.schedulerAudit.SchedulerAudit;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.service.SchedulerLockService;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.StatusConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DbrService {
    private static final Logger logger = LoggerFactory.getLogger(DbrService.class);

    public static final String MODULE = " [DbrService] ";

    @Autowired
    private CustomersService customersService;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerChargeHistoryRepo customerChargeHistoryRepo;

    @Autowired
    private TaxService taxService;

    @Autowired
    private CustomerDBRRepository customerDBRRepository;

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private CustomDailyRevenueRepository customDailyRevenueRepository;

    @Autowired
    private CustomMonthlyRevenueRepository customMonthlyRevenueRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private CustChargeRepository custChargeRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    PartnerCommissionService partnerCommissionService;

    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    private DateConverterService dateConverterService;

    @Autowired
    private PartnerLedgerDetailsRepository partnerLedgerDetailsRepository;

    @Autowired
    private TempPartnerLedgerDetailsRepository tempPartnerLedgerDetailsRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private PartnerCommissionRepository partnerCommissionRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    DebitDocDetailRepository debitDocDetailRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    CustomerChargeDBRRepository customerChargeDBRRepository;

    @Autowired
    CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    TempCustomerDBRRepository tempCustomerDBRRepository;

    @Autowired
    TempCustomerChargeDBRRepository tempCustomerChargeDBRRepository;

    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    public void addDbrForPrepaidCustomerCreation(Integer customerId) {
        DecimalFormat df = new DecimalFormat("0.00");
        Customers customer =  customersRepository.findById(customerId).get();
        CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
        if (customersPojo.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_ACTIVE)) {
            List<CustPlanMapppingPojo> mapppingPojoList = customersPojo.getPlanMappingList();
            List<CustChargeDetailsPojo> directChargeList = customersPojo.getOverChargeList();
            for (int i = 0; i < mapppingPojoList.size(); i++) {
                AtomicReference<Double> offerPrice = new AtomicReference<>(0.0);
                List<CustomerChargeHistory> chargeHistoryList = customerChargeHistoryRepo.findAllChargesByCprId(mapppingPojoList.get(i).getId());
                chargeHistoryList = chargeHistoryList.stream().filter(x -> x.getChargeAmount() > 0).collect(Collectors.toList());
                chargeHistoryList.stream().forEach(data -> {
                    Double chargePriceIncludingTax = data.getChargeAmount();
                    for (int k = 0; k < taxService.get(data.getTaxId(),customer.getMvnoId()).getTieredList().size(); k++)
                        chargePriceIncludingTax = chargePriceIncludingTax + chargePriceIncludingTax * taxService.get(data.getTaxId(),customer.getMvnoId()).getTieredList().get(k).getRate() / 100.0;
                    Double finalChargePriceIncludingTax1 = chargePriceIncludingTax;
                    offerPrice.updateAndGet(v -> v + finalChargePriceIncludingTax1);
                });
                int finalI = i;
//                offerPrice.updateAndGet(v -> v - (v * mapppingPojoList.get(finalI).getDiscount() / 100.0));
                addEntryIntoCustomerDbr(customerId, Double.parseDouble(df.format(offerPrice.get())), mapppingPojoList.get(i), false, false);

                List<CustChargeDetailsPojo> list = directChargeList.stream().filter(x -> x.getPlanid().equals(mapppingPojoList.get(finalI).getPlanId())).collect(Collectors.toList());
                if (list.size() > 0) {
                    for (int k = 0; k < list.size(); k++) {
                        Double directChargeOfferPrice = list.get(k).getPrice();
                        directChargeOfferPrice = directChargeOfferPrice + directChargeOfferPrice * taxService.get(list.get(k).getTaxId(),customer.getMvnoId()).getTieredList().get(0).getRate() / 100.0;
                        if (taxService.get(list.get(k).getTaxId(),customer.getMvnoId()).getTieredList().size() > 1)
                            directChargeOfferPrice = directChargeOfferPrice + directChargeOfferPrice * taxService.get(list.get(k).getTaxId(),customer.getMvnoId()).getTieredList().get(1).getRate() / 100.0;
                        addEntryIntoCustomerDbr(customerId, Double.parseDouble(df.format(directChargeOfferPrice)), mapppingPojoList.get(i), true, !list.get(i).getType().equalsIgnoreCase("One-time"));
                    }
                }
            }
        }
    }

    public void addDbrForPostpaidCustomerForGivenDate(LocalDate date) {
        QCustomers qCustomers = QCustomers.customers;
        BooleanExpression expression = qCustomers.isNotNull();
        expression = expression.and(qCustomers.nextBillDate.eq(date));
        List<Customers> customers = (List<Customers>) customersRepository.findAll(expression);
        customers.stream().forEach(x -> addDbrForPostpaidCustomer(x.getId()));
    }

    public void addDbrForPostpaidCustomer(Integer customerId) {
        DecimalFormat df = new DecimalFormat("0.00");
        Customers customer =  customersRepository.findById(customerId).get();
        CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
        LocalDate nextBillDate = customersPojo.getNextBillDate();
        if (customersPojo.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_ACTIVE)) {
            List<CustomerChargeHistory> chargeHistoryList = customerChargeHistoryRepo.findAllChargesByCustIdAndNextBillDate(customerId, nextBillDate);
            List<Integer> cprIds = chargeHistoryList.stream().map(x -> x.getCustPlanMapppingId()).collect(Collectors.toList());
            List<CustPlanMapppingPojo> mapppingPojoList = customersPojo.getPlanMappingList().stream().filter(x -> cprIds.contains(x.getId())).collect(Collectors.toList());

            for (int i = 0; i < mapppingPojoList.size(); i++) {
                AtomicReference<Double> offerPrice = new AtomicReference<>(0.0);
                int finalI1 = i;
                List<CustomerChargeHistory> chargeHistory = chargeHistoryList.stream().filter(x -> x.getCustPlanMapppingId().equals(mapppingPojoList.get(finalI1).getId())).collect(Collectors.toList());
                chargeHistoryList = chargeHistory.stream().filter(x -> x.getChargeAmount() > 0).collect(Collectors.toList());
                if (!chargeHistoryList.get(0).getIsFirstChargeApply()) {
                    Long planValidityDays = Long.valueOf(mapppingPojoList.get(i).getPlanValidityDays().toString());
                    LocalDate planStartDate = mapppingPojoList.get(i).getStartDate().toLocalDate();
                    LocalDate planEndDate = customer.getNextBillDate();
                    long planDiff = ChronoUnit.DAYS.between(planStartDate, planEndDate);
                    if (planDiff != planValidityDays) {
                        for (int j = 0; j < chargeHistoryList.size(); j++) {
                            Double chargePrice = chargeHistoryList.get(j).getChargeAmount();
                            Double dbr = chargePrice / planValidityDays;
                            Double proratCharge = dbr * planDiff;
                            chargeHistoryList.get(j).setChargeAmount(proratCharge);
                        }
                    }
                }

                chargeHistoryList.stream().forEach(data -> {
                    Double chargePriceIncludingTax = data.getChargeAmount();
                    for (int k = 0; k < taxService.get(data.getTaxId(),customer.getMvnoId()).getTieredList().size(); k++)
                        chargePriceIncludingTax = chargePriceIncludingTax + chargePriceIncludingTax * taxService.get(data.getTaxId(),customer.getMvnoId()).getTieredList().get(k).getRate() / 100.0;
                    Double finalChargePriceIncludingTax1 = chargePriceIncludingTax;
                    offerPrice.updateAndGet(v -> v + finalChargePriceIncludingTax1);
                });
                int finalI = i;
//                offerPrice.updateAndGet(v -> v - (v * mapppingPojoList.get(finalI).getDiscount() / 100.0));
                addEntryIntoCustomerDbrForPostpaid(customerId, Double.parseDouble(df.format(offerPrice.get())), mapppingPojoList.get(i));
            }
        }
    }

    private void addEntryIntoCustomerDbrForPostpaid(Integer customerId, double offerPrice, CustPlanMapppingPojo planMapppingPojo) {
        DecimalFormat df = new DecimalFormat("0.00");
        CustomerDBR customerDBR = new CustomerDBR();
        customerDBR.setCprid(Long.parseLong(planMapppingPojo.getId().toString()));
        customerDBR.setCustid(Long.parseLong(customerId.toString()));
        customerDBR.setPlanid(Long.parseLong(planMapppingPojo.getPlanId().toString()));
        customerDBR.setCustname(planMapppingPojo.getCustomer().getCustname());
        customerDBR.setPlanname(postpaidPlanService.get(planMapppingPojo.getPlanId(),customersService.getMvnoIdFromCurrentStaff(customerId)).getName());
        customerDBR.setCusttype(planMapppingPojo.getCustomer().getCusttype());
        customerDBR.setValidity_days(planMapppingPojo.getPlanValidityDays());
        customerDBR.setOffer_price(Double.parseDouble(df.format(offerPrice)));
        customerDBR.setStartdate(LocalDate.now());
        customerDBR.setStatus(planMapppingPojo.getCustomer().getStatus());
        customerDBR.setEnddate(LocalDate.now());
        customerDBR.setDbr(Double.parseDouble(df.format(offerPrice)));
        customerDBR.setPendingamt(0.0);
        customerDBR.setIsDirectCharge(false);
        customerDBR.setCumm_revenue(offerPrice);
        customerDBRRepository.save(customerDBR);
    }

    public void addDbrForPostpaidCustomerCreation(Integer customerId) {
        Customers customer =  customersRepository.findById(customerId).get();
        CustomersPojo customersPojo = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
        LocalDate nextBillDate = customersPojo.getNextBillDate();
        if (customersPojo.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_ACTIVE)) {
            List<CustPlanMapppingPojo> mapppingPojoList = customersPojo.getPlanMappingList();
            List<CustChargeDetailsPojo> directChargeList = customersPojo.getOverChargeList();
        }
    }

    public void addEntryIntoCustomerDbr(Integer customerId, Double offerPrice, CustPlanMapppingPojo planMapppingPojo, Boolean isDirectCharge, Boolean isRecurringCharge) {
        if (!isDirectCharge || (isDirectCharge && isRecurringCharge)) {
            DecimalFormat df = new DecimalFormat("0.00");
            Double tmpOfferPrice = offerPrice;
            Double cummValue = 0d;
            Double dbr = offerPrice / planMapppingPojo.getPlanValidityDays();
            for (int i = 0; i < planMapppingPojo.getPlanValidityDays(); i++) {
                CustomerDBR customerDBR = new CustomerDBR();
                customerDBR.setCprid(Long.parseLong(planMapppingPojo.getId().toString()));
                customerDBR.setCustid(Long.parseLong(customerId.toString()));
                customerDBR.setPlanid(Long.parseLong(planMapppingPojo.getPlanId().toString()));
                customerDBR.setCustname(planMapppingPojo.getCustomer().getCustname());
                customerDBR.setPlanname(postpaidPlanService.get(planMapppingPojo.getPlanId(),customersService.getMvnoIdFromCurrentStaff(customerId)).getName());
                customerDBR.setCusttype(planMapppingPojo.getCustomer().getCusttype());
                customerDBR.setValidity_days(planMapppingPojo.getPlanValidityDays());
                customerDBR.setOffer_price(Double.parseDouble(df.format(offerPrice).toString()));
                customerDBR.setStartdate(LocalDate.from(planMapppingPojo.getStartDate().plusDays(i)));
                customerDBR.setStatus(planMapppingPojo.getCustomer().getStatus());
                customerDBR.setEnddate(LocalDate.from(planMapppingPojo.getEndDate()));
                customerDBR.setDbr(Double.parseDouble(df.format(dbr).toString()));
                customerDBR.setIsDirectCharge(isDirectCharge);
                customerDBR.setPendingamt(Double.parseDouble(df.format(tmpOfferPrice - dbr).toString()));
                customerDBR.setCumm_revenue(cummValue);
                customerDBRRepository.save(customerDBR);
                tmpOfferPrice = tmpOfferPrice - dbr;
                cummValue = cummValue + dbr;
            }
        } else if (isDirectCharge && !isRecurringCharge) {
            DecimalFormat df = new DecimalFormat("0.00");
            CustomerDBR customerDBR = new CustomerDBR();
            customerDBR.setCprid(Long.parseLong(planMapppingPojo.getId().toString()));
            customerDBR.setCustid(Long.parseLong(customerId.toString()));
            customerDBR.setPlanid(Long.parseLong(planMapppingPojo.getPlanId().toString()));
            customerDBR.setCustname(planMapppingPojo.getCustomer().getCustname());
            customerDBR.setPlanname(postpaidPlanService.get(planMapppingPojo.getPlanId(),customersService.getMvnoIdFromCurrentStaff(customerId)).getName());
            customerDBR.setCusttype(planMapppingPojo.getCustomer().getCusttype());
            customerDBR.setValidity_days(planMapppingPojo.getPlanValidityDays());
            customerDBR.setOffer_price(Double.parseDouble(df.format(offerPrice).toString()));
            customerDBR.setStartdate(LocalDate.now());
            customerDBR.setStatus(planMapppingPojo.getCustomer().getStatus());
            customerDBR.setEnddate(LocalDate.now());
            customerDBR.setDbr(Double.parseDouble(df.format(offerPrice).toString()));
            customerDBR.setPendingamt(0.0);
            customerDBR.setIsDirectCharge(isDirectCharge);
            customerDBR.setCumm_revenue(offerPrice);
            customerDBRRepository.save(customerDBR);
        }
    }

    public void addEntryIntoDirectCharge() {
    }

    public void addDbrForPrepaidCustomerForDirectCharges(CustChargeDetails custChargeDetails, Customers customers) {
        if (customers.getCusttype().equalsIgnoreCase(CommonConstants.CUST_TYPE_PREPAID)) {
            if (!custChargeDetails.getType().equalsIgnoreCase("One-time")) {
                Double tmpOfferPrice = custChargeDetails.getPrice();
                Double cummValue = 0d;
                Long daysDiff = ChronoUnit.DAYS.between(custChargeDetails.getStartdate().toLocalDate(), custChargeDetails.getEnddate().toLocalDate());
                DecimalFormat df = new DecimalFormat("0.00");
                for (int i = 0; i < daysDiff; i++) {

                    tmpOfferPrice = tmpOfferPrice - custChargeDetails.getDbr();
                    cummValue = cummValue + custChargeDetails.getDbr();
                    CustomerDBR customerDBR = new CustomerDBR();
                    customerDBR.setCprid(Long.parseLong(custChargeDetails.getCustPlanMapppingId().toString()));
                    customerDBR.setCustid(Long.parseLong(customers.getId().toString()));
                    customerDBR.setPlanid(Long.parseLong(custChargeDetails.getPlanid().toString()));
                    customerDBR.setCustname(customers.getCustname());
                    customerDBR.setPlanname(postpaidPlanService.get(custChargeDetails.getPlanid(),customers.getMvnoId()).getName());
                    customerDBR.setCusttype(customers.getCusttype());
                    customerDBR.setValidity_days(daysDiff.intValue());
                    customerDBR.setOffer_price(Double.parseDouble(df.format(custChargeDetails.getPrice()).toString()));
                    customerDBR.setStartdate(LocalDate.from(custChargeDetails.getStartdate().plusDays(i)));
                    customerDBR.setStatus(customers.getStatus());
                    customerDBR.setEnddate(LocalDate.from(custChargeDetails.getEnddate()));
                    customerDBR.setDbr(Double.parseDouble(df.format(custChargeDetails.getDbr())));
                    customerDBR.setIsDirectCharge(true);
                    customerDBR.setPendingamt(Double.parseDouble(df.format(tmpOfferPrice)));
                    customerDBR.setCumm_revenue(cummValue);
                    customerDBRRepository.save(customerDBR);
                }
            } else {
                DecimalFormat df = new DecimalFormat("0.00");
                CustomerDBR customerDBR = new CustomerDBR();
                customerDBR.setCprid(Long.parseLong(custChargeDetails.getCustPlanMapppingId().toString()));
                customerDBR.setCustid(Long.parseLong(customers.getId().toString()));
                customerDBR.setPlanid(Long.parseLong(custChargeDetails.getPlanid().toString()));
                customerDBR.setCustname(customers.getCustname());
                customerDBR.setPlanname(postpaidPlanService.get(custChargeDetails.getPlanid(),customers.getMvnoId()).getName());
                customerDBR.setCusttype(customers.getCusttype());
                customerDBR.setValidity_days(custChargeDetails.getPlanValidity());
                customerDBR.setOffer_price(Double.parseDouble(df.format(custChargeDetails.getPrice()).toString()));
                customerDBR.setStartdate(LocalDate.now());
                customerDBR.setStatus(customers.getStatus());
                customerDBR.setEnddate(LocalDate.now());
                customerDBR.setDbr(Double.parseDouble(df.format(custChargeDetails.getDbr())));
                customerDBR.setIsDirectCharge(true);
                customerDBR.setPendingamt(0.0);
                customerDBR.setCumm_revenue(custChargeDetails.getDbr());
                customerDBRRepository.save(customerDBR);
            }
        }
    }

    public void addDbrForPrepaidCustomerForChangePlan(CustPlanMappping mapping, Customers customer) {
        DecimalFormat df = new DecimalFormat("0.00");
        AtomicReference<Double> offerPrice = new AtomicReference<>(0.0);
        List<CustomerChargeHistory> chargeHistoryList = customerChargeHistoryRepo.findAllChargesByCprId(mapping.getId());
        chargeHistoryList = chargeHistoryList.stream().filter(x -> x.getChargeAmount() > 0).collect(Collectors.toList());
        chargeHistoryList.stream().forEach(data -> {
            Double chargePriceIncludingTax = data.getChargeAmount();
            for (int k = 0; k < taxService.get(data.getTaxId(),customer.getMvnoId()).getTieredList().size(); k++)
                chargePriceIncludingTax = chargePriceIncludingTax + chargePriceIncludingTax * taxService.get(data.getTaxId(),customer.getMvnoId()).getTieredList().get(k).getRate() / 100.0;
            Double finalChargePriceIncludingTax1 = chargePriceIncludingTax;
            offerPrice.updateAndGet(v -> v + finalChargePriceIncludingTax1);
        });
//        offerPrice.updateAndGet(v -> v - (v * mapping.getDiscount() / 100.0));
        addEntryIntoCustomerDbr(customer.getId(), Double.parseDouble(df.format(offerPrice.get())), customerMapper.mapCustPlanMapToCustPlanMapPojo(mapping, new CycleAvoidingMappingContext()), false, false);
    }

    @Scheduled(cron = "${cronjobtimeforrevenueeverydaymidnight}")
    public void addDayWiseRevenue() {
        log.info("XXXXXXXXXXXX----------CRON RevMid Night Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.REVENUE_DAY_MID_NIGHT_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.REVENUE_DAY_MID_NIGHT)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.REVENUE_DAY_MID_NIGHT);
            try {
                LocalDate currentDate = LocalDate.now().minusDays(1l);
                List<AggregateCount> aggregateList = customerDBRRepository.getAllByAggregateByDate(currentDate);
                if (aggregateList != null && !aggregateList.isEmpty()) {
                    aggregateList.stream().forEach(y -> {
                        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
                        BooleanExpression expression = qCustomerDBR.isNotNull();
                        expression = expression.and(qCustomerDBR.startdate.eq(currentDate));
                        if (y.getMvnoId() != null)
                            expression = expression.and(qCustomerDBR.mvnoId.eq(y.getMvnoId().intValue()));
                        else
                            expression = expression.and(qCustomerDBR.mvnoId.isNull());

                        if (y.getBuId() != null)
                            expression = expression.and(qCustomerDBR.buId.eq(y.getBuId()));
                        else
                            expression = expression.and(qCustomerDBR.buId.isNull());

                        if (y.getServiceAreaId() != null)
                            expression = expression.and(qCustomerDBR.serviceArea.eq(y.getServiceAreaId()));
                        else
                            expression = expression.and(qCustomerDBR.serviceArea.isNull());

                        List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.findAll(expression);
                        Double revenue = dbrList.stream().mapToDouble(x -> x.getDbr()).sum();
                        Double outstanding = dbrList.stream().mapToDouble(x -> x.getPendingamt()).sum();
                        CustomDailyRevenue dailyRevenue = new CustomDailyRevenue();
                        dailyRevenue.setDate(currentDate);
                        dailyRevenue.setRevenue(revenue);
                        dailyRevenue.setOutstanding(outstanding);
                        if (y.getMvnoId() != null)
                            dailyRevenue.setMvnoId(y.getMvnoId().intValue());
                        else
                            dailyRevenue.setMvnoId(null);

                        if (y.getBuId() != null)
                            dailyRevenue.setBuId(y.getBuId());
                        else
                            dailyRevenue.setBuId(null);

                        if (y.getServiceAreaId() != null)
                            dailyRevenue.setServiceAreaId(y.getServiceAreaId());
                        else
                            dailyRevenue.setServiceAreaId(null);
                        customDailyRevenueRepository.save(dailyRevenue);
                    });
                    schedulerAudit.setEndTime(LocalDateTime.now());
                    schedulerAudit.setDescription("Mid Night Revenue Success");
                    schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                    schedulerAudit.setTotalCount(aggregateList.size());
                }
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.REVENUE_DAY_MID_NIGHT);
                log.info("XXXXXXXXXXXX---------- RevMid Night Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("RevMid Night Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------RevMid Night Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

    @Scheduled(cron = "${cronjobtimeforeverymonthfirstday}")
    public void addMonthWiseRevenue() {
        log.info("XXXXXXXXXXXX----------CRON Every Month First Day Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.EVERY_MONTH_FIRST_DAY_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.EVERY_MONTH_FIRST_DAY)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.EVERY_MONTH_FIRST_DAY);
            try {
                List<AggregateCount> aggregateList = customDailyRevenueRepository.getAllByAggregateByDate();
                LocalDate currentDate = LocalDate.now();
                Integer month = currentDate.getMonth().getValue();
                Integer year = currentDate.getYear();
                aggregateList.stream().forEach(y -> {
                    QCustomDailyRevenue dailyRevenue = QCustomDailyRevenue.customDailyRevenue;
                    BooleanExpression expression = dailyRevenue.isNotNull();

                    if (y.getMvnoId() != null)
                        expression = expression.and(dailyRevenue.mvnoId.eq(y.getMvnoId().intValue()));
                    else
                        expression = expression.and(dailyRevenue.mvnoId.isNull());

                    if (y.getBuId() != null)
                        expression = expression.and(dailyRevenue.buId.eq(y.getBuId()));
                    else
                        expression = expression.and(dailyRevenue.buId.isNull());

                    if (y.getServiceAreaId() != null)
                        expression = expression.and(dailyRevenue.serviceAreaId.eq(y.getServiceAreaId()));
                    else
                        expression = expression.and(dailyRevenue.serviceAreaId.isNull());

                    List<CustomDailyRevenue> dailyRevenues = (List<CustomDailyRevenue>) customDailyRevenueRepository.findAll(expression);
                    Double revenue = dailyRevenues.stream().filter(x -> x.getDate().getMonth().getValue() == month && x.getDate().getYear() == year).mapToDouble(x -> x.getRevenue()).sum();
                    Double outstanding = dailyRevenues.stream().mapToDouble(x -> x.getOutstanding()).sum();
                    CustomMonthlyRevenue monthlyRevenue = new CustomMonthlyRevenue();
                    monthlyRevenue.setMonth(currentDate.getMonthValue());
                    monthlyRevenue.setYear(year.toString());
                    monthlyRevenue.setRevenue(revenue);
                    monthlyRevenue.setOutstanding(outstanding);
                    if (y.getMvnoId() != null)
                        monthlyRevenue.setMvnoId(y.getMvnoId().intValue());
                    else
                        monthlyRevenue.setMvnoId(null);

                    if (y.getBuId() != null)
                        monthlyRevenue.setBuId(y.getBuId());
                    else
                        monthlyRevenue.setBuId(null);

                    if (y.getServiceAreaId() != null)
                        monthlyRevenue.setServiceAreaId(y.getServiceAreaId());
                    else
                        monthlyRevenue.setServiceAreaId(null);
                    customMonthlyRevenueRepository.save(monthlyRevenue);
                });
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription("Every Month First Day Scheduler Success");
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                schedulerAudit.setTotalCount(aggregateList.size());
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.EVERY_MONTH_FIRST_DAY);
                log.info("XXXXXXXXXXXX---------- Every Month First Day Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("REvery Month First Day Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Every Month First Day Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

    public List<CustomDailyRevenue> getDailyDbrDeatils(LocalDate startdate, LocalDate endate) {

        QCustomDailyRevenue qCustomDailyRevenue = QCustomDailyRevenue.customDailyRevenue;
        BooleanExpression expression = qCustomDailyRevenue.isNotNull();
        // TODO: pass mvnoID manually 6/5/2025
        Integer mvnoId = customersService.getLoggedInMvnoId(null);
        List<Long> buIdsList = customersService.getBUIdsFromCurrentStaff();
        List<Long> serviceAreaIdsList = customersService.getLoggedInUser().getServiceAreaIdList().stream().map(x -> x.longValue()).collect(Collectors.toList());

        if (mvnoId != null)
            expression = expression.and(qCustomDailyRevenue.mvnoId.eq(mvnoId));
        else
            expression = expression.and(qCustomDailyRevenue.mvnoId.isNull());

        if (buIdsList != null && !buIdsList.isEmpty())
            expression = expression.and(qCustomDailyRevenue.buId.in(buIdsList));

        if (serviceAreaIdsList != null && !serviceAreaIdsList.isEmpty())
            expression = expression.and(qCustomDailyRevenue.serviceAreaId.in(serviceAreaIdsList));

        expression = expression.and(qCustomDailyRevenue.date.between(startdate, endate));

        //List<CustomDailyRevenue> dailyRevenues = (List<CustomDailyRevenue>) customDailyRevenueRepository.getAllDbrBetweenStartDateAndEndDate(startdate, endate);
        List<CustomDailyRevenue> dailyRevenues = (List<CustomDailyRevenue>) customDailyRevenueRepository.findAll(expression);
        return dailyRevenues;
    }

//    public List<CustomMonthlyRevenue> getMonthWiseDbrDeatils() {
//        QCustomMonthlyRevenue qCustomMonthlyRevenue = QCustomMonthlyRevenue.customMonthlyRevenue;
//        BooleanExpression expression = qCustomMonthlyRevenue.isNotNull();
//        Integer mvnoId = customersService.getLoggedInMvnoId();
//        List<Long> buIdsList = customersService.getBUIdsFromCurrentStaff();
//        List<Long> serviceAreaIdsList = customersService.getLoggedInUser().getServiceAreaIdList().stream().map(x -> x.longValue()).collect(Collectors.toList());
//
//        if (mvnoId != null)
//            expression = expression.and(qCustomMonthlyRevenue.mvnoId.eq(mvnoId));
//        else
//            expression = expression.and(qCustomMonthlyRevenue.mvnoId.isNull());
//
//        if (buIdsList != null && !buIdsList.isEmpty())
//            expression = expression.and(qCustomMonthlyRevenue.buId.in(buIdsList));
//
//
//        if (serviceAreaIdsList != null && !serviceAreaIdsList.isEmpty())
//            expression = expression.and(qCustomMonthlyRevenue.serviceAreaId.in(serviceAreaIdsList));
//
//        List<CustomMonthlyRevenue> dailyRevenues = (List<CustomMonthlyRevenue>) customMonthlyRevenueRepository.findAll(expression);
//        return dailyRevenues;
//    }

    public void addDbrForNewService(List<CustPlanMapppingPojo> list, Customers customers) {
        for (int i = 0; i < list.size(); i++) {
            DecimalFormat df = new DecimalFormat("0.00");
            AtomicReference<Double> offerPrice = new AtomicReference<>(0.0);
            List<CustomerChargeHistory> chargeHistoryList = customerChargeHistoryRepo.findAllChargesByCprId(list.get(i).getId());
            chargeHistoryList = chargeHistoryList.stream().filter(x -> x.getChargeAmount() > 0).collect(Collectors.toList());
            chargeHistoryList.stream().forEach(data -> {
                Double chargePriceIncludingTax = data.getChargeAmount();
                for (int k = 0; k < taxService.get(data.getTaxId(),customers.getMvnoId()).getTieredList().size(); k++)
                    chargePriceIncludingTax = chargePriceIncludingTax + chargePriceIncludingTax * taxService.get(data.getTaxId(),customers.getMvnoId()).getTieredList().get(k).getRate() / 100.0;
                Double finalChargePriceIncludingTax1 = chargePriceIncludingTax;
                offerPrice.updateAndGet(v -> v + finalChargePriceIncludingTax1);
            });
            int finalI = i;
//            offerPrice.updateAndGet(v -> v - (v * list.get(finalI).getDiscount() / 100.0));
            addEntryIntoCustomerDbr(customers.getId(), Double.parseDouble(df.format(offerPrice.get())), list.get(i), false, false);
        }
    }

    public void addDbrForOrgCustomerDirectChargeForPrepaid(PrepaidInvoiceCharges message) {
        ApplicationLogger.logger.info("Start addDbrForOrgCustomerDirectChargeForPrepaid() ", APIConstants.SUCCESS, message);
        Optional<Customers> customers = customersRepository.findById(message.getCustId());

        List<ItemCharge> list = message.getItemCharges();
        for (int k = 0; k < list.size(); k++) {
            Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(list.get(k).getPlanid()));
            DecimalFormat df = new DecimalFormat("0.00");

            CustomerDBR customerDBR = new CustomerDBR();
            customerDBR.setInvoiceId(message.getInvoiceId());
            customerDBR.setCprid(Long.parseLong(list.get(k).getCustpackageid()));
            customerDBR.setCustid(Long.parseLong(message.getCustId().toString()));
            customerDBR.setPlanid(Long.parseLong(list.get(k).getPlanid()));
            customerDBR.setCustname(message.getCustomerName());
            customerDBR.setPlanname(getPostPaidPlan(Integer.parseInt(list.get(k).getPlanid())).getName());
            customerDBR.setCusttype(message.getCustomerType());
            String validity = list.get(k).getPlanValidityDays();
            Double d = Double.parseDouble(validity);
            customerDBR.setValidity_days(d.intValue());
            customerDBR.setOffer_price(Double.parseDouble(df.format(list.get(k).getPrice())));//+taxAmount)));
            customerDBR.setStartdate(LocalDate.now());
            customerDBR.setStatus("Active");
            customerDBR.setEnddate(LocalDate.now());
            customerDBR.setDbr(Double.parseDouble(df.format(list.get(k).getPrice())));//+taxAmount)));
            customerDBR.setIsDirectCharge(true);
            customerDBR.setPendingamt(0.0);
            customerDBR.setCumm_revenue(list.get(k).getPrice());
            if (plan.isPresent())
                customerDBR.setServiceId(plan.get().getServiceId().longValue());
            else
                customerDBR.setServiceId(null);
            if (customers.isPresent()) {
                customerDBR.setServiceArea(customers.get().getServicearea().getId());
                customerDBR.setBuId(customers.get().getBuId());
                customerDBR.setMvnoId(customers.get().getMvnoId());
            }
            customerDBR.setRemark("Direct Charge Added");
            customerDBRRepository.save(customerDBR);
            addDbrForOrgCustomerDirectChargeForPrepaidForChargeDbr(list.get(k), message.getCustId(), message.getCustomerName(), message.getCustomerType(), message.getInvoiceId());
        }
        ApplicationLogger.logger.info("End addDbrForOrgCustomerDirectChargeForPrepaid ", APIConstants.SUCCESS, message);
    }

    private void addDbrForOrgCustomerDirectChargeForPrepaidForChargeDbr(ItemCharge itemCharge, Integer
            custId, String customerName, String customerType, Long invoiceId) {
        Optional<Customers> customers = customersRepository.findById(custId);
        Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(itemCharge.getPlanid()));
        DecimalFormat df = new DecimalFormat("0.00");
        CustomerChargeDBR customerDBR = new CustomerChargeDBR();
        customerDBR.setChargeId(Long.parseLong(itemCharge.getChargeid()));
        customerDBR.setInvoiceId(invoiceId);
        customerDBR.setCprid(Long.parseLong(itemCharge.getCustpackageid()));
        customerDBR.setCustid(Long.parseLong(custId.toString()));
        customerDBR.setPlanid(Long.parseLong(itemCharge.getPlanid()));
        customerDBR.setCustname(customerName);
        customerDBR.setPlanname(getPostPaidPlan(Integer.parseInt(itemCharge.getPlanid())).getName());
        customerDBR.setCusttype(customerType);
        String validity = itemCharge.getPlanValidityDays();
        Double d = Double.parseDouble(validity);
        customerDBR.setValidity_days(d.intValue());
        customerDBR.setOffer_price(Double.parseDouble(df.format(itemCharge.getPrice())));//+taxAmount)));
        customerDBR.setStartdate(LocalDate.now());
        customerDBR.setStatus("Active");
        customerDBR.setEnddate(LocalDate.now());
        customerDBR.setDbr(Double.parseDouble(df.format(itemCharge.getPrice())));//+taxAmount)));
        customerDBR.setIsDirectCharge(true);
        customerDBR.setPendingamt(0.0);
        customerDBR.setCumm_revenue(itemCharge.getPrice());
        if (plan.isPresent())
            customerDBR.setServiceId(plan.get().getServiceId().longValue());
        else
            customerDBR.setServiceId(null);
        if (customers.isPresent()) {
            customerDBR.setServiceArea(customers.get().getServicearea().getId());
            customerDBR.setBuId(customers.get().getBuId());
            customerDBR.setMvnoId(customers.get().getMvnoId());
        }
        customerDBR.setRemark("Direct Charge Added");
        customerChargeDBRRepository.save(customerDBR);
    }


    public void addDbrForOrgCustomerPrepaid(PrepaidInvoiceCharges message) {
        ApplicationLogger.logger.info("Start addDbrForOrgCustomerPrepaid() ", APIConstants.SUCCESS, message);
        DecimalFormat df = new DecimalFormat("0.00");
        if (message.getCustId() != null && message.getItemCharges() != null && message.getItemCharges().size() > 0) {
            Optional<Customers> customers = customersRepository.findById(message.getCustId());
            if (customers.isPresent()) {
                if (customers.get().getCalendarType().equalsIgnoreCase(CommonConstants.CAL_TYPE_NEPALI)) {
                    Optional<DebitDocument> document = debitDocRepository.findById(message.getInvoiceId().intValue());
                    if (document.isPresent()) {
                        //update xml of debitDocument
                        updateInvoiceDatesForLocalCalendar(document.get());
                    }
                }
            }
            List<ItemCharge> itemCharges = message.getItemCharges();
            List<String> cprIds = itemCharges.stream().map(x -> x.getCustpackageid()).distinct().collect(Collectors.toList());
            for (int index = 0; index < cprIds.size(); index++) {

                int finalIndex = index;
                List<ItemCharge> list = itemCharges.stream().filter(data -> data.getCustpackageid().equalsIgnoreCase(cprIds.get(finalIndex))).collect(Collectors.toList());
                list.stream().forEach(data -> {
                    Double chargePriceIncludingTax = data.getPrice();
                    Tax tax = getTax(Integer.parseInt(data.getTaxid()));
                    Double taxAmount = getTaxAmount(tax, chargePriceIncludingTax);
                    data.setTax(taxAmount);
                });
                Double offerPrice = list.stream().mapToDouble(y -> y.getPrice()).sum();
                Double tmpOfferPrice = offerPrice;
                offerPrice = tmpOfferPrice;

                if (list.size() > 0) {
                    LocalDate startDate = Instant.ofEpochMilli(list.get(0).getPlanStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate endDate = Instant.ofEpochMilli(list.get(0).getPlanExpireDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    Long daysDiff = ChronoUnit.DAYS.between(startDate, endDate);
                    Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(list.get(0).getPlanid()));

                    if (plan.get().getUnitsOfValidity().equalsIgnoreCase("Hours")) {
                        Double hours = plan.get().getValidity();
                        Double converIntoDays = Math.ceil(hours / 24.0);
                        daysDiff = converIntoDays.longValue();
                    }

                    CustomerDBR customerDBR = new CustomerDBR();
                    customerDBR.setInvoiceId(message.getInvoiceId());
                    customerDBR.setCprid(Long.parseLong(cprIds.get(index)));
                    customerDBR.setCustid(Long.parseLong(message.getCustId().toString()));
                    customerDBR.setPlanid(Long.parseLong(list.get(0).getPlanid()));
                    customerDBR.setCustname(message.getCustomerName());
                    customerDBR.setPlanname(list.get(0).getPlanname());
                    customerDBR.setCusttype(message.getCustomerType());
                    customerDBR.setValidity_days(daysDiff.intValue());
                    customerDBR.setOffer_price(Double.parseDouble(df.format(offerPrice)));
                    customerDBR.setStartdate(LocalDate.from(startDate));
                    customerDBR.setStatus("Active");
                    customerDBR.setEnddate(endDate);
                    customerDBR.setDbr(offerPrice);
                    customerDBR.setIsDirectCharge(false);
                    customerDBR.setPendingamt(Double.parseDouble(df.format(tmpOfferPrice)));
                    customerDBR.setCumm_revenue(tmpOfferPrice);
                    if (plan.isPresent())
                        customerDBR.setServiceId(plan.get().getServiceId().longValue());
                    else
                        customerDBR.setServiceId(null);
                    customerDBR.setRemark("");

                    if (customers.isPresent()) {
                        customerDBR.setServiceArea(customers.get().getServicearea().getId());
                        customerDBR.setBuId(customers.get().getBuId());
                        customerDBR.setMvnoId(customers.get().getMvnoId());
                    }
                    customerDBRRepository.save(customerDBR);
                    addDbrForPrepaidCustomerForChargeLevel(list, message.getInvoiceId(), message.getCustId(), message.getCustomerName(), message.getCustomerType(), customers);
                }
            }
        }
        ApplicationLogger.logger.info("End addDbrForOrgCustomerPrepaid() ", APIConstants.SUCCESS, message);
    }

    public void addDbrForOrgCustomerPostpaid(PostpaidInvoiceCharges message) {
        ApplicationLogger.logger.info("Start addDbrForOrgCustomerPostpaid() ", APIConstants.SUCCESS, message);
        DecimalFormat df = new DecimalFormat("0.00");
        Optional<Customers> customers = customersRepository.findById(message.getCustId());
        if (message.getCustId() != null && message.getItemCharges() != null && message.getItemCharges().size() > 0) {
            List<PostpaidItemCharge> itemCharges = message.getItemCharges();
            Long daysDiff = 1l;
            List<Integer> cprIds = itemCharges.stream().map(x -> x.getCustpackageId()).distinct().collect(Collectors.toList());
            for (int index = 0; index < cprIds.size(); index++) {

                int finalIndex = index;
                List<PostpaidItemCharge> list = itemCharges.stream().filter(data -> data.getCustpackageId().equals(cprIds.get(finalIndex))).collect(Collectors.toList());
                list.stream().forEach(data -> {
                    Double chargePriceIncludingTax = data.getPrice();
                    Tax tax = getTax(Integer.parseInt(data.getTaxid()));
                    Double taxAmount = getTaxAmount(tax, chargePriceIncludingTax);
                    data.setTax(taxAmount);
                });

                List<PostpaidItemCharge> oneTimeCharges = list.stream().filter(x -> x.getChargetype().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_NONRECURRING)).collect(Collectors.toList());
                oneTimeCharges.stream().forEach(x -> {
                    list.remove(x);
                });
                Double offerPrice = list.stream().mapToDouble(y -> y.getPrice()).sum();

//                Double offerPrice = list.stream().mapToDouble(y -> y.getPrice()).sum();
                if (list.size() > 0) {
                    LocalDate startDate = Instant.ofEpochMilli(list.get(0).getPlanStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate endDate = Instant.ofEpochMilli(message.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    daysDiff = Math.abs(ChronoUnit.DAYS.between(startDate, endDate));
                    Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(list.get(0).getPlanid()));

                    if (plan.get().getUnitsOfValidity().equalsIgnoreCase("Hours")) {
                        Double hours = plan.get().getValidity();
                        Double converIntoDays = Math.ceil(hours / 24.0);
                        daysDiff = converIntoDays.longValue();
                    }

                    CustomerDBR customerDBR = new CustomerDBR();
                    customerDBR.setInvoiceId(message.getInvoiceId());
                    customerDBR.setCprid(cprIds.get(index).longValue());
                    customerDBR.setCustid(Long.parseLong(message.getCustId().toString()));
                    customerDBR.setPlanid(Long.parseLong(list.get(0).getPlanid()));
                    customerDBR.setCustname(message.getCustomerName());
                    customerDBR.setPlanname(list.get(0).getPlanname());
                    customerDBR.setCusttype(message.getCustomerType());
                    customerDBR.setValidity_days(daysDiff.intValue());
                    customerDBR.setOffer_price(Double.parseDouble(df.format(offerPrice)));
                    customerDBR.setStartdate(endDate);
                    customerDBR.setStatus("Active");
                    customerDBR.setEnddate(endDate);
                    customerDBR.setDbr(Double.parseDouble(df.format(offerPrice)));
                    customerDBR.setIsDirectCharge(false);
                    customerDBR.setPendingamt(0.0);
                    customerDBR.setCumm_revenue(offerPrice);
                    if (plan.isPresent())
                        customerDBR.setServiceId(plan.get().getServiceId().longValue());
                    else
                        customerDBR.setServiceId(null);
                    customerDBR.setRemark("");

                    if (customers.isPresent()) {
                        customerDBR.setServiceArea(customers.get().getServicearea().getId());
                        customerDBR.setBuId(customers.get().getBuId());
                        customerDBR.setMvnoId(customers.get().getMvnoId());
                    }

                    customerDBRRepository.save(customerDBR);
                }

                addOneTimeEntryForPostpaidIntoDBR(oneTimeCharges, message.getCustomerName(), message.getCustomerType(), message.getCustId(), daysDiff, message.getInvoiceId());
                if (message.getTotalDirectChargeAmount() != null && message.getTotalDirectChargeAmount() > 0) {
                    List<CustChargeDetails> details = getDirectChargeDetail(Integer.parseInt(cprIds.get(index).toString()));
                    if (details != null)
                        addDbrForPrepaidCustomerForDirectCharge(details, message.getCustId().toString(), message.getCustomerName(), message.getCustomerType(), message.getInvoiceId());
                }
            }
        }
        ApplicationLogger.logger.info("End addDbrForOrgCustomerPostpaid() ", APIConstants.SUCCESS, message);
    }


    public void addDbrForPrepaidCustomer(PrepaidInvoiceCharges message) {
        ApplicationLogger.logger.info("Start addDbrForPrepaidCustomer() ", APIConstants.SUCCESS, message);
        List<ItemCharge> items = message.getItemCharges();
        items = items.stream().filter(x -> (x.getPlanid() == null && x.getCustpackageid() == null)).collect(Collectors.toList());
        if (message.getCustId() != null && message.getItemCharges() != null && message.getItemCharges().size() > 0) {
            Optional<Customers> customers = customersRepository.findById(message.getCustId());
            Long daysDiff = 1l;
            if (customers.isPresent()) {
                if (customers.get().getCalendarType().equalsIgnoreCase(CommonConstants.CAL_TYPE_NEPALI)) {
                    Optional<DebitDocument> document = debitDocRepository.findById(message.getInvoiceId().intValue());
                    if (document.isPresent()) {
                        //update xml of debitDocument
                        updateInvoiceDatesForLocalCalendar(document.get());
                    }
                }
            }

            Integer requestFromPartnerId = -1;
            StaffUser staffUser = null;
            if (message.getLoggedInUserId() != null && message.getLoggedInUserId() != -1) {
                Optional<StaffUser> user = staffUserRepository.findById(message.getLoggedInUserId());
                if (user.isPresent()) {
                    staffUser = user.get();
                    requestFromPartnerId = user.get().getPartnerid();
                }
            }

            List<ItemCharge> itemCharges = message.getItemCharges();
            itemCharges = itemCharges.stream().filter(x -> (x.getPlanid() != null && x.getCustpackageid() != null)).collect(Collectors.toList());

            Integer paymentStatusForFranCustomer = partnerCommissionService.checkAndUpdatePaymentAdjustmentAgainstInvoiceAmount(itemCharges, message.getTotalInvoiceAmount(), customers.get(), staffUser, message.getInvoiceId());

            List<String> cprIds = itemCharges.stream().map(x -> x.getCustpackageid()).distinct().collect(Collectors.toList());
            for (int index = 0; index < cprIds.size(); index++) {
                int finalIndex = index;
                List<ItemCharge> list = itemCharges.stream().filter(data -> data.getCustpackageid().equalsIgnoreCase(cprIds.get(finalIndex))).collect(Collectors.toList());
                List<ItemCharge> oneTimeCharges = list.stream().filter(x -> x.getChargetype().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_NONRECURRING)).collect(Collectors.toList());
                oneTimeCharges.stream().forEach(x -> {
                    list.remove(x);
                });

                Double offerPrice1 = list.stream().mapToDouble(y -> y.getPrice()).sum() + list.stream().mapToDouble(y -> y.getTax()).sum();
                Double tmpOfferPrice1 = offerPrice1;
                offerPrice1 = tmpOfferPrice1;

                Double offerPrice = list.stream().mapToDouble(y -> y.getPrice()).sum();
                Double tmpOfferPrice = offerPrice;
                offerPrice = tmpOfferPrice;

                partnerCommissionService.partnerCommissionForPrepaidCustomerCreation(message.getTotalInvoiceAmount(), offerPrice1, list, customers.get(), staffUser, message.getInvoiceId(), paymentStatusForFranCustomer);


                if (list.size() > 0) {

                    LocalDateTime promiseStartDate = null;
                    LocalDateTime promiseEndDate = null;
                    Long promiseDays = 0l;
                    Double totalGraceAmount = 0.0;
                    Boolean isPromiseToPay = false;
                    Boolean isAfterPromise = false;

                    LocalDate startDate = Instant.ofEpochMilli(list.get(0).getPlanStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate endDate = Instant.ofEpochMilli(list.get(0).getPlanExpireDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    daysDiff = ChronoUnit.DAYS.between(startDate, endDate);
                    Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(list.get(0).getPlanid()));

                    if (plan.get().getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                        Double hours = plan.get().getValidity();
                        Double converIntoDays = Math.ceil(hours / 24.0);
                        daysDiff = converIntoDays.longValue();
                    }

                    CustPlanMappping mapping = custPlanMappingRepository.findById(Integer.valueOf(cprIds.get(finalIndex)));
                    if (mapping != null && mapping.getCprIdForPromiseToPay() != null) {
                        CustPlanMappping custMap = custPlanMappingRepository.findById(mapping.getCprIdForPromiseToPay());
                        if (custMap != null && custMap.getGraceDays() != null && custMap.getGraceDays() > 0 && custMap.getPromise_to_pay_startdate() != null && custMap.getPromise_to_pay_enddate() != null) {
                            isPromiseToPay = true;
                            promiseStartDate = custMap.getPromise_to_pay_startdate();
                            promiseEndDate = custMap.getPromise_to_pay_enddate();
                            promiseDays = custMap.getGraceDays().longValue();
                        }
                    }

                    Double dbr = tmpOfferPrice / daysDiff;
                    Double cummulativeRevenue = 0d;
                    DecimalFormat df = new DecimalFormat("0.00");
                    if (promiseStartDate != null && promiseEndDate != null && isPromiseToPay) {
                        if (isPromiseToPay && LocalDate.now().isAfter(promiseStartDate.toLocalDate()) && (LocalDate.now().isBefore(promiseEndDate.toLocalDate())) || LocalDate.now().equals(promiseEndDate.toLocalDate())) {
                            promiseDays = ChronoUnit.DAYS.between(promiseStartDate.toLocalDate(), LocalDate.now());
                            if (LocalDate.now().equals(promiseEndDate.toLocalDate()))
                                promiseDays = promiseDays - 1;
                            totalGraceAmount = dbr * promiseDays;
                            daysDiff = daysDiff - promiseDays;
                            endDate = endDate.minusDays(promiseDays);
                        } else if (isPromiseToPay && LocalDate.now().isAfter(promiseStartDate.toLocalDate()) && LocalDate.now().isAfter(promiseEndDate.toLocalDate())) {
                            totalGraceAmount = dbr * promiseDays;
                            daysDiff = daysDiff - promiseDays;
                            endDate = endDate.minusDays(promiseDays);
                            isAfterPromise = true;
                        }
                    }


                    for (int i = 0; i < daysDiff; i++) {
                        if (isPromiseToPay) {
                            tmpOfferPrice = tmpOfferPrice - dbr - totalGraceAmount;
                            cummulativeRevenue = cummulativeRevenue + dbr + totalGraceAmount;
                        } else {
                            tmpOfferPrice = tmpOfferPrice - dbr;
                            cummulativeRevenue = cummulativeRevenue + dbr;
                        }
                        CustomerDBR customerDBR = new CustomerDBR();
                        customerDBR.setInvoiceId(message.getInvoiceId());
                        customerDBR.setCprid(Long.parseLong(cprIds.get(index)));
                        customerDBR.setCustid(Long.parseLong(message.getCustId().toString()));
                        customerDBR.setPlanid(Long.parseLong(list.get(0).getPlanid()));
                        customerDBR.setCustname(message.getCustomerName());
                        customerDBR.setPlanname(list.get(0).getPlanname());
                        customerDBR.setCusttype(message.getCustomerType());
                        customerDBR.setValidity_days(daysDiff.intValue());
                        customerDBR.setOffer_price(Double.parseDouble(df.format(offerPrice)));
                        customerDBR.setStartdate(LocalDate.from(startDate.plusDays(i)));
                        customerDBR.setStatus("Active");
                        customerDBR.setEnddate(endDate);
                        if (isPromiseToPay) {
                            Double dbr1 = dbr + totalGraceAmount;
                            customerDBR.setDbr(Double.parseDouble(dbr1.toString()));
                        } else
                            customerDBR.setDbr(Double.parseDouble(dbr.toString()));
                        customerDBR.setIsDirectCharge(false);
                        customerDBR.setPendingamt(Double.parseDouble(tmpOfferPrice.toString()));
                        customerDBR.setCumm_revenue(cummulativeRevenue);
                        if (isPromiseToPay)
                            customerDBR.setRemark("Promise To Pay Adjusted for amount " + Double.parseDouble(df.format(totalGraceAmount)) + " for " + promiseDays + " Days");
                        else
                            customerDBR.setRemark("");
                        if (plan.isPresent())
                            customerDBR.setServiceId(plan.get().getServiceId().longValue());
                        else
                            customerDBR.setServiceId(null);

                        if (customers.isPresent()) {
                            customerDBR.setServiceArea(customers.get().getServicearea().getId());
                            customerDBR.setBuId(customers.get().getBuId());
                            customerDBR.setMvnoId(customers.get().getMvnoId());
                        }

                        if (plan.isPresent())
                            customerDBR.setServiceId(plan.get().getServiceId().longValue());
                        else
                            customerDBR.setServiceId(null);
                        customerDBRRepository.save(customerDBR);
                        isPromiseToPay = false;
                    }
                    addDbrForPrepaidCustomerForChargeLevel(list, message.getInvoiceId(), message.getCustId(), message.getCustomerName(), message.getCustomerType(), customers);
                }

                addOneTimeEntryForPrepaidIntoDBR(oneTimeCharges, message.getCustomerName(), message.getCustomerType(), message.getCustId(), daysDiff, message.getInvoiceId());
                if (message.getTotalDirectChargeAmount() != null && message.getTotalDirectChargeAmount() > 0.0d) {
                    List<CustChargeDetails> details = getDirectChargeDetail(Integer.parseInt(cprIds.get(index)));
                    if (details != null)
                        addDbrForPrepaidCustomerForDirectCharge(details, message.getCustId().toString(), message.getCustomerName(), message.getCustomerType(), message.getInvoiceId());
                }
            }
        }
        addInventoryDbr(message, items);
        ApplicationLogger.logger.info("End addDbrForPrepaidCustomer() ", APIConstants.SUCCESS, message);
    }

    private void addDbrForPrepaidCustomerForChargeLevel(List<ItemCharge> list, Long invoiceId, Integer
            custId, String customerName, String customerType, Optional<Customers> customers) {

        if (list.size() > 0) {

            list.stream().forEach(data -> {

                LocalDateTime promiseStartDate = null;
                LocalDateTime promiseEndDate = null;
                Long promiseDays = 0l;
                Double totalGraceAmount = 0.0;
                Boolean isPromiseToPay = false;
                Boolean isAfterPromise = false;

                LocalDate startDate = Instant.ofEpochMilli(list.get(0).getPlanStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endDate = Instant.ofEpochMilli(list.get(0).getPlanExpireDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                Long daysDiff = ChronoUnit.DAYS.between(startDate, endDate);
                Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(list.get(0).getPlanid()));

                if (plan.get().getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                    Double hours = plan.get().getValidity();
                    Double converIntoDays = Math.ceil(hours / 24.0);
                    daysDiff = converIntoDays.longValue();
                }


                CustPlanMappping mapping = custPlanMappingRepository.findById(Integer.valueOf(data.getCustpackageid()));
                if (mapping != null && mapping.getCprIdForPromiseToPay() != null) {
                    CustPlanMappping custMap = custPlanMappingRepository.findById(mapping.getCprIdForPromiseToPay());
                    if (custMap != null && custMap.getGraceDays() != null && custMap.getGraceDays() > 0 && custMap.getPromise_to_pay_startdate() != null && custMap.getPromise_to_pay_enddate() != null) {
                        isPromiseToPay = true;
                        promiseStartDate = custMap.getPromise_to_pay_startdate();
                        promiseEndDate = custMap.getPromise_to_pay_enddate();
                        promiseDays = custMap.getGraceDays().longValue();
                    }
                }

                Double tmpOfferPrice = data.getPrice();
                Double dbr = tmpOfferPrice / daysDiff;
                Double cummulativeRevenue = 0d;
                DecimalFormat df = new DecimalFormat("0.00");

                if (isPromiseToPay && promiseStartDate != null && promiseEndDate != null) {

                    if (isPromiseToPay && LocalDate.now().isAfter(promiseStartDate.toLocalDate()) && (LocalDate.now().isBefore(promiseEndDate.toLocalDate())) || LocalDate.now().equals(promiseEndDate.toLocalDate())) {
                        promiseDays = ChronoUnit.DAYS.between(promiseStartDate.toLocalDate(), LocalDate.now());
                        if (LocalDate.now().equals(promiseEndDate.toLocalDate()))
                            promiseDays = promiseDays - 1;
                        totalGraceAmount = dbr * promiseDays;
                        daysDiff = daysDiff - promiseDays;
                        endDate = endDate.minusDays(promiseDays);
                    } else if (isPromiseToPay && LocalDate.now().isAfter(promiseStartDate.toLocalDate()) && LocalDate.now().isAfter(promiseEndDate.toLocalDate())) {
                        totalGraceAmount = dbr * promiseDays;
                        daysDiff = daysDiff - promiseDays;
                        endDate = endDate.minusDays(promiseDays);
                        isAfterPromise = true;
                    }
                }

                for (int i = 0; i < daysDiff; i++) {
                    if (isPromiseToPay) {
                        tmpOfferPrice = tmpOfferPrice - dbr - totalGraceAmount;
                        cummulativeRevenue = cummulativeRevenue + dbr + totalGraceAmount;
                    } else {
                        tmpOfferPrice = tmpOfferPrice - dbr;
                        cummulativeRevenue = cummulativeRevenue + dbr;
                    }
                    CustomerChargeDBR customerDBR = new CustomerChargeDBR();
                    customerDBR.setChargeId(Long.parseLong(data.getChargeid()));
                    customerDBR.setInvoiceId(invoiceId);
                    customerDBR.setCprid(Long.parseLong(data.getCustpackageid()));
                    customerDBR.setCustid(Long.parseLong(custId.toString()));
                    customerDBR.setPlanid(Long.parseLong(list.get(0).getPlanid()));
                    customerDBR.setCustname(customerName);
                    customerDBR.setPlanname(list.get(0).getPlanname());
                    customerDBR.setCusttype(customerType);
                    customerDBR.setValidity_days(daysDiff.intValue());
                    customerDBR.setOffer_price(Double.parseDouble(df.format(tmpOfferPrice)));
                    customerDBR.setStartdate(LocalDate.from(startDate.plusDays(i)));
                    customerDBR.setStatus("Active");
                    customerDBR.setEnddate(endDate);
                    if (isPromiseToPay) {
                        Double dbr1 = dbr + totalGraceAmount;
                        customerDBR.setDbr(Double.parseDouble(dbr1.toString()));
                    } else
                        customerDBR.setDbr(Double.parseDouble(dbr.toString()));
                    customerDBR.setIsDirectCharge(false);
                    customerDBR.setPendingamt(Double.parseDouble(tmpOfferPrice.toString()));
                    customerDBR.setCumm_revenue(cummulativeRevenue);
                    if (plan.isPresent())
                        customerDBR.setServiceId(plan.get().getServiceId().longValue());
                    else
                        customerDBR.setServiceId(null);
                    customerDBR.setRemark("");

                    if (customers.isPresent()) {
                        customerDBR.setServiceArea(customers.get().getServicearea().getId());
                        customerDBR.setBuId(customers.get().getBuId());
                        customerDBR.setMvnoId(customers.get().getMvnoId());
                    }

                    if (plan.isPresent())
                        customerDBR.setServiceId(plan.get().getServiceId().longValue());
                    else
                        customerDBR.setServiceId(null);
                    customerChargeDBRRepository.save(customerDBR);
                    isPromiseToPay = false;
                }
            });
        }
    }

    private void addInventoryDbr(PrepaidInvoiceCharges message, List<ItemCharge> items) {
        ApplicationLogger.logger.info("Start addDbrForCustomerInventoryCharge() ", APIConstants.SUCCESS);
        Optional<Customers> customers = customersRepository.findById(message.getCustId());
        Long planId = null;
        String planName = null;
        Long serviceId = -1l;
        if (customers != null && customers.isPresent()) {
            if (customers.get().getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_ACTIVE)) {
                Optional<DebitDocument> debitDocument = debitDocRepository.findById(message.getInvoiceId().intValue());
                if (debitDocument.isPresent()) {
                    if (debitDocument.get().getInventoryMappingId() != null) {
                        Optional<CustomerInventoryMapping> mapping = customerInventoryMappingRepo.findById(debitDocument.get().getInventoryMappingId());
                        if (mapping.isPresent()) {

                            if (mapping.get().getPlanId() != null) {
                                Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(mapping.get().getPlanId().intValue());
                                if (plan.isPresent()) {
                                    serviceId = plan.get().getServiceId().longValue();
                                    planName = plan.get().getName();
                                }
                            }
                            if (mapping.get().getStaff() != null && mapping.get().getStaff().getPartnerid() != null && !mapping.get().getStaff().getPartnerid().equals(CommonConstants.DEFAULT_PARTNER_ID)) {
                                if (customers.get().getLcoId() == null) {
                                    partnerCommissionService.checkAndUpdatePaymentAdjustmentAgainstInventoryInvoiceAmount(message.getTotalInvoiceAmount(), customers.get(), mapping.get().getStaff(), message.getInvoiceId());
                                    //partnerCommissionService.updatePartnerBalanceAgainstInvoiceAmount(customers.get(),message.getTotalInvoiceAmount(), message.getInvoiceId());
                                }
                            }
                        }
                    }
                }
                if (items != null) {
                    for (int i = 0; i < items.size(); i++) {
                        Double applicableAmount = items.get(i).getPrice();
                        CustomerDBR dbr = new CustomerDBR();
                        dbr.setInvoiceId(message.getInvoiceId());
                        dbr.setCustid(message.getCustId().longValue());
                        dbr.setStartdate(LocalDate.now());
                        dbr.setEnddate(LocalDate.now());
                        dbr.setDbr(applicableAmount);
                        dbr.setPendingamt(0.0);
                        dbr.setCustname(message.getCustomerName());
                        dbr.setStatus("Active");
                        dbr.setCusttype(message.getCustomerType());
                        dbr.setIsDirectCharge(true);
                        dbr.setCumm_revenue(applicableAmount);
                        dbr.setServiceId(serviceId);
                        dbr.setPlanid(planId);
                        dbr.setPlanname(planName);
                        dbr.setRemark("Direct Charge Added");
                        if (debitDocument.get().getInventoryMappingId() != null)
                            dbr.setCustInvMappingId(debitDocument.get().getInventoryMappingId());
                        if (customers.isPresent()) {
                            dbr.setServiceArea(customers.get().getServicearea().getId());
                            dbr.setBuId(customers.get().getBuId());
                            dbr.setMvnoId(customers.get().getMvnoId());
                        }
                        customerDBRRepository.save(dbr);
                        addInventoryChargeDbr(message, items.get(i), planId, planName, serviceId);
                    }
                }
            }
        }

        ApplicationLogger.logger.info("End addDbrForCustomerInventoryCharge() ", APIConstants.SUCCESS, message);
    }

    private void addInventoryChargeDbr(PrepaidInvoiceCharges message, ItemCharge itemCharge, Long
            planId, String planName, Long serviceId) {
        Optional<Customers> customers = customersRepository.findById(message.getCustId());
        if (customers != null && customers.isPresent()) {
            if (customers.get().getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_ACTIVE)) {
                if (itemCharge != null) {
                    Double applicableAmount = itemCharge.getPrice();
                    CustomerChargeDBR dbr = new CustomerChargeDBR();
                    dbr.setChargeId(Long.parseLong(itemCharge.getChargeid()));
                    dbr.setInvoiceId(message.getInvoiceId());
                    dbr.setCustid(message.getCustId().longValue());
                    dbr.setStartdate(LocalDate.now());
                    dbr.setEnddate(LocalDate.now());
                    dbr.setDbr(applicableAmount);
                    dbr.setPendingamt(0.0);
                    dbr.setCustname(message.getCustomerName());
                    dbr.setStatus("Active");
                    dbr.setCusttype(message.getCustomerType());
                    dbr.setIsDirectCharge(true);
                    dbr.setCumm_revenue(applicableAmount);
                    dbr.setServiceId(serviceId);
                    dbr.setPlanid(planId);
                    dbr.setPlanname(planName);
                    dbr.setRemark("Direct Charge Added");
                    if (message.getInventoryMappingId() != null)
                        dbr.setCustInvMappingId(message.getInventoryMappingId());
                    if (customers.isPresent()) {
                        dbr.setServiceArea(customers.get().getServicearea().getId());
                        dbr.setBuId(customers.get().getBuId());
                        dbr.setMvnoId(customers.get().getMvnoId());
                    }
                    customerChargeDBRRepository.save(dbr);
                }
            }
        }
    }

    private void addOneTimeEntryForPrepaidIntoDBR(List<ItemCharge> oneTimeCharges, String
            customerName, String customerType, Integer custId, Long validityInDays, Long invoiceId) {
        ApplicationLogger.logger.info("Start addOneTimeEntryForPrepaidIntoDBR() ", APIConstants.SUCCESS, oneTimeCharges);
        Optional<Customers> customers = customersRepository.findById(custId);
        DebitDocument debitDocument = debitDocRepository.findById(invoiceId.intValue()).get();

        for (int i = 0; i < oneTimeCharges.size(); i++) {
            DecimalFormat df = new DecimalFormat("0.00");
            Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(oneTimeCharges.get(i).getPlanid()));
            CustomerDBR customerDBR = new CustomerDBR();
            customerDBR.setInvoiceId(invoiceId);
            customerDBR.setCprid(Long.parseLong(oneTimeCharges.get(i).getCustpackageid()));
            customerDBR.setCustid(Long.parseLong(custId.toString()));
            customerDBR.setPlanid(Long.parseLong(oneTimeCharges.get(i).getPlanid()));
            customerDBR.setCustname(customerName);
            customerDBR.setPlanname(oneTimeCharges.get(i).getPlanname());
            customerDBR.setCusttype(customerType);
            customerDBR.setValidity_days(validityInDays.intValue());
            customerDBR.setOffer_price(Double.parseDouble(df.format(oneTimeCharges.get(i).getPrice())));//+oneTimeCharges.get(i).getTax())));
            customerDBR.setStartdate(debitDocument.getStartdate().toLocalDate());
            customerDBR.setStatus("Active");
            customerDBR.setEnddate(debitDocument.getStartdate().toLocalDate());
            customerDBR.setDbr(Double.parseDouble(df.format(oneTimeCharges.get(i).getPrice())));//+oneTimeCharges.get(i).getTax())));
            customerDBR.setIsDirectCharge(true);
            customerDBR.setPendingamt(0.0);
            customerDBR.setCumm_revenue(oneTimeCharges.get(i).getPrice());
            if (plan.isPresent())
                customerDBR.setServiceId(plan.get().getServiceId().longValue());
            else
                customerDBR.setServiceId(null);
            customerDBR.setRemark("Onetime Charge Added");

            if (customers.isPresent()) {
                customerDBR.setServiceArea(customers.get().getServicearea().getId());
                customerDBR.setBuId(customers.get().getBuId());
                customerDBR.setMvnoId(customers.get().getMvnoId());
            }

            customerDBRRepository.save(customerDBR);
            addOneTimeEntryForPrepaidIntoChargeDBR(oneTimeCharges.get(i), customerName, customerType, custId, validityInDays, invoiceId);

        }
        ApplicationLogger.logger.info("End addOneTimeEntryForPrepaidIntoDBR() ", APIConstants.SUCCESS, oneTimeCharges);
    }

    private void addOneTimeEntryForPrepaidIntoChargeDBR(ItemCharge itemCharge, String customerName, String
            customerType, Integer custId, Long validityInDays, Long invoiceId) {
        Optional<Customers> customers = customersRepository.findById(custId);
        DebitDocument debitDocument = debitDocRepository.findById(invoiceId.intValue()).get();
        DecimalFormat df = new DecimalFormat("0.00");
        Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(itemCharge.getPlanid()));
        CustomerChargeDBR customerDBR = new CustomerChargeDBR();
        customerDBR.setInvoiceId(invoiceId);
        customerDBR.setChargeId(Long.parseLong(itemCharge.getChargeid()));
        customerDBR.setCprid(Long.parseLong(itemCharge.getCustpackageid()));
        customerDBR.setCustid(Long.parseLong(custId.toString()));
        customerDBR.setPlanid(Long.parseLong(itemCharge.getPlanid()));
        customerDBR.setCustname(customerName);
        customerDBR.setPlanname(itemCharge.getPlanname());
        customerDBR.setCusttype(customerType);
        customerDBR.setValidity_days(validityInDays.intValue());
        customerDBR.setOffer_price(Double.parseDouble(df.format(itemCharge.getPrice())));//+oneTimeCharges.get(i).getTax())));
        customerDBR.setStartdate(debitDocument.getStartdate().toLocalDate());
        customerDBR.setStatus("Active");
        customerDBR.setEnddate(debitDocument.getStartdate().toLocalDate());
        customerDBR.setDbr(Double.parseDouble(df.format(itemCharge.getPrice())));//+oneTimeCharges.get(i).getTax())));
        customerDBR.setIsDirectCharge(true);
        customerDBR.setPendingamt(0.0);
        customerDBR.setCumm_revenue(itemCharge.getPrice());
        if (plan.isPresent())
            customerDBR.setServiceId(plan.get().getServiceId().longValue());
        else
            customerDBR.setServiceId(null);
        customerDBR.setRemark("Onetime Charge Added");

        if (customers.isPresent()) {
            customerDBR.setServiceArea(customers.get().getServicearea().getId());
            customerDBR.setBuId(customers.get().getBuId());
            customerDBR.setMvnoId(customers.get().getMvnoId());
        }
        customerChargeDBRRepository.save(customerDBR);
    }

    private void addOneTimeEntryForPostpaidIntoDBR(List<PostpaidItemCharge> oneTimeCharges, String
            customerName, String customerType, Integer custId, Long validityInDays, Long invoiceId) {
        ApplicationLogger.logger.info("Start addOneTimeEntryForPostpaidIntoDBR() ", APIConstants.SUCCESS, oneTimeCharges);
        Optional<Customers> customers = customersRepository.findById(custId);
        for (int i = 0; i < oneTimeCharges.size(); i++) {
            DecimalFormat df = new DecimalFormat("0.00");
            Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(oneTimeCharges.get(i).getPlanid()));
            CustomerDBR customerDBR = new CustomerDBR();
            customerDBR.setInvoiceId(invoiceId);
            customerDBR.setCprid(oneTimeCharges.get(i).getCustpackageId().longValue());
            customerDBR.setCustid(Long.parseLong(custId.toString()));
            customerDBR.setPlanid(Long.parseLong(oneTimeCharges.get(i).getPlanid()));
            customerDBR.setCustname(customerName);
            customerDBR.setPlanname(oneTimeCharges.get(i).getPlanname());
            customerDBR.setCusttype(customerType);
            customerDBR.setValidity_days(validityInDays.intValue());
            customerDBR.setOffer_price(Double.parseDouble(df.format(oneTimeCharges.get(i).getPrice())));//+oneTimeCharges.get(i).getTax())));
            customerDBR.setStartdate(LocalDate.now());
            customerDBR.setStatus("Active");
            customerDBR.setEnddate(LocalDate.now());
            customerDBR.setDbr(Double.parseDouble(df.format(oneTimeCharges.get(i).getPrice())));//+oneTimeCharges.get(i).getTax())));
            customerDBR.setIsDirectCharge(true);
            customerDBR.setPendingamt(0.0);
            customerDBR.setCumm_revenue(oneTimeCharges.get(i).getPrice());
            if (plan.isPresent())
                customerDBR.setServiceId(plan.get().getServiceId().longValue());
            else
                customerDBR.setServiceId(null);
            customerDBR.setRemark("Onetime Charge Added");

            if (customers.isPresent()) {
                customerDBR.setServiceArea(customers.get().getServicearea().getId());
                customerDBR.setBuId(customers.get().getBuId());
                customerDBR.setMvnoId(customers.get().getMvnoId());
            }
            customerDBRRepository.save(customerDBR);
        }
        ApplicationLogger.logger.info("End addOneTimeEntryForPostpaidIntoDBR() ", APIConstants.SUCCESS, oneTimeCharges);
    }

    public void addDbrForPostpaidCustomer(PostpaidInvoiceCharges message) {
        ApplicationLogger.logger.info("Start addDbrForPostpaidCustomer() ", APIConstants.SUCCESS, message);
        Optional<Customers> customers = customersRepository.findById(message.getCustId());
        if (message.getCustId() != null && message.getItemCharges() != null && message.getItemCharges().size() > 0) {
            //Boolean isPaymentDone=partnerCommissionService.checkAndUpdatePaymentAdjustmentAgainstInvoiceAmount(message.getTotalInvoiceAmount(),message.getCustId());
            List<PostpaidItemCharge> itemCharges = message.getItemCharges();
            Long daysDiff = 1l;
            List<Integer> cprIds = itemCharges.stream().map(x -> x.getCustpackageId()).distinct().collect(Collectors.toList());
            for (int index = 0; index < cprIds.size(); index++) {
                int finalIndex = index;
                List<PostpaidItemCharge> list = itemCharges.stream().filter(data -> data.getCustpackageId().equals(cprIds.get(finalIndex))).collect(Collectors.toList());
                list.stream().forEach(data -> {
                    Double chargePriceIncludingTax = data.getPrice();
                    Tax tax = getTax(Integer.parseInt(data.getTaxid()));
                    Double taxAmount = getTaxAmount(tax, chargePriceIncludingTax);
                    //data.setTax(taxAmount);
                });

                List<PostpaidItemCharge> oneTimeCharges = list.stream().filter(x -> x.getChargetype().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_NONRECURRING)).collect(Collectors.toList());
                oneTimeCharges.stream().forEach(x -> {
                    list.remove(x);
                });

                Double offerPrice1 = list.stream().mapToDouble(y -> y.getPrice()).sum() + list.stream().mapToDouble(y -> y.getTax()).sum();
                Double tmpOfferPrice1 = offerPrice1;
                offerPrice1 = tmpOfferPrice1;
                Double offerPrice = list.stream().mapToDouble(y -> y.getPrice()).sum();

                //partnerCommissionService.partnerCommissionForPostpaidCustomerCreation(message.getTotalDirectChargeAmount(),offerPrice1, list, message.getCustId(), message.getInvoiceId());

                if (list.size() > 0) {
                    LocalDate startDate = Instant.ofEpochMilli(list.get(0).getPlanStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate endDate = Instant.ofEpochMilli(message.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
                    daysDiff = Math.abs(ChronoUnit.DAYS.between(startDate, endDate));
                    Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(list.get(0).getPlanid()));

                    if (plan.get().getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_HOURS)) {
                        Double hours = plan.get().getValidity();
                        Double converIntoDays = Math.ceil(hours / 24.0);
                        daysDiff = converIntoDays.longValue();
                    }

                    DecimalFormat df = new DecimalFormat("0.00");
                    CustomerDBR customerDBR = new CustomerDBR();
                    customerDBR.setInvoiceId(message.getInvoiceId());
                    customerDBR.setCprid(cprIds.get(index).longValue());
                    customerDBR.setCustid(Long.parseLong(message.getCustId().toString()));
                    customerDBR.setPlanid(Long.parseLong(list.get(0).getPlanid()));
                    customerDBR.setCustname(message.getCustomerName());
                    customerDBR.setPlanname(list.get(0).getPlanname());
                    customerDBR.setCusttype(message.getCustomerType());
                    customerDBR.setValidity_days(daysDiff.intValue());
                    customerDBR.setOffer_price(Double.parseDouble(df.format(offerPrice)));
                    customerDBR.setStartdate(endDate);
                    customerDBR.setStatus("Active");
                    customerDBR.setEnddate(endDate);
                    customerDBR.setDbr(Double.parseDouble(df.format(offerPrice)));
                    customerDBR.setIsDirectCharge(false);
                    customerDBR.setPendingamt(0.0);
                    customerDBR.setCumm_revenue(offerPrice);
                    if (plan.isPresent())
                        customerDBR.setServiceId(plan.get().getServiceId().longValue());
                    else
                        customerDBR.setServiceId(null);
                    customerDBR.setRemark("");

                    if (customers.isPresent()) {
                        customerDBR.setServiceArea(customers.get().getServicearea().getId());
                        customerDBR.setBuId(customers.get().getBuId());
                        customerDBR.setMvnoId(customers.get().getMvnoId());
                    }
                    customerDBRRepository.save(customerDBR);
                }

                addOneTimeEntryForPostpaidIntoDBR(oneTimeCharges, message.getCustomerName(), message.getCustomerType(), message.getCustId(), daysDiff, message.getInvoiceId());
                if (message.getTotalDirectChargeAmount() != null && message.getTotalDirectChargeAmount() > 0) {
                    List<CustChargeDetails> details = getDirectChargeDetail(Integer.parseInt(cprIds.get(index).toString()));
                    if (details != null)
                        addDbrForPrepaidCustomerForDirectCharge(details, message.getCustId().toString(), message.getCustomerName(), message.getCustomerType(), message.getInvoiceId());
                }
            }
        }
        ApplicationLogger.logger.info("End addDbrForPostpaidCustomer() ", APIConstants.SUCCESS, message);
    }


    public void addDbrForPrepaidCustomerForDirectCharge(List<CustChargeDetails> custChargeDetails, String
            custId, String customerName, String customerType, Long invoiceId) {
        ApplicationLogger.logger.info("Start addDbrForPrepaidCustomerForDirectCharge() ", APIConstants.SUCCESS, custChargeDetails);
        Optional<Customers> customers = customersRepository.findById(Integer.parseInt(custId));
        if (custChargeDetails != null && !custChargeDetails.isEmpty()) {
            for (int i = 0; i < custChargeDetails.size(); i++) {
                if (customerType.equalsIgnoreCase(CommonConstants.CUST_TYPE_PREPAID)) {
                    Double tmpOfferPrice = custChargeDetails.get(i).getPrice();
                    DecimalFormat df = new DecimalFormat("0.00");
                    Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(custChargeDetails.get(i).getPlanid().toString()));
                    CustomerDBR customerDBR = new CustomerDBR();
                    customerDBR.setInvoiceId(invoiceId);
                    customerDBR.setCprid(Long.parseLong(custChargeDetails.get(i).getCustPlanMapppingId().toString()));
                    customerDBR.setCustid(Long.parseLong(custId));
                    customerDBR.setPlanid(Long.parseLong(custChargeDetails.get(i).getPlanid().toString()));
                    customerDBR.setCustname(customerName);
                    customerDBR.setPlanname(getPostPaidPlan(custChargeDetails.get(i).getPlanid()).getName());
                    customerDBR.setCusttype(customerType);
                    customerDBR.setValidity_days(custChargeDetails.get(i).getPlanValidity());
                    customerDBR.setOffer_price(Double.parseDouble(df.format(custChargeDetails.get(i).getPrice())));
                    customerDBR.setStartdate(LocalDate.now());
                    customerDBR.setStatus("Active");
                    customerDBR.setEnddate(LocalDate.now());
                    customerDBR.setDbr(Double.parseDouble(df.format(tmpOfferPrice)));
                    customerDBR.setIsDirectCharge(true);
                    customerDBR.setPendingamt(0.0);
                    customerDBR.setCumm_revenue(tmpOfferPrice);
                    if (plan.isPresent())
                        customerDBR.setServiceId(plan.get().getServiceId().longValue());
                    else
                        customerDBR.setServiceId(null);
                    customerDBR.setRemark("Direct Charge Added");

                    if (customers.isPresent()) {
                        customerDBR.setServiceArea(customers.get().getServicearea().getId());
                        customerDBR.setBuId(customers.get().getBuId());
                        customerDBR.setMvnoId(customers.get().getMvnoId());
                    }
                    customerDBRRepository.save(customerDBR);
                    addDbrForPrepaidCustomerForDirectChargeChargeDbr(custChargeDetails.get(i), custId, customerName, customerType, invoiceId);
                }
            }
        }
        ApplicationLogger.logger.info("End addDbrForPrepaidCustomerForDirectCharge() ", APIConstants.SUCCESS, custChargeDetails);

    }

    private void addDbrForPrepaidCustomerForDirectChargeChargeDbr(CustChargeDetails custChargeDetails, String
            custId, String customerName, String customerType, Long invoiceId) {
        Optional<Customers> customers = customersRepository.findById(Integer.parseInt(custId));
        if (custChargeDetails != null) {
            if (customerType.equalsIgnoreCase(CommonConstants.CUST_TYPE_PREPAID)) {
                Double tmpOfferPrice = custChargeDetails.getPrice();
                DecimalFormat df = new DecimalFormat("0.00");
                Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(custChargeDetails.getPlanid().toString()));
                CustomerChargeDBR customerDBR = new CustomerChargeDBR();
                customerDBR.setInvoiceId(invoiceId);
                customerDBR.setChargeId(custChargeDetails.getChargeid().longValue());
                customerDBR.setCprid(Long.parseLong(custChargeDetails.getCustPlanMapppingId().toString()));
                customerDBR.setCustid(Long.parseLong(custId));
                customerDBR.setPlanid(Long.parseLong(custChargeDetails.getPlanid().toString()));
                customerDBR.setCustname(customerName);
                customerDBR.setPlanname(getPostPaidPlan(custChargeDetails.getPlanid()).getName());
                customerDBR.setCusttype(customerType);
                customerDBR.setValidity_days(custChargeDetails.getPlanValidity());
                customerDBR.setOffer_price(Double.parseDouble(df.format(custChargeDetails.getPrice())));
                customerDBR.setStartdate(LocalDate.now());
                customerDBR.setStatus("Active");
                customerDBR.setEnddate(LocalDate.now());
                customerDBR.setDbr(Double.parseDouble(df.format(tmpOfferPrice)));
                customerDBR.setIsDirectCharge(true);
                customerDBR.setPendingamt(0.0);
                customerDBR.setCumm_revenue(tmpOfferPrice);
                if (plan.isPresent())
                    customerDBR.setServiceId(plan.get().getServiceId().longValue());
                else
                    customerDBR.setServiceId(null);
                customerDBR.setRemark("Direct Charge Added");

                if (customers.isPresent()) {
                    customerDBR.setServiceArea(customers.get().getServicearea().getId());
                    customerDBR.setBuId(customers.get().getBuId());
                    customerDBR.setMvnoId(customers.get().getMvnoId());
                }
                customerChargeDBRRepository.save(customerDBR);
            }
        }
    }

    public void addDbrForPostpaidCustomerDirectCharge(PostpaidInvoiceCharges message) {
        ApplicationLogger.logger.info("Start addDbrForPostpaidCustomerDirectCharge() ", APIConstants.SUCCESS, message);
        List<PostpaidItemCharge> list = message.getItemCharges();
        Optional<Customers> customers = customersRepository.findById(message.getCustId());
        for (int k = 0; k < list.size(); k++) {
            Double taxAmount = getTaxAmount(getTax(Integer.parseInt(list.get(k).getTaxid())), list.get(k).getPrice());
            Double tmpOfferPrice = list.get(k).getPrice();//+taxAmount;
            LocalDate startDate = Instant.ofEpochMilli(list.get(k).getPlanStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = Instant.ofEpochMilli(list.get(k).getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            Long daysDiff = Math.abs(ChronoUnit.DAYS.between(startDate, endDate));
            DecimalFormat df = new DecimalFormat("0.00");
            Double dbr = tmpOfferPrice / daysDiff;
            Double cummulativeRevenue = 0d;
            Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(list.get(k).getPlanid()));
            if (!list.get(k).getType().equalsIgnoreCase("One-time")) {
                for (int i = 0; i < daysDiff; i++) {
                    tmpOfferPrice = tmpOfferPrice - dbr;
                    cummulativeRevenue = cummulativeRevenue + dbr;
                    CustomerDBR customerDBR = new CustomerDBR();
                    customerDBR.setInvoiceId(message.getInvoiceId());
                    customerDBR.setCprid(Long.parseLong(list.get(k).getCustpackageId().toString()));
                    customerDBR.setCustid(Long.parseLong(message.getCustId().toString()));
                    customerDBR.setPlanid(Long.parseLong(list.get(k).getPlanid()));
                    customerDBR.setCustname(message.getCustomerName());
                    customerDBR.setPlanname(getPostPaidPlan(Integer.parseInt(list.get(k).getPlanid())).getName());
                    customerDBR.setCusttype(message.getCustomerType());
                    String validity = list.get(k).getPlanValidityDays();
                    Double d = Double.parseDouble(validity);
                    customerDBR.setValidity_days(d.intValue());
                    customerDBR.setOffer_price(Double.parseDouble(df.format(list.get(k).getPrice())));
                    customerDBR.setStartdate(startDate.plusDays(i));
                    customerDBR.setStatus("Active");
                    customerDBR.setEnddate(endDate);
                    customerDBR.setDbr(Double.parseDouble(df.format(dbr)));
                    customerDBR.setIsDirectCharge(true);
                    customerDBR.setPendingamt(Double.parseDouble(df.format(tmpOfferPrice)));
                    customerDBR.setCumm_revenue(cummulativeRevenue);
                    if (plan.isPresent())
                        customerDBR.setServiceId(plan.get().getServiceId().longValue());
                    else
                        customerDBR.setServiceId(null);
                    customerDBR.setRemark("Direct Charge Added");

                    if (customers.isPresent()) {
                        customerDBR.setServiceArea(customers.get().getServicearea().getId());
                        customerDBR.setBuId(customers.get().getBuId());
                        customerDBR.setMvnoId(customers.get().getMvnoId());
                    }
                    customerDBRRepository.save(customerDBR);
                }
            } else {
                CustomerDBR customerDBR = new CustomerDBR();
                customerDBR.setInvoiceId(message.getInvoiceId());
                customerDBR.setCprid(Long.parseLong(list.get(k).getCustpackageId().toString()));
                customerDBR.setCustid(Long.parseLong(message.getCustId().toString()));
                customerDBR.setPlanid(Long.parseLong(list.get(k).getPlanid()));
                customerDBR.setCustname(message.getCustomerName());
                customerDBR.setPlanname(getPostPaidPlan(Integer.parseInt(list.get(k).getPlanid())).getName());
                customerDBR.setCusttype(message.getCustomerType());
                String validity = list.get(k).getPlanValidityDays();
                Double d = Double.parseDouble(validity);
                customerDBR.setValidity_days(d.intValue());
                customerDBR.setOffer_price(Double.parseDouble(df.format(list.get(k).getPrice())));//+taxAmount)));
                customerDBR.setStartdate(LocalDate.now());
                customerDBR.setStatus("Active");
                customerDBR.setEnddate(LocalDate.now());
                customerDBR.setDbr(Double.parseDouble(df.format(list.get(k).getPrice())));//+taxAmount)));
                customerDBR.setIsDirectCharge(true);
                customerDBR.setPendingamt(0.0);
                customerDBR.setCumm_revenue(list.get(k).getPrice());
                if (plan.isPresent())
                    customerDBR.setServiceId(plan.get().getServiceId().longValue());
                else
                    customerDBR.setServiceId(null);
                customerDBR.setRemark("Direct Charge Added");

                if (customers.isPresent()) {
                    customerDBR.setServiceArea(customers.get().getServicearea().getId());
                    customerDBR.setBuId(customers.get().getBuId());
                    customerDBR.setMvnoId(customers.get().getMvnoId());
                }
                customerDBRRepository.save(customerDBR);
            }
        }
        ApplicationLogger.logger.info("End addDbrForPostpaidCustomerDirectCharge() ", APIConstants.SUCCESS, message);
    }


    public void addDbrForPrepaidCustomerDirectCharge(PrepaidInvoiceCharges message) {
        ApplicationLogger.logger.info("Start addDbrForPrepaidCustomerDirectCharge() ", APIConstants.SUCCESS, message);
        Optional<Customers> customers = customersRepository.findById(message.getCustId());
        Integer requestFromPartnerId = -1;
        StaffUser staffUser = null;
        if (message.getLoggedInUserId() != null && message.getLoggedInUserId() != -1) {
            Optional<StaffUser> user = staffUserRepository.findById(message.getLoggedInUserId());
            if (user.isPresent()) {
                staffUser = user.get();
                requestFromPartnerId = user.get().getPartnerid();
            }
        }
        Integer paymentStatusForFranCustomer = partnerCommissionService.checkAndUpdatePaymentAdjustmentAgainstInvoiceAmount(message.getItemCharges(), message.getTotalInvoiceAmount(), customers.get(), staffUser, message.getInvoiceId());

        List<ItemCharge> list = message.getItemCharges();

//        Integer paymentStatusForFranCustomer=partnerCommissionService.checkAndUpdatePaymentAdjustmentAgainstInvoiceAmount(list,message.getTotalInvoiceAmount(),customers.get(),staffUser,message.getInvoiceId());

        for (int k = 0; k < list.size(); k++) {
            Optional<PostpaidPlan> plan = null;
            if (list.get(k).getPlanid() != null)
                plan = postpaidPlanRepo.findById(Integer.parseInt(list.get(k).getPlanid()));
            Double taxAmount = getTaxAmount(getTax(Integer.parseInt(list.get(k).getTaxid())), list.get(k).getPrice());
            Double tmpOfferPrice = list.get(k).getPrice();//+taxAmount;
            LocalDate startDate = Instant.ofEpochMilli(list.get(k).getPlanStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = Instant.ofEpochMilli(list.get(k).getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            Long daysDiff = Math.abs(ChronoUnit.DAYS.between(startDate, endDate));
            DecimalFormat df = new DecimalFormat("0.00");
            Double dbr = tmpOfferPrice / daysDiff;
            Double cummulativeRevenue = 0d;

            CustomerDBR customerDBR = new CustomerDBR();
            customerDBR.setInvoiceId(message.getInvoiceId());
            if (list.get(k).getCustpackageid() != null)
                customerDBR.setCprid(Long.parseLong(list.get(k).getCustpackageid()));
            customerDBR.setCustid(Long.parseLong(message.getCustId().toString()));
            if (list.get(k).getPlanid() != null) {
                customerDBR.setPlanid(Long.parseLong(list.get(k).getPlanid()));
                customerDBR.setPlanname(getPostPaidPlan(Integer.parseInt(list.get(k).getPlanid())).getName());
            }
            customerDBR.setCustname(message.getCustomerName());
            customerDBR.setCusttype(message.getCustomerType());
            String validity = list.get(k).getPlanValidityDays();
            Double d = Double.parseDouble(validity);
            customerDBR.setValidity_days(d.intValue());
            customerDBR.setOffer_price(Double.parseDouble(df.format(list.get(k).getPrice())));//+taxAmount)));
            customerDBR.setStartdate(LocalDate.now());
            customerDBR.setStatus("Active");
            customerDBR.setEnddate(LocalDate.now());
            customerDBR.setDbr(Double.parseDouble(df.format(list.get(k).getPrice())));//+taxAmount)));
            customerDBR.setIsDirectCharge(true);
            customerDBR.setPendingamt(0.0);
            customerDBR.setCumm_revenue(list.get(k).getPrice());
            if (plan != null && plan.isPresent())
                customerDBR.setServiceId(plan.get().getServiceId().longValue());
            else
                customerDBR.setServiceId(-1l);
            if (customers.isPresent()) {
                customerDBR.setServiceArea(customers.get().getServicearea().getId());
                customerDBR.setBuId(customers.get().getBuId());
                customerDBR.setMvnoId(customers.get().getMvnoId());
            }
            customerDBR.setRemark("Direct Charge Added");
            customerDBRRepository.save(customerDBR);
            addDbrForPrepaidCustomerDirectChargeForChargeLevel(list.get(k), customers.get(), message.getInvoiceId(), message.getCustId(), message.getCustomerName(), message.getCustomerType());
        }
        ApplicationLogger.logger.info("End addDbrForPrepaidCustomerDirectCharge() ", APIConstants.SUCCESS, message);
    }

    private void addDbrForPrepaidCustomerDirectChargeForChargeLevel(ItemCharge itemCharge, Customers
            customers, Long invoiceId, Integer custId, String customerName, String customerType) {
        Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(itemCharge.getPlanid()));
        Double taxAmount = getTaxAmount(getTax(Integer.parseInt(itemCharge.getTaxid())), itemCharge.getPrice());
        Double tmpOfferPrice = itemCharge.getPrice();//+taxAmount;
        LocalDate startDate = Instant.ofEpochMilli(itemCharge.getPlanStartDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = Instant.ofEpochMilli(itemCharge.getEndDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        Long daysDiff = Math.abs(ChronoUnit.DAYS.between(startDate, endDate));
        DecimalFormat df = new DecimalFormat("0.00");
        Double dbr = tmpOfferPrice / daysDiff;
        Double cummulativeRevenue = 0d;

        CustomerChargeDBR customerDBR = new CustomerChargeDBR();
        customerDBR.setInvoiceId(invoiceId);
        customerDBR.setChargeId(Long.parseLong(itemCharge.getChargeid()));
        customerDBR.setCprid(Long.parseLong(itemCharge.getCustpackageid()));
        customerDBR.setCustid(Long.parseLong(custId.toString()));
        customerDBR.setPlanid(Long.parseLong(itemCharge.getPlanid()));
        customerDBR.setCustname(customerName);
        customerDBR.setPlanname(getPostPaidPlan(Integer.parseInt(itemCharge.getPlanid())).getName());
        customerDBR.setCusttype(customerType);
        String validity = itemCharge.getPlanValidityDays();
        Double d = Double.parseDouble(validity);
        customerDBR.setValidity_days(d.intValue());
        customerDBR.setOffer_price(Double.parseDouble(df.format(itemCharge.getPrice())));//+taxAmount)));
        customerDBR.setStartdate(LocalDate.now());
        customerDBR.setStatus("Active");
        customerDBR.setEnddate(LocalDate.now());
        customerDBR.setDbr(Double.parseDouble(df.format(itemCharge.getPrice())));//+taxAmount)));
        customerDBR.setIsDirectCharge(true);
        customerDBR.setPendingamt(0.0);
        customerDBR.setCumm_revenue(itemCharge.getPrice());
        if (plan.isPresent())
            customerDBR.setServiceId(plan.get().getServiceId().longValue());
        else
            customerDBR.setServiceId(null);
        if (customers != null) {
            customerDBR.setServiceArea(customers.getServicearea().getId());
            customerDBR.setBuId(customers.getBuId());
            customerDBR.setMvnoId(customers.getMvnoId());
        }
        customerDBR.setRemark("Direct Charge Added");
        customerChargeDBRRepository.save(customerDBR);
    }

    public void addDbrForCustomerInventoryCharge(PrepaidInvoiceCharges message) {
        ApplicationLogger.logger.info("Start addDbrForCustomerInventoryCharge() ", APIConstants.SUCCESS, message);
        List<ItemCharge> inventoryCharges = message.getItemCharges();
        Optional<Customers> customers = customersRepository.findById(message.getCustId());
        customers.get().setWalletbalance(message.getWalletBalance());
        customersRepository.save(customers.get());
        for (int i = 0; i < inventoryCharges.size(); i++) {
            Double applicableAmount = inventoryCharges.get(i).getPrice();
            Double tax = 0.0;
            CustomerDBR dbr = new CustomerDBR();
            dbr.setInvoiceId(message.getInvoiceId());
            dbr.setCustid(message.getCustId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(applicableAmount);
            dbr.setPendingamt(0.0);
            dbr.setCustname(message.getCustomerName());
            dbr.setStatus("Active");
            dbr.setCusttype(message.getCustomerType());
            dbr.setIsDirectCharge(true);
            dbr.setCumm_revenue(applicableAmount);
            dbr.setServiceId(-1l);
            dbr.setRemark("Direct Charge Added");
            if (customers.isPresent()) {
                dbr.setServiceArea(customers.get().getServicearea().getId());
                dbr.setBuId(customers.get().getBuId());
                dbr.setMvnoId(customers.get().getMvnoId());
            }
            customerDBRRepository.save(dbr);
            addDbrForCustomerInventoryChargeForChargeLevel(customers, inventoryCharges.get(i), message.getCustId(), message.getCustomerName(), message.getCustomerType(), message.getInvoiceId());
        }
        ApplicationLogger.logger.info("End addDbrForCustomerInventoryCharge() ", APIConstants.SUCCESS, message);
    }

    private void addDbrForCustomerInventoryChargeForChargeLevel(Optional<Customers> customers, ItemCharge
            itemCharge, Integer custId, String customerName, String customerType, Long invoiceId) {
        Double applicableAmount = itemCharge.getPrice();
        Double tax = 0.0;
        CustomerChargeDBR dbr = new CustomerChargeDBR();
        dbr.setChargeId(Long.parseLong(itemCharge.getChargeid()));
        dbr.setInvoiceId(invoiceId);
        dbr.setCustid(custId.longValue());
        dbr.setStartdate(LocalDate.now());
        dbr.setEnddate(LocalDate.now());
        dbr.setDbr(applicableAmount);
        dbr.setPendingamt(0.0);
        dbr.setCustname(customerName);
        dbr.setStatus("Active");
        dbr.setCusttype(customerType);
        dbr.setIsDirectCharge(true);
        dbr.setCumm_revenue(applicableAmount);
        dbr.setServiceId(-1l);
        dbr.setRemark("Direct Charge Added");
        if (customers.isPresent()) {
            dbr.setServiceArea(customers.get().getServicearea().getId());
            dbr.setBuId(customers.get().getBuId());
            dbr.setMvnoId(customers.get().getMvnoId());
        }
        customerChargeDBRRepository.save(dbr);
    }

    public Tax getTax(Integer taxId) {
        Optional<Tax> tax = taxRepository.findById(taxId);
        return tax.isPresent() ? tax.get() : null;
    }

    public List<CustChargeDetails> getDirectChargeDetail(Integer cprId) {
        List<CustChargeDetails> custChargeDetails = custChargeRepository.findByCprId(cprId);
        return custChargeDetails;
    }

    public PostpaidPlan getPostPaidPlan(Integer planId) {
        Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(planId);
        return plan.get();
    }

    public Double getTaxAmount(Tax tax, Double offerPrice) {
        Double tmpOfferPrice = offerPrice;
        Double totalTaxAmount = 0.0;
        if (tax != null) {
            Double tier1 = 0.0;
            Double tier2 = 0.0;
            Double tier3 = 0.0;

            for (int i = 0; i < tax.getTieredList().size(); i++) {
                String tierGroup = tax.getTieredList().get(i).getTaxGroup();
                Double rate = tax.getTieredList().get(i).getRate();
                if (tierGroup.equalsIgnoreCase("TIER1")) {
                    tier1 = ((tmpOfferPrice + tier1) * (rate / 100.0));
                    totalTaxAmount += tier1;
                }
                if (tierGroup.equalsIgnoreCase("TIER2") && tier1 != 0) {
                    tier2 = ((tier1) * (rate / 100.0));
                    totalTaxAmount += tier2;
                }
                if (tierGroup.equalsIgnoreCase("TIER1") && tier2 != 0) {
                    tier3 = ((tier2) * (rate / 100.0));
                    totalTaxAmount += tier3;
                }
            }
        }
        return totalTaxAmount;
    }

    public void removedbrByCPRStartDate(Long cprId, LocalDate from, LocalDate to) {
        try {
            QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
            BooleanExpression expression = qCustomerDBR.isNotNull().and(qCustomerDBR.invoiceId.eq(cprId)).and(qCustomerDBR.startdate.between(from, to));
            List<CustomerDBR> customerDBRS = (List<CustomerDBR>) customerDBRRepository.findAll(expression);
            customerDBRRepository.deleteAll(customerDBRS);
        } catch (Exception ex) {
            logger.error("Error while deleting DBR: " + ex.getMessage());
        }
    }

    public void removedbrByCPRListAndInvoiceIdStartDate(List<Long> cprId, Long invoiceId, LocalDate
            from, LocalDate to) {
        try {
            QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
            BooleanExpression expression = qCustomerDBR.isNotNull().and(qCustomerDBR.cprid.in(cprId)).and(qCustomerDBR.invoiceId.eq(invoiceId)).and(qCustomerDBR.startdate.between(from, to));
            List<CustomerDBR> customerDBRS = (List<CustomerDBR>) customerDBRRepository.findAll(expression);
            customerDBRRepository.deleteAll(customerDBRS);
        } catch (Exception ex) {
            logger.error("Error while deleting DBR: " + ex.getMessage());
        }
    }

    public void removeDbrByCPRListAndInvoiceIdStartDateAtChargeLevel(List<Long> cprId, Long
            invoiceId, LocalDate from, LocalDate to) {
        try {
            QCustomerChargeDBR qCustomerDBR = QCustomerChargeDBR.customerChargeDBR;
            BooleanExpression expression = qCustomerDBR.isNotNull().and(qCustomerDBR.cprid.in(cprId)).and(qCustomerDBR.invoiceId.eq(invoiceId)).and(qCustomerDBR.startdate.between(from, to));
            List<CustomerChargeDBR> customerDBRS = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAll(expression);
            customerChargeDBRRepository.deleteAll(customerDBRS);
        } catch (Exception ex) {
            logger.error("Error while deleting DBR: " + ex.getMessage());
        }
    }

    public void removeDbrByCPRStartDateAtChargeLevel(Long cprId, LocalDate from, LocalDate to) {
        try {
            QCustomerChargeDBR qCustomerDBR = QCustomerChargeDBR.customerChargeDBR;
            BooleanExpression expression = qCustomerDBR.isNotNull().and(qCustomerDBR.invoiceId.eq(cprId)).and(qCustomerDBR.startdate.between(from, to));
            List<CustomerChargeDBR> customerDBRS = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAll(expression);
            customerChargeDBRRepository.deleteAll(customerDBRS);
        } catch (Exception ex) {
            logger.error("Error while deleting DBR: " + ex.getMessage());
        }
    }

    public Double getCreditNotePriceExcludingTax(DebitDocument document, Double creditNoteAmount) {
        DecimalFormat df = new DecimalFormat("####0.00");
        Double offerPriceExcludeTax = 0.0d;

        if (document != null && creditNoteAmount != null && creditNoteAmount > 0) {
            offerPriceExcludeTax = (((document.getSubtotal() + document.getDiscount()) / document.getTotalamount()) * creditNoteAmount);
            /*Double totalAmount = document.getTotalamount();
            Double totalTax = document.getTax();
            Double taxPercentage = 0d;
            if (totalAmount != null && totalTax != null)
                taxPercentage = (totalTax * 100.0d) / (totalAmount - totalTax);
            creditNoteAmount = creditNoteAmount / (1 + (taxPercentage / 100.0d));
            offerPriceExcludeTax = creditNoteAmount;*/
        }
        return Double.parseDouble(df.format(offerPriceExcludeTax));
    }

    public Double getCreditNotePriceExcludingTax(DebitDocument document) {
        DecimalFormat df = new DecimalFormat("####0.00");
        Double offerPriceExcludeTax = 0d;
        List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findBydebtDocId(document.getId());
        Double creditNoteAmount = creditDebitDocMappings.stream().filter(creditDebitDocMapping -> creditDebitDocMapping.getAdjustedAmount() > 0).mapToDouble(CreditDebitDocMapping::getAdjustedAmount).sum();

        if (document != null && creditNoteAmount != null && creditNoteAmount > 0) {
            Double totalAmount = document.getTotalamount();
            Double totalTax = document.getTax();
            Double taxPercentage = 0d;
            if (totalAmount != null && totalTax != null)
                taxPercentage = (totalTax * 100.0d) / (totalAmount - totalTax);
            creditNoteAmount = creditNoteAmount / (1 + (taxPercentage / 100.0d));
            offerPriceExcludeTax = creditNoteAmount;
        }
        return Double.parseDouble(df.format(offerPriceExcludeTax));
    }


    public void creditNoteDbrEntry(DebitDocument document, Double creditNoteAmount, Boolean
            isNeedToCalculateWithoutTax) {
        revertPartnerLedgerAndDetail(document, creditNoteAmount);
        DecimalFormat df = new DecimalFormat("0.00");
        LocalDate currentDate = LocalDate.now();
        Double creditAmountExcludeTax = getCreditNotePriceExcludingTax(document, creditNoteAmount);
        if (!isNeedToCalculateWithoutTax)
            creditAmountExcludeTax = creditNoteAmount;
        creditAmountExcludeTax = Double.parseDouble(df.format(creditAmountExcludeTax));

        try {
            if (!currentDate.isBefore(document.getStartdate().toLocalDate())) {
                if (!document.getIsDirectChargeInvoice()) {
                    List<CustomerDBR> customerDBRList = getCustomerDBRListBetweenStartDateAndEndDate(currentDate, document);
                    List<CustomerChargeDBR> customerChargeDBRList = getCustomerDBRListBetweenStartDateAndEndDateAtChargeLevel(currentDate, document);
                    Double pendingAmount = null;
                    if (customerDBRList != null && customerDBRList.size() > 0)
                        pendingAmount = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getStartdate().equals(currentDate)).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));

                    List<CustomerDBR> customerDBRList1 = getCustomerDBRListBetweenStartDateAndEndDate1(currentDate, document);
                    List<CustomerChargeDBR> customerChargeDBRList1 = getCustomerDBRListBetweenStartDateAndEndDate1AtChargeLevel(currentDate, document);
                    Double pendingAmount1 = null;
                    if (customerDBRList1 != null && customerDBRList1.size() > 0)
                        pendingAmount1 = Double.parseDouble(df.format(customerDBRList1.stream().filter(x -> x.getStartdate().equals(currentDate)).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));


                    if (creditAmountExcludeTax != null && creditAmountExcludeTax > 0 && document != null) {
                        if (pendingAmount != null) {
                            if (creditAmountExcludeTax.doubleValue() == pendingAmount.doubleValue()) {

                                removeAllEntry(document.getId().longValue(), currentDate, document.getEndate().toLocalDate());
                                addDbrEntry(document, document.getId().longValue(), creditAmountExcludeTax);
                                logger.info("Dbr entry against CreditNote Successfully Added", APIConstants.SUCCESS);

                            } else if (pendingAmount.doubleValue() < creditAmountExcludeTax.doubleValue()) {

                                removeAllEntry(document.getId().longValue(), currentDate, document.getEndate().toLocalDate());
                                if (document.getStartdate().equals(currentDate))
                                    addDbrEntry(document, creditAmountExcludeTax, currentDate, 2, customerDBRList, customerChargeDBRList);
                                else
                                    addDbrEntry1(document, creditAmountExcludeTax, pendingAmount - creditAmountExcludeTax, currentDate, customerDBRList);
                                logger.info("Dbr entry against CreditNote Successfully Added and Future Entries Deleted", APIConstants.SUCCESS);

                            } else if (pendingAmount > creditAmountExcludeTax) {
                                if (pendingAmount1 != null) {
                                    if (pendingAmount1.doubleValue() == creditAmountExcludeTax.doubleValue()) {

                                        removeAllEntry1(document.getId().longValue(), currentDate, document.getEndate().toLocalDate());
                                        addDbrEntry(document, creditAmountExcludeTax, currentDate, 3, customerDBRList, customerChargeDBRList);
                                        logger.info("Dbr entry against CreditNote Successfully Added", APIConstants.SUCCESS);

                                    } else if (pendingAmount1.doubleValue() > creditAmountExcludeTax.doubleValue()) {

                                        updateAllEntry(creditAmountExcludeTax, document.getStartdate().equals(currentDate), customerDBRList1, customerChargeDBRList1);
                                        addDbrEntry(document, creditAmountExcludeTax, currentDate, 3, customerDBRList, customerChargeDBRList);
                                        logger.info("Dbr entry against CreditNote Successfully Added", APIConstants.SUCCESS);

                                    }
                                } else {
                                    addDbrEntry1(document, document.getId().longValue(), creditAmountExcludeTax);
                                }
                            }
                        } else {
                            if (!document.getStartdate().equals(currentDate)) {
                                List<CustomerDBR> customerDBRList2 = getCustomerDBRListBetweenStartDateAndJustBeforeCurrentDate(document);
                                Double cumulativeRevenue = null;
                                if (customerDBRList2 != null && customerDBRList2.size() > 0)
                                    cumulativeRevenue = Double.parseDouble(df.format(customerDBRList2.stream().mapToDouble(x -> x.getDbr()).sum()));

                                if (cumulativeRevenue != null)
                                    addDbrEntry1(document, creditAmountExcludeTax, currentDate, customerDBRList);
                            }
                        }
                    }
                } else {
                    CustomerDBR customerDBR = getCustomerPendingRevenueForDirectChargeInvoice(document);
                    addDbrEntryForDirectCharge(document, creditAmountExcludeTax, currentDate);
                }
            } else {
                //future dbr update code
                List<CustomerDBR> futureDbrList = getFutureDbrList(document);
                List<CustomerChargeDBR> futureChargeDbrList = getFutureChargeDbrList(document);
                Double pendingAmount = 0.0;
                if (futureChargeDbrList != null && !futureChargeDbrList.isEmpty()) {
                    pendingAmount = Double.parseDouble(df.format(futureDbrList.get(0).getPendingamt() + futureDbrList.get(0).getDbr()));
                }

                if (futureDbrList != null && !futureDbrList.isEmpty()) {
                    if (document.getTotalamount().doubleValue() == creditNoteAmount.doubleValue() || pendingAmount.doubleValue() == creditAmountExcludeTax.doubleValue()) {
                        removeAllEntry(document.getId().longValue(), document.getStartdate().toLocalDate(), document.getEndate().toLocalDate());
                        addDbrEntry(document, document.getId().longValue(), creditAmountExcludeTax);
                    } else {
                        updateAllFutureEntry(futureDbrList, creditAmountExcludeTax, futureChargeDbrList);
                        addDbrFutureEntry(document, document.getId().longValue(), creditAmountExcludeTax, futureDbrList, futureChargeDbrList);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to Add Dbr Against CreditNote :error{};exception{}", APIConstants.FAIL, e.getStackTrace());
        }
    }

    public void revertPartnerLedgerAndDetail(DebitDocument document, Double creditNoteAmount) {
        try {
            Double adjustedCreditNoteAmount = creditNoteAmount;
            Double totalAdjustedAmount = 0.0;
            QPartnerLedgerDetails qpartnerLedgerDetails = QPartnerLedgerDetails.partnerLedgerDetails;
            BooleanExpression exp = qpartnerLedgerDetails.isNotNull();
            exp = exp.and(qpartnerLedgerDetails.debitDocId.eq(document.getId().longValue())).and(qpartnerLedgerDetails.isDeleted.eq(false));
            List<PartnerLedgerDetails> details = (List<PartnerLedgerDetails>) partnerLedgerDetailsRepository.findAll(exp);

            List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findBydebtDocId(document.getId());
            List<Integer> creditDocIdList = creditDebitDocMappings.stream().map(x -> x.getCreditDocId()).collect(Collectors.toList());
            List<CreditDocument> creditDocuments = creditDocRepository.findAllByIdIn(creditDocIdList);
            creditDocuments = creditDocuments.stream().filter(x -> x.getStatus().equalsIgnoreCase(CommonConstants.PAYMENT_STATUS_PENDDING) || x.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_REJECTED)).collect(Collectors.toList());
            creditDocIdList = creditDocuments.stream().map(x -> x.getId()).collect(Collectors.toList());

            creditDocIdList.stream().forEach(id -> {
                for (int i = 0; i < creditDebitDocMappings.size(); i++) {
                    if (creditDebitDocMappings.get(i).getCreditDocId().equals(id)) {
                        creditDebitDocMappings.remove(creditDebitDocMappings.get(i));
                    }
                }
            });

            if (creditDebitDocMappings != null && !creditDebitDocMappings.isEmpty()) {
                totalAdjustedAmount = creditDebitDocMappings.stream().mapToDouble(x -> x.getAdjustedAmount()).sum();
                adjustedCreditNoteAmount = creditDebitDocMappings.get(creditDebitDocMappings.size() - 1).getAdjustedAmount();
            }

            if (creditNoteAmount > 0.0) {
                if (details != null && !details.isEmpty()) {
                    List<PartnerLedgerDetails> commissionList = details.stream().filter(x -> x.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_COMMISSION)).collect(Collectors.toList());
                    if (commissionList != null && !commissionList.isEmpty()) {
                        Double commission = commissionList.stream().mapToDouble(x -> x.getCommission()).sum();
                        if (document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() != null)
                            commission = commissionList.stream().mapToDouble(x -> x.getAmount()).sum();

                        Double prorateCommission = creditNoteAmount * commission / commissionList.get(0).getGrossOfferPrice();
                        if (document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() != null) {
                            revertCommission(document, creditNoteAmount, prorateCommission, commissionList, null);
                        } else if (document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() == null) {
                            revertCommission(document, creditNoteAmount, prorateCommission, commissionList, null);
                        } else if (!document.getCustomer().getIs_from_pwc() && document.getCustomer().getPartner().getId() != CommonConstants.DEFAULT_PARTNER_ID) {
                            revertCommission(document, creditNoteAmount, prorateCommission, commissionList, null);
                        }
                    }
                    List<PartnerLedgerDetails> balanceList = details.stream().filter(x -> x.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_ADD_BALANCE) && x.getTranstype().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT)).collect(Collectors.toList());
                    if (balanceList != null && !balanceList.isEmpty()) {
                        revertCreditNoteAmount(document, creditNoteAmount);
                    }
                } else if (!document.getCustomer().getIs_from_pwc() && document.getCustomer().getPartner().getId() != CommonConstants.DEFAULT_PARTNER_ID) {
                    if (document.getTotalamount().doubleValue() > creditNoteAmount) {
                        QTempPartnerLedgerDetail qTempPartnerLedgerDetail = QTempPartnerLedgerDetail.tempPartnerLedgerDetail;
                        BooleanExpression expression = qTempPartnerLedgerDetail.isNotNull();
                        expression = expression.and(qTempPartnerLedgerDetail.debitDocId.eq(document.getId().longValue())).and(qTempPartnerLedgerDetail.isDeleted.eq(false));
                        expression = expression.and(qTempPartnerLedgerDetail.transcategory.eq(CommonConstants.TRANS_CATEGORY_COMMISSION));
                        List<TempPartnerLedgerDetail> details1 = (List<TempPartnerLedgerDetail>) tempPartnerLedgerDetailsRepository.findAll(expression);
                        if (details1 != null && !details1.isEmpty()) {
                            Double commission = details1.stream().mapToDouble(x -> x.getCommission()).sum();
                            Double prorateCommission = creditNoteAmount * commission / details1.get(0).getGrossOfferPrice();
                            addRevertCommissionEntryInTmp(document, creditNoteAmount, prorateCommission, details1);
                            if (creditDebitDocMappings != null && !creditDebitDocMappings.isEmpty()) {
                                if (document.getTotalamount().doubleValue() == totalAdjustedAmount.doubleValue()) {
                                    QTempPartnerLedgerDetail qTempPartnerLedgerDetail1 = QTempPartnerLedgerDetail.tempPartnerLedgerDetail;
                                    BooleanExpression exp1 = qTempPartnerLedgerDetail1.isNotNull();
                                    exp1 = exp1.and(qTempPartnerLedgerDetail1.debitDocId.eq(document.getId().longValue())).and(qTempPartnerLedgerDetail1.isDeleted.eq(false));
                                    List<TempPartnerLedgerDetail> detail = (List<TempPartnerLedgerDetail>) tempPartnerLedgerDetailsRepository.findAll(exp1);

                                    if (detail != null && !detail.isEmpty()) {
                                        tempPartnerLedgerDetailsRepository.deleteAll(detail);
                                        partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(detail);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to Add Revert Commission Against CreditNote :error{};exception{}", APIConstants.FAIL, e.getStackTrace());
        }
    }

    private void revertCreditNoteAmount(DebitDocument document, Double creditNoteAmount) {
        PartnerLedgerDetails reverseCommission = new PartnerLedgerDetails();
        reverseCommission.setAmount(creditNoteAmount);
        reverseCommission.setDebitDocId(document.getId().longValue());
        reverseCommission.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
        reverseCommission.setGrossOfferPrice(document.getTotalamount());
        reverseCommission.setOfferprice(document.getTotalamount());
        reverseCommission.setCustid(document.getCustomer().getId());
        reverseCommission.setPartner(document.getCustomer().getPartner());
        reverseCommission.setIsDeleted(false);
        reverseCommission.setDebitDocId(document.getId().longValue());
        reverseCommission.setCreateDate(LocalDateTime.now());
        reverseCommission.setDescription("CreditNote Amount reverted for the invoice " + document.getDocnumber());
        reverseCommission.setTranscategory("Revert Balance");
        reverseCommission.setCommission(0.0d);
        partnerLedgerDetailsRepository.save(reverseCommission);

        Optional<Partner> partner = partnerRepository.findById(document.getCustomer().getPartner().getId());
        if (partner.isPresent()) {
            Partner partner1 = partner.get();
            partner1.setBalance(partner.get().getBalance() + creditNoteAmount);
            partner1 = partnerRepository.save(partner1);
        }
    }

    private void revertCommission(DebitDocument document, Double creditNoteAmount, Double
            prorateCommission, List<PartnerLedgerDetails> commissionList, List<TempPartnerLedgerDetail> tempCommissionList) {
        if (document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() != null) {
            PartnerLedgerDetails reverseCommission = new PartnerLedgerDetails();
            reverseCommission.setAmount(prorateCommission);
            reverseCommission.setDebitDocId(document.getId().longValue());
            reverseCommission.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
            reverseCommission.setCustid(document.getCustomer().getId());
            reverseCommission.setPartner(document.getCustomer().getPartner());
            reverseCommission.setIsDeleted(false);
            reverseCommission.setCreateDate(LocalDateTime.now());
            reverseCommission.setDescription("Commission reverted for the invoice " + document.getDocnumber());
            reverseCommission.setTranscategory("Revert Commission");
            reverseCommission.setCommission(0.0);
            reverseCommission.setGrossOfferPrice(document.getTotalamount());
            reverseCommission.setOfferprice(document.getTotalamount());

            if (commissionList != null && !commissionList.isEmpty()) {
                Double agr = commissionList.stream().mapToDouble(x -> x.getAgr_amount()).sum();
                Double tax = commissionList.stream().mapToDouble(x -> x.getTax()).sum();
                Double offerPrice = commissionList.stream().mapToDouble(x -> x.getOfferprice()).sum();
                Double revertOfferPrice = creditNoteAmount * (offerPrice / document.getTotalamount());
                Double revertTax = revertOfferPrice * (tax / offerPrice);
                Double revertAgr = (revertOfferPrice - revertTax) * (agr / (offerPrice - tax));
                reverseCommission.setTax(revertTax);
                reverseCommission.setAgr_amount(revertAgr);
                reverseCommission.setOfferprice(revertOfferPrice);
            }

            if (tempCommissionList != null && !tempCommissionList.isEmpty()) {
                Double agr = tempCommissionList.stream().mapToDouble(x -> x.getAgr_amount()).sum();
                Double tax = tempCommissionList.stream().mapToDouble(x -> x.getTax()).sum();
                Double offerPrice = tempCommissionList.stream().mapToDouble(x -> x.getOfferprice()).sum();
                Double revertOfferPrice = creditNoteAmount * (offerPrice / document.getTotalamount());
                Double revertTax = revertOfferPrice * (tax / offerPrice);
                Double revertAgr = (revertOfferPrice - revertTax) * (agr / (offerPrice - tax));
                reverseCommission.setTax(revertTax);
                reverseCommission.setAgr_amount(revertAgr);
                reverseCommission.setOfferprice(revertOfferPrice);
            }

            partnerLedgerDetailsRepository.save(reverseCommission);
        } else {
            PartnerLedgerDetails reverseCommission = new PartnerLedgerDetails();
            reverseCommission.setAmount(0.0d);
            reverseCommission.setDebitDocId(document.getId().longValue());
            reverseCommission.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
            reverseCommission.setCustid(document.getCustomer().getId());
            reverseCommission.setPartner(document.getCustomer().getPartner());
            reverseCommission.setIsDeleted(false);
            reverseCommission.setCreateDate(LocalDateTime.now());
            reverseCommission.setDescription("Commission reverted for the invoice " + document.getDocnumber());
            reverseCommission.setTranscategory("Revert Commission");
            reverseCommission.setCommission(prorateCommission);
            reverseCommission.setGrossOfferPrice(document.getTotalamount());
            reverseCommission.setOfferprice(document.getTotalamount());

            if (commissionList != null && !commissionList.isEmpty()) {
                Double agr = commissionList.stream().mapToDouble(x -> x.getAgr_amount()).sum();
                Double tax = commissionList.stream().mapToDouble(x -> x.getTax()).sum();
                Double offerPrice = commissionList.stream().mapToDouble(x -> x.getOfferprice()).sum();
                Double revertOfferPrice = creditNoteAmount * (offerPrice / document.getTotalamount());
                Double revertTax = revertOfferPrice * (tax / offerPrice);
                Double revertAgr = (revertOfferPrice - revertTax) * (agr / (offerPrice - tax));
                reverseCommission.setTax(revertTax);
                reverseCommission.setAgr_amount(revertAgr);
                reverseCommission.setOfferprice(revertOfferPrice);
            }

            if (tempCommissionList != null && !tempCommissionList.isEmpty()) {
                Double agr = tempCommissionList.stream().mapToDouble(x -> x.getAgr_amount()).sum();
                Double tax = tempCommissionList.stream().mapToDouble(x -> x.getTax()).sum();
                Double offerPrice = tempCommissionList.stream().mapToDouble(x -> x.getOfferprice()).sum();
                Double revertOfferPrice = creditNoteAmount * (offerPrice / document.getTotalamount());
                Double revertTax = revertOfferPrice * (tax / offerPrice);
                Double revertAgr = (revertOfferPrice - revertTax) * (agr / (offerPrice - tax));
                reverseCommission.setTax(revertTax);
                reverseCommission.setAgr_amount(revertAgr);
                reverseCommission.setOfferprice(revertOfferPrice);
            }

            partnerLedgerDetailsRepository.save(reverseCommission);
        }

        Optional<Partner> partner = partnerRepository.findById(document.getCustomer().getPartner().getId());
        if (partner.isPresent()) {
            if (partner.get().getCommissionShareType().equalsIgnoreCase("Balance")) {
                Partner partner1 = partner.get();
                partner1.setBalance(partner.get().getBalance() - prorateCommission);
                partner1 = partnerRepository.save(partner1);
            }

            if (partner.get().getCommissionShareType().equalsIgnoreCase("Revenue")) {
                Partner partner1 = partner.get();
                partner1.setCommrelvalue(partner.get().getCommrelvalue() - prorateCommission);
                partner1 = partnerRepository.save(partner1);

                PartnerCommission commission = new PartnerCommission();
                commission.setPartnerid(partner1.getId());
                commission.setCustomerid(document.getCustomer().getId());
                commission.setCommtype(partner1.getCommissionShareType());
                commission.setCommval(Double.parseDouble(new DecimalFormat("##.##").format(-prorateCommission)));
                commission.setStatus(SubscriberConstants.STATUS_PENDING);
                commission.setBilldate(LocalDateTime.now());
                commission = partnerCommissionRepository.save(commission);
            }
        }
    }

    private void addDbrFutureEntry(DebitDocument document, long invoiceId, Double
            creditAmountExcludeTax, List<CustomerDBR> customerDBRList, List<CustomerChargeDBR> futureChargeDbrList) {
        Map<Integer, Double> map = getServiceWiseRatioForCreditNoteAmount(customerDBRList);

        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer serviceId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();
            Double amount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerDBR dbr = new CustomerDBR();
            dbr.setInvoiceId(invoiceId);
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(0.0);
            dbr.setPendingamt(-(creditAmountExcludeTax * percentage) / 100.0d);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setPartnerId(null);
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setCumm_revenue(0.0);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
            addDbrFutureEntryAtChargeLevel(document, invoiceId, amount, futureChargeDbrList, serviceId);
        }
    }

    private void addDbrFutureEntryAtChargeLevel(DebitDocument document, long invoiceId, Double
            creditAmountExcludeTax, List<CustomerChargeDBR> futureChargeDbrList, Integer serviceId) {
        Map<Integer, Double> map = getChargeWiseRatioForCreditNoteAmount(futureChargeDbrList, serviceId);

        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer chargeId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();
            CustomerChargeDBR dbr = new CustomerChargeDBR();
            dbr.setChargeId(chargeId.longValue());
            dbr.setInvoiceId(invoiceId);
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(0.0);
            dbr.setPendingamt(-(creditAmountExcludeTax * percentage) / 100.0d);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setCumm_revenue(0.0);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerChargeDBRRepository.save(dbr);
        }
    }

    private void updateAllFutureEntry(List<CustomerDBR> customerDBRList, Double
            creditAmountExcludeTax, List<CustomerChargeDBR> futureChargeDbrList) {
        Map<Integer, Double> map = getServiceWiseRatioForCreditNoteAmount(customerDBRList);
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer serviceId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();
            Double amount1 = (creditAmountExcludeTax * percentage) / 100.0d;
            List<CustomerDBR> list = customerDBRList.stream().filter(x -> x.getServiceId().equals(serviceId.longValue())).collect(Collectors.toList());
            Integer days = list.size();
            if (days > 0) {
                CustomerDBR customerDBR1 = list.stream().filter(x -> x.getServiceId().equals(serviceId.longValue())).findFirst().get();
                AtomicReference<Double> amount = new AtomicReference<>(customerDBR1.getPendingamt() + customerDBR1.getDbr() - (creditAmountExcludeTax * percentage) / 100.0d);
                Double dbr = amount.get() / days;
                AtomicReference<Double> cummRevenue = new AtomicReference<>(0.0d);

                list.stream().forEach(data -> {
                    amount.set(amount.get() - dbr);
                    data.setPendingamt(amount.get());
                    data.setDbr(dbr);
                    cummRevenue.updateAndGet(v -> v + dbr);
                    data.setCumm_revenue(cummRevenue.get());
                    customerDBRRepository.save(data);
                });
            }
            updateAllFutureEntryAtChargeLevel(amount1, futureChargeDbrList, serviceId);
        }
    }

    private void updateAllFutureEntryAtChargeLevel(Double
                                                           creditAmountExcludeTax, List<CustomerChargeDBR> futureChargeDbrList, Integer serviceId) {
        Map<Integer, Double> map = getChargeWiseRatioForCreditNoteAmount(futureChargeDbrList, serviceId);
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer chargeId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();
            List<CustomerChargeDBR> list = futureChargeDbrList.stream().filter(x -> x.getChargeId().equals(chargeId.longValue())).collect(Collectors.toList());
            Integer days = list.size();
            if (days > 0) {
                CustomerChargeDBR customerDBR1 = list.stream().findFirst().get();
                AtomicReference<Double> amount = new AtomicReference<>(customerDBR1.getPendingamt() + customerDBR1.getDbr() - (creditAmountExcludeTax * percentage) / 100.0d);
                Double dbr = amount.get() / days;
                AtomicReference<Double> cummRevenue = new AtomicReference<>(0.0d);

                list.stream().forEach(data -> {
                    amount.set(amount.get() - dbr);
                    data.setPendingamt(amount.get());
                    data.setDbr(dbr);
                    cummRevenue.updateAndGet(v -> v + dbr);
                    data.setCumm_revenue(cummRevenue.get());
                    customerChargeDBRRepository.save(data);
                });
            }
        }
    }

    private List<CustomerDBR> getFutureDbrList(DebitDocument document) {
        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
        BooleanExpression expression = qCustomerDBR.isNotNull();
        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.startdate.between(document.getStartdate().toLocalDate(), document.getEndate().toLocalDate())).and(qCustomerDBR.isDirectCharge.eq(false)).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.findAll(expression);
        if (dbrList != null && !dbrList.isEmpty())
            return dbrList;
        else
            return null;
    }

    private List<CustomerChargeDBR> getFutureChargeDbrList(DebitDocument document) {
        QCustomerChargeDBR qCustomerDBR = QCustomerChargeDBR.customerChargeDBR;
        BooleanExpression expression = qCustomerDBR.isNotNull();
        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.startdate.between(document.getStartdate().toLocalDate(), document.getEndate().toLocalDate())).and(qCustomerDBR.isDirectCharge.eq(false)).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerChargeDBR> dbrList = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAll(expression);
        if (dbrList != null && !dbrList.isEmpty())
            return dbrList;
        else
            return null;
    }

    private void addDbrEntryForDirectCharge(DebitDocument document, Double creditAmountExcludeTax, LocalDate
            currentDate) {
        Map<Integer, Double> map = getServiceWiseRatioForInvoiceAmount(document.getId().longValue());

        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer serviceId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();

            CustomerDBR dbr = new CustomerDBR();
            dbr.setDbr(-(creditAmountExcludeTax * percentage) / 100.0d);
            dbr.setPendingamt((creditAmountExcludeTax * percentage) / 100.0d);
            dbr.setCumm_revenue(-(creditAmountExcludeTax * percentage) / 100.0d);
            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setPartnerId(null);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
        }
    }

    private CustomerDBR getCustomerPendingRevenueForDirectChargeInvoice(DebitDocument document) {
        CustomerDBR customerDBR = null;
        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
        BooleanExpression expression = qCustomerDBR.isNotNull();
        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue()));
        List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.findAll(expression);
        if (dbrList != null && !dbrList.isEmpty()) {
            return dbrList.get(dbrList.size() - 1);
        }
        return customerDBR;

    }

    private void updateAllEntry(Double creditAmountExcludeTax, Boolean isSameDay,
                                final List<CustomerDBR> customerDBRList, List<CustomerChargeDBR> customerChargeDBRList1) {

        List<Long> serviceIdList = customerDBRList.stream().map(x -> x.getServiceId()).distinct().collect(Collectors.toList());
        Double totalAmount = customerDBRList.stream().mapToDouble(x -> x.getDbr()).sum();

        serviceIdList.stream().forEach(x -> {

            List<CustomerDBR> list = customerDBRList.stream().filter(y -> y.getServiceId().equals(x.longValue())).collect(Collectors.toList());
            Double serviceAmount = list.stream().mapToDouble(d -> d.getDbr()).sum();
            Double percentage = (serviceAmount / totalAmount) * 100.0d;

            long days = list.size();
            if (days > 0) {
                Double customerPendingRevenue = 0d;
                Double updatedDbr = 0d;
                Double cummRevenue = 0d;

                if (isSameDay) {
                    customerPendingRevenue = list.get(0).getPendingamt() + list.get(0).getDbr() - (creditAmountExcludeTax * percentage) / 100.0d;
                    updatedDbr = customerPendingRevenue / days;
                } else {
                    customerPendingRevenue = list.get(0).getPendingamt() + list.get(0).getDbr() - (creditAmountExcludeTax * percentage) / 100.0d;
                    updatedDbr = customerPendingRevenue / days;
                    cummRevenue = list.get(0).getCumm_revenue() - list.get(0).getDbr();
                }

                //update Future Entries
                for (CustomerDBR customerDBR : list) {
                    cummRevenue += updatedDbr;
                    customerPendingRevenue -= updatedDbr;
                    customerDBR.setDbr(updatedDbr);
                    customerDBR.setPendingamt(customerPendingRevenue);
                    customerDBR.setCumm_revenue(cummRevenue);
                    customerDBRRepository.save(customerDBR);
                }
            }
        });

        updateAllEntryAtChargeLevel(creditAmountExcludeTax, isSameDay, customerChargeDBRList1);
    }

    private void updateAllEntryAtChargeLevel(Double creditAmountExcludeTax, Boolean
            isSameDay, List<CustomerChargeDBR> customerDBRList) {
        List<Long> serviceIdList = customerDBRList.stream().map(x -> x.getChargeId()).distinct().collect(Collectors.toList());
        Double totalAmount = customerDBRList.stream().mapToDouble(x -> x.getDbr()).sum();

        serviceIdList.stream().forEach(x -> {

            List<CustomerChargeDBR> list = customerDBRList.stream().filter(y -> y.getChargeId().equals(x.longValue())).collect(Collectors.toList());
            Double serviceAmount = list.stream().mapToDouble(d -> d.getDbr()).sum();
            Double percentage = (serviceAmount / totalAmount) * 100.0d;

            long days = list.size();
            if (days > 0) {
                Double customerPendingRevenue = 0d;
                Double updatedDbr = 0d;
                Double cummRevenue = 0d;

                if (isSameDay) {
                    customerPendingRevenue = list.get(0).getPendingamt() + list.get(0).getDbr() - (creditAmountExcludeTax * percentage) / 100.0d;
                    updatedDbr = customerPendingRevenue / days;
                } else {
                    customerPendingRevenue = list.get(0).getPendingamt() + list.get(0).getDbr() - (creditAmountExcludeTax * percentage) / 100.0d;
                    updatedDbr = customerPendingRevenue / days;
                    cummRevenue = list.get(0).getCumm_revenue() - list.get(0).getDbr();
                }

                //update Future Entries
                for (CustomerChargeDBR customerDBR : list) {
                    cummRevenue += updatedDbr;
                    customerPendingRevenue -= updatedDbr;
                    customerDBR.setDbr(updatedDbr);
                    customerDBR.setPendingamt(customerPendingRevenue);
                    customerDBR.setCumm_revenue(cummRevenue);
                    customerChargeDBRRepository.save(customerDBR);
                }
            }
        });
    }

    private void addDbrEntry(DebitDocument document, Double creditAmountExcludeTax, LocalDate
            currentDate, Integer
                                     flag, List<CustomerDBR> customerDBRList, List<CustomerChargeDBR> customerChargeDBRList) {
        Map<Integer, Double> map = getServiceWiseRatioForCreditNoteAmount(customerDBRList);
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer serviceId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();
            CustomerDBR customerDBR1 = customerDBRList.stream().filter(x -> x.getServiceId().equals(serviceId.longValue())).findFirst().get();

            Double amount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerDBR dbr = new CustomerDBR();

            if (flag.equals(2)) {

                dbr.setPendingamt(amount);
                dbr.setDbr(0d);
                dbr.setCumm_revenue(0d);
            }
            if (flag.equals(3)) {
                dbr.setDbr(0d);
                dbr.setPendingamt(amount);
                dbr.setCumm_revenue(0d);
            }

            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setPartnerId(null);
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
            addDbrEntryAtChargeLevel(document, amount, currentDate, flag, customerChargeDBRList, serviceId);
        }
    }

    private void addDbrEntryAtChargeLevel(DebitDocument document, Double creditAmountExcludeTax, LocalDate
            currentDate, Integer flag, List<CustomerChargeDBR> customerChargeDBRList, Integer serviceId) {
        Map<Integer, Double> map = getChargeWiseRatioForCreditNoteAmount(customerChargeDBRList, serviceId);
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer chargeId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();
            CustomerChargeDBR customerDBR1 = customerChargeDBRList.stream().filter(x -> x.getChargeId().equals(chargeId.longValue())).findFirst().get();

            Double amount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerChargeDBR dbr = new CustomerChargeDBR();
            dbr.setChargeId(chargeId.longValue());

            if (flag.equals(2)) {

                dbr.setPendingamt(amount);
                dbr.setDbr(0d);
                dbr.setCumm_revenue(0d);
            }
            if (flag.equals(3)) {
                dbr.setDbr(0d);
                dbr.setPendingamt(amount);
                dbr.setCumm_revenue(0d);
            }

            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerChargeDBRRepository.save(dbr);
        }
    }


    private void addDbrEntry1(DebitDocument document, Double creditAmountExcludeTax, Double
            extraAmount, LocalDate currentDate, List<CustomerDBR> customerDBRList) {
        Map<Integer, Double> map = getServiceWiseRatioForInvoiceAmount(document.getId().longValue());

        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer serviceId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();

            Double extra = (extraAmount * percentage) / 100.0d;
            Double creditAmount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerDBR dbr = new CustomerDBR();
            dbr.setDbr(extra);
            dbr.setPendingamt(-(creditAmount + extra));
            dbr.setCumm_revenue((extraAmount * percentage) / 100.0d);
            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setPartnerId(null);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
            addDbrEntry1AtChargeLevel(document, creditAmount, extra, currentDate, serviceId);
        }
    }

    private void addDbrEntry1AtChargeLevel(DebitDocument document, Double creditAmountExcludeTax, Double
            extraAmount, LocalDate currentDate, Integer serviceId) {
        Map<Integer, Double> serviceMap = getChargeWiseRatioForInvoiceAmount(document.getId().longValue(), serviceId.longValue());
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : serviceMap.entrySet()) {
            Integer chargeId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();

            Double extra = (extraAmount * percentage) / 100.0d;
            Double creditAmount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerChargeDBR dbr = new CustomerChargeDBR();
            dbr.setChargeId(chargeId.longValue());
            dbr.setDbr(extra);
            dbr.setPendingamt(-(creditAmount + extra));
            dbr.setCumm_revenue((extraAmount * percentage) / 100.0d);
            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark("");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerChargeDBRRepository.save(dbr);
        }
    }

    private void addDbrEntry1(DebitDocument document, Double creditAmountExcludeTax, LocalDate
            currentDate, List<CustomerDBR> customerDBRList) {
        Map<Integer, Double> map = getServiceWiseRatioForInvoiceAmount(document.getId().longValue());

        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer serviceId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();

            Double creditAmount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerDBR dbr = new CustomerDBR();
            dbr.setDbr(-creditAmount);
            dbr.setPendingamt(0.0d);
            dbr.setCumm_revenue(-creditAmount);
            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setPartnerId(null);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
            addDbrEntry1AtChargeLevelX(document, creditAmount, currentDate, serviceId);
        }
    }

    private void addDbrEntry1AtChargeLevelX(DebitDocument document, Double creditAmountExcludeTax, LocalDate
            currentDate, Integer serviceId) {
        Map<Integer, Double> map = getChargeWiseRatioForInvoiceAmount(document.getId().longValue(), serviceId.longValue());
        DecimalFormat df = new DecimalFormat("0.00");
        for (Map.Entry<Integer, Double> doubleMap : map.entrySet()) {
            Integer chargeId = doubleMap.getKey();
            Double percentage = doubleMap.getValue();

            Double creditAmount = (creditAmountExcludeTax * percentage) / 100.0d;
            CustomerChargeDBR dbr = new CustomerChargeDBR();
            dbr.setChargeId(chargeId.longValue());
            dbr.setDbr(-creditAmount);
            dbr.setPendingamt(0.0d);
            dbr.setCumm_revenue(-creditAmount);
            dbr.setInvoiceId(document.getId().longValue());
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(currentDate);
            dbr.setEnddate(currentDate);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setServiceId(serviceId.longValue());
            dbr.setRemark(df.format(((creditAmountExcludeTax * percentage) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(serviceId.longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerChargeDBRRepository.save(dbr);
        }
    }


    public void addDbrEntry(DebitDocument document, Long invoiceId, Double creditAmountExcludeTax) {
        DecimalFormat df = new DecimalFormat("0.00");
        Map<Integer, Double> map = getServiceWiseRatioForInvoiceAmount(document.getId().longValue());
        map.entrySet().stream().forEach(data -> {
            Double amount = (creditAmountExcludeTax * data.getValue()) / 100.0d;
            CustomerDBR dbr = new CustomerDBR();
            dbr.setInvoiceId(invoiceId);
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(0.0);
            dbr.setPartnerId(null);
            dbr.setPendingamt(amount);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setCumm_revenue(0.0);
            dbr.setServiceId(data.getKey().longValue());
            dbr.setRemark(df.format(amount) + " CreditNote Adjusted for " + getServiceNameById(data.getKey().longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
            addDbrEntryAtChargeLevel(document, invoiceId, amount, data.getKey().longValue());
        });
    }

    private void addDbrEntryAtChargeLevel(DebitDocument document, Long invoiceId, Double creditAmountExcludeTax,
                                          long serviceId) {
        DecimalFormat df = new DecimalFormat("0.00");
        Map<Integer, Double> map = getChargeWiseRatioForInvoiceAmount(document.getId().longValue(), serviceId);
        map.entrySet().stream().forEach(data -> {
            CustomerChargeDBR dbr = new CustomerChargeDBR();
            dbr.setChargeId(data.getKey().longValue());
            dbr.setInvoiceId(invoiceId);
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(0.0);
            dbr.setPendingamt((creditAmountExcludeTax * data.getValue()) / 100.0d);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setCumm_revenue(0.0);
            dbr.setServiceId(serviceId);
            dbr.setRemark(df.format(((creditAmountExcludeTax * data.getValue()) / 100.0d)) + " CreditNote Adjusted for " + getServiceNameById(data.getKey().longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerChargeDBRRepository.save(dbr);
        });

    }

    private void addDbrEntry1(DebitDocument document, Long invoiceId, Double creditAmountExcludeTax) {
        DecimalFormat df = new DecimalFormat("0.00");
        Map<Integer, Double> map = getServiceWiseRatioForInvoiceAmount(document.getId().longValue());
        map.entrySet().stream().forEach(data -> {
            CustomerDBR dbr = new CustomerDBR();
            Double amount = (creditAmountExcludeTax * data.getValue()) / 100.0d;
            dbr.setInvoiceId(invoiceId);
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(-amount);
            dbr.setPartnerId(null);
            dbr.setPendingamt(amount);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setCumm_revenue(-amount);
            dbr.setServiceId(data.getKey().longValue());
            dbr.setRemark(df.format(amount) + " CreditNote Adjusted for " + getServiceNameById(data.getKey().longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerDBRRepository.save(dbr);
            addDbrEntry1AtChargeLevel(document, invoiceId, amount, data.getKey().longValue());

        });
    }

    private void addDbrEntry1AtChargeLevel(DebitDocument document, Long invoiceId, Double
            creditAmountExcludeTax, Long serviceId) {
        DecimalFormat df = new DecimalFormat("0.00");
        Map<Integer, Double> map = getChargeWiseRatioForInvoiceAmount(document.getId().longValue(), serviceId);
        map.entrySet().stream().forEach(data -> {
            CustomerChargeDBR dbr = new CustomerChargeDBR();
            Double amount = (creditAmountExcludeTax * data.getValue()) / 100.0d;
            dbr.setChargeId(data.getKey().longValue());
            dbr.setInvoiceId(invoiceId);
            dbr.setCustid(document.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(-amount);
            dbr.setPendingamt(amount);
            dbr.setCustname(document.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(document.getCustomer().getCustomerType());
            dbr.setIsDirectCharge(false);
            dbr.setCumm_revenue(-amount);
            dbr.setServiceId(serviceId);
            dbr.setRemark(df.format(amount) + " CreditNote Adjusted for " + getServiceNameById(data.getKey().longValue()) + " Service");
            dbr.setServiceArea(document.getCustomer().getServicearea().getId());
            dbr.setBuId(document.getCustomer().getBuId());
            dbr.setMvnoId(document.getCustomer().getMvnoId());
            customerChargeDBRRepository.save(dbr);
        });
    }

    public void removeAllEntry(Long invoiceId, LocalDate currentDate, LocalDate endate) {
        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
        BooleanExpression exp = qCustomerDBR.isNotNull();
        exp = exp.and(qCustomerDBR.startdate.between(currentDate, endate)).and(qCustomerDBR.invoiceId.eq(invoiceId)).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> customerDBRSList = (List<CustomerDBR>) customerDBRRepository.findAll(exp);
        for (CustomerDBR customerDBR : customerDBRSList) {
            customerDBRRepository.delete(customerDBR);
        }
        removeAllEntryAtChargeLevel(invoiceId, currentDate, endate);
    }

    private void removeAllEntryAtChargeLevel(Long invoiceId, LocalDate currentDate, LocalDate endate) {
        QCustomerChargeDBR qCustomerChargeDBR = QCustomerChargeDBR.customerChargeDBR;
        BooleanExpression exp = qCustomerChargeDBR.isNotNull();
        exp = exp.and(qCustomerChargeDBR.startdate.between(currentDate, endate)).and(qCustomerChargeDBR.invoiceId.eq(invoiceId)).and(qCustomerChargeDBR.cprid.isNotNull());
        List<CustomerChargeDBR> customerChargeDBRList = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAll(exp);
        for (CustomerChargeDBR customerChargeDBR : customerChargeDBRList) {
            customerChargeDBRRepository.delete(customerChargeDBR);
        }

    }

//    private void removeAllEntry1(Long invoiceId, LocalDate currentDate, LocalDate endate) {
//        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
//        BooleanExpression exp = qCustomerDBR.isNotNull();
//        exp = exp.and(qCustomerDBR.startdate.between(currentDate, endate)).and(qCustomerDBR.invoiceId.eq(invoiceId)).and(qCustomerDBR.isDirectCharge.eq(false)).and(qCustomerDBR.cprid.isNotNull());
//        List<CustomerDBR> customerDBRSList = (List<CustomerDBR>) customerDBRRepository.findAll(exp);
//        for (CustomerDBR customerDBR: customerDBRSList) {
//            customerDBRRepository.delete(customerDBR);
//        }
//    }

    private void removeAllEntry1(Long invoiceId, LocalDate currentDate, LocalDate endate) {
        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
        BooleanExpression exp = qCustomerDBR.isNotNull();
        exp = exp.and(qCustomerDBR.startdate.between(currentDate, endate)).and(qCustomerDBR.invoiceId.eq(invoiceId)).and(qCustomerDBR.isDirectCharge.eq(false)).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> customerDBRSList = (List<CustomerDBR>) customerDBRRepository.findAll(exp);
        for (CustomerDBR customerDBR : customerDBRSList) {
            customerDBRRepository.delete(customerDBR);
        }
        removeAllEntry1AtChargeLevel(invoiceId, currentDate, endate);
    }


    private void removeAllEntry1AtChargeLevel(Long invoiceId, LocalDate currentDate, LocalDate endate) {
        QCustomerChargeDBR qCustomerDBR = QCustomerChargeDBR.customerChargeDBR;
        BooleanExpression exp = qCustomerDBR.isNotNull();
        exp = exp.and(qCustomerDBR.startdate.between(currentDate, endate)).and(qCustomerDBR.invoiceId.eq(invoiceId)).and(qCustomerDBR.isDirectCharge.eq(false)).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerChargeDBR> customerDBRSList = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAll(exp);
        for (CustomerChargeDBR customerDBR : customerDBRSList) {
            customerChargeDBRRepository.delete(customerDBR);
        }
    }

    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateInvoiceDatesForLocalCalendar(DebitDocument debitDocument) {
        try {
            String billDateString = "<localbilldate>" + debitDocument.getLocalbilldate() + "</localbilldate>";
            String startDateString = "<localstartdate>" + debitDocument.getLocalstartdate() + "</localstartdate>";
            String endDateString = "<localenddate>" + debitDocument.getLocalenddate() + "</localenddate>";

            String billDate = debitDocument.getBilldate().getDayOfMonth() + "-" + debitDocument.getBilldate().getMonthValue() + "-" + debitDocument.getBilldate().getYear() + " " + debitDocument.getBilldate().getHour() + ":" + debitDocument.getBilldate().getMinute() + ":" + debitDocument.getBilldate().getSecond();
            String startDate = debitDocument.getStartdate().getDayOfMonth() + "-" + debitDocument.getStartdate().getMonthValue() + "-" + debitDocument.getStartdate().getYear() + " " + debitDocument.getStartdate().getHour() + ":" + debitDocument.getStartdate().getMinute() + ":" + debitDocument.getStartdate().getSecond();
            String endDate = debitDocument.getEndate().getDayOfMonth() + "-" + debitDocument.getEndate().getMonthValue() + "-" + debitDocument.getEndate().getYear() + " " + debitDocument.getEndate().getHour() + ":" + debitDocument.getEndate().getMinute() + ":" + debitDocument.getEndate().getSecond();
            NepaliDateDTO nepaliBillDateDTO = dateConverterService.getNepaliDateFromEnglishDate(billDate);
            NepaliDateDTO nepaliStartDateDTO = dateConverterService.getNepaliDateFromEnglishDate(startDate);
            NepaliDateDTO nepaliEndDateDTO = dateConverterService.getNepaliDateFromEnglishDate(endDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Map<Integer, Double> getServiceWiseRatioForInvoiceAmount(Long invoiceId) {
        AtomicReference<Double> invoiceAmountWithoutTax = new AtomicReference<>(0d);
        Map<Integer, Double> serviceWiseDbrs = new HashMap<>();
        QDebitDocDetails qDebitDocDetails = QDebitDocDetails.debitDocDetails;
        BooleanExpression expression = qDebitDocDetails.isNotNull();
        expression = expression.and(qDebitDocDetails.debitdocumentid.eq(invoiceId.intValue()));
        List<DebitDocDetails> docDetails = (List<DebitDocDetails>) debitDocDetailRepository.findAll(expression);
        if (docDetails != null && !docDetails.isEmpty()) {
            docDetails.stream().forEach(data -> {
                if (data.getPlanId() != null) {
                    Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(data.getPlanId()));
                    invoiceAmountWithoutTax.updateAndGet(v -> v + (data.getSubtotal() + data.getDiscount()));
                    if (plan.isPresent()) {
                        PostpaidPlan postpaidPlan = plan.get();
                        if (serviceWiseDbrs.containsKey(postpaidPlan.getServiceId()))
                            serviceWiseDbrs.put(postpaidPlan.getServiceId(), serviceWiseDbrs.get(postpaidPlan.getServiceId()) + data.getSubtotal() + data.getDiscount());
                        else
                            serviceWiseDbrs.put(postpaidPlan.getServiceId(), data.getSubtotal() + data.getDiscount());
                    }
                } else {
                    if (serviceWiseDbrs.containsKey(-1))
                        serviceWiseDbrs.put(-1, data.getSubtotal() + data.getDiscount());
                    else
                        serviceWiseDbrs.put(-1, serviceWiseDbrs.get(-1) + data.getSubtotal() + data.getDiscount());
                }
            });
        }

        serviceWiseDbrs.entrySet().stream().forEach(data -> {
            Double planAmount = data.getValue();
            Double invoiceAmount = invoiceAmountWithoutTax.get();
            Double percentageRatio = (planAmount / invoiceAmount) * 100.00d;
            data.setValue(percentageRatio);
        });
        return serviceWiseDbrs;
    }


    public Map<Integer, Double> getChargeWiseRatioForInvoiceAmount(Long invoiceId, Long serviceId) {
        AtomicReference<Double> invoiceAmountWithoutTax = new AtomicReference<>(0d);
        Map<Integer, Double> serviceWiseDbrs = new HashMap<>();
        QDebitDocDetails qDebitDocDetails = QDebitDocDetails.debitDocDetails;
        BooleanExpression expression = qDebitDocDetails.isNotNull();
        expression = expression.and(qDebitDocDetails.debitdocumentid.eq(invoiceId.intValue()));
        List<DebitDocDetails> docDetails = (List<DebitDocDetails>) debitDocDetailRepository.findAll(expression);
        if (docDetails != null && !docDetails.isEmpty()) {
            docDetails.stream().forEach(data ->
            {
                if (data.getPlanId() != null) {
                    invoiceAmountWithoutTax.updateAndGet(v -> v + (data.getSubtotal() + data.getDiscount()));
                    Optional<PostpaidPlan> plan = postpaidPlanRepo.findById(Integer.parseInt(data.getPlanId()));
                    if (plan.isPresent() && plan.get().getServiceId().equals(serviceId.intValue())) {
                        if (plan.isPresent()) {
                            PostpaidPlan postpaidPlan = plan.get();
                            if (serviceWiseDbrs.containsKey(data.getChargeid()))
                                serviceWiseDbrs.put(data.getChargeid(), serviceWiseDbrs.get(data.getChargeid()) + data.getSubtotal() + data.getDiscount());
                            else
                                serviceWiseDbrs.put(data.getChargeid(), data.getSubtotal() + data.getDiscount());
                        }
                    }

                } else if (serviceId.intValue() == -1) {
                    if (serviceWiseDbrs.containsKey(-1))
                        serviceWiseDbrs.put(-1, data.getSubtotal() + data.getDiscount());
                    else
                        serviceWiseDbrs.put(-1, serviceWiseDbrs.get(-1) + data.getSubtotal() + data.getDiscount());
                }
            });
        }

        serviceWiseDbrs.entrySet().stream().forEach(data -> {
            Double planAmount = data.getValue();
            Double invoiceAmount = invoiceAmountWithoutTax.get();
            Double percentageRatio = (planAmount / invoiceAmount) * 100.00d;
            data.setValue(percentageRatio);
        });
        return serviceWiseDbrs;
    }

    public List<CustomerDBR> getCustomerDBRListBetweenStartDateAndEndDate(LocalDate pendingDate, DebitDocument
            document) {
        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
        BooleanExpression expression = qCustomerDBR.isNotNull();
        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.startdate.between(pendingDate, document.getEndate().toLocalDate())).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.findAll(expression);
        return dbrList;
    }

    public List<CustomerChargeDBR> getCustomerChargeDBRListBetweenStartDateAndEndDate(LocalDate
                                                                                              pendingDate, DebitDocument document) {
        QCustomerChargeDBR qCustomerChargeDBR = QCustomerChargeDBR.customerChargeDBR;
        BooleanExpression expression = qCustomerChargeDBR.isNotNull();
        expression = expression.and(qCustomerChargeDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerChargeDBR.startdate.between(pendingDate, document.getEndate().toLocalDate()));
        List<CustomerChargeDBR> dbrList = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAll(expression);
        return dbrList;
    }

    public List<CustomerChargeDBR> getCustomerChargeDBRListBetweenStartDateAndEndDateAndcustInvMappId(LocalDate
                                                                                                              pendingDate, DebitDocument document, List<Long> custInvMapp) {
        QCustomerChargeDBR qCustomerChargeDBR = QCustomerChargeDBR.customerChargeDBR;
        BooleanExpression expression = qCustomerChargeDBR.isNotNull();
        expression = expression.and(qCustomerChargeDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerChargeDBR.startdate.between(pendingDate, document.getEndate().toLocalDate()))
                .and(qCustomerChargeDBR.custInvMappingId.in(custInvMapp));
        List<CustomerChargeDBR> dbrList = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAll(expression);
        return dbrList;
    }

    public List<CustomerChargeDBR> getCustomerChargeDBRListBetweenStartDateAndEndDateAndByService(LocalDate
                                                                                                          pendingDate, DebitDocument document, List<Long> custPackIds) {
        QCustomerChargeDBR qCustomerChargeDBR = QCustomerChargeDBR.customerChargeDBR;
        BooleanExpression expression = qCustomerChargeDBR.isNotNull();
        expression = expression.and(qCustomerChargeDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerChargeDBR.cprid.in(custPackIds)).and(qCustomerChargeDBR.startdate.between(pendingDate, document.getEndate().toLocalDate()));
        List<CustomerChargeDBR> dbrList = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAll(expression);
        return dbrList;
    }

    public List<CustomerDBR> getCustomerDBRListBetweenStartDateAndEndDateAndByService(LocalDate
                                                                                              pendingDate, DebitDocument document, List<Long> custPackIds) {
        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
        BooleanExpression expression = qCustomerDBR.isNotNull();
        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.cprid.in(custPackIds)).and(qCustomerDBR.startdate.between(pendingDate, document.getEndate().toLocalDate())).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.findAll(expression);
        return dbrList;
    }

    public List<CustomerChargeDBR> getCustomerDBRListBetweenStartDateAndEndDateAtChargeLevel(LocalDate
                                                                                                     pendingDate, DebitDocument document) {
        QCustomerChargeDBR qCustomerDBR = QCustomerChargeDBR.customerChargeDBR;
        BooleanExpression expression = qCustomerDBR.isNotNull();
        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.startdate.between(pendingDate, document.getEndate().toLocalDate())).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerChargeDBR> dbrList = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAll(expression);
        return dbrList;
    }


    public List<CustomerDBR> getCustomerDBRListBetweenStartDateAndJustBeforeCurrentDate(DebitDocument document) {
        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
        BooleanExpression expression = qCustomerDBR.isNotNull();
        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.startdate.between(document.getStartdate().toLocalDate(), document.getEndate().toLocalDate()));
        List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.findAll(expression);
        return dbrList;
    }


    public List<CustomerDBR> getCustomerDBRListBetweenStartDateAndEndDate1(LocalDate pendingDate, DebitDocument
            document) {
        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
        BooleanExpression expression = qCustomerDBR.isNotNull();
        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.startdate.between(pendingDate, document.getEndate().toLocalDate())).and(qCustomerDBR.isDirectCharge.eq(false)).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.findAll(expression);
        return dbrList;
    }

    public List<CustomerChargeDBR> getCustomerDBRListBetweenStartDateAndEndDate1AtChargeLevel(LocalDate
                                                                                                      pendingDate, DebitDocument document) {
        QCustomerChargeDBR qCustomerDBR = QCustomerChargeDBR.customerChargeDBR;
        BooleanExpression expression = qCustomerDBR.isNotNull();
        expression = expression.and(qCustomerDBR.invoiceId.eq(document.getId().longValue())).and(qCustomerDBR.startdate.between(pendingDate, document.getEndate().toLocalDate())).and(qCustomerDBR.isDirectCharge.eq(false)).and(qCustomerDBR.cprid.isNotNull());
        List<CustomerChargeDBR> dbrList = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAll(expression);
        return dbrList;
    }

    public Map<Integer, Double> getServiceWiseRatioForCreditNoteAmount(List<CustomerDBR> customerDBRList) {
        Map<Integer, Double> serviceWiseDbrs = new HashMap<>();
        List<Integer> serviceIdList = customerDBRList.stream().map(x -> x.getServiceId().intValue()).distinct().collect(Collectors.toList());
        Double totalAmount = customerDBRList.stream().mapToDouble(x -> x.getDbr()).sum();

        if (serviceIdList != null && !serviceIdList.isEmpty()) {
            serviceIdList.stream().forEach(x -> {
                Double totalDbr = customerDBRList.stream().filter(y -> y.getServiceId().equals(x.longValue())).mapToDouble(y -> y.getDbr()).sum();
                Double percentage = (totalDbr / totalAmount) * 100.0d;
                serviceWiseDbrs.put(x.intValue(), percentage);

            });
        }
        return serviceWiseDbrs;
    }


    public Map<Integer, Double> getChargeWiseRatioForCreditNoteAmount
            (List<CustomerChargeDBR> customerDBRList, Integer serviceId) {
        Map<Integer, Double> serviceWiseDbrs = new HashMap<>();
        customerDBRList = customerDBRList.stream().filter(x -> x.getServiceId().equals(serviceId.longValue())).collect(Collectors.toList());
        List<Integer> chargeIdList = customerDBRList.stream().map(x -> x.getChargeId().intValue()).distinct().collect(Collectors.toList());
        Double totalAmount = customerDBRList.stream().mapToDouble(x -> x.getDbr()).sum();

        if (chargeIdList != null && !chargeIdList.isEmpty()) {
            List<CustomerChargeDBR> finalCustomerDBRList = customerDBRList;
            chargeIdList.stream().forEach(x -> {
                Double totalDbr = finalCustomerDBRList.stream().filter(y -> y.getChargeId().equals(x.longValue())).mapToDouble(y -> y.getDbr()).sum();
                Double percentage = (totalDbr / totalAmount) * 100.0d;
                serviceWiseDbrs.put(x.intValue(), percentage);

            });
        }
        return serviceWiseDbrs;
    }

    public void updateServiceAreaIdForCustomer(Integer custId, ServiceArea serviceArea, LocalDate currentDate) {
        if (custId != null && serviceArea != null) {
            List<CustomerDBR> dbrList = (List<CustomerDBR>) customerDBRRepository.getAllByCustomerId(custId);
            dbrList = dbrList.stream().filter(x -> x.getStartdate().equals(currentDate)).collect(Collectors.toList());
            if (dbrList != null && !dbrList.isEmpty()) {
                dbrList.stream().forEach(x -> {
                    x.setServiceArea(serviceArea.getId());
                    customerDBRRepository.save(x);
                });
            }
        }
    }

    public String getServiceNameById(Long serviceId) {
        Optional<Services> services = serviceRepository.findById(serviceId);
        if (services.isPresent())
            return services.get().getServiceName();
        else
            return "Inventory As";
    }

    public Double getTaxAmountForCreditNoteAmount(Double creditNoteAmount, Integer debitDocId) {
        DebitDocument debitDocument = debitDocRepository.findById(debitDocId).get();
        Double totalTax = 0.0;
        if (debitDocument != null) {
            List<DebitDocDetails> details = debitDocument.getDebitDocDetailsList();
            for (int i = 0; i < details.size(); i++) {
                DebitDocDetails docDetails = details.get(i);
                if (docDetails != null) {
                    Double docChargeAmount = docDetails.getTotalamount();
                    Double percentage = (docChargeAmount / debitDocument.getTotalamount()) * 100.0;
                    Double newDocChargeAmount = (creditNoteAmount * percentage) / 100.0;
                    List<DebitDocumentTAXRel> taxRelList = debitDocument.getDebitDocumentTAXRels().stream().filter(x -> x.getChargeid().equals(docDetails.getChargeid().toString())).collect(Collectors.toList());
                    Double newTax = 0.0;
                    Double newCharge = newDocChargeAmount;
                    for (int j = taxRelList.size() - 1; j >= 0; j--) {
                        DebitDocumentTAXRel taxRel = taxRelList.get(j);
                        Double taxPercentage = taxRel.getPercentage();
                        newTax = (newCharge * taxPercentage) / (100 + taxPercentage);
                        newCharge = newCharge - newTax;
                        totalTax += newTax;
                    }
                }
            }
        }
        return totalTax;
    }

    public void extendsValidityForDBR(Long extendValidity, Integer planId, Integer debitDocId, Integer custId) {
        DecimalFormat df = new DecimalFormat("0.00");
        if (debitDocId != null) {
            Optional<DebitDocument> debitDocument = debitDocRepository.findById(debitDocId);
            if (debitDocument.isPresent()) {
                if (!LocalDate.now().isBefore(debitDocument.get().getStartdate().toLocalDate())) {
                    if (!LocalDate.now().isAfter(debitDocument.get().getEndate().toLocalDate())) {
                        List<CustomerDBR> customerDBRList = getCustomerDBRListBetweenStartDateAndEndDate1(LocalDate.now(), debitDocument.get());
                        customerDBRList = customerDBRList.stream().filter(x -> x.getPlanid().equals(planId.longValue())).collect(Collectors.toList());
                        Double pendingAmount = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getPlanid().equals(planId.longValue())).filter(x -> x.getStartdate().equals(LocalDate.now())).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));

                        if (pendingAmount != null && pendingAmount > 0) {
                            Integer days = customerDBRList.size() + extendValidity.intValue();
                            Double dbr = pendingAmount / days;
                            Double cummRevenue = 0.0;
                            Double customerPendingRevenue = pendingAmount;
                            if (LocalDate.now().isAfter(debitDocument.get().getStartdate().toLocalDate()))
                                cummRevenue = pendingAmount;
                            for (CustomerDBR customerDBR : customerDBRList) {
                                cummRevenue += dbr;
                                customerPendingRevenue -= dbr;
                                customerDBR.setDbr(dbr);
                                customerDBR.setPendingamt(customerPendingRevenue);
                                customerDBR.setCumm_revenue(cummRevenue);
                                customerDBR.setEnddate(customerDBR.getEnddate().plusDays(extendValidity));
                                customerDBRRepository.save(customerDBR);
                            }

                            CustomerDBR customerDBR = customerDBRList.get(customerDBRList.size() - 1);
                            for (int i = 0; i < extendValidity; i++) {
                                CustomerDBR d = new CustomerDBR();
                                cummRevenue += dbr;
                                customerPendingRevenue -= dbr;
                                d.setDbr(dbr);
                                d.setPendingamt(customerPendingRevenue);
                                d.setCumm_revenue(cummRevenue);
                                d.setStartdate(customerDBR.getStartdate().plusDays(i + 1));
                                d.setCustid(custId.longValue());
                                d.setPlanid(planId.longValue());
                                d.setIsDirectCharge(false);
                                d.setBuId(customerDBR.getBuId());
                                d.setMvnoId(customerDBR.getMvnoId());
                                d.setServiceId(customerDBR.getServiceId());
                                d.setServiceArea(customerDBR.getServiceArea());
                                d.setCprid(customerDBR.getCprid());
                                d.setCusttype(customerDBR.getCusttype());
                                d.setCustname(customerDBR.getCustname());
                                d.setEnddate(customerDBR.getEnddate().plusDays(extendValidity));
                                d.setInvoiceId(customerDBR.getInvoiceId());
                                d.setPlanname(customerDBR.getPlanname());
                                d.setValidity_days(customerDBR.getValidity_days());
                                d.setStatus(customerDBR.getStatus());
                                d.setOffer_price(customerDBR.getOffer_price());
                                customerDBRRepository.save(d);
                            }
                            debitDocument.get().setEndate(debitDocument.get().getEndate().plusDays(extendValidity));
                            debitDocRepository.save(debitDocument.get());
                        }
                    }
                } else {
                    List<CustomerDBR> customerDBRList = getCustomerDBRListBetweenStartDateAndEndDate1(debitDocument.get().getStartdate().toLocalDate(), debitDocument.get());
                    customerDBRList = customerDBRList.stream().filter(x -> x.getPlanid().equals(planId.longValue())).collect(Collectors.toList());
                    Double pendingAmount = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getPlanid().equals(planId.longValue())).filter(x -> x.getStartdate().equals(debitDocument.get().getStartdate().toLocalDate())).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));

                    if (pendingAmount != null && pendingAmount > 0) {
                        Integer days = customerDBRList.size() + extendValidity.intValue();
                        Double dbr = pendingAmount / days;
                        Double cummRevenue = 0.0;
                        Double customerPendingRevenue = pendingAmount;

                        for (CustomerDBR customerDBR : customerDBRList) {
                            cummRevenue += dbr;
                            customerPendingRevenue -= dbr;
                            customerDBR.setDbr(dbr);
                            customerDBR.setPendingamt(customerPendingRevenue);
                            customerDBR.setCumm_revenue(cummRevenue);
                            customerDBR.setEnddate(customerDBR.getEnddate().plusDays(extendValidity));
                            customerDBRRepository.save(customerDBR);
                        }

                        CustomerDBR customerDBR = customerDBRList.get(customerDBRList.size() - 1);
                        for (int i = 0; i < extendValidity; i++) {
                            CustomerDBR d = new CustomerDBR();
                            cummRevenue += dbr;
                            customerPendingRevenue -= dbr;
                            d.setDbr(dbr);
                            d.setPendingamt(customerPendingRevenue);
                            d.setCumm_revenue(cummRevenue);
                            d.setStartdate(customerDBR.getStartdate().plusDays(i + 1));
                            d.setCustid(custId.longValue());
                            d.setPlanid(planId.longValue());
                            d.setIsDirectCharge(false);
                            d.setBuId(customerDBR.getBuId());
                            d.setMvnoId(customerDBR.getMvnoId());
                            d.setServiceId(customerDBR.getServiceId());
                            d.setServiceArea(customerDBR.getServiceArea());
                            d.setCprid(customerDBR.getCprid());
                            d.setCusttype(customerDBR.getCusttype());
                            d.setCustname(customerDBR.getCustname());
                            d.setEnddate(customerDBR.getEnddate().plusDays(extendValidity));
                            d.setInvoiceId(customerDBR.getInvoiceId());
                            d.setPlanname(customerDBR.getPlanname());
                            d.setValidity_days(customerDBR.getValidity_days());
                            d.setStatus(customerDBR.getStatus());
                            d.setOffer_price(customerDBR.getOffer_price());
                            customerDBRRepository.save(d);
                        }
                        debitDocument.get().setEndate(debitDocument.get().getEndate().plusDays(extendValidity));
                        debitDocRepository.save(debitDocument.get());
                    }
                }
            }
        }
    }

    private void addRevertCommissionEntryInTmp(DebitDocument document, Double creditNoteAmount, Double
            prorateCommission, List<TempPartnerLedgerDetail> tempCommissionList) {
        if (document.getCustomer().getIs_from_pwc() && document.getCustomer().getLcoId() != null) {
            TempPartnerLedgerDetail reverseCommission = new TempPartnerLedgerDetail();
            reverseCommission.setAmount(prorateCommission);
            reverseCommission.setDebitDocId(document.getId().longValue());
            reverseCommission.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
            reverseCommission.setCustid(document.getCustomer().getId());
            reverseCommission.setPartner(document.getCustomer().getPartner());
            reverseCommission.setIsDeleted(false);
            //reverseCommission.setCreateDate(LocalDateTime.now());
            reverseCommission.setDescription("Commission reverted for the invoice " + document.getDocnumber());
            reverseCommission.setTranscategory(CommonConstants.TRANS_CATEGORY_REVERT_COMMISSION);
            reverseCommission.setCommission(0.0);
            reverseCommission.setGrossOfferPrice(document.getTotalamount());
            reverseCommission.setOfferprice(document.getTotalamount());
            //reverseCommission.setPlanid("");
            reverseCommission.setCREATE_DATE(LocalDateTime.now());

            if (tempCommissionList != null && !tempCommissionList.isEmpty()) {
                Double agr = tempCommissionList.stream().mapToDouble(x -> x.getAgr_amount()).sum();
                Double tax = tempCommissionList.stream().mapToDouble(x -> x.getTax()).sum();
                Double offerPrice = tempCommissionList.stream().mapToDouble(x -> x.getOfferprice()).sum();
                Double revertOfferPrice = creditNoteAmount * (offerPrice / document.getTotalamount());
                Double revertTax = revertOfferPrice * (tax / offerPrice);
                Double revertAgr = (revertOfferPrice - revertTax) * (agr / (offerPrice - tax));
                reverseCommission.setTax(revertTax);
                reverseCommission.setAgr_amount(revertAgr);
                reverseCommission.setOfferprice(revertOfferPrice);
            }
            tempPartnerLedgerDetailsRepository.save(reverseCommission);
        } else {
            TempPartnerLedgerDetail reverseCommission = new TempPartnerLedgerDetail();
            reverseCommission.setAmount(0.0d);
            reverseCommission.setDebitDocId(document.getId().longValue());
            reverseCommission.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
            reverseCommission.setCustid(document.getCustomer().getId());
            reverseCommission.setPartner(document.getCustomer().getPartner());
            reverseCommission.setIsDeleted(false);
            //reverseCommission.setCreateDate(LocalDateTime.now());
            reverseCommission.setDescription("Commission reverted for the invoice " + document.getDocnumber());
            reverseCommission.setTranscategory("Revert Commission");
            reverseCommission.setCommission(prorateCommission);
            reverseCommission.setGrossOfferPrice(document.getTotalamount());
            reverseCommission.setOfferprice(document.getTotalamount());
            //reverseCommission.setPlanid("");
            reverseCommission.setCREATE_DATE(LocalDateTime.now());

            if (tempCommissionList != null && !tempCommissionList.isEmpty()) {
                Double agr = tempCommissionList.stream().mapToDouble(x -> x.getAgr_amount()).sum();
                Double tax = tempCommissionList.stream().mapToDouble(x -> x.getTax()).sum();
                Double offerPrice = tempCommissionList.stream().mapToDouble(x -> x.getOfferprice()).sum();
                Double revertOfferPrice = creditNoteAmount * (offerPrice / document.getTotalamount());
                Double revertTax = revertOfferPrice * (tax / offerPrice);
                Double revertAgr = (revertOfferPrice - revertTax) * (agr / (offerPrice - tax));
                reverseCommission.setTax(revertTax);
                reverseCommission.setAgr_amount(revertAgr);
                reverseCommission.setOfferprice(revertOfferPrice);
            }
            tempPartnerLedgerDetailsRepository.save(reverseCommission);
        }
    }

    public void dbrHoldOnServicePause(List<CustPlanMappping> list) {
        List<CustomerDBR> dbrList = new ArrayList<>();
        List<CustomerChargeDBR> dbrChargeList = new ArrayList<>();

        List<Long> cprIds = list.stream().map(x -> x.getId().longValue()).collect(Collectors.toList());
        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
        BooleanExpression expression = qCustomerDBR.isNotNull();
        expression = expression.and(qCustomerDBR.cprid.in(cprIds));
        expression = expression.and(qCustomerDBR.startdate.after(LocalDate.now().minusDays(1)));
        dbrList = (List<CustomerDBR>) customerDBRRepository.findAll(expression);

        QCustomerChargeDBR qCustomerChargeDBR = QCustomerChargeDBR.customerChargeDBR;
        expression = qCustomerChargeDBR.isNotNull();
        expression = expression.and(qCustomerChargeDBR.cprid.in(cprIds));
        expression = expression.and(qCustomerChargeDBR.startdate.after(LocalDate.now().minusDays(1)));
        dbrChargeList = (List<CustomerChargeDBR>) customerChargeDBRRepository.findAll(expression);

        moveDbrListIntoTemp(dbrList);
        moveDbrChargeListIntoTemp(dbrChargeList);
        customerDBRRepository.deleteAll(dbrList);
        customerChargeDBRRepository.deleteAll(dbrChargeList);
    }

    public void moveDbrListIntoTemp(List<CustomerDBR> dbrList) {
        if (dbrList != null && !dbrList.isEmpty()) {
            dbrList.stream().forEach(x -> {
                TempCustomerDBR dbr = new TempCustomerDBR();
                dbr.setBuId(x.getBuId());
                dbr.setCprid(x.getCprid());
                dbr.setCustname(x.getCustname());
                dbr.setCustid(x.getCustid());
                dbr.setCusttype(x.getCusttype());
                dbr.setDbrid(x.getDbrid());
                dbr.setDbr(x.getDbr());
                dbr.setInvoiceId(x.getInvoiceId());
                dbr.setEnddate(x.getEnddate());
                dbr.setStartdate(x.getStartdate());
                dbr.setRemark(x.getRemark());
                dbr.setServiceArea(x.getServiceArea());
                dbr.setCumm_revenue(x.getCumm_revenue());
                dbr.setDeleteFlag(x.getDeleteFlag());
                dbr.setIsDirectCharge(x.getIsDirectCharge());
                dbr.setMvnoId(x.getMvnoId());
                dbr.setOffer_price(x.getOffer_price());
                dbr.setPendingamt(x.getPendingamt());
                dbr.setPlanid(x.getPlanid());
                dbr.setPlanname(x.getPlanname());
                dbr.setStatus(x.getStatus());
                dbr.setValidity_days(x.getValidity_days());
                dbr.setPartnerId(x.getPartnerId());
                dbr.setServiceId(x.getServiceId());
                tempCustomerDBRRepository.save(dbr);
            });
        }
    }

    private void moveDbrChargeListIntoTemp(List<CustomerChargeDBR> dbrChargeList) {
        if (dbrChargeList != null && !dbrChargeList.isEmpty()) {
            dbrChargeList.stream().forEach(x -> {
                TempCustomerChargeDBR dbr = new TempCustomerChargeDBR();
                dbr.setBuId(x.getBuId());
                dbr.setCprid(x.getCprid());
                dbr.setCustname(x.getCustname());
                dbr.setCustid(x.getCustid());
                dbr.setCusttype(x.getCusttype());
                dbr.setDbrid(x.getDbrid());
                dbr.setDbr(x.getDbr());
                dbr.setInvoiceId(x.getInvoiceId());
                dbr.setEnddate(x.getEnddate());
                dbr.setStartdate(x.getStartdate());
                dbr.setRemark(x.getRemark());
                dbr.setServiceArea(x.getServiceArea());
                dbr.setCumm_revenue(x.getCumm_revenue());
                dbr.setDeleteFlag(x.getDeleteFlag());
                dbr.setIsDirectCharge(x.getIsDirectCharge());
                dbr.setMvnoId(x.getMvnoId());
                dbr.setOffer_price(x.getOffer_price());
                dbr.setPendingamt(x.getPendingamt());
                dbr.setPlanid(x.getPlanid());
                dbr.setPlanname(x.getPlanname());
                dbr.setStatus(x.getStatus());
                dbr.setValidity_days(x.getValidity_days());
                dbr.setServiceId(x.getServiceId());
                tempCustomerChargeDBRRepository.save(dbr);
            });
        }
    }

    public void dbrResumeOnServiceResume(List<CustPlanMappping> list) {
        List<TempCustomerDBR> dbrList = new ArrayList<>();
        List<TempCustomerChargeDBR> dbrChargeList = new ArrayList<>();

        List<Long> cprIds = list.stream().map(x -> x.getId().longValue()).collect(Collectors.toList());
        QTempCustomerDBR qTempCustomerDBR = QTempCustomerDBR.tempCustomerDBR;
        BooleanExpression expression = qTempCustomerDBR.isNotNull();
        expression = expression.and(qTempCustomerDBR.cprid.in(cprIds));
        dbrList = (List<TempCustomerDBR>) tempCustomerDBRRepository.findAll(expression);

        QTempCustomerChargeDBR qTempCustomerChargeDBR = QTempCustomerChargeDBR.tempCustomerChargeDBR;
        expression = qTempCustomerChargeDBR.isNotNull();
        expression = expression.and(qTempCustomerChargeDBR.cprid.in(cprIds));
        dbrChargeList = (List<TempCustomerChargeDBR>) tempCustomerChargeDBRRepository.findAll(expression);

        moveTempDbrListIntoMain(dbrList);
        moveTempDbrChargeListIntoMain(dbrChargeList);
        tempCustomerDBRRepository.deleteAll(dbrList);
        tempCustomerChargeDBRRepository.deleteAll(dbrChargeList);
    }

    private void moveTempDbrChargeListIntoMain(List<TempCustomerChargeDBR> dbrChargeList) {
        if (dbrChargeList != null && !dbrChargeList.isEmpty()) {
            List<Long> cprIds = dbrChargeList.stream().map(x -> x.getCprid()).distinct().collect(Collectors.toList());
            if (cprIds != null && !cprIds.isEmpty()) {
                cprIds.stream().forEach(cprId -> {
                    List<TempCustomerChargeDBR> list = dbrChargeList.stream().filter(x -> x.getCprid().equals(cprId)).collect(Collectors.toList());
                    AtomicReference<Integer> count = new AtomicReference<>(0);
                    LocalDate startDate = LocalDate.now();
                    list.stream().forEach(x -> {
                        CustomerChargeDBR dbr = new CustomerChargeDBR();
                        dbr.setBuId(x.getBuId());
                        dbr.setChargeId(x.getChargeId());
                        dbr.setCprid(x.getCprid());
                        dbr.setCustname(x.getCustname());
                        dbr.setPlanname(x.getPlanname());
                        dbr.setCustid(x.getCustid());
                        dbr.setCusttype(x.getCusttype());
                        //dbr.setDbrid(x.getDbrid());
                        dbr.setDbr(x.getDbr());
                        dbr.setInvoiceId(x.getInvoiceId());
                        dbr.setEnddate(x.getEnddate());
                        dbr.setStartdate(startDate.plusDays(count.get()));
                        dbr.setRemark(x.getRemark());
                        dbr.setServiceArea(x.getServiceArea());
                        dbr.setCumm_revenue(x.getCumm_revenue());
                        dbr.setDeleteFlag(x.getDeleteFlag());
                        dbr.setIsDirectCharge(x.getIsDirectCharge());
                        dbr.setMvnoId(x.getMvnoId());
                        dbr.setOffer_price(x.getOffer_price());
                        dbr.setPendingamt(x.getPendingamt());
                        dbr.setPlanid(x.getPlanid());
                        dbr.setStatus(x.getStatus());
                        dbr.setValidity_days(x.getValidity_days());
                        dbr.setServiceId(x.getServiceId());
                        customerChargeDBRRepository.save(dbr);
                        count.getAndSet(count.get() + 1);
                    });
                });
            }
        }
    }

    private void moveTempDbrListIntoMain(List<TempCustomerDBR> dbrList) {
        if (dbrList != null && !dbrList.isEmpty()) {
            List<Long> cprIds = dbrList.stream().map(x -> x.getCprid()).distinct().collect(Collectors.toList());
            if (cprIds != null && !cprIds.isEmpty()) {
                cprIds.stream().forEach(cprId -> {
                    List<TempCustomerDBR> list = dbrList.stream().filter(x -> x.getCprid().equals(cprId)).collect(Collectors.toList());
                    AtomicReference<Integer> count = new AtomicReference<>(0);
                    LocalDate startDate = LocalDate.now();
                    list.stream().forEach(x -> {
                        CustomerDBR dbr = new CustomerDBR();
                        dbr.setBuId(x.getBuId());
                        dbr.setCprid(x.getCprid());
                        dbr.setCustname(x.getCustname());
                        dbr.setPlanname(x.getPlanname());
                        dbr.setCustid(x.getCustid());
                        dbr.setCusttype(x.getCusttype());
                        //dbr.setDbrid(x.getDbrid());
                        dbr.setDbr(x.getDbr());
                        dbr.setInvoiceId(x.getInvoiceId());
                        dbr.setEnddate(x.getEnddate());
                        dbr.setStartdate(startDate.plusDays(count.get()));
                        dbr.setRemark(x.getRemark());
                        dbr.setServiceArea(x.getServiceArea());
                        dbr.setCumm_revenue(x.getCumm_revenue());
                        dbr.setDeleteFlag(x.getDeleteFlag());
                        dbr.setIsDirectCharge(x.getIsDirectCharge());
                        dbr.setMvnoId(x.getMvnoId());
                        dbr.setOffer_price(x.getOffer_price());
                        dbr.setPendingamt(x.getPendingamt());
                        dbr.setPlanid(x.getPlanid());
                        dbr.setStatus(x.getStatus());
                        dbr.setValidity_days(x.getValidity_days());
                        dbr.setServiceId(x.getServiceId());
                        customerDBRRepository.save(dbr);
                        count.getAndSet(count.get() + 1);
                    });
                });
            }
        }
    }

    public List<CustomerChargeDBR> findAllCustomerChargedbrByDebitDoc(DebitDocument debitDocument) {
        List<CustomerChargeDBR> customerChargeDBRList = new ArrayList<>();
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByDebitdocidAndCustPlanStatus(debitDocument.getId(), CommonConstants.STOP_STATUS);
        if (CollectionUtils.isEmpty(custPlanMapppingList)) {
            //For cancel  and regenerate
            CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(debitDocument.getCustpackrelid());
            if (custPlanMappping != null) {
                custPlanMapppingList.add(custPlanMappping);
                if (custPlanMappping.getPlanGroup() != null) {
                    List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByCustomerIsAndPlanGroupAndIsHold(custPlanMappping.getCustomer(), custPlanMappping.getPlanGroup(), false);
                    if (!CollectionUtils.isEmpty(custPlanMapppings)) {
                        custPlanMapppingList.addAll(custPlanMapppings);
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(custPlanMapppingList)) {
            List<Integer> cprIds = custPlanMapppingList.stream().map(CustPlanMappping::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(cprIds))
                customerChargeDBRList = getCustomerChargeDBRListBetweenStartDateAndEndDateAndByService(LocalDate.now(), debitDocument, cprIds.stream().mapToLong(Integer::longValue).boxed().collect(Collectors.toList()));
        }
        return customerChargeDBRList;
    }
}
