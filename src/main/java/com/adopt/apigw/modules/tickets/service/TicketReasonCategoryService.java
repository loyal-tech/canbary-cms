package com.adopt.apigw.modules.tickets.service;


import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.QCustomerServiceMapping;
import com.adopt.apigw.model.postpaid.QPostpaidPlan;
import com.adopt.apigw.modules.servicePlan.domain.QServices;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.modules.subscriber.model.CustomerPlansModel;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.modules.tickets.domain.Case;
import com.adopt.apigw.modules.tickets.domain.QCase;
import com.adopt.apigw.modules.tickets.domain.QTicketReasonCategory;
import com.adopt.apigw.modules.tickets.domain.TicketReasonCategory;
import com.adopt.apigw.modules.tickets.mapper.TicketReasonCategoryMapper;
import com.adopt.apigw.modules.tickets.model.TicketReasonCategoryDTO;
import com.adopt.apigw.modules.tickets.repository.CaseRepository;
import com.adopt.apigw.modules.tickets.repository.TicketReasonCategoryRepo;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketReasonCategoryService extends ExBaseAbstractService2<TicketReasonCategoryDTO, TicketReasonCategory, Long> {


    @Autowired
    TicketReasonCategoryRepo repository;
    @Autowired
    TicketReasonCategoryMapper mapper;

    @Autowired
    SubscriberService subscriberService;
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    CaseRepository caseRepository;
    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;

    public TicketReasonCategoryService(TicketReasonCategoryRepo repository, TicketReasonCategoryMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "ticket_reason_category_id");
    }

    @Override
    public String getModuleNameForLog() {
        return "{TicketReasonCategoryService}";
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression booleanExpression = qTicketReasonCategory.isNotNull().and(qTicketReasonCategory.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        makeGenericResponse()
        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                switch (genericSearchModel.getFilterColumn()) {
                    case "name":
                        booleanExpression = booleanExpression.and(qTicketReasonCategory.categoryName.containsIgnoreCase(genericSearchModel.getFilterValue()));
                        break;
                    case "service":
                        booleanExpression = booleanExpression.and(qTicketReasonCategory.service.name.containsIgnoreCase(genericSearchModel.getFilterValue()));
                        break;
                }
            }
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff(null )));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.eq(1).or(qTicketReasonCategory.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qTicketReasonCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }

        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.isNull());

        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        PageRequest pageRequest = super.generatePageRequest(page, size, "createdate", 0);
        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression booleanExpression = qTicketReasonCategory.isNotNull().and(qTicketReasonCategory.isDeleted.eq(false));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.eq(1).or(qTicketReasonCategory.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qTicketReasonCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }

        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.isNull());

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
//        return super.getListByPageAndSizeAndSortByAndOrderBy(page,size,"createdate",0,filterList);
    }


    public List<TicketReasonCategoryDTO> getReasonCategoryByCustomer(Integer customerId) {
        List<Integer> activePlanIds = subscriberService.getActivePlanList(customerId,false).stream().map(CustomerPlansModel::getPlanId).collect(Collectors.toList());
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        List<Integer> serviceIds = new ArrayList<>();
        postpaidPlanRepo.findAll(qPostpaidPlan.id.in(activePlanIds)).forEach(postpaidPlan -> {
            serviceIds.add(postpaidPlan.getServiceId());
        });
        List<TicketReasonCategoryDTO> ticketReasonCategoryDTOS = new ArrayList<>();
        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression booleanExpression = qTicketReasonCategory.isNotNull().and(qTicketReasonCategory.isDeleted.eq(false)).and(qTicketReasonCategory.status.eq("Active")).and(qTicketReasonCategory.service.id.in(serviceIds));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.eq(1).or(qTicketReasonCategory.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qTicketReasonCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }

        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.isNull());

