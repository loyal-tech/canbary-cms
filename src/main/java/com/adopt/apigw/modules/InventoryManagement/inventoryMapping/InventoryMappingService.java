package com.adopt.apigw.modules.InventoryManagement.inventoryMapping;


import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.NMSIntegrationConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.EndMacMappping;
import com.adopt.apigw.modules.CommonList.repository.CommonListRepository;
import com.adopt.apigw.modules.CustomerDBR.repository.CustomerDBRRepository;
import com.adopt.apigw.modules.Integration.IntegrationClient;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMacRepo;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.QInOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.PopManagement;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.mapper.PopManagementMapper;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.repository.PopManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.model.PopManagementDTO;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.service.PopManagementService;
import com.adopt.apigw.modules.InventoryManagement.VendorManagment.Vendor;
import com.adopt.apigw.modules.InventoryManagement.VendorManagment.VendorRepo;
import com.adopt.apigw.modules.InventoryManagement.inward.*;
import com.adopt.apigw.modules.InventoryManagement.item.*;
import com.adopt.apigw.modules.InventoryManagement.outward.OutwardServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.product.ProductDto;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.modules.InventoryManagement.product.ProductServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategoryService;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerService;
import com.adopt.apigw.modules.NetworkDevices.model.NetworkDeviceDTO;
import com.adopt.apigw.modules.NetworkDevices.service.NetworkDeviceService;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.Teams.repository.HierarchyRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.pojo.api.StaffUserPojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.ItemMessage;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.DebitDocRepository;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.common.WorkflowAuditService;
import com.adopt.apigw.service.postpaid.ChargeService;
import com.adopt.apigw.service.postpaid.EndMacMapppingService;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class InventoryMappingService extends ExBaseAbstractService<InventoryMappingDto, InventoryMapping, Long> {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    InventoryMappingRepo repository;

    @Autowired
    ProductServiceImpl productService;

    @Autowired
    InventoryMappingMapper mapper;


    @Autowired
    CommonListRepository commonListRepository;

    @Autowired
    HierarchyRepository hierarchyRepository;

    @Autowired
    StaffUserService staffUserService;

    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    OutwardServiceImpl outwardService;

    @Autowired
    InwardServiceImpl inwardService;

    @Autowired
    InOutWardMACService inOutWardMACService;

    @Autowired
    ClientServiceRepository clientServiceRepository;
    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    EndMacMapppingService endMacMapppingService;

    @Autowired
    ChargeService chargeService;

    @Autowired
    CustomerDBRRepository customerDBRRepository;

    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    WorkflowAuditService workflowAuditService;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    InwardRepository inwardRepository;
    @Autowired
    PopManagementService popManagementService;
    @Autowired
    ServiceAreaService serviceAreaService;
    @Autowired
    ProductCategoryService productCategoryService;

    @Autowired
    ServiceAreaMapper serviceAreaMapper;

    @Autowired
    NetworkDeviceService networkDeviceService;
    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;
    @Autowired
    ItemServiceImpl itemService;
    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;
    @Autowired
    CustInvParamsRepo custInvParamsRepo;
    @Autowired
    VendorRepo vendorRepo;


    @Autowired
    InOutWardMacRepo inOutWardMacRepo;

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    InventoryMappingRepo inventoryMappingRepo;

    @Autowired
    private PopManagementRepository popManagementRepository;
    @Autowired
    private PopManagementMapper popManagementMapper;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductOwnerService productOwnerService;

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    InventoryMappingMapper inventoryMappingMapper;

    @Autowired
    IntegrationClient integrationClient;
    @Autowired
    private StaffUserRepository staffUserRepository;


    public InventoryMappingService(InventoryMappingRepo repository, InventoryMappingMapper mapper) {
        super(repository, mapper);
    }


    @Override
    public InventoryMappingRepo getRepository() {
        return repository;
    }

    @Override
    public String getModuleNameForLog() {
        return "[InventoryMappingService]";
    }

    public List<InventoryMappingDto> getInventoryMappingByStaffId(Long staffId) {
        QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
        JPAQuery<?> query = new JPAQuery<Void>(entityManager);
        BooleanExpression booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId)).and(qInventoryMapping.isDeleted.eq(false)));
        return mapper.domainToDTO((List<InventoryMapping>) repository.findAll(booleanExpression), new CycleAvoidingMappingContext());
    }

    @Transactional
    @Override
    public InventoryMappingDto saveEntity(InventoryMappingDto inventoryMappingDto) throws Exception {
//        InventoryMappingDto inventoryMappingDto = null;
        try {
//            inventoryMappingDto = super.saveEntity(entity);
            if (Objects.equals(getLoggedInUser().getUsername(), "admin") || Objects.equals(getLoggedInUser().getUsername(), "superadmin")) {
                inventoryMappingDto.setNextApproverId(null);
                inventoryMappingDto.setTeamHierarchyMappingId(null);
                inventoryMappingDto.setApprovalStatus("Approve");

            } else {
                if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(getMvnoIdForWorkflow(inventoryMappingDto.getOwnerId(), inventoryMappingDto.getOwnerType()), null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, false, true, getMapper().dtoToDomain(inventoryMappingDto, new CycleAvoidingMappingContext()));
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                        StaffUser staffUser = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).get();
                        inventoryMappingDto.setNextApproverId(Integer.valueOf(map.get("staffId")));
                        inventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                        inventoryMappingDto.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        inventoryMappingDto.setApprovalStatus("Pending");
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(inventoryMappingDto.getId()), inventoryMappingDto.getProductName(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
                    } else {
                        inventoryMappingDto.setNextApproverId(getLoggedInUserId());
                        inventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                        inventoryMappingDto.setTeamHierarchyMappingId(null);
                        inventoryMappingDto.setApprovalStatus("Pending");
                    }
                } else {
                    inventoryMappingDto.setNextApproverId(getLoggedInUserId());
                    inventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                    inventoryMappingDto.setTeamHierarchyMappingId(null);
                    inventoryMappingDto.setApprovalStatus("Pending");
                }
            }
            ProductDto productDto = productService.getEntityById(inventoryMappingDto.getProductId(),inventoryMappingDto.getMvnoId());
            inventoryMappingDto.setProductId(productDto.getId());
            if (productDto.getProductCategory().isHasMac() || productDto.getProductCategory().isHasSerial())
                switch (productDto.getExpiryTimeUnit()) {
                    case "Day":
                        inventoryMappingDto.setExpiryDateTime(LocalDateTime.now().plusDays(productDto.getExpiryTime()));
                        break;
                    case "Month":
                        inventoryMappingDto.setExpiryDateTime(LocalDateTime.now().plusMonths(productDto.getExpiryTime()));
                        break;
                }
            //update itemHistory and Item
            InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(inventoryMappingDto.getInOutWardMACMapping().get(0).getId()).orElse(null);
            if (inOutWardMACMapping.getMacAddress() == null && inventoryMappingDto.getInOutWardMACMapping().get(0).getMacAddress() != null) {
                inOutWardMacRepo.save(inOutWardMACMapping);
                Item item = itemRepository.findById(inOutWardMACMapping.getItemId()).orElse(null);
                item.setMacAddress(inventoryMappingDto.getInOutWardMACMapping().get(0).getMacAddress());
                itemRepository.save(item);
                QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
                BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull();
                booleanExpression = booleanExpression.and(qInOutWardMACMapping.itemId.in(item.getId()).and(qInOutWardMACMapping.isForwarded.ne(-1)));
                List<InOutWardMACMapping> inOutWardMACMappingList = (List<InOutWardMACMapping>) inOutWardMacRepo.findAll(booleanExpression);
                inOutWardMACMappingList.stream().forEach(r -> {
                    r.setMacAddress(item.getMacAddress());
                    inOutWardMacRepo.save(r);
                });
            }

            //Set mac Address To InOumapping
            List<InOutWardMACMapping> inOutWardMACMappings = inventoryMappingDto.getInOutWardMACMapping();
            for (InOutWardMACMapping mapping : inOutWardMACMappings) {
                long count = Duration.between(LocalDateTime.now(), inventoryMappingDto.assignedDateTime).toDays();
                mapping.setUsedCount((int) count);
            }
