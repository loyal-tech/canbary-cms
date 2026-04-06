package com.adopt.apigw.utils;

import com.adopt.apigw.MicroSeviceDataShare.SharedDataConstants.SharedDataConstants;
import com.adopt.apigw.constants.CaseConstants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.postpaid.StaffUserMapper;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.lead.*;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.Area.service.AreaService;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.repository.BranchRepository;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.adopt.apigw.modules.BusinessVerticals.Respository.BusinessVerticalsRepository;
import com.adopt.apigw.modules.BusinessVerticals.domain.BusinessVerticals;
import com.adopt.apigw.modules.CaseCustomerDetails.model.CaseCustomerDetails;
import com.adopt.apigw.modules.CaseCustomerDetails.repository.CaseCustomerDetailsRepository;
import com.adopt.apigw.modules.CommonList.domain.CommonList;
import com.adopt.apigw.modules.CommonList.model.CommonListDTO;
import com.adopt.apigw.modules.CommonList.repository.CommonListRepository;
import com.adopt.apigw.modules.CommonList.service.CommonListService;
import com.adopt.apigw.modules.CustomerDBR.repository.CustomerDBRRepository;
import com.adopt.apigw.modules.CustomerDBR.service.CustomerDBRService;
import com.adopt.apigw.modules.CustomerMacMgmt.Service.CustMacMgmtService;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.QCustomerInventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.QInOutWardMACMapping;
import com.adopt.apigw.modules.PartnerLedger.domain.QPartnerPayment;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerPaymentDTO;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerLedgerDetailsRepository;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerPaymentRepository;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.Pincode.service.PincodeService;
import com.adopt.apigw.modules.Region.domain.Region;
import com.adopt.apigw.modules.Region.repository.RegionBranchRepository;
import com.adopt.apigw.modules.Region.repository.RegionRepository;
import com.adopt.apigw.modules.RvenueClient.RevenueClient;
import com.adopt.apigw.modules.ServiceArea.SubscriberMapper;
import com.adopt.apigw.modules.ServiceArea.domain.QServiceArea;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.Teams.domain.*;
import com.adopt.apigw.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.adopt.apigw.modules.Teams.repository.TeamUserMappingsRepocitory;
import com.adopt.apigw.modules.Teams.repository.TeamsRepository;
import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.modules.customerDocDetails.domain.QCustomerDocDetails;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.customerDocDetails.repository.CustomerDocDetailsRepository;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.modules.subscriber.service.ChargeThread;
import com.adopt.apigw.modules.subscriber.service.InvoiceCreationThread;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.modules.tickets.domain.Case;
import com.adopt.apigw.modules.tickets.model.CaseDTO;
import com.adopt.apigw.modules.tickets.repository.CaseRepository;
import com.adopt.apigw.modules.tickets.repository.TicketReasonCategoryRepo;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.*;
import com.adopt.apigw.repository.LeadCustPlanMapppingRepository;
import com.adopt.apigw.repository.LeadCustomerAddressRepository;
import com.adopt.apigw.repository.LeadMasterRepository;
import com.adopt.apigw.repository.LeadQuotationDetailsRepository;
import com.adopt.apigw.repository.common.BranchServiceAreaMappingRepository;
import com.adopt.apigw.repository.common.CustomerApproveRepo;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.common.StaffUserServiceRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.schedulers.ApiGatewayScheduler;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.EzBillServiceUtility;
import com.adopt.apigw.service.common.WorkflowAuditService;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.spring.LoggedInUser;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import feign.FeignException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.jdo.annotations.Persistent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;



