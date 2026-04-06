package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaConstant;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.postpaid.PlangroupMapper;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Branch.repository.BranchRepository;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping.ProductPlanGroupMapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping.ProductPlanGroupMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping.QProductPlanGroupMapping;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategory;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategoryRepository;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBookPlanDetail;
import com.adopt.apigw.modules.PriceGroup.domain.QPriceBookPlanDetail;
import com.adopt.apigw.modules.PriceGroup.repository.PriceBookPlanDtlRepository;
import com.adopt.apigw.modules.ServiceArea.domain.QServiceArea;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.planUpdate.domain.CustomerPackage;
import com.adopt.apigw.modules.planUpdate.domain.QCustomerPackage;
import com.adopt.apigw.modules.planUpdate.repository.CustomerPackageRepository;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.PlanGroupMsg;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.common.WorkflowAuditService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlanGroupService extends AbstractService<PlanGroup, PlanGroupDTO, Integer> {

    @Autowired
    private PriceBookPlanDtlRepository priceBookPlanDtlRepository;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private PlanGroupRepository entityRepository;

    @Autowired
    private ServiceAreaService serviceAreaService;

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private PlanGroupMappingService planGroupMappingService;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomerPackageRepository customerPackageRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    WorkflowAuditService workflowAuditService;

    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    StaffUserService staffUserService;
    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private TatUtils tatUtils;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private ProductPlanGroupMappingRepository productPlanGroupMappingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ServiceAreaPlangroupMappingRepo serviceAreaPlangroupMappingRepo;

    @Autowired
    private PlanGroupMappingRepository planGroupMappingRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;
    private static String MODULE = " [PlanGroupService] ";
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private PlanGroupMappingChargeRelRepo planGroupMappingChargeRelRepo;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private PlanGroupMappingChargeRelRepo chargerelrepo;

    @Autowired
    private  PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private  PlanGroupRepository planGroupRepository;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    PlangroupMapper plangroupMapper;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    CacheService cacheService;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository<PlanGroup, Integer> getRepository() {
        return entityRepository;
    }


    public List<PlanGroup> findAllPlanGroupList(Integer mvnoId, String mode, String planCategory, Integer custId, String accessibility,Integer specialPlanId) {
        try {
            QPlanGroup qPlanGroup = QPlanGroup.planGroup;
            QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
            QPlanGroupMapping qPlanGroupMapping = QPlanGroupMapping.planGroupMapping;

            BooleanExpression exp = qPlanGroup.isNotNull();
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId != 1)
                exp = exp.and(qPlanGroup.mvnoId.in(mvnoId, 1));
            exp = exp.and(qPlanGroup.isDelete.eq(false));
            exp=exp.and(qPlanGroup.status.notEqualsIgnoreCase("Inactive"));
            exp=exp.and(qPlanGroup.status.notEqualsIgnoreCase("Rejected"));

            if( mode.equalsIgnoreCase("SPECIAL")) {
                exp = exp.and(qPlanGroup.planGroupType.notEqualsIgnoreCase("DTV Addon"));
                exp = exp.and(qPlanGroup.planGroupType.notEqualsIgnoreCase("Volume Booster"));
                exp = exp.and(qPlanGroup.planGroupType.notEqualsIgnoreCase("Bandwidth Booster"));
            }

            if (mode != null && !mode.isEmpty()) {
                if (mode.equalsIgnoreCase(Constants.NORMAL))
                    exp = exp.and(qPlanGroup.planMode.eq(Constants.NORMAL)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
                else if (mode.equalsIgnoreCase(Constants.SPECIAL))
                    exp = exp.and(qPlanGroup.planMode.eq(Constants.SPECIAL)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
            }
            if (getLoggedInUserPartnerId() != 1) {
                Partner partner = partnerRepository.findById(getLoggedInUserPartnerId()).get();
               Boolean isAllPlanGroupSelected =  partner.getPriceBookId().getIsAllPlanGroupSelected();
                if (!isAllPlanGroupSelected) {
                    QPriceBookPlanDetail qPriceBookPlanDetail = QPriceBookPlanDetail.priceBookPlanDetail;
                    BooleanExpression expression = qPriceBookPlanDetail.isNotNull().and(qPriceBookPlanDetail.priceBook.eq(partner.getPriceBookId()));
                    List<PriceBookPlanDetail> list = (List<PriceBookPlanDetail>) priceBookPlanDtlRepository.findAll(expression);
                    List<Integer> plangroupIds = new ArrayList<>();
                    for (PriceBookPlanDetail planGroup : list) {
                        if (planGroup.getPlanGroup() != null) {
                            plangroupIds.add(planGroup.getPlanGroup().getPlanGroupId());
                        }
                    }
                    exp = exp.and(qPlanGroup.planGroupId.in(plangroupIds));
                }
            }

            if (planCategory != null && !planCategory.isEmpty()) {
                if (planCategory.equalsIgnoreCase(Constants.NORMAL))
                    exp = exp.and(qPlanGroup.category.eq(Constants.NORMAL)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
                else if (planCategory.equalsIgnoreCase(Constants.BUSINESS_PROMOTION))
                    exp = exp.and(qPlanGroup.category.eq(Constants.BUSINESS_PROMOTION)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));

            }

            if (accessibility != null && !accessibility.isEmpty()) {
                exp = exp.and(qPlanGroup.accessibility.equalsIgnoreCase(accessibility)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
            }


            if (getBUIdsFromCurrentStaff().size() != 0)
                // TODO: pass mvnoID manually 6/5/2025
                exp = exp
                        .and(qPlanGroup.mvnoId.eq(1)
                                .or(qPlanGroup.mvnoId.eq(mvnoId).and(qPlanGroup.buId.in(getBUIdsFromCurrentStaff())))).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
            List<Integer> serviceIds = null;
            if (custId != null) {
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
            }

            exp=exp.and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
            List<PlanGroup> planGroupList = (List<PlanGroup>) entityRepository.findAll(exp);
           //********************************************************************************************
            if (specialPlanId != null) {
                // Fetch service names associated with the special plan
                List<String> specialMappingServiceNames = planGroupMappingRepository.findServiceNamesByPlanGroupId(specialPlanId);
                Set<Long> specialMappingServiceIds = new HashSet<>();

                if (!CollectionUtils.isEmpty(specialMappingServiceNames)) {
                    for (String serviceName : specialMappingServiceNames) {
                        // Fetch ID for the current service name
                        Long serviceId = serviceRepository.findServiceIdsByName(serviceName);
                        if (serviceId != null) {
                            specialMappingServiceIds.add(serviceId);
                        }
                    }
                }

                // Filter plan groups to only include those matching the special mapping service IDs
                planGroupList = planGroupList.stream()
                        .filter(planGroup -> {
                            List<String> planGroupServiceNames = planGroupMappingRepository.findServiceNamesByPlanGroupId(planGroup.getPlanGroupId());
                            Set<Long> planGroupServiceIds = new HashSet<>();

                            for (String planGroupServiceName : planGroupServiceNames) {
                                Long planGroupServiceId = serviceRepository.findServiceIdsByName(planGroupServiceName);
                                if (planGroupServiceId != null) {
                                    planGroupServiceIds.add(planGroupServiceId);
                                }
                            }

                            return planGroupServiceIds.equals(specialMappingServiceIds);
                        })
                        .collect(Collectors.toList());
            }


            //********************************************************************************************

            if (serviceIds != null) {
                List<Integer> finalServiceIds = serviceIds.stream().sorted().collect(Collectors.toList());
                planGroupList.removeIf(planGroup -> planGroup.getPlanMappingList().size() != finalServiceIds.size());
                planGroupList.removeIf(planGroup -> {
                    List<PostpaidPlan> list = planGroup.getPlanMappingList().stream().map(planGroupMapping -> planGroupMapping.getPlan()).collect(Collectors.toList());
                    List<Integer> serviceIdsList = list.stream().map(PostpaidPlan::getServiceId).collect(Collectors.toList());
                    return !UtilsCommon.listEqualsIgnoreOrder(serviceIdsList, finalServiceIds);
                });
                //planGroup.getPlanMappingList().stream().filter(planGroupMapping -> finalServiceIds.contains(planGroupMapping.getPlan().getServiceId()))
            }
            Integer partnerId = getLoggedInUserPartnerId();
            if (partnerId!=null) {
                Partner partner = partnerRepository.findById(partnerId).get();
                if (partner.getPartnerType() != "LCO" && partnerId != 1) {
                    QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
                    BooleanExpression exp1 = qPartnerServiceAreaMapping.isNotNull().and(qPartnerServiceAreaMapping.partnerId.eq(partnerId));
                    List<PartnerServiceAreaMapping> partnerServiceAreaMappings = (List<PartnerServiceAreaMapping>) partnerServiceAreaMappingRepo.findAll(exp1);
                    List<Long> serviceReaIds = partnerServiceAreaMappings.stream()
                            .mapToLong(PartnerServiceAreaMapping::getServiceId)
                            .boxed()
                            .collect(Collectors.toList());
                    List<ServiceArea> serviceAreas = serviceAreaRepository.findAllByIdIn(serviceReaIds);
                    planGroupList = planGroupList.stream()
                            .filter(two -> two.getServicearea().stream().anyMatch(serviceAreas::contains))
                            .collect(Collectors.toList());
                }
            }

            return planGroupList;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }




    public List<PlanGroup> findAllPartnerPlanGroupList(Integer partnerId1,Integer mvnoId, String mode, String planCategory, String accessibility) {
        try {
            List<PlanGroup> planGroupList=new ArrayList<>();
            QPlanGroup qPlanGroup = QPlanGroup.planGroup;
            QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
            QPlanGroupMapping qPlanGroupMapping = QPlanGroupMapping.planGroupMapping;

            BooleanExpression exp = qPlanGroup.isNotNull();
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId != 1)
                exp = exp.and(qPlanGroup.mvnoId.in(mvnoId, 1));
            exp = exp.and(qPlanGroup.isDelete.eq(false));
            exp=exp.and(qPlanGroup.status.notEqualsIgnoreCase("Inactive"));
            exp=exp.and(qPlanGroup.status.notEqualsIgnoreCase("Rejected"));
            if(!mode.equalsIgnoreCase("NORMAL") && !mode.equalsIgnoreCase("SPECIAL")) {
                exp = exp.and(qPlanGroup.planGroupType.notEqualsIgnoreCase("DTV Addon"));
            }
            if (mode != null && !mode.isEmpty()) {
                if (mode.equalsIgnoreCase(Constants.NORMAL))
                    exp = exp.and(qPlanGroup.planMode.eq(Constants.NORMAL)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
                else if (mode.equalsIgnoreCase(Constants.SPECIAL))
                    exp = exp.and(qPlanGroup.planMode.eq(Constants.SPECIAL)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
            }
            if (getLoggedInUserPartnerId() != 1) {
                Partner partner = partnerRepository.findById(getLoggedInUserPartnerId()).get();
                Boolean isAllPlanGroupSelected =  partner.getPriceBookId().getIsAllPlanGroupSelected();
                if (!isAllPlanGroupSelected) {
                    QPriceBookPlanDetail qPriceBookPlanDetail = QPriceBookPlanDetail.priceBookPlanDetail;
                    BooleanExpression expression = qPriceBookPlanDetail.isNotNull().and(qPriceBookPlanDetail.priceBook.eq(partner.getPriceBookId()));
                    List<PriceBookPlanDetail> list = (List<PriceBookPlanDetail>) priceBookPlanDtlRepository.findAll(expression);
                    List<Integer> plangroupIds = new ArrayList<>();
                    for (PriceBookPlanDetail planGroup : list) {
                        if (planGroup.getPlanGroup() != null) {
                            plangroupIds.add(planGroup.getPlanGroup().getPlanGroupId());
                        }
                    }
                    exp = exp.and(qPlanGroup.planGroupId.in(plangroupIds));
                }
            }

            if (planCategory != null && !planCategory.isEmpty()) {
                if (planCategory.equalsIgnoreCase(Constants.NORMAL))
                    exp = exp.and(qPlanGroup.category.eq(Constants.NORMAL)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
                else if (planCategory.equalsIgnoreCase(Constants.BUSINESS_PROMOTION))
                    exp = exp.and(qPlanGroup.category.eq(Constants.BUSINESS_PROMOTION)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));

            }

            if (accessibility != null && !accessibility.isEmpty()) {
                exp = exp.and(qPlanGroup.accessibility.equalsIgnoreCase(accessibility)).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
            }


            if (getBUIdsFromCurrentStaff().size() != 0)
                // TODO: pass mvnoID manually 6/5/2025
                exp = exp
                        .and(qPlanGroup.mvnoId.eq(1)
                                .or(qPlanGroup.mvnoId.eq(mvnoId).and(qPlanGroup.buId.in(getBUIdsFromCurrentStaff())))).and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
            List<Integer> serviceIds = null;

            exp=exp.and(qPlanGroup.status.notEqualsIgnoreCase("NewActivation"));
            planGroupList = (List<PlanGroup>) entityRepository.findAll(exp);
            if (serviceIds != null) {
                List<Integer> finalServiceIds = serviceIds.stream().sorted().collect(Collectors.toList());
                planGroupList.removeIf(planGroup -> planGroup.getPlanMappingList().size() != finalServiceIds.size());
                planGroupList.removeIf(planGroup -> {
                    List<PostpaidPlan> list = planGroup.getPlanMappingList().stream().map(planGroupMapping -> planGroupMapping.getPlan()).collect(Collectors.toList());
                    List<Integer> serviceIdsList = list.stream().map(PostpaidPlan::getServiceId).collect(Collectors.toList());
                    return !UtilsCommon.listEqualsIgnoreOrder(serviceIdsList, finalServiceIds);
                });
                //planGroup.getPlanMappingList().stream().filter(planGroupMapping -> finalServiceIds.contains(planGroupMapping.getPlan().getServiceId()))
            }
            Integer partnerId = getLoggedInUserPartnerId();
            if (partnerId!=null) {
                Partner partner = partnerRepository.findById(partnerId).get();
                if (partner.getPartnerType() != "LCO" && partnerId != 1) {
                    QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
                    BooleanExpression exp1 = qPartnerServiceAreaMapping.isNotNull().and(qPartnerServiceAreaMapping.partnerId.eq(partnerId));
                    List<PartnerServiceAreaMapping> partnerServiceAreaMappings = (List<PartnerServiceAreaMapping>) partnerServiceAreaMappingRepo.findAll(exp1);
                    List<Long> serviceReaIds = partnerServiceAreaMappings.stream()
                            .mapToLong(PartnerServiceAreaMapping::getServiceId)
                            .boxed()
                            .collect(Collectors.toList());
//                    List<ServiceArea> serviceAreas = serviceAreaRepository.findAllByIdIn(serviceReaIds);
//                    planGroupList = planGroupList.stream()
//                            .filter(two -> two.getServicearea().stream().anyMatch(serviceAreas::contains))
//                            .collect(Collectors.toList());
                }
            }


            Partner partner=partnerRepository.findById(partnerId1).orElse(null);
            PriceBook priceBook=partner.getPriceBookId();
            if(!priceBook.getIsAllPlanGroupSelected())
            {
                List<PriceBookPlanDetail> bookPlanDetails=priceBook.getPriceBookPlanDetailList();
                bookPlanDetails=bookPlanDetails.stream().filter(x->x.getPlanGroup()!=null).collect(Collectors.toList());
                if(bookPlanDetails!=null && !bookPlanDetails.isEmpty())
                {
                    List<Integer> planGrouplist=bookPlanDetails.stream().map(y->y.getPlanGroup().getPlanGroupId()).collect(Collectors.toList());
                    if(!planGrouplist.isEmpty())
                        planGroupList=planGroupList.stream().filter(x->planGrouplist.contains(x.getPlanGroupId())).collect(Collectors.toList());
                }
                else planGroupList.clear();
            }

            if(priceBook.getIsAllPlanGroupSelected() && partner.getParentPartner()!=null && !partner.getParentPartner().getPriceBookId().getIsAllPlanGroupSelected() && partner.getParentPartner().getParentPartner()==null)
            {
                List<PriceBookPlanDetail> bookPlanDetails=partner.getParentPartner().getPriceBookId().getPriceBookPlanDetailList();
                bookPlanDetails=bookPlanDetails.stream().filter(x->x.getPostpaidPlan()!=null).collect(Collectors.toList());
                if(bookPlanDetails!=null && !bookPlanDetails.isEmpty())
                {
                    List<Integer> planGroupList1=bookPlanDetails.stream().map(y->y.getPlanGroup().getPlanGroupId()).collect(Collectors.toList());
                    if(!planGroupList1.isEmpty())
                        planGroupList=planGroupList.stream().filter(x->planGroupList1.contains(x.getPlanGroupId())).collect(Collectors.toList());
                }
                else planGroupList.clear();
            }

            if(priceBook.getIsAllPlanGroupSelected() && partner.getParentPartner()!=null && partner.getParentPartner().getPriceBookId().getIsAllPlanGroupSelected() && partner.getParentPartner().getParentPartner()!=null && !partner.getParentPartner().getParentPartner().getPriceBookId().getIsAllPlanGroupSelected())
            {
                List<PriceBookPlanDetail> bookPlanDetails=partner.getParentPartner().getParentPartner().getPriceBookId().getPriceBookPlanDetailList();
                bookPlanDetails=bookPlanDetails.stream().filter(x->x.getPostpaidPlan()!=null).collect(Collectors.toList());
                if(bookPlanDetails!=null && !bookPlanDetails.isEmpty())
                {
                    List<Integer> planlist=bookPlanDetails.stream().map(y->y.getPlanGroup().getPlanGroupId()).collect(Collectors.toList());
                    if(!planlist.isEmpty())
                        planGroupList=planGroupList.stream().filter(x->planlist.contains(x.getPlanGroupId())).collect(Collectors.toList());
                }
                else planGroupList.clear();
            }
            return planGroupList;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Page<PlanGroup> getPlanGroupList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Integer mvnoId) {

        pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
        QPlanGroup qPlanGroup=QPlanGroup.planGroup;
        BooleanExpression booleanExpression = qPlanGroup.isNotNull().and(qPlanGroup.isDelete.eq(false));
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPlanGroup.mvnoId.in(Arrays.asList(mvnoId, 1)));
        }
        if (!getBUIdsFromCurrentStaff().isEmpty()) {
            booleanExpression = booleanExpression.and(qPlanGroup.buId.in(getBUIdsFromCurrentStaff()));
        }
        List<Long> serviceAreaIds = getServiceAreaIdList();
        if (!getServiceAreaIdList().isEmpty()) {
            List<Integer>plangroupIds=serviceAreaPlangroupMappingRepo.findPlanGroupIdsByServiceAreaIds(serviceAreaIds);
//            List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappings = serviceAreaPlangroupMappingRepo.findAllByServiceArea_IdIn(getServiceAreaIdList());
//            if (!serviceAreaPlanGroupMappings.isEmpty()) {
//                List<Integer> planGroupId = serviceAreaPlanGroupMappings.stream()
//                        .map(ServiceAreaPlanGroupMapping::getPlanGroup)
//                        .map(PlanGroup::getPlanGroupId)
//                        .collect(Collectors.toList());
//                booleanExpression = booleanExpression.and(qPlanGroup.planGroupId.in(planGroupId));
//            }
        }
        return planGroupRepository.findAll(booleanExpression, pageRequest);
        }



    public BooleanExpression getPlanGroupList() {
        QPlanGroup qPlanGroup = QPlanGroup.planGroup;
        BooleanExpression booleanExpression = qPlanGroup.isNotNull().and(qPlanGroup.isDelete.eq(false));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPlanGroup.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
        if(!getBUIdsFromCurrentStaff().isEmpty() && getBUIdsFromCurrentStaff()!=null){
            booleanExpression=booleanExpression.and(qPlanGroup.buId.in(getBUIdsFromCurrentStaff()));
        }
        List<Long> serviceAreaIds = getServiceAreaIdList();
        QServiceArea qServiceArea=QServiceArea.serviceArea;
        BooleanExpression booleanExpression1=qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false)).and(qServiceArea.id.in(serviceAreaIds));
        List<ServiceArea>serviceAreaList=IterableUtils.toList(serviceAreaRepository.findAll(booleanExpression1));
        if(!getServiceAreaIdList().isEmpty()){
            booleanExpression=booleanExpression.and(qPlanGroup.servicearea.any().in(serviceAreaList))    ;
        }

        return booleanExpression;
    }

    public PlanGroup findPlanGroupById(Integer planGroupId, Integer mvnoId) {
        try {
            QPlanGroup qPlanGroup = QPlanGroup.planGroup;
            BooleanExpression boolExp = qPlanGroup.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qPlanGroup.mvnoId.in(mvnoId, 1));
            boolExp = boolExp.and(qPlanGroup.planGroupId.eq(planGroupId));
            if (getBUIdsFromCurrentStaff().size() != 0)
                // TODO: pass mvnoID manually 6/5/2025
                boolExp = boolExp
                        .and(qPlanGroup.mvnoId.eq(1)
                                .or(qPlanGroup.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qPlanGroup.buId.in(getBUIdsFromCurrentStaff()))));
            Optional<PlanGroup> planGroup = entityRepository.findOne(boolExp);

           if(planGroup.isPresent()) {
               List<PlanGroupMapping> planGroupMappingList = new ArrayList<>();
               planGroupMappingList = planGroup.get().getPlanMappingList().stream().filter(planGroupMapping -> !planGroupMapping.getIsDelete()).collect(Collectors.toList());
               planGroup.get().setPlanMappingList(planGroupMappingList);
               List<ProductPlanGroupMapping> productPlanGroupMappingList = planGroup.get().getProductPlanGroupMappingList();
               if (productPlanGroupMappingList != null) {
                   productPlanGroupMappingList.stream().forEach(r -> {
                       if (r.getProductId() != null) {
                           Product product = productRepository.findById(r.getProductId()).orElse(null);
                           r.setProductName(product.getName());
                       }
                       if (r.getProductCategoryId() != null) {
                           ProductCategory productCategory = productCategoryRepository.findById(r.getProductCategoryId()).get();
                           r.setProductCategoryName(productCategory.getName());
                       }
                       if (r.getPlanId() != null) {
                           PostpaidPlan postpaidPlan = new PostpaidPlan(postpaidPlanRepo.findById(Math.toIntExact(r.getPlanId())).get());
                           r.setPlanName(postpaidPlan.getName());
                       }
                   });
               }
               return planGroup.get();
           }


//            if (!planGroup.isPresent()) {
//                throw new IllegalArgumentException("No record found for Plan Group with id : '" + planGroupId
//                        + "'. Or you are not authorised to update/delete this record.");
//            }
            return null;
        } catch (RuntimeException e) {
                throw new RuntimeException(e.getMessage());
        }
    }

    public List<PlanGroup> findPlanGroupByName(String name, Integer mvnoId) {
        try {
            QPlanGroup qPlanGroup = QPlanGroup.planGroup;
            BooleanExpression boolExp = qPlanGroup.isNotNull();
            if (StringUtils.isBlank(name) || name.equalsIgnoreCase("null")) {
                return (List<PlanGroup>) entityRepository.findAll(boolExp);
            }
            boolExp = boolExp.and(qPlanGroup.planGroupName.like("%" + name + "%"));
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qPlanGroup.mvnoId.in(mvnoId, 1));
            if (getBUIdsFromCurrentStaff().size() != 0)
                // TODO: pass mvnoID manually 6/5/2025
                boolExp = boolExp
                        .and(qPlanGroup.mvnoId.eq(1)
                                .or(qPlanGroup.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qPlanGroup.buId.in(getBUIdsFromCurrentStaff()))));
            List<PlanGroup> planGroupList = (List<PlanGroup>) entityRepository.findAll(boolExp);
            return planGroupList;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Page<PlanGroup> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = MODULE + " [search()] ";
        QPlanGroup qPlanGroup = QPlanGroup.planGroup;
        BooleanExpression booleanExpression = qPlanGroup.isNotNull().and(qPlanGroup.isDelete.eq(false));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null)!=1){
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression.and(qPlanGroup.mvnoId.eq(getMvnoIdFromCurrentStaff(null)));
            if (staffUserService.getBUIdsFromCurrentStaff().size() != 0) {
                booleanExpression = booleanExpression.and(qPlanGroup.buId.in(staffUserService.getBUIdsFromCurrentStaff()));
            }
        }
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                   if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLAN_STATUS)) {
                        if (searchModel.getFilterValue().trim().contains(SearchConstants.PLAN_STATUS_ACTIVE)) {
                            if (!searchModel.getFilterValue().isEmpty()) {
                                booleanExpression = booleanExpression.and(qPlanGroup.status.eq("Active"));
                            }
                        }
                        if (searchModel.getFilterValue().trim().contains(SearchConstants.PLAN_STATUS_EXPIRED)) {
                            if (!searchModel.getFilterValue().isEmpty()) {
                                booleanExpression = booleanExpression.and(qPlanGroup.status.eq("Expired"));
                            }
                        }
                        if (searchModel.getFilterValue().trim().contains(SearchConstants.PLAN_STATUS_REJECTED)) {
                            if (!searchModel.getFilterValue().isEmpty()) {
                                booleanExpression = booleanExpression.and(qPlanGroup.status.eq("Rejected"));
                            }
                        }
                        if (searchModel.getFilterValue().trim().contains(SearchConstants.PLAN_STATUS_NEWACTIVATION)) {
                            if (!searchModel.getFilterValue().isEmpty()) {
                                booleanExpression = booleanExpression.and(qPlanGroup.status.eq("NewActivation"));
                            }
                        }
                        if (searchModel.getFilterValue().trim().contains(SearchConstants.PLAN_STATUS_INACTIVE)) {
                            if (!searchModel.getFilterValue().isEmpty()) {
                                booleanExpression = booleanExpression.and(qPlanGroup.status.eq("INACTIVE"));
                            }
                        }
                    } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLAN_CREATEDBY)) {
                        String s1 = searchModel.getFilterValue();
                        booleanExpression = booleanExpression.and(qPlanGroup.createdByName.containsIgnoreCase("%" + s1 + "%").or(qPlanGroup.createdByName.containsIgnoreCase(searchModel.getFilterValue())));
                    } else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLANCREATEDATE)) {
                        if (!searchModel.getFilterValue().isEmpty()) {
                            JSONObject filterValue = new JSONObject(searchModel.getFilterValue());
                            String fromDate = filterValue.getString("from") + "T00:00:00";
                            String toDate = filterValue.getString("to") + "T23:59:59";

                            LocalDateTime fromDateTime = LocalDateTime.parse(fromDate);
                            LocalDateTime toDateTime = LocalDateTime.parse(toDate);

                            booleanExpression = booleanExpression
                                    .and(qPlanGroup.createdate.goe(fromDateTime))
                                    .and(qPlanGroup.createdate.loe(toDateTime));
                        }
                    }
                    else if (searchModel.getFilterDataType().trim().equalsIgnoreCase(SearchConstants.POSTPAID)) {
                        String s1 = searchModel.getFilterValue();
                        if(searchModel.getFilterValue()!=null) {
                            booleanExpression = booleanExpression.and(qPlanGroup.planGroupName.likeIgnoreCase("%" + s1 + "%").and(qPlanGroup.planGroupName.containsIgnoreCase(searchModel.getFilterValue()))).and(qPlanGroup.plantype.eq("Postpaid"));
                        }
                        if(searchModel.getFilterValue()=="") {
                            booleanExpression = booleanExpression.and(qPlanGroup.plantype.eq("Postpaid"));
                        }
                    }
                    else if (searchModel.getFilterDataType().trim().equalsIgnoreCase(SearchConstants.PREPAID)) {
                        String s1 = searchModel.getFilterValue();
                        if (searchModel.getFilterValue() != null) {
                            booleanExpression = booleanExpression.and(qPlanGroup.planGroupName.likeIgnoreCase("%" + s1 + "%").and(qPlanGroup.planGroupName.containsIgnoreCase(searchModel.getFilterValue()))).and(qPlanGroup.plantype.eq("Prepaid"));
                        }
                        if(searchModel.getFilterValue()=="") {
                            booleanExpression = booleanExpression.and(qPlanGroup.plantype.eq("Prepaid"));
                        }
                    }
                    else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        String s1 = searchModel.getFilterValue();
                        booleanExpression = booleanExpression.and(qPlanGroup.planGroupName.likeIgnoreCase("%" + s1 + "%").and(qPlanGroup.planGroupName.containsIgnoreCase(searchModel.getFilterValue())));
                    }
                    else if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.PLAN_PRICE)) {
                        String s1 = searchModel.getFilterValue();
                        booleanExpression =  booleanExpression.and(
                                qPlanGroup.offerprice.like("%" + s1 + "%"));
                    }
                    else
                        booleanExpression = this.getPlanGroupList();
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return planGroupRepository.findAll(booleanExpression, pageRequest);
    }

    public Page<PlanGroup> getPlanGroupByNameAndPlanType(String s1, PageRequest pageRequest) {
// TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return entityRepository.findByPanGroupNameOrPlanTypeContainingIgnoreCaseAndIsDeleteIsFalse(s1, s1, pageRequest);
        if (getBUIdsFromCurrentStaff().size() == 0)
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.findByPanGroupNameOrPlanTypeContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(s1, s1, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
        else
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.findByPanGroupNameOrPlanTypeContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(s1, s1, pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
    }

    public PlanGroup savePlanGroup(PlanGroupDTO planGroupDTO, Integer mvnoId) throws Exception {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            validatePlanGroup(planGroupDTO);
            PlanGroup planGroup = new PlanGroup();
            if (getBUIdsFromCurrentStaff().size() == 1)
                planGroup.setBuId(getBUIdsFromCurrentStaff().get(0));
            planGroup.setMvnoId(mvnoId);
            planGroup.setPlantype(planGroupDTO.getPlanType());
            planGroup.setPlanMode(planGroupDTO.getPlanMode());
            planGroup.setCategory(planGroupDTO.getCategory());
            planGroup.setPlanGroupName(planGroupDTO.getPlanGroupName());
            planGroup.setStatus(planGroupDTO.getStatus());
            planGroup.setCreatedate(LocalDateTime.now());
            planGroup.setCreatedByName(getLoggedInUser().getFirstName() + " " + getLoggedInUser().getLastName());
            planGroup.setCreatedById(getLoggedInUserId());
            planGroup.setLastModifiedById(getLoggedInUserId());
            planGroup.setLastModifiedByName(getLoggedInUser().getFirstName() + getLoggedInUser().getLastName());
            planGroup.setUpdatedate(LocalDateTime.now());
            planGroup.setAllowDiscount(planGroupDTO.getAllowdiscount());
            planGroup.setInvoiceToOrg(planGroupDTO.getInvoiceToOrg());
            planGroup.setRequiredApproval(planGroupDTO.getRequiredApproval());
            // TODO: pass mvnoID manually 6/5/2025
            planGroup.setMvnoName(mvnoRepository.findMvnoNameById(mvnoId.longValue()));
//            planGroup.setProductPlanGroupMappingList(planGroupDTO.getProductPlanGroupMappingList());
            if (planGroupDTO.getServiceAreaId()!=null){
                getServiceareamappingId(planGroupDTO,planGroup);
            }
            if(planGroupDTO.getOfferprice() != null){
                planGroup.setOfferprice(planGroupDTO.getOfferprice());
            }
//            ServiceArea serviceArea = serviceAreaService.getByID(planGroupDTO.getServiceAreaId());
//            planGroup.setServicearea(serviceArea);
            planGroup.setPlanGroupType(planGroupDTO.getPlanGroupType());
            planGroup.setAllowDiscount(planGroupDTO.getAllowdiscount());
            if (planGroupDTO.getPlanMappingList() != null) {
                planGroup.setDbr(0.0);
                for (PlanGroupMappingDTO planGroupMappingDTO : planGroupDTO.getPlanMappingList()) {
                    planGroupMappingDTO.setMvnoId(mvnoId);
                    // TODO: pass mvnoID manually 6/5/2025
                    planGroupMappingDTO.setMvnoName(mvnoRepository.findMvnoNameById(mvnoId.longValue()));
                    planGroupMappingDTO.setValidity(planGroupMappingDTO.getValidity());
                    if(planGroupMappingDTO.getPostpaidPlanPojo()!=null){
                        PostpaidPlanPojo postpaidPlanPojo = postpaidPlanService.save(planGroupMappingDTO.getPostpaidPlanPojo());
                        planGroupMappingDTO.setPlanId(postpaidPlanPojo.getId());
                    }
                    PostpaidPlan postpaidPlan = postpaidPlanService.findById(planGroupMappingDTO.getPlanId());
                    if (postpaidPlan == null) {
                        throw new IllegalArgumentException(
                                "No record found for Plan with Plan id : '" + planGroupMappingDTO.getPlanId());
                    }
                    planGroupMappingDTO.setPlanId(postpaidPlan.getId());
                    Double offerPrice = postpaidPlan.getOfferprice();
                    Double newOfferPrice = postpaidPlan.getNewOfferPrice();
                    if (newOfferPrice != null && postpaidPlan.getCategory().equalsIgnoreCase(Constants.BUSINESS_PROMOTION)) {
                        offerPrice = newOfferPrice;
                    }
                    Double validity = postpaidPlan.getValidity();
                    if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_MONTHS))
                        validity = 30 * validity;
                    if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_YEARS))
                        validity = 365 * validity;
                    planGroup.setDbr(Double.parseDouble(df.format(planGroup.getDbr() + (offerPrice / validity))));
                }
            }
            PlanGroup planGroup1 = entityRepository.save(planGroup);
            if (planGroup1.getNextTeamHierarchyMappingId() == null) {

                if (planGroup1.getStatus() != null && !"".equals(planGroup1.getStatus())) {
                    StaffUser assignedUser;
                    if (planGroup1.getStatus().equalsIgnoreCase("NewActivation")) {
                        if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,mvnoId).equals("TRUE")) {
                            Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(planGroup1.getMvnoId(), planGroup1.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, CommonConstants.HIERARCHY_TYPE, false, true, planGroup1);
                            int staffId = 0;
                            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                staffId = Integer.parseInt(map.get("staffId"));
                                StaffUser assignedStaff = staffUserService.get(staffId,mvnoId);
                                assignedUser = assignedStaff;
                                planGroup1.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                planGroup1.setNextStaff(staffId);
                                if (planGroup1 != null) {
//                                    assignPlanGroupDocStaff(planGroup1, staffId);
                                    String action = CommonConstants.WORKFLOW_MSG_ACTION.PLAN_GROUP+" with name : "+" ' "+planGroupDTO.getPlanGroupName()+" '";
                                    hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(),assignedStaff.getPhone(),assignedStaff.getEmail(),assignedStaff.getMvnoId(),assignedStaff.getFullName(),action,assignedStaff.getId().longValue());

                                }

                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup1.getPlanGroupId(), planGroup1.getPlanGroupName(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                                    if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                                        map.put("tat_id", map.get("current_tat_id"));
                                    tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, planGroup1.getPlanGroupId(), null);
                                }
                            } else {
                                StaffUser currentStaff = staffUserService.get(getLoggedInUserId(),mvnoId);
                                assignedUser = currentStaff;
                                planGroup1.setNextTeamHierarchyMappingId(null);
                                planGroup1.setNextStaff(currentStaff.getId());
//                                if (currentStaff != null) {
//                                    assignPlanGroupDocStaff(planGroup1, currentStaff.getId());
//                                }
                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup1.getPlanGroupId(), planGroup1.getPlanGroupName(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                            }
                        } else {
                            StaffUser currentStaff = staffUserService.get(getLoggedInUserId(),mvnoId);
                            planGroup1.setNextTeamHierarchyMappingId(null);
                            planGroup1.setNextStaff(currentStaff.getId());
//                            if (currentStaff != null) {
//                                assignPlanGroupDocStaff(planGroup1, currentStaff.getId());
//                            }

                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup1.getPlanGroupId(), planGroup1.getPlanGroupName(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                        }
                        //TAT functionality


                    }
                }
            }

            PlanGroupMapping planGroupMapping;
            List<PlanGroupMapping> planGroupMappingList = new ArrayList<PlanGroupMapping>();
            if (planGroupDTO.getPlanMappingList() != null) {
                for (PlanGroupMappingDTO planGroupMappingDTO : planGroupDTO.getPlanMappingList()) {

                    planGroupMappingDTO.setPlanGroupId(planGroup1.getPlanGroupId());
                    planGroupMapping = planGroupMappingService.savePlanGroupMapping(planGroupMappingDTO, mvnoId);
                    planGroupMappingList.add(planGroupMapping);

                    for (PlanGroupMappingChargeRelDto chargeDto : planGroupMappingDTO.getChargeList()){
                            PlanGroupMappingChargeRel planGroupMappingChargeRel = new PlanGroupMappingChargeRel();
                            planGroupMappingChargeRel.setPlanGroupMapping(planGroupMapping);
                            planGroupMappingChargeRel.setPrice(chargeDto.getChargeprice());
//                            PostpaidPlanCharge postpaidPlanCharge = postpaidPlanChargeRepo.findById(chargeDto.getId()).get();
//                            Charge charge= chargeRepository.findById(postpaidPlanCharge.getCharge().getId()).get();
                            Charge charge= chargeRepository.findById(chargeDto.getId()).get();
                            planGroupMappingChargeRel.setCharge(charge);
                            if (chargeDto.getChargeName()!=null) {
                                planGroupMappingChargeRel.setChargeName(chargeDto.getChargeName());
                            }else {
                                planGroupMappingChargeRel.setChargeName(charge.getName());
                            }
                            planGroupMappingChargeRel.setPlanId(planGroupMappingDTO.getPlanId());
                            planGroupMappingChargeRelRepo.save(planGroupMappingChargeRel);
                    }
                }
            }
            planGroup1.setPlanMappingList(planGroupMappingList);
            planGroup1.setProductPlanGroupMappingList(planGroupDTO.getProductPlanGroupMappingList());

            //Share plangroup data to CRM
            planGroupSharedToCRM(planGroup1, true);

            return planGroup1;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void validatePlanGroup(PlanGroupDTO planGroupDTO) {
        if (planGroupDTO.getPlanGroupName() == null || planGroupDTO.getPlanGroupName().equalsIgnoreCase("string") || planGroupDTO.getPlanGroupName().isEmpty()) {
            throw new IllegalArgumentException(
                    "Plan Group Name is mandatory. Please enter valid Plan Group Name.");
        } else if (planGroupDTO.getPlanType() == null || planGroupDTO.getPlanType().equalsIgnoreCase("string") || planGroupDTO.getPlanType().isEmpty()) {
            throw new IllegalArgumentException(
                    "Plan Type is mandatory. Please enter valid Plan Type.");
        } else if (CollectionUtils.isEmpty(planGroupDTO.getServiceAreaId())) {
            throw new IllegalArgumentException(
                    "Service Area is mandatory. Please enter valid Service Area.");
        } else if (planGroupDTO.getPlanMode() == null || planGroupDTO.getPlanMode().equalsIgnoreCase("string") || planGroupDTO.getPlanMode().isEmpty()) {
            throw new IllegalArgumentException(
                    "Plan Mode is mandatory. Please enter valid Plan Mode.");
        }
    }

    @Transactional
    public PlanGroup updatePlanGroup(PlanGroupDTO planGroupDTO, Integer mvnoId) {
        try {
            DecimalFormat df = new DecimalFormat("0.00");
            // TODO: pass mvnoID manually 6/5/2025
            planGroupDTO.setMvnoId(mvnoId);
            PlanGroup planGroup = getEntityForUpdateAndDelete(planGroupDTO.getPlanGroupId(),mvnoId);

            planGroup.setAllowDiscount(planGroupDTO.getAllowdiscount());
            planGroup.setPlanGroupName(planGroupDTO.getPlanGroupName());
            planGroup.setPlantype(planGroupDTO.getPlanType());
            planGroup.setStatus(planGroupDTO.getStatus());
            planGroup.setPlanMode(planGroupDTO.getPlanMode());
            planGroup.setPlanGroupType(planGroupDTO.getPlanGroupType());
            // TODO: pass mvnoID manually 6/5/2025
            planGroup.setMvnoName(mvnoRepository.findMvnoNameById(mvnoId.longValue()));
            if(planGroupDTO.getOfferprice() != null && planGroupDTO.getOfferprice()>0.0){
                planGroup.setOfferprice(planGroupDTO.getOfferprice());
            }
            if (planGroupDTO.getServiceAreaId()==null){
                throw new IllegalArgumentException(
                        "No record found for  Service Area with  Service Area id : '" + planGroupDTO.getPlanGroupId());
            }
            getServiceareamappingId(planGroupDTO,planGroup);
//            ServiceArea serviceArea = serviceAreaService.getByID(planGroupDTO.getServiceAreaId());
//            if (serviceArea == null) {
//                throw new IllegalArgumentException(
//                        "No record found for  Service Area with  Service Area id : '" + planGroupDTO.getPlanGroupId());
//            }
//            planGroup.setServicearea(serviceArea);
//            List<ProductPlanGroupMapping> productPlanGroupMappingList= updateProductPlanGroupMapping(planGroupDTO.getProductPlanGroupMappingList(), planGroup.getPlanGroupId(), mvnoId);
            List<PlanGroupMapping> planGroupMappingList = planGroupMappingService.updatePlanGroupMapping(planGroupDTO.getPlanMappingList(), planGroupDTO.getPlanGroupId(), mvnoId);
            planGroup.setDbr(0.0);
            planGroupMappingList.stream().forEach(x -> {
                PostpaidPlan postpaidPlan = postpaidPlanService.findById(x.getPlan().getId());
                Double offerPrice = postpaidPlan.getOfferprice();
                Double validity = postpaidPlan.getValidity();
                if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_MONTHS))
                    validity = 30 * validity;
                if (postpaidPlan.getUnitsOfValidity().equalsIgnoreCase(CommonConstants.VALIDIDY_UNIT_YEARS))
                    validity = 365 * validity;
                planGroup.setDbr(Double.parseDouble(df.format(planGroup.getDbr() + (offerPrice / validity))));

            });
            planGroup.setMvnoId(mvnoId);
            planGroup.setPlanMappingList(planGroupMappingList);
            planGroup.setLastModifiedById(getLoggedInUserId());
            planGroup.setLastModifiedByName(getLoggedInUser().getFirstName() + getLoggedInUser().getLastName());
            planGroup.setUpdatedate(LocalDateTime.now());
