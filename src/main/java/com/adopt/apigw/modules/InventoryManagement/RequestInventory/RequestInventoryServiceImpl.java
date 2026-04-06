package com.adopt.apigw.modules.InventoryManagement.RequestInventory;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.mapper.postpaid.StaffUserMapper;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.common.StaffUserServiceAreaMapping;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.PopManagement;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.PopServiceAreaMapping;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.mapper.PopManagementMapper;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.repository.PopManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.repository.PopServiceAreaMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.outward.Outward;
import com.adopt.apigw.modules.InventoryManagement.outward.OutwardRepository;
import com.adopt.apigw.modules.InventoryManagement.outward.QOutward;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategory;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategoryRepository;
import com.adopt.apigw.modules.InventoryManagement.warehouse.*;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.Teams.repository.TeamUserMappingsRepocitory;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.common.StaffUserServiceAreaMappingRepository;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RequestInventoryServiceImpl extends ExBaseAbstractService<RequestInventoryDto, RequestInventory, Long> {

    @Autowired
    RequestInventoryRepo requestInventoryRepo;

    @Autowired
    RequestInventoryProductMappingRepo requestInventoryProductMappingRepo;

    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;

    @Autowired
    WarehouseManagementRepository warehouseManagementRepository;

    @Autowired
    WareHouseManagmentServiceAreamappingRepo wareHouseManagmentServiceAreamappingRepo;

    @Autowired
    PopManagementRepository popManagementRepository;

    @Autowired
    PopServiceAreaMappingRepo popServiceAreaMappingRepo;

    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @Autowired
    RequestInventoryMapper requestInventoryMapper;

    @Autowired
    RequestInventoryProductMappingRepo requestInvenotryProductMappingRepository;

    @Autowired
    OutwardRepository outwardRepository;

    @Autowired
    RequestInventoryProductMappingMapper requestInventoryProductMappingMapper;

    @Autowired
    WarhouseMapper warhouseMapper;

    @Autowired
    ServiceAreaMapper serviceAreaMapper;

    @Autowired
    PopManagementMapper popManagementMapper;

    @Autowired
    StaffUserMapper staffUserMapper;
    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    TeamUserMappingsRepocitory teamUserMappingsRepocitory;

    @Autowired
    WareHouseTeamsMappingRepo wareHouseTeamsMappingRepo;

    @Autowired
    private RequestInventoryServiceImpl requestInventoryService;

    @Autowired
    private RequestInventoryHistoryRepo requestInventoryHistoryRepo;

    public RequestInventoryServiceImpl(JpaRepository<RequestInventory, Long> repository, IBaseMapper<RequestInventoryDto, RequestInventory> mapper) {
        super(repository, mapper);
    }


    @Override
    public RequestInventoryDto saveEntity(RequestInventoryDto requestInventoryDto) throws Exception {
        try {
            RequestInventory requestInventory = requestInventoryMapper.dtoToDomain(requestInventoryDto, new CycleAvoidingMappingContext());
            List<RequestInventoryProductMappingDto> invenotryProductMappingList = requestInventoryDto.getRequestInvenotryProductMappings();
            requestInventory.setInventoryRequestStatus("Waiting for Approval");
            RequestInventory finalrequestInventory = requestInventoryRepo.save(requestInventory);
            if (requestInventoryDto != null) {
                String requestInventoryName = getInventoryRequestName("Request", "-Inventory-", "");
                finalrequestInventory.setRequestInventoryName(requestInventoryName);
                requestInventoryRepo.save(finalrequestInventory);
                invenotryProductMappingList.stream().forEach(requestInvenotryProductMapping -> {
                    RequestInvenotryProductMapping invenotryProductMapping = new RequestInvenotryProductMapping();
                    invenotryProductMapping.setInventoryRequestId(finalrequestInventory.getId());
                    invenotryProductMapping.setProductId(requestInvenotryProductMapping.getProductId());
                    invenotryProductMapping.setProductCategoryId(requestInvenotryProductMapping.getProductCategoryId());
                    invenotryProductMapping.setQuantity(requestInvenotryProductMapping.getQuantity());
                    invenotryProductMapping.setItemType(requestInvenotryProductMapping.getItemType());
                    requestInventoryProductMappingRepo.save(invenotryProductMapping);
                });

                if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase("Pop")) {
                    requestInventoryDto.setRequesterName(popManagementRepository.findById(requestInventory.getRequestNameId()).get().getPopName());
                } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase("ServiceArea")) {
                    requestInventoryDto.setRequesterName(serviceAreaRepository.findById(requestInventory.getRequestNameId()).get().getName());
                } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase("StaffUser")) {
                    requestInventoryDto.setRequesterName(staffUserRepository.findById(requestInventory.getRequestNameId().intValue()).get().getUsername());
                } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase("WareHouse")) {
                    requestInventoryDto.setRequesterName(warehouseManagementRepository.findById(requestInventory.getRequestNameId()).get().getName());
                }
                requestInventoryDto.setRequestToName(warehouseManagementRepository.findById(requestInventory.getRequestToWarehouseId()).get().getName());

            }

        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
        return requestInventoryDto;
    }
    public void validateRequest(RequestInventoryDto requestInventoryDto) {
        if (requestInventoryDto != null) {
            if (requestInventoryDto.getRequestInvenotryProductMappings() != null || requestInventoryDto.getRequestInvenotryProductMappings().size() > 0) {
                List<Long> productId = new ArrayList<>();
                for (RequestInventoryProductMappingDto requestInventoryProductMappingDto : requestInventoryDto.getRequestInvenotryProductMappings()) {
                    if (productId.contains(requestInventoryProductMappingDto.getProductId())) {
                        throw new CustomValidationException(APIConstants.FAIL, "Duplicate product is not allow", null);
                    }
                    productId.add(requestInventoryProductMappingDto.getProductId());
                }
            }
        }
    }

