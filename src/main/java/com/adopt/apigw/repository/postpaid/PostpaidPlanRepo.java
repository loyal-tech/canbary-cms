package com.adopt.apigw.repository.postpaid;


import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.Customers.LightPostpaidPlanDTO;
import com.adopt.apigw.pojo.api.postpaidPlanFetchPojo;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@JaversSpringDataAuditable
@Repository
@Transactional
public interface PostpaidPlanRepo extends JpaRepository<PostpaidPlan, Integer>, QuerydslPredicateExecutor<PostpaidPlan> {

    @Query(value = "select * from TBLMPOSTPAIDPLAN where lower(name) like '%' :search  '%' order by POSTPAIDPLANID AND  MVNOID= :MVNOID OR MVNOID IS NULL",
            countQuery = "select count(*) from TBLMPOSTPAIDPLAN where lower(name) like '%' :search '%' AND  MVNOID= :MVNOID OR MVNOID IS NULL",
            nativeQuery = true)
    Page<PostpaidPlan> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOID") Integer mvnoId);

    List<PostpaidPlan> findByStatusAndPlantype(String status, String planType);

    List<PostpaidPlan> findAllByStatusAndPlantypeAndServiceIdAndPlanGroup(String status, String planType, Integer serviceId, String planGroup);

    @Query("SELECT plan.name FROM PostpaidPlan plan where plan.id = :id")
    String findNameById(@Param("id") Integer id);

    @Query("select t from PostpaidPlan t where t.isDelete=false")
    List<PostpaidPlan> findAll();

    @Query(nativeQuery = true, value = "select * from tblmpostpaidplan t where t.is_delete = 0", countQuery = "select count(*) from tblmpostpaidplan t where t.is_delete = 0")
    Page<PostpaidPlan> findAll(Pageable pageable);