//            CustomerCafAssignment customerCafAssignment = customerCafAssignmentRepository.findByPlanGroup(planGroup);
//            if (customerCafAssignment.getPlanGroup() != null) {
//                customerCafAssignmentRepository.deleteAllByPlanGroup(planGroup);
//            }
            if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,mvnoId).equals("TRUE")) {
                Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(planGroup.getMvnoId(), planGroup.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, CommonConstants.HIERARCHY_TYPE, false, true, planGroup);
                int staffId = 0;
                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                    staffId = Integer.parseInt(map.get("staffId"));
                    StaffUser assignedStaff = staffUserService.get(staffId,mvnoId);

                    planGroup.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                    planGroup.setNextStaff(staffId);
                    if (planGroup != null) {
//                        assignPlanGroupDocStaff(planGroup, staffId);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.PLAN_GROUP + " with name : " + " ' " + planGroupDTO.getPlanGroupName() + " '";
                        hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(), action,assignedStaff.getId().longValue());

                    }

                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup.getPlanGroupId(), planGroup.getPlanGroupName(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                    if (assignedStaff.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                        if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                            map.put("tat_id", map.get("current_tat_id"));
                        tatUtils.saveOrUpdateDataForTatMatrix(map, assignedStaff, planGroup.getPlanGroupId(), null);
                    }
                } else {
                    StaffUser currentStaff = staffUserService.get(getLoggedInUserId(),mvnoId);
                    planGroup.setNextTeamHierarchyMappingId(null);
                    planGroup.setNextStaff(currentStaff.getId());
//                    if (currentStaff != null) {
//                        assignPlanGroupDocStaff(planGroup, currentStaff.getId());
//                    }
                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup.getPlanGroupId(), planGroup.getPlanGroupName(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                }
            } else {
                StaffUser currentStaff = staffUserService.get(getLoggedInUserId(),mvnoId);
                planGroup.setNextTeamHierarchyMappingId(null);
                planGroup.setNextStaff(currentStaff.getId());
//                if (currentStaff != null) {
//                    assignPlanGroupDocStaff(planGroup, currentStaff.getId());
//                }

                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup.getPlanGroupId(), planGroup.getPlanGroupName(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
            }

           if(planGroupDTO.getPlanMappingList()==null || planGroupDTO.getPlanMappingList().isEmpty() ){
                throw new RuntimeException("One plan mapping is mandatary");
            }

            //TAT functionality
            if(!planGroupDTO.getStatus().equals("Inactive")) {
                planGroup.setStatus("NewActivation");
            }
            PlanGroup updatedPlanGroup = entityRepository.save(planGroup);
            planGroupSharedToCRM(planGroup,false);
            return updatedPlanGroup;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deletePlanGroupById(Integer planGroupId, Integer mvnoId) {

        try {

            PlanGroup planGroup = getEntityForUpdateAndDelete(planGroupId, mvnoId);

            if (customersRepository.countByPlanGroupId(planGroupId) == 0) {
                planGroupMappingService.deleteByPlanGroupId(planGroupId, mvnoId);
                planGroup.setIsDelete(true);
                entityRepository.save(planGroup);
            } else {
                throw new RuntimeException(
                        "This operation will not allow as this Plan Group is used for Customer creation.");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Optional<PlanGroup> validatePlanGroupById(Integer id, Integer mvnoId) {

        try {
            QPlanGroup qPlanGroup = QPlanGroup.planGroup;
            BooleanExpression boolExp = qPlanGroup.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qPlanGroup.mvnoId.in(mvnoId, 1));
            boolExp = boolExp.and(qPlanGroup.planGroupId.eq(id));
            Optional<PlanGroup> planGroup = entityRepository.findOne(boolExp);
            return planGroup;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
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

    public boolean isSameStaff(String name, Integer mvnoId) throws Exception {
        boolean flag = true;
        Integer userId = getLoggedInUserId();
        // TODO: pass mvnoID manually 6/5/2025
//        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
        if (name != null) {
            name = name.trim();
            Integer createdById;
            if (getBUIdsFromCurrentStaff().size() == 0)
                createdById = entityRepository.getCreatedBy(name, mvnoId);
            else
                createdById = entityRepository.getCreatedBy(name, mvnoId, getBUIdsFromCurrentStaff());
            if (createdById != userId) {
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else if (getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    countEdit = entityRepository.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

  /*  @Override
    public PlanGroup get(Integer id) {
        PlanGroup planGroup = entityRepository.findById(id).orElse(null);
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId == 1 || ((planGroup.getMvnoId().intValue() == mvnoId.intValue() || planGroup.getMvnoId() == 1) && (planGroup.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(planGroup.getBuId()))))
            return planGroup;
        return null;
    }*/



    @Override
    public PlanGroup get(Integer id,Integer currentMvnoId) {
        String cacheKey = cacheKeys.PLANGROUP + id;  // Make sure this key is defined properly
        PlanGroup planGroup = null;

        try {
            // Try to get from cache
            planGroup = (PlanGroup) cacheService.getFromCache(cacheKey, PlanGroup.class);

            if (planGroup != null) {
                log.info("PlanGroup from cache :::::::::::::::: ID: " + planGroup.getPlanGroupId() + " Name: " + planGroup.getPlanGroupName());

//                Integer currentMvnoId = getMvnoIdFromCurrentStaff();
                if (currentMvnoId == 1 ||
                        ((planGroup.getMvnoId().intValue() == currentMvnoId || planGroup.getMvnoId() == 1) &&
                                (planGroup.getMvnoId() == 1 || getBUIdsFromCurrentStaff().isEmpty() || getBUIdsFromCurrentStaff().contains(planGroup.getBuId())))) {
                    return planGroup;
                }

                return null;
            }

            // Fetch from DB if not in cache
            planGroup = entityRepository.findById(id).orElse(null);

            if (planGroup != null) {
//                Integer currentMvnoId = getMvnoIdFromCurrentStaff();

                if (currentMvnoId == 1 ||
                        ((planGroup.getMvnoId().intValue() == currentMvnoId || planGroup.getMvnoId() == 1) &&
                                (planGroup.getMvnoId() == 1 || getBUIdsFromCurrentStaff().isEmpty() || getBUIdsFromCurrentStaff().contains(planGroup.getBuId())))) {

                    log.info("PlanGroup from DB :::::::::::::::: ID: " + planGroup.getPlanGroupId() + " Name: " + planGroup.getPlanGroupName());
                    cacheService.putInCacheWithExpire(cacheKey, planGroup);  // Optional: pass TTL if needed
                    return planGroup;
                }
            }
        } catch (Exception e) {
            log.error("Error while fetching PlanGroup: ", e);
        }

        return null;
    }


    public PlanGroup getEntityForUpdateAndDelete(Integer id, Integer mvnoId) {
        PlanGroup planGroup = planGroupRepository.findById(id).get();
        // TODO: pass mvnoID manually 6/5/2025
        if (planGroup == null || (!(mvnoId == 1 || mvnoId == planGroup.getMvnoId().intValue()) && (planGroup.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(planGroup.getBuId()))))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return planGroup;
    }

    public List<PostpaidPlan> findAllPlansByPlanGroups(Integer mvnoId, Integer planGroupId) {
        List<PostpaidPlan> planList = new ArrayList<PostpaidPlan>();
        List<PlanGroupMapping> planGroupMappingList = planGroupMappingService.findPlanGroupMappingByPlanGroupId(planGroupId, mvnoId);
        if (planGroupMappingList != null) {
            for (PlanGroupMapping planGroupMapping : planGroupMappingList) {
                PostpaidPlan plan = planGroupMapping.getPlan();
                planList.add(plan);
            }
        }
        return planList.stream().filter(postpaidPlan -> postpaidPlan.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(postpaidPlan.getBuId())).collect(Collectors.toList());
    }

    public List<PlanGroup> getPlanGroupsForChildCustomers(Integer parentCustId) {

        QCustomerPackage qcustomerPackage = QCustomerPackage.customerPackage;
        BooleanExpression booleanExpression = qcustomerPackage.isNotNull().and(qcustomerPackage.isDelete.eq(false))
                .and(qcustomerPackage.customers.id.eq(parentCustId));
        List<CustomerPackage> customerPackageList = (List<CustomerPackage>) customerPackageRepository.findAll(booleanExpression);
        List<Double> dbrList = customerPackageList.stream().map(CustomerPackage::getDbr).filter(data -> null != data).collect(Collectors.toList());
        Double maxDbr = 0.0;
        if (dbrList != null && !dbrList.isEmpty() && dbrList.size() > 0)
            maxDbr = dbrList.stream().max(Double::compare).get();

        QPlanGroup qPlanGroup = QPlanGroup.planGroup;
        BooleanExpression boolExp = qPlanGroup.isNotNull().and(qPlanGroup.isDelete.eq(false))
                .and(qPlanGroup.status.eq(CommonConstants.ACTIVE_STATUS));

        if (maxDbr != 0.0)
            boolExp = boolExp.and(qPlanGroup.dbr.loe(maxDbr));

        if (getBUIdsFromCurrentStaff().size() != 0)
            // TODO: pass mvnoID manually 6/5/2025
            boolExp = boolExp
                    .and(qPlanGroup.mvnoId.eq(1)
                            .or(qPlanGroup.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qPlanGroup.buId.in(getBUIdsFromCurrentStaff()))));

        List<PlanGroup> planGroupList = (List<PlanGroup>) this.getRepository().findAll((Pageable) boolExp);
        return planGroupList;
    }

//    public void assignPlanGroupDocStaff(PlanGroup planGroup, Integer staffId) {
//        CustomerCafAssignment cafAssignment = new CustomerCafAssignment();
//        cafAssignment.setAssignedDate(LocalDateTime.now());
//        if (staffId != null) {
//            StaffUser staff = staffUserService.get(staffId);
//            if (planGroup != null && staff != null) {
//                savePlanGroupAssignment(planGroup, staff, true);
//            }
//        }
//    }

//    public void savePlanGroupAssignment(PlanGroup planGroup, StaffUser staffUser, boolean flag) {
//        CustomerCafAssignment cafAssignment = new CustomerCafAssignment();
//        cafAssignment.setAssignedDate(LocalDateTime.now());
//        if (planGroup != null && staffUser != null) {
//            cafAssignment.setPlanGroup(planGroup);
//            cafAssignment.setStaffUser(staffUser);
//            cafAssignment.setStatus("NewActivation");
//            cafAssignment.setRemark("");
//
//            if (staffUser.getStaffUserparent() != null)  //check team and then varify this condtion
//                cafAssignment.setNextStaffUser(staffUser.getStaffUserparent());
//            if (flag) {
//                cafAssignment.setStatus("Approved");
//                cafAssignment.setRemark("Approved by : " + staffUser.getFirstname());
//                customerCafAssignmentRepository.save(cafAssignment);
//            } else {
//                cafAssignment.setStatus("NewActivation");
//                cafAssignment.setRemark("");
//                customerCafAssignmentRepository.save(cafAssignment);
//            }
//            if (staffUser.getStaffUserparent() != null && staffUser.getStaffUserparent() != null && staffUser.getServicearea() != null && staffUser.getStaffUserparent() != null && staffUser.getServicearea().getName().equalsIgnoreCase(staffUser.getStaffUserparent().getServicearea().getName())) {
//                findAllParentStaff(planGroup, staffUser.getStaffUserparent(), false);
//            }
//        }
//
//    }

//    public void initAssignPlan(PlanGroup planGroup, StaffUser staffUser, boolean flag) {
//        if (staffUser != null) {
//            savePlanGroupAssignment(planGroup, staffUser, false);
//        }
//    }

//    public void findAllParentStaff(PlanGroup planGroup, StaffUser staffUser, boolean flag) {
//        initAssignPlan(planGroup, staffUser, flag);
//    }

    public GenericDataDTO updatePlanGroupAssignment(CustomerCafAssignmentPojo pojo, Integer mvnoId) throws NoSuchFieldException {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (pojo.getPlanGroupId() != null && pojo.getStaffId() != null) {
            PlanGroup planGroup = getEntityForUpdateAndDelete(pojo.getPlanGroupId(),mvnoId);
            StaffUser staffUser = staffUserRepository.findById(pojo.getStaffId()).get();
            StaffUser loggedInUser = staffUserRepository.findById(getLoggedInUserId()).get();
            StringBuilder approvedByName = new StringBuilder();
            StaffUser assignedUser;
            assignedUser = staffUser;
            if (!staffUser.getUsername().equalsIgnoreCase("admin")) {
                if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,mvnoId).equals("TRUE")) {
                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(planGroup.getMvnoId(), planGroup.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, CommonConstants.HIERARCHY_TYPE, pojo.getFlag().equalsIgnoreCase("approved"), false, planGroup);
                    if (!map.containsKey("staffId") && !map.containsKey("nextTatMappingId")) {
                        planGroup.setNextTeamHierarchyMappingId(null);
                        planGroup.setNextStaff(null);
                        if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
                            planGroup.setStatus(SubscriberConstants.ACTIVE);
                        } else if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected")) {
                            planGroup.setStatus(SubscriberConstants.REJECT);
                        }
                        if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                            if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                                map.put("tat_id", map.get("current_tat_id"));
                            tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, planGroup.getPlanGroupId(), null);
                        }
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup.getPlanGroupId(), planGroup.getPlanGroupName(), staffUser.getId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " by :- " + staffUser.getUsername());
                    } else {
                        planGroup.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        planGroup.setNextStaff(Integer.valueOf(map.get("staffId")));
                        StaffUser assigned = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).get();
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup.getPlanGroupId(), planGroup.getPlanGroupName(), staffUser.getId(), assigned.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(),
                                "Remarks  : " + pojo.getRemark() + "\n" + "Assigned to :- " + assigned.getUsername());
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup.getPlanGroupId(), planGroup.getPlanGroupName(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " by :- " + staffUser.getUsername());
                    }
                } else {

                    /*for direct reject by creater*/
                    if(pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected") && planGroup.getNextTeamHierarchyMappingId() == null) {
                        hierarchyService.rejectDirectFromCreatedStaff(CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup.getPlanGroupId());
                        planGroup.setNextStaff(null);
                        planGroup.setStatus(SubscriberConstants.REJECT);
                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup.getPlanGroupId(), planGroup.getPlanGroupName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());


                    } else {

                        Map<String, Object> mapForNextApproval = hierarchyService.getTeamForNextApprove(planGroup.getMvnoId(), planGroup.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, CommonConstants.HIERARCHY_TYPE, pojo.getFlag().equalsIgnoreCase("approved"), planGroup.getNextTeamHierarchyMappingId() == null, planGroup);
                        if (mapForNextApproval.containsKey("assignableStaff")) {
                            genericDataDTO.setDataList((List<StaffUserPojo>) mapForNextApproval.get("assignableStaff"));
                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup.getPlanGroupId(), planGroup.getPlanGroupName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());

                        } else {

                            planGroup.setNextTeamHierarchyMappingId(null);
                            planGroup.setNextStaff(null);
                            if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {

                                if (!planGroup.getStatus().equals("Inactive")) {
                                    planGroup.setStatus(SubscriberConstants.ACTIVE);
                                }
                            } else if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected")) {

                                planGroup.setStatus(SubscriberConstants.REJECT);
                            }


                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PLAN_GROUP, planGroup.getPlanGroupId(), planGroup.getPlanGroupName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());

                        }
                    }
                }

            } else {
                approvedByName.append("Administrator");
                if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
                    if(!planGroup.getStatus().equals("Inactive")) {
                        planGroup.setStatus(SubscriberConstants.ACTIVE);
                    }

                } else {

                    planGroup.setStatus(SubscriberConstants.REJECT);
                }
                planGroup.setNextTeamHierarchyMappingId(null);
                planGroup.setNextStaff(null);
                if(!planGroup.getStatus().equals("Inactive") && !planGroup.getStatus().equals(SubscriberConstants.REJECT )) {
                    planGroup.setStatus(SubscriberConstants.ACTIVE);
                }

            }
