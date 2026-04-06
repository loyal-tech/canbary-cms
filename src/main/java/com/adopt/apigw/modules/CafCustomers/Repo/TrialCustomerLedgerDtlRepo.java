package com.adopt.apigw.modules.CafCustomers.Repo;

import com.adopt.apigw.model.postpaid.CustomerLedgerDtls;
import com.adopt.apigw.modules.CafCustomers.Domain.TrialCustomerLedgerDtls;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface TrialCustomerLedgerDtlRepo extends JpaRepository<TrialCustomerLedgerDtls,Integer>, QuerydslPredicateExecutor<TrialCustomerLedgerDtls> {

    List<TrialCustomerLedgerDtls> findByCustomerIdAndIsDelete(Integer custId, boolean isDelete);
    @Query(value = "select (sum(case when t.transtype='CR' then t.amount else 0 end) - sum(case when t.transtype='DR' then t.amount else 0 end))as totaldebit from tblttrialcustledgerdetails as t where t.CUSTID=:custId and date(t.CREATEDATE) <:lastDate", nativeQuery = true)
    Double findOpeningAmount(@Param("lastDate") LocalDate lastDate, @Param("custId") Integer custId);

    @Query(value = "select * from tblttrialcustledgerdetails t where date(t.CREATEDATE) between :startDate AND :endDate AND t.CUSTID=:custId AND t.is_delete=:is_delete order by t.CREATEDATE asc", nativeQuery = true)
    List<TrialCustomerLedgerDtls> findAllByCREATE_DATEAndEndDateAndCustomerIdAndIsDelete(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("custId") Integer id, @Param("is_delete") boolean isDelete);

    @Query(value = "select ( sum(case when t.transtype='DR' then t.amount else 0 end)- sum(case when t.transtype='CR' then t.amount else 0 end))as totalAmount from tblttrialcustledgerdetails as t where is_void = false and (t.CUSTID=:custId and date(t.CREATEDATE) BETWEEN :startDate AND :endDate) order by t.CREATEDATE asc", nativeQuery = true)
    Double findClsoingAmount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("custId") Integer id);

    @Query(value = "select ( sum(case when t.transtype='DR' then t.amount else 0 end)- sum(case when t.transtype='CR' then t.amount else 0 end))as totalAmount from tblttrialcustledgerdetails as t where is_void = false and t.CUSTID=:custId order by t.CREATEDATE asc ", nativeQuery = true)
    Double findClsoingAmountById(@Param("custId") Integer custId);
}
