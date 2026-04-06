package com.adopt.apigw.modules.InventoryManagement.productOwner;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.modules.InventoryManagement.inward.QInward;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.service.postpaid.ChargeService;
import com.adopt.apigw.utils.CommonConstants;
import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductOwnerService extends ExBaseAbstractService<ProductOwnerDto, ProductOwner, Long> {


    @Autowired
    private ProductOwnerRepository productOwnerRepository;
    @Autowired
    ChargeService chargeService;
    @Autowired
    private ProductOwnerMapper productOwnerMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    StaffUserRepository staffUserRepository;


    public ProductOwnerService(ProductOwnerRepository productOwnerRepository, IBaseMapper<ProductOwnerDto, ProductOwner> mapper) {
        super(productOwnerRepository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ProductOwnerService]";
    }

    @Override
    public void deleteEntity(ProductOwnerDto entity) throws Exception {
        super.deleteEntity(entity);
    }

    public ProductOwner saveEntityFromRms(ProductOwnerDto productOwnerDto){
        ProductOwner owner = productOwnerMapper.dtoToDomain(productOwnerDto,new CycleAvoidingMappingContext());
        productOwnerRepository.save(owner);
        return owner;
    }

    public ProductOwnerDto findByProductIdOwnerIdAndOwnerType(Long productId, Long ownerId, String type){
        return productOwnerMapper.domainToDTO(productOwnerRepository.findByProductIdOwnerIdAndOwnerType(productId, ownerId, type), new CycleAvoidingMappingContext());
    }
    public List<ProductOwner> getAvailableQtyDetailsByProductAndDestination(Long productId, Long ownerId, String ownerType) {
        QProductOwner qProductOwner = QProductOwner.productOwner;
        BooleanExpression booleanExpression = qProductOwner.isNotNull()
                .and(qProductOwner.productId.eq(productId))
                .and(qProductOwner.ownerId.eq(ownerId))
                .and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
        return Lists.newArrayList(productOwnerRepository.findAll(booleanExpression));
    }

    public List<ProductOwnerDto> getNonTrackableProductQty(Long productId, Long ownerId, String ownerType) throws Exception {
        try {
            QProductOwner qProductOwner = QProductOwner.productOwner;
            BooleanExpression booleanExpression = qProductOwner.productId.eq(productId).and(qProductOwner.ownerId.eq(ownerId)).and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
            List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(booleanExpression));
            List<ProductOwnerDto> productOwnerDtos = productOwnerMapper.domainToDTO(productOwnerList, new CycleAvoidingMappingContext());
            productOwnerDtos.stream().forEach(r -> {
                r.setProductId(productId);
                r.setProductName(productRepository.findById(r.getProductId()).get().getName());
            });
            return productOwnerDtos;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ProductOwner updateProductOwnerForNonTrackable(Long qty, Long productId, Long ownerId, String ownerType) throws Exception {
        QProductOwner qProductOwner = QProductOwner.productOwner;
        BooleanExpression booleanExpression = qProductOwner.productId.eq(productId).and(qProductOwner.ownerId.eq(ownerId)).and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
        List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(booleanExpression));
        productOwnerList.get(0).setUnusedQty(productOwnerList.get(0).getUnusedQty() - qty);
        productOwnerList.get(0).setUsedQty(productOwnerList.get(0).getUsedQty() + qty);
        productOwnerList = productOwnerRepository.saveAll(productOwnerList);
        return productOwnerList.get(0);
    }

    public ProductOwner updateProductOwnerForNonTrackableAfterReject(Long qty, Long productId, Long ownerId, String ownerType) throws Exception {
        QProductOwner qProductOwner = QProductOwner.productOwner;
        BooleanExpression booleanExpression = qProductOwner.productId.eq(productId).and(qProductOwner.ownerId.eq(ownerId)).and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
        List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(booleanExpression));
        productOwnerList.get(0).setUnusedQty(productOwnerList.get(0).getUnusedQty() + qty);
        productOwnerList.get(0).setUsedQty(productOwnerList.get(0).getUsedQty() - qty);
        productOwnerList = productOwnerRepository.saveAll(productOwnerList);
        return productOwnerList.get(0);
    }
    public ProductOwner updateProductOwnerForSerializedProduct(Long qty, Long productId, Long ownerId, String ownerType) throws Exception {
        QProductOwner qProductOwner = QProductOwner.productOwner;
        StaffUser staffUser = staffUserRepository.findById(Math.toIntExact(ownerId)).get();
        if(staffUser.getPartnerid() != 1) {
            ownerId = Long.valueOf(staffUser.getPartnerid());
            ownerType = CommonConstants.PARTNER;
        }
        BooleanExpression booleanExpression = qProductOwner.productId.eq(productId).and(qProductOwner.ownerId.eq(ownerId)).and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
        List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(booleanExpression));
        productOwnerList.get(0).setUnusedQty(productOwnerList.get(0).getUnusedQty() - qty);
        productOwnerList.get(0).setUsedQty(productOwnerList.get(0).getUsedQty() + qty);
        productOwnerList = productOwnerRepository.saveAll(productOwnerList);
        return productOwnerList.get(0);
    }
    public ProductOwner updateProductOwnerForSerializedProductReject(Long qty, Long productId, Long ownerId, String ownerType) throws Exception {
        QProductOwner qProductOwner = QProductOwner.productOwner;
        StaffUser staffUser = staffUserRepository.findById(Math.toIntExact(ownerId)).get();
        if(staffUser.getPartnerid() != 1) {
            ownerId = Long.valueOf(staffUser.getPartnerid());
            ownerType = CommonConstants.PARTNER;
        }
        BooleanExpression booleanExpression = qProductOwner.productId.eq(productId).and(qProductOwner.ownerId.eq(ownerId)).and(qProductOwner.ownerType.equalsIgnoreCase(ownerType));
        List<ProductOwner> productOwnerList = IterableUtils.toList(productOwnerRepository.findAll(booleanExpression));
        productOwnerList.get(0).setUnusedQty(productOwnerList.get(0).getUnusedQty() + qty);
        productOwnerList.get(0).setUsedQty(productOwnerList.get(0).getUsedQty() - qty);
        productOwnerList = productOwnerRepository.saveAll(productOwnerList);
        return productOwnerList.get(0);
    }
}
