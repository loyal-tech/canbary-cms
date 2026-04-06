package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.GsonConfig;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.postpaid.PostpaidPlanChargeMapper;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.lead.LeadCustPlanMappping;
import com.adopt.apigw.model.lead.LeadMaster;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.domain.QBranch;
import com.adopt.apigw.modules.Branch.repository.BranchRepository;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.BusinessUnit.repository.BusinessUnitRepository;
import com.adopt.apigw.modules.CommonList.repository.CommonListRepository;
import com.adopt.apigw.modules.Customers.LightPostpaidPlanDTO;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.domain.Productplanmapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.dto.Productplanmappingdto;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.mapper.Productplanmappingmapper;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.repository.ProductPlanMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.service.ProductplanmappingService;
import com.adopt.apigw.modules.InventoryManagement.product.ProductServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategoryService;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.PlanQosMapping.*;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBookPlanDetail;
import com.adopt.apigw.modules.PriceGroup.domain.QPriceBookPlanDetail;
import com.adopt.apigw.modules.PriceGroup.model.PriceBookDTO;
import com.adopt.apigw.modules.PriceGroup.model.PriceBookPlanDetailDTO;
import com.adopt.apigw.modules.PriceGroup.repository.PriceBookPlanDtlRepository;
import com.adopt.apigw.modules.PriceGroup.service.PriceBookService;
import com.adopt.apigw.modules.ServiceArea.domain.QServiceArea;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceParameterMapping.domain.QServiceParamMapping;
import com.adopt.apigw.modules.ServiceParameterMapping.domain.ServiceParamMapping;
import com.adopt.apigw.modules.ServiceParameterMapping.repository.ServiceParamMappingRepository;
import com.adopt.apigw.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.adopt.apigw.modules.Teams.repository.TeamsRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.planUpdate.domain.CustomerPackage;
import com.adopt.apigw.modules.planUpdate.domain.QCustomerPackage;
import com.adopt.apigw.modules.planUpdate.repository.CustomerPackageRepository;
import com.adopt.apigw.modules.planUpdate.service.CustomerPackageService;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
import com.adopt.apigw.modules.qosPolicy.repository.QOSPolicyRepository;
import com.adopt.apigw.modules.qosPolicy.service.QOSPolicyService;
import com.adopt.apigw.modules.servicePlan.model.ServicesDTO;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.modules.servicePlan.service.ServicesService;
import com.adopt.apigw.modules.subscriber.model.CustomerPlansModel;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.pojo.customer.plans.GetPlansByFilter;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.PlanServiceAreaBindingCheckMessage;
import com.adopt.apigw.rabbitMq.message.PostpaidPlanMessage;
import com.adopt.apigw.rabbitMq.message.SavePlanAssignmentMessage;
import com.adopt.apigw.repository.DepartmentPlanMappingRepository;
import com.adopt.apigw.repository.LeadCustPlanMapppingRepository;
import com.adopt.apigw.repository.LeadMasterRepository;
import com.adopt.apigw.repository.common.BranchServiceAreaMappingRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.common.WorkflowAuditRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.common.WorkflowAuditService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.*;
import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PostpaidPlanService extends AbstractService<PostpaidPlan, PostpaidPlanPojo, Integer> {

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private PostpaidPlanRepo entityRepository;

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private PostpaidPlanMapper postpaidPlanMapper;

    @Autowired
    private ChargeMapper chargeMapper;

    @Autowired
    private PlanServiceService planServiceService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private PriceBookService priceBookService;

    @Autowired
    private CustomerPackageService customerPackageService;
    @Autowired
    private TaxService taxService;
    @Autowired
    private TaxRepository taxRepository;
    @Autowired
    private ClientServiceSrv clientServiceSrv;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    LeadCustPlanMapppingRepository leadCustPlanMapppingRepository;

    @Autowired
    private CustSpecialPlanMapppingService custSpecialPlanMapppingService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;

    @Autowired
    CommonListRepository commonListRepository;
    @Autowired
    StaffUserService staffUserService;

    @Autowired
    TeamHierarchyMappingRepo teamHierarchyMappingRepo;

    @Autowired
    TeamsRepository teamsRepository;

    @Autowired
    CustomersService customersService;

    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    DebitDocRepository debitDocRepository;


    @Autowired
    private PostPaidPlanServiceAreaMappingRepo planServiceAreaRepo;

    @Autowired
    private CustomerPackageRepository customerPackageRepository;

    @Autowired
    WorkflowAuditService workflowAuditService;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private ServicesService servicesService;

    @Autowired
    private WorkflowAuditRepository workflowAuditRepository;
    @Autowired
    private WorkFlowQueryUtils workFlowQueryUtils;

    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    private TatUtils tatUtils;

    @Autowired
    private ProductPlanMappingRepository productPlanMappingRepository;
    @Autowired
    private Productplanmappingmapper productplanmappingmapper;

    @Autowired
    private ProductplanmappingService productplanmappingService;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private PriceBookPlanDtlRepository priceBookPlanDtlRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    private PostpaidPlanChargeService postpaidPlanChargeService;

    @Autowired
    private PostpaidPlanChargeMapper postpaidPlanChargeMapper;

    @Autowired
    private QOSPolicyRepository qosPolicyRepository;

    @Autowired
    private QOSPolicyService qosPolicyService;

    @Autowired
    private PlanQosMappingRepo planQosMappingRepo;

    @Autowired
    PlanQosMappingMapper planQosMappingMapper;

    @Autowired
    PlanQosMappingService planQosMappingService;

    @Autowired
    LeadMasterRepository leadMasterRepository;

    @Autowired
    PlanGroupMappingRepository planGroupMappingRepository;

    @Autowired
    private PlanGroupMappingService planGroupMappingService;

    @Autowired
    private BusinessUnitRepository businessUnitRepository;
    private static String MODULE = " [PostpaidPlanService] ";
    @Autowired
    private CustSpecialPlanRelMapppingRepository custSpecialPlanRelMapppingRepository;
    @Autowired
    private CustSpecialPlanMapppingRepository custSpecialPlanMapppingRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;

    @Autowired
    private PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private PlanServiceRepository planServiceRepository;

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private CustPlanMapppingRepository custPlanMapppingRepository;

    @Autowired
    private ServiceAreaPlangroupMappingRepo serviceAreaPlangroupMappingRepo;

    @Autowired
    private PlanGroupRepository planGroupRepository;

    @Autowired
    private ServiceParamMappingRepository serviceParamMappingRepository;

    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    DepartmentPlanMappingRepository departmentPlanMappingRepository;


    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);
    @Autowired
    private PostPaidPlanServiceAreaMappingRepo postPaidPlanServiceAreaMappingRepository;
    @Autowired
    CacheService cacheService;



    @Autowired
    private CustChargeService custChargeService;

//    @Autowired
//    LeadMasterRepository leadMasterRepository;
//    @Autowired
//    PlanGroupMappingRepository planGroupMappingRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplates;

    @Autowired
    private ObjectMapper objectMapper;

    /*public PostpaidPlanService(RedisTemplate<String, PostpaidPlan> redisPostpaidPlan) {
        this.redisPostpaidPlan = redisPostpaidPlan;
    }*/
    public PostpaidPlanService() {
        sortColMap.put("id", "POSTPAIDPLANID");
    }

    public PostpaidPlanService(MessagesPropertyConfig messagesProperty) {
        this.messagesProperty = messagesProperty;
    }

    @Override
    protected PostpaidPlanRepo getRepository() {
        return entityRepository;
    }

    public Page<PostpaidPlan> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.searchEntity(searchText, pageRequest, getMvnoIdFromCurrentStaff(null));
    }

     public List<PostpaidPlan> getAllActiveEntities(String type, String planGroup,Integer mvnoId) {
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        QPostPaidPlanServiceAreaMapping qPostPaidPlanServiceAreaMapping = QPostPaidPlanServiceAreaMapping.postPaidPlanServiceAreaMapping;
        BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.status.eq(CommonConstants.ACTIVE_STATUS));
        if (type.equalsIgnoreCase(Constants.NORMAL))
            booleanExpression = booleanExpression.and(qPostpaidPlan.mode.eq(Constants.NORMAL));
        else if (type.equalsIgnoreCase(Constants.SPECIAL))
            booleanExpression = booleanExpression.and(qPostpaidPlan.mode.eq(Constants.SPECIAL));

        if (planGroup != null && !"".equals(planGroup) && !Constants.PLAN_GROUP_ALL.equalsIgnoreCase(planGroup))
            booleanExpression = booleanExpression.and(qPostpaidPlan.planGroup.eq(planGroup));

        if (getLoggedInUserId() != 1) {
            List<Integer> serviceIDs = getLoggedInUser().getServiceAreaIdList().stream().collect(Collectors.toList());
            if (serviceIDs != null && serviceIDs.size() > 0)
                booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(query.select(qPostPaidPlanServiceAreaMapping.planId).from(qPostPaidPlanServiceAreaMapping).where(qPostPaidPlanServiceAreaMapping.serviceId.in(serviceIDs)).fetch()));
        }

        booleanExpression = booleanExpression.and(qPostpaidPlan.status.notEqualsIgnoreCase("INACTIVE"));
        if (getBUIdsFromCurrentStaff().size() != 0)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()));
         if(mvnoId!=null && mvnoId!=1){
             booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(mvnoId)));
         }
        if (getLoggedInUserPartnerId() != 1) {
            List<Integer> planIds = new ArrayList<>();

            Partner partner = partnerRepository.findById(getLoggedInUserPartnerId()).orElse(null);
            if (partner != null) {
                QPriceBookPlanDetail qPriceBookPlanDetail = QPriceBookPlanDetail.priceBookPlanDetail;
                BooleanExpression expression = qPriceBookPlanDetail.isNotNull().and(qPriceBookPlanDetail.priceBook.eq(partner.getPriceBookId()));
                planIds = query.select(qPriceBookPlanDetail.postpaidPlan.id).from(qPriceBookPlanDetail).where(expression).fetch();
//                List<PriceBookPlanDetail> list = (List<PriceBookPlanDetail>) priceBookPlanDtlRepository.findAll(expression);
//                for (PriceBookPlanDetail plan : list) {
//                    if (plan.getPostpaidPlan() != null) {
//                        planIds.add(plan.getPostpaidPlan().getId());
//                    }
//                }
            }

            if (partner != null && partner.getPriceBookId() != null && partner.getPriceBookId().getIsAllPlanSelected() != null && partner.getPriceBookId().getIsAllPlanSelected()) {
                List<PostpaidPlanPojo> plans = getPlanListByServiceArea(getLoggedInUser().getServiceAreaIdList(), type, Constants.PLAN_GROUP_ALL, null, null, null, null,mvnoId);
                List<Integer> planIds1 = plans.stream().map(x -> x.getId()).collect(Collectors.toList());
                if (planIds1 != null && !planIds1.isEmpty())
                    planIds.addAll(planIds1);
            }

            booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(planIds));
        }

//        Iterable<PostpaidPlan> postpaidPlanList = entityRepository.findAll(booleanExpression);
        JPAQuery<?> planQuery = new JPAQuery<>(entityManager);
        List<PostpaidPlan> postpaidPlanList = (List<PostpaidPlan>) planQuery.from(qPostpaidPlan)
                .where(booleanExpression)
                .fetch();
        /* filter all service ids and collect */
        List<Integer> serviceIds = postpaidPlanList.stream()
                .map(PostpaidPlan::getServiceId)
                .distinct()
                .collect(Collectors.toList());
        List<Long> longList = serviceIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());

        /*based on that serviceIds and fetch service area names */
        ;
        List<String> serviceNames = serviceRepository.findServiceNamesByIds(longList);
        postpaidPlanList.stream().peek(plan -> plan.setServiceName(serviceNames.toString())).collect(Collectors.toList());

//        for (PostpaidPlan plan : postpaidPlanList) {
//            plan.setServiceName(serviceRepository.findById(plan.getServiceId().longValue()).get().getServiceName());
//        }

        BusinessUnit businessUnit = new BusinessUnit();
        List<PostpaidPlan> postpaidPlanList1 = new ArrayList<>();
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        if ((Objects.isNull(businessUnit.getPlanBindingType())) || (CommonConstants.PREDEFINED).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
            // TODO: pass mvnoID manually 6/5/2025
            postpaidPlanList1 = IterableUtils.toList(postpaidPlanList).stream().filter(postpaidPlan -> postpaidPlan.getMvnoId() == 1 || getLoggedInMvnoId() == 1 || postpaidPlan.getMvnoId() == getLoggedInMvnoId() &&
                    (Objects.isNull(postpaidPlan.getBusinessType()) || (CommonConstants.RETAIL).equalsIgnoreCase(postpaidPlan.getBusinessType()))).collect(Collectors.toList());
        } else if ((CommonConstants.ON_DEMAND).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
            // TODO: pass mvnoID manually 6/5/2025
            postpaidPlanList1 = IterableUtils.toList(postpaidPlanList).stream().filter(postpaidPlan -> postpaidPlan.getMvnoId() == 1 || getLoggedInMvnoId() == 1 || postpaidPlan.getMvnoId() == getLoggedInMvnoId() &&
                    ((CommonConstants.ENTERPRISE).equalsIgnoreCase(postpaidPlan.getBusinessType()))).collect(Collectors.toList());
        }
//        return entityRepository.findAllByStatusAndIsDeleteIsFalseOrderByIdDesc(CommonConstants.ACTIVE_STATUS).stream().filter(plan -> plan.getMvnoId() == getMvnoIdFromCurrentStaff() || plan.getMvnoId() == null).collect(Collectors.toList());
        // return IterableUtils.toList(entityRepository.findAll(booleanExpression)).stream().filter(postpaidPlan -> postpaidPlan.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || postpaidPlan.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
//        return entityRepository.findAllByStatusAndIsDeleteIsFalseOrderByIdDesc(CommonConstants.ACTIVE_STATUS).stream().filter(plan -> plan.getMvnoId() == getMvnoIdFromCurrentStaff() || plan.getMvnoId() == null).collect(Collectors.toList());
        return postpaidPlanList1;
    }



    public List<postpaidPlanFetchPojo> getAllActiveEntities1(String type, String planGroup, Integer mvnoId) {
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        List<Integer> planIds = new ArrayList<>();
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        QPostPaidPlanServiceAreaMapping qPostPaidPlanServiceAreaMapping = QPostPaidPlanServiceAreaMapping.postPaidPlanServiceAreaMapping;
        BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.status.eq(CommonConstants.ACTIVE_STATUS));
        if (type.equalsIgnoreCase(Constants.NORMAL))
            booleanExpression = booleanExpression.and(qPostpaidPlan.mode.eq(Constants.NORMAL));
        else if (type.equalsIgnoreCase(Constants.SPECIAL))
            booleanExpression = booleanExpression.and(qPostpaidPlan.mode.eq(Constants.SPECIAL));

        if (planGroup != null && !"".equals(planGroup) && !Constants.PLAN_GROUP_ALL.equalsIgnoreCase(planGroup))
            booleanExpression = booleanExpression.and(qPostpaidPlan.planGroup.eq(planGroup));

        if (getLoggedInUserId() != 1) {
            List<Integer> serviceIDs = getLoggedInUser().getServiceAreaIdList().stream().collect(Collectors.toList());
            if (serviceIDs != null && serviceIDs.size() > 0)
                booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(query.select(qPostPaidPlanServiceAreaMapping.planId).distinct().from(qPostPaidPlanServiceAreaMapping).where(qPostPaidPlanServiceAreaMapping.serviceId.in(serviceIDs)).fetch()));
        }

        booleanExpression = booleanExpression.and(qPostpaidPlan.status.notEqualsIgnoreCase("INACTIVE"));
        if (getBUIdsFromCurrentStaff().size() != 0)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(mvnoId).and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()))));
        if (getLoggedInUserPartnerId() != 1) {


            Partner partner = partnerRepository.findById(getLoggedInUserPartnerId()).orElse(null);
            if (partner != null) {
                QPriceBookPlanDetail qPriceBookPlanDetail = QPriceBookPlanDetail.priceBookPlanDetail;
                BooleanExpression expression = qPriceBookPlanDetail.isNotNull().and(qPriceBookPlanDetail.priceBook.eq(partner.getPriceBookId()));
                planIds = query.select(qPriceBookPlanDetail.postpaidPlan.id).from(qPriceBookPlanDetail).where(expression).fetch();
//                List<PriceBookPlanDetail> list = (List<PriceBookPlanDetail>) priceBookPlanDtlRepository.findAll(expression);
//                for (PriceBookPlanDetail plan : list) {
//                    if (plan.getPostpaidPlan() != null) {
//                        planIds.add(plan.getPostpaidPlan().getId());
//                    }
//                }
            }

            if (partner != null && partner.getPriceBookId() != null && partner.getPriceBookId().getIsAllPlanSelected() != null && partner.getPriceBookId().getIsAllPlanSelected()) {
                List<PostpaidPlanPojo> plans = getPlanListByServiceArea(getLoggedInUser().getServiceAreaIdList(), type, Constants.PLAN_GROUP_ALL, null, null, null, null,mvnoId);
                List<Integer> planIds1 = plans.stream().map(x -> x.getId()).collect(Collectors.toList());
                if (planIds1 != null && !planIds1.isEmpty())
                    planIds.addAll(planIds1);
            }

            booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(planIds));
        }

        List<Integer> finalPlanIds = new JPAQuery<>(entityManager)
                .select(qPostpaidPlan.id)
                .from(qPostpaidPlan)
                .where(booleanExpression)
                .fetch();

        if (finalPlanIds == null || finalPlanIds.isEmpty()) {
            return Collections.emptyList();
        }

//        Iterable<PostpaidPlan> postpaidPlanList = entityRepository.findAll(booleanExpression);

        List<postpaidPlanFetchPojo> postpaidPlanList = entityRepository.findAllPlanDetailsByIds(finalPlanIds);

/*
        List<postpaidPlanFetchPojo> postpaidPlanList = query.from(qPostpaidPlan)
                .select(Projections.constructor(
                                postpaidPlanFetchPojo.class,
                                qPostpaidPlan.id,
                                qPostpaidPlan.name,
                                qPostpaidPlan.displayName,
                                qPostpaidPlan.code,
                                qPostpaidPlan.desc,
                                qPostpaidPlan.category,
                                qPostpaidPlan.startDate,
                                qPostpaidPlan.endDate,
                                qPostpaidPlan.allowOverUsage,
                                qPostpaidPlan.quotaUnit,
                                qPostpaidPlan.quota,
                                qPostpaidPlan.planStatus,
                                qPostpaidPlan.childQuota,
                                qPostpaidPlan.childQuotaUnit,
                                qPostpaidPlan.mvnoId,
                                qPostpaidPlan.status,
                                qPostpaidPlan.serviceId,
                                qPostpaidPlan.timebasepolicyId,
                                qPostpaidPlan.plantype,
                                qPostpaidPlan.dbr,
                                qPostpaidPlan.planGroup,
                                qPostpaidPlan.validity,
                                qPostpaidPlan.maxconcurrentsession,
                                qPostpaidPlan.quotaResetInterval,
                                qPostpaidPlan.mode,
                                qPostpaidPlan.unitsOfValidity,
                                qPostpaidPlan.newOfferPrice,
                                qPostpaidPlan.allowdiscount,
                                qPostpaidPlan.basePlan,
                                qPostpaidPlan.useQuota,
                                qPostpaidPlan.mvnoName,
                                qPostpaidPlan.usageQuotaType,
                                qPostpaidPlan.taxamount
                       )
                ).
                where(booleanExpression).fetch();
*/

        /* filter all service ids and collect */
//        List<Integer> serviceIds = postpaidPlanList.stream()
//                .map(PostpaidPlan::getServiceId)
//                .distinct()
//                .collect(Collectors.toList());


        List<Integer> serviceIds = postpaidPlanList.stream()
                .map(postpaidPlanFetchPojo::getServiceId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<Long> longList = serviceIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());

        /*based on that serviceIds and fetch service area names */;
        List<String> serviceNames = serviceRepository.findServiceNamesByIds(longList);
        postpaidPlanList.stream().peek(plan -> plan.setServiceName(serviceNames.toString())).collect(Collectors.toList());

//        for (PostpaidPlan plan : postpaidPlanList) {
//            plan.setServiceName(serviceRepository.findById(plan.getServiceId().longValue()).get().getServiceName());
//        }

        BusinessUnit businessUnit = new BusinessUnit();
        List<postpaidPlanFetchPojo> postpaidPlanList1 = new ArrayList<>();
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        if ((Objects.isNull(businessUnit.getPlanBindingType())) || (CommonConstants.PREDEFINED).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
            // TODO: pass mvnoID manually 6/5/2025
            postpaidPlanList1 = IterableUtils.toList(postpaidPlanList).stream().filter(postpaidPlan -> postpaidPlan.getMvnoId() == 1 || mvnoId == 1 || postpaidPlan.getMvnoId() == mvnoId &&
                    (Objects.isNull(postpaidPlan.getBusinessType()) || (CommonConstants.RETAIL).equalsIgnoreCase(postpaidPlan.getBusinessType()))).collect(Collectors.toList());
        } else if ((CommonConstants.ON_DEMAND).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
            // TODO: pass mvnoID manually 6/5/2025
            postpaidPlanList1 = IterableUtils.toList(postpaidPlanList).stream().filter(postpaidPlan -> postpaidPlan.getMvnoId() == 1 || mvnoId == 1 || postpaidPlan.getMvnoId() == mvnoId &&
                    ((CommonConstants.ENTERPRISE).equalsIgnoreCase(postpaidPlan.getBusinessType()))).collect(Collectors.toList());
        }
//        return entityRepository.findAllByStatusAndIsDeleteIsFalseOrderByIdDesc(CommonConstants.ACTIVE_STATUS).stream().filter(plan -> plan.getMvnoId() == getMvnoIdFromCurrentStaff() || plan.getMvnoId() == null).collect(Collectors.toList());
        // return IterableUtils.toList(entityRepository.findAll(booleanExpression)).stream().filter(postpaidPlan -> postpaidPlan.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || postpaidPlan.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