//        inventoryMappingDto.setInOutWardMACMapping(inOutWardMACMappings);
//        inventoryMappingDto = super.saveEntity(inventoryMappingDto);
            if (inventoryMappingDto.inwardId != null) {
                InwardDto inwardDto = inwardService.getEntityForUpdateAndDelete(inventoryMappingDto.getInwardId(),inventoryMappingDto.getMvnoId());
                if (inwardDto.getUnusedQty() <= 0 && inwardDto.getUsedQty() <= 0) {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), " ** qty -ve **", null);
                } else {
                    inventoryMappingDto.setInOutWardMACMapping(inOutWardMACMappings);
                    inventoryMappingDto = super.saveEntity(inventoryMappingDto);
                    inwardDto.setUnusedQty(inwardDto.getUnusedQty() - inventoryMappingDto.getQty());
                    inwardDto.setUsedQty(inwardDto.getUsedQty() + inventoryMappingDto.getQty());
                    inwardService.updateEntity(inwardDto);
                }
//                InventoryMappingDto inventoryMappingDto1 = super.saveEntity(inventoryMappingDto);
                productOwnerService.updateProductOwnerForSerializedProduct(inventoryMappingDto.getQty(), inventoryMappingDto.getProductId(), Long.valueOf(inventoryMappingDto.getStaffId()), CommonConstants.STAFF);
            }
            productOwnerService.updateProductOwnerForSerializedProduct(inventoryMappingDto.getQty(), inventoryMappingDto.getProductId(), Long.valueOf(inventoryMappingDto.getStaffId()), CommonConstants.STAFF);
            inventoryMappingDto = super.saveEntity(inventoryMappingDto);

        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
        return inventoryMappingDto;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        PageRequest pageRequest = super.generatePageRequest(page, pageSize, sortBy, sortOrder);
        QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
        BooleanExpression booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.isDeleted.eq(false));
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (filterList.size() > 0) {
            for (GenericSearchModel genericSearchModel : filterList) {
                booleanExpression = booleanExpression.and(qInventoryMapping.ownerId.eq(Long.parseLong(genericSearchModel.getFilterValue())).and(qInventoryMapping.ownerType.equalsIgnoreCase(genericSearchModel.getFilterColumn())));
            }
        }
        return makeGenericResponse(genericDataDTO, repository.findAll(booleanExpression, pageRequest));
    }

    @Transactional
    public GenericDataDTO approveInventory(Long inventoryMappingId, boolean isApproveRequest, String inventoryApprovalRemark,Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        InventoryMappingDto entity = super.getEntityById(inventoryMappingId,mvnoId);
        ProductDto dto = productService.getEntityById(entity.getProductId(),mvnoId);
//        StaffUser loggedInUser = staffUserRepository.findById(getLoggedInUserId()).get();
        entity = updateItemChanges(inventoryMappingId, isApproveRequest, inventoryApprovalRemark);
        InventoryMapping inventoryMapping1 = inventoryMappingRepo.findById(inventoryMappingId).get();
        if (entity.getInOutWardMACMapping().size() != 0) {
            List<InOutWardMACMapping> inOutWardMACMapping = inOutWardMacRepo.findByInventoryMappingId(inventoryMappingId);
            Item item = null;
            if (inOutWardMACMapping.size() > 0) {
                item = itemRepository.findById(inOutWardMACMapping.get(0).getItemId()).orElse(null);
            }
            ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal for Pop and Service Area");
//            messageSender.send(itemMessage, RabbitMqConstants.QUEUE_APIGW_APPROVE_SERIALIZEDITEM_FOR_INTEGRATION);
            kafkaMessageSender.send(new KafkaMessageData(itemMessage, ItemMessage.class.getSimpleName()));

        }
       /* if (Objects.equals(loggedInUser.getUsername(), "admin") || Objects.equals(getLoggedInUser().getUsername(), "superadmin")) {

            entity.setNextApproverId(null);
            entity.setPreviousApproveId(getLoggedInUserId());
            entity.setTeamHierarchyMappingId(null);
            if(isApproveRequest) {
                entity.setApprovalStatus("Approve");
                entity=updateItemChanges(inventoryMappingId,isApproveRequest,inventoryApprovalRemark);
            }else{
                entity.setApprovalStatus("REJECTED");
                entity=updateItemChanges(inventoryMappingId,isApproveRequest,inventoryApprovalRemark);
            }
            genericDataDTO.setData(super.saveEntity(entity));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            return genericDataDTO;
        }*/

        /*if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
            Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(getMvnoIdForWorkflow(entity.getOwnerId(), entity.getOwnerType()), null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest,false, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()));
            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                StaffUser staffUser = staffUserService.get(Integer.valueOf(map.get("staffId")));
                entity.setNextApproverId(Integer.valueOf(map.get("staffId")));
                entity.setPreviousApproveId(getLoggedInUserId());
                entity.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                entity.setApprovalStatus("PENDING");
                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(entity.getId()), entity.getProductName(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(entity.getId()), entity.getProductName(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
            } else {
                entity.setNextApproverId(null);
                entity.setTeamHierarchyMappingId(null);
                entity.setPreviousApproveId(getLoggedInUserId());
                if (isApproveRequest) {
                    entity.setApprovalStatus("Approve");
                    entity=updateItemChanges(inventoryMappingId,isApproveRequest,inventoryApprovalRemark);
                } else {
                    entity.setApprovalStatus("Rejected");
                    entity=updateItemChanges(inventoryMappingId,isApproveRequest,inventoryApprovalRemark);
                }
                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(entity.getId()), entity.getProductName(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
            }
        } else {
            Map<String, Object> map = hierarchyService.getTeamForNextApprove(getMvnoIdForWorkflow(entity.getOwnerId(), entity.getOwnerType()), null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest,false, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()));
            if (map.containsKey("assignableStaff")) {
                genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            } else {
                entity.setNextApproverId(null);
                entity.setTeamHierarchyMappingId(null);
                if (isApproveRequest) {
                    entity.setApprovalStatus("Approve");
                    entity=updateItemChanges(inventoryMappingId,isApproveRequest,inventoryApprovalRemark);
                } else {
                    entity.setApprovalStatus("Rejected");
                    entity=updateItemChanges(inventoryMappingId,isApproveRequest,inventoryApprovalRemark);
                }
                entity.setPreviousApproveId(getLoggedInUserId());

            }
        }*/
        entity.setApprovalRemark(inventoryApprovalRemark);
        genericDataDTO.setData(super.saveEntity(entity));
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        return genericDataDTO;
    }

    public InventoryMappingDto updateItemChanges(Long inventoryMappingId, boolean isApproveRequest, String inventoryApprovalRemark) throws Exception {
        InventoryMappingDto entity;
        try {
            InventoryMapping inventoryMapping = inventoryMappingRepo.findById(inventoryMappingId).get();
            entity =  inventoryMappingMapper.domainToDTO(inventoryMapping,new CycleAvoidingMappingContext());
//            super.getEntityById(inventoryMappingId);
            ProductDto dto = productService.getEntityById(entity.getProductId(),entity.getMvnoId());
//            StaffUser loggedInUser = staffUserService.get(getLoggedInUserId());

            if (isApproveRequest) {
                entity.getInOutWardMACMapping().forEach(inOutWardMACMapping -> {
                    EndMacMappping custMacMappping = new EndMacMappping();
                    custMacMappping.setMacAddress(inOutWardMACMapping.getMacAddress());
                    endMacMapppingService.save(custMacMappping);
                });

                List<InOutWardMACMapping> inventoryMappings = inOutWardMacRepo.findByInventoryMappingId(inventoryMappingId);
                inventoryMappings.forEach(inOutWardMACMapping -> {
                    Item item = itemRepository.findById(inOutWardMACMapping.getItemId()).get();
                    Inward inward = inwardRepository.findById(inOutWardMACMapping.getInwardId()).get();

                    if (item.getWarranty().equalsIgnoreCase("NotStarted") || item.getWarranty().equalsIgnoreCase("Paused")) {

                        LocalDateTime localDateTime = inward.getCreatedate().plusDays(item.getWarrantyPeriod());
                        Duration setRemaingDaysDuration = Duration.between(LocalDateTime.now(), localDateTime);
                        if (setRemaingDaysDuration.toDays() == 0) {
                            try {
                                itemService.updateItemWarranty(item.getId(), "Expired");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            item.setRemainingDays(String.valueOf(0));
                        } else {
                            try {
                                itemService.updateItemWarranty(item.getId(), "InWarranty");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            item.setRemainingDays(String.valueOf(setRemaingDaysDuration.toDays()));
                            InventoryMapping mapping = inventoryMappingRepo.findById(inventoryMappingId).get();

                            if (mapping.getOwnerType().equalsIgnoreCase("Pop")) {
                                try {
                                    itemService.updateItemStatusForServiceAreaAndPop(item.getId(), CommonConstants.ALLOCATED, null, null, mapping.getOwnerId(), CommonConstants.ASSIGN_INVETORIES);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (mapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                                try {
                                    itemService.updateItemStatusForServiceAreaAndPop(item.getId(), CommonConstants.ALLOCATED, null, mapping.getOwnerId(), null, CommonConstants.ASSIGN_INVETORIES);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    if (entity.getOwnerType().equalsIgnoreCase("Pop")) {
                        item.setOwnerType("Pop");
                        item.setOwnerId(entity.getOwnerId());
                    }
                    if (entity.getOwnerType().equalsIgnoreCase("Service Area")) {
                        item.setOwnerType("Service Area");
                        item.setOwnerId(entity.getOwnerId());
                    }
                    itemRepository.save(item);
                });


            }

            if (!isApproveRequest) {
                if (dto.getProductCategory().isHasMac() || dto.getProductCategory().isHasSerial() || dto.getProductCategory().isHasTrackable()) {
                    List<InOutWardMACMapping> inventoryMappings = inOutWardMacRepo.findByInventoryMappingId(inventoryMappingId);
                    inventoryMappings.forEach(inOutWardMACMapping -> {
                        Item item = itemRepository.findById(inOutWardMACMapping.getItemId()).get();
                        InventoryMapping mapping = inventoryMappingRepo.findById(inventoryMappingId).get();
                        if (mapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            try {
                                itemService.updateItemStatusForServiceAreaAndPop(item.getId(), CommonConstants.UNALLOCATED, null, null, mapping.getOwnerId(), CommonConstants.REJECT_INVETORIES);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (mapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            try {
                                itemService.updateItemStatusForServiceAreaAndPop(item.getId(), CommonConstants.UNALLOCATED, null, mapping.getOwnerId(), null, CommonConstants.REJECT_INVETORIES);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    for (InOutWardMACMapping inOutWardMACMapping : entity.getInOutWardMACMapping()) {
                        inOutWardMACService.removeMappingWithPopANdServiceAreaInventory(inOutWardMACMapping.getId());
                    }
                    productOwnerService.updateProductOwnerForSerializedProductReject(entity.getQty(), entity.productId, Long.valueOf(entity.getStaffId()), CommonConstants.STAFF);
                } else {
                    productOwnerService.updateProductOwnerForNonTrackableAfterReject(entity.getQty(), entity.getProductId(), Long.valueOf(entity.getStaffId()), CommonConstants.STAFF);
                }

                entity.getInOutWardMACMapping().stream().forEach(r -> {
                    Inward inward = inwardRepository.findById(r.getInwardId()).get();
                    inward.setUnusedQty(inward.getUnusedQty() + 1);
                    inward.setUsedQty(inward.getUsedQty() - 1);
                    inwardRepository.save(inward);

                });
            }

            if (isApproveRequest) {
                entity.setApprovalStatus("Approve");
                //Add Network Device
                if ((dto.getProductCategory().isHasMac() || dto.getProductCategory().isHasSerial()) && dto.getProductCategory().getType().contains("NetworkBind")) {
                    if (entity.getApprovalStatus().equalsIgnoreCase("Approve")) {
                        createNetworkDevice(dto, entity);
                    }
                }
            }
            if (!isApproveRequest) {
                entity.setApprovalStatus("Rejected");

            }
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
        return super.saveEntity(entity);
    }


    private void createNetworkDevice(ProductDto dto, InventoryMappingDto entity) {
//        Entry in Network Device
        NetworkDeviceDTO networkDeviceDTO = new NetworkDeviceDTO();
        try {
            networkDeviceDTO.setProductId(dto.getId());
            networkDeviceDTO.setStatus(dto.getStatus());
            networkDeviceDTO.setMvnoId(dto.getMvnoId());
            networkDeviceDTO.setDevicetype(productCategoryService.getById(dto.getProductCategory().getId()).getName());
//            networkDeviceDTO.setLatitude(customers.getLatitude());
//            networkDeviceDTO.setLongitude(customers.getLongitude());
            networkDeviceDTO.setAvailableInPorts(dto.getAvailableInPorts());
            networkDeviceDTO.setTotalInPorts(dto.getTotalInPorts());
            networkDeviceDTO.setAvailableOutPorts(dto.getAvailableOutPorts());
            networkDeviceDTO.setTotalOutPorts(dto.getTotalOutPorts());
            networkDeviceDTO.setInwardId(entity.getInwardId());
            if (entity.getOwnerType().equalsIgnoreCase("Pop")) {
                PopManagement popManagement = popManagementRepository.findById(entity.getOwnerId()).get();
                networkDeviceDTO.setServiceAreaNameList((List<ServiceAreaDTO>) serviceAreaMapper.domainToDTO(popManagement.getServiceAreaNameList(), new CycleAvoidingMappingContext()));
                networkDeviceDTO.setServiceAreaIdsList(popManagementService.getEntityById(entity.getOwnerId(),entity.getMvnoId()).getServiceAreaIdsList());
            } else if (entity.getOwnerType().equalsIgnoreCase("Service Area")) {
                ServiceArea serviceArea = serviceAreaRepository.findById(entity.getOwnerId()).get();
                networkDeviceDTO.setServicearea((ServiceAreaDTO) serviceAreaMapper.domainToDTO(serviceArea, new CycleAvoidingMappingContext()));
                networkDeviceDTO.setServiceAreaIdsList(Collections.singletonList(serviceAreaService.getEntityById(entity.getOwnerId(),entity.getMvnoId()).getId()));
            }
            Product product = productRepository.getOne(dto.getId());
            networkDeviceDTO.setProductName(product.getName());

            networkDeviceDTO.setIsDeleted(dto.getIsDeleted());
            networkDeviceDTO.setInventorymappingId(entity.getId());
            networkDeviceDTO.setName(dto.getName() + " - " + LocalDateTime.now());
            InOutWardMACMapping inOutWardMACMapping = inOutWardMACService.findByInventoryMappingId(entity.getId()).get(0);
            List<InOutWardMACMapping> inventoryMappings = inOutWardMacRepo.findByInventoryMappingId(entity.getId());
            inventoryMappings.stream().forEach(inventoryMappings11 -> {
                Item item = itemRepository.findById(inventoryMappings11.getItemId()).get();
                networkDeviceDTO.setItemId(item.getId());
                try {
                    networkDeviceService.saveEntity(networkDeviceDTO);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
    }

    @Transactional
    public InOutWardMACMapping replaceInventory(Long oldMacMappingId, Long newMacMappingId) {
        try {
            InOutWardMACMapping oldInOutWardMACMapping = inOutWardMACService.getRepository().findById(oldMacMappingId).orElse(null);
            InOutWardMACMapping newInOutWardMACMapping = inOutWardMACService.getRepository().findById(newMacMappingId).orElse(null);
            if (oldInOutWardMACMapping != null) {
                InventoryMappingDto entity = super.getEntityById(oldInOutWardMACMapping.getInventoryMappingId(),oldInOutWardMACMapping.getMvnoId());
                StaffUser loggedInStaffUser = staffUserRepository.findById(getLoggedInUserId()).get();
                if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(getMvnoIdForWorkflow(entity.getOwnerId(), entity.getOwnerType()), null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, false, true, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()));
                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                        oldInOutWardMACMapping.setCurrentApproveId(Integer.valueOf(map.get("staffId")));
                        oldInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                        oldInOutWardMACMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        oldInOutWardMACMapping.setStatus("PENDING");
                        Long daysDiff = Duration.between(entity.assignedDateTime, LocalDateTime.now()).toDays();
                        // TODO: pass mvnoID manually 6/5/2025
                        if (Long.valueOf(clientServiceRepository.findValueByNameandMvnoId(Constants.INVENTORYCOUNTLIMIT, getMvnoIdFromCurrentStaff(null))) < daysDiff)
                            oldInOutWardMACMapping.setStatus("Refurbished");
                        newInOutWardMACMapping.setCurrentApproveId(Integer.valueOf(map.get("staffId")));
                        newInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                        newInOutWardMACMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        newInOutWardMACMapping.setStatus("New");
                        newInOutWardMACMapping.setCustInventoryMappingId(entity.getId());
                    } else {
                        oldInOutWardMACMapping.setCurrentApproveId(loggedInStaffUser.getId());
                        oldInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                        oldInOutWardMACMapping.setTeamHierarchyMappingId(null);
                        oldInOutWardMACMapping.setStatus("PENDING");
                        Long daysDiff = Duration.between(entity.assignedDateTime, LocalDateTime.now()).toDays();
                        // TODO: pass mvnoID manually 6/5/2025
                        if (Long.valueOf(clientServiceRepository.findValueByNameandMvnoId(Constants.INVENTORYCOUNTLIMIT, getMvnoIdFromCurrentStaff(null))) < daysDiff)
                            oldInOutWardMACMapping.setStatus("Refurbished");
                        oldInOutWardMACMapping.setUsedCount(Math.toIntExact(daysDiff));
                        newInOutWardMACMapping.setCurrentApproveId(loggedInStaffUser.getId());
                        newInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                        newInOutWardMACMapping.setTeamHierarchyMappingId(null);
                        newInOutWardMACMapping.setCustInventoryMappingId(entity.getId());
                        newInOutWardMACMapping.setStatus("New");
                    }
                } else {
                    oldInOutWardMACMapping.setCurrentApproveId(loggedInStaffUser.getId());
                    oldInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                    oldInOutWardMACMapping.setTeamHierarchyMappingId(null);
                    oldInOutWardMACMapping.setStatus("PENDING");
                    Long daysDiff = Duration.between(entity.assignedDateTime, LocalDateTime.now()).toDays();
                    // TODO: pass mvnoID manually 6/5/2025
                    if (Long.valueOf(clientServiceRepository.findValueByNameandMvnoId(Constants.INVENTORYCOUNTLIMIT, getMvnoIdFromCurrentStaff(null))) < daysDiff)
                        oldInOutWardMACMapping.setStatus("Refurbished");
                    oldInOutWardMACMapping.setUsedCount(Math.toIntExact(daysDiff));
                    newInOutWardMACMapping.setCurrentApproveId(loggedInStaffUser.getId());
                    newInOutWardMACMapping.setPreviousApproveId(loggedInStaffUser.getId());
                    newInOutWardMACMapping.setTeamHierarchyMappingId(null);
                    newInOutWardMACMapping.setCustInventoryMappingId(entity.getId());
                    newInOutWardMACMapping.setStatus("New");
                }
                inOutWardMACService.getRepository().save(oldInOutWardMACMapping);
                return inOutWardMACService.getRepository().save(newInOutWardMACMapping);
            } else {
                throw new RuntimeException("No mapping found.");
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage());

        }
        return null;
    }

    @Transactional
    public GenericDataDTO approveReplaceInventory(Long macMappingId, boolean billAble, boolean isApproveRequest) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        InOutWardMACMapping inOutWardMACMapping = inOutWardMACService.getRepository().findById(macMappingId).orElse(null);
        InventoryMapping entitydomain = inventoryMappingRepo.findById(inOutWardMACMapping.getCustInventoryMappingId()).get();
        InventoryMappingDto entity =inventoryMappingMapper.domainToDTO(entitydomain, new CycleAvoidingMappingContext());
//        ProductDto dto = productService.getEntityById(entity.getProductId());
        if (Objects.equals(getLoggedInUser().getUsername(), "admin") || Objects.equals(getLoggedInUser().getUsername(), "superadmin")) {
            entity.setNextApproverId(null);
            entity.setTeamHierarchyMappingId(null);
            entity.setApprovalStatus("Approve");
            genericDataDTO.setData(super.saveEntity(entity));
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            return genericDataDTO;
        }
        StaffUser loggedInUser = staffUserRepository.findById(getLoggedInUserId()).get();
        if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
            Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(getMvnoIdForWorkflow(entity.getOwnerId(), entity.getOwnerType()), null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, true, false, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()));
            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                inOutWardMACMapping.setCurrentApproveId(Integer.valueOf(map.get("staffId")));
                inOutWardMACMapping.setPreviousApproveId(loggedInUser.getId());
                inOutWardMACMapping.setStatus("PENDING");
                inOutWardMACMapping.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
            } else {
                inOutWardMACMapping.setCurrentApproveId(null);
                inOutWardMACMapping.setPreviousApproveId(loggedInUser.getId());
                inOutWardMACMapping.setStatus("ACTIVE");
                inOutWardMACMapping.setTeamHierarchyMappingId(null);
                /*if (!billAble) {
                    inOutWardMACMapping.setCustInventoryMappingId(null);
                    endMacMapppingService.deleteByMacAddress(inOutWardMACMapping.getMacAddress(), customers.getId());
                } else {
                    EndMacMappping custMacMappping = new EndMacMappping();
                    custMacMappping.setMacAddress(inOutWardMACMapping.getMacAddress());
                    endMacMapppingService.save(custMacMappping);
                }*/
            }
        } else {
            Map<String, Object> map = hierarchyService.getTeamForNextApprove(getMvnoIdForWorkflow(entity.getOwnerId(), entity.getOwnerType()), null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, true, false, getMapper().dtoToDomain(entity, new CycleAvoidingMappingContext()));
            if (map.containsKey("assignableStaff")) {
                genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            } else {
                inOutWardMACMapping.setCurrentApproveId(null);
                inOutWardMACMapping.setPreviousApproveId(loggedInUser.getId());
                inOutWardMACMapping.setStatus("ACTIVE");
                inOutWardMACMapping.setTeamHierarchyMappingId(null);
                /*if (!billAble) {
                    inOutWardMACMapping.setCustInventoryMappingId(null);
                    endMacMapppingService.deleteByMacAddress(inOutWardMACMapping.getMacAddress(), customers.getId());
                } else {
                    EndMacMappping custMacMappping = new EndMacMappping();
                    custMacMappping.setMacAddress(inOutWardMACMapping.getMacAddress());
                    endMacMapppingService.save(custMacMappping);
                }*/
            }
        }

        genericDataDTO.setData(inOutWardMACService.getRepository().save(inOutWardMACMapping));
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        return genericDataDTO;
    }

//    public String getStaffDetails(Long inventoryMappingId) {
//        StaffUser staffUser = staffUserService.get(getLoggedInUserId());
//        return staffUser.getUsername();
//    }

    private Integer getMvnoIdForWorkflow(Long ownerId, String ownerType) {
        Integer mvnoId = 0;
        try {
            PopManagementDTO popManagement = null;
            ServiceAreaDTO serviceArea = null;
            if (ownerType.equalsIgnoreCase(CommonConstants.POP)) {
//                popManagement = popManagementService.getEntityById(ownerId);
                PopManagement popManagement1 = popManagementRepository.findById(ownerId).get();
                popManagement = popManagementMapper.domainToDTO(popManagement1,new CycleAvoidingMappingContext());
                mvnoId = popManagement.getMvnoId();
            } else if (ownerType.equalsIgnoreCase(CommonConstants.SERVICE_AREA)) {
                serviceArea= serviceAreaMapper.domainToDTO(serviceAreaRepository.findById(ownerId).get(),new CycleAvoidingMappingContext());
//                serviceArea = serviceAreaService.getEntityById(ownerId);
                mvnoId = serviceArea.getMvnoId();
            }
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
        }
        return mvnoId;
    }

    @Transactional
    public InventoryMappingDto saveNonSerializedEntity(InventoryMappingDto entity,Integer mvnoId) throws Exception {
        try {
            if (entity.getQty() == null) {
                throw new Exception("Please Enter Assign Quantity");
            } else {
                InventoryMappingDto inventoryMappingDto = entity;
                ProductDto productDto = productService.getEntityById(inventoryMappingDto.getProductId(),mvnoId);
                Product product = productRepository.findById(productDto.getId()).get();
                boolean hasSerial = product.getProductCategory().isHasSerial();
                boolean isTrackable = product.getProductCategory().isHasTrackable();
                if (!hasSerial && !isTrackable) {
                    if (Objects.equals(getLoggedInUser().getUsername(), "admin") || Objects.equals(getLoggedInUser().getUsername(), "superadmin")) {
                        inventoryMappingDto.setNextApproverId(null);
                        inventoryMappingDto.setTeamHierarchyMappingId(null);
                        inventoryMappingDto.setApprovalStatus("Approve");

                    } else {
                        if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                            Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(getMvnoIdForWorkflow(inventoryMappingDto.getOwnerId(), inventoryMappingDto.getOwnerType()), null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, false, true, getMapper().dtoToDomain(inventoryMappingDto, new CycleAvoidingMappingContext()));
                            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                StaffUser staffUser = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).get();
                                inventoryMappingDto.setNextApproverId(Integer.valueOf(map.get("staffId")));
                                inventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                                inventoryMappingDto.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                inventoryMappingDto.setApprovalStatus("Pending");
                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(inventoryMappingDto.getId()), inventoryMappingDto.getProductName(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
                            } else {
                                inventoryMappingDto.setNextApproverId(getLoggedInUserId());
                                inventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                                inventoryMappingDto.setTeamHierarchyMappingId(null);
                                inventoryMappingDto.setApprovalStatus("Pending");
                            }
                        } else {
                            inventoryMappingDto.setNextApproverId(getLoggedInUserId());
                            inventoryMappingDto.setPreviousApproveId(getLoggedInUserId());
                            inventoryMappingDto.setTeamHierarchyMappingId(null);
                            inventoryMappingDto.setApprovalStatus("Pending");
                        }
                    }
                    saveEntityForNonTrackable(inventoryMappingDto);
                    productOwnerService.updateProductOwnerForNonTrackable(entity.getQty(), entity.productId, Long.valueOf(entity.staffId), CommonConstants.STAFF);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    public InventoryMappingDto saveEntityForNonTrackable(InventoryMappingDto entity) throws Exception {
        InventoryMappingDto inventoryMappingDto = null;
        try {
            //entity.setItemId(entity.getProductId());
            inventoryMappingDto = super.saveEntity(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return inventoryMappingDto;
    }

    public void validateMac(InventoryMappingDto inventoryMappingDto,Integer mvnoId) throws Exception {
        if (inventoryMappingDto.getInOutWardMACMapping().get(0).getMacAddress() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter mac in selected item", null);
        } else {
            validateMacInItem(inventoryMappingDto,mvnoId);
        }
    }

    public void validateMacInItem(InventoryMappingDto inventoryMappingDto, Integer mvnoId) throws Exception {

        ItemDto itemDto = itemService.getEntityForUpdateAndDelete(inventoryMappingDto.getInOutWardMACMapping().get(0).getItemId(),mvnoId);
        if (itemDto.getMacAddress() == null && inventoryMappingDto.getInOutWardMACMapping().get(0).getMacAddress() != null) {
            itemDto.setMacAddress(inventoryMappingDto.getInOutWardMACMapping().get(0).getMacAddress());
            itemRepository.save(itemMapper.dtoToDomain(itemDto, new CycleAvoidingMappingContext()));
        }
        if (itemDto.getMacAddress() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please update mac in selected item", null);
        }
    }

    public void validateSerialNumber(InventoryMappingDto inventoryMappingDto,Integer mvnoId) throws Exception {
        if (inventoryMappingDto.getInOutWardMACMapping().get(0).getSerialNumber() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please enter serial number in selected item", null);
        } else {
            validateSerialNumberInItem(inventoryMappingDto,mvnoId);
        }
    }

    public void validateSerialNumberInItem(InventoryMappingDto inventoryMappingDto, Integer mvnoId) throws Exception {
        ItemDto itemDto = itemService.getEntityForUpdateAndDelete(inventoryMappingDto.getInOutWardMACMapping().get(0).getItemId(),mvnoId);
        if (itemDto.getSerialNumber() == null) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Please update serial number in selected item", null);
        }
    }

    public GenericDataDTO getPopInventoryMappingByStaffId(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long staffId, boolean isGetSerializedItem) {
        String SUBMODULE = getModuleNameForLog() + " [getPopInventoryMappingByStaffId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
            PageRequest pageRequest;
            Page<InventoryMapping> inventoryMappingPage = null;
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            if (isGetSerializedItem) {
                BooleanExpression booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve")).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("pop"));
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != 1) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                }
                inventoryMappingPage = inventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (inventoryMappingPage.getSize() != 0) {
                    inventoryMappingPage.stream().forEach(inventoryMapping -> {
                        PopManagement popManagement = popManagementRepository.findById(inventoryMapping.getOwnerId()).get();
                        inventoryMapping.setPopName(popManagement.getPopName());
                    });
                }
                genericDataDTO.setDataList(inventoryMappingMapper.domainToDTO(inventoryMappingPage.getContent(), new CycleAvoidingMappingContext()));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(inventoryMappingPage.getTotalElements());
                genericDataDTO.setPageRecords(inventoryMappingPage.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(inventoryMappingPage.getNumber() + 1);
                genericDataDTO.setTotalPages(inventoryMappingPage.getTotalPages());
            }
            if (!isGetSerializedItem) {
                BooleanExpression booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve")).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("pop"));
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != 1) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                }
                inventoryMappingPage = inventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (inventoryMappingPage.getSize() != 0) {
                    inventoryMappingPage.stream().forEach(inventoryMapping -> {
                        PopManagement popManagement = popManagementRepository.findById(inventoryMapping.getOwnerId()).get();
                        inventoryMapping.setPopName(popManagement.getPopName());
                    });
                }
                genericDataDTO.setDataList(inventoryMappingMapper.domainToDTO(inventoryMappingPage.getContent(), new CycleAvoidingMappingContext()));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(inventoryMappingPage.getTotalElements());
                genericDataDTO.setPageRecords(inventoryMappingPage.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(inventoryMappingPage.getNumber() + 1);
                genericDataDTO.setTotalPages(inventoryMappingPage.getTotalPages());
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    public GenericDataDTO getServiceAreaInventoryMappingByStaffId(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, Long staffId, boolean isGetSerializedItem) {
        String SUBMODULE = getModuleNameForLog() + " [getServiceAreaInventoryMappingByStaffId()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            QInventoryMapping qInventoryMapping = QInventoryMapping.inventoryMapping;
            PageRequest pageRequest;
            Page<InventoryMapping> inventoryMappingPage = null;
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            if (isGetSerializedItem) {
                BooleanExpression booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve")).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isNotEmpty()).and(qInventoryMapping.ownerType.equalsIgnoreCase("Service Area"));
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != 1) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                }
                inventoryMappingPage = inventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (inventoryMappingPage.getSize() != 0) {
                    inventoryMappingPage.stream().forEach(inventoryMapping -> {
                        ServiceArea serviceArea = serviceAreaRepository.findById(inventoryMapping.getOwnerId()).get();
                        inventoryMapping.setServiceAreaName(serviceArea.getName());
                    });
                }
                genericDataDTO.setDataList(inventoryMappingMapper.domainToDTO(inventoryMappingPage.getContent(), new CycleAvoidingMappingContext()));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(inventoryMappingPage.getTotalElements());
                genericDataDTO.setPageRecords(inventoryMappingPage.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(inventoryMappingPage.getNumber() + 1);
                genericDataDTO.setTotalPages(inventoryMappingPage.getTotalPages());
            }
            if (!isGetSerializedItem) {
                BooleanExpression booleanExpression = qInventoryMapping.isNotNull().and(qInventoryMapping.staff.id.eq(Math.toIntExact(staffId))).and(qInventoryMapping.isDeleted.eq(false)).and(qInventoryMapping.approvalStatus.equalsIgnoreCase("Approve")).and(qInventoryMapping.qty.gt(0)).and(qInventoryMapping.inOutWardMACMapping.isEmpty()).and(qInventoryMapping.ownerType.containsIgnoreCase("Service Area"));
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) != 1) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qInventoryMapping.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
                }
                inventoryMappingPage = inventoryMappingRepo.findAll(booleanExpression, pageRequest);
                if (inventoryMappingPage.getSize() != 0) {
                    inventoryMappingPage.stream().forEach(inventoryMapping -> {
                        ServiceArea serviceArea = serviceAreaRepository.findById(inventoryMapping.getOwnerId()).get();
                        inventoryMapping.setServiceAreaName(serviceArea.getName());
                    });
                }
                genericDataDTO.setDataList(inventoryMappingMapper.domainToDTO(inventoryMappingPage.getContent(), new CycleAvoidingMappingContext()));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                genericDataDTO.setTotalRecords(inventoryMappingPage.getTotalElements());
                genericDataDTO.setPageRecords(inventoryMappingPage.getNumberOfElements());
                genericDataDTO.setCurrentPageNumber(inventoryMappingPage.getNumber() + 1);
                genericDataDTO.setTotalPages(inventoryMappingPage.getTotalPages());
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }

    public NMSIntegrationResDto sendNMSIntegration(String token, Integer customerId, String username, String password) {
        NMSIntegrationResDto nmsIntegrationResDto = new NMSIntegrationResDto();
        Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerId);
        if (customers == null) {
            nmsIntegrationResDto.setApiFlag(false);
            nmsIntegrationResDto.setApiMessage("Customer not found!");
            return nmsIntegrationResDto;
        }

        // Fetching CustomerServiceMapping List
        List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>(customerServiceMappingRepository
                .findAllByCustIdAndStatusIn(customerId, Arrays.asList("NewActivation", "ActivationPending")));

        if (customerServiceMappingList.isEmpty()) {
            nmsIntegrationResDto.setApiFlag(true);
            nmsIntegrationResDto.setApiMessage("No service mapping to process!");
            return nmsIntegrationResDto;
        }

        boolean flag = true; // Track API call status
        String apiStatus = "Success"; // Default status

        for (CustomerServiceMapping customerServiceMapping : customerServiceMappingList) {
            ClientService clientService = clientServiceRepository.getByNameAndMvnoId(
                    // TODO: pass mvnoID manually 6/5/2025
                    CommonConstants.FIBER_HOME_CONSTANTS.FIBER_HOME_MANUFACTURER, getMvnoIdFromCurrentStaff(null)
            );

            List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo
                    .findAllByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatus(
                            customerServiceMapping.getConnectionNo(), customerId, "ACTIVE"
                    );

            for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                log.info("Customer Inventory Mapping Id: " + customerInventoryMapping.getId());
                String vendorName = getVendorName(customerId, customerServiceMapping.getConnectionNo());
                if (vendorName == null || !vendorName.equalsIgnoreCase(clientService.getName())) {
                    continue; // Skip if vendor name does not match
                }

                if (customerInventoryMapping.getExternalItemId() != null ||
                        customerInventoryMapping.getPlanId() != null ||
                        customerInventoryMapping.getPlanGroupId() != null) {
                    continue; // Skip if externalItemId, planId, or planGroupId exists
                }

                // Fetching Serial Number
                String serialNumber = itemRepository.getSerialNumber(customerInventoryMapping.getItemId());
                if (serialNumber == null) {
                    flag = false;
                    continue; // Skip processing if serial number is missing
                }

                // Building Parameter List
                List<Object[]> paramList = custInvParamsRepo.findParamNameAndValueByCustInvId(customerInventoryMapping.getId());
                addCustomParam(paramList, NMSIntegrationConstants.NMS_INTEGRATION.SERIAL_NO, serialNumber);
                addCustomParam(paramList, NMSIntegrationConstants.NMS_INTEGRATION.ONU_ID, serialNumber);
                addCustomParam(paramList, NMSIntegrationConstants.NMS_INTEGRATION.PPPOEUSER, username);
                addCustomParam(paramList, NMSIntegrationConstants.NMS_INTEGRATION.PPPOEPASSWD, password);
                List<IntegrationSpecificParamDTO> integrationSpecificParamDTOS = new ArrayList<>();

                for (Object[] param : paramList) {
                    if (param[0] == null || param[1] == null) {
                        continue; // Skip null parameters
                    }

                    String paramName = (String) param[0];
                    String paramValue = (String) param[1];
                    IntegrationSpecificParamDTO integrationSpecificParamDTO = new IntegrationSpecificParamDTO();

                    switch (paramName.toUpperCase()) {
                        default:
                            integrationSpecificParamDTO.setParamName(paramName);
                            integrationSpecificParamDTO.setParamValue(paramValue);
                            break;
                    }

                    integrationSpecificParamDTOS.add(integrationSpecificParamDTO);
                }

                // Creating Integration DTO
                NMSIntegrationMessage dto = new NMSIntegrationMessage();
                dto.setList(integrationSpecificParamDTOS);
                dto.setOperation(NMSIntegrationConstants.NMS_INTEGRATION.ADD_ONU_OPERATION);
                dto.setCustomerId(customerId.longValue());
                dto.setItemId(customerInventoryMapping.getItemId());
                dto.setConfigName(NMSIntegrationConstants.NMS_INTEGRATION.CONFIGURATION_NAME);
                dto.setCustInvenId(customerInventoryMapping.getId());
                dto.setMvnoId(getMvnoIdFromCurrentStaff(null).longValue()); // TODO: pass mvnoID manually 6/5/2025
                dto.setLoggedInUserId(getLoggedInUserId());
                dto.setSerialNumber(serialNumber);

                // Calling API
                apiStatus = callNMSProvisionAPI(token, dto);
                log.info("NMS Integration API responded with status: " + apiStatus + " for Serial Number: " + serialNumber + " and Customer Name: " + username);
                if (!"Success".equalsIgnoreCase(apiStatus)) {
                    log.warn("NMS Integration API responded with status: " + apiStatus + " for Serial Number: " + serialNumber + " and Customer Name: " + username);
                    flag = false; // Mark failure if API response is not successful
                }
            }
        }
        log.info("************** Final returing flag status: " + flag);
        nmsIntegrationResDto.setApiFlag(flag);
        nmsIntegrationResDto.setApiMessage(apiStatus);
        return nmsIntegrationResDto;
    }

    public NMSIntegrationResDto sendNMSUpdateWANConfig(String token, Integer customerId, String username, String password) {
        NMSIntegrationResDto responseDto = new NMSIntegrationResDto();
        // Fetch Customer
        Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerId);
        if (customers == null) {
            responseDto.setApiFlag(false);
            responseDto.setApiMessage("Customer not found!");
            return responseDto;
        }

        // Fetching CustomerServiceMapping List
        List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>(customerServiceMappingRepository
                .findAllByCustIdAndStatusIn(customerId, Arrays.asList("Active")));

        if (customerServiceMappingList.isEmpty()) {
            responseDto.setApiFlag(true);
            responseDto.setApiMessage("No service mapping to process!");
            return responseDto;
        }

        boolean flag = true;
        String apiStatus = "Success"; // Default status

        for (CustomerServiceMapping customerServiceMapping : customerServiceMappingList) {
            ClientService clientService = clientServiceRepository.getByNameAndMvnoId(
                    CommonConstants.FIBER_HOME_CONSTANTS.FIBER_HOME_MANUFACTURER, getMvnoIdFromCurrentStaff(null));// TODO: pass mvnoID manually 6/5/2025

            List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo
                    .findAllByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatus(
                            customerServiceMapping.getConnectionNo(), customerId, "ACTIVE"
                    );

            for (CustomerInventoryMapping customerInventoryMapping : customerInventoryMappings) {
                log.info("Customer Inventory Mapping Id: " + customerInventoryMapping.getId());
                String vendorName = getVendorName(customerId, customerServiceMapping.getConnectionNo());
                if (vendorName == null || !vendorName.equalsIgnoreCase(clientService.getName())) {
                    continue; // Skip if vendor name does not match
                }

                if (customerInventoryMapping.getExternalItemId() != null ||
                        customerInventoryMapping.getPlanId() != null ||
                        customerInventoryMapping.getPlanGroupId() != null) {
                    continue; // Skip if externalItemId, planId, or planGroupId exists
                }

                // Fetching Serial Number
                String serialNumber = itemRepository.getSerialNumber(customerInventoryMapping.getItemId());
                if (serialNumber == null) {
                    flag = false;
                    continue; // Skip processing if serial number is missing
                }

                // Building Parameter List
                List<Object[]> paramList = custInvParamsRepo.findParamNameAndValueByCustInvId(customerInventoryMapping.getId());
                addCustomParam(paramList, NMSIntegrationConstants.NMS_INTEGRATION.SERIAL_NO, serialNumber);
                addCustomParam(paramList, NMSIntegrationConstants.NMS_INTEGRATION.ONU_ID, serialNumber);
                addCustomParam(paramList, NMSIntegrationConstants.NMS_INTEGRATION.PPPOEUSER, username);
                addCustomParam(paramList, NMSIntegrationConstants.NMS_INTEGRATION.PPPOEPASSWD, password);
                List<IntegrationSpecificParamDTO> integrationSpecificParamDTOS = new ArrayList<>();

                for (Object[] param : paramList) {
                    if (param[0] == null || param[1] == null) {
                        continue; // Skip null parameters
                    }

                    String paramName = (String) param[0];
                    String paramValue = (String) param[1];
                    IntegrationSpecificParamDTO integrationSpecificParamDTO = new IntegrationSpecificParamDTO();

                    switch (paramName.toUpperCase()) {
                        default:
                            integrationSpecificParamDTO.setParamName(paramName);
                            integrationSpecificParamDTO.setParamValue(paramValue);
                            break;
                    }

                    integrationSpecificParamDTOS.add(integrationSpecificParamDTO);
                }

                // Creating Integration DTO
                NMSIntegrationMessage dto = new NMSIntegrationMessage();
                dto.setList(integrationSpecificParamDTOS);
                dto.setOperation(NMSIntegrationConstants.NMS_INTEGRATION.WAN_CONFIG);
                dto.setCustomerId(customerId.longValue());
                dto.setItemId(customerInventoryMapping.getItemId());
                dto.setConfigName(NMSIntegrationConstants.NMS_INTEGRATION.CONFIGURATION_NAME);
                dto.setCustInvenId(customerInventoryMapping.getId());
                // TODO: pass mvnoID manually 6/5/2025
                dto.setMvnoId(getMvnoIdFromCurrentStaff(null).longValue());
                dto.setLoggedInUserId(getLoggedInUserId());
                dto.setSerialNumber(serialNumber);

                // Calling API
                apiStatus = callNMSUpdateWANConfigAPI(token, dto);
                log.info("NMS Integration API responded with status: " + apiStatus + " for Serial Number: " + serialNumber + " and Customer Name: " + username);
                if (!"Success".equalsIgnoreCase(apiStatus)) {
                    log.warn("NMS Integration API responded with status: " + apiStatus + " for Serial Number: " + serialNumber + " and Customer Name: " + username);
                    flag = false; // Mark failure if API response is not successful
                }
            }
        }
        log.info("Final API Call Status: {}", flag);
        responseDto.setApiFlag(flag);
        responseDto.setApiMessage(apiStatus);
        return responseDto;
    }

    private void addCustomParam(List<Object[]> paramList, String paramName, String paramValue) {
        paramList.add(new Object[]{paramName, paramValue});
    }

    public String getVendorName(Integer custId, String connectionNumber) {
        CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findByCustomer_IdAndConnectionNoAndAndIsDeletedFalse(custId, connectionNumber);
        if (customerInventoryMapping != null) {
            Vendor vendor = vendorRepo.findById(customerInventoryMapping.getVendorId()).orElse(null);
            if (vendor != null) {
                return vendor.getName();
            }
            return null;
        }
        return null;
    }

    public String callNMSProvisionAPI(String Token, NMSIntegrationMessage dto) {
        return integrationClient.generateNMSAPICALL(Token, dto);
    }

    public String callNMSUpdateWANConfigAPI(String Token, NMSIntegrationMessage dto) {
        return integrationClient.generateNMSUpdateWANConfig(Token, dto);
    }
}
