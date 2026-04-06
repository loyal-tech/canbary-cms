package com.adopt.apigw.modules.Branch.service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveBranchSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateBranchSharedData;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.*;

import com.adopt.apigw.modules.BranchService.model.BranchServiceMappingEntity;
import com.adopt.apigw.modules.BusinessUnit.service.BusinessUnitService;

import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.repository.common.BranchServiceAreaMappingRepository;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.mapper.BranchMapper;
import com.adopt.apigw.modules.Branch.model.BranchDTO;
import com.adopt.apigw.modules.Branch.repository.BranchRepository;
import com.adopt.apigw.utils.CommonConstants;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Service
public class BranchService extends ExBaseAbstractService<BranchDTO, Branch, Long> {

//
//    public BranchService(@Lazy BranchRepository repository, @Lazy BranchMapper mapper) {
//        super(repository, mapper);
//        sortColMap.put("id", "branchid");
//        sortColMap.put("name", "name");
//        sortColMap.put("status", "status");
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[BranchService]";
//    }
//
//    @PersistenceContext
//    EntityManager entityManager;
//
    @Autowired
    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;
//
//    @Autowired
//    private ServiceAreaRepository serviceAreaRepository;
//
    @Autowired
    BranchMapper branchMapper;
//
//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<Branch> paginationList = null;
//        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
//        if (getMvnoIdFromCurrentStaff() == 1)
//            paginationList = branchRepository.findAll(pageRequest);
//        else
//            paginationList = branchRepository.findAll(pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//        if (null != paginationList && 0 < paginationList.getContent().size()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }
//
//    @Override
//    public boolean duplicateVerifyAtSave(String name) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = branchRepository.duplicateVerifyAtSave(name);
//            else count = branchRepository.duplicateVerifyAtSave(name, mvnoIds);
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    //Update Business Unit
//    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (name != null) {
//            name = name.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = branchRepository.duplicateVerifyAtSave(name);
//            else count = branchRepository.duplicateVerifyAtSave(name, mvnoIds);
//            if (count >= 1) {
//                Integer countEdit;
//                if (getMvnoIdFromCurrentStaff() == 1) countEdit = branchRepository.duplicateVerifyAtEdit(name, id);
//                else countEdit = branchRepository.duplicateVerifyAtEdit(name, id, mvnoIds);
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
//    @Override
//    public boolean deleteVerification(Integer id) throws Exception {
//        boolean flag = false;
//        Integer count = branchRepository.deleteVerify(id);
//        if (count == 0) {
//            flag = true;
//        }
//        return flag;
//    }
//
////   @Override
//    public boolean deleteVerificationForRegion(Integer id) throws Exception {
//        boolean flag = false;
//        Integer count = branchRepository.deleteVerifyForRegion(id);
//        if (count == 0) {
//            flag = true;
//        }
//        return flag;
//    }
//
//    public PageRequest generatePageRequest(Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        if (null != sortOrder && sortOrder.equals(CommonConstants.SORT_ORDER_DESC))
//            pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).descending());
//        else pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(sortBy).ascending());
//        return pageRequest;
//    }
//
//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, "id", sortOrder);
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getBranchByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//        }
//        return null;
//    }
//
//    public GenericDataDTO getBranchByName(String name, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getBranchByName()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            Page<Branch> branchList = null;
//            QBranch qBranch = QBranch.branch;
//            BooleanExpression booleanExpression = qBranch.isNotNull()
//                    .and(qBranch.isDeleted.eq(false))
//                    .and(qBranch.name.likeIgnoreCase("%" + name + "%"))
//                    .or(qBranch.status.equalsIgnoreCase(name));
//            if (getMvnoIdFromCurrentStaff() == 1) {
//                //branchList = branchRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
//                branchList = branchRepository.findAll(booleanExpression, pageRequest);
//            } else {
//                //branchList = branchRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                booleanExpression = booleanExpression.and(qBranch.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                branchList = branchRepository.findAll(booleanExpression, pageRequest);
//            }
//            if (null != branchList && 0 < branchList.getSize()) {
//                makeGenericResponse(genericDataDTO, branchList);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
//        }
//        return null;
//    }
//
    public Branch getById(Long id) {
        return branchRepository.findById(id).get();
    }
//
//    //Get All BranchId By ServiceAreas
//    public List<Branch> getBranchByServiceArea() {
//        try {
//            QBranch qBranch = QBranch.branch;
//            QBranchServiceAreaMapping qBranchServiceAreaMapping = QBranchServiceAreaMapping.branchServiceAreaMapping;
//            JPAQuery<?> query = new JPAQuery<>(entityManager);
//            BooleanExpression aBoolean = qBranch.isNotNull().and(qBranch.isDeleted.eq(false));
//            if (getLoggedInUserId() != 1) {
//                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
//                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
//                aBoolean = aBoolean
//                        .and(qBranch.id.in(new JPAQuery[]{query.select(qBranchServiceAreaMapping.branchId)
//                                        .from(qBranchServiceAreaMapping)
//                                        .where(qBranchServiceAreaMapping.serviceareaId.in(serviceAreaIds))})
//                                .and(qBranch.mvnoId.eq(getMvnoIdFromCurrentStaff())));
//            }
//            if (getMvnoIdFromCurrentStaff() != 1) {
//                return IterableUtils.toList(branchRepository.findAll(aBoolean));
//            } else {
//                return branchRepository.findAll();
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    // Get All Service Area List By UserStaff
//    public List<ServiceArea> getAllServiceAreaByBranchId(Integer branchId) {
//        List<BranchServiceAreaMapping> branchServiceAreaMappingList = branchServiceAreaMappingRepository.findAllByBranchId(Collections.singletonList(branchId));
//        List<Long> result = new ArrayList<>();
//        for (int i = 0; i < branchServiceAreaMappingList.size(); i++) {
//            result.add(Long.valueOf(branchServiceAreaMappingList.get(i).getServiceareaId()));
//        }
//        List<ServiceArea> serviceAreaList = serviceAreaRepository.findAllByIdIn(result);
//        return serviceAreaList;
//    }
//
//    // Get All Branch List By ServiceArea
    public List<BranchDTO> getAllBranchesByServieAreaId(List<Integer> serviceAreaId) {
        //Find Branch List By Service Area Ids from BranchServiceAreaMapping
        List<BranchServiceAreaMapping> branchServiceAreaMappingList = branchServiceAreaMappingRepository.findAllByServiceareaIdIn(serviceAreaId);
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < branchServiceAreaMappingList.size(); i++) {
            result.add(branchServiceAreaMappingList.get(i).getBranchId().longValue());
        }
        List<Branch> branchList = branchRepository.findAllByIdIn(result);
        //Return active branch list
        return branchList.stream().map(branch -> branchMapper.domainToDTO(branch, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                .stream().filter(branchDTO -> branchDTO.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).collect(Collectors.toList())
                .stream().filter(branchDTO -> branchDTO.getIsDeleted().equals(false)).collect(Collectors.toList());
    }
//
//
//    public List<BranchDTO> getAllBranachesforPartnerByServiceAreaID(List<Integer> serviceAreaId) {
//
//        try {
//            //Find Branch List By Service Area Ids from BranchServiceAreaMapping
//            List<BranchServiceAreaMapping> branchServiceAreaMappingList = branchServiceAreaMappingRepository.findAllByServiceareaIdIn(serviceAreaId);
//            List<Long> branchIds = new ArrayList<>();
//            for (int i = 0; i < branchServiceAreaMappingList.size(); i++) {
//                branchIds.add(branchServiceAreaMappingList.get(i).getBranchId().longValue());
//            }
//            boolean allEqual = true;
//            if (serviceAreaId.size() > 1 && branchIds.size() > 1) {
//                for (int i = 1; i < branchIds.size(); i++) {
//                    if (!branchIds.get(i).equals(branchIds.get(0))) {
//                        allEqual = false;
//                        break;
//                    }
//                }
//            }
//            if (allEqual) {
//                List<Branch> branchList = branchRepository.findAllByStatusAndIsDeletedFalseAndIdIn("ACTIVE",branchIds);
//                return branchList.stream().map(branch -> branchMapper.domainToDTO(branch, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//            } else {
//              return new ArrayList<>();
//            }
//        }catch (Exception ex){
//            throw  ex;
//        }
//    }




    //
//
    public BranchService(@Lazy BranchRepository repository, @Lazy BranchMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "branchid");
        sortColMap.put("name", "name");
        sortColMap.put("status", "status");
    }
    //
    private static Log log = LogFactory.getLog(BusinessUnitService.class);
    @Override
    public String getModuleNameForLog() {
        return "[BranchService]";
    }