//        return entityRepository.findAllByStatusAndIsDeleteIsFalseOrderByIdDesc(CommonConstants.ACTIVE_STATUS).stream().filter(plan -> plan.getMvnoId() == getMvnoIdFromCurrentStaff() || plan.getMvnoId() == null).collect(Collectors.toList());
        return postpaidPlanList1;
    }

    public List<PostpaidPlan> getAllEntities() {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAllByIsDeleteIsFalseOrderByIdDesc().stream().filter(plan -> plan.getMvnoId() == getMvnoIdFromCurrentStaff(null) || plan.getMvnoId() == null).collect(Collectors.toList());
    }

    public List<PostpaidPlan> getAllPlanList() {
        List<PostpaidPlan> postpaidPlanList = entityRepository.findAllByIsDeleteIsFalseOrderByIdDesc();
// TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1) return postpaidPlanList;
        else
            // TODO: pass mvnoID manually 6/5/2025
            return postpaidPlanList.stream().filter(postpaidPlan -> (postpaidPlan.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || postpaidPlan.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()) && (postpaidPlan.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(postpaidPlan.getBuId()))).collect(Collectors.toList());
    }

    public List<PostpaidPlan> getAllPrepaidPlans() {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findByStatusAndPlantype(CommonConstants.YES_STATUS, CommonConstants.PLAN_TYPE_PREPAID).stream().filter(plan -> plan.getMvnoId() == getMvnoIdFromCurrentStaff(null) || plan.getMvnoId() == null).collect(Collectors.toList());
    }

    public List<PostpaidPlan> getAllRenewableDataPrepaidPlans() {
        PlanService planService = this.planServiceService.getAllServices().stream().filter(data -> data.getName().equalsIgnoreCase(SubscriberConstants.SERVICE_DATA)).findAny().orElse(null);
        String planGrp = "Renew";
        if (planService != null && planGrp.trim().length() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.findAllByStatusAndPlantypeAndServiceIdAndPlanGroup(CommonConstants.YES_STATUS, CommonConstants.PLAN_TYPE_PREPAID, planService.getId(), planGrp).stream().filter(plan -> plan.getMvnoId() == getMvnoIdFromCurrentStaff(null) || plan.getMvnoId() == null).collect(Collectors.toList());
        }
        return null;
    }

    public List<PostpaidPlan> getAllPostpaidPlans() {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findByStatusAndPlantype(CommonConstants.YES_STATUS, CommonConstants.PLAN_TYPE_POSTPAID).stream().filter(plan -> plan.getMvnoId() == getMvnoIdFromCurrentStaff(null) || plan.getMvnoId() == null).collect(Collectors.toList());
    }

    public boolean deleteVerification(Integer id) throws Exception {
        Integer count = entityRepository.deleteverified(id);
        Boolean flag = false;
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    public void deletePostpaidPlan(Integer id,Integer mvnoId) throws Exception {
        Boolean flag = this.deleteVerification(id);
        if (flag == true) {
            PostpaidPlanPojo postpaidPlanPojo = postpaidPlanMapper.domainToDTO(get(id,mvnoId), new CycleAvoidingMappingContext());
            postpaidPlanPojo.setIsDelete(true);
            PostpaidPlanMessage message = new PostpaidPlanMessage(postpaidPlanPojo);
            //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_POSTPAIDPLAN);
            kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
            entityRepository.deleteById(id);

            //check other plan has same serviceArea or not
            PostpaidPlan postpaidPlan = entityRepository.findById(id).orElse(null);


        } else {
            throw new RuntimeException(DeleteContant.POSTPAID_PLAN_DELETE_EXIST);
        }

    }

    public PostpaidPlan getPostpaidPlanForAdd() {
        return new PostpaidPlan();
    }

    public PostpaidPlan getPostpaidPlanForEdit(Integer id, Integer mvmoId) throws Exception {
        return get(id,mvmoId);
    }

    public PostpaidPlanCharge getPostpaidPlanCharge() {
        return new PostpaidPlanCharge();
    }

    public PostpaidPlan deleteSlab(PostpaidPlan entity, int index) {
        entity.getChargeList().remove(index);
        return entity;
    }

    public List<PostpaidPlanCharge> getPostpaidPlanChargeList() {
        return new ArrayList<>();
    }

    @Transactional
    public PostpaidPlan savePostpaidPlan(PostpaidPlan postpaidPlan) throws Exception {
        String SUBMODULE = MODULE + "[savePostpaidPlan()]";
        Double dbr = 0.0;
        DecimalFormat df = new DecimalFormat("0.00");
        try {
            /*for (PostpaidPlanCharge item : postpaidPlan.getChargeList()) {
                item.setPlan(postpaidPlan);
                if (item.getCharge() != null) {
                    dbr = dbr + item.getCharge().getDbr();
                    postpaidPlan.setDbr(Double.parseDouble(new DecimalFormat("##.##").format(dbr)));
                }
            }*/


            Double offerPrice = postpaidPlan.getOfferprice();
            Double validity = postpaidPlan.getValidity();
            if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_MONTHS))
                validity = 30 * validity;
            if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_YEARS))
                validity = 365 * validity;
            dbr = offerPrice / validity;
            postpaidPlan.setDbr(Double.parseDouble(df.format(dbr)));

            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != null) {
                // TODO: pass mvnoID manually 6/5/2025
                postpaidPlan.setMvnoId(getMvnoIdFromCurrentStaff(null));
            }
            if (getBUIdsFromCurrentStaff().size() == 1) postpaidPlan.setBuId(getBUIdsFromCurrentStaff().get(0));
