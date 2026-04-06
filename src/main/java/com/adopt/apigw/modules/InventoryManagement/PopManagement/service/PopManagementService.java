package com.adopt.apigw.modules.InventoryManagement.PopManagement.service;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.PopManagement;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.QPopManagement;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.QPopServiceAreaMapping;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.mapper.PopManagementMapper;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.model.PopManagementDTO;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.repository.PopManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.InventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.InventoryMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.QInventoryMapping;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PopManagementService extends ExBaseAbstractService<PopManagementDTO, PopManagement, Long> {

    public PopManagementService(PopManagementRepository repository, PopManagementMapper mapper) {
        super(repository, mapper);
    }

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private ServiceAreaMapper serviceAreaMapper;
    @Override
    public String getModuleNameForLog() {
        return "[PopManagementService]";
    }

    @Autowired
    public PopManagementRepository popManagementRepository;

    @Autowired
    public ServiceAreaRepository serviceAreaRepository;

    @Autowired
    public InventoryMappingRepo inventoryMappingRepo;

    @Autowired
    PopManagementMapper popManagementMapper;

    //Check Duplicate Pop Name at Save
    @Override
    public boolean duplicateVerifyAtSave(String popname) throws Exception{
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (popname != null) {
            popname = popname.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = popManagementRepository.duplicateVerifyAtSave(popname);
            else count = popManagementRepository.duplicateVerifyAtSave(popname, mvnoIds);
            if (count == 0){
                flag = true;
            }
        }
        return flag;
    }

    //Search Pop Management
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId){
        String SUBMODULE = getModuleNameForLog() + " [ search()] ";
        try{
            PageRequest pageRequest1 = generatePageRequest(page,pageSize, sortBy, sortOrder);
            if (null != filterList && 0< filterList.size()){
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getPopManagementByName(searchModel.getFilterValue(), pageRequest1);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getPopManagementByName(String s1, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QPopManagement qPopManagement = QPopManagement.popManagement;
            QPopServiceAreaMapping qPopServiceAreaMapping =QPopServiceAreaMapping.popServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression booleanExpression = qPopManagement.isNotNull()
                    .and(qPopManagement.isDeleted.eq(false))
                    .and(qPopManagement.popName.likeIgnoreCase("%" + s1 + "%"));
            // Common method for find Service Area List Based on StaffId
            ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
            List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
            if(getMvnoIdFromCurrentStaff(null) != 1) { // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression
                            .and(qPopManagement.id.in(query.select(qPopServiceAreaMapping.popId)
                            .from(qPopServiceAreaMapping).where((qPopServiceAreaMapping.serviceId.in(serviceAreaIds))
                                            .and(qPopManagement.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1))))); // TODO: pass mvnoID manually 6/5/2025
            }
            Page<PopManagement> popManagements = popManagementRepository.findAll(booleanExpression, pageRequest);
            if (null != popManagements && 0 < popManagements.getSize()) {
                makeGenericResponse(genericDataDTO, popManagements);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    //Get List By Page and Size and Sort By and Order By
    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + "[getListByPageAndSizeAndSortByAndOrderBy()]";
        QPopManagement qPopManagement = QPopManagement.popManagement;
        QPopServiceAreaMapping qPopServiceAreaMapping =QPopServiceAreaMapping.popServiceAreaMapping;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        BooleanExpression booleanExpression = qPopManagement.isNotNull().and(qPopManagement.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<PopManagement> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
        try {
            // Common method for find Service Area List Based on StaffId
            ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
            List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
            if(serviceAreaIds.size() != 0) {
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != 1) {
                    booleanExpression = booleanExpression
                            .and(qPopManagement.id.in(query.select(qPopServiceAreaMapping.popId)
                                    .from(qPopServiceAreaMapping).where((qPopServiceAreaMapping.serviceId.in(serviceAreaIds))
                                            .and(qPopManagement.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1))))); // TODO: pass mvnoID manually 6/5/2025
                }
            } else {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qPopManagement.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            }
            paginationList = popManagementRepository.findAll(booleanExpression, pageRequest);
            if (paginationList.getSize()>0) {
                makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    //Update Pop Management
    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if(getMvnoIdFromCurrentStaff(null) == 1) count = popManagementRepository.duplicateVerifyAtSave(name);
            else count = popManagementRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null) == 1) countEdit = popManagementRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id));
                else countEdit = popManagementRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id), mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    //Delete Pop Management
    @Override
    public boolean deleteVerification(Integer id)throws Exception{
        boolean flag = false;
        Integer count = popManagementRepository.deleteVerify(id);
        if(count==1){
            flag=true;
        }
        return flag;
    }

    //Save Pop Entity
    @Override
    public PopManagementDTO saveEntity(PopManagementDTO popManagementDTO) throws Exception{
        popManagementDTO.setMvnoId(getMvnoIdFromCurrentStaff(null)); // TODO: pass mvnoID manually 6/5/2025
        if (popManagementDTO.getServiceAreaIdsList() != null){
            PopManagement popManagement = new PopManagement();
            popManagement.setServiceAreaNameList(serviceAreaRepository.findAllById(popManagementDTO.getServiceAreaIdsList()));
            //popManagementDTO.setServiceAreaNameList(popManagement.getServiceAreaNameList());
            if(popManagementDTO.getServiceAreaIdsList() != null && popManagementDTO.getServiceAreaIdsList().size() > 0) {
                List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(popManagementDTO.getServiceAreaIdsList());
                for (int i=0; i<popManagementDTO.getServiceAreaIdsList().size() ; i++) {
                    serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                }
                popManagementDTO.setServiceAreaNameList(serviceAreaDTOS);
            }
        }
        return super.saveEntity(popManagementDTO);
    }

    //Update Pop Entity
    @Override
    public PopManagementDTO updateEntity(PopManagementDTO popManagementDTO) throws Exception {
        getEntityForUpdateAndDelete(popManagementDTO.getId(),popManagementDTO.getMvnoId());
        popManagementDTO.setMvnoId(getMvnoIdFromCurrentStaff(null)); // TODO: pass mvnoID manually 6/5/2025
        if(popManagementDTO.getServiceAreaIdsList()!=null) {
            PopManagement popManagement = new PopManagement();
            popManagement.setServiceAreaNameList(serviceAreaRepository.findAllById(popManagementDTO.getServiceAreaIdsList()));
            //popManagementDTO.setServiceAreaNameList(popManagement.getServiceAreaNameList());
            if(popManagementDTO.getServiceAreaIdsList() != null && popManagementDTO.getServiceAreaIdsList().size() > 0) {
                List<ServiceAreaDTO> serviceAreaDTOS = new ArrayList<>();
                List<ServiceArea> serviceArea = serviceAreaRepository.findAllById(popManagementDTO.getServiceAreaIdsList());
                for (int i=0; i<popManagementDTO.getServiceAreaIdsList().size() ; i++) {
                    serviceAreaDTOS.add(serviceAreaMapper.domainToDTO(serviceArea.get(i), new CycleAvoidingMappingContext()));
                }
                popManagementDTO.setServiceAreaNameList(serviceAreaDTOS);
            }
        }
        return super.updateEntity(popManagementDTO);
    }

    //Get all Pop Entity
    @Override
    public List<PopManagementDTO> getAllEntities(Integer mvnoId){
        try{
            QPopManagement qPopManagement = QPopManagement.popManagement;
            QPopServiceAreaMapping qPopServiceAreaMapping = QPopServiceAreaMapping.popServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression booleanExpression = qPopManagement.isNotNull().and(qPopManagement.isDeleted.eq(false));
            if (getLoggedInUserId() != 1){
                // Common method for find Service Area List Based on StaffId
                ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
                List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
                //List<Integer> serviceAreaIds = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                booleanExpression = booleanExpression
                        .and(qPopManagement.id.in(query.select(qPopServiceAreaMapping.popId)
                                .from(qPopServiceAreaMapping).where(qPopServiceAreaMapping.serviceId.in(serviceAreaIds)))
                                .and(qPopManagement.mvnoId.eq(getMvnoIdFromCurrentStaff(null))));   // TODO: pass mvnoID manually 6/5/2025
            }
            List<PopManagement> serviceAreas = IterableUtils.toList(popManagementRepository.findAll(booleanExpression));
            // TODO: pass mvnoID manually 6/5/2025
            return serviceAreas.stream().map(data ->super.getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())
                    .stream().filter(popManagementDTO -> popManagementDTO.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || popManagementDTO.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
        } catch (Exception ex){
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<PopManagementDTO>getAllWithoutPagination(){
        try{
            QPopManagement qPopManagement=QPopManagement.popManagement;
            BooleanExpression booleanExpression=qPopManagement.isNotNull();
            booleanExpression=booleanExpression.and(qPopManagement.status.eq(CommonConstants.ACTIVE_STATUS).and(qPopManagement.isDeleted.eq(false)));
            List<PopManagement> popManagementList= (List<PopManagement>) popManagementRepository.findAll(booleanExpression);
            return popManagementMapper.domainToDTO(popManagementList,new CycleAvoidingMappingContext());

        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }


    }

    //Validate Inventory Assign to POP at Delete
    public void validatePOP(PopManagementDTO entityDto) {
        QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
        BooleanExpression booleanExpression = qInventoryMapping.isDeleted.eq(false).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Pending").or(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve"))).and(qInventoryMapping.ownerId.eq(entityDto.getId())).and(qInventoryMapping.ownerType.equalsIgnoreCase(CommonConstants.POP));
        List<InventoryMapping> inventoryMappings = IterableUtils.toList(inventoryMappingRepo.findAll(booleanExpression));
        if (inventoryMappings.size() != 0) {
           throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Do not delete pop due to inventory assigned to pop", null);
        }
    }
}
