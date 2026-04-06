package com.adopt.apigw.repository.postpaid;


import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.pojo.api.PlanServiceCustomDto;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@JaversSpringDataAuditable
@Repository
public interface PlanServiceRepository extends JpaRepository<PlanService, Integer>  , QuerydslPredicateExecutor<PlanService> {

    @Query(value = "select * from TBLMSERVICES where lower(servicename) like '%' :search  '%' order by serviceid AND MVNOID= :MVNOID OR MVNOID IS NULL",
            countQuery = "select count(*) from TBLMSERVICES where lower(servicename) like '%' :search '%' AND MVNOID= :MVNOID OR MVNOID IS NULL",
            nativeQuery = true)
    Page<PlanService> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOID") Integer mvnoId);

    @Query("Select t from PlanService t where mvnoId in :mvnoIds")
    Page<PlanService> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query("Select t from PlanService t where MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds)")
    Page<PlanService> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from TBLMSERVICES  where servicename=:name and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from TBLMSERVICES  where servicename=:name and (MVNOID=1 or (MVNOID=:mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from TBLMSERVICES  where servicename=:name and iccode=:iccode and icname=:icname", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("icname") String icname, @Param("iccode") String iccode);


    @Query(value = "select count(*) from TBLMSERVICES  where servicename=:name and serviceid=:id", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select count(*) from TBLMSERVICES  where iccode=:iccode and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSaveICCode(@Param("iccode") String iccode, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from TBLMSERVICES  where iccode=:iccode", nativeQuery = true)
    Integer duplicateVerifyAtSaveICCode(@Param("iccode") String iccode);

    @Query(value = "select count(*) from TBLMSERVICES  where iccode=:iccode and (MVNOID=1 or (MVNOID=:mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSaveICCode(@Param("iccode") String iccode, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);


//    @Query(value = "select count(*) from TBLMSERVICES  where icname=:icname and MVNOID in :mvnoIds", nativeQuery = true)
//    Integer duplicateVerifyAtSaveICName(@Param("icname") String icname, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from TBLMSERVICES  where icname=:icname", nativeQuery = true)
    Integer duplicateVerifyAtSaveICName(@Param("icname") String icname);


    @Query(value = "select count(*) from TBLMSERVICES  where icname=:icname and (MVNOID = 1 or (MVNOID=:mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSaveICName(@Param("icname") String icname, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from TBLMSERVICES  where servicename=:name", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from TBLMSERVICES  where icname=:icname and iccode=:iccode", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("icname") String icname, @Param("iccode") String iccode);

    @Query(value = "select count(*) from TBLMSERVICES  where servicename=:name and  iccode=:iccode and icname=:icname and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("iccode") String iccode, @Param("icname") String icname, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from TBLMSERVICES  where servicename=:name and iccode=:iccode and icname=:icname and (MVNOID = 1 or (MVNOID=:mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("iccode") String iccode, @Param("icname") String icname, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);


    @Query(value = "select count(*) from TBLMSERVICES  where icname=:icname and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSaveICName(@Param("icname") String icname, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from TBLMSERVICES  where icname=:icname and iccode=:iccode and serviceid=:id", nativeQuery = true)
    Integer duplicateVerifyAtEditICCodeAndIcName(@Param("icname") String icname, @Param("iccode") String iccode, @Param("id") Integer id);

    @Query(value = "select count(*) from TBLMSERVICES where icname=:icname and iccode=:iccode and  serviceid=:id and MVNOID=:mvnoId ", nativeQuery = true)
    Integer duplicateVerifyAtEditICCodeAndIcName(@Param("icname") String icname, @Param("iccode") String iccode, @Param("id") Integer id, @Param("mvnoId") List mvnoId);

    @Query(value = "select count(*) from TBLMSERVICES where icname=:icname and iccode=:iccode and serviceid=:id  and (MVNOID=1 or (MVNOID=:mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEditICCodeAndIcName(@Param("icname") String icname, @Param("iccode") String iccode, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from TBLMSERVICES  where servicename=:name and  serviceid =:id and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from TBLMSERVICES  where servicename=:name and serviceid =:id and (MVNOID = 1 or (MVNOID=:mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from TBLMSERVICES  where icname=:icname and serviceid =:id", nativeQuery = true)
    Integer duplicateVerifyAtEditICName(@Param("icname") String icname, @Param("id") Integer id);

    @Query(value = "select count(*) from TBLMSERVICES  where icname=:icname and serviceid =:id and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEditICName(@Param("icname") String icname, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);


    @Query(value = "select count(*) from TBLMSERVICES  where icname=:icname and serviceid =:id and (MVNOID = 1 or (MVNOID=:mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEditICName(@Param("icname") String icname, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);


    @Query(value = "select count(*) from TBLMSERVICES  where iccode=:iccode and serviceid =:id", nativeQuery = true)
    Integer duplicateVerifyAtEditICCode(@Param("iccode") String iccode, @Param("id") Integer id);

    @Query(value = "select count(*) from TBLMSERVICES  where iccode=:iccode and serviceid =:id and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEditICCode(@Param("iccode") String iccode, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);


    @Query(value = "select count(*) from TBLMSERVICES  where iccode=:iccode and serviceid =:id and (MVNOID = 1 or (MVNOID=:mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEditICCode(@Param("iccode") String iccode, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);


    @Query(value = "Select t from PlanService t where name = :serviceName", nativeQuery = true)
    PlanService findByName(@Param("serviceName") String serviceName);


    List<PlanService> findAllByName(@Param("planServiceName") String planServiceName);


    PlanService findAllByIdIn(@Param("planServices") List<PlanService> planServices);

    PlanService findAllByNameEqualsAndMvnoIdIn(String serviceNames, List<Integer> mvnoId);
    @Query(value = "SELECT DISTINCT ps.* " +
            "FROM TBLMSERVICES ps " +
            "JOIN tblmpostpaidplan pp ON pp.serviceid = ps.serviceid " +
            "JOIN tblplanservicearearel psl ON psl.planid = pp.POSTPAIDPLANID " +
            "WHERE psl.serviceareaid IN (:serviceAreaId)", nativeQuery = true)
    List<PlanService> findAllByServiceAreaId(@Param("serviceAreaId") List<Integer> serviceAreaId);

    @Query("SELECT p.isServiceThroughLead FROM PlanService p WHERE p.id = :id")
    Boolean findIsServiceThroughLeadById(@Param("id") Integer id);

    @Query(
            "SELECT p.id, p.name, p.displayName, p.mvnoId, " +
                    "       spm.id, spm.serviceid, spm.serviceParamName, spm.serviceParamId, p.mvnoName " +
                    "  FROM PlanService p " +
                    "  LEFT JOIN p.serviceParamMappingList spm " +
                    " WHERE p.isDeleted = false " +
                    "   AND (:mvnoId = 1 OR p.mvnoId IN :mvnoIds) " +
                    "   AND (:buIds IS NULL OR p.buId IN :buIds)"
    )
    List<Object[]> fetchFlatPlanServiceData(
            @Param("mvnoId") Integer mvnoId,
            @Param("mvnoIds") List<Integer> mvnoIds,
            @Param("buIds")  List<Long>    buIds
    );





}
