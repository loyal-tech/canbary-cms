package com.adopt.apigw.modules.InventoryManagement.InOutMACMapping;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.QCustomerServiceMapping;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.model.postpaid.QCustMacMappping;
import com.adopt.apigw.modules.Cas.Domain.CasMaster;
import com.adopt.apigw.modules.Cas.Domain.CasMasterRepository;
import com.adopt.apigw.modules.CommonList.utils.TypeConstants;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.*;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemMacSerialMapping.ExternalItemMacSerialMapping;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemMacSerialMapping.ExternalItemMacSerialMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain.ExternalItemManagement;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.repository.ExternalItemManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItem.*;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItemHierarchy.NonSerializedItemHierarchy;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItemHierarchy.NonSerializedItemHierarchyRepository;
import com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnModel.Return;
import com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnRepository.ReturnRepo;
import com.adopt.apigw.modules.InventoryManagement.generateremoveInventoryRequest.GenerateRemoveRequest;
import com.adopt.apigw.modules.InventoryManagement.generateremoveInventoryRequest.GenerateRemoveRequestRepo;
import com.adopt.apigw.modules.InventoryManagement.generateremoveInventoryRequest.QGenerateRemoveRequest;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.InventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.InventoryMappingRepo;
import com.adopt.apigw.modules.InventoryManagement.inventoryMapping.InventoryMappingService;
import com.adopt.apigw.modules.InventoryManagement.inward.*;
import com.adopt.apigw.modules.InventoryManagement.item.*;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionMappingServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionsMapping;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionsMappingDto;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionsMappingMapper;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMapping;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingDto;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingMapper;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.outward.Outward;
import com.adopt.apigw.modules.InventoryManagement.outward.OutwardDto;
import com.adopt.apigw.modules.InventoryManagement.outward.OutwardRepository;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.modules.InventoryManagement.product.ProductServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwner;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerRepository;
import com.adopt.apigw.modules.InventoryManagement.productOwner.ProductOwnerService;
import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices;
import com.adopt.apigw.modules.NetworkDevices.repository.NetworkDeviceRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.pojo.api.RecordPaymentPojo;
import com.adopt.apigw.pojo.api.StaffUserPojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustomerInventoryMappingMessage;
import com.adopt.apigw.rabbitMq.message.ItemMessage;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.CustMacMapppingRepository;
import com.adopt.apigw.repository.postpaid.DebitDocRepository;
import com.adopt.apigw.repository.postpaid.PlanServiceRepository;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.*;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.service.postpaid.CustMacMapppingService;
import com.adopt.apigw.service.postpaid.DebitDocService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.TatUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class InOutWardMACService extends ExBaseAbstractService<InOutWardMACMapingDTO, InOutWardMACMapping, Long> {
    @Autowired
    InOutWardMacRepo repository;

    @Autowired
    ExternalItemMacSerialMappingRepo externalItemMacSerialMappingRepo;
    @Autowired
    CustMacMapppingRepository custMacMapppingRepository;
    @Autowired
    private InwardRepository inwardRepository;
    @Autowired
    CustMacMapppingService custMacMapppingService;
    @Autowired
    CustomerInventoryMappingService customerInventoryMappingService;
    @Autowired
    ProductServiceImpl productService;
    @Autowired
    InOutWardMACService inOutWardMACService;
    @Autowired
    CreditDocService creditDocService;

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    ItemServiceImpl itemService;

    @Autowired
    InOutWardMacRepo inOutWardMacRepo;

    @Autowired
    ItemConditionMappingServiceImpl itemConditionMappingService;

    @Autowired
    ItemWarrantyMappingServiceImpl itemWarrantyMappingService;

    @Autowired
    ExternalItemManagementRepository externalItemManagementRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ReturnRepo returnRepo;

    @Autowired
    private InwardServiceImpl inwardService;


    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    InventoryMappingRepo inventoryMappingRepo;

    @Autowired
    InventoryMappingService inventoryMappingService;
    @Autowired
    NetworkDeviceRepository networkDeviceRepository;

    @Autowired
    OutwardRepository outwardRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NonSerializedItemServiceImpl nonSerializedItemService;
    @Autowired
    private NonSerializedItemRepository nonSerializedItemRepository;
    @Autowired
    private NonSerializedItemHierarchyRepository nonSerializedItemHierarchyRepository;
    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private DebitDocService debitDocService;
    @Autowired
    private ProductOwnerService productOwnerService;
    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private PlanServiceRepository planServiceRepository;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;
    @Autowired
    private CasMasterRepository casMasterRepository;

    @Autowired
    private CustomerInventoryMappingMapper customerInventoryMappingMapper;


    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    WorkflowAuditService workflowAuditService;
    @Autowired
    private TatUtils tatUtils;
    @Autowired
    HierarchyService hierarchyService;
    @Autowired
    InOutWardMacMapper mapper;

    @Autowired
    GenerateRemoveRequestRepo generateRemoveRequestRepo;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private ProductOwnerRepository productOwnerRepository;
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    public ItemConditionMappingRepository itemConditionMappingRepository;

    @Autowired
    public ItemWarrantyMappingRepository itemWarrantyMappingRepository;

    @Autowired
    InwardMapper inwardMapper;

    @Autowired
    private ItemConditionsMappingMapper itemConditionsMappingMapper;

    @Autowired
    private ItemWarrantyMappingMapper itemWarrantyMappingMapper;

    @Autowired
    private InOutWardMacMapper inOutWardMacMapper;

    public InOutWardMACService(InOutWardMacRepo repository, InOutWardMacMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[InOutWardMACService]";
    }

    List<InOutWardMACMapping> getByInwardId(Long inwardId) {
        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
        Inward inwardDetail = inwardRepository.findById(inwardId).get();
        Outward outward = inwardDetail.getOutwardId();
        if (inwardId != null) {
            if (outward != null) {
                Integer outwardId = Math.toIntExact(outward.getId());
                BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull().and(qInOutWardMACMapping.isDeleted.eq(false))
                        .and(qInOutWardMACMapping.inwardId.eq(inwardId));
                return IterableUtils.toList(repository.findAll(booleanExpression));
            } else {
                BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull().and(qInOutWardMACMapping.inwardId.eq(inwardId)).and(qInOutWardMACMapping.isDeleted.eq(false));
                return IterableUtils.toList(repository.findAll(booleanExpression));
            }
        } else {
            return null;
        }
    }

    public List<InOutWardMACMapping> getByBulkConsumptionId(Long bulkconsumptionId) {
        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
        //       Inward inwardDetail = inwardRepository.findById(inwardId).get();
        //      Outward outward = inwardDetail.getOutwardId();
        if (bulkconsumptionId != null) {
            //   Integer outwardId = Math.toIntExact(outward.getId());
            BooleanExpression booleanExpression = qInOutWardMACMapping.isNotNull().and(qInOutWardMACMapping.isDeleted.eq(false))
                    .and(qInOutWardMACMapping.bulkConsumptionId.eq(bulkconsumptionId));
            return IterableUtils.toList(repository.findAll(booleanExpression));
        } else {
            return null;
        }

    }


    List<InOutWardMACMapping> getAllMACMappingByInwardId(Long inwardId) {
        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
        return IterableUtils.toList(repository.findAllItemsByInwardIdAndItemStatus(inwardId));
    }


    List<InOutWardMACMapping> getAllMACByExisitingMacType(Long inwardId, Long inOutMappingId, String inventoryType) {
        List<InOutWardMACMapping> outWardMACMappingListbaseOnType = new ArrayList<>();
        try {
            Item item = itemRepository.findById(inOutWardMacRepo.findById(inOutMappingId).get().getItemId()).get();
            if (inventoryType.equalsIgnoreCase("Permanant Replacement")) {

                List<InOutWardMACMapping> inOutWardMACMappingList = repository.findAllItemsByInwardIdAndItemStatus(inwardId);
                inOutWardMACMappingList.stream().forEach(inOutWardMACMapping -> {
                    Item itemType = itemRepository.findById(inOutWardMACMapping.getItemId()).get();
                    if (itemType.getCondition().equalsIgnoreCase(item.getCondition())) {
                        outWardMACMappingListbaseOnType.add(inOutWardMACMapping);
                    }
                });
            }
            if (inventoryType.equalsIgnoreCase("Temporary Replacement")) {
                List<InOutWardMACMapping> inOutWardMACMappingList = repository.findAllItemsByInwardIdAndItemStatus(inwardId);
                inOutWardMACMappingList.stream().forEach(inOutWardMACMapping -> {
                    Item itemType = itemRepository.findById(inOutWardMACMapping.getItemId()).get();
                    if (itemType.getCondition().equalsIgnoreCase(CommonConstants.REFURBISHED)) {
                        outWardMACMappingListbaseOnType.add(inOutWardMACMapping);
                    }
                });
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return outWardMACMappingListbaseOnType;
    }

    List<InOutWardMACMapping> getAllMACMappingByExternalId(Long externalId) {
        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
        return IterableUtils.toList(repository.findAllItemsByExternalIdAndItemStatus(externalId));
    }

    @Transactional
    void deleteMacMapInCustomer(Integer customerId, String macAddress) {
        QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
        BooleanExpression booleanExpression = qCustMacMappping.isNotNull().and(qCustMacMappping.customer.id.eq(customerId)).and(qCustMacMappping.macAddress.eq(macAddress));
        CustMacMappping custMacMappping = custMacMapppingRepository.findOne(booleanExpression).orElse(null);
        if (Objects.nonNull(custMacMappping)) {
            custMacMapppingService.delete(custMacMappping.getId());
        }
    }

    @Transactional
    void deleteMac(Long itemId) {
        Item item = itemRepository.findById(itemId).get();
        if (!Objects.isNull(item)) {
            item.setIsDeleted(true);
            itemRepository.save(item);
        }
        InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findByItemId(itemId);
        if (!Objects.isNull(inOutWardMACMapping)) {
            inOutWardMACMapping.setIsDeleted(true);
            inOutWardMacRepo.save(inOutWardMACMapping);
        }
        Inward inward = inwardRepository.findById(inOutWardMACMapping.getInwardId()).get();
        if (!Objects.isNull(inward)) {
            inward.setTotalMacSerial(inward.getTotalMacSerial() - 1);
            inwardRepository.save(inward);
        }
    }

    @Transactional
    public InOutWardMACMapingDTO removeMappingWithCustomerInventory(Long mappingId) throws Exception {

        InOutWardMACMapingDTO outWardMACMapping = getMapper().domainToDTO(repository.findById(mappingId).get(), new CycleAvoidingMappingContext());
        if (Objects.nonNull(outWardMACMapping)) {
            CustomerInventoryMappingDto entity = customerInventoryMappingService.getEntityById(outWardMACMapping.getCustInventoryMappingId(),outWardMACMapping.getMvnoId());
            CustomersService customersService = SpringContext.getBean(CustomersService.class);
            Customers customers =  customersRepository.findById(entity.getCustomerId()).get();
            if (outWardMACMapping.getMacAddress() != null) {
                custMacMapppingService.deleteByMacAddress(outWardMACMapping.getMacAddress(), customers.getId());
            }
            outWardMACMapping.setCustInventoryMappingId(null);
            return super.saveEntity(outWardMACMapping);
        }

        return outWardMACMapping;
    }


    @Transactional
    public InOutWardMACMapping removeMappingWithPopANdServiceAreaInventory(Long mappingId) throws Exception {
        InOutWardMACMapping outWardMACMapping = inOutWardMacRepo.findById(mappingId).get();
        outWardMACMapping.setInventoryMappingId(null);
        inOutWardMacRepo.save(outWardMACMapping);
        return outWardMACMapping;
    }


//    //@Transactional
//    public GenericDataDTO removeInventory(Long mappingId, Long customerInventoryId, Long customerId, boolean isflag, String remark, boolean isApproveRequest) throws Exception {
//        GenericDataDTO genericDataDTO=new GenericDataDTO();
//        CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingService.getRepository().findById(customerInventoryId).orElse(null);
//        customerInventoryMapping.setApprovalRemark(remark);
//        PlanService planService=planServiceRepository.findById(customerServiceMappingRepository.findByConnectionNo(customerInventoryMapping.getConnectionNo()).getServiceId().intValue()).get();
//        if (planService != null) {
//            if (planService.getIs_dtv() == true) {
//                EzBillServiceUtility ezBillService = new EzBillServiceUtility();
//                Product product = productRepository.findById(customerInventoryMapping.getProduct().getId()).orElse(null);
//                CasMaster casMaster = casMasterRepository.findById(customerInventoryMapping.getProduct().getCaseId()).orElse(null);
//                Item item = itemRepository.findById(customerInventoryMapping.getItemId()).orElse(null);
//                if (product.getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
//                    if (casMaster != null && item != null) {
//                        ezBillService.replaceSetupBox(casMaster, null, item.getSerialNumber(), 4);
//                      //  ezBillService.getUnPairedInfoResponse(casMaster, item.getSerialNumber());
//
//                    }
//                }
//            }
//        }
//        CustomerInventoryMappingDto entity = customerInventoryMappingService.getEntityById(customerInventoryId);
//
//        DebitDocument debitDocument=debitDocRepository.findByInventoryMappingId(customerInventoryId);
//        if(debitDocument!=null){
//            debitDocService.voidInvoice(debitDocument.getId());
//        }
//        StaffUser loggedInUser = staffUserService.get(getLoggedInUserId());
//        Customers customers = null;
//        if (customerId != null)
//            customers = customersRepository.findById(Math.toIntExact(customerId)).get();
//        if (Objects.nonNull(customerInventoryMapping)) {
//            if (Objects.equals(loggedInUser.getUsername(), "admin") || Objects.equals(loggedInUser.getUsername(), "superadmin")) {
//                customerInventoryMapping.setNextApprover(null);
//                customerInventoryMapping.setPreviousApproveId(getLoggedInUserId());
//                customerInventoryMapping.setTeamHierarchyMapping(null);
//                customerInventoryMapping.setStatus("TERMINATED");
//                if(!isApproveRequest){
//                    updateInventoryMapping(customerInventoryMapping,mappingId,customerInventoryId,customerId,isflag,remark);
//                }else {
//                    customerInventoryMappingRepo.save(customerInventoryMapping);
//                }
//
//            }
//            if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
//                Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(getLoggedInUserId(), customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
//                StaffUser assignedUser = null;
//                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//                    StaffUser staffUser = staffUserService.get(Integer.valueOf(map.get("staffId")));
//                    assignedUser = staffUser;
//                    entity.setNextApproverId(Integer.valueOf(map.get("staffId")));
//                    entity.setPreviousApproveId(getLoggedInUserId());
//                    entity.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//                    entity.setStatus("PENDING");
//                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(entity.getId()), entity.getProductName(), staffUser.getId(), staffUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
//                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(entity.getId()), entity.getProductName(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
//                } else {
//                    entity.setNextApproverId(null);
//                    entity.setTeamHierarchyMappingId(null);
//                    entity.setPreviousApproveId(getLoggedInUserId());
//                    if (!isApproveRequest) {
//                        entity.setStatus("REMOVED");
//                        entity =  updateInventoryMapping(customerInventoryMapping,mappingId,customerInventoryId,customerId,isflag,remark);;
//                    }
////                    else {
////                        entity.setStatus("REJECTED");
// //                       entity =  updateInventoryMapping(customerInventoryMapping,mappingId,customerInventoryId,customerId,isflag,remark);
////                    }
//                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(entity.getId()), entity.getProductName(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
//                }
//                //TAT functionality
//                if (assignedUser != null) {
//                    if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//                        if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
//                            map.put("tat_id", map.get("current_tat_id"));
//                        tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, entity.getId().intValue(), null);
//                    }
//                }
//            } else {
//                Map<String, Object> map = hierarchyService.getTeamForNextApprove(getLoggedInUserId(), customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
//                if (map.containsKey("assignableStaff")) {
//                    genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
//                    genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                    entity.setTeamHierarchyMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
//
//                } else {
//                    entity.setNextApproverId(null);
//                    entity.setTeamHierarchyMappingId(null);
//                    if (!isApproveRequest) {
//                        entity.setStatus("REMOVED");
//                        entity =  updateInventoryMapping(customerInventoryMapping,mappingId,customerInventoryId,customerId,isflag,remark);
//                        entity.setPreviousApproveId(getLoggedInUserId());
//
//                    }
////                    else {
////                        entity.setStatus("REJECTED");
////                        entity =  updateInventoryMapping(customerInventoryMapping,mappingId,customerInventoryId,customerId,isflag,remark);
////                    }
//
//                }
//                customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
//            }
//
//        }
//        return genericDataDTO;
//    }

    public void removeInventory(Long mappingId, Long customerInventoryId, Long customerId, String remark) throws Exception {
        try {
            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingService.getRepository().findById(customerInventoryId).orElse(null);
            DebitDocument debitDocument = debitDocRepository.findByInventoryMappingId(customerInventoryId);

            ProductOwner productOwner=new ProductOwner();
            if(getLoggedInUser().getPartnerId() != 1) {
                productOwner  =productOwnerRepository.findByProductIdOwnerIdAndOwnerType(customerInventoryMapping.getProduct().getId(),customerInventoryMapping.getCreatedById().longValue(),"Partner");
            } else {
                productOwner  =productOwnerRepository.findByProductIdOwnerIdAndOwnerType(customerInventoryMapping.getProduct().getId(),customerInventoryMapping.getCreatedById().longValue(),"Staff");
            }
            Long quantity=null;
            Long unUsedQty=null;
            Long usedQty=null;
            if (productOwner != null) {
                quantity = productOwner.getQuantity();
                unUsedQty = productOwner.getUnusedQty();
                usedQty = productOwner.getUsedQty();
            }

                Optional<PlanService> planServiceOptional = planServiceRepository.findById(customerInventoryMapping.getServiceId().intValue());
                if (planServiceOptional.isPresent()) {
                    PlanService planService = planServiceOptional.get();
                    if (planService.getIs_dtv() == true) {
                        EzBillServiceUtility ezBillService = new EzBillServiceUtility();
                        Product product = productRepository.findById(customerInventoryMapping.getProduct().getId()).orElse(null);
                        CasMaster casMaster = casMasterRepository.findById(customerInventoryMapping.getProduct().getCaseId()).orElse(null);
                        Item item = itemRepository.findById(customerInventoryMapping.getItemId()).orElse(null);
                        if (product.getProductCategory().getDtvCategory().equalsIgnoreCase("STB")) {
                            if (casMaster != null && item != null) {
                                try {
                                    ezBillService.replaceSetupBox(casMaster, null, item.getSerialNumber(), 4, customerInventoryMapping.getConnectionNo(), customerInventoryMapping);
                                }catch (Exception e)
                                {
                                    if(!e.getMessage().contains("old STB doesn't exist or STB doesn't have customer or Old STB Already upgraded/surrender/defective."))
                                    {
                                            throw e;
                                    }
                                }
                            }
                        }
                    }
                }
            Customers customers = null;
            if (customerId != null)
                customers = customersRepository.findById(Math.toIntExact(customerId)).get();
            if (Objects.nonNull(customerInventoryMapping)) {

                customerInventoryMapping.setQty(customerInventoryMapping.getQty() - 1);
                removeMappingWithCustomerInventory(mappingId);
                Product product = productService.getRepository().getOne(customerInventoryMapping.getProduct().getId());
                //Creadit Note as Refund Amount for Remove Inventory
                if (Objects.nonNull(product)) {
                    QGenerateRemoveRequest qGenerateRemoveRequest = QGenerateRemoveRequest.generateRemoveRequest;
                    BooleanExpression booleanExpression = qGenerateRemoveRequest.customerid.eq(customerId).and(qGenerateRemoveRequest.customerinventoryId.eq(customerInventoryId)).and(qGenerateRemoveRequest.macmappingid.eq(mappingId)).and(qGenerateRemoveRequest.isDeleted.eq(false));
                    GenerateRemoveRequest generateRemoveRequests = generateRemoveRequestRepo.findOne(booleanExpression).orElse(null);
                    LocalDate date = LocalDate.now();
                    if(debitDocument != null) {
                        if(!customerInventoryMapping.getCreatedate().toLocalDate().equals(date)) {
                            if (generateRemoveRequests.getRevisedcharge() != null) {
                                RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
                                recordPaymentPojo.setCustomerid(customerInventoryMapping.getCustomer().getId());
                                recordPaymentPojo.setAmount(Double.valueOf(generateRemoveRequests.getRevisedcharge()));
                                List<Integer> invoiceIds = new ArrayList<>();
                                invoiceIds.add(0);
                                recordPaymentPojo.setInvoiceId(invoiceIds);
                                recordPaymentPojo.setPaymentdate(LocalDate.now());
                                recordPaymentPojo.setPaymode("Cash");
                                recordPaymentPojo.setPaytype("advance");
                                recordPaymentPojo.setType("creditnote");
                                recordPaymentPojo.setRemark("Refund amount for removing Product :-" + product.getName());
                                creditDocService.save(recordPaymentPojo, false, false, false,null);
                            }
                        }
                    }
                }

                if (customerInventoryMapping.getInwardId() != null) {
                    Inward inward = inwardRepository.findById(customerInventoryMapping.getInwardId()).get();
                    if (inward != null) {
                        inward.setUnusedQty(inward.getUnusedQty() + 1);
                        inward.setUsedQty(inward.getUsedQty() - 1);
                        inwardRepository.save(inward);
                    }
                }

                if (customerInventoryMapping.getExternalItemId() != null) {
                    ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(customerInventoryMapping.getExternalItemId()).get();
                    if (externalItemManagement != null) {
                        externalItemManagement.setUnusedQty(externalItemManagement.getUnusedQty() + 1);
                        externalItemManagement.setUsedQty(externalItemManagement.getUsedQty() - 1);
                        externalItemManagementRepository.save(externalItemManagement);
                    }
                    for (int i = 0; i < customerInventoryMapping.getExternalItemMacSerialMappings().size(); i++) {
                        ExternalItemMacSerialMapping externalItemMacSerialMapping = externalItemMacSerialMappingRepo.findById(customerInventoryMapping.getExternalItemMacSerialMappings().get(i).getId()).get();
                        if (externalItemMacSerialMapping != null) {
                            externalItemMacSerialMapping.setCustInventoryMappingId(null);
                            externalItemMacSerialMappingRepo.save(externalItemMacSerialMapping);
                        }
                    }
                }
                InOutWardMACMapping inOutWardMACMapping = repository.findById(mappingId).get();
                Item item = itemRepository.findById(inOutWardMACMapping.getItemId()).get();
             /*if(customers != null) {
                if (!customers.getIstrialplan() && item.getWarranty().equalsIgnoreCase("InWarranty")) {
                    item.setItemStatus(CommonConstants.RETURNED);
                    item.setWarranty("Paused");
                    item.setCondition(CommonConstants.DEFECTIVE);
                    item.setIntransiantWarrenty(null);
                    itemRepository.save(item);
                }*/
                if (!Objects.isNull(item)) {
                    if (item.getOwnershipType().equalsIgnoreCase("Subisu Owned")) {
                        itemService.updateItemStatusForCustomer(item.getId(), CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                        if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.getId(), "Paused");
                        }
                        NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndCustInventoryIdAndIsDeletedIsFalse(item.getId(), customerInventoryId);
//                        NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndIsDeletedFalse(item.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                        List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
                        ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
                        itemReturnDTO.setId(item.getId());
                        itemReturnDTOList.add(itemReturnDTO);
                        //  itemService.returnItemfromStaffremove(itemReturnDTOList);
                        itemService.removeAndreturnItemfromStaffremove(itemReturnDTOList, customerInventoryMapping);
                        if (getLoggedInUser().getPartnerId() != 1) {
                            GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findByCustomerinventoryIdAndIsDeletedFalse(customerInventoryId);
                            if (generateRemoveRequest != null) {
                                item.setOwnerType(CommonConstants.PARTNER);
                                StaffUser staffUser = staffUserRepository.findById(Math.toIntExact(generateRemoveRequest.getStaffid())).get();
                                item.setOwnerId(staffUser.getPartnerid().longValue());
//                                item.setOwnerId(generateRemoveRequest.getStaffid());
                            }
                        } else {
                            GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findByCustomerinventoryIdAndIsDeletedFalse(customerInventoryId);
                            item.setOwnerType(CommonConstants.STAFF);
                            item.setOwnerId(generateRemoveRequest.getStaffid());
                        }
                        itemRepository.save(item);
                    }
//                    if (item.getOwnershipType().equalsIgnoreCase("Sold")) {
//                        itemService.updateItemStatusForCustomer(item.getId(), CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
//                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(customerInventoryId);
//                        if (!Objects.isNull(networkDevices)) {
//                            networkDevices.setIsDeleted(true);
//                            networkDeviceRepository.save(networkDevices);
//                        }
//
//                    }
                    if (item.getOwnershipType().equalsIgnoreCase("Sold")) {
                        itemService.updateItemStatusForCustomer(item.getId(), CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(customerInventoryId);
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                        item.setOwnershipType("Subisu Owned");
                        if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.getId(), "Paused");
                        }
                        List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
                        ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
                        itemReturnDTO.setId(item.getId());
                        itemReturnDTOList.add(itemReturnDTO);
                        //   itemService.returnItemfromStaffremove(itemReturnDTOList);
                        itemService.removeAndreturnItemfromStaffremove(itemReturnDTOList, customerInventoryMapping);
                        if (getLoggedInUser().getPartnerId() != 1) {
                            GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findByCustomerinventoryIdAndIsDeletedFalse(customerInventoryId);
                            if (generateRemoveRequest != null) {
                                item.setOwnerType(CommonConstants.PARTNER);
                                StaffUser staffUser = staffUserRepository.findById(Math.toIntExact(generateRemoveRequest.getStaffid())).get();
                                item.setOwnerId(staffUser.getPartnerid().longValue());
//                                item.setOwnerId(generateRemoveRequest.getStaffid());
                            }
                        } else {
                            GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findByCustomerinventoryIdAndIsDeletedFalse(customerInventoryId);
                            item.setOwnerType(CommonConstants.STAFF);
                            item.setOwnerId(generateRemoveRequest.getStaffid());
                        }
                        itemRepository.save(item);

                    }

                    if (item.getOwnershipType().equalsIgnoreCase("Customer Owned") || item.getOwnershipType().equalsIgnoreCase("Temporary") || item.getOwnershipType().equalsIgnoreCase("Partner Owned")) {
                        itemService.updateItemStatusForCustomer(item.getId(), CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(customerInventoryId);
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                    }
                }
                //deleteCustomerInventory
                customerInventoryMapping.setIsDeleted(true);
                customerInventoryMapping.setApprovalRemark(remark);
                customerInventoryMappingRepo.save(customerInventoryMapping);
                List<Item> items = itemRepository.getall(inOutWardMACMapping.getItemId());
                Return aReturn = new Return();
                aReturn.setMac_name(items.get(0).getMacAddress());
                aReturn.setItem_status(items.get(0).getItemStatus());
                aReturn.setItem_condition(items.get(0).getCondition());
                aReturn.setProduct_id(items.get(0).getProductId());
                aReturn.setCurrent_inward_type(items.get(0).getCurrentInwardType());
                aReturn.setCurrent_inward_id(items.get(0).getCurrentInwardId());
                aReturn.setSerial_no(items.get(0).getSerialNumber());
                aReturn.setProduct_name(items.get(0).getName());
                aReturn.setCust_id(Long.parseLong(customerInventoryMapping.getCustomer().getId().toString()));
                returnRepo.save(aReturn);

            }
            if (productOwner != null) {
                //updateProductOwner Table
                productOwner.setQuantity(quantity);
                productOwner.setUsedQty(usedQty - 1);
                productOwner.setUnusedQty(unUsedQty + 1);
                ProductOwner owner = productOwnerRepository.save(productOwner);
            }
        } catch (Exception ex) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage(), null);
        }
    }

    public void removeInventoryfrompop(Long macMacmappingId, boolean isflag) {
        try {
            InventoryMapping inventoryMapping = inventoryMappingRepo.findById(inOutWardMacRepo.findById(macMacmappingId).get().getInventoryMappingId()).get();
            Optional<Item> item = itemRepository.findById(inOutWardMacRepo.findById(macMacmappingId).get().getItemId());
            ProductOwner productOwner=new ProductOwner();
            if(getLoggedInUser().getPartnerId() != 1) {
                productOwner  =productOwnerRepository.findByProductIdOwnerIdAndOwnerType(inventoryMapping.getProduct().getId(), Long.valueOf(getLoggedInUser().getPartnerId()),"Staff");
            } else {
                productOwner  =productOwnerRepository.findByProductIdOwnerIdAndOwnerType(inventoryMapping.getProduct().getId(), Long.valueOf(getLoggedInUser().getUserId()),"Staff");
            }
            Long quantity = productOwner.getQuantity();
            Long unUsedQty=productOwner.getUnusedQty();
            Long usedQty=productOwner.getUsedQty();
            if (inventoryMapping.getQty() - 1 == 0) {
                InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(macMacmappingId).get();
                inOutWardMACMapping.setInventoryMappingId(null);
                inOutWardMacRepo.save(inOutWardMACMapping);

                if (!Objects.isNull(item)) {

                    if (item.get().getOwnershipType().equalsIgnoreCase("Subisu Owned")) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get().getId(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }

                        NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndIsDeletedFalse(item.get().getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                        List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
                        ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
                        itemReturnDTO.setId(item.get().getId());
                        itemReturnDTOList.add(itemReturnDTO);
                        itemService.returnItemfromStaffremove(itemReturnDTOList);
                        item.get().setOwnerType(CommonConstants.STAFF);
                        item.get().setOwnerId(Long.valueOf(getLoggedInUser().getUserId()));
                        itemRepository.save(item.get());
                    }

                    if (item.get().getOwnershipType().equalsIgnoreCase("Sold") && isflag) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get().getId(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }
                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(inventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                    }

                    if (item.get().getOwnershipType().equalsIgnoreCase("Sold") && isflag == true) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get().getId(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }
                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(inventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                        item.get().setOwnershipType("Subisu Owned");
                        if (item.get().getWarranty().equalsIgnoreCase("InWarrenty")) {
                            itemService.updateItemWarranty(item.get().getId(), "Paused");
                        }

                        List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
                        ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
                        itemReturnDTO.setId(item.get().getId());
                        itemReturnDTOList.add(itemReturnDTO);
                        itemService.returnItemfromStaffremove(itemReturnDTOList);
                        item.get().setOwnerType(CommonConstants.STAFF);
                        item.get().setOwnerId(Long.valueOf(getLoggedInUser().getUserId()));
                        itemRepository.save(item.get());
                    }

                    if (item.get().getOwnershipType().equalsIgnoreCase("Customer Owned") || item.get().getOwnershipType().equalsIgnoreCase("Temporary") || item.get().getOwnershipType().equalsIgnoreCase("Partner Owned")) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get().getId(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }

                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(inventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                    }
                }

                inventoryMapping.setQty(0L);
                inventoryMapping.setIsDeleted(true);
                itemRepository.save(item.get());
                inventoryMappingRepo.save(inventoryMapping);
                item.get().setRemoveFrom("Pop");
                ItemMessage message = new ItemMessage(item.get(),"Item Remove From Pop and Service Area");
                //messageSender.send(message,RabbitMqConstants.QUEUE_APIGW_APPROVE_REMOVE_INVENTORY_SERIALIZEDITEM_REQUEST_IN_INTEGRATION);
            } else {
               // Optional<Item> item = itemRepository.findById(inOutWardMacRepo.findById(macMacmappingId).get().getItemId());
                InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findById(macMacmappingId).get();
                inOutWardMACMapping.setInventoryMappingId(null);
                inOutWardMacRepo.save(inOutWardMACMapping);
                if (!Objects.isNull(item)) {
                    if (item.get().getOwnershipType().equalsIgnoreCase("Subisu Owned")) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get().getId(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }

                        NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndIsDeletedFalse(item.get().getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                        List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
                        ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
                        itemReturnDTO.setId(item.get().getId());
                        itemReturnDTOList.add(itemReturnDTO);
                        itemService.returnItemfromStaffremove(itemReturnDTOList);
                        item.get().setOwnerType(CommonConstants.STAFF);
                        item.get().setOwnerId(Long.valueOf(getLoggedInUser().getUserId()));
                        itemRepository.save(item.get());
                    }

                    if (item.get().getOwnershipType().equalsIgnoreCase("Sold") && isflag) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get().getId(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }
                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(inventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                    }

                    if (item.get().getOwnershipType().equalsIgnoreCase("Sold") && isflag == true) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get().getId(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }
                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(inventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                        item.get().setOwnershipType("Subisu Owned");
                        if (item.get().getWarranty().equalsIgnoreCase("InWarrenty")) {
                            itemService.updateItemWarranty(item.get().getId(), "Paused");
                        }

                        List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
                        ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
                        itemReturnDTO.setId(item.get().getId());
                        itemReturnDTOList.add(itemReturnDTO);
                        itemService.returnItemfromStaffremove(itemReturnDTOList);
                        item.get().setOwnerType(CommonConstants.STAFF);
                        item.get().setOwnerId(Long.valueOf(getLoggedInUser().getUserId()));
                        itemRepository.save(item.get());
                    }

                    if (item.get().getOwnershipType().equalsIgnoreCase("Customer Owned") || item.get().getOwnershipType().equalsIgnoreCase("Temporary") || item.get().getOwnershipType().equalsIgnoreCase("Partner Owned")) {
                        if (item.get().getWarranty().equalsIgnoreCase("InWarranty")) {
                            itemService.updateItemWarranty(item.get().getId(), "Paused");
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Pop")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, null, inventoryMapping.getOwnerId(), CommonConstants.REMOVE_INVETORIES);
                        }
                        if (inventoryMapping.getOwnerType().equalsIgnoreCase("Service Area")) {
                            itemService.updateItemStatusForServiceAreaAndPop(item.get().getId(), CommonConstants.UNALLOCATED, null, inventoryMapping.getOwnerId(), null, CommonConstants.REMOVE_INVETORIES);
                        }

                        NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(inventoryMapping.getId());
                        if (!Objects.isNull(networkDevices)) {
                            networkDevices.setIsDeleted(true);
                            networkDeviceRepository.save(networkDevices);
                        }
                    }
                }

                inventoryMapping.setQty(inventoryMapping.getQty() - 1);
                itemRepository.save(item.get());
                inventoryMappingRepo.save(inventoryMapping);

            }
            //updateProductOwner Table
            productOwner.setQuantity(quantity);
            productOwner.setUsedQty(usedQty-1);
            productOwner.setUnusedQty(unUsedQty+1);
            ProductOwner owner=productOwnerRepository.save(productOwner);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<CustomerInventoryMappingDto> getAllAssemblyInventory(Long assemblyId) {
        try {
            List<CustomerInventoryMapping> customerInventoryMappings = customerInventoryMappingRepo.findAllByItemAssemblyId(assemblyId);
            if (customerInventoryMappings.size() != 0) {

                List<CustomerInventoryMappingDto> customerInventoryMappingDtoList = customerInventoryMappingMapper.domainToDTO(customerInventoryMappings, new CycleAvoidingMappingContext());
                customerInventoryMappingDtoList.stream().forEach(r -> {
                    Product product = productRepository.findById(r.getProductId()).orElse(null);
                    if (product != null) {
                        r.setDtvCategory(product.getProductCategory().getDtvCategory());
                    }
                });
                return customerInventoryMappingDtoList;
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
        return null;
    }

    @Override
    public InOutWardMACMapingDTO saveEntity(InOutWardMACMapingDTO entity) throws Exception {
        try {
            boolean flag = true;
            if (entity.getMacAddress() != null) {
                flag = inOutWardMACService.duplicateVerifyAtSave(entity.getMacAddress());
            }
            // TODO: pass mvnoID manually 6/5/2025
            entity.setMvnoId(getMvnoIdFromCurrentStaff(null));
            if (flag) {
                Inward inward = inwardRepository.findById(entity.getInwardId()).get();
                InwardServiceImpl inwardService = SpringContext.getBean(InwardServiceImpl.class);
                InwardDto inwardDto = inwardMapper.domainToDTO(inwardRepository.findById(entity.getInwardId()).orElse(null),new CycleAvoidingMappingContext());
                inwardDto.setTotalMacSerial(inward.getTotalMacSerial() + 1);
                inwardService.updateEntity(inwardDto);
                ItemDto item = new ItemDto();
                item.setMacAddress(entity.getMacAddress());
                item.setSerialNumber(entity.getSerialNumber());
                item.setName(inward.getProductId().getName());
                item.setCondition(inward.getType());
                item.setMvnoId(inward.getMvnoId());

                item.setOwnerId(inward.getDestinationId());
                item.setOwnerType(inward.getDestinationType());
                item.setCurrentInwardType(TypeConstants.FORWARDED);
                item.setCurrentInwardId(inward.getId());
                item.setProductId(inward.getProductId().getId());
                item.setOwnershipType("Subisu Owned");

                item.setItemStatus(CommonConstants.UNALLOCATED);

                Integer wrty = inward.getProductId().getExpiryTime();
                if (inward.getProductId().getExpiryTimeUnit().equalsIgnoreCase("Month")) {
                    wrty = 30 * wrty;
                    item.setWarrantyPeriod(wrty);
                } else {
                    item.setWarrantyPeriod(wrty);
                }

                item.setWarranty("NotStarted");

                ItemDto item1 = itemService.saveEntity(item);

                ItemConditionsMappingDto itemConditionsMappingDto = new ItemConditionsMappingDto();
                itemConditionsMappingDto.setItemId(item1.getId());
                itemConditionsMappingDto.setCondition(inward.getType());
                itemConditionsMappingDto.setMvnoId(inward.getMvnoId());

                itemConditionMappingService.saveEntity(itemConditionsMappingDto);

                ItemWarrantyMappingDto itemWarrantyMappingDto = new ItemWarrantyMappingDto();
                itemWarrantyMappingDto.setItemId(item1.getId());
                itemWarrantyMappingDto.setWarranty(item1.getWarranty());
                itemWarrantyMappingDto.setMvnoId(inward.getMvnoId());

                itemWarrantyMappingService.saveEntity(itemWarrantyMappingDto);
                entity.setItemId(item1.getId());
                return super.saveEntity(entity);
            } else {

                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Mac Address Already Exists, It Should Be Unique", null);
            }
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
    }



    public void saveItem(InwardDto entity) throws Exception {

        boolean flag = true;

//        if (inOutWardMacRepo.getOne(entity.getId()).getMacAddress() != null) {
//            flag = inOutWardMACService.duplicateVerifyAtSave(inOutWardMacRepo.getOne(entity.getId()).getMacAddress());
//        }
        // TODO: pass mvnoID manually 6/5/2025
//        entity.setMvnoId(getMvnoIdFromCurrentStaff(null));
        if (flag) {
            Inward inward = inwardRepository.findById(entity.getId()).get();
            InwardServiceImpl inwardService = SpringContext.getBean(InwardServiceImpl.class);
            InwardDto inwardDto = inwardService.getEntityForUpdateAndDelete(entity.getId(),entity.getMvnoId());
            //inwardDto.setTotalMacSerial(inward.getTotalMacSerial() + 1);
            inwardService.updateEntity(inwardDto);

            Integer wrty = inward.getProductId().getExpiryTime();
            if (inward.getProductId().getExpiryTimeUnit().equalsIgnoreCase("Month")) {
                wrty = 30 * wrty;

            }


        }

    }

    @Override
    public boolean duplicateVerifyAtSave(String mac) throws Exception {
        boolean flag = false;
        // TODO: pass mvnoID manually 6/5/2025
        List<Integer> mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(null), 1);
        if (!Objects.equals(mac, null)) {
            mac = mac.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = repository.duplicateVerifyAtSave(mac);
            else count = repository.duplicateVerifyAtSave(mac, mvnoIds);
            if (count == 0) {
                flag = true;
            } else {
                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Entered a MAC " + mac + " is Already Exist", null);
            }
        }
        return flag;
    }

    public List<InOutWardMACMapping> findByCustInventoryMappingId(Long id) {
        return repository.findByCustInventoryMappingId(id);
    }

    public List<InOutWardMACMapping> findByInventoryMappingId(Long id) {
        return repository.findByInventoryMappingId(id);
    }

    public List<InOutWardMACMapping> findbyinwardid(Long id) {
        return repository.findbyinwardid(id);
    }

    public List<InOutWardMACMapping> findbyinwardOfOutwardId(Long inwardOfOutwardId) {
        QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
        BooleanExpression booleanExpression = qInOutWardMACMapping.inwardIdOfOutward.eq(inwardOfOutwardId).and(qInOutWardMACMapping.isDeleted.eq(false)).and(qInOutWardMACMapping.isForwarded.eq(1));
        List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(booleanExpression));
        return inOutWardMACMappingList;
    }

    public List<InOutWardMACMapping> findbyoutwardid(Long id) {
        return repository.findbyoutwardid(id);
    }

    public List<InOutWardMACMapping> delete(Integer id) throws Exception {
        List<InOutWardMACMapping> list = inOutWardMacRepo.deleteVerify(id);
        return list;
    }

    @Transactional
    public void saveNonSerializedItemsAfterApprovalInward(InwardDto entity, String uom) throws Exception {
        // TODO: pass mvnoID manually 6/5/2025
//        entity.setMvnoId(getMvnoIdFromCurrentStaff(null));
        InwardDto inwardDto = inwardService.getEntityForUpdateAndDelete(entity.getId(),entity.getMvnoId());
        inwardService.updateEntity(inwardDto);
        Product pcId = productRepository.findById(inwardDto.getProductId().getId()).get();
        NonSerializedItemDto nonSerializedItemDto = new NonSerializedItemDto();
        nonSerializedItemDto.setName(nonSerializedItemService.getRandomenumber("NSI", "-", ""));
        nonSerializedItemDto.setProductId(pcId.getId());
        nonSerializedItemDto.setNonSerializedItemcondition(inwardDto.getType());
        nonSerializedItemDto.setCurrentInwardId(inwardDto.getId());
        nonSerializedItemDto.setMvnoId(inwardDto.getMvnoId());
        nonSerializedItemDto.setOwnerId(inwardDto.getDestinationId());
        nonSerializedItemDto.setOwnerType(inwardDto.getDestinationType());
        nonSerializedItemDto.setCurrentInwardType(TypeConstants.FORWARDED);
        nonSerializedItemDto.setOwnershipType("Subisu Owned");
        nonSerializedItemDto.setItemStatus(CommonConstants.UNALLOCATED);
        nonSerializedItemDto.setWarranty("NotStarted");
        if (uom.equalsIgnoreCase("meter")) {
            nonSerializedItemDto.setQty(inwardDto.getInTransitQty());
        } else if (uom.equalsIgnoreCase("kilometer")) {
            nonSerializedItemDto.setQty(1000 * inwardDto.getInTransitQty());
        }
        Integer wrty = pcId.getExpiryTime();
        if (pcId.getExpiryTimeUnit().equalsIgnoreCase("Month")) {
            wrty = 30 * wrty;
            nonSerializedItemDto.setWarrantyPeriod(wrty);
        } else {
            nonSerializedItemDto.setWarrantyPeriod(wrty);
        }
        NonSerializedItemDto dto = nonSerializedItemService.saveEntity(nonSerializedItemDto);
        saveAutoInOutwardMacMappingForNSI(dto, inwardDto);
    }

    public void saveAutoMAC(Inward entity) throws Exception {
        try {
            Integer remainingTotalMacQty = Math.toIntExact(entity.getInTransitQty() - entity.getTotalMacSerial());
            ConcurrentLinkedQueue<Item> items = new ConcurrentLinkedQueue<>();
            // TODO: pass mvnoID manually 6/5/2025
            entity.setMvnoId(getMvnoIdFromCurrentStaff(null));
            Inward inward = inwardRepository.findById(entity.getId()).get();
            InwardServiceImpl inwardService = SpringContext.getBean(InwardServiceImpl.class);
            Inward inwardDto = inwardRepository.findById(entity.getId()).get();
            InwardDto inwardDto1 = inwardMapper.domainToDTO(inwardDto , new CycleAvoidingMappingContext());

            for (int i = 0; i < remainingTotalMacQty; i++) {
                inwardDto1.setTotalMacSerial(inward.getTotalMacSerial() + 1);
                try {
                    inwardService.updateEntity(inwardDto1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            IntStream.range(0, remainingTotalMacQty)
                    .parallel()
                    .forEach(i -> {
                        try {

                            Integer wrty = inward.getProductId().getExpiryTime();
                            if (inward.getProductId().getExpiryTimeUnit().equalsIgnoreCase("Month")) {
                                wrty = 30 * wrty;
                            }
                            Item item = new Item();
                            item.setMacAddress(null);
                            item.setSerialNumber(null);
                            item.setName(itemService.getRandomenumber("SI", "-", ""));
                            item.setCondition(inward.getType());
                            item.setMvnoId(inward.getMvnoId());
                            item.setOwnerId(inward.getDestinationId());
                            item.setOwnerType(inward.getDestinationType());
                            item.setCurrentInwardType(TypeConstants.FORWARDED);
                            item.setCurrentInwardId(inward.getId());
                            item.setProductId(inward.getProductId().getId());
                            item.setOwnershipType("Subisu Owned");
                            item.setItemStatus(CommonConstants.UNALLOCATED);
                            item.setWarrantyPeriod(wrty);
                            item.setWarranty("NotStarted");
                            items.add(item);

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            itemRepository.saveAll(items);
            System.out.println("item saved");
            List<ItemConditionsMapping> conditionsMappings = new ArrayList<>();
            List<ItemWarrantyMapping> warrantyMappings = new ArrayList<>();
            List<InOutWardMACMapping> macMappings = new ArrayList<>();

            for (Item item : items) {
                ItemConditionsMapping conditionsMapping = saveAutoItemConditionsMapping(item, entity);
                ItemWarrantyMapping warrantyMapping = saveAutoItemWarrantyMapping(item, entity);
                InOutWardMACMapping macMapping = saveAutoInOutwardMacMapping(item, entity);
                conditionsMappings.add(conditionsMapping);
                warrantyMappings.add(warrantyMapping);
                macMappings.add(macMapping);
            }

            System.out.println("listing done");
            int batchSize = 1000; // Set your desired batch size

            List<InOutWardMACMapping> inOutWardBatch = new ArrayList<>();
            for (int i = 0; i < macMappings.size(); i++) {
                inOutWardBatch.add(macMappings.get(i));
                if (inOutWardBatch.size() % batchSize == 0 || i == macMappings.size() - 1) {
                    inOutWardMacRepo.saveAll(inOutWardBatch);
                    inOutWardBatch.clear();
                }
            }inOutWardMacRepo.flush();
            List<ItemWarrantyMapping> itemWarrantyBatch = new ArrayList<>();
            for (int i = 0; i < warrantyMappings.size(); i++) {
                itemWarrantyBatch.add(warrantyMappings.get(i));
                if (itemWarrantyBatch.size() % batchSize == 0 || i == warrantyMappings.size() - 1) {
                    itemWarrantyMappingRepository.saveAll(itemWarrantyBatch);
                    itemWarrantyBatch.clear();
                }
            }itemWarrantyMappingRepository.flush();
            List<ItemConditionsMapping> itemConditionsBatch = new ArrayList<>();
            for (int i = 0; i < conditionsMappings.size(); i++) {
                itemConditionsBatch.add(conditionsMappings.get(i));
                if (itemConditionsBatch.size() % batchSize == 0 || i == conditionsMappings.size() - 1) {
                    itemConditionMappingRepository.saveAll(itemConditionsBatch);
                    itemConditionsBatch.clear();
                }
            }itemConditionMappingRepository.flush();
            System.out.println("saving done");
//            itemConditionMappingRepository.saveAll(conditionsMappings);
//            itemWarrantyMappingRepository.saveAll(warrantyMappings);
//            inOutWardMacRepo.saveAll(macMappings);
            // System.out.println("saving done");
            // savedatas(items,conditionsMappings, warrantyMappings, macMappings,remainingTotalMacQty);
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
    }

//    public Inward saveAutoItemsAfterApprovalInward(Inward entity) throws Exception {
//        try {
//            entity.setMvnoId(getMvnoIdFromCurrentStaff());
//            Inward inward = inwardRepository.findById(entity.getId()).get();
//            InwardServiceImpl inwardService = SpringContext.getBean(InwardServiceImpl.class);
//            InwardDto inwardDto = inwardService.getEntityForUpdateAndDelete(entity.getId());
//            inwardDto.setTotalMacSerial(inward.getTotalMacSerial() + 1);
//            inwardService.updateEntity(inwardDto);
//            ItemDto item1 = saveAutoItem(entity);
//            saveAutoItemConditionsMapping(item1, entity);
//            saveAutoItemWarrantyMapping(item1, entity);
//            saveAutoInOutwardMacMapping(item1, entity);
//        } catch (Exception e) {
//            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
//        }
//        return entity;
//    }

//    public ItemDto saveAutoItem(Inward inward) {
//        try {
//            ItemDto item = new ItemDto();
//            item.setMacAddress(null);
//            item.setSerialNumber(null);
//            item.setName(itemService.getRandomenumber("SI", "-", ""));
//            item.setCondition(inward.getType());
//            item.setMvnoId(inward.getMvnoId());
//
//            item.setOwnerId(inward.getDestinationId());
//            item.setOwnerType(inward.getDestinationType());
//            item.setCurrentInwardType(TypeConstants.FORWARDED);
//            item.setCurrentInwardId(inward.getId());
//            item.setProductId(inward.getProductId().getId());
//            item.setOwnershipType("Subisu Owned");
//
//            item.setItemStatus(CommonConstants.UNALLOCATED);
//
//            Integer wrty = inward.getProductId().getExpiryTime();
//            if (inward.getProductId().getExpiryTimeUnit().equalsIgnoreCase("Month")) {
//                wrty = 30 * wrty;
//                item.setWarrantyPeriod(wrty);
//            } else {
//                item.setWarrantyPeriod(wrty);
//            }
//            item.setWarranty("NotStarted");
//            ItemDto item1 = itemService.saveEntity(item);
//            return item1;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public ItemConditionsMapping saveAutoItemConditionsMapping(Item item1, Inward inward) {
        try {
            ItemConditionsMapping itemConditionsMappingDto = new ItemConditionsMapping();
            itemConditionsMappingDto.setItemId(item1.getId());
            itemConditionsMappingDto.setCondition(inward.getType());
            itemConditionsMappingDto.setMvnoId(inward.getMvnoId());
            // itemConditionMappingService.saveEntity(itemConditionsMappingDto);
            return itemConditionsMappingDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ItemWarrantyMapping saveAutoItemWarrantyMapping(Item item1, Inward inward) {
        try {
            ItemWarrantyMapping itemWarrantyMappingDto = new ItemWarrantyMapping();
            itemWarrantyMappingDto.setItemId(item1.getId());
            itemWarrantyMappingDto.setWarranty(item1.getWarranty());
            itemWarrantyMappingDto.setMvnoId(inward.getMvnoId());
            // itemWarrantyMappingService.saveEntity(itemWarrantyMappingDto);
            return itemWarrantyMappingDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InOutWardMACMapping saveAutoInOutwardMacMapping(Item item1, Inward inward) {
        try {
            InOutWardMACMapping inOutWardMACMapingDTO = new InOutWardMACMapping();
            inOutWardMACMapingDTO.setInwardId(inward.getId());
            inOutWardMACMapingDTO.setOutwardId(null);
            inOutWardMACMapingDTO.setCustInventoryMappingId(null);
            inOutWardMACMapingDTO.setMacAddress(null);
            // TODO: pass mvnoID manually 6/5/2025
            inOutWardMACMapingDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
            inOutWardMACMapingDTO.setStatus(CommonConstants.ACTIVE_STATUS);
            inOutWardMACMapingDTO.setIsForwarded(0);
            inOutWardMACMapingDTO.setIsReturned(0);
            inOutWardMACMapingDTO.setItemId(item1.getId());
            // super.saveEntity(inOutWardMACMapingDTO);
            return inOutWardMACMapingDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Transactional
    public void checkItemsForInwardOfOutward(List<InOutWardMACMapping> list) throws Exception {
        //Update Inward
        list.stream().forEach(inOutWardMACMapping -> {
            if (inOutWardMACMapping.getMacAddress() != null) {
                itemService.updateItemMacAndSerial(inOutWardMACMapping.getId(), inOutWardMACMapping.getMacAddress(), inOutWardMACMapping.getSerialNumber());
            } else {
                itemService.updateItemSerial(inOutWardMACMapping.getId(), inOutWardMACMapping.getSerialNumber());
            }
        });
        // Save InOutwardMACMapping
        List<Long> itemIds = list.stream().map(InOutWardMACMapping::getId).collect(Collectors.toList());
        List<InOutWardMACMapping> inOutWardMACMappingList = new ArrayList<>();
        inOutWardMACMappingList.addAll(inOutWardMacRepo.findAllByItemIdInAndIsForwarded(itemIds, 0));
        System.out.println("done fetching");
        // Update Old Inward
//        Inward inward = inwardRepository.findByOutwardId(list.get(0).getOutwardId());
        QInward qInward = QInward.inward;
        BooleanExpression booleanExpression = qInward.isDeleted.eq(false).and(qInward.status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).and(qInward.outwardId.id.eq(list.get(0).getOutwardId()));
        Inward inward = inwardRepository.findOne(booleanExpression).get();
        inward.setTotalMacSerial(inward.getTotalMacSerial() + list.size());
        inwardRepository.save(inward);
        System.out.println("complete saving 1111");
        // Update InoutMapping BY Old Inward
        List<InOutWardMACMapping> updatedMappings = new ArrayList<>();
        List<Item> updatedItems = new ArrayList<>();
        inOutWardMACMappingList.stream().forEach(inOutWardMACMapping -> {
            InOutWardMACMapping inOutWardMACMapping1 = repository.findById(Long.valueOf(inOutWardMACMapping.getId())).get();
            inOutWardMACMapping1.setIsForwarded(1);
            inOutWardMACMapping1.setInwardIdOfOutward(inward.getId());
            updatedMappings.add(inOutWardMACMapping1);
            //InOutWardMACMapping newInOutMapping = repository.save(inOutWardMACMapping1);
            Inward newInward = inwardRepository.findById((long) inward.getId()).get();
            Item item = itemRepository.findById(inOutWardMACMapping.getItemId()).get();
            item.setCurrentInwardId(inOutWardMACMapping1.getInwardIdOfOutward());
            item.setOwnerId(newInward.getDestinationId());
            item.setOwnerType(newInward.getDestinationType());
            item.setItemStatus(CommonConstants.UNALLOCATED);
            //itemRepository.save(item);
            updatedItems.add(item);
        });

        repository.saveAll(updatedMappings);

        System.out.println("complte saving 22222");

        itemRepository.saveAll(updatedItems);

        System.out.println("complete saving 3333");

        // Create New InoutMapping By New Inward
        //   List<InOutWardMACMapping> inOutWardMACMappingListBYInward = repository.findByInwardId(inOutWardMACMappingList.get(0).getInwardId());
        List<InOutWardMACMapping> result = new ArrayList<>();
//        String staffuer= staffUserRepository.findById(getLoggedInUserId()).get().getUsername();
//        Integer id = staffUserRepository.findById(getLoggedInUserId()).get().getId();
//            if (k == 0) {
        for (int j = 0; j < list.size(); j++) {
            InOutWardMACMapping inOutWardMACMapping = new InOutWardMACMapping();
            inOutWardMACMapping.setInwardId(Long.valueOf(inward.getId()));
            inOutWardMACMapping.setOutwardId(list.get(j).getOutwardId());
            inOutWardMACMapping.setIsDeleted(false);
            inOutWardMACMapping.setIsForwarded(0);
            inOutWardMACMapping.setStatus(inOutWardMACMappingList.get(j).getStatus());
            inOutWardMACMapping.setMacAddress(inOutWardMACMappingList.get(j).getMacAddress());
            inOutWardMACMapping.setSerialNumber(inOutWardMACMappingList.get(j).getSerialNumber());
            inOutWardMACMapping.setCreatedByName(getLoggedInUser().getFullName());
            inOutWardMACMapping.setLastModifiedById(getLoggedInUserId());
            inOutWardMACMapping.setCreatedById(getLoggedInUserId());
            inOutWardMACMapping.setCreatedate(LocalDateTime.now());
            inOutWardMACMapping.setUpdatedate(LocalDateTime.now());
            inOutWardMACMapping.setLastModifiedByName(getLoggedInUser().getFullName());
            inOutWardMACMapping.setItemId(inOutWardMACMappingList.get(j).getItemId());
            result.add(inOutWardMACMapping);
            //  repository.save(inOutWardMACMapping);
        }
//            }

        repository.saveAll(result);
        System.out.println("complete saving 4444");
    }

    public void saveAutoInOutwardMacMappingForNSI(NonSerializedItemDto dto, InwardDto inwardDto) {
        try {
            InOutWardMACMapingDTO inOutWardMACMapingDTO = new InOutWardMACMapingDTO();
            inOutWardMACMapingDTO.setInwardId(inwardDto.getId());
            inOutWardMACMapingDTO.setOutwardId(null);
            inOutWardMACMapingDTO.setCustInventoryMappingId(null);
            inOutWardMACMapingDTO.setMacAddress(null);
            // TODO: pass mvnoID manually 6/5/2025
            inOutWardMACMapingDTO.setMvnoId(getMvnoIdFromCurrentStaff(null));
            inOutWardMACMapingDTO.setStatus(CommonConstants.ACTIVE_STATUS);
            inOutWardMACMapingDTO.setIsForwarded(0);
            inOutWardMACMapingDTO.setIsReturned(0);
            inOutWardMACMapingDTO.setNonSerializedItemId(dto.getId());
            super.saveEntity(inOutWardMACMapingDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void checkNonSerializedItemsForInwardOfOutward(List<InOutWardMACMapping> list, OutwardDto outwardDto) throws Exception {
        try {
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            List<InOutWardMACMapping> resultInOutWardMACMappingList = new ArrayList<>();
            QNonSerializedItem qNonSerializedItem = QNonSerializedItem.nonSerializedItem;
            List<NonSerializedItem> resultNonSerializedItemList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                BooleanExpression booleanExpressionInoutward = qInOutWardMACMapping.isDeleted.eq(false).and(qInOutWardMACMapping.nonSerializedItemId.eq(list.get(i).getId())).and(qInOutWardMACMapping.isForwarded.eq(0));
                List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(booleanExpressionInoutward));
                resultInOutWardMACMappingList.addAll(inOutWardMACMappingList);
            }
            for (int j = 0; j < list.size(); j++) {
                BooleanExpression booleanExpressionNonSerializedItem = qNonSerializedItem.isDeleted.eq(false).and(qNonSerializedItem.id.eq(list.get(j).getId()));
                List<NonSerializedItem> nonSerializedItemList = IterableUtils.toList(nonSerializedItemRepository.findAll(booleanExpressionNonSerializedItem));
                resultNonSerializedItemList.addAll(nonSerializedItemList);
            }
            Long selectedNonSerializedItemQTY = 0L;
            for (int j = 0; j < resultNonSerializedItemList.size(); j++) {
                selectedNonSerializedItemQTY = selectedNonSerializedItemQTY + resultNonSerializedItemList.get(j).getQty();
            }
            if (selectedNonSerializedItemQTY > outwardDto.getInTransitQty()) {
                checkNewInwardFromOutwardDetail(resultInOutWardMACMappingList, resultNonSerializedItemList, outwardDto);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void checkNewInwardFromOutwardDetail(List<InOutWardMACMapping> resultInOutWardMACMappingList, List<NonSerializedItem> resultNonSerializedItemList, OutwardDto outwardDto) throws Exception {
        try {
            QInward qInward = QInward.inward;
            BooleanExpression booleanExpressionInwardOfOutward = qInward.isDeleted.eq(false).and(qInward.outwardId.id.eq(outwardDto.getId()));
            List<Inward> inwards = IterableUtils.toList(inwardRepository.findAll(booleanExpressionInwardOfOutward));
            for (int i = 0; i < resultNonSerializedItemList.size(); i++) {
                if (outwardDto.getInTransitQty() > inwards.get(0).getInTransitQty() || outwardDto.getInTransitQty().equals(inwards.get(0).getInTransitQty())) {
                    NonSerializedItem nonSerializedItem = resultNonSerializedItemList.get(i);
                    InOutWardMACMapping inOutWardMACMapping = resultInOutWardMACMappingList.get(i);
                    updateNonSerializedItemQty(inOutWardMACMapping, nonSerializedItem, outwardDto, inwards);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateNonSerializedItemQty(InOutWardMACMapping inOutWardMACMapping, NonSerializedItem nonSerializedItem, OutwardDto outwardDto, List<Inward> inwards) throws Exception {
        try {
            Inward inward = inwardRepository.findById(inwards.get(0).getId()).get();
            Long newQty = 0L;
            if (Objects.equals(nonSerializedItem.getQty(), outwardDto.getInTransitQty())) {
                newQty = nonSerializedItem.getQty();
                nonSerializedItem.setQty(nonSerializedItem.getQty());
                nonSerializedItemRepository.save(nonSerializedItem);
                inward.setAssignNonSerializedItemQty(newQty);
                inwardRepository.save(inward);
            } else if (nonSerializedItem.getQty() < outwardDto.getInTransitQty()) {
                if (inward.getAssignNonSerializedItemQty() == 0) {
                    newQty = nonSerializedItem.getQty();
                    nonSerializedItem.setQty(nonSerializedItem.getQty());
                    nonSerializedItemRepository.save(nonSerializedItem);
                    inward.setAssignNonSerializedItemQty(newQty);
                    inwardRepository.save(inward);
                } else {
                    if (!nonSerializedItem.getQty().equals(inward.getAssignNonSerializedItemQty())) {
                        newQty = nonSerializedItem.getQty() - inward.getAssignNonSerializedItemQty();
                        nonSerializedItem.setQty(nonSerializedItem.getQty() - inward.getAssignNonSerializedItemQty());
                        nonSerializedItemRepository.save(nonSerializedItem);
                        inward.setAssignNonSerializedItemQty(newQty + inward.getAssignNonSerializedItemQty());
                        inwardRepository.save(inward);
                    } else if (inward.getAssignNonSerializedItemQty() < outwardDto.getInTransitQty()) {
                        newQty = outwardDto.getInTransitQty() - nonSerializedItem.getQty();
                        nonSerializedItem.setQty(nonSerializedItem.getQty() - newQty);
                        nonSerializedItemRepository.save(nonSerializedItem);
                        inward.setAssignNonSerializedItemQty(newQty + inward.getAssignNonSerializedItemQty());
                        inwardRepository.save(inward);
                    }
                }
            } else if (nonSerializedItem.getQty() > outwardDto.getInTransitQty()) {
                newQty = outwardDto.getInTransitQty();
                nonSerializedItem.setQty(nonSerializedItem.getQty() - outwardDto.getInTransitQty());
                nonSerializedItemRepository.save(nonSerializedItem);
                inward.setAssignNonSerializedItemQty(newQty + inward.getAssignNonSerializedItemQty());
                inwardRepository.save(inward);
            }
            NonSerializedItemDto dto = saveNewNonSerializedItem(inward);
            saveNonSerializedItemHierarchy(nonSerializedItem, dto);
//            updateInoutMappingBYOldInwardForNonSerializedItem(resultInOutWardMACMappingList, )
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public NonSerializedItemDto saveNewNonSerializedItem(Inward inward) throws Exception {
        try {
            InwardDto inwardDto = inwardService.getEntityForUpdateAndDelete(inward.getId(),inward.getMvnoId());
            Product pcId = productRepository.findById(inwardDto.getProductId().getId()).get();
            NonSerializedItemDto newNonSerializedItemDto = new NonSerializedItemDto();
            newNonSerializedItemDto.setName(nonSerializedItemService.getRandomenumber("NSI", "-", ""));
            newNonSerializedItemDto.setProductId(pcId.getId());
            newNonSerializedItemDto.setNonSerializedItemcondition(null);
            newNonSerializedItemDto.setCurrentInwardId(inwardDto.getId());
            newNonSerializedItemDto.setMvnoId(inwardDto.getMvnoId());
            newNonSerializedItemDto.setOwnerId(inwardDto.getDestinationId());
            newNonSerializedItemDto.setOwnerType(inwardDto.getDestinationType());
            newNonSerializedItemDto.setCurrentInwardType(TypeConstants.FORWARDED);
            newNonSerializedItemDto.setOwnershipType("Subisu Owned");
            newNonSerializedItemDto.setItemStatus(CommonConstants.UNALLOCATED);
            newNonSerializedItemDto.setWarranty("NotStarted");
            newNonSerializedItemDto.setQty(inwardDto.getAssignNonSerializedItemQty());
            Integer wrty = pcId.getExpiryTime();
            if (pcId.getExpiryTimeUnit().equalsIgnoreCase("Month")) {
                wrty = 30 * wrty;
                newNonSerializedItemDto.setWarrantyPeriod(wrty);
            } else {
                newNonSerializedItemDto.setWarrantyPeriod(wrty);
            }
            NonSerializedItemDto dto = nonSerializedItemService.saveEntity(newNonSerializedItemDto);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveNonSerializedItemHierarchy(NonSerializedItem nonSerializedItem, NonSerializedItemDto dto) {
        NonSerializedItemHierarchy nonSerializedItemHierarchy = new NonSerializedItemHierarchy();
        nonSerializedItemHierarchy.setParentItemId(nonSerializedItem.getId());
        nonSerializedItemHierarchy.setChildItemId(dto.getId());
        // TODO: pass mvnoID manually 6/5/2025
        nonSerializedItemHierarchy.setMvnoId(getMvnoIdFromCurrentStaff(null));
        nonSerializedItemHierarchy.setQty(dto.getQty());
        nonSerializedItemHierarchyRepository.save(nonSerializedItemHierarchy);
    }

    public void updateMacSerialByItem(Long itemId, String macAddress, String serialNumber) throws Exception {
        try {
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            List<InOutWardMACMapping> list =new ArrayList<>();


            BooleanExpression booleanExpression = qInOutWardMACMapping.isDeleted.eq(false).and(qInOutWardMACMapping.itemId.eq(itemId));
            List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(booleanExpression));
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    inOutWardMACMappingList.parallelStream().map(inOutWardMACMapping -> CompletableFuture.runAsync(() -> {
                        inOutWardMACMapping.setMacAddress(macAddress);
                        inOutWardMACMapping.setSerialNumber(serialNumber);
                        list.add(inOutWardMACMapping);
                    })).toArray(CompletableFuture[]::new)
            );

            allFutures.join();
            inOutWardMacRepo.saveAll(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSerialByItem(Long itemId, String serialNumber) throws Exception {
        try {
            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
            BooleanExpression booleanExpression = qInOutWardMACMapping.isDeleted.eq(false).and(qInOutWardMACMapping.itemId.eq(itemId));
            List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(booleanExpression));
            for (int i = 0; i < inOutWardMACMappingList.size(); i++) {
                inOutWardMACMappingList.get(i).setSerialNumber(serialNumber);
                inOutWardMacRepo.saveAll(inOutWardMACMappingList);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CustomerInventoryMappingDto updateInventoryMapping(CustomerInventoryMapping customerInventoryMapping, Long mappingId, Long customerInventoryId, Long customerId, boolean isflag, String remark) throws Exception {

        customerInventoryMapping.setQty(customerInventoryMapping.getQty() - 1);
        removeMappingWithCustomerInventory(mappingId);
        Product product = productService.getRepository().getOne(customerInventoryMapping.getProduct().getId());
        if (Objects.nonNull(product)) {
            RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo();
            recordPaymentPojo.setAmount(Double.valueOf(product.getRefurburshiedProductRefAmountInWarranty()));
            recordPaymentPojo.setCustomerid(customerInventoryMapping.getCustomer().getId());
            List<Integer> invoiceIds = new ArrayList<>();
            invoiceIds.add(0);
            recordPaymentPojo.setInvoiceId(invoiceIds);
            recordPaymentPojo.setPaymentdate(LocalDate.now());
            recordPaymentPojo.setPaymode("Cash");
            recordPaymentPojo.setPaytype("advance");
            recordPaymentPojo.setType("creditnote");
            recordPaymentPojo.setRemark("Refund amount for removing Product :-" + product.getName());
            creditDocService.save(recordPaymentPojo, false, false, false,null);

        }

        if (customerInventoryMapping.getInwardId() != null) {
            Inward inward = inwardRepository.findById(customerInventoryMapping.getInwardId()).get();
            if (inward != null) {
                inward.setUnusedQty(inward.getUnusedQty() + 1);
                inward.setUsedQty(inward.getUsedQty() - 1);
                inwardRepository.save(inward);
            }
        }

        if (customerInventoryMapping.getExternalItemId() != null) {
            ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(customerInventoryMapping.getExternalItemId()).get();
            if (externalItemManagement != null) {
                externalItemManagement.setUnusedQty(externalItemManagement.getUnusedQty() + 1);
                externalItemManagement.setUsedQty(externalItemManagement.getUsedQty() - 1);
                externalItemManagementRepository.save(externalItemManagement);
            }
            for (int i = 0; i < customerInventoryMapping.getExternalItemMacSerialMappings().size(); i++) {
                ExternalItemMacSerialMapping externalItemMacSerialMapping = externalItemMacSerialMappingRepo.findById(customerInventoryMapping.getExternalItemMacSerialMappings().get(i).getId()).get();
                if (externalItemMacSerialMapping != null) {
                    externalItemMacSerialMapping.setCustInventoryMappingId(null);
                    externalItemMacSerialMappingRepo.save(externalItemMacSerialMapping);
                }
            }
        }
        InOutWardMACMapping inOutWardMACMapping = repository.findById(mappingId).get();
        Item item = itemRepository.findById(inOutWardMACMapping.getItemId()).get();
             /*if(customers != null) {
                if (!customers.getIstrialplan() && item.getWarranty().equalsIgnoreCase("InWarranty")) {
                    item.setItemStatus(CommonConstants.RETURNED);
                    item.setWarranty("Paused");
                    item.setCondition(CommonConstants.DEFECTIVE);
                    item.setIntransiantWarrenty(null);
                    itemRepository.save(item);
                }*/
        if (!Objects.isNull(item)) {
            if (item.getOwnershipType().equalsIgnoreCase("Subisu Owned")) {
                itemService.updateItemStatusForCustomer(item.getId(), CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                    itemService.updateItemWarranty(item.getId(), "Paused");
                }
                NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndCustInventoryIdAndIsDeletedIsFalse(item.getId(), customerInventoryId);
//                NetworkDevices networkDevices = networkDeviceRepository.findByItemIdAndIsDeletedFalse(item.getId());
                if (!Objects.isNull(networkDevices)) {
                    networkDevices.setIsDeleted(true);
                    networkDeviceRepository.save(networkDevices);
                }
                List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
                ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
                itemReturnDTO.setId(item.getId());
                itemReturnDTOList.add(itemReturnDTO);
                itemService.returnItemfromStaffremove(itemReturnDTOList);
                if(getLoggedInUser().getPartnerId() != 1) {
                    item.setOwnerType(CommonConstants.PARTNER);
                    item.setOwnerId(Long.valueOf(getLoggedInUser().getPartnerId()));
                } else {
                    item.setOwnerType(CommonConstants.STAFF);
                    item.setOwnerId(Long.valueOf(getLoggedInUser().getUserId()));
                }
                itemRepository.save(item);
            }
            if (item.getOwnershipType().equalsIgnoreCase("Sold") && !isflag) {
                itemService.updateItemStatusForCustomer(item.getId(), CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(customerInventoryId);
                if (!Objects.isNull(networkDevices)) {
                    networkDevices.setIsDeleted(true);
                    networkDeviceRepository.save(networkDevices);
                }

            }
            if (item.getOwnershipType().equalsIgnoreCase("Sold") && isflag == true) {
                itemService.updateItemStatusForCustomer(item.getId(), CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(customerInventoryId);
                if (!Objects.isNull(networkDevices)) {
                    networkDevices.setIsDeleted(true);
                    networkDeviceRepository.save(networkDevices);
                }
                item.setOwnershipType("Subisu Owned");
                if (item.getWarranty().equalsIgnoreCase("InWarranty")) {
                    itemService.updateItemWarranty(item.getId(), "Paused");
                }
                List<ItemReturnDTO> itemReturnDTOList = new ArrayList<>();
                ItemReturnDTO itemReturnDTO = new ItemReturnDTO();
                itemReturnDTO.setId(item.getId());
                itemReturnDTOList.add(itemReturnDTO);
                itemService.returnItemfromStaffremove(itemReturnDTOList);
                if(getLoggedInUser().getPartnerId() != 1) {
                    item.setOwnerType(CommonConstants.PARTNER);
                    item.setOwnerId(Long.valueOf(getLoggedInUser().getPartnerId()));
                } else {
                    item.setOwnerType(CommonConstants.STAFF);
                    item.setOwnerId(Long.valueOf(getLoggedInUser().getUserId()));
                }
                itemRepository.save(item);

            }

            if (item.getOwnershipType().equalsIgnoreCase("Customer Owned") || item.getOwnershipType().equalsIgnoreCase("Temporary") || item.getOwnershipType().equalsIgnoreCase("Partner Owned")) {
                itemService.updateItemStatusForCustomer(item.getId(), CommonConstants.UNALLOCATED, LocalDateTime.now(), customerInventoryMapping.getCustomer().getId().longValue(), CommonConstants.REMOVE_INVETORIES);
                NetworkDevices networkDevices = networkDeviceRepository.findByCustInventoryId(customerInventoryId);
                if (!Objects.isNull(networkDevices)) {
                    networkDevices.setIsDeleted(true);
                    networkDeviceRepository.save(networkDevices);
                }
            }
        }
        //deleteCustomerInventory
        customerInventoryMapping.setIsDeleted(true);
        customerInventoryMapping.setApprovalRemark(remark);
        customerInventoryMapping.setNextApprover(null);
        customerInventoryMapping.setStatus("REMOVED");
        customerInventoryMapping.setTeamHierarchyMappingId(null);
        customerInventoryMappingRepo.save(customerInventoryMapping);


        try {
            List<Item> items = itemRepository.getall(inOutWardMACMapping.getItemId());
            Return aReturn = new Return();
            aReturn.setMac_name(items.get(0).getMacAddress());
            aReturn.setItem_status(items.get(0).getItemStatus());
            aReturn.setItem_condition(items.get(0).getCondition());
            aReturn.setProduct_id(items.get(0).getProductId());
            aReturn.setCurrent_inward_type(items.get(0).getCurrentInwardType());
            aReturn.setCurrent_inward_id(items.get(0).getCurrentInwardId());
            aReturn.setSerial_no(items.get(0).getSerialNumber());
            aReturn.setProduct_name(items.get(0).getName());
            aReturn.setCust_id(Long.parseLong(customerInventoryMapping.getCustomer().getId().toString()));
            returnRepo.save(aReturn);

        } catch (Exception ex) {
            ex.getMessage();
        }
        return customerInventoryMappingMapper.domainToDTO(customerInventoryMapping, new CycleAvoidingMappingContext());
    }

    public GenericDataDTO genearateRemoveInventoryRequest(Long macmappingId,Long customerInventoryId,Long customerId, boolean isflag,Long revisedcharge){
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        try{
            GenerateRemoveRequest generateRemoveRequest=new GenerateRemoveRequest();
            generateRemoveRequest.setMacmappingid(macmappingId);
            generateRemoveRequest.setCustomerinventoryId(customerInventoryId);
            generateRemoveRequest.setCustomerid(customerId);
            generateRemoveRequest.setStaffid((long) getLoggedInUserId());
            generateRemoveRequest.setFlag(isflag);
            generateRemoveRequest.setRequestStatus("PENDING");
            generateRemoveRequest.setDeleted(false);
            generateRemoveRequest.setRevisedcharge(revisedcharge);
            generateRemoveRequest=generateRemoveRequestRepo.save(generateRemoveRequest);
            genericDataDTO.setData(generateRemoveRequest);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Generate Remove Inventory Request Successfully");
        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }
        return genericDataDTO;
    }

    public  GenericDataDTO removeInventoryWorkFlowNew(Long mappingId, Long customerInventoryId, Long customerId,Integer nextStaff, String remark,boolean isApprove){
        try{
            GenericDataDTO genericDataDTO=new GenericDataDTO();
            CustomerInventoryMappingDto entity = customerInventoryMappingMapper.domainToDTO(customerInventoryMappingRepo.findById(customerInventoryId).orElse(null),new CycleAvoidingMappingContext());
            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingRepo.findById(customerInventoryId).get();
            Item item = itemRepository.findById(customerInventoryMapping.getItemId()).get();
            CustomersService customersService = SpringContext.getBean(CustomersService.class);
            Customers customers =  customersRepository.findById(entity.getCustomerId()).get();
            StaffUser loggedInUser = staffUserService.get(getLoggedInUserId(),customers.getMvnoId());
            QGenerateRemoveRequest qGenerateRemoveRequest = QGenerateRemoveRequest.generateRemoveRequest;
            BooleanExpression booleanExpression = qGenerateRemoveRequest.customerid.eq(customerId).and(qGenerateRemoveRequest.customerinventoryId.eq(customerInventoryId)).and(qGenerateRemoveRequest.macmappingid.eq(mappingId)).and(qGenerateRemoveRequest.isDeleted.eq(false));
            GenerateRemoveRequest generateRemoveRequests = generateRemoveRequestRepo.findOne(booleanExpression).orElse(null);
//            boolean isflag = generateRemoveRequests.isFlag();
            if (Objects.equals(loggedInUser.getUsername(), "admin") || Objects.equals(loggedInUser.getUsername(), "superadmin")) {
                if (isApprove) {
                    entity.setStatus("ACTIVE");
                    inOutWardMACService.removeInventory(mappingId, customerInventoryId, customerId, remark);
                    entity.setApprovalRemark(remark);
                    entity.setFlag("approved");
                    entity.setNextApproverId(null);
                    entity.setPreviousApproveId(null);
                    entity.setTeamHierarchyMappingId(null);
                    entity.setIsDeleted(true);
                    generateRemoveRequests.setRequestStatus("APPROVE");
                    generateRemoveRequestRepo.save(generateRemoveRequests);
//                    updateGenerateRemoveRequestStatus(customerInventoryId, customerId, mappingId, "APPROVE");
                    genericDataDTO.setResponseMessage("Remove Inventory Successfully");
                    customerInventoryMapping= customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
                    genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping,new CycleAvoidingMappingContext()));
                    ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                    messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                    kafkaMessageSender.send(new KafkaMessageData(itemMessage,ItemMessage.class.getSimpleName()));

                    itemMessage.setMessage("Serialized Item after Approval of Remove Inventory Item");
                    item.setRemoveFrom("Customer");
                    ItemMessage itemMessage2 = new ItemMessage(item, "Serialized Item at Inventory Approveal");
                    //messageSender.send(itemMessage2,RabbitMqConstants.QUEUE_APIGW_APPROVE_REMOVE_INVENTORY_SERIALIZEDITEM_REQUEST_IN_INTEGRATION);
                    CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                    messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                    kafkaMessageSender.send(new KafkaMessageData(message,CustomerInventoryMappingMessage.class.getSimpleName()));
                } else {
                    entity.setStatus("ACTIVE");
                    entity.setFlag("rejected");
                    entity.setApprovalRemark(remark);
                    entity.setNextApproverId(null);
                    entity.setPreviousApproveId(null);
                    entity.setTeamHierarchyMappingId(null);
                    //updateGenerateRemoveRequestStatus(customerInventoryId, customerId, mappingId, "REJECTED");
                    customerInventoryMapping= customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
                    generateRemoveRequests.setDeleted(true);
                    generateRemoveRequests.setRequestStatus("REJECTED");
                    generateRemoveRequestRepo.save(generateRemoveRequests);
                    genericDataDTO.setResponseMessage("Rejected Remove Inventory Successfully");
                    genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping,new CycleAvoidingMappingContext()));
                }
                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + entity.getApprovalRemark() + "\n" + entity.getFlag() + " By :- " + loggedInUser.getUsername());
                customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                return genericDataDTO;
            }

            if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE,isApprove, false, customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                StaffUser assignedUser = null;
                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                    StaffUser staffUser = staffUserService.get(Integer.valueOf(map.get("staffId")),customers.getMvnoId());
                    assignedUser = staffUser;
                    entity.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                    entity.setNextApproverId(Integer.valueOf(map.get("staffId")));
                    entity.setPreviousApproveId(getLoggedInUserId());
                    entity.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                    entity.setStatus("PENDING");
                    generateRemoveRequests.setRequestStatus("PENDING");
                    generateRemoveRequestRepo.save(generateRemoveRequests);
                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, Math.toIntExact(entity.getId()), entity.getProductName(), assignedUser.getId(), assignedUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REMOVE, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, Math.toIntExact(entity.getId()), entity.getProductName(), loggedInUser.getId(), loggedInUser.getUsername(), isApprove ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApprove ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
                } else {
                    if (isApprove) {
                        entity.setStatus("ACTIVE");
                        inOutWardMACService.removeInventory(mappingId, customerInventoryId, customerId, remark);
                        entity.setFlag("approved");
                        entity.setNextApproverId(null);
                        entity.setTeamHierarchyMappingId(null);
                        entity.setPreviousApproveId(null);
                        entity.setIsDeleted(true);
                        generateRemoveRequests.setRequestStatus("APPROVE");
                        generateRemoveRequestRepo.save(generateRemoveRequests);
                        //updateGenerateRemoveRequestStatus(customerInventoryId, customerId, mappingId, "APPROVE");
                        customerInventoryMapping=customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
                        ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                        messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                        kafkaMessageSender.send(new KafkaMessageData(itemMessage,ItemMessage.class.getSimpleName()));
                        CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                        kafkaMessageSender.send(new KafkaMessageData(message,CustomerInventoryMappingMessage.class.getSimpleName()));
                        genericDataDTO.setResponseMessage("Remove Inventory Successfully");
                        genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping,new CycleAvoidingMappingContext()));
                    } else {
                        entity.setStatus("ACTIVE");
                        entity.setFlag("rejected");
                        entity.setNextApproverId(null);
                        entity.setTeamHierarchyMappingId(null);
                        entity.setPreviousApproveId(null);
                        entity.setIsDeleted(true);
                       // updateGenerateRemoveRequestStatus(customerInventoryId, customerId, mappingId, "REJECTED");
                        customerInventoryMapping=customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
                        generateRemoveRequests.setDeleted(true);
                        generateRemoveRequests.setRequestStatus("REJECTED");
                        generateRemoveRequestRepo.save(generateRemoveRequests);
                        genericDataDTO.setResponseMessage("Rejected Remove Inventory Successfully");
                        genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping,new CycleAvoidingMappingContext()));
                    }
                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, Math.toIntExact(entity.getId()), entity.getProductName(), loggedInUser.getId(), loggedInUser.getUsername(), isApprove ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), isApprove ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED + " By :- " + loggedInUser.getUsername());
                }
                //TAT functionality
                if (assignedUser != null) {
                    if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                        if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                            map.put("tat_id", map.get("current_tat_id"));
                        tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, entity.getId().intValue(), null);
                    }
                }
            } else {
                Map<String, Object> map = hierarchyService.getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, isApprove, false,customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
                if (map.containsKey("assignableStaff")) {
                    StaffUser staffUser = staffUserService.get(nextStaff,customers.getMvnoId());
                    genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
                    genericDataDTO.setResponseCode(HttpStatus.OK.value());
                    if (isApprove) {
                        entity.setFlag("approved");
                        entity.setApprovalRemark(remark);
                        entity.setStatus("PENDING FOR REMOVE");
                        generateRemoveRequests.setRequestStatus("PENDING");
                        generateRemoveRequestRepo.save(generateRemoveRequests);
                        genericDataDTO.setResponseMessage("Approved Successfully");

                    } else {
                        entity.setFlag("rejected");
                        entity.setApprovalRemark(remark);
                        entity.setStatus("PENDING FOR REMOVE");
//                        generateRemoveRequests.setDeleted(true);
                        generateRemoveRequests.setRequestStatus("PENDING");
                        generateRemoveRequestRepo.save(generateRemoveRequests);
                        genericDataDTO.setResponseMessage("Rejected Remove Inventory Successfully");
                    }
                    customerInventoryMapping=customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + entity.getApprovalRemark() + "\n" + entity.getFlag() + " By :- " + staffUser.getUsername());
                    genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping,new CycleAvoidingMappingContext()));
                    return genericDataDTO;
                } else {

                    if (isApprove) {
                        entity.setStatus("ACTIVE");
                        inOutWardMACService.removeInventory(mappingId, customerInventoryId, customerId, remark);
                        entity.setFlag("approved");
                        entity.setApprovalRemark(remark);
                        entity.setNextApproverId(null);
                        entity.setTeamHierarchyMappingId(null);
                        entity.setPreviousApproveId(null);
                        entity.setIsDeleted(true);
//                        updateGenerateRemoveRequestStatus(customerInventoryId, customerId, mappingId, "APPROVE");
                        customerInventoryMapping=customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
                        ItemMessage itemMessage = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//                        messageSender.send(itemMessage, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
                        kafkaMessageSender.send(new KafkaMessageData(itemMessage,ItemMessage.class.getSimpleName()));
                        CustomerInventoryMappingMessage message = new CustomerInventoryMappingMessage(customerInventoryMapping, "Customer Inventory Message for Intrigation", false);
//                        messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_CUSTOMER_INVENTORY);
                        kafkaMessageSender.send(new KafkaMessageData(message,CustomerInventoryMappingMessage.class.getSimpleName()));
                        generateRemoveRequests.setRequestStatus("APPROVE");
                        generateRemoveRequestRepo.save(generateRemoveRequests);
                        genericDataDTO.setResponseMessage("Remove Inventory Successfully");
                        genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping,new CycleAvoidingMappingContext()));
                    } else {
                        entity.setStatus("ACTIVE");
                        entity.setApprovalRemark(remark);
                        entity.setFlag("rejected");
                        entity.setNextApproverId(null);
                        entity.setTeamHierarchyMappingId(null);
                        entity.setPreviousApproveId(null);
//                        updateGenerateRemoveRequestStatus(customerInventoryId, customerId, mappingId, "REJECTED");
                        customerInventoryMapping=customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
                        generateRemoveRequests.setDeleted(true);
                        generateRemoveRequests.setRequestStatus("REJECTED");
                        generateRemoveRequestRepo.save(generateRemoveRequests);
                        genericDataDTO.setResponseMessage("Rejected Remove Inventory Successfully");
                        genericDataDTO.setData(customerInventoryMappingMapper.domainToDTO(customerInventoryMapping,new CycleAvoidingMappingContext()));
                    }
                    entity.setPreviousApproveId(null);
                    customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + entity.getApprovalRemark() + "\n" + entity.getFlag() + " By :- " + loggedInUser.getUsername());
                    return genericDataDTO;
                }
            }

        }catch (Exception exception){
            throw new RuntimeException(exception.getMessage());
        }
        return null;
     }
//    public GenericDataDTO removeinventoryWorkflow(Long mappingId, Long customerInventoryId, Long customerId, boolean isflag, String remark) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            inOutWardMACService.removeInventory(mappingId, customerInventoryId, customerId, remark);
//            CustomerInventoryMapping customerInventoryMapping = customerInventoryMappingService.getRepository().findById(customerInventoryId).orElse(null);
////            customerInventoryMapping.setStatus("PENDING");
//            customerInventoryMappingRepo.save(customerInventoryMapping);
////            CustomerInventoryMappingDto entity = customerInventoryMappingMapper.domainToDTO(customerInventoryMapping, new CycleAvoidingMappingContext());
////            StaffUser loggedInUser = staffUserService.get(getLoggedInUserId());
////            entity.setFlag("rejected");
////            entity.setNextApproverId(null);
////            entity.setIsDeleted(true);
////            entity.setPreviousApproveId(getLoggedInUserId());
////            entity.setPreviousApproveId(null);
////            entity.setTeamHierarchyMappingId(null);
////            customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));//            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + entity.getApprovalRemark() + "\n" + entity.getFlag() + " By :- " + loggedInUser.getUsername());
//    //        genericDataDTO.setData(customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext())));
//            genericDataDTO.setData(null);
//            genericDataDTO.setResponseMessage("Remove Inventory Successfully");
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//
//    //        }
//    //        if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
//    //            Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, false, true, customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
//    //            StaffUser assignedUser = null;
//    //            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
//    //                StaffUser staffUser = staffUserService.get(Integer.valueOf(map.get("staffId")));
//    //                assignedUser = staffUser;
//    //                entity.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//    //                entity.setNextApproverId(Integer.valueOf(map.get("staffId")));
//    //                entity.setPreviousApproveId(getLoggedInUserId());
//    //                entity.setTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
//    //                entity.setStatus("PENDING");
//    ////                genericDataDTO.setData(customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext())));
//    //                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, Math.toIntExact(entity.getId()), entity.getProductName(), assignedUser.getId(), assignedUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUser.getUsername());
//    //            } else {
//    //                entity.setNextApproverId(getLoggedInUserId());
//    //                entity.setTeamHierarchyMappingId(null);
//    //                entity.setIsDeleted(true);
//    //                try {
//    //                    inOutWardMACService.removeInventory(mappingId, customerInventoryId, customerId, isflag, remark);
//    //                } catch (Exception e) {
//    //                    throw new RuntimeException(e);
//    //                }
//    //            }
//    //            //TAT functionality
//    //            if (assignedUser != null) {
//    //                if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
//    //                    if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
//    //                        map.put("tat_id", map.get("current_tat_id"));
//    //                    tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, entity.getId().intValue(), null);
//    //                }
//    //            }
//    //        } else {
//    //            Map<String, Object> map = hierarchyService.getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, CommonConstants.HIERARCHY_TYPE, false, true, customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
//    //            if (map.containsKey("assignableStaff")) {
//    //                genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
//    ////                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + entity.getApprovalRemark() + "\n" + entity.getFlag() + " By :- " + loggedInUser.getUsername());
//    //            } else {
//    //                entity.setFlag("approved");
//    //                entity.setStatus("REJECTED");
//    //                try {
//    //                    inOutWardMACService.removeInventory(mappingId, customerInventoryId, customerId, isflag, remark);
//    //                } catch (Exception e) {
//    //                    throw new RuntimeException(e);
//    //                }
//    //                entity.setIsDeleted(true);
//    //                entity.setNextApproverId(getLoggedInUserId());
//    //                entity.setTeamHierarchyMappingId(null);
//    //                entity.setPreviousApproveId(null);
//    //                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CUSTOMER_INVENTORY_ASSIGN, entity.getId().intValue(), entity.getProductName(), getLoggedInUserId(), loggedInUser.getFullName(), entity.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + entity.getApprovalRemark() + "\n" + entity.getFlag() + " By :- " + loggedInUser.getUsername());
//    //            }
//    //            customerInventoryMappingRepo.save(customerInventoryMappingMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
//    //        }
//        } catch (Exception e) {
//            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
//        }
//        return genericDataDTO;
//    }

//    public void updateInoutwardMacMappingforSerialized(Long custInventoryMapId, CustomerInventoryMappingDto customerInventoryMappingDto) {
//        //To Set CustometInventoryMappingId to InOutMacMappingId
//        QCustomerInventoryMapping qCustomerInventoryMapping = QCustomerInventoryMapping.customerInventoryMapping;
//        BooleanExpression booleanExpression = qCustomerInventoryMapping.isDeleted.eq(false).and(qCustomerInventoryMapping.id.eq(custInventoryMapId));
//        List<CustomerInventoryMapping> customerInventoryMappings = IterableUtils.toList(customerInventoryMappingRepo.findAll(booleanExpression));
//        customerInventoryMappings.stream().forEach(customerInventoryMapping -> {
//            customerInventoryMappingDto.getInOutWardMACMapping().get(0).setCustInventoryMappingId(customerInventoryMapping.getId());
//            inOutWardMacRepo.save(customerInventoryMappingDto.getInOutWardMACMapping().get(0));
//        });
//    }

    //Update Generate Remove Request
//    public void updateGenerateRemoveRequestStatus(Long customerInventoryId, Long customerId, Long mappingId, String status) {
//        try {
//            GenerateRemoveRequest generateRemoveRequest = generateRemoveRequestRepo.findByCustomerinventoryIdAndCustomeridAndMacmappingid(customerInventoryId, customerId, mappingId);
//            if(generateRemoveRequest != null) {
//                generateRemoveRequest.setCustomerinventoryId(generateRemoveRequest.getCustomerinventoryId());
//                generateRemoveRequest.setFlag(generateRemoveRequest.isFlag());
//                generateRemoveRequest.setCustomerid(generateRemoveRequest.getCustomerid());
//                generateRemoveRequest.setMacmappingid(generateRemoveRequest.getMacmappingid());
//                generateRemoveRequest.setStaffid(generateRemoveRequest.getStaffid());
//                if(status.equalsIgnoreCase("APPROVE") || status.equalsIgnoreCase("REJECTED")) {
//                    generateRemoveRequest.setRequestStatus(status);
//                } else {
//                    generateRemoveRequest.setRequestStatus(generateRemoveRequest.getRequestStatus());
//                }
//                generateRemoveRequestRepo.save(generateRemoveRequest);
//            }
//        } catch (CustomValidationException e) {
//            throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
//        }
//    }

//    //Send Serialized Item for Intrigartion
//    public void sendSerializedItemforIntrigation(Item item) {
//        try {
//            ItemMessage message = new ItemMessage(item, "Serialized Item at Inventory Approveal");
//            messageSender.send(message, RabbitMqConstants.QUEUE_SERVICE_FOR_INVENTORY_ITEM);
//        } catch (CustomValidationException e) {
//            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
//        }
//    }
//    public void validateUpdateMacMappingList(List<InOutWardMACMapping> list, OutwardDto outwardDto, boolean hasMac, boolean hasSerial) {
//        try {
//            Integer mvnoId = getMvnoIdFromCurrentStaff();
//            QInOutWardMACMapping qInOutWardMACMapping = QInOutWardMACMapping.inOutWardMACMapping;
//            BooleanExpression booleanExpression = qInOutWardMACMapping.isDeleted.eq(false).and(qInOutWardMACMapping.outwardId.eq(outwardDto.getId())).and(qInOutWardMACMapping.isForwarded.eq(0));
//            List<InOutWardMACMapping> inOutWardMACMappingList = IterableUtils.toList(inOutWardMacRepo.findAll(booleanExpression));
//            Integer totalSelQty = list.size() + inOutWardMACMappingList.size();
//            if (outwardDto.getInTransitQty() < list.size()) {
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "The selected Items are more than outward qty.", null);
//            } else if (totalSelQty > outwardDto.getInTransitQty()) {
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Outward have only " + outwardDto.getInTransitQty() + " quantity available for mac mapping.", null);
//            }
//            // Check Duplicate Mac Address in Selected Item From List
//
//            List<String> macAddress = new ArrayList<>();
//            List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
//            if (hasSerial) {
//                list.stream().forEach(inOutWardMACMapping -> {
//
//                    completableFutures.add(CompletableFuture.runAsync(()->{
//
//                        // Check Serial Number is present in selected item
//                        if (inOutWardMACMapping.getSerialNumber() == null || inOutWardMACMapping.getSerialNumber().equals("")) {
//                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Please enter serial number in selected items..", null);
//                        }
//                        if (inOutWardMACMapping.getMacAddress()!=null) {
//                            if (macAddress.contains(inOutWardMACMapping.getMacAddress())) {
//                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Entered a MAC " + inOutWardMACMapping.getMacAddress() + " is Already Exist", null);
//                            }
//                            macAddress.add(inOutWardMACMapping.getMacAddress());
//                        }
//                        // Check Duplicate Mac Address in Selected Item From Database
//                        Item item = itemRepository.findById(inOutWardMACMapping.getId()).get();
//                        String itemMac = item.getMacAddress();
//
//                        if (!Objects.equals(inOutWardMACMapping.getMacAddress(), null)) {
//                            Item  item1 = null;
//                            if (itemMac != null) {
//                                if (!itemMac.equals(inOutWardMACMapping.getMacAddress())) {
//
//                                    if (mvnoId == 1) {
//                                        item1 = itemRepository.findByIsDeletedIsFalseAndMacAddress(inOutWardMACMapping.getMacAddress());
//
//                                    } else {
//                                        item1 = itemRepository.findByIsDeletedIsFalseAndMacAddressAndMvnoIdIn(inOutWardMACMapping.getMacAddress(), Arrays.asList(mvnoId, 1));
//                                    }
//                                }
//                            } else {
//                                if (mvnoId == 1) {
//                                    item1 =itemRepository.findByIsDeletedIsFalseAndMacAddress(inOutWardMACMapping.getMacAddress());
//                                } else {
//                                    item1 = itemRepository.findByIsDeletedIsFalseAndMacAddressAndMvnoIdIn(inOutWardMACMapping.getMacAddress(), Arrays.asList(mvnoId, 1));
//
//                                }
//                            }
//                            if (item1 != null) {
//                                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Entered a MAC " + macAddress + " already exists", null);
//                            }
//                            new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Entered a MAC " + inOutWardMACMapping.getMacAddress() + " is Already Exist", null);
//                        }
//
//                    }));
//
//
//                });
//                for(CompletableFuture completableFuture:completableFutures){
//                    completableFuture.join();
//                }
//            }
//        } catch (CustomValidationException exception) {
//            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), exception.getMessage(), null);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public InOutWardMACMapingDTO saveEntityFromIntegrationRms(InOutWardMACMapingDTO entity) throws Exception {
        try {
            boolean flag = true;
            if (entity.getMacAddress() != null) {
                flag = inOutWardMACService.duplicateVerifyAtSave(entity.getMacAddress());
            }
            // TODO: pass mvnoID manually 6/5/2025
            entity.setMvnoId(getMvnoIdFromCurrentStaff(null));
            if (flag) {
                Inward inward = inwardRepository.findByRmsInwardId(String.valueOf(entity.getInwardId()));
                InwardServiceImpl inwardService = SpringContext.getBean(InwardServiceImpl.class);
                InwardDto inwardDto = inwardMapper.domainToDTO(inwardRepository.findByRmsInwardId(String.valueOf(entity.getInwardId())),new CycleAvoidingMappingContext());
                entity.setInwardId(inwardDto.getId());
                inwardDto.setTotalMacSerial(inward.getTotalMacSerial() + 1);
               // inwardService.updateEntity(inwardDto);
                inwardRepository.save(inwardMapper.dtoToDomain(inwardDto,new CycleAvoidingMappingContext()));
                ItemDto item = new ItemDto();
                item.setMacAddress(entity.getMacAddress());
                item.setSerialNumber(entity.getSerialNumber());
                item.setName(inwardDto.getProductId().getName());
                item.setCondition(inward.getType());
                item.setMvnoId(inward.getMvnoId());

                item.setOwnerId(inward.getDestinationId());
                item.setOwnerType(inward.getDestinationType());
                item.setCurrentInwardType(TypeConstants.FORWARDED);
                item.setCurrentInwardId(inward.getId());
                item.setProductId(inward.getProductId().getId());
                item.setOwnershipType("Subisu Owned");

                item.setItemStatus(CommonConstants.UNALLOCATED);

                Integer wrty = inward.getProductId().getExpiryTime();
                if (inward.getProductId().getExpiryTimeUnit().equalsIgnoreCase("Month")) {
                    wrty = 30 * wrty;
                    item.setWarrantyPeriod(wrty);
                } else {
                    item.setWarrantyPeriod(wrty);
                }

                item.setWarranty("NotStarted");

              //  ItemDto item1 = itemService.saveEntity(item);
                Item item2 = itemRepository.save(itemMapper.dtoToDomain(item,new CycleAvoidingMappingContext()));
                ItemDto itemDto=itemMapper.domainToDTO(item2,new CycleAvoidingMappingContext());
                ItemConditionsMappingDto itemConditionsMappingDto = new ItemConditionsMappingDto();
                itemConditionsMappingDto.setItemId(itemDto.getId());
                itemConditionsMappingDto.setCondition(inward.getType());
                itemConditionsMappingDto.setMvnoId(inward.getMvnoId());
                itemConditionMappingRepository.save(itemConditionsMappingMapper.dtoToDomain(itemConditionsMappingDto,new CycleAvoidingMappingContext()));

                ItemWarrantyMappingDto itemWarrantyMappingDto = new ItemWarrantyMappingDto();
                itemWarrantyMappingDto.setItemId(itemDto.getId());
                itemWarrantyMappingDto.setWarranty(itemDto.getWarranty());
                itemWarrantyMappingDto.setMvnoId(inward.getMvnoId());
                itemWarrantyMappingRepository.save(itemWarrantyMappingMapper.dtoToDomain(itemWarrantyMappingDto,new CycleAvoidingMappingContext()));

                entity.setItemId(itemDto.getId());
                InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.save(inOutWardMacMapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
                return inOutWardMacMapper.domainToDTO(inOutWardMACMapping,new CycleAvoidingMappingContext());
            } else {

                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Mac Address Already Exists, It Should Be Unique", null);
            }
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), null);
        }
    }
}
