package com.adopt.apigw.modules.InventoryManagement.ExternalItemMacSerialMapping;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.model.postpaid.QCustMacMappping;
import com.adopt.apigw.modules.CommonList.utils.TypeConstants;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingDto;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain.ExternalItemManagement;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.model.ExternalItemManagementDTO;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.repository.ExternalItemManagementRepository;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.service.ExternalItemManagementService;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.*;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardDto;
import com.adopt.apigw.modules.InventoryManagement.inward.InwardServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import com.adopt.apigw.modules.InventoryManagement.item.ItemDto;
import com.adopt.apigw.modules.InventoryManagement.item.ItemRepository;
import com.adopt.apigw.modules.InventoryManagement.item.ItemServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionMappingServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionsMapping;
import com.adopt.apigw.modules.InventoryManagement.itemConditionMapping.ItemConditionsMappingDto;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMapping;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingDto;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingRepository;
import com.adopt.apigw.modules.InventoryManagement.itemWarranty.ItemWarrantyMappingServiceImpl;
import com.adopt.apigw.modules.InventoryManagement.outward.Outward;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.product.ProductServiceImpl;
import com.adopt.apigw.pojo.api.RecordPaymentPojo;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.service.postpaid.CustMacMapppingService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class ExternalItemMacSerialMappingService extends ExBaseAbstractService<ExternalItemMacSerialMappingDTO, ExternalItemMacSerialMapping, Long> {

    @Autowired
    public ExternalItemMacSerialMappingRepo externalItemMacSerialMappingRepo;

    @Autowired
    public ExternalItemManagementRepository externalItemManagementRepository;

    @Autowired
    public ExternalItemManagementService externalItemManagementService;

    @Autowired
    public CustomerInventoryMappingService customerInventoryMappingService;

    @Autowired
    public CustMacMapppingService custMacMapppingService;

    @Autowired
    public CreditDocService creditDocService;

    @Autowired
    public ProductServiceImpl productService;

    @Autowired
    public ItemServiceImpl itemService;

    @Autowired
    public ItemRepository itemRepository;

    @Autowired
    public ItemConditionMappingRepository itemConditionMappingRepository;

    @Autowired
    public ItemWarrantyMappingRepository itemWarrantyMappingRepository;

    @Autowired
    InOutWardMacRepo inOutWardMacRepo;

    @Autowired
    public ItemWarrantyMappingServiceImpl itemWarrantyMappingService;

    @Autowired
    public ItemConditionMappingServiceImpl itemConditionMappingService;

    public ExternalItemMacSerialMappingService(ExternalItemMacSerialMappingRepo externalItemMacSerialMappingRepo, ExternalItemMacSerialMappingMapper externalItemMacSerialMappingMapper) {
        super(externalItemMacSerialMappingRepo, externalItemMacSerialMappingMapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ExternalItemMacSerialMappingService]";
    }

    //Get List By External Item Id
    List<ExternalItemMacSerialMapping> getByExternalItemId(Long externalItemId) {
        QExternalItemMacSerialMapping qExternalItemMacSerialMapping = QExternalItemMacSerialMapping.externalItemMacSerialMapping;
        ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(externalItemId).get();
        if (externalItemId != null) {
            BooleanExpression booleanExpression = qExternalItemMacSerialMapping.isNotNull().and(qExternalItemMacSerialMapping.externalItemId.eq(externalItemId)).and(qExternalItemMacSerialMapping.isDeleted.eq(false)).and(qExternalItemMacSerialMapping.custInventoryMappingId.isNull());
            return IterableUtils.toList(externalItemMacSerialMappingRepo.findAll(booleanExpression));
        } else {
            return null;
        }
    }

    //Get all MacMapping By External Item Id
//    List<ExternalItemMacSerialMapping> getAllMACMappingByExternalItemId(Long externalItemId) {
//        QExternalItemMacSerialMapping qExternalItemMacSerialMapping = QExternalItemMacSerialMapping.externalItemMacSerialMapping;
//        BooleanExpression booleanExpression = qExternalItemMacSerialMapping.isNotNull().and(qExternalItemMacSerialMapping.isDeleted.eq(false)).and(qExternalItemMacSerialMapping.custInventoryMappingId.isNull());
//        return IterableUtils.toList(externalItemMacSerialMappingRepo.findAll(booleanExpression));
//    }

    @Override
    public ExternalItemMacSerialMappingDTO saveEntity(ExternalItemMacSerialMappingDTO entity) throws Exception {
        try {
            if (entity.getExternalItemId() != null) {
                boolean flag = true;
                if (entity.getMacAddress() != null) {
                    InOutWardMACService inOutWardMACService = SpringContext.getBean(InOutWardMACService.class);
                    flag = inOutWardMACService.duplicateVerifyAtSave(entity.getMacAddress());
                }
                if (flag) {
                    // TODO: pass mvnoID manually 6/5/2025
//                    entity.setMvnoId(getMvnoIdFromCurrentStaff(null));
                    ExternalItemManagementService externalItemManagementService = SpringContext.getBean(ExternalItemManagementService.class);
                    ExternalItemManagementDTO externalItemManagementDTO = externalItemManagementService.getEntityForUpdateAndDelete(entity.getExternalItemId(),entity.getMvnoId());
                    externalItemManagementDTO.setTotalMacSerial(externalItemManagementDTO.getTotalMacSerial() + 1);
                    externalItemManagementService.updateEntity(externalItemManagementDTO);
                    ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(entity.getExternalItemId()).get();
                    // Save Item Entity By External Item Entity
                    Item item = new Item();
                    item.setMacAddress(entity.getMacAddress());
                    item.setSerialNumber(entity.getSerialNumber());
                    item.setName(externalItemManagement.getProductId().getName());
                    item.setCondition(externalItemManagement.getOwnershipType());
                    item.setMvnoId(externalItemManagement.getMvnoId());
                    item.setOwnerId(null);
                    item.setOwnerId(externalItemManagement.getServiceAreaId().getId());
                    item.setOwnerType("ServiceArea");
                    item.setItemStatus(CommonConstants.UNALLOCATED);
                    item.setCurrentInwardType(null);
                    item.setCurrentInwardId(null);
                    item.setExternalItemId(externalItemManagement.getId());
                    item.setProductId(externalItemManagement.getProductId().getId());
                    item.setOwnershipType(externalItemManagement.getOwnershipType());

                    Item item1 = null;
                    item1 = itemRepository.save(item);
                    ItemConditionsMapping itemConditionsMapping = new ItemConditionsMapping();
                    itemConditionsMapping.setItemId(item1.getId());
                    itemConditionsMapping.setCondition(externalItemManagement.getOwnershipType());
                    itemConditionsMapping.setMvnoId(externalItemManagement.getMvnoId());
                    itemConditionMappingRepository.save(itemConditionsMapping);
                    //itemConditionMappingService.saveEntity(itemConditionsMappingDto);

                    ItemWarrantyMapping itemWarrantyMapping = new ItemWarrantyMapping();
                    itemWarrantyMapping.setItemId(item1.getId());
                    itemWarrantyMapping.setWarranty(item1.getWarranty());
                    itemWarrantyMapping.setMvnoId(externalItemManagement.getMvnoId());
                    itemWarrantyMappingRepository.save(itemWarrantyMapping);

                    // Save InoutMac Mapping Entity By ExternalItem
                    InOutWardMACMapping inOutWardMACMapping = new InOutWardMACMapping();
                    inOutWardMACMapping.setMacAddress(entity.getMacAddress());
                    inOutWardMACMapping.setSerialNumber(entity.getSerialNumber());
                    inOutWardMACMapping.setStatus(externalItemManagement.getStatus());
                    inOutWardMACMapping.setIsForwarded(0);
                    inOutWardMACMapping.setExternalItemId(externalItemManagement.getId());
                    inOutWardMACMapping.setItemId(item1.getId());
                    inOutWardMacRepo.save(inOutWardMACMapping);
                    entity.setItemId(item1.getId());
                    return super.saveEntity(entity);
                }
                else {
                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Duplicate Mac Exists Already", null);
                }
            }
        } catch (CustomValidationException ce) {
            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), ce.getMessage(), null);
        }
        return null;
    }