//    RequestInventoryDto findById(Long id) {
//        try {
//
//            RequestInventory requestInventory = requestInventoryRepo.findById(id).get();
//            RequestInventoryDto requestInventoryDto = requestInventoryMapper.domainToDTO(requestInventory, new CycleAvoidingMappingContext());
//            List<RequestInvenotryProductMapping> requestInvenotryProductMappingList = requestInvenotryProductMappingRepository.findAllByInventoryRequestId(id);
//            List<RequestInventoryProductMappingDto> requestInventoryProductMappingDtoList = new ArrayList<>();
//            requestInvenotryProductMappingList.stream().forEach(r -> {
//
//                RequestInventoryProductMappingDto requestInventoryProductMappingDto = new RequestInventoryProductMappingDto();
//                ProductCategory productCategory = productCategoryRepository.findById(r.getProductCategoryId()).get();
//                Product product = productRepository.findById(r.getProductId()).get();
//                requestInventoryProductMappingDto.setId(r.getId());
//                requestInventoryProductMappingDto.setInventoryRequestId(r.getInventoryRequestId());
//                requestInventoryProductMappingDto.setProductId(r.getProductId());
//                requestInventoryProductMappingDto.setProductName(product.getName());
//                requestInventoryProductMappingDto.setProductCategoryId(r.getProductCategoryId());
//                requestInventoryProductMappingDto.setProductCategoryName(productCategory.getName());
//                requestInventoryProductMappingDto.setQuantity(r.getQuantity());
//                requestInventoryProductMappingDto.setItemType(r.getItemType());
//                requestInventoryProductMappingDto.setRequestStatus(r.getRequestStatus());
//
//                //to Set Invetory Status
//                QOutward qOutward = QOutward.outward;
//                BooleanExpression booleanExpression = qOutward.isNotNull();
//                booleanExpression = booleanExpression.and(qOutward.productId.id.eq(r.getProductId()).and(qOutward.requestInventoryId.eq(r.getInventoryRequestId())).and(qOutward.requestInventoryProductId.eq(r.getId())));
//                Optional<Outward> outward = outwardRepository.findOne(booleanExpression);
//                if (outward.isPresent()) {
//                    requestInventoryProductMappingDto.setRequestStatus(r.getRequestStatus());
//                    requestInventoryProductMappingDto.setOutWardCreated(true);
//                }
//                requestInventoryProductMappingDtoList.add(requestInventoryProductMappingDto);
//            });
//            requestInventoryDto.setRequestInvenotryProductMappings(requestInventoryProductMappingDtoList);
//
//            if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase("Pop")) {
//                requestInventoryDto.setRequesterName(popManagementRepository.findById(requestInventory.getRequestNameId()).get().getPopName());
//            } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase("ServiceArea")) {
//                requestInventoryDto.setRequesterName(serviceAreaRepository.findById(requestInventory.getRequestNameId()).get().getName());
//            } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase("StaffUser")) {
//                requestInventoryDto.setRequesterName(staffUserRepository.findById(requestInventory.getRequestNameId().intValue()).get().getUsername());
//            } else if (requestInventoryDto.getOnBehalfOf().equalsIgnoreCase("WareHouse")) {
//                requestInventoryDto.setRequesterName(warehouseManagementRepository.findById(requestInventory.getRequestNameId()).get().getName());
//            }
//            requestInventoryDto.setRequestToName(warehouseManagementRepository.findById(requestInventory.getRequestToWarehouseId()).get().getName());
//            return requestInventoryDto;
//        } catch (Exception exception) {
//            throw new RuntimeException(exception.getMessage());
//        }
//    }


