package com.adopt.apigw.modules.dashboard;

import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.QCustomers;
import com.adopt.apigw.model.common.QStaffUser;
import com.adopt.apigw.model.lead.LeadMaster;
import com.adopt.apigw.model.lead.QLeadMaster;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.model.radius.LiveUser;
import com.adopt.apigw.model.radius.QLiveUser;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.modules.InventoryManagement.inward.QInward;
import com.adopt.apigw.modules.InventoryManagement.outward.Outward;
import com.adopt.apigw.modules.InventoryManagement.outward.QOutward;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.modules.InventoryManagement.product.QProduct;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwner;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerRepository;
import com.adopt.apigw.modules.InventoryManagement.productOwner.QProductOwner;
import com.adopt.apigw.modules.InventoryManagement.warehouse.*;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedgerDetails;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerPayment;
import com.adopt.apigw.modules.PartnerLedger.domain.QPartnerLedgerDetails;
import com.adopt.apigw.modules.PartnerLedger.domain.QPartnerPayment;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.Teams.domain.QTeamUserMapping;
import com.adopt.apigw.modules.Teams.domain.QTeams;
import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.modules.customerDocDetails.domain.QCustomerDocDetails;
import com.adopt.apigw.modules.customerDocDetails.repository.CustomerDocDetailsRepository;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.modules.subscriber.model.Constants;
import com.adopt.apigw.modules.tickets.domain.Case;
import com.adopt.apigw.modules.tickets.domain.CaseAssignment;
import com.adopt.apigw.modules.tickets.domain.QCase;
import com.adopt.apigw.modules.tickets.domain.QCaseAssignment;
import com.adopt.apigw.repository.LeadMasterRepository;
import com.adopt.apigw.service.LeadMasterService;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    StaffUserService staffUserService;
    @Autowired
    ClientServiceSrv clientServiceSrv;


    public Integer MAX_PAGE_SIZE;

    public Map<String, String> sortColMap = new HashMap<>();
    public PageRequest pageRequest = null;
    @Autowired
    private LeadMasterRepository leadMasterRepository;

    @Autowired
    LeadMasterService leadMasterService;

    @Autowired
    CustomerDocDetailsService customerDocDetailsService;

    @Autowired
    CustomerDocDetailsRepository customerDocDetailsRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductOwnerRepository productOwnerRepository;
    @Autowired
    WarehouseManagementServiceImpl warehouseManagementService;
    @Autowired
    WarehouseManagementRepository warehouseManagementRepository;

    @Autowired
    ServiceAreaService serviceAreaService;
    @Override
    public Map<String, String> typeWiseCustomerCount(Long mvnoId) {
        JPAQuery<Customers> query = new JPAQuery<>(entityManager);
        List<Tuple> queryResult;
        Map<String, String> dataMap = new HashMap<>();
        QCustomers qCustomers = QCustomers.customers;
        BooleanExpression booleanExpression = qCustomers.isNotNull().and(qCustomers.isDeleted.eq(false)).and(qCustomers.status.notEqualsIgnoreCase(Constants.NEW_ACTIVE));
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCustomers.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCustomers.buId.in(staffUserService.getBUIdsFromCurrentStaff())).and(qCustomers.servicearea.id.in(staffUserService.getServiceAreaIdList())));

                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCustomers.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCustomers.servicearea.id.in(staffUserService.getServiceAreaIdList())));
                }
            }
            queryResult = query.from(qCustomers).groupBy(qCustomers.custtype).select(qCustomers.custtype, qCustomers.custtype.count()).where(booleanExpression).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    dataMap.put(result.get(qCustomers.custtype) + " Customers", Objects.requireNonNull(result.get(qCustomers.custtype.count())).toString());
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, String> getStatusWiseCount(Long mvnoId) {
        JPAQuery<Customers> query = new JPAQuery<>(entityManager);
        QCustomers qCustomer = QCustomers.customers;
        List<Tuple> queryResult;
        Map<String, String> dataMap = new HashMap<>();
        QCustomers qCustomers = QCustomers.customers;
        BooleanExpression booleanExpression = qCustomers.isNotNull().and(qCustomers.isDeleted.eq(false));
        try {

            if (staffUserService.getLoggedInUserId() != 1) {
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCustomers.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCustomers.buId.in(staffUserService.getBUIdsFromCurrentStaff())).and(qCustomers.servicearea.id.in(staffUserService.getServiceAreaIdList())));
                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCustomers.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCustomers.servicearea.id.in(staffUserService.getServiceAreaIdList())));
                }
            }
            queryResult = query.from(qCustomer).groupBy(qCustomer.status).select(qCustomer.status, qCustomer.status.count()).where(booleanExpression).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    dataMap.put(result.get(qCustomer.status) + " Customers", Objects.requireNonNull(result.get(qCustomer.status.count())).toString());
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, String> getNewlyActivatedCustomer(Long mvnoId) {
        JPAQuery<Customers> query = new JPAQuery<>(entityManager);
        QCustomers qCustomer = QCustomers.customers;
        BooleanExpression booleanExpression = qCustomer.isNotNull().and(qCustomer.isDeleted.eq(false)).and(qCustomer.status.eq(Constants.NEW_ACTIVE));
        Map<String, String> dataMap = new HashMap<>();
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCustomer.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCustomer.buId.in(staffUserService.getBUIdsFromCurrentStaff())).and(qCustomer.servicearea.id.in(staffUserService.getServiceAreaIdList())));
                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCustomer.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCustomer.servicearea.id.in(staffUserService.getServiceAreaIdList())));
                }
            }
            List<Tuple> queryResult = query.from(qCustomer).groupBy(qCustomer.custtype).select(qCustomer.custtype, qCustomer.custtype.count()).where(booleanExpression).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    dataMap.put("CAF " + result.get(qCustomer.custtype) + " Customers", Objects.requireNonNull(result.get(qCustomer.custtype.count())).toString());
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, Integer> getPlanWiseCustomer(Long mvnoId) {
        JPAQuery<CustPlanMappping> query = new JPAQuery<>(entityManager);
        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        QCustomers qCustomers = QCustomers.customers;
        BooleanExpression booleanExpression = qCustomers.isNotNull().and(qCustomers.isDeleted.eq(false));
        TreeMap<String, Integer> dataMap = new TreeMap<>();
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCustomers.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCustomers.buId.in(staffUserService.getBUIdsFromCurrentStaff())).and(qCustomers.servicearea.id.in(staffUserService.getServiceAreaIdList())));
                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCustomers.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCustomers.servicearea.id.in(staffUserService.getServiceAreaIdList())));
                }
            }
            List<Tuple> queryResult = query.select(qPostpaidPlan.name, qCustPlanMappping.customer.id.count()).from(qCustPlanMappping, qCustomers, qPostpaidPlan).where(qCustomers.status.eq(Constants.ACTIVE_DB).and(qCustomers.mvnoId.in(mvnoId, 1)).and(qCustPlanMappping.customer.id.eq(qCustomers.id)).and(qPostpaidPlan.id.eq(qCustPlanMappping.planId)).and(booleanExpression)).groupBy(qCustPlanMappping.planId).orderBy(qCustPlanMappping.customer.id.count().desc()).limit(10).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    dataMap.put(result.get(qPostpaidPlan.name), Objects.requireNonNull(result.get(qCustPlanMappping.customer.id.count())).intValue());
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }

        return sortByValue(dataMap);
    }

    //Payment APIs
    @Override
    public Map<String, Double> getMonthWiseCollection(Long mvnoId, String year) {
        final SimpleDateFormat df = new SimpleDateFormat("MMMM");
        JPAQuery<CreditDocument> query = new JPAQuery<>(entityManager);
        QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
        BooleanExpression booleanExpression = qCreditDocument.isNotNull().and(qCreditDocument.isDelete.eq(false));
        Map<String, Double> dataMap = new TreeMap<>((Comparator) (o1, o2) -> {
            String s1 = (String) o1;
            String s2 = (String) o2;
            try {
                return df.parse(s1).compareTo(df.parse(s2));
            } catch (ParseException e) {
                throw new RuntimeException("Bad date format");
            }
        });
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCreditDocument.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCreditDocument.buID.in(staffUserService.getBUIdsFromCurrentStaff())).and(qCreditDocument.customer.servicearea.id.in(staffUserService.getServiceAreaIdList())));

                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCreditDocument.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCreditDocument.customer.servicearea.id.in(staffUserService.getServiceAreaIdList())));
                }
            }
            List<Tuple> queryResult = query.select(qCreditDocument.amount.sum(), qCreditDocument.paymentdate).from(qCreditDocument).where(qCreditDocument.status.eq(Constants.APPROVED).and(qCreditDocument.paymentdate.isNotNull()).and(qCreditDocument.paymentdate.year().eq(Integer.valueOf(year))).and(qCreditDocument.amount.isNotNull()).and(booleanExpression)).groupBy(qCreditDocument.paymentdate).orderBy(qCreditDocument.paymentdate.month().asc()).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    String month = Objects.requireNonNull(result.get(qCreditDocument.paymentdate).getMonth().name());
                    dataMap.put(month, result.get(qCreditDocument.amount.sum()));
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }

        return dataMap;
    }

    @Override
    public Map<String, Double> pendingApprovalPayments(Long mvnoId) {
        JPAQuery<CreditDocument> query = new JPAQuery<>(entityManager);
        QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
        BooleanExpression booleanExpression = qCreditDocument.isNotNull().and(qCreditDocument.isDelete.eq(false));
        Map<String, Double> dataMap = new HashMap<>();
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCreditDocument.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCreditDocument.buID.in(staffUserService.getBUIdsFromCurrentStaff())).and(qCreditDocument.customer.servicearea.id.in(staffUserService.getServiceAreaIdList())));

                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCreditDocument.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCreditDocument.customer.servicearea.id.in(staffUserService.getServiceAreaIdList())));

                }
            }
            List<Tuple> queryResult = query.select(qCreditDocument.amount.sum(), qCreditDocument.amount.count()).from(qCreditDocument).where(qCreditDocument.status.eq(Constants.PENDING).and(qCreditDocument.paymentdate.isNotNull()).and(qCreditDocument.amount.isNotNull()).and(booleanExpression)).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    if (Objects.isNull(result.get(qCreditDocument.amount.sum()))) {
                        dataMap.put(Constants.PENDING, (double) 0);
                    } else {
                        dataMap.put(Constants.PENDING, result.get(qCreditDocument.amount.sum()));
                    }
                    if (Objects.isNull(result.get(qCreditDocument.amount.count()))) {
                        dataMap.put("Total", (double) 0);
                    } else {
                        dataMap.put("Total", Objects.requireNonNull(result.get(qCreditDocument.amount.count())).doubleValue());
                    }

                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, Double> nextTenDaysReceivablePayment(Long mvnoId) throws ParseException {
        JPAQuery<CustPlanMappping> query = new JPAQuery<>(entityManager);
        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +10);
        Date toDate = sdf.parse(sdf.format(cal.getTime()));
        Map<String, Double> dataMap = new HashMap<>();
        BooleanExpression booleanExpression = qCustPlanMappping.isNotNull().and(qCustPlanMappping.isDelete.eq(false));
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qPostpaidPlan.buId.in(staffUserService.getMvnoIdFromCurrentStaff(null))).and(qCustPlanMappping.customer.servicearea.id.in(staffUserService.getServiceAreaIdList())));

                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCustPlanMappping.customer.servicearea.id.in(staffUserService.getServiceAreaIdList())));

                }
            }
            List<Double> queryResult = query.select(qPostpaidPlan.offerprice.sum()).from(qCustPlanMappping, qPostpaidPlan).where(qCustPlanMappping.customer.status.eq(Constants.ACTIVE_DB).and(qCustPlanMappping.expiryDate.between(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()), LocalDateTime.ofInstant(toDate.toInstant(), ZoneId.systemDefault()))).and(qPostpaidPlan.id.eq(qCustPlanMappping.planId)).and(booleanExpression)).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    dataMap.put("data", (double) result.longValue());
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public List<Customers> nextTenDaysRenewableCustomer(Long mvnoId) throws ParseException {
        JPAQuery<CustPlanMappping> query = new JPAQuery<>(entityManager);
        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +10);
        Date toDate = sdf.parse(sdf.format(cal.getTime()));
        List<Customers> customers = new ArrayList<>();
        QCustomers qCustomers = QCustomers.customers;
        BooleanExpression booleanExpression = qCustomers.isNotNull().and(qCustomers.isDeleted.eq(false));
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qCustomers.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCustomers.servicearea.id.in(staffUserService.getServiceAreaIdList())));
            }
            List<Customers> queryResult = query.select(qCustPlanMappping.customer).from(qCustPlanMappping).where(qCustPlanMappping.customer.status.eq(Constants.ACTIVE_DB).and(qCustPlanMappping.expiryDate.between(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()), LocalDateTime.ofInstant(toDate.toInstant(), ZoneId.systemDefault())))).groupBy(qCustPlanMappping.customer.id, qCustPlanMappping.planId).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(customer -> {
                    customers.add(new Customers(customer.getFirstname(), customer.getUsername(), customer.getMobile(), customer.getEmail(), customer.getAcctno(), customer.getCusttype(), customer.getCafno(), customer.getPartner().getName(), customer.getServicearea().getName()));
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return customers;

    }

//    Tickets APIs

    @Override
    public Map<String, Long> totalOpenTickets(Long mvnoId) {
        JPAQuery<Case> query = new JPAQuery<>(entityManager);
        QCase qCase = QCase.case$;
        Map<String, Long> dataMap = new HashMap<>();
        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false));
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCase.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCase.buId.in(staffUserService.getBUIdsFromCurrentStaff())).and(qCase.customers.servicearea.id.in(staffUserService.getServiceAreaIdList())));
                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCase.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCase.customers.servicearea.id.in(staffUserService.getServiceAreaIdList())));
                }
            }
            List<Long> queryResult = query.select(qCase.count()).from(qCase).where(qCase.caseStatus.notEqualsIgnoreCase(Constants.RESOLVED)).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    dataMap.put("data", result);
