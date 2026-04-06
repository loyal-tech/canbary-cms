package com.adopt.apigw.repository.postpaid;

import java.util.List;

import com.adopt.apigw.model.lead.LeadMaster;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustSpecialPlanMappping;
import com.adopt.apigw.model.postpaid.CustSpecialPlanRelMappping;
import com.adopt.apigw.model.postpaid.PlanGroup;
import com.adopt.apigw.model.postpaid.PostpaidPlan;

@Repository
//@JaversSpringDataAuditable
public interface CustSpecialPlanMapppingRepository extends JpaRepository<CustSpecialPlanMappping, Integer>, QuerydslPredicateExecutor<CustSpecialPlanMappping>{

	List<CustSpecialPlanMappping> findAllByCustomer(Customers customer);
	
	boolean existsByNormalPlanGroup(PlanGroup planGroup);
	
	@Query(value = "select count(custspecialplanid) from TBLMCUSTSPECIALPLANREL c where c.normalplanid in ?1",nativeQuery = true)
	Long countByNormalPlan(List<Integer> ids);

	List<CustSpecialPlanMappping> findAllBySpecialPlan(PostpaidPlan postpaidPlan);
	
	@Query(value = "select count(custspecialplanid) from TBLMCUSTSPECIALPLANREL where specialplanid= :specialPlanId AND normalplanid= :normalPlanId AND custid= :customerId AND CUSTSPPLANID= :custspplanid",nativeQuery = true)
	Long isDuplicateRecordFound(@Param("specialPlanId")Integer specialPlanId,@Param("normalPlanId")Integer normalPlanId,@Param("customerId")Integer customerId, @Param("custspplanid")Long custspplanid);
	
	@Query(value = "select count(custspecialplanid) from TBLMCUSTSPECIALPLANREL where specialplanid= :specialPlanId AND custid= :customerId AND CUSTSPPLANID= :custspplanid",nativeQuery = true)
	Long isDuplicateRecordFoundForCustomer(@Param("specialPlanId")Integer specialPlanId,@Param("customerId")Integer customerId, @Param("custspplanid")Long custspplanid);

	@Query(value = "select count(custspecialplanid) from TBLMCUSTSPECIALPLANREL where specialplangroupid= :specialPlanGroupId AND custid= :customerId AND CUSTSPPLANID= :custspplanid",nativeQuery = true)
	Long isDuplicateRecordFoundForCustomerPlanGroup(@Param("specialPlanGroupId")Integer specialPlanGroupId,@Param("customerId")Integer customerId, @Param("custspplanid")Long custspplanid);
	
	@Query(value = "select count(custspecialplanid) from TBLMCUSTSPECIALPLANREL where specialplanid= :specialPlanId AND normalplanid= :normalPlanId AND CUSTSPPLANID= :custspplanid",nativeQuery = true)
	Long isDuplicateRecordFoundForPlan(@Param("specialPlanId")Integer specialPlanId,@Param("normalPlanId")Integer normalPlanId, @Param("custspplanid")Long custspplanid);

	@Query(value = "select count(custspecialplanid) from TBLMCUSTSPECIALPLANREL where specialplangroupid= :specialPlanGroupId AND normalplangroupid= :normalPlanGroupId AND CUSTSPPLANID= :custspplanid",nativeQuery = true)
	Long isDuplicateRecordFoundPlanGroup(@Param("specialPlanGroupId")Integer specialPlanGroupId,@Param("normalPlanGroupId")Integer normalPlanGroupId, @Param("custspplanid")Long custspplanid);
	@Query(value = "select count(custspecialplanid) from TBLMCUSTSPECIALPLANREL where specialplangroupid= :specialPlanGroupId AND normalplangroupid= :normalPlanGroupId AND custid= :customerId AND CUSTSPPLANID= :custspplanid",nativeQuery = true)
	Long isDuplicateRecordFoundForPlanGroup(@Param("specialPlanGroupId")Integer specialPlanGroupId,@Param("normalPlanGroupId")Integer normalPlanGroupId,@Param("customerId")Integer customerId, @Param("custspplanid")Long custspplanid);

	@Modifying
	@Query("delete from CustSpecialPlanMappping c where c.id in ?1")
	void deleteUsersWithIds(List<Integer> ids);
	
	
	List<CustSpecialPlanMappping> findAllByCustSpecialPlanRelMappping(CustSpecialPlanRelMappping custSpecialPlanRelMappping);

	@Query(value = "select count(custspecialplanid) from TBLMCUSTSPECIALPLANREL where specialplanid= :specialPlanId AND leadcustid= :customerId AND CUSTSPPLANID= :custspplanid",nativeQuery = true)
	Integer isDuplicateRecordFoundForeadCustomer(@Param("specialPlanId")Integer specialPlanId, @Param("customerId")Integer leadCustId, @Param("custspplanid")Long id);

	@Query(value = "select count(custspecialplanid) from TBLMCUSTSPECIALPLANREL where specialplangroupid= :specialPlanGroupId AND leadcustid= :customerId AND CUSTSPPLANID= :custspplanid",nativeQuery = true)
	Integer isDuplicateRecordFoundForLeadCustomerPlanGroup(@Param("specialPlanGroupId")Integer specialPlanGroupId,@Param("customerId") Integer leadCustId, @Param("custspplanid") Long id);

	List<CustSpecialPlanMappping> findAllByLeadMaster(LeadMaster leadMaster);

	boolean existsByNormalPlanGroup(Integer plangroupid);

	@Query(value = "select t.specialplanid  from adoptconvergebss.tblmcustspecialplanrel t where t.normalplanid in (:planId)",nativeQuery = true)
	List<Integer> findSpecialPlanId(@Param("planId") List<Integer> planId);
}