//    public RequestInventoryDto approveStatus(String status, Long id, String remarks) {
//        try {
//            RequestInventory requestInventory = requestInventoryRepo.findById(id).get();
//            List<RequestInvenotryProductMapping> requestInvenotryProductMappings = requestInvenotryProductMappingRepository.findAllByInventoryRequestId(id);
//            if (status.equalsIgnoreCase("Rejected")) {
//                requestInvenotryProductMappings.stream().forEach(requestInventoryProductMapping -> {
//                    RequestInvenotryProductMapping requestInvenotryProductMapping = requestInventoryProductMappingRepo.findById(requestInventoryProductMapping.getId()).get();
//                    //requestInvenotryProductMapping.setDeleted(true);
//                    requestInvenotryProductMapping.setRequestStatus("Rejected");
//                    requestInventoryProductMappingRepo.save(requestInvenotryProductMapping);
//                });
//                //requestInventory.setDeleted(true);
//                requestInventory.setStatus("Rejected");
//                requestInventory.setInventoryRequestStatus("Rejected");
//                requestInventory.setRemarks(remarks);
//                RequestInventory inventory = requestInventoryRepo.save(requestInventory);
//                return requestInventoryMapper.domainToDTO(inventory, new CycleAvoidingMappingContext());
//            } else {
//                requestInventory.setStatus("Approve");
//                requestInvenotryProductMappings.stream().forEach(requestInventoryProductMapping -> {
//                    RequestInvenotryProductMapping requestInvenotryProductMapping = requestInventoryProductMappingRepo.findById(requestInventoryProductMapping.getId()).get();
//                    requestInvenotryProductMapping.setRequestStatus("Open");
//                    requestInventoryProductMappingRepo.save(requestInvenotryProductMapping);
//                });
//                requestInventory.setInventoryRequestStatus("In-Progress");
//                requestInventory.setRemarks(remarks);
//                RequestInventory inventory = requestInventoryRepo.save(requestInventory);
//                return requestInventoryMapper.domainToDTO(inventory, new CycleAvoidingMappingContext());
//            }
//
//        } catch (Exception exception) {
//            throw new RuntimeException(exception.getMessage());
//        }
//    }


    public GenericDataDTO getAll(String onBehalgOf) throws Exception {
        GenericDataDTO genericDataDTO;
        try {
            genericDataDTO = new GenericDataDTO();
            if (onBehalgOf.equalsIgnoreCase("WareHouse")) {
                List wareHouseList = getAllWareHousesByLoggedInSA();
                genericDataDTO.setDataList(wareHouseList);
            } else if (onBehalgOf.equalsIgnoreCase("Pop")) {
                List popList = getAllPop();
                genericDataDTO.setDataList(popList);
            } else if (onBehalgOf.equalsIgnoreCase("ServiceArea")) {
                List serviceAreaList = getAllServiceAreas();
                genericDataDTO.setDataList(serviceAreaList);
            } else {
                List staffUserList = getAllStaff();
                genericDataDTO.setDataList(staffUserList);

            }
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }

        return genericDataDTO;
    }

    List<CommonResponceDto> getAllWareHousesByLoggedInSA() {
//        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingsList = staffUserServiceAreaMappingRepository.findAllByStaffId(Arrays.asList(getLoggedInUserId()));
//        List<Integer> serviceAreaIdList = staffUserServiceAreaMappingsList.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toList());
//        List<WareHouseServiceAreaMapping> wareHouseServiceAreaMappingList = wareHouseManagmentServiceAreamappingRepo.findAllByServiceIdIn(serviceAreaIdList);
//        List<WareHouse> wareHouseList = new ArrayList<>();
//        Set<Long> ids = wareHouseServiceAreaMappingList.stream().map(WareHouseServiceAreaMapping::getWarehouseId).collect(Collectors.toSet());
//        ids.stream().forEach(r -> {
//            WareHouse wareHouse1 = warehouseManagementRepository.findById(r).get();
//            wareHouseList.add(wareHouse1);
//        });
//        List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
//        List<WareHouse> allActiveWareHouses = wareHouseList.stream().filter(wareHouse -> wareHouse.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).filter(wareHouse -> wareHouse.getIsDeleted().equals(false)).collect(Collectors.toList());
//        allActiveWareHouses.stream().forEach(wareHouse -> {
//            CommonResponceDto commonResponceDto = new CommonResponceDto();
//            commonResponceDto.setId(wareHouse.getId());
//            commonResponceDto.setName(wareHouse.getName());
//            commonResponceDtos.add(commonResponceDto);
//
//        });
//        return commonResponceDtos;
        List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingsList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Arrays.asList(getLoggedInUserId()));
        List<Integer> serviceAreaIdList = staffUserServiceAreaMappingsList.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toList());
        if (!serviceAreaIdList.isEmpty()) {
            List<WareHouseServiceAreaMapping> wareHouseServiceAreaMappingList = wareHouseManagmentServiceAreamappingRepo.findAllByServiceIdIn(serviceAreaIdList);
            List<Long> ids = wareHouseServiceAreaMappingList.stream().map(WareHouseServiceAreaMapping::getWarehouseId).collect(Collectors.toList());
            QWareHouse qWareHouse = QWareHouse.wareHouse;
            BooleanExpression booleanExpression = qWareHouse.isDeleted.eq(false).and(qWareHouse.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qWareHouse.id.in(ids));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qWareHouse.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            }
            List<WareHouse> allActiveWareHouses = IterableUtils.toList(warehouseManagementRepository.findAll(booleanExpression));
            allActiveWareHouses.stream().forEach(wareHouse -> {
                CommonResponceDto commonResponceDto = new CommonResponceDto();
                commonResponceDto.setId(wareHouse.getId());
                commonResponceDto.setName(wareHouse.getName());
                commonResponceDtos.add(commonResponceDto);
            });
        }
        return commonResponceDtos;
    }


    List<CommonResponceDto> getAllServiceAreas() {
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingsList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Arrays.asList(getLoggedInUserId()));
        List<ServiceArea> serviceAreaList = new ArrayList<>();
        Set<Integer> ids = staffUserServiceAreaMappingsList.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toSet());
        ids.stream().forEach(r -> {
            ServiceArea area = serviceAreaRepository.findById(Long.valueOf(r)).get();
            serviceAreaList.add(area);
        });
        List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
        List<ServiceArea> allActiveServiceAreas = serviceAreaList.stream().filter(serviceArea -> serviceArea.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).filter(serviceArea -> serviceArea.getIsDeleted().equals(false)).collect(Collectors.toList());
        allActiveServiceAreas.stream().forEach(serviceArea -> {
            CommonResponceDto commonResponceDto = new CommonResponceDto();
            commonResponceDto.setId(serviceArea.getId());
            commonResponceDto.setName(serviceArea.getName());
            commonResponceDtos.add(commonResponceDto);

        });
        return commonResponceDtos;
    }

    List<CommonResponceDto> getAllStaff() {
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingsList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Arrays.asList(getLoggedInUserId()));
        List<Integer> serviceAreaIdList = staffUserServiceAreaMappingsList.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toList());
        Set<StaffUser> staffUserList = new HashSet<>();
        serviceAreaIdList.stream().forEach(serviceAreaid -> {
            List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings = staffUserServiceAreaMappingRepository.findAllByServiceId(serviceAreaid);
            staffUserServiceAreaMappings.stream().collect(Collectors.toSet()).forEach(s -> {
                StaffUser staffUser = staffUserRepository.findById(s.getStaffId()).get();
                staffUserList.add(staffUser);
            });
        });
        List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
        List<StaffUser> allActiveStaffUsers = staffUserList.stream().filter(staffUser -> staffUser.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).filter(staffUser -> staffUser.getIsDelete().equals(false)).collect(Collectors.toList());
        allActiveStaffUsers.stream().forEach(staffUser -> {
            CommonResponceDto commonResponceDto = new CommonResponceDto();
            commonResponceDto.setId(staffUser.getId().longValue());
            commonResponceDto.setName(staffUser.getUsername());
            commonResponceDtos.add(commonResponceDto);
        });
        return commonResponceDtos;
    }


    List<CommonResponceDto> getAllPop() {
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingsList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Arrays.asList(getLoggedInUserId()));
        List<Integer> serviceAreaIdList = staffUserServiceAreaMappingsList.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toList());
        List<PopServiceAreaMapping> popServiceAreaMappingList = popServiceAreaMappingRepo.findAllByServiceIdIn(serviceAreaIdList);
        List<PopManagement> popManagementList = new ArrayList<>();
        Set<Long> ids = popServiceAreaMappingList.stream().map(PopServiceAreaMapping::getPopId).collect(Collectors.toSet());
        ids.stream().forEach(r -> {
            PopManagement pop1 = popManagementRepository.findById(r).get();
            popManagementList.add(pop1);

        });
        List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
        List<PopManagement> allActivePops = popManagementList.stream().filter(popManagement -> popManagement.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).filter(popManagement -> popManagement.getIsDeleted().equals(false)).collect(Collectors.toList());
        allActivePops.stream().forEach(pop -> {
            CommonResponceDto commonResponceDto = new CommonResponceDto();
            commonResponceDto.setId(pop.getId().longValue());
            commonResponceDto.setName(pop.getPopName());
            commonResponceDtos.add(commonResponceDto);

        });
        return commonResponceDtos;
    }

