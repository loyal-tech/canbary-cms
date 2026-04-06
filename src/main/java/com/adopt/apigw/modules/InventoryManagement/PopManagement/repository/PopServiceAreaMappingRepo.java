package com.adopt.apigw.modules.InventoryManagement.PopManagement.repository;

import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.PopServiceAreaMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PopServiceAreaMappingRepo extends JpaRepository<PopServiceAreaMapping,Long> {

        List<PopServiceAreaMapping> findAllByServiceIdIn(List<Integer> serviceAreaId);
}
