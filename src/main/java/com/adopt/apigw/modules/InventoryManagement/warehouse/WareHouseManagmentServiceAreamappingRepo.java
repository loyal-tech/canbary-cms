package com.adopt.apigw.modules.InventoryManagement.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WareHouseManagmentServiceAreamappingRepo extends JpaRepository<WareHouseServiceAreaMapping,Long> {

    List<WareHouseServiceAreaMapping> findAllByServiceIdIn(List<Integer> serviceAreaId);
}