//    public GenericDataDTO getAllRequestByCurrentStaff(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            QRequestInventory qRequestInventory = QRequestInventory.requestInventory;
//            BooleanExpression boolExp = qRequestInventory.isNotNull().and(qRequestInventory.createdById.eq(getLoggedInUserId()).and(qRequestInventory.isDeleted.eq(false)));
//            Page<RequestInventory> page1 = requestInventoryRepo.findAll(boolExp, pageRequest);
//            page1.forEach(p -> p.setRequesterName(
//                    p.getOnBehalfOf().equalsIgnoreCase("Pop") ? popManagementRepository.findById(p.getRequestNameId()).get().getPopName() :
//                            p.getOnBehalfOf().equalsIgnoreCase("Warehouse") ? warehouseManagementRepository.findById(p.getRequestNameId()).get().getName() :
//                                    p.getOnBehalfOf().equalsIgnoreCase("ServiceArea") ? serviceAreaRepository.findById(p.getRequestNameId()).get().getName() :
//                                            p.getOnBehalfOf().equalsIgnoreCase("StaffUser") ? staffUserRepository.findById(p.getRequestNameId().intValue()).get().getUsername() : ""));
//
//            page1.forEach(p -> p.setRequestToName(warehouseManagementRepository.findById(p.getRequestToWarehouseId()).get().getName()));
//            page1.forEach(r -> {
//                r.setInventoryRequestStatus(r.getInventoryRequestStatus());
//                List<RequestInvenotryProductMapping> requestInvenotryProductMappingList = requestInvenotryProductMappingRepository.findAllByInventoryRequestId(r.getId());
//                requestInvenotryProductMappingList.stream().forEach(t->{
//                    t.setRequestStatus(t.getRequestStatus());
//                });
//            });
//
//            if (null != page && 0 < page1.getSize()) {
//                makeGenericResponse(genericDataDTO, page1);
//            }
//
//
//            return genericDataDTO;
//        } catch (Exception exception) {
//            throw new RuntimeException(exception.getMessage());
//        }
//    }


