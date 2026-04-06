package com.adopt.apigw.service.postpaid;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.servicePlan.model.ServicesDTO;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.modules.servicePlan.service.ServicesService;
import com.adopt.apigw.modules.subscriber.model.CustomerPlansModel;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.utils.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.adopt.apigw.pojo.api.PlanGroupMappingDTO;
import com.adopt.apigw.service.radius.AbstractService;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class PlanGroupMappingService extends AbstractService<PlanGroupMapping, PlanGroupMappingDTO, Integer> {

    @Autowired
    private PlanGroupMappingRepository entityRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private PlanGroupService planGroupService;
    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private PlanGroupMappingRepository planGroupMappingRepository;

    @Autowired
    private ServicesService servicesService;

    @Autowired
    private PlanGroupMappingChargeRelRepo chargeRelRepo;

    @Autowired
    private ChargeRepository chargeRepository;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;


    @Override
    protected JpaRepository<PlanGroupMapping, Integer> getRepository() {
        return entityRepository;
    }

    public PlanGroupMapping savePlanGroupMapping(PlanGroupMappingDTO planGroupMappingDTO, Integer mvnoId) {
        try {
            PlanGroupMapping planGroupMapping = new PlanGroupMapping();
            planGroupMapping.setMvnoId(mvnoId);
            PostpaidPlan postpaidPlan = new PostpaidPlan(postpaidPlanService.findById(planGroupMappingDTO.getPlanId()));
            if (postpaidPlan == null) {
                throw new IllegalArgumentException(
                        "No record found for Plan with Plan id : '" + planGroupMappingDTO.getPlanId());
            }
            PlanGroup planGroup = planGroupService.findPlanGroupById(planGroupMappingDTO.getPlanGroupId(), mvnoId);
            planGroupMapping.setPlan(postpaidPlan);
            Long servicId= Long.parseLong(planGroupMappingDTO.getService());
            planGroupMapping.setService(serviceRepository.findById(servicId).get().getServiceName());
            planGroupMapping.setCreatedate(LocalDateTime.now());
            planGroupMapping.setCreatedByName(getLoggedInUser().getFirstName() + getLoggedInUser().getLastName());
            planGroupMapping.setCreatedById(getLoggedInUserId());
            planGroupMapping.setLastModifiedById(getLoggedInUserId());
            planGroupMapping.setLastModifiedByName(getLoggedInUser().getFirstName() + getLoggedInUser().getLastName());
            planGroupMapping.setUpdatedate(LocalDateTime.now());
            planGroupMapping.setPlanGroup(planGroup);
            planGroupMapping.setNewofferprice(planGroupMappingDTO.getNewOfferPrice());
            postpaidPlanService.checkServiceAreaBind(planGroup);
            return entityRepository.save(planGroupMapping);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<PlanGroupMapping> findAllPlanGroupMappings(Integer mvnoId) {
        try {
            QPlanGroupMapping qPlanGroupMapping = QPlanGroupMapping.planGroupMapping;
            BooleanExpression exp = qPlanGroupMapping.isNotNull();
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                exp = exp.and(qPlanGroupMapping.mvnoId.in(mvnoId, 1));
            return (List<PlanGroupMapping>) entityRepository.findAll(exp);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<PlanGroupMapping> updatePlanGroupMapping(List<PlanGroupMappingDTO> planGroupMappingList,
                                                         Integer planGroupId, Integer mvnoId) {
        try {
            deleteOldPlanGroupMapping(planGroupId, mvnoId);
            deleteOldPlanGroupChargeMapping(planGroupMappingList);
            List<PlanGroupMapping> mappingList = saveChangedPlanGroupMapping(planGroupMappingList, planGroupId, mvnoId);
            return mappingList;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void deleteOldPlanGroupChargeMapping(List<PlanGroupMappingDTO> planGroupMappingList) {
        try {
            List<Integer> plangGroupMappingIds = planGroupMappingList.stream().map(PlanGroupMappingDTO::getPlanGroupMappingId).filter(Objects::nonNull).collect(Collectors.toList());
            QPlanGroupMappingChargeRel qPlanGroupMappingChargeRel = QPlanGroupMappingChargeRel.planGroupMappingChargeRel;
            BooleanExpression exp = qPlanGroupMappingChargeRel.isNotNull().and(qPlanGroupMappingChargeRel.planGroupMapping.planGroupMappingId.in(plangGroupMappingIds));
            List<PlanGroupMappingChargeRel> planGroupMappingChargeRel = (List<PlanGroupMappingChargeRel>) chargeRelRepo.findAll(exp);
            if (!planGroupMappingChargeRel.isEmpty()) {
                for (PlanGroupMappingChargeRel planGroupMappingChargeRel1 : planGroupMappingChargeRel) {
                    chargeRelRepo.delete(planGroupMappingChargeRel1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deletePlanGroupMappingById(Integer planGroupMappingId, Integer mvnoId) {
        try {
            Optional<PlanGroupMapping> planGroupMapping = entityRepository.findById(planGroupMappingId);
            if (planGroupMapping.isPresent()) {
                planGroupMapping.get().setIsDelete(true);
            }
            entityRepository.save(planGroupMapping.get());
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void deleteOldPlanGroupMapping(Integer planGroupId, Integer mvnoId) {
        List<PlanGroupMapping> planGroupMappingToDelete = new ArrayList<PlanGroupMapping>();
        List<PlanGroupMapping> planGroupMappings = entityRepository.findPlanGroupMappingByPlanGroupId(planGroupId, mvnoId);
        if (!planGroupMappings.isEmpty()) {
            for (PlanGroupMapping planGroupMapping : planGroupMappings) {
                if (planGroupMapping.getMvnoId() == mvnoId) {
                    planGroupMappingToDelete.add(planGroupMapping);
                    planGroupMapping.setIsDelete(true);
                }
            }
        }
        entityRepository.deleteAll(planGroupMappingToDelete);
    }

    private List<PlanGroupMapping> saveChangedPlanGroupMapping(List<PlanGroupMappingDTO> planGroupMappingList, Integer planGroupId, Integer mvnoId) {

        List<PlanGroupMapping> planGroupMappingListToSave = new ArrayList<PlanGroupMapping>();
        PlanGroupMapping planGroupMapping;
        PlanGroupMapping oldObj = null;
//        if (pojo.getId() != null) {
//            oldObj = get(pojo.getId());
//        }
        if (!planGroupMappingList.isEmpty()) {
            for (PlanGroupMappingDTO mapping : planGroupMappingList) {
                planGroupMapping = new PlanGroupMapping();
                PlanGroup planGroup = planGroupService.findPlanGroupById(planGroupId, mvnoId);
                planGroupMapping.setPlanGroup(planGroup);
                planGroupMapping.setMvnoId(mvnoId);
                planGroupMapping.setLastModifiedById(getLoggedInUserId());
                planGroupMapping.setLastModifiedByName(getLoggedInUser().getFirstName() + getLoggedInUser().getLastName());
                PostpaidPlan postpaidPlan = postpaidPlanService.findById(mapping.getPlanId());
                if (postpaidPlan == null) {
                    throw new IllegalArgumentException(
                            "No record found for Plan with Plan id : '" + mapping.getPlanId());
                }
                planGroupMapping.setPlan(postpaidPlan);
                Long servicId= Long.parseLong(mapping.getService());
                planGroupMapping.setService(serviceRepository.findById(servicId).get().getServiceName());
//                planGroupMapping.setService(mapping.getService());
                planGroupMapping.setUpdatedate(LocalDateTime.now());
                planGroupMapping.setNewofferprice(mapping.getNewOfferPrice());
                planGroupMappingListToSave.add(planGroupMapping);


                //loop for saving charge and plangroupmapping data ,when charge is revised during plangroup updation
                for (PlanGroupMappingChargeRelDto chargeDto : mapping.getChargeList()) {
                    PlanGroupMappingChargeRel planGroupMappingChargeRel = new PlanGroupMappingChargeRel();
                    planGroupMappingChargeRel.setPlanGroupMapping(planGroupMapping);
                    planGroupMappingChargeRel.setPrice(chargeDto.getChargeprice());
                    Charge charge = chargeRepository.findById(chargeDto.getId()).get();
                    planGroupMappingChargeRel.setCharge(charge);
                    planGroupMappingChargeRel.setPlanId(mapping.getPlanId());
                    if (chargeDto.getChargeName() != null) {
                        planGroupMappingChargeRel.setChargeName(chargeDto.getChargeName());
                    } else {
                        planGroupMappingChargeRel.setChargeName(charge.getName());
                    }
                    chargeRelRepo.save(planGroupMappingChargeRel);
                }

            }
        }
        return entityRepository.saveAll(planGroupMappingListToSave);
    }

    public List<PlanGroup> findPlanGroupMappingByCustId(Integer custId) {
        try {
            List<PlanGroup> planGroups = new ArrayList<>();
            // TODO: pass mvnoID manually 6/5/2025
            Integer mvnoId = getMvnoIdFromCurrentStaff(null);
            QPlanGroupMapping qPlanGroupMapping = QPlanGroupMapping.planGroupMapping;
            BooleanExpression boolExp = qPlanGroupMapping.isNotNull().and(qPlanGroupMapping.isDelete.eq(false));
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qPlanGroupMapping.mvnoId.in(mvnoId, 1));

//			for getting customer current plan IDs
            List<CustomerPlansModel> planslist = subscriberService.getActivePlanList(custId, false);
            planslist = planslist.stream().filter(i -> !i.isIsdeleteforVoid()).collect(Collectors.toList());
            List<Integer> customePlanIds = planslist.stream().map(plan -> plan.getPlanId()).collect(Collectors.toList());

            List<CustomerServiceMapping> customerServiceMappingList = customerServiceMappingRepository.findByCustId(custId);
            Set<String> customerServiceNames = new HashSet<>();
            List<Long> customerServiceids = customerServiceMappingList.stream().map(CustomerServiceMapping::getServiceId).collect(Collectors.toList());

            List<Integer> newCustomersIds = customerServiceids.stream().map(Long::intValue).collect(Collectors.toList());
            ;
            List<PlanGroupMapping> newplanGroupMappingList = planGroupMappingRepository.findPlanGroupMappingByServiceIdsIn(mvnoId, newCustomersIds);


            for (CustomerServiceMapping customerServiceMapping : customerServiceMappingList) {
                ServicesDTO services = servicesService.getEntityById(customerServiceMapping.getServiceId(),mvnoId);
                if (services != null) {
                    customerServiceNames.add(services.getServiceName());
                }
            }
            List<PlanGroupMapping> planGroupMappingList = (List<PlanGroupMapping>) entityRepository.findAll(boolExp);
//			List<PlanGroupMapping> planGroupMappingList2 = planGroupMappingList.stream()
//					.filter(planGroupMapping -> customerServiceids.stream()
//							.anyMatch(serviceId -> serviceId == Long.valueOf(planGroupMapping.getPlan().getServiceId()))).collect(Collectors.toList());
            List<Integer> planGroupIds = newplanGroupMappingList.stream().map(PlanGroupMapping::getPlanGroup).collect(Collectors.toList())
                    .stream().map(PlanGroup::getPlanGroupId).collect(Collectors.toList());
            Set<Integer> uniqueIds = new HashSet<>(planGroupIds);
            if (planGroupIds.size() > 0) {
                for (Integer id : uniqueIds) {
                    List<String> serviceCheck = new ArrayList<>();
//					List<String> serviceCheck = planGroupMappingList.stream()
//							.filter(planGroupMapping -> customerServiceids.stream()
//									.filter(serviceid -> id == planGroupMapping.getPlanGroup().getPlanGroupId())
//									.anyMatch(serviceId -> serviceId == Long.valueOf(planGroupMapping.getPlan().getServiceId())))
//							.collect(Collectors.toList()).stream().map(PlanGroupMapping::getService).collect(Collectors.toList());
                    List<PlanGroupMapping> testplanGroupMappingList = planGroupMappingRepository.findPlanGroupMappingByServiceIdsInAndPlanGroupId(mvnoId, newCustomersIds, id);
                    if (testplanGroupMappingList != null && testplanGroupMappingList.size() > 0) {
                        serviceCheck = testplanGroupMappingList.stream().map(PlanGroupMapping::getService).collect(Collectors.toList());
                    }
                    if (serviceCheck.size() == customerServiceids.size()) {
                        // TODO: pass mvnoID manually 6/5/2025
                        PlanGroup newPlangroup = planGroupService.findPlanGroupById(id, getMvnoIdFromCurrentStaff(null));
                        if (newPlangroup != null && newPlangroup.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)) {
                            planGroups.add(newPlangroup);
                        }
                    }
                }
            }
            Integer partnerId = getLoggedInUserPartnerId();
            if (partnerId!=null) {
                Partner partner = partnerRepository.findById(partnerId).get();
                if (partner.getPartnerType() != "LCO" && partnerId != 1) {
                    QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
                    BooleanExpression exp = qPartnerServiceAreaMapping.isNotNull().and(qPartnerServiceAreaMapping.partnerId.eq(partnerId));
                    List<PartnerServiceAreaMapping> partnerServiceAreaMappings = (List<PartnerServiceAreaMapping>) partnerServiceAreaMappingRepo.findAll(exp);
                    List<Long> serviceReaIds = partnerServiceAreaMappings.stream()
                            .mapToLong(PartnerServiceAreaMapping::getServiceId)
                            .boxed()
                            .collect(Collectors.toList());
                    List<ServiceArea> serviceAreas = serviceAreaRepository.findAllByIdIn(serviceReaIds);
                    planGroups = planGroups.stream()
                            .filter(two -> two.getServicearea().stream().anyMatch(serviceAreas::contains))
                            .collect(Collectors.toList());
                }
            }
            return planGroups;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<PlanGroupMapping> findPlanGroupMappingByPlanGroupId(Integer planGroupId, Integer mvnoId) {
        try {
            QPlanGroupMapping qPlanGroupMapping = QPlanGroupMapping.planGroupMapping;
            BooleanExpression boolExp = qPlanGroupMapping.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qPlanGroupMapping.mvnoId.in(mvnoId, 1));
            boolExp = boolExp.and(qPlanGroupMapping.planGroup.planGroupId.eq(planGroupId));
            boolExp = boolExp.and(qPlanGroupMapping.isDelete.eq(false));
            List<PlanGroupMapping> planGroupMappingList = (List<PlanGroupMapping>) entityRepository.findAll(boolExp);
            return planGroupMappingList;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteByPlanGroupId(Integer planGroupId, Integer mvnoId) {
        try {
            QPlanGroupMapping qPlanGroupMapping = QPlanGroupMapping.planGroupMapping;
            BooleanExpression boolExp = qPlanGroupMapping.isNotNull();
            if (mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qPlanGroupMapping.mvnoId.eq(mvnoId));
            boolExp = boolExp.and(qPlanGroupMapping.planGroup.planGroupId.eq(planGroupId));
            List<PlanGroupMapping> planGroupMappingList = (List<PlanGroupMapping>) entityRepository.findAll(boolExp);

            if (planGroupMappingList.size() > 0) {
                deleteOldPlanGroupChargeMappingList(planGroupMappingList);
                for (PlanGroupMapping planGroupMapping : planGroupMappingList) {
                    planGroupMapping.setIsDelete(true);
                }
                entityRepository.deleteAll(planGroupMappingList);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void deleteOldPlanGroupChargeMappingList(List<PlanGroupMapping> planGroupMappingList) {
        List<PlanGroupMappingChargeRel> planGroupMappingChargeRel = chargeRelRepo.findAllByPlanGroupMappingIn(planGroupMappingList);
        for (PlanGroupMappingChargeRel rel : planGroupMappingChargeRel) {
            chargeRelRepo.delete(rel);
        }
    }

    public PlanGroupMappingChargeRelDto getChargeList(List<PlanGroupMapping> planGroupMappingList) {
        try {
            PlanGroupMappingChargeRelDto planGroupMappingChargeRelDtos = new PlanGroupMappingChargeRelDto();
            List<PlanGroupMappingChargeRel> planGroupMappingChargeRelList = chargeRelRepo.findAllByPlanGroupMappingIn(planGroupMappingList);
            planGroupMappingChargeRelDtos.setPlanGroupMappingChargeRelList(planGroupMappingChargeRelList);

//			Double totalCharge = 0.00;
//			for (PlanGroupMappingChargeRel planGroupMappingChargeRel :planGroupMappingChargeRelList){
//				totalCharge = totalCharge + planGroupMappingChargeRel.getPrice();
//			}
            return planGroupMappingChargeRelDtos;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public PlanGroupMappingDTO savechargeinDto(Integer planId, PlanGroupMappingDTO planGroupMappingDTO) {

        Integer planGroupMappingId = planGroupMappingDTO.getPlanGroupMappingId();
        PlanGroupMapping planGroupMapping = planGroupMappingRepository.findById(planGroupMappingId).get();
        List<PlanGroupMappingChargeRel> planGroupMappingChargeRel = chargeRelRepo.findAllByPlanGroupMapping(planGroupMapping);

        for (PlanGroupMappingChargeRel planGroupMappingChargeRel1 : planGroupMappingChargeRel) {
            if (planGroupMappingChargeRel1.getPlanId().equals(planId)) {
                planGroupMappingDTO.getPlan().getChargeList().forEach(
                        data -> {
                            if (data.getCharge().getId().equals(planGroupMappingChargeRel1.getCharge().getId())) {
                                data.setChargeprice(planGroupMappingChargeRel1.getPrice());
                                data.setChargeName(planGroupMappingChargeRel1.getChargeName());
                                data.setId(planGroupMappingChargeRel1.getCharge().getId());
                            }
                        }
                );
            }

        }
        return planGroupMappingDTO;
    }


}
