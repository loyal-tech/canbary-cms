package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustomerLedger;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface CustomerLedgerRepository extends JpaRepository<CustomerLedger, Integer> , QuerydslPredicateExecutor<CustomerLedger> {

//	@Query(value = "select * from TBLMDebitDocument where lower(name) like '%' :search  '%' order by DebitDocumentID",
//            countQuery = "select count(*) from TBLMDebitDocument where lower(name) like '%' :search '%'",
//            nativeQuery = true)
//    Page<DebitDocument> searchEntity(@Param("search") String searchText, Pageable pageable);

//	List<DebitDocument> findByStatus(String status);

    List<CustomerLedger> findByCustomer(Customers customer);

    @Query(value ="Select * from tblmcustledger where CUSTID=:custId",nativeQuery = true)
    CustomerLedger findByCustomerId(@Param("custId") Integer custId);
}
