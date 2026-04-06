package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.service.radius.AbstractService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CustChargeDetailsRepository extends JpaRepository<CustChargeDetails, Integer>, QuerydslPredicateExecutor<CustChargeDetails> {

    List<CustChargeDetails> findAllByCustPlanMapppingIdIn(List<Integer> custPlanIds);

    List<CustChargeDetails> findAllByDebitdocid(Long debitdocid);
    @Query(value = "SELECT * FROM tblcustchargedtls t WHERE t.custpackageid = :id", nativeQuery = true)
    CustChargeDetails findCustChargeDetailsByCustPlanMapppingId(@Param("id")Integer id);

    @Query(value = "select t.charge_date from tblcustchargedtls t where t.custid =:custId", nativeQuery = true)
    LocalDateTime findChargeDateByCustomerId(@Param("custId") Integer custId);

    @Modifying
    @Transactional
    @Query("UPDATE CustChargeDetails c SET " +
            "c.nextInstallmentDate = :nextInstallmentDate, " +
            "c.lastInstallmentDate = :lastInstallmentDate, " +
            "c.installmentNo = :installmentNo " +
            "WHERE c.id = :custChargeId")
    void updateInstallmentDatesAndNo(@Param("nextInstallmentDate") LocalDate nextInstallmentDate,
                                     @Param("lastInstallmentDate") LocalDate lastInstallmentDate,
                                     @Param("installmentNo") Integer installmentNo,
                                     @Param("custChargeId") Integer custChargeId);
}