//            customerCafAssignmentRepository.save(customerCafAssignment);
            PlanGroup planGroup1 = save(planGroup);

            genericDataDTO.setData(planGroup1);
            genericDataDTO.setResponseCode(200);
//            workflowAuditService.planAssignByStaffID(pojo, plan, customerCafAssignment, getLoggedInUserId());
            planGroupSharedToCRM(planGroup1,false);
            createDataSharedService.updateEntityDataForAllMicroService(planGroup1);

        }

        return genericDataDTO;
    }

    public GenericDataDTO getPlanGroupApprovalsList(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder, Integer mvnoId) {
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", CommonConstants.SORT_ORDER_DESC);
        QPlanGroup qPlanGroup = QPlanGroup.planGroup;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        BooleanExpression booleanExpression = qPlanGroup.isNotNull().and(qPlanGroup.isDelete.eq(false)).and(qPlanGroup.status.eq(SubscriberConstants.NEW_ACTIVATION)).and(qPlanGroup.nextStaff.eq(getLoggedInUserId()));
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPlanGroup.mvnoId.in(1, mvnoId));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPlanGroup.mvnoId.eq(1).or(qPlanGroup.mvnoId.eq(mvnoId).and(qPlanGroup.buId.in(getBUIdsFromCurrentStaff()))));
        }

        Page<PlanGroup> paginationList = entityRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    private PlanGroup getServiceareamappingId(PlanGroupDTO planGroupDTO, PlanGroup planGroup) {
        List<Long> serviceAreaPlanGroupMappinglist=planGroupDTO.getServiceAreaId();
        List<ServiceArea> serviceAreas=serviceAreaRepository.getServiceAreaByServiceAreaId(planGroupDTO.getServiceAreaId());
        List<ServiceArea> serviceAreass=serviceAreaRepository.findAllById(serviceAreaPlanGroupMappinglist);
        if(serviceAreas!=null){
            planGroup.setServicearea(serviceAreas);
        }
        return planGroup;
    }

    public ServiceAreaPlanGroupMappingDTO getAllserviceAreaByPlangroupId(Integer planGroupId) {
        ServiceAreaPlanGroupMappingDTO pojo =  new ServiceAreaPlanGroupMappingDTO();
        QServiceAreaPlanGroupMapping qServiceAreaPlanGroupMapping = QServiceAreaPlanGroupMapping.serviceAreaPlanGroupMapping;
        BooleanExpression exp = qServiceAreaPlanGroupMapping.isNotNull();
        exp = exp.and(qServiceAreaPlanGroupMapping.planGroup.planGroupId.eq(planGroupId));
        List<ServiceAreaPlanGroupMapping> plangroup = (List<ServiceAreaPlanGroupMapping>) serviceAreaPlangroupMappingRepo.findAll(exp);
        List<String> serviceArea = new ArrayList<>();
        List<ServiceArea> serviceareaids = new ArrayList<>();
        for (int i = 0; i < plangroup.size(); i++) {
            serviceArea.add(plangroup.get(i).getServiceArea().getName());
            serviceareaids.add(plangroup.get(i).getServiceArea());
        }
        pojo.setServiceareaid(serviceareaids);
        pojo.setServiceareaName(serviceArea);
        return pojo;
    }