    @Query(nativeQuery = true, value = "select * from tblmpostpaidplan t where t.is_delete = 0 and MVNOID in :mvnoIds", countQuery = "select count(*) from tblmpostpaidplan t where t.is_delete = 0 and MVNOID in :mvnoIds")
    Page<PostpaidPlan> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);


    @Query("update PostpaidPlan b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    @Query(value = "select count(*) from tblcustpackagerel t where t.planid=:id", nativeQuery = true)
    Integer deleteverified(@Param("id") Integer id);


    @Query(value = "select count(*) from tblmpostpaidplan m where m.NAME=:name and m.is_delete=false and m.MVNOID in :mvnoIds ", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmpostpaidplan m where m.NAME=:name and m.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmpostpaidplan m where m.NAME=:name and m.POSTPAIDPLANID =:id and m.is_delete=false and m.MVNOID in :mvnoIds ", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmpostpaidplan m where m.NAME=:name and m.POSTPAIDPLANID =:id and m.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblmpostpaidplan m where m.NAME=:name and m.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmpostpaidplan m where m.NAME=:name and m.POSTPAIDPLANID =:id and m.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select CREATEDBYSTAFFID from tblmpostpaidplan where NAME=:name and is_delete=false and MVNOID=:mvnoId and BUID in :buIds", nativeQuery = true)
    Integer getCreatedBy(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select CREATEDBYSTAFFID from tblmpostpaidplan where NAME=:name and is_delete=false and MVNOID=:mvnoId", nativeQuery = true)
    Integer getCreatedBy(@Param("name") String name, @Param("mvnoId") Integer mvnoId);

    @Query(value = "select * from tblmpostpaidplan where serviceid=:serviceid and STATUS=:STATUS and (plangroup='Registration and Renewal' OR plangroup='Registration') and plantype=:plantype", nativeQuery = true)
    List<PostpaidPlan> findAllByServiceIdAndStatusAndPlanGroup(@Param("serviceid") Integer serviceid, @Param("STATUS") String STATUS, @Param("plantype") String plantype);

    List<PostpaidPlan> findPostpaidPlanByServiceId(Integer id);

    List<PostpaidPlan> findAllByIsDeleteIsFalseOrderByIdDesc();


    @Query(value = "select count(*) from tblcustomercafassignment t where t.staff_id =:s1", nativeQuery = true)
    Long findMinimumApprovalReuqestForPlanByStaff(@Param("s1") Integer staffId);

    Optional<PostpaidPlan> findById(@Param("postPaidPlanId") Integer postPaidPlanId);

    //    @Query(value = "select POSTPAIDPLANID, NAME from TBLMPOSTPAIDPLAN t where t.POSTPAIDPLANID IN (:ids)", nativeQuery = true)
    List<PostpaidPlan> findAllByIdIn(List<Integer> ids);

    Boolean existsByIdInAndServiceId(List<Integer> ids, Integer serviceId);

    List<PostpaidPlan> findAllByStatusAndIsDeleteFalseAndIdIn(String active, List<Integer> leadplanIds);

    @Query("SELECT plan.serviceId FROM PostpaidPlan plan where plan.id in :ids")
    List<Long> findServiceIdByPlanId(@Param("ids") Set<Integer> ids);

    @Query("SELECT plan.serviceId FROM PostpaidPlan plan WHERE plan.id = :ids")
    Integer findServiceIdByPlanId(@Param("ids") Integer ids);

    /**
     * select t.POSTPAIDPLANID , t.NAME as planName, t.offerprice  , tqp.qosspeed, t3.servicename  from tblmpostpaidplan t
     * left join tblplanservicearearel t2 on t.POSTPAIDPLANID = t2.planid
     * left join tbl_qos_policy tqp on tqp.id = t.qospolicy_id
     * left join tblmservices t3 on t3.serviceid = t.serviceid
     * where t2.serviceareaid in (2,1) and t3.serviceid = 1;
     * pp.id, pp.offerprice, pp.name, qp.qosspeed, qp.qosspeed, ps.name
     * (Integer id, String serviceName, String name, Double offerprice, Integer mvnoId, String uploadSpeed, String downloadSpeed)
     */
    @Query("SELECT DISTINCT new com.adopt.apigw.modules.Customers.LightPostpaidPlanDTO(pp.id, ps.name, pp.name, pp.offerprice, pp.mvnoId, qp.qosspeed, qp.qosspeed,mv.profileImage,mv.logo_file_name, pp.plantype, ps.id, pp.validity, pp.unitsOfValidity) from PostpaidPlan pp " +
            "left join PostPaidPlanServiceAreaMapping psm on pp.id = psm.planId " +
            "left join QOSPolicy qp on qp.id=pp.qospolicy.id " +
            "left join PlanService ps on ps.id=pp.serviceId " +
            "left join Mvno mv on mv.id=pp.mvnoId " +
            "where psm.serviceId IN (:serviceAreaId) and ps.id IN (:serviceId) and pp.planGroup IN (:planGroupTypes) and mv.status = 'Active' and pp.status='Active'")
    List<LightPostpaidPlanDTO> findAllByServiceAreaIdsAndService(@Param(value = "serviceAreaId") List serviceAreaId, @Param(value = "serviceId") List serviceId, @Param(value = "planGroupTypes") List planGroupTypes);

    Optional<PostpaidPlan> findAllByNameEqualsAndMvnoIdIn(String name, List<Integer> mvnoId);

    @Query("SELECT plan.planGroup FROM PostpaidPlan plan where plan.id = :id")
    String findPlangroupById(@Param("id") Integer id);

    @Query("SELECT new com.adopt.apigw.modules.Customers.LightPostpaidPlanDTO(pp.id, ps.name, pp.name, pp.offerprice, pp.mvnoId, qp.qosspeed, qp.qosspeed, mv.profileImage, mv.logo_file_name, pp.plantype, ps.id, pp.validity, pp.unitsOfValidity, pp.param1, pp.param2) " +
            "from PostpaidPlan pp " +
            "left join QOSPolicy qp on qp.id=pp.qospolicy.id " +
            "left join PlanService ps on ps.id=pp.serviceId " +
            "left join Mvno mv on mv.id=pp.mvnoId " +
            "where ps.id IN (:serviceId) and pp.planGroup IN (:planGroupTypes) and mv.status = 'Active' and pp.status='Active' " +
            "and pp.isDelete = false and pp.param1 IS NOT NULL and pp.param1 <> '' " +
            "and pp.param2 IS NOT NULL and pp.param2 <> ''")
    List<LightPostpaidPlanDTO> findAllByService(@Param(value = "serviceId") List serviceId, @Param(value = "planGroupTypes") List planGroupTypes);

    List<PostpaidPlan> findPostpaidPlanByname(String name);

    @Query("SELECT p.id, p.planGroup, p.quotaUnit FROM PostpaidPlan p WHERE p.name = :name")
    List<Object[]> findPostpaidPlanByName(@Param("name") String name);

    PostpaidPlan findPostpaidPlanBynameAndMvnoId(String name, Integer mvnoId);

    @Query("SELECT plan.id, plan.serviceId, plan.validity, plan.plantype FROM PostpaidPlan plan where plan.name = :name")
    List<Object[]> findPostpaidPlansByname(String name);

    @Query("SELECT p.quotaResetInterval FROM PostpaidPlan p WHERE p.id = :planId")
    String findQuotaResetIntervalByPlanId(@Param("planId") Integer planId);


    @Query("SELECT p FROM PostpaidPlan p JOIN FETCH p.qospolicy WHERE p.isDelete = false")
    List<PostpaidPlan> findAllPlansWithQosPolicy();

    @Query("SELECT DISTINCT p FROM PostpaidPlan p " +
            "JOIN FETCH p.qospolicy " +
            "WHERE p.isDelete = false " +
            "AND p.status = :status " +
            "AND p.mvnoId IN :mvnoIds")
    List<PostpaidPlan> findAllActivePlansWithQosPolicyByMvnoIds(
            @Param("status") String status,
            @Param("mvnoIds") List<Integer> mvnoIds
    );

    @Query("SELECT DISTINCT p FROM PostpaidPlan p " +
            "JOIN FETCH p.qospolicy " +
            "WHERE p.isDelete = false " +
            "AND p.status = :status ")
    List<PostpaidPlan> findAllActivePlansWithQosPolicy(
            @Param("status") String status
            );

    @Query("SELECT new PostpaidPlan(p.id, p.displayName, p.plantype, p.validity, p.unitsOfValidity, " +
            "p.startDate, p.endDate, p.category, p.newOfferPrice, p.offerprice, " +
            "p.taxamount, p.status, p.quotatype, p.createdByName, p.mvnoName, p.nextStaff, p.currency, p.planGroup) " +
            "FROM PostpaidPlan p WHERE p.isDelete = false AND p.status <> 'Rejected'")
    Page<PostpaidPlan> findAllFilteredPlans(BooleanExpression booleanExpression, Pageable pageable);

    @Query("SELECT p.id FROM PostpaidPlan p " +
            "WHERE p.isDelete = false AND p.status = :status " +
            "AND p.name = :packageName")
    List<Integer> findActivePlansWithQosPolicy(@Param("packageName") String packageName,
                                               @Param("status") String status);

    @Query("SELECT plan.offerprice FROM PostpaidPlan plan where plan.id = :planId")
    Double findPlanPriceByCustId(@Param("planId") Integer planId);

    @Query(value = "select p.NAME from TBLMPOSTPAIDPLAN p where p.POSTPAIDPLANID = :id",nativeQuery = true)
    String findPlanNameByPlanId(@Param("id") Integer id);
    @Query("SELECT new PostpaidPlan (plan.id,plan.name,plan.plantype,plan.maxHoldDurationDays,plan.maxHoldAttempts)FROM PostpaidPlan plan where plan.id = :planId")
    Optional<PostpaidPlan> getPlanDetailsById(Integer planId);



    @Query("SELECT p.id, p.planGroup, p.status, p.serviceId ,p.displayName FROM PostpaidPlan p WHERE p.name = :name")
    List<Object[]> findPostpaidPlanDetailsByName(@Param("name") String name);


    @Query("SELECT new com.adopt.apigw.pojo.api.postpaidPlanFetchPojo(" +
            "e.id, e.name, e.displayName, e.code, e.desc, e.category, " +
            "e.startDate, e.endDate, e.allowOverUsage, e.quotaUnit, e.quota, e.planStatus, " +
            "e.childQuota, e.childQuotaUnit, e.mvnoId, e.status, e.serviceId, " +
            "e.timebasepolicyId, e.plantype, e.dbr, e.planGroup, e.validity, e.maxconcurrentsession, " +
            "e.quotaResetInterval, e.mode, e.unitsOfValidity, e.newOfferPrice, e.allowdiscount, " +
            "e.basePlan, e.useQuota, e.mvnoName, e.usageQuotaType, e.taxamount) " +
            "FROM PostpaidPlan e " +
            "WHERE e.id IN :planIds")
    List<postpaidPlanFetchPojo> findAllPlanDetailsByIds(@Param("planIds") List<Integer> planIds);

    @Query("SELECT new com.adopt.apigw.modules.Customers.LightPostpaidPlanDTO(" +
            "pp.name, qp.qosspeed) " +
            "FROM PostpaidPlan pp " +
            "LEFT JOIN pp.qospolicy qp " +
            "WHERE pp.id = :postpaidPlanId")
    LightPostpaidPlanDTO findPlanNameAndPlanSpeedById(@Param("postpaidPlanId") Integer postpaidPlanId);

    @Query(value = "SELECT pp.validity, pp.offerprice, s.servicename  FROM tblmpostpaidplan pp join tblmservices s on pp.serviceid = s.serviceid where" +
            " pp.POSTPAIDPLANID = :planId", nativeQuery = true)
    List<Object[]> getPostpaidPlanDetailsByPlanId(Integer planId);

    @Modifying
    @Transactional
    @Query(" UPDATE PostpaidPlan p SET p.status = :expiredStatus, p.updatedate = :now WHERE p.endDate < :today AND p.isDelete = false AND  p.status <>:expiredStatus")
    int markPlansAsExpired(@Param("today") LocalDate today, @Param("expiredStatus") String expiredStatus, @Param("now") LocalDateTime now);

}