//        if (!departmentName.isEmpty()) {
//            booleanExpression = booleanExpression.and(qTicketReasonCategory.department.equalsIgnoreCase(departmentName));
//        }
        repository.findAll(booleanExpression).forEach(ticketReasonSubCategory -> ticketReasonCategoryDTOS.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
        return ticketReasonCategoryDTOS;

    }

    public List<TicketReasonCategoryDTO> getAllActiveReasonCategory() {
        List<TicketReasonCategoryDTO> ticketReasonCategoryDTOS = new ArrayList<>();
        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression booleanExpression = qTicketReasonCategory.isNotNull().and(qTicketReasonCategory.isDeleted.eq(false)).and(qTicketReasonCategory.status.eq("Active"));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.eq(1).or(qTicketReasonCategory.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qTicketReasonCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }
        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.isNull());

        repository.findAll(booleanExpression).forEach(ticketReasonSubCategory -> ticketReasonCategoryDTOS.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
        return ticketReasonCategoryDTOS;
    }

    @Override
    public List<TicketReasonCategoryDTO> getAllEntities(Integer mvnoId) throws Exception {
        List<TicketReasonCategoryDTO> list=new ArrayList<>();
        QTicketReasonCategory ticketReasonCategory=QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression expression=ticketReasonCategory.isNotNull();
        expression=expression.and(ticketReasonCategory.isDeleted.eq(false));

        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId != 1)
        {
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                expression=expression.and(ticketReasonCategory.mvnoId.in(1,mvnoId));
            else
                // TODO: pass mvnoID manually 6/5/2025
                expression=expression.and(ticketReasonCategory.buId.in(getBUIdsFromCurrentStaff())).and(ticketReasonCategory.mvnoId.in(mvnoId));
        }

        if(getLoggedInUser().getLco())
            expression=expression.and(ticketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            expression=expression.and(ticketReasonCategory.lcoId.isNull());

        repository.findAll(expression).forEach(ticketReasonSubCategory -> list.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
        return list;
    }

    @Override
    public TicketReasonCategoryDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }


    @Override
    public boolean duplicateVerifyAtSave(String name,Integer mvnoId) {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = repository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id,Integer mvnoId) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) count = repository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(name, Arrays.asList(mvnoId, 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = repository.duplicateVerifyAtSave(name, mvnoId, getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) == 1) countEdit = repository.duplicateVerifyAtEdit(name, id);
                else {
                    if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = repository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                    else
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = repository.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
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


    public List<Services> getActiveServiceForSubscribers(Integer customerId){
        List<Integer> activePlanIds = subscriberService.getActivePlanList(customerId,false).stream().map(CustomerPlansModel::getPlanId).collect(Collectors.toList());
        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
        List<Long> serviceIds = new ArrayList<>();
        postpaidPlanRepo.findAll(qPostpaidPlan.id.in(activePlanIds)).forEach(postpaidPlan -> {
            serviceIds.add(Long.valueOf(postpaidPlan.getServiceId()));
        });

        List<Services> activeServiceList = new ArrayList<>();
        activeServiceList = serviceRepository.findServicesByIdIn(serviceIds);

        return activeServiceList;


    }

    public List<TicketReasonCategoryDTO> getReasonCategoryByActiveServices(List<Integer> servicesListIds) {
//        List<Integer> activePlanIds = subscriberService.getActivePlanList(customerId).stream().map(CustomerPlansModel::getPlanId).collect(Collectors.toList());
//        QPostpaidPlan qPostpaidPlan = QPostpaidPlan.postpaidPlan;
//        List<Integer> serviceIds = new ArrayList<>();
//        postpaidPlanRepo.findAll(qPostpaidPlan.id.in(activePlanIds)).forEach(postpaidPlan -> {
//            serviceIds.add(postpaidPlan.getServiceId());
//        });
        List<TicketReasonCategoryDTO> ticketReasonCategoryDTOS = new ArrayList<>();
        QTicketReasonCategory qTicketReasonCategory = QTicketReasonCategory.ticketReasonCategory;
        BooleanExpression booleanExpression = qTicketReasonCategory.isNotNull().and(qTicketReasonCategory.isDeleted.eq(false)).and(qTicketReasonCategory.status.eq("Active")).and(qTicketReasonCategory.service.id.in(servicesListIds));
// TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qTicketReasonCategory.mvnoId.eq(1).or(qTicketReasonCategory.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qTicketReasonCategory.buId.in(getBUIdsFromCurrentStaff()))));
        }

        if(getLoggedInUser().getLco())
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            booleanExpression=booleanExpression.and(qTicketReasonCategory.lcoId.isNull());

//        if (!departmentName.isEmpty()) {
//            booleanExpression = booleanExpression.and(qTicketReasonCategory.department.equalsIgnoreCase(departmentName));
//        }
        repository.findAll(booleanExpression).forEach(ticketReasonSubCategory -> ticketReasonCategoryDTOS.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
        return ticketReasonCategoryDTOS;

    }

    public Boolean getUniqueCategory(Long reasoneCatId) {
        Boolean falg=false;
        QCase qCase=QCase.case$;
        BooleanExpression booleanExpression=qCase.isNotNull();
        booleanExpression=booleanExpression.and(qCase.isDelete.eq(false));
        booleanExpression=booleanExpression.and(qCase.ticketReasonCategoryId.eq(reasoneCatId));
       List<Case>caselist=IterableUtils.toList(caseRepository.findAll(booleanExpression));
       if(caselist.size()>0){
           falg=true;
       }
       return falg;

    }

    public List<Services> getAllServiceForSubscribers(Integer customerId){
        QCustomerServiceMapping qCustomerServiceMapping=QCustomerServiceMapping.customerServiceMapping;
        List<Services>servicesList=new ArrayList<>();
        BooleanExpression booleanExpression=qCustomerServiceMapping.isNotNull().and(qCustomerServiceMapping.isDeleted.eq(false))
                .and(qCustomerServiceMapping.customer.id.eq(customerId)).and(qCustomerServiceMapping.status.notEqualsIgnoreCase("Terminate"));
        List<CustomerServiceMapping> customerServiceMappingList=IterableUtils.toList(customerServiceMappingRepository.findAll(booleanExpression));
            if(!customerServiceMappingList.isEmpty()){
                List<Long>serviIds=customerServiceMappingList.stream().map(i->i.getServiceId()).collect(Collectors.toList());
                servicesList.addAll(serviceRepository.findServicesByIdIn(serviIds));
            }
        return servicesList;
    }
}
