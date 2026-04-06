package com.adopt.apigw.modules.BuildingMgmt.Repository;

import com.adopt.apigw.modules.BuildingMgmt.Domain.BuildingManagement;
import com.adopt.apigw.modules.BuildingMgmt.Domain.BuildingMapping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface BuildingMappingRepository extends JpaRepository<BuildingMapping,Long>, QuerydslPredicateExecutor<BuildingMapping> {

    @Modifying
    @Transactional
    @Query("DELETE FROM BuildingMapping bm WHERE bm.id IN :ids")
    void deleteByIds(@Param("ids") List<Long> ids);

    List<BuildingMapping>findAllByBuildingManagement(BuildingManagement buildingManagement);


}
