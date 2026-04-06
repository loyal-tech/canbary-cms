package com.adopt.apigw.modules.tickets.service;
//
//import com.adopt.apigw.core.dto.GenericDataDTO;
//import com.adopt.apigw.core.dto.GenericSearchModel;
//import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
//import com.adopt.apigw.core.service.ExBaseAbstractService;
//import com.adopt.apigw.core.service.ExBaseAbstractService2;
//import com.adopt.apigw.modules.TicketTatMatrix.Domain.QTicketTatMatrixMapping;
//import com.adopt.apigw.modules.tickets.domain.*;
//import com.adopt.apigw.modules.tickets.mapper.TicketReasonSubCategoryMapper;
//import com.adopt.apigw.modules.tickets.model.TicketReasonSubCategoryDTO;
//import com.adopt.apigw.modules.tickets.repository.CaseRepository;
//import com.adopt.apigw.modules.tickets.repository.TicketReasonCategoryTATMappingRepo;
//import com.adopt.apigw.modules.tickets.repository.TicketReasonSubCategoryRepo;
//import com.adopt.apigw.modules.tickets.repository.TicketSubCategoryTatMappingRepo;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import org.apache.commons.collections4.IterableUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
public class TicketReasonSubCategoryService  {
//
//
//    @Autowired
//    TicketReasonSubCategoryRepo repository;
//    @Autowired
//    TicketReasonSubCategoryMapper mapper;
//
//    @Autowired
//    TicketReasonSubCategoryMapper ticketReasonSubCategoryMapper;
//
//    @Autowired
//    TicketReasonSubCategoryRepo reasonSubCategoryRepo;
//
//    @Autowired
//    TicketReasonCategoryTATMappingRepo mappingRepo;
//
//    @Autowired
//    TicketSubCategoryTatMappingRepo repo;
//    @Autowired
//    CaseRepository caseRepository;
//    @Autowired
//    public TicketReasonSubCategoryService(TicketReasonSubCategoryRepo repository, TicketReasonSubCategoryMapper mapper) {
//        super(repository, mapper);
//        sortColMap.put("id", "ticket_reason_category_id");
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[TicketReasonSubCategoryService]";
//    }
//
//    @Override
//    public TicketReasonSubCategoryDTO getEntityForUpdateAndDelete(Long aLong) throws Exception {
//        return super.getEntityForUpdateAndDelete(aLong);
//    }
//
//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
//        QTicketReasonSubCategory qTicketReasonSubCategory = QTicketReasonSubCategory.ticketReasonSubCategory;
//        BooleanExpression booleanExpression = qTicketReasonSubCategory.isNotNull().and(qTicketReasonSubCategory.isDeleted.eq(false));
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
////        makeGenericResponse()
//        if (filterList.size() > 0) {
//            for (GenericSearchModel genericSearchModel : filterList) {
//                switch (genericSearchModel.getFilterColumn()) {
//                    case "name":
//                        booleanExpression = booleanExpression.and(qTicketReasonSubCategory.subCategoryName.containsIgnoreCase(genericSearchModel.getFilterValue()));
//                        break;
//                   /* case "parentCategoryName":
//                        booleanExpression = booleanExpression.and(qTicketReasonSubCategory.parentCategory.categoryName.containsIgnoreCase(genericSearchModel.getFilterValue()));
//                        break;*/
//                }
//            }
//        }
//        if(getMvnoIdFromCurrentStaff() != 1)
//            booleanExpression = booleanExpression.and(qTicketReasonSubCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0)
//        {
//            booleanExpression = booleanExpression
//                    .and(qTicketReasonSubCategory.mvnoId.eq(1)
//                            .or(qTicketReasonSubCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketReasonSubCategory.buId.in(getBUIdsFromCurrentStaff()))));
//        }
//
//        if(getLoggedInUser().getLco())
//            booleanExpression=booleanExpression.and(qTicketReasonSubCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
//        else
//            booleanExpression=booleanExpression.and(qTicketReasonSubCategory.lcoId.isNull());
//
//        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
//    }
//
//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        PageRequest pageRequest = super.generatePageRequest(page, size, "createdate", 0);
//        QTicketReasonSubCategory qTicketReasonSubCategory = QTicketReasonSubCategory.ticketReasonSubCategory;
//        BooleanExpression booleanExpression = qTicketReasonSubCategory.isNotNull().and(qTicketReasonSubCategory.isDeleted.eq(false));
//        if(getMvnoIdFromCurrentStaff() != 1)
//            booleanExpression = booleanExpression.and(qTicketReasonSubCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0)
//        {
//            booleanExpression = booleanExpression
//                    .and(qTicketReasonSubCategory.mvnoId.eq(1)
//                            .or(qTicketReasonSubCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketReasonSubCategory.buId.in(getBUIdsFromCurrentStaff()))));
//        }
//
//
//        if(getLoggedInUser().getLco())
//            booleanExpression=booleanExpression.and(qTicketReasonSubCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
//        else
//            booleanExpression=booleanExpression.and(qTicketReasonSubCategory.lcoId.isNull());
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
////        return super.getListByPageAndSizeAndSortByAndOrderBy(page,size,"createdate",0,filterList);
//    }
//
//    public  List<TicketReasonSubCategoryDTO> getSubCategoryReasons(Long parentCategoryId) {
//        List<TicketReasonSubCategoryDTO> ticketReasonSubCategoryDTOS = new ArrayList<>();
//        HashSet<TicketReasonSubCategoryDTO> finalResultTicketReasonSubCategoryDTOS = new HashSet<>();
//        QTicketReasonSubCategory qTicketReasonSubCategory = QTicketReasonSubCategory.ticketReasonSubCategory;
//        BooleanExpression booleanExpression = qTicketReasonSubCategory.isNotNull().and(qTicketReasonSubCategory.isDeleted.eq(false)).and(qTicketReasonSubCategory.status.eq("Active"));
//        if(getMvnoIdFromCurrentStaff() != 1)
//            booleanExpression = booleanExpression.and(qTicketReasonSubCategory.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//        if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0)
//        {
//            booleanExpression = booleanExpression
//                    .and(qTicketReasonSubCategory.mvnoId.eq(1)
//                            .or(qTicketReasonSubCategory.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qTicketReasonSubCategory.buId.in(getBUIdsFromCurrentStaff()))));
//        }
//
//        if(getLoggedInUser().getLco())
//            booleanExpression=booleanExpression.and(qTicketReasonSubCategory.lcoId.eq(getLoggedInUser().getPartnerId()));
//        else
//            booleanExpression=booleanExpression.and(qTicketReasonSubCategory.lcoId.isNull());
//
//        repository.findAll(booleanExpression).forEach(ticketReasonSubCategory -> ticketReasonSubCategoryDTOS.add(mapper.domainToDTO(ticketReasonSubCategory, new CycleAvoidingMappingContext())));
//        for(TicketReasonSubCategoryDTO ticketReasonSubCategoryDTO: ticketReasonSubCategoryDTOS){
//            if(!ticketReasonSubCategoryDTO.getTicketSubCategoryReasonCategoryMappingList().isEmpty() &&
//                    ticketReasonSubCategoryDTO.getTicketSubCategoryReasonCategoryMappingList()!=null){
//                for(TicketSubCategoryReasonCategoryMapping ticketSubCategoryReasonCategoryMapping : ticketReasonSubCategoryDTO.getTicketSubCategoryReasonCategoryMappingList()){
//                    if(ticketSubCategoryReasonCategoryMapping.getTicketReasonCategoryId() == parentCategoryId){
//                        finalResultTicketReasonSubCategoryDTOS.add(ticketReasonSubCategoryDTO);
//                    }
//                }
//            }
//        }
//        return finalResultTicketReasonSubCategoryDTOS.stream().collect(Collectors.toList());
//    }
//
//    @Override
//    public boolean duplicateVerifyAtSave(String name) {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(name);
//            else {
//                if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) count = repository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                else count = repository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//            }
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    @Override
//    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(name);
//            else {
//                if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
//                    count = repository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                else
//                    count = repository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());            }
//            if (count >= 1) {
//                Integer countEdit;
//                if(getMvnoIdFromCurrentStaff() == 1) countEdit = repository.duplicateVerifyAtEdit(name, id);
//                else {
//                    if(getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
//                        countEdit = repository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                    else
//                        countEdit = repository.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//                }
//                if (countEdit == 1) {
//                    flag = true;
//                }
//            } else {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    public Iterable<TicketSubCategoryTatMapping> updateStatus(TicketReasonSubCategoryDTO entityDTO) {
//        Iterable<TicketSubCategoryTatMapping>mappings= new ArrayList<>();
//        QTicketSubCategoryTatMapping ticketSubCategoryTatMapping=QTicketSubCategoryTatMapping.ticketSubCategoryTatMapping;
//        BooleanExpression booleanExpression=ticketSubCategoryTatMapping.ticketReasonSubCategoryId.isNull();
//        mappings=  repo.findAll(booleanExpression);
////        mappings.forEach(ticketSubCategoryTatMapping1 ->booleanExpression.and(ticketSubCategoryTatMapping.isDeleted.eq(true)) );
//        for(TicketSubCategoryTatMapping map : mappings){
//            entityDTO.setStatus("True");
//            map.setDeleteFlag(true);
//            repo.save(map);
//            repo.delete(map);
//        }
//
//        return mappings;
//    }
//
//    public Boolean getUniqueSubCategory(Long reasoneCatId) {
//        Boolean falg=false;
//        QCase qCase=QCase.case$;
//        BooleanExpression booleanExpression=qCase.isNotNull();
//        booleanExpression=booleanExpression.and(qCase.isDelete.eq(false));
//        booleanExpression=booleanExpression.and(qCase.reasonSubCategoryId.eq(reasoneCatId));
//        List<Case>caselist= IterableUtils.toList(caseRepository.findAll(booleanExpression));
//        if(caselist.size()>0){
//            falg=true;
//        }
//        return falg;
//
//    }
}
