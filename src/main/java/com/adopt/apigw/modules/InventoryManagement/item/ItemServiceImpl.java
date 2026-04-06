package com.adopt.apigw.modules.InventoryManagement.item;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.fileUtillity.FileUtility;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.QCustomers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.common.StaffUserServiceAreaMapping;
import com.adopt.apigw.model.postpaid.PartnerServiceAreaMapping;
import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.model.postpaid.QPartnerServiceAreaMapping;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain.ExternalItemManagement;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.repository.ExternalItemManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.*;
import com.adopt.apigw.modules.InventoryManagement.ItemStatusMapping.ItemStatusMapping;
import com.adopt.apigw.modules.InventoryManagement.ItemStatusMapping.ItemStatusMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItem.NonSerializedItem;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItem.NonSerializedItemRepository;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItem.QNonSerializedItem;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.repository.PopManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping.ProductPlanGroupMapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping.ProductPlanGroupMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping.QProductPlanGroupMapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.domain.Productplanmapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.domain.QProductplanmapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.repository.ProductPlanMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.generateremoveInventoryRequest.GenerateRemoveRequest;
import com.adopt.apigw.modules.InventoryManagement.generateremoveInventoryRequest.GenerateRemoveRequestRepo;
import com.adopt.apigw.modules.InventoryManagement.inward.QInward;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.outward.*;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.service.PopManagementService;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardRepository;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionMappingServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionsMappingDto;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingDto;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.product.*;
import com.adopt.apigw.modules.InventoryManagement.productBundle.BulkConsumptionRepository;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategoryRepository;
import com.adopt.apigw.modules.InventoryManagement.warehouse.*;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.common.StaffUserServiceAreaMappingRepository;
import com.adopt.apigw.repository.common.StaffUserServiceRepository;
import com.adopt.apigw.repository.postpaid.PartnerServiceAreaMappingRepo;
import com.adopt.apigw.repository.postpaid.PlanServiceRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.ChargeService;
import com.adopt.apigw.service.postpaid.PartnerService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl extends ExBaseAbstractService<ItemDto, Item, Long> {

    @Autowired
    ItemMapper itemMapper;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    ChargeService chargeService;
    @Autowired
    ItemServiceImpl itemService;
    @Autowired
    private InwardServiceImpl inwardService;
    @Autowired
    InwardRepository inwardRepository;
    @Autowired
    OutwardServiceImpl outwardService;
    @Autowired
    InOutWardMacRepo inOutWardMacRepo;
    @Autowired
    InOutWardMacMapper inOutWardMacMapper;
    @Autowired
    StaffUserService staffService;
    @Autowired
    WarehouseManagementServiceImpl warehouseManagementService;
    @Autowired
    PopManagementService popManagementService;
    @Autowired
    ServiceAreaService serviceAreaService;

    @Autowired
    PartnerService partnerService;

    @Autowired
    ItemConditionMappingServiceImpl itemConditionMappingService;

    @Autowired
    ItemWarrantyMappingServiceImpl itemWarrantyMappingService;

    @Autowired
    ItemWarrantyMappingRepository itemWarrantyMappingRepository;

    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private ItemConditionMappingRepository itemConditionMappingRepository;

    @Autowired
    ItemStatusMappingRepo itemStatusMappingRepo;

    @Autowired
    BulkConsumptionRepository bulkConsumptionRepository;

    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    @Autowired
    PopManagementRepository popManagementRepository;
    @Autowired
    InOutWardMACService inOutWardMACService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;

    @Autowired
    ProductPlanMappingRepository productPlanMappingRepository;

    @Autowired
    ProductPlanGroupMappingRepository productPlanGroupMappingRepository;

    @Autowired
    NonSerializedItemRepository nonSerializedItemRepository;

    @Autowired
    CustomerInventoryMappingMapper customerInventoryMappingMapper;
    @Autowired
    OutwardRepository outwardRepository;
    @Autowired
    ExternalItemManagementRepository externalItemManagementRepository;
    @Autowired
    PlanServiceRepository planServiceRepository;
    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;

    @Autowired
    WareHouseManagmentServiceAreamappingRepo wareHouseManagmentServiceAreamappingRepo;

    @Autowired
    CustomersService customersService;

    @Autowired
    StaffUserRepository staffUserRepository;

    public static final String MODULE = "[CreditDocService]";

    public String PATH;


    @Autowired
    ProductServiceImpl productService;

    public ItemServiceImpl(ItemRepository itemRepository, IBaseMapper<ItemDto, Item> mapper) {
        super(itemRepository, mapper);
    }

    private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private GenerateRemoveRequestRepo generateRemoveRequestRepo;

    @Override
    public String getModuleNameForLog() {
        return "[ProductServiceImpl]";
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [getListByPageAndSizeAndSortByAndOrderBy()] ";
        QProduct qProduct = QProduct.product;
        BooleanExpression booleanExpression = qProduct.isNotNull().and(qProduct.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<Item> paginationList = null;
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qProduct.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//            if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//                paginationList = itemRepository.findAll(pageRequest);
//            } else {
            paginationList = itemRepository.findAll(booleanExpression, pageRequest);
//            }

            if (paginationList.getSize() > 0) {
                makeGenericResponse(genericDataDTO, paginationList);
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
            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getProductList(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Unable to Search :  Response : {{}};Error :{} ;Exception:{}", APIConstants.FAIL, HttpStatus.NOT_ACCEPTABLE, ex.getStackTrace());
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getProductList(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getProductList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Page<Item> productList;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1)
                productList = itemRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(name, pageRequest);
            else
                // TODO: pass mvnoID manually 6/5/2025
                productList = itemRepository.findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(name, pageRequest, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
            if (null != productList && 0 < productList.getSize()) {
                makeGenericResponse(genericDataDTO, productList);
            }
            if (productList.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Data Not Found.");
            }
        } catch (Exception ex) {
            logger.error("Unable to Fetch all charge by Type :  Response : {{}};Error :{} ;Exception:{}", APIConstants.FAIL, HttpStatus.NOT_ACCEPTABLE, ex.getStackTrace());
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return genericDataDTO;
    }

    @Override
    public void deleteEntity(ItemDto entity) throws Exception {
        super.deleteEntity(entity);
    }

    @Override
    public ItemDto saveEntity(ItemDto entity) throws Exception {
        // TODO: pass mvnoID manually 6/5/2025
        entity.setMvnoId(getMvnoIdFromCurrentStaff(null));
        return super.saveEntity(entity);
    }


    public ItemDto saveEntityFromRms(ItemDto entity) throws Exception {
        // TODO: pass mvnoID manually 6/5/2025
        entity.setMvnoId(getMvnoIdFromCurrentStaff(null));
        return super.saveEntity(entity);
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
            if (getMvnoIdFromCurrentStaff(null) == 1) count = itemRepository.duplicateVerifyAtSave(name);
            else count = itemRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean deleteVerification(Integer id) throws Exception {
        boolean flag = false;
        Integer count = itemRepository.deleteVerify(id);
        if (count == 1) {
            flag = true;
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
            if (getMvnoIdFromCurrentStaff(null) == 1) count = itemRepository.duplicateVerifyAtSave(name);
            else count = itemRepository.duplicateVerifyAtSave(name, mvnoIds);
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) == 1)
                    countEdit = itemRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id));
                else countEdit = itemRepository.duplicateVerifyAtEdit(name, Math.toIntExact(id), mvnoIds);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

//    public GenericDataDTO getAllItemsByOwner(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long ownerId, String ownerType) throws Exception {
//        String SUBMODULE = getModuleNameForLog() + " [getAssignInventories()] ";
//        QItem qItem = QItem.item;
//        BooleanExpression booleanExpression = qItem.isNotNull().and(qItem.ownerType.equalsIgnoreCase(ownerType)).and(qItem.ownerId.eq(ownerId).and(qItem.isDeleted.eq(false)));
////        List<ServiceArea> serviceAreaList = serviceAreaService.getAllServiceAreaByStaffId();
////        List<StaffUser> staffUserList = staffUserServiceAreaMappingRepository.find();
////        if (ownerType.equalsIgnoreCase(CommonConstants.STAFF)){
////            booleanExpression = booleanExpression.and()
////        }
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        PageRequest pageRequest;
//        Page<Item> paginationList = null;
//        try {
//            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
//            // TODO: pass mvnoID manually 6/5/2025
//            if (getMvnoIdFromCurrentStaff(null) != 1)
//                // TODO: pass mvnoID manually 6/5/2025
//                booleanExpression = booleanExpression.and(qItem.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
//            paginationList = itemRepository.findAll(booleanExpression, pageRequest);
//
//            List<ItemDto> dto = paginationList.get().map(item -> itemMapper.domainToDTO(item, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//
//            List<ItemDto> itemDtoList = new ArrayList<>();
//            for (ItemDto itemDto : dto) {
//
//                itemDto.setCurrentInwardNumber(inwardRepository.findById(itemDto.getCurrentInwardId()).get().getInwardNumber());
//                itemDto.setProductName(productService.getEntityById(itemDto.getProductId().longValue()).getName());
//                System.out.println(itemDto.getId());
//                System.out.println("*********************()()()()()()");
//                if (!itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).isEmpty()) {
//                    itemDto.setFilename(itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).get(0).getFilename());
//                    itemDto.setItemConditionId(itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).get(0).getId());
//                }
//                if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.STAFF))
//                    itemDto.setOwnerName(staffService.get(itemDto.getOwnerId().intValue(),getMvnoIdFromCurrentStaff(customerIds.get(0))).getFirstname());
//                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.WAREHOUSE))
//                    itemDto.setOwnerName(warehouseManagementService.getEntityById(itemDto.getOwnerId().longValue()).getName());
//                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.POP))
//                    itemDto.setOwnerName(popManagementService.getEntityById(itemDto.getOwnerId().longValue()).getName());
//                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.SERVICE_AREA))
//                    itemDto.setOwnerName(serviceAreaService.getByID(itemDto.getOwnerId().longValue()).getName());
//                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.PARTNER))
//                    itemDto.setOwnerName(partnerService.get(itemDto.getOwnerId().intValue()).getName());
//
//                itemDtoList.add(itemDto);
//            }
//
//            genericDataDTO.setDataList(itemDtoList);
//
//            genericDataDTO.setTotalRecords(paginationList.getTotalElements());
//            genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
//            genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
//            genericDataDTO.setTotalPages(paginationList.getTotalPages());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return genericDataDTO;
//    }

    @Transactional
    public String returnItem(List<ItemReturnDTO> itemReturnDTOList) {
        // Considering all itemIds here are of same inward
        List<Long> itemIds = new ArrayList<>();
        for(ItemReturnDTO itemReturnDTO : itemReturnDTOList){
            itemIds.add(itemReturnDTO.getId());
        }
        try {
            List<Item> itemList = itemRepository.findAllById(itemIds);
            Map<String, String> serialRemarksMap = new HashMap<>();
            for (int i = 0; i < itemList.size(); i++) {
                serialRemarksMap.put(itemList.get(i).getSerialNumber(), itemReturnDTOList.get(i).getRemarks());
            }
            Inward inward = inwardRepository.findById(itemList.get(0).getCurrentInwardId()).get();

            // Outward by taking destination as inward's source and source as inward's destination
            OutwardDto outwardDto = new OutwardDto();
            outwardDto.setQty((long) itemList.size());
            outwardDto.setStatus(inward.getStatus());
            outwardDto.setProductId(inward.getProductId());
            outwardDto.setMvnoId(inward.getMvnoId());
            outwardDto.setOutwardDateTime(LocalDateTime.now());
            outwardDto.setIsDeleted(false);
            outwardDto.setInwardId(inward);
            outwardDto.setSourceType(inward.getDestinationType());
            outwardDto.setSourceId(inward.getDestinationId());
            outwardDto.setDestinationType(inward.getSourceType());
            outwardDto.setDestinationId(inward.getSourceId());
            outwardDto.setInTransitQty((long) itemIds.size());
            outwardDto.setCategoryType(CommonConstants.RETURNED_INWARD_TYPE);
            outwardDto.setType(CommonConstants.NEW);
            //Return Outward and Inward
            OutwardDto savedOutward = outwardService.saveEntity(outwardDto, true);
            QInward qInward = QInward.inward;
            BooleanExpression inwardBoolExp = qInward.isNotNull();
            inwardBoolExp = inwardBoolExp.and(qInward.outwardId.id.eq(savedOutward.getId()));
            Inward inwardOfSavedOutward = inwardRepository.findOne(inwardBoolExp).get();

            // Get List of serials by current items productId, inwardId and is_forwarded = 0(items which are not forwarded and currently with destination)
            // place is_returned = 1 in them and create new mappings with by placing inwardIdOfOutward into existing records just like when we select mac from outward, new entry is created in mac,
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull();
            booleanExpression = booleanExpression.and(qInOutWardMACMapping.inwardId.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(0)));
//                    .or(qInOutWardMACMapping.inwardIdOfOutward.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(1)));
            List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(booleanExpression);
            inOutWardMACMappingList = inOutWardMACMappingList.stream().filter(macMapping -> itemList.stream().map(Item::getSerialNumber).collect(Collectors.toSet()).contains(macMapping.getSerialNumber())).collect(Collectors.toList());

            List<InOutWardMACMapping> newInOutwardMacMapping = new ArrayList<>();
            for (InOutWardMACMapping inOutWardMACMapping : inOutWardMACMappingList) {
                InOutWardMACMapingDTO macMapping = new InOutWardMACMapingDTO();
                macMapping = inOutWardMacMapper.domainToDTO(inOutWardMACMapping, new CycleAvoidingMappingContext());
                macMapping.setId(null);
                macMapping.setInwardId(inwardOfSavedOutward.getId());
                macMapping.setOutwardId(savedOutward.getId());
                macMapping.setIsForwarded(0);
                InOutWardMACMapingDTO finalMacMapping = macMapping;
                String remark = serialRemarksMap.get(serialRemarksMap.keySet().stream().filter(s -> s.equalsIgnoreCase(finalMacMapping.getSerialNumber())).collect(Collectors.toList()).get(0));
                macMapping.setRemark(remark);
                newInOutwardMacMapping.add(inOutWardMacMapper.dtoToDomain(macMapping, new CycleAvoidingMappingContext()));
            }
            inOutWardMacRepo.saveAll(newInOutwardMacMapping);
            inOutWardMACMappingList.forEach(s -> s.setIsReturned(1));
            inOutWardMACMappingList.forEach(s -> s.setIsForwarded(1));
            inOutWardMacRepo.saveAll(inOutWardMACMappingList);
            for (Item item : itemList) {
                item.setCurrentInwardId(inwardOfSavedOutward.getId());
                item.setCurrentInwardType(CommonConstants.RETURNED_INWARD_TYPE);
                item.setOwnerType(inwardOfSavedOutward.getDestinationType());
                item.setOwnerId(inwardOfSavedOutward.getDestinationId());
//                itemList.forEach(s -> s.setCurrentInwardId(inwardOfSavedOutward.getId()));
            }
            itemRepository.saveAll(itemList);

            // manage product owner quantities after return

        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
        return "Items returned";
    }
    @Transactional
    public String returnItemfromStaffremove(List<ItemReturnDTO> itemReturnDTOList) {
        // Considering all itemIds here are of same inward
        List<Long> itemIds = new ArrayList<>();
        for(ItemReturnDTO itemReturnDTO : itemReturnDTOList){
            itemIds.add(itemReturnDTO.getId());
        }
        try {
            List<Item> itemList = itemRepository.findAllById(itemIds);
          //  Map<String, String> serialRemarksMap = new HashMap<>();
            /*for (int i = 0; i < itemList.size(); i++) {
                serialRemarksMap.put(itemList.get(i).getSerialNumber(), itemReturnDTOList.get(i).getRemarks());
            }*/
            Inward inward = inwardRepository.findById(itemList.get(0).getCurrentInwardId()).get();

            // Outward by taking destination as inward's source and source as inward's destination
            OutwardDto outwardDto = new OutwardDto();
            outwardDto.setQty((long) itemList.size());
            outwardDto.setStatus(inward.getStatus());
            outwardDto.setProductId(inward.getProductId());
            outwardDto.setMvnoId(inward.getMvnoId());
            outwardDto.setOutwardDateTime(LocalDateTime.now());
            outwardDto.setIsDeleted(false);
            outwardDto.setSourceType(inward.getDestinationType());
            outwardDto.setSourceId(inward.getDestinationId());
            if(getLoggedInUser().getPartnerId() != 1) {
                outwardDto.setDestinationType(CommonConstants.PARTNER);
                outwardDto.setDestinationId(Long.valueOf(getLoggedInUser().getPartnerId()));
            } else {
                outwardDto.setDestinationType(CommonConstants.STAFF);
                outwardDto.setDestinationId(Long.valueOf(getLoggedInUser().getUserId()));
            }
            outwardDto.setInTransitQty((long) itemIds.size());
            outwardDto.setCategoryType(CommonConstants.RETURNED_INWARD_TYPE);
            outwardDto.setType(CommonConstants.NEW);
            //Return Outward and Inward
            OutwardDto savedOutward = outwardService.saveEntity(outwardDto, true);
            QInward qInward = QInward.inward;
            BooleanExpression inwardBoolExp = qInward.isNotNull();
            inwardBoolExp = inwardBoolExp.and(qInward.outwardId.id.eq(savedOutward.getId()));
            Inward inwardOfSavedOutward = inwardRepository.findOne(inwardBoolExp).get();

            // Get List of serials by current items productId, inwardId and is_forwarded = 0(items which are not forwarded and currently with destination)
            // place is_returned = 1 in them and create new mappings with by placing inwardIdOfOutward into existing records just like when we select mac from outward, new entry is created in mac,
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull();
            booleanExpression = booleanExpression.and(qInOutWardMACMapping.inwardId.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(0)));
//                    .or(qInOutWardMACMapping.inwardIdOfOutward.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(1)));
            List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(booleanExpression);
            inOutWardMACMappingList = inOutWardMACMappingList.stream().filter(macMapping -> itemList.stream().map(Item::getSerialNumber).collect(Collectors.toSet()).contains(macMapping.getSerialNumber())).collect(Collectors.toList());

            List<InOutWardMACMapping> newInOutwardMacMapping = new ArrayList<>();
            for (InOutWardMACMapping inOutWardMACMapping : inOutWardMACMappingList) {
                InOutWardMACMapingDTO macMapping = new InOutWardMACMapingDTO();
                macMapping = inOutWardMacMapper.domainToDTO(inOutWardMACMapping, new CycleAvoidingMappingContext());
                macMapping.setId(null);
                macMapping.setInwardId(inwardOfSavedOutward.getId());
                macMapping.setOutwardId(savedOutward.getId());
                macMapping.setIsForwarded(0);
                InOutWardMACMapingDTO finalMacMapping = macMapping;
             //   String remark = serialRemarksMap.get(serialRemarksMap.keySet().stream().filter(s -> s.equalsIgnoreCase(finalMacMapping.getSerialNumber())).collect(Collectors.toList()).get(0));
             //   macMapping.setRemark(remark);
                newInOutwardMacMapping.add(inOutWardMacMapper.dtoToDomain(macMapping, new CycleAvoidingMappingContext()));
            }
            inOutWardMacRepo.saveAll(newInOutwardMacMapping);
            inOutWardMACMappingList.forEach(s -> s.setIsReturned(1));
            inOutWardMACMappingList.forEach(s -> s.setIsForwarded(1));
            inOutWardMacRepo.saveAll(inOutWardMACMappingList);
            for (Item item : itemList) {
                item.setCurrentInwardId(inwardOfSavedOutward.getId());
                item.setCurrentInwardType(CommonConstants.RETURNED_INWARD_TYPE);
                item.setOwnerType(inwardOfSavedOutward.getDestinationType());
                item.setOwnerId(inwardOfSavedOutward.getDestinationId());
                inwardService.saveInwardApproval(inwardOfSavedOutward.getId(), "Approve", "", item.getProductId());
//                itemList.forEach(s -> s.setCurrentInwardId(inwardOfSavedOutward.getId()));
            }
            itemRepository.saveAll(itemList);

            // manage product owner quantities after return

        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
        return "Items returned";
    }


    @Transactional
    public String replaceAndreturnItemfromStaffremove(List<ItemReturnDTO> itemReturnDTOList) {
        // Considering all itemIds here are of same inward
        List<Long> itemIds = new ArrayList<>();
        for(ItemReturnDTO itemReturnDTO : itemReturnDTOList){
            itemIds.add(itemReturnDTO.getId());
        }
        try {
            List<Item> itemList = itemRepository.findAllById(itemIds);
            //  Map<String, String> serialRemarksMap = new HashMap<>();
            /*for (int i = 0; i < itemList.size(); i++) {
                serialRemarksMap.put(itemList.get(i).getSerialNumber(), itemReturnDTOList.get(i).getRemarks());
            }*/
            Inward inward = inwardRepository.findById(itemList.get(0).getCurrentInwardId()).get();

            // Outward by taking destination as inward's source and source as inward's destination
            OutwardDto outwardDto = new OutwardDto();
            outwardDto.setQty((long) itemList.size());
            outwardDto.setStatus(inward.getStatus());
            outwardDto.setProductId(inward.getProductId());
            outwardDto.setMvnoId(inward.getMvnoId());
            outwardDto.setOutwardDateTime(LocalDateTime.now());
            outwardDto.setIsDeleted(false);
            outwardDto.setSourceType(inward.getDestinationType());
            outwardDto.setSourceId(inward.getDestinationId());
            if(getLoggedInUser().getPartnerId() != 1) {
                Long id=itemIds.get(0);
                QCustomerInventoryMapping qCustomerInventoryMapping=QCustomerInventoryMapping.customerInventoryMapping;
                BooleanExpression booleanExpression=qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(id)).and(qCustomerInventoryMapping.isDeleted.eq(false));
                CustomerInventoryMapping customerInventoryMapping=customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                outwardDto.setDestinationType(CommonConstants.PARTNER);
                StaffUser staffUser = staffUserRepository.findById(customerInventoryMapping.getPreviousApproveId()).get();
                outwardDto.setDestinationId(staffUser.getPartnerid().longValue());
            } else {
                Long id=itemIds.get(0);
                QCustomerInventoryMapping qCustomerInventoryMapping=QCustomerInventoryMapping.customerInventoryMapping;
                BooleanExpression booleanExpression=qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(id)).and(qCustomerInventoryMapping.isDeleted.eq(false));
                CustomerInventoryMapping customerInventoryMapping=customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                outwardDto.setDestinationType(CommonConstants.STAFF);
                outwardDto.setDestinationId(customerInventoryMapping.getPreviousApproveId().longValue());
            }
            outwardDto.setInTransitQty((long) itemIds.size());
            outwardDto.setCategoryType(CommonConstants.RETURNED_INWARD_TYPE);
            outwardDto.setType(CommonConstants.NEW);
            //Return Outward and Inward
            OutwardDto savedOutward = outwardService.saveEntity(outwardDto, true);
            QInward qInward = QInward.inward;
            BooleanExpression inwardBoolExp = qInward.isNotNull();
            inwardBoolExp = inwardBoolExp.and(qInward.outwardId.id.eq(savedOutward.getId()));
            Inward inwardOfSavedOutward = inwardRepository.findOne(inwardBoolExp).get();

            // Get List of serials by current items productId, inwardId and is_forwarded = 0(items which are not forwarded and currently with destination)
            // place is_returned = 1 in them and create new mappings with by placing inwardIdOfOutward into existing records just like when we select mac from outward, new entry is created in mac,
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull();
            booleanExpression = booleanExpression.and(qInOutWardMACMapping.inwardId.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(0)));
//                    .or(qInOutWardMACMapping.inwardIdOfOutward.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(1)));
            List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(booleanExpression);
            inOutWardMACMappingList = inOutWardMACMappingList.stream().filter(macMapping -> itemList.stream().map(Item::getSerialNumber).collect(Collectors.toSet()).contains(macMapping.getSerialNumber())).collect(Collectors.toList());

            List<InOutWardMACMapping> newInOutwardMacMapping = new ArrayList<>();
            for (InOutWardMACMapping inOutWardMACMapping : inOutWardMACMappingList) {
                InOutWardMACMapingDTO macMapping = new InOutWardMACMapingDTO();
                macMapping = inOutWardMacMapper.domainToDTO(inOutWardMACMapping, new CycleAvoidingMappingContext());
                macMapping.setId(null);
                macMapping.setInwardId(inwardOfSavedOutward.getId());
                macMapping.setOutwardId(savedOutward.getId());
                macMapping.setIsForwarded(0);
                InOutWardMACMapingDTO finalMacMapping = macMapping;
                //   String remark = serialRemarksMap.get(serialRemarksMap.keySet().stream().filter(s -> s.equalsIgnoreCase(finalMacMapping.getSerialNumber())).collect(Collectors.toList()).get(0));
                //   macMapping.setRemark(remark);
                newInOutwardMacMapping.add(inOutWardMacMapper.dtoToDomain(macMapping, new CycleAvoidingMappingContext()));
            }
            inOutWardMacRepo.saveAll(newInOutwardMacMapping);
            inOutWardMACMappingList.forEach(s -> s.setIsReturned(1));
            inOutWardMACMappingList.forEach(s -> s.setIsForwarded(1));
            inOutWardMacRepo.saveAll(inOutWardMACMappingList);
            for (Item item : itemList) {
                item.setCurrentInwardId(inwardOfSavedOutward.getId());
                item.setCurrentInwardType(CommonConstants.RETURNED_INWARD_TYPE);
                item.setOwnerType(inwardOfSavedOutward.getDestinationType());
                item.setOwnerId(inwardOfSavedOutward.getDestinationId());
                inwardService.saveInwardApproval(inwardOfSavedOutward.getId(), "Approve", "", item.getProductId());
//                itemList.forEach(s -> s.setCurrentInwardId(inwardOfSavedOutward.getId()));
            }
            itemRepository.saveAll(itemList);

            // manage product owner quantities after return

        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
        return "Items returned";
    }


    @Transactional
    public String removeAndreturnItemfromStaffremove(List<ItemReturnDTO> itemReturnDTOList, CustomerInventoryMapping customerInventoryMapping) {
        // Considering all itemIds here are of same inward
        List<Long> itemIds = new ArrayList<>();
        for(ItemReturnDTO itemReturnDTO : itemReturnDTOList){
            itemIds.add(itemReturnDTO.getId());
        }
        try {
            List<Item> itemList = itemRepository.findAllById(itemIds);
            //  Map<String, String> serialRemarksMap = new HashMap<>();
            /*for (int i = 0; i < itemList.size(); i++) {
                serialRemarksMap.put(itemList.get(i).getSerialNumber(), itemReturnDTOList.get(i).getRemarks());
            }*/
            Inward inward = inwardRepository.findById(itemList.get(0).getCurrentInwardId()).get();

            // Outward by taking destination as inward's source and source as inward's destination
            OutwardDto outwardDto = new OutwardDto();
            outwardDto.setQty((long) itemList.size());
            outwardDto.setStatus(inward.getStatus());
            outwardDto.setProductId(inward.getProductId());
            outwardDto.setMvnoId(inward.getMvnoId());
            outwardDto.setOutwardDateTime(LocalDateTime.now());
            outwardDto.setIsDeleted(false);
            outwardDto.setSourceType(inward.getDestinationType());
            outwardDto.setSourceId(inward.getDestinationId());
            if(getLoggedInUser().getPartnerId() != 1) {
                Long id=itemIds.get(0);
//                QCustomerInventoryMapping qCustomerInventoryMapping=QCustomerInventoryMapping.customerInventoryMapping;
//                BooleanExpression booleanExpression=qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(id)).and(qCustomerInventoryMapping.isDeleted.eq(false));;
//                CustomerInventoryMapping customerInventoryMapping=customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                GenerateRemoveRequest generateRemoveRequest=generateRemoveRequestRepo.findByCustomerinventoryIdAndIsDeletedFalse(customerInventoryMapping.getId());
                if (generateRemoveRequest != null) {
                    outwardDto.setDestinationType(CommonConstants.PARTNER);
                    StaffUser staffUser = staffUserRepository.findById(Math.toIntExact(generateRemoveRequest.getStaffid())).get();
                    outwardDto.setDestinationId(staffUser.getPartnerid().longValue());
                }
            } else {
                Long id=itemIds.get(0);
//                QCustomerInventoryMapping qCustomerInventoryMapping=QCustomerInventoryMapping.customerInventoryMapping;
//                BooleanExpression booleanExpression=qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.itemId.eq(id)).and(qCustomerInventoryMapping.isDeleted.eq(false));
//                CustomerInventoryMapping customerInventoryMapping=customerInventoryMappingRepo.findOne(booleanExpression).orElse(null);
                GenerateRemoveRequest generateRemoveRequest=generateRemoveRequestRepo.findByCustomerinventoryIdAndIsDeletedFalse(customerInventoryMapping.getId());
                if (generateRemoveRequest != null) {
                    outwardDto.setDestinationType(CommonConstants.STAFF);
                    outwardDto.setDestinationId(generateRemoveRequest.getStaffid());
                }
            }
            outwardDto.setInTransitQty((long) itemIds.size());
            outwardDto.setCategoryType(CommonConstants.RETURNED_INWARD_TYPE);
            outwardDto.setType(CommonConstants.NEW);
            //Return Outward and Inward
            OutwardDto savedOutward = outwardService.saveEntity(outwardDto, true);
            QInward qInward = QInward.inward;
            BooleanExpression inwardBoolExp = qInward.isNotNull();
            inwardBoolExp = inwardBoolExp.and(qInward.outwardId.id.eq(savedOutward.getId()));
            Inward inwardOfSavedOutward = inwardRepository.findOne(inwardBoolExp).get();

            // Get List of serials by current items productId, inwardId and is_forwarded = 0(items which are not forwarded and currently with destination)
            // place is_returned = 1 in them and create new mappings with by placing inwardIdOfOutward into existing records just like when we select mac from outward, new entry is created in mac,
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull();
            booleanExpression = booleanExpression.and(qInOutWardMACMapping.inwardId.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(0)));
//                    .or(qInOutWardMACMapping.inwardIdOfOutward.eq(inward.getId()).and(qInOutWardMACMapping.isForwarded.eq(1)));
            List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(booleanExpression);
            inOutWardMACMappingList = inOutWardMACMappingList.stream().filter(macMapping -> itemList.stream().map(Item::getSerialNumber).collect(Collectors.toSet()).contains(macMapping.getSerialNumber())).collect(Collectors.toList());

            List<InOutWardMACMapping> newInOutwardMacMapping = new ArrayList<>();
            for (InOutWardMACMapping inOutWardMACMapping : inOutWardMACMappingList) {
                InOutWardMACMapingDTO macMapping = new InOutWardMACMapingDTO();
                macMapping = inOutWardMacMapper.domainToDTO(inOutWardMACMapping, new CycleAvoidingMappingContext());
                macMapping.setId(null);
                macMapping.setInwardId(inwardOfSavedOutward.getId());
                macMapping.setOutwardId(savedOutward.getId());
                macMapping.setIsForwarded(0);
                InOutWardMACMapingDTO finalMacMapping = macMapping;
                //   String remark = serialRemarksMap.get(serialRemarksMap.keySet().stream().filter(s -> s.equalsIgnoreCase(finalMacMapping.getSerialNumber())).collect(Collectors.toList()).get(0));
                //   macMapping.setRemark(remark);
                newInOutwardMacMapping.add(inOutWardMacMapper.dtoToDomain(macMapping, new CycleAvoidingMappingContext()));
            }
            inOutWardMacRepo.saveAll(newInOutwardMacMapping);
            inOutWardMACMappingList.forEach(s -> s.setIsReturned(1));
            inOutWardMACMappingList.forEach(s -> s.setIsForwarded(1));
            inOutWardMacRepo.saveAll(inOutWardMACMappingList);
            for (Item item : itemList) {
                item.setCurrentInwardId(inwardOfSavedOutward.getId());
                item.setCurrentInwardType(CommonConstants.RETURNED_INWARD_TYPE);
                item.setOwnerType(inwardOfSavedOutward.getDestinationType());
                item.setOwnerId(inwardOfSavedOutward.getDestinationId());
                inwardService.saveInwardApproval(inwardOfSavedOutward.getId(), "Approve", "", item.getProductId());
//                itemList.forEach(s -> s.setCurrentInwardId(inwardOfSavedOutward.getId()));
            }
            itemRepository.saveAll(itemList);

            // manage product owner quantities after return

        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
        return "Items returned";
    }



//    public Boolean itemReturnCheck(List<Long> itemIds) {
//        Boolean canReturnItems = false;
////    As gui is giving same inwards items so no need to place check
////    Before hitting this api, check all ids selected has same inward, if not give error from gui
//
////        avoid first inward from return by sending false, if not first inward then it will has outward and
////        can be returned
//        Inward inward = inwardRepository.findById(itemRepository.findById(itemIds.get(0)).get().getCurrentInwardId()).get();
//        if (inward.getOutwardId() != null)
//            canReturnItems = true;
//        return canReturnItems;
//    }
//
//    public List<Item> findAllByByInwardInAndProductId(Long inwardId, Long productId) {
//        return itemRepository.findAllByCurrentInwardIdAndProductId(inwardId, productId);
//    }
//
//    public List<Item> findByMac(String mac) {
//        return itemRepository.findByMacAddress(mac);
//    }


    public GenericDataDTO updateItemWarrantyByList(List<ItemWarrantyTypeDTO> itemWarrantyTypeDTOS,Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        QItem qItem = QItem.item;
        try {
            ItemWarrantyMappingDto itemWarrantyMappingDto = new ItemWarrantyMappingDto();
            List<Long> itemIds = new ArrayList<>();
            for(ItemWarrantyTypeDTO itemWarrantyTypeDTO: itemWarrantyTypeDTOS){
                itemIds.add(itemWarrantyTypeDTO.getId());
            }
            List<Item> itemList = itemRepository.findAllById(itemIds);

            List<ItemDto> lst = new ArrayList<>();
            if (itemList != null) {
                if (itemList.size() > 0)
                    for (int i = 0; i <= itemList.size() - 1; i++) {
                        ItemDto itemDto = getEntityForUpdateAndDelete(itemIds.get(i),mvnoId);
                        Product product=productRepository.findById(itemDto.getProductId()).orElse(null);
                        itemDto.setWarranty(itemWarrantyTypeDTOS.get(i).getWarranty());
                        if (itemDto.getRemainingDays() == null) {
                            if(product!=null && product.getExpiryTimeUnit().equalsIgnoreCase("Month")){
                                LocalDateTime expDate=LocalDateTime.now().plusMonths(product.getExpiryTime());
                                itemDto.setExpireDate(expDate);
                                LocalDateTime now = LocalDateTime.now();
                                Duration duration = Duration.between(now, expDate);
                                long remainingDays = duration.toDays();
                                itemDto.setRemainingDays(String.valueOf(remainingDays));
                                itemDto.setWarrantyPeriod((int) remainingDays);
                            }
                            if(product!=null && product.getExpiryTimeUnit().equalsIgnoreCase("Day")){
                                LocalDateTime expDate=LocalDateTime.now().plusDays(product.getExpiryTime());
                                itemDto.setExpireDate(expDate);
                                Duration duration=Duration.between(LocalDateTime.now(),expDate);
                                itemDto.setRemainingDays(String.valueOf(duration.toDays()));
                                itemDto.setWarrantyPeriod((int) duration.toDays());
                            }
                        }

                        itemWarrantyMappingDto.setWarranty(itemWarrantyTypeDTOS.get(i).getWarranty());
                        itemWarrantyMappingDto.setItemId(itemIds.get(i));

                        itemWarrantyMappingService.saveEntity(itemWarrantyMappingDto);

                        lst.add(super.updateEntity(itemDto));

                    }
                dataDTO.setDataList(lst);

            }
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }

    public GenericDataDTO updateItemTypeByList(List<ItemChangeTypeDto> itemChangeTypeDto, List<MultipartFile> files,Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        QItem qItem = QItem.item;
        try {
            ItemConditionsMappingDto itemConditionsMapping = new ItemConditionsMappingDto();

            List<Long> itemIds = new ArrayList<>();
            for (ItemChangeTypeDto itemChangeTypeDto1 : itemChangeTypeDto) {
                itemIds.add(itemChangeTypeDto1.getItemId());
            }
            List<Item> itemList = itemRepository.findAllById(itemIds);

            List<ItemDto> lst = new ArrayList<>();
            if (itemList != null) {
                if (itemList.size() > 0)
                    for (int i = 0; i <= itemList.size() - 1; i++) {
                        ItemDto itemDto = getEntityForUpdateAndDelete(itemIds.get(i),mvnoId);
                        if ((itemDto.getWarranty().equalsIgnoreCase(CommonConstants.EXPIRED)) && (itemDto.getCondition().equalsIgnoreCase(CommonConstants.NEW)) && (itemDto.getItemStatus().equalsIgnoreCase(CommonConstants.DEFECTIVE))) {
                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setRemarks(itemChangeTypeDto.get(i).getRemarks());
                            if (itemChangeTypeDto.get(i).getOtherreason() != null && itemChangeTypeDto.get(i).getOtherreason().length() != 0) {
                                itemConditionsMapping.setOtherreason(itemChangeTypeDto.get(i).getOtherreason());
                            }
                            if (!files.isEmpty()) {
                                if (files.get(i) != null && itemChangeTypeDto.get(i).getFilename().length() != 0) {
                                    uploadDocument(itemList.get(i).getId(), files.get(i), itemConditionsMapping);
                                    itemConditionsMapping.setFilename(files.get(i).getOriginalFilename());
                                    //itemConditionsMapping.setUniquename(fileUtility.saveFileToServer(files ,path));


                                }
                            }
                            itemConditionsMapping.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setItemId(itemChangeTypeDto.get(i).getItemId());

                            itemConditionMappingService.saveEntity(itemConditionsMapping);

                            lst.add(super.updateEntity(itemDto));
                        } else if ((itemDto.getCondition().equalsIgnoreCase(CommonConstants.DAMAGED_AT_STORE)) && (itemDto.getWarranty().equalsIgnoreCase(CommonConstants.EXPIRED)) && (itemDto.getItemStatus().equalsIgnoreCase(CommonConstants.DEFECTIVE))) {
                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());
                        } else if ((!(itemDto.getCondition().equalsIgnoreCase(CommonConstants.DAMAGED_AT_STORE) || (itemDto.getCondition().equalsIgnoreCase(CommonConstants.DAMAGED_AT_SITE)))) && (itemDto.getItemStatus().equalsIgnoreCase(CommonConstants.DEFECTIVE))) {
                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());

                            if (!files.isEmpty()) {
                                if (files.get(i) != null && itemChangeTypeDto.get(i).getFilename().length() != 0) {
                                    uploadDocument(itemList.get(i).getId(), files.get(i), itemConditionsMapping);
                                    itemConditionsMapping.setFilename(files.get(i).getOriginalFilename());


                                }
                            }
                            itemConditionsMapping.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setRemarks(itemChangeTypeDto.get(i).getRemarks());
                            if (itemChangeTypeDto.get(i).getOtherreason() != null && itemChangeTypeDto.get(i).getOtherreason().length() != 0) {
                                itemConditionsMapping.setOtherreason(itemChangeTypeDto.get(i).getOtherreason());
                            }
                            itemConditionsMapping.setItemId(itemIds.get(i));

                            itemConditionMappingService.saveEntity(itemConditionsMapping);

                            lst.add(super.updateEntity(itemDto));
                        } else if ((itemDto.getCondition().equalsIgnoreCase(CommonConstants.DAMAGED_AT_STORE)) && (itemDto.getItemStatus().equalsIgnoreCase(CommonConstants.DEFECTIVE))) {
                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());
                            //     itemDto.setIsDeleted(true);
                            if (files.isEmpty()) {
                                if (files.get(i) != null && itemChangeTypeDto.get(i).getFilename().length() != 0) {
                                    uploadDocument(itemList.get(i).getId(), files.get(i), itemConditionsMapping);
                                    itemConditionsMapping.setFilename(files.get(i).getOriginalFilename());
                                }
                            }
                            itemConditionsMapping.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setRemarks(itemChangeTypeDto.get(i).getRemarks());
                            if (itemChangeTypeDto.get(i).getOtherreason() != null && itemChangeTypeDto.get(i).getOtherreason().length() != 0) {
                                itemConditionsMapping.setOtherreason(itemChangeTypeDto.get(i).getOtherreason());
                            }
                            itemConditionsMapping.setItemId(itemIds.get(i));
                            if (files.get(i) != null && itemChangeTypeDto.get(i).getFilename().length() != 0) {
                                uploadDocument(itemList.get(i).getId(), files.get(i), itemConditionsMapping);
                                itemConditionsMapping.setFilename(itemChangeTypeDto.get(i).getFilename());


                            }
                            itemConditionsMapping.setRemarks(itemChangeTypeDto.get(i).getRemarks());
                            if (itemChangeTypeDto.get(i).getOtherreason() != null && itemChangeTypeDto.get(i).getOtherreason().length() != 0) {
                                itemConditionsMapping.setOtherreason(itemChangeTypeDto.get(i).getOtherreason());
                            }
                            itemConditionsMapping.setIsDeleted(true);

                            itemConditionMappingService.saveEntity(itemConditionsMapping);

                            lst.add(super.updateEntity(itemDto));

                        }
//                        else if((itemDto.getCondition().equalsIgnoreCase(CommonConstants.DAMAGED_AT_SITE)) &&(itemDto.getWarranty().equalsIgnoreCase(CommonConstants.EXPIRED))&& (itemDto.getItemStatus().equalsIgnoreCase(CommonConstants.DEFECTIVE))){
//                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());
                        else if ((itemDto.getCondition().equalsIgnoreCase(CommonConstants.DAMAGED_AT_SITE)) && (itemDto.getItemStatus().equalsIgnoreCase(CommonConstants.DEFECTIVE))) {
                            itemDto.setCondition(itemChangeTypeDto.get(i).getCondition());
                            //     itemDto.setIsDeleted(true);

                            itemConditionsMapping.setCondition(itemChangeTypeDto.get(i).getCondition());
                            itemConditionsMapping.setItemId(itemIds.get(i));
                            if (files.get(i) != null && itemChangeTypeDto.get(i).getFilename().length() != 0) {
                                uploadDocument(itemList.get(i).getId(), files.get(i), itemConditionsMapping);
                                itemConditionsMapping.setFilename(itemChangeTypeDto.get(i).getFilename());
                                itemConditionsMapping.setRemarks(itemChangeTypeDto.get(i).getRemarks());
                                if (itemChangeTypeDto.get(i).getOtherreason() != null && itemChangeTypeDto.get(i).getOtherreason().length() != 0) {
                                    itemConditionsMapping.setOtherreason(itemChangeTypeDto.get(i).getOtherreason());
                                }

                            }
                            itemConditionsMapping.setIsDeleted(true);

                            itemConditionMappingService.saveEntity(itemConditionsMapping);

                            lst.add(super.updateEntity(itemDto));

                        }

                    }
            }
            dataDTO.setDataList(lst);

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }

    public GenericDataDTO updateItemType(Long itemId, String condition,Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        QItem qItem = QItem.item;
        try {
            ItemConditionsMappingDto itemConditionsMapping = new ItemConditionsMappingDto();
            ItemDto itemDto = getEntityForUpdateAndDelete(itemId,mvnoId);

            itemDto.setCondition(condition);
            itemConditionsMapping.setCondition(condition);
            itemConditionsMapping.setItemId(itemId);

            itemConditionMappingService.saveEntity(itemConditionsMapping);

            dataDTO.setData(super.updateEntity(itemDto));
        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }

    public GenericDataDTO updateItemWarranty(Long itemId, String warranty) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            ItemWarrantyMappingDto itemWarrantyMappingDto = new ItemWarrantyMappingDto();
            Item item=itemRepository.findById(itemId).orElse(null);
            ItemDto itemDto=itemMapper.domainToDTO(item,new CycleAvoidingMappingContext());
            itemDto.setWarranty(warranty);
            itemWarrantyMappingDto.setWarranty(warranty);
            itemWarrantyMappingDto.setItemId(itemId);

            itemWarrantyMappingService.saveEntity(itemWarrantyMappingDto);
            Item item1=itemMapper.dtoToDomain(itemDto,new CycleAvoidingMappingContext());
            dataDTO.setData(itemRepository.save(item1));
        }catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }

    public void changeItemWarrantyStatus(Long inventoryMappingId, Long itemId, LocalDateTime billdate) {
        try {
            Item item = itemRepository.findById(itemId).orElse(null);
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (inventoryMappingId != null) {
                if (item != null && inventoryMappingId != null && product.getExpiryTime() != 0) {
                    item.setOwnershipType("Sold");
                    if (product != null && product.getExpiryTimeUnit().equalsIgnoreCase("Month")) {
                        LocalDateTime expDate = billdate.plusMonths(product.getExpiryTime());
                        item.setExpireDate(expDate);
                    }
                    if (product != null && product.getExpiryTimeUnit().equalsIgnoreCase("Day")) {
                        LocalDateTime expDate = billdate.plusDays(product.getExpiryTime());
                        item.setExpireDate(expDate);
                    }
                    itemRepository.save(item);
                    updateItemWarranty(itemId, "InWarranty");
                } else {
                    itemRepository.save(item);
                    updateItemWarranty(itemId, "NoWarranty");
                }
            } else {
                if (inventoryMappingId == null && product.getExpiryTime() != 0) {
                    updateItemWarranty(itemId, "InWarranty");
                } else {
                    itemRepository.save(item);
                    updateItemWarranty(itemId, "NoWarranty");
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }


    public GenericDataDTO updateItemStatusForCustomer(Long itemId, String itemStatus, LocalDateTime assignDate, Long customerId,String event) {
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {

            ItemStatusMapping itemStatusMapping = new ItemStatusMapping();
            itemStatusMapping.setItemId(itemId);
            itemStatusMapping.setCustomerId(customerId);
            itemStatusMapping.setItemStatus(itemStatus);
            itemStatusMapping.setEvent(event);

            if (itemStatus.equalsIgnoreCase(CommonConstants.ALLOCATED)) {
                List<ItemStatusMapping> itemStatusMappings = itemStatusMappingRepo.findByStatus(itemId);
                if (itemStatusMappings.size() != 0) {
                    ItemStatusMapping statusMapping = itemStatusMappings.get(itemStatusMappings.size() - 1);
                    if (statusMapping != null && statusMapping.getItemStatus().equalsIgnoreCase(CommonConstants.ALLOCATED)) {
                        if (statusMapping.getEndDate().isAfter(assignDate)) {
                            throw new RuntimeException("Item was already allocated during this assigned date.");
                        } else {
                            itemStatusMapping.setStartDate(assignDate);
                            List<ItemStatusMapping> itemStatusMappingList = itemStatusMappingRepo.findByItemStatus(itemId);
                            if (itemStatusMappingList.size() != 0) {
                                ItemStatusMapping statusMappings = itemStatusMappingList.get(itemStatusMappingList.size() - 1);
                                statusMappings.setEndDate(assignDate);
                                itemStatusMappingRepo.save(statusMappings);
                            }
                        }
                    }

                } else {
                    itemStatusMapping.setStartDate(assignDate);
                }
            }
            if (itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)) {
                List<ItemStatusMapping> itemStatusMappings = itemStatusMappingRepo.findByStatus(itemId);
                if (itemStatusMappings.size() != 0) {
                    ItemStatusMapping statusMapping = itemStatusMappings.get(itemStatusMappings.size() - 1);
                    if (statusMapping.getItemStatus().equalsIgnoreCase(CommonConstants.ALLOCATED)) {
                        statusMapping.setEndDate(LocalDateTime.now());
                        itemStatusMappingRepo.save(statusMapping);
                        itemStatusMapping.setStartDate(LocalDateTime.now());
                        //    itemStatusMapping.setEndDate(LocalDateTime.now());
                        Long days = 0L;
                        if (itemStatusMappings.size() != 0) {
                            for (ItemStatusMapping mapping : itemStatusMappings) {
                                if ((mapping.getStartDate() != null) && mapping.getEndDate() != null) {
                                    Duration duration = Duration.between(mapping.getStartDate(), mapping.getEndDate());
                                    days = days + duration.toDays();
                                }
                                if (mapping.getEndDate() == null) {
                                    Duration duration = Duration.between(mapping.getStartDate(), LocalDateTime.now());
                                    days = days + duration.toDays();
                                }
                            }
                        }
                        if (days >= 60) {
                            Item item = itemRepository.findById(itemId).get();
                            item.setCondition(CommonConstants.REFURBISHED);
                        }
                    }
                } else {

                    itemStatusMapping.setStartDate(LocalDateTime.now());
                    //  itemStatusMapping.setEndDate(LocalDateTime.now());
                }
            }

            if (itemStatus.equalsIgnoreCase(CommonConstants.DEFECTIVE)) {
                List<ItemStatusMapping> itemStatusMappings = itemStatusMappingRepo.findByStatus(itemId);
                if (itemStatusMappings.size() != 0) {
                    ItemStatusMapping statusMapping = itemStatusMappings.get(itemStatusMappings.size() - 1);
                    if (statusMapping.getItemStatus().equalsIgnoreCase(CommonConstants.ALLOCATED)) {
                        statusMapping.setEndDate(LocalDateTime.now());
                        itemStatusMappingRepo.save(statusMapping);
                        itemStatusMapping.setStartDate(LocalDateTime.now());
                        //    itemStatusMapping.setEndDate(LocalDateTime.now());
                        Long days = 0L;
                        if (itemStatusMappings.size() != 0) {
                            for (ItemStatusMapping mapping : itemStatusMappings) {
                                if ((mapping.getStartDate() != null) && mapping.getEndDate() != null) {
                                    Duration duration = Duration.between(mapping.getStartDate(), mapping.getEndDate());
                                    days = days + duration.toDays();
                                }
                                if (mapping.getEndDate() == null) {
                                    Duration duration = Duration.between(mapping.getStartDate(), LocalDateTime.now());
                                    days = days + duration.toDays();
                                }
                            }
                        }
                        if (days >= 60) {
                            Item item = itemRepository.findById(itemId).get();
                            item.setCondition(CommonConstants.REFURBISHED);
                        }
                    }
                } else {

                    itemStatusMapping.setStartDate(LocalDateTime.now());
                    //  itemStatusMapping.setEndDate(LocalDateTime.now());
                }
            }

            Item item = itemRepository.findById(itemId).get();
            item.setItemStatus(itemStatus);
            itemRepository.save(item);
            itemStatusMappingRepo.save(itemStatusMapping);

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }

    public GenericDataDTO updateItemStatusForServiceAreaAndPop(Long itemId, String itemStatus, Long bulkConsumptionID, Long serviceAreaID, Long popId,String event) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            ItemStatusMapping itemStatusMapping = new ItemStatusMapping();
            itemStatusMapping.setItemId(itemId);
            itemStatusMapping.setEvent(event);
            if (bulkConsumptionID != null) {
                itemStatusMapping.setBulkConsumptionId(bulkConsumptionID);
            }
            if (serviceAreaID != null) {
                itemStatusMapping.setServiceAreaId(serviceAreaID);
            }
            if (popId != null) {
                itemStatusMapping.setPopId(popId);
            }
            itemStatusMapping.setItemStatus(itemStatus);
            Item item = itemRepository.findById(itemId).get();
            Inward inward = inwardRepository.findById(item.getCurrentInwardId()).get();
            if (itemStatus.equalsIgnoreCase(CommonConstants.ALLOCATED)) {
                List<ItemStatusMapping> itemStatusMappings = itemStatusMappingRepo.findByStatus(itemId);
                if (itemStatusMappings.size() != 0) {
                    ItemStatusMapping statusMapping = itemStatusMappings.get(itemStatusMappings.size() - 1);
                    itemStatusMapping.setStartDate(statusMapping.getEndDate());
                } else {
                    itemStatusMapping.setStartDate(inward.getInwardDateTime());
                }
            }
            Long allocatedDays = 0L;
            if (itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)) {
                List<ItemStatusMapping> statusMapping = itemStatusMappingRepo.findByItemId(itemId);
                if (statusMapping.size() != 0) {
                    ItemStatusMapping mapping = statusMapping.get(statusMapping.size() - 1);
                    mapping.setEndDate(LocalDateTime.now());
                    itemStatusMappingRepo.save(mapping);

                    itemStatusMapping.setStartDate(LocalDateTime.now());
                    itemStatusMapping.setEndDate(LocalDateTime.now());

                    List<ItemStatusMapping> itemStatusMappingList = itemStatusMappingRepo.findByStatus(itemId);
                    if (itemStatusMappingList.size() != 0) {
                        for (ItemStatusMapping statusMapping1 : itemStatusMappingList) {
                            if (statusMapping1.getStartDate() != null && statusMapping1.getEndDate() != null) {
                                Duration duration = Duration.between(statusMapping1.getStartDate(), statusMapping1.getEndDate());
                                allocatedDays = allocatedDays + duration.toDays();
                            }
                            if (statusMapping1.getEndDate() == null) {
                                Duration duration = Duration.between(statusMapping1.getStartDate(), LocalDateTime.now());
                                allocatedDays = allocatedDays + duration.toDays();
                            }
                        }
                    }
                }
            }
            if (bulkConsumptionID != null && itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)) {
                itemStatusMapping.setStartDate(LocalDateTime.now());
                itemStatusMapping.setEndDate(LocalDateTime.now());
            }

            if (allocatedDays >= 60) {
                item.setCondition(CommonConstants.REFURBISHED);
            }
            item.setItemStatus(itemStatus);
            itemRepository.save(item);
            itemStatusMappingRepo.save(itemStatusMapping);

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }


    public GenericDataDTO updateItemStatusByList(List<ItemStatusDTO> itemsStatusDtoList,Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        QItem qItem = QItem.item;
        try {
            List<Long> itemIds = new ArrayList<>();
            for (ItemStatusDTO itemStatusDTO : itemsStatusDtoList) {
                itemIds.add(itemStatusDTO.getId());
            }
//            List<String> statuses = new ArrayList<>(itemsList.values());
            List<Item> itemList = itemRepository.findAllById(itemIds);
            ItemDto itemDto = new ItemDto();
            List<ItemDto> lst = new ArrayList<>();
            if (itemList != null) {
                if (itemList.size() > 0)
                    for (int i = 0; i <= itemList.size() - 1; i++) {
                        itemDto = getEntityForUpdateAndDelete(itemIds.get(i),mvnoId);
                        itemDto.setItemStatus(itemsStatusDtoList.get(i).getItemStatus());

                        lst.add(super.updateEntity(itemDto));
                    }
            }
            dataDTO.setDataList(lst);

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }

    public GenericDataDTO updateItemOwnerShipStatusByList(List<ItemOwnerShipDTO> itemOwnerShipDTOList,Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        QItem qItem = QItem.item;
        try {
            List<Long> itemIds = new ArrayList<>();
            for (ItemOwnerShipDTO itemOwnerShipDTO : itemOwnerShipDTOList) {
                itemIds.add(itemOwnerShipDTO.getId());
            }
            //List<String> ownerships = new ArrayList<>(itemsList.values());
            List<Item> itemList = itemRepository.findAllById(itemIds);

            List<ItemDto> lst = new ArrayList<>();
            if (itemList != null) {
                if (itemList.size() > 0)
                    for (int i = 0; i <= itemList.size() - 1; i++) {
                        ItemDto itemDto = getEntityForUpdateAndDelete(itemIds.get(i),mvnoId);
                        itemDto.setOwnershipType(itemOwnerShipDTOList.get(i).getOwnershipType());
                        if (itemOwnerShipDTOList.get(i).getRemarks() != null) {
                            itemDto.setRemarks(itemOwnerShipDTOList.get(i).getRemarks());
                        }
                        lst.add(super.updateEntity(itemDto));
                    }
            }
            dataDTO.setDataList(lst);

        } catch (Exception e) {
            dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            dataDTO.setResponseMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
        }
        return dataDTO;
    }

    public void uploadDocument(Long id, MultipartFile file, ItemConditionsMappingDto itemConditionsMapping) throws Exception {
        String SUBMODULE = "item" + " [uploadDocument()] ";
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.ITEM_COMPLAIN).get(0).getValue();
        // List<RecordPayment> finalResponseList = new ArrayList<>();
        try {
            Item item = itemRepository.getOne(id);
            String subFolderName = item.getName().trim() + "/";
            String path = PATH + subFolderName;
            ApplicationLogger.logger.debug(SUBMODULE + ":File Path:" + path);
            if (null != file.getOriginalFilename()) {
                System.out.println(file.getSize());

                MultipartFile file1 = fileUtility.getFileFromArrayForTicket(file);
                if (null != file1) {
                    itemConditionsMapping.setUniquename(fileUtility.saveFileToServer(file1, path));
                    itemConditionsMapping.setFilename(file.getOriginalFilename());


                }


            } else {
                if (null != file) {
                    if (null != file.getOriginalFilename()
                            && null != file.getOriginalFilename()
                            && !file.getOriginalFilename().equalsIgnoreCase(file.getOriginalFilename())) {
                        fileUtility.removeFileAtServer(itemConditionsMapping.getUniquename(), path);
                    }

                    MultipartFile file1 = fileUtility.getFileFromArrayForTicket(file);
                    if (null != file1) {
                        itemConditionsMapping.setUniquename(fileUtility.saveFileToServer(file1, path));
                    }
//                    RecordPayment obj = convertRecordPaymentPojoToRecordPaymentModel(pojo);
//
//                    finalResponseList.add(obj);
                }


            }

        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }


    public GenericDataDTO searchItems(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, SearchItemsPojo searchItemsPojo,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            if (searchItemsPojo != null) {

                genericDataDTO = itemService.findItems(pageNumber, customPageSize, sortBy, sortOrder, searchItemsPojo,mvnoId);
            }
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        return genericDataDTO;
    }


    public GenericDataDTO findItems(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, SearchItemsPojo search,Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        PageRequest pageRequest;
        Page<Item> paginationList = null;
        QItem qItem = QItem.item;
        List<ItemDto> itemDtoList = new ArrayList<>();

        BooleanExpression booleanExpression = qItem.isNotNull().and(qItem.isDeleted.eq(false));

        //getAllLoggedInUser StaffUserServiceAreaList
        List<StaffUserServiceAreaMapping> getLoogetInUserSeviceAreaList=staffUserServiceAreaMappingRepository.findAllByStaffId(getLoggedInUserId());
        List<Integer> seriveAreaIdList=getLoogetInUserSeviceAreaList.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toList());
        List<StaffUserServiceAreaMapping> getAllStaffInServiceArea=staffUserServiceAreaMappingRepository.findAllByServiceIdIn(seriveAreaIdList);

        List<Long> staffIdList = getAllStaffInServiceArea.stream()
                .map(StaffUserServiceAreaMapping::getStaffId)
                .map(Integer::longValue)
                .collect(Collectors.toList());

        //getAllPatnerBased On Logger User In ServiceArea
        List<Integer> partnerIds=null;
        List<Long> partnerIdList=null;
        List<Integer> customerIds = null;
        List<Long> customersIdList = null;
        if (seriveAreaIdList.size()!=0) {
            partnerIds = partnerServiceAreaMappingRepo.partnerIdList(seriveAreaIdList);
            partnerIdList = partnerIds.stream().map(Integer::longValue).collect(Collectors.toList());
            //GetAll WareHouses based On Service Area
            List<WareHouseServiceAreaMapping> wareHouseServiceAreaMappingList = wareHouseManagmentServiceAreamappingRepo.findAllByServiceIdIn(seriveAreaIdList);
            List<Long> wareHouseServiceAreaIdList = wareHouseServiceAreaMappingList.stream().map(WareHouseServiceAreaMapping::getWarehouseId).collect(Collectors.toList());
            //Get All Customer Based on Service Area
            customerIds= customersRepository.findByServiceAreaIds(seriveAreaIdList);
            customersIdList = customerIds.stream().map(Integer::longValue).collect(Collectors.toList());
            booleanExpression = booleanExpression.and(qItem.ownerId.in(wareHouseServiceAreaIdList)).or(qItem.ownerId.in(staffIdList)).or(qItem.ownerId.in(partnerIdList)).or(qItem.ownerId.in(customersIdList));
        }
        try {
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            // TODO: pass mvnoID manually 6/5/2025
//            if (getMvnoIdFromCurrentStaff(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qItem.mvnoId.in(mvnoId, 1));

            if (search.getOwnerType() != null && !"null".equals(search.getOwnerType()) && !"".equals(search.getOwnerType())) {
                booleanExpression = booleanExpression.and(qItem.ownerType.startsWithIgnoreCase(search.getOwnerType()));
            }
            if (search.getOwnerId() != null && !"null".equals(search.getOwnerType()) && !"".equals(search.getOwnerType())) {
                booleanExpression = booleanExpression.and(qItem.ownerId.eq(Long.valueOf(String.valueOf(search.getOwnerId()))));
            }
            if (search.getProductId() != null && !"null".equals(search.getProductId()) && !"".equals(search.getProductId())) {
                booleanExpression = booleanExpression.and(qItem.productId.eq(Long.valueOf(String.valueOf(search.getProductId()))));
            }

            if (search.getInwardId() != null && !"null".equals(search.getInwardId()) && !"".equals(search.getInwardId())) {
                booleanExpression = booleanExpression.and(qItem.currentInwardId.eq(Long.valueOf(String.valueOf(search.getInwardId()))));
            }

            if (search.getItemType() != null && !"null".equals(search.getItemType()) && !"".equals(search.getItemType())) {
                booleanExpression = booleanExpression.and(qItem.condition.startsWithIgnoreCase(search.getItemType()));
            }

            if (search.getItemStatus() != null && !"null".equals(search.getItemStatus()) && !"".equals(search.getItemStatus())) {
                booleanExpression = booleanExpression.and(qItem.itemStatus.startsWithIgnoreCase(search.getItemStatus()));
            }

            if (search.getOwnership() != null && !"null".equals(search.getOwnership()) && !"".equals(search.getOwnership())) {
                booleanExpression = booleanExpression.and(qItem.ownershipType.startsWithIgnoreCase(search.getOwnership()));
            }

            if (search.getWarrantyStatus() != null && !"null".equals(search.getWarrantyStatus()) && !"".equals(search.getWarrantyStatus())) {
                booleanExpression = booleanExpression.and(qItem.warranty.startsWithIgnoreCase(search.getWarrantyStatus()));
            }
            if (search.getSerialNumber() != null && !"null".equals(search.getSerialNumber()) && !"".equals(search.getSerialNumber())) {
                booleanExpression = booleanExpression.and(qItem.serialNumber.startsWithIgnoreCase(search.getSerialNumber()));
            }
            if (search.getMacAddress() != null && !"null".equals(search.getMacAddress()) && !"".equals(search.getMacAddress())) {
                booleanExpression = booleanExpression.and(qItem.macAddress.startsWithIgnoreCase(search.getSerialNumber()));
            }
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);

            paginationList = itemRepository.findAll(booleanExpression, pageRequest);
            List<ItemDto> dto = paginationList.get().map(item -> itemMapper.domainToDTO(item, new CycleAvoidingMappingContext())).collect(Collectors.toList());

            for (ItemDto itemDto : dto) {
                if (itemDto.getCurrentInwardId() != null) {
                    itemDto.setCurrentInwardNumber(inwardRepository.findById(itemDto.getCurrentInwardId()).get().getInwardNumber());
                }
                if (itemDto.getProductId() != null) {
                    itemDto.setProductName(productService.getEntityById(itemDto.getProductId().longValue(),mvnoId).getName());
                }
                if (!itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).isEmpty()) {
                    itemDto.setFilename(itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).get(0).getFilename());
                    itemDto.setItemConditionId(itemConditionMappingRepository.getItemConditionByItemId(itemDto.getId()).get(0).getId());
                }
                if (itemRepository.getOne(itemDto.getId()).getRemarks() != null) {
                    itemDto.setRemarks(itemRepository.getOne(itemDto.getId()).getRemarks());
                }
                if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.STAFF))
                    itemDto.setOwnerName(staffService.get(itemDto.getOwnerId().intValue(),getMvnoIdFromCurrentStaff(customerIds.get(0))).getUsername());
                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.WAREHOUSE))
                    itemDto.setOwnerName(warehouseManagementService.getEntityById(itemDto.getOwnerId().longValue(),mvnoId).getName());
                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.POP))
                    itemDto.setOwnerName(popManagementService.getEntityById(itemDto.getOwnerId().longValue(),mvnoId).getName());
                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.SERVICE_AREA))
                    itemDto.setOwnerName(serviceAreaService.getByID(itemDto.getOwnerId().longValue()).getName());
                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.PARTNER))
                    itemDto.setOwnerName(partnerService.get(itemDto.getOwnerId().intValue(),getMvnoIdFromCurrentStaff(customerIds.get(0))).getName());
                else if (itemDto.getOwnerType().equalsIgnoreCase(CommonConstants.CUSTOMER))
                    itemDto.setOwnerName( customersRepository.findById(itemDto.getOwnerId().intValue()).get().getUsername());

                itemDtoList.add(itemDto);
            }

        } catch (Exception ex) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }

        genericDataDTO.setDataList(itemDtoList);
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        return genericDataDTO;
    }


    public GenericDataDTO searchItembasedOnProductAndCustomer(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = getModuleNameForLog() + " [ search()] ";
        try {
            PageRequest pageRequest1 = generatePageRequest(page, pageSize, sortBy, sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim() != null) {
                        return getProductNameAndCustomerName(searchModel.getFilterValue(), pageRequest1,searchModel.getFilterColumn());
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }


    public GenericDataDTO getProductNameAndCustomerName(String s1, PageRequest pageRequest, String s2) {
        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            if (s2.equalsIgnoreCase("Product")) {
                QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(getLoggedInUserId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.product.name.likeIgnoreCase(("%" + s1 + "%"))));
                Page<CustomerInventoryMapping> page = customerInventoryMappingRepo.findAll(booleanExpression,pageRequest);
                genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(page.getContent(),new CycleAvoidingMappingContext()));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            }
            if (s2.equalsIgnoreCase("Customer")) {

                QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
                BooleanExpression booleanExpression = qCustomerInventoryMapping.isNotNull().and(qCustomerInventoryMapping.staff.id.eq(Math.toIntExact(getLoggedInUserId())).and(qCustomerInventoryMapping.isDeleted.eq(false)).and(qCustomerInventoryMapping.product.name.likeIgnoreCase(("%" + s1 + "%"))));
                 Page<CustomerInventoryMapping> page =  customerInventoryMappingRepo.findAll(booleanExpression,pageRequest);
                 genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(page.getContent(),new CycleAvoidingMappingContext()));
                 genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public List<ItemDto> findItemsSuibiseOwned(Long currentInwardId) {
        List<ItemDto> itemDtoList = null;
        try {
            QItem qItem = QItem.item;
            BooleanExpression booleanExpression = qItem.isNotNull().and(qItem.currentInwardId.eq(currentInwardId)).and(qItem.isDeleted.eq(false).and(qItem.ownershipType.eq("Subisu Owned")).and(qItem.itemStatus.eq("Unallocated")));
            List<Item> itemList = (List<Item>) itemRepository.findAll(booleanExpression);
            itemDtoList = itemList.stream().map(item -> itemMapper.domainToDTO(item, new CycleAvoidingMappingContext())).collect(Collectors.toList());
            return itemDtoList;
        } catch (Exception exception) {
            exception.getMessage();
        }
        return itemDtoList;
    }

    public List<ItemStatusMapping> getAllCustomerInvetoryHistory(Long custId) {
        List<ItemStatusMapping> itemList=new ArrayList<>();
         try {
             itemList=itemStatusMappingRepo.findByCustomerId(custId);

             QCustomers qCustomers = QCustomers.customers;
             BooleanExpression getChildExpression = qCustomers.isNotNull().and(qCustomers.parentCustomers.id.eq(custId.intValue())).and(qCustomers.parentExperience.equalsIgnoreCase(CommonConstants.PARENT_EXPERIENCE_SINGLE).and(qCustomers.isDeleted.eq(false).and(qCustomers.status.eq(CommonConstants.CUSTOMER_STATUS_ACTIVE))));
             List<Long> childCustIds = ((List<Customers>) customersRepository.findAll(getChildExpression)).stream().map(customers -> Long.valueOf(customers.getId())).collect(Collectors.toList());
             if (childCustIds != null && childCustIds.size() > 0){
                 itemList.addAll(itemStatusMappingRepo.findByCustomerIdIn(childCustIds));
             }
             if (itemList.size() != 0) {
                 itemList.stream().forEach(itemStatusMapping -> {
                     Item item = itemRepository.findById(itemStatusMapping.getItemId()).get();
                     if (item != null) {
                         List<CustomerInventoryMapping> customerInventoryMappingList = customerInventoryMappingRepo.findByItemId(item.getId());
                         if (customerInventoryMappingList.size() > 0 || customerInventoryMappingList != null) {
                             customerInventoryMappingList.stream().forEach(customerInventoryMapping -> {
                                 itemStatusMapping.setCondition(item.getCondition());
                                 itemStatusMapping.setMacAddress(item.getMacAddress());
                                 itemStatusMapping.setSerialNumber(item.getSerialNumber());
                                 if (customerInventoryMapping.getExternalItemId() != null) {
                                     ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(customerInventoryMapping.getExternalItemId()).get();
                                     itemStatusMapping.setExternalItemGroupNumber(externalItemManagement.getExternalItemGroupNumber());
                                 }
                                 if(itemStatusMapping.getEvent().equalsIgnoreCase("assign_inventory")){
                                     itemStatusMapping.setApprovalRemark(null);
                                 } else {
                                     itemStatusMapping.setApprovalRemark(customerInventoryMapping.getApprovalRemark());
                                 }
                                 if (customerInventoryMapping.getPlanId() != null) {
                                     PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(Math.toIntExact(customerInventoryMapping.getPlanId())).get();
                                     itemStatusMapping.setPostPaidPlanName(postpaidPlan.getName());
                                 }
                                 if (customerInventoryMapping.getServiceId() != null) {
                                     PlanService planService = planServiceRepository.findById(Math.toIntExact(customerInventoryMapping.getServiceId())).get();
                                     itemStatusMapping.setServiceName(planService.getName());
                                 }
                                 if (customerInventoryMapping.getBillTo() != null) {
                                     itemStatusMapping.setBillTo(customerInventoryMapping.getBillTo());
                                 }
                                 if (customerInventoryMapping.getIsInvoiceToOrg() != null) {
                                    itemStatusMapping.setIsInvoiceToOrg(customerInventoryMapping.getIsInvoiceToOrg());
                                 }
                                 if (customerInventoryMapping.getIsRequiredApproval() != null) {
                                    itemStatusMapping.setIsRequiredApproval(customerInventoryMapping.getIsRequiredApproval());
                                 }
                                 itemStatusMapping.setConnectionNo(customerInventoryMapping.getConnectionNo());
                             });
                         }
                     }
                 });
             }

        } catch (Exception exception) {
            exception.getMessage();
        }
        return itemList;
    }


    public List<InOutWardMACMapingDTO> getInOutMacMappingForSerializedItem(Long id, Long ownerId, String ownerType) {
        QItem qItem = QItem.item;
        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(id)).and(qItem.ownerId.eq(ownerId)).and(qItem.ownerType.equalsIgnoreCase(ownerType)).and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qItem.itemStatus.ne(CommonConstants.DEFECTIVE));
        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
        QOutward qOutward = QOutward.outward;
        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(id)).and(qOutward.approvalStatus.equalsIgnoreCase("Approve"));
        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
        List<InOutWardMACMapping> inOutWardMACMappingList= (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS=inOutWardMacMapper.domainToDTO(inOutWardMACMappingList,new CycleAvoidingMappingContext());
        inOutWardMACMapingDTOS.stream().forEach(r->{
            r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
            r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
            r.setHasMac(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasMac());
            r.setHasSerial(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasSerial());
            r.setCondition(itemRepository.findById(r.getItemId()).get().getCondition());
            r.setOwnerShip(itemRepository.findById(r.getItemId()).get().getOwnershipType());
        });
        return inOutWardMACMapingDTOS;
    }


    public List<InOutWardMACMapingDTO> getInOutMacMappingForSerializedItemBasedOnItemCondtion(Long id,Long olditemId,Long ownerId,String ownerShipType,String replacementReason) {
        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS=null;
        try {
            Item olditem=itemRepository.findById(olditemId).orElse(null);
            if(olditem!=null){
                if(olditem.getOwnershipType().equalsIgnoreCase("Subisu Owned") && olditem.getCondition().equalsIgnoreCase("New")){
                    QItem qItem = QItem.item;
                    BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(id)).and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED))
                            .and(qItem.condition.in("Refurbished","New")).and(qItem.ownershipType.equalsIgnoreCase("Subisu Owned")).and(qItem.ownerId.eq(ownerId)).and(qItem.ownerType.equalsIgnoreCase(ownerShipType)).and(qItem.itemStatus.ne(CommonConstants.DEFECTIVE));
                    List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                    List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                    QOutward qOutward = QOutward.outward;
                    BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(id)).and(qOutward.approvalStatus.equalsIgnoreCase("Approve"));
                    List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                    List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                    QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                    BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                    boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                    List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                    inOutWardMACMapingDTOS= inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                    inOutWardMACMapingDTOS.stream().forEach(r -> {
                        r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
                        r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
                        r.setHasMac(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasMac());
                        r.setHasSerial(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasSerial());
                        r.setCondition(itemRepository.findById(r.getItemId()).get().getCondition());
                    });
                }
                if(olditem.getOwnershipType().equalsIgnoreCase("Sold") && olditem.getCondition().equalsIgnoreCase("New")){
                    QItem qItem = QItem.item;
                    BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(id)).and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qItem.condition.equalsIgnoreCase("New")).and(qItem.ownershipType.equalsIgnoreCase("Subisu Owned")).and(qItem.ownerId.eq(ownerId)).and(qItem.ownerType.equalsIgnoreCase(ownerShipType)).and(qItem.itemStatus.ne(CommonConstants.DEFECTIVE));
                    List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                    List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                    QOutward qOutward = QOutward.outward;
                    BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(id)).and(qOutward.approvalStatus.equalsIgnoreCase("Approve"));
                    List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                    List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                    QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                    BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                    boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                    List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                    inOutWardMACMapingDTOS= inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                    inOutWardMACMapingDTOS.stream().forEach(r -> {
                        r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
                        r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
                        r.setHasMac(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasMac());
                        r.setHasSerial(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasSerial());
                        r.setCondition(itemRepository.findById(r.getItemId()).get().getCondition());
                    });
                }
                if(olditem.getCondition().equalsIgnoreCase("Refurbished") || replacementReason.equalsIgnoreCase("Temporary Replacement")){
                    QItem qItem = QItem.item;
                    BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(id)).and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qItem.condition.equalsIgnoreCase("Refurbished")).and(qItem.ownershipType.equalsIgnoreCase("Subisu Owned")).and(qItem.ownerId.eq(ownerId)).and(qItem.ownerType.equalsIgnoreCase(ownerShipType)).and(qItem.itemStatus.ne(CommonConstants.DEFECTIVE));
                    List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                    List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                    QOutward qOutward = QOutward.outward;
                    BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(id)).and(qOutward.approvalStatus.equalsIgnoreCase("Approve"));
                    List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                    List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                    QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                    BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                    boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                    List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                    inOutWardMACMapingDTOS= inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                    inOutWardMACMapingDTOS.stream().forEach(r -> {
                        r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
                        r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
                        r.setHasMac(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasMac());
                        r.setHasSerial(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasSerial());
                        r.setCondition(itemRepository.findById(r.getItemId()).get().getCondition());
                    });
                }

            }

        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }
            return inOutWardMACMapingDTOS;
    }

    public List<InOutWardMACMapingDTO> getInOutMacMappingBasedOnProductType(Long productId, Long ownerId, String ownerType, Long planId, Long planGroupId, Long productCategoryId) {
        List<InOutWardMACMapingDTO> listList = new ArrayList<>();
        if(getLoggedInUser().getPartnerId() != 1) {
            ownerId = Long.valueOf(getLoggedInUser().getPartnerId());
            ownerType = CommonConstants.PARTNER;
        }
        Long finalOwnerId = ownerId;
        String finalOwnerType = ownerType;
        try {
            List<Productplanmapping> productplanmappingList = null;
            List<ProductPlanGroupMapping> productPlanGroupMappingList = null;
            if(planGroupId != null) {
                QProductPlanGroupMapping qProductPlanGroupMapping = QProductPlanGroupMapping.productPlanGroupMapping;
                BooleanExpression booleanExpression = qProductPlanGroupMapping.planId.eq(planId).and(qProductPlanGroupMapping.planGroupId.eq(planGroupId));
                if(productCategoryId != null) {
                    booleanExpression = booleanExpression.and(qProductPlanGroupMapping.productCategoryId.eq(productCategoryId));
                }
                productPlanGroupMappingList = IterableUtils.toList(productPlanGroupMappingRepository.findAll(booleanExpression));
            } else {
                QProductplanmapping qProductplanmapping = QProductplanmapping.productplanmapping;
                BooleanExpression booleanExpression = qProductplanmapping.planId.eq(planId).and(qProductplanmapping.productId.eq(productId));
                if(productCategoryId != null) {
                    booleanExpression = booleanExpression.and(qProductplanmapping.productCategoryId.eq(productCategoryId));
                }
                productplanmappingList = IterableUtils.toList(productPlanMappingRepository.findAll(booleanExpression));
            }
            if (productplanmappingList != null && productplanmappingList.size() != 0) {
                productplanmappingList.stream().forEach(t -> {
                    if (t.getProduct_type().equalsIgnoreCase("New")) {
                        QItem qItem = QItem.item;
                        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(productId)).and(qItem.ownerId.eq(finalOwnerId)).and(qItem.ownerType.equalsIgnoreCase(finalOwnerType))
                                .and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qItem.condition.equalsIgnoreCase("New")).and(qItem.itemStatus.ne(CommonConstants.DEFECTIVE));
                        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(productId)).and(qOutward.approvalStatus.equalsIgnoreCase("Approve"));
                        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                        List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        inOutWardMACMapingDTOS.stream().forEach(r->{
                            r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
                            r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
                            r.setHasMac(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasMac());
                            r.setHasSerial(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasSerial());
                            r.setCondition(itemRepository.findById(r.getItemId()).get().getCondition());
                        });
                        listList.addAll(inOutWardMACMapingDTOS);
                    }
                    if (t.getProduct_type().equalsIgnoreCase("Refurbished")) {
                        QItem qItem = QItem.item;
                        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(productId)).and(qItem.ownerId.eq(finalOwnerId)).and(qItem.ownerType.equalsIgnoreCase(finalOwnerType)).and(qItem.condition.eq("Refurbished"));
                        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(productId)).and(qOutward.approvalStatus.equalsIgnoreCase("Approve"));
                        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                        List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        inOutWardMACMapingDTOS.stream().forEach(r->{
                            r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
                            r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
                            r.setHasMac(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasMac());
                            r.setHasSerial(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasSerial());
                            r.setCondition(itemRepository.findById(r.getItemId()).get().getCondition());
                        });
                        listList.addAll(inOutWardMACMapingDTOS);
                    }
                });
            }
            if (productPlanGroupMappingList != null && productPlanGroupMappingList.size() != 0) {
                productPlanGroupMappingList.stream().forEach(t -> {
                    if (t.getProduct_type().equalsIgnoreCase("New")) {
                        QItem qItem = QItem.item;
                        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(productId)).and(qItem.ownerId.eq(finalOwnerId)).and(qItem.ownerType.equalsIgnoreCase(finalOwnerType))
                                .and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qItem.condition.equalsIgnoreCase("New")).and(qItem.itemStatus.ne(CommonConstants.DEFECTIVE));
                        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(productId)).and(qOutward.approvalStatus.equalsIgnoreCase("Approve"));
                        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                        List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        inOutWardMACMapingDTOS.stream().forEach(r->{
                            r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
                            r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
                            r.setHasMac(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasMac());
                            r.setHasSerial(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasSerial());
                            r.setCondition(itemRepository.findById(r.getItemId()).get().getCondition());
                        });
                        listList.addAll(inOutWardMACMapingDTOS);
                    }
                    if (t.getProduct_type().equalsIgnoreCase("Refurbished")) {
                        QItem qItem = QItem.item;
                        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(productId)).and(qItem.ownerId.eq(finalOwnerId)).and(qItem.ownerType.equalsIgnoreCase(finalOwnerType)).and(qItem.condition.eq("Refurbished"));
                        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(productId)).and(qOutward.approvalStatus.equalsIgnoreCase("Approve"));
                        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                        List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        inOutWardMACMapingDTOS.stream().forEach(r->{
                            r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
                            r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
                            r.setHasMac(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasMac());
                            r.setHasSerial(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasSerial());
                            r.setCondition(itemRepository.findById(r.getItemId()).get().getCondition());
                        });
                        listList.addAll(inOutWardMACMapingDTOS);
                    }
                });
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
        return listList;
    }

    public List<InOutWardMACMapingDTO> getInOutMacMappingForNonSerializedItemBasedOnProductCondtion(Long id, Long ownerId, String ownerType, Long planId, Long planGroupId, Long productCategoryId) {
        try {
            List<InOutWardMACMapingDTO> inOutWardMACMapingDTOList = new ArrayList<>();
            List<Productplanmapping> productplanmappingList = productPlanMappingRepository.getallfromplanid(planId);
            if (productplanmappingList.size() != 0) {
                productplanmappingList.stream().forEach(r -> {
                    if (r.getProduct_type().equalsIgnoreCase("New")) {
                        QNonSerializedItem qNonSerializedItem = QNonSerializedItem.nonSerializedItem;
                        BooleanExpression booleanExpression = qNonSerializedItem.isDeleted.eq(false).and(qNonSerializedItem.productId.eq(id)).and(qNonSerializedItem.ownerId.eq(ownerId)).and(qNonSerializedItem.ownerType.equalsIgnoreCase(ownerType))
                                .and(qNonSerializedItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qNonSerializedItem.itemStatus.ne(CommonConstants.DEFECTIVE)).and(qNonSerializedItem.nonSerializedItemcondition.equalsIgnoreCase("New"));
                        List<NonSerializedItem> nonSerializedItemList = IterableUtils.toList(nonSerializedItemRepository.findAll(booleanExpression));
                        List<Long> nonSerializedItemIds = nonSerializedItemList.stream().map(NonSerializedItem::getId).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = qInOutWardMACMapping.nonSerializedItemId.in(nonSerializedItemIds).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull());
                        List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(boolExp));
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        inOutWardMACMapingDTOS.stream().forEach(l->{
                            l.setProductName(productRepository.findById(itemRepository.findById(l.getItemId()).get().getProductId()).get().getName());

                        });
                        inOutWardMACMapingDTOList.addAll(inOutWardMACMapingDTOS);
                    }
                    if (r.getProduct_type().equalsIgnoreCase("Refurbished")) {
                        QNonSerializedItem qNonSerializedItem = QNonSerializedItem.nonSerializedItem;
                        BooleanExpression booleanExpression = qNonSerializedItem.isDeleted.eq(false).and(qNonSerializedItem.productId.eq(id)).and(qNonSerializedItem.ownerId.eq(ownerId)).and(qNonSerializedItem.ownerType.equalsIgnoreCase(ownerType)).and(qNonSerializedItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qNonSerializedItem.nonSerializedItemcondition.equalsIgnoreCase("Refurbished"));
                        List<NonSerializedItem> nonSerializedItemList = IterableUtils.toList(nonSerializedItemRepository.findAll(booleanExpression));
                        List<Long> nonSerializedItemIds = nonSerializedItemList.stream().map(NonSerializedItem::getId).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = qInOutWardMACMapping.nonSerializedItemId.in(nonSerializedItemIds).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull());
                        List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(boolExp));
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        inOutWardMACMapingDTOS.stream().forEach(l->{
                            l.setProductId(productRepository.findById(itemRepository.findById(l.getItemId()).get().getProductId()).get().getId());
                            l.setProductName(productRepository.findById(itemRepository.findById(l.getItemId()).get().getProductId()).get().getName());

                        });
                        inOutWardMACMapingDTOList.addAll(inOutWardMACMapingDTOS);
                    }
                });

            }
            return inOutWardMACMapingDTOList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<InOutWardMACMapingDTO> getInOutMacMappingForSerializedItemBasedOnProductType(Long id, Long ownerId, String ownerType, Long planid, Long planGroupId, Long productCategoryId) {
        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOList = new ArrayList<>();
        try {
            List<Productplanmapping> productplanmappingList = productPlanMappingRepository.getallfromplanid(planid);
            if (productplanmappingList.size() != 0) {
                productplanmappingList.stream().forEach(t -> {
                    if (t.getProduct_type().equalsIgnoreCase("New")) {
                        QItem qItem = QItem.item;
                        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(id)).and(qItem.ownerId.eq(ownerId)).and(qItem.ownerType.equalsIgnoreCase(ownerType))
                                .and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qItem.condition.equalsIgnoreCase("New")).and(qItem.itemStatus.ne(CommonConstants.DEFECTIVE));
                        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(id)).and(qOutward.approvalStatus.equalsIgnoreCase("Approve"));
                        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                        List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        inOutWardMACMapingDTOS.stream().forEach(r -> {
                            r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
                            r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
                            r.setHasMac(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasMac());
                            r.setHasSerial(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasSerial());
                        });
                        inOutWardMACMapingDTOList.addAll(inOutWardMACMapingDTOS);
                    }
                    if (t.getProduct_type().equalsIgnoreCase("New")) {
                        QItem qItem = QItem.item;
                        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(id)).and(qItem.ownerId.eq(ownerId)).and(qItem.ownerType.equalsIgnoreCase(ownerType)).and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qItem.condition.equalsIgnoreCase("New"));
                        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
                        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
                        QOutward qOutward = QOutward.outward;
                        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(id)).and(qOutward.approvalStatus.equalsIgnoreCase("Approve"));
                        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
                        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
                        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
                        boolExp = boolExp.and(qInOutWardMACMapping.itemId.in(itemIds)).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.custInventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
                        List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
                        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS = inOutWardMacMapper.domainToDTO(inOutWardMACMappingList, new CycleAvoidingMappingContext());
                        inOutWardMACMapingDTOS.stream().forEach(r -> {
                            r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
                            r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
                            r.setHasMac(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasMac());
                            r.setHasSerial(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getProductCategory().isHasSerial());
                        });
                        inOutWardMACMapingDTOList.addAll(inOutWardMACMapingDTOS);
                    }
                });
            }

        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
        return inOutWardMACMapingDTOList;
    }


    public List<Item> getSerializedItemForInward(Long inwardId, Long id, Long ownerId, String ownerType) {
        QItem qItem = QItem.item;
        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(id)).and(qItem.ownerId.eq(ownerId)).and(qItem.ownerType.equalsIgnoreCase(ownerType)).and(qItem.currentInwardId.eq(inwardId));
        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
