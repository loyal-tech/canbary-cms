package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.controller.common.VasPlan.VasPlanResponseDTO;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.PlanGroup;
import com.adopt.apigw.modules.Customers.LightCustomerPlanMappingDTO;
import com.adopt.apigw.modules.Customers.LightPostpaidPlanDTO;
import com.adopt.apigw.modules.planUpdate.domain.CustomerPackage;
import com.adopt.apigw.pojo.NewCustPojos.NewCustPlanMappingPojo;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface CustPlanMappingRepository extends JpaRepository<CustPlanMappping, Long>, QuerydslPredicateExecutor<CustPlanMappping> {

    List<CustPlanMappping> findAllByCustomerId(Integer id);

    List<CustPlanMappping> findAllByCustomerIdAndIsDeleteIsFalse(Integer id);

    //List<CustPlanMappping> findAllByCustomerIdIn(List<Integer> iDs);

    //List<CustPlanMappping> findAllByCustServiceMappingId(List<Integer> iDs);

    List<CustPlanMappping> findAllByCustomerIdAndPlanId(Integer customerId, Integer planId);

    List<CustPlanMappping> findAllByCustomerIdAndPlanIdAndAndCustServiceMappingId(Integer customerId, Integer planId, Integer custServiceMappingId);

    List<CustPlanMappping> findByPlanId(Integer planId);

    CustPlanMappping findById(Integer planId);

    CustPlanMappping findByIdAndCustPlanStatusNotAndIsDeleteFalse(Integer planId, String status);

    CustPlanMappping findByIdAndCustPlanStatus(Integer planId, String custPlanStatus);

    CustPlanMappping findByIdAndCustPlanStatusAndStopServiceDateIsNotNull(Integer planId, String custPlanStatus);

    @Query(value = "select * from TBLCUSTPACKAGEREL where custid= :customerId and expirydate > now()", nativeQuery = true)
    List<CustPlanMappping> getAllCustPlanMappingWithActiveAndFutuerBycustomerId(@Param("customerId") Integer CustomerId);

    @Query(value = "select * from TBLCUSTPACKAGEREL where debitdocid= :debitDocId", nativeQuery = true)
    List<CustPlanMappping> findAllByDebitdocid(@Param("debitDocId") Integer debitDocId);

    @Query(value = "select * from TBLCUSTPACKAGEREL where debitdocid= :debitDocId and cust_plan_status= :status", nativeQuery = true)
    List<CustPlanMappping> findAllByDebitdocidAndCustPlanStatus(@Param("debitDocId") Integer debitDocId, String status);

    @Query(value = "select debitdocid from TBLCUSTPACKAGEREL where cust_ref_id in (:custRefId)", nativeQuery = true)
    List<Integer> findAllByCustRefId(Set<Integer> custRefId);

//    @Query(value = "select debitdocid from TBLCUSTPACKAGEREL where cust_ref_id in (:custRefId)", nativeQuery = true)
//    List<Integer> findAllByCustRefId(List<Long> custRefId);


    List<CustPlanMappping> findAllByCustServiceMappingId(Integer customerServiceMappingId);


    @Query("select cpm from CustPlanMappping cpm,CustomerServiceMapping csm  where cpm.endDate < current_date and cpm.id = csm.id and csm.id  in (select ps.id  from PlanService ps where ps.is_dtv = true ) ")
    List<CustPlanMappping> findAllDTVServiceExpiringToday();

    @Query("select cpm from CustPlanMappping cpm where cpm.startDate >current_date and cpm.custServiceMappingId in (select csm from CustomerServiceMapping csm ,PlanService ps where csm.id=:custServiceMappingId and csm.serviceId=ps.id and ps.is_dtv=true)  ")
    List<CustPlanMappping> getAllWithConnectionNumberAndForDTVService(@Param("custServiceMappingId") Integer custServiceMappingId);

    List<CustPlanMappping> findAllByCustPlanStatusAndIdIn(String stop, List<Integer> ids);

    List<CustPlanMappping> findAllByCustomerIdAndCustPlanStatus(Integer id, String custPlanStatus);

    @Query(value = "select debitdocid from TBLCUSTPACKAGEREL where cust_ref_id in (:custRefId)", nativeQuery = true)
    List<Integer> findAllByCustRefId(List<Long> custRefId);

    @Query("select cpm from CustPlanMappping cpm where cpm.custServiceMappingId in (select csm from CustomerServiceMapping csm ,PlanService ps where csm.connectionNo=:connectionNumber and csm.serviceId=ps.id and ps.is_dtv=true) and cpm.expiryDate > current_date and cpm.custPlanStatus !='STOP'")
    List<CustPlanMappping> getAllWithConnectionNumberAndForDTVService(@Param("connectionNumber") String connectionNumber);

    List<CustPlanMappping> findAllByCustRefIdIsNotNull();

    List<CustPlanMappping> findAllByIdIn(List<Integer> custpackIds);

    List<CustPlanMappping> findAllByCustomerIsAndPlanGroupAndIsHold(Customers customers, PlanGroup planGroup, Boolean isHold);

    List<CustPlanMappping> findAllByCustomerIsAndPlanGroupInAndIsHold(Customers customers, List<PlanGroup> planGroup, Boolean isHold);

    List<CustPlanMappping> findAllByCustomerIsAndCustServiceMappingIdInAndIsHold(Customers customers, Set<Integer> custServiceMappingId, Boolean isHold);

    List<CustPlanMappping> findAllByCustServiceMappingIdAndIsHold(Integer custServiceMappingId, Boolean isHold);

    List<CustPlanMappping> findAllByCustServiceMappingIdInAndCustPlanStatusAndEndDateIsAfter(List<Integer> iDs, String status, LocalDateTime endDate);

    List<CustPlanMappping> findAllByCustServiceMappingIdInAndIsHoldAndIsVoid(List<Integer> iDs, Boolean isHold, Boolean isVoid);

    List<CustPlanMappping> findAllByCustServiceMappingIdIn(List<Integer> iDs);

    @Query("select cpm.id from CustPlanMappping cpm where cpm.custServiceMappingId in (:custServiceMappingId) and cpm.custPlanStatus in ('STOP','Terminate')")
    List<Integer> getAllByCustServiceMappingIdIn(@Param("custServiceMappingId") List<Integer> custServiceMappingId);

    List<CustPlanMappping> findAllByPlanGroupIn(List<PlanGroup> planGroups);

    @Query("select cpm.custServiceMappingId from CustPlanMappping cpm where cpm.id in (:Ids)")
    List<Integer> getAllByCustServiceMappingIdInCprIds(@Param("Ids") List<Integer> Ids);

    @Query("select cpm from CustPlanMappping cpm where cpm.custServiceMappingId in (:Ids)")
    List<CustPlanMappping> getDebitDocIdByCustServiceMappingIdInCprIds(@Param("Ids") List<Integer> Ids);

    @Query("select cpm.id from CustPlanMappping cpm where cpm.debitdocid in (:Ids)")
    List<Integer> getAllByCustPlanMappingIdInDebitDocIds(@Param("Ids") List<Long> Ids);

    @Query(value = "select t.discount from CustPlanMappping t where t.id =:cprId")
    Double findDiscountById(Integer cprId);

    @Query(value = "select * from TBLCUSTPACKAGEREL where cust_cpr in :custCprs", nativeQuery = true)
    List<CustPlanMappping> getAllCustPlanMappingByCustCPRList(@Param("custCprs") List<Integer> custCprs);

    @Query(value = "select t.debitdocumentid  from tbltdebitdocument t " +
            "where t.debitdocumentid IN (select t2.debitdocumentid  from tbltcreditdebitmapping t2 " +
            "where t2.CREDITDOCID IN (select t3.CREDITDOCID from TBLTCREDITDOC t3 where DATEDIFF(CURRENT_DATE(), Date(t3.CREATEDATE)) =:dateDiff  and t3.status ='pending' and t3.next_team_hir_mapping is not null)) " +
            "and (t.payment_status ='UnPaid' or t.payment_status is null)", nativeQuery = true)
    List<Integer> getAllCprIdsForServiceHold(@Param("dateDiff") Integer dateDiff);

    @Query("select cpm.id from CustPlanMappping cpm where cpm.id in (:Ids) and cpm.custPlanStatus = 'Active'")
    List<Integer> getAllByCustPlanMappingIdInCustPlanMappingIdsandstatus(@Param("Ids") List<Integer> Ids);


    @Query("select cpm.id from CustPlanMappping cpm where cpm.id in (:Ids)  and cpm.custPlanStatus = 'Active'")
    List<Integer> getAllByCustPlanMappingIdInCustPlanMappingIdsandstatusAndPlangroup(@Param("Ids") List<Integer> Ids);

    @Query("select cpm.id from CustPlanMappping cpm where cpm.planGroup.planGroupId in (:Ids) and cpm.custPlanStatus = 'Active'")
    List<Integer> getAllByCustPlanMappingIdInCustPlanMappingIdsandstatusAndwithPlangroup(@Param("Ids") List<Integer> Ids);

    @Query(value = "select * from TBLCUSTPACKAGEREL  where debitdocid=:debitDocId", nativeQuery = true)
    List<Integer> getAllByDebitDocId(@Param("debitDocId") Integer debitDocId);

    @Query(value = "select cpm from CustPlanMappping cpm WHERE cpm.customer.id=:custId and cpm.serviceId in (:serviceIds)")
    List<CustPlanMappping> findAllByCustomerIdAndService(Integer custId, List<Integer> serviceIds);

    @Query("select cpm from CustPlanMappping cpm where cpm.debitdocid =:longValue")
    List<CustPlanMappping> findAllByDebitdocumentid(Long longValue);


    @Query(value = "select t.planId , q.totalQuota , q.quotaType , q.usedQuota , q.totalReservedQuota ,q.isChunkAvailable, t.endDate , t.startDate , q.timeTotalQuota , q.timeQuotaUsed  from CustPlanMappping t join CustQuotaDetails q on q.custPlanMappping.id = t.id  where  t.customer.id =:custId")
    List<Object[]> getActivePlanQuotaByCustId(Integer custId);

    @Query(value = "select pp.maxconcurrentsession from CustPlanMappping cpm inner join PostpaidPlan pp on cpm.planId = pp.id where cpm.custServiceMappingId =:custServiceMapId")
    List<String> getMaxConcurrencybyPlanId(Integer custServiceMapId);

    @Query("select cpm.id from CustPlanMappping cpm where cpm.custServiceMappingId in (:custServiceMappingId)")
    List<Integer> getAllByCustServiceMappingIn(@Param("custServiceMappingId") List<Integer> custServiceMappingId);

    @Query("SELECT new com.adopt.apigw.modules.Customers.LightCustomerPlanMappingDTO(cs.id, cs.username, cs.password,cs.custtype,pp.id,pp.serviceId,pp.name,pp.planGroup,pp.offerprice,cs.mobile, cp.custServiceMappingId,cp.id,cp.startDate,cp.endDate,cp.expiryDate,cq.quotaType , cq.usedQuota , cq.totalQuota , cq.timeQuotaUsed , cq.timeTotalQuota , cq.quotaUnit , cq.timeQuotaUnit," +
            "(CASE WHEN EXISTS (SELECT 1 FROM com.adopt.apigw.model.postpaid.CustPlanMappping subCp " +
            "                  JOIN com.adopt.apigw.model.postpaid.PostpaidPlan subPp ON subCp.planId = subPp.id " +
            "                  WHERE subCp.customer.id = cs.id AND subPp.planGroup IN ('Volume Booster', 'Bandwidthbooster', 'DTV Addon')) " +
            "      THEN true ELSE false END) as isVolumeBooster) " +
            "from CustPlanMappping cp " +
            "left join PostpaidPlan pp on pp.id = cp.planId " +
            "left join Customers cs on cs.id = cp.customer.id " +
            "left join CustQuotaDetails cq on cq.custPlanMappping.id = cp.id " +
            "left join PlanService ps on ps.id=pp.serviceId " +
            "left join Mvno mv on mv.id=pp.mvnoId " +
            "where mv.status = 'Active' and pp.status='Active' " +
            "and (cp.startDate <= CURRENT_TIMESTAMP or cp.startDate is null) " +
            "and (cp.endDate >= CURRENT_TIMESTAMP or cp.endDate is null) " +
            "and (:mobileNo is null or cs.mobile = :mobileNo) " +
            "and (:emailId is null or cs.email = :emailId)")
    List<LightCustomerPlanMappingDTO> findAllByMobileOrEmail(@Param(value = "mobileNo") String mobileNo,
                                                             @Param(value = "emailId") String emailId);

    @Modifying
    @Transactional
    @Query("UPDATE CustPlanMappping e SET e.debitdocid = :debitdocid, e.endDate=:endDate, e.expiryDate=:expiryDate  WHERE e.id = :id")
    int updateCustPlanMapping(@Param("id") Integer id, @Param("debitdocid") Long debitdocid, @Param("endDate") LocalDateTime endDate, @Param("expiryDate") LocalDateTime expiryDate);


    @Modifying
    @Transactional
    @Query("UPDATE CustPlanMappping e SET e.debitdocid = :debitdocid WHERE e.id = :id")
    int updateCustPlanMapping(@Param("id") Integer id, @Param("debitdocid") Long debitdocid);


    @Query("SELECT new CustPlanMappping(c.id,c.planId) FROM CustPlanMappping c WHERE c.id = :id")
    CustPlanMappping findById1(Integer id);

    @Query("SELECT new CustPlanMappping(c.id,c.endDate,c.expiryDate,c.debitdocid) FROM CustPlanMappping c WHERE c.id = :id")
    CustPlanMappping findById2(Integer id);

    @Query(value = "select custpackageid from TBLCUSTPACKAGEREL where debitdocid= :debitDocId", nativeQuery = true)
    List<Integer> findAllByDebitdocid1(@Param("debitDocId") Integer debitDocId);

    @Query(value = "select * from TBLCUSTPACKAGEREL t where t.planid= :id", nativeQuery = true)
    List<CustPlanMappping> findByPostpaidPlanId(@Param("id") Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE CustPlanMappping e SET e.endDate=:endDate, e.expiryDate=:expiryDate  WHERE e.id = :id")
    int updateCustPlanMappingEnddateAndExpiryDate(@Param("id") Integer id, @Param("endDate") LocalDateTime endDate, @Param("expiryDate") LocalDateTime expiryDate);


    @Modifying
    @Query(value = "update tblcustpackagerel t set t.qospolicyid = :qosPolicyId where t.planid = :planId and t.cust_plan_status = 'Active' and t.enddate > current_timestamp()", nativeQuery = true)
    int updateQosPolicyIdByPlanId(@Param("qosPolicyId") Long qosPolicyId, @Param("planId") int planId);


    @Query("SELECT t.id FROM CustPlanMappping t WHERE t.planId = :planId AND t.custPlanStatus = 'Active' AND t.endDate > CURRENT_TIMESTAMP")
    List<Long> fetchUpdatedCprIds(@Param("planId") int planId);

    @Query("SELECT cp.id, cp.expiryDate, cp.offerPrice FROM CustPlanMappping cp " +
            "WHERE cp.planId = :planId AND cp.customer.id = :custId")
    Object[] findPlanPriceAndEndDateByPlanId(@Param("planId") Integer planId, @Param("custId") Integer custId);


    @Query("SELECT cpm.id , cpm.planId, cpm.startDate , cpm.endDate, cpm.expiryDate " +
            "FROM CustPlanMappping cpm " +
            "JOIN cpm.customer cust " +
            "WHERE cust.id = :customerId " +
            "AND LOWER(cpm.purchaseType) = 'new' " +
            "AND LOWER(cpm.custPlanStatus) = 'active' " +
            "AND cpm.expiryDate > CURRENT_TIMESTAMP " +
            "ORDER BY cpm.startDate ASC")
    List<Object[]> findActiveNewPlansByCustomerId(@Param("customerId") Integer customerId);


    @Query("SELECT c.startServiceDate, c.endDate, c.remarks, c.billTo, c.planGroup.id, " +
            "c.isVoid, c.extendValidityremarks, c.createdate, c.promise_to_pay_remarks " +
            "FROM CustPlanMappping c WHERE c.id = :planMapId")
    Object[] getPlanMappingByPlanId(@Param("planMapId") Integer planMapId);




    @Query("Select new com.adopt.apigw.pojo.NewCustPojos.NewCustPlanMappingPojo(" +
            "c.planId, c.service, c.billTo, c.custPlanStatus, c.discount, c.isInvoiceToOrg, c.billableCustomerId, c.custServiceMappingId)" +
            "from CustPlanMappping c WHERE c.customer.id = :customerId and c.custPlanStatus = 'Active' order by c.planId desc ")
    List<NewCustPlanMappingPojo> findAllCustPlanMappingByCustId(@Param("customerId") Integer customerId);
    @Query("SELECT t.debitdocid FROM CustPlanMappping t, DebitDocument d " +
            "WHERE t.debitdocid = d.id " +
            "AND d.startdate <= CURRENT_TIMESTAMP " +
            "AND d.duedate >= CURRENT_TIMESTAMP " +
            "AND t.id = :custplanmappingId")
    Long getCurrentDebitdocIdByPlanMappId(@Param("custplanmappingId") Integer custplanmappingId);


    @Query("select cpm.custServiceMappingId from CustPlanMappping cpm JOIN CustomerServiceMapping cm on cpm.custServiceMappingId = cm.id where cpm.id in (:Ids) and (cm.previousStatus is null or cm.previousStatus = :status)")
    List<Integer> getAllByCustServiceMappingIdInCprIdsAndActiveStatus(@Param("Ids") List<Integer> Ids,@Param("status") String status);
    @Query("select cpm.custServiceMappingId from CustPlanMappping cpm JOIN CustomerServiceMapping cm on cpm.custServiceMappingId = cm.id where cpm.id in (:Ids) and  cm.previousStatus = :status")
    List<Integer> getAllByCustServiceMappingIdInCprIdsAndHoldStatus(@Param("Ids") List<Integer> Ids,@Param("status") String status);

    @Query("SELECT new com.adopt.apigw.pojo.NewCustPojos.NewCustPlanMappingPojo(" +
            "c.planId, c.service, c.billTo, c.custPlanStatus, c.discount, " +
            "c.isInvoiceToOrg, c.billableCustomerId, c.custServiceMappingId, " +
            "c.startDate, c.expiryDate,c.vasId) " +
            "FROM CustPlanMappping c " +
            "WHERE c.customer.id = :customerId " +
            "AND c.custPlanStatus = 'Active' " +
            "AND CURRENT_TIMESTAMP BETWEEN c.startDate AND c.expiryDate " +
            "ORDER BY c.planId DESC")
    List<NewCustPlanMappingPojo> findAllCurrentPlansByCustId(@Param("customerId") Integer customerId);


    @Query("SELECT c " +
            "FROM CustPlanMappping c " +
            "WHERE c.customer.id = :customerId " +
            "AND c.custPlanStatus = 'Active' " +
            "AND CURRENT_TIMESTAMP BETWEEN c.startDate AND c.expiryDate " +
            "AND c.vasId = :vasId "+
            "AND (c.status IS NULL OR LOWER(c.status) <> 'expired') " +
            "ORDER BY c.planId DESC")
    List<CustPlanMappping> findAllCurrentPlansByCustIdAndVasId(@Param("customerId") Integer customerId,@Param("vasId") Integer vasId);

    @Modifying
    @Transactional
    @Query("UPDATE CustPlanMappping c " +
            "SET c.expiryDate = :newExpiryDate, " +
            "c.status = 'Expired' " +
            "WHERE c.id = :custPlanMappingId")
    int updateExpiryAndStatus(@Param("custPlanMappingId") Integer custPlanMappingId,
                              @Param("newExpiryDate") LocalDateTime newExpiryDate);


    @Query("SELECT new com.adopt.apigw.pojo.NewCustPojos.NewCustPlanMappingPojo(" +
            "c.planId, c.service, c.billTo, c.custPlanStatus, c.discount, " +
            "c.isInvoiceToOrg, c.billableCustomerId, c.custServiceMappingId, " +
            "c.startDate, c.expiryDate, c.vasId) " +
            "FROM CustPlanMappping c " +
            "JOIN VasPlan v ON v.id = c.vasId " +
            "WHERE c.customer.id = :customerId " +
            "AND c.custPlanStatus in ('Active' , 'STOP') " +
            "AND (c.status != 'Expired' OR c.status IS NULL) "+
            "AND CURRENT_TIMESTAMP BETWEEN c.startDate AND c.expiryDate " +
            "AND c.vasId IS NOT NULL " +
            "AND v.isdefault = false " +
            "ORDER BY c.id DESC")
    List<NewCustPlanMappingPojo> findActivePlansWithVasFilter(@Param("customerId") Integer customerId);

    @Query(value = "SELECT " +
            "m.vas_name AS vasName, " +
            "m.vasamount AS vasOfferPrice, " +
            "m.pausedayslimit AS pauseDaysLimit, " +
            "m.pausetimelimit AS pauseTimeLimit, " +
            "m.tatid AS tatId, " +
            "m.inventory_replace_afteryears AS inventoryReplaceAfterYears, " +
            "m.inventory_paid_months AS inventoryPaidMonths, " +
            "m.inventory_count AS inventoryCount, " +
            "m.shiftlocation_years AS shiftLocationYears, " +
            "m.shiftlocation_months AS shiftLocationMonths, " +
            "m.shiftlocation_count AS shiftLocationCount, " +
            "m.validity AS validity, " +
            "m.unitsofvalidity AS unitsOfValidity, " +
            "c.startdate AS startDate, " +
            "c.enddate AS endDate, " +
            "c.expirydate AS expiryDate, " +
            "i.installment_frequency AS installmentType, " +
            "i.total_installments AS totalInstallments, " +
            "i.installment_start_date AS installmentStartDate, " +
            "i.last_installment_date AS installmentEndDate, " +
            "i.installment_no AS installmentNo, " +
            "i.amount_per_installment As amountPerInstallment, "+
            "i.next_installment_date AS installmentNextDate, " +
            "i.installment_enabled As installmentEnabled " +
            "FROM TBLCUSTPACKAGEREL c " +
            "JOIN tblmvasplan m ON c.vasid = m.id " +
            "LEFT JOIN tbltcustchargehistory h ON h.cust_plan_mapping_id = c.custpackageid " +
            "LEFT JOIN tblcustchargeinstallments i ON i.cust_charge_history_id = h.history_id " +
            "WHERE c.custid = :custId " +
            "AND c.vasid IS NOT NULL " +
            "AND (c.status != 'Expired' OR c.status IS NULL) "+
            "AND CURRENT_TIMESTAMP BETWEEN c.startdate AND c.expirydate " +
            "AND m.isdelete = false",
            nativeQuery = true)
    List<Object[]> findVasPlansByCustomerId(@Param("custId") Integer custId);


    @Query("SELECT new com.adopt.apigw.pojo.NewCustPojos.NewCustPlanMappingPojo(" +
            "c.planId, c.service, c.billTo, c.custPlanStatus, c.discount, " +
            "c.isInvoiceToOrg, c.billableCustomerId, c.custServiceMappingId, " +
            "c.startDate, c.expiryDate,c.vasId) " +
            "FROM CustPlanMappping c " +
            "WHERE c.customer.id = :customerId " +
            "AND c.custPlanStatus = 'Active' " +
            "AND c.vasId IS NULL "+
            "ORDER BY c.planId DESC")
    List<NewCustPlanMappingPojo> findAllCurrentPlansByCustIdByVasnot(@Param("customerId") Integer customerId);


    @Query("SELECT new CustPlanMappping(t.id, t.startDate, t.serviceHoldDate,t.customerCpr,t.custServiceMappingId,t.createdById) from CustPlanMappping t where t.custServiceMappingId in :iDs")
    List<CustPlanMappping> findAllByCustServiceMappingIdsIn(@Param("iDs") List<Integer> iDs);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE CustPlanMappping c " +
            "SET c.status = :customerStatusHold, " +
            "c.custPlanStatus = :customerStatusHold " +
            "WHERE c.id IN :custPlanMappingIds")
    void updateStatus(@Param("customerStatusHold") String customerStatusHold,
                      @Param("custPlanMappingIds") List<Integer> custPlanMappingIds);

    @Query("select c.custPlanStatus from CustPlanMappping c WHERE c.customer.id = :custId")
    String getCustPlanStatusByCustomer_Id(@Param("custId") Integer custId);
}