//            if(postpaidPlan.getCasId()!=null)
//                postpaidPlan.setCasId(postpaidPlan.getCasId());
//            if(postpaidPlan.getCasPlanMappingId()!=null)
//                postpaidPlan.setCasPlanMappingId(postpaidPlan.getCasPlanMappingId());
            PostpaidPlan save = entityRepository.save(postpaidPlan);
            checkServiceAreaBind(save);
            return save;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public boolean duplicateVerifyAtSave(String name,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean isSameStaff(String name,Integer mvnoId) throws Exception {
        boolean flag = true;
        Integer userId = getLoggedInUserId();
        // TODO: pass mvnoID manually 6/5/2025
        if (name != null) {
            name = name.trim();
            Integer createdById;
            if (getBUIdsFromCurrentStaff().size() == 0) {
                createdById = entityRepository.getCreatedBy(name, mvnoId);
            } else {
                createdById = entityRepository.getCreatedBy(name, mvnoId, getBUIdsFromCurrentStaff());
            }
            System.out.println("Flag for plan is approved by created by id : "+createdById);
            System.out.println("Flag for plan is approved by user id : "+userId);
            if (createdById != null && !userId.equals(createdById)) {
                flag = false;
            }
        }
        System.out.println("Flag for plan is approved by : "+flag);
        return flag;
    }

    public List<Productplanmappingdto> saveProductPlanPlanMappingList(List<Productplanmappingdto> productplanmapping, Long planId) throws Exception {

        List<Productplanmapping> productplanmappingList = new ArrayList<>();
        //deteRecord
        if (productplanmapping.size() != 0) {
            List<Productplanmapping> productplanmappings = productPlanMappingRepository.findAllByPlanId(productplanmapping.get(0).getPlanId());
            List<Long> idsList = productplanmappings.stream().map(Productplanmapping::getId).collect(Collectors.toList());
            List<Long> productPlanMappingIdList = productplanmapping.stream().map(Productplanmappingdto::getId).collect(Collectors.toList());
            if (productplanmappings.size() != 0) {
                ArrayList<Long> deletedItems = new ArrayList<Long>(idsList);
                deletedItems.removeAll(productPlanMappingIdList);
                deletedItems.stream().forEach(r -> {
                    productPlanMappingRepository.deleteById(r);
                });
            }
        } else {
            List<Productplanmapping> productplanmappings = productPlanMappingRepository.findAllByPlanId(planId);
            productplanmappings.stream().forEach(productplanmapping1 -> {
                productPlanMappingRepository.delete(productplanmapping1);
            });
        }
        for (Productplanmappingdto dto : productplanmapping) {
            if (dto.getName() == null) {
                if (dto.getProductId() != null) {
                    dto.setName(getRandomNumber(productService.getEntityById(dto.getProductId().longValue(),dto.getMvnoId()).getName(), "-", ""));
                } else if (dto.getProductCategoryId() != null) {
                    dto.setName(getRandomNumber(productCategoryService.getEntityById(dto.getProductCategoryId().longValue(),dto.getMvnoId()).getName(), "-", ""));
                }
                productplanmappingList.add(productPlanMappingRepository.save(productplanmappingmapper.dtoToDomain(dto, new CycleAvoidingMappingContext())));
            }
        }
        return productplanmappingList.stream().map(productplanmapping1 -> productplanmappingmapper.domainToDTO(productplanmapping1, new CycleAvoidingMappingContext())).collect(Collectors.toList());

    }

    public String getRandomNumber(String flag1, String flag2, String flag3) {
        String flag = "";
        if (flag1 != null) {
            flag += flag1;
        }
        if (flag2 != null) {
            flag += flag2;
        }
        if (flag3 != null) {
            Productplanmapping productplanmapping = productPlanMappingRepository.findTopByOrderByIdDesc();
            if (productplanmapping == null) {
                flag += 1;
            } else {
                flag += productplanmapping.getId() + 1;
            }
        }
        return flag;
    }

    /*
    public List<Productplanmappingdto> saveProductPlanPlanMappingList(List<Productplanmappingdto> productplanmapping) throws Exception {

        List<Productplanmapping> productplanmappingList = new ArrayList<>();
        for (Productplanmappingdto dto : productplanmapping) {
            if (dto.getName() == null) {
                if (dto.getProductId() != null) {
                    dto.setName(getRandomNumber(productService.getEntityById(dto.getProductId().longValue()).getName(), "-", ""));
                } else {
                    dto.setName(getRandomNumber(productCategoryService.getEntityById(dto.getProductCategoryId().longValue()).getName(), "-", ""));
                }
                productplanmappingList.add(productPlanMappingRepository.save(productplanmappingmapper.dtoToDomain(dto, new CycleAvoidingMappingContext())));
            } else if ((dto.getName() != null)) {
                productplanmappingList.add(productPlanMappingRepository.save(productplanmappingmapper.dtoToDomain(dto, new CycleAvoidingMappingContext())));
            }
        }
        return productplanmappingList.stream().map(productplanmapping1 -> productplanmappingmapper.domainToDTO(productplanmapping1, new CycleAvoidingMappingContext())).collect(Collectors.toList());

    }
    */

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (mvnoId == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if (getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(mvnoId, 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, mvnoId, getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    public PostpaidPlanPojo save(PostpaidPlanPojo pojo) throws Exception {
        String SUBMODULE = MODULE + "[save()]";
//        PostpaidPlan oldObj = null;
//        if (pojo.getId() != null) {
//            oldObj = get(pojo.getId());
//        }
        try {
            //setChargeByPlanType(pojo);
            // TODO: pass mvnoID manually 6/5/2025
            pojo.setMvnoId(pojo.getMvnoId());
            // TODO: pass mvnoID manually 6/5/2025
            pojo.setMvnoName(mvnoRepository.findMvnoNameById(pojo.getMvnoId().longValue()));

            if (pojo.getCode().equals("") && pojo.getCode().length() == 0) {
                pojo.setCode(pojo.getName());
            }
            if (pojo.getChargeList() != null) {
                List<PostpaidPlanChargePojo> chargePojos = pojo.getChargeList();
                for (PostpaidPlanChargePojo postpaidPlanChargePojo : chargePojos) {
                    if (postpaidPlanChargePojo.getCharge().getId() == null) {
                        ChargePojo chargePojo = chargeService.save(postpaidPlanChargePojo.getCharge());
                        postpaidPlanChargePojo.setCharge(chargePojo);
                        String cacheKey = cacheKeys.CHARGE + chargePojo.getId();
                        cacheService.saveOrUpdateInCacheAsync(chargePojo,cacheKey);
                    } else {
                        Charge charge = chargeRepository.findById(postpaidPlanChargePojo.getCharge().getId()).get();
                        charge.setActualprice(postpaidPlanChargePojo.getCharge().getActualprice());
                        charge = chargeRepository.save(charge);
//                        ChargePojo chargePojo = chargeService.save(postpaidPlanChargePojo.getCharge());
                        postpaidPlanChargePojo.setCharge(chargeMapper.domainToDTO(charge, new CycleAvoidingMappingContext()));
                        String cacheKey = cacheKeys.CHARGE + charge.getId();
                        cacheService.saveOrUpdateInCacheAsync(charge,cacheKey);

                    }
//                        postpaidPlanChargeService.save(postpaidPlanChargeMapper.dtoToDomain(postpaidPlanChargePojo, new CycleAvoidingMappingContext()));
                }
                pojo.setChargeList(chargePojos);
            }

            List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllById(pojo.getServiceAreaIds())
                    .stream()
                    .map(id -> new ServiceArea(id))
                    .collect(Collectors.toList());

            if (pojo.getCategory().equalsIgnoreCase("Business Promotion")) {
                if (pojo.getNewOfferPrice() == null) {
                    throw new CustomValidationException(404, "Please add new offer Price", null);
                }
            }
            if (pojo.getPlantype().equalsIgnoreCase(CommonConstants.PLAN_TYPE_POSTPAID) && !pojo.getPlanGroup().equalsIgnoreCase("Volume Booster")
                    && !pojo.getPlanGroup().equalsIgnoreCase("DTV Addon") && !pojo.getPlanGroup().equalsIgnoreCase("Bandwidthbooster")) {
                List<PostpaidPlanChargePojo> unSortedList = pojo.getChargeList();
                List<PostpaidPlanChargePojo> sortedList = unSortedList.stream().sorted(Comparator.comparing(x -> x.getBillingCycle())).collect(Collectors.toList());
                Integer largestBillCycle = sortedList.get(sortedList.size() - 1).getBillingCycle();
                LocalDate endDate = LocalDate.now().plusMonths(largestBillCycle);
                Long validity = Duration.between(LocalDateTime.now(), endDate.atStartOfDay()).toDays();
                pojo.setValidity(Double.valueOf(validity));
            }
            if (pojo.getValidity() == 0) {
                throw new CustomValidationException(404, "Plan Validity can't be zero", null);
            }
//            if (pojo.getQospolicyid() == null) {
//                throw new CustomValidationException(400, "Qospolicy ID can't be null", null);
//            }
//
//            Optional<QOSPolicy> qosPolicy = qosPolicyRepository.findById(pojo.getQospolicyid());
//
//            if (!qosPolicy.isPresent()) {
//                throw new CustomValidationException(404, "Qospolicy not found for ID: " + pojo.getQospolicyid(), null);
//            }
//
            if (!CollectionUtils.isEmpty(serviceAreaList)) {
                pojo.setServiceAreaNameList(serviceAreaList.stream().map(data -> serviceAreaMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
            }
            List<Productplanmapping> productplanmappingList = productPlanMappingRepository.findAllById(pojo.getProductplanmappingids());

            if (!CollectionUtils.isEmpty(productplanmappingList)) {
                pojo.setProductplanmappingList(productplanmappingList.stream().map(data -> productplanmappingmapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
            }
            BusinessUnit businessUnit = new BusinessUnit();
            if (getBUIdsFromCurrentStaff().size() == 1) {
                businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
            }
            if (getBUIdsFromCurrentStaff().size() != 0) {
                if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.PREDEFINED)) {
                    pojo.setBusinessType(CommonConstants.RETAIL);
                } else if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND)) {
                    pojo.setBusinessType(CommonConstants.ENTERPRISE);
                }
            }

            PostpaidPlan obj = postpaidPlanMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext());
//            if(oldObj!=null) {
//                log.info("PostpaidPlan update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
//            }
            obj.setTimebasepolicyId(pojo.getTimebasepolicyId());
            obj.setServiceAreaNameList(serviceAreaList);
//            if(pojo.getCasId()!=null)
//                obj.setCasId(pojo.getCasId());
//            if(pojo.getCasPlanMappingId()!=null)
//                obj.setCasPlanMappingId(pojo.getCasPlanMappingId());

            if (!pojo.getPlanQosMappingEntityList().isEmpty()) {
                if (Objects.nonNull(pojo.getPlanQosMappingEntityList().get(0).getPlanid())) {
                    List<PlanQosMappingEntity> planQosMappingEntities = planQosMappingRepo.findAllByPlanId(pojo.getPlanQosMappingEntityList().get(0).getPlanid().longValue());

                    if (!planQosMappingEntities.isEmpty()) {
                        planQosMappingEntities.stream().forEach(planQosMapping -> planQosMapping.setIsdelete(true));
                        planQosMappingRepo.saveAll(planQosMappingEntities);
                    }
                }
            }
            obj = savePostpaidPlan(obj);

            PostpaidPlan finalObj = obj;
//            pojo.getProductplanmappingList().forEach(productplanmappingdto -> productplanmappingdto.setPlanId(Long.valueOf(finalObj.getId())));
//           obj.setProductplanmappingList(saveProductPlanPlanMappingList(pojo.getProductplanmappingList(), Long.valueOf(finalObj.getId())));
            List<PlanQosMappingEntity> planQosMappingEntityList = new ArrayList<>();
            if (pojo.getPlanQosMappingEntityList() != null) {
                List<PlanQosMappingPojo> planQosMappingPojos = pojo.getPlanQosMappingEntityList();
                planQosMappingPojos.forEach(planQosMappingPojo -> planQosMappingPojo.setPlanid(Math.toIntExact(Long.valueOf(finalObj.getId()))));
                planQosMappingEntityList = planQosMappingMapper.dtoToDomain(planQosMappingPojos, new CycleAvoidingMappingContext());
                planQosMappingRepo.saveAll(planQosMappingEntityList);
            }

            TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO();
            taxDetailCountReqDTO.setPlanId(obj.getId());
            ClientService clientService = clientServiceSrv.getByName(ChargeConstants.CLIENT_SERVICE_LOCATION_ID);
            if (clientService != null) {
                taxDetailCountReqDTO.setLocationId(Integer.parseInt(clientService.getValue()));
            }
            obj.setTaxamount(taxService.taxCalculationByPlan(taxDetailCountReqDTO, finalObj.getChargeList()));
            //    obj = update(obj);

//            obj.setStatus("NewActivation");

            if (obj.getNextTeamHierarchyMapping() == null && obj.getNextStaff() == null) {
                if (obj.getStatus() != null && !"".equals(obj.getStatus())) {
                    if (obj.getStatus().equalsIgnoreCase("NewActivation")) {
                        pojo.setId(obj.getId());
                        StaffUser assignedStaff = null;
                        if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,pojo.getMvnoId()).equals("TRUE")) {
                            Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(obj.getMvnoId(), obj.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PLAN, CommonConstants.HIERARCHY_TYPE, false, true, pojo);
                            int staffId = 0;
                            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                staffId = Integer.parseInt(map.get("staffId"));
                                assignedStaff = staffUserRepository.findById(staffId).get();
                                obj.setNextTeamHierarchyMapping(Integer.valueOf(map.get("nextTatMappingId")));
                                obj.setNextStaff(staffId);
                                if (obj != null) {
                                    String action = CommonConstants.WORKFLOW_MSG_ACTION.PLAN + " with Name : " + " ' " + pojo.getName() + " ' ";
                                    hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(), action,assignedStaff.getId().longValue());

                                }
                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, obj.getId(), obj.getName(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                if (assignedStaff.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                                    if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                                        map.put("tat_id", map.get("current_tat_id"));
                                    tatUtils.saveOrUpdateDataForTatMatrix(map, assignedStaff, obj.getId(), null);
                                }
                            } else {
                                StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                                assignedStaff = currentStaff;
                                obj.setNextTeamHierarchyMapping(null);
                                obj.setNextStaff(currentStaff.getId());
                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, obj.getId(), obj.getName(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                            }
                        } else {
                            StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                            assignedStaff = currentStaff;
                            obj.setNextTeamHierarchyMapping(null);
                            obj.setNextStaff(currentStaff.getId());
                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, obj.getId(), obj.getName(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                        }
                    }
                }
            }
            PostpaidPlan savedPlan = update(obj);

            //store postpaid plan in cache
            String cacheKey = cacheKeys.POSTPAIDPLAN + savedPlan.getId();
            cacheService.saveOrUpdateInCacheAsync(savedPlan,cacheKey);
            PostpaidPlanPojo pojo2 = postpaidPlanMapper.domainToDTO(obj, new CycleAvoidingMappingContext());
            pojo2.setServiceAreaNameList(pojo.getServiceAreaNameList());
            pojo2.setServiceAreaIds(pojo.getServiceAreaIds());
            pojo2.setTimebasepolicyId(pojo.getTimebasepolicyId());
            pojo2.setTimebasepolicyName(pojo.getTimebasepolicyName());
            pojo2.setPlanQosMappingEntityList(pojo.getPlanQosMappingEntityList());
            pojo2.setProductplanmappingList(pojo.getProductplanmappingList());
            PostpaidPlanMessage message = new PostpaidPlanMessage(pojo2);
            //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_POSTPAIDPLAN);
            kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
            //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_POSTPAIDPLAN_FOR_INTEGRATION);
            return pojo2;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Async
    public CompletableFuture<Void> addCache(Object obj) {
        try {
            // Generate cache key using the object ID (assuming obj has a getId method)
            String cacheKey = obj.getClass().getSimpleName() + ":" + ((Charge) obj).getId();  // Example for Charge object

            // Store the object in cache
            cacheService.putInCache(cacheKey, obj);

            System.out.println(obj.getClass().getSimpleName() + " added to cache with key: " + cacheKey);
        } catch (Exception e) {
            System.err.println("Failed to add object to cache: " + e.getMessage());
        }

        return CompletableFuture.completedFuture(null);
    }


    public PostpaidPlanPojo saveFromMigration(PostpaidPlanPojo pojo) throws Exception {
        String SUBMODULE = MODULE + "[save()]";
//        PostpaidPlan oldObj = null;
//        if (pojo.getId() != null) {
//            oldObj = get(pojo.getId());
//        }
        try {
            //setChargeByPlanType(pojo);
            // TODO: pass mvnoID manually 6/5/2025
            pojo.setMvnoId(pojo.getMvnoId());
            // TODO: pass mvnoID manually 6/5/2025
            pojo.setMvnoName(mvnoRepository.findMvnoNameById(pojo.getMvnoId().longValue()));

            if (pojo.getCode().equals("") && pojo.getCode().length() == 0) {
                pojo.setCode(pojo.getName());
            }
            if (pojo.getChargeList() != null) {
                List<PostpaidPlanChargePojo> chargePojos = pojo.getChargeList();
                for (PostpaidPlanChargePojo postpaidPlanChargePojo : chargePojos) {
                    if (postpaidPlanChargePojo.getCharge().getId() == null) {
                        ChargePojo chargePojo = chargeService.save(postpaidPlanChargePojo.getCharge());
                        postpaidPlanChargePojo.setCharge(chargePojo);
                    } else {
                        Charge charge = chargeRepository.findById(postpaidPlanChargePojo.getCharge().getId()).get();
                        charge.setActualprice(postpaidPlanChargePojo.getCharge().getActualprice());
                        chargeRepository.save(charge);

                    }
//                        postpaidPlanChargeService.save(postpaidPlanChargeMapper.dtoToDomain(postpaidPlanChargePojo, new CycleAvoidingMappingContext()));
                }
            }
            if (pojo.getServiceAreaNameList() != null) {
                List<Long> serviceAreaIds = new ArrayList<>();
                for (ServiceAreaDTO serviceArea : pojo.getServiceAreaNameList()) {
                    serviceAreaIds.add(serviceArea.getId());
                }
                pojo.setServiceAreaIds(serviceAreaIds);
            }
            List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllById(pojo.getServiceAreaIds())
                    .stream()
                    .map(id -> new ServiceArea(id))
                    .collect(Collectors.toList());
            if (pojo.getCategory().equalsIgnoreCase("Business Promotion")) {
                if (pojo.getNewOfferPrice() == null) {
                    throw new CustomValidationException(404, "Please add new offer Price", null);
                }
            }
            if (pojo.getPlantype().equalsIgnoreCase(CommonConstants.PLAN_TYPE_POSTPAID) && !pojo.getPlanGroup().equalsIgnoreCase("Volume Booster")
                    && !pojo.getPlanGroup().equalsIgnoreCase("DTV Addon") && !pojo.getPlanGroup().equalsIgnoreCase("Bandwidthbooster")) {
                List<PostpaidPlanChargePojo> unSortedList = pojo.getChargeList();
                List<PostpaidPlanChargePojo> sortedList = unSortedList.stream().sorted(Comparator.comparing(x -> x.getBillingCycle())).collect(Collectors.toList());
                Integer largestBillCycle = sortedList.get(sortedList.size() - 1).getBillingCycle();
                LocalDate endDate = LocalDate.now().plusMonths(largestBillCycle);
                Long validity = Duration.between(LocalDateTime.now(), endDate.atStartOfDay()).toDays();
                pojo.setValidity(Double.valueOf(validity));
            }
            if (pojo.getValidity() == 0) {
                throw new CustomValidationException(404, "Plan Validity can't be zero", null);
            }
            if (!CollectionUtils.isEmpty(serviceAreaList)) {
                pojo.setServiceAreaNameList(serviceAreaList.stream().map(data -> serviceAreaMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
            }
            List<Productplanmapping> productplanmappingList = productPlanMappingRepository.findAllById(pojo.getProductplanmappingids());

            if (!CollectionUtils.isEmpty(productplanmappingList)) {
                pojo.setProductplanmappingList(productplanmappingList.stream().map(data -> productplanmappingmapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
            }
            BusinessUnit businessUnit = new BusinessUnit();
            if (getBUIdsFromCurrentStaff().size() == 1) {
                businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
            }
            if (getBUIdsFromCurrentStaff().size() != 0) {
                if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.PREDEFINED)) {
                    pojo.setBusinessType(CommonConstants.RETAIL);
                } else if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND)) {
                    pojo.setBusinessType(CommonConstants.ENTERPRISE);
                }
            }

            PostpaidPlan obj = postpaidPlanMapper.dtoToDomain(pojo, new CycleAvoidingMappingContext());
//            if(oldObj!=null) {
//                log.info("PostpaidPlan update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
//            }
            obj.setTimebasepolicyId(pojo.getTimebasepolicyId());
            obj.setServiceAreaNameList(serviceAreaList);
//            if(pojo.getCasId()!=null)
//                obj.setCasId(pojo.getCasId());
//            if(pojo.getCasPlanMappingId()!=null)
//                obj.setCasPlanMappingId(pojo.getCasPlanMappingId());

            if (!pojo.getPlanQosMappingEntityList().isEmpty()) {
                if (Objects.nonNull(pojo.getPlanQosMappingEntityList().get(0).getPlanid())) {
                    List<PlanQosMappingEntity> planQosMappingEntities = planQosMappingRepo.findAllByPlanId(pojo.getPlanQosMappingEntityList().get(0).getPlanid().longValue());

                    if (!planQosMappingEntities.isEmpty()) {
                        planQosMappingEntities.stream().forEach(planQosMapping -> planQosMapping.setIsdelete(true));
                        planQosMappingRepo.saveAll(planQosMappingEntities);
                    }
                }
            }
            obj = savePostpaidPlan(obj);

            PostpaidPlan finalObj = obj;
//            pojo.getProductplanmappingList().forEach(productplanmappingdto -> productplanmappingdto.setPlanId(Long.valueOf(finalObj.getId())));
//           obj.setProductplanmappingList(saveProductPlanPlanMappingList(pojo.getProductplanmappingList(), Long.valueOf(finalObj.getId())));
            List<PlanQosMappingEntity> planQosMappingEntityList = new ArrayList<>();
            if (pojo.getPlanQosMappingEntityList() != null) {
                List<PlanQosMappingPojo> planQosMappingPojos = pojo.getPlanQosMappingEntityList();
                planQosMappingPojos.forEach(planQosMappingPojo -> planQosMappingPojo.setPlanid(Math.toIntExact(Long.valueOf(finalObj.getId()))));
                planQosMappingEntityList = planQosMappingMapper.dtoToDomain(planQosMappingPojos, new CycleAvoidingMappingContext());
                planQosMappingRepo.saveAll(planQosMappingEntityList);
            }

            TaxDetailCountReqDTO taxDetailCountReqDTO = new TaxDetailCountReqDTO();
            taxDetailCountReqDTO.setPlanId(obj.getId());
            ClientService clientService = clientServiceSrv.getByName(ChargeConstants.CLIENT_SERVICE_LOCATION_ID);
            if (clientService != null) {
                taxDetailCountReqDTO.setLocationId(Integer.parseInt(clientService.getValue()));
            }
            obj.setTaxamount(taxService.taxCalculationByPlan(taxDetailCountReqDTO, finalObj.getChargeList()));
            //    obj = update(obj);

//            obj.setStatus("NewActivation");

            if (obj.getNextTeamHierarchyMapping() == null && obj.getNextStaff() == null) {
                if (obj.getStatus() != null && !"".equals(obj.getStatus())) {
                    if (obj.getStatus().equalsIgnoreCase("NewActivation")) {
                        pojo.setId(obj.getId());
                        StaffUser assignedStaff = null;
                        if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                            Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(obj.getMvnoId(), obj.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PLAN, CommonConstants.HIERARCHY_TYPE, false, true, pojo);
                            int staffId = 0;
                            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                staffId = Integer.parseInt(map.get("staffId"));
                                assignedStaff = staffUserRepository.findById(staffId).get();
                                obj.setNextTeamHierarchyMapping(Integer.valueOf(map.get("nextTatMappingId")));
                                obj.setNextStaff(staffId);
                                if (obj != null) {
                                    String action = CommonConstants.WORKFLOW_MSG_ACTION.PLAN + " with Name : " + " ' " + pojo.getName() + " ' ";
                                    hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(), action,assignedStaff.getId().longValue());

                                }
                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, obj.getId(), obj.getName(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                if (assignedStaff.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                                    if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                                        map.put("tat_id", map.get("current_tat_id"));
                                    tatUtils.saveOrUpdateDataForTatMatrix(map, assignedStaff, obj.getId(), null);
                                }
                            } else {
                                StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                                assignedStaff = currentStaff;
                                obj.setNextTeamHierarchyMapping(null);
                                obj.setNextStaff(currentStaff.getId());
                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, obj.getId(), obj.getName(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                            }
                        } else {
                            StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                            assignedStaff = currentStaff;
                            obj.setNextTeamHierarchyMapping(null);
                            obj.setNextStaff(currentStaff.getId());
                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, obj.getId(), obj.getName(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                        }
                    }
                }
            }
            update(obj);
            PostpaidPlanPojo pojo2 = postpaidPlanMapper.domainToDTO(obj, new CycleAvoidingMappingContext());
            pojo2.setServiceAreaNameList(pojo.getServiceAreaNameList());
            pojo2.setServiceAreaIds(pojo.getServiceAreaIds());
            pojo2.setTimebasepolicyId(pojo.getTimebasepolicyId());
            pojo2.setTimebasepolicyName(pojo.getTimebasepolicyName());
            pojo2.setPlanQosMappingEntityList(pojo.getPlanQosMappingEntityList());
            pojo2.setProductplanmappingList(pojo.getProductplanmappingList());
            PostpaidPlanMessage message = new PostpaidPlanMessage(pojo2);
            //          kafkaMessageSender.send(new KafkaMessageData(message, PostpaidPlanMessage.class.getSimpleName()));
//            messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_POSTPAIDPLAN);
            //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_POSTPAIDPLAN_FOR_INTEGRATION);
            return pojo2;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Transactional
    public void setChargeByPlanType(PostpaidPlanPojo entity,Integer mvnoId) throws Exception {

        //Save
        String SUBMODULE = MODULE + "[setChargeByPlanType()]";
        try {
            if (null != entity.getOfferprice() && null == entity.getId()) {
                PostpaidPlanChargePojo postpaidPlanChargePojo = new PostpaidPlanChargePojo();
                ChargePojo chargePojo = new ChargePojo();
                chargePojo.setName(null != entity.getDisplayName() ? entity.getDisplayName() : "");
                if (entity.getSaccode() != null) {
                    chargePojo.setSaccode(entity.getSaccode());
                }
                chargePojo.setActualprice(entity.getOfferprice());
                chargePojo.setPrice(entity.getOfferprice());
                if (null != entity.getPlantype() && CommonConstants.PLAN_TYPE_POSTPAID.equalsIgnoreCase(entity.getPlantype()))
                    chargePojo.setChargetype(CommonConstants.CHARGE_RECURRING_AUTO);
                if (null != entity.getPlantype() && CommonConstants.PLAN_TYPE_PREPAID.equalsIgnoreCase(entity.getPlantype()))
                    chargePojo.setChargetype(CommonConstants.CHARGE_ADVANCE_AUTO);
                if (null != entity.getTaxId()) {
                    chargePojo.setTaxid(entity.getTaxId());
                }
                chargePojo = chargeService.save(chargePojo);
                postpaidPlanChargePojo.setCharge(chargePojo);
                postpaidPlanChargePojo.setCreatedate(LocalDateTime.now());
                postpaidPlanChargePojo.setPlan(entity);
                entity.setChargeList(Collections.singletonList(postpaidPlanChargePojo));
            }

            //Update
            if (null != entity.getOfferprice() && null != entity.getId()) {

                PostpaidPlanPojo planPojo = postpaidPlanMapper.domainToDTO(get(entity.getId(),mvnoId), new CycleAvoidingMappingContext());

                if (null != planPojo) {
                    entity.setChargeList(planPojo.getChargeList());
                    if (null != entity.getChargeList() && 0 < entity.getChargeList().size()) {
                        for (PostpaidPlanChargePojo postpaidPlanChargePojo : entity.getChargeList()) {

                            if (null != postpaidPlanChargePojo.getCharge()) {
                                if (null != entity.getOfferprice() && planPojo.getOfferprice() != entity.getOfferprice()) {
                                    postpaidPlanChargePojo.getCharge().setActualprice(entity.getOfferprice());
                                    postpaidPlanChargePojo.getCharge().setPrice(entity.getOfferprice());
                                }
                                if (null != entity.getDisplayName() && !planPojo.getDisplayName().equalsIgnoreCase(entity.getDisplayName())) {
                                    postpaidPlanChargePojo.getCharge().setName(entity.getDisplayName());
                                }
                                if (null != entity.getSaccode() && planPojo.getSaccode() != entity.getSaccode()) {
                                    postpaidPlanChargePojo.getCharge().setSaccode(entity.getSaccode());
                                }

                                if (null != entity.getDisplayName() && !planPojo.getDisplayName().equalsIgnoreCase(entity.getDisplayName())) {
                                    postpaidPlanChargePojo.getCharge().setName(entity.getDisplayName());
                                }

                                if (null != entity.getSaccode() && !planPojo.getSaccode().equalsIgnoreCase(entity.getSaccode())) {
                                    postpaidPlanChargePojo.getCharge().setSaccode(entity.getSaccode());
                                }

                                if (null == entity.getTaxId()) postpaidPlanChargePojo.getCharge().setTaxid(null);
                                else {
                                    postpaidPlanChargePojo.getCharge().setTaxid(entity.getTaxId());
                                    Tax tax = taxRepository.findById(entity.getTaxId()).get();
                                    if (null != tax && !tax.getIsDelete()) {
                                        ChargePojo charge = postpaidPlanChargePojo.getCharge();
                                        postpaidPlanChargePojo.getCharge().setTaxamount(charge.getPrice() * (18.0f / 100.0f));
                                    }
                                }

                                if (null != entity.getPlantype() && !planPojo.getPlantype().equalsIgnoreCase(entity.getPlantype())) {
                                    if (CommonConstants.PLAN_TYPE_POSTPAID.equalsIgnoreCase(entity.getPlantype()))
                                        postpaidPlanChargePojo.getCharge().setChargetype(CommonConstants.CHARGE_RECURRING_AUTO);
                                    if (CommonConstants.PLAN_TYPE_PREPAID.equalsIgnoreCase(entity.getPlantype()))
                                        postpaidPlanChargePojo.getCharge().setChargetype(CommonConstants.CHARGE_ADVANCE_AUTO);
                                    if (!planPojo.getName().equalsIgnoreCase(entity.getName()))
                                        postpaidPlanChargePojo.getCharge().setName(null != entity.getDisplayName() ? entity.getDisplayName() : "");
                                    planPojo.setPlantype(entity.getPlantype());

                                }

                                Charge charge = chargeService.update(chargeMapper.dtoToDomain(postpaidPlanChargePojo.getCharge(), new CycleAvoidingMappingContext()));
                                postpaidPlanChargePojo.setCharge(chargeMapper.domainToDTO(charge, new CycleAvoidingMappingContext()));
                                postpaidPlanChargePojo.setPlan(planPojo);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public PostpaidPlan convertPostpaidPlanPojoToPostpaidPlanModel(PostpaidPlanPojo postpaidPlanPojo) throws Exception {
        String SUBMODULE = MODULE + "[convertPostpaidPlanPojoToPostpaidPlanModel()]";
        PostpaidPlan postpaidPlan = new PostpaidPlan();
        try {
            if (postpaidPlanPojo.getId() != null) {
                postpaidPlan.setId(postpaidPlanPojo.getId());
            }
            postpaidPlan.setName(postpaidPlanPojo.getName());
            postpaidPlan.setDisplayName(postpaidPlanPojo.getDisplayName());
            postpaidPlan.setCategory(postpaidPlanPojo.getCategory());
            if (postpaidPlanPojo.getCode() == null) {
                postpaidPlan.setCode(postpaidPlanPojo.getName());
            } else {
                postpaidPlan.setCode(postpaidPlanPojo.getCode());
            }
            postpaidPlan.setDesc(postpaidPlanPojo.getDesc());
            postpaidPlan.setMaxChild(postpaidPlanPojo.getMaxChild());
            postpaidPlan.setStartDate(postpaidPlanPojo.getStartDate());
            postpaidPlan.setEndDate(postpaidPlanPojo.getEndDate());
            postpaidPlan.setQuotatype(postpaidPlanPojo.getQuotatype());
            postpaidPlan.setQuota(postpaidPlanPojo.getQuota());
            postpaidPlan.setQuotaUnit(postpaidPlanPojo.getQuotaUnit());
            postpaidPlan.setQuotatime(postpaidPlanPojo.getQuotatime());
            postpaidPlan.setQuotatime(postpaidPlanPojo.getQuotatime());
            postpaidPlan.setStatus(postpaidPlanPojo.getStatus());
            postpaidPlan.setPlanStatus(postpaidPlanPojo.getPlanStatus());
            postpaidPlan.setTaxId(postpaidPlanPojo.getTaxId());
            postpaidPlan.setServiceId(postpaidPlanPojo.getServiceId());
            postpaidPlan.setTimebasepolicyId(postpaidPlanPojo.getTimebasepolicyId());
            postpaidPlan.setPlantype(postpaidPlanPojo.getPlantype());
            postpaidPlan.setSaccode(postpaidPlanPojo.getSaccode());
            postpaidPlan.setMaxconcurrentsession(postpaidPlanPojo.getMaxconcurrentsession());
            postpaidPlan.setMode(postpaidPlanPojo.getMode());
            postpaidPlan.setAccessibility(postpaidPlanPojo.getAccessibility());
            postpaidPlan.setAllowdiscount(postpaidPlanPojo.getAllowdiscount());
            postpaidPlan.setInvoiceToOrg(postpaidPlanPojo.getInvoiceToOrg());
            postpaidPlan.setRequiredApproval(postpaidPlanPojo.getRequiredApproval());
            // postpaidPlan.setCasPlanMappingId(postpaidPlan.getCasPlanMappingId());
            //postpaidPlan.setCasId(postpaidPlan.getCasId());
        /*if (null != postpaidPlanPojo.getQospolicyid()) {
            QOSPolicyDTO qosPolicy = qosPolicyService.getEntityById(postpaidPlanPojo.getQospolicyid(), false);
            if (null != qosPolicy)
                postpaidPlan.setQospolicy(qosPolicyMapper.dtoToDomain(qosPolicy, new CycleAvoidingMappingContext()));
        }
        if (null != postpaidPlanPojo.getRadiusprofileIds() && 0 < postpaidPlanPojo.getRadiusprofileIds().size()) {
            postpaidPlan.setRadiusprofile(radiusProfileService.getAllEntities(postpaidPlanPojo.getRadiusprofileIds()));
        }*/
            postpaidPlan.setOfferprice(postpaidPlanPojo.getOfferprice());
            postpaidPlan.setNewOfferPrice(postpaidPlanPojo.getNewOfferPrice());
            postpaidPlan.setAttachedToAllHotSpots(postpaidPlanPojo.getAttachedToAllHotSpots());


            if (postpaidPlanPojo.getChargeList() != null && postpaidPlanPojo.getChargeList().size() > 0) {
                PostpaidPlanCharge postpaidPlanCharge = null;
                ChargeService chargeService = SpringContext.getBean(ChargeService.class);
                for (PostpaidPlanChargePojo postpaidPlanChargePojo : postpaidPlanPojo.getChargeList()) {
                    postpaidPlanCharge = new PostpaidPlanCharge();
                    postpaidPlanCharge.setCharge(chargeService.convertChargePojoToChargeModel(postpaidPlanChargePojo.getCharge()));
                    postpaidPlanCharge.setBillingCycle(postpaidPlanChargePojo.getBillingCycle());
                    postpaidPlanCharge.setPlan(postpaidPlan);
                    postpaidPlan.getChargeList().add(postpaidPlanCharge);
                }
            }
            postpaidPlan.setTaxamount(postpaidPlanPojo.getTaxamount());
            //postpaidPlan.setChargeList(null);
            postpaidPlan.setUnitsOfValidity(postpaidPlanPojo.getUnitsOfValidity());
            if (postpaidPlanPojo.getValidity() != null) {
                postpaidPlan.setValidity(postpaidPlanPojo.getValidity());
            }
            if (postpaidPlanPojo.getNextTeamHierarchyMapping() != null)
                postpaidPlan.setNextTeamHierarchyMapping(postpaidPlanPojo.getNextTeamHierarchyMapping());
            if (postpaidPlanPojo.getUsageQuotaType() != null)
                postpaidPlan.setUsageQuotaType(postpaidPlanPojo.getUsageQuotaType());
            else
                postpaidPlan.setUsageQuotaType("TOTAL");
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return postpaidPlan;
    }

    public PostpaidPlanPojo convertPostpaidPlanModelToPostpaidPlanPojo(PostpaidPlan postpaidPlan) throws Exception {
        String SUBMODULE = MODULE + "[convertPostpaidPlanModelToPostpaidPlanPojo()]";
        PostpaidPlanPojo pojo = null;
        try {
            if (postpaidPlan != null) {
                pojo = new PostpaidPlanPojo();
                pojo.setId(postpaidPlan.getId());
                pojo.setName(postpaidPlan.getName());
                pojo.setDisplayName(postpaidPlan.getDisplayName());
                pojo.setCategory(postpaidPlan.getCategory());
                pojo.setCode(postpaidPlan.getCode());
                pojo.setDesc(postpaidPlan.getDesc());
                pojo.setStatus(postpaidPlan.getStatus());
                pojo.setPlanStatus(postpaidPlan.getPlanStatus());
                pojo.setStartDate(postpaidPlan.getStartDate());
                pojo.setEndDate(postpaidPlan.getEndDate());
                pojo.setQuota(postpaidPlan.getQuota());
                pojo.setQuotaUnit(postpaidPlan.getQuotaUnit());
                pojo.setTaxId(postpaidPlan.getTaxId());
                pojo.setServiceId(postpaidPlan.getServiceId());
                pojo.setTimebasepolicyId(postpaidPlan.getTimebasepolicyId());
                pojo.setServiceName(postpaidPlan.getServiceName());
                pojo.setTimebasepolicyName(postpaidPlan.getTimebasepolicyName());
                pojo.setPlantype(postpaidPlan.getPlantype());
                pojo.setMaxChild(postpaidPlan.getMaxChild());
                pojo.setQuotatype(postpaidPlan.getQuotatype());
                pojo.setQuotatime(postpaidPlan.getQuotatime());
                pojo.setQuotaunittime(postpaidPlan.getQuotaunittime());
                pojo.setQuotaResetInterval(postpaidPlan.getQuotaResetInterval());
                pojo.setAccessibility(postpaidPlan.getAccessibility());
                pojo.setAllowdiscount(postpaidPlan.isAllowdiscount());
                pojo.setInvoiceToOrg(postpaidPlan.getInvoiceToOrg());
                pojo.setRequiredApproval(postpaidPlan.getRequiredApproval());
                if (postpaidPlan.getValidity() != null) {
                    pojo.setValidity(postpaidPlan.getValidity());
                }
                //  pojo.setCasPlanMappingId(postpaidPlan.getCasPlanMappingId());
                //  pojo.setCasId(postpaidPlan.getCasId());
                if (null != postpaidPlan.getQospolicy()) {
                    pojo.setQospolicyid(postpaidPlan.getQospolicy().getId());
                    pojo.setQospolicyName(postpaidPlan.getQospolicy().getName());
                }
//            if (null != postpaidPlan.getRadiusprofile() && 0 < postpaidPlan.getRadiusprofile().size()) {
//                pojo.setRadiusprofileIds(radiusProfileService.convertResponseModelIntoPojo(postpaidPlan.getRadiusprofile())
//                        .stream().map(RadiusProfilePojo::getId).collect(Collectors.toList()));
//            }*/
                pojo.setSaccode(postpaidPlan.getSaccode());
                pojo.setMaxconcurrentsession(postpaidPlan.getMaxconcurrentsession());
                pojo.setOfferprice(postpaidPlan.getOfferprice());
                pojo.setAttachedToAllHotSpots(postpaidPlan.getAttachedToAllHotSpots());

                PostpaidPlanChargePojo postpaidPlanChargePojo = null;
                List<PostpaidPlanChargePojo> postpaidPlanChargePojoList = null;

                ChargeService chargeService = SpringContext.getBean(ChargeService.class);

                if (postpaidPlan.getChargeList() != null && postpaidPlan.getChargeList().size() > 0) {
                    postpaidPlanChargePojoList = new ArrayList<>();
                    for (PostpaidPlanCharge element : postpaidPlan.getChargeList()) {
                        postpaidPlanChargePojo = new PostpaidPlanChargePojo();
                        postpaidPlanChargePojo.setId(element.getId());
                        postpaidPlanChargePojo.setChargeprice(element.getChargeprice());
                        postpaidPlanChargePojo.setCharge(chargeService.convertChargeModelToChargePojo(element.getCharge()));
                        postpaidPlanChargePojo.setBillingCycle(element.getBillingCycle());
                        postpaidPlanChargePojoList.add(postpaidPlanChargePojo);
                    }
                    pojo.setChargeList(postpaidPlanChargePojoList);
                }

//                if (postpaidPlan.getChargeList() != null) {
//                    postpaidPlanChargePojoList = new ArrayList<>();
//                    for (PostpaidPlanCharge item : postpaidPlan.getChargeList()) {
//                        postpaidPlanChargePojo = new PostpaidPlanChargePojo();
//                        if (item.getPlan() != null) {
//                            postpaidPlanChargePojo.setPlanId(item.getPlan().getId());
//                        } else {
//                            postpaidPlanChargePojo.setPlanId(pojo.getId());
//                        }
//                        postpaidPlanChargePojo.setId(item.getId());
//                        postpaidPlanChargePojo.setChargeId(item.getCharge().getId());
//                        postpaidPlanChargePojo.setChargeName(item.getCharge().getName());
//                        postpaidPlanChargePojo.setBillingCycle(item.getBillingCycle());
//                        postpaidPlanChargePojo.setChargeprice(item.getChargeprice());
//                        postpaidPlanChargePojoList.add(postpaidPlanChargePojo);
//                    }
//                    pojo.setChargeList(postpaidPlanChargePojoList);
//                }

                pojo.setTaxamount(postpaidPlan.getTaxamount());
                pojo.setMode(postpaidPlan.getMode());
                pojo.setUnitsOfValidity(postpaidPlan.getUnitsOfValidity());
                if (postpaidPlan.getPlanGroup() != null) {
                    pojo.setPlanGroup(postpaidPlan.getPlanGroup());
                }
                if (postpaidPlan.getNextTeamHierarchyMapping() != null)
                    pojo.setNextTeamHierarchyMapping(postpaidPlan.getNextTeamHierarchyMapping());
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public List<PostpaidPlanPojo> convertResponseModelIntoPojo(List<PostpaidPlan> postpaidPlanList) throws Exception {
        String SUBMODULE = MODULE + "[convertResponseModelIntoPojo()]";
        List<PostpaidPlanPojo> pojoListRes = new ArrayList<>();
        try {
            if (postpaidPlanList != null && postpaidPlanList.size() > 0) {
                for (PostpaidPlan postpaidPlan : postpaidPlanList) {
                    pojoListRes.add(convertPostpaidPlanModelToPostpaidPlanPojo(postpaidPlan));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }

    public void validateRequest(PostpaidPlanPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
//        if (!(pojo.getStatus().equalsIgnoreCase("Y") || pojo.getStatus().equalsIgnoreCase("N") || pojo.getStatus().equalsIgnoreCase("A")
//                || pojo.getStatus().equalsIgnoreCase("S"))) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
//        }
        /*if (!(pojo.getCategory().equalsIgnoreCase("1") || pojo.getStatus().equalsIgnoreCase("2"))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.category"), null);
        }*/
        /*if (!(pojo.getQuotaUnit().equalsIgnoreCase("MB") || pojo.getQuotaUnit().equalsIgnoreCase("GB") || pojo.getQuotaUnit().equalsIgnoreCase("Minute") || pojo.getQuotaUnit().equalsIgnoreCase("Hour"))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.quota.unit"), null);
        }*/
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_ADD))) {
            if (pojo.getChargeList() != null && pojo.getChargeList().size() > 0) {
                List<Integer> chargeIds = new ArrayList<>();
                for (PostpaidPlanChargePojo postpaidPlanChargePojo : pojo.getChargeList()) {
                    if (chargeIds.contains(postpaidPlanChargePojo.getCharge().getId())) {
                        throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.postpaid.plan.duplicate.charge.found"), null);
                    }
                    if (postpaidPlanChargePojo.getCharge().getId() != null)
                        chargeIds.add(postpaidPlanChargePojo.getCharge().getId());
                }
            }
        }
        // Validation if user can create dtv plan but not add CAS mapping
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_ADD) || operation.equals(CommonConstants.OPERATION_UPDATE))) {
            if (pojo.getServiceId() != null) {
                PlanService planService = planServiceRepository.findById(pojo.getServiceId()).get();
                if (planService.getIs_dtv()) {
                    if (pojo.getPlanCasMappingList().size() == 0 || pojo.getPlanCasMappingList() == null) {
                        throw new CustomValidationException(APIConstants.FAIL, "Please add CAS Mapping for DTV Service Plan", null);
                    }
                }
            }
        }
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_ADD))) {
            if (pojo.getProductplanmappingList() != null || pojo.getProductplanmappingList().size() > 0) {
                List<Long> productIds = new ArrayList<>();
                for (Productplanmappingdto productplanmappingdto : pojo.getProductplanmappingList()) {
                    if (productIds.contains(productplanmappingdto.getProductId())) {
                        throw new CustomValidationException(APIConstants.FAIL, "Duplicate product is not allowed", null);
                    }
                    productIds.add(productplanmappingdto.getProductId());
                }
            }
        }
        if (null != pojo && pojo.getQuotatype().isEmpty()) {
            throw new CustomValidationException(APIConstants.FAIL, "Please Select QuotaType!", null);
        }

        if (null != pojo && !pojo.getQuotatype().isEmpty() && pojo.getQuotatype().equalsIgnoreCase(CommonConstants.TIME_QUOTA_TYPE)) {
            if (null == pojo.getQuotaunittime() || null == pojo.getQuotatime()) {
                throw new CustomValidationException(APIConstants.FAIL, "Please enter QuotUnitTime And QuotaTime!", null);
            }
        }
        if (null != pojo && !pojo.getQuotatype().isEmpty() && pojo.getQuotatype().equalsIgnoreCase(CommonConstants.DATA_QUOTA_TYPE)) {
            if (null == pojo.getQuotaUnit()) {
                throw new CustomValidationException(APIConstants.FAIL, "Please enter Quota And QuotaUnit!", null);
            }
        }
        if (null != pojo && !pojo.getQuotatype().isEmpty() && pojo.getQuotatype().equalsIgnoreCase(CommonConstants.BOTH_QUOTA_TYPE)) {
            if (null == pojo.getQuotaunittime() || null == pojo.getQuotatime() || null == pojo.getQuotaUnit()) {
                throw new CustomValidationException(APIConstants.FAIL, "Please enter QuotUnitTime And QuotaTime And Quota And QuotaUnit!", null);
            }
        }
        if (null == pojo.getOfferprice()) {
            throw new CustomValidationException(APIConstants.FAIL, "Please enter offer price!", null);
        }
        if (null == pojo.getPlantype() || pojo.getPlantype().isEmpty()) {
            throw new CustomValidationException(APIConstants.FAIL, "Please enter plantype!", null);
        }
        if (!(pojo.getQuotaResetInterval().equals("Daily") || pojo.getQuotaResetInterval().equals("Weekly") || pojo.getQuotaResetInterval().equals("Monthly") || pojo.getQuotaResetInterval().equals("Total"))) {
            throw new RuntimeException("Please enter valid quotaResetInterval status");
        }
        if (!(pojo.getUnitsOfValidity().equals("Hours") || pojo.getUnitsOfValidity().equals("Days") || pojo.getUnitsOfValidity().equals("Months") || pojo.getUnitsOfValidity().equals("Years"))) {
            throw new RuntimeException("Please enter valid validity type");
        }
        if ((operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_ADD)) && pojo.getPlanGroup() != null &&
                pojo.getPlanGroup().equalsIgnoreCase("Bandwidthbooster") == false && pojo.getAddonToBase() != null &&
                pojo.getAddonToBase()) {
            throw new CustomValidationException(APIConstants.FAIL, "Addon To Base will only support for Bandwidth Booster", null);
        }
    }

    public String findNameById(@Param("id") Integer id) {
        return entityRepository.findNameById(id);
    }

    public List<PostpaidPlanPojo> findPlanByService(Integer serviceId, String planType,Integer mvnoId) {
        String SUBMODULE = MODULE + " [findPlanByService()] ";
        try {
            ArrayList<String> plangroup = new ArrayList<>(5);
            plangroup.add("Registration and Renewal");
            plangroup.add("Registration");
            List<PostpaidPlan> postPaidPlanList = entityRepository.findAllByServiceIdAndStatusAndPlanGroup(serviceId, CommonConstants.ACTIVE_STATUS, planType);
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) return postPaidPlanList.stream().map(data -> {
                try {
                    return postpaidPlanMapper.domainToDTO(data, new CycleAvoidingMappingContext());
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            if (null != postPaidPlanList && 0 < postPaidPlanList.size()) {
                if (getBUIdsFromCurrentStaff().size() == 0) return postPaidPlanList.stream().map(data -> {
                    try {
                        return postpaidPlanMapper.domainToDTO(data, new CycleAvoidingMappingContext());
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    return null;
                    // TODO: pass mvnoID manually 6/5/2025
                }).collect(Collectors.toList()).stream().filter(postpaidPlanPojo -> postpaidPlanPojo.getMvnoId() == 1 || mvnoId == 1 || postpaidPlanPojo.getMvnoId() == mvnoId).collect(Collectors.toList());
                else return postPaidPlanList.stream().map(data -> {
                    try {
                        return postpaidPlanMapper.domainToDTO(data, new CycleAvoidingMappingContext());
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                    return null;
                    // TODO: pass mvnoID manually 6/5/2025
                }).collect(Collectors.toList()).stream().filter(postpaidPlanPojo -> (postpaidPlanPojo.getMvnoId() == 1 || mvnoId == 1 || postpaidPlanPojo.getMvnoId() == mvnoId) && (postpaidPlanPojo.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(postpaidPlanPojo.getBuId()))).collect(Collectors.toList());
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return new ArrayList<>();
    }

    public List<PostpaidPlanPojo> findPlanByTypeServiceModeStatusAndServiceArea(String type, Integer serviceId, List<Integer> serviceAreaId, String mode, String status, String planGroup, String planCategory, Integer validity, String unitsOfValidity, Integer custId,Integer mvnoId) {
        String SUBMODULE = MODULE + " [findPlanByTypeServiceModeStatusAndServiceArea()] ";
        try {
            return getPlanListByServiceArea(serviceAreaId, mode, planGroup, planCategory, validity, unitsOfValidity, custId,mvnoId).stream().filter(postpaidPlanPojo -> postpaidPlanPojo.getPlantype().equalsIgnoreCase(type) && postpaidPlanPojo.getMode().equalsIgnoreCase(mode) && postpaidPlanPojo.getStatus().equalsIgnoreCase(status) && postpaidPlanPojo.getServiceId().equals(serviceId)).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return new ArrayList<>();
    }

    public PostpaidPlan findById(Integer id) {
        Optional<PostpaidPlan> postpaidPlan = entityRepository.findById(id);
        if (postpaidPlan.isPresent()) {
            return postpaidPlan.get();
        }
        return null;
    }

    public List<PostpaidPlan> findByServiceId(Integer id) {
        return entityRepository.findPostpaidPlanByServiceId(id);
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Postpaid Plans");
        List<PostpaidPlanPojo> postpaidPlanPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, PostpaidPlanPojo.class, postpaidPlanPojoList, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{PostpaidPlanPojo.class.getDeclaredField("id"), PostpaidPlanPojo.class.getDeclaredField("name"), PostpaidPlanPojo.class.getDeclaredField("plantype"), PostpaidPlanPojo.class.getDeclaredField("offerprice"), PostpaidPlanPojo.class.getDeclaredField("status"), PostpaidPlanPojo.class.getDeclaredField("Accessibility"), PostpaidPlanPojo.class.getDeclaredField("allowdiscount")};
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<PostpaidPlanPojo> postpaidPlanPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, PostpaidPlanPojo.class, postpaidPlanPojoList, getFields());
    }

    @Override
    public Page<PostpaidPlan> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = MODULE + " [search()] ";
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false));
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.in(mvnoId, 1));
        }
        if (getBUIdsFromCurrentStaff().size() != 0) {
            booleanExpression = booleanExpression.and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()));
        }
//        if (getBUIdsFromCurrentStaff().size() != 0)
//            booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()))));
        if (getLoggedInUserId() != 1) {
            QPostPaidPlanServiceAreaMapping qPostPaidPlanServiceAreaMapping = QPostPaidPlanServiceAreaMapping.postPaidPlanServiceAreaMapping;
            List<Integer> serviceIDs = getLoggedInUser().getServiceAreaIdList();
            //  List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
            if (serviceIDs.size() > 0) {
                booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(query.select(qPostPaidPlanServiceAreaMapping.planId).from(qPostPaidPlanServiceAreaMapping).where(qPostPaidPlanServiceAreaMapping.serviceId.in(serviceIDs))));
            }
        }

        try {
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (null != searchModel.getFilterColumn()) {
                        if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLAN_STATUS)) {
                            if (searchModel.getFilterValue().trim().contains(SearchConstants.PLAN_STATUS_ACTIVE)) {
                                if (!searchModel.getFilterValue().isEmpty()) {
                                    booleanExpression = booleanExpression.and(qPostpaidPlan.status.eq("Active"));
                                }
                            }
                            if (searchModel.getFilterValue().trim().contains(SearchConstants.PLAN_STATUS_EXPIRED)) {
                                if (!searchModel.getFilterValue().isEmpty()) {
                                    booleanExpression = booleanExpression.and(qPostpaidPlan.status.eq("Expired"));
                                }
                            }
                            if (searchModel.getFilterValue().trim().contains(SearchConstants.PLAN_STATUS_REJECTED)) {
                                if (!searchModel.getFilterValue().isEmpty()) {
                                    booleanExpression = booleanExpression.and(qPostpaidPlan.status.eq("Rejected"));
                                }
                            }
                            if (searchModel.getFilterValue().trim().contains(SearchConstants.PLAN_STATUS_NEWACTIVATION)) {
                                if (!searchModel.getFilterValue().isEmpty()) {
                                    booleanExpression = booleanExpression.and(qPostpaidPlan.status.eq("NewActivation"));
                                }
                            }
                            if (searchModel.getFilterValue().trim().contains(SearchConstants.PLAN_STATUS_INACTIVE)) {
                                if (!searchModel.getFilterValue().isEmpty()) {
                                    booleanExpression = booleanExpression.and(qPostpaidPlan.status.eq("INACTIVE"));
                                }
                            }
                        } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLAN_VALIDITY)) {
                            String s = searchModel.getFilterValue();
                            String[] parts = s.split(" ");

                            String firstPart = parts[0];
                            String secondPart = parts[1];
                            booleanExpression = booleanExpression.and(qPostpaidPlan.validity.stringValue().equalsIgnoreCase(firstPart)).and(qPostpaidPlan.unitsOfValidity.stringValue().equalsIgnoreCase(secondPart));

                        } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLAN_PRICE)) {
                            String s1 = searchModel.getFilterValue();
                            booleanExpression = booleanExpression.and(
                                    qPostpaidPlan.offerprice.like("%" + s1 + "%"));
                        } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLAN_CREATEDBY)) {
                            String s1 = searchModel.getFilterValue();
                            booleanExpression = booleanExpression.and(qPostpaidPlan.createdByName.likeIgnoreCase("%" + s1 + "%").and(qPostpaidPlan.createdByName.containsIgnoreCase(searchModel.getFilterValue())));
                        } else if (searchModel.getFilterDataType().trim().equalsIgnoreCase(SearchConstants.POSTPAID)) {
                            String s1 = searchModel.getFilterValue();
                            if (searchModel.getFilterValue() != null) {
                                booleanExpression = booleanExpression.and(qPostpaidPlan.displayName.likeIgnoreCase("%" + s1 + "%").and(qPostpaidPlan.displayName.containsIgnoreCase(searchModel.getFilterValue()))).and(qPostpaidPlan.plantype.eq("Postpaid"));
                            }
                            if (searchModel.getFilterValue() == "") {
                                booleanExpression = booleanExpression.and(qPostpaidPlan.plantype.eq("Postpaid"));
                            }
                        } else if (searchModel.getFilterDataType().trim().equalsIgnoreCase(SearchConstants.PREPAID)) {
                            String s1 = searchModel.getFilterValue();
                            if (searchModel.getFilterValue() != null) {
                                booleanExpression = booleanExpression.and(qPostpaidPlan.displayName.likeIgnoreCase("%" + s1 + "%").and(qPostpaidPlan.displayName.containsIgnoreCase(searchModel.getFilterValue()))).and(qPostpaidPlan.plantype.eq("Prepaid"));
                            }
                            if (searchModel.getFilterValue() == "") {
                                booleanExpression = booleanExpression.and(qPostpaidPlan.plantype.eq("Prepaid"));
                            }
                        } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLANCREATEDATE)) {
                            if (!searchModel.getFilterValue().isEmpty()) {
                                JSONObject filterValue = new JSONObject(searchModel.getFilterValue());
                                String fromDate = filterValue.getString("from") + "T00:00:00";
                                String toDate = filterValue.getString("to") + "T23:59:59";

                                LocalDateTime fromDateTime = LocalDateTime.parse(fromDate);
                                LocalDateTime toDateTime = LocalDateTime.parse(toDate);

                                booleanExpression = booleanExpression
                                        .and(qPostpaidPlan.createdate.goe(fromDateTime))
                                        .and(qPostpaidPlan.createdate.loe(toDateTime));
                            }
////
                        } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.SERVICE_AREA)) {
                            if (!searchModel.getFilterValue().isEmpty()) {
                                QServiceArea qServiceArea = QServiceArea.serviceArea;
                                BooleanExpression exp1 = qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false)).and(qServiceArea.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qServiceArea.name.containsIgnoreCase(searchModel.getFilterValue()));
                                List<ServiceArea> serviceAreas = (List<ServiceArea>) serviceAreaRepository.findAll(exp1);
                                List<Long> serviceAreaIdLong = serviceAreas.stream().mapToLong(ServiceArea::getId).boxed().collect(Collectors.toList());
                                List<Integer> serviceAreaId = serviceAreaIdLong.stream().map(Long::intValue).collect(Collectors.toList());
                                List<PostPaidPlanServiceAreaMapping> serviceAreaMappings = planServiceAreaRepo.findAllByServiceIdIn(serviceAreaId);
                                List<Integer> planIds = serviceAreaMappings.stream().map(PostPaidPlanServiceAreaMapping::getPlanId).collect(Collectors.toList());
                                booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(planIds));
                            }
                        } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                            String s1 = searchModel.getFilterValue();
                            booleanExpression = booleanExpression.and(qPostpaidPlan.displayName.likeIgnoreCase("%" + s1 + "%").and(qPostpaidPlan.displayName.containsIgnoreCase(searchModel.getFilterValue())));
                        } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLAN_FRANCHISE)) {
                            if (!searchModel.getFilterValue().isEmpty()) {
                                QPartner qPartner = QPartner.partner;
                                BooleanExpression exp1 = qPartner.isNotNull().and(qPartner.isDelete.eq(false)).and(qPartner.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qPartner.name.containsIgnoreCase(searchModel.getFilterValue()));
                                List<Partner> partners = (List<Partner>) partnerRepository.findAll(exp1);
                                List<Long> partner = partners.stream().mapToLong(Partner::getId).boxed().collect(Collectors.toList());
                                List<Integer> partnerIds = partner.stream().map(Long::intValue).collect(Collectors.toList());
                                List<PartnerServiceAreaMapping> partnerServiceAreaMappings = partnerServiceAreaMappingRepo.findAllByPartnerIdIn(partnerIds);
                                List<Integer> planIds = partnerServiceAreaMappings.stream().map(PartnerServiceAreaMapping::getServiceId).collect(Collectors.toList());
                                booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(planIds));
                            }

                        } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLAN_STARTDATE)) {
                            booleanExpression = booleanExpression.and(qPostpaidPlan.startDate.goe(LocalDate.from((LocalDateTime.parse(searchModel.getFilterValue() + "T00:00:00")))).and(qPostpaidPlan.startDate.loe(LocalDate.from((LocalDateTime.parse(searchModel.getFilterValue() + "T23:59:59"))))));
                        } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLAN_ENDDATE)) {
                            booleanExpression = booleanExpression.and(qPostpaidPlan.endDate.goe(LocalDate.from((LocalDateTime.parse(searchModel.getFilterValue() + "T00:00:00")))).and(qPostpaidPlan.endDate.loe(LocalDate.from((LocalDateTime.parse(searchModel.getFilterValue() + "T23:59:59"))))));
                        } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLAN_BRANCH)) {
                            if (!searchModel.getFilterValue().isEmpty()) {
                                QBranch qBranch = QBranch.branch;
                                BooleanExpression exp1 = qBranch.isNotNull().and(qBranch.isDeleted.eq(false)).and(qBranch.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qBranch.name.containsIgnoreCase(searchModel.getFilterValue()));
                                List<Branch> branches = (List<Branch>) branchRepository.findAll(exp1);
                                List<Long> branch = branches.stream().mapToLong(Branch::getId).boxed().collect(Collectors.toList());
                                List<Integer> branchIds = branch.stream().map(Long::intValue).collect(Collectors.toList());
                                List<BranchServiceAreaMapping> branchServiceAreaMappings = branchServiceAreaMappingRepository.findAllByBranchIdIn(branchIds);
                                List<Integer> serviceAreaId = branchServiceAreaMappings.stream().map(BranchServiceAreaMapping::getServiceareaId).collect(Collectors.toList());
                                List<PostPaidPlanServiceAreaMapping> postPaidPlanServiceAreaMappings = planServiceAreaRepo.findAllByServiceIdIn(serviceAreaId);
                                List<Integer> planIds = postPaidPlanServiceAreaMappings.stream().map(PostPaidPlanServiceAreaMapping::getPlanId).collect(Collectors.toList());
                                booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(planIds));
                            }
                        }
                    } else throw new RuntimeException("Please Provide Search Column!");

                }
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return getPostpaidPlans(booleanExpression, pageRequest);
//       return postpaidPlanRepo.findAll(booleanExpression, pageRequest);
    }

    public Page<PostpaidPlan> getPostpaidPlans(BooleanExpression booleanExpression, Pageable pageable) {
        QPostpaidPlan p = QPostpaidPlan.postpaidPlan; // Ensure this matches your entity class
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        // Build query dynamically using QueryDSL
        JPAQuery<PostpaidPlan> query = queryFactory
                .select(Projections.constructor(
                        PostpaidPlan.class, // Constructor projection
                        p.id,
                        p.displayName,
                        p.name,
                        p.plantype,
                        p.validity,
                        p.unitsOfValidity,
                        p.startDate,
                        p.endDate,
                        p.category,
                        p.newOfferPrice,
                        p.offerprice,
                        p.taxamount,
                        p.status,
                        p.quotatype,
                        p.createdByName,
                        p.mvnoName,
                        p.nextStaff,
                        p.currency,
                        p.planGroup
                ))
                .from(p)
                .where(booleanExpression) // Apply dynamic filtering
                .orderBy(p.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // Execute query and return results
        List<PostpaidPlan> resultList = query.fetch();
        // Fetch total count for pagination
        long totalCount = queryFactory
                .select(p.count())
                .from(p)
                .where(booleanExpression)
                .fetchOne();

        return new PageImpl<>(resultList, pageable, totalCount);
    }


    public Page<PostpaidPlan> getPlanByNameOrSacCode(String s1, String s2, String dataType, PageRequest pageRequest) {
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        QPostPaidPlanServiceAreaMapping qPostPaidPlanServiceAreaMapping = QPostPaidPlanServiceAreaMapping.postPaidPlanServiceAreaMapping;
        BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.status.notEqualsIgnoreCase("Rejected"));
        if (!s1.isEmpty()) {
            booleanExpression = booleanExpression.and((qPostpaidPlan.displayName.like("%" + s1 + "%")).or(qPostpaidPlan.saccode.like("%" + s1 + "%")));
        }
        if (getLoggedInUserId() != 1) {
            List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
            booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(query.select(qPostPaidPlanServiceAreaMapping.planId).from(qPostPaidPlanServiceAreaMapping).where(qPostPaidPlanServiceAreaMapping.serviceId.in(serviceIDs))));
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1) return getRepository().findAll(booleanExpression, pageRequest);
            // TODO: pass mvnoID manually 6/5/2025
        else booleanExpression = booleanExpression.and((qPostpaidPlan.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1)));
        if (getBUIdsFromCurrentStaff().size() != 0)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()))));
        if (!dataType.equalsIgnoreCase("")) {
            booleanExpression = booleanExpression.and(qPostpaidPlan.plantype.eq(dataType));
        }
        return entityRepository.findAll(booleanExpression, pageRequest);
    }

    public List<PostpaidPlanPojo> getPlanByPartnerId(PlanByPartnerReqDTO reqDTO) throws Exception {
        List<PostpaidPlanPojo> postpaidPlanPojoList = new ArrayList<>();
        List<PostpaidPlanPojo> returnList = new ArrayList<>();
        Partner partner = partnerRepository.findById(reqDTO.getPartnerId()).get();
        if (partner != null) {
            if (partner.getPriceBookId() != null) {
                PriceBookDTO priceBookDTO = priceBookService.getEntityById(partner.getPriceBookId().getId(),partner.getMvnoId());
                List<PriceBookPlanDetailDTO> priceBookPlanDetailList = priceBookDTO.getPriceBookPlanDetailList();
                if (priceBookPlanDetailList != null && priceBookPlanDetailList.size() > 0) {

                    for (PriceBookPlanDetailDTO priceBookPlanDetailDTO : priceBookPlanDetailList) {
                        postpaidPlanPojoList.add(priceBookPlanDetailDTO.getPostpaidPlan());
                    }

                    if (reqDTO.getServiceType() != null && reqDTO.getServiceType() != 0) {
                        if (reqDTO.getServiceType().equals(CommonConstants.DATA_SERVICE_ID)) {
                            postpaidPlanPojoList = postpaidPlanPojoList.stream().filter(data -> data.getServiceId().equals(CommonConstants.DATA_SERVICE_ID) && !data.getIsDelete()).collect(Collectors.toList());
                        }

                        if (reqDTO.getServiceType().equals(CommonConstants.VOICE_SERVICE_ID)) {
                            postpaidPlanPojoList = postpaidPlanPojoList.stream().filter(data -> data.getServiceId().equals(CommonConstants.VOICE_SERVICE_ID) && !data.getIsDelete()).collect(Collectors.toList());
                        }
                    }

                    if (reqDTO.getPlanGroup() != null && !reqDTO.getPlanGroup().equals("")) {
                        postpaidPlanPojoList = postpaidPlanPojoList.stream().filter(data -> reqDTO.getPlanGroup().equalsIgnoreCase(data.getPlanGroup()) && !data.getIsDelete()).collect(Collectors.toList());
                    }

                }
            }
        }
        return postpaidPlanPojoList;
    }

    public Page<PostpaidPlan> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, String access,Integer mvnoId) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
        BusinessUnit businessUnit = new BusinessUnit();
        if (getBUIdsFromCurrentStaff().size() > 0) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        if (null == filterList || 0 == filterList.size()) {
            QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
            JPAQuery<?> query = new JPAQuery<>(entityManager);

            BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.status.notEqualsIgnoreCase(SubscriberConstants.REJECT));
            if (access != null) {
                booleanExpression = booleanExpression.and(qPostpaidPlan.Accessibility.eq(access));
            }
            if (getLoggedInUserId() != 1) {
                QPostPaidPlanServiceAreaMapping qPostPaidPlanServiceAreaMapping = QPostPaidPlanServiceAreaMapping.postPaidPlanServiceAreaMapping;
                List<Integer> serviceIDs = getLoggedInUser().getServiceAreaIdList();
                //  List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                if (serviceIDs.size() > 0) {
                    booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(query.select(qPostPaidPlanServiceAreaMapping.planId).from(qPostPaidPlanServiceAreaMapping).where(qPostPaidPlanServiceAreaMapping.serviceId.in(serviceIDs))));
                }
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId != 1) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and((qPostpaidPlan.mvnoId.in(mvnoId, 1)));
            }
            if (getBUIdsFromCurrentStaff().size() != 0 && businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.PREDEFINED)) {
                // TODO: pass mvnoID manually 6/5/2025
                if (mvnoId != 1) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(mvnoId).and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff())).and(qPostpaidPlan.businessType.isNull().or(qPostpaidPlan.businessType.equalsIgnoreCase(CommonConstants.RETAIL)))));
                } else if (mvnoId == 1) { // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff())).and(qPostpaidPlan.businessType.isNull().or(qPostpaidPlan.businessType.equalsIgnoreCase(CommonConstants.RETAIL)));
                }
            } else if (getBUIdsFromCurrentStaff().size() != 0 && businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND)) {
                // TODO: pass mvnoID manually 6/5/2025
                if (mvnoId != 1) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff())).and(qPostpaidPlan.businessType.equalsIgnoreCase(CommonConstants.ENTERPRISE))));
                } else if (getMvnoIdFromCurrentStaff(null) == 1) { // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff())).and(qPostpaidPlan.businessType.equalsIgnoreCase(CommonConstants.ENTERPRISE));
                }

            } else if ((Objects.isNull(businessUnit.getPlanBindingType())) || (CommonConstants.PREDEFINED).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != 1) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qPostpaidPlan.businessType.isNull().or(qPostpaidPlan.businessType.equalsIgnoreCase(CommonConstants.RETAIL)))));
                } else if (mvnoId == 1) { // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPostpaidPlan.businessType.isNull().or(qPostpaidPlan.businessType.equalsIgnoreCase(CommonConstants.RETAIL)));
                }

            }
            return getRepository().findAll(booleanExpression, pageRequest);
        } else {
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
        }
    }

    public Set<PostpaidPlan> getAllActivePremierePlan(Integer serviceAreaId, Integer custId, boolean isPremiere, Integer leadCustId) {

        // All normal plans
        List<PostPaidPlanServiceAreaMapping> list = planServiceAreaRepo.findAllByServiceId(serviceAreaId);
        List<Integer> planIds = list.stream().map(PostPaidPlanServiceAreaMapping::getPlanId).collect(Collectors.toList());

        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.mode.eq(Constants.NORMAL)).and(qPostpaidPlan.status.eq(CommonConstants.ACTIVE_STATUS));
        booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(planIds));
        if (getBUIdsFromCurrentStaff().size() != 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(getMvnoIdFromCurrentStaff(custId)).and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()))));
        }
        List<PostpaidPlan> plansid = (List<PostpaidPlan>) this.getRepository().findAll(booleanExpression);

        Integer partnerId = getLoggedInUserPartnerId();
        Partner partner = partnerRepository.findById(partnerId).get();
        if (partnerId != 1 && !Objects.equals(partner.getPartnerType(), "LCO")) {
            PriceBook priceBook = partner.getPriceBookId();
            if (!priceBook.getIsAllPlanSelected()) {
                Long priceBookId = priceBook.getId();
                if (priceBookId != null) {
                    QPriceBookPlanDetail qPriceBookPlanDetail = QPriceBookPlanDetail.priceBookPlanDetail;
                    BooleanExpression exp = qPriceBookPlanDetail.isNotNull().and(qPriceBookPlanDetail.priceBook.id.eq(priceBookId));
                    List<PriceBookPlanDetail> priceBookPlanDetail = (List<PriceBookPlanDetail>) priceBookPlanDtlRepository.findAll(exp);
                    List<Integer> planId = priceBookPlanDetail.stream().filter(x -> x.getPostpaidPlan() != null).map(i -> i.getPostpaidPlan().getId()).collect(Collectors.toList());
                    if (planId.size() > 0) {
                        plansid = plansid.stream().filter(i -> planId.contains(i.getId())).collect(Collectors.toList());
                    }
                }
            }
        }


        if (custId != null) {
            QCustomerPackage qCustomerPackage = QCustomerPackage.customerPackage;
            BooleanExpression exp = qCustomerPackage.isNotNull();
            exp = exp.and(qCustomerPackage.customers.id.eq(custId)).and(qCustomerPackage.plan.status.equalsIgnoreCase("Active"));
            List<CustomerPackage> cutomerid = (List<CustomerPackage>) customerPackageRepository.findAll(exp);

            for (int i = 0; i < cutomerid.size(); i++) {
                PostpaidPlan plans = cutomerid.get(i).getPlan();
                plansid.add(plans);
            }
        } else {
            List<LeadCustPlanMappping> leadCustPlanMapppingList = leadCustPlanMapppingRepository.findByLeadMasterId(Long.valueOf(leadCustId));
            if (!leadCustPlanMapppingList.isEmpty()) {
                List<Integer> leadplanIds = leadCustPlanMapppingList.stream().map(x -> x.getPlanId()).collect(Collectors.toList());
                List<PostpaidPlan> postpaidPlanList = postpaidPlanRepo.findAllByStatusAndIsDeleteFalseAndIdIn("Active", leadplanIds);
                plansid.addAll(postpaidPlanList);
            }
        }

        if (isPremiere) {
            // Fetch all special plan for customer mapping.
            List<CustSpecialPlanMappping> custSpecialPlanMapppings = new ArrayList<>();
            List<CustSpecialPlanRelMappping> custSpecialPlanRelMapppingList = new ArrayList<>();
            if (custId != null) {
                custSpecialPlanMapppings = custSpecialPlanMapppingService.findAllByCustomers(custId);
            } else {
                custSpecialPlanMapppings = custSpecialPlanMapppingService.findAllByLeadCustomers(leadCustId);
                custSpecialPlanRelMapppingList = custSpecialPlanMapppings.stream().map(x -> x.getCustSpecialPlanRelMappping()).collect(Collectors.toList());
                custSpecialPlanRelMapppingList = custSpecialPlanRelMapppingList.stream().filter(x -> x.getStatus().equalsIgnoreCase("active")).collect(Collectors.toList());
            }
            if (!custSpecialPlanRelMapppingList.isEmpty() && leadCustId != null) {
                List<Long> mappingIds = custSpecialPlanRelMapppingList.stream().map(x -> x.getId()).collect(Collectors.toList());
                QCustSpecialPlanMappping qCustSpecialPlanMappping = QCustSpecialPlanMappping.custSpecialPlanMappping;
                BooleanExpression exp = qCustSpecialPlanMappping.isNotNull().and(qCustSpecialPlanMappping.custSpecialPlanRelMappping.id.in(mappingIds));
                exp = exp.and(qCustSpecialPlanMappping.leadMaster.id.eq(Long.valueOf(leadCustId)));
                custSpecialPlanMapppings = (List<CustSpecialPlanMappping>) custSpecialPlanMapppingRepository.findAll(exp);
                Set<PostpaidPlan> custSpecialPlans = custSpecialPlanMapppings.stream().map(CustSpecialPlanMappping::getSpecialPlan).collect(Collectors.toSet());
                plansid.addAll(custSpecialPlans);
            }

            if (!CollectionUtils.isEmpty(custSpecialPlanMapppings) && leadCustId == null) {
                Set<PostpaidPlan> custSpecialPlans = custSpecialPlanMapppings.stream().map(CustSpecialPlanMappping::getSpecialPlan).collect(Collectors.toSet());
                plansid.addAll(custSpecialPlans);
            } else {
                List<CustSpecialPlanMappping> planSpecialPlanMapppings = custSpecialPlanMapppingService.findAllByNormalPlanIds(plansid);
                if (!CollectionUtils.isEmpty(planSpecialPlanMapppings)) {
                    Set<PostpaidPlan> specialPlans = planSpecialPlanMapppings.stream().map(CustSpecialPlanMappping::getSpecialPlan).collect(Collectors.toSet());
                    plansid.addAll(specialPlans);
                }
            }
        }
        return plansid.stream().collect(Collectors.toSet());
    }

    public List<PostpaidPlanPojo> getPlanListByServiceArea(List<Integer> serviceAreaId, String planmode, String planGroup, String planCategory, Integer validity, String unitsOfValidity, Integer custId,Integer mvnoId) {

//        List<PostPaidPlanServiceAreaMapping> list = planServiceAreaRepo.findAllByServiceIdIn(serviceAreaId);
//        List<Integer> planIds = list.stream().map(PostPaidPlanServiceAreaMapping::getPlanId).collect(Collectors.toList());
        List<Integer> planIds = planServiceAreaRepo.findAllByService(serviceAreaId);
        System.out.println("planIDs: "+planIds);

        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        QDepartmentPlanMapping qDepartmentPlanMapping = QDepartmentPlanMapping.departmentPlanMapping;
        BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.status.eq(CommonConstants.ACTIVE_STATUS));

        booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(planIds));

        if (!planmode.equals(Constants.ALL)) booleanExpression = booleanExpression.and(qPostpaidPlan.mode.eq(planmode));

        if (planGroup != null && !"".equals(planGroup) && !Constants.PLAN_GROUP_ALL.equalsIgnoreCase(planGroup))
            booleanExpression = booleanExpression.and(qPostpaidPlan.planGroup.equalsIgnoreCase(planGroup));