//        List<InOutWardMACMapping> result = new ArrayList<>();
//        for (int i=0; i<itemList.size(); i++) {
//            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
//            BooleanExpression aBoolean = qInOutWardMACMapping.isDeleted.eq(false).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.itemId.eq(itemList.get(i).getId())).and(qInOutWardMACMapping.isReturned.eq(0));
//            List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(aBoolean));
//            for (int j=0; j<inOutWardMACMappingList.size(); j++) {
//                result.add(inOutWardMACMappingList.get(j));
//            }
//        }
        return itemList;
    }

    public List<Item> getSerializedItemForOutward(Long id, Long ownerId, String ownerType) {
        QItem qItem = QItem.item;
        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(id)).and(qItem.ownerId.eq(ownerId)).and(qItem.ownerType.equalsIgnoreCase(ownerType));
        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
//        List<InOutWardMACMapping> result = new ArrayList<>();
//        for (int i=0; i<itemList.size(); i++) {
//            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
//            BooleanExpression aBoolean = qInOutWardMACMapping.isDeleted.eq(false).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.itemId.eq(itemList.get(i).getId())).and(qInOutWardMACMapping.isReturned.eq(0));
//            List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(aBoolean));
//            for (int j=0; j<inOutWardMACMappingList.size(); j++) {
//                result.add(inOutWardMACMappingList.get(j));
//            }
//        }
        return itemList;
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
            Item serializedItem = itemRepository.findTopByOrderByIdDesc();
            if (serializedItem == null) {
                flag += 1;
            } else {
                flag += serializedItem.getId() + 1;
            }
        }
        return flag;
    }

    @Transactional
    public Item updateItemMacAndSerial(Long itemId, String macAddress, String serialNumber) {
        try {
            Item item = itemRepository.findById(itemId).get();
            item.setMacAddress(macAddress);
            item.setSerialNumber(serialNumber);
            itemRepository.save(item);
            inOutWardMACService.updateMacSerialByItem(itemId, macAddress, serialNumber);
            return item;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public Item updateItemSerial(Long itemId, String serialNumber) {
        try {
            Item item = itemRepository.findById(itemId).get();
            item.setSerialNumber(serialNumber);
            itemRepository.save(item);
            inOutWardMACService.updateSerialByItem(itemId, serialNumber);
            return item;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<InOutWardMACMapingDTO> getInOutMacMappingForPopAndSA(Long id, Long ownerId, String ownerType) {
        QItem qItem = QItem.item;
        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.productId.eq(id)).and(qItem.ownerId.eq(ownerId)).and(qItem.ownerType.equalsIgnoreCase(ownerType)).and(qItem.itemStatus.equalsIgnoreCase(CommonConstants.UNALLOCATED)).and(qItem.itemStatus.ne(CommonConstants.DEFECTIVE));
        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
        List<Long> itemIds = itemList.stream().map(item -> item.getId()).collect(Collectors.toList());
        QOutward qOutward = QOutward.outward;
        BooleanExpression booleanExpressionOutward = qOutward.isDeleted.eq(false).and(qOutward.productId.id.eq(id)).and(qOutward.approvalStatus.equalsIgnoreCase("Approve"));
        List<Outward> outwardList = IterableUtils.toList(outwardRepository.findAll(booleanExpressionOutward));
        List<Long> outwardIds = outwardList.stream().map(outward -> outward.getId()).collect(Collectors.toList());
        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
        BooleanExpression boolExp = qInOutWardMACMapping.isNotNull();
        boolExp = qInOutWardMACMapping.itemId.in(itemIds).and(qInOutWardMACMapping.isForwarded.eq(0)).and(qInOutWardMACMapping.inventoryMappingId.isNull()).and(qInOutWardMACMapping.outwardId.in(outwardIds));
        List<InOutWardMACMapping> inOutWardMACMappingList= (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(boolExp);
        List<InOutWardMACMapingDTO> inOutWardMACMapingDTOS=inOutWardMacMapper.domainToDTO(inOutWardMACMappingList,new CycleAvoidingMappingContext());
        inOutWardMACMapingDTOS.stream().forEach(r->{
            r.setProductId(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getId());
            r.setProductName(productRepository.findById(itemRepository.findById(r.getItemId()).get().getProductId()).get().getName());
            r.setCondition(itemRepository.findById(r.getItemId()).get().getCondition());
        });
        return inOutWardMACMapingDTOS;
    }

    @Transactional
    public void updateSelectedItemMacAndSerial (Long itemId, String macAddress, String serialNumber) throws Exception {
        try {
            if (!Objects.equals(macAddress, null)) {
                Item item = itemRepository.findById(itemId).get();
                String itemMac = item.getMacAddress();
                if (!Objects.equals(macAddress, null)) {
                    if (itemMac != null) {
                        if (!itemMac.equals(macAddress)) {
                            inOutWardMACService.duplicateVerifyAtSave(macAddress);
                        }
                    } else {
                        inOutWardMACService.duplicateVerifyAtSave(macAddress);
                    }
                    updateItemMacAndSerial(itemId, macAddress, serialNumber);
                } else {
                    updateItemSerial(itemId, serialNumber);
                }
            } else {
                updateItemSerial(itemId, serialNumber);
            }
        } catch (Exception e) {
            throw new Exception("Selected " + macAddress + " is Already Exist");
        }
    }
    public void validateMac(String macAddress, String serialNumber) throws Exception {
        if (macAddress == null || macAddress.equals("")) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter mac in selected item", null);
        } else {
            validateSerialNumber(serialNumber);
        }
    }
    public void validateSerialNumber(String serialNumber) throws Exception {
        if (serialNumber == null || serialNumber.equals("")) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter serial number in selected item", null);
        }
    }

    public Item getItemDetails(Long itemId, Long custinventoryid) {
        CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(custinventoryid).get();
        QItem qItem = QItem.item;
        BooleanExpression booleanExpression = qItem.isDeleted.eq(false).and(qItem.id.eq(itemId));
        List<Item> itemList = IterableUtils.toList(itemRepository.findAll(booleanExpression));
        if(!customerInventoryMapping.getCreatedate().toLocalDate().equals(LocalDate.now())) {
            itemList.stream().forEach(item -> {
                Product product = productRepository.findById(item.getProductId()).get();
                if (item.getCondition().equalsIgnoreCase("New")) {
                    if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                        item.setProductRefundAmount(product.getNewProductRefAmountInWarranty());
                    } else if (item.getWarranty().equalsIgnoreCase("Expired")) {
                        item.setProductRefundAmount(product.getNewProductRefAmountPostWarranty());
                    }
                }
                if (item.getCondition().equalsIgnoreCase("Refurbished")) {
                    if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                        item.setProductRefundAmount(product.getRefurburshiedProductRefAmountInWarranty());
                    } else if (item.getWarranty().equalsIgnoreCase("Expired")) {
                        item.setProductRefundAmount(product.getRefurburshiedProductRefAmountPostWarranty());
                    }
                }
                item.setRefundFlag(true);
            });
        } else {
            itemList.stream().forEach(item -> {
                item.setRefundFlag(false);
            });
        }
        return itemList.get(0);
    }
}



