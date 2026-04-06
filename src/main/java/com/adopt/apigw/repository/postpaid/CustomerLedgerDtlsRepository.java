package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustomerLedgerDtls;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface CustomerLedgerDtlsRepository extends JpaRepository<CustomerLedgerDtls, Integer>, QuerydslPredicateExecutor<CustomerLedgerDtls> {

//	@Query(value = "select * from TBLMDebitDocument where lower(name) like '%' :search  '%' order by DebitDocumentID",
//            countQuery = "select count(*) from TBLMDebitDocument where lower(name) like '%' :search '%'",
//            nativeQuery = true)
//    Page<DebitDocument> searchEntity(@Param("search") String searchText, Pageable pageable);

    List<CustomerLedgerDtls> findByCustomer(Customers customer);

    List<CustomerLedgerDtls> findByCustomerIdAndIsDelete(Integer custId, boolean isDelete);

    @Query(value = "select (sum(case when t.transtype='CR' then t.amount else 0 end) - sum(case when t.transtype='DR' then t.amount else 0 end))as totaldebit from tbltcustledgerdetails as t where t.CUSTID=:custId and date(t.CREATEDATE) <:lastDate", nativeQuery = true)
    Double findOpeningAmount(@Param("lastDate") LocalDate lastDate, @Param("custId") Integer custId);

    @Query(value = "select * from tbltcustledgerdetails t where date(t.CREATEDATE) between :startDate AND :endDate AND t.CUSTID=:custId AND t.is_delete=:is_delete order by t.CREATEDATE asc", nativeQuery = true)
    List<CustomerLedgerDtls> findAllByCREATE_DATEAndEndDateAndCustomerIdAndIsDelete(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("custId") Integer id, @Param("is_delete") boolean isDelete);

    @Query(value = "select ( sum(case when t.transtype='DR' then t.amount else 0 end)- sum(case when t.transtype='CR' then t.amount else 0 end))as totalAmount from tbltcustledgerdetails as t where is_void = false and (t.CUSTID=:custId and date(t.CREATEDATE) BETWEEN :startDate AND :endDate) order by t.CREATEDATE asc", nativeQuery = true)
    Double findClsoingAmount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("custId") Integer id);

    @Query(value = "select (sum(case when t.transtype='CR' then t.amount else 0 end) - sum(case when t.transtype='DR' then t.amount else 0 end))as totalAmount from tbltcustledgerdetails as t where t.CUSTID=:custId ", nativeQuery = true)
    Double findWalletAmt(@Param("custId") Integer custId);

    @Query(value = "select ( sum(case when t.transtype='DR' then t.amount else 0 end)- sum(case when t.transtype='CR' then t.amount else 0 end))as totalAmount from tbltcustledgerdetails as t where is_void = false and t.CUSTID=:custId order by t.CREATEDATE asc ", nativeQuery = true)
    Double findClsoingAmountById(@Param("custId") Integer custId);

    @Query(value = "select amount from tbltcreditdoc where custId=:custId", nativeQuery = true)
    Double findtest1(@Param("custId") Integer custId);

    @Query(value = "select adjustedamount from tbltcreditdoc where custId=:custId", nativeQuery = true)
    Double findtest2(@Param("custId") Integer custId);


//    @Query(value ="select sum(tbltcreditdoc.AMOUNT-adjustedamount) from tbltcreditdoc join tbltcustledgerdetails where tbltcustledgerdetails.CUSTID=tbltcreditdoc.CUSTID and tbltcreditdoc.CUSTID=:custId and tbltcustledgerdetails.TRANSCATEGORY='PAYMENT' ",nativeQuery = true)
//    Double getWalletBalance(@Param("custId") Integer custId);

    @Query(value = "select sum(AMOUNT-adjustedamount) from tbltcreditdoc where tbltcreditdoc.CUSTID=:custId ", nativeQuery = true)
    Double getWalletBalance(@Param("custId") Integer custId);

    CustomerLedgerDtls findByCreditdocid(Integer creditdocid);
}