//        if (planCategory != null && !"".equals(planCategory) && !Constants.PLAN_CATEGORY_ALL.equalsIgnoreCase(planCategory))
//            booleanExpression = booleanExpression.and(qPostpaidPlan.category.equalsIgnoreCase(planCategory));


        if (getBUIdsFromCurrentStaff().size() != 0)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()));

        if(mvnoId!=null && mvnoId!=1){
            booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(mvnoId)));
        }
        if (custId != null) {
//            List<CustomerServiceMapping> customerServiceMappings = customerServiceMappingRepository.findByCustId(custId);
//            if (!CollectionUtils.isEmpty(customerServiceMappings)) {
            List<Integer> serviceIds = Optional.ofNullable(customerServiceMappingRepository.serviceIdByCustomerId(custId)).orElse(Collections.emptyList()).stream().map(aLong -> aLong.intValue()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(serviceIds))
                    booleanExpression = booleanExpression.and(qPostpaidPlan.serviceId.in(serviceIds));
//            }
            String currency = customersRepository.findCurrencyByCustomerId(custId).orElse(null);
            if(currency != null){
                booleanExpression = booleanExpression.and(qPostpaidPlan.currency.equalsIgnoreCase(currency));
            }
        }

        booleanExpression = booleanExpression.and(qPostpaidPlan.endDate.eq(LocalDate.now()).or(qPostpaidPlan.endDate.after(LocalDate.now())));
        if (validity != null && !(unitsOfValidity.isEmpty())) {
            booleanExpression = booleanExpression.and(qPostpaidPlan.unitsOfValidity.equalsIgnoreCase(unitsOfValidity)).and(qPostpaidPlan.validity.eq(validity.doubleValue()));
        }
        // booleanExpression = getPlanByPartnerBundle(qPostpaidPlan, booleanExpression);
