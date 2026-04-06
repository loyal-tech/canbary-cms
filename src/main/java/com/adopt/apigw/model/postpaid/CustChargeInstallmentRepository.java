package com.adopt.apigw.model.postpaid;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface CustChargeInstallmentRepository extends JpaRepository<CustChargeInstallment, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE CustChargeInstallment c SET " +
            "c.installmentStartDate = :installmentStartDate, " +
            "c.nextInstallmentDate = :nextInstallmentDate, " +
            "c.lastInstallmentDate = :lastInstallmentDate, " +
            "c.installmentNo = :installmentNo " +
            "WHERE c.custChargeDetails.id = :custChargeId")
    void updateChargeDetailsInstallmentDatesAndNo(@Param("installmentStartDate") LocalDate installmentStartDate,
                                                  @Param("nextInstallmentDate") LocalDate nextInstallmentDate,
                                                  @Param("lastInstallmentDate") LocalDate lastInstallmentDate,
                                                  @Param("installmentNo") Integer installmentNo,
                                                  @Param("custChargeId") Integer custChargeId);


    @Modifying
    @Transactional
    @Query("UPDATE CustChargeInstallment c SET " +
            "c.installmentStartDate = :installmentStartDate, " +
            "c.nextInstallmentDate = :nextInstallmentDate, " +
            "c.lastInstallmentDate = :lastInstallmentDate, " +
            "c.installmentNo = :installmentNo " +
            "WHERE c.custChargeHistory.id = :custChargeId")
    void updateChargeHistoryInstallmentDatesAndNo(@Param("installmentStartDate") LocalDate installmentStartDate,
                                                  @Param("nextInstallmentDate") LocalDate nextInstallmentDate,
                                                  @Param("lastInstallmentDate") LocalDate lastInstallmentDate,
                                                  @Param("installmentNo") Integer installmentNo,
                                                  @Param("custChargeId") Integer custChargeId);

    @Query("SELECT c FROM CustChargeInstallment c JOIN FETCH c.custChargeHistory h WHERE h.id IN :historyIds")
    List<CustChargeInstallment> findByCustChargeHistoryIdsFetch(@Param("historyIds") List<Integer> historyIds);

    @Query("SELECT c FROM CustChargeInstallment c WHERE c.custChargeDetails.id IN :custChargeDetailsIds")
    List<CustChargeInstallment> findByCustChargeDetailsIdsFetch(@Param("custChargeDetailsIds") List<Integer> custChargeDetailsIds);

}
