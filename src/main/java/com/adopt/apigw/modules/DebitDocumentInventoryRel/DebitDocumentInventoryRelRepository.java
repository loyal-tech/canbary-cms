package com.adopt.apigw.modules.DebitDocumentInventoryRel;

import com.adopt.apigw.model.postpaid.DebitDocumentInventoryRel;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface DebitDocumentInventoryRelRepository extends JpaRepository<DebitDocumentInventoryRel , Integer> , QuerydslPredicateExecutor<DebitDocumentInventoryRel> {
    DebitDocumentInventoryRel findByCustInventoryMappingId(Long custInventoryMappingId );


}
