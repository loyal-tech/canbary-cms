package com.adopt.apigw.modules.PartnerLedger.repository;

import com.adopt.apigw.model.common.CustomerPayment;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.adopt.apigw.modules.PartnerLedger.domain.PartnerPayment;

import java.time.LocalDate;
import java.util.List;

@JaversSpringDataAuditable
public interface PartnerPaymentRepository extends JpaRepository<PartnerPayment,Long>, QuerydslPredicateExecutor<PartnerPayment> {
    @Query(value = "select * from tblmpartnerpayment t where date(t.CREATEDATE) between :startDate AND :endDate AND t.partner_id=:partner_id",nativeQuery = true)
    List<PartnerPayment> findAllByStartDateAndEndDateAndPartnerId(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate, @Param("partner_id")Integer partner_id);

    List<PartnerPayment> findAllByPartner_Id(Integer id);
}
