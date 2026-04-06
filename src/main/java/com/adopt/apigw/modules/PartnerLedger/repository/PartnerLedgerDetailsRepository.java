package com.adopt.apigw.modules.PartnerLedger.repository;

import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedgerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface PartnerLedgerDetailsRepository extends JpaRepository<PartnerLedgerDetails,Long>, QuerydslPredicateExecutor<PartnerLedgerDetails>
{
    @Query(value="select (sum(case when t.transtype='CR' then t.commission else 0 end) - sum(case when t.transtype='DR' then t.commission else 0 end))as totaldebit from tblmpartnerledgerdetails as t where t.partner_id=:partner_id and date(t.CREATEDATE) <:startdate",nativeQuery = true)
    Double findOpeningAmount(@Param("partner_id") Integer partner_id, @Param("startdate")LocalDate startdate);
    @Query(value = "select * from tblmpartnerledgerdetails t where t.is_deleted=0 AND t.CREATEDATE >= :startdate AND t.CREATEDATE <= :enddate AND t.partner_id=:partner_id order by t.CREATEDATE",nativeQuery = true)
    List<PartnerLedgerDetails> findAllByStartDateAndEndDateAndPartnerId(@Param("startdate")LocalDate startdate,@Param("enddate") LocalDate enddate,@Param("partner_id") Integer partner_id);

    @Query(value = "select * from tblmpartnerledgerdetails t where t.is_deleted=0 AND t.partner_id=:partner_id order by t.CREATEDATE",nativeQuery = true)
    List<PartnerLedgerDetails> findAllByPartner_IdOrderByCreateDateAsc(@Param("partner_id") Integer partner_id);
    @Query(value = "select (sum(case when t.transtype='CR' then t.amount else 0 end) - sum(case when t.transtype='DR' then t.amount else 0 end))as totalAmount from tblmpartnerledgerdetails as t where t.partner_id=:partner_id and date(t.CREATEDATE) BETWEEN :startDate AND :endDate",nativeQuery = true)
    Double findClsoingAmount(@Param("startDate")LocalDate startDate,@Param("endDate")LocalDate endDate,@Param("partner_id") Integer partner_id);
    @Query(value = "select (sum(case when t.transtype='CR' then t.amount else 0 end) - sum(case when t.transtype='DR' then t.amount else 0 end))as totalAmount from tblmpartnerledgerdetails as t where t.partner_id=:partner_id ",nativeQuery = true)
    Double findClsoingAmountById(@Param("partner_id") Integer partner_id);

    @Query(value = "select * from tblmpartnerledgerdetails t where t.is_deleted=0  AND t.debit_doc_id=:debitDocId",nativeQuery = true)
    List<PartnerLedgerDetails> findAllByDebitDocumentId(@Param("debitDocId") Integer debitDocId);
}