    @Autowired
    private BranchRepository branchRepository;

    @Transactional
    public void saveBranch(SaveBranchSharedDataMessage message){
        try {
            Branch branch = new Branch();
            branch.setId(message.getId());
            branch.setName(message.getName());
            branch.setBranch_code(message.getBranch_code());
            branch.setBranchServiceMappingEntityList(message.getBranchServiceMappingEntityList());
            branch.setStatus(message.getStatus());
            branch.setRevenue_sharing(message.getRevenue_sharing());
            branch.setSharing_percentage(message.getSharing_percentage());
            branch.setDunningDays(message.getDunningDays());
            branch.setServiceAreaNameList(message.getServiceAreaNameList());
            branch.setIsDeleted(message.getIsDeleted());
            branch.setMvnoId(message.getMvnoId());
            branch.setStatus(message.getStatus());
            branch.setCreatedById(message.getCreatedById());
            branch.setLastModifiedById(message.getLastModifiedById());
            branch.setCreatedByName(message.getCreatedByName());
            branch.setLastModifiedByName(message.getLastModifiedByName());
            branchRepository.save(branch);

        }catch (Exception e){
            log.error("Unable to create  Branch With name"+ message.getName()+""+e.getMessage());
        }


    }

    @Transactional
    public void updateBranch(UpdateBranchSharedData message){
        try {
            Branch branch = new Branch();
            if(message.getId()!=null) {
                branch = branchRepository.findById(message.getId()).orElse(null);
                if(branch!=null) {
                    branch.setCreatedByName(message.getCreatedByName());
                    branch.setLastModifiedByName(message.getLastModifiedByName());
                    branch.setName(message.getName());
                    branch.setBranch_code(message.getBranch_code());
                    if (!message.getBranchServiceMappingEntityList().isEmpty())
                        branch.setBranchServiceMappingEntityList(message.getBranchServiceMappingEntityList());
                    branch.setStatus(message.getStatus());
                    branch.setRevenue_sharing(message.getRevenue_sharing());
                    branch.setSharing_percentage(message.getSharing_percentage());
                    branch.setDunningDays(message.getDunningDays());
                    if (!message.getServiceAreaNameList().isEmpty()) {
                        branch.setServiceAreaNameList(message.getServiceAreaNameList());
                    }
                    branch.setIsDeleted(message.getIsDeleted());
                    branch.setMvnoId(message.getMvnoId());
                    branch.setStatus(message.getStatus());
                    branch.setCreatedById(message.getCreatedById());
                    branch.setLastModifiedById(message.getLastModifiedById());
                    branchRepository.save(branch);
                    if (branch.getIsDeleted()) {
                        List<BranchServiceAreaMapping> areaMappings=branchServiceAreaMappingRepository.findAllByBranchId(branch.getId().intValue());
                        areaMappings.stream().forEach(x-> {
                            branchServiceAreaMappingRepository.deleteById(x.getId());
                        });
                    }
                }else{
                    Branch branch1 = new Branch();
                    branch1.setId(message.getId());
                    branch1.setName(message.getName());
                    branch1.setBranch_code(message.getBranch_code());
                    branch1.setBranchServiceMappingEntityList(message.getBranchServiceMappingEntityList());
                    branch1.setStatus(message.getStatus());
                    branch1.setRevenue_sharing(message.getRevenue_sharing());
                    branch1.setSharing_percentage(message.getSharing_percentage());
                    branch1.setDunningDays(message.getDunningDays());
                    branch1.setServiceAreaNameList(message.getServiceAreaNameList());
                    branch1.setIsDeleted(message.getIsDeleted());
                    branch1.setMvnoId(message.getMvnoId());
                    branch1.setStatus(message.getStatus());
                    branch1.setCreatedById(message.getCreatedById());
                    branch1.setLastModifiedById(message.getLastModifiedById());
                    branch1.setCreatedByName(
                            message.getCreatedByName() != null ? message.getCreatedByName() : "-"
                    );
                    branch1.setLastModifiedByName(message.getLastModifiedByName());
                    branchRepository.save(branch1);
                }
            }
        }catch (Exception e){
            log.error("Unable to create  Branch "+e.getMessage());
        }
    }
}