//    public GenericDataDTO getAllAssignedRequestInventory(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        try {
//
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//
//            List<Long> teamIdList = teamUserMappingsRepocitory.teamIds(Long.valueOf(getLoggedInUserId()));
//            List<WareHouseTeamsMapping> wareHouseTeamsMappings = wareHouseTeamsMappingRepo.findAllByTeamIdIn(teamIdList);
//            if (!(wareHouseTeamsMappings.isEmpty())) {
//                List<Long> wareHouseIdList = wareHouseTeamsMappings.stream().map(WareHouseTeamsMapping::getWarehouseId).collect(Collectors.toList());
//
//                QRequestInventory qRequestInventory = QRequestInventory.requestInventory;
//                BooleanExpression booleanExpression = qRequestInventory.isNotNull();
//                booleanExpression = booleanExpression.and(qRequestInventory.isDeleted.isFalse()).and(qRequestInventory.requestToWarehouseId.in(wareHouseIdList)).and(qRequestInventory.createdById.ne(getLoggedInUserId()));
//
//                Page<RequestInventory> page1 = requestInventoryRepo.findAll(booleanExpression, pageRequest);
//                page1.forEach(p -> p.setRequesterName(
//                        p.getOnBehalfOf().equalsIgnoreCase("Pop") ? popManagementRepository.findById(p.getRequestNameId()).get().getPopName() :
//                                p.getOnBehalfOf().equalsIgnoreCase("Warehouse") ? warehouseManagementRepository.findById(p.getRequestNameId()).get().getName() :
//                                        p.getOnBehalfOf().equalsIgnoreCase("ServiceArea") ? serviceAreaRepository.findById(p.getRequestNameId()).get().getName() :
//                                                p.getOnBehalfOf().equalsIgnoreCase("StaffUser") ? staffUserRepository.findById(p.getRequestNameId().intValue()).get().getUsername() : ""));
//
//                page1.forEach(p -> p.setRequestToName(warehouseManagementRepository.findById(p.getRequestToWarehouseId()).get().getName()));
//                page1.forEach(r -> {
//                    List<RequestInvenotryProductMapping> requestInvenotryProductMappingList = requestInvenotryProductMappingRepository.findAllByInventoryRequestId(r.getId());
//                    if (requestInvenotryProductMappingList.size() != 0) {
//                        List<String> list = new ArrayList<>(requestInvenotryProductMappingList.stream().map(RequestInvenotryProductMapping::getRequestStatus).collect(Collectors.toList()));
//                        if (!(list.stream().anyMatch(str->str==null))) {
//                            if (list.stream().anyMatch(str -> str.equalsIgnoreCase("Pending"))) {
//                                r.setInventoryRequestStatus("Waiting for Approval");
//                            } else if (list.stream().allMatch(str -> str.equalsIgnoreCase("Open"))) {
//                                r.setInventoryRequestStatus("In Progress");
//                            } else if (list.stream().anyMatch(str -> str.equalsIgnoreCase("Close"))) {
//                                r.setInventoryRequestStatus("Completed");
//                            } else {
//                                r.setInventoryRequestStatus("Partially Completed");
//                            }
//                        }
//                    }
//                });
//
//                if (null != page && 0 < page1.getSize()) {
//                    makeGenericResponse(genericDataDTO, page1);
//                }
//            }
//            return genericDataDTO;
//
//        } catch (Exception exception) {
//            throw new RuntimeException(exception.getMessage());
//        }
//    }

