package com.adopt.apigw.modules.staffLedgerDetails.repository;


import com.adopt.apigw.modules.staffLedgerDetails.Service.StaffLedgerDetailsService;
import com.adopt.apigw.modules.staffLedgerDetails.dto.StaffLedgerDetailsDto;
import com.adopt.apigw.modules.staffLedgerDetails.entity.StaffLedgerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StaffLedgerDetailsRepository extends JpaRepository<StaffLedgerDetails,Long>, QuerydslPredicateExecutor<StaffLedgerDetails> {

    @Query(value = "select * from tblmstaffledgerdetails t where t.staff_id =:staff_id and t.id IS NOT NULL", nativeQuery = true)
    List<StaffLedgerDetailsService> findbyStaffId(@Param("staff_id") Integer id);

   List<StaffLedgerDetails> findAllByTransactionType(String transactiontype);

   StaffLedgerDetails findById(Integer id);


    @Query("SELECT new com.adopt.apigw.modules.staffLedgerDetails.dto.StaffLedgerDetailsDto( SUM(CASE WHEN t.transactionType = 'CR' THEN t.amount ELSE 0.0 END), SUM(CASE WHEN t.transactionType = 'DR' THEN t.amount ELSE 0.0 END) ) " +
            "FROM StaffLedgerDetails t WHERE t.staff.id = :staff_id ")
    StaffLedgerDetailsDto getStaffLedgerSummary(@Param("staff_id") Integer staff_id);
}
