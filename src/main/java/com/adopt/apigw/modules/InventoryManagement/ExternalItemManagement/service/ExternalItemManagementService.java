package com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.service;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.postpaid.CustomerMapper;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain.ExternalItemManagement;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain.QExternalItemManagement;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.mapper.ExternalItemManagementMapper;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.model.ExternalItemManagementDTO;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.repository.ExternalItemManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.repository.PopManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.RequestInventory.CommonResponceDto;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardDto;
import com.adopt.apigw.modules.InventoryManagement.inward.QInward;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerDto;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerService;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.PartnerRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.CommonConstants;
 import com.google.common.collect.Lists;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExternalItemManagementService extends ExBaseAbstractService<ExternalItemManagementDTO, ExternalItemManagement, Long> {

    public ExternalItemManagementService(ExternalItemManagementRepository repository, ExternalItemManagementMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ExternalItemManagementService]";
    }

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    public ExternalItemManagementRepository externalItemManagementRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    ExternalItemManagementMapper externalItemManagementMapper;

    @Autowired
    CustomersRepository customersRepository;
    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    CustomerMapper customerMapper;

    //Get External Item Group Details By Product and ServiceArea Id
    public List<ExternalItemManagement> getExtrenalItemDetailsByProductAndServiceAreaId(Long productId, Long serviceAreaId) {
        QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
        BooleanExpression booleanExpression = qExternalItemManagement.isNotNull().and(qExternalItemManagement.productId.id.eq(productId))
                .and(qExternalItemManagement.status.eq(CommonConstants.ACTIVE_STATUS))
                .and(qExternalItemManagement.serviceAreaId.id.eq(serviceAreaId))
                .and(qExternalItemManagement.isDeleted.eq(false));
        // TODO: pass mvnoID manually 6/5/2025
        return Lists.newArrayList(externalItemManagementRepository.findAll(booleanExpression))
                .stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
    }

    //Get List By Page And Size And SortBy And OrderBy
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
        List<Long> resultPaginationList = new ArrayList<>();
        Page<ExternalItemManagement> finalPaginationList = null;
        String inwardNumber = null;
        QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
        BooleanExpression booleanExpression = qExternalItemManagement.isNotNull().and(qExternalItemManagement.isDeleted.eq(false));
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                // Common method for find Service Area List Based on StaffId with Long
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Long> serviceAreaIds = serviceAreaService.getServiceAreaByStaffIdLong();
                List<ExternalItemManagement> inwardServiceAreaStaffList = externalItemManagementRepository.findAllByServiceAreaIdIdInAndIsDeletedIsFalseAndMvnoIdIn(serviceAreaIds,Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                if (inwardServiceAreaStaffList != null) {
                    if (inwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < inwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(inwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }
                finalPaginationList = externalItemManagementRepository.findAllByIdIn(resultPaginationList, pageRequest);
            } else {
                finalPaginationList = externalItemManagementRepository.findAll(booleanExpression, pageRequest);
            }
            if (finalPaginationList != null && finalPaginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, finalPaginationList);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    //Save
    @Transactional
    public ExternalItemManagementDTO saveEntity(ExternalItemManagementDTO entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        ExternalItemManagementDTO externalItemManagementDTO = null;
        try {
            entity.setExternalItemGroupNumber(getRandomenumber("EX","-",""));
            //entityDTO.unusedQty=entityDTO.getQty();
            entity.setQty(0L);
            entity.setUnusedQty(0L);
            entity.setInTransitQty(entity.getInTransitQty());
            entity.setUsedQty(0L);
            entity.setRejectedQty(0L);
            entity.setApprovalStatus("Pending");
            entity.setTotalMacSerial(0L);
            externalItemManagementDTO = super.saveEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return externalItemManagementDTO;
    }

    //Get All External Item Group By Product And Staff
    public List<ExternalItemManagement> getAllExternalItemByProductAndStaff(Long productId, Long ownerId) {
        QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
        JPAQuery<ExternalItemManagement> query = new JPAQuery<>(entityManager);
        Long partnerId = Long.valueOf(getLoggedInUser().getPartnerId());
        List<ExternalItemManagement> externalItemManagementList = new ArrayList<>();
        BooleanExpression booleanExpression = qExternalItemManagement.isNotNull().
                and(qExternalItemManagement.productId.id.eq(productId))
                .and(qExternalItemManagement.isDeleted.eq(false))
                .and(qExternalItemManagement.ownerId.in(ownerId, partnerId))
                .and(qExternalItemManagement.approvalStatus.contains("Approve"));
        List<Tuple> result = query.select(qExternalItemManagement.id, qExternalItemManagement.externalItemGroupNumber, qExternalItemManagement.unusedQty, qExternalItemManagement.mvnoId).from(qExternalItemManagement).where(booleanExpression).fetch();
        if (!result.isEmpty()) {
            result.forEach(tuple -> {
                ExternalItemManagement externalItemManagement = new ExternalItemManagement();
                externalItemManagement.setId(tuple.get(qExternalItemManagement.id));
                externalItemManagement.setExternalItemGroupNumber(tuple.get(qExternalItemManagement.externalItemGroupNumber));
                externalItemManagement.setUnusedQty(tuple.get(qExternalItemManagement.unusedQty));
                externalItemManagement.setMvnoId(tuple.get(qExternalItemManagement.mvnoId));
                externalItemManagementList.add(externalItemManagement);
            });
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return externalItemManagementList;
        else
            // TODO: pass mvnoID manually 6/5/2025
            return externalItemManagementList.stream().filter(externalItemManagement -> externalItemManagement.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || externalItemManagement.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
    }

    //Update
    @Transactional
    public ExternalItemManagementDTO updateEntity(ExternalItemManagementDTO entity) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [updateEntity()] ";
        ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(entity.getId()).get();
        ExternalItemManagementDTO externalItemManagementDTO = null;
        try {
            if (externalItemManagement.getApprovalStatus().equalsIgnoreCase("Pending")) {
                entity.setQty(0L);
                entity.setUnusedQty(0L);
                entity.setInTransitQty(entity.getInTransitQty());
                entity.setUsedQty(0L);
                entity.setRejectedQty(0L);
                entity.setApprovalStatus("Pending");
                if (entity.getTotalMacSerial() == externalItemManagement.getTotalMacSerial()) {
                    entity.setTotalMacSerial(externalItemManagement.getTotalMacSerial());
                }
                if (entity.getTotalMacSerial() != externalItemManagement.getTotalMacSerial()) {
                    entity.setTotalMacSerial(entity.getTotalMacSerial());
                }
            }
            externalItemManagementDTO = super.updateEntity(entity);
            //externalItemManagementDTO = super.saveEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return externalItemManagementDTO;
    }

    //Delete Verification
//    @Override
//    public boolean deleteVerification(Integer id) throws Exception {
//        boolean flag = false;
//        Integer count = externalItemManagementRepository.deleteVerify(id);
//        if (count == 0) {
//            flag = true;
//        }
//        return flag;
//    }

    public ExternalItemManagementDTO saveExternalItemGroupApproval(Long externalItemId, String externalItemGroupApprovalStatus, String approvalRemark) {
        try {
            QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
            BooleanExpression booleanExpression = qExternalItemManagement.isNotNull()
                    .and(qExternalItemManagement.id.eq(externalItemId))
                    .and(qExternalItemManagement.unusedQty.eq(0L))
                    .and(qExternalItemManagement.qty.eq(0L))
                    .and(qExternalItemManagement.usedQty.eq(0L))
                    .and(qExternalItemManagement.rejectedQty.eq(0L))
                    .and(qExternalItemManagement.status.eq(CommonConstants.ACTIVE_STATUS))
                    .and(qExternalItemManagement.approvalStatus.contains("Pending"))
                    .and(qExternalItemManagement.isDeleted.eq(false));

            // TODO: pass mvnoID manually 6/5/2025
            List<ExternalItemManagement> externalItemManagementList = Lists.newArrayList(externalItemManagementRepository.findAll(booleanExpression))
                    .stream().filter(externalItemManagement -> externalItemManagement.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || externalItemManagement.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
            return updateExternalItemGroup(externalItemManagementList, externalItemGroupApprovalStatus, approvalRemark);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Update External Item Group Approval Status
    public ExternalItemManagementDTO updateExternalItemGroup(List<ExternalItemManagement> externalItemManagementList, String externalItemGroupApprovalStatus, String approval_remark) {
        try {
            if (externalItemManagementList != null) {
                if (externalItemManagementList.size() > 0) {
                    Long inTransitQty = externalItemManagementList.get(0).getInTransitQty();
                    //InwardDto inwardDto = null;
                    ExternalItemManagementDTO externalItemManagementDTO = null;
                    externalItemManagementDTO = getEntityForUpdateAndDelete(externalItemManagementList.get(0).getId(),externalItemManagementList.get(0).getMvnoId());
                    if (externalItemGroupApprovalStatus.equalsIgnoreCase("Approve")) {
                        externalItemManagementDTO.setQty(inTransitQty);
                        externalItemManagementDTO.setUnusedQty(inTransitQty);
                        externalItemManagementDTO.setUsedQty(0L);
                        externalItemManagementDTO.setInTransitQty(0L);
                        externalItemManagementDTO.setRejectedQty(0L);
                        externalItemManagementDTO.setApprovalStatus("Approve");
                        externalItemManagementDTO.setApprovalRemark(approval_remark);
                        super.updateEntity(externalItemManagementDTO);
                        return externalItemManagementDTO;
                    } else if (externalItemGroupApprovalStatus.equalsIgnoreCase("Rejected")) {
                        externalItemManagementDTO.setQty(0L);
                        externalItemManagementDTO.setUnusedQty(0L);
                        externalItemManagementDTO.setUsedQty(0L);
                        externalItemManagementDTO.setInTransitQty(0L);
                        externalItemManagementDTO.setRejectedQty(inTransitQty);
                        externalItemManagementDTO.setApprovalStatus("Rejected");
                        externalItemManagementDTO.setApprovalRemark(approval_remark);
                        super.updateEntity(externalItemManagementDTO);
                        return externalItemManagementDTO;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    //Search
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getInwardList(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getInwardList(String externalItemGroupNumber, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getInwardList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<ExternalItemManagement> finalPaginationList = null;
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            List<Long> resultPaginationList = new ArrayList<>();
            // Common method for find Service Area List Based on StaffId With Long
            ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
            List<Long> serviceAreaIds = serviceAreaService.getServiceAreaByStaffIdLong();
            // TODO: pass mvnoID manually 6/5/2025
            List<ExternalItemManagement> inwardServiceAreaStaffList = externalItemManagementRepository.findAllByexternalItemGroupNumberContainingIgnoreCaseAndServiceAreaIdIdInAndIsDeletedIsFalseAndMvnoIdIn(externalItemGroupNumber,serviceAreaIds,Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (inwardServiceAreaStaffList != null) {
                if (inwardServiceAreaStaffList.size() > 0) {
                    for (int s = 0; s < inwardServiceAreaStaffList.size(); s++) {
                        resultPaginationList.add(inwardServiceAreaStaffList.get(s).getId());
                    }
                }
            }finalPaginationList = externalItemManagementRepository.findAllByIdIn(resultPaginationList, pageRequest);
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1) {
            finalPaginationList = externalItemManagementRepository.findAllByexternalItemGroupNumberContainingIgnoreCaseAndIsDeletedIsFalse(externalItemGroupNumber, pageRequest);
        }
        if (finalPaginationList != null && finalPaginationList.getSize() > 0) {
            makeGenericResponse(genericDataDTO, finalPaginationList);
        }
        return genericDataDTO;
    }

    public String getRandomenumber(String flag1, String flag2, String flag3) {
        String flag = "";
        if (flag1 != null) {
            flag += flag1;
        }
        if (flag2 != null) {
            flag += flag2;
        }
        if (flag3 != null) {
             ExternalItemManagement externalItemManagement= externalItemManagementRepository.findTopByOrderByIdDesc();
            if(externalItemManagement==null){
                flag+=1;
            }
            else{
                flag +=  externalItemManagement.getId()+1;
            }
        }
        return flag;
    }

    public List<ExternalItemManagementDTO> getAllExtenralItemBaseOnStatus(Long ownerId,String ownershipType){
            List<ExternalItemManagementDTO> externalItemManagementDTOS=null;
             try {
                QExternalItemManagement qExternalItemManagement = QExternalItemManagement.externalItemManagement;
                BooleanExpression booleanExpression = qExternalItemManagement.isNotNull().and(qExternalItemManagement.ownerId.eq(ownerId).and(qExternalItemManagement.ownershipType.equalsIgnoreCase(ownershipType)));
                List<ExternalItemManagement> externalItemManagementList = (List<ExternalItemManagement>) externalItemManagementRepository.findAll(booleanExpression);
                 externalItemManagementDTOS=externalItemManagementList.stream().map(externalItemManagement -> externalItemManagementMapper.domainToDTO(externalItemManagement, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            }
            catch (Exception exception){
                throw new RuntimeException(exception.getMessage());
            }
        return externalItemManagementDTOS;
    }


   public List<CommonResponceDto> getAllCustomerBasedOnServiceArea(List<Long> serviceAreaIds) {
        Map<Integer, String> map=new HashedMap();
            QCustomers qCustomers = QCustomers.customers;
            BooleanExpression booleanExpression = qCustomers.isNotNull();
            booleanExpression = booleanExpression.and(qCustomers.status.eq(CommonConstants.ACTIVE_STATUS)).and(qCustomers.isDeleted.eq(false)).and(qCustomers.servicearea.id.in(serviceAreaIds));
        List<Customers> customersList = (List<Customers>) customersRepository.findAll(booleanExpression);
             List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
             customersList.stream().forEach(r->{
                 CommonResponceDto commonResponceDto = new CommonResponceDto();
                 commonResponceDto.setId(r.getId().longValue());
                 commonResponceDto.setName(r.getUsername());
                 commonResponceDtos.add(commonResponceDto);
            });
             return commonResponceDtos;
    }

    @Override
    public ExternalItemManagementDTO getEntityById(Long id,Integer mvnoId) {
        ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(id).get();
        ExternalItemManagementDTO externalItemManagementDTO = getMapper().domainToDTO(externalItemManagement, new CycleAvoidingMappingContext());
        if(externalItemManagementDTO.getOwnershipType().equals("") || externalItemManagementDTO.getOwnershipType() == null) {
            externalItemManagementDTO.setOwnerName("");
        } else if(externalItemManagementDTO.getOwnershipType().equalsIgnoreCase("Customer Owned")) {
            externalItemManagementDTO.setOwnerName(customersRepository.getOne(Math.toIntExact(externalItemManagementDTO.getOwnerId())).getFirstname());
        } else if (externalItemManagementDTO.getOwnershipType().equalsIgnoreCase("Partner Owned")) {
            externalItemManagementDTO.setOwnerName(partnerRepository.getOne(Math.toIntExact(externalItemManagementDTO.getOwnerId())).getName());
        }
        return externalItemManagementDTO;
    }
}