//                dataMap.put(Constants.PENDING,result.get(qCreditDocument.amount.sum()));
//                dataMap.put("Total", Objects.requireNonNull(result.get(qCreditDocument.amount.count())).doubleValue());

                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, Map<String, Long>> monthWiseTicketCount(Long mvnoId, String year) {
        JPAQuery<Case> query = new JPAQuery<>(entityManager);
        QCase qCase = QCase.case$;
        Map<String, Map<String, Long>> dataMap = new HashMap<>();
        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false));
        final SimpleDateFormat df = new SimpleDateFormat("MMMM");
        Map<String, Long> valueMap = new TreeMap<>((Comparator) (o1, o2) -> {
            String s1 = (String) o1;
            String s2 = (String) o2;
            try {
                return df.parse(s1).compareTo(df.parse(s2));
            } catch (ParseException e) {
                throw new RuntimeException("Bad date format");
            }
        });
        Map<String, Long> valueMapForResolved = new TreeMap<>((Comparator) (o1, o2) -> {
            String s1 = (String) o1;
            String s2 = (String) o2;
            try {
                return df.parse(s1).compareTo(df.parse(s2));
            } catch (ParseException e) {
                throw new RuntimeException("Bad date format");
            }
        });
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCase.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCase.buId.in(staffUserService.getBUIdsFromCurrentStaff())).and(qCase.customers.servicearea.id.in(staffUserService.getServiceAreaIdList())));

                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCase.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCase.customers.servicearea.id.in(staffUserService.getServiceAreaIdList())));
                }
            }
            List<Tuple> queryResultForTotal = query.select(qCase.count(), qCase.createdate).from(qCase).where(qCase.createdate.year().eq(Integer.valueOf(year)).and(booleanExpression)).groupBy(qCase.createdate).orderBy(qCase.createdate.month().asc()).fetch();
            List<Tuple> queryResultForResolved = query.select(qCase.caseStatus.count(), qCase.createdate).from(qCase).where(qCase.caseStatus.eq(Constants.RESOLVED).and(qCase.createdate.year().eq(Integer.valueOf(year))).and(booleanExpression)).groupBy(qCase.createdate).orderBy(qCase.createdate.month().asc()).fetch();
            if (!queryResultForTotal.isEmpty()) {
                queryResultForTotal.forEach(result -> {
                    String month = String.valueOf(Objects.requireNonNull(result.get(qCase.createdate)).getMonth());
                    if (valueMap.containsKey(month)) {
                        valueMap.put(month, valueMap.get(month) + Objects.requireNonNull(result.get(qCase.count())));
                    } else {
                        valueMap.put(month, Objects.requireNonNull(result.get(qCase.count())));
                    }
                });
                dataMap.put("Created", valueMap);
            }

            if (!queryResultForTotal.isEmpty()) {
                queryResultForResolved.forEach(result -> {
                    String month = String.valueOf(Objects.requireNonNull(result.get(qCase.createdate)).getMonth());
                    if (valueMapForResolved.containsKey(month)) {
                        valueMapForResolved.put(month, valueMapForResolved.get(month) + Objects.requireNonNull(result.get(qCase.caseStatus.count())));
                    } else {
                        valueMapForResolved.put(month, Objects.requireNonNull(result.get(qCase.caseStatus.count())));
                    }
                });
                dataMap.put("Resolved", valueMapForResolved);
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, Map<String, String>> staffWiseTicketCount(Long mvnoId) {
        JPAQuery<CaseAssignment> query = new JPAQuery<>(entityManager);
        JPAQuery<CaseAssignment> queryTotal = new JPAQuery<>(entityManager);
        QCase qCase = QCase.case$;
        QCaseAssignment qCaseAssignment = QCaseAssignment.caseAssignment;
        QStaffUser qStaffUser = QStaffUser.staffUser;
        Map<String, Map<String, String>> dataMap = new HashMap<>();
        Map<String, String> valueMap = new HashMap<>();
        Map<String, String> valueMapForResolved = new HashMap<>();
        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false));

        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCase.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCase.buId.in(staffUserService.getBUIdsFromCurrentStaff())).and(qCase.customers.servicearea.id.in(staffUserService.getServiceAreaIdList())));

                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCase.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCase.customers.servicearea.id.in(staffUserService.getServiceAreaIdList())));

                }
            }
            List<Tuple> queryResultForResolved = query.select(qCase.count(), qStaffUser.username).from(qCase, qCaseAssignment, qStaffUser).where(qCase.caseId.eq(qCaseAssignment.cases.caseId).and(qStaffUser.id.eq(qCaseAssignment.staffUser.id)).and(qCase.caseStatus.eq(Constants.RESOLVED)).and(booleanExpression)).groupBy(qStaffUser.username).fetch();
            List<Tuple> queryResultForAssigned = queryTotal.select(qCase.count(), qStaffUser.username).from(qCase, qCaseAssignment, qStaffUser).where(qCase.caseId.eq(qCaseAssignment.cases.caseId).and(qStaffUser.id.eq(qCaseAssignment.staffUser.id)).and(qCase.caseStatus.eq(Constants.ASSIGNED)).and(booleanExpression)).groupBy(qStaffUser.username).fetch();
            if (!queryResultForResolved.isEmpty()) {
                queryResultForResolved.forEach(result -> {
                    valueMapForResolved.put(result.get(qStaffUser.username), Objects.requireNonNull(result.get(qCase.count())).toString());
                });
                dataMap.put("Resolved", valueMapForResolved);

            }
            if (!queryResultForAssigned.isEmpty()) {
                queryResultForAssigned.forEach(result -> {
                    valueMap.put(result.get(qStaffUser.username), Objects.requireNonNull(result.get(qCase.count())).toString());
                });
                dataMap.put("Assigned", valueMap);

            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }

        return dataMap;
    }

    @Override
    public Map<String, Map<String, String>> teamWiseTicketCount(Long mvnoId) {
        JPAQuery<CaseAssignment> query = new JPAQuery<>(entityManager);
        JPAQuery<CaseAssignment> queryTotal = new JPAQuery<>(entityManager);
        QCase qCase = QCase.case$;
        QCaseAssignment qCaseAssignment = QCaseAssignment.caseAssignment;
        QStaffUser qStaffUser = QStaffUser.staffUser;
        QTeams qTeams = QTeams.teams;
        QTeamUserMapping qTeamUserMapping = QTeamUserMapping.teamUserMapping;
        Map<String, Map<String, String>> dataMap = new HashMap<>();
        Map<String, String> valueMap = new HashMap<>();
        Map<String, String> valueMapForResolved = new HashMap<>();
        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false));
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCase.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCase.buId.in(staffUserService.getBUIdsFromCurrentStaff())).and(qCase.customers.servicearea.id.in(staffUserService.getServiceAreaIdList())));

                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCase.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCase.customers.servicearea.id.in(staffUserService.getServiceAreaIdList())));

                }
            }
            List<Tuple> queryResultForResolved = query.select(qCase.count(), qTeams.name).from(qCase, qCaseAssignment, qStaffUser, qTeams, qTeamUserMapping).where(qCase.caseId.eq(qCaseAssignment.cases.caseId).and(qStaffUser.id.eq(qCaseAssignment.staffUser.id)).and(qCase.caseStatus.eq(Constants.RESOLVED)).and(qStaffUser.id.eq(qTeamUserMapping.staffId.intValue()).and(qTeams.id.eq(qTeamUserMapping.teamId)).and(booleanExpression))).groupBy(qTeams.name).fetch();
            List<Tuple> queryResultForAssigned = queryTotal.select(qCase.count(), qTeams.name).from(qCase, qCaseAssignment, qStaffUser, qTeams, qTeamUserMapping).where(qCase.caseId.eq(qCaseAssignment.cases.caseId).and(qStaffUser.id.eq(qCaseAssignment.staffUser.id)).and(qCase.caseStatus.eq(Constants.ASSIGNED)).and(qStaffUser.id.eq(qTeamUserMapping.staffId.intValue())).and(qTeams.id.eq(qTeamUserMapping.teamId)).and(booleanExpression)).groupBy(qTeams.name).fetch();
            if (!queryResultForResolved.isEmpty()) {
                queryResultForResolved.forEach(result -> {
                    valueMapForResolved.put(result.get(qTeams.name), Objects.requireNonNull(result.get(qCase.count())).toString());
                });
                dataMap.put("Resolved", valueMapForResolved);

            }
            if (!queryResultForAssigned.isEmpty()) {
                queryResultForAssigned.forEach(result -> {
                    valueMap.put(result.get(qTeams.name), Objects.requireNonNull(result.get(qCase.count())).toString());
                });
                dataMap.put("Assigned", valueMap);
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, Double> partnerWisePayment(Long mvnoId) throws ParseException {
        JPAQuery<PartnerPayment> query = new JPAQuery<>(entityManager);
        QPartnerPayment qPartnerPayment = QPartnerPayment.partnerPayment;
        QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
        Map<String, Double> dataMap = new HashMap<>();
        BooleanExpression booleanExpression = qPartnerPayment.isNotNull().and(qPartnerPayment.isDeleted.eq(false));
        JPAQuery<Partner> queryForPartner = new JPAQuery<>(entityManager);
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = staffUserService.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qPartnerPayment.partner.id.in(queryForPartner.select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))).and(qPartnerPayment.partner.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null))));
            }
            List<Tuple> queryResult = query.select(qPartnerPayment.partner.name, qPartnerPayment.amount.sum())
                    .from(qPartnerPayment)
                    .where(qPartnerPayment.partner.status.eq(Constants.ACTIVE_DB)
                            .and(booleanExpression))
                    .groupBy(qPartnerPayment.partner.id).orderBy(qPartnerPayment.amount.sum().desc()).limit(10).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    dataMap.put(result.get(qPartnerPayment.partner.name), Objects.requireNonNull(result.get(qPartnerPayment.amount.sum())));
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, Double> monthWiseVolumeUsages(Long mvnoId, Integer year) {
        if (year == null) year = LocalDate.now().getYear();
        JPAQuery<CustQuotaDetails> query = new JPAQuery<>(entityManager);
        QCustQuotaDetails qCustQuotaDetails = QCustQuotaDetails.custQuotaDetails;
        Map<String, Double> dataMap = new HashMap<>();
        QCustomers qCustomers = QCustomers.customers;
        BooleanExpression booleanExpression = qCustomers.isNotNull().and(qCustomers.isDeleted.eq(false)).and(qCustomers.status.notEqualsIgnoreCase(Constants.NEW_ACTIVE));
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qCustQuotaDetails.customer.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCustQuotaDetails.customer.servicearea.id.in(staffUserService.getServiceAreaIdList())));
            }
            List<Tuple> queryResultForTotal = query.select(qCustQuotaDetails.usedQuotaKB, qCustQuotaDetails.createdate)
                    .from(qCustQuotaDetails).where(qCustQuotaDetails.quotaType.in("Data", "Both")
                            .and(qCustQuotaDetails.createdate.year().eq(year)).and(booleanExpression))
                    .orderBy(qCustQuotaDetails.createdate.month().asc()).fetch();
            if (!queryResultForTotal.isEmpty()) {
                queryResultForTotal.forEach(result -> {
                    String month = String.valueOf(Objects.requireNonNull(result.get(qCustQuotaDetails.createdate)).getMonth());
                    if (dataMap.containsKey(month)) {
                        if (result.get(qCustQuotaDetails.usedQuotaKB) != null && result.get(qCustQuotaDetails.createdate).toLocalDate().isBefore(LocalDate.now()))
                            dataMap.put(month, dataMap.get(month) + (result.get(qCustQuotaDetails.usedQuotaKB) / 1024.0));
                    } else {
                        if (result.get(qCustQuotaDetails.usedQuotaKB) != null && result.get(qCustQuotaDetails.createdate).toLocalDate().isBefore(LocalDate.now()))
                            dataMap.put(month, result.get(qCustQuotaDetails.usedQuotaKB) / 1024.0);
                    }
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, Double> monthWiseTimeUsages(Long mvnoId, Integer year) {
        if (year == null) year = LocalDate.now().getYear();
        JPAQuery<CustQuotaDetails> query = new JPAQuery<>(entityManager);
        QCustQuotaDetails qCustQuotaDetails = QCustQuotaDetails.custQuotaDetails;
        Map<String, Double> dataMap = new HashMap<>();
        QCustomers qCustomers = QCustomers.customers;
        BooleanExpression booleanExpression = qCustomers.isNotNull().and(qCustomers.isDeleted.eq(false)).and(qCustomers.status.notEqualsIgnoreCase(Constants.NEW_ACTIVE));
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qCustQuotaDetails.customer.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCustQuotaDetails.customer.servicearea.id.in(staffUserService.getServiceAreaIdList())));
            }
            List<Tuple> queryResultForTotal = query.select(qCustQuotaDetails.timeUsedQuotaSec, qCustQuotaDetails.createdate)
                    .from(qCustQuotaDetails)
                    .where(qCustQuotaDetails.quotaType.in("Time", "Both")
                            .and(qCustQuotaDetails.createdate.year().eq(year)).and(booleanExpression))
                    .orderBy(qCustQuotaDetails.createdate.month().asc()).fetch();
            if (!queryResultForTotal.isEmpty()) {
                queryResultForTotal.forEach(result -> {
                    String month = String.valueOf(Objects.requireNonNull(result.get(qCustQuotaDetails.createdate)).getMonth());
                    if (dataMap.containsKey(month)) {
                        if (result.get(qCustQuotaDetails.timeUsedQuotaSec) != null && result.get(qCustQuotaDetails.createdate).toLocalDate().isBefore(LocalDate.now()))
                            dataMap.put(month, dataMap.get(month) + (result.get(qCustQuotaDetails.timeUsedQuotaSec) / 60.0));
                    } else {
                        if (result.get(qCustQuotaDetails.timeUsedQuotaSec) != null && result.get(qCustQuotaDetails.createdate).toLocalDate().isBefore(LocalDate.now()))
                            dataMap.put(month, result.get(qCustQuotaDetails.timeUsedQuotaSec) / 60.0);
                    }
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Long connectedUser(Long mvnoId) {
        JPAQuery<LiveUser> query = new JPAQuery<>(entityManager);
        QLiveUser qLiveUser = QLiveUser.liveUser;
        if (Objects.isNull(mvnoId)) {
            throw new IllegalArgumentException("MVNOId is mandatory. PLease enter valid MVNOId");
        }
        if (mvnoId == 0) {
            throw new IllegalArgumentException("MVNOId can not be 0. PLease enter valid MVNOId");
        }
        if (mvnoId != 1)
            return query.select(qLiveUser.mvnoId).from(qLiveUser).where(qLiveUser.mvnoId.in(mvnoId, 1)).fetchCount();
        else return query.select(qLiveUser.mvnoId).from(qLiveUser).fetchCount();
    }

    public static HashMap<String, Integer> sortByValue(TreeMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    @Override
    public List<Case> overDueTicketList(Long mvnoId) throws ParseException {
        JPAQuery<Case> query = new JPAQuery<>(entityManager);
        QCase qCase = QCase.case$;
        List<Case> cases = new ArrayList<>();
        BooleanExpression booleanExpression = qCase.isNotNull().and(qCase.isDelete.eq(false));

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, +10);
            Date toDate = sdf.parse(sdf.format(cal.getTime()));
            if (staffUserService.getLoggedInUserId() != 1) {
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCase.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCase.buId.in(staffUserService.getBUIdsFromCurrentStaff())).and(qCase.customers.servicearea.id.in(staffUserService.getServiceAreaIdList())));

                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qCase.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)).and(qCase.customers.servicearea.id.in(staffUserService.getServiceAreaIdList())));
                }
            }
            List<Case> queryResult = query.select(qCase).from(qCase).where(qCase.nextFollowupDate.before(LocalDate.from(LocalDateTime.ofInstant(toDate.toInstant(), ZoneId.systemDefault()))).and(qCase.caseStatus.notIn(Constants.RESOLVED, "Closed")).and(booleanExpression)).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(tuple -> {
                    cases.add(new Case(tuple.getCaseNumber(), tuple.getPriority(), tuple.getCaseStatus(), tuple.getCustomers().getCustname(), tuple.getCaseType(), tuple.getNextFollowupDate(), tuple.getCurrentAssignee().getFullName()));
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return cases;
    }

    @Override
    public Map<String, Double> monthWiseAGRPayable(Long mvnoId, String year) {
        final SimpleDateFormat df = new SimpleDateFormat("MMMM");
        JPAQuery<PartnerLedgerDetails> query = new JPAQuery<>(entityManager);
        QPartnerLedgerDetails qPartnerLedgerDetails = QPartnerLedgerDetails.partnerLedgerDetails;
        BooleanExpression booleanExpression = qPartnerLedgerDetails.isNotNull().and(qPartnerLedgerDetails.isDeleted.eq(false));
        QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
        JPAQuery<Partner> queryForPartner = new JPAQuery<>(entityManager);
        Map<String, Double> dataMap = new TreeMap<String, Double>((o1, o2) -> {
            try {
                return df.parse((String) o1).compareTo(df.parse((String) o2));
            } catch (ParseException e) {
                throw new RuntimeException("Bad date format");
            }
        });

        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = staffUserService.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPartnerLedgerDetails.partner.id.in(queryForPartner.select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))).and(qPartnerLedgerDetails.partner.buId.in(staffUserService.getBUIdsFromCurrentStaff())).and(qPartnerLedgerDetails.partner.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null))));

                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPartnerLedgerDetails.partner.id.in(queryForPartner.select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))).and(qPartnerLedgerDetails.partner.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null))));

                }
            }
            List<Tuple> queryResult = query.select(qPartnerLedgerDetails.agr_amount.sum(), qPartnerLedgerDetails.createDate)
                    .from(qPartnerLedgerDetails)
                    .where(qPartnerLedgerDetails.isNotNull()
                            .and(qPartnerLedgerDetails.createDate.year().eq(Integer.valueOf(year))).and(booleanExpression))
                    .groupBy(qPartnerLedgerDetails.createDate).orderBy(qPartnerLedgerDetails.createDate.month().asc()).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    String month = String.valueOf(Objects.requireNonNull(result.get(qPartnerLedgerDetails.createDate)).getMonth());
                    Double amount = result.get(qPartnerLedgerDetails.agr_amount.sum());
                    if (amount != null) {
                        if (dataMap.containsKey(month)) {
                            dataMap.put(month, amount + dataMap.get(month));
                        } else {
                            dataMap.put(month, amount);
                        }
                    }
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, Double> monthWiseTDSPayable(Long mvnoId, String year) {
        final SimpleDateFormat df = new SimpleDateFormat("MMMM");
        JPAQuery<PartnerLedgerDetails> query = new JPAQuery<>(entityManager);
        QPartnerLedgerDetails qPartnerLedgerDetails = QPartnerLedgerDetails.partnerLedgerDetails;
        BooleanExpression booleanExpression = qPartnerLedgerDetails.isNotNull().and(qPartnerLedgerDetails.isDeleted.eq(false));
        QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
        JPAQuery<Partner> queryForPartner = new JPAQuery<>(entityManager);
        Map<String, Double> dataMap = new TreeMap<String, Double>((o1, o2) -> {
            try {
                return df.parse((String) o1).compareTo(df.parse((String) o2));
            } catch (ParseException e) {
                throw new RuntimeException("Bad date format");
            }
        });
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = staffUserService.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPartnerLedgerDetails.partner.id.in(queryForPartner.select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))).and(qPartnerLedgerDetails.partner.buId.in(staffUserService.getMvnoIdFromCurrentStaff(null))).and(qPartnerLedgerDetails.partner.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null))));

                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPartnerLedgerDetails.partner.id.in(queryForPartner.select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))).and(qPartnerLedgerDetails.partner.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null))));
                }
            }
            List<Tuple> queryResult = query.select(qPartnerLedgerDetails.tds_amount.sum(), qPartnerLedgerDetails.createDate)
                    .from(qPartnerLedgerDetails).where(qPartnerLedgerDetails.isNotNull()
                            .and(qPartnerLedgerDetails.createDate.year().eq(Integer.valueOf(year))).and(booleanExpression)).groupBy(qPartnerLedgerDetails.createDate).orderBy(qPartnerLedgerDetails.createDate.month().asc()).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    String month = String.valueOf(Objects.requireNonNull(result.get(qPartnerLedgerDetails.createDate)).getMonth());
                    Double amount = result.get(qPartnerLedgerDetails.tds_amount.sum());
                    if (amount != null) {
                        if (dataMap.containsKey(month)) {
                            dataMap.put(month, amount + dataMap.get(month));
                        } else {
                            dataMap.put(month, amount);
                        }
                    }
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, Double> partnerWiseTDSDetails(Long mvnoId, String year) {
        JPAQuery<PartnerLedgerDetails> query = new JPAQuery<>(entityManager);
        QPartnerLedgerDetails qPartnerLedgerDetails = QPartnerLedgerDetails.partnerLedgerDetails;
        Map<String, Double> dataMap = new HashMap<>();
        BooleanExpression booleanExpression = qPartnerLedgerDetails.isNotNull().and(qPartnerLedgerDetails.isDeleted.eq(false));
        QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
        JPAQuery<Partner> queryForPartner = new JPAQuery<>(entityManager);
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = staffUserService.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    booleanExpression = booleanExpression.and(qPartnerLedgerDetails.partner.id.in(queryForPartner.select(qPartnerServiceAreaMapping.partnerId)
                                    .from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))).and(qPartnerLedgerDetails.partner.buId.in(staffUserService.getBUIdsFromCurrentStaff())).
                            and(qPartnerLedgerDetails.partner.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null))));    // TODO: pass mvnoID manually 6/5/2025
                } else {
                    booleanExpression = booleanExpression.and(qPartnerLedgerDetails.partner.id.in(queryForPartner.select(qPartnerServiceAreaMapping.partnerId)
                                    .from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))).
                            and(qPartnerLedgerDetails.partner.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null))));        // TODO: pass mvnoID manually 6/5/2025
                }
            }
            List<Tuple> queryResult = query.select(qPartnerLedgerDetails.tds_amount.sum(), qPartnerLedgerDetails.partner.name)
                    .from(qPartnerLedgerDetails).where(qPartnerLedgerDetails.isNotNull()
                            .and(qPartnerLedgerDetails.createDate.month().eq(Calendar.getInstance().get(Calendar.MONTH) + 1)).and(booleanExpression)).groupBy(qPartnerLedgerDetails.partner.name).orderBy(qPartnerLedgerDetails.tds_amount.sum().asc()).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    String month = Objects.requireNonNull(result.get(qPartnerLedgerDetails.partner.name));
                    Double amount = result.get(qPartnerLedgerDetails.tds_amount.sum());
                    if (amount != null) {
                        if (dataMap.containsKey(month)) {
                            dataMap.put(month, amount + dataMap.get(month));
                        } else {
                            dataMap.put(month, amount);
                        }
                    }
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, Map<String, Double>> monthWiseTotalDetails(Long mvnoId, String year) {
        JPAQuery<PartnerLedgerDetails> query = new JPAQuery<>(entityManager);
        final SimpleDateFormat df = new SimpleDateFormat("MMMM");
        QPartnerLedgerDetails qPartnerLedgerDetails = QPartnerLedgerDetails.partnerLedgerDetails;
        Map<String, Map<String, Double>> dataMap = new HashMap<>();
        BooleanExpression booleanExpression = qPartnerLedgerDetails.isNotNull().and(qPartnerLedgerDetails.isDeleted.eq(false));
        QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
        JPAQuery<Partner> queryForPartner = new JPAQuery<>(entityManager);
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = staffUserService.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    booleanExpression = booleanExpression.and(qPartnerLedgerDetails.partner.id.in(queryForPartner.select(qPartnerServiceAreaMapping.partnerId)
                                    .from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))).and(qPartnerLedgerDetails.partner.buId.in(staffUserService.getBUIdsFromCurrentStaff())).
                            and(qPartnerLedgerDetails.partner.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null))));    // TODO: pass mvnoID manually 6/5/2025
                } else {
                    booleanExpression = booleanExpression.and(qPartnerLedgerDetails.partner.id.in(queryForPartner.select(qPartnerServiceAreaMapping.partnerId)
                                    .from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))).
                            and(qPartnerLedgerDetails.partner.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null))));    // TODO: pass mvnoID manually 6/5/2025
                }

            }
            List<Tuple> queryResult = query.select(qPartnerLedgerDetails.tds_amount.sum(), qPartnerLedgerDetails.agr_amount.sum(), qPartnerLedgerDetails.commission.sum(),
                            qPartnerLedgerDetails.createDate.month())
                    .from(qPartnerLedgerDetails)
                    .where(qPartnerLedgerDetails.isNotNull()
                            .and(qPartnerLedgerDetails.createDate.year().eq(Integer.valueOf(year))).and(booleanExpression)).groupBy(qPartnerLedgerDetails.createDate.month()).orderBy(qPartnerLedgerDetails.createDate.month().asc()).fetch();
            if (!queryResult.isEmpty()) {
                Map<String, Double> tdsMap = new HashMap<>();
                Map<String, Double> agrMap = new HashMap<>();
                Map<String, Double> commissionMap = new HashMap<>();
                queryResult.forEach(result -> {
                    Month month = Month.of(Objects.requireNonNull(result.get(qPartnerLedgerDetails.createDate.month())));
                    tdsMap.put(month.name(), result.get(qPartnerLedgerDetails.tds_amount.sum()));
                    agrMap.put(month.name(), result.get(qPartnerLedgerDetails.agr_amount.sum()));
                    commissionMap.put(month.name(), result.get(qPartnerLedgerDetails.commission.sum()));
                });
                dataMap.put("TDS", tdsMap);
                dataMap.put("AGR", agrMap);
                dataMap.put("COMMISSION", commissionMap);

            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public Map<String, Double> topFivePartnerCommissionWise(Long mvnoId, String year) {
        JPAQuery<PartnerLedgerDetails> query = new JPAQuery<>(entityManager);
        QPartnerLedgerDetails qPartnerLedgerDetails = QPartnerLedgerDetails.partnerLedgerDetails;
        Map<String, Double> dataMap = new HashMap<>();
        BooleanExpression booleanExpression = qPartnerLedgerDetails.isNotNull().and(qPartnerLedgerDetails.isDeleted.eq(false));
        QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
        JPAQuery<Partner> queryForPartner = new JPAQuery<>(entityManager);
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = staffUserService.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                    booleanExpression = booleanExpression.and(qPartnerLedgerDetails.partner.id.in(queryForPartner.select(qPartnerServiceAreaMapping.partnerId)
                                    .from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))).and(qPartnerLedgerDetails.partner.buId.in(staffUserService.getBUIdsFromCurrentStaff())).
                            and(qPartnerLedgerDetails.partner.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)))); // TODO: pass mvnoID manually 6/5/2025
                } else {
                    booleanExpression = booleanExpression.and(qPartnerLedgerDetails.partner.id.in(queryForPartner.select(qPartnerServiceAreaMapping.partnerId)
                                    .from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))).
                            and(qPartnerLedgerDetails.partner.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null))));   // TODO: pass mvnoID manually 6/5/2025
                }
            }
            List<Tuple> queryResult = query.select(qPartnerLedgerDetails.commission.sum(), qPartnerLedgerDetails.partner.name)
                    .from(qPartnerLedgerDetails)
                    .where(qPartnerLedgerDetails.isNotNull().and(booleanExpression))
                    .groupBy(qPartnerLedgerDetails.partner.id).orderBy(qPartnerLedgerDetails.commission.sum().desc()).limit(5).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    String partnerName = result.get(qPartnerLedgerDetails.partner.name);
                    Double amount = result.get(qPartnerLedgerDetails.commission.sum());
                    dataMap.put(partnerName, amount);
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataMap;
    }

    @Override
    public List<Map<String, String>> availableInventoryProductWise(Long mvnoId) {
        JPAQuery<Inward> query = new JPAQuery<>(entityManager);
        QInward qInward = QInward.inward;
        QWareHouse qWareHouse = QWareHouse.wareHouse;
        QWareHouseServiceAreaMapping qWareHouseServiceAreaMapping = QWareHouseServiceAreaMapping.wareHouseServiceAreaMapping;
        List<Map<String, String>> dataList = new ArrayList<>();
        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.isDeleted.eq(false));
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = staffUserService.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());

                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qInward.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)))
                        .and(qInward.destinationType.equalsIgnoreCase(CommonConstants.WAREHOUSE))
                        .and(qInward.destinationId.in(query.select(qWareHouseServiceAreaMapping.warehouseId)
                                .from(qWareHouseServiceAreaMapping)
                                .where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))));

            }
            List<Tuple> queryResult = query.select(qInward.productId.name, qInward.unusedQty.sum(), qInward.qty.sum(), qInward.usedQty.sum())
                    .from(qInward).where(qInward.isNotNull().and(booleanExpression)).groupBy(qInward.productId.id).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put("productName", result.get(qInward.productId.name));
                    dataMap.put("availableQty", result.get(qInward.unusedQty.sum()).toString());
                    dataMap.put("totalQty", result.get(qInward.qty.sum()).toString());
                    dataMap.put("usedQty", result.get(qInward.usedQty.sum()).toString());
                    dataMap.put("unit", result.get(qInward.productId.productCategory.unit));
                    dataList.add(dataMap);
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataList;
    }

    @Override
    public List<Map<String, String>> inventoryAlert(Long mvnoId) {
        JPAQuery<Inward> queryInward = new JPAQuery<>(entityManager);
        QInward qInward = QInward.inward;
        QWareHouse qWareHouse = QWareHouse.wareHouse;
        QWareHouseServiceAreaMapping qWareHouseServiceAreaMapping = QWareHouseServiceAreaMapping.wareHouseServiceAreaMapping;
        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.isDeleted.eq(false));
        List<Map<String, String>> dataList = new ArrayList<>();
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = staffUserService.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
// TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qInward.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)))
                        .and(qInward.destinationType.equalsIgnoreCase(CommonConstants.WAREHOUSE))
                        .and(qInward.destinationId.in(queryInward.select(qWareHouseServiceAreaMapping.warehouseId)
                                .from(qWareHouseServiceAreaMapping)
                                .where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))));
            }
            List<Tuple> queryResult = queryInward.select(qInward.productId.name, qInward.unusedQty.sum(), qInward.productId.productCategory.unit)
                    .from(qInward).where(qInward.isNotNull().and(booleanExpression)).groupBy(qInward.productId.id).having(qInward.unusedQty.sum().lt(10)).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put("productName", result.get(qInward.productId.name));
                    dataMap.put("availableQty", result.get(qInward.unusedQty.sum()).toString());
                    dataMap.put("unit", result.get(qInward.productId.productCategory.unit));
                    dataList.add(dataMap);
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataList;
    }

    @Override
    public List<Map<String, String>> staffAndProductWiseInventories(Long mvnoId) {
        JPAQuery<Outward> query = new JPAQuery<>(entityManager);
        QOutward qOutward = QOutward.outward;
        QStaffUser qStaffUser = QStaffUser.staffUser;
        List<Map<String, String>> dataList = new ArrayList<>();
        QWareHouse qWareHouse = QWareHouse.wareHouse;
        QWareHouseServiceAreaMapping qWareHouseServiceAreaMapping = QWareHouseServiceAreaMapping.wareHouseServiceAreaMapping;
        BooleanExpression booleanExpression = qOutward.isNotNull().and(qOutward.isDeleted.eq(false));
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = staffUserService.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qOutward.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)))
                        .and(qOutward.sourceType.equalsIgnoreCase(CommonConstants.WAREHOUSE))
                        .and(qOutward.sourceId.in(query.select(qWareHouseServiceAreaMapping.warehouseId)
                                .from(qWareHouseServiceAreaMapping)
                                .where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))));
            }
            List<Tuple> queryResult = query.select(qOutward.productId.name, qOutward.unusedQty.sum(), qOutward.usedQty.sum(), qStaffUser.firstname, qOutward.qty.sum(), qOutward.productId.productCategory.unit).from(qOutward, qStaffUser)
                    .where(qOutward.isNotNull()
//                            .and(qStaffUser.id.eq(qOutward.staffId.intValue())
                            .and(booleanExpression)).groupBy(qOutward.productId.id, qStaffUser.firstname).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put("productName", result.get(qOutward.productId.name));
                    dataMap.put("unit", result.get(qOutward.productId.productCategory.unit));
                    dataMap.put("availableQty", result.get(qOutward.unusedQty.sum()).toString());
                    dataMap.put("staffName", result.get(qStaffUser.firstname));
                    dataMap.put("usedQty", result.get(qOutward.usedQty.sum()).toString());
                    dataMap.put("totalQty", result.get(qOutward.qty.sum()).toString());
                    dataList.add(dataMap);
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataList;
    }

    @Override
    public List<Map<String, String>> wareHouseAndProductWiseInventories(Long mvnoId) {
        JPAQuery<Inward> query = new JPAQuery<>(entityManager);
        QInward qInward = QInward.inward;
        List<Map<String, String>> dataList = new ArrayList<>();
        QWareHouse qWareHouse = QWareHouse.wareHouse;
        QWareHouseServiceAreaMapping qWareHouseServiceAreaMapping = QWareHouseServiceAreaMapping.wareHouseServiceAreaMapping;
        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.isDeleted.eq(false));
        try {
            if (staffUserService.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = staffUserService.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qInward.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null)))
                        .and(qInward.destinationType.equalsIgnoreCase(CommonConstants.WAREHOUSE))
                        .and(qInward.destinationId.in(query.select(qWareHouseServiceAreaMapping.warehouseId)
                                .from(qWareHouseServiceAreaMapping)
                                .where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))));
            }
            List<Tuple> queryResult = query.select(qInward.productId.name, qInward.usedQty.sum(), qInward.unusedQty.sum(), qInward.qty.sum(), qInward.productId.productCategory.unit).from(qInward).where(qInward.isNotNull().and(booleanExpression)).groupBy(qInward.productId.id).fetch();
