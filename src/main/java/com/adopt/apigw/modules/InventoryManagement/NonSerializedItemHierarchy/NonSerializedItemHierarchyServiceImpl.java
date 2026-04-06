package com.adopt.apigw.modules.InventoryManagement.NonSerializedItemHierarchy;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItem.NonSerializedItem;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItem.NonSerializedItemRepository;
import com.adopt.apigw.modules.InventoryManagement.NonSerializedItem.QNonSerializedItem;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.product.ProductRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NonSerializedItemHierarchyServiceImpl extends ExBaseAbstractService<NonSerializedItemHierarchyDto, NonSerializedItemHierarchy, Long> {

    @Autowired
    NonSerializedItemRepository nonSerializedItemRepository;

    @Autowired
    NonSerializedItemHierarchyRepository nonSerializedItemHierarchyRepository;

    @Autowired
    ProductRepository productRepository;
    public NonSerializedItemHierarchyServiceImpl(NonSerializedItemHierarchyRepository repository, IBaseMapper<NonSerializedItemHierarchyDto, NonSerializedItemHierarchy> mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[NonSerializedItemHierarchyServiceImpl]";
    }

    public void updateNonSerializedItemHierarchy(Long inwardId, Long productId) throws Exception {
        try {
            Product product = productRepository.findById(productId).get();
            boolean hasSerial = product.getProductCategory().isHasSerial();
            boolean isTrackable = product.getProductCategory().isHasTrackable();
            if (!hasSerial && isTrackable) {
                QNonSerializedItem qNonSerializedItem = QNonSerializedItem.nonSerializedItem;
                BooleanExpression booleanExpressionNonSerializedItem = qNonSerializedItem.isDeleted.eq(false).and(qNonSerializedItem.currentInwardId.eq(inwardId));
                List<NonSerializedItem> nonSerializedItemList = IterableUtils.toList(nonSerializedItemRepository.findAll(booleanExpressionNonSerializedItem));
                QNonSerializedItemHierarchy qNonSerializedItemHierarchy = QNonSerializedItemHierarchy.nonSerializedItemHierarchy;
                for (int i = 0; i < nonSerializedItemList.size(); i++) {
                    BooleanExpression booleanExpressionNonSerializedItemHierarchy = qNonSerializedItemHierarchy.isDeleted.eq(false).and(qNonSerializedItemHierarchy.childItemId.eq(nonSerializedItemList.get(i).getId()));
                    List<NonSerializedItemHierarchy> nonSerializedItemHierarchyList = IterableUtils.toList(nonSerializedItemHierarchyRepository.findAll(booleanExpressionNonSerializedItemHierarchy));
                    for (int j = 0; j < nonSerializedItemHierarchyList.size(); j++) {
                        nonSerializedItemHierarchyList.get(j).setIsDeleted(true);
                        nonSerializedItemHierarchyRepository.save(nonSerializedItemHierarchyList.get(j));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
