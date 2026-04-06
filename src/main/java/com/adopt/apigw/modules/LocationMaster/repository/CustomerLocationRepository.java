package com.adopt.apigw.modules.LocationMaster.repository;

import com.adopt.apigw.modules.LocationMaster.domain.CustomerLocationMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CustomerLocationRepository  extends JpaRepository<CustomerLocationMapping, Long>, QuerydslPredicateExecutor<CustomerLocationMapping> {
    List<CustomerLocationMapping> findByLocationName(String locationName);

    List<CustomerLocationMapping> findByCustId(Long customerId);

    List<CustomerLocationMapping> findByLocationId(Long locationId);

    List<CustomerLocationMapping> findByLocationIdAndIsActiveAndIsParentLocationAndIsDeleteAndMac(Long locationId , Boolean isActive ,Boolean isParent , Boolean isDelete , String mac);

    boolean existsByLocationIdAndCustId(Long locationId, Long custId);
    CustomerLocationMapping findByCustIdAndLocationId(Long customerId,Long locationId);

    boolean existsAllByCustIdAndLocationIdIn(Long custId, Set<Long> locationId);

    boolean existsAllByLocationIdIn(Set<Long> locationId);

    @Query("select cl.locationId from CustomerLocationMapping cl where cl.custId=:custId")
    List<Long> findAllLocationIdsByCustomer(Long custId);
    boolean existsByLocationIdAndMacAndIsParentLocationAndMvnoId(Long locationId, String mac, Boolean isParentLocation, Integer mvnoId);

    @Query("SELECT clm.custId FROM CustomerLocationMapping clm WHERE LOWER(clm.locationName) = LOWER(:locationName) OR LOWER(clm.locationName) LIKE %:locationName%")
    List<Long> findByLocationNameExactOrContainingIgnoreCase(@Param("locationName") String locationName);


    @Query("SELECT new com.adopt.apigw.modules.LocationMaster.domain.CustomerLocationMapping( " +
            "c.isParentLocation, c.locationId, c.mac, c.locationName) " +
            "FROM CustomerLocationMapping c WHERE c.custId = :custId")
    List<Object[]> findAllByCustomerId(@Param("custId") Long custId);
}
