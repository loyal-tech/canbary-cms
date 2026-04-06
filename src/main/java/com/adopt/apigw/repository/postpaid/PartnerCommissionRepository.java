package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.PartnerCommission;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerPayment;

import java.time.LocalDate;
import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface PartnerCommissionRepository extends JpaRepository<PartnerCommission, Integer> {

    @Query(value = "select * from TBLPARTNERCOMMREL t where t.PARTNERID=:partnerid",nativeQuery = true)
    List<PartnerCommission> findAllByPartnerId(@Param("partnerid")Integer partnerid);

    @Query(value = "select * from TBLPARTNERCOMMREL t where date(t.CREATEDATE) between :startDate AND :endDate AND t.PARTNERID=:partner_id",nativeQuery = true)
    List<PartnerCommission> findAllByStartDateAndEndDateAndPartnerId(@Param("startDate") LocalDate startDate, @Param("endDate")LocalDate endDate, @Param("partner_id")Integer partner_id);

    @Query(value = "select sum(COMM_VALUE) from TBLPARTNERCOMMREL t where t.PARTNERID=:partnerid and t.COMM_TYPE=:comm_type",nativeQuery = true)
    Double findAllByPartnerIdAndCommType(@Param("comm_type")String comm_type,@Param("partnerid")Integer partnerid);

}
