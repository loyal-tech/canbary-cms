package com.adopt.apigw.modules.InventoryManagement.inward;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.QStaffUser;
import com.adopt.apigw.model.common.QStaffUserServiceAreaMapping;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.model.postpaid.QPartner;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.*;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItem.NonSerializedItemRepository;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItemHierarchy.NonSerializedItemHierarchyServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.PopManagement;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.QPopManagement;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.QPopServiceAreaMapping;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.repository.PopManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.RequestInventory.RequestInvenotryProductMapping;
import com.adopt.apigw.modules.InventoryManagement.RequestInventory.RequestInventory;
import com.adopt.apigw.modules.InventoryManagement.RequestInventory.RequestInventoryProductMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.RequestInventory.RequestInventoryRepo;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.InventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.InventoryMappingMapper;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.InventoryMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.QInventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import com.adopt.apigw.modules.InventoryManagement.item.ItemRepository;
import com.adopt.apigw.modules.InventoryManagement.item.QItem;
import com.adopt.apigw.modules.InventoryManagement.outward.*;
import com.adopt.apigw.modules.InventoryManagement.productBundle.BulkConsumption;
import com.adopt.apigw.modules.InventoryManagement.productBundle.QBulkConsumption;
import com.adopt.apigw.modules.InventoryManagement.productOwner.*;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwner;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerDto;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerMapper;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerService;
import com.adopt.apigw.modules.InventoryManagement.warehouse.*;
import com.adopt.apigw.modules.ServiceArea.domain.QServiceArea;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.servicePlan.domain.QServices;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.PartnerRepository;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.CommonConstants;
import com.google.common.collect.Lists;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InwardServiceImpl extends ExBaseAbstractService<InwardDto, Inward, Long> {

    @Autowired
    InwardRepository inwardRepository;

    @Autowired
    InwardMapper inwardMapper;

    @Autowired
    ProductOwnerService productOwnerService;

    @Autowired
    private InwardServiceImpl inwardService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    ProductOwnerMapper productOwnerMapper;

    @Autowired
    OutwardRepository outwardRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    WarehouseManagementRepository warehouseManagementRepository;

    @Autowired
    PopManagementRepository popManagementRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    InOutWardMacRepo inOutWardMacRepo;

    @Autowired
    private InOutWardMACService inOutWardMACService;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NonSerializedItemRepository nonSerializedItemRepository;

    @Autowired
    private NonSerializedItemHierarchyServiceImpl nonSerializedItemHierarchyService;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProductOwnerRepository productOwnerRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private RequestInventoryProductMappingRepo requestInvenotryProductMappingRepo;

    @Autowired
    private RequestInventoryRepo requestInventoryRepo;
    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private CustomerInventoryMappingMapper customerInventoryMappingMapper;
    @Autowired
    private InventoryMappingRepo inventoryMappingRepo;

    @Autowired
    private InventoryMappingMapper inventoryMappingMapper;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;


    public InwardServiceImpl(InwardRepository inwardRepository, InwardMapper inwardMapper) {
        super(inwardRepository, inwardMapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[InwardServiceImpl]";
    }

    public List<Inward> getInwardDetailsByProductAndDestination(Long productId, Long warehouseId, String destinationType) {
        QInward qInward = QInward.inward;
        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.productId.id.eq(productId))
                .and(qInward.destinationId.eq(warehouseId))
                .and(qInward.destinationType.equalsIgnoreCase(destinationType))
                .and(qInward.type.in(CommonConstants.REFURBISHED, CommonConstants.NEW, CommonConstants.OLD))
                .and(qInward.status.eq(CommonConstants.ACTIVE_STATUS))
                .and(qInward.isDeleted.eq(false))
                .and(qInward.approvalStatus.equalsIgnoreCase("Approve"));
        // TODO: pass mvnoID manually 6/5/2025
        return Lists.newArrayList(inwardRepository.findAll(booleanExpression))
                .stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest = generatePageRequest(pageNumber, customPageSize, "createdate", sortOrder);
        List<Long> resultPaginationList = new ArrayList<>();
        Page<Inward> finalPaginationList = null;
        String inwardNumber = null;
        QInward qInward = QInward.inward;
        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.isDeleted.eq(false));
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                List<Inward> inwardWarehouseList = getAllWarehouseForInward(null);
                List<Inward> inwardPopManagementList = getAllPOPForInward(null);
                List<Inward> inwardStaffList = getAllStaffForInward(null);
                List<Inward> inwardPartnerList = getAllPartnerStaffForInward(null);
                List<Inward> inwardServiceAreaStaffList = getAllServiceAreaForInward(null);
                if (inwardWarehouseList != null) {
                    if (inwardWarehouseList.size() > 0) {
                        for (int w = 0; w < inwardWarehouseList.size(); w++) {
                            resultPaginationList.add(inwardWarehouseList.get(w).getId());
                        }
                    }
                }
                if (inwardPopManagementList != null) {
                    if (inwardPopManagementList.size() > 0) {
                        for (int p = 0; p < inwardPopManagementList.size(); p++) {
                            resultPaginationList.add(inwardPopManagementList.get(p).getId());
                        }
                    }
                }
                if (inwardStaffList != null) {
                    if (inwardStaffList.size() > 0) {
                        for (int s = 0; s < inwardStaffList.size(); s++) {
                            resultPaginationList.add(inwardStaffList.get(s).getId());
                        }
                    }
                }
                if (inwardPartnerList != null) {
                    if (inwardPartnerList.size() > 0) {
                        for (int p = 0; p < inwardPartnerList.size(); p++) {
                            resultPaginationList.add(inwardPartnerList.get(p).getId());
                        }
                    }
                }
                if (inwardServiceAreaStaffList != null) {
                    if (inwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < inwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(inwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }
                finalPaginationList = inwardRepository.findAllByIdIn(resultPaginationList, pageRequest);
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) {
                finalPaginationList = inwardRepository.findAll(booleanExpression, pageRequest);
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
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase("Inward Number")) {
//                        return getInwardList(searchModel.getFilterValue(), pageRequest);
//                    }
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase("Product Name")) {
//                        return getInwardListbaseOnProductname(searchModel.getFilterValue(), pageRequest);
//                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getInwardList(String inwardNumber, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getInwardList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Inward> finalPaginationList = null;
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                List<Long> resultPaginationList = new ArrayList<>();
                List<Inward> inwardWarehouseList = getAllWarehouseForInward(inwardNumber);
                List<Inward> inwardPopManagementList = getAllPOPForInward(inwardNumber);
                List<Inward> inwardStaffList = getAllStaffForInward(inwardNumber);
                List<Inward> inwardPartnerList = getAllPartnerStaffForInward(inwardNumber);
                List<Inward> inwardServiceAreaStaffList = getAllServiceAreaForInward(inwardNumber);
                if (inwardWarehouseList != null) {
                    if (inwardWarehouseList.size() > 0) {
                        for (int w = 0; w < inwardWarehouseList.size(); w++) {
                            resultPaginationList.add(inwardWarehouseList.get(w).getId());
                        }
                    }
                }
                if (inwardPopManagementList != null) {
                    if (inwardPopManagementList.size() > 0) {
                        for (int p = 0; p < inwardPopManagementList.size(); p++) {
                            resultPaginationList.add(inwardPopManagementList.get(p).getId());
                        }
                    }
                }
                if (inwardStaffList != null) {
                    if (inwardStaffList.size() > 0) {
                        for (int s = 0; s < inwardStaffList.size(); s++) {
                            resultPaginationList.add(inwardStaffList.get(s).getId());
                        }
                    }
                }
                if (inwardPartnerList != null) {
                    if (inwardPartnerList.size() > 0) {
                        for (int p = 0; p < inwardPartnerList.size(); p++) {
                            resultPaginationList.add(inwardPartnerList.get(p).getId());
                        }
                    }
                }
                if (inwardServiceAreaStaffList != null) {
                    if (inwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < inwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(inwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }
                finalPaginationList = inwardRepository.findAllByIdIn(resultPaginationList, pageRequest);
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) {
                finalPaginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndIsDeletedIsFalse(inwardNumber, pageRequest);
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

    public GenericDataDTO searchByProductAndStatusAndServiceName(String filterColumn, String value, Long customerId) {
        String SUBMODULE = getModuleNameForLog() + " [ search()] ";
        GenericDataDTO genericDataDTO = null;
        try {
            genericDataDTO = new GenericDataDTO();
            QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
            QServices qServices = QServices.services;
            BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull();
            if (filterColumn.equalsIgnoreCase("Product")) {
                booleanExpression = booleanExpression.and(qCustomerInventoryMapping.customer.id.eq(Math.toIntExact(customerId))).and(qCustomerInventoryMapping.product.name.likeIgnoreCase("%" + value + "%")).and(qCustomerInventoryMapping.isDeleted.eq(false));
            }
            if (filterColumn.equalsIgnoreCase("Status")) {
                booleanExpression = booleanExpression.and(qCustomerInventoryMapping.customer.id.eq(Math.toIntExact(customerId))).and(qCustomerInventoryMapping.status.likeIgnoreCase("%" + value + "%")).and(qCustomerInventoryMapping.isDeleted.eq(false));
            }
            if (filterColumn.equalsIgnoreCase("ServiceName")) {
                booleanExpression = booleanExpression.and(qCustomerInventoryMapping.customer.id.eq(Math.toIntExact(customerId)))
                        .and(qCustomerInventoryMapping.isDeleted.eq(false))
                        .and(qCustomerInventoryMapping.serviceId.in(
                                JPAExpressions.select(qServices.id)
                                        .from(qServices)
                                        .where(qServices.serviceName.likeIgnoreCase("%" + value + "%"))));
            }

            List<CustomerInventoryMapping> customerInventoryMappingList = (List<CustomerInventoryMapping>) customerInventoryMappingRepo.findAll(booleanExpression);
            List<CustomerInventoryMappingDto> customerInventoryMappingDtoList = customerInventoryMappingMapper.domainToDTO(customerInventoryMappingList, new CycleAvoidingMappingContext());
            if (null != customerInventoryMappingDtoList) {
                genericDataDTO.setDataList(customerInventoryMappingDtoList);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(customerInventoryMappingList.size());
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return genericDataDTO;
    }
    public GenericDataDTO searchByCustomerAndPopAndServiceAreaName(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Long staffID,String fileterName,boolean isSerelized) {
        String SUBMODULE = getModuleNameForLog() + " [ search()] ";
        try {
            PageRequest pageRequest1 = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase("Outward Number")) {
                        return getPopAndServiceAreaAndsCustomerByName(searchModel.getFilterValue(), pageRequest1,fileterName,staffID,searchModel.getFilterColumn().trim(),isSerelized);
                    }
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase("ProductName")) {
                        return getInwardListbaseOnProductname(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public List<Inward> getAllInward() {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<Long> resultPaginationList = new ArrayList<>();
        List<Inward> finalPaginationList = null;
        String inwardNumber = null;
        QInward qInward = QInward.inward;
        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.isDeleted.eq(false));
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                List<Inward> inwardWarehouseList = getAllWarehouseForInward(null);
                List<Inward> inwardPopManagementList = getAllPOPForInward(null);
                List<Inward> inwardStaffList = getAllStaffForInward(null);
                List<Inward> inwardPartnerList = getAllPartnerStaffForInward(null);
                List<Inward> inwardServiceAreaStaffList = getAllServiceAreaForInward(null);
                if (inwardWarehouseList != null) {
                    if (inwardWarehouseList.size() > 0) {
                        for (int w = 0; w < inwardWarehouseList.size(); w++) {
                            resultPaginationList.add(inwardWarehouseList.get(w).getId());
                        }
                    }
                }
                if (inwardPopManagementList != null) {
                    if (inwardPopManagementList.size() > 0) {
                        for (int p = 0; p < inwardPopManagementList.size(); p++) {
                            resultPaginationList.add(inwardPopManagementList.get(p).getId());
                        }
                    }
                }
                if (inwardStaffList != null) {
                    if (inwardStaffList.size() > 0) {
                        for (int s = 0; s < inwardStaffList.size(); s++) {
                            resultPaginationList.add(inwardStaffList.get(s).getId());
                        }
                    }
                }
                if (inwardPartnerList != null) {
                    if (inwardPartnerList.size() > 0) {
                        for (int p = 0; p < inwardPartnerList.size(); p++) {
                            resultPaginationList.add(inwardPartnerList.get(p).getId());
                        }
                    }
                }
                if (inwardServiceAreaStaffList != null) {
                    if (inwardServiceAreaStaffList.size() > 0) {
                        for (int s = 0; s < inwardServiceAreaStaffList.size(); s++) {
                            resultPaginationList.add(inwardServiceAreaStaffList.get(s).getId());
                        }
                    }
                }
                finalPaginationList = inwardRepository.findAllByIdIn(resultPaginationList);
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) {
                finalPaginationList = (List<Inward>) inwardRepository.findAll(booleanExpression);
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return finalPaginationList;
    }


    public GenericDataDTO getInwardListbaseOnProductname(String productName, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getInwardwardList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Inward> finalPaginationList = null;
        try {
            List<Inward> inwardList = getAllInward();

            // Filter the outwardList based on the productName
            if (productName != null) {
                inwardList = inwardList.stream()
                        .filter(outward -> outward.getProductId().getName().contains(productName))
                        .collect(Collectors.toList());
            }

            // Create a Pageable object based on the provided pageRequest
            Pageable pageable = pageRequest;

            // Apply pagination and sorting to the filtered data
            List<Inward> paginatedList = inwardList.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());

            // Count the total number of matching records
            long totalCount = inwardList.size();

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


   public GenericDataDTO getPopAndServiceAreaAndsCustomerByName(String s1, PageRequest pageRequest,String fileterName,Long staffId,String fileterColumn,boolean isSerelized) {
        String SUBMODULE = getModuleNameForLog() + " [getCustomerAndPopAndServiceAreaByName()] ";
        try {
            if(fileterName.equalsIgnoreCase("Customer")) {
                GenericDataDTO genericDataDTO = new GenericDataDTO();
                QCustomerInventoryMapping qCustomerInventoryMapping=QCustomerInventoryMapping.customerInventoryMapping;
                JPAQuery<?> query = new JPAQuery<>(entityManager);
                BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull();
                if(isSerelized) {
                    if (fileterColumn.equalsIgnoreCase("name")) {
                        booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qCustomerInventoryMapping.qty.gt(0)).and(qCustomerInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qCustomerInventoryMapping.customer.firstname.likeIgnoreCase("%" + s1 + "%"));
                    }
                    if (fileterColumn.equalsIgnoreCase("Product Name")) {
                        booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qCustomerInventoryMapping.qty.gt(0)).and(qCustomerInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qCustomerInventoryMapping.product.name.likeIgnoreCase("%" + s1 + "%"));
                    }
                    // TODO: pass mvnoID manually 6/5/2025
                    if (getMvnoIdFromCurrentStaff(null) != 1) {
                        // TODO: pass mvnoID manually 6/5/2025
                        booleanExpression = booleanExpression.and(qCustomerInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                    }
                }
                if (!isSerelized) {
                    if (fileterColumn.equalsIgnoreCase("name")) {
                        booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qCustomerInventoryMapping.qty.gt(0)).and(qCustomerInventoryMapping.inOutWardMACMapping.isEmpty()).and(qCustomerInventoryMapping.customer.firstname.likeIgnoreCase("%" + s1 + "%"));
                    }
                    if (fileterColumn.equalsIgnoreCase("Product Name")) {
                        booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qCustomerInventoryMapping.qty.gt(0)).and(qCustomerInventoryMapping.inOutWardMACMapping.isEmpty()).and(qCustomerInventoryMapping.product.name.likeIgnoreCase("%" + s1 + "%"));
                    }
                    // TODO: pass mvnoID manually 6/5/2025
                    if (getMvnoIdFromCurrentStaff(null) != 1) {
                        // TODO: pass mvnoID manually 6/5/2025
                        booleanExpression = booleanExpression.and(qCustomerInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                    }
                }

                Page<CustomerInventoryMapping> customerInventoryMappingPage = customerInventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (customerInventoryMappingPage.getSize() != 0) {
                    customerInventoryMappingPage.stream().forEach(r -> {
                        r.setCustomerFirstName(r.getCustomer().getFirstname());
                        r.setCustomerLastName(r.getCustomer().getLastname());
                        r.setServiceAreaName(r.getCustomer().getServicearea().getName());
                        Item item = itemRepository.findById(r.getItemId()).orElse(null);
                        if (item != null) {
                            r.setItemwarranty(item.getWarranty());
                            r.setExpDate(item.getExpireDate());
                        }
                    });
                }
                //paginationList.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList())

                if (null != customerInventoryMappingPage && 0 < customerInventoryMappingPage.getSize()) {
                    genericDataDTO.setDataList(customerInventoryMappingPage.getContent().stream().map(data -> customerInventoryMappingMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setTotalRecords(customerInventoryMappingPage.getTotalElements());
                    genericDataDTO.setPageRecords(customerInventoryMappingPage.getNumberOfElements());
                    genericDataDTO.setCurrentPageNumber(customerInventoryMappingPage.getNumber() + 1);
                    genericDataDTO.setTotalPages(customerInventoryMappingPage.getTotalPages());
                }
                return genericDataDTO;
            }

            if(fileterName.equalsIgnoreCase("Pop")) {
                GenericDataDTO genericDataDTO = new GenericDataDTO();
                QInventoryMapping qInventoryMapping=QInventoryMapping.inventoryMapping;
                QPopManagement qPopManagement=QPopManagement.popManagement;
                JPAQuery<?> query = new JPAQuery<>(entityManager);
                BooleanExpression booleanExpression = qInventoryMapping.isNotNull();
                if(isSerelized) {
                    if (fileterColumn.equalsIgnoreCase("name")) {
                         booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve")).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("pop")).and(qInventoryMapping.ownerId.in(
                                JPAExpressions.select(qPopManagement.id)
                                        .from(qPopManagement)
                                        .where(qPopManagement.popName.likeIgnoreCase(("%" + s1 + "%")))));

                    }
                    if (fileterColumn.equalsIgnoreCase("Product Name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve")).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("pop")).and(qInventoryMapping.product.name.likeIgnoreCase(("%" + s1 + "%")));

                    }
                    // TODO: pass mvnoID manually 6/5/2025
                    if (getMvnoIdFromCurrentStaff(null) != 1) {
                        // TODO: pass mvnoID manually 6/5/2025
                        booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                    }
                }
                if (!isSerelized) {
                    if (fileterColumn.equalsIgnoreCase("name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve")).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("pop")).and(qInventoryMapping.ownerId.in(
                                JPAExpressions.select(qPopManagement.id)
                                        .from(qPopManagement)
                                        .where(qPopManagement.popName.likeIgnoreCase(("%" + s1 + "%")))));;
                     }
                    if (fileterColumn.equalsIgnoreCase("Product Name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve")).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("pop")).and(qInventoryMapping.product.name.likeIgnoreCase("%"+s1+"%"));

                    }
                    // TODO: pass mvnoID manually 6/5/2025
                    if (getMvnoIdFromCurrentStaff(null) != 1) {
                        // TODO: pass mvnoID manually 6/5/2025
                        booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                    }
                }

                Page<InventoryMapping> inventoryMappingPage = inventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (inventoryMappingPage.getSize() != 0) {
                    inventoryMappingPage.stream().forEach(inventoryMapping -> {
                        PopManagement popManagement = popManagementRepository.findById(inventoryMapping.getOwnerId()).get();
                        inventoryMapping.setPopName(popManagement.getPopName());
                    });
                }
                if (null != inventoryMappingPage && 0 < inventoryMappingPage.getSize()) {
                    genericDataDTO.setDataList(inventoryMappingPage.getContent().stream().map(data -> inventoryMappingMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setTotalRecords(inventoryMappingPage.getTotalElements());
                    genericDataDTO.setPageRecords(inventoryMappingPage.getNumberOfElements());
                    genericDataDTO.setCurrentPageNumber(inventoryMappingPage.getNumber() + 1);
                    genericDataDTO.setTotalPages(inventoryMappingPage.getTotalPages());
                }
                return genericDataDTO;
            }

            if(fileterName.equalsIgnoreCase("Service Area")) {
                GenericDataDTO genericDataDTO = new GenericDataDTO();
                QInventoryMapping qInventoryMapping=QInventoryMapping.inventoryMapping;
                QServiceArea qServiceArea=QServiceArea.serviceArea;
                JPAQuery<?> query = new JPAQuery<>(entityManager);
                BooleanExpression booleanExpression = qInventoryMapping.isNotNull();
                if(isSerelized) {
                    if (fileterColumn.equalsIgnoreCase("name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve")).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("Service Area")).and(qInventoryMapping.ownerId.in(
                                JPAExpressions.select(qServiceArea.id)
                                        .from(qServiceArea)
                                        .where(qServiceArea.name.likeIgnoreCase(("%" + s1 + "%")))));

                    }
                    if (fileterColumn.equalsIgnoreCase("Product Name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve")).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("Service Area")).and(qInventoryMapping.product.name.likeIgnoreCase("%"+s1+"%"));

                    }
                    // TODO: pass mvnoID manually 6/5/2025
                    if (getMvnoIdFromCurrentStaff(null) != 1) {
                        // TODO: pass mvnoID manually 6/5/2025
                        booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                    }
                }
                if (!isSerelized) {
                    if (fileterColumn.equalsIgnoreCase("name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve")).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isEmpty()).and(qInventoryMapping.ownerType.containsIgnoreCase("Service Area")).and(qInventoryMapping.ownerId.in(
                                JPAExpressions.select(qServiceArea.id)
                                        .from(qServiceArea)
                                        .where(qServiceArea.name.likeIgnoreCase(("%" + s1 + "%")))));
                    }
                    if (fileterColumn.equalsIgnoreCase("Product Name")) {
                        booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve")).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isEmpty()).and(qInventoryMapping.ownerType.containsIgnoreCase("Service Area")).and(qInventoryMapping.product.name.likeIgnoreCase("%"+s1+"%"));

                    }
                    // TODO: pass mvnoID manually 6/5/2025
                    if (getMvnoIdFromCurrentStaff(null) != 1) {
                        // TODO: pass mvnoID manually 6/5/2025
                        booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                    }
                }

                Page<InventoryMapping> inventoryMappingPage = inventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (inventoryMappingPage.getSize() != 0) {
                    inventoryMappingPage.stream().forEach(inventoryMapping -> {
                        ServiceArea serviceArea = serviceAreaRepository.findById(inventoryMapping.getOwnerId()).get();
                        inventoryMapping.setServiceAreaName(serviceArea.getName());
                    });
                }
                if (null != inventoryMappingPage && 0 < inventoryMappingPage.getSize()) {
                    genericDataDTO.setDataList(inventoryMappingPage.getContent().stream().map(data -> inventoryMappingMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                    genericDataDTO.setTotalRecords(inventoryMappingPage.getTotalElements());
                    genericDataDTO.setPageRecords(inventoryMappingPage.getNumberOfElements());
                    genericDataDTO.setCurrentPageNumber(inventoryMappingPage.getNumber() + 1);
                    genericDataDTO.setTotalPages(inventoryMappingPage.getTotalPages());
                }
                return genericDataDTO;
            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }



    @Transactional
    public InwardDto saveEntity(InwardDto entity, Boolean fromOutward, Boolean isReturned) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        Product product = productRepository.findById(entity.getProductId().getId()).get();
        String uom = product.getProductCategory().getUnit();
        InwardDto inwardDto = null;
        try {
                entity.setInwardNumber(getRandomenumber("IN", "-", ""));
            entity.setQty(0L);
            entity.setUnusedQty(0L);
            if (uom.equalsIgnoreCase("kilometer")) {
                entity.setInTransitQty(1000 * entity.getInTransitQty());
            } else {
                entity.setInTransitQty(entity.getInTransitQty());
            }
            entity.setUsedQty(0L);
            entity.setOutTransitQty(0L);
            entity.setRejectedQty(0L);
            entity.setApprovalStatus("Pending");
            entity.setAssignNonSerializedItemQty(0L);
            if(!(entity.getTotalMacSerial() != null && entity.getTotalMacSerial() != 0))
                entity.setTotalMacSerial(0L);
            if(entity.getType() .equalsIgnoreCase(CommonConstants.NEW))
                entity.setType(CommonConstants.NEW);
            else if (entity.getType().equalsIgnoreCase(CommonConstants.REFURBISHED))
                entity.setType(CommonConstants.REFURBISHED);
            else if (entity.getType().equalsIgnoreCase(CommonConstants.OLD))
                entity.setType(CommonConstants.OLD);
            if (!isReturned)
                entity.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);
            inwardDto = super.saveEntity(entity);
            //messageSender.send(inwardDto, RabbitMqConstants.QUEUE_SEND_INWARD_TO_INTEGRATOIN);
            saveProductOwnerAfterInward(entity, fromOutward);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inwardDto;
    }

    @Transactional
    public InwardDto saveEntityFromRms(InwardDto entity, Boolean fromOutward, Boolean isReturned) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