//    public String deleteInventory(Long id) {
//        try {
//            RequestInventory requestInventory = requestInventoryRepo.findById(id).get();
//            List<RequestInvenotryProductMapping> requestInventoryProductMappingRepoList = requestInvenotryProductMappingRepository.findAllByInventoryRequestId(id);
//            requestInventoryProductMappingRepoList.stream().forEach(r -> {
//                r.setDeleted(true);
//                requestInvenotryProductMappingRepository.save(r);
//            });
//            requestInventory.setDeleteFlag(true);
//            requestInventoryRepo.save(requestInventory);
//            return "Deleted Successfully";
//        } catch (Exception exception) {
//            throw new RuntimeException(exception.getMessage());
//        }
//    }


    public String getInventoryRequestName(String flag1, String flag2, String flag3) {
        String flag = "";
        if (flag1 != null) {
            flag += flag1;
        }
        if (flag2 != null) {
            flag += flag2;
        }
        if (flag3 != null) {
            RequestInventory requestInventory = requestInventoryRepo.findTopByOrderByIdDesc();
            if (requestInventory == null) {
                flag += 1;
            } else {
                flag += requestInventory.getId() + 1;
            }
        }
        return flag;
    }

    public GenericDataDTO forwardRequestToWareHouse (Long reqId, Long forwardToReqId, String remarks) throws Exception {

        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            RequestInventory requestInventory = requestInventoryRepo.findById(reqId).get();
            RequestInventoryDto requestInventoryDto = new RequestInventoryDto();
            if (requestInventory.getOnBehalfOf().equalsIgnoreCase("WareHouse")) {
                if (requestInventory.getRequestNameId() == forwardToReqId) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "WareHouses should be different", null);
                }
            }
            if (requestInventory.getRequestToWarehouseId() == forwardToReqId ){
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "WareHouses should be different", null);
            } else if (requestInventory.getRequestToWarehouseId() != forwardToReqId ) {
                RequestInventoryHistory requestInventoryHistory = new RequestInventoryHistory();
                requestInventoryHistory.setRequestInventoryId(requestInventory.getId());
                requestInventoryHistory.setRequestInventoryName(requestInventory.getRequestInventoryName());
                requestInventoryHistory.setRequestNameId(requestInventory.getRequestNameId());
                requestInventoryHistory.setRequestToWarehouseId(requestInventory.getRequestToWarehouseId());
                requestInventoryHistory.setRemarks(remarks);
                requestInventoryHistoryRepo.save(requestInventoryHistory);
                requestInventory.setRequestToWarehouseId(forwardToReqId);
                WareHouse wareHouse = warehouseManagementRepository.findById(forwardToReqId).get();
                requestInventory.setRequestToName(wareHouse.getName());
                RequestInventory requestInventory1 = requestInventoryRepo.save(requestInventory);
                requestInventoryDto = requestInventoryMapper.domainToDTO(requestInventory1, new CycleAvoidingMappingContext());
                genericDataDTO.setData(requestInventoryDto);
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                return genericDataDTO;
            }
        }catch (CustomValidationException ex){
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "WareHouses should be different", null);
        }
        return genericDataDTO;
    }
    List<CommonResponceDto> getAllWareHouse() {
        List<StaffUserServiceAreaMapping> staffUserServiceAreaMappingsList = staffUserServiceAreaMappingRepository.findAllByStaffIdIn(Arrays.asList(getLoggedInUserId()));
        List<Integer> serviceAreaIdList = staffUserServiceAreaMappingsList.stream().map(StaffUserServiceAreaMapping::getServiceId).collect(Collectors.toList());
        if (!serviceAreaIdList.isEmpty()) {
            List<WareHouseServiceAreaMapping> wareHouseServiceAreaMappingList = wareHouseManagmentServiceAreamappingRepo.findAllByServiceIdIn(serviceAreaIdList);
            List<Long> ids = wareHouseServiceAreaMappingList.stream().map(WareHouseServiceAreaMapping::getWarehouseId).collect(Collectors.toList());
            QWareHouse qWareHouse = QWareHouse.wareHouse;
            BooleanExpression booleanExpression = qWareHouse.isDeleted.eq(false).and(qWareHouse.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qWareHouse.id.in(ids));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qWareHouse.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            }
            List<WareHouse> allActiveWareHouses = IterableUtils.toList(warehouseManagementRepository.findAll(booleanExpression));
            List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
            allActiveWareHouses.stream().forEach(wareHouse -> {
                CommonResponceDto commonResponceDto = new CommonResponceDto();
                commonResponceDto.setId(wareHouse.getId());
                commonResponceDto.setName(wareHouse.getName());
                commonResponceDtos.add(commonResponceDto);
            });
            return commonResponceDtos;
        } else {
            List<CommonResponceDto> commonResponceDtos = new ArrayList<>();
            QWareHouse qWareHouse = QWareHouse.wareHouse;
            BooleanExpression booleanExpression = qWareHouse.isDeleted.eq(false).and(qWareHouse.status.eq(CommonConstants.ACTIVE_STATUS));
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != 1) {
                // TODO: pass mvnoID manually 6/5/2025
                booleanExpression = booleanExpression.and(qWareHouse.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
            }
            List<WareHouse> wareHouseList = IterableUtils.toList(warehouseManagementRepository.findAll(booleanExpression));
            wareHouseList.stream().forEach(wareHouse -> {
                CommonResponceDto commonResponceDto = new CommonResponceDto();
                commonResponceDto.setId(wareHouse.getId());
                commonResponceDto.setName(wareHouse.getName());
                commonResponceDtos.add(commonResponceDto);
            });
            return commonResponceDtos;
        }
    }
    @Override
    public String getModuleNameForLog() {
        return "[RequestIvnetoryService]";
    }

    //Validate Approve Request
    public void validateApproveRequest(Long requestId) {
        QRequestInventory qRequestInventory = QRequestInventory.requestInventory;
        BooleanExpression booleanExpression = qRequestInventory.id.eq(requestId);
        List<RequestInventory> requestInventories = IterableUtils.toList(requestInventoryRepo.findAll(booleanExpression));
        if (requestInventories.get(0).isDeleted()) {
           throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "This request is deleted please refresh the page", null);
        }
    }
}
