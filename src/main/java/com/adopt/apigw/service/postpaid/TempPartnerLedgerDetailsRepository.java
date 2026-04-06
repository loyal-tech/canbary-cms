package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.model.postpaid.TempPartnerLedgerDetail;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedgerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TempPartnerLedgerDetailsRepository extends JpaRepository<TempPartnerLedgerDetail,Long>, QuerydslPredicateExecutor<TempPartnerLedgerDetail>
{
    List<TempPartnerLedgerDetail> findAllByPartner_Id(Integer id);

    @Query(value = "SELECT * FROM tbltmppartnerledgerdetails t WHERE  t.invoice_id=:invoice_id",nativeQuery = true)
    List<TempPartnerLedgerDetail> findAllByInvoiceId(@Param("invoice_id") Integer invoice_id);
}
