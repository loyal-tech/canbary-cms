package com.adopt.apigw.modules.InventoryManagement.productBundle;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardRepository;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import com.adopt.apigw.modules.InventoryManagement.item.ItemRepository;
import com.adopt.apigw.modules.InventoryManagement.item.ItemServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwner;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerRepository;
import com.adopt.apigw.utils.CommonConstants;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class BulkConsumptionServiceImp extends ExBaseAbstractService<BulkConsumptionDto, BulkConsumption, Long> {

    @Autowired
    BulkConsumptionRepository bulkConsumptionRepository;


    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    InwardRepository inwardRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    InOutWardMacRepo inOutWardMacRepo;

    @Autowired
    InOutWardMACService inOutWardMACService;

    @Autowired
    BulkConsumptionMappingRepo bulkConsumptionMappingRepo;

    @Autowired
    ProductOwnerRepository productOwnerRepository;

    @Autowired
    ItemServiceImpl itemService;


    public BulkConsumptionServiceImp(BulkConsumptionRepository bulkConsumptionRepository, IBaseMapper<BulkConsumptionDto, BulkConsumption> mapper) {
        super(bulkConsumptionRepository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "BulkConsumptionServiceImp";
    }

    @Override
    public BulkConsumptionDto saveEntity(BulkConsumptionDto bulkConsumptionDto) throws Exception {
        bulkConsumptionDto.setMvnoId(getMvnoIdFromCurrentStaff(null));      // TODO: pass mvnoID manually 6/5/2025
        Long qty = bulkConsumptionDto.getQty();
        Long productId = bulkConsumptionDto.getProductId();
        Long ownerId = bulkConsumptionDto.getOwnerId();
        String ownerType = bulkConsumptionDto.getOwnerType();
        bulkConsumptionDto.setApprovalStatus("Pending");
        BulkConsumptionDto finalBulkConsumptionDto = super.saveEntity(bulkConsumptionDto);
        List<InOutWardMACMapping> inOutWardMACMappingList = bulkConsumptionDto.getInOutWardMACMappings();
        if (inOutWardMACMappingList.size() != 0) {
            inOutWardMACMappingList.stream().forEach(r -> {
                InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(r.getId()).get();
                Item item = itemRepository.findById(r.getItemId()).get();
                item.setMacAddress(r.getMacAddress());
                item.setSerialNumber(r.getSerialNumber());
                itemRepository.save(item);
                inOutWardMACMapping.setInwardId(r.getInwardId());
                inOutWardMACMapping.setMacAddress(r.getMacAddress());
                inOutWardMACMapping.setStatus(r.getStatus());
                inOutWardMACMapping.setSerialNumber(r.getSerialNumber());
                inOutWardMACMapping.setInwardIdOfOutward(r.getInwardIdOfOutward());
                inOutWardMACMapping.setIsForwarded(r.getIsForwarded());
                inOutWardMACMapping.setIsReturned(r.getIsReturned());
                inOutWardMACMapping.setItemId(r.getItemId());
                inOutWardMACMapping.setBulkConsumptionId(finalBulkConsumptionDto.getId());
                if (r.getOutwardId() != null) {
                    inOutWardMACMapping.setOutwardId(r.getOutwardId());
                }
                if (r.getCurrentApproveId() != null) {
                    inOutWardMACMapping.setCurrentApproveId(r.getCurrentApproveId());
                }
                if (r.getPreviousApproveId() != null) {
                    inOutWardMACMapping.setPreviousApproveId(r.getPreviousApproveId());
                }
                if (r.getTeamHierarchyMappingId() != null) {
                    inOutWardMACMapping.setTeamHierarchyMappingId(r.getTeamHierarchyMappingId());
                }
                if (r.getUsedCount() != null) {
                    inOutWardMACMapping.setUsedCount(r.getUsedCount());
                }
                if (r.getRemark() != null) {
                    inOutWardMACMapping.setRemark(r.getRemark());
                }
                inOutWardMacRepo.save(inOutWardMACMapping);
            });
            }
        //update product owner table for serialized table
        if (bulkConsumptionDto.getItemType().equalsIgnoreCase("Serialized Item")) {
            ProductOwner productOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(bulkConsumptionDto.getProductId(), bulkConsumptionDto.getOwnerId(), bulkConsumptionDto.getOwnerType());
            productOwner.setQuantity(productOwner.getQuantity());
            productOwner.setUnusedQty(productOwner.getUnusedQty() - bulkConsumptionDto.getInOutWardMACMappings().size());
            productOwner.setUsedQty(productOwner.getUsedQty() + bulkConsumptionDto.getInOutWardMACMappings().size());
            productOwnerRepository.save(productOwner);
        }
        if (bulkConsumptionDto.getItemType().equalsIgnoreCase("Non Serialized Item")) {
            ProductOwner productOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(bulkConsumptionDto.getProductId(), bulkConsumptionDto.getOwnerId(), bulkConsumptionDto.getOwnerType());
            productOwner.setQuantity(productOwner.getQuantity());
            productOwner.setUnusedQty(productOwner.getUnusedQty() - bulkConsumptionDto.getQty());
            productOwner.setUsedQty(productOwner.getUsedQty() + bulkConsumptionDto.getQty());
            productOwnerRepository.save(productOwner);
        }
        return bulkConsumptionDto;
    }

    @Override
    public BulkConsumptionDto updateEntity(BulkConsumptionDto bulkConsumptionDto) throws Exception {
        bulkConsumptionDto.setMvnoId(getMvnoIdFromCurrentStaff(null));  // TODO: pass mvnoID manually 6/5/2025
        bulkConsumptionDto.setApprovalStatus("Pending");
        List<Long> mappingIds = bulkConsumptionDto.getItemListLongId();
        for (int i = 0; i < mappingIds.size(); i++) {
            InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(mappingIds.get(i)).get();
            inOutWardMACMapping.setInwardId(inOutWardMACMapping.getInwardId());
            inOutWardMACMapping.setMacAddress(inOutWardMACMapping.getMacAddress());
            inOutWardMACMapping.setStatus(inOutWardMACMapping.getStatus());
            inOutWardMACMapping.setSerialNumber(inOutWardMACMapping.getSerialNumber());
            inOutWardMACMapping.setInwardIdOfOutward(inOutWardMACMapping.getInwardIdOfOutward());
            inOutWardMACMapping.setIsForwarded(inOutWardMACMapping.getIsForwarded());
            inOutWardMACMapping.setIsReturned(inOutWardMACMapping.getIsReturned());
            inOutWardMACMapping.setItemId(inOutWardMACMapping.getItemId());
            inOutWardMACMapping.setBulkConsumptionId(bulkConsumptionDto.getId());
            if (inOutWardMACMapping.getOutwardId() != null) {
                inOutWardMACMapping.setOutwardId(inOutWardMACMapping.getOutwardId());
            }
            if (inOutWardMACMapping.getCurrentApproveId() != null) {
                inOutWardMACMapping.setCurrentApproveId(inOutWardMACMapping.getCurrentApproveId());
            }
            if (inOutWardMACMapping.getPreviousApproveId() != null) {
                inOutWardMACMapping.setPreviousApproveId(inOutWardMACMapping.getPreviousApproveId());
            }
            if (inOutWardMACMapping.getTeamHierarchyMappingId() != null) {
                inOutWardMACMapping.setTeamHierarchyMappingId(inOutWardMACMapping.getTeamHierarchyMappingId());
            }
            if (inOutWardMACMapping.getUsedCount() != null) {
                inOutWardMACMapping.setUsedCount(inOutWardMACMapping.getUsedCount());
            }
            if (inOutWardMACMapping.getRemark() != null) {
                inOutWardMACMapping.setRemark(inOutWardMACMapping.getRemark());
            }
            inOutWardMacRepo.save(inOutWardMACMapping);
        }
        return super.updateEntity(bulkConsumptionDto);
    }

    public BulkConsumptionDto saveInwardApproval(Long bulckConsumptionId, String bulckConsumptionApprovalStatus, String bulckConsumptionApprovalRemark) {
        try {
            QBulkConsumption qBulkConsumption = QBulkConsumption.bulkConsumption;
            BooleanExpression booleanExpression = qBulkConsumption.isNotNull()
                    .and(qBulkConsumption.id.eq(bulckConsumptionId))
                    .and(qBulkConsumption.approvalStatus.contains("Pending"))
                    .and(qBulkConsumption.isDeleted.eq(false));

            List<BulkConsumption> bulkConsumptionList = Lists.newArrayList(bulkConsumptionRepository.findAll(booleanExpression))
                    // TODO: pass mvnoID manually 6/5/2025
                    .stream().filter(bulkConsumption -> bulkConsumption.getMvnoId() == 1 ).collect(Collectors.toList());
            if (bulkConsumptionList.size() > 0) {
                BulkConsumptionDto bulkConsumptionDto = null;
                bulkConsumptionDto = getEntityForUpdateAndDelete(bulkConsumptionList.get(0).getId(),bulkConsumptionDto.getMvnoId());
                if(bulkConsumptionDto.getItemType().equalsIgnoreCase("Serialized Item")) {
                    if (bulckConsumptionApprovalStatus.equalsIgnoreCase("Approve")) {
                        List<BulkConsumptionMapping> bulkConsumptionMappings = bulkConsumptionMappingRepo.findByBulkConsumptionId(bulckConsumptionId);
                        bulkConsumptionMappings.forEach(bulkConsumptionMapping -> {
                            Item item = itemRepository.findById(inOutWardMacRepo.findById(bulkConsumptionMapping.getMacMappingId()).get().getItemId()).get();
                            try {
                                itemService.updateItemStatusForServiceAreaAndPop(item.getId(), CommonConstants.ALLOCATED, bulckConsumptionId, null, null, CommonConstants.ASSIGN_INVETORIES);
                            } catch (Exception e) {
                                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
                            }
                        });

                        bulkConsumptionDto.setApprovalStatus("Approve");
                        bulkConsumptionDto.setApprovalRemark(bulckConsumptionApprovalRemark);
                        return super.updateEntity(bulkConsumptionDto);
                    }
                    if (bulckConsumptionApprovalStatus.equalsIgnoreCase("Rejected")) {
                        bulkConsumptionDto.setApprovalStatus("Rejected");
                        List<InOutWardMACMapping> inOutWardMACMappings = inOutWardMacRepo.bulkConsumptionId(bulckConsumptionId);
                        inOutWardMACMappings.forEach(inOutWardMACMapping -> {

                            try {
                                itemService.updateItemStatusForServiceAreaAndPop(inOutWardMACMapping.getItemId(), CommonConstants.UNALLOCATED, bulckConsumptionId, null, null, CommonConstants.REJECT_INVETORIES);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            inOutWardMACMapping.setBulkConsumptionId(null);
                            inOutWardMacRepo.save(inOutWardMACMapping);
                        });
                        //update product owner for serelized item
                        if (bulkConsumptionDto.getItemType().equalsIgnoreCase("Serialized Item")) {
                            List<BulkConsumptionMapping> bulkConsumptionMappings1 = bulkConsumptionMappingRepo.findByBulkConsumptionId(bulkConsumptionDto.getId());
                            ProductOwner productOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(bulkConsumptionDto.getProductId(), bulkConsumptionDto.getOwnerId(), bulkConsumptionDto.getOwnerType());
                            productOwner.setQuantity(productOwner.getQuantity());
                            productOwner.setUnusedQty(productOwner.getUnusedQty() + bulkConsumptionMappings1.size());
                            productOwner.setUsedQty(productOwner.getUsedQty() - bulkConsumptionMappings1.size());
                            productOwnerRepository.save(productOwner);
                        }
                        bulkConsumptionDto.setApprovalRemark(bulckConsumptionApprovalRemark);
                        return super.updateEntity(bulkConsumptionDto);
                    }
                } else if (bulkConsumptionDto.getItemType().equalsIgnoreCase("Non Serialized Item")) {
                    if (bulckConsumptionApprovalStatus.equalsIgnoreCase("Approve")) {
                        bulkConsumptionDto.setApprovalStatus("Approve");
                        bulkConsumptionDto.setApprovalRemark(bulckConsumptionApprovalRemark);
                        return super.updateEntity(bulkConsumptionDto);
                    } else if (bulckConsumptionApprovalStatus.equalsIgnoreCase("Rejected")) {
                        bulkConsumptionDto.setApprovalStatus("Rejected");
                        bulkConsumptionDto.setApprovalRemark(bulckConsumptionApprovalRemark);
                        //update product owner for non-serilized item
                        if (bulkConsumptionDto.getItemType().equalsIgnoreCase("Non Serialized Item")) {
                            ProductOwner productOwner = productOwnerRepository.findByProductIdOwnerIdAndOwnerType(bulkConsumptionDto.getProductId(), bulkConsumptionDto.getOwnerId(), bulkConsumptionDto.getOwnerType());
                            productOwner.setQuantity(productOwner.getQuantity());
                            productOwner.setUnusedQty(productOwner.getUnusedQty() + bulkConsumptionDto.getQty());
                            productOwner.setUsedQty(productOwner.getUsedQty() - bulkConsumptionDto.getQty());
                            productOwnerRepository.save(productOwner);
                        }
                        return super.updateEntity(bulkConsumptionDto);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public BulkConsumptionDto findByBulkId(Long id) {
        try {

            BulkConsumption bulkConsumption = bulkConsumptionRepository.findById(id).get();
            Product product = productRepository.findById(bulkConsumption.getProductId()).get();
            BulkConsumptionDto bulkConsumptionDto = new BulkConsumptionDto();
            bulkConsumptionDto.setBulkConsumptionName(bulkConsumption.getBulkConsumptionName());
            bulkConsumptionDto.setProductName(product.getName());
            bulkConsumptionDto.setQty(bulkConsumption.getQty());
            bulkConsumptionDto.setApprovalStatus(bulkConsumption.getApprovalStatus());
            bulkConsumptionDto.setItemType(bulkConsumption.getItemType());
            List<BulkConsumptionMapping> bulkConsumptionMapping=bulkConsumptionMappingRepo.findByBulkConsumptionId(id);
            List<Long> idLongList=bulkConsumptionMapping.stream().map(BulkConsumptionMapping::getMacMappingId).collect(Collectors.toList());
            List<InOutWardMACMapping> inOutWardMACMappings=inOutWardMacRepo.findAllById(idLongList);
            bulkConsumptionDto.setInOutWardMACMappings(inOutWardMACMappings);
            return bulkConsumptionDto;

        } catch (CustomValidationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) {
                count = bulkConsumptionRepository.duplicateVerifyAtSave(name);
            } else {
                count = bulkConsumptionRepository.duplicateVerifyAtSave(name, mvnoIds);
            }

            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = bulkConsumptionRepository.duplicateVerifyAtSave(name);
            else count = bulkConsumptionRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) == 1)
                    countEdit = bulkConsumptionRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id));
                else countEdit = bulkConsumptionRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id), mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }


    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = bulkConsumptionRepository.deleteVerify(id);
        if (count == 1) {
            flag = true;
        }
        return flag;
    }


    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [ search()] ";
        try {
            PageRequest pageRequest1 = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim() != null) {
                        return getBulkConusmptionByName(searchModel.getFilterValue(), pageRequest1);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }


    public GenericDataDTO getBulkConusmptionByName(String s1, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QBulkConsumption qBulkConsumption = QBulkConsumption.bulkConsumption;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression booleanExpression = qBulkConsumption.isNotNull()
                    .and(qBulkConsumption.isDeleted.eq(false))
                    .and(qBulkConsumption.bulkConsumptionName.likeIgnoreCase("%" + s1 + "%"));

            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression
                        .and(qBulkConsumption.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            }
            Page<BulkConsumption> bulkConsumptions = bulkConsumptionRepository.findAll(booleanExpression, pageRequest);
            if (null != bulkConsumptions && 0 < bulkConsumptions.getSize()) {
                makeGenericResponse(genericDataDTO, bulkConsumptions);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }


    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + "[getListByPageAndSizeAndSortByAndOrderBy()]";
        QBulkConsumption qBulkConsumption = QBulkConsumption.bulkConsumption;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        BooleanExpression booleanExpression = qBulkConsumption.isNotNull().and(qBulkConsumption.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<BulkConsumption> paginationList = null;
        //PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
        PageRequest pageRequest = super.generatePageRequest(page, size, "createdate", sortOrder);
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression
                        .and(qBulkConsumption.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            }
            //paginationList = bulkConsumptionRepository.findAll(booleanExpression, pageRequest);
//            if (paginationList.getSize()>0) {
            return makeGenericResponse(genericDataDTO, bulkConsumptionRepository.findAll(booleanExpression, pageRequest));
            //}
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
//        return null;
    }

    @Override
    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<BulkConsumption> paginationList) {

        List<BulkConsumptionDto> list = paginationList.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        List<BulkConsumptionDto> productList = new ArrayList<>();
        productList = list.stream().filter(bulkConsumptionDto -> bulkConsumptionDto.getProductId() != null).collect(Collectors.toList());
        productList.forEach(bulkConsumptionDto -> bulkConsumptionDto.setProductName(productRepository.findById(bulkConsumptionDto.getProductId()).get().getName()));
        list = productList;
        genericDataDTO.setDataList(list);
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }


    public BulkConsumptionDto deleteBulkConsumption(BulkConsumptionDto bulkConsumptionDto) throws Exception {
        List<Long> mappingIds = bulkConsumptionDto.getItemListLongId();
        for (int i = 0; i < mappingIds.size(); i++) {
            InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(mappingIds.get(i)).get();
            inOutWardMACMapping.setInwardId(inOutWardMACMapping.getInwardId());
            inOutWardMACMapping.setMacAddress(inOutWardMACMapping.getMacAddress());
            inOutWardMACMapping.setStatus(inOutWardMACMapping.getStatus());
            inOutWardMACMapping.setSerialNumber(inOutWardMACMapping.getSerialNumber());
            inOutWardMACMapping.setInwardIdOfOutward(inOutWardMACMapping.getInwardIdOfOutward());
            inOutWardMACMapping.setIsForwarded(inOutWardMACMapping.getIsForwarded());
            inOutWardMACMapping.setIsReturned(inOutWardMACMapping.getIsReturned());
            inOutWardMACMapping.setItemId(inOutWardMACMapping.getItemId());
            if (inOutWardMACMapping.getOutwardId() != null) {
                inOutWardMACMapping.setOutwardId(inOutWardMACMapping.getOutwardId());
            }
            if (inOutWardMACMapping.getCurrentApproveId() != null) {
                inOutWardMACMapping.setCurrentApproveId(inOutWardMACMapping.getCurrentApproveId());
            }
            if (inOutWardMACMapping.getPreviousApproveId() != null) {
                inOutWardMACMapping.setPreviousApproveId(inOutWardMACMapping.getPreviousApproveId());
            }
            if (inOutWardMACMapping.getTeamHierarchyMappingId() != null) {
                inOutWardMACMapping.setTeamHierarchyMappingId(inOutWardMACMapping.getTeamHierarchyMappingId());
            }
            if (inOutWardMACMapping.getUsedCount() != null) {
                inOutWardMACMapping.setUsedCount(inOutWardMACMapping.getUsedCount());
            }
            if (inOutWardMACMapping.getRemark() != null) {
                inOutWardMACMapping.setRemark(inOutWardMACMapping.getRemark());
            }
            inOutWardMACMapping.setBulkConsumptionId(null);
            inOutWardMacRepo.save(inOutWardMACMapping);
        }
        return bulkConsumptionDto;
    }

    public void validateBulkConsumption(BulkConsumptionDto entityDto) {
        Product product = productRepository.findById(entityDto.getProductId()).get();
        boolean hasSerial = product.getProductCategory().isHasSerial();
        if(entityDto.getItemType().equalsIgnoreCase("Serialized Item")) {
            if(entityDto.getQty() != null) {
                List<InOutWardMACMapping> list = entityDto.getInOutWardMACMappings();
                if (list.size() == 0) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please select atleast one serialized item...", null);
                } else {
                    if(hasSerial) {
                        for (InOutWardMACMapping inOutWardMACMapping : list) {
                            if (inOutWardMACMapping.getSerialNumber() == null || inOutWardMACMapping.getSerialNumber().equals("")) {
                                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter serial number in selected items..", null);
                            }
                        }
                    }
                }
            }
        } else if (entityDto.getItemType().equalsIgnoreCase("Non Serialized Item")) {
            if(entityDto.getQty() == null || entityDto.getQty() == 0) {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter more than 0 quantity", null);
            }
        }
    }
}