//        List<PostpaidPlan> planList = (List<PostpaidPlan>) this.getRepository().findAll(booleanExpression);
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        List<PostpaidPlanPojo> planList = query
                .select(Projections.bean(
                        PostpaidPlanPojo.class,
                        qPostpaidPlan.id,
                        qPostpaidPlan.name,
                        qPostpaidPlan.displayName,
                        qPostpaidPlan.code,
                        qPostpaidPlan.desc,
                        qPostpaidPlan.category,
                        qPostpaidPlan.startDate,
                        qPostpaidPlan.endDate,
                        qPostpaidPlan.allowOverUsage,
                        qPostpaidPlan.quotaUnit,
                        qPostpaidPlan.quota,
                        qPostpaidPlan.planStatus,
                        qPostpaidPlan.childQuota,
                        qPostpaidPlan.childQuotaUnit,
                        qPostpaidPlan.mvnoId,
                        qPostpaidPlan.status,
                        qPostpaidPlan.serviceId,
                        qPostpaidPlan.timebasepolicyId,
                        qPostpaidPlan.plantype,
                        qPostpaidPlan.dbr,
                        qPostpaidPlan.planGroup,
                        qPostpaidPlan.validity,
                        qPostpaidPlan.maxconcurrentsession,
                        qPostpaidPlan.quotaResetInterval,
                        qPostpaidPlan.mode,
                        qPostpaidPlan.unitsOfValidity,
                        qPostpaidPlan.newOfferPrice,
                        qPostpaidPlan.allowdiscount,
                        qPostpaidPlan.basePlan,
                        qPostpaidPlan.useQuota,
                        qPostpaidPlan.mvnoName,
                        qPostpaidPlan.usageQuotaType,
                        qPostpaidPlan.taxamount,

                        qPostpaidPlan.businessType,
                        qPostpaidPlan.chunk,

                        qPostpaidPlan.addonToBase,
                        qPostpaidPlan.maxHoldDurationDays,
                        qPostpaidPlan.maxHoldAttempts,


                        // Additional fields from your list
                        qPostpaidPlan.uploadQOS,
                        qPostpaidPlan.downloadQOS,
                        qPostpaidPlan.uploadTs,
                        qPostpaidPlan.downloadTs,
                        qPostpaidPlan.slice,
                        qPostpaidPlan.sliceUnit,
                        qPostpaidPlan.attachedToAllHotSpots,
                        qPostpaidPlan.param1,
                        qPostpaidPlan.param2,
                        qPostpaidPlan.param3,
                        qPostpaidPlan.taxId,
                        qPostpaidPlan.maxChild,
                        qPostpaidPlan.saccode,
                        qPostpaidPlan.quotaunittime,
                        qPostpaidPlan.quotatime,
                        qPostpaidPlan.quotatype,
                        qPostpaidPlan.offerprice,
                        qPostpaidPlan.isDelete,
                        qPostpaidPlan.quotadid,
                        qPostpaidPlan.quotaintercom,
                        qPostpaidPlan.quotaunitdid,
                        qPostpaidPlan.quotaunitintercom,
                        qPostpaidPlan.dataCategory,
                        qPostpaidPlan.buId,
                        qPostpaidPlan.nextStaff,
                        qPostpaidPlan.nextTeamHierarchyMapping,
                        qPostpaidPlan.productId,
                        qPostpaidPlan.invoiceToOrg,
                        qPostpaidPlan.requiredApproval,
                        qPostpaidPlan.bandwidth,
                        qPostpaidPlan.link_type,
                        qPostpaidPlan.connection_type,
                        qPostpaidPlan.distance,
                        qPostpaidPlan.ram,
                        qPostpaidPlan.cpu,
                        qPostpaidPlan.storage,
                        qPostpaidPlan.storage_type,
                        qPostpaidPlan.auto_backup,
                        qPostpaidPlan.cpanel,
                        qPostpaidPlan.location,
                        qPostpaidPlan.quantity,
                        qPostpaidPlan.package_type,
                        qPostpaidPlan.number_of_days,
                        qPostpaidPlan.no_of_users,
                        qPostpaidPlan.rack_space,
                        qPostpaidPlan.rack_unit,
                        qPostpaidPlan.power_consumption,
                        qPostpaidPlan.network_card,
                        qPostpaidPlan.ip_or_ip_pool,
                        qPostpaidPlan.no_of_license,
                        qPostpaidPlan.no_of_email_user_license,
                        qPostpaidPlan.no_of_server_license,
                        qPostpaidPlan.no_of_user_license,
                        qPostpaidPlan.no_of_nodes,
                        qPostpaidPlan.event_per_second,
                        qPostpaidPlan.no_of_additional_server,
                        qPostpaidPlan.no_of_additional_storage,
                        qPostpaidPlan.additional_storage_type,
                        qPostpaidPlan.eps_License,
                        qPostpaidPlan.no_of_nodes_license,
                        qPostpaidPlan.hardware_resource,
                        qPostpaidPlan.man_power,
                        qPostpaidPlan.no_of_domains,
                        qPostpaidPlan.security_modules,
                        qPostpaidPlan.hardware_or_servers,
                        qPostpaidPlan.country,
                        qPostpaidPlan.no_of_vpn,
                        qPostpaidPlan.device_throughput,
                        qPostpaidPlan.retail,
                        qPostpaidPlan.currency
                ))
                .from(qPostpaidPlan)
                .where(booleanExpression)
                .fetch();