//    @Transactional
//    public void deleteExternalItemMac(Long itemId) {
//        Item item = itemRepository.findById(itemId).get();
//        item.setIsDeleted(true);
//        itemRepository.save(item);
//        InOutWardMACMapping inOutWardMACMapping = inOutWardMacRepo.findByItemId(itemId);
//        if (!Objects.isNull(inOutWardMACMapping)) {
//            inOutWardMACMapping.setIsDeleted(true);
//            inOutWardMacRepo.save(inOutWardMACMapping);
//        }
//        QExternalItemMacSerialMapping qExternalItemMacSerialMapping = QExternalItemMacSerialMapping.externalItemMacSerialMapping;
//        BooleanExpression booleanExpression = qExternalItemMacSerialMapping.itemId.eq(itemId);
//        List<ExternalItemMacSerialMapping> externalItemMacSerialMapping = IterableUtils.toList(externalItemMacSerialMappingRepo.findAll(booleanExpression));
//        for (int i = 0; i < externalItemMacSerialMapping.size(); i++) {
//            externalItemMacSerialMapping.get(0).setIsDeleted(true);
//            externalItemMacSerialMappingRepo.save(externalItemMacSerialMapping.get(0));
//        }
//        ExternalItemManagement externalItemManagement = externalItemManagementRepository.findById(externalItemMacSerialMapping.get(0).getExternalItemId()).get();
//        externalItemManagement.setTotalMacSerial(externalItemManagement.getTotalMacSerial() - 1);
//        externalItemManagementRepository.save(externalItemManagement);
//    }
}