@Service
public class
WorkFlowQueryUtils {


    @Autowired
    PostPaidPlanServiceAreaMappingRepo postPaidPlanServiceAreaMappingRepo;
    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;


    @Autowired
    ChargeRepository chargeRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    StaffUserMapper staffUserMapper;

    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    RegionBranchRepository regionBranchRepository;

    @Autowired
    SubscriberMapper subscriberMapper;
//    @Autowired
//    BusinessVerticalMappingRepository businessVerticalMappingRepository;

    @Autowired
    TeamHierarchyMappingRepo teamHierarchyMappingRepo;
    @Autowired
    TeamsRepository teamsRepository;
    @Autowired
    PlanServiceRepository planServiceRepository;

    @Autowired
    PlanGroupService planGroupService;

    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    CustomerDocDetailsService customerDocDetailsService;

    @Autowired
    AreaService areaService;
    @Autowired
    PincodeService pincodeService;
    @Autowired
    CityService cityService;
    @Autowired
    StateService stateService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    PostpaidPlanService postpaidPlanService;
    @Autowired
    private DbrService dbrService;

    @Autowired
    ServiceAreaService serviceAreaService;

    @Autowired
    CustomerAddressService customerAddressService;

//    @Autowired
//    CaseService caseService;

    @Autowired
    ApiGatewayScheduler apiGatewayScheduler;

    @Autowired
    ChargeService chargeService;
    @Autowired
    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    CustomerDBRRepository customerDBRRepository;

    @Autowired
    CustMacMapppingService custMacMapppingService;

    @Autowired
    private CustMacMapppingRepository custMacMapppingRepository;

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private CustomerDBRService customerDBRService;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    @Autowired
    CustomerAddressRepository customerAddressRepository;

    @Autowired
    PartnerPaymentRepository partnerPaymentRepository;

    @Autowired
    CustomerDocDetailsRepository customerDocDetailsRepository;

    @Autowired
    LeadMasterRepository leadMasterRepository;
    @Autowired
    LeadCustomerAddressRepository leadCustomerAddressRepository;

    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    WorkflowAuditService workflowAuditService;
    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private StaffUserServiceRepository staffUserServiceRepository;

    @Autowired
    CustomerApproveRepo customerApproveRepo;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private BusinessVerticalsRepository businessVerticalsRepository;
    @Autowired
    private PlanGroupRepository planGroupRepository;
    @Autowired
    private BusinessUnitRepository businessUnitRepository;
    @Autowired
    private TicketReasonCategoryRepo ticketReasonCategoryRepo;

    @Autowired
    private CommonListRepository commonListRepository;

    @Autowired
    private PartnerLedgerDetailsRepository partnerLedgerDetailsRepository;

    @Autowired
    SubscriberService subscriberService;

    @Autowired
    CommonListService commonListService;
    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    TrialDebitDocRepository trialDebitDocRepository;
    @Autowired
    private CustPlanMappingRepository custPlanMapppingRepository;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;
    @Autowired
    InOutWardMacRepo inOutWardMacRepo;
    @Autowired
    CustSpecialPlanRelMapppingRepository custSpecialPlanRelMapppingRepository;
    @Autowired
    LeadQuotationDetailsRepository leadQuotationDetailsRepository;
    //    @Autowired
//    CustomerMapper customerMapper;
//    @Autowired
//    TrialDebitDocRepository trialDebitDocRepository;
//    @Autowired
//    PartnerRepository partnerrepo;
//
//    @Autowired
//    PartnerLedgerService partnerLedgerService;
//
//    @Autowired
//    PartnerLedgerDetailsService partnerLedgerDetailsService;
//
//    @Autowired
//    private PartnerPaymentMapper partnerPaymentMapper;
    @Autowired
    CustPlanMappingService custPlanMappingService;
    @Autowired
    EzBillServiceUtility ezBillServiceUtility;

    @Autowired
    TeamUserMappingsRepocitory teamUserMappingsRepocitory;
    @Autowired
    CaseRepository caseRepository;

    @Autowired
    LeadCustPlanMapppingRepository leadCustPlanMapppingRepository;
    @Autowired
    ShiftLocationRepository shiftLocationRepository;

   @Autowired
    CaseCustomerDetailsRepository caseCustomerDetailsRepository;

   @Autowired
   StateRepository stateRepository;
   @Autowired
   PlanGroupMappingRepository planGroupMappingRepository;
    @Autowired
    CustMacMgmtService custMacMgmtService;
    @Autowired
    RevenueClient revenueClient;

    @Autowired
    DepartmentRepository departmentRepository;
    @PersistenceContext
    private EntityManager entityManager;


    public Boolean checkCondition(List<QueryFieldMapping> queryFieldMappingList, String eventName, Object entity) {
        StringBuilder queryInit = new StringBuilder();
        boolean condition = false;
        DecimalFormat df = new DecimalFormat("0.00");
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        switch (eventName) {
            case CommonConstants.WORKFLOW_EVENT_NAME.CAF: {
                if (entity instanceof CustomersPojo) {
                    CustomersPojo customers = (CustomersPojo) entity;
                    CustomerAddress customerAddress = customerAddressService.findByAddressTypeAndCustomerId("Present", customers.getId());
                    for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                        switch (queryFieldMapping.getQueryField()) {
                            case CommonConstants.CAF_CONDITION.DISCOUNT: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    if (custPlanMapppingPojo.getDiscount() != null) {
                                        Double discount = Double.valueOf(df.format(custPlanMapppingPojo.getDiscount()));
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = discount == Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case ">":
                                                condition = discount > Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "<":
                                                condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "<=":
                                                condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case ">=":
                                                condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = discount != Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.OFFER_PRICE: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = custPlanMapppingPojo.getOfferPrice() == Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case ">":
                                            condition = custPlanMapppingPojo.getOfferPrice() > Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case "<":
                                            condition = custPlanMapppingPojo.getOfferPrice() < Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case "<=":
                                            condition = custPlanMapppingPojo.getOfferPrice() <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case ">=":
                                            condition = custPlanMapppingPojo.getOfferPrice() >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = custPlanMapppingPojo.getOfferPrice() != Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
//                            case CommonConstants.CAF_CONDITION.PLAN_PURCHASE_TYPE: {
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = customers.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !customers.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//                                break;
//                            }
                            case CommonConstants.CAF_CONDITION.PLAN_MODE: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                    if (postpaidPlan != null)
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.SERVICE_AREA: {
                                ServiceArea serviceArea = serviceAreaService.getByID(customers.getServiceareaid());
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.CALENDAR_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = customers.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !customers.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.TRIAL: {
                            	System.out.println("Trial Condition:"+customers.getIstrialplan()+":Condition:"+queryFieldMapping.getQueryValue());
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = customers.getIstrialplan().equals(Boolean.parseBoolean(queryFieldMapping.getQueryValue()));
                                        break;
                                    case "!=":
                                        condition = !customers.getIstrialplan().equals(Boolean.parseBoolean(queryFieldMapping.getQueryValue()));
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.CUSTOMER_CATEGORY: {
                                if (customers.getDunningCategory() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = customers.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !customers.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.PARTNER_NAME: {
                                Partner partner = new Partner();
                                if(customers.getPartnerid()!=null){
                                    partner = partnerRepository.findById(customers.getPartnerid()).orElse(null);
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = partner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !partner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                    break;
                                }

                            }
                            case CommonConstants.CAF_CONDITION.PARTNER_EMAIL: {
                                Partner partner = new Partner();
                                if(customers.getPartnerid()!=null){
                                    partner = partnerRepository.findById(customers.getPartnerid()).orElse(null);
                                    if (partner != null && partner.getEmail() != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = partner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !partner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                    break;
                                }

                            }
                            case CommonConstants.CAF_CONDITION.AREA: {
                                Area area = customerAddress.getArea();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.PINCODE: {
                                Pincode pincode = customerAddress.getPincode();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.CITY: {
                                City city = customerAddress.getCity();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.STATE: {
                                State state = customerAddress.getState();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.BILL_TO: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.INVOICE_TO_ORG: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.PARENT_CUSTOMER_USERNAME: {
                                if (customers.getParentCustomers() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = customers.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !customers.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.USERNAME: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = customers.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !customers.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;


                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.PLAN_SERVICE: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                }
                                break;
                            }
//                            case CommonConstants.CAF_CONDITION.CURRENT_TEAM_ASSIGNED:
//                            case CommonConstants.CAF_CONDITION.TEAM_ASSIGNED_NEW: {
////                                if (customers.getNextTeamHierarchyMapping() != null) {
////                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(customers.getNextTeamHierarchyMapping()).orElse(null);
////                                    if (teamHierarchyMapping != null) {
////                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
////                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
////                                            case "==":
////                                                condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
////                                                break;
////                                            case "!=":
////                                                condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
////                                                break;
////
////
////                                        }
////                                    }
////                                }
////                                break;
////                            }
//                                if (customers.getNextTeamHierarchyMapping() != null) {
//                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(customers.getNextTeamHierarchyMapping()).orElse(null);
//                                    if (teamHierarchyMapping != null) {
//                                        Teams teams = getNextTeamHirMappingByOrderNumberAndWorkflowId(teamHierarchyMapping.getOrderNumber() + 1, teamHierarchyMapping.getHierarchyId());
////                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
//                                        if (teams != null) {
//                                            switch (queryFieldMapping.getQueryOperator()) {
//                                                case "==": {
//                                                    condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                    break;
//                                                }
//                                                case "!=": {
//                                                    condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
                            case CommonConstants.CAF_CONDITION.LEAD_SOURCE: {
                                if (customers.getLeadSource() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = customers.getLeadSource().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                        case "!=":
                                            condition = !customers.getLeadSource().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }

                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.FEASIBILITY_REQUIRED: {
                                if (customers.getFeasibilityRequired() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = customers.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                        case "!=":
                                            condition = !customers.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }

                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.BRANCH: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customers.getServiceareaid());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && branch != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == branch.getId().longValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == branch.getId().longValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.REGION: {
//                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customers.getServiceareaid());
//                                if (queryFieldMapping.getQueryOperator() != null) {
//                                    Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
//                                    if (branchServiceAreaMappings.size() > 0 && region != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customers.getServiceareaid());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && region != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                //condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId().longValue()));
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == region.getId().longValue());

                                                break;
                                            }
                                            case "!=": {
                                                // condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId().longValue()));
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == region.getId().longValue());

                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.BUSINESS_VERTICAL: {
//                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customers.getServiceareaid());
//                                if (queryFieldMapping.getQueryOperator() != null) {
//                                    BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
//                                    if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                for (Region region : businessVerticals.getBuregionidList()) {
//                                                    condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                                    break;
//                                                }
//                                            }
//                                            case "!=": {
//                                                for (Region region : businessVerticals.getBuregionidList()) {
//                                                    condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customers.getServiceareaid());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
//                                                for (BusinessVerticals businessVerticals1 : businessVerticals.getbu) {
                                                //condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId().longValue()));
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == businessVerticals.getId().longValue());

                                                break;
                                                // }
                                            }
                                            case "!=": {
                                                //  for (Region region : businessVerticals.getBuregionidList()) {
                                                //condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId().longValue()));
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == businessVerticals.getId().longValue());

                                                break;
                                                // }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
//                            case CommonConstants.CAF_CONDITION.PLAN_GROUP: {
////                                if (customers.getPlangroupid() != null) {
////                                    PlanGroup planGroup = planGroupRepository.findById(customers.getPlangroupid()).orElse(null);
////                                    if (planGroup != null) {
////                                        switch (queryFieldMapping.getQueryOperator()) {
////                                            case "==":
////                                                condition = planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
////                                                break;
////                                            case "!=":
////                                                condition = !planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
////                                                break;
////                                        }
////                                    }
////                                }
////                                break;
//                                for (CustPlanMapppingPojo custPlanMapppingPojo : customers.getPlanMappingList()) {
//                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
//                                    if (postpaidPlan != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
                            case CommonConstants.CAF_CONDITION.PLAN_CATEGORY: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.BU: {
                                if (customers.getBuId() != null) {
                                    BusinessUnit businessUnit = businessUnitRepository.findById(customers.getBuId()).orElse(null);
                                    if (businessUnit != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                    break;
                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.OLD_DISCOUNT: {

                                for (CustPlanMapppingPojo custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    CustomerServiceMapping customerServiceMapping=customerServiceMappingRepository.findById(custPlanMapppingPojo.getCustServiceMappingId()).orElse(null);
                                    if (customerServiceMapping.getOld_discount() != null) {
                                        Double discount = Double.valueOf(df.format(customerServiceMapping.getOld_discount()));
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = discount == Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = discount != Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case ">":
                                                condition = discount > Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;

                                            case "<":
                                                condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;

                                            case "<=":
                                                condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;

                                            case ">=":
                                                condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }

                                }
                                break;
                            }
                            case CommonConstants.CAF_CONDITION.DEPARTMENT: {
                                if (customers.getDepartmentId()!= null) {
                                    Department department = departmentRepository.findById(customers.getDepartmentId()).orElse(null);
                                    condition = true;
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = department.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }

                                        case "!=": {
                                            condition = !department.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }

                                    }
                                }
                                break;
                            }


                        }
                        queryInit.append(condition);
                        if (queryFieldMapping.getQueryCondition().equals("and")) {
                            queryInit.append("&&");
                        } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                            queryInit.append("||");
                        }
                    }
                    try {
                        return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                    } catch (ScriptException e) {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                    }
                }
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION: {
                if (entity instanceof CustomersPojo) {
                    CustomersPojo terminatedCustomer = (CustomersPojo) entity;
                    Partner partner = partnerRepository.findById(terminatedCustomer.getPartnerid()).orElse(null);
                    CustomerAddress customerAddress = customerAddressService.findByAddressTypeAndCustomerId("Present", terminatedCustomer.getId());
                    for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                        switch (queryFieldMapping.getQueryField()) {
                            case CommonConstants.TERMINATION_CONDITION.DISCOUNT: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : terminatedCustomer.getPlanMappingList()) {
                                    if (custPlanMapppingPojo.getDiscount() != null) {
                                        Double discount = Double.valueOf(df.format(custPlanMapppingPojo.getDiscount()));

                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = discount == Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case ">":
                                                condition = discount > Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;

                                            case "<":
                                                condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;

                                            case "<=":
                                                condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;

                                            case ">=":
                                                condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = discount != Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.WALLET_AMOUNT: {
                                if (terminatedCustomer.getWalletbalance() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = terminatedCustomer.getWalletbalance() == Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case ">":
                                            condition = terminatedCustomer.getWalletbalance() > Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case "<":
                                            condition = terminatedCustomer.getWalletbalance() < Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case "<=":
                                            condition = terminatedCustomer.getWalletbalance() <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case ">=":
                                            condition = terminatedCustomer.getWalletbalance() >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = terminatedCustomer.getWalletbalance() != Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.PLAN_PURCHASE_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = terminatedCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !terminatedCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.PLAN_MODE: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : terminatedCustomer.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.SERVICE_AREA: {
                                ServiceArea serviceArea = serviceAreaService.getByID(terminatedCustomer.getServiceareaid());
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.CALENDAR_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = terminatedCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !terminatedCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.CATEGORY:
                            case CommonConstants.TERMINATION_CONDITION.CUSTOMER_CATEGORY: {
                                if (terminatedCustomer.getDunningCategory() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = terminatedCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !terminatedCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }

                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.PARTNER_NAME: {
                                if (partner != null) switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = partner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !partner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;


                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.PARTNER_EMAIL: {
                                if (partner != null && partner.getEmail() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = partner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !partner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }

                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.AREA: {
                                Area area = customerAddress.getArea();
                                if (area != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.PINCODE: {
                                Pincode pincode = customerAddress.getPincode();
                                if (pincode != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.CITY: {
                                City city = customerAddress.getCity();
                                if (city != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.STATE: {
                                State state = customerAddress.getState();
                                if (state != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.BILL_TO: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : terminatedCustomer.getPlanMappingList()) {
                                    if (custPlanMapppingPojo.getBillTo() != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;


                                        }
                                    }
                                }
                                break;
                            }
//                            case CommonConstants.TERMINATION_CONDITION.INVOICE_TO_ORG: {
//                                for (CustPlanMapppingPojo custPlanMapppingPojo : terminatedCustomer.getPlanMappingList()) {
//                                    if (custPlanMapppingPojo.getIsInvoiceToOrg().toString() != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//
//
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.TERMINATION_CONDITION.PARENT_CUSTOMER_USERNAME: {
//                                if (terminatedCustomer.getParentCustomers() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = terminatedCustomer.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !terminatedCustomer.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//
//                                    }
//                                }
//                                break;
//                            }
                            case CommonConstants.TERMINATION_CONDITION.USERNAME: {
                                if (terminatedCustomer.getUsername() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = terminatedCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !terminatedCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;

                            }
                            case CommonConstants.TERMINATION_CONDITION.PLAN_SERVICES: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : terminatedCustomer.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                }
                                break;
                            }
//                            case CommonConstants.TERMINATION_CONDITION.CURRENT_TEAM_ASSIGNED: {
//                                if (terminatedCustomer.getNextTeamHierarchyMapping() != null) {
//                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(terminatedCustomer.getNextTeamHierarchyMapping()).orElse(null);
//                                    if (teamHierarchyMapping != null) {
//                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
//                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//
//                                        }
//                                    }
//                                }
//                                break;
//                            }
                            case CommonConstants.TERMINATION_CONDITION.LEAD_SOURCE: {
                                if (terminatedCustomer.getLeadSource() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = terminatedCustomer.getLeadSource().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                        case "!=":
                                            condition = !terminatedCustomer.getLeadSource().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }

                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.FEASIBILITY_REQUIRED: {
                                if (terminatedCustomer.getFeasibilityRequired() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = terminatedCustomer.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                        case "!=":
                                            condition = !terminatedCustomer.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }

                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.BRANCH: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(terminatedCustomer.getServiceareaid());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                    if (branchServiceAreaMappings.size() > 0 && branch != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
                                                break;
                                            }
                                            case "!=": {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.REGION: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(terminatedCustomer.getServiceareaid());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                    if (branchServiceAreaMappings.size() > 0 && region != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                break;
                                            }
                                            case "!=": {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.BUSINESS_VERTICAL: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(terminatedCustomer.getServiceareaid());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                    if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                for (Region region : businessVerticals.getBuregionidList()) {
                                                    condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                    break;
                                                }
                                            }
                                            case "!=": {
                                                for (Region region : businessVerticals.getBuregionidList()) {
                                                    condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.PLAN_GROUP: {
//                                if (terminatedCustomer.getPlangroupid() != null) {
//                                    PlanGroup planGroup = planGroupRepository.findById(terminatedCustomer.getPlangroupid()).orElse(null);
//                                    if (planGroup != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//                                    }
//                                }
//                                break;
                                for (CustPlanMapppingPojo custPlanMapppingPojo : terminatedCustomer.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.PLAN_CATEGORY: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : terminatedCustomer.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.BU: {
                                if (terminatedCustomer.getBuId() != null) {
                                    BusinessUnit businessUnit = businessUnitRepository.findById(terminatedCustomer.getBuId()).orElse(null);
                                    if (businessUnit != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                    break;
                                }
                                break;
                            }
                            case CommonConstants.TERMINATION_CONDITION.OLD_DISCOUNT: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : terminatedCustomer.getPlanMappingList()) {
                                    if (custPlanMapppingPojo.getOldDiscount() != null) {
                                        Double discount = Double.valueOf(df.format(custPlanMapppingPojo.getOldDiscount()));
                                        if (custPlanMapppingPojo.getOldDiscount() != null) {
                                            // Double discount = Double.valueOf(df.format(custPlanMapppingPojo.getOldDiscount()));
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==":
                                                    condition = discount == Double.parseDouble(queryFieldMapping.getQueryValue());
                                                    break;
                                                case "!=":
                                                    condition = discount != Double.parseDouble(queryFieldMapping.getQueryValue());
                                                    break;
                                                case ">":
                                                    condition = discount > Double.parseDouble(queryFieldMapping.getQueryValue());
                                                    break;
                                                case ">=":
                                                    condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                    break;
                                                case "<":
                                                    condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
                                                    break;
                                                case "<=":
                                                    condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                    break;
                                            }
                                        }

                                    }
                                }
                                break;
                            }


                        }
                        queryInit.append(condition);
                        if (queryFieldMapping.getQueryCondition().equals("and")) {
                            queryInit.append("&&");
                        } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                            queryInit.append("||");
                        }
                    }
                    try {
                        return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                    } catch (ScriptException e) {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                    }
                }
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN: {
                if (entity instanceof CustomerInventoryMapping) {
                    CustomerInventoryMapping customerInventoryMapping = (CustomerInventoryMapping) entity;
                    Customers assignInventoryCustomer = customerInventoryMapping.getCustomer();
                    CustomersPojo customersinv = customerMapper.domainToDTO(assignInventoryCustomer, new CycleAvoidingMappingContext());
                    Partner assignInventoryCustomerPartner = assignInventoryCustomer.getPartner();
                    CustomerAddress customerAddress = customerAddressService.findByAddressTypeAndCustomerId("Present", assignInventoryCustomer.getId());
                    for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                        switch (queryFieldMapping.getQueryField()) {
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.PLAN_PURCHASE_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = assignInventoryCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }

                                    case "!=": {
                                        condition = !assignInventoryCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.PLAN_MODE: {
                                for (CustPlanMappping custPlanMapppingPojo : assignInventoryCustomer.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.SERVICE_AREA: {
                                ServiceArea serviceArea = assignInventoryCustomer.getServicearea();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.CALENDAR_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = assignInventoryCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !assignInventoryCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.CUSTOMER_CATEGORY: {
                                if (assignInventoryCustomer.getDunningCategory() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = assignInventoryCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !assignInventoryCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.PARTNER_NAME: {
                                if (assignInventoryCustomerPartner != null)
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = assignInventoryCustomerPartner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !assignInventoryCustomerPartner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.PARTNER_EMAIL: {
                                if (assignInventoryCustomerPartner != null && assignInventoryCustomerPartner.getEmail() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = assignInventoryCustomerPartner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !assignInventoryCustomerPartner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }

                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.AREA: {
                                Area area = customerAddress.getArea();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.PINCODE: {
                                Pincode pincode = customerAddress.getPincode();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.CITY: {
                                City city = customerAddress.getCity();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.STATE: {
                                State state = customerAddress.getState();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.BILL_TO: {
                                for (CustPlanMappping custPlanMapppingPojo : assignInventoryCustomer.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.INVOICE_TO_ORG: {
                                for (CustPlanMappping custPlanMapppingPojo : assignInventoryCustomer.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.PARENT_CUSTOMER_USERNAME: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = assignInventoryCustomer.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !assignInventoryCustomer.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;


                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.USERNAME: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = assignInventoryCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !assignInventoryCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;


                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.PLAN_SERVICES: {
                                for (CustPlanMappping custPlanMapppingPojo : assignInventoryCustomer.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.CURRENT_TEAM_ASSIGNED: {
                                if (customerInventoryMapping.getTeamHierarchyMappingId() != null) {
                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(customerInventoryMapping.getTeamHierarchyMappingId()).orElse(null);
                                    if (teamHierarchyMapping != null) {
                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }

                                    }
                                }
                                break;
                            }
//                        case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.CURRENT_PARTNER:
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition =assignInventoryCustomerPartner.get().equals(queryFieldMapping.getQueryValue());
//                                    break;
//
//                                case "!=":
//                                    condition = !customers.getLeadSource().equals(queryFieldMapping.getQueryValue());
//                                    break;
//
//                            }
//                            break;
//                        case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.OLD_INVENTORY_CATEGORY:
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = customers.getFeasibilityRequired().equals(queryFieldMapping.getQueryValue());
//                                    break;
//
//                                case "!=":
//                                    condition = !customers.getFeasibilityRequired().equals(queryFieldMapping.getQueryValue());
//                                    break;
//
//                            }
//                            break;
//                        case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.OLD_INVENTORY_PRODUCT:
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = customers.getFeasibilityRequired().equals(queryFieldMapping.getQueryValue());
//                                    break;
//
//                                case "!=":
//                                    condition = !customers.getFeasibilityRequired().equals(queryFieldMapping.getQueryValue());
//                                    break;
//
//                            }
//                            break;
//                        case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.NEW_INVENTORY_CATEGORY:
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = customers.getFeasibilityRequired().equals(queryFieldMapping.getQueryValue());
//                                    break;
//
//                                case "!=":
//                                    condition = !customers.getFeasibilityRequired().equals(queryFieldMapping.getQueryValue());
//                                    break;
//
//                            }
//                            break;
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.NEW_INVENTORY_PRODUCT: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = customerInventoryMapping.getProduct().getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;

                                    case "!=":
                                        condition = !customerInventoryMapping.getProduct().getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;

                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.BILL_CYCLE: {
                                if (customerInventoryMapping.getCustomer().getPlanMappingList().size() > 0) {
                                    for (CustPlanMappping custPlanMappping : customerInventoryMapping.getCustomer().getPlanMappingList()) {
                                        QPostpaidPlanCharge qPostpaidPlanCharge = QPostpaidPlanCharge.postpaidPlanCharge;
                                        List<PostpaidPlanCharge> postpaidPlanCharges = IterableUtils.toList(postpaidPlanChargeRepo.findAll(qPostpaidPlanCharge.plan.id.eq(custPlanMappping.getPostpaidPlan().getId())));
                                        for (PostpaidPlanCharge charge : postpaidPlanCharges) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==":
                                                    condition = charge.getBillingCycle().toString().equals(queryFieldMapping.getQueryValue());
                                                    break;
                                                case "!=":
                                                    condition = !charge.getBillingCycle().toString().equals(queryFieldMapping.getQueryValue());
                                                    break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.BRANCH: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customersinv.getServiceareaid());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                    if (branchServiceAreaMappings.size() > 0 && branch != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
                                                break;
                                            }
                                            case "!=": {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.REGION: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customersinv.getServiceareaid());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                    if (branchServiceAreaMappings.size() > 0 && region != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                break;
                                            }
                                            case "!=": {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.BUSINESS_VERTICAL: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customersinv.getServiceareaid());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                    if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                for (Region region : businessVerticals.getBuregionidList()) {
                                                    condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                    break;
                                                }
                                            }
                                            case "!=": {
                                                for (Region region : businessVerticals.getBuregionidList()) {
                                                    condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.PLAN_GROUP: {
//                                if (customersinv.getPlangroupid() != null) {
//                                    PlanGroup planGroup = planGroupRepository.findById(customersinv.getPlangroupid()).orElse(null);
//                                    if (planGroup != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//                                    }
//                                }
//                                break;
//                            }
                                for (CustPlanMappping custPlanMapppingPojo : assignInventoryCustomer.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.PLAN_CATEGORY: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : customersinv.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.BU: {
                                if (customersinv.getBuId() != null) {
                                    BusinessUnit businessUnit = businessUnitRepository.findById(customersinv.getBuId()).orElse(null);
                                    if (businessUnit != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                    break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.OLD_DISCOUNT: {
                                for (CustPlanMapppingPojo custPlanMapppingPojo : customersinv.getPlanMappingList()) {
                                    //Double discount = Double.valueOf(df.format(custPlanMapppingPojo.getOldDiscount()));
                                    if (custPlanMapppingPojo.getOldDiscount() != null) {
                                        Double discount = Double.valueOf(df.format(custPlanMapppingPojo.getOldDiscount()));
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = discount == Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = discount != Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case ">":
                                                condition = discount > Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case ">=":
                                                condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "<":
                                                condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "<=":
                                                condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }

                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_CONDITION.FEASIBILITY_REQUIRED: {
                                if (customersinv.getFeasibilityRequired() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = customersinv.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                        case "!=":
                                            condition = !customersinv.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }
                                    break;
                                }
                                break;
                            }

                        }
                        queryInit.append(condition);
                        if (queryFieldMapping.getQueryCondition().equals("and")) {
                            queryInit.append("&&");
                        } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                            queryInit.append("||");
                        }
                    }
                    try {
                        return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                    } catch (ScriptException e) {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                    }
                }
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE:
            case CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT: {
                if (entity instanceof CreditDocument) {
                    CreditDocument creditDocument = (CreditDocument) entity;
                    //String creditnoteType = creditDocument.getPaytype().replace(" ", "");
                    String creditnoteType = creditDocument.getType().replace(" ", "");
                    if(creditnoteType=="DR"){
                        creditnoteType="Withdrawal";
                    }
                    Customers paymentCustomer = customersService.getById(creditDocument.getCustomer().getId());
                    CustomerAddress customerAddress = customerAddressService.findByAddressTypeAndCustomerId("Present", paymentCustomer.getId());
//                    CustomersPojo customers = (CustomersPojo) entity;
                    Partner paymentCustomerPartner = paymentCustomer.getPartner();
                    for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                        switch (queryFieldMapping.getQueryField()) {
                            case CommonConstants.PAYMENT_CONDITION.PAYMENT_AMOUNT: {
                                if (creditDocument.getAmount() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = creditDocument.getAmount() == Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case ">":
                                            condition = creditDocument.getAmount() > Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case "<":
                                            condition = creditDocument.getAmount() < Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case "<=":
                                            condition = creditDocument.getAmount() <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case ">=":
                                            condition = creditDocument.getAmount() >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = creditDocument.getAmount() != Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.DISCOUNT: {
                                for (CustomerServiceMapping serviceMapping : paymentCustomer.getCustomerServiceMappingList()) {
                                    if (serviceMapping.getDiscount() != null) {
                                        Double discount = Double.valueOf(df.format(serviceMapping.getDiscount()));
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = discount == Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case ">":
                                                condition = discount > Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;

                                            case "<":
                                                condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;

                                            case "<=":
                                                condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;

                                            case ">=":
                                                condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = discount != Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.PLAN_PURCHASE_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = paymentCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !paymentCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.PLAN_MODE: {
                                for (CustPlanMappping custPlanMapppingPojo : paymentCustomer.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.SERVICE_AREA: {
                                ServiceArea serviceArea = serviceAreaService.getByID(paymentCustomer.getServicearea().getId());
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.CALENDAR_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = paymentCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !paymentCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.CUSTOMER_CATEGORY: {
                                if (paymentCustomer.getDunningCategory() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = paymentCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !paymentCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.PARTNER_NAME: {
                                if (paymentCustomerPartner != null) switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = paymentCustomerPartner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !paymentCustomerPartner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;


                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.PARTNER_EMAIL: {
                                if (paymentCustomerPartner != null && paymentCustomerPartner.getEmail() != null)
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = paymentCustomerPartner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !paymentCustomerPartner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.AREA: {
                                Area area = customerAddress.getArea();
                                if (area != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.PINCODE: {
                                Pincode pincode = customerAddress.getPincode();
                                if (pincode != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.CITY: {
                                City city = customerAddress.getCity();
                                if (city != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.STATE: {
                                State state = customerAddress.getState();
                                if (state != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.BILL_TO: {
                                for (CustPlanMappping custPlanMapppingPojo : paymentCustomer.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.INVOICE_TO_ORG: {
                                for (CustPlanMappping custPlanMapppingPojo : paymentCustomer.getPlanMappingList()) {
                                    if (custPlanMapppingPojo.getIsInvoiceToOrg().toString() != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;


                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.PARENT_CUSTOMER_USERNAME: {
                                if (paymentCustomer.getParentCustomers() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = paymentCustomer.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !paymentCustomer.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }

                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.USERNAME: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = paymentCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !paymentCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;


                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.PLAN_SERVICE: {
                                for (CustPlanMappping custPlanMapppingPojo : paymentCustomer.getPlanMappingList()) {
                                    if (custPlanMapppingPojo.getService() != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;


                                        }
                                    }
                                }
                                break;
                            }
//                            case CommonConstants.PAYMENT_CONDITION.CURRENT_TEAM_ASSIGNED: {
//                                if (paymentCustomer.getNextTeamHierarchyMapping() != null) {
//                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(paymentCustomer.getNextTeamHierarchyMapping()).orElse(null);
//                                    if (teamHierarchyMapping != null) {
//                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
//                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//
//                                    }
//                                }
//                                break;
//                            }
                            case CommonConstants.PAYMENT_CONDITION.LEAD_SOURCE: {
                                if (paymentCustomer.getLeadSource() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = paymentCustomer.getLeadSource().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                        case "!=":
                                            condition = !paymentCustomer.getLeadSource().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }
                                    break;
                                }
                            }
                            case CommonConstants.PAYMENT_CONDITION.FEASIBILITY_REQUIRED: {
                                if (paymentCustomer.getFeasibilityRequired() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = paymentCustomer.getFeasibility().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                        case "!=":
                                            condition = !paymentCustomer.getFeasibility().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }
                                    break;
                                }
                            }
                            case CommonConstants.PAYMENT_CONDITION.BRANCH: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(paymentCustomer.getServicearea().getId());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && branch != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId().intValue()));
                                                break;
                                            }
                                            case "!=": {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId().intValue()));
                                                break;
                                            }
                                        }
                                    }else{
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = false;
                                                break;
                                            }
                                            case "!=": {
                                                condition = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.REGION: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(paymentCustomer.getServicearea().getId());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && region != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                //condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == region.getId().longValue());

                                                break;
                                            }
                                            case "!=": {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.BUSINESS_VERTICAL: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(paymentCustomer.getServicearea().getId());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                for (Region region : businessVerticals.getBuregionidList()) {
                                                    condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                    //condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList()==region.getId().longValue());
                                                    break;
                                                }
                                            }
                                            case "!=": {
                                                for (Region region : businessVerticals.getBuregionidList()) {
                                                    condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.PLAN_GROUP: {

                                for (CustPlanMappping custPlanMappping : paymentCustomer.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.PLAN_CATEGORY: {
                                for (CustPlanMappping custPlanMappping : paymentCustomer.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PAYMENT_CONDITION.BU: {
                                if (paymentCustomer.getBuId() != null) {
                                    BusinessUnit businessUnit = businessUnitRepository.findById(paymentCustomer.getBuId()).orElse(null);
                                    if (businessUnit != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                    break;
                                }
                                break;
                            }
//                            case CommonConstants.PAYMENT_CONDITION.OLD_DISCOUNT: {
//                                for (CustomerServiceMapping serviceMapping : paymentCustomer.getCustomerServiceMappingList()) {
//                                    if (serviceMapping.getOld_discount() != null) {
//                                        Double discount = Double.valueOf(df.format(serviceMapping.getOld_discount()));
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = discount == Double.parseDouble(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = discount != Double.parseDouble(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case ">":
//                                                condition = discount > Double.parseDouble(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case ">=":
//                                                condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "<":
//                                                condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "<=":
//                                                condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
//                                                break;
////                                            case ">=":
////                                                condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
////                                                break;
////                                            case "<":
////                                                condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
////                                                break;
////                                            case "<=":
////                                                condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
////                                                break;
//                                        }
//                                    }
//
//                                }
//                                break;
//                            }

                            case CommonConstants.PAYMENT_CONDITION.PAYMENT_MODE: {
                                try {
                                    if (creditDocument.getPaymode() != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = creditDocument.getPaymode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !creditDocument.getPaymode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // List<CommonListDTO>commonListDTOS=

                                break;
                            }

//                            case CommonConstants.PAYMENT_CONDITION.PAYMENT_TYPE: {
//                                if (creditDocument.getPaymode() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = creditDocument.getPaytype().equalsIgnoreCase(queryFieldMapping.getQueryValue().replace(" ", ""));
//                                            break;
//                                        case "!=":
//                                            condition = !creditDocument.getPaytype().equalsIgnoreCase(queryFieldMapping.getQueryValue().replace(" ", ""));
//                                            break;
//                                    }
//
//                                }
//                                break;
//                            }
//                            case CommonConstants.PAYMENT_CONDITION.WITHDRAWAL:{
//                                if(creditDocument.getId()!=null){
//                                  List<CreditDebitDocMapping> creditDebitDocMapping=  creditDebtMappingRepository.findByCreditDocId(creditDocument.getId());
//                                  if(creditDebitDocMapping.size()>0){
//                                      for(CreditDebitDocMapping creditDebitDocMappings : creditDebitDocMapping){
//                                          if(creditDebitDocMappings.getWithdrawId()!=null){
//
//                                          }
//
//                                      }
//                                  }
//                                }
//
//                               break;
//                            }
//                            case CommonConstants.PAYMENT_CONDITION.PAYMENT_MODE: {
//                                try {
//                                    List<CommonList> commonListDTOList = commonListRepository.findAllByTypeAndStatusOrderByValueAsc("paymentmode", CommonConstants.ACTIVE_STATUS);
//                                    if (creditDocument.getPaymode() != null) {
//                                        //     for (CommonList commonListDTO : commonListDTOList) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                for (CommonList commonListDTO : commonListDTOList) {
//                                                    if(commonListDTO.getValue().equalsIgnoreCase(queryFieldMapping.getQueryValue())){
//                                                        condition =true;
//                                                        break;
//                                                    }else {
//                                                        condition =false;
//                                                    }
//                                                  //  break;
//                                                }
//                                                break;
//                                            case "!=":
//                                                for (CommonList commonListDTO : commonListDTOList) {
//                                                    if(!commonListDTO.getValue().equalsIgnoreCase(queryFieldMapping.getQueryValue())){
//                                                        condition =true;
//                                                        break;
//                                                    }else {
//                                                        condition =false;
//                                                    }
//                                                   // break;
//                                                }
//                                                break;
//                                        }
//                                        //     }
//                                    }
//                                    break;
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                // List<CommonListDTO>commonListDTOS=
//
//                                break;
//                            }
                            case CommonConstants.PAYMENT_CONDITION.PAYMENT_TYPE: {
                                if (creditDocument.getPaytype() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = creditnoteType.equalsIgnoreCase(queryFieldMapping.getQueryValue().replace(" ", ""));
                                            break;

                                        case "!=":
                                            condition = !creditnoteType.equalsIgnoreCase(queryFieldMapping.getQueryValue().replace(" ", ""));
                                    }

                                }
                                break;
                            }

//
                            case CommonConstants.PAYMENT_CONDITION.TEAM_ASSIGNED_NEW: {
                                CustPlanMappping custPlanMappping = paymentCustomer.getPlanMappingList().get(0);
                                Customers customers = custPlanMappping.getCustomer();
                                if (creditDocument.getNextTeamHierarchyMappingId() != null) {
                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(creditDocument.getNextTeamHierarchyMappingId()).orElse(null);
                                    if (teamHierarchyMapping != null) {
                                        Teams teams = getNextTeamHirMappingByOrderNumberAndWorkflowId(teamHierarchyMapping.getOrderNumber() + 1, teamHierarchyMapping.getHierarchyId());
//                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
                                        if (teams != null) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==": {
                                                    condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                                case "!=": {
                                                    condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        queryInit.append(condition);
                        if (queryFieldMapping.getQueryCondition().equals("and")) {
                            queryInit.append("&&");
                        } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                            queryInit.append("||");
                        }
                    }
                    try {
                        return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                    } catch (ScriptException e) {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                    }
                }
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.PLAN: {
                if (entity instanceof PostpaidPlanPojo) {
                    PostpaidPlanPojo plan = (PostpaidPlanPojo) entity;
                    List<PostPaidPlanServiceAreaMapping> postPaidPlanServiceAreaMappings = postPaidPlanServiceAreaMappingRepo.findAllByPlanId(plan.getId());
                    for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                        switch (queryFieldMapping.getQueryField()) {
                            case CommonConstants.PLAN_CONDITION.OFFER_PRICE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = plan.getOfferprice() == Double.parseDouble(queryFieldMapping.getQueryValue());
                                        break;
                                    case ">":
                                        condition = plan.getOfferprice() > Double.parseDouble(queryFieldMapping.getQueryValue());
                                        break;
                                    case "<":
                                        condition = plan.getOfferprice() < Double.parseDouble(queryFieldMapping.getQueryValue());
                                        break;
                                    case "<=":
                                        condition = plan.getOfferprice() <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                        break;
                                    case ">=":
                                        condition = plan.getOfferprice() >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = plan.getOfferprice() != Double.parseDouble(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.PLAN_CONDITION.SERVICE_AREA: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = plan.getServiceAreaNameList().stream().anyMatch(serviceAreaDTO -> serviceAreaDTO.getName().equals(queryFieldMapping.getQueryValue()));
                                        break;
                                    case "!=":
                                        condition = plan.getServiceAreaNameList().stream().noneMatch(serviceAreaDTO -> serviceAreaDTO.getName().equals(queryFieldMapping.getQueryValue()));
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.PLAN_CONDITION.SERVICE: {
                                PlanService service = planServiceRepository.findById(plan.getServiceId()).orElse(null);
                                if (service != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = service.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !service.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PLAN_CONDITION.PLAN_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = plan.getPlantype().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !plan.getPlantype().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
//                            case CommonConstants.PLAN_CONDITION.PLAN_GROUP: {
//                                if (plan.getPlanGroup() != null) {
//                                    String planGroup = plan.getPlanGroup();
//                                    if (planGroup != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = planGroup.equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !planGroup.equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//                                    }
//                                }
//                                break;
//                            }
                            case CommonConstants.PLAN_CONDITION.PLAN_CATEGORY: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = plan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    }
                                    break;
                                    case "!=": {
                                        condition = !plan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    }
                                    break;
                                }
                                break;
                            }
                            case CommonConstants.PLAN_CONDITION.QUOTA_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = plan.getQuotatype().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    }
                                    break;
                                    case "!=": {
                                        condition = !plan.getQuotatype().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    }
                                    break;
                                }
                                break;
                            }
                            case CommonConstants.PLAN_CONDITION.QUOTA_RESET_INTERVAL: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = plan.getQuotaResetInterval().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                    case "!=":
                                        condition = !plan.getQuotaResetInterval().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.PLAN_CONDITION.BILL_CYCLE: {
                                QPostpaidPlanCharge qPostpaidPlanCharge = QPostpaidPlanCharge.postpaidPlanCharge;
                                List<PostpaidPlanCharge> postpaidPlanChargeList = IterableUtils.toList(postpaidPlanChargeRepo.findAll(qPostpaidPlanCharge.plan.id.eq(plan.getId())));
                                for (PostpaidPlanCharge postpaidPlanCharge : postpaidPlanChargeList) {
                                    if (postpaidPlanCharge.getBillingCycle() != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = postpaidPlanCharge.getBillingCycle().toString().equals(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !postpaidPlanCharge.getBillingCycle().toString().equals(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
//                            case CommonConstants.PLAN_CONDITION.ICCODE: {
//                                PlanService service = planServiceRepository.findById(plan.getServiceId()).orElse(null);
//                                if (service != null && service.getIccode() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = service.getIccode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !service.getIccode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//                                }
//                                break;
//                            }
                            case CommonConstants.PLAN_CONDITION.BRANCH: {
                                QBranchServiceAreaMapping qBranchServiceAreaMapping = QBranchServiceAreaMapping.branchServiceAreaMapping;
                                qBranchServiceAreaMapping.serviceareaId.in(plan.getServiceAreaNameList().stream().map(serviceAreaDTO -> serviceAreaDTO.getId().intValue()).collect(Collectors.toList()));
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = IterableUtils.toList(branchServiceAreaMappingRepository.findAll(qBranchServiceAreaMapping.serviceareaId.in(plan.getServiceAreaNameList().stream().map(serviceAreaDTO -> serviceAreaDTO.getId().intValue()).collect(Collectors.toList()))));
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && branch != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId().intValue()));
                                                break;
                                            }
                                            case "!=": {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId().intValue()));
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PLAN_CONDITION.REGION: {

                                QBranchServiceAreaMapping qBranchServiceAreaMapping = QBranchServiceAreaMapping.branchServiceAreaMapping;
                                qBranchServiceAreaMapping.serviceareaId.in(plan.getServiceAreaNameList().stream().map(serviceAreaDTO -> serviceAreaDTO.getId().intValue()).collect(Collectors.toList()));
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = IterableUtils.toList(branchServiceAreaMappingRepository.findAll(qBranchServiceAreaMapping.serviceareaId.in(plan.getServiceAreaNameList().stream().map(serviceAreaDTO -> serviceAreaDTO.getId().intValue()).collect(Collectors.toList()))));
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && region != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().stream().map(branch -> branch.getId().intValue()).collect(Collectors.toList()).contains(branchServiceAreaMapping.getBranchId()));
                                            }
                                            break;
                                            case "!=": {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().stream().map(branch -> branch.getId().intValue()).collect(Collectors.toList()).contains(branchServiceAreaMapping.getBranchId()));
                                            }
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PLAN_CONDITION.BUSINESS_VERTICAL: {

                                QBranchServiceAreaMapping qBranchServiceAreaMapping;
                                qBranchServiceAreaMapping = QBranchServiceAreaMapping.branchServiceAreaMapping;
                                qBranchServiceAreaMapping.serviceareaId.in(plan.getServiceAreaNameList().stream().map(serviceAreaDTO -> serviceAreaDTO.getId().intValue()).collect(Collectors.toList()));
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = IterableUtils.toList(branchServiceAreaMappingRepository.findAll(qBranchServiceAreaMapping.serviceareaId.in(plan.getServiceAreaNameList().stream().map(serviceAreaDTO -> serviceAreaDTO.getId().intValue()).collect(Collectors.toList()))));
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                for (Region region : businessVerticals.getBuregionidList()) {
                                                    condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().stream().map(branch -> branch.getId().intValue()).collect(Collectors.toList()).contains(branchServiceAreaMapping.getBranchId()));
                                                }
                                                break;
                                            }
                                            case "!=": {
                                                for (Region region : businessVerticals.getBuregionidList()) {
                                                    condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().stream().map(branch -> branch.getId().intValue()).collect(Collectors.toList()).contains(branchServiceAreaMapping.getBranchId()));
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
//                            case CommonConstants.PLAN_CONDITION.CURRENT_TEAM_ASSIGNED: {
//                                if (plan.getNextTeamHierarchyMapping() != null) {
//                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(plan.getNextTeamHierarchyMapping()).orElse(null);
//                                    if (teamHierarchyMapping != null) {
//                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
//                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//                                    }
//                                }
//                                break;
//                            }

                            case CommonConstants.PLAN_CONDITION.PLAN_MODE: {

//                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                if (plan != null) switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = plan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !plan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }

                                break;
                            }
                            case CommonConstants.PLAN_CONDITION.BU: {
                                if (plan.getBuId() != null) {
                                    BusinessUnit businessUnit = businessUnitRepository.findById(plan.getBuId()).orElse(null);
                                    if (businessUnit != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                    break;
                                }
                                break;
                            }
//plan condtion for discount
                            case CommonConstants.PLAN_CONDITION.DISCOUNT: {
                                if (plan.getAllowdiscount() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = (plan.getAllowdiscount()).toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        }
                                        break;
                                        case "!=": {
                                            condition = !(plan.getAllowdiscount()).toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        }
                                        break;
                                    }
                                    break;
                                }
                            }

                            case CommonConstants.PLAN_CONDITION.PLAN_GROUP: {
                                if (plan.getPlanGroup() != null) {
                                    String planGroup = plan.getPlanGroup();
                                    if (planGroup != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = planGroup.equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !planGroup.equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }

                        }
                        queryInit.append(condition);
                        if (queryFieldMapping.getQueryCondition().equals("and")) {
                            queryInit.append("&&");
                        } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                            queryInit.append("||");
                        }
                    }
                    try {
                        return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                    } catch (ScriptException e) {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                    }
                }
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.CASE: {
               return checkTicketCondition(entity,queryFieldMappingList,condition,queryInit,engine);
//                if (entity instanceof CaseDTO) {
//                    CaseDTO caseDTO = (CaseDTO) entity;
//                    Customers ticketCustomer = customersService.get(caseDTO.getCustomersId());
//                    Partner ticketCustomerPartner = partnerRepository.findById(ticketCustomer.getPartner().getId()).orElse(null);
//                    CustomerAddress ticketCustomerAddress = customerAddressService.findByAddressTypeAndCustomerId("Present", ticketCustomer.getId());
//                    for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
//                        switch (queryFieldMapping.getQueryField()) {
//                            case CommonConstants.CASE_CONDITION.PLAN_PURCHASE_TYPE: {
//                                if (ticketCustomer.getPlanPurchaseType() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = ticketCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !ticketCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//                                    break;
//                                }
//                                break;
//
//                            }
//                            case CommonConstants.CASE_CONDITION.PLAN_MODE: {
//                                for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
//                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
//                                    if (postpaidPlan != null) switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.SERVICE_AREA: {
//                                ServiceArea serviceArea = serviceAreaService.getByID(ticketCustomer.getServicearea().getId());
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.CALENDAR_TYPE: {
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = ticketCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !ticketCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.CATEGORY: {
//                                if (ticketCustomer.getDunningCategory() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//
//                                    }
//                                    break;
//                                }
//                            }
//                            case CommonConstants.CASE_CONDITION.PARTNER_NAME: {
//                                if (ticketCustomerPartner != null) switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = ticketCustomerPartner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !ticketCustomerPartner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//
//
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.PARTNER_EMAIL: {
//                                if (ticketCustomerPartner != null && ticketCustomerPartner.getEmail() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = ticketCustomerPartner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !ticketCustomerPartner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//                                    break;
//                                }
//                                break;
//
//                            }
//                            case CommonConstants.CASE_CONDITION.AREA: {
//                                Area area = ticketCustomerAddress.getArea();
//                                switch (queryFieldMapping.getQueryOperator()) {
//
//                                    case "==":
//                                        condition = area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.PINCODE: {
//                                Pincode pincode = ticketCustomerAddress.getPincode();
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.CITY: {
//                                City city = ticketCustomerAddress.getCity();
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.STATE: {
//                                State state = ticketCustomerAddress.getState();
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.BILL_TO: {
//                                for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//
//
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.INVOICE_TO_ORG: {
//                                for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//
//
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.PARENT_CUSTOMER_USERNAME: {
//                                if (ticketCustomer.getParentCustomers() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = ticketCustomer.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !ticketCustomer.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.USERNAME: {
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = ticketCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !ticketCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//
//
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.PLAN_SERVICES: {
//                                for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//
//
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.CURRENT_TEAM_ASSIGNED: {
//                                if (caseDTO.getTeamHierarchyMappingId() != null) {
//                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Math.toIntExact(caseDTO.getTeamHierarchyMappingId())).orElse(null);
//                                    if (teamHierarchyMapping != null) {
//                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
//                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//
//                                    }
//                                }
//                                break;
//                            }
//
//                            case CommonConstants.PAYMENT_CONDITION.CUSTOMER_CATEGORY: {
//                                if (ticketCustomer.getDunningCategory() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//
//                                    }
//                                }
//                                break;
//                            }
////                        case CommonConstants.CASE_CONDITION.CURRENT_PARTNER:
////
////                            break;
//                            case CommonConstants.CASE_CONDITION.TICKET_CATEGORY: {
//                                if (caseDTO.getCaseReasonCategory() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==": {
//                                            condition = caseDTO.getCaseReasonCategory().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
//                                            break;
//                                        }
//                                        case "!=": {
//                                            condition = !caseDTO.getCaseReasonCategory().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
//                                            break;
//                                        }
//
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.TICKET_SUB_CATEGORY: {
//                                if (caseDTO.getCaseReasonSubCategory() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = caseDTO.getCaseReasonSubCategory().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
//                                            break;
//                                        case "!=":
//                                            condition = !caseDTO.getCaseReasonSubCategory().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
//                                            break;
//                                    }
//
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.PRIORITY: {
//                                if (caseDTO.getPriority() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = caseDTO.getPriority().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !caseDTO.getPriority().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//                                }
//                                break;
//
//                            }
//                            case CommonConstants.CASE_CONDITION.DEPARTMENT: {
//                                if (caseDTO.getDepartment() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==": {
//                                            condition = caseDTO.getDepartment().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//                                        case "!=": {
//                                            condition = !caseDTO.getDepartment().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.BRANCH: {
//                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(ticketCustomer.getServicearea().getId());
//                                if (queryFieldMapping.getQueryOperator() != null) {
//                                    Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
//                                    if (branchServiceAreaMappings.size() > 0 && branch != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.REGION: {
//                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(ticketCustomer.getServicearea().getId());
//                                if (queryFieldMapping.getQueryOperator() != null) {
//                                    Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
//                                    if (branchServiceAreaMappings.size() > 0 && region != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.BUSINESS_VERTICAL: {
//                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(ticketCustomer.getServicearea().getId());
//                                if (queryFieldMapping.getQueryOperator() != null) {
//                                    BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
//                                    if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                for (Region region : businessVerticals.getBuregionidList()) {
//                                                    condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                                    break;
//                                                }
//                                            }
//                                            case "!=": {
//                                                for (Region region : businessVerticals.getBuregionidList()) {
//                                                    condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.PLAN_GROUP: {
////                                if (ticketCustomer.getPlangroup() != null) {
////                                    PlanGroup planGroup = ticketCustomer.getPlangroup();
////                                    if (planGroup != null) {
////                                        switch (queryFieldMapping.getQueryOperator()) {
////                                            case "==": {
////                                                condition = planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
////                                                break;
////                                            }
////                                            case "!=": {
////                                                condition = !planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
////                                                break;
////                                            }
////                                        }
////                                    }
////                                }
////                                break;
//                                for (CustPlanMappping custPlanMappping : ticketCustomer.getPlanMappingList()) {
//                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
//                                    if (postpaidPlan != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.PLAN_CATEGORY: {
//                                for (CustPlanMappping custPlanMappping : ticketCustomer.getPlanMappingList()) {
//                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
//                                    if (postpaidPlan != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.FEASIBILITY_REQUIRED: {
//                                if (ticketCustomer.getFeasibilityRequired() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==": {
//
//                                            condition = ticketCustomer.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//
//                                        case "!=": {
//                                            condition = !ticketCustomer.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//
//                                    }
//
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.BU: {
//                                if (ticketCustomer.getBuId() != null) {
//                                    BusinessUnit businessUnit = businessUnitRepository.findById(ticketCustomer.getBuId()).orElse(null);
//                                    if (businessUnit != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//                                    }
//
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.VALLEY_TYPE: {
//                                if (ticketCustomer.getValleyType() != null) {
//                                    String valleyType = ticketCustomer.getValleyType().replace(" ", "");
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = valleyType.equalsIgnoreCase(queryFieldMapping.getQueryValue().replace(" ", ""));
//                                            break;
//                                        case "!=":
//                                            condition = !valleyType.equalsIgnoreCase(queryFieldMapping.getQueryValue().replace(" ", ""));
//                                            break;
////                                        case "!=":
////                                            condition = !ticketCustomer.getCustomerArea().equalsIgnoreCase(queryFieldMapping.getQueryValue());
////                                            break;
//                                    }
//                                    break;
//                                }
//                            }
//                            case CommonConstants.CASE_CONDITION.CUSTOMER_AREA: {
//                                if (ticketCustomer.getCustomerArea() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = ticketCustomer.getCustomerArea().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !ticketCustomer.getCustomerArea().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.CUSTOMER_TYPE: {
//                                if (ticketCustomer != null) {
//                                switch (queryFieldMapping.getQueryOperator()){
//                                    case "==":
//                                        condition = ticketCustomer.getCustcategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !ticketCustomer.getCustcategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//
//                                }
//                                }
//
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.TICKET_STSTUS: {
//                                if (caseDTO.getCaseStatus() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = caseDTO.getCaseStatus().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !caseDTO.getCaseStatus().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.TICKET_RAISED_BY_TEAM: {
//                                List<Long> teams=teamsRepository.findAllByStaff(caseDTO.getCreatedById());
//                                if(teams.size()>0){
//                                List<Teams>teamsList=teamsRepository.findAllByIdIn(teams);
//                                for(Teams teams1:teamsList){
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = teams1.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !teams1.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//                                }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.CASE_TYPE: {
//                                if(caseDTO.getCaseType()!=null){
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = caseDTO.getCaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !caseDTO.getCaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//                                }
//
//                                break;
//                            }
//                            case CommonConstants.CASE_CONDITION.TICCKET_CREATED_DURATION: {
//                                if(caseDTO!=null){
//                                    LocalDate localDate =caseDTO.getCreatedate().toLocalDate();
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = localDate.toString().equals(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = !localDate.toString().equals(queryFieldMapping.getQueryValue());;
//                                            break;
//                                    }
//                                }
//                                break;
//                            }
//
//
//                        }
//
//                        queryInit.append(condition);
//                        if (queryFieldMapping.getQueryCondition().equals("and")) {
//                            queryInit.append("&&");
//                        } else if (queryFieldMapping.getQueryCondition().equals("or")) {
//                            queryInit.append("||");
//                        }
//                    }
//                    try {
//                        return (Boolean) engine.eval(queryInit.toString().toLowerCase());
//                    } catch (ScriptException e) {
//                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
//                    }
//                }
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP: {
                if (entity instanceof PlanGroup) {
                    PlanGroup planGroup = (PlanGroup) entity;

                    List<PostpaidPlan> postpaidPlanList = planGroupService.findAllPlansByPlanGroups(planGroup.getMvnoId(), planGroup.getPlanGroupId());
                    List<PostPaidPlanServiceAreaMapping> postPaidPlanServiceAreaMappings = postPaidPlanServiceAreaMappingRepo.findAllByPlanId(postpaidPlanList.get(0).getId());
                    for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                        switch (queryFieldMapping.getQueryField()) {
                            case CommonConstants.PLAN_GROUP_CONDITION.OFFER_PRICE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = planGroup.getOfferprice() == Double.parseDouble(queryFieldMapping.getQueryValue());
                                        break;
                                    case ">":
                                        condition = planGroup.getOfferprice() > Double.parseDouble(queryFieldMapping.getQueryValue());
                                        break;
                                    case "<":
                                        condition = planGroup.getOfferprice() < Double.parseDouble(queryFieldMapping.getQueryValue());
                                        break;
                                    case "<=":
                                        condition = planGroup.getOfferprice() <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                        break;
                                    case ">=":
                                        condition = planGroup.getOfferprice() >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.PLAN_GROUP_CONDITION.SERVICE_AREA: {
                                List<String> name = (List<String>) planGroup.getServicearea().stream().map(serviceArea -> serviceArea.getName()).collect(Collectors.toList());
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = name.contains(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !name.contains(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PLAN_GROUP_CONDITION.SERVICE: {
                                if (postpaidPlanList != null && !postpaidPlanList.isEmpty()) {
                                    for (PostpaidPlan postpaidPlan : postpaidPlanList) {
                                        Services service = serviceRepository.findById(postpaidPlan.getServiceId().longValue()).get();
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = service.getServiceName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !service.getServiceName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PLAN_GROUP_CONDITION.PLAN_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = planGroup.getPlantype().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !planGroup.getPlantype().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PLAN_GROUP_CONDITION.PLAN_CATEGORY: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = planGroup.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !planGroup.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.PLAN_GROUP_CONDITION.QUOTA_TYPE: {
                                if (postpaidPlanList != null && !postpaidPlanList.isEmpty()) {
                                    for (PostpaidPlan plan : postpaidPlanList) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = plan.getQuotatype().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !plan.getQuotatype().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PLAN_GROUP_CONDITION.QUOTA_RESET_INTERVAL: {
                                if (postpaidPlanList != null && !postpaidPlanList.isEmpty()) {
                                    for (PostpaidPlan postpaidPlan : postpaidPlanList) {
                                        if (postpaidPlan.getQuotaResetInterval() != null) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==": {
                                                    condition = postpaidPlan.getQuotaResetInterval().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                                case "!=": {
                                                    condition = !postpaidPlan.getQuotaResetInterval().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
//                            case CommonConstants.PLAN_GROUP_CONDITION.ICCODE: {
//                                if (postpaidPlanList != null && !postpaidPlanList.isEmpty()) {
//                                    for (PostpaidPlan postpaidPlan : postpaidPlanList) {
//                                        PlanService service = planServiceRepository.findById(postpaidPlan.getServiceId()).orElse(null);
//                                        if (service != null && service.getIccode() != null)
//                                            switch (queryFieldMapping.getQueryOperator()) {
//                                                case "==":
//                                                    condition = service.getIccode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                    break;
//                                                case "!=":
//                                                    condition = !service.getIccode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                    break;
//                                            }
//                                    }
//
//                                }
//                                break;
//                            }
//                            case CommonConstants.PLAN_GROUP_CONDITION.BILL_CYCLE: {
//                                if (postpaidPlanList != null && !postpaidPlanList.isEmpty()) {
//                                    for (PostpaidPlan plan : postpaidPlanList) {
//                                        QPostpaidPlanCharge qPostpaidPlanCharge = QPostpaidPlanCharge.postpaidPlanCharge;
//                                        List<PostpaidPlanCharge> list = IterableUtils.toList(postpaidPlanChargeRepo.findAll(qPostpaidPlanCharge.plan.id.eq(plan.getId())));
//                                        for (PostpaidPlanCharge planCharge : list) {
//                                            if (planCharge != null && planCharge.getBillingCycle() != null)
//                                                switch (queryFieldMapping.getQueryOperator()) {
//                                                    case "==": {
//                                                        condition = condition || planCharge.getBillingCycle().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                        break;
//                                                    }
//                                                    case "!=": {
//                                                        condition = condition || !planCharge.getBillingCycle().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                        break;
//                                                    }
//                                                }
//                                        }
//                                    }
//
//                                }
//                                break;
//                            }
                            case CommonConstants.PLAN_GROUP_CONDITION.BRANCH: {
                                QBranchServiceAreaMapping qBranchServiceAreaMapping = QBranchServiceAreaMapping.branchServiceAreaMapping;
                                qBranchServiceAreaMapping.serviceareaId.in(planGroup.getServicearea().stream().map(serviceAreaDTO -> serviceAreaDTO.getId().intValue()).collect(Collectors.toList()));
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = IterableUtils.toList(branchServiceAreaMappingRepository.findAll(qBranchServiceAreaMapping.serviceareaId.in(planGroup.getServicearea().stream().map(serviceAreaDTO -> serviceAreaDTO.getId().intValue()).collect(Collectors.toList()))));
                                // List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(planGroup.getServicearea().stream().map(serviceArea -> serviceArea.getId().intValue()).collect(Collectors.toList()));
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && branch != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition =branchServiceAreaMappings.stream()
                                                        .map(BranchServiceAreaMapping::getBranchId)
                                                        .anyMatch(branchId -> branchId.equals(branch.getId().intValue()));
                                                break;
                                            }
                                            case "!=": {
                                                condition =  !branchServiceAreaMappings.stream()
                                                        .map(BranchServiceAreaMapping::getBranchId)
                                                        .anyMatch(branchId -> branchId.equals(branch.getId().intValue()));
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PLAN_GROUP_CONDITION.REGION: {
                                QBranchServiceAreaMapping qBranchServiceAreaMapping = QBranchServiceAreaMapping.branchServiceAreaMapping;
                                qBranchServiceAreaMapping.serviceareaId.in(planGroup.getServicearea().stream().map(serviceAreaDTO -> serviceAreaDTO.getId().intValue()).collect(Collectors.toList()));
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = IterableUtils.toList(branchServiceAreaMappingRepository.findAll(qBranchServiceAreaMapping.serviceareaId.in(planGroup.getServicearea().stream().map(serviceAreaDTO -> serviceAreaDTO.getId().intValue()).collect(Collectors.toList()))));
                                // List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(planGroup.getServicearea().stream().map(serviceArea -> serviceArea.getId().intValue()).collect(Collectors.toList()));
                                List<Long> branchServiceAreaMappingIds = branchServiceAreaMappings.stream()
                                        .map(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().longValue())
                                        .collect(Collectors.toList());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && region != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition  = region.getBranchidList().stream()
                                                        .map(Branch::getId)
                                                        .anyMatch(branchServiceAreaMappingIds::contains);
                                                break;
                                            }
                                            case "!=": {
                                                condition = !region.getBranchidList().stream()
                                                        .map(Branch::getId)
                                                        .anyMatch(branchServiceAreaMappingIds::contains);
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PLAN_GROUP_CONDITION.BUSINESS_VERTICAL: {
                                QBranchServiceAreaMapping qBranchServiceAreaMapping = QBranchServiceAreaMapping.branchServiceAreaMapping;
                                qBranchServiceAreaMapping.serviceareaId.in(planGroup.getServicearea().stream().map(serviceAreaDTO -> serviceAreaDTO.getId().intValue()).collect(Collectors.toList()));
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = IterableUtils.toList(branchServiceAreaMappingRepository.findAll(qBranchServiceAreaMapping.serviceareaId.in(planGroup.getServicearea().stream().map(serviceAreaDTO -> serviceAreaDTO.getId().intValue()).collect(Collectors.toList()))));
                                // List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(planGroup.getServicearea().stream().map(serviceArea -> serviceArea.getId().intValue()).collect(Collectors.toList()));
                                List<Long> branchServiceAreaMappingIds = branchServiceAreaMappings.stream()
                                        .map(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().longValue())
                                        .collect(Collectors.toList());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                    condition = businessVerticals.getBuregionidList().stream()
                                                            .anyMatch(region -> region.getBranchidList().stream()
                                                                    .map(Branch::getId)
                                                                    .anyMatch(branchServiceAreaMappingIds::contains));
                                                    break;
                                            }
                                            case "!=": {
                                                for (Region region : businessVerticals.getBuregionidList()) {
                                                    condition = !branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PLAN_CONDITION.PLAN_GROUP: {
                                if (planGroup.getPlanGroupType() != null) {
                                    //String planGroup = plan;
                                    if (planGroup != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
//                            case CommonConstants.PLAN_GROUP_CONDITION.PLAN_GROUP: {
//                                if (customers.getPlangroupid() != null) {
//                                    if (planGroup != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = planGroup.getPlanGroupName().equals(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !planGroup.getPlanGroupName().equals(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.PLAN_GROUP_CONDITION.DISCOUNT: {
//                                for (CustPlanMapppingPojo custPlanMapppingPojo : customers.getPlanMappingList()) {
//                                    if (custPlanMapppingPojo.getDiscount() != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = custPlanMapppingPojo.getDiscount() == Double.parseDouble(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case ">":
//                                                condition = custPlanMapppingPojo.getDiscount() > Double.parseDouble(queryFieldMapping.getQueryValue());
//                                                break;
//
//                                            case "<":
//                                                condition = custPlanMapppingPojo.getDiscount() < Double.parseDouble(queryFieldMapping.getQueryValue());
//                                                break;
//
//                                            case "<=":
//                                                condition = custPlanMapppingPojo.getDiscount() <= Double.parseDouble(queryFieldMapping.getQueryValue());
//                                                break;
//
//                                            case ">=":
//                                                condition = custPlanMapppingPojo.getDiscount() >= Double.parseDouble(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = custPlanMapppingPojo.getDiscount() != Double.parseDouble(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.PLAN_GROUP_CONDITION.PARTNER_NAME: {
//                                Partner partner = partnerRepository.findById(customers.getPartnerid()).orElse(null);
//                                if (partner != null) switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = partner.getName().equals(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !partner.getName().equals(queryFieldMapping.getQueryValue());
//                                        break;
//
//
//                                }
//                                break;
//                            }
                            case CommonConstants.PLAN_GROUP_CONDITION.CURRENT_TEAM_ASSIGNED: {
                                if (planGroup.getNextTeamHierarchyMappingId() != null) {
                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(planGroup.getNextTeamHierarchyMappingId()).orElse(null);
                                    if (teamHierarchyMapping != null) {
                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }


                                        }
                                    }
                                }
                                break;
                            }
//Plan Group condtion for discount
                            case CommonConstants.PLAN_GROUP_CONDITION.DISCOUNT: {
                                String allowDiscount = null;
                                Boolean data = planGroup.isAllowDiscount();

                                if (data == true) {
                                    allowDiscount = "true";
                                } else {
                                    allowDiscount = "false";
                                }
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = allowDiscount.equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    }
                                    break;
                                    case "!=": {
                                        condition = !allowDiscount.equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    }
                                    break;
                                }
                                break;
                            }
                            case CommonConstants.PLAN_GROUP_CONDITION.PLAN_MODE: {
                                for (PlanGroupMapping planGroupMapping : planGroup.getPlanMappingList()) {
                                    if (planGroupMapping.getPlan() != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = planGroupMapping.getPlan().getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !planGroupMapping.getPlan().getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.PLAN_GROUP_CONDITION.BU: {
                                if (planGroup.getBuId() != null) {
                                    BusinessUnit businessUnit = businessUnitRepository.findById(planGroup.getBuId()).orElse(null);
                                    if (businessUnit != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = businessUnit.getBuname().equals(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !businessUnit.getBuname().equals(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                }
                                break;
                            }

//                            case CommonConstants.PLAN_GROUP_CONDITION.TEAM_ASSIGNED_NEW: {
//                                if (customers.getNextTeamHierarchyMapping() != null) {
//                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(customers.getNextTeamHierarchyMapping()).orElse(null);
//                                    if (teamHierarchyMapping != null) {
//                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
//                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = teams.getName().equals(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !teams.getName().equals(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//                                    }
//                                }
//                                break;
//                            }


                        }

                        queryInit.append(condition);
                        if (queryFieldMapping.getQueryCondition().equals("and")) {
                            queryInit.append("&&");
                        } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                            queryInit.append("||");
                        }
//                    map.put(queryFieldMapping.getQueryCondition()==null? String.valueOf(j) :queryFieldMapping.getQueryCondition()"##"j,condition);
//                    j;
                    }
                    try {
                        return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                    } catch (ScriptException e) {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                    }
                }
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION: {
                if (entity instanceof CustomerAddressPojo) {
                    CustomerAddressPojo customerAddressPojo = (CustomerAddressPojo) entity;
//                    CustomersPojo customers = (CustomersPojo) entity;
                    Customers customers = customersService.getById(customerAddressPojo.getCustomerId());
                    Partner partner = partnerRepository.findById(customers.getPartner().getId()).orElse(null);
                    for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                        switch (queryFieldMapping.getQueryField()) {
                            case CommonConstants.SHIFT_LOCATION_CONDITION.PLAN_PURCHASE_TYPE: {
                                if (customers.getPlanPurchaseType() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = customers.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !customers.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                    }

                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.PLAN_MODE: {
                                for (CustPlanMappping custPlanMappping : customers.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.SERVICE_AREA: {
                                ServiceArea serviceArea = serviceAreaService.getByID(customers.getServicearea().getId());
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.CALENDAR_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = customers.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !customers.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }

                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.CATEGORY: {
                                if (customers.getDunningCategory() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = customers.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !customers.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }

                                    }

                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.PARTNER_NAME: {
                                if (partner != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = partner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !partner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }


                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.PARTNER_EMAIL: {
                                if (partner != null && partner.getEmail() != null)
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = partner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !partner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                    }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.AREA: {
                                Area area = areaService.getById(customerAddressPojo.getAreaId().longValue());
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.PINCODE: {
                                String pincode = pincodeService.getPincode(customerAddressPojo.getPincodeId().longValue());
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = pincode.equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !pincode.equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.CITY: {
                                City city = cityService.get(customerAddressPojo.getCityId(),customers.getMvnoId());
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.STATE: {
                                State state = stateService.get(customerAddressPojo.getStateId(),customers.getMvnoId());
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.BILL_TO: {
                                for (CustPlanMappping custPlanMappping : customers.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = custPlanMappping.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !custPlanMappping.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }


                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.INVOICE_TO_ORG: {
                                for (CustPlanMappping custPlanMappping : customers.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = custPlanMappping.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !custPlanMappping.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }

                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.PARENT_CUSTOMER_USERNAME: {
                                if (customers.getParentCustomers() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = customers.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !customers.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                    }

                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.USERNAME: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = customers.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !customers.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }


                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.PLAN_SERVICES: {
                                for (CustPlanMappping custPlanMappping : customers.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = custPlanMappping.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !custPlanMappping.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }


                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.CURRENT_TEAM_ASSIGNED: {
                                if (customerAddressPojo.getNextTeamHierarchyMappingId() != null) {
                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(customerAddressPojo.getNextTeamHierarchyMappingId()).orElse(null);
                                    if (teamHierarchyMapping != null) {
                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.BRANCH: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customers.getServicearea().getId());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                    if (branchServiceAreaMappings.size() > 0 && branch != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
                                                break;
                                            }
                                            case "!=": {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.REGION: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customers.getServicearea().getId());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                    if (branchServiceAreaMappings.size() > 0 && region != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                break;
                                            }
                                            case "!=": {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.BUSINESS_VERTICAL: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customers.getServicearea().getId());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                    if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                for (Region region : businessVerticals.getBuregionidList()) {
                                                    condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                    break;
                                                }
                                            }
                                            case "!=": {
                                                for (Region region : businessVerticals.getBuregionidList()) {
                                                    condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.PLAN_GROUP: {
//                                if (customers.getPlangroup() != null) {
//                                    PlanGroup planGroup = customers.getPlangroup();
//                                    if (planGroup != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
                                for (CustPlanMappping custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    if (custPlanMapppingPojo.getPlanId() != null) {
//                                        PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                        PlanGroup planGroup=planGroupRepository.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                        if (planGroup != null) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==": {
                                                    condition = planGroup.getPlanGroupName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                                case "!=": {
                                                    condition = !planGroup.getPlanGroupName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.PLAN_CATEGORY: {
                                for (CustPlanMappping custPlanMappping : customers.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.FEASIBILITY_REQUIRED: {
                                if (customers.getFeasibility() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = customers.getFeasibility().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                        case "!=":
                                            condition = !customers.getFeasibility().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }

                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.OLD_SERVICE_AREA: {
                                ServiceArea serviceArea = serviceAreaService.getByID(customers.getServicearea().getId());
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = serviceArea.getName().equals(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !serviceArea.getName().equals(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.BU: {
                                if (customers.getBuId() != null) {
                                    BusinessUnit businessUnit = businessUnitRepository.findById(customers.getBuId()).orElse(null);
                                    if (businessUnit != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                    break;
                                }
                                break;
                            }
                            case CommonConstants.SHIFT_LOCATION_CONDITION.CHARGE:{
                               ShiftLocation shiftLocation=shiftLocationRepository.findLatestByCustomerId(customers.getId());
                               if(shiftLocation!=null){
                                   if (shiftLocation.getAmount() != null) {
                                       switch (queryFieldMapping.getQueryOperator()) {
                                           case "==":
                                               condition = shiftLocation.getAmount() == Double.parseDouble(queryFieldMapping.getQueryValue());
                                               break;
                                           case ">":
                                               condition = shiftLocation.getAmount() > Double.parseDouble(queryFieldMapping.getQueryValue());
                                               break;
                                           case "<":
                                               condition = shiftLocation.getAmount() < Double.parseDouble(queryFieldMapping.getQueryValue());
                                               break;
                                           case "<=":
                                               condition = shiftLocation.getAmount() <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                               break;
                                           case ">=":
                                               condition = shiftLocation.getAmount() >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                               break;
                                           case "!=":
                                               condition = shiftLocation.getAmount() != Double.parseDouble(queryFieldMapping.getQueryValue());
                                               break;
                                       }
                                   }
                               }
                                break;

                            }
//                            case CommonConstants.SHIFT_LOCATION_CONDITION.TEAM_ASSIGNED_NEW: {
//                                if (customers.getNextTeamHierarchyMapping() != null) {
//                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(customers.getNextTeamHierarchyMapping()).orElse(null);
//                                    if (teamHierarchyMapping != null) {
//                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
//                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = teams.getName().equals(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !teams.getName().equals(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//                                    }
//                                }
//                                break;
//                            }
                        }
                        queryInit.append(condition);
                        if (queryFieldMapping.getQueryCondition().equals("and")) {
                            queryInit.append("&&");
                        } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                            queryInit.append("||");
                        }
                    }
                    // break;
                    try {
                        return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                    } catch (ScriptException e) {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                    }
                }
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT: {
                if (entity instanceof CustomerServiceMapping) {
                    CustomerServiceMapping customerServiceMapping = (CustomerServiceMapping) entity;
                    Customers customers = customersRepository.findById(customerServiceMapping.getCustId()).get();
                    List<CustPlanMappping>custPlanMapppingList=custPlanMapppingRepository.findAllByCustomerId(customers.getId());
                    Partner partnerForCustPlanMapping = customers.getPartner();
                    CustomerAddress customerAddress = customerAddressService.findByAddressTypeAndCustomerId("Present", customers.getId());
                    for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                        switch (queryFieldMapping.getQueryField()) {
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.DISCOUNT: {
//                                if (customerServiceMapping.getDiscount() != null) {
//                                    Double discount = Double.valueOf(df.format(customerServiceMapping.getDiscount()));
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = discount == Double.parseDouble(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case ">":
//                                            condition = discount > Double.parseDouble(queryFieldMapping.getQueryValue());
//                                            break;
//
//                                        case "<":
//                                            condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
//                                            break;
//
//                                        case "<=":
//                                            condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
//                                            break;
//
//                                        case ">=":
//                                            condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = discount != Double.parseDouble(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//                                }
//
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.OFFER_PRICE: {
//                                if (customerServiceMapping.getOfferPrice() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                            condition = customerServiceMapping.getOfferPrice() == Double.parseDouble(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case ">":
//                                            condition = customerServiceMapping.getOfferPrice() > Double.parseDouble(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "<":
//                                            condition = customerServiceMapping.getOfferPrice() < Double.parseDouble(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "<=":
//                                            condition = customerServiceMapping.getOfferPrice() <= Double.parseDouble(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case ">=":
//                                            condition = customerServiceMapping.getOfferPrice() >= Double.parseDouble(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                            condition = customerServiceMapping.getOfferPrice() != Double.parseDouble(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//
//
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.PLAN_PURCHASE_TYPE: {
//                                if (customers != null) {
//                                    if (customers.getPlanPurchaseType() != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = customers.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !customers.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//                                    }
//                                    break;
//                                }
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.PLAN_MODE: {
//                                if (customerServiceMapping.getPostpaidPlan() != null) {
//                                    if (customerServiceMapping.getPostpaidPlan().getMode() != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = customerServiceMapping.getPostpaidPlan().getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !customerServiceMapping.getPostpaidPlan().getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//                                    }
//                                    break;
//                                }
//                            }
                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.SERVICE_AREA: {
                                if (customers != null) {
                                    if (customers.getServicearea() != null) {
                                        ServiceArea serviceArea = customers.getServicearea();
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }

                                    }
                                    break;
                                }
                            }
                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.PLAN_CATEGORY: {
                                    if (customers != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = customers.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !customers.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }

                                break;
                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.CALENDAR_TYPE: {
//                                if (customers != null) {
//                                    if (customers.getCalendarType() != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = customers.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !customers.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//                                    }
//                                }
//                                break;
//                            }
                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.CUSTOMER_CATEGORY: {
                                if (customers != null) {
                                    if (customers.getDunningCategory() != null) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==": {
                                                    condition = customers.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                                case "!=": {
                                                    condition = !customers.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }

                                            }
                                    }
                                    break;
                                }
                            }
                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.PARTNER_NAME: {
                                if (partnerForCustPlanMapping != null) {
                                    if (partnerForCustPlanMapping.getName() != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = partnerForCustPlanMapping.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !partnerForCustPlanMapping.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }


                                        }
                                    }
                                    break;
                                }
                            }
                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.PARTNER_EMAIL: {
                                if (partnerForCustPlanMapping != null) {
                                    if (partnerForCustPlanMapping.getEmail() != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = partnerForCustPlanMapping.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !partnerForCustPlanMapping.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }


                                        }
                                    }
                                    break;
                                }
                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.AREA: {
//                                if (customerAddress != null) {
//                                    if (customerAddress.getArea() != null) {
//                                        Area area = customerAddress.getArea();
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//
//                                    }
//                                    break;
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.PINCODE: {
//                                if (customerAddress != null) {
//                                    if (customerAddress.getPincode() != null) {
//                                        Pincode pincode = customerAddress.getPincode();
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//
//                                    }
//                                    break;
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.CITY: {
//                                if (customerAddress != null) {
//                                    if (customerAddress.getPincode() != null) {
//                                        City city = customerAddress.getCity();
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//
//                                    }
//                                    break;
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.STATE: {
//                                if (customerAddress != null) {
//                                    if (customerAddress.getState() != null) {
//                                        State state = customerAddress.getState();
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//
//                                    }
//                                    break;
//                                }
//                                break;
//
//                            }
                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.BILL_TO: {
                                if (custPlanMapppingList != null) {
                                    for (CustPlanMappping mappping : custPlanMapppingList) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = mappping.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !mappping.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.INVOICE_TO_ORG: {
                                if (custPlanMapppingList != null) {
                                    for (CustPlanMappping mappping : custPlanMapppingList) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = mappping.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !mappping.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());;
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.PARENT_CUSTOMER_USERNAME: {
                                if (customers != null) {
                                    if (customers.getParentCustomers() != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = customers.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !customers.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.USERNAME: {
//                                if (customers != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==": {
//                                            condition = customers.getUsername().equals(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//                                        case "!=": {
//                                            condition = !customers.getUsername().equals(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//                                    }
//
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.PLAN_SERVICE: {
//                                if (customerServiceMapping.getService() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==": {
//                                            condition = customerServiceMapping.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//                                        case "!=": {
//                                            condition = !customerServiceMapping.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//
//
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.CURRENT_TEAM_ASSIGNED: {
//                                if (customerServiceMapping.getNextTeamHierarchyMappingId() != null) {
//                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(customerServiceMapping.getNextTeamHierarchyMappingId()).orElse(null);
//                                    if (teamHierarchyMapping != null) {
//                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
//                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.LEAD_SOURCE: {
//                                if (customers != null) {
//                                    if (customers.getLeadSource() != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = customers.getLeadSource().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//
//                                            case "!=":
//                                                condition = !customers.getLeadSource().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//
//                                        }
//                                    }
//                                    break;
//                                }
//                            }
                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.FEASIBILITY_REQUIRED: {
                                if (customers != null) {
                                    if (customers.getFeasibility() != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = customers.getFeasibility().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }

                                            case "!=": {
                                                condition = !customers.getFeasibility().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }

                                        }

                                    }
                                    break;
                                }
                                break;
                            }
                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.OLD_DISCOUNT: {
                                if (customerServiceMapping.getDiscount() != null) {
                                    Double discount = Double.valueOf(df.format(customerServiceMapping.getDiscount()));
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = discount == Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = discount != Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case ">=": {
                                            condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "<=": {
                                            condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case ">": {
                                            condition = discount > Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "<": {
                                            condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.NEW_DISCOUNT: {
                                if (customerServiceMapping.getNewDiscount() != null) {
                                    Double discount = Double.valueOf(df.format(customerServiceMapping.getNewDiscount()));
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = discount == Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = discount != Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case ">=": {
                                            condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "<=": {
                                            condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case ">": {
                                            condition = discount > Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "<": {
                                            condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        }

                                    }


                                }
                                break;
                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.BRANCH: {
//                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(custPlanMappping.getCustomer().getServicearea().getId());
//                                if (queryFieldMapping.getQueryOperator() != null) {
//                                    Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
//                                    if (branchServiceAreaMappings.size() > 0 && branch != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.REGION: {
//                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(custPlanMappping.getCustomer().getServicearea().getId());
//                                if (queryFieldMapping.getQueryOperator() != null) {
//                                    Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
//                                    if (branchServiceAreaMappings.size() > 0 && region != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.BUSINESS_VERTICAL: {
//                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(custPlanMappping.getCustomer().getServicearea().getId());
//                                if (queryFieldMapping.getQueryOperator() != null) {
//                                    BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
//                                    if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                for (Region region : businessVerticals.getBuregionidList()) {
//                                                    condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                                    break;
//                                                }
//                                            }
//                                            case "!=": {
//                                                for (Region region : businessVerticals.getBuregionidList()) {
//                                                    condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.PLAN_GROUP: {
//                                if (customerServiceMapping.getPlanGroup() != null) {
//                                    PlanGroup planGroup = customerServiceMapping.getPlanGroup();
//                                    if (planGroup != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.PLAN_CATEGORY: {
//                                PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(customerServiceMapping.getPlanId()).orElse(null);
//                                if (postpaidPlan != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==": {
//                                            condition = postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//                                        case "!=": {
//                                            condition = !postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//                                    }
//                                }
//
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.BU: {
//                                if (customers.getBuId() != null) {
//                                    BusinessUnit businessUnit = businessUnitRepository.findById(customers.getBuId()).orElse(null);
//                                    if (businessUnit != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//                                    }
//                                    break;
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.PLAN_TYPE: {
//                                List<CustPlanMappping> custPlanMapppings = custPlanMapppingRepository.findAllByCustServiceMappingId(customerServiceMapping.getId()).stream().filter(item -> !item.getPostpaidPlan().getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_VOLUME_BOOSTER) && !item.getPostpaidPlan().getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_BANDWIDTH_BOOSTER) && !item.getPostpaidPlan().getPlanGroup().equalsIgnoreCase(CommonConstants.PLAN_GROUP_DTV_ADDON)).collect(Collectors.toList());
//                                CustPlanMappping custPlanMappping = null;
//                                if (custPlanMapppings.size() == 1){
//                                    custPlanMappping = custPlanMapppings.get(0);
//                                }
//                                if (custPlanMapppings != null)
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==":
//                                        condition = custPlanMappping.getPostpaidPlan().getPlantype().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        case "!=":
//                                        condition = !custPlanMappping.getPostpaidPlan().getPlantype().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                    }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.PLAN_TYPE: {
//                                PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(customerServiceMapping.getPlanId()).orElse(null);
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = postpaidPlan.getPlantype().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !postpaidPlan.getPlantype().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//                                break;
//                            }
//                            case CommonConstants.CHANGE_DISCOUNT_CONDITION.TEAM_ASSIGNED_NEW: {
//                                if (customers.getNextTeamHierarchyMapping() != null) {
//                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(customers.getNextTeamHierarchyMapping()).orElse(null);
//                                    if (teamHierarchyMapping != null) {
//                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
//                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = teams.getName().equals(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !teams.getName().equals(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//                                    }
//                                }
//                                break;
//                            }

                        }
                        queryInit.append(condition);
                        if (queryFieldMapping.getQueryCondition().equals("and")) {
                            queryInit.append("&&");
                        } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                            queryInit.append("||");
                        }
                    }
                    try {
                        return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                    } catch (ScriptException e) {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                    }
                }
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.LEAD: {
                if (entity instanceof LeadMgmtWfDTO) {
                    LeadMgmtWfDTO leadMgmtWfDTO = (LeadMgmtWfDTO) entity;
                    LeadMaster leadMaster = leadMasterRepository.findById(leadMgmtWfDTO.getId()).orElse(null);
//                    LeadMaster leadMaster1 = leadMasterRepository.findById(Long.valueOf(leadMgmtWfDTO.getCreatedBy())).orElse(null);
                //    TeamUserMapping teamUserMapping=new TeamUserMapping();
                    List<Teams> teams1 =new ArrayList<>();
                    List <Long> teamUserMapping1=teamUserMappingsRepocitory.teamIds(Long.valueOf(leadMaster.getCreatedBy()));
                    if (teamUserMapping1 != null) {
                      teams1 = teamsRepository.findAllByIdIn(teamUserMapping1);
                    }
                    LeadCustomerAddress leadCustomerAddress = null;
                    Partner partner = null;
                    if (leadMaster != null) {
                        if (leadMaster.getPartnerid() != null) {
                            partner = partnerRepository.findById(leadMaster.getPartnerid()).orElse(null);
                        }
                        List<LeadCustomerAddress> leadCustomerAddresses = leadCustomerAddressRepository.findByAddressTypeAndLeadMasterId("Present", leadMaster.getId());
                        if (leadCustomerAddresses.size() > 0) {

                            leadCustomerAddress = leadCustomerAddresses.get(0);
                        }
                    }
                    for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                        switch (queryFieldMapping.getQueryField()) {
                            case CommonConstants.LEAD_CONDITION.DISCOUNT: {
                                if (leadMaster.getPlanMappingList() != null && leadMaster.getPlanMappingList().size() > 0) {
                                    for (LeadCustPlanMappping leadCustPlanMappping : leadMaster.getPlanMappingList()) {
                                        if (leadCustPlanMappping.getDiscount() != null) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==":
                                                    condition = leadCustPlanMappping.getDiscount() == Double.parseDouble(queryFieldMapping.getQueryValue());
                                                    break;
                                                case ">":
                                                    condition = leadCustPlanMappping.getDiscount() > Double.parseDouble(queryFieldMapping.getQueryValue());
                                                    break;

                                                case "<":
                                                    condition = leadCustPlanMappping.getDiscount() < Double.parseDouble(queryFieldMapping.getQueryValue());
                                                    break;

                                                case "<=":
                                                    condition = leadCustPlanMappping.getDiscount() <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                    break;

                                                case ">=":
                                                    condition = leadCustPlanMappping.getDiscount() >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                    break;
                                                case "!=":
                                                    condition = leadCustPlanMappping.getDiscount() != Double.parseDouble(queryFieldMapping.getQueryValue());
                                                    break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                                case CommonConstants.LEAD_CONDITION.PLAN_PURCHASE_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        if (queryFieldMapping.getQueryValue().equalsIgnoreCase("Plan Group") && leadMaster.getPlangroupid() != null) {
                                            condition = true;
                                        } else {
                                            if ( queryFieldMapping.getQueryValue().equalsIgnoreCase("individual") && leadMaster.getPlangroupid() == null) {
                                                condition = true;
                                            }
                                        }
                                        break;
                                    case "!=":
                                        if (queryFieldMapping.getQueryValue().equalsIgnoreCase("Plan Group") && leadMaster.getPlangroupid() != null) {
                                            condition = false;
                                        } else {
                                            if ( queryFieldMapping.getQueryValue().equalsIgnoreCase("individual") && leadMaster.getPlangroupid() == null) {
                                                condition = false;
                                            }
                                        }
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.PLAN_MODE: {
                                if (leadMaster.getPlanMappingList() != null && leadMaster.getPlanMappingList().size() > 0) {
                                    for (LeadCustPlanMappping leadCustPlanMappping : leadMaster.getPlanMappingList()) {
                                        PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(leadCustPlanMappping.getPlanId()).orElse(null);
                                        if (postpaidPlan != null) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==": {
                                                    condition = postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                                case "!=": {
                                                    condition = !postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
//                                if (leadMaster.getPlanType() != null) {
//                                    switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==": {
//                                            condition = leadMaster.getPlanType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//                                        case "!=": {
//                                            condition = !leadMaster.getPlanType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//                                    }
//                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.SERVICE_AREA: {
                                if (leadMaster.getServiceareaid() != null) {
                                    ServiceArea serviceArea = serviceAreaService.getByID(leadMaster.getServiceareaid());
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.CALENDAR_TYPE: {
                                if (leadMaster.getCalendarType() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = leadMaster.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !leadMaster.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.CATEGORY: {
                                if (leadMaster.getLeadCategory() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = leadMaster.getLeadCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !leadMaster.getLeadCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }

                                    }

                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.PARTNER_NAME: {
                                if (partner != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = partner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !partner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.PARTNER_EMAIL: {
                                List<Partner> partnerList=partnerRepository.findAllByEmailAndIsDeleteIsFalseOrderByIdDesc(queryFieldMapping.getQueryValue());
                                if (partnerList.size()>0) {
                                    for (Partner partner1 : partnerList) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = partner1.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !partner1.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }

                                    }
                                }
                                break;
                            }
                            /*case CommonConstants.LEAD_CONDITION.AREA: {
                                if (leadCustomerAddress != null) {
                                    Area area = areaService.getById(leadCustomerAddress.getAreaId().longValue());
                                    if (area != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }*/
                            case CommonConstants.LEAD_CONDITION.PINCODE: {
                                if (leadCustomerAddress != null) {
                                    Pincode pincode = pincodeService.getRepository().findById(leadCustomerAddress.getPincodeId().longValue()).orElse(null);
                                    if (pincode != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.CITY: {
                                if (leadCustomerAddress != null) {
                                    City city = cityService.getRepository().findById(leadCustomerAddress.getCityId()).orElse(null);
                                    if (city != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.STATE: {
                                if (leadCustomerAddress != null) {
                                    State state = stateRepository.findById(leadCustomerAddress.getStateId()).orElse(null);
                                    if (state != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.BILL_TO: {
                                if (leadMaster.getPlanMappingList() != null && leadMaster.getPlanMappingList().size() > 0) {
                                    for (LeadCustPlanMappping custPlanMapppingPojo : leadMaster.getPlanMappingList()) {
                                        if (custPlanMapppingPojo.getBillTo() != null) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==": {
                                                    condition = custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                                case "!=": {
                                                    condition = !custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.INVOICE_TO_ORG: {
                                if (leadMaster.getPlanMappingList() != null && leadMaster.getPlanMappingList().size() > 0) {
                                    for (LeadCustPlanMappping leadCustPlanMappping : leadMaster.getPlanMappingList()) {
                                        if (leadCustPlanMappping.getIsInvoiceToOrg().toString() != null) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==": {
                                                    condition = leadCustPlanMappping.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                                case "!=": {
                                                    condition = !leadCustPlanMappping.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }


                                            }
                                        }
                                    }
                                }
                                break;
                            }
//                            case CommonConstants.LEAD_CONDITION.PARENT_CUSTOMER_USERNAME: {
//                                if (leadMaster.getParentCustomerId() != null) {
//                                    Optional<Customers> customers = customersRepository.findById(leadMaster.getParentCustomerId());
//                                    if (customers.isPresent()){
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = customers.get().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !customers.get().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//
//                                        }
//                                    }
//                                }
//                                break;
//                            }
                            case CommonConstants.LEAD_CONDITION.USERNAME: {
                                if (leadMaster.getUsername() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = leadMaster.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !leadMaster.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.PLAN_SERVICES: {
                                if (leadMaster.getPlanMappingList() != null) {
                                    for (LeadCustPlanMappping custPlanMapppingPojo : leadMaster.getPlanMappingList()) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.CURRENT_TEAM_ASSIGNED: {
                                if (leadMaster.getNextTeamMappingId() != null) {
                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(leadMaster.getNextTeamMappingId()).orElse(null);
                                    if (teamHierarchyMapping != null) {
                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }

                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.LEAD_SOURCE: {
                                if (leadMaster.getLeadSource() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = leadMaster.getLeadSource().getLeadSourceName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }

                                        case "!=": {
                                            condition = !leadMaster.getLeadSource().getLeadSourceName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }

                                    }

                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.FEASIBILITY_REQUIRED: {
                                if (leadMaster.getFeasibilityRequired() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = leadMaster.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                        case "!=":
                                            condition = !leadMaster.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }
                                    break;
                                }
                            }
                            case CommonConstants.LEAD_CONDITION.BRANCH: {
                                if (leadMaster.getServiceareaid() != null) {
                                    List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(leadMaster.getServiceareaid());
                                    if (queryFieldMapping.getQueryOperator() != null) {
                                        Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                        if (branchServiceAreaMappings.size() > 0 && branch != null) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==": {
                                                    condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
                                                    break;
                                                }
                                                case "!=": {
                                                    condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.REGION: {
                                if (leadMaster.getServiceareaid() != null) {
                                    List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(leadMaster.getServiceareaid());
                                    if (queryFieldMapping.getQueryOperator() != null) {
                                        Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                        if (branchServiceAreaMappings.size() > 0 && region != null) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==": {
                                                    condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                    break;
                                                }
                                                case "!=": {
                                                    condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.BUSINESS_VERTICAL: {
                                if (leadMaster.getServiceareaid() != null) {
                                    List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(leadMaster.getServiceareaid());
                                    if (queryFieldMapping.getQueryOperator() != null) {
                                        BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                                        if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==": {
                                                    for (Region region : businessVerticals.getBuregionidList()) {
                                                        condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                        break;
                                                    }
                                                }
                                                case "!=": {
                                                    for (Region region : businessVerticals.getBuregionidList()) {
                                                        condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.PLAN_GROUP: {
//                                if (leadMaster.getPlangroupid() != null) {
                                    PlanGroup planGroup = planGroupRepository.findByName(queryFieldMapping.getQueryValue());
                                if (leadMaster.getPlangroupid() != null && planGroup!=null) {
                                    if (planGroup != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = leadMaster.getPlangroupid().equals(planGroup.getPlanGroupId());
                                                break;
                                            case "!=":
                                                condition = !leadMaster.getPlangroupid().equals(planGroup.getPlanGroupId());
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.PLAN_CATEGORY: {
                                if (leadMaster.getPlanMappingList().size() > 0) {
                                    for (LeadCustPlanMappping custPlanMappping : leadMaster.getPlanMappingList()) {
                                        PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                                        if (postpaidPlan != null) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==": {
                                                    condition = postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                                case "!=": {
                                                    condition = !postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.LEAD_CONDITION.BU: {
                                if (leadMaster.getBuId() != null) {
                                    BusinessUnit businessUnit = businessUnitRepository.findById(leadMaster.getBuId()).orElse(null);
                                    if (businessUnit != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
//                            case CommonConstants.LEAD_CONDITION.TEAM_NAME: {
//                                if (teams1.size() >0) {
//                                    for (Teams  teamId: teams1) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                        case "==": {
//                                            condition = teamId.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        }
//                                            break;
//                                        case "!=": {
//                                            condition = !teamId.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
//                                        }
//                                    }
//                                }
//                                break;
//                            }}
                            case CommonConstants.LEAD_CONDITION.TRIAL: {
                                List<LeadCustPlanMappping>leadCustPlanMapppingList=leadCustPlanMapppingRepository.findByLeadMasterId(leadMaster.getId());
                                //System.out.println("Trial Condition:"+leadMaster.getCustomers().getIstrialplan()+":Condition:"+queryFieldMapping.getQueryValue());
                               for(LeadCustPlanMappping leadCustPlanMappping : leadCustPlanMapppingList) {
                                   switch (queryFieldMapping.getQueryOperator()) {
                                       case "==": {
                                           condition = leadCustPlanMappping.getIsTrialPlan().equals(Boolean.parseBoolean(queryFieldMapping.getQueryValue()));
                                       }
                                       break;
                                       case "!=": {
                                           condition = !leadCustPlanMappping.getIsTrialPlan().equals(Boolean.parseBoolean(queryFieldMapping.getQueryValue()));
                                       }
                                       break;
                                   }
                               }

                               break;

                            }
                            case CommonConstants.LEAD_CONDITION.DEPARTMENT: {
                                if (leadMaster.getLeadDepartment() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = leadMaster.getLeadDepartment().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }

                                        case "!=": {
                                            condition = !leadMaster.getLeadDepartment().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }

                                    }

                                }
                                break;
                            }
//                            case CommonConstants.LEAD_CONDITION.TEAM_ASSIGNED_NEW: {
//                                if (customers.getNextTeamHierarchyMapping() != null) {
//                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(customers.getNextTeamHierarchyMapping()).orElse(null);
//                                    if (teamHierarchyMapping != null) {
//                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
//                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = teams.getName().equals(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !teams.getName().equals(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//                                    }
//                                }
//                                break;
//                            }
                        }
                        queryInit.append(condition);
                        if (queryFieldMapping.getQueryCondition().equals("and")) {
                            queryInit.append("&&");
                        } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                            queryInit.append("||");
                        }
                    }
                    try {
                        return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                    } catch (ScriptException e) {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                    }
                }
            }
//            case CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE: {
//
//                PartnerPaymentDTO partnerPaymentDTO = (PartnerPaymentDTO) entity;
//                Partner partner = partnerRepository.findById(partnerPaymentDTO.getPartnerId()).orElse(null);
//                for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
//                    switch (queryFieldMapping.getQueryField()) {
//                        case CommonConstants.PARTNER_PAYMENT_CONDITION.BALANCE: {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = partner.getBalance() == Double.parseDouble(queryFieldMapping.getQueryValue());
//                                    break;
//                                case ">":
//                                    condition = partner.getBalance() > Double.parseDouble(queryFieldMapping.getQueryValue());
//                                    break;
//
//                                case "<":
//                                    condition = partner.getBalance() < Double.parseDouble(queryFieldMapping.getQueryValue());
//                                    break;
//
//                                case "<=":
//                                    condition = partner.getBalance() <= Double.parseDouble(queryFieldMapping.getQueryValue());
//                                    break;
//
//                                case ">=":
//                                    condition = partner.getBalance() >= Double.parseDouble(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = partner.getBalance() != Double.parseDouble(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                            break;
//
//                        }
//                        case CommonConstants.PARTNER_PAYMENT_CONDITION.PAYMENT_MODE: {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = partnerPaymentDTO.getPaymentmode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !partnerPaymentDTO.getPaymentmode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                            break;
//                        }
//
//                        case CommonConstants.PARTNER_PAYMENT_CONDITION.CREDIT_LIMIT: {
//                            //Partner partner=partnerRepository.findById(partnerPaymentDTO.getPartnerId()).orElse(null);
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = partner.getCredit() == Double.parseDouble(queryFieldMapping.getQueryValue());
//                                    break;
//                                case ">":
//                                    condition = partner.getCredit() > Double.parseDouble(queryFieldMapping.getQueryValue());
//                                    break;
//
//                                case "<":
//                                    condition = partner.getCredit() < Double.parseDouble(queryFieldMapping.getQueryValue());
//                                    break;
//
//                                case "<=":
//                                    condition = partner.getCredit() <= Double.parseDouble(queryFieldMapping.getQueryValue());
//                                    break;
//
//                                case ">=":
//                                    condition = partner.getCredit() >= Double.parseDouble(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = partner.getCredit() != Double.parseDouble(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                            break;
//                        }
//
//                        case CommonConstants.PARTNER_PAYMENT_CONDITION.BALANCE_TRANSFER: {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = partnerPaymentDTO.getTranscategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !partnerPaymentDTO.getTranscategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                            break;
//                        }
//
//
//                    }
//                    queryInit.append(condition);
//                    if (queryFieldMapping.getQueryCondition().equals("and")) {
//                        queryInit.append("&&");
//                    } else if (queryFieldMapping.getQueryCondition().equals("or")) {
//                        queryInit.append("||");
//                    }
//                }
//                try {
//                    return (Boolean) engine.eval(queryInit.toString().toLowerCase());
//                } catch (ScriptException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }


            case CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE: {

                PartnerPaymentDTO partnerPaymentDTO = (PartnerPaymentDTO) entity;
                Partner partner = partnerRepository.findById(partnerPaymentDTO.getPartnerId()).orElse(null);
                for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                    switch (queryFieldMapping.getQueryField()) {
                        case CommonConstants.PARTNER_PAYMENT_CONDITION.BALANCE: {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = partner.getBalance() == Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;
                                case ">":
                                    condition = partner.getBalance() > Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;

                                case "<":
                                    condition = partner.getBalance() < Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;

                                case "<=":
                                    condition = partner.getBalance() <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;

                                case ">=":
                                    condition = partner.getBalance() >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = partner.getBalance() != Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;
                            }
                            break;

                        }
                        case CommonConstants.PARTNER_PAYMENT_CONDITION.PAYMENT_MODE: {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = partnerPaymentDTO.getPaymentmode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !partnerPaymentDTO.getPaymentmode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                            break;
                        }

                        case CommonConstants.PARTNER_PAYMENT_CONDITION.CREDIT_LIMIT: {
                            //Partner partner=partnerRepository.findById(partnerPaymentDTO.getPartnerId()).orElse(null);
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = partnerPaymentDTO.getCredit() == Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;
                                case ">":
                                    condition = partnerPaymentDTO.getCredit() > Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;

                                case "<":
                                    condition = partnerPaymentDTO.getCredit() < Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;

                                case "<=":
                                    condition = partnerPaymentDTO.getCredit() <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;

                                case ">=":
                                    condition = partnerPaymentDTO.getCredit() >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = partnerPaymentDTO.getCredit() != Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;
                            }
                            break;
                        }

                        case CommonConstants.PARTNER_PAYMENT_CONDITION.BALANCE_TRANSFER: {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = partnerPaymentDTO.getAmount() == Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;
                                case ">":
                                    condition = partnerPaymentDTO.getAmount() > Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;

                                case "<":
                                    condition = partnerPaymentDTO.getAmount() < Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;

                                case "<=":
                                    condition = partnerPaymentDTO.getAmount() <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;

                                case ">=":
                                    condition = partnerPaymentDTO.getAmount() >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = partnerPaymentDTO.getAmount() != Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;
                            }
                            break;
                        }
                        //adi

                        case CommonConstants.PARTNER_PAYMENT_CONDITION.WITHDRAWAL: {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = partnerPaymentDTO.getAmount() == Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;
                                case ">":
                                    condition = partnerPaymentDTO.getAmount() > Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;

                                case "<":
                                    condition = partnerPaymentDTO.getAmount() < Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;

                                case "<=":
                                    condition = partnerPaymentDTO.getAmount() <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;

                                case ">=":
                                    condition = partnerPaymentDTO.getAmount() >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = partnerPaymentDTO.getAmount() != Double.parseDouble(queryFieldMapping.getQueryValue());
                                    break;
                            }
                            break;
                        }

                        //adi
                    }
                    queryInit.append(condition);
                    if (queryFieldMapping.getQueryCondition().equals("and")) {
                        queryInit.append("&&");
                    } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                        queryInit.append("||");
                    }
                }
                try {
                    return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                } catch (ScriptException e) {
                    throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                }

            }

            case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD: {
                if (entity instanceof CustomerServiceMapping) {

                    CustomerServiceMapping customerServiceMapping = (CustomerServiceMapping) entity;
                    Customers customers = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);

                    Partner partner = partnerRepository.findById(customers.getPartner().getId()).orElse(null);
                    CustomerAddress customerAddress = customerAddressService.findByAddressTypeAndCustomerId("Present", customers.getId());
                    for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                        switch (queryFieldMapping.getQueryField()) {
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.DISCOUNT: {
                                for (CustPlanMappping custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    if (custPlanMapppingPojo.getDiscount() != null) {
                                        Double discount = Double.valueOf(df.format(custPlanMapppingPojo.getDiscount()));
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = discount == Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case ">":
                                                condition = discount > Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "<":
                                                condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "<=":
                                                condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case ">=":
                                                condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = discount != Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.OFFER_PRICE: {
                                for (CustPlanMappping custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = custPlanMapppingPojo.getOfferPrice() == Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case ">":
                                            condition = custPlanMapppingPojo.getOfferPrice() > Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case "<":
                                            condition = custPlanMapppingPojo.getOfferPrice() < Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case "<=":
                                            condition = custPlanMapppingPojo.getOfferPrice() <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case ">=":
                                            condition = custPlanMapppingPojo.getOfferPrice() >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = custPlanMapppingPojo.getOfferPrice() != Double.parseDouble(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.PLAN_PURCHASE_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = customers.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !customers.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.PLAN_MODE: {
                                for (CustPlanMappping custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                    if (postpaidPlan != null)
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.SERVICE_AREA: {
                                ServiceArea serviceArea = serviceAreaService.getByID(customers.getServicearea().getId());
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.CALENDAR_TYPE: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = customers.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !customers.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.CUSTOMER_CATEGORY: {
                                if (customers.getDunningCategory() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = customers.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !customers.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.PARTNER_NAME: {
                                if (partner != null)
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = partner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !partner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.PARTNER_EMAIL: {
                                if (partner != null && partner.getEmail() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = partner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !partner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.AREA: {
                                Area area = customerAddress.getArea();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.PINCODE: {
                                Pincode pincode = customerAddress.getPincode();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.CITY: {
                                City city = customerAddress.getCity();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.STATE: {
                                State state = customerAddress.getState();
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.BILL_TO: {
                                List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
                                    custPlanMapppingList = customers.getPlanMappingList().stream().sorted(Comparator.comparing(CustPlanMappping::getCreatedate).reversed()).collect(Collectors.toList());
                                    if(custPlanMapppingList.size()>0){
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = custPlanMapppingList.get(0).getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !custPlanMapppingList.get(0).getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.INVOICE_TO_ORG: {
                                for (CustPlanMappping custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.PARENT_CUSTOMER_USERNAME: {
                                if (customers.getParentCustomers() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = customers.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !customers.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.USERNAME: {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = customers.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !customers.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;


                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.PLAN_SERVICE: {
                                for (CustPlanMappping custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        case "!=":
                                            condition = !custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;


                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.CURRENT_TEAM_ASSIGNED:{
                                if (customerServiceMapping.getNextTeamHierarchyMappingId() != null) {
                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(customerServiceMapping.getNextTeamHierarchyMappingId()).orElse(null);
                                    if (teamHierarchyMapping != null) {
                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.TEAM_ASSIGNED_NEW: {
//                                if (customers.getNextTeamHierarchyMapping() != null) {
//                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(customers.getNextTeamHierarchyMapping()).orElse(null);
//                                    if (teamHierarchyMapping != null) {
//                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
//                                        if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//
//
//                                        }
//                                    }
//                                }
//                                break;
//                            }
//                                CustPlanMappping custPlanMappping = paymentCustomer.getPlanMappingList().get(0);
//                                Customers customers = custPlanMappping.getCustomer();
                                if (customerServiceMapping.getNextTeamHierarchyMappingId() != null) {
                                    TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(customerServiceMapping.getNextTeamHierarchyMappingId()).orElse(null);
                                    if (teamHierarchyMapping != null) {
                                        Teams teams = getNextTeamHirMappingByOrderNumberAndWorkflowId(teamHierarchyMapping.getOrderNumber() + 1, teamHierarchyMapping.getHierarchyId());
//                                        Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
                                        if (teams != null) {
                                            switch (queryFieldMapping.getQueryOperator()) {
                                                case "==": {
                                                    condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                                case "!=": {
                                                    condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.LEAD_SOURCE: {
                                if (customers.getLeadSource() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = customers.getLeadSource().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                        case "!=":
                                            condition = !customers.getLeadSource().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }

                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.FEASIBILITY_REQUIRED: {
                                if (customers.getFeasibilityRequired() != null) {
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==":
                                            condition = customers.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                        case "!=":
                                            condition = !customers.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;

                                    }

                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.BRANCH: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customers.getServicearea().getId());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && branch != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == branch.getId().longValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == branch.getId().longValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.REGION: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customers.getServicearea().getId());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && region != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                //condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId().longValue()));
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == region.getId().longValue());

                                                break;
                                            }
                                            case "!=": {
                                                // condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId().longValue()));
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == region.getId().longValue());

                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.BUSINESS_VERTICAL: {
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(customers.getServicearea().getId());
                                if (queryFieldMapping.getQueryOperator() != null) {
                                    BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryValue());
                                    if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
//                                                for (BusinessVerticals businessVerticals1 : businessVerticals.getbu) {
                                                //condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId().longValue()));
                                                condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == businessVerticals.getId().longValue());

                                                break;
                                                // }
                                            }
                                            case "!=": {
                                                //  for (Region region : businessVerticals.getBuregionidList()) {
                                                //condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId().longValue()));
                                                condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId() == businessVerticals.getId().longValue());

                                                break;
                                                // }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.PLAN_GROUP: {
//                                if (customers.getPlangroupid() != null) {
//                                    PlanGroup planGroup = planGroupRepository.findById(customers.getPlangroupid()).orElse(null);
//                                    if (planGroup != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==":
//                                                condition = planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            case "!=":
//                                                condition = !planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                        }
//                                    }
//                                }
                                for (CustPlanMappping custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.PLAN_CATEGORY: {
                                for (CustPlanMappping custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                                    if (postpaidPlan != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==": {
                                                condition = postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                            case "!=": {
                                                condition = !postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.BU: {
                                if (customers.getBuId() != null) {
                                    BusinessUnit businessUnit = businessUnitRepository.findById(customers.getBuId()).orElse(null);
                                    if (businessUnit != null) {
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }
                                    break;
                                }
                                break;
                            }
                            case CommonConstants.CUSTOMER_SERVICE_ADD_CONDITION.OLD_DISCOUNT: {

                                for (CustPlanMappping custPlanMapppingPojo : customers.getPlanMappingList()) {
                                    if (custPlanMapppingPojo.getDiscount() != null) {
                                        Double discount = Double.valueOf(df.format(custPlanMapppingPojo.getDiscount()));
                                        switch (queryFieldMapping.getQueryOperator()) {
                                            case "==":
                                                condition = discount == Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case "!=":
                                                condition = discount != Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                            case ">":
                                                condition = discount > Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;

                                            case "<":
                                                condition = discount < Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;

                                            case "<=":
                                                condition = discount <= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;

                                            case ">=":
                                                condition = discount >= Double.parseDouble(queryFieldMapping.getQueryValue());
                                                break;
                                        }
                                    }

                                }
                                break;
                            }


                        }
                        queryInit.append(condition);
                        if (queryFieldMapping.getQueryCondition().equals("and")) {
                            queryInit.append("&&");
                        } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                            queryInit.append("||");
                        }
                    }
                    try {
                        return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                    } catch (ScriptException e) {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                    }
                }
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION:{
                if(entity instanceof CustomerDocDetailsDTO){
                    CustomerDocDetailsDTO customerDocDetailsDTO = (CustomerDocDetailsDTO) entity;
                    for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                        switch (queryFieldMapping.getQueryField()) {
                            case CommonConstants.CAF_CONDITION.DEPARTMENT: {
                                if (customerDocDetailsDTO.getCustomer().getDepartmentId()!= null) {
                                    Department department = departmentRepository.findById(customerDocDetailsDTO.getCustomer().getDepartmentId()).orElse(null);
                                    condition = true;
                                    switch (queryFieldMapping.getQueryOperator()) {
                                        case "==": {
                                            condition = department.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }
                                        case "!=": {
                                            condition = !department.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                            break;
                                        }

                                    }
                                }
                                break;
                            }
                        }
                        queryInit.append(condition);
                        if (queryFieldMapping.getQueryCondition().equals("and")) {
                            queryInit.append("&&");
                        } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                            queryInit.append("||");
                        }
                    }
                    try {
                        return (Boolean) engine.eval(queryInit.toString().toLowerCase());
                    } catch (ScriptException e) {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
                    }

                }

            }


        }
        return false;

    }
    private Boolean checkTicketCondition(Object entity, List<QueryFieldMapping> queryFieldMappingList, boolean condition, StringBuilder queryInit, ScriptEngine engine) {
        Boolean result=false;
        if (entity instanceof CaseDTO) {
            CaseDTO caseDTO = (CaseDTO) entity;
            Customers ticketCustomer = customersRepository.findById(caseDTO.getCustomersId()).get();
            Partner ticketCustomerPartner = partnerRepository.findById(ticketCustomer.getPartner().getId()).orElse(null);
            CustomerAddress ticketCustomerAddress = customerAddressService.findByAddressTypeAndCustomerId("Present", ticketCustomer.getId());
            for (QueryFieldMapping queryFieldMapping : queryFieldMappingList) {
                switch (queryFieldMapping.getQueryField()) {
//                    case CommonConstants.CASE_CONDITION.PLAN_PURCHASE_TYPE: {
//                        if (ticketCustomer.getPlanPurchaseType() != null) {
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition = ticketCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                                case "!=":
//                                    condition = !ticketCustomer.getPlanPurchaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                    break;
//                            }
//                            break;
//                        }
//                        break;
//
//                    }
                    case CommonConstants.CASE_CONDITION.PLAN_MODE: {
                        for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
                            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMapppingPojo.getPlanId()).orElse(null);
                            if (postpaidPlan != null) switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !postpaidPlan.getMode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.SERVICE_AREA: {
                        ServiceArea serviceArea = serviceAreaService.getByID(ticketCustomer.getServicearea().getId());
                        switch (queryFieldMapping.getQueryOperator()) {
                            case "==":
                                condition = serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                            case "!=":
                                condition = !serviceArea.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.CALENDAR_TYPE: {
                        switch (queryFieldMapping.getQueryOperator()) {
                            case "==":
                                condition = ticketCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                            case "!=":
                                condition = !ticketCustomer.getCalendarType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.CATEGORY: {
                        if (ticketCustomer.getDunningCategory() != null) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;

                            }
                            break;
                        }
                    }
                    case CommonConstants.CASE_CONDITION.PARTNER_NAME: {
                        if (ticketCustomerPartner != null) switch (queryFieldMapping.getQueryOperator()) {
                            case "==":
                                condition = ticketCustomerPartner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                            case "!=":
                                condition = !ticketCustomerPartner.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;


                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.PARTNER_EMAIL: {
                        if (ticketCustomerPartner != null && ticketCustomerPartner.getEmail() != null) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = ticketCustomerPartner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !ticketCustomerPartner.getEmail().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                            break;
                        }
                        break;

                    }
                    case CommonConstants.CASE_CONDITION.AREA: {
                        Area area = ticketCustomerAddress.getArea();
                        switch (queryFieldMapping.getQueryOperator()) {

                            case "==":
                                condition = area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                            case "!=":
                                condition = !area.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.PINCODE: {
                        Pincode pincode = ticketCustomerAddress.getPincode();
                        switch (queryFieldMapping.getQueryOperator()) {
                            case "==":
                                condition = pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                            case "!=":
                                condition = !pincode.getPincode().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.CITY: {
                        City city = ticketCustomerAddress.getCity();
                        switch (queryFieldMapping.getQueryOperator()) {
                            case "==":
                                condition = city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                            case "!=":
                                condition = !city.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.STATE: {
                        State state = ticketCustomerAddress.getState();
                        switch (queryFieldMapping.getQueryOperator()) {
                            case "==":
                                condition = state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                            case "!=":
                                condition = !state.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.BILL_TO: {
                        for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !custPlanMapppingPojo.getBillTo().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;


                            }
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.INVOICE_TO_ORG: {
                        for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !custPlanMapppingPojo.getIsInvoiceToOrg().toString().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;


                            }
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.PARENT_CUSTOMER_USERNAME: {
                        if (ticketCustomer.getParentCustomers() != null) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = ticketCustomer.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !ticketCustomer.getParentCustomers().getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.USERNAME: {
                        switch (queryFieldMapping.getQueryOperator()) {
                            case "==":
                                condition = ticketCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;
                            case "!=":
                                condition = !ticketCustomer.getUsername().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                break;


                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.PLAN_SERVICES: {
                        for (CustPlanMappping custPlanMapppingPojo : ticketCustomer.getPlanMappingList()) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !custPlanMapppingPojo.getService().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;


                            }
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.CURRENT_TEAM_ASSIGNED: {
                        if (caseDTO.getTeamHierarchyMappingId() != null) {
                            TeamHierarchyMapping teamHierarchyMapping = teamHierarchyMappingRepo.findById(Math.toIntExact(caseDTO.getTeamHierarchyMappingId())).orElse(null);
                            if (teamHierarchyMapping != null) {
                                Teams teams = teamsRepository.findById(teamHierarchyMapping.getTeamId().longValue()).orElse(null);
                                if (teams != null) switch (queryFieldMapping.getQueryOperator()) {
                                    case "==":
                                        condition = teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    case "!=":
                                        condition = !teams.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                }

                            }
                        }
                        break;
                    }

                    case CommonConstants.PAYMENT_CONDITION.CUSTOMER_CATEGORY: {
                        if (ticketCustomer.getDunningCategory() != null) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !ticketCustomer.getDunningCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;

                            }
                        }
                        break;
                    }
//                        case CommonConstants.CASE_CONDITION.CURRENT_PARTNER:
//
//                            break;
                    case CommonConstants.CASE_CONDITION.TICKET_CATEGORY: {
                        if (caseDTO.getCaseReasonCategory() != null) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==": {
                                    condition = caseDTO.getCaseReasonCategory().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
                                    break;
                                }
                                case "!=": {
                                    condition = !caseDTO.getCaseReasonCategory().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
                                    break;
                                }

                            }
                        }
                        if(condition==false){
                            return false;
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.TICKET_SUB_CATEGORY: {
                        if (caseDTO.getCaseReasonSubCategory() != null) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = caseDTO.getCaseReasonSubCategory().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
                                    break;
                                case "!=":
                                    condition = !caseDTO.getCaseReasonSubCategory().trim().equalsIgnoreCase(queryFieldMapping.getQueryValue().trim());
                                    break;
                            }

                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.PRIORITY: {
                        if (caseDTO.getPriority() != null) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = caseDTO.getPriority().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !caseDTO.getPriority().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                        }
                        break;

                    }
                    case CommonConstants.CASE_CONDITION.DEPARTMENT: {
                        if (caseDTO.getDepartment() != null) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==": {
                                    condition = caseDTO.getDepartment().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                }
                                case "!=": {
                                    condition = !caseDTO.getDepartment().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.BRANCH: {
                        List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(ticketCustomer.getServicearea().getId());
                        if (queryFieldMapping.getQueryOperator() != null) {
                            Branch branch = branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                            if (branchServiceAreaMappings.size() > 0 && branch != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
                                        break;
                                    }
                                    case "!=": {
                                        condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> branchServiceAreaMapping.getBranchId().equals(branch.getId()));
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.REGION: {
                        List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(ticketCustomer.getServicearea().getId());
                        if (queryFieldMapping.getQueryOperator() != null) {
                            Region region = regionRepository.findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                            if (branchServiceAreaMappings.size() > 0 && region != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                        break;
                                    }
                                    case "!=": {
                                        condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.BUSINESS_VERTICAL: {
                        List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByServiceareaId(ticketCustomer.getServicearea().getId());
                        if (queryFieldMapping.getQueryOperator() != null) {
                            BusinessVerticals businessVerticals = businessVerticalsRepository.findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(queryFieldMapping.getQueryOperator());
                            if (branchServiceAreaMappings.size() > 0 && businessVerticals != null && businessVerticals.getBuregionidList().size() > 0) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        for (Region region : businessVerticals.getBuregionidList()) {
                                            condition = branchServiceAreaMappings.stream().anyMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                            break;
                                        }
                                    }
                                    case "!=": {
                                        for (Region region : businessVerticals.getBuregionidList()) {
                                            condition = branchServiceAreaMappings.stream().noneMatch(branchServiceAreaMapping -> region.getBranchidList().contains(branchServiceAreaMapping.getBranchId()));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.PLAN_GROUP: {
//                                if (ticketCustomer.getPlangroup() != null) {
//                                    PlanGroup planGroup = ticketCustomer.getPlangroup();
//                                    if (planGroup != null) {
//                                        switch (queryFieldMapping.getQueryOperator()) {
//                                            case "==": {
//                                                condition = planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                            case "!=": {
//                                                condition = !planGroup.getPlanGroupType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                break;
                        for (CustPlanMappping custPlanMappping : ticketCustomer.getPlanMappingList()) {
                            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                            if (postpaidPlan != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !postpaidPlan.getPlanGroup().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.PLAN_CATEGORY: {
                        for (CustPlanMappping custPlanMappping : ticketCustomer.getPlanMappingList()) {
                            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                            if (postpaidPlan != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !postpaidPlan.getCategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.FEASIBILITY_REQUIRED: {
                        if (ticketCustomer.getFeasibilityRequired() != null) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==": {

                                    condition = ticketCustomer.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                }

                                case "!=": {
                                    condition = !ticketCustomer.getFeasibilityRequired().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                }

                            }

                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.BU: {
                        if (ticketCustomer.getBuId() != null) {
                            BusinessUnit businessUnit = businessUnitRepository.findById(ticketCustomer.getBuId()).orElse(null);
                            if (businessUnit != null) {
                                switch (queryFieldMapping.getQueryOperator()) {
                                    case "==": {
                                        condition = businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                    case "!=": {
                                        condition = !businessUnit.getBuname().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                        break;
                                    }
                                }
                            }

                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.VALLEY_TYPE: {
                        if (ticketCustomer.getValleyType() != null) {
                            String valleyType = ticketCustomer.getValleyType().replace(" ", "");
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = valleyType.equalsIgnoreCase(queryFieldMapping.getQueryValue().replace(" ", ""));
                                    break;
                                case "!=":
                                    condition = !valleyType.equalsIgnoreCase(queryFieldMapping.getQueryValue().replace(" ", ""));
                                    break;
//                                        case "!=":
//                                            condition = !ticketCustomer.getCustomerArea().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                            break;
                            }
                            break;
                        }
                    }
                    case CommonConstants.CASE_CONDITION.CUSTOMER_AREA: {
                        if (ticketCustomer.getCustomerArea() != null) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = ticketCustomer.getCustomerArea().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !ticketCustomer.getCustomerArea().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                        }
                        break;
                    }
                    case CommonConstants.CASE_CONDITION.CUSTOMER_TYPE: {
                        if (ticketCustomer != null) {
                            switch (queryFieldMapping.getQueryOperator()){
                                case "==":
                                    condition = ticketCustomer.getCustcategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !ticketCustomer.getCustcategory().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;

                            }
                        }

                        break;
                    }
                    case CommonConstants.CASE_CONDITION.TICKET_STATUS: {
                        if (caseDTO.getCaseStatus() != null) {
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = caseDTO.getCaseStatus().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !caseDTO.getCaseStatus().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                        }
                        break;
                    }
//                    case CommonConstants.CASE_CONDITION.TICKET_RAISED_BY_TEAM: {
//                        List<Long> teams=teamsRepository.findAllByStaff(caseDTO.getCreatedById());
//                        if(teams.size()>0){
//                            List<Teams>teamsList=teamsRepository.findAllByIdIn(teams);
//                            for(Teams teams1:teamsList){
//                                switch (queryFieldMapping.getQueryOperator()) {
//                                    case "==":
//                                        condition = teams1.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                    case "!=":
//                                        condition = !teams1.getName().equalsIgnoreCase(queryFieldMapping.getQueryValue());
//                                        break;
//                                }
//                            }
//                        }
//                        break;
//                    }
                    case CommonConstants.CASE_CONDITION.CASE_TYPE: {
                        if(caseDTO.getCaseType()!=null){
                            switch (queryFieldMapping.getQueryOperator()) {
                                case "==":
                                    condition = caseDTO.getCaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                                case "!=":
                                    condition = !caseDTO.getCaseType().equalsIgnoreCase(queryFieldMapping.getQueryValue());
                                    break;
                            }
                        }

                        break;
                    }
//                    case CommonConstants.CASE_CONDITION.TICCKET_CREATED_DURATION: {
//                        if(caseDTO!=null){
//                            DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("dd-MM-yyyy");
//                            String localDate =caseDTO.getCreatedate().toLocalDate().format(formatter);
//                            LocalDate localDate1 = LocalDate.parse(localDate, formatter);
//                            LocalDate localDate2 = LocalDate.parse(queryFieldMapping.getQueryValue(), formatter);
//                            switch (queryFieldMapping.getQueryOperator()) {
//                                case "==":
//                                    condition =localDate1.equals(localDate2);
//                                    break;
//                                case "!=":
//                                    condition = !localDate1.equals(localDate2);
//                                    break;
//                            }
//                        }
//                        break;
//                    }
                }
                queryInit.append(condition);
                if (queryFieldMapping.getQueryCondition().equals("and")) {
                    queryInit.append("&&");
                } else if (queryFieldMapping.getQueryCondition().equals("or")) {
                    queryInit.append("||");
                }
            }
            try {
                result= (Boolean) engine.eval(queryInit.toString().toLowerCase());
                return (Boolean) engine.eval(queryInit.toString().toLowerCase());
            } catch (ScriptException e) {
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Condtion set for assigning to this team is not proper.", null);
            }
        }
        return result;
    }

    public List<StaffUserPojo> assignCAFToStaffFromTeam(List<ServiceArea> serviceAreaList, Long buId, Teams team) {
        List<StaffUser> tempStaffList = new ArrayList<>();
        Set<StaffUserPojo> returnList;
        Set<StaffUser> staffList = new HashSet<>(getActiveStaffUserByTeamId(team.getId(),serviceAreaList));
        staffList = staffList.parallelStream().filter(staffUser -> staffUser.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).collect(Collectors.toSet());
        if (staffList != null && staffList.size() > 0) {
            for (StaffUser staff : staffList) {
                if (!staff.getIsDelete() && staff.getStatus().equalsIgnoreCase("Active")) {
                    if (staff.getServiceAreaNameList() != null && staff.getServiceAreaNameList().size() > 0) {
                        for (ServiceArea serviceArea : staff.getServiceAreaNameList()) {
                            if (serviceAreaList.stream().anyMatch(serviceArea1 -> serviceArea1.getId().equals(serviceArea.getId()))) {
                                if (staff.getBusinessUnitNameList().size() > 0) {
                                    if (buId != null && buId != 0) {
                                        for (BusinessUnit businessUnit : staff.getBusinessUnitNameList()) {
                                            if (buId.equals(businessUnit.getId())) {
                                                if (tempStaffList.stream().noneMatch(staffUser -> Objects.equals(staffUser.getId(), staff.getId()))) {
                                                    tempStaffList.add(staff);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    tempStaffList.add(staff);
                                }
                            }
                        }
                    } else if (staff.getServiceAreaNameList().size() == 0) {
                        if (staff.getBusinessUnitNameList().size() > 0) {
                            if (buId != null && buId != 0) {
                                for (BusinessUnit businessUnit : staff.getBusinessUnitNameList()) {
                                    if (buId == businessUnit.getId().longValue()) {
                                        if (tempStaffList.stream().noneMatch(staffUser -> Objects.equals(staffUser.getId(), staff.getId()))) {
                                            tempStaffList.add(staff);
                                        }
                                    }
                                }
                            }
                        } else {
                            tempStaffList.add(staff);
                        }

                    }
                }
            }
        }
        returnList = tempStaffList.stream().map(staffUser -> staffUserMapper.domainToDTO(staffUser, new CycleAvoidingMappingContext())).collect(Collectors.toSet());
        List<StaffUserPojo> staffUserList = returnList.stream().collect(Collectors.toList());
        return staffUserList;
    }


    public int assignStaffFromList(List<StaffUserPojo> staffList, String eventName, Object entity) {
        int staffId = 0;
        Long count;
        if (staffList.size() > 0) {
            HashMap<Integer, Long> countListMap = new HashMap<>();
            for (StaffUserPojo staffUserTemp : staffList) {
                if (entity instanceof CustomersPojo && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.CAF)) {
                    QCustomers qCustomers = QCustomers.customers;
                    count = customersRepository.count(qCustomers.currentAssigneeId.eq(staffUserTemp.getId()));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof PostpaidPlanPojo && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.PLAN)) {
                    QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
                    qPostpaidPlan.nextStaff.eq(staffUserTemp.getId());
                    count = postpaidPlanRepo.count(qPostpaidPlan.nextStaff.eq(staffUserTemp.getId()));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof CreditDocument && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT)) {
                    QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
                    count = creditDocRepository.count(qCreditDocument.isNotNull().and(qCreditDocument.approverid.eq(staffUserTemp.getId())));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof CustomerInventoryMapping && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN)) {
                    QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                    count = customerInventoryMappingRepo.count(qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.nextApprover.id.eq(staffUserTemp.getId())));
                    countListMap.put(staffUserTemp.getId(), count);
//                } else if (entity instanceof CaseDTO && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.CASE)) {
//                    count = caseService.findMinimumAssignReuqestByStaff(staffUserTemp.getId());
//                    if (count != null) {
//                        countListMap.put(staffUserTemp.getId(), count);
//                    }
               }
                else if (entity instanceof CustomersPojo && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION)) {
                    QCustomerApprove qCustomerApprove = QCustomerApprove.customerApprove;
                    count = customerApproveRepo.count(qCustomerApprove.isNotNull().and(qCustomerApprove.currentStaff.equalsIgnoreCase(staffUserTemp.getUsername())));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof PlanGroup) {
                    QPlanGroup qPlanGroup = QPlanGroup.planGroup;
                    count = planGroupRepository.count(qPlanGroup.isNotNull().and(qPlanGroup.nextStaff.eq(staffUserTemp.getId())));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof LeadMgmtWfDTO) {
                    QLeadMaster qLeadMaster = QLeadMaster.leadMaster;
                    count = leadMasterRepository.count(qLeadMaster.isNotNull().and(qLeadMaster.nextApproveStaffId.eq(staffUserTemp.getId())));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof CustomerServiceMapping) {
                    QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
                    count = customerServiceMappingRepository.count(qCustomerServiceMapping.isNotNull().and(qCustomerServiceMapping.nextStaff.eq(staffUserTemp.getId())));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof OrganizationBillDTO) {
                    QDebitDocument debitDocument = QDebitDocument.debitDocument;
                    count = debitDocRepository.count(debitDocument.nextStaff.eq(staffUserTemp.getId()));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof CustomerAddressPojo) {
                    QCustomerAddress customerAddress = QCustomerAddress.customerAddress;
                    count = customerAddressRepository.count(customerAddress.nextStaff.eq(staffUserTemp.getId()));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof PartnerPaymentDTO && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE)) {
                    QPartnerPayment partnerPayment = QPartnerPayment.partnerPayment;
                    count = partnerPaymentRepository.count(partnerPayment.nextStaff.eq(staffUserTemp.getId()));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof CustomerDocDetailsDTO && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION)) {
                    QCustomerDocDetails customerDocDetails = QCustomerDocDetails.customerDocDetails;
                    count = customerDocDetailsRepository.count(customerDocDetails.nextStaff.eq(staffUserTemp.getId()));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof CreditDocument && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE)) {
                    QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
                    count = creditDocRepository.count(qCreditDocument.isNotNull().and(qCreditDocument.approverid.eq(staffUserTemp.getId())));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof InOutWardMACMapping && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN)) {
                    QInOutWardMACMapping inOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                    count = inOutWardMacRepo.count(inOutWardMACMapping.isNotNull().and(inOutWardMACMapping.currentApproveId.eq(staffUserTemp.getId())));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof CustomerServiceMapping && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION)) {
                    QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
                    count = customerServiceMappingRepository.count(qCustomerServiceMapping.isNotNull().and(qCustomerServiceMapping.nextStaff.eq(staffUserTemp.getId())));
                    countListMap.put(staffUserTemp.getId(), count);
                } else if (entity instanceof CustSpecialPlanRelMapppingPojo && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING)) {
                    QCustSpecialPlanRelMappping qCustSpecialPlanRelMappping = QCustSpecialPlanRelMappping.custSpecialPlanRelMappping;
                    count = custSpecialPlanRelMapppingRepository.count(qCustSpecialPlanRelMappping.isNotNull().and(qCustSpecialPlanRelMappping.nextStaff.eq(staffUserTemp.getId())));
                    countListMap.put(staffUserTemp.getId(), count);

                } else if (entity instanceof LeadQuotationWfDTO && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.LEAD_QUOTATION)) {
                    QLeadQuotationDetails qLeadQuotationDetails = QLeadQuotationDetails.leadQuotationDetails;
                    count = leadQuotationDetailsRepository.count(qLeadQuotationDetails.isNotNull().and(qLeadQuotationDetails.nextApproveStaffId.eq(staffUserTemp.getId())));
                    countListMap.put(staffUserTemp.getId(), count);

                }else if (entity instanceof CustSpecialPlanRelMappping && eventName.equals(CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING)) {
                    QCustSpecialPlanRelMappping qCustSpecialPlanRelMappping = QCustSpecialPlanRelMappping.custSpecialPlanRelMappping;
                    count = custSpecialPlanRelMapppingRepository.count(qCustSpecialPlanRelMappping.isNotNull().and(qCustSpecialPlanRelMappping.nextStaff.eq(staffUserTemp.getId())));
                    countListMap.put(staffUserTemp.getId(), count);

                }
            }
            if (countListMap.values().size() == 0) {
                return staffId;
            } else {
                Long minValueInMap = Collections.min(countListMap.values());
                // This will return min value in the HashMap
                for (Map.Entry<Integer, Long> entry : countListMap.entrySet()) {  // Iterate through HashMap
                    if (Objects.equals(entry.getValue(), minValueInMap)) {
                        staffId = entry.getKey();     // staff id with minimum reuqest
                    }
                }
                if (countListMap.size() > 0 && staffId != 0) {
                    return staffId;
                }
            }
        }

        return staffId;
    }

    public void checkAction(String actionName, String eventName, Object entity) {
        switch (eventName) {
            case CommonConstants.WORKFLOW_EVENT_NAME.CAF: {
                if (entity instanceof CustomersPojo) {//CAF
                    CustomersPojo customers = (CustomersPojo) entity;
                    switch (actionName) {
                        case CommonConstants.CAF_ACTION.INVENTORY_ASSIGNMENT:
                            checkInventoryAssignMent(customers);
                            break;
                        case CommonConstants.CAF_ACTION.INVOICE_GENERATION:
                            checkInvoiceGenerated(customers);
                            break;
                        case CommonConstants.CAF_ACTION.ACTIVATION:
                            activateCustomer(customers);
                            break;
                        case CommonConstants.CAF_ACTION.DOCUMENT_UPLOAD:
                            checkDocumentUploadaded(customers);
                            break;
                        case CommonConstants.CAF_ACTION.UPLOAD_ALL_DOCUMENTS:
                            checkallTypesOfDocuments(customers);
                            break;
                        case CommonConstants.CAF_ACTION.DOCUMENT_VERIFICATION:
                            checDocumentVerified(customers);
                            break;
                        case CommonConstants.CAF_ACTION.FEASIBILITY_RESULT:
                            checkFeasibilityResult(customers, null);
                            break;
                        case CommonConstants.CAF_ACTION.DOCUMENT_TYPE_PROOF_OF_IDENTITY:
                            checkDocumentTypeAvaialability(customers, CommonConstants.CAF_ACTION.DOCUMENT_TYPE_PROOF_OF_IDENTITY);
                            break;
                        case CommonConstants.CAF_ACTION.DOCUMENT_TYPE_CONTRACT:
                            checkDocumentTypeAvaialability(customers, CommonConstants.CAF_ACTION.DOCUMENT_TYPE_CONTRACT);
                            break;
                        case CommonConstants.CAF_ACTION.DOCUMENT_TYPE_PROOF_OF_ADDRESS:
                            checkDocumentTypeAvaialability(customers, CommonConstants.CAF_ACTION.DOCUMENT_TYPE_PROOF_OF_ADDRESS);
                            break;
                        case CommonConstants.CAF_ACTION.DIGITAL_SIGNATURE:
                            break;
                        case CommonConstants.CAF_ACTION.WALLET_SETTLEMENT: {
                            CAFwalletSettlement(customers);
                            break;
                        }
                        case CommonConstants.CAF_ACTION.NETWORK_LOCATION:
                            checkNetworkLocation(customers);
                            break;


                    }
                }
                break;
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN: {
                if (entity instanceof CustomerInventoryMapping) {
                    CustomerInventoryMapping customerInventoryMapping = (CustomerInventoryMapping) entity;
                    //inventory assign
                    switch (actionName) {
                        case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_ACTION.INVOICE_GENERATION: {
                            generateInvoiceForInventory(customerInventoryMapping);
                            break;
                        }
                        case CommonConstants.CUSTOMER_INVENTORY_ASSIGN_ACTION.MAC_CHANGE_PROVISION: {
                            addMACAddressInventory(customerInventoryMapping);
                            break;
                        }
                    }
                }
                break;
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT: {
                if (actionName.equals(CommonConstants.PAYMENT_ACTION.APPROVE) && entity instanceof CreditDocument) {
                    CreditDocument creditDocument = (CreditDocument) entity;
                    approvePayment(creditDocument);
                }
                break;
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.PLAN: {
                //plan
                if (actionName.equals(CommonConstants.PLAN_ACTION.ACTIVATION) && entity instanceof PostpaidPlanPojo) {
                    PostpaidPlanPojo plan = (PostpaidPlanPojo) entity;
                    activatePlan(plan);
                }
                break;

            }
            case CommonConstants.WORKFLOW_EVENT_NAME.CHANGE_DISCOUNT: {
                //change discount
                break;
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP: {
                //planGroup
                if (actionName.equals(CommonConstants.PLAN_GROUP_ACTION.ACTIVATION) && entity instanceof PlanGroup) {
                    PlanGroup planGroup = (PlanGroup) entity;
                    activatePlanGroup(planGroup);
                }
                break;
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.TERMINATION: {
                if (entity instanceof CustomersPojo) {
                    CustomersPojo custTerminate = (CustomersPojo) entity;
                    switch (actionName) {
                        case CommonConstants.TERMINATION_ACTION.WALLET_SETTLEMENT: {
                            walletSettlement(custTerminate);
                            break;
                        }
                        case CommonConstants.TERMINATION_ACTION.REMOVE_INVENTORY: {
                            checkInventoryForTermination(custTerminate);
                            break;
                        }
                        case CommonConstants.TERMINATION_ACTION.CHANGE_STATUS_TO_TERMINATE: {
                            changeStatusToTerminate(custTerminate);
                            break;
                        }
                        case CommonConstants.TERMINATION_ACTION.CLOSE_ALL_TICKETS: {
                            closeALLTickets(custTerminate);
                            break;
                        }
                        case CommonConstants.TERMINATION_ACTION.CLOSE_ALL_PENDING_TASKS: {
                            closeAllPendingTAsks(custTerminate);
                            break;
                        }
                        case CommonConstants.TERMINATION_ACTION.SEVICE_WALLET_SETTLEMENT: {
                            serviceWalletSettlement(custTerminate);
                            break;
                        }
                    }
                }
                break;

            }
            case CommonConstants.WORKFLOW_EVENT_NAME.LEAD: {
                if (entity instanceof LeadMgmtWfDTO) {
                    LeadMgmtWfDTO leadMgmtWfDTO = (LeadMgmtWfDTO) entity;
                    LeadMaster leadMaster = null;
                    if (leadMgmtWfDTO != null) {
                        if (leadMgmtWfDTO.getId() != null) {
                            leadMaster = leadMasterRepository.findById(leadMgmtWfDTO.getId()).orElse(null);
                        }
                    }
                    switch (actionName) {
                        case CommonConstants.LEAD_ACTION.FEASIBILITY_RESULT: {
                            checkFeasibilityResult(null, leadMaster);
                            break;
                        }
                    }
                }
                break;

            }
            case CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE: {
//                if(actionName.equals(CommonConstants.PARTNER_PAYMENT_ACTION.PARTNER_PAYMENT_APPROVE) && entity instanceof PartnerPayment)
//                {
//                    PartnerPaymentDTO partnerPaymentDTO = (PartnerPaymentDTO) entity;
//                    Partner partner = partnerRepository.findById(partnerPaymentDTO.getPartnerId()).orElse(null);
//                    approvePartnerPayment(partnerPaymentDTO,partner);
////                    partnerPaymentMapper.domainToDTO((PartnerPayment) entity,new CycleAvoidingMappingContext());
////
//
//                }
                break;
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE: {
                if (actionName.equals(CommonConstants.PAYMENT_ACTION.APPROVE) && entity instanceof CreditDocument) {
                    CreditDocument creditDocument = (CreditDocument) entity;
                    approvePayment(creditDocument);
                }
                break;
            }

            case CommonConstants.WORKFLOW_EVENT_NAME.SPECIAL_PLAN_MAPPING: {
                break;
            }

            case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_ADD: {
                if (entity instanceof CustomerServiceMapping) {//CAF
                    CustomerServiceMapping customerServiceMapping = (CustomerServiceMapping) entity;
                    Customers customer = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                    CustomersPojo customers = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                    switch (actionName) {
                        case CommonConstants.CUSTOMER_SERVICE_ADD_ACTION.INVENTORY_ASSIGNMENT:
                            checkInventoryAssignMent(customers);
                            break;
                        case CommonConstants.CUSTOMER_SERVICE_ADD_ACTION.INVOICE_GENERATION:
                            checkInvoiceGenerated(customers);
                            break;
                        case CommonConstants.CUSTOMER_SERVICE_ADD_ACTION.ACTIVATION:
                            activateCustomer(customers);
                            break;
                        case CommonConstants.CUSTOMER_SERVICE_ADD_ACTION.DOCUMENT_UPLOAD:
                            checkDocumentUploadaded(customers);
                            break;
                        case CommonConstants.CUSTOMER_SERVICE_ADD_ACTION.DOCUMENT_VERIFICATION:
                            checDocumentVerified(customers);
                            break;
                        case CommonConstants.CUSTOMER_SERVICE_ADD_ACTION.FEASIBILITY_RESULT:
                            checkFeasibilityResult(customers, null);
                            break;
                        case CommonConstants.CUSTOMER_SERVICE_ADD_ACTION.DOCUMENT_TYPE_PROOF_OF_IDENTITY:
                            checkDocumentTypeAvaialability(customers, CommonConstants.CAF_ACTION.DOCUMENT_TYPE_PROOF_OF_IDENTITY);
                            break;
                        case CommonConstants.CUSTOMER_SERVICE_ADD_ACTION.DOCUMENT_TYPE_CONTRACT:
                            checkDocumentTypeAvaialability(customers, CommonConstants.CAF_ACTION.DOCUMENT_TYPE_CONTRACT);
                            break;
                        case CommonConstants.CUSTOMER_SERVICE_ADD_ACTION.DOCUMENT_TYPE_PROOF_OF_ADDRESS:
                            checkDocumentTypeAvaialability(customers, CommonConstants.CAF_ACTION.DOCUMENT_TYPE_PROOF_OF_ADDRESS);
                            break;


                    }
                }
                break;
            }
            case CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_SERVICE_TERMINATION: {
                if (entity instanceof CustomerServiceMapping) {//SERVICE TERMINATION
                    CustomerServiceMapping customerServiceMapping = (CustomerServiceMapping) entity;
                    Customers customer = customersRepository.findById(customerServiceMapping.getCustId()).orElse(null);
                    CustomersPojo customers = customerMapper.domainToDTO(customer, new CycleAvoidingMappingContext());
                    switch (actionName) {
                        case CommonConstants.CUSTOMER_SERVICE_TERMINATION_ACTION.SERVICE_TERMINATE:
                            changeServiceStatusToTerminate(customers,customerServiceMapping);
                            break;
                    }
                }
            }
        break;
            default: {
                System.out.println("please enter valid input");
            }
        }
    }

    private void changeServiceStatusToTerminate(CustomersPojo customers,CustomerServiceMapping customerServiceMapping) {
        customerServiceMapping.setNextTeamHierarchyMappingId(null);
        customerServiceMapping.setNextStaff(null);
        customerServiceMapping.setIsDelete(true);
            custPlanMappingService.changeStatusOfCustServices(Collections.singletonList(customerServiceMapping.getId()), StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE, "remarks", Boolean.FALSE);
            ServiceTerminationMessage message = new ServiceTerminationMessage(Collections.singletonList(customerServiceMapping.getId()), StatusConstants.CUSTOMER_SERVICE_STATUS.TERMINATE, "remarks",Boolean.FALSE);
//            messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_TERMINATION_REVENUE);
        kafkaMessageSender.send(new KafkaMessageData(message,ServiceTerminationMessage.class.getSimpleName()));
        CustMacMappping custMacMappping = custMacMgmtService.getCustMacMappingByCustServiceMapping(customerServiceMapping.getId());
            if(custMacMappping!=null)
                custMacMgmtService.deleteMacMapping(custMacMappping.getId());
    }

    @Transactional
    public void serviceWalletSettlement(CustomersPojo custTerminate) {
        List<Integer> customerServiceMappingList=customerServiceMappingRepository.custServicemappingIdByCustId(custTerminate.getId());
        List<DebitDocument> debitDocuments=custPlanMappingService.getUnpaidDebitDoc(customerServiceMappingList);
        if(debitDocuments.size()>0){
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Complete Payment Before Termination.", new Throwable());
        }
    }

    @Transactional
    public void closeAllPendingTAsks(CustomersPojo custTerminate) {
        Customers customers=customerMapper.dtoToDomain(custTerminate,new CycleAvoidingMappingContext());
        QCustomers qCustomers=QCustomers.customers;
        QCustomerAddress qCustomerAddress=QCustomerAddress.customerAddress;
      //  BooleanExpression booleanExpression=qCustomers.isNotNull().and(qCustomers.id.eq(qCustomerAddress.customer.id).and(qCustomerAddress.status.equalsIgnoreCase("NewActivation")));
        BooleanExpression booleanExpression=qCustomerAddress.isNotNull().and(qCustomerAddress.customer.id.eq(custTerminate.getId())).and(qCustomerAddress.status.equalsIgnoreCase("NewActivation"));
        List<CustomerAddress> customerAddress=IterableUtils.toList(customerAddressRepository.findAll(booleanExpression));
        if(customerAddress.size()>0) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Approve Customer address before termination.", new Throwable());
        }
        List<CaseCustomerDetails>caseCustList=caseCustomerDetailsRepository.findByCustomerId(custTerminate.getId());
        if(caseCustList.size()>0){
            for(CaseCustomerDetails caseCustomerDetails:caseCustList){
                if(caseCustomerDetails.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_OPEN) ||
                        caseCustomerDetails.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP) ||
                        caseCustomerDetails.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_ON_HOLD) ||
                        caseCustomerDetails.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_IN_PROGRESS) ||
                        caseCustomerDetails.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_REOPEN)){
                    throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Close All Tickets before Termination.", new Throwable());
                }

            }

        }

        List<CustomerInventoryMapping> customerInventoryMappingList=customerInventoryMappingRepo.findAllByCustomerAndStatus(customers,"PENDING");
            if(customerInventoryMappingList.size()>0){
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Appove Inventory Before Termination.", new Throwable());

            }
            List<CustomerServiceMapping> customerServiceMapping=customerServiceMappingRepository.findByCustId(custTerminate.getId());
            if(customerServiceMapping.size()>0){
                for (CustomerServiceMapping customerServiceMappinglist: customerServiceMapping ){
                    if(customerServiceMappinglist.getStatus().equalsIgnoreCase("PENDING")){
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Appove Customer Discount Before Termination.", new Throwable());
                    }
                }
            }
            List<CreditDocument>creditDocumentList=creditDocRepository.findAllByCustId(custTerminate.getId());
            if(creditDocumentList.size()>0) {
                for (CreditDocument creditDocument : creditDocumentList) {
                    if (creditDocument.getStatus().equalsIgnoreCase("PENDING")) {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Appove Credit Doc Before Termination.", new Throwable());
                    }
                }
            }
            List<DebitDocument>debitDocumentList=debitDocRepository.findAllByCustomer(customers);
            if(debitDocumentList.size()>0) {
                for (DebitDocument deditDocument : debitDocumentList) {
                    if (deditDocument.getStatus() != null) {
                        if (deditDocument.getStatus().equalsIgnoreCase("PENDING")) {
                            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Complete Payment Before Termination.", new Throwable());
                        }
                    }
                }
            }
    }

    @Transactional
    public void closeALLTickets(CustomersPojo custTerminate) {
        List<CaseCustomerDetails>caseCustList=caseCustomerDetailsRepository.findByCustomerId(custTerminate.getId());
        if(caseCustList.size()>0){
            for(CaseCustomerDetails caseCustomerDetails:caseCustList){
                if(caseCustomerDetails.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_OPEN) ||
                        caseCustomerDetails.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_FOLLOW_UP) ||
                        caseCustomerDetails.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_ON_HOLD) ||
                        caseCustomerDetails.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_IN_PROGRESS) ||
                        caseCustomerDetails.getCaseStatus().equalsIgnoreCase(CaseConstants.STATUS_REOPEN)){
                    throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Close All Tickets before Termination.", new Throwable());
                }

            }

        }

    }




    private void checkFeasibilityResult(CustomersPojo customers, LeadMaster leadMaster) {
        if (customers != null) {
            if (customers.getFeasibilityRequired() == null) {
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please update customer feasibility.", new Throwable());
            }
        } else if (leadMaster != null) {
            if (leadMaster.getFeasibility() == null) {
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please update lead feasibility result.", new Throwable());
            }

        }
    }

    private void checkInventoryForTermination(CustomersPojo customers) {
//        List<CustomerInventoryMapping> customerInventoryMapping = customerInventoryMappingRepo.findAllByCustomerAndStatusAndQtyIsGreaterThanAndIsDeletedFalse(customersRepository.findById(customers.getId()).orElse(null), CommonConstants.ACTIVE_STATUS, 0L);
        QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
        BooleanExpression booleanExpression = qCustomerInventoryMapping.isDeleted.eq(false)
                .and(qCustomerInventoryMapping.customer.id.eq(customers.getId()))
                .and((qCustomerInventoryMapping.status.containsIgnoreCase(CommonConstants.CUSTOMER_INVENTORY_STATUS.PENDING))
                        .or(qCustomerInventoryMapping.status.containsIgnoreCase(CommonConstants.CUSTOMER_INVENTORY_STATUS.ACTIVE)))
                .and(qCustomerInventoryMapping.qty.gt(0L));
        List<CustomerInventoryMapping> customerInventoryMappingList = IterableUtils.toList(customerInventoryMappingRepo.findAll(booleanExpression));
        if (!customerInventoryMappingList.isEmpty()) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Inventories are assigned to customer, please remove inventories", new Throwable());
        }

    }

    private void changeStatusToTerminate(CustomersPojo customersPojo) {
        Optional<Customers> customers = customersRepository.findById(customersPojo.getId());
        if (customers.isPresent()) {
            Customers termiantedCustomers = customers.get();
            CustomerApprove customerApprove = customersService.finCustmerApproveForTermination(customers.get().getId());
            StaffUser staffUser = staffUserRepository.findById(customersService.getLoggedInUserId()).orElse(null);
            if (staffUser != null) {
//                CustomerCafAssignment customerCafAssignment = customerCafAssignmentRepository.findByCustTerminateAndStaffUser(customers.get(), staffUser);
//                customerCafAssignment.setStatus("Approved");
//                customerCafAssignment.setRemark("Approved"  " By :"  staffUser.getFirstname());
                termiantedCustomers.setNextTeamHierarchyMapping(null);
                termiantedCustomers.setStatus(SubscriberConstants.TERMINATE);
                customerApprove.setParentStaff(null);
                customerApprove.setStatus("Approved");
                customerApproveRepo.save(customerApprove);
                customersRepository.save(termiantedCustomers);
//                customerCafAssignmentRepository.delete(customerCafAssignment);
                CustomerTerminationMessage cutomerCustomerTerminationMessage = new CustomerTerminationMessage();
                cutomerCustomerTerminationMessage.setCustId(termiantedCustomers.getId());
                cutomerCustomerTerminationMessage.setStatus(termiantedCustomers.getStatus());
                cutomerCustomerTerminationMessage.setGenerateCreditnote(false);
//                messageSender.send(cutomerCustomerTerminationMessage, SharedDataConstants.QUEUE_CUSTOMER_TERMINATION_DATA_REVENUE);
                kafkaMessageSender.send(new KafkaMessageData(cutomerCustomerTerminationMessage,CustomerTerminationMessage.class.getSimpleName()));


            }
        }
    }

    @Transactional
    public void walletSettlement(CustomersPojo customersPojo) {
        Optional<Customers> customers = customersRepository.findById(customersPojo.getId());
        if (customers.isPresent()) try {

            CustomerApprove customerApprove = customersService.finCustmerApproveForTermination(customers.get().getId());
            List<DebitDocument> serviceExpiredAndInvoiceNotClearList = debitDocRepository.getInvoiceWhereServiceExpiredAndNotClear(customersPojo.getId());
            if (serviceExpiredAndInvoiceNotClearList.size() > 0) {
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please clear all previous pending dues .", new Throwable());
            } else {
                List<DebitDocument> currentServiceInoviceList = debitDocRepository.currentServiceInoviceList(customersPojo.getId());
                if (currentServiceInoviceList.size() > 0) {
                    for (DebitDocument debitDocument : currentServiceInoviceList) {
                        LocalDateTime startDate = debitDocument.getStartdate();
                        LocalDateTime endDate = debitDocument.getEndate();
                        LocalDateTime todayDate = LocalDateTime.now();
                        double paidAmount = debitDocument.getAdjustedAmount() != null ? debitDocument.getAdjustedAmount() : 0L;
                        Long invoiceDays = ChronoUnit.DAYS.between(startDate, endDate);
                        long usedDays = ChronoUnit.DAYS.between(startDate, todayDate);
                        long pendingDays = invoiceDays - usedDays;
                        double perdayPayement = debitDocument.getTotalamount() / invoiceDays;
                        double requiredPaymentForTermination = usedDays * perdayPayement;
                        if ((usedDays > 0 && paidAmount == 0) || (requiredPaymentForTermination - paidAmount > 0)) {
                            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please pay current used service due amount .", new Throwable());
                        } else {
                            RecordPaymentPojo pojo = new RecordPaymentPojo();
                            pojo.setPaytype("invoice");
                            List<Integer> invoiceIds = new ArrayList<>();
                            invoiceIds.add(debitDocument.getId());
                            pojo.setInvoiceId(invoiceIds);
                            pojo.setAmount(Double.valueOf(pendingDays * perdayPayement));
                            pojo.setCustomerid(customers.get().getId());
                            pojo.setPaymentdate(LocalDate.now());
                            pojo.setPaymode("Online");
//                                pojo.setReferenceno(debitDocument.get().getId().toString());
                            pojo.setRemark("Credit not for pending amount on termination");
                            pojo.setType("creditnote");
                            List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findByCustId(customers.get().getId());
                            for (CustomerServiceMapping customerServiceMapping : customerServiceMappingList) {
                                List<CustPlanMappping> custPlanMapppingList = custPlanMapppingRepository.findAllByCustServiceMappingId(customerServiceMapping.getId());
                                if (custPlanMapppingList.size() > 0) {
                                    for (CustPlanMappping custPlanMapping : custPlanMapppingList) {
                                        custPlanMapping.setCustPlanStatus("STOP");
                                        final LocalDate endDateForDbr = custPlanMapping.getEndDate().toLocalDate();
                                        dbrService.removedbrByCPRStartDate(Long.valueOf(custPlanMapping.getId()), LocalDate.now(), endDateForDbr);
                                        custPlanMappingService.update(custPlanMapping, "");
                                    }
                                    ezBillServiceUtility.deactivateService(custPlanMapppingList, 13);
                                }
                            }


//                        for (CustPlanMappping custPlanMappping : customers.get().getPlanMappingList()) {
//
//                                custPlanMappping.setCustPlanStatus("STOP");
//                                final LocalDate endDateForDbr = custPlanMappping.getEndDate().toLocalDate();
//                                dbrService.removedbrByCPRStartDate(Long.valueOf(custPlanMappping.getId()), LocalDate.now(), endDateForDbr);
//                                custPlanMappingService.update(custPlanMappping, "");
//                                //  customersService.removeService(custPlanMappping.getPlanId(), custPlanMappping.getCustomer().getId(), custPlanMappping.getId(), 13);
//                            }
                            // ezBillServiceUtility.deactivateService(customers.get().getPlanMappingList(), 13);
                            if(customerApprove.getIsWalletSetteled() != null && customerApprove.getIsWalletSetteled()) {
                                CustomerTerminationMessage cutomerCustomerTerminationMessage = new CustomerTerminationMessage();
//                                cutomerCustomerTerminationMessage.setCustId(customersPojo.getId());
//                               cutomerCustomerTerminationMessage.setStatus(customerApprove.getCurrentStatus());
//                                messageSender.send(cutomerCustomerTerminationMessage, SharedDataConstants.QUEUE_CUSTOMER_TERMINATION_DATA_REVENUE);
                                customerApprove.setIsWalletSetteled(true);
                                customers.get().setWalletbalance(0d);
                            }
                            customerApproveRepo.save(customerApprove);
                        }
                    }
              //      customers.get().setStatus(CommonConstants.CUSTOMER_STATUS.INAVCTIVE);
                    customersRepository.save(customers.get());
                    subscriberService.sendcustomerRadiusUpdateStatus(customersPojo.getId());
                }
            }
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please Settle Wallet before termination.", null);
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, ex.getMessage(), new Throwable());
        }
    }


    private void approvePayment(CreditDocument creditDocument) {
        StaffUser staffUser = staffUserRepository.findById(stateService.getLoggedInUserId()).orElse(null);
        creditDocument.setApproverid(null);
        creditDocument.setNextTeamHierarchyMappingId(null);
        List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
        QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
        BooleanExpression booleanExpression = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.creditDocId.in(creditDocument.getId()));
        creditDebitDocMappings = IterableUtils.toList(creditDebtMappingRepository.findAll(booleanExpression)).stream().sorted(Comparator.comparing(CreditDebitDocMapping::getDebtDocId)).collect(Collectors.toList());
        if (creditDocument.getPaytype().equals(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT)) {
            creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT);
        }
        if (creditDocument.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
            creditDocService.adjustCreditNote(Optional.of(creditDocument), creditDebitDocMappings,creditDocument.getCustomer().getMvnoId());
        } else {
            if (creditDocument.getPaytype().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL)) {
                creditDocService.adjustWithDrawal(creditDocument);
            } else {
                creditDocService.adjustPayment(Optional.of(creditDocument), creditDebitDocMappings,true);
            }
        }
        Customers customers=creditDocument.getCustomer();
        creditDocRepository.save(creditDocument);
        creditDocument.setPaymentdate(LocalDate.now());
        creditDocument.setAdjustedAmount(0.0);
        CreditDocMessage creditDocMessage = new CreditDocMessage(creditDocument, IterableUtils.toList(creditDebitDocMappings));
        kafkaMessageSender.send(new KafkaMessageData(creditDocMessage, CreditDocMessage.class.getSimpleName()));
//        messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_REVENUE);
        customers.setWalletbalance(customers.getWalletbalance()-creditDocument.getAmount());
        customersRepository.save(customers);
//        creditDocService.addLedgeAfterApproval(creditDocument);
        creditDocService.custPackStatusUpdate(creditDocument);
        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, creditDocument.getId(), creditDocument.getReferenceno(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "Approved By :- " + staffUser.getUsername());

    }

    private void addMACAddressInventory(CustomerInventoryMapping customerInventoryMapping) {
        customerInventoryMapping.getInOutWardMACMapping().forEach(inOutWardMACMapping -> {
                    PlanService planService = planServiceRepository.findById(Math.toIntExact(customerInventoryMapping.getServiceId())).orElse(null);
                    if (planService.getIs_dtv()==false) {
                        QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
                        List<CustMacMappping> customerMacMapping = (List<CustMacMappping>) custMacMapppingRepository.findAll(qCustMacMappping.macAddress.eq(inOutWardMACMapping.getMacAddress()).and(qCustMacMappping.customer.id.eq(customerInventoryMapping.getCustomer().getId())));
                        if (customerMacMapping.size() == 0) {
                            CustMacMappping custMacMappping = new CustMacMappping();
                            custMacMappping.setMacAddress(inOutWardMACMapping.getMacAddress());
                            custMacMappping.setCustomer(customerInventoryMapping.getCustomer());
                            custMacMapppingService.save(custMacMappping);
                        }
                    }
                }

        );
    }

    private void generateInvoiceForInventory(CustomerInventoryMapping customerInventoryMapping) {
        if (customerInventoryMapping.getProduct().getRefurburshiedProductCharge() != null) {
           // Charge charge = chargeService.get(customerInventoryMapping.getProduct().getRefurburshiedProductCharge());
            Charge charge = chargeRepository.findById(customerInventoryMapping.getProduct().getRefurburshiedProductCharge()).get();
            Double applicableAmount = charge.getPrice() + ((charge.getPrice() * charge.getTax().getTieredList().get(0).getRate()) / 100.0);
            Runnable chargeRunnable = new ChargeThread(customerInventoryMapping.getCustomer().getId(), new ArrayList<>(), customersService, customerInventoryMapping.getId(), "", null);
            Thread billChargeThread = new Thread(chargeRunnable);
            billChargeThread.start();
            /*CustomerDBR dbr = new CustomerDBR();
            dbr.setCustid(customerInventoryMapping.getCustomer().getId().longValue());
            dbr.setStartdate(LocalDate.now());
            dbr.setEnddate(LocalDate.now());
            dbr.setDbr(applicableAmount);
            dbr.setPendingamt(0.0);
            dbr.setCustname(customerInventoryMapping.getCustomer().getCustname());
            dbr.setStatus("Active");
            dbr.setCusttype(customerInventoryMapping.getCustomer().getCusttype());
            customerDBRRepository.save(dbr);*/
        }

    }

    @Transactional
    public void activatePlan(PostpaidPlanPojo plan) {
        PostpaidPlan postpaidPlan = postpaidPlanService.findById(plan.getId());
        postpaidPlan.setStatus(CommonConstants.CUSTOMER_STATUS_ACTIVE);
//        postpaidPlan.setNextTeamHierarchyMapping(null);
        postpaidPlan.setNextStaff(null);
        postpaidPlanService.save(postpaidPlan);
    }

    @Transactional
    public void activatePlanGroup(PlanGroup planGroup) {
        PlanGroup planGroup1 = planGroupService.findPlanGroupById(planGroup.getPlanGroupId(), planGroup.getMvnoId());
        planGroup1.setStatus(CommonConstants.CUSTOMER_STATUS_ACTIVE);
        planGroup1.setNextTeamHierarchyMappingId(null);
        planGroup1.setNextStaff(null);
        planGroupService.save(planGroup1);
    }

    @Transactional
    public void activateCustomer(CustomersPojo customers) {
        Customers customer =  customersRepository.findById(customers.getId()).get();
        customer.setStatus(CommonConstants.CUSTOMER_STATUS_ACTIVE);
        customer.setNextTeamHierarchyMapping(null);
        customersService.update(customer);
        checkInvoiceGenerated(customers);
    }

    private void checDocumentVerified(CustomersPojo customers) {
        List<CustomerDocDetailsDTO> customerDocDetailsDTOS = customerDocDetailsService.findDocsByCustomerId(customers.getId());
        if (customerDocDetailsDTOS.size() == 0) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Customer documents are not uploaded.", null);
        }
        if (customerDocDetailsDTOS.stream().anyMatch(customerDocDetailsDTO -> customerDocDetailsDTO.getDocStatus().equalsIgnoreCase("pending"))) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Customer documents are not verified.", new Throwable());
        }

    }

    private void checkDocumentUploadaded(CustomersPojo customers) {
        List<CustomerDocDetailsDTO> customerDocDetailsDTOS = customerDocDetailsService.findDocsByCustomerId(customers.getId());
        if (customerDocDetailsDTOS.size() == 0) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Customer documents are not uploaded.", null);
        }
    }

    private void checkInvoiceGenerated(CustomersPojo customers) {
        Customers checkCustomer =  customersRepository.findById(customers.getId()).get();
        List<Integer> integers = checkCustomer.getPlanMappingList().stream().map(CustPlanMappping::getId).collect(Collectors.toList());
        List<DebitDocument> debitDocuments = debitDocRepository.findAllByCustpackrelidIn(integers);
        List<TrialDebitDocument> trialDebitDocuments = trialDebitDocRepository.findAllByCustpackrelidIn(integers);
        if (debitDocuments.size() == 0 && trialDebitDocuments.size() == 0) {
//            dbrService.addDbrForPrepaidCustomerCreation(customers.getId());
//            Runnable chargeRunnable = new InvoiceCreationThread(customers, customersService, null, false, null, CommonConstants.INVOICE_TYPE.NEW_SERVICE);
//            Thread billchargeThread = new Thread(chargeRunnable);
//            billchargeThread.start();
        }
    }

    private void checkInventoryAssignMent(CustomersPojo customers) {
        List<CustomerInventoryMapping> customerInventoryMapping = customerInventoryMappingRepo.findAllByCustomerAndStatusAndIsDeletedFalse(customersRepository.findById(customers.getId()).orElse(null), CommonConstants.ACTIVE_STATUS);
        if (customerInventoryMapping.size() == 0) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Customer inventory not assigned.", new Throwable());
        }
    }

    private void checkDocumentTypeAvaialability(CustomersPojo customers, String docType) {
        List<CustomerDocDetailsDTO> customerDocDetailsDTOS = customerDocDetailsService.findDocsByCustomerId(customers.getId());
        List<CustomerDocDetails> customerDocDetailsDTOList = null;
        QCustomerDocDetails qCustomerDocDetails = QCustomerDocDetails.customerDocDetails;
        BooleanExpression booleanExpression = qCustomerDocDetails.isDelete.eq(false);
        if (customerDocDetailsDTOS.size() == 0) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Customer documents are not uploaded.", null);
        } else {
            switch (docType) {
                case CommonConstants.CAF_ACTION.DOCUMENT_TYPE_CONTRACT:
                    booleanExpression = booleanExpression.and(qCustomerDocDetails.docType.eq("contract")).and(qCustomerDocDetails.customer.id.eq(customers.getId()));
                    customerDocDetailsDTOList = (List<CustomerDocDetails>) customerDocDetailsRepository.findAll(booleanExpression);
                    break;
                case CommonConstants.CAF_ACTION.DOCUMENT_TYPE_PROOF_OF_ADDRESS:
                    booleanExpression = booleanExpression.and(qCustomerDocDetails.docType.eq("proofofaddress")).and(qCustomerDocDetails.customer.id.eq(customers.getId()));
                    customerDocDetailsDTOList = (List<CustomerDocDetails>) customerDocDetailsRepository.findAll(booleanExpression);
                    break;

                case CommonConstants.CAF_ACTION.DOCUMENT_TYPE_PROOF_OF_IDENTITY:
                    booleanExpression = booleanExpression.and(qCustomerDocDetails.docType.eq("proofofidentity")).and(qCustomerDocDetails.customer.id.eq(customers.getId()));
                    customerDocDetailsDTOList = (List<CustomerDocDetails>) customerDocDetailsRepository.findAll(booleanExpression);
                    break;
            }

        }
        if (customerDocDetailsDTOList.size() == 0) {
            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Required type of document is not available", null);
        }

    }

//    private void approvePartnerPayment(PartnerPaymentDTO partnerPaymentDTO, Partner partner)
//    {
//        partnerPaymentDTO.setStatus(SubscriberConstants.ACTIVE);
//
//        if (partnerPaymentDTO.getAmount() != null) {
//            if (partner.getCreditConsume() == 0) {
//                partner.setBalance(partner.getBalance() + partnerPaymentDTO.getAmount());
//            } else if (partner.getCreditConsume() < (partner.getBalance() + partnerPaymentDTO.getAmount())) {
//                partner.setBalance(partner.getBalance() + partnerPaymentDTO.getAmount() - partner.getCreditConsume());
//                partner.setCreditConsume(0.0d);
//            } else if (partner.getCreditConsume() > (partner.getBalance() + partnerPaymentDTO.getAmount())) {
//                partner.setBalance(0.0d);
//                partner.setCreditConsume(partner.getCreditConsume() - (partner.getBalance() + partnerPaymentDTO.getAmount()));
//            }
//            partner.setCredit(partnerPaymentDTO.getCredit() + partner.getCredit());
//            partnerrepo.save(partner);
//        }
//
//        PartnerLedgerBalanceDTO dto1 = new PartnerLedgerBalanceDTO();
//        dto1.setCredit(partnerPaymentDTO.getCredit());
//        dto1.setPartner_id(partnerPaymentDTO.getPartnerId());
//        dto1.setAmount(partnerPaymentDTO.getAmount());
//        dto1.setPaymentdate(LocalDate.now());
//        partnerPaymentRepository.save(partnerPaymentMapper.dtoToDomain(partnerPaymentDTO,new CycleAvoidingMappingContext()));
//
//        try {
//            partnerLedgerService.addBalance(dto1);
//            partnerLedgerDetailsService.reverseBalance(null, 0.0, partnerPaymentDTO.getAmount(), partner.getId(), CommonConstants.TRANS_CATEGORY_ADD_BALANCE, "Add Balance in Partner wallet");
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }

    Teams getNextTeamHirMappingByOrderNumberAndWorkflowId(Integer orderNumber, Integer hierarchyId) {
        QTeamHierarchyMapping qTeamHierarchyMapping = QTeamHierarchyMapping.teamHierarchyMapping;
        BooleanExpression booleanExpression = qTeamHierarchyMapping.isNotNull().and(qTeamHierarchyMapping.orderNumber.eq(orderNumber + 1)).and(qTeamHierarchyMapping.hierarchyId.eq(hierarchyId));
        TeamHierarchyMapping hierarchyMapping = teamHierarchyMappingRepo.findOne(booleanExpression).get();
        if (hierarchyMapping != null) {
            return teamsRepository.findById(Long.valueOf(hierarchyMapping.getTeamId())).get();
        }
        return null;
    }
    @Transactional
    public void CAFwalletSettlement(CustomersPojo customersPojo) {
        Optional<Customers> customers = customersRepository.findById(customersPojo.getId());
        ResponseEntity<?> entity = null;
        if (customers.isPresent()) {
            try {
                CustomerLedgerDtlsPojo customerLedgerDtlsPojo = new CustomerLedgerDtlsPojo();
                customerLedgerDtlsPojo.setCustId(customers.get().getId());

                ObjectMapper objectMapper = new ObjectMapper();
                if(stateService.getJwtToken()!=null){
                    entity = revenueClient.getCafWalletAmount(stateService.getJwtToken(), customerLedgerDtlsPojo);
                }else{
                    String token= apiGatewayScheduler.GenerateTokenUsingLoggedInUser(getLoggedInUser());
                    entity = revenueClient.getCafWalletAmount(token, customerLedgerDtlsPojo);
                }
                if (entity.getStatusCode().value() == HttpStatus.SC_OK) {
                    Map<String, Object> responseMap;
                    if (entity.getBody() instanceof String) {
                        responseMap = objectMapper.readValue((String) entity.getBody(), Map.class);
                    } else if (entity.getBody() instanceof Map) {
                        responseMap = (Map<String, Object>) entity.getBody();
                    } else {
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Invalid response format.", null);
                    }
                    if (responseMap.containsKey("customerWalletDetails")) {
                        Object value = responseMap.get("customerWalletDetails");
                        long longValue;
                        if (value instanceof Number) {
                            longValue = ((Number) value).longValue();
                        } else if (value instanceof String) {
                            longValue = Long.parseLong((String) value);
                        } else {
                            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Unexpected data type for customerWalletDetails.", null);
                        }
                        if (longValue < 0) {
                            throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Please settle the wallet before approval.", null);
                        }
                    } else if(responseMap.containsKey("noInvoiceFound")){
                        throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, responseMap.get("noInvoiceFound").toString(), null);
                    }else {
                        System.out.println("Key 'customerWalletDetails' not found in response.");
                    }
                }
            }catch (FeignException fe){
                ApplicationLogger.logger.error(fe.getMessage());
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Unexpected error occures from fiegnClient : "+fe.getMessage(), fe);
            }
            catch (CustomValidationException ex) {
                throw ex;
            } catch (NumberFormatException ex) {
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, "Invalid number format in response.", ex);
            } catch (Exception ex) {
                throw new CustomValidationException(HttpStatus.SC_EXPECTATION_FAILED, ex.getMessage(), ex);
            }
        } else {
            throw new CustomValidationException(HttpStatus.SC_NOT_FOUND, "Customer not found.", null);
        }
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("ApiGatewayScheduler" + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

    private void checkNetworkLocation(CustomersPojo customers) {
            if (customers.getOltid()!= null || (Objects.nonNull(customers.getPopid()) && customers.getPopid()!=0 ) || customers.getMasterdbid()!=null ||
                    customers.getNasPortId()!=null || customers.getNasIpAddress()!=null || customers.getVlan_id()!=null ||customers.getFramedIp()!=null || customers.getFramedIpv6Address()!=null) {
                System.out.println("Network Details Are already added.");
            }else{
                throw new CustomValidationException(HttpStatus.SC_NOT_FOUND,"Please Upload CUstomer Network Location details",null);
            }
    }

    private void checkallTypesOfDocuments(CustomersPojo customers) {
        List<String> typeLits = new ArrayList<>();
        typeLits.add(CommonConstants.CUSTOMER_DOC_TYPE_ONLINE);
        typeLits.add(CommonConstants.CUSTOMER_DOC_TYPE_OFFLINE);
        Set<String> commonListDTOS = typeLits.stream()
                .flatMap(type -> commonListService
                        .getCommonListByType(type)
                        .stream()
                        .map(CommonListDTO::getText))
                .collect(Collectors.toSet());

        List<CustomerDocDetailsDTO> customerDocDetailsDTOS =
                customerDocDetailsService.findDocsByCustomerId(customers.getId());

        if (customerDocDetailsDTOS.isEmpty()) {
            throw new CustomValidationException(
                    HttpStatus.SC_EXPECTATION_FAILED,
                    "Customer documents are not uploaded.",
                    null
            );
        }

        Set<String> uploadedTypes = customerDocDetailsDTOS.stream()
                .map(CustomerDocDetailsDTO::getDocSubType)
                .collect(Collectors.toSet());
        if (!uploadedTypes.containsAll(commonListDTOS)) {
            throw new CustomValidationException(
                    HttpStatus.SC_EXPECTATION_FAILED,
                    "Customer must upload all required document types: " + commonListDTOS,
                    null
            );
        }
    }

    List<StaffUser> getActiveStaffUserByTeamId(Long teamId, List<ServiceArea> serviceAreas) {
        List<Long> serviceAreaIds=serviceAreas.stream().map(i->i.getId()).collect(Collectors.toList());
        return staffUserRepository.getDistinctStaffByServiceAreaAndTeamId(serviceAreaIds,teamId);
    }

}
