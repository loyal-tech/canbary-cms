package com.adopt.apigw.modules.LocationMaster.repository;

import com.adopt.apigw.modules.LocationMaster.domain.LocationMaster;
import com.adopt.apigw.modules.LocationMaster.domain.LocationMasterMapping;
import com.adopt.apigw.modules.LocationMaster.module.LocationMasterMappingDto;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface LocationMasterMappingRepository extends JpaRepository<LocationMasterMapping, Long>, QuerydslPredicateExecutor<LocationMasterMapping> {


    List<LocationMasterMapping>  findAllByMac(String mac);
    boolean existsByMac(String mac);


    @Query(value = "select count(*) from tblmlocationmastermapping t where locationid!=:locationId and mac=:mac", nativeQuery = true)
    int existsByMacAndLocationMasterNotIn(@Param("locationId") Long locationId,@Param("mac") String mac);

    List<LocationMasterMapping> findAllByLocationMasterId(Long locationMasterId);

    @Modifying
    @Query("delete from LocationMasterMapping lm where lm.locationMasterId=:locationId")
    void deleteLocationMacMapping(@Param("locationId") Long locationId);

    @Query(value = "select * from tblmlocationmastermapping t where locationid=:locationId and mac=:mac", nativeQuery = true)
    List<LocationMasterMapping> findAllByLocationMasterAndMac(@Param("locationId") Long locationId, @Param("mac") String mac);
}
