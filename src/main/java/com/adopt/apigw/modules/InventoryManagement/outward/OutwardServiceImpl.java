package com.adopt.apigw.modules.InventoryManagement.outward;

import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.QStaffUser;
import com.adopt.apigw.model.common.QStaffUserServiceAreaMapping;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.PopManagement;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.QPopManagement;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.QPopServiceAreaMapping;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.repository.PopManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.RequestInventory.RequestInvenotryProductMapping;
import com.adopt.apigw.modules.InventoryManagement.RequestInventory.RequestInventoryProductMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.inward.*;
import com.adopt.apigw.modules.InventoryManagement.item.ItemRepository;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.product.ProductServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerDto;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerMapper;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerService;
import com.adopt.apigw.modules.InventoryManagement.warehouse.QWareHouse;
import com.adopt.apigw.modules.InventoryManagement.warehouse.QWareHouseServiceAreaMapping;
import com.adopt.apigw.modules.InventoryManagement.warehouse.WareHouse;
import com.adopt.apigw.modules.InventoryManagement.warehouse.WarehouseManagementRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.PartnerRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OutwardServiceImpl extends ExBaseAbstractService<OutwardDto, Outward, Long> {
    @Autowired
    private OutwardRepository outwardRepository;

    @Autowired
    private InwardServiceImpl inwardService;

    @Autowired
    private OutwardMapper outwardMapper;
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private ProductOwnerService productOwnerService;

    @Autowired
    private ProductOwnerMapper productOwnerMapper;

    @Autowired
    private InwardRepository inwardRepository;

    @Autowired
    private WarehouseManagementRepository warehouseManagementRepository;

    @Autowired
    private PopManagementRepository popManagementRepository;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private InOutWardMacRepo inOutWardMacRepo;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private CustomersService customersService;

    @Autowired
    private RequestInventoryProductMappingRepo requestInventoryProductMappingRepo;

    public OutwardServiceImpl(OutwardRepository outwardRepository, OutwardMapper outwardMapper) {
        super(outwardRepository, outwardMapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[OutwardServiceImpl]";
    }

    public List<Outward> getAllOutwardByProductAndStaff(Long productId, Long staffId) {
        QOutward qOutward = QOutward.outward;
        JPAQuery<Outward> query = new JPAQuery<>(entityManager);
        List<Outward> outwardList = new ArrayList<>();
        BooleanExpression booleanExpression = qOutward.isNotNull().and(qOutward.productId.id.eq(productId)).and(qOutward.destinationType.equalsIgnoreCase(CommonConstants.STAFF)).and(qOutward.destinationId.eq(staffId).and(qOutward.isDeleted.eq(false)));
        List<Tuple> result = query.select(qOutward.id, qOutward.outwardNumber, qOutward.unusedQty, qOutward.mvnoId).from(qOutward).where(booleanExpression).fetch();
        if (!result.isEmpty()) {
            result.forEach(tuple -> {
                Outward outward = new Outward();
                outward.setId(tuple.get(qOutward.id));
                outward.setOutwardNumber(tuple.get(qOutward.outwardNumber));
                outward.setUnusedQty(tuple.get(qOutward.unusedQty));
                outward.setMvnoId(tuple.get(qOutward.mvnoId));
                outwardList.add(outward);
            });
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return outwardList;
        else
            // TODO: pass mvnoID manually 6/5/2025
            return outwardList.stream().filter(outward -> outward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || outward.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
    }


    public List<Outward> getByStaffId(Long staffId) {
        QOutward qOutward = QOutward.outward;
        JPAQuery<Outward> query = new JPAQuery<>(entityManager);
        List<Outward> outwardList = new ArrayList<>();
        BooleanExpression booleanExpression = qOutward.isNotNull().and(qOutward.destinationType.eq(CommonConstants.STAFF)).and(qOutward.destinationId.eq(staffId).and(qOutward.isDeleted.eq(false)));
        List<Tuple> result = query.select(qOutward.id, qOutward.outwardNumber, qOutward.productId.name, qOutward.sourceType, qOutward.sourceId, qOutward.inwardId.inwardNumber, qOutward.outwardDateTime, qOutward.qty, qOutward.usedQty, qOutward.unusedQty, qOutward.productId.productCategory.unit).from(qOutward).where(booleanExpression).fetch();
        if (!result.isEmpty()) {
            result.forEach(tuple -> {
                Outward outward = new Outward();
                outward.setId(tuple.get(qOutward.id));
                outward.setOutwardNumber(tuple.get(qOutward.outwardNumber));
                outward.setProductName(tuple.get(qOutward.productId.name));
                outward.setSourceType(tuple.get(qOutward.sourceType));
                outward.setSourceId(tuple.get(qOutward.sourceId));
                outward.setInwardNumber(tuple.get(qOutward.inwardId.inwardNumber));
                outward.setOutwardDateTime(tuple.get(qOutward.outwardDateTime));
                outward.setQty(tuple.get(qOutward.qty));
                outward.setUsedQty(tuple.get(qOutward.usedQty));
                outward.setUnusedQty(tuple.get(qOutward.unusedQty));
                outward.setUnit(tuple.get(qOutward.productId.productCategory.unit));
                outwardList.add(outward);
            });
        }
        return outwardList;
    }

    public List<Outward> getAssignInventories(Long staffId) {
        QOutward qOutward = QOutward.outward;
        JPAQuery<Outward> query = new JPAQuery<>(entityManager);
        List<Outward> outwardList = new ArrayList<>();
        BooleanExpression booleanExpression = qOutward.isNotNull().and(qOutward.sourceType.eq(CommonConstants.STAFF)).and(qOutward.sourceId.eq(staffId).and(qOutward.isDeleted.eq(false)));
        List<Tuple> result = query.select(qOutward.id, qOutward.outwardNumber, qOutward.productId.name, qOutward.sourceType, qOutward.sourceId, qOutward.inwardId.inwardNumber, qOutward.outwardDateTime, qOutward.qty, qOutward.usedQty, qOutward.unusedQty, qOutward.productId.productCategory.unit).from(qOutward).where(booleanExpression).fetch();
        if (!result.isEmpty()) {
            result.forEach(tuple -> {
                Outward outward = new Outward();
                outward.setId(tuple.get(qOutward.id));
                outward.setOutwardNumber(tuple.get(qOutward.outwardNumber));
                outward.setProductName(tuple.get(qOutward.productId.name));
                outward.setSourceType(tuple.get(qOutward.sourceType));
                outward.setSourceId(tuple.get(qOutward.sourceId));
                outward.setInwardNumber(tuple.get(qOutward.inwardId.inwardNumber));
                outward.setOutwardDateTime(tuple.get(qOutward.outwardDateTime));
                outward.setQty(tuple.get(qOutward.qty));
                outward.setUsedQty(tuple.get(qOutward.usedQty));
                outward.setUnusedQty(tuple.get(qOutward.unusedQty));
                outward.setUnit(tuple.get(qOutward.productId.productCategory.unit));
                outwardList.add(outward);
            });
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return outwardList;
        else
            // TODO: pass mvnoID manually 6/5/2025
            return outwardList.stream().filter(outward -> outward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || outward.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
    }

//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
//        QOutward qOutward = QOutward.outward;
//        BooleanExpression booleanExpression = qOutward.isNotNull().and(qOutward.isDeleted.eq(false));
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
//        List<Long> resultPaginationList = new ArrayList<>();
//        Page<Outward> finalPaginationList = null;
//        String outwardNumber = null;
//        //Page<Outward> paginationList = null;
//        try {
//            if (getMvnoIdFromCurrentStaff() != 1) {
//                List<Outward> outwardWarehouseList = getAllWarehouseForOutward(null);
//                List<Outward> outwardPopManagementList = getAllPOPForOutward(null);
//                List<Outward> outwardStaffList = getAllStaffForOutward(null);
//                List<Outward> outwardPartnerList = getAllPartnerStaffForOutward(null);
//                List<Outward> outwardServiceAreaStaffList = getAllServiceAreaForOutward(null);
//                if (outwardWarehouseList != null) {
//                    if (outwardWarehouseList.size() > 0) {
//                        for (int w = 0; w < outwardWarehouseList.size(); w++) {
//                            resultPaginationList.add(outwardWarehouseList.get(w).getId());
//                        }
//                    }
//                }
//                if (outwardPopManagementList != null) {
//                    if (outwardPopManagementList.size() > 0) {
//                        for (int p = 0; p < outwardPopManagementList.size(); p++) {
//                            resultPaginationList.add(outwardPopManagementList.get(p).getId());
//                        }
//                    }
//                }
//                if (outwardStaffList != null) {
//                    if (outwardStaffList.size() > 0) {
//                        for (int s = 0; s < outwardStaffList.size(); s++) {
//                            resultPaginationList.add(outwardStaffList.get(s).getId());
//                        }
//                    }
//                }
//                if (outwardPartnerList != null) {
//                    if (outwardPartnerList.size() > 0) {
//                        for (int p = 0; p < outwardPartnerList.size(); p++) {
//                            resultPaginationList.add(outwardPartnerList.get(p).getId());
//                        }
//                    }
//                }
//                if (outwardServiceAreaStaffList != null) {
//                    if (outwardServiceAreaStaffList.size() > 0) {
//                        for (int s = 0; s < outwardServiceAreaStaffList.size(); s++) {
//                            resultPaginationList.add(outwardServiceAreaStaffList.get(s).getId());
//                        }
//                    }
//                }
//                finalPaginationList = outwardRepository.findAllByIdIn(resultPaginationList, pageRequest);
//            }
//            if (getMvnoIdFromCurrentStaff() == 1) {
//                finalPaginationList = outwardRepository.findAll(booleanExpression, pageRequest);
//            }
//            if (finalPaginationList != null && finalPaginationList.getSize() > 0) {
//                makeGenericResponse(genericDataDTO, finalPaginationList);
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return genericDataDTO;
//    }


    public List<Outward> getAllOutwards() {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        QOutward qOutward = QOutward.outward;
        BooleanExpression booleanExpression = qOutward.isNotNull().and(qOutward.isDeleted.eq(false));
        List<Long> resultPaginationList = new ArrayList<>();
        List<Outward> outwardList = new ArrayList<>();
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                List<Outward> outwardWarehouseList = getAllWarehouseForOutward(null);
                List<Outward> outwardPopManagementList = getAllPOPForOutward(null);
                List<Outward> outwardStaffList = getAllStaffForOutward(null);
                List<Outward> outwardPartnerList = getAllPartnerStaffForOutward(null);
                List<Outward> outwardServiceAreaStaffList = getAllServiceAreaForOutward(null);
                if (outwardWarehouseList != null) {
                    if (outwardWarehouseList.size() > 0) {
                        for (int w = 0; w < outwardWarehouseList.size(); w++) {
                            resultPaginationList.add(outwardWarehouseList.get(w).getId());
                        }
                    }
                }
                if (outwardPopManagementList != null) {
                    if (outwardPopManagementList.size() > 0) {
                        for (int p = 0; p < outwardPopManagementList.size(); p++) {
                            resultPaginationList.add(outwardPopManagementList.get(p).getId());
                        }
                    }
                }
                if (outwardStaffList != null) {
                    if (outwardStaffList.size() > 0) {
                        for (int s = 0; s < outwardStaffList.size(); s++) {
                            resultPaginationList.add(outwardStaffList.get(s).getId());
                        }
                    }
                }
                if (outwardPartnerList != null) {
                    if (outwardPartnerList.size() > 0) {
                        for (int p = 0; p < outwardPartnerList.size(); p++) {
                            resultPaginationList.add(outwardPartnerList.get(p).getId());
                        }
                    }
                }
                if (outwardServiceAreaStaffList != null) {
                    if (outwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < outwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(outwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }
                outwardList = outwardRepository.findAllByIdIn(resultPaginationList);

            }
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) {
                outwardList = (List<Outward>) outwardRepository.findAll(booleanExpression);
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return outwardList;
    }
    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getOutwardList(searchModel.getFilterValue(), pageRequest);
                    }
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase("Outward Number")) {
//                        return getOutwardList(searchModel.getFilterValue(), pageRequest);
//                    }
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase("Product Name")) {
//                        return getOutwardListbaseOnProductname(searchModel.getFilterValue(), pageRequest);
//                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getOutwardList(String outwardNumber, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getOutwardList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Outward> finalPaginationList = null;
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                List<Long> resultPaginationList = new ArrayList<>();
                List<Outward> outwardWarehouseList = getAllWarehouseForOutward(outwardNumber);
                List<Outward> outwardPopManagementList = getAllPOPForOutward(outwardNumber);
                List<Outward> outwardStaffList = getAllStaffForOutward(outwardNumber);
                List<Outward> outwardPartnerList = getAllPartnerStaffForOutward(outwardNumber);
                List<Outward> outwardServiceAreaStaffList = getAllServiceAreaForOutward(outwardNumber);
                if (outwardWarehouseList != null) {
                    if (outwardWarehouseList.size() > 0) {
                        for (int w = 0; w < outwardWarehouseList.size(); w++) {
                            resultPaginationList.add(outwardWarehouseList.get(w).getId());
                        }
                    }
                }
                if (outwardPopManagementList != null) {
                    if (outwardPopManagementList.size() > 0) {
                        for (int p = 0; p < outwardPopManagementList.size(); p++) {
                            resultPaginationList.add(outwardPopManagementList.get(p).getId());
                        }
                    }
                }
                if (outwardStaffList != null) {
                    if (outwardStaffList.size() > 0) {
                        for (int s = 0; s < outwardStaffList.size(); s++) {
                            resultPaginationList.add(outwardStaffList.get(s).getId());
                        }
                    }
                }
                if (outwardPartnerList != null) {
                    if (outwardPartnerList.size() > 0) {
                        for (int p = 0; p < outwardPartnerList.size(); p++) {
                            resultPaginationList.add(outwardPartnerList.get(p).getId());
                        }
                    }
                }
                if (outwardServiceAreaStaffList != null) {
                    if (outwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < outwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(outwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }
                finalPaginationList = outwardRepository.findAllByIdIn(resultPaginationList, pageRequest);
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) {
                finalPaginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndIsDeletedIsFalse(outwardNumber, pageRequest);
            }
            if (finalPaginationList != null && finalPaginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, finalPaginationList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getOutwardListbaseOnProductname(String productName, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getOutwardList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Outward> finalPaginationList = null;
        try {
            List<Outward> outwardList = getAllOutwards();

            // Filter the outwardList based on the productName
            if (productName != null) {
                outwardList = outwardList.stream()
                        .filter(outward -> outward.getProductId().getName().contains(productName))
                        .collect(Collectors.toList());
            }

            // Create a Pageable object based on the provided pageRequest
            Pageable pageable = pageRequest;

            // Apply pagination and sorting to the filtered data
            List<Outward> paginatedList = outwardList.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());

            // Count the total number of matching records
            long totalCount = outwardList.size();

            // Create a Page object containing the paginated list and the total count
            finalPaginationList = new PageImpl<>(paginatedList, pageable, totalCount);

            if (finalPaginationList != null && finalPaginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, finalPaginationList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO searchAssignInventories(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, Long staffId) {
        String SUBMODULE = getModuleNameForLog() + " [searchAssignInventories()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Pageable pageable = generatePageRequest(page, pageSize, "id", sortOrder);
        try {
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpression = qOutward.isDeleted.eq(false);
                        if (!searchModel.getFilterValue().isEmpty()) {
                            String searchKey = searchModel.getFilterValue();
                            List<Product> product = productService.getByName(searchKey);
                            booleanExpression = booleanExpression.and(qOutward.destinationId.eq(staffId))
                                    .and(qOutward.destinationType.equalsIgnoreCase(CommonConstants.STAFF));
                            if (product != null && product.size() > 0) {
                                booleanExpression = booleanExpression.and(qOutward.productId.id.in(product.stream().map(product1 -> product1.getId()).collect(Collectors.toList())));
                            }
                        }
                        // TODO: pass mvnoID manually 6/5/2025
                        if (getMvnoIdFromCurrentStaff(null) != 1)
                            // TODO: pass mvnoID manually 6/5/2025
                            booleanExpression = booleanExpression.and(qOutward.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                        Page<Outward> assignInventoriesList = outwardRepository.findAll(booleanExpression, pageable);
                        if (null != assignInventoriesList && 0 < assignInventoriesList.getSize()) {
                            makeGenericResponse(genericDataDTO, assignInventoriesList);
                        }
                        return genericDataDTO;
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            throw e;
        }
        return null;
    }

   /* public GenericDataDTO getAssignInventories(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long staffId) {
        String SUBMODULE = getModuleNameForLog() + " [getAssignInventories()] ";
        QInward qInward = QInward.inward;
        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.destinationType.equalsIgnoreCase(CommonConstants.STAFF)).and(qInward.destinationId.eq(staffId).and(qInward.isDeleted.eq(false)));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<Inward> paginationList = null;
        try {
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            if (getMvnoIdFromCurrentStaff() != 1)
                booleanExpression = booleanExpression.and(qInward.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            paginationList = inwardRepository.findAll(booleanExpression, pageRequest);
            if (paginationList.getSize() > 0) {
                genericDataDTO = inwardService.makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }*/

    @Transactional
    public OutwardDto saveEntity(OutwardDto entity, Boolean isReturned) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        try {
            if(entity.getSourceType().equalsIgnoreCase(CommonConstants.WAREHOUSE) && entity.getDestinationType().equalsIgnoreCase(CommonConstants.WAREHOUSE)){
                if(warehouseManagementRepository.findById(entity.getSourceId()).get().getWarehouseType().equalsIgnoreCase("3PL")
                && warehouseManagementRepository.findById(entity.getDestinationId()).get().getWarehouseType().equalsIgnoreCase("3PL")){
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "3rd party to 3rd party warehouse transefer not allowed.", null);
                }
            }
            entity.setQty(0L);
            entity.setUnusedQty(0L);
            entity.setInTransitQty(entity.getInTransitQty());
            entity.setUsedQty(0L);
            entity.setOutTransitQty(0L);
            entity.setRejectedQty(0L);
            entity.setType(null);
            if(!isReturned )
                entity.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);
            entity.setApprovalStatus("Pending");
            entity.setOutwardNumber(getRandomenumber("OUT","-",""));
            if(!isReturned && !inventoryTransferValidation(entity.getSourceType(), entity.getDestinationType()))
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), MessageConstants.SOURCE_DESTINATION_MISMATCH, null);
            OutwardDto outwardDto = super.saveEntity(entity);
            InwardDto inward = inwardService.saveInwardOfOutwardEntity(convertOutwardDtoToInwardDto(outwardDto, isReturned), true, true);
            outwardDto.setOutwardsInwardId(inward.getId());

            // Managing quantities in product owner
            ProductOwnerDto destination = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
            if (destination != null) {
                destination.setQuantity(destination.getQuantity());
                destination.setUnusedQty(destination.getUnusedQty());
                destination.setUsedQty(destination.getUsedQty());
                destination.setInTransitQty(destination.getInTransitQty() + entity.getInTransitQty());
                destination.setProductId(entity.getProductId().getId());
                destination.setOwnerId(entity.getDestinationId());
                destination.setOwnerType(entity.getDestinationType());
                productOwnerService.updateEntity(destination);
            } else {
                ProductOwnerDto productOwnerDto = new ProductOwnerDto();
                productOwnerDto.setQuantity(entity.getQty());
                productOwnerDto.setUnusedQty(entity.getQty());
                productOwnerDto.setUsedQty(entity.getUsedQty());
                productOwnerDto.setInTransitQty(entity.getInTransitQty());
                productOwnerDto.setProductId(entity.getProductId().getId());
                productOwnerDto.setOwnerId(entity.getDestinationId());
                productOwnerDto.setOwnerType(entity.getDestinationType());
                productOwnerService.saveEntity(productOwnerDto);
            }
            ProductOwnerDto source = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getSourceId(), entity.getSourceType());
            if(!isReturned) {
                if (source != null) {
                    source.setQuantity(source.getQuantity());
                    source.setUnusedQty(source.getUnusedQty() - entity.getInTransitQty());
                    source.setUsedQty(source.getUsedQty() + entity.getInTransitQty());
                    source.setInTransitQty(source.getInTransitQty());
                    source.setProductId(entity.getProductId().getId());
                    source.setOwnerId(entity.getSourceId());
                    source.setOwnerType(entity.getSourceType());
                    productOwnerService.updateEntity(source);
                } else {
                    ProductOwnerDto productOwnerDto = new ProductOwnerDto();
                    productOwnerDto.setQuantity(entity.getQty());
                    productOwnerDto.setUnusedQty(entity.getQty());
                    productOwnerDto.setUsedQty(entity.getUsedQty());
                    productOwnerDto.setInTransitQty(entity.getInTransitQty());
                    productOwnerDto.setProductId(entity.getProductId().getId());
                    productOwnerDto.setOwnerId(entity.getSourceId());
                    productOwnerDto.setOwnerType(entity.getSourceType());
                    productOwnerService.saveEntity(productOwnerDto);
                }
            }
            //To add RequestInvetory Status

            if(outwardDto.getRequestInventoryProductId()!=null){
               RequestInvenotryProductMapping requestInvenotryProductMapping= requestInventoryProductMappingRepo.findById(outwardDto.getRequestInventoryProductId()).orElse(null);
               if(requestInvenotryProductMapping!=null){
                   if(outwardDto.getApprovalStatus().equalsIgnoreCase("Approve")){
                       requestInvenotryProductMapping.setRequestStatus("Close");
                   }
                   else if(outwardDto.getApprovalStatus().equalsIgnoreCase("Pending")){
                       requestInvenotryProductMapping.setRequestStatus("Open");
                   }
                   else{
                       requestInvenotryProductMapping.setRequestStatus("Reject");
                   }
               }
            }
            return outwardDto;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    private InwardDto convertOutwardDtoToInwardDto(OutwardDto outwardDto, Boolean isReturned) {
        InwardDto inwardDto = new InwardDto();
        inwardDto.setInwardNumber(UtilsCommon.getResponse("", "", null, 5));
        //inwardDto.setQty(outwardDto.getQty());
        inwardDto.setQty(outwardDto.getInTransitQty());
        if(isReturned)
            inwardDto.setTotalMacSerial(outwardDto.getInTransitQty());
        inwardDto.setUsedQty(0L);
        inwardDto.setUnusedQty(outwardDto.getUsedQty());
        //inwardDto.setInTransitQty(outwardDto.getInTransitQty());
        inwardDto.setInTransitQty(outwardDto.getInTransitQty());
        inwardDto.setInwardDateTime(outwardDto.getOutwardDateTime());
        inwardDto.setDestinationType(outwardDto.getDestinationType());
        inwardDto.setDestinationId(outwardDto.getDestinationId());
        inwardDto.setSourceType(outwardDto.getSourceType());
        inwardDto.setSourceId(outwardDto.getSourceId());
        inwardDto.setIsDeleted(outwardDto.getIsDeleted());
        inwardDto.setMvnoId(outwardDto.getMvnoId());
        inwardDto.setType(null);
        inwardDto.setStatus(outwardDto.getStatus());
        inwardDto.setProductId(outwardDto.getProductId());
        inwardDto.setServiceAreaId(outwardDto.getServiceAreaId());
        inwardDto.setApprovalStatus("Pending");
        inwardDto.setType(outwardDto.getType());
        if(!isReturned)
            inwardDto.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);
        else
            inwardDto.setCategoryType(CommonConstants.RETURNED_INWARD_TYPE);
        inwardDto.setOutwardId(outwardMapper.dtoToDomain(outwardDto, new CycleAvoidingMappingContext()));
        return inwardDto;
    }

    public boolean checkHasMacAndHasSerial(Long inwardId) throws Exception {
        boolean flag = false;
        QInward qInward = QInward.inward;
        BooleanExpression booleanExpression = QInward.inward.isNotNull().and(qInward.isDeleted.eq(false));
        booleanExpression = booleanExpression.and(qInward.id.eq(inwardId));
        Inward inward = inwardRepository.findOne(booleanExpression).get();
        boolean hasMac = inward.getProductId().getProductCategory().isHasMac();
        boolean hasSerial = inward.getProductId().getProductCategory().isHasSerial();
        Inward inward1 = inwardRepository.findById(inwardId).get();
        Integer count = null;
        if (hasMac == true && hasSerial == true) {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) {
                if (inward1.getOutwardId() == null) {
                    count = inOutWardMacRepo.countInward(Math.toIntExact(inwardId));
                } else {
                    if (inward1 != null) {
                        Integer countInwardId = Math.toIntExact(inward1.getId());
                        if (countInwardId != null) {
                            count = inOutWardMacRepo.countInwardIdOfOutward(Math.toIntExact(countInwardId));
                        }
                    }
                }
            } else {
                if (inward1.getOutwardId() == null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    count = inOutWardMacRepo.countInward(Math.toIntExact(inwardId), Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                } else {
                    if (inward1 != null) {
                        Integer countInwardId = Math.toIntExact(inward1.getId());
                        if (countInwardId != null) {
                            // TODO: pass mvnoID manually 6/5/2025
                            count = inOutWardMacRepo.countInwardIdOfOutward(Math.toIntExact(inwardId), Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                        }
                    }
                }
            }
            if (count != 0) {
                flag = true;
            } else {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please enter has mac and has serial", null);
            }
        } else if (hasSerial == true) {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) {
                if (inward1.getOutwardId() == null) {
                    count = inOutWardMacRepo.countInward(Math.toIntExact(inwardId));
                } else {
                    if (inward1 != null) {
                        Integer countInwardId = Math.toIntExact(inward1.getId());
                        if (countInwardId != null) {
                            count = inOutWardMacRepo.countInwardIdOfOutward(Math.toIntExact(countInwardId));
                        }
                    }
                }
            } else {
                if (inward1.getOutwardId() == null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    count = inOutWardMacRepo.countInward(Math.toIntExact(inwardId), Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                } else {
                    if (inward1 != null) {
                        Integer countInwardId = Math.toIntExact(inward1.getId());
                        if (countInwardId != null) {
                            // TODO: pass mvnoID manually 6/5/2025
                            count = inOutWardMacRepo.countInwardIdOfOutward(Math.toIntExact(inwardId), Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                        }
                    }
                }
            }
            if (count != 0) {
                flag = true;
            } else {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please enter has serial", null);
            }
        } else if (hasMac == false && hasSerial == false) {
            flag = true;
        }
        return flag;
    }

    private Boolean inventoryTransferValidation(String source, String destination) {
        // Source       - WH, Partner, Staff
        // Destination  - WH, Partner, Staff, POP, SA, Customer
        // END          - POP, SA, Customer
        if(source.equalsIgnoreCase(CommonConstants.WAREHOUSE)){
            if(destination.equalsIgnoreCase(CommonConstants.WAREHOUSE) || destination.equalsIgnoreCase(CommonConstants.PARTNER) || destination.equalsIgnoreCase(CommonConstants.STAFF))
                return true;
            else return false;
        } else if (source.equalsIgnoreCase(CommonConstants.PARTNER)){
            if(destination.equalsIgnoreCase(CommonConstants.PARTNER))
                return true;
            else return false;
        } else if (source.equalsIgnoreCase(CommonConstants.STAFF)){
            if(destination.equalsIgnoreCase(CommonConstants.WAREHOUSE))
                return true;
            else return false;
        }
        return false;
    }

//    @Override
//    public boolean deleteVerification(Integer id) throws Exception {
//        boolean flag = false;
//        Integer count = outwardRepository.deleteVerify(id);
//        if (count == 0) {
//            flag = true;
//        }
//        return flag;
//    }

    //Get All Inward based on Warehouse
    public List<Outward> getAllWarehouseForOutward(String outwardNumber) {
        QWareHouse qWareHouse = QWareHouse.wareHouse;
        QWareHouseServiceAreaMapping qWareHouseServiceAreaMapping = QWareHouseServiceAreaMapping.wareHouseServiceAreaMapping;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        List<Outward> paginationList = null;
        // Common method for find Service Area List Based on StaffId
        ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
        List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
        BooleanExpression aBoolean = qWareHouse.isNotNull().and(qWareHouse.isDeleted.eq(false));
        aBoolean = aBoolean
                .and(qWareHouse.id.in(query.select(qWareHouseServiceAreaMapping.warehouseId)
                        .from(qWareHouseServiceAreaMapping)
                        .where(qWareHouseServiceAreaMapping.serviceId.in(serviceAreaIds))));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            aBoolean = aBoolean.and(qWareHouse.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
        }
        List<WareHouse> wareHouseList = (List<WareHouse>) warehouseManagementRepository.findAll(aBoolean);
        if (wareHouseList.size() > 0) {
            String warehouseDestinationType = "Warehouse";
            List<Long> warehouseResult = new ArrayList<>();
            for (int i = 0; i < wareHouseList.size(); i++) {
                Long warehouseDestinationId = wareHouseList.get(i).getId();
                warehouseResult.add(warehouseDestinationId);
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                if (outwardNumber != null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(outwardNumber, warehouseResult, warehouseDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(warehouseResult, warehouseDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                }
            }
        }
        return paginationList;
    }

//    Get All Inward based on POP
    public List<Outward> getAllPOPForOutward(String outwardNumber) {
        QPopManagement qPopManagement = QPopManagement.popManagement;
        QPopServiceAreaMapping qPopServiceAreaMapping = QPopServiceAreaMapping.popServiceAreaMapping;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        List<Outward> paginationList = null;
        // Common method for find Service Area List Based on StaffId
        ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
        List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
        BooleanExpression aBoolean = qPopManagement.isNotNull().and(qPopManagement.isDeleted.eq(false));
        aBoolean = aBoolean
                .and(qPopManagement.id.in(query.select(qPopServiceAreaMapping.popId)
                        .from(qPopServiceAreaMapping)
                        .where(qPopServiceAreaMapping.serviceId.in(serviceAreaIds))));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            aBoolean = aBoolean.and(qPopManagement.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
        }
        List<PopManagement> popManagementList = (List<PopManagement>) popManagementRepository.findAll(aBoolean);
        String popDestinationType = "POP";
        List<Long> popResult = new ArrayList<>();
        for (int p = 0; p < popManagementList.size(); p++) {
            Long popDestinationId = popManagementList.get(p).getId();
            popResult.add(popDestinationId);
        }
//        if (getMvnoIdFromCurrentStaff() == 1) {
//            if (inwardNumber != null){
//                paginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(inwardNumber, popResult, popDestinationType);
//            } else {
//                paginationList = inwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(popResult, popDestinationType);
//            }
//        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            if (outwardNumber != null) {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(outwardNumber, popResult, popDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            } else {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(popResult, popDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            }
        }
        return paginationList;
    }

    public List<Outward> getAllStaffForOutward(String outwardNumber) {
        String status = "ACTIVE";
        List<Long> resultStaffId = new ArrayList<>();
        List<Outward> paginationList = null;
//        if (getMvnoIdFromCurrentStaff() == 1) {
//            List<StaffUser> staffUserList = staffUserRepository.findByIdAndStatusAndIsDeleteIsFalse(getLoggedInUserId(), status);
//            for (int i=0; i<staffUserList.size(); i++){
//                Integer staffIds = staffUserList.get(i).getId();
//                resultStaffId.add(Long.valueOf(staffIds));
//            }
//        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            List<StaffUser> staffUserList = staffUserRepository.findByIdAndStatusAndIsDeleteIsFalseAndMvnoIdIn(getLoggedInUserId(), status, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            for (int i = 0; i < staffUserList.size(); i++) {
                Integer staffIds = staffUserList.get(i).getId();
                resultStaffId.add(Long.valueOf(staffIds));
            }
        }
        String staffDestinationType = "Staff";
//        if (getMvnoIdFromCurrentStaff() == 1) {
//            if (inwardNumber != null){
//                paginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(inwardNumber, resultStaffId, staffDestinationType);
//            } else {
//                paginationList = inwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(resultStaffId, staffDestinationType);
//            }
//        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            if (outwardNumber != null) {
                QOutward qOutward=QOutward.outward;
                BooleanExpression booleanExpression=qOutward.isNotNull();
                booleanExpression=booleanExpression.and(qOutward.outwardNumber.likeIgnoreCase(outwardNumber)).and(qOutward.isDeleted.eq(false));
                paginationList= (List<Outward>) outwardRepository.findAll(booleanExpression);
                //paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(outwardNumber, resultStaffId, staffDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            } else {
                QOutward qOutward=QOutward.outward;
                BooleanExpression booleanExpression=qOutward.isNotNull();
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression=booleanExpression.and(qOutward.createdById.eq(getLoggedInUserId()).and(qOutward.isDeleted.eq(false)).and(qOutward.mvnoId.in(1,getMvnoIdFromCurrentStaff(null))));
                paginationList= (List<Outward>) outwardRepository.findAll(booleanExpression);
               // paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(resultStaffId, staffDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            }
        }
        return paginationList;
    }

    public List<Outward> getAllPartnerStaffForOutward(String outwardNumber) {
        String status = "ACTIVE";
        List<Long> resultStaffId = new ArrayList<>();
        List<Outward> paginationList = null;
//        if (getMvnoIdFromCurrentStaff() == 1) {
//            List<StaffUser> staffUserList = staffUserRepository.findByIdAndStatusAndIsDeleteIsFalse(getLoggedInUserId(), status);
//            for (int i=0; i<staffUserList.size(); i++){
//                Integer staffIds = staffUserList.get(i).getId();
//                resultStaffId.add(Long.valueOf(staffIds));
//            }
//        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            List<StaffUser> staffUserList = staffUserRepository.findByIdAndStatusAndIsDeleteIsFalseAndMvnoIdIn(getLoggedInUserId(), status, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            for (int i = 0; i < staffUserList.size(); i++) {
                Integer staffIds = staffUserList.get(i).getId();
                resultStaffId.add(Long.valueOf(staffIds));
            }
        }
        String partnerDestinationType = "Partner";
//        if (getMvnoIdFromCurrentStaff() == 1) {
//            if (inwardNumber != null){
//                paginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(inwardNumber, resultStaffId, partnerDestinationType);
//            } else {
//                paginationList = inwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(resultStaffId, partnerDestinationType);
//            }
//        }
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            if (outwardNumber != null) {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(outwardNumber, resultStaffId, partnerDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            } else {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(resultStaffId, partnerDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            }
        }
        return paginationList;
    }

    public List<Outward> getAllServiceAreaForOutward(String outwardNumber) {
        QStaffUser qStaffUser = QStaffUser.staffUser;
        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        List<Outward> paginationList = null;
        // Common method for find Service Area List Based on StaffId
        ServiceAreaService serviceAreaService = SpringContext.getBean(ServiceAreaService.class);
        List<Integer> serviceAreaIds = serviceAreaService.getServiceAreaByStaffId();
        BooleanExpression aBoolean = qStaffUser.isNotNull().and(qStaffUser.isDelete.eq(false));
        aBoolean = aBoolean
                .and(qStaffUser.id.in(query.select(qStaffUserServiceAreaMapping.staffId)
                        .from(qStaffUserServiceAreaMapping)
                        .where(qStaffUserServiceAreaMapping.serviceId.in(serviceAreaIds))));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            aBoolean = aBoolean.and(qStaffUser.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
        }
        List<StaffUser> staffUserList = (List<StaffUser>) staffUserRepository.findAll(aBoolean);
        String serviceAreaDestinationType = "ServiceArea";
        List<Long> serviceAreaStaffResult = new ArrayList<>();
        for (int p = 0; p < staffUserList.size(); p++) {
            Long serviceAreaDestinationId = Long.valueOf(staffUserList.get(p).getId());
            serviceAreaStaffResult.add(serviceAreaDestinationId);
        }
//        if (getMvnoIdFromCurrentStaff() == 1) {
//            if (inwardNumber != null){
//                paginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(inwardNumber, serviceAreaStaffResult, serviceAreaDestinationType);
//            } else {
//                paginationList = inwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(serviceAreaStaffResult, serviceAreaDestinationType);
//            }
//        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            if (outwardNumber != null) {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = outwardRepository.findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(outwardNumber, serviceAreaStaffResult, serviceAreaDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            } else {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = outwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(serviceAreaStaffResult, serviceAreaDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            }
        }
        return paginationList;
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
            Outward outward= outwardRepository.findTopByOrderByIdDesc();
            if(outward==null){
                flag+=1;
            }
            else{
                flag +=  outward.getId()+1;
            }
        }
        return flag;
    }

    @Override
    public OutwardDto getEntityById(Long id,Integer mvnoId) {

        Outward outward = outwardRepository.findById(id).get();
        OutwardDto outwardDto = getMapper().domainToDTO(outward, new CycleAvoidingMappingContext());
        if (outwardDto.getDestinationType().equals("") || (outwardDto.getDestinationType() == null)) {
            outwardDto.setDestination("");
        } else if (outwardDto.getDestinationType().equalsIgnoreCase(CommonConstants.WAREHOUSE)) {
            outwardDto.setDestination(warehouseManagementRepository.getOne(outwardDto.getDestinationId()).getName());
        } else if (outwardDto.getDestinationType().equalsIgnoreCase(CommonConstants.STAFF)) {
            outwardDto.setDestination(staffUserRepository.getOne(Math.toIntExact(outwardDto.getDestinationId())).getFullName());
        } else if (outwardDto.getDestinationType().equalsIgnoreCase(CommonConstants.PARTNER)) {
            outwardDto.setDestination(partnerRepository.getOne(Math.toIntExact(outwardDto.getDestinationId())).getName());
        }

        if ((outwardDto.getSourceType() == null) || (outwardDto.getSourceType().equals(""))) {
            outwardDto.setSource("");
        } else if (outwardDto.getSourceType().equalsIgnoreCase(CommonConstants.WAREHOUSE)) {
            outwardDto.setSource(warehouseManagementRepository.getOne(outwardDto.getSourceId()).getName());
        } else if (outwardDto.getSourceType().equalsIgnoreCase(CommonConstants.STAFF)) {
            outwardDto.setSource(staffUserRepository.getOne(Math.toIntExact(outwardDto.getSourceId())).getFullName());
        } else if (outwardDto.getSourceType().equalsIgnoreCase(CommonConstants.PARTNER)) {
            outwardDto.setSource(partnerRepository.getOne(Math.toIntExact(outwardDto.getSourceId())).getName());
        }
        return outwardDto;
    }
}

