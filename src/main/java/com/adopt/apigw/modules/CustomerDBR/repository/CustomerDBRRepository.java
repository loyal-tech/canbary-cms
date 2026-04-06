package com.adopt.apigw.modules.CustomerDBR.repository;

import com.adopt.apigw.modules.CustomerDBR.domain.CustomerDBR;
import com.adopt.apigw.modules.CustomerDBR.model.CustomerDBRDTO;
import com.adopt.apigw.pojo.AggregateCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerDBRRepository extends JpaRepository<CustomerDBR, Long>, QuerydslPredicateExecutor<CustomerDBR> {

    @Query(value = " select * from tblcustomerdbr t " +
            "where start_date between :startdate and :endate", nativeQuery = true)
    List<CustomerDBR> getdbrdeatails(@Param(value = "startdate") LocalDate startdate,
                                     @Param(value = "endate") LocalDate endate);

    @Query(value = " delete from tblcustomerdbr " +
            "where  custid =:custid and status ='inactive'", nativeQuery = true)
    CustomerDBRDTO deletedbr(@Param(value = "custid") Long custid);

    @Query(value = "select sum(distinct dbr) from tblcustomerdbr t " +
            " where start_date =:startdate and custtype ='postpaid'", nativeQuery = true)
    Double getpostpaiddbr(@Param(value = "startdate") LocalDate startdate);

    @Query(value = "select * from tblcustomerdbr " +
            "where custid =:custid order by dbr_id desc limit 1", nativeQuery = true)
    CustomerDBR getCustValue(@Param(value = "custid") Long custid);

    @Query(value = "select *  from tblcustomerdbr where " +
            "(start_date >= :startDate) and custid =:custid", nativeQuery = true)
    List<CustomerDBR> getValuefordelete(@Param(value = "startDate") LocalDate startDate,
                                        @Param(value = "custid") Long custid);

    List<CustomerDBR> findAllByCustid(Long cusId);

    @Query(value = "select *  from tblcustomerdbr t " +
            "where (start_date between :startdate and :endate) and custid =:custid", nativeQuery = true)
    List<CustomerDBR> getbyCustid(@Param(value = "startdate") LocalDate startdate,
                                  @Param(value = "endate") LocalDate endate,
                                  @Param(value = "custid") Long custid);

    @Query(value = "select *  from tblcustomerdbr t " +
            "where (start_date=:startdate) and custid =:custid order by start_date", nativeQuery = true)
    List<CustomerDBR> getbyCustid(@Param(value = "startdate") LocalDate startdate,
                                  @Param(value = "custid") Long custid);

    @Query("SELECT SUM(m.pendingamt) FROM CustomerDBR m where m.cprid in ( :cprIds) and m.startdate=current_date ")
    Double getPendingRevenue(@Param("cprIds") List<Long> cprIds);

    @Query("SELECT SUM(m.pendingamt + m.dbr) FROM CustomerDBR m where m.cprid in ( :cprIds) and m.startdate=current_date ")
    Double getPendingRevenueWithDbr(@Param("cprIds") List<Long> cprIds);

    @Query(value = "SELECT * FROM tblcustomerdbr  where invoiceid=:invoiceId AND start_date between :startDate and :endDate AND is_direct_charge=false",nativeQuery = true)
    List<CustomerDBR> getCustomerDBRListByInBetweenStartDateAndEndDate(@Param(value = "startDate") LocalDate startDate,@Param(value = "endDate") LocalDate endDate,@Param("invoiceId") Long invoiceId);

    @Modifying
    @Query(value = " delete from tblcustomerdbr t where (t.start_date between :startdate and :endate) and t.invoiceid =:invoiceid", nativeQuery = true)
    void deleteAllByInvoiceId(@Param(value = "invoiceid") Long invoiceId, @Param(value = "startdate") LocalDate startdate, @Param(value = "endate") LocalDate endate);

    @Query(value = "SELECT mvnoid as mvnoId, buid as buId, service_area as serviceAreaId FROM tblcustomerdbr  WHERE start_date=:startDate GROUP BY mvnoid, buid, service_area",nativeQuery = true)
    List<AggregateCount> getAllByAggregateByDate(@Param(value = "startDate") LocalDate startDate);

    @Query(value = "SELECT * FROM tblcustomerdbr  WHERE custid=:custId",nativeQuery = true)
    List<CustomerDBR> getAllByCustomerId(@Param(value = "custId") Integer custId);
}
