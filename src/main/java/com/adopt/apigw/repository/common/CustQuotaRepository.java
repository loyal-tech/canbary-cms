package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.postpaid.CustQuotaDetails;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustQuotaRepository extends JpaRepository<CustQuotaDetails, Integer> , QuerydslPredicateExecutor<CustQuotaDetails> {
    List<CustQuotaDetails> findAllByCustomerId(Integer id);

    CustQuotaDetails findByPostpaidPlanIdAndCustomerId(Integer id, Integer custId);

    CustQuotaDetails findByCustPlanMappping_Id(Integer id);

    public List<CustQuotaDetails> findAllByUsedQuotaLessThan(Double usedQuota);

    public List<CustQuotaDetails> findAllByUsedQuotaGreaterThan(Double usedQuota);

    @Query(value = "SELECT * FROM tblcustquotadtls where ROUND(usedquota * 100.0 / totalquota, 1)>=:percent", nativeQuery = true)
    List<CustQuotaDetails> getQuotaByGreaterPercent(@Param("percent") Integer percent);

    @Query(value = "SELECT * FROM tblcustquotadtls where ROUND(usedquota * 100.0 / totalquota, 1)<=:percent", nativeQuery = true)
    List<CustQuotaDetails> getQuotaByLessPercent(@Param("percent") Integer percent);

    CustQuotaDetails getByCustomerIdAndCustPlanMapppingId(Integer custId, Integer custPackageId);

    @Query(value = "SELECT * FROM tblcustquotadtls  where custpackageid IS NOT NULL and custid =:id", nativeQuery = true)
    List<CustQuotaDetails> findAllBycustPlanMapppingId(@Param(value = "id") Integer id);

    @Query(value = "select * from tblcustquotadtls t where t.custid =:custid and t.custpackageid IS NOT NULL", nativeQuery = true)
    List<CustQuotaDetails> findBycustid(@Param("custid") Integer custid);

    @Query(value = "select * from tblcustquotadtls t where t.custid =:custid and t.custpackageid IS NULL",nativeQuery = true)
    List<CustQuotaDetails> findOnlyByCustId(@Param("custid") Integer custid);

    @Query(value = "select * from tblcustquotadtls t where t.custid=:custId",nativeQuery = true)
    List<CustQuotaDetails> findByCustomerId(@Param("custId") Integer custId);

    @Query(value = "select parnet_quota_type from tblcustquotadtls t where t.custid=:custId and t.parnet_quota_type IS NOT NULL ORDER BY t.CREATEDATE DESC LIMIT 1",nativeQuery = true)
    List<String> getParentQuotaType(@Param("custId") Integer custId);
    @Query(value = "select * from tblcustquotadtls t where t.custpackageid=:id",nativeQuery = true)
    CustQuotaDetails findCustQuotaDetailsByCustPackId(@Param("id") Integer id);

    @Modifying
    @Query(value = "update tblcustquotadtls c set c.totalquota = :totalQuota, c.quotaunit = :quotaUnit, c.quotatype = :quotaType, c.usage_quota_type = :usageQuotaType WHERE c.custpackageid IN :cprIds", nativeQuery = true)
    int updateQuotaDetailsByCprIds(@Param("totalQuota") Double totalQuota, @Param("quotaUnit") String quotaUnit, @Param("quotaType") String quotaType, @Param("usageQuotaType") String usageQuotaType, @Param("cprIds") List<Long> cprIds);

    @Query("SELECT t.id " +
            "FROM CustQuotaDetails t " +
            "JOIN CustPlanMappping cpm ON t.custPlanMappping.id = cpm.id " +
            "JOIN PostpaidPlan pp ON cpm.planId = pp.id " +
            "WHERE  t.customer.id  = :custid " +
            "AND cpm.custPlanStatus = 'Active' " +
            "AND cpm.endDate > CURRENT_TIMESTAMP " +
            "ORDER BY t.id DESC")
    List<Integer> findIdsByCustomerId(@Param("custid") int custid);

    @Modifying
    @Query(value = "update tblcustquotadtls c set c.usedquota = 0, c.currentsessionusagevolume = 0, c.currentsessionusagetime = 0, c.timequotaused = 0, c.last_quota_reset = :lastQuotaResetDate where c.custpackageid = :custpackageid", nativeQuery = true)
    int updateQuotaDetailsAndLastQuotaResetDateByCprIds(@Param("lastQuotaResetDate") LocalDateTime lastQuotaResetDate, @Param("custpackageid") Integer custpackageid);

}
