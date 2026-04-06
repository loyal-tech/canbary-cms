package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.modules.subscriber.model.CustomerDetailsDTO;
import feign.Param;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@JaversSpringDataAuditable
public interface CustPlanMapppingRepository extends JpaRepository<CustPlanMappping, Integer>, QuerydslPredicateExecutor<CustPlanMappping> {
    CustPlanMappping findByCustServiceMappingIdAndCustPlanStatus(Integer ServiceMappingId, String status);
    CustPlanMappping findByCustServiceMappingId(Integer ServiceMappingId);

    @Query("SELECT c.expiryDate FROM CustPlanMappping c WHERE c.customer.id = :custId ORDER BY c.id DESC")
    LocalDateTime findLatestExpiryDateByCustId(@Param("custId") Integer custId);

    @Query(value = "SELECT * FROM TBLCUSTPACKAGEREL c WHERE c.custid = :custId AND c.startdate <= CURRENT_TIMESTAMP AND c.expirydate >= CURRENT_TIMESTAMP AND c.purchase_type IN :purchaseTypes", nativeQuery = true)
    List<CustPlanMappping> findActivePlanListByCustomerId(@org.springframework.data.repository.query.Param("custId") Integer custId, @org.springframework.data.repository.query.Param("purchaseTypes") List<String> purchaseTypes);

    @Query(value = "SELECT p.POSTPAIDPLANID , p.NAME , c.startdate " +
            "FROM TBLCUSTPACKAGEREL c " +
            "JOIN TBLMPOSTPAIDPLAN p ON c.planid = p.POSTPAIDPLANID " +
            "WHERE c.custid = :custId " +
            "AND c.startdate <= CURRENT_TIMESTAMP " +
            "AND c.expirydate >= CURRENT_TIMESTAMP " +
            "AND c.purchase_type IN :purchaseTypes AND p.offerprice > 0", nativeQuery = true)
    List<Object[]> findActivePlanDetails(@Param("custId") Integer custId,
                                         @Param("purchaseTypes") List<String> purchaseTypes);

    @Query(value = "SELECT p.POSTPAIDPLANID , p.NAME , c.expirydate , p.offerprice " +
            "FROM TBLCUSTPACKAGEREL c " +
            "JOIN TBLMPOSTPAIDPLAN p ON c.planid = p.POSTPAIDPLANID " +
            "WHERE c.custid = :custId " +
            "AND c.startdate >= CURRENT_TIMESTAMP " +
            "AND c.purchase_type IN :purchaseTypes AND c.cust_plan_status = 'Active' AND p.offerprice > 0 order by c.custpackageid desc", nativeQuery = true)
    List<Object[]> findFuturePlanDetails(@Param("custId") Integer custId,
                                         @Param("purchaseTypes") List<String> purchaseTypes);

    @Query(value = "SELECT p.POSTPAIDPLANID , p.NAME , c.expirydate , p.offerprice " +
            "FROM TBLCUSTPACKAGEREL c " +
            "JOIN TBLMPOSTPAIDPLAN p ON c.planid = p.POSTPAIDPLANID " +
            "WHERE c.custid = :custId " +
            "AND c.startdate <= CURRENT_TIMESTAMP " +
            "AND c.expirydate >= CURRENT_TIMESTAMP " +
            "AND c.purchase_type IN :purchaseTypes AND c.cust_plan_status = 'Active' AND p.offerprice > 0 order by c.custpackageid desc", nativeQuery = true)
    List<Object[]> findActivePlanDetailsLatest(@Param("custId") Integer custId,
                                         @Param("purchaseTypes") List<String> purchaseTypes);

    @Query(value = "SELECT p.POSTPAIDPLANID , p.NAME , c.expirydate , p.offerprice " +
            "FROM TBLCUSTPACKAGEREL c " +
            "JOIN TBLMPOSTPAIDPLAN p ON c.planid = p.POSTPAIDPLANID " +
            "WHERE c.custid = :custId " +
            "AND c.expirydate <= CURRENT_TIMESTAMP " +
            "AND c.purchase_type IN :purchaseTypes AND p.offerprice > 0 order by c.custpackageid desc", nativeQuery = true)
    List<Object[]> findExpiredPlanDetails(@Param("custId") Integer custId,
                                         @Param("purchaseTypes") List<String> purchaseTypes);


//    @Query("SELECT p.name " +
//            "FROM TBLCUSTPACKAGEREL c " +
//            "JOIN TBLMPOSTPAIDPLAN p ON c.planId = p.POSTPAIDPLANID " +
//            "WHERE c.custId = :custId")
//    List<String> findPlanNameByCustId(@Param("custId") Integer custId);

    @Modifying
    @Transactional
    @Query("UPDATE CustPlanMappping c SET c.renewalForBooster = :renewalForBooster WHERE c.id = :custPlanMappingId")
    int updateRenewalForBoosterById(@Param("renewalForBooster") Boolean renewalForBooster,
                                    @Param("custPlanMappingId") Integer custPlanMappingId);


    @Query("SELECT new com.adopt.apigw.modules.subscriber.model.CustomerDetailsDTO(" +
            "c.username, c.id, c.mvnoId, c.email, c.buId, cpr.renewalForBooster, c.phone) " +
            "FROM CustPlanMappping cpr " +
            "JOIN cpr.customer c " +
            "WHERE cpr.id = :custPackageId")
    CustomerDetailsDTO getCustomerDetailsByCprId(@Param("custPackageId") Integer custPackageId);



}