//        List<PostpaidPlanPojo> result = planList.stream().map(data -> {
//            try {
//                return postpaidPlanMapper.domainToDTO(data, new CycleAvoidingMappingContext());
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }).collect(Collectors.toList());
        BusinessUnit businessUnit = new BusinessUnit();
        List<PostpaidPlanPojo> pojoList = new ArrayList<>();
        System.out.println("planList.size1 "+planList.size());
        if (getBUIdsFromCurrentStaff().size() == 1) {
            businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
        }
        if ((Objects.isNull(businessUnit.getPlanBindingType())) || (CommonConstants.PREDEFINED).equalsIgnoreCase(businessUnit.getPlanBindingType())) {
            pojoList = planList.stream().filter(postpaidPlanPojo -> (Objects.isNull(postpaidPlanPojo.getBusinessType()) || (CommonConstants.RETAIL).equalsIgnoreCase(postpaidPlanPojo.getBusinessType()))).collect(Collectors.toList());
        } else if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND)) {
            pojoList = planList.stream().filter(postpaidPlanPojo -> (CommonConstants.ENTERPRISE).equalsIgnoreCase(postpaidPlanPojo.getBusinessType())).collect(Collectors.toList());
        }
        System.out.println("pojoList.size2 "+pojoList.size());
        return pojoList;
    }


    public List<PostpaidPlanPojo> getPartnerPlanListByServiceArea(Integer partnerId, List<Integer> serviceAreaId, String planmode, String planGroup,Integer mvnoId) {

        List<PostpaidPlanPojo> pojoList = new ArrayList<>();
        Partner partner = partnerRepository.findById(partnerId).orElse(null);
        if (partner != null && partner.getPriceBookId() != null) {
            List<PostPaidPlanServiceAreaMapping> list = planServiceAreaRepo.findAllByServiceIdIn(serviceAreaId);
            List<Integer> planIds = list.stream().map(PostPaidPlanServiceAreaMapping::getPlanId).collect(Collectors.toList());

            QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
            BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.status.eq(CommonConstants.ACTIVE_STATUS));

            booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(planIds));

            if (!planmode.equals(Constants.ALL))
                booleanExpression = booleanExpression.and(qPostpaidPlan.mode.eq(planmode));

            if (planGroup != null && !"".equals(planGroup) && !Constants.PLAN_GROUP_ALL.equalsIgnoreCase(planGroup))
                booleanExpression = booleanExpression.and(qPostpaidPlan.planGroup.equalsIgnoreCase(planGroup));

            if (getBUIdsFromCurrentStaff().size() != 0)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()));

            if(mvnoId!=1){
                booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(mvnoId)));
            }

            List<PostpaidPlan> planList = (List<PostpaidPlan>) this.getRepository().findAll(booleanExpression);
            List<PostpaidPlanPojo> result = planList.stream().map(data -> {
                try {
                    return postpaidPlanMapper.domainToDTO(data, new CycleAvoidingMappingContext());
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());

            BusinessUnit businessUnit = new BusinessUnit();
            if (getBUIdsFromCurrentStaff().size() == 1)
                businessUnit = businessUnitRepository.findById(getBUIdsFromCurrentStaff().get(0)).get();
            if ((Objects.isNull(businessUnit.getPlanBindingType())) || (CommonConstants.PREDEFINED).equalsIgnoreCase(businessUnit.getPlanBindingType()))
                pojoList = result.stream().filter(postpaidPlanPojo -> (Objects.isNull(postpaidPlanPojo.getBusinessType()) || (CommonConstants.RETAIL).equalsIgnoreCase(postpaidPlanPojo.getBusinessType()))).collect(Collectors.toList());
            else if (businessUnit.getPlanBindingType().equalsIgnoreCase(CommonConstants.ON_DEMAND))
                pojoList = result.stream().filter(postpaidPlanPojo -> (CommonConstants.ENTERPRISE).equalsIgnoreCase(postpaidPlanPojo.getBusinessType())).collect(Collectors.toList());

            PriceBook priceBook = partner.getPriceBookId();

            if (priceBook != null && priceBook.getCommission_on().equalsIgnoreCase("Service level")) {
                List<Long> serviceIds = priceBook.getServiceCommissionList().stream().map(x -> x.getServiceId()).collect(Collectors.toList());
                if (serviceIds != null && !serviceIds.isEmpty())
                    pojoList = pojoList.stream().filter(x -> x.getServiceId() != null && serviceIds.contains(x.getServiceId().longValue())).collect(Collectors.toList());
            }

            if (!priceBook.getIsAllPlanSelected()) {
                List<PriceBookPlanDetail> bookPlanDetails = priceBook.getPriceBookPlanDetailList();
                bookPlanDetails = bookPlanDetails.stream().filter(x -> x.getPostpaidPlan() != null).collect(Collectors.toList());
                if (bookPlanDetails != null && !bookPlanDetails.isEmpty()) {
                    List<Integer> planlist = bookPlanDetails.stream().map(y -> y.getPostpaidPlan().getId()).collect(Collectors.toList());
                    if (!planlist.isEmpty())
                        pojoList = pojoList.stream().filter(x -> planlist.contains(x.getId().intValue())).collect(Collectors.toList());
                } else pojoList.clear();
            }

            if (priceBook.getIsAllPlanSelected() && partner.getPartnerType().equalsIgnoreCase("Franchise") && partner.getParentPartner() != null && !partner.getParentPartner().getPriceBookId().getIsAllPlanSelected() && partner.getParentPartner().getParentPartner() == null) {
                List<PriceBookPlanDetail> bookPlanDetails = partner.getParentPartner().getPriceBookId().getPriceBookPlanDetailList();
                bookPlanDetails = bookPlanDetails.stream().filter(x -> x.getPostpaidPlan() != null).collect(Collectors.toList());
                if (bookPlanDetails != null && !bookPlanDetails.isEmpty()) {
                    List<Integer> planlist = bookPlanDetails.stream().map(y -> y.getPostpaidPlan().getId()).collect(Collectors.toList());
                    if (!planlist.isEmpty())
                        pojoList = pojoList.stream().filter(x -> planlist.contains(x.getId().intValue())).collect(Collectors.toList());
                } else pojoList.clear();
            }

            if (priceBook.getIsAllPlanSelected() && partner.getPartnerType().equalsIgnoreCase("Franchise") && partner.getParentPartner() != null && partner.getParentPartner().getPriceBookId().getIsAllPlanSelected() && partner.getParentPartner().getParentPartner() != null && !partner.getParentPartner().getParentPartner().getPriceBookId().getIsAllPlanSelected()) {
                List<PriceBookPlanDetail> bookPlanDetails = partner.getParentPartner().getParentPartner().getPriceBookId().getPriceBookPlanDetailList();
                bookPlanDetails = bookPlanDetails.stream().filter(x -> x.getPostpaidPlan() != null).collect(Collectors.toList());
                if (bookPlanDetails != null && !bookPlanDetails.isEmpty()) {
                    List<Integer> planlist = bookPlanDetails.stream().map(y -> y.getPostpaidPlan().getId()).collect(Collectors.toList());
                    if (!planlist.isEmpty())
                        pojoList = pojoList.stream().filter(x -> planlist.contains(x.getId().intValue())).collect(Collectors.toList());
                } else pojoList.clear();
            }
        }
        return pojoList;
    }

    public List<PostpaidPlanPojo> getPlanListByServiceAreaAndCustId(Integer serviceAreaId, String planmode, String planGroup, Integer custId) {
        List<PostpaidPlanPojo> result = null;
        try {
            List<PostPaidPlanServiceAreaMapping> list = planServiceAreaRepo.findAllByServiceIdIn(Collections.singletonList(serviceAreaId));
            List<Integer> planIds = list.stream().map(PostPaidPlanServiceAreaMapping::getPlanId).collect(Collectors.toList());

            QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
            BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.status.eq(CommonConstants.ACTIVE_STATUS));

            booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(planIds));

            if (!planmode.equals(Constants.ALL))
                booleanExpression = booleanExpression.and(qPostpaidPlan.mode.eq(planmode));

            if (planGroup != null && !"".equals(planGroup) && !Constants.PLAN_GROUP_ALL.equalsIgnoreCase(planGroup))
                booleanExpression = booleanExpression.and(qPostpaidPlan.planGroup.equalsIgnoreCase(planGroup));

            if (getBUIdsFromCurrentStaff().size() != 0)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(getMvnoIdFromCurrentStaff(custId)).and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()))));
            List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findByCustId(custId);
            List<ServicesDTO> servicesList = new ArrayList<>();
            for (CustomerServiceMapping customerServiceMapping : customerServiceMappingList)
                servicesList.add(servicesService.getEntityById(customerServiceMapping.getServiceId(),getMvnoIdFromCurrentStaff(custId)));
            if (customerServiceMappingList.size() == 1)
                booleanExpression = booleanExpression.and(qPostpaidPlan.serviceId.eq(customerServiceMappingList.get(0).getServiceId().intValue()));
            List<PostpaidPlan> planList = (List<PostpaidPlan>) this.getRepository().findAll(booleanExpression);
            result = planList.stream().map(data -> {
                try {
                    return postpaidPlanMapper.domainToDTO(data, new CycleAvoidingMappingContext());
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
    @Override
    public Page<PostpaidPlan> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder,
                                  List<GenericSearchModel> filterList) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if(getMvnoIdFromCurrentStaff() == 1)
            return entityRepository.findAll(pageRequest);
        if (null == filterList || 0 == filterList.size())
            return entityRepository.findAll(pageRequest, Arrays.asList(1, getMvnoIdFromCurrentStaff()));
        else
            return search(filterList, pageNumber, customPageSize, sortBy, sortOrder);
    }
*/
  /*  @Override
    public PostpaidPlan get(Integer id) {
        String cacheKey = "postpaidPlan:" + id;
        PostpaidPlan postpaidPlan = null;
        redisPostpaidPlan = ApplicationContextProvider.getApplicationContext().getBean("redisPostpaidPlan", RedisTemplate.class);
        try {
        // First check if data is available in Redis cache
            postpaidPlan = redisPostpaidPlan.opsForValue().get(cacheKey);
//        if (postpaidPlan != null) {
//
//               postpaidPlan =  new ObjectMapper().readValue(cachedData, PostpaidPlan.class); // ✅ Immediate response from cache
//
//        }

        if (postpaidPlan == null) {
            // Fetch from DB if not in cache
            postpaidPlan = super.get(id);
            if (postpaidPlan != null) {
                redisPostpaidPlan.opsForValue().set(cacheKey, postpaidPlan); // Cache for 10 minutes
            }
        }

        // Apply filtering logic before returning
        return filterPostpaidPlan(postpaidPlan);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
*/
    @Override
    public PostpaidPlan get(Integer id,Integer mvnoId) {
//        long startTime = System.currentTimeMillis();
        String cacheKey = cacheKeys.POSTPAIDPLAN + id;
        PostpaidPlan postpaidPlan = null;
        try {
            postpaidPlan = (PostpaidPlan) cacheService.getFromCache(cacheKey, PostpaidPlan.class);
//            Long endtime = System.currentTimeMillis();
//            log.warn("::::::::: Total get postpaid data from cache time ::::::::: "+ (endtime - startTime));

            if (postpaidPlan != null) {
                // Return the cached plan if available
                return postpaidPlan;
            }

            // If not found in the cache, fetch from database
            postpaidPlan = super.get(id,mvnoId);

            if (postpaidPlan != null) {
                PostpaidPlan filteredPlan = filterPostpaidPlan(postpaidPlan, mvnoId);

                // Cache the result for future use (both in local and Redis cache)
                cacheService.putInCache(cacheKey, filteredPlan);
                return filteredPlan;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public String getQuotaResetInterval(Integer planId) {
        // Generate the cache key for PostpaidPlan based on the planId
        String cacheKey = cacheKeys.POSTPAIDPLAN + planId;
        String quotaResetInterval = null;

        try {
            PostpaidPlan postpaidPlan = (PostpaidPlan) cacheService.getFromCache(cacheKey, PostpaidPlan.class);
            if (postpaidPlan != null) {
                quotaResetInterval = postpaidPlan.getQuotaResetInterval();
                if (quotaResetInterval != null) {
                    return quotaResetInterval;
                }
            }

            quotaResetInterval = postpaidPlanRepo.findQuotaResetIntervalByPlanId(planId);

            if (quotaResetInterval != null) {
                cacheService.putInCache(cacheKey, postpaidPlan);
                return quotaResetInterval;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }




    private PostpaidPlan filterPostpaidPlan(PostpaidPlan postpaidPlan,Integer mvnoId) {
        if (postpaidPlan == null) return null;

//        List<Long> buids = getBUIdsFromCurrentStaff();
        Set<Long> buidSet = new HashSet<>(getBUIdsFromCurrentStaff());

        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId== 1 ||
                ((postpaidPlan.getMvnoId() == mvnoId.intValue() || postpaidPlan.getMvnoId() == 1) &&
                        (postpaidPlan.getMvnoId() == 1 || buidSet.isEmpty() || buidSet.contains(postpaidPlan.getBuId()) || postpaidPlan.getBuId() == null))) {
            return postpaidPlan;
        }
        return null;
    }


    public PostpaidPlan getEntityForUpdateAndDelete(Integer id,Integer mvnoId) {
        PostpaidPlan postpaidPlan = get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if (postpaidPlan == null || (!(mvnoId == 1 || mvnoId.intValue() == postpaidPlan.getMvnoId().intValue()) && (postpaidPlan.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(postpaidPlan.getBuId()))))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return postpaidPlan;
    }

    public List<PostpaidPlan> getAllPlanForCustomerCreationOrChangePlan(String type, Integer mvnoId) {
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        QPostPaidPlanServiceAreaMapping qPostPaidPlanServiceAreaMapping = QPostPaidPlanServiceAreaMapping.postPaidPlanServiceAreaMapping;
        BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.status.eq(CommonConstants.ACTIVE_STATUS));
        if (type.equalsIgnoreCase(Constants.NORMAL))
            booleanExpression = booleanExpression.and(qPostpaidPlan.mode.eq(Constants.NORMAL));
        else if (type.equalsIgnoreCase(Constants.SPECIAL))
            booleanExpression = booleanExpression.and(qPostpaidPlan.mode.eq(Constants.SPECIAL));
        booleanExpression = booleanExpression.and(qPostpaidPlan.planGroup.equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_ONLY).or(qPostpaidPlan.planGroup.equalsIgnoreCase(SubscriberConstants.PLAN_PURCHASE_REGISTRATION_AND_RENEWWAL)));
        if (getLoggedInUserId() != 1) {
            List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
            booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(query.select(qPostPaidPlanServiceAreaMapping.planId).from(qPostPaidPlanServiceAreaMapping).where(qPostPaidPlanServiceAreaMapping.serviceId.in(serviceIDs))));
        }
        if (getBUIdsFromCurrentStaff().size() != 0)
            booleanExpression = booleanExpression.and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()));
        // TODO: pass mvnoID manually 6/5/2025
        return IterableUtils.toList(entityRepository.findAll(booleanExpression)).stream().filter(postpaidPlan -> postpaidPlan.getMvnoId() == 1 || mvnoId== 1 || postpaidPlan.getMvnoId() == mvnoId).collect(Collectors.toList());
    }

    public List<PostpaidPlanPojo> getPlansForChildCustomers(Integer parentCustId) {

        QCustomerPackage qcustomerPackage = QCustomerPackage.customerPackage;
        BooleanExpression booleanExpression = qcustomerPackage.isNotNull().and(qcustomerPackage.isDelete.eq(false)).and(qcustomerPackage.customers.id.eq(parentCustId));
        List<CustomerPackage> customerPackageList = (List<CustomerPackage>) customerPackageRepository.findAll(booleanExpression);
        List<Double> dbrList = customerPackageList.stream().map(CustomerPackage::getDbr).filter(data -> null != data).collect(Collectors.toList());
        Double maxDbr = 0.0;
        if (dbrList != null && !dbrList.isEmpty() && dbrList.size() > 0)
            maxDbr = dbrList.stream().max(Double::compare).get();

        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        BooleanExpression boolExp = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.status.eq(CommonConstants.ACTIVE_STATUS));

        if (maxDbr != 0.0) boolExp = boolExp.and(qPostpaidPlan.dbr.loe(maxDbr));

        if (getBUIdsFromCurrentStaff().size() != 0)
            // TODO: pass mvnoID manually 6/5/2025
            boolExp = boolExp.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(getMvnoIdFromCurrentStaff(parentCustId)).and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()))));

        List<PostpaidPlan> planList = (List<PostpaidPlan>) this.getRepository().findAll(boolExp);
        List<PostpaidPlanPojo> result = planList.stream().map(data -> {
            try {
                return postpaidPlanMapper.domainToDTO(data, new CycleAvoidingMappingContext());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        return result;
    }

    public GenericDataDTO updatePlanAssignment(CustomerCafAssignmentPojo pojo, Integer mvnoid) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (pojo.getPlanId() != null && pojo.getStaffId() != null) {
            PostpaidPlan plan = getEntityForUpdateAndDelete(pojo.getPlanId(),mvnoid);
            StaffUser staffUser = staffUserService.get(pojo.getStaffId(),mvnoid);
            //StaffUser loggedInUser = staffUserService.get(getLoggedInUserId());
            StringBuilder approvedByName = new StringBuilder();
            if (!staffUser.getUsername().equalsIgnoreCase("admin")) {
                if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,mvnoid).equals("TRUE")) {
                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(plan.getMvnoId(), plan.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PLAN, CommonConstants.HIERARCHY_TYPE, pojo.getFlag().equalsIgnoreCase("approved") ? true : false, plan.getNextTeamHierarchyMapping() == null ? true : false, postpaidPlanMapper.domainToDTO(plan, new CycleAvoidingMappingContext()));
                    if (!map.containsKey("staffId") && !map.containsKey("nextTatMappingId")) {
                        plan.setNextStaff(null);
                        plan.setNextTeamHierarchyMapping(null);
                        if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
                            plan.setStatus(SubscriberConstants.ACTIVE);
                        } else if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected")) {
                            plan.setStatus(SubscriberConstants.REJECT);
                        }
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, plan.getId(), plan.getName(), staffUser.getId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " by :- " + staffUser.getUsername());
                    } else {
                        plan.setNextTeamHierarchyMapping(Integer.valueOf(map.get("nextTatMappingId")));
                        plan.setNextStaff(Integer.valueOf(map.get("staffId")));
                        StaffUser assigned = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).get();
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, plan.getId(), plan.getName(), staffUser.getId(), assigned.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + "Assigned to :- " + assigned.getUsername());
                        if (assigned.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                            tatUtils.saveOrUpdateDataForTatMatrix(map, assigned, plan.getId(), null);
                        }
                    }
                    if (plan.getPlanStatus() != null) {
                        if (plan.getPlanStatus().equalsIgnoreCase("Approved") || plan.getPlanStatus().equalsIgnoreCase("Rejected")) {
                            map.put("entityId", plan.getId().toString());
                            tatUtils.inActivateTatWorkflowMapping(map);
                        }
                    }
                }
                else {

                    if (plan != null && plan.getCreatedById() != null && plan.getCreatedById() != getLoggedInUserId()) {
                        String flag = pojo != null && pojo.getFlag() != null
                                ? pojo.getFlag().toLowerCase()
                                : "";

                        throw new CustomValidationException(
                                APIConstants.FORBIDDEN,
                                "You are not allowed to " + flag,
                                null
                        );
                    }
                    if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected") && plan.getNextTeamHierarchyMapping() == null) {
                        hierarchyService.rejectDirectFromCreatedStaff(CommonConstants.WORKFLOW_EVENT_NAME.PLAN, plan.getId());
                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, plan.getId(), plan.getName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());

                    }
                    else {
                        Map<String, Object> mapForManual = hierarchyService.getTeamForNextApprove(plan.getMvnoId(), plan.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PLAN, CommonConstants.HIERARCHY_TYPE, pojo.getFlag().equalsIgnoreCase("approved") ? true : false, plan.getNextTeamHierarchyMapping() == null, postpaidPlanMapper.domainToDTO(plan, new CycleAvoidingMappingContext()));
                        if (mapForManual.containsKey("assignableStaff")) {
                            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(pojo.getPlanId()).orElse(null);
                            if (postpaidPlan != null) {
                                if (postpaidPlan.getStatus().equalsIgnoreCase("Active")) {
                                    genericDataDTO.setDataList(null);
                                    plan.setNextStaff(null);
                                    plan.setNextTeamHierarchyMapping(null);
                                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, plan.getId(), plan.getName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
                                } else {
                                    genericDataDTO.setDataList((List<StaffUserPojo>) mapForManual.get("assignableStaff"));
                                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, plan.getId(), plan.getName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
                                }
                            }


                        }
                        else {
                            plan.setNextStaff(null);
                            plan.setNextTeamHierarchyMapping(null);
                            if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
                                plan.setPlanStatus("Approved");
                                plan.setStatus(SubscriberConstants.ACTIVE);
                            } else if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected")) {
                                plan.setPlanStatus("Rejected");
                                plan.setStatus(SubscriberConstants.REJECT);
                            }

                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, plan.getId(), plan.getName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
                        }
                    }
                }


            } else {
                approvedByName.append("Administrator");
                if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
                    plan.setStatus(SubscriberConstants.ACTIVE);
                    plan.setNextStaff(null);
                    plan.setNextTeamHierarchyMapping(null);
                } else {
                    plan.setStatus(SubscriberConstants.REJECT);
                    plan.setNextStaff(null);
                    plan.setNextTeamHierarchyMapping(null);
                }
                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN, plan.getId(), plan.getName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("rejected") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
            }
            PostpaidPlan savedPlan = save(plan);
            genericDataDTO.setData(savedPlan);
            String cacheKey = cacheKeys.POSTPAIDPLAN + savedPlan.getId();
            cacheService.saveOrUpdateInCacheAsync(savedPlan,cacheKey);
            if (savedPlan.getStatus().equalsIgnoreCase("Active")) {
                PostpaidPlanPojo postpaidPlanPojo = convertPostpaidPlanModelToPostpaidPlanPojo(savedPlan);
                sendCreateDataShared(postpaidPlanPojo, CommonConstants.OPERATION_UPDATE, true);
            }
        }
        return genericDataDTO;
    }


    public Long getCountOfApprovalReuqestforPlanByStaff(StaffUser staffUserTemp) {
        return postpaidPlanRepo.findMinimumApprovalReuqestForPlanByStaff(staffUserTemp.getId());
    }

    public String updatePlanStatus(Integer id, String status,Integer mvnoId,LocalDate endDate) {

        PostpaidPlan postpaidPlan = new PostpaidPlan();
        postpaidPlan = postpaidPlanRepo.findById(id).orElse(null);
        // TODO: pass mvnoID manually 6/5/2025
        if (postpaidPlan == null || (!(mvnoId == 1 || mvnoId.intValue() == postpaidPlan.getMvnoId().intValue()) && (postpaidPlan.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(postpaidPlan.getBuId())))) {
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        }
        //Integer updateIndex= planRepository.updatePlanStatusById(id,status);
        if(endDate!=null) postpaidPlan.setEndDate(endDate);
        postpaidPlan.setUpdatedate(LocalDateTime.now());
        postpaidPlan.setStatus(status);
        update(postpaidPlan);
        if (Objects.nonNull(postpaidPlan)) {
            return "Updated successfully";
        }
        return "Updation Failed";
    }

    public GenericDataDTO getPlanApprovalsList(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", CommonConstants.SORT_ORDER_DESC);
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        BooleanExpression booleanExpression = qPostpaidPlan.isNotNull().and(qPostpaidPlan.isDelete.eq(false)).and(qPostpaidPlan.status.eq(SubscriberConstants.NEW_ACTIVATION)).and(qPostpaidPlan.nextStaff.eq(getLoggedInUserId()));
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.in(1, mvnoId));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPostpaidPlan.mvnoId.eq(1).or(qPostpaidPlan.mvnoId.eq(mvnoId).and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()))));
        }

        Page<PostpaidPlan> paginationList = postpaidPlanRepo.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> {
            try {
                return postpaidPlanMapper.domainToDTO(data, new CycleAvoidingMappingContext());
            } catch (NoSuchFieldException e) {
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


    private BooleanExpression getPlanByPartnerBundle(QPostpaidPlan qPostpaidPlan, BooleanExpression booleanExpression,Integer mvnoId) {
        if (getLoggedInUserPartnerId() != 1) {
            Partner partner = partnerService.get(getLoggedInUserPartnerId(),mvnoId);
            Boolean isAllPlanSelected = partner.getPriceBookId().getIsAllPlanSelected();
            if (!isAllPlanSelected) {
                QPriceBookPlanDetail qPriceBookPlanDetail = QPriceBookPlanDetail.priceBookPlanDetail;
                BooleanExpression expression = qPriceBookPlanDetail.isNotNull().and(qPriceBookPlanDetail.priceBook.eq(partner.getPriceBookId()));
                List<PriceBookPlanDetail> priceBookPlanDetails = (List<PriceBookPlanDetail>) priceBookPlanDtlRepository.findAll(expression);
                List<Integer> partnerPlanIds = new ArrayList<>();
                for (PriceBookPlanDetail plan : priceBookPlanDetails) {
                    if (plan.getPostpaidPlan() != null) {
                        partnerPlanIds.add(plan.getPostpaidPlan().getId());
                    }
                }
                booleanExpression = booleanExpression.and(qPostpaidPlan.id.in(partnerPlanIds));
            }
        }
        return booleanExpression;
    }

    public List<PostpaidPlan> getPlanByLeadId(Long id) {
        try {
            LeadMaster leadMaster = leadMasterRepository.findById(id).get();
            Integer planGroupId = leadMaster.getPlangroupid();
            QPlanGroupMapping qPlanGroupMapping = QPlanGroupMapping.planGroupMapping;
            BooleanExpression exp = qPlanGroupMapping.isNotNull().and(qPlanGroupMapping.planGroup.planGroupId.eq(planGroupId));
            List<PlanGroupMapping> planGroupMappingList = (List<PlanGroupMapping>) planGroupMappingRepository.findAll(exp);
            List<PostpaidPlan> postpaidPlanList = planGroupMappingList.stream().map(i -> i.getPlan()).collect(Collectors.toList());

            return postpaidPlanList;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private double parseDoubleOrDefault(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String sanitizeString(String input) {
        return input.replaceAll("[^\\d.]", "");
    }

    public List<PostpaidPlan> getPlansByFilters(GetPlansByFilter requestDto, Integer parentPlanId,Integer mvnoId) {
//        if any changes done in this code snippet PLEASE inform Vikas Ak

//      **********************************  1. FOR INDIVIDUAL PLAN  ****************************************
        try {
//          Data for common filters
            Optional<Customers> customers = customersRepository.findById(requestDto.getCustId());
            String custType = customers.get().getCusttype();
            String custCurrency = customers.get().getCurrency();
            Integer parentId = null;
            Integer parentPlanIdForplangroup = null;
            if (customers.get().getParentCustomers() != null) {
                parentId = customers.get().getParentCustomers().getId();
                List<CustomerPlansModel> parentPlans = subscriberService.getActivePlanList(parentId, false);
                List<Integer> planIds = parentPlans.stream().map(CustomerPlansModel::getPlanId).collect(Collectors.toList());
                List<PostpaidPlan> postpaidPlanList = postpaidPlanRepo.findAllByIdIn(planIds);
                Double parentplanDbr = 0.00;
                for (PostpaidPlan plan : postpaidPlanList) {
                    if (parentplanDbr < plan.getDbr()) {
                        parentplanDbr = plan.getDbr();
                        parentPlanIdForplangroup = plan.getId();
                    }
                }
            }
            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(requestDto.getCustomerServiceMappingID()).get();
            Long serviceId = customerServiceMapping.getServiceId();
            Boolean isPrime = custSpecialPlanMapppingService.isCustomerPrimeOrNot(requestDto.getCustId(), null);
            boolean isbandWidthBooster = false;

//          Data for getting primePlans
            Set<PostpaidPlan> postpaidPlanListOfPrime = postpaidPlanService.getAllActivePremierePlan(customers.get().getServicearea().getId().intValue(),
                    requestDto.getCustId(), isPrime, null);
            List<Integer> primeIds = postpaidPlanListOfPrime.stream().map(PostpaidPlan::getId).collect(Collectors.toList());

            if (isPrime) {
                List<CustomerPlansModel> customerPlansModel = subscriberService.getActivePlanList(requestDto.getCustId(), false);
                List<Integer> planIds = customerPlansModel.stream().map(CustomerPlansModel::getPlanId).collect(Collectors.toList());
                if (planIds.size() > 0 && planIds != null) {
                    List<Integer> splPlanId = custSpecialPlanMapppingRepository.findSpecialPlanId(planIds);
                    if (splPlanId != null) {
                        primeIds.addAll(splPlanId);
                    }
                }
            }

//            Data for servicerea filter
            Long serviceAreaId = customers.get().getServicearea().getId();
            List<PostPaidPlanServiceAreaMapping> areaMapping = planServiceAreaRepo.findAllByServiceId(serviceAreaId.intValue());
            List<Integer> planIdsByarea = areaMapping.stream().map(PostPaidPlanServiceAreaMapping::getPlanId).collect(Collectors.toList());

//            ********************************************Common filters*************************************************
            QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
            BooleanExpression exp = qPostpaidPlan.isNotNull().and(qPostpaidPlan.status.equalsIgnoreCase(Constants.ACTIVE))
                    .and(qPostpaidPlan.plantype.equalsIgnoreCase(custType));
            if (!isPrime) {
                exp = exp.and(qPostpaidPlan.mode.equalsIgnoreCase("NORMAL"));
            }

            if (getBUIdsFromCurrentStaff().size() != 0) {
                exp = exp.and(qPostpaidPlan.buId.in(getBUIdsFromCurrentStaff()));
            }

            String changePlanType = requestDto.getChangePlanType();
            switch (changePlanType) {
                case Constants.CHANGE_PLAN:
                    exp = exp.and(qPostpaidPlan.serviceId.eq(serviceId.intValue()));
                    exp = exp.and(qPostpaidPlan.mode.notEqualsIgnoreCase(Constants.SPECIAL));
                    Boolean isInvoiceExist = isBillRunStatus(requestDto.getCustId(), requestDto.getCustomerServiceMappingID(), null);
                    if (isInvoiceExist) {
                        exp = exp.and((qPostpaidPlan.planGroup.equalsIgnoreCase(Constants.RENEW)).or(qPostpaidPlan.planGroup.eq(Constants.REGISTRATION_RENEWAL)));
                    } else {
                        exp = exp.and((qPostpaidPlan.planGroup.equalsIgnoreCase(Constants.REGISTRATION)).or(qPostpaidPlan.planGroup.eq(Constants.REGISTRATION_RENEWAL)));
                    }

                    if (parentId != null) {
                        Double parentplanDbr = parentDbr(parentId, serviceId, parentPlanIdForplangroup);
                        exp = exp.and(qPostpaidPlan.dbr.loe(parentplanDbr));
                    }
                    break;
//                    if (clientServiceSrv.getValueByName(ClientServiceConstant.CHECK_PENDING_REVENUE_CHANGEPLAN).equals(1))

                case Constants.CREATION:
                    exp = exp.and(qPostpaidPlan.serviceId.eq(serviceId.intValue()));
                    exp = exp.and((qPostpaidPlan.planGroup.equalsIgnoreCase(Constants.REGISTRATION)).or(qPostpaidPlan.planGroup.eq(Constants.REGISTRATION_RENEWAL)));
                    break;

                case Constants.RENEW:
                    exp = exp.and(qPostpaidPlan.serviceId.eq(serviceId.intValue()));
                    exp = exp.and((qPostpaidPlan.planGroup.equalsIgnoreCase(Constants.RENEW)).or(qPostpaidPlan.planGroup.eq(Constants.REGISTRATION_RENEWAL)));

                    if (parentId != null) {
                        Double parentplanDbr = parentDbr(parentId, serviceId, parentPlanIdForplangroup);
                        exp = exp.and(qPostpaidPlan.dbr.loe(parentplanDbr));
                    }
                    if (clientServiceSrv.getValueByName(ClientServiceConstant.RENEW_BY_BUSINESS_PROMOTION_ONLY).equalsIgnoreCase("1")) {
                        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
                        BooleanExpression exp4 = qCustPlanMappping.isNotNull().and(qCustPlanMappping.custServiceMappingId.eq(requestDto.getCustomerServiceMappingID())).
                                and(qCustPlanMappping.customer.id.eq(requestDto.getCustId())).and(qCustPlanMappping.billTo.equalsIgnoreCase(com.adopt.apigw.constants.Constants.ORGANIZATION)).
                                and(qCustPlanMappping.planGroup.isNull());
                        List<CustPlanMappping> customerPackages = (List<CustPlanMappping>) custPlanMapppingRepository.findAll(exp4);
                        if (customerPackages.size() > 0) {
                            exp = exp.and(qPostpaidPlan.category.equalsIgnoreCase(Constants.BUSINESS_PROMOTION));
                        }
                    }
                    break;

                case Constants.ADD_ON:
                    PlanService services = planServiceRepository.findById(serviceId.intValue()).get();
                    exp = exp.and(qPostpaidPlan.serviceId.eq(serviceId.intValue()));
                    exp = exp.and((qPostpaidPlan.planGroup.notEqualsIgnoreCase(Constants.RENEW)).and(qPostpaidPlan.planGroup.notEqualsIgnoreCase(Constants.REGISTRATION_RENEWAL)));
                    exp = exp.and(qPostpaidPlan.planGroup.notEqualsIgnoreCase(Constants.REGISTRATION));
                    exp = exp.and(qPostpaidPlan.planGroup.equalsIgnoreCase(Constants.VOLUME_BOOSTER));
                    if (services.getIs_dtv()) {
                        exp = exp.and(qPostpaidPlan.planGroup.equalsIgnoreCase(Constants.DTV_ADDON));
                    } else if (checkForBandwidthParameter(services)) {
                        exp = exp.or(qPostpaidPlan.planGroup.equalsIgnoreCase(Constants.BANDWIDTH_BOOSTER));
                        isbandWidthBooster = true;
                    }
                    break;

                default:
                    break;
            }
            if(mvnoId != 1){
                exp = exp.and(qPostpaidPlan.mvnoId.in(1L , mvnoId));
            }

            List<PostpaidPlan> postpaidPlanList = (List<PostpaidPlan>) postpaidPlanRepo.findAll(exp);

//          area filter
            postpaidPlanList = postpaidPlanList.stream().filter(i -> planIdsByarea.contains(i.getId())).collect(Collectors.toList());

//          serviceid filter
            postpaidPlanList = postpaidPlanList.stream().filter(i -> i.getServiceId().equals(serviceId.intValue())).collect(Collectors.toList());
//          *********************************************************************************************************************************************

//          serviceid filter
            postpaidPlanList = postpaidPlanList.stream().filter(i -> i.getServiceId().equals(serviceId.intValue())).collect(Collectors.toList());


            if (requestDto.getChangePlanType().equals(Constants.CHANGE_PLAN) || requestDto.getChangePlanType().equals(Constants.RENEW)) {
//          Prime Filter
                if (isPrime) {
                    postpaidPlanList = postpaidPlanList.stream().filter(i -> primeIds.contains(i.getId())).collect(Collectors.toList());
                }
//          filter for Checking based on parents plan Validity for child in renew and change plan
                if (parentId != null) {
                    Double parentValidity = parentValidity(parentId, serviceId);
                    List<PostpaidPlan> moreValidityPlans = new ArrayList<>();
                    for (PostpaidPlan postpaidPlan : postpaidPlanList) {
                        Double validity = postpaidPlan.getValidity();
                        if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_MONTHS))
                            validity = 30 * validity;
                        if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_YEARS))
                            validity = 365 * validity;

                        if (parentValidity < validity) {
                            moreValidityPlans.add(postpaidPlan);
                        }
                    }
                    postpaidPlanList.removeAll(moreValidityPlans);
                }
            } else if (requestDto.getChangePlanType().equals(Constants.ADD_ON) && isbandWidthBooster) {
//                Integer activeQosSpeed = highestQosSpeed(requestDto.getCustId(), requestDto.getCustomerServiceMappingID());
//                if (activeQosSpeed != 0) {
//                    List<PostpaidPlan> listQosIdNotNull = postpaidPlanList.stream().filter(postpaidPlan -> postpaidPlan.getQospolicy() != null && postpaidPlan.getPlanGroup().equalsIgnoreCase("Bandwidthbooster") &&
//                            extractNumericQosSpeed(postpaidPlan.getQospolicy().getQosspeed()) < activeQosSpeed).collect(Collectors.toList());
//
//                    if (listQosIdNotNull.size() > 0) {
//                        postpaidPlanList.removeAll(listQosIdNotNull);
//                    }
//                }
            }
            if (custCurrency != null) {
                postpaidPlanList = postpaidPlanList.stream()
                        .filter(plan -> custCurrency.equalsIgnoreCase(plan.getCurrency()))
                        .collect(Collectors.toList());
            }
            return postpaidPlanList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static int extractNumericQosSpeed(String input) {
        if (input == null) return 0;
        try {
            return Integer.parseInt(input.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private Integer highestQosSpeed(Integer custId, Integer customerServiceMappingId) {
        try {
            List<CustomerPlansModel> currentPlanList = this.subscriberService.getActivePlanList(custId, false);
            Optional<Integer> highestQosSpeed = Optional.of(1);
            if (currentPlanList.size() > 0) {
                currentPlanList = currentPlanList.stream().filter(i -> i.getPlangroup().equalsIgnoreCase(Constants.REGISTRATION)
                        || i.getPlangroup().equalsIgnoreCase(Constants.REGISTRATION_RENEWAL) || i.getPlangroup().equalsIgnoreCase(Constants.RENEW) || i.getPlangroup().equalsIgnoreCase(Constants.BANDWIDTH_BOOSTER)).filter(i -> i.getCustomerServiceMappingId().equals(customerServiceMappingId)).collect(Collectors.toList());

//                if (currentPlanList.get(0).getQosPolicyId() != null) {
//                    if (currentPlanList.get(0).getQosSpeed() != null) {
//                        try {
//                            int tempSpeed = Integer.parseInt(currentPlanList.get(0).getQosSpeed().trim());
//                            if (tempSpeed > highestQosSpeed) {
//                                highestQosSpeed = tempSpeed;
//                            }
//                        } catch (NumberFormatException e) {
//                        }
//                    }
//                } else {
//                    highestQosSpeed = 1;
//                }
                highestQosSpeed = currentPlanList.stream()
                        .filter(plan -> plan.getQosPolicyId() != null && plan.getQosSpeed() != null)
                        .map(plan -> {
                            try {
                                return Integer.parseInt(plan.getQosSpeed().trim());
                            } catch (NumberFormatException e) {
                                return null; // or handle the exception as per your requirement
                            }
                        })
                        .filter(speed -> speed != null)
                        .max(Integer::compareTo);

            }
            if (!highestQosSpeed.isPresent()) {
                return 0;
            } else {
                return highestQosSpeed.get();
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkForBandwidthParameter(PlanService services) {
        try {
            QServiceParamMapping qServiceParamMapping = QServiceParamMapping.serviceParamMapping;
            BooleanExpression exp = qServiceParamMapping.isNotNull().and(qServiceParamMapping.serviceid.eq(services.getId().longValue())).and(qServiceParamMapping.serviceParamId.eq(1L));
            List<ServiceParamMapping> serviceParamMappings = (List<ServiceParamMapping>) serviceParamMappingRepository.findAll(exp);
            if (serviceParamMappings.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Double parentValidity(Integer parentId, Long childServiceId) {
        Double parentValidity = 0.00;
        Customers parentCustomer = customersRepository.findById(parentId).get();
        List<CustomerServiceMapping> customerServiceMapping = customerServiceMappingRepository.findByCustId(parentId);
        List<Long> parentServiceId = customerServiceMapping.stream().map(CustomerServiceMapping::getServiceId).collect(Collectors.toList());
        boolean sameServiceid = parentServiceId.stream().anyMatch(serviceId -> Objects.equals(serviceId, childServiceId));

        if (sameServiceid) {
            QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
            BooleanExpression exp2 = qCustomerServiceMapping.isNotNull().and(qCustomerServiceMapping.serviceId.eq(childServiceId)).and(qCustomerServiceMapping.custId.eq(parentId));
            List<CustomerServiceMapping> customerServiceMappingList = (List<CustomerServiceMapping>) customerServiceMappingRepository.findAll(exp2);

            if (customerServiceMappingList.size() > 0) {
                List<Integer> parentCustServiceMappingIds = customerServiceMappingList.stream().map(CustomerServiceMapping::getId).collect(Collectors.toList());
                QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
                BooleanExpression exp3 = qCustPlanMappping.planGroup.planGroupId.isNull().and(qCustPlanMappping.custServiceMappingId.in(parentCustServiceMappingIds))
                        .and(qCustPlanMappping.customer.id.eq(parentId));
                List<CustPlanMappping> customerPackage = (List<CustPlanMappping>) custPlanMapppingRepository.findAll(exp3);
                if (customerPackage.size() > 0) {
                    List<Integer> postpaidPlanids = customerPackage.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toList());
                    List<PostpaidPlan> postpaidPlanList = postpaidPlanRepo.findAllByIdIn(postpaidPlanids);

                    for (PostpaidPlan plan : postpaidPlanList) {
                        Double validity = plan.getValidity();
                        if (plan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_MONTHS))
                            validity = 30 * validity;
                        if (plan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_YEARS))
                            validity = 365 * validity;

                        if (parentValidity < validity) {
                            parentValidity = validity;
                        }

                    }
                }


                if (parentValidity == 0.00) {
                    QCustPlanMappping qCustPlanMappping2 = QCustPlanMappping.custPlanMappping;
                    BooleanExpression exp4 = qCustPlanMappping2.custServiceMappingId.in(parentCustServiceMappingIds)
                            .and(qCustPlanMappping.customer.id.eq(parentId));
                    List<CustPlanMappping> customerPackagePlanGroup = (List<CustPlanMappping>) custPlanMapppingRepository.findAll(exp4);
                    if (customerPackagePlanGroup.size() > 0) {
                        List<Integer> postpaidPlanidsPlangroup = customerPackagePlanGroup.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toList());
                        List<PostpaidPlan> postpaidPlanListPlangroup = postpaidPlanRepo.findAllByIdIn(postpaidPlanidsPlangroup);

                        for (PostpaidPlan plan : postpaidPlanListPlangroup) {
                            if (parentValidity < plan.getDbr()) {
                                parentValidity = plan.getDbr();
                            }
                        }
                    }
                }


            }
        }
        return parentValidity;
    }

    private Double parentDbr(Integer parentId, Long childServiceId, Integer parentPlanIdForplangroup) {
        try {
            Double parentplanDbr = 0.00;
            Customers parentCustomer = customersRepository.findById(parentId).get();
            List<CustomerServiceMapping> customerServiceMapping = customerServiceMappingRepository.findByCustId(parentId);
            List<Long> parentServiceId = customerServiceMapping.stream().map(CustomerServiceMapping::getServiceId).collect(Collectors.toList());
            boolean sameServiceid = parentServiceId.stream().anyMatch(serviceId -> Objects.equals(serviceId, childServiceId));

            if (sameServiceid) {
                QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
                BooleanExpression exp2 = qCustomerServiceMapping.isNotNull().and(qCustomerServiceMapping.serviceId.eq(childServiceId)).and(qCustomerServiceMapping.custId.eq(parentId));
                List<CustomerServiceMapping> customerServiceMappingList = (List<CustomerServiceMapping>) customerServiceMappingRepository.findAll(exp2);

                if (customerServiceMappingList.size() > 0) {
                    List<Integer> parentCustServiceMappingIds = customerServiceMappingList.stream().map(CustomerServiceMapping::getId).collect(Collectors.toList());
                    QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
                    BooleanExpression exp3 = qCustPlanMappping.planGroup.planGroupId.isNull().and(qCustPlanMappping.custServiceMappingId.in(parentCustServiceMappingIds))
                            .and(qCustPlanMappping.customer.id.eq(parentId));
                    List<CustPlanMappping> customerPackage = (List<CustPlanMappping>) custPlanMapppingRepository.findAll(exp3);
                    if (customerPackage.size() > 0) {
                        List<Integer> postpaidPlanids = customerPackage.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toList());
                        List<PostpaidPlan> postpaidPlanList = postpaidPlanRepo.findAllByIdIn(postpaidPlanids);

                        for (PostpaidPlan plan : postpaidPlanList) {
                            if (parentplanDbr < plan.getDbr()) {
                                parentplanDbr = plan.getDbr();
                            }
                        }
                    }


                    if (parentplanDbr == 0.00) {
                        QCustPlanMappping qCustPlanMappping2 = QCustPlanMappping.custPlanMappping;
                        BooleanExpression exp4 = qCustPlanMappping2.custServiceMappingId.in(parentCustServiceMappingIds)
                                .and(qCustPlanMappping.customer.id.eq(parentId));
                        List<CustPlanMappping> customerPackagePlanGroup = (List<CustPlanMappping>) custPlanMapppingRepository.findAll(exp4);
                        if (customerPackagePlanGroup.size() > 0) {
                            List<Integer> postpaidPlanidsPlangroup = customerPackagePlanGroup.stream().map(CustPlanMappping::getPlanId).collect(Collectors.toList());
                            List<PostpaidPlan> postpaidPlanListPlangroup = postpaidPlanRepo.findAllByIdIn(postpaidPlanidsPlangroup);

                            for (PostpaidPlan plan : postpaidPlanListPlangroup) {
                                if (parentplanDbr < plan.getDbr()) {
                                    parentplanDbr = plan.getDbr();
                                }
                            }
                        }
                    }


                }
            }
            return parentplanDbr;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private Boolean isBillRunStatus(Integer custId, Integer customerServiceMappingID, Integer plangroupId) {
        try {
            Boolean isBillRunStatus = false;
            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
            BooleanExpression exp1 = qCustPlanMappping.isNotNull().and(qCustPlanMappping.customer.id.eq(custId))
                    .and(qCustPlanMappping.custServiceMappingId.eq(customerServiceMappingID)).and(qCustPlanMappping.custPlanStatus.equalsIgnoreCase("Active"));
            if (plangroupId != null) {
                exp1 = exp1.and(qCustPlanMappping.planGroup.planGroupId.eq(plangroupId));
            }
            List<CustPlanMappping> customerPackage = (List<CustPlanMappping>) custPlanMapppingRepository.findAll(exp1);
            if (customerPackage.size() > 0) {
                if (customerPackage.get(0).getIstrialplan() != null && !customerPackage.get(0).getIstrialplan()) {
                    List<Long> longIds = customerPackage.stream().map(CustPlanMappping::getDebitdocid).filter(Objects::nonNull).collect(Collectors.toList());
                    List<Integer> debitDocIds = longIds.stream()
                            .map(Long::intValue)
                            .collect(Collectors.toList());
                    List<DebitDocument> debitDocument = debitDocRepository.findAllByBillrunstatusAndIdIn("Generated", debitDocIds);
                    if (debitDocument.size() > 0) {
                        isBillRunStatus = true;
                    }
                }
            }
            return isBillRunStatus;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<PlanGroup> getPlanGroupByFilters(GetPlansByFilter requestDto) {
//        if any changes done in this code snippet PLEASE inform Vikas Ak

//       *********************************  2. FOR PLAN GROUP    *****************************************
        try {
//          Data for common filters
            Optional<Customers> customers = customersRepository.findById(requestDto.getCustId());
            String custType = customers.get().getCusttype();
            Integer parentId = null;
            if (customers.get().getParentCustomers() != null) {
                parentId = customers.get().getParentCustomersId();
            }
            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findById(requestDto.getCustomerServiceMappingID()).get();
            Long serviceId = customerServiceMapping.getServiceId();
            Boolean isPrime = custSpecialPlanMapppingService.isCustomerPrimeOrNot(requestDto.getCustId(), null);


//              Data for servicerea filter
            List<ServiceAreaPlanGroupMapping> areaMapping = serviceAreaPlangroupMappingRepo.findByServiceArea(customers.get().getServicearea());
            List<Integer> planGroupIdsByarea = areaMapping.stream().map(i -> i.getPlanGroup().getPlanGroupId()).collect(Collectors.toList());

//                Data for ServiceId filter,checks for same service id inside plans
            List<PlanGroup> plangroupsByservice = planGroupList(requestDto.getCustId());
            List<Integer> plangroupsByserviceId = plangroupsByservice.stream().map(PlanGroup::getPlanGroupId).collect(Collectors.toList());

//            ********************************************Common filters*************************************************
            QPlanGroup qPlanGroup = QPlanGroup.planGroup;
            BooleanExpression exp = qPlanGroup.isNotNull().and(qPlanGroup.status.equalsIgnoreCase(Constants.ACTIVE))
                    .and(qPlanGroup.plantype.equalsIgnoreCase(custType));

            if (getBUIdsFromCurrentStaff().size() != 0) {
                exp = exp.and(qPlanGroup.buId.in(getBUIdsFromCurrentStaff()));
            }

            if (!isPrime) {
                exp = exp.and(qPlanGroup.planMode.equalsIgnoreCase("NORMAL"));
            }

            String changePlanType = requestDto.getChangePlanType();
            switch (changePlanType) {
                case Constants.CHANGE_PLAN:
                    Boolean isInvoiceExist = isBillRunStatus(requestDto.getCustId(), requestDto.getCustomerServiceMappingID(), null);
                    if (isInvoiceExist) {
                        exp = exp.and((qPlanGroup.planGroupType.equalsIgnoreCase(Constants.REGISTRATION_RENEWAL)).or(qPlanGroup.planGroupType.equalsIgnoreCase(Constants.RENEW)));
                        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
                        BooleanExpression exp4 = qCustPlanMappping.isNotNull().and(qCustPlanMappping.custServiceMappingId.eq(requestDto.getCustomerServiceMappingID())).
                                and(qCustPlanMappping.customer.id.eq(requestDto.getCustId()));
                        if (requestDto.getPlanGroupId() != null)
                            exp4 = exp4.and(qCustPlanMappping.planGroup.planGroupId.eq(requestDto.getPlanGroupId()));
                        List<CustPlanMappping> customerPackages = (List<CustPlanMappping>) custPlanMapppingRepository.findAll(exp4);
                        if (customerPackages.size() > 0) {
                            exp = exp.or(qPlanGroup.category.equalsIgnoreCase(Constants.BUSINESS_PROMOTION));
                        } else {
                            exp = exp.and(qPlanGroup.category.equalsIgnoreCase(Constants.NORMAL));
                        }
                    } else {
                        exp = exp.and((qPlanGroup.planGroupType.equalsIgnoreCase(Constants.REGISTRATION_RENEWAL)).or(qPlanGroup.planGroupType.equalsIgnoreCase(Constants.REGISTRATION)));

                    }
                    if (parentId != null) {
                        Double parentplanDbr = parentDbrForPlanGroup(parentId, serviceId, requestDto.getPlanGroupId());
                        exp = exp.and(qPlanGroup.dbr.loe(parentplanDbr));
                    }

                    break;


                case Constants.RENEW:
                    exp = exp.and((qPlanGroup.planGroupType.equalsIgnoreCase(Constants.REGISTRATION_RENEWAL)).or(qPlanGroup.planGroupType.equalsIgnoreCase(Constants.RENEW)));
                    if (parentId != null) {
                        Double parentplanDbr = parentDbrForPlanGroup(parentId, serviceId, requestDto.getPlanGroupId());
                        exp = exp.and(qPlanGroup.dbr.loe(parentplanDbr));
                    }
                    if (clientServiceSrv.getValueByName(ClientServiceConstant.RENEW_BY_BUSINESS_PROMOTION_ONLY).equalsIgnoreCase("1")) {
                        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
                        BooleanExpression exp4 = qCustPlanMappping.isNotNull().and(qCustPlanMappping.custServiceMappingId.eq(requestDto.getCustomerServiceMappingID())).
                                and(qCustPlanMappping.customer.id.eq(requestDto.getCustId()));
                        if (requestDto.getPlanGroupId() != null)
                            exp4 = exp4.and(qCustPlanMappping.planGroup.planGroupId.eq(requestDto.getPlanGroupId()));
                        List<CustPlanMappping> customerPackages = (List<CustPlanMappping>) custPlanMapppingRepository.findAll(exp4);
                        if (customerPackages.size() > 0) {
                            exp = exp.or(qPlanGroup.category.equalsIgnoreCase(Constants.BUSINESS_PROMOTION));
                        } else {
                            exp = exp.and(qPlanGroup.category.equalsIgnoreCase(Constants.NORMAL));
                        }
                    }
                    break;
                default:
                    break;
            }
            List<PlanGroup> planGroups = (List<PlanGroup>) planGroupRepository.findAll(exp);

//            Prime planGroup adding to list
            if (isPrime) {
                QCustSpecialPlanMappping qCustSpecialPlanMappping = QCustSpecialPlanMappping.custSpecialPlanMappping;
                BooleanExpression exp7 = qCustSpecialPlanMappping.isNotNull().and(qCustSpecialPlanMappping.customer.id.eq(requestDto.getCustId()));
                if (requestDto.getPlanGroupId() != null) {
                    exp7 = exp7.and(qCustSpecialPlanMappping.normalPlanGroup.planGroupId.eq(requestDto.getPlanGroupId()));
                }
                List<CustSpecialPlanMappping> custSpecialPlanMapppingList = (List<CustSpecialPlanMappping>) custSpecialPlanMapppingRepository.findAll(exp7);
                List<PlanGroup> primePlangroups = custSpecialPlanMapppingList.stream().map(CustSpecialPlanMappping::getSpecialPlanGroup).distinct().collect(Collectors.toList());
                if (primePlangroups.size() > 0) {
                    planGroups.addAll(primePlangroups);
                }
            }

//          serviceId filter && ServiceArea filter
            planGroups = planGroups.stream()
                    .filter(i -> planGroupIdsByarea.contains(i.getPlanGroupId()) && plangroupsByserviceId.contains(i.getPlanGroupId()))
                    .collect(Collectors.toList());

            return planGroups;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Double parentDbrForPlanGroup(Integer parentId, Long childServiceId, Integer planGroupId) {
        try {
            Double parentDbrForPlanGroup = 0.00;
            Customers parentCustomer = customersRepository.findById(parentId).get();
            List<CustomerServiceMapping> customerServiceMapping = customerServiceMappingRepository.findByCustId(parentId);
            List<Long> parentServiceId = customerServiceMapping.stream().map(CustomerServiceMapping::getServiceId).collect(Collectors.toList());
            boolean isSameServiceid = parentServiceId.stream().anyMatch(serviceId -> Objects.equals(serviceId, childServiceId));

            if (isSameServiceid) {
                QCustomerServiceMapping qCustomerServiceMapping = QCustomerServiceMapping.customerServiceMapping;
                BooleanExpression exp2 = qCustomerServiceMapping.isNotNull().and(qCustomerServiceMapping.serviceId.eq(childServiceId)).and(qCustomerServiceMapping.custId.eq(parentId));
                List<CustomerServiceMapping> customerServiceMappingList = (List<CustomerServiceMapping>) customerServiceMappingRepository.findAll(exp2);

                if (customerServiceMappingList.size() > 0) {
                    List<Integer> parentCustServiceMappingIds = customerServiceMappingList.stream().map(CustomerServiceMapping::getId).collect(Collectors.toList());
                    QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
                    BooleanExpression exp3 = qCustPlanMappping.planGroup.planGroupId.isNotNull().and(qCustPlanMappping.custServiceMappingId.in(parentCustServiceMappingIds))
                            .and(qCustPlanMappping.customer.id.eq(parentId)).and(qCustPlanMappping.planGroup.planGroupId.eq(planGroupId));
                    List<CustPlanMappping> customerPackage = (List<CustPlanMappping>) custPlanMapppingRepository.findAll(exp3);
                    if (customerPackage.size() > 0) {
                        List<PlanGroup> postpaidPlanList = customerPackage.stream().map(CustPlanMappping::getPlanGroup).collect(Collectors.toList());


                        for (PlanGroup planGroup : postpaidPlanList) {
                            if (parentDbrForPlanGroup < planGroup.getDbr()) {
                                parentDbrForPlanGroup = planGroup.getDbr();
                            }

                        }
                    }
                }
            }
            return parentDbrForPlanGroup;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    List<PlanGroup> planGroupList(Integer custId) {
        QPlanGroupMapping qPlanGroupMapping = QPlanGroupMapping.planGroupMapping;
        QPlanGroup qPlanGroup = QPlanGroup.planGroup;
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        BooleanExpression exp = qPlanGroup.isNotNull();
        List<PlanGroup> plangroupList = new ArrayList<>();
        if (custId != null) {
            List<Integer> serviceIds = null;
            List<CustomerServiceMapping> customerServiceMappings = customerServiceMappingRepository.findByCustId(custId);
            if (!CollectionUtils.isEmpty(customerServiceMappings)) {
                serviceIds = customerServiceMappings.stream().map(map -> map.getServiceId().intValue()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(serviceIds)) {
                    JPAQuery<QPostpaidPlan> queryPlan = new JPAQuery<>(entityManager);
                    List<Integer> planIdList = queryPlan.from(qPostpaidPlan, qPlanGroupMapping).select(qPlanGroupMapping.planGroup.planGroupId)
                            .where(qPlanGroupMapping.plan.serviceId.in(serviceIds)).distinct().fetch();
                    exp = exp.and(qPlanGroup.planGroupId.in(planIdList));
                }
            }
            plangroupList = (List<PlanGroup>) planGroupRepository.findAll(exp);
            if (serviceIds != null) {
                List<Integer> finalServiceIds = serviceIds.stream().sorted().collect(Collectors.toList());
                plangroupList.removeIf(planGroup -> planGroup.getPlanMappingList().size() != finalServiceIds.size());
                plangroupList.removeIf(planGroup -> {
                    List<PostpaidPlan> list = planGroup.getPlanMappingList().stream().map(planGroupMapping -> planGroupMapping.getPlan()).collect(Collectors.toList());
                    List<Integer> serviceIdsList = list.stream().map(PostpaidPlan::getServiceId).collect(Collectors.toList());
                    return !UtilsCommon.listEqualsIgnoreOrder(serviceIdsList, finalServiceIds);
                });
                //planGroup.getPlanMappingList().stream().filter(planGroupMapping -> finalServiceIds.contains(planGroupMapping.getPlan().getServiceId()))
            }
        }
        return plangroupList;
    }

    public void sendCreateDataShared(PostpaidPlanPojo pojo, Integer operation, boolean isApprove) throws Exception {
        try {
            PostpaidPlan postPiadPlanEntity = getPostPaidPlanEntity(pojo);
            postPiadPlanEntity.setMvnoId(pojo.getMvnoId());
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                createDataSharedService.sendEntitySaveDataForAllMicroService(postPiadPlanEntity);
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                if (isApprove) {
                    postPiadPlanEntity.setIsApprove(true);
                    createDataSharedService.updateEntityDataForAllMicroService(postPiadPlanEntity);
                } else {
                    postPiadPlanEntity.setIsApprove(false);
                    createDataSharedService.updateEntityDataForAllMicroService(postPiadPlanEntity);
                }
            } else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
                createDataSharedService.deleteEntityDataForAllMicroService(postPiadPlanEntity);
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    public PostpaidPlan getPostPaidPlanEntity(PostpaidPlanPojo pojo) throws Exception {
        try {
            PostpaidPlan postPiadPlanEntity = convertPostpaidPlanPojoToPostpaidPlanModel(pojo);
            List<PostpaidPlanCharge> postpaidPlanCharges = new ArrayList<>();
            List<ServiceArea> serviceAreaList = new ArrayList<>();
            List<Productplanmappingdto> productplanmappingdtos = new ArrayList<>();
            List<PlanCasMapping> planCasMappingList = new ArrayList<>();
            if (pojo.getChargeList() != null) {
                for (PostpaidPlanChargePojo item : pojo.getChargeList()) {
                    PostpaidPlanCharge postpaidPlanCharge = new PostpaidPlanCharge();

                    if (item.getPlan() != null) {
                        postpaidPlanCharge.setPlanId(item.getPlan().getId());
                    } else {
                        postpaidPlanCharge.setPlanId(pojo.getId());
                    }
                    postpaidPlanCharge.setId(item.getId());
                    Optional<Charge> charge = chargeRepository.findById(item.getCharge().getId());
                    postpaidPlanCharge.setCharge(charge.get());
                    postpaidPlanCharge.setChargeName(charge.get().getName());
                    postpaidPlanCharge.setBillingCycle(item.getBillingCycle());
                    postpaidPlanCharge.setChargeprice(item.getChargeprice());
                    postpaidPlanCharges.add(postpaidPlanCharge);
//                    String cacheKey = cacheKeys.CHARGE_LIST + postpaidPlanCharge.getPlanId() + "_" + charge.get().getId();
//                    cacheService.saveOrUpdateInCacheAsync(postpaidPlanCharge,cacheKey);
                }
                postPiadPlanEntity.setChargeList(postpaidPlanCharges);
            }
            if (pojo.getServiceAreaNameList() != null) {
                for (ServiceAreaDTO item : pojo.getServiceAreaNameList()) {
                    ServiceArea serviceArea = new ServiceArea();
                    serviceArea.setId(item.getId());
                    serviceAreaList.add(serviceArea);
                }
                postPiadPlanEntity.setServiceAreaNameList(serviceAreaList);
            }
            if (pojo.getServiceAreaIds().size() > 0) {
                List<PostPaidPlanServiceAreaMapping> planServiceAreaMapping = planServiceAreaRepo.findAllByServiceIdInAndPlanId(pojo.getServiceAreaIds().stream()
                        .map(Long::intValue).collect(Collectors.toList()), pojo.getId());
                List<PostPaidPlanServiceAreaMapping> planServiceAreaMappingList = new ArrayList<>();
                for (PostPaidPlanServiceAreaMapping entity : planServiceAreaMapping) {
                    PostPaidPlanServiceAreaMapping postPaidPlanServiceAreaMapping = new PostPaidPlanServiceAreaMapping();
                    postPaidPlanServiceAreaMapping.setId(entity.getId());
                    postPaidPlanServiceAreaMapping.setPlanId(entity.getPlanId());
                    postPaidPlanServiceAreaMapping.setServiceId(entity.getServiceId());
                    postPaidPlanServiceAreaMapping.setCreatedOnString(entity.getCreatedOn().toString());
                    postPaidPlanServiceAreaMapping.setLastmodifiedOnString(entity.getLastmodifiedOn().toString());
                    planServiceAreaMappingList.add(postPaidPlanServiceAreaMapping);
                }
                postPiadPlanEntity.setPostPaidPlanServiceAreaMappingList(planServiceAreaMappingList);
            }
            if (pojo.getProductplanmappingList() != null) {
                for (Productplanmappingdto item : pojo.getProductplanmappingList()) {
                    Productplanmappingdto productplanmapping = new Productplanmappingdto();
                    productplanmapping.setPlanId(item.getPlanId());
                    productplanmapping.setProductCategoryId(item.getProductCategoryId());
                    productplanmapping.setProduct_type(item.getProduct_type());
                    productplanmapping.setProductId(item.getProductId());
                    productplanmapping.setRevisedCharge(item.getRevisedCharge());
                    productplanmapping.setOwnershipType(item.getOwnershipType());
                    productplanmapping.setProductQuantity(item.getProductQuantity());
                    productplanmapping.setCreatedById(getLoggedInUserId());
                    productplanmapping.setLastModifiedById(getLoggedInUserId());
                    productplanmapping.setCreatedByName(getLoggedInUser().getFullName());
                    productplanmapping.setLastModifiedByName(getLoggedInUser().getFullName());
                    productplanmappingdtos.add(productplanmapping);
                }
                postPiadPlanEntity.setProductplanmappingList(productplanmappingdtos);
            }
            if (pojo.getPlanCasMappingList() != null) {
                for (PlanCasMapping item : pojo.getPlanCasMappingList()) {
                    PlanCasMapping planCasMapping = new PlanCasMapping();
                    planCasMapping.setPlanId(item.getPlanId());
                    planCasMapping.setCasId(item.getCasId());
                    planCasMapping.setPackageId(item.getPackageId());
                    planCasMappingList.add(planCasMapping);
                }
                postPiadPlanEntity.setPlanCasMappingList(planCasMappingList);
            }
            if (pojo.getQospolicyid() != null) {
                Optional<QOSPolicy> qosPolicy = qosPolicyRepository.findById(pojo.getQospolicyid());
                if (qosPolicy.isPresent()) {
                    postPiadPlanEntity.setQospolicy(qosPolicy.get());
                }
            }
            if (pojo.getPlanGroup() != null) {
                postPiadPlanEntity.setPlanGroup(pojo.getPlanGroup());
            }
            postPiadPlanEntity.setCreatedById(getLoggedInUserId());
            postPiadPlanEntity.setLastModifiedById(getLoggedInUserId());
            postPiadPlanEntity.setCreatedByName(getLoggedInUser().getFullName());
            postPiadPlanEntity.setLastModifiedByName(getLoggedInUser().getFullName());
            return postPiadPlanEntity;
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }


    public void checkServiceAreaBind(Object object) {
        List<PostPaidPlanServiceAreaMapping> planServiceAreaMappings = new ArrayList<>();
        List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMapping = new ArrayList<>();

        List<Long> serviceAreaIds = new ArrayList<>();
        if (Objects.nonNull(object) && object.getClass().equals(PostpaidPlan.class)) {
            planServiceAreaMappings = planServiceAreaRepo.findAllByPlanId(((PostpaidPlan) object).getId());
            if (planServiceAreaMappings != null) {

                for (PostPaidPlanServiceAreaMapping postPaidPlanServiceAreaMapping : planServiceAreaMappings) {
                    serviceAreaIds.add(postPaidPlanServiceAreaMapping.getServiceId().longValue());
                }

                //Send Message to CommonGateWay
                PlanServiceAreaBindingCheckMessage message = new PlanServiceAreaBindingCheckMessage(serviceAreaIds);
                //messageSender.send(message,RabbitMqConstants.QUEUE_PLAN_SERVICE_AREA_BINDING_CHECK);
                //             kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));


            }

        } else if (Objects.nonNull(object) && object.getClass().equals(PlanGroup.class)) {
            serviceAreaPlanGroupMapping = serviceAreaPlangroupMappingRepo.findAllByPlanGroup(((PlanGroup) object));
            if (serviceAreaPlanGroupMapping != null) {
                for (ServiceAreaPlanGroupMapping serviceAreaPlanGroupMappings : serviceAreaPlanGroupMapping) {
                    serviceAreaIds.add(serviceAreaPlanGroupMappings.getServiceArea().getAreaId());
                }
                //Send Message to CommonGateWay
                PlanServiceAreaBindingCheckMessage message = new PlanServiceAreaBindingCheckMessage(serviceAreaIds);
                //messageSender.send(message,RabbitMqConstants.QUEUE_PLAN_SERVICE_AREA_BINDING_CHECK);
                //             kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
            }

        }
    }

    public void checkServiceAreaWhileDeletingPlan(Object object, Object updatedObject) {
        List<PostPaidPlanServiceAreaMapping> planServiceAreaMappings = new ArrayList<>();
        List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMapping = new ArrayList<>();

        List<Long> serviceAreaIds = new ArrayList<>();
        List<Long> serviceAreDelIds = new ArrayList<>();

        if (Objects.nonNull(object) && object.getClass().equals(PostpaidPlan.class) && Objects.nonNull(updatedObject) && updatedObject.getClass().equals(PostpaidPlanPojo.class)) {
            //comapare the old servicearea list to new servicearealist and find which servicearea is not in updated list
            List<ServiceArea> oldServieAreaList = ((PostpaidPlan) object).getServiceAreaNameList();
            List<Integer> removedAreaIds = new ArrayList<>();
            if (oldServieAreaList.size() > 0) {
                for (ServiceArea serviceArea : oldServieAreaList) {
                    if (!((PostpaidPlanPojo) updatedObject).getServiceAreaIds().contains(serviceArea.getAreaId())) {
                        removedAreaIds.add(serviceArea.getId().intValue());
                    }
                }
                planServiceAreaMappings = planServiceAreaRepo.findAllByServiceIdIn(removedAreaIds);
                if (planServiceAreaMappings.size() == 0) {
                    List<Long> removeIds = new ArrayList<>();
                    for (Integer ids : removedAreaIds) {
                        removeIds.add(ids.longValue());
                    }
                    //send message
                    PlanServiceAreaBindingCheckMessage message = new PlanServiceAreaBindingCheckMessage(removeIds);
                    //messageSender.send(message, RabbitMqConstants.QUEUE_PLAN_SERVICE_AREA_BINDING_CHECK);
                    kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));
                }
            }
            //check that service area has any entry in planservicearea mapping.

            //if not have entry then set it to false


        }
    }

    public PostpaidPlan getPlanByName(String planName, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(planName)) {
                throw new IllegalArgumentException("Plan name is mandatory. Please enter valid plan name.");
            } else {
                QPostpaidPlan qPlan = QPostpaidPlan.postpaidPlan;
                BooleanExpression boolExp = qPlan.isNotNull();
                boolExp = boolExp.and(qPlan.name.eq(planName));
                if (mvnoId != 1) boolExp = boolExp.and((qPlan.mvnoId.eq(mvnoId)).or(qPlan.mvnoId.eq(1)));
                Optional<PostpaidPlan> optionalPlan = postpaidPlanRepo.findOne(boolExp);
                if (!optionalPlan.isPresent()) {
                    throw new IllegalArgumentException("No plan found with plan name : '" + planName + "'. Please enter valid plan name.");
                } else {
                    return optionalPlan.get();
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<LightPostpaidPlanDTO> getPlanDetailsByServiceAreaSiteNameAndServiceId(List<Integer> serviceAreaId, List<Integer> serviceId, List<String> type,Integer mvnoId) {
        List<String> siteName = serviceAreaRepository.findSiteNameByServiceAreaId(serviceAreaId.stream().map(Integer::longValue).collect(Collectors.toList()));
        List<Integer> serviceAreaIds;
        if (siteName.size() > 0) {
            serviceAreaIds = serviceAreaRepository.findServiceAreaIdsFromSiteName(siteName).stream().map(Long::intValue).collect(Collectors.toList());
        } else {
            serviceAreaIds = new ArrayList<>(serviceAreaId);
        }
        List<LightPostpaidPlanDTO> lightPostpaidPlanDTOS = postpaidPlanRepo.findAllByServiceAreaIdsAndService(serviceAreaIds, serviceId, type);
        for (LightPostpaidPlanDTO postpaidPlan : lightPostpaidPlanDTOS) {
            List<PostPaidPlanServiceAreaMapping> planServiceAreaMappings = planServiceAreaRepo.findAllByPlanId(postpaidPlan.getId());
            postpaidPlan.setServiceAreaIds(planServiceAreaMappings.stream().map(postPaidPlanServiceAreaMapping -> postPaidPlanServiceAreaMapping.getServiceId().longValue()).collect(Collectors.toList()));
        }
//        lightPostpaidPlanDTOS = lightPostpaidPlanDTOS.stream().peek(lightPostpaidPlanDTO -> lightPostpaidPlanDTO.setServiceAreaIds(serviceAreaIds.stream().map(Integer::longValue).collect(Collectors.toList()))).collect(Collectors.toList());
        List<Integer> branchId = branchServiceAreaMappingRepository.findAllByServiceareaIdIn(serviceAreaIds.stream().map(i -> i.intValue()).collect(Collectors.toList())).stream().map(BranchServiceAreaMapping::getBranchId).collect(Collectors.toList()); // Assuming you want to get branchId based on the first serviceAreaId
        if (branchId.size() > 0) {
            lightPostpaidPlanDTOS = lightPostpaidPlanDTOS.stream().peek(i -> i.setBranchIds(branchId)).collect(Collectors.toList());
        }
        return updatePostpaidData(lightPostpaidPlanDTOS , mvnoId);
    }

    public List<LightPostpaidPlanDTO> updatePostpaidData(List<LightPostpaidPlanDTO> lightPostpaidPlanDTOS,Integer mvnoId) {

        lightPostpaidPlanDTOS = lightPostpaidPlanDTOS.stream()
                .peek(lightPostpaidPlanDTO -> {
                    Double price = getProratedPrice(lightPostpaidPlanDTO,mvnoId);
                    lightPostpaidPlanDTO.setProRatedCharge(price);
                })
                .collect(Collectors.toList());

        return lightPostpaidPlanDTOS;
    }

    private Double getProratedPrice(LightPostpaidPlanDTO lightPostpaidPlanDTO,Integer mvnoId) {
        LocalDate nextBillDate = null;
        Double proratedPrice;
        LocalDate minNextBillDate = LocalDate.now().plusDays(400);
        try {
            PostpaidPlan plan = postpaidPlanService.get(lightPostpaidPlanDTO.getId(), mvnoId);
            LocalDate today = LocalDate.now();
            int billingCycle = plan.getChargeList().get(0).getBillingCycle();
            int billDay = 1;
            if (today.getDayOfMonth() < billDay && billingCycle == 1) {
                nextBillDate = today.withDayOfMonth(billDay);
                if (nextBillDate.isBefore(minNextBillDate)) minNextBillDate = nextBillDate;
            } else if (plan != null && plan.getChargeList() != null && plan.getChargeList().size() > 0) {
                for (int j = 0; j < plan.getChargeList().size(); j++) {
                    if (plan.getChargeList().get(j).getCharge().getChargetype().equals(CommonConstants.CHARGE_TYPE_RECURRING) ||
                            plan.getChargeList().get(j).getCharge().getChargetype().equals(CommonConstants.CHARGE_TYPE_ADVANCE)) {
                        nextBillDate = LocalDate.now().plusMonths(plan.getChargeList().get(j).getBillingCycle()).withDayOfMonth(1);
                        if (nextBillDate.isBefore(minNextBillDate)) minNextBillDate = nextBillDate;
                    }
                    if (plan.getChargeList().get(j).getCharge().getChargetype().equals(CommonConstants.CHARGE_TYPE_NONRECURRING)) {
                        nextBillDate = LocalDate.now().plusMonths(plan.getChargeList().get(j).getBillingCycle()).withDayOfMonth(1);
                        if (nextBillDate.isBefore(minNextBillDate)) minNextBillDate = nextBillDate;
                    }
                }
            }

            long usedDaysValidity = Duration.between(LocalDate.now().atStartOfDay(), nextBillDate.atStartOfDay()).toDays();
            Double price = 0d;
            for (PostpaidPlanCharge charge : plan.getChargeList()) {
                LocalDateTime endDate = LocalDateTime.now().plusMonths(charge.getBillingCycle());
                Long planValidityDays = Duration.between(LocalDate.now().atStartOfDay(), endDate).toDays();
                Double rate = charge.getCharge().getTax().getTieredList().get(0).getRate();
                Double dbr = charge.getChargeprice() / planValidityDays;
                double priceWithDbr = dbr * usedDaysValidity;
                Double tax = priceWithDbr * (rate / 100);
                price = price + (priceWithDbr + tax);
            }
            return price;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getCustomerPlanListByAccountNo(String accountNo, Integer mvnoId) {
        List<Long> customerIds = customersRepository.findCustomerIdByAccountNo(accountNo, mvnoId);
        if (customerIds == null || customerIds.isEmpty()) {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Account not found", null);
        }
        if (customerIds.size() > 1) {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(),
                    "Duplicate Account found", null);
        }
        Long custId = customerIds.get(0);
        List<Map<String, Object>> postpaidPlans = customerPackageRepository.findPlansByCustomerId(custId.intValue());
        if (postpaidPlans == null || postpaidPlans.isEmpty()) {
            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "No plans found", null);
        }

        return postpaidPlans.get(postpaidPlans.size() - 1);
    }

    public void checkCustomerPlanChargeChange(PostpaidPlan plan , PostpaidPlanPojo pojo){
        List<PostpaidPlanCharge> previousChargeList = plan.getChargeList();
        List<PostpaidPlanChargePojo> newChargeList = pojo.getChargeList();
        for (PostpaidPlanChargePojo newCharge : newChargeList) {
            for (PostpaidPlanCharge oldCharge : previousChargeList) {
                if (newCharge.getCharge().getId().equals(oldCharge.getCharge().getId())) {
                    // Compare chargePrice or other properties
                    if (newCharge.getChargeprice() == null || oldCharge.getChargeprice() == null
                            || !newCharge.getChargeprice().equals(oldCharge.getChargeprice())) {
                        custChargeService.UpdateCustomerChargeHistoryByUpdatePlan(plan.getPlantype(),oldCharge.getCharge() , newCharge.getChargeprice(), plan.getId());

                        System.out.println("Charge price changed for chargeId: " + newCharge.getChargeId());
                    }
                    break;
                }
            }
        }
    }

    public boolean assignPlanToServiceArea(Long serviceAreaId, List<Integer> planIds) {
        try {
            PostpaidPlan savedPlan = null;
            if (!CollectionUtils.isEmpty(planIds)) {

                List<PostPaidPlanServiceAreaMapping> postPaidPlanServiceAreaMappings = new ArrayList<>();
                for (Integer planId : planIds) {
                    PostpaidPlan plan = postpaidPlanRepo.findById(planId).orElse(null);
                    ServiceArea serviceArea = serviceAreaRepository.findById(serviceAreaId).orElse(null);
                    List<ServiceArea> serviceAreaList = plan.getServiceAreaNameList();
                    serviceAreaList.add(serviceArea);
                    plan.setServiceAreaNameList(serviceAreaList);
                    savedPlan = postpaidPlanRepo.save(plan);
                }

                List<PostPaidPlanServiceAreaMapping> planServiceAreaMappings = postPaidPlanServiceAreaMappingRepository.findAllByServiceId(Math.toIntExact(serviceAreaId));
//                PostpaidPlanPojo pojo = postpaidPlanMapper.domainToDTO(savedPlan, new CycleAvoidingMappingContext());
//                PostpaidPlanMessage message = new PostpaidPlanMessage(pojo);
//                kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));

                SavePlanAssignmentMessage message = new SavePlanAssignmentMessage();
                message.setMappingList(planServiceAreaMappings);
                message.setCreatedById(getLoggedInUserId());
                message.setUpdatedById(getLoggedInUserId());
                message.setAreaId(serviceAreaId);
                message.setStaffSAMap(true);

                Gson gson = GsonConfig.buildGson();
                String json = gson.toJson(message);
                KafkaMessageData kafkaMsg = new KafkaMessageData(gson.fromJson(json, Object.class), SavePlanAssignmentMessage.class.getSimpleName());
                kafkaMessageSender.send(kafkaMsg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ApplicationLogger.logger.error("Error while saving service area mappings for Plan " + ex.getMessage(), ex);
            return false;
        }
        return true;
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


}
