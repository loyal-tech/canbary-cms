package com.adopt.apigw.repository.postpaid;

import java.util.List;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.adopt.apigw.model.postpaid.PostPaidPlanServiceAreaMapping;

@Repository
public interface PostPaidPlanServiceAreaMappingRepo extends JpaRepository<PostPaidPlanServiceAreaMapping, Integer>, QuerydslPredicateExecutor<PostPaidPlanServiceAreaMapping>
{
	List<PostPaidPlanServiceAreaMapping> findAllByServiceIdIn(List<Integer> serviceId);
	List<PostPaidPlanServiceAreaMapping> findAllByServiceIdInAndPlanId(List<Integer> serviceId, Integer planId);
	List<PostPaidPlanServiceAreaMapping> findAllByServiceId(Integer serviceId);
	List<PostPaidPlanServiceAreaMapping> findAllByPlanId(Integer planid);

	@Query(" select new PostPaidPlanServiceAreaMapping(pam.serviceId, pam.planId) from PostPaidPlanServiceAreaMapping pam where pam.planId in :planIds ")
	List<PostPaidPlanServiceAreaMapping> findAllServiceAreaIdsbyPlanId(List<Integer> planIds);

	@Query(value = "SELECT pam.planid FROM tblplanservicearearel pam WHERE pam.serviceareaid IN (:serviceId)", nativeQuery = true)
	List<Integer> findAllByService(@Param("serviceId") List<Integer> serviceId);
}