//        Product product = productRepository.findById(entity.getProductId().getId()).get();
        Product product = productRepository.findByName(entity.getProductId().getName());
        String uom = product.getProductCategory().getUnit();
        InwardDto inwardDto = null;
        try {
            entity.setInwardNumber(getRandomenumber("IN", "-", ""));
            entity.setUnusedQty(0L);
            if (uom.equalsIgnoreCase("kilometer")) {
                entity.setInTransitQty(1000 * entity.getInTransitQty());
            } else {
                entity.setInTransitQty(entity.getInTransitQty());
            }
            entity.setUsedQty(0L);
            entity.setOutTransitQty(0L);
            entity.setRejectedQty(0L);
            entity.setAssignNonSerializedItemQty(0L);
            if(!(entity.getTotalMacSerial() != null && entity.getTotalMacSerial() != 0))
                entity.setTotalMacSerial(0L);
            if(entity.getType() .equalsIgnoreCase(CommonConstants.NEW))
                entity.setType(CommonConstants.NEW);
            else if (entity.getType().equalsIgnoreCase(CommonConstants.REFURBISHED))
                entity.setType(CommonConstants.REFURBISHED);
            else if (entity.getType().equalsIgnoreCase(CommonConstants.OLD))
                entity.setType(CommonConstants.OLD);
            if (!isReturned)
                entity.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);
            //inwardDto = super.saveEntity(entity);
            entity.setProductId(product);
            inwardDto = inwardMapper.domainToDTO( inwardRepository.save(inwardMapper.dtoToDomain(entity,new CycleAvoidingMappingContext())),new CycleAvoidingMappingContext()) ;
            saveProductOwnerAfterInwardFromRms(entity, fromOutward);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inwardDto;
    }

    public List<Inward> getAllInwardByProductAndStaff(Long productId, Long staffId) {
        QInward qInward = QInward.inward;
        JPAQuery<Inward> query = new JPAQuery<>(entityManager);
        List<Inward> inwardList = new ArrayList<>();
        BooleanExpression booleanExpression = qInward.isNotNull()
                .and(qInward.productId.id.eq(productId))
                .and(qInward.destinationType.equalsIgnoreCase(CommonConstants.STAFF))
                .and(qInward.destinationId.eq(staffId))
                .and(qInward.isDeleted.eq(false))
                .and(qInward.productId.productCategory.type.eq(CommonConstants.CUSTOMER_BIND))
                .and(qInward.approvalStatus.contains("Approve"));
        List<Tuple> result = query.select(qInward.id, qInward.inwardNumber, qInward.unusedQty, qInward.mvnoId).from(qInward).where(booleanExpression).fetch();
        if (!result.isEmpty()) {
            result.forEach(tuple -> {
                Inward inward = new Inward();
                inward.setId(tuple.get(qInward.id));
                inward.setInwardNumber(tuple.get(qInward.inwardNumber));
                inward.setUnusedQty(tuple.get(qInward.unusedQty));
                inward.setMvnoId(tuple.get(qInward.mvnoId));
                inwardList.add(inward);
            });
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return inwardList;
        else
            // TODO: pass mvnoID manually 6/5/2025
            return inwardList.stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList())
                    .stream().filter(inward -> inward.getUnusedQty() > 0).collect(Collectors.toList());
    }
    public List<Inward> getAllNetworkBindInwards(Long productId, Long staffId) {
        QInward qInward = QInward.inward;
        JPAQuery<Inward> query = new JPAQuery<>(entityManager);
        List<Inward> inwardList = new ArrayList<>();
        BooleanExpression booleanExpression = qInward.isNotNull()
                .and(qInward.productId.id.eq(productId))
                .and(qInward.destinationType.equalsIgnoreCase(CommonConstants.STAFF))
                .and(qInward.destinationId.eq(staffId))
                .and(qInward.isDeleted.eq(false))
                .and((qInward.productId.productCategory.type.eq(CommonConstants.NA))
                        .or(qInward.productId.productCategory.type.eq(CommonConstants.NETWORK_BIND)))
                .and(qInward.approvalStatus.contains("Approve"));
        List<Tuple> result = query.select(qInward.id, qInward.inwardNumber, qInward.unusedQty, qInward.mvnoId).from(qInward).where(booleanExpression).fetch();
        if (!result.isEmpty()) {
            result.forEach(tuple -> {
                Inward inward = new Inward();
                inward.setId(tuple.get(qInward.id));
                inward.setInwardNumber(tuple.get(qInward.inwardNumber));
                inward.setUnusedQty(tuple.get(qInward.unusedQty));
                inward.setMvnoId(tuple.get(qInward.mvnoId));
                inwardList.add(inward);
            });
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return inwardList;
        else
            // TODO: pass mvnoID manually 6/5/2025
            return inwardList.stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
    }



    public List<Inward> getAllNetworkBindandCustomerandPopInwards(Long productId, Long staffId) {
        QInward qInward = QInward.inward;
        JPAQuery<Inward> query = new JPAQuery<>(entityManager);
        List<Inward> inwardList = new ArrayList<>();
        BooleanExpression booleanExpression = qInward.isNotNull()
                .and(qInward.productId.id.eq(productId))
                .and(qInward.destinationType.equalsIgnoreCase(CommonConstants.STAFF))
                .and(qInward.destinationId.eq(staffId))
                .and(qInward.isDeleted.eq(false))
                .and((qInward.productId.productCategory.type.eq(CommonConstants.NA))
                        .or(qInward.productId.productCategory.type.eq(CommonConstants.NETWORK_BIND))
                        .or(qInward.productId.productCategory.type.eq(CommonConstants.CUSTOMER_BIND)))
                .and(qInward.approvalStatus.contains("Approve"));
        List<Tuple> result = query.select(qInward.id, qInward.inwardNumber, qInward.unusedQty, qInward.mvnoId).from(qInward).where(booleanExpression).fetch();
        if (!result.isEmpty()) {
            result.forEach(tuple -> {
                Inward inward = new Inward();
                inward.setId(tuple.get(qInward.id));
                inward.setInwardNumber(tuple.get(qInward.inwardNumber));
                inward.setUnusedQty(tuple.get(qInward.unusedQty));
                inward.setMvnoId(tuple.get(qInward.mvnoId));
                inwardList.add(inward);
            });
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return inwardList;
        else
            // TODO: pass mvnoID manually 6/5/2025
            return inwardList.stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
    }


    @Transactional
    public InwardDto updateEntity(InwardDto entity, Boolean fromOutward, Boolean isReturned) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [updateEntity()] ";
        Inward inward = inwardRepository.findById(entity.id).get();
        InwardDto inwardDto = null;
        try {
            entity.setQty(0L);
            entity.setUnusedQty(0L);
            entity.setInTransitQty(entity.getInTransitQty());
            entity.setUsedQty(0L);
            entity.setOutTransitQty(0L);
            entity.setRejectedQty(0L);
            entity.setApprovalStatus("Pending");
            entity.setTotalMacSerial(inward.getTotalMacSerial());
            if (!isReturned || entity.getType().equalsIgnoreCase(CommonConstants.NEW))
                entity.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);
            inwardDto = super.updateEntity(entity);
            if (!fromOutward) {
                // Managing quantities in product owner
                ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
                List<Inward> inwardList = inwardRepository.findAllByProductId(Math.toIntExact(entity.getProductId().getId()));
//                Inward inward = inwardRepository.findById(entity.id).get();
                if (productOwner != null) {
                    productOwner.setProductId(entity.getProductId().getId());
                    productOwner.setOwnerId(entity.getDestinationId());
                    productOwner.setOwnerType(entity.getDestinationType());
                    productOwnerService.updateEntity(productOwner);
                    if (inwardList.size() == 1) {
                        productOwner.setQuantity(entity.getQty());
                        productOwner.setUnusedQty(entity.getQty());
                        productOwner.setUsedQty(entity.getUsedQty());
                        productOwner.setInTransitQty(entity.getInTransitQty());
                        productOwnerService.updateEntity(productOwner);
                    }
                    if (inwardList.size() > 1) {
                        //Set Quantity
                        if (entity.getQty() != null) {
                            if (inward.getQty() > entity.getQty()) {
                                productOwner.setQuantity(productOwner.getQuantity() - (inward.getQty() - entity.getQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else if (inward.getQty() < entity.getQty()) {
                                productOwner.setQuantity(productOwner.getQuantity() + (entity.getQty() - inward.getQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else {
                                productOwner.setQuantity(productOwner.getQuantity());
                                productOwnerService.updateEntity(productOwner);
                            }
                        }
                        //Set UnUsedQty
                        if (entity.getUnusedQty() != null) {
                            if (inward.getUnusedQty() > entity.getUnusedQty()) {
                                productOwner.setUnusedQty(productOwner.getUnusedQty() - (inward.getUnusedQty() - entity.getUnusedQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else if (inward.getUnusedQty() < entity.getUnusedQty()) {
                                productOwner.setUnusedQty(productOwner.getUnusedQty() + (entity.getUnusedQty() - inward.getUnusedQty()));
                                productOwnerService.updateEntity(productOwner);
                            }
                        }
                        //Set UsedQty
                        if (entity.getUsedQty() != null) {
                            if (inward.getUsedQty() > entity.getUsedQty()) {
                                productOwner.setUsedQty(productOwner.getUsedQty() - (inward.getUsedQty() - entity.getUsedQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else if (inward.getUsedQty() < entity.getUsedQty()) {
                                productOwner.setUsedQty(productOwner.getUsedQty() + (entity.getUsedQty() - inward.getUsedQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else {
                                productOwner.setUsedQty(productOwner.getUsedQty());
                                productOwnerService.updateEntity(productOwner);
                            }
                        }
                        //Set InTransitQty
                        if (entity.getInTransitQty() != null) {
                            if (inward.getInTransitQty() > entity.getInTransitQty()) {
                                productOwner.setInTransitQty(productOwner.getInTransitQty() - (inward.getInTransitQty() - entity.getInTransitQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else if (inward.getInTransitQty() < entity.getInTransitQty()) {
                                productOwner.setInTransitQty(productOwner.getInTransitQty() + (entity.getInTransitQty() - inward.getInTransitQty()));
                                productOwnerService.updateEntity(productOwner);
                            } else {
                                productOwner.setInTransitQty(productOwner.getInTransitQty());
                                productOwnerService.updateEntity(productOwner);
                            }
                        }
                    }
                }
            }
            inwardDto = super.saveEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inwardDto;
    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = inwardRepository.deleteVerify(id);
        if (count == 0) {
            flag = true;
        }
        return flag;
    }

    public GenericDataDTO deleteInward(InwardDto entityDTO) {
        try {
            List<InOutWardMACMapping> list = null;
            list = inOutWardMACService.delete(entityDTO.getId().intValue());
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setIsDeleted(true);
            }
            QItem qItem = QItem.item;
            BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.currentInwardId.eq(entityDTO.getId()));
            List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
            for (int j = 0; j < itemList.size(); j++) {
                itemList.get(j).setIsDeleted(true);
                itemRepository.save(itemList.get(j));
            }
            QProductOwner qProductOwner = QProductOwner.productOwner;
            BooleanExpression aBoolean = qProductOwner.ownerId.eq(entityDTO.getDestinationId()).and(qProductOwner.ownerType.equalsIgnoreCase(entityDTO.getDestinationType())).and(qProductOwner.productId.eq(entityDTO.getProductId().getId()));
            List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(aBoolean));
            for (int k= 0 ; k < productOwnerList.size(); k++) {
                productOwnerList.get(k).setInTransitQty(productOwnerList.get(k).getInTransitQty() - entityDTO.getInTransitQty());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    //Get All Inward based on Warehouse
    public List<Inward> getAllWarehouseForInward(String inwardNumber) {
        QWareHouse qWareHouse = QWareHouse.wareHouse;
        QWareHouseServiceAreaMapping qWareHouseServiceAreaMapping = QWareHouseServiceAreaMapping.wareHouseServiceAreaMapping;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        List<Inward> paginationList = null;
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
                if (inwardNumber != null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    paginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(inwardNumber, warehouseResult, warehouseDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                } else {
                    // TODO: pass mvnoID manually 6/5/2025
                    paginationList = inwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(warehouseResult, warehouseDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                }
            }
        }
        return paginationList;
    }

    //Get All Inward based on POP
    public List<Inward> getAllPOPForInward(String inwardNumber) {
        QPopManagement qPopManagement = QPopManagement.popManagement;
        QPopServiceAreaMapping qPopServiceAreaMapping = QPopServiceAreaMapping.popServiceAreaMapping;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        List<Inward> paginationList = null;
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
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            if (inwardNumber != null) {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(inwardNumber, popResult, popDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            } else {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = inwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(popResult, popDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            }
        }
        return paginationList;
    }

    public List<Inward> getAllStaffForInward(String inwardNumber) {
        String status = "ACTIVE";
        List<Long> resultStaffId = new ArrayList<>();
        List<Inward> paginationList = null;
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
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            if (inwardNumber != null) {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(inwardNumber, resultStaffId, staffDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            } else {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = inwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(resultStaffId, staffDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            }
        }
        return paginationList;
    }

    public List<Inward> getAllPartnerStaffForInward(String inwardNumber) {
        String status = "ACTIVE";
        List<Long> resultStaffId = new ArrayList<>();
        List<Inward> paginationList = null;
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            // TODO: pass mvnoID manually 6/5/2025
            List<StaffUser> staffUserList = staffUserRepository.findByIdAndStatusAndIsDeleteIsFalseAndMvnoIdIn(getLoggedInUserId(), status, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            QPartner qPartner = QPartner.partner;
            BooleanExpression booleanExpression = qPartner.isDelete.eq(false).and(qPartner.id.eq(staffUserList.get(0).getPartnerid())).and(qPartner.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS));
            List<Partner> partnerList = IterableUtils.toList(partnerRepository.findAll(booleanExpression));
            for (int i = 0; i < partnerList.size(); i++) {
                Integer partnerId = partnerList.get(i).getId();
                resultStaffId.add(Long.valueOf(partnerId));
            }
        }
        String partnerDestinationType = "Partner";
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            if (inwardNumber != null) {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(inwardNumber, resultStaffId, partnerDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            } else {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = inwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(resultStaffId, partnerDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            }
        }
        return paginationList;
    }

    public List<Inward> getAllServiceAreaForInward(String inwardNumber) {
        QStaffUser qStaffUser = QStaffUser.staffUser;
        QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping = QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        List<Inward> paginationList = null;
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
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            if (inwardNumber != null) {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = inwardRepository.findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(inwardNumber, serviceAreaStaffResult, serviceAreaDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            } else {
                // TODO: pass mvnoID manually 6/5/2025
                paginationList = inwardRepository.findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(serviceAreaStaffResult, serviceAreaDestinationType, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            }
        }
        return paginationList;
    }

    public GenericDataDTO getAssignInventories(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long staffId) {
        String SUBMODULE = getModuleNameForLog() + " [getAssignInventories()] ";
        QInward qInward = QInward.inward;
        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.destinationType.equalsIgnoreCase(CommonConstants.STAFF)).and(qInward.destinationId.eq(staffId).and(qInward.isDeleted.eq(false)));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<Inward> paginationList = null;
        try {
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qInward.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            paginationList = inwardRepository.findAll(booleanExpression, pageRequest);
            if (paginationList.getSize() > 0) {
                genericDataDTO = inwardService.makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    public GenericDataDTO getAllInventoriesByOwner(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long ownerId, String ownerType) {
        String SUBMODULE = getModuleNameForLog() + " [getAssignInventories()] ";
        QInward qInward = QInward.inward;

        BooleanExpression booleanExpression = qInward.isNotNull().and(qInward.destinationType.equalsIgnoreCase(ownerType)).and(qInward.destinationId.eq(ownerId).and(qInward.isDeleted.eq(false)));

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<Inward> paginationList = null;
        try {
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qInward.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            paginationList = inwardRepository.findAll(booleanExpression, pageRequest);
            if (paginationList.getSize() > 0) {
                genericDataDTO = inwardService.makeGenericResponse(genericDataDTO, paginationList);
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    public InwardDto saveInwardApproval(Long inwardId, String inwardApprovalStatus, String approvalRemark, Long productId) {
        try {
            QInward qInward = QInward.inward;
            BooleanExpression booleanExpression = qInward.isNotNull()
                    .and(qInward.id.eq(inwardId))
                    .and(qInward.unusedQty.eq(0L))
                    .and(qInward.qty.eq(0L))
                    .and(qInward.usedQty.eq(0L))
                    .and(qInward.outTransitQty.eq(0L))
                    .and(qInward.rejectedQty.eq(0L))
                    .and(qInward.status.eq(CommonConstants.ACTIVE_STATUS))
                    .and(qInward.approvalStatus.contains("Pending"))
                    .and(qInward.isDeleted.eq(false));
// TODO: pass mvnoID manually 6/5/2025
            List<Inward> inwardList = Lists.newArrayList(inwardRepository.findAll(booleanExpression))
                    .stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
            if (inwardList != null) {
                if (inwardList.get(0).getOutwardId() == null) {
                    if (inwardApprovalStatus.equalsIgnoreCase("Rejected")) {
                        String ownerType = null;
                        Long ownerId = null;
                        updateProductOwner(inwardId, productId, inwardApprovalStatus, ownerType, ownerId);
                        updateInOutMacMapping(inwardId, productId, inwardApprovalStatus);
                    }
                    return updateInward(inwardList, inwardApprovalStatus, approvalRemark);
                } else if (inwardList.get(0).getOutwardId() != null) {
                    if (inwardApprovalStatus.equalsIgnoreCase("Rejected")) {
                        String ownerType = inwardList.get(0).getOutwardId().getSourceType();
                        Long ownerId = inwardList.get(0).getOutwardId().getSourceId();
                        updateProductOwner(inwardId, productId, inwardApprovalStatus, ownerType, ownerId);
                        if(inwardApprovalStatus.equalsIgnoreCase("Rejected")) {
                            updateItem(inwardId);
                        }
                        updateInOutMacMapping(inwardId, productId, inwardApprovalStatus);
                        updateInOutMacMappingAfterInOutward(inwardId, productId, inwardApprovalStatus);
                        nonSerializedItemHierarchyService.updateNonSerializedItemHierarchy(inwardId, productId);
                    }
                    return saveInwardByOutwardApproval(inwardList, inwardApprovalStatus, approvalRemark);
                }
            } else {
                throw new Exception("No record found!");
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "No record found with this inwardId", null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public InwardDto saveInwardByOutwardApproval(List<Inward> inwardList, String inwardApprovalStatus, String approval_remark) {
        try {
            //Get inward which is created by outward Id.
            Long outwardId = inwardList.get(0).getOutwardId().getId();
            Integer mvnoId = inwardList.get(0).getMvnoId();
            QInward qInward = QInward.inward;
            BooleanExpression booleanExpression = qInward.isNotNull()
                    .and(qInward.outwardId.id.eq(outwardId))
                    .and(qInward.unusedQty.eq(0L))
                    .and(qInward.qty.eq(0L))
                    .and(qInward.usedQty.eq(0L))
                    .and(qInward.outTransitQty.eq(0L))
                    .and(qInward.rejectedQty.eq(0L))
                    .and(qInward.status.eq(CommonConstants.ACTIVE_STATUS))
                    .and(qInward.isDeleted.eq(false));
            // TODO: pass mvnoID manually 6/5/2025
            List<Inward> inwardByOutwardList = Lists.newArrayList(inwardRepository.findAll(booleanExpression))
                    .stream().filter(inward -> inward.getMvnoId() == 1 || mvnoId == 1 ).collect(Collectors.toList());
            updateInward(inwardByOutwardList, inwardApprovalStatus, approval_remark);
            QOutward qOutward = QOutward.outward;
            BooleanExpression aBoolean = QOutward.outward.isNotNull()
                    .and(qOutward.id.eq(outwardId))
                    .and(qOutward.isDeleted.eq(false))
                    .and(qOutward.status.eq(CommonConstants.ACTIVE_STATUS))
                    .and(qOutward.unusedQty.eq(0L))
                    .and(qOutward.qty.eq(0L))
                    .and(qOutward.usedQty.eq(0L))
                    .and(qOutward.outTransitQty.eq(0L))
                    .and(qOutward.rejectedQty.eq(0L));
            // TODO: pass mvnoID manually 6/5/2025
            List<Outward> outwardList = Lists.newArrayList(outwardRepository.findAll(aBoolean))
                    .stream().filter(outward -> outward.getMvnoId() == 1 || mvnoId!= null || outward.getMvnoId()==1).collect(Collectors.toList());
            Long outwardInTransitQty = outwardList.get(0).getInTransitQty();
            if (inwardList != null) {
                if (inwardList.size() > 0) {
                    OutwardServiceImpl outwardService = SpringContext.getBean(OutwardServiceImpl.class);
                    OutwardDto outwardDto = outwardService.getEntityForUpdateAndDelete(outwardId,mvnoId);
                    if (inwardApprovalStatus.equalsIgnoreCase("Approve")) {
                        //Update Outward
                        outwardDto.setQty(outwardInTransitQty);
                        outwardDto.setUnusedQty(outwardInTransitQty);
                        outwardDto.setInTransitQty(0L);
                        outwardDto.setUsedQty(0L);
                        outwardDto.setOutTransitQty(0L);
                        outwardDto.setRejectedQty(0L);
                        outwardDto.setApprovalStatus("Approve");
                        outwardDto.setApprovalRemark(approval_remark);
                        //update RequestInvetoryPrpduct
                        if(outwardDto.getRequestInventoryProductId()!=null){
                            RequestInvenotryProductMapping requestInvenotryProductMapping=requestInvenotryProductMappingRepo.findById(outwardDto.getRequestInventoryProductId()).orElse(null);
                            requestInvenotryProductMapping.setRequestStatus("Close");
                            requestInvenotryProductMappingRepo.save(requestInvenotryProductMapping);
                            RequestInventory requestInventory=requestInventoryRepo.findById(outwardDto.getRequestInventoryId()).orElse(null);
                            if(requestInventory!=null){
                                List<RequestInvenotryProductMapping> requestInvenotryProductMappingList=requestInvenotryProductMappingRepo.findAllByInventoryRequestId(requestInventory.getId());
                                List<String> statusList=requestInvenotryProductMappingList.stream().map(RequestInvenotryProductMapping::getRequestStatus).collect(Collectors.toList());
                                if(statusList.stream().allMatch(str->str.equalsIgnoreCase("Close"))){
                                    requestInventory.setInventoryRequestStatus("Complted");
                                    requestInventoryRepo.save(requestInventory);
                                }else if(statusList.contains("Close") && statusList.contains("Open")){
                                    requestInventory.setInventoryRequestStatus("Partially Completed");
                                    requestInventoryRepo.save(requestInventory);
                                }
                            }
                        }
                        outwardService.updateEntity(outwardDto);
                    } else if (inwardApprovalStatus.equalsIgnoreCase("Rejected")) {
                        //Update Outward
                        outwardDto.setQty(0L);
                        outwardDto.setUnusedQty(0L);
                        outwardDto.setInTransitQty(0L);
                        outwardDto.setUsedQty(0L);
                        outwardDto.setOutTransitQty(0L);
                        outwardDto.setRejectedQty(outwardInTransitQty);
                        outwardDto.setApprovalStatus("Rejected");
                        outwardDto.setApprovalRemark(approval_remark);
                        if(outwardDto.getRequestInventoryProductId()!=null){
                            RequestInvenotryProductMapping requestInvenotryProductMapping=requestInvenotryProductMappingRepo.findById(outwardDto.getRequestInventoryProductId()).orElse(null);
                            requestInvenotryProductMapping.setRequestStatus("Rejected");
                            requestInvenotryProductMappingRepo.save(requestInvenotryProductMapping);
                            RequestInventory requestInventory=requestInventoryRepo.findById(outwardDto.getRequestInventoryId()).orElse(null);
                            List<RequestInvenotryProductMapping> requestInvenotryProductMappingList=requestInvenotryProductMappingRepo.findAllByInventoryRequestId(requestInventory.getId());
                            List<String> statusList=requestInvenotryProductMappingList.stream().map(RequestInvenotryProductMapping::getRequestStatus).collect(Collectors.toList());
                            if(statusList.stream().allMatch(str->str.equalsIgnoreCase("Rejected"))){
                                requestInventory.setInventoryRequestStatus("Rejected");
                                requestInventoryRepo.save(requestInventory);
                            }
                        }
                        outwardService.updateEntity(outwardDto);
                    }
                }
            } else {
                throw new Exception("No record found!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public InwardDto updateInward(List<Inward> inwardList, String inwardApprovalStatus, String approval_remark) {
        try {
            if (inwardList != null) {
                if (inwardList.size() > 0) {
                    Long inTransitQty = inwardList.get(0).getInTransitQty();
                    InwardDto inwardDto = null;
                    inwardDto = getEntityForUpdateAndDelete(inwardList.get(0).getId(),inwardList.get(0).getMvnoId());
                    if (inwardApprovalStatus.equalsIgnoreCase("Approve")) {
                        inwardDto.setQty(inTransitQty);
                        inwardDto.setUnusedQty(inTransitQty);
                        inwardDto.setUsedQty(0L);
                        inwardDto.setInTransitQty(0L);
                        inwardDto.setOutTransitQty(0L);
                        inwardDto.setRejectedQty(0L);
                        inwardDto.setApprovalStatus("Approve");
                        inwardDto.setApprovalRemark(approval_remark);
                        addInOwner(inwardDto , true);
                        updateEntity(inwardDto);

                        return inwardDto;
                    } else if (inwardApprovalStatus.equalsIgnoreCase("Rejected")) {
                        inwardDto.setQty(0L);
                        inwardDto.setUnusedQty(0L);
                        inwardDto.setUsedQty(0L);
                        inwardDto.setInTransitQty(0L);
                        inwardDto.setOutTransitQty(0L);
                        inwardDto.setRejectedQty(inTransitQty);
                        inwardDto.setApprovalStatus("Rejected");
                        inwardDto.setApprovalRemark(approval_remark);
                        updateEntity(inwardDto);
                        return inwardDto;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
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
            Inward inward = inwardRepository.findTopByOrderByIdDesc();
            if (inward == null) {
                flag += 1;
            } else {
                flag += inward.getId() + 1;
            }
        }
        return flag;
    }

    public List<Inward> getAllInwards() {
        QInward qInward = QInward.inward;
        BooleanExpression booleanExpression = qInward.isNotNull()
                .and(qInward.isDeleted.eq(false));
        // TODO: pass mvnoID manually 6/5/2025
        return Lists.newArrayList(inwardRepository.findAll(booleanExpression))
                .stream().filter(inward -> inward.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || inward.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
    }

    public InwardDto updateInOutMacMapping(Long inwardId, Long productId, String inwardApprovalStatus) {
        try {
            Product product = productRepository.findById(productId).get();
            boolean hasSerial = product.getProductCategory().isHasSerial();
            boolean hasMac = product.getProductCategory().isHasMac();
            boolean isTrackable = product.getProductCategory().isHasTrackable();
            if (hasMac || hasSerial) {
                if (inwardApprovalStatus.equalsIgnoreCase("Rejected")) {
                    QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                    BooleanExpression booleanExpression = qInOutWardMACMapping.inwardId.eq(inwardId).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.isReturned.eq(0));
                    List<InOutWardMACMapping> inOutWardMACMappingList = Lists.newArrayList(inOutWardMacRepo.findAll(booleanExpression));
                    for (int i = 0; i < inOutWardMACMappingList.size(); i++) {
                        InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(inOutWardMACMappingList.get(i).getId()).get();
                        inOutWardMACMapping.setInwardId(inOutWardMACMapping.getInwardId());
                        inOutWardMACMapping.setMacAddress(inOutWardMACMapping.getMacAddress());
                        inOutWardMACMapping.setStatus(inOutWardMACMapping.getStatus());
                        inOutWardMACMapping.setSerialNumber(inOutWardMACMapping.getSerialNumber());
                        inOutWardMACMapping.setIsForwarded(-1);
                        inOutWardMACMapping.setIsReturned(inOutWardMACMapping.getIsReturned());
                        inOutWardMACMapping.setItemId(inOutWardMACMapping.getItemId());
                        if (inOutWardMACMapping.getInwardIdOfOutward() != null) {
                            inOutWardMACMapping.setInwardIdOfOutward(inOutWardMACMapping.getInwardIdOfOutward());
                        }
                        inOutWardMacRepo.save(inOutWardMACMapping);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public InwardDto updateInOutMacMappingAfterInOutward(Long inwardId, Long productId, String inwardApprovalStatus) {
        try {
            Product product = productRepository.findById(productId).get();
            boolean hasSerial = product.getProductCategory().isHasSerial();
            boolean hasMac = product.getProductCategory().isHasMac();
            boolean isTrackable = product.getProductCategory().isHasTrackable();
            if (hasMac || hasSerial) {
                if (inwardApprovalStatus.equalsIgnoreCase("Rejected")) {
                    QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                    BooleanExpression booleanExpression = qInOutWardMACMapping.inwardIdOfOutward.eq(inwardId).and(qInOutWardMACMapping.isForwarded.eq(1)).and(qInOutWardMACMapping.isReturned.eq(0));
                    List<InOutWardMACMapping> inOutWardMACMappingList = Lists.newArrayList(inOutWardMacRepo.findAll(booleanExpression));
                    for (int i = 0; i < inOutWardMACMappingList.size(); i++) {
                        InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(inOutWardMACMappingList.get(i).getId()).get();
                        inOutWardMACMapping.setInwardId(inOutWardMACMapping.getInwardId());
                        inOutWardMACMapping.setMacAddress(inOutWardMACMapping.getMacAddress());
                        inOutWardMACMapping.setStatus(inOutWardMACMapping.getStatus());
                        inOutWardMACMapping.setSerialNumber(inOutWardMACMapping.getSerialNumber());
                        inOutWardMACMapping.setIsForwarded(0);
                        inOutWardMACMapping.setIsReturned(0);
                        inOutWardMACMapping.setItemId(inOutWardMACMapping.getItemId());
                        if (inOutWardMACMapping.getInwardIdOfOutward() != null) {
                            inOutWardMACMapping.setInwardIdOfOutward(null);
                        }
                        inOutWardMacRepo.save(inOutWardMACMapping);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public ProductOwner updateProductOwner(Long inwardId, Long productId, String inwardApprovalStatus, String ownerType, Long ownerId) {
        try{
            if (inwardApprovalStatus.equalsIgnoreCase("Rejected")) {
                Inward inward = inwardRepository.findById(inwardId).get();
                QProductOwner qProductOwner = QProductOwner.productOwner;
                BooleanExpression booleanExpression = qProductOwner.productId.eq(productId).and(qProductOwner.ownerId.eq(inward.getDestinationId())).and(qProductOwner.ownerType.equalsIgnoreCase(inward.getDestinationType()));
                List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(booleanExpression));
                for (int i=0; i<productOwnerList.size(); i++) {
                    productOwnerList.get(i).setInTransitQty(productOwnerList.get(i).getInTransitQty() - inward.getInTransitQty());
                }
                if (ownerType!=null && ownerId!= null) {
                    BooleanExpression booleanExpressionSource = qProductOwner.productId.eq(productId).and(qProductOwner.ownerId.eq(ownerId)).and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
                    List<ProductOwner> productOwnerListSource = IterableUtils.toList(productOwnerRepository.findAll(booleanExpressionSource));
                    for (int i=0; i<productOwnerListSource.size(); i++) {
                        productOwnerListSource.get(i).setUnusedQty(productOwnerListSource.get(i).getUnusedQty() + inward.getInTransitQty());
                        productOwnerListSource.get(i).setUsedQty(productOwnerListSource.get(i).getUsedQty() - inward.getInTransitQty());
                    }
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void updateItem(Long inwardId) {
        try {
            Inward inward = inwardRepository.findById(inwardId).get();
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull();
            booleanExpression = booleanExpression.and(qInOutWardMACMapping.inwardId.in(inwardId).and(qInOutWardMACMapping.isDeleted.eq(false)));
            List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(booleanExpression);
            if (inOutWardMACMappingList.size() != 0) {
                for (int i=0; i<inOutWardMACMappingList.size(); i++) {
                    Item item = itemRepository.findById(inOutWardMACMappingList.get(i).getItemId()).get();
                    item.setOwnerId(inward.getSourceId());
                    item.setOwnerType(inward.getSourceType());
                    itemRepository.save(item);
                }
            }

        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
    @Transactional
    public InwardDto saveInwardOfOutwardEntity(InwardDto entity, Boolean fromOutward, Boolean isReturned) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
        InwardDto inwardDto = null;
        try {
            entity.setInwardNumber(getRandomenumber("IN", "-", ""));
            //entityDTO.unusedQty=entityDTO.getQty();
            entity.setQty(0L);
            entity.setUnusedQty(0L);
            entity.setInTransitQty(entity.getInTransitQty());
            entity.setUsedQty(0L);
            entity.setOutTransitQty(0L);
            entity.setRejectedQty(0L);
            entity.setAssignNonSerializedItemQty(0L);
            entity.setApprovalStatus("Pending");
            if(!(entity.getTotalMacSerial() != null && entity.getTotalMacSerial() != 0))
                entity.setTotalMacSerial(0L);
            if (!isReturned)
                entity.setCategoryType(CommonConstants.FORWARDED_INWARD_TYPE);
            inwardDto = super.saveEntity(entity);

            if (!fromOutward) {
                // Managing quantities in product owner
                ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
                if (productOwner != null) {
                    productOwner.setQuantity(productOwner.getQuantity() + entity.getQty());
                    productOwner.setUnusedQty(productOwner.getUnusedQty() + entity.getQty());
                    productOwner.setUsedQty(productOwner.getUsedQty());
                    productOwner.setInTransitQty(productOwner.getInTransitQty() + entity.getInTransitQty());
                    productOwner.setProductId(entity.getProductId().getId());
                    productOwner.setOwnerId(entity.getDestinationId());
                    productOwner.setOwnerType(entity.getDestinationType());
                    productOwnerService.updateEntity(productOwner);
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
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return inwardDto;
    }
    public void addInOwner(InwardDto entity, Boolean fromOutward) throws Exception {
        if (fromOutward) {
            // Managing quantities in product owner
            Inward inward = inwardRepository.findById(entity.id).get();
            ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
            if (productOwner != null) {
                updateProductOwnerByOutwardId(productOwner, inward, entity);
            }
        }
    }
    public void updateProductOwnerByOutwardId(ProductOwnerDto productOwner, Inward inward, InwardDto entity) throws Exception {
        if (entity.getOutwardId() == null && productOwner.getQuantity() == 0) {
            productOwner.setQuantity(entity.getQty());
            productOwner.setUnusedQty(entity.getQty());
            productOwner.setUsedQty(entity.getUsedQty());
            productOwner.setInTransitQty(entity.getInTransitQty());
            productOwnerService.updateEntity(productOwner);
        } else {
            updateUnUsedQty(inward, entity);
        }
    }
    public void updateUnUsedQty(Inward inward, InwardDto entity) throws Exception {
        ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
        //Set UnUsedQty
        if (entity.getUnusedQty() != null) {
            if (inward.getUnusedQty() > entity.getUnusedQty()) {
                productOwner.setUnusedQty(productOwner.getUnusedQty() - (inward.getUnusedQty() - entity.getUnusedQty()));
                productOwnerService.updateEntity(productOwner);
            } else if (inward.getUnusedQty() < entity.getUnusedQty()) {
                productOwner.setUnusedQty(productOwner.getUnusedQty() + (entity.getUnusedQty() - inward.getUnusedQty()));
                productOwnerService.updateEntity(productOwner);
            }
        }
        updateUsedQty(inward, entity);
    }

    public void updateUsedQty(Inward inward, InwardDto entity) throws Exception {
        ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
        //Set UsedQty
        if (entity.getUsedQty() != null)  {
            if (inward.getUsedQty() > entity.getUsedQty()) {
                productOwner.setUsedQty(productOwner.getUsedQty() - (inward.getUsedQty() - entity.getUsedQty()));
                productOwnerService.updateEntity(productOwner);
            } else if (inward.getUsedQty() < entity.getUsedQty()) {
                productOwner.setUsedQty(productOwner.getUsedQty() + (entity.getUsedQty() - inward.getUsedQty()));
                productOwner.setInTransitQty(productOwner.getInTransitQty() -(entity.getInTransitQty() + inward.getOutTransitQty()));
                productOwner.setUnusedQty(productOwner.getUnusedQty() - (inward.getOutTransitQty()));
                productOwnerService.updateEntity(productOwner);
            } else {
                productOwner.setUsedQty(productOwner.getUsedQty());
                productOwnerService.updateEntity(productOwner);
            }
        }
        updateInTransitQty(inward, entity);
    }

    public void updateInTransitQty(Inward inward, InwardDto entity) throws Exception {
        ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
        //Set InTransitQty
        if (entity.getInTransitQty() != null) {
            if (inward.getInTransitQty() > entity.getInTransitQty()) {
                productOwner.setInTransitQty(productOwner.getInTransitQty() - (inward.getInTransitQty() - entity.getInTransitQty()));
                productOwnerService.updateEntity(productOwner);
            } else if (inward.getInTransitQty() < entity.getInTransitQty()) {
                productOwner.setInTransitQty(productOwner.getInTransitQty() + (entity.getInTransitQty() - inward.getInTransitQty()));
                productOwnerService.updateEntity(productOwner);
            } else {
                productOwner.setInTransitQty(productOwner.getInTransitQty());
                productOwnerService.updateEntity(productOwner);
            }
        }
        updateQty(entity);
    }

    public void updateQty(InwardDto entity) throws Exception {
        ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
        productOwner.setQuantity(productOwner.getUsedQty() + productOwner.getUnusedQty());
        productOwnerService.updateEntity(productOwner);
    }

    public void saveProductOwnerAfterInward(InwardDto entity, Boolean fromOutward) throws Exception {
        Product product = productRepository.findById(entity.getProductId().getId()).get();
        String uom = product.getProductCategory().getUnit();
        if (!fromOutward) {
            // Managing quantities in product owner
            ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
            if (productOwner != null) {
                productOwner.setQuantity(productOwner.getQuantity() + entity.getQty());
                productOwner.setUnusedQty(productOwner.getUnusedQty() + entity.getQty());
                productOwner.setUsedQty(productOwner.getUsedQty());
                productOwner.setInTransitQty(productOwner.getInTransitQty() + entity.getInTransitQty());
                productOwner.setProductId(entity.getProductId().getId());
                productOwner.setOwnerId(entity.getDestinationId());
                productOwner.setOwnerType(entity.getDestinationType());
                productOwnerService.updateEntity(productOwner);
            } else {
                ProductOwnerDto productOwnerDto = new ProductOwnerDto();
                productOwnerDto.setQuantity(entity.getQty());
                productOwnerDto.setUnusedQty(entity.getQty());
                productOwnerDto.setUsedQty(entity.getUsedQty());
                if (uom.equalsIgnoreCase("kilometer")) {
                    productOwnerDto.setInTransitQty(1000 * entity.getInTransitQty());
                } else {
                    productOwnerDto.setInTransitQty(entity.getInTransitQty());
                }
                productOwnerDto.setProductId(entity.getProductId().getId());
                productOwnerDto.setOwnerId(entity.getDestinationId());
                productOwnerDto.setOwnerType(entity.getDestinationType());
                productOwnerService.saveEntity(productOwnerDto);
            }
        }
    }

    public void saveProductOwnerAfterInwardFromRms(InwardDto entity, Boolean fromOutward) throws Exception {
        Product product = productRepository.findByName(entity.getProductId().getName());
        String uom = product.getProductCategory().getUnit();
        if (!fromOutward) {
            // Managing quantities in product owner
            ProductOwnerDto productOwner = productOwnerService.findByProductIdOwnerIdAndOwnerType(entity.getProductId().getId(), entity.getDestinationId(), entity.getDestinationType());
            if (productOwner != null) {
                productOwner.setQuantity(productOwner.getQuantity() + entity.getQty());
                productOwner.setUnusedQty(productOwner.getUnusedQty() + entity.getQty());
                productOwner.setUsedQty(productOwner.getUsedQty());
                productOwner.setInTransitQty(productOwner.getInTransitQty() + entity.getInTransitQty());
                productOwner.setProductId(product.getId());
                productOwner.setOwnerId(entity.getDestinationId());
                productOwner.setOwnerType(entity.getDestinationType());
                productOwnerService.saveEntityFromRms(productOwner);
            } else {
                ProductOwnerDto productOwnerDto = new ProductOwnerDto();
                productOwnerDto.setQuantity(entity.getQty());
                productOwnerDto.setUnusedQty(entity.getQty());
                productOwnerDto.setUsedQty(entity.getUsedQty());
                if (uom.equalsIgnoreCase("kilometer")) {
                    productOwnerDto.setInTransitQty(1000 * entity.getInTransitQty());
                } else {
                    productOwnerDto.setInTransitQty(entity.getInTransitQty());
                }
                productOwnerDto.setProductId(product.getId());
                productOwnerDto.setOwnerId(entity.getDestinationId());
                productOwnerDto.setOwnerType(entity.getDestinationType());
                productOwnerService.saveEntityFromRms(productOwnerDto);
            }
        }
    }

    public InwardDto getInwardOfOutwardByOutwardId (Long outwardId,Integer mvnoId) throws Exception {
        try {
            QInward qInward = QInward.inward;
            BooleanExpression booleanExpression = qInward.isDeleted.eq(false).and(qInward.outwardId.id.eq(outwardId));
            List<Inward> inwardList = IterableUtils.toList(inwardRepository.findAll(booleanExpression));
            Long inwardId = inwardList.get(0).getId();
            return getEntityForUpdateAndDelete(inwardId,mvnoId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InwardDto getEntityById(Long id,Integer mvnoId) {

        Inward inward = inwardRepository.findById(id).get();
        InwardDto inwardDto = getMapper().domainToDTO(inward, new CycleAvoidingMappingContext());

        if (inwardDto.getDestinationType().equals("") || (inwardDto.getDestinationType() == null)) {
            inwardDto.setDestination("");
        } else if (inwardDto.getDestinationType().equalsIgnoreCase(CommonConstants.WAREHOUSE)) {
            inwardDto.setDestination(warehouseManagementRepository.getOne(inwardDto.getDestinationId()).getName());
        } else if (inwardDto.getDestinationType().equalsIgnoreCase(CommonConstants.STAFF)) {
            inwardDto.setDestination(staffUserRepository.getOne(Math.toIntExact(inwardDto.getDestinationId())).getFullName());
        } else if (inwardDto.getDestinationType().equalsIgnoreCase(CommonConstants.PARTNER)) {
            inwardDto.setDestination(partnerRepository.getOne(Math.toIntExact(inwardDto.getDestinationId())).getName());
        }

        if ((inwardDto.getSourceType() == null) || (inwardDto.getSourceType().equals(""))) {
            inwardDto.setSource("");
        } else if (inwardDto.getSourceType().equalsIgnoreCase(CommonConstants.WAREHOUSE)) {
            inwardDto.setSource(warehouseManagementRepository.getOne(inwardDto.getSourceId()).getName());
        } else if (inwardDto.getSourceType().equalsIgnoreCase(CommonConstants.STAFF)) {
            inwardDto.setSource(staffUserRepository.getOne(Math.toIntExact(inwardDto.getSourceId())).getFullName());
        } else if (inwardDto.getSourceType().equalsIgnoreCase(CommonConstants.PARTNER)) {
            inwardDto.setSource(partnerRepository.getOne(Math.toIntExact(inwardDto.getSourceId())).getName());
        }
        return inwardDto;
    }

    public void updateInwardOfOutwardStatus(Long outwardId, String status,Integer mvnoId) throws Exception {
        try{
            QInward qInward = QInward.inward;
            BooleanExpression booleanExpression = qInward.isDeleted.eq(false).and(qInward.outwardId.id.eq(outwardId));
            List<Inward> inwardList = IterableUtils.toList(inwardRepository.findAll(booleanExpression));
            Long inwardId = inwardList.get(0).getId();
            InwardDto inwardDto = getEntityForUpdateAndDelete(inwardId,mvnoId);
            inwardDto.setStatus(status);
            inwardService.updateEntity(inwardDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
