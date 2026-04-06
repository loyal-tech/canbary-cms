package com.adopt.apigw.repository.postpaid;

import java.util.List;
import java.util.Set;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.PlanGroupMapping;

//@JaversSpringDataAuditable
@Repository
public interface PlanGroupMappingRepository extends JpaRepository<PlanGroupMapping, Integer>, QuerydslPredicateExecutor<PlanGroupMapping> {

	@Query(value = "select * from tblmplangroupmapping t where t.plangroupid=:planGroupId and t.is_deleted=false and t.MVNOID=:mvnoId", nativeQuery = true)
	List<PlanGroupMapping> findPlanGroupMappingByPlanGroupId(@Param("planGroupId")Integer planGroupId, @Param("mvnoId")Integer mvnoId);

	@Query(value = "select * from tblmplangroupmapping tm JOIN TBLMPOSTPAIDPLAN p ON p.POSTPAIDPLANID = tm.POSTPAIDPLANID where  p.serviceid IN :serviceIds and tm.is_deleted=false and tm.MVNOID=:mvnoId", nativeQuery = true)
	List<PlanGroupMapping> findPlanGroupMappingByServiceIdsIn(@Param("mvnoId")Integer mvnoId, @Param("serviceIds") List<Integer> serviceIds);

	@Query(value = "select * from tblmplangroupmapping tm JOIN TBLMPOSTPAIDPLAN p ON p.POSTPAIDPLANID = tm.POSTPAIDPLANID where tm.plangroupid = :plangroupId and p.serviceid IN :serviceIds and tm.is_deleted=false and tm.MVNOID=:mvnoId", nativeQuery = true)
	List<PlanGroupMapping> findPlanGroupMappingByServiceIdsInAndPlanGroupId(@Param("mvnoId")Integer mvnoId, @Param("serviceIds") List<Integer> serviceIds, @Param("plangroupId") Integer plangroupId);

	@Query(value = "SELECT service FROM tblmplangroupmapping WHERE plangroupid = :specialMappingId", nativeQuery = true)
	List<String> findServiceNamesByPlanGroupId(@Param("specialMappingId") Integer specialMappingId);


}
