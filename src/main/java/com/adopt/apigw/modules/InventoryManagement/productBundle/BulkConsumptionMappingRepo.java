package com.adopt.apigw.modules.InventoryManagement.productBundle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulkConsumptionMappingRepo extends JpaRepository<BulkConsumptionMapping,Long> {


    List<BulkConsumptionMapping> findByBulkConsumptionId(Long id);
}
