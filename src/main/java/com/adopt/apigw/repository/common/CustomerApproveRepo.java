package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.CustomerApprove;
import com.adopt.apigw.model.common.Customers;
import org.hibernate.sql.Select;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface CustomerApproveRepo extends JpaRepository<CustomerApprove, Integer>, QuerydslPredicateExecutor<CustomerApprove> {

    Customers findBycustName(String custName);

    CustomerApprove findByCustomerID(Integer custId);

    CustomerApprove findByCustomerIDAndStatus(Integer customerID, String status);

    List<CustomerApprove> findAllByCustomerIDIsAndCurrentStaffIsNotNullAndParentStaffIsNotNullAndStatus(Integer custId,String status);

    List<CustomerApprove>findAllByCustomerIDIsAndCurrentStaffIsNotNullAndStatusEquals(Integer custId,String status);
    @Query("SELECT t.customerID FROM CustomerApprove t WHERE t.currentStaff = :username AND t.status = 'pending'")
    List<Integer> findCustIds(@Param("username") String username);
}
