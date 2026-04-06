package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.BranchServiceAreaMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchServiceAreaMappingRepository extends JpaRepository<BranchServiceAreaMapping, Long>, QuerydslPredicateExecutor<BranchServiceAreaMapping> {

    List<BranchServiceAreaMapping> findAllByBranchId(Integer branchId);
    List<BranchServiceAreaMapping> findAllByServiceareaIdIn(List<Integer> serviceAreaId);

    @Query(value = "select * from adoptconvergebss.tblmbranchservicearearel t where servicearea_id =:serviceAreaid", nativeQuery = true)
    List<BranchServiceAreaMapping> findAllByServiceareaId(@Param("serviceAreaid") Long serviceAreaid);

    @Query(value = "select * from adoptconvergebss.tblmbranchservicearearel t where servicearea_id in (:serviceAreaids)", nativeQuery = true)
    List<BranchServiceAreaMapping> findAllByServiceareaId(@Param("serviceAreaids") List<Integer> serviceAreaids);

    @Query(value = "select branchServiceAreaMapping.serviceareaId\n" +
            "from BranchServiceAreaMapping branchServiceAreaMapping")
    List<Integer> serviceAreaIdListWhereBranchIsBind();

    @Query(value  = "select branchServiceAreaMapping.serviceareaId\n" +
            "from BranchServiceAreaMapping branchServiceAreaMapping where branchId =:branchId")
    List<Integer> getAllServiceAreaIdsWithBranchId(@Param("branchId")Integer branchId);


    List<BranchServiceAreaMapping> findAllByBranchIdIn(List<Integer> branchIds);

    @Query("select bsa.branchId from BranchServiceAreaMapping bsa where bsa.serviceareaId=:serviceareaId")
    List<Integer> findBranchByServiceareaId(Integer serviceareaId);

}