//            List<Tuple> queryResult = query.select(qInward.productId.name, qInward.wareHouseId.name, qInward.usedQty.sum(), qInward.unusedQty.sum(), qInward.qty.sum(), qInward.productId.productCategory.unit).from(qInward).where(qInward.isNotNull().and(booleanExpression)).groupBy(qInward.productId.id, qInward.wareHouseId).fetch();
            if (!queryResult.isEmpty()) {
                queryResult.forEach(result -> {
                    Map<String, String> dataMap = new HashMap<>();
                    dataMap.put("productName", result.get(qInward.productId.name));
                    dataMap.put("unit", result.get(qInward.productId.productCategory.unit));
                    dataMap.put("availableQty", result.get(qInward.unusedQty.sum()).toString());
                    dataMap.put("usedQty", result.get(qInward.usedQty.sum()).toString());
                    dataMap.put("totalQty", result.get(qInward.qty.sum()).toString());
//                    dataMap.put("wareHouseName", result.get(qInward.wareHouseId.name));
                    dataList.add(dataMap);
                });
            }
        } catch (Exception e) {
            throw new CustomValidationException(APIConstants.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
        return dataList;
    }

    @Override
    public GenericDataDTO getLeadApprovals(PaginationRequestDTO paginationRequestDTO) {
        PageRequest pageRequest = staffUserService.generatePageRequest(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), "paymentdate", CommonConstants.SORT_ORDER_DESC);
        QLeadMaster leadMaster = QLeadMaster.leadMaster;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        BooleanExpression booleanExpression = leadMaster.isNotNull().and(leadMaster.isDeleted.eq(false)).and(leadMaster.nextApproveStaffId.eq(staffUserService.getLoggedInUserId()));
        // TODO: pass mvnoID manually 6/5/2025
        if (staffUserService.getMvnoIdFromCurrentStaff(null) != 1) {
            booleanExpression = booleanExpression.and(leadMaster.mvnoId.in(1, staffUserService.getMvnoIdFromCurrentStaff(null)));  // TODO: pass mvnoID manually 6/5/2025
        }
        if (staffUserService.getBUIdsFromCurrentStaff() != null && staffUserService.getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(leadMaster.mvnoId.eq(1L).or(leadMaster.mvnoId.eq(staffUserService.getMvnoIdFromCurrentStaff(null).longValue()).and(leadMaster.buId.in(staffUserService.getBUIdsFromCurrentStaff()))));
        }

        Page<LeadMaster> paginationList = leadMasterRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> {
            try {

                return data;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO getCustomerDocApprovals(PaginationRequestDTO paginationRequestDTO) {
        return null;
    }



    public GenericDataDTO getProductQtyByStaff(PaginationRequestDTO paginationRequestDTO, Long mvnoId) {
        PageRequest pageRequest = staffUserService.generatePageRequest(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        QProduct qProduct = QProduct.product;
        Long ownerId = (long) staffUserService.getLoggedInUserId();
        List<Long> productIds = IterableUtils.toList(productRepository.findAll(qProduct.isDeleted.eq(false).and(qProduct.mvnoId.eq(Math.toIntExact(mvnoId))))).stream().map(Product::getId).collect(Collectors.toList());
        String ownerType = CommonConstants.STAFF;
        QProductOwner qProductOwner = QProductOwner.productOwner;
        BooleanExpression booleanExpression = qProductOwner.productId.in(productIds).and(qProductOwner.ownerId.eq(ownerId)).and(qProductOwner.ownerType.eq(ownerType));
        Page<ProductOwner> paginationList = productOwnerRepository.findAll(booleanExpression, pageRequest);
        paginationList.stream().forEach(r->{
            Product product = productRepository.findById(r.getProductId()).get();
            r.setProductName(product.getName());
        });
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> {
            try {

                return data;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    public GenericDataDTO getProductQtyByWarehouse(PaginationRequestDTO paginationRequestDTO, Long mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest = staffUserService.generatePageRequest(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);
        Long staffId = (long) staffUserService.getLoggedInUserId();
        QProduct qProduct = QProduct.product;
        QWareHouse qWareHouse =QWareHouse.wareHouse;
        QWareHouseServiceAreaMapping qWareHouseServiceAreaMapping = QWareHouseServiceAreaMapping.wareHouseServiceAreaMapping;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        BooleanExpression booleanExpressionWarehouse = qWareHouse.isNotNull().and(qWareHouse.status.eq(CommonConstants.ACTIVE_STATUS)).and(qWareHouse.isDeleted.eq(false)).and(qWareHouse.mvnoId.eq(Math.toIntExact(mvnoId)));
        List<Integer> serviceIDs = serviceAreaService.getServiceAreaByStaffId();
        booleanExpressionWarehouse = booleanExpressionWarehouse.and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId).from(qWareHouseServiceAreaMapping).where(qWareHouseServiceAreaMapping.serviceId.in(serviceIDs))));
        List<WareHouse> wareHouses = IterableUtils.toList(warehouseManagementRepository.findAll(booleanExpressionWarehouse));
        QProductOwner qProductOwner = QProductOwner.productOwner;
        List<Long> productIds = IterableUtils.toList(productRepository.findAll(qProduct.isDeleted.eq(false).and(qProduct.mvnoId.eq(Math.toIntExact(mvnoId))))).stream().map(Product::getId).collect(Collectors.toList());
        List<Long> warehouseIds = new ArrayList<>();
        wareHouses.stream().forEach(wareHouse -> {
            WareHouse wareHouse1 = warehouseManagementRepository.findById(wareHouse.getId()).get();
            warehouseIds.add(wareHouse1.getId());
        });
        //List<Long> warehouseIds = IterableUtils.toList(warehouseManagementRepository.findAll(qWareHouse.isDeleted.eq(false).and(qWareHouse.mvnoId.eq(Math.toIntExact(mvnoId))).and(qWareHouse.createdById.eq(Math.toIntExact(staffId))))).stream().map(WareHouse::getId).collect(Collectors.toList());
        BooleanExpression booleanExpression = qProductOwner.ownerId.in(warehouseIds).and(qProductOwner.ownerType.eq(CommonConstants.WAREHOUSE)).and(qProductOwner.productId.in(productIds));
        Page<ProductOwner> paginationList = productOwnerRepository.findAll(booleanExpression, pageRequest);
        paginationList.stream().forEach(r->{
            WareHouse wareHouse=warehouseManagementRepository.findById(r.getOwnerId()).get();
            Product product = productRepository.findById(r.getProductId()).get();
            r.setWareHouseName(wareHouse.getName());
            r.setProductName(product.getName());
        });
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> {
            try {

                return data;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }
}