//    private Charge getServicesMappingId(ChargePojo pojo, Charge charge) {
//        ServiceChargeMapping serviceChargeMapping = new ServiceChargeMapping();
//        List<Long> serviceChargeMappingList = pojo.getServiceid();
//        List<Services> services = serviceRepository.findAllById(serviceChargeMappingList);
//        if(services != null){
//            charge.setServiceList(services);
//        }
//        return charge;
//    }
    public List<ProductPlanGroupMapping> updateProductPlanGroupMapping(List<ProductPlanGroupMapping> productPlanGroupMappingList, Integer planGroupId, Integer mvnoId) {
        try {
            List<ProductPlanGroupMapping> planGroupMappingListToSave = new ArrayList<ProductPlanGroupMapping>();
            ProductPlanGroupMapping planGroupMapping;
            if (!productPlanGroupMappingList.isEmpty()) {
                for (ProductPlanGroupMapping mapping : productPlanGroupMappingList) {
                    if(mapping.getId() != null) {
                        planGroupMapping = productPlanGroupMappingRepository.findById(mapping.getId()).get();
                        PlanGroup planGroup = findPlanGroupById(planGroupId, mvnoId);
                        planGroupMapping.setPlanGroupId(Long.valueOf(planGroupId));
                        if (mapping.getProductId() != null) {
                            planGroupMapping.setProductId(mapping.getProductId());
                            planGroupMapping.setProductName(mapping.getProductName());
                        } else {
                            planGroupMapping.setProductId(null);
                            planGroupMapping.setProductName(null);
                        }
                        planGroupMapping.setProductCategoryId(mapping.getProductCategoryId());
                        planGroupMapping.setProduct_type(mapping.getProduct_type());
                        planGroupMapping.setOwnershipType(mapping.getOwnershipType());
                        planGroupMapping.setRevisedCharge(mapping.getRevisedCharge());
                        planGroupMapping.setPlanName(mapping.getPlanName());
                        planGroupMapping.setProductCategoryName(mapping.getProductCategoryName());
                        planGroupMapping.setPlanId(mapping.getPlanId());
                        planGroupMappingListToSave.add(planGroupMapping);
                    }
                    if(mapping.getId() == null) {
                        ProductPlanGroupMapping productPlanGroupMapping = new ProductPlanGroupMapping();
                        productPlanGroupMapping.setPlanGroupId(Long.valueOf(planGroupId));
                        productPlanGroupMapping.setProductCategoryId(mapping.getProductCategoryId());
                        if (mapping.getProductId() != null) {
                            productPlanGroupMapping.setProductId(mapping.getProductId());
                            productPlanGroupMapping.setProductName(mapping.getProductName());
                        } else {
                            productPlanGroupMapping.setProductId(null);
                            productPlanGroupMapping.setProductName(null);
                        }
                        productPlanGroupMapping.setOwnershipType(mapping.getOwnershipType());
                        productPlanGroupMapping.setRevisedCharge(mapping.getRevisedCharge());
                        productPlanGroupMapping.setProduct_type(mapping.getProduct_type());
                        productPlanGroupMapping.setPlanName(mapping.getPlanName());
                        productPlanGroupMapping.setProductCategoryName(mapping.getProductCategoryName());
                        productPlanGroupMapping.setPlanId(mapping.getPlanId());
                        planGroupMappingListToSave.add(productPlanGroupMapping);
                    }
                }
            }
            return productPlanGroupMappingRepository.saveAll(planGroupMappingListToSave);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    // Validate PlanGroup
    public void validatePlanGroup(PlanGroupDTO planGroupDTO, Integer operation) {
        if(planGroupDTO == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if(planGroupDTO != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_ADD))) {
            if(planGroupDTO.getPlanMappingList() != null || planGroupDTO.getPlanMappingList().size() > 0) {
                List<Integer> planIds = new ArrayList<>();
                for(PlanGroupMappingDTO planGroupMappingDTO : planGroupDTO.getPlanMappingList()) {
                    if(planIds.contains(planGroupMappingDTO.getPlanId())) {
                        throw new CustomValidationException(APIConstants.FAIL, "Duplicate plan is not allowed", null);
                    }
                    planIds.add(planGroupMappingDTO.getPlanId());
                }
            }
        }
    }
    //Delete Product Plan and Plan Group Mapping by Plan Group Id and Plan Id
    public void deleteProductPlanGroupMapping(Long planGroupId, Long planId) {
        try {
            QProductPlanGroupMapping qProductPlanGroupMapping = QProductPlanGroupMapping.productPlanGroupMapping;
            BooleanExpression booleanExpression = qProductPlanGroupMapping.planId.eq(planId).and(qProductPlanGroupMapping.planGroupId.eq(planGroupId));
            List<ProductPlanGroupMapping> productPlanGroupMappingList = IterableUtils.toList(productPlanGroupMappingRepository.findAll(booleanExpression));
            productPlanGroupMappingList.stream().forEach(productPlanGroupMapping -> {
                productPlanGroupMappingRepository.delete(productPlanGroupMapping);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private double parseDoubleOrDefault(String value, Double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String sanitizeString(String input) {
        return input.replaceAll("[^\\d.]", "");
    }

    public void sendCreateDataShared(PlanGroup planGroup, Integer operation) throws Exception {
        try {
//            PlanGroup planGroup = plangroupMapper.dtoToDomain(planGroupDTO, new CycleAvoidingMappingContext());
//            PlanGroup planGroup = planGroupRepository.findById(planGroupId).get();
            PlanGroup finalPlanGroupMaping = getPlanGroupEntity(planGroup);
            if (operation.equals(CommonConstants.OPERATION_ADD)) {
                createDataSharedService.sendEntitySaveDataForAllMicroService(finalPlanGroupMaping);
            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
                createDataSharedService.updateEntityDataForAllMicroService(finalPlanGroupMaping);
            } else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
                createDataSharedService.deleteEntityDataForAllMicroService(finalPlanGroupMaping);
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
    }

    public PlanGroup getPlanGroupEntity(PlanGroup planGroup) throws Exception {
        PlanGroup finalPlanGroupEntity = planGroup;
        try {
            List<PlanGroupMapping> planGroupMappingList = new ArrayList<>();
            List<ServiceArea> serviceAreas = new ArrayList<>();
            List<ProductPlanGroupMapping> productPlanGroupMappingList = new ArrayList<>();
            if (planGroup.getPlanMappingList() != null) {
                for (PlanGroupMapping item : planGroup.getPlanMappingList()) {
                    PlanGroupMapping planGroupMapping = new PlanGroupMapping();
                    planGroupMapping.setPlanGroupMappingId(item.getPlanGroupMappingId());
                    planGroupMapping.setPlanId(item.getPlan().getId().longValue());
                    planGroupMapping.setPlanGroupId(planGroup.getPlanGroupId());
                    planGroupMapping.setMvnoId(item.getMvnoId());
                    planGroupMapping.setIsDelete(item.getIsDelete());
                    planGroupMapping.setService(item.getService());
                    planGroupMapping.setNewofferprice(item.getNewofferprice());
                    planGroupMappingList.add(planGroupMapping);
                }
                finalPlanGroupEntity.setPlanMappingList(planGroupMappingList);
            }
            if (planGroup.getServicearea() != null) {
                for (ServiceArea item : planGroup.getServicearea()) {
                    ServiceArea serviceArea = new ServiceArea();
                    serviceArea.setId(item.getId());
                    serviceAreas.add(serviceArea);
                }
                finalPlanGroupEntity.setServicearea(serviceAreas);
            }
            if (planGroup.getProductPlanGroupMappingList() != null) {
                for (ProductPlanGroupMapping item : planGroup.getProductPlanGroupMappingList()) {
                    ProductPlanGroupMapping productPlanGroupMapping = new ProductPlanGroupMapping();
                    PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(Math.toIntExact(item.getPlanId())).get();
                    productPlanGroupMapping.setPlanGroupId(Long.valueOf(planGroup.getPlanGroupId()));
                    productPlanGroupMapping.setPlanId(item.getPlanId());
                    productPlanGroupMapping.setProduct_type(item.getProduct_type());
                    productPlanGroupMapping.setProductId(item.getProductId());
                    productPlanGroupMapping.setRevisedCharge(item.getRevisedCharge());
                    productPlanGroupMapping.setProductCategoryId(item.getProductCategoryId());
                    productPlanGroupMapping.setOwnershipType(item.getOwnershipType());
                    productPlanGroupMapping.setName(item.getName());
                    productPlanGroupMappingList.add(productPlanGroupMapping);
                }
                finalPlanGroupEntity.setProductPlanGroupMappingList(productPlanGroupMappingList);
            }
            finalPlanGroupEntity.setCreatedById(getLoggedInUserId());
            finalPlanGroupEntity.setLastModifiedById(getLoggedInUserId());
            finalPlanGroupEntity.setMvnoId(getMvnoIdFromCurrentStaff(null));
            finalPlanGroupEntity.setCreatedByName(getLoggedInUser().getFullName());
            finalPlanGroupEntity.setLastModifiedByName(getLoggedInUser().getFullName());
        } catch (CustomValidationException e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }
        return finalPlanGroupEntity;
    }

    public void planGroupSharedToCRM(PlanGroup planGroup, Boolean isforSave){
        PlanGroupDTO planGroupSharedDTO = plangroupMapper.domainToDTO(planGroup,new CycleAvoidingMappingContext());

        if(isforSave.equals(true)){
            if(planGroupSharedDTO.getAllowdiscount()==null){
                planGroupSharedDTO.setAllowdiscount(planGroup.isAllowDiscount());
            }
            PlanGroupMsg planGroupMsg = new PlanGroupMsg(planGroupSharedDTO);
//            messageSender.send(planGroupMsg, RabbitMqConstants.QUEUE_PLANGROUP_SALESCRM);
            kafkaMessageSender.send(new KafkaMessageData(planGroupMsg, KafkaConstant.LEAD_SAVE_PLANGROUP,PlanGroupMsg.class.getSimpleName()));

        }
        else{
            if(planGroupSharedDTO.getAllowdiscount()==null){
                planGroupSharedDTO.setAllowdiscount(planGroup.isAllowDiscount());
            }
            PlanGroupMsg planGroupMsg = new PlanGroupMsg(planGroupSharedDTO);
//            messageSender.send(planGroupMsg, RabbitMqConstants.QUEUE_PLANGROUP_SALESCRM_UPDATE);
            kafkaMessageSender.send(new KafkaMessageData(planGroupMsg,KafkaConstant.LEAD_UPDATE_PLANGROUP,PlanGroupMsg.class.getSimpleName()));
        }

    }
    @Override
   public List<Long> getServiceAreaIdList(){
        return staffUserRepository.findServiceAreaIdsByStaffId(getLoggedInStaffId());
    }
}
