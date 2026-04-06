package com.adopt.apigw.modules.planUpdate.repository;

import com.adopt.apigw.model.common.OTP;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.planUpdate.model.CustomerPackageDTO;
import org.apache.tomcat.jni.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.planUpdate.domain.CustomerPackage;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface CustomerPackageRepository extends JpaRepository<CustomerPackage, Long>, QuerydslPredicateExecutor<CustomerPackage> {
    public List<CustomerPackage> findAllByCustomersId(Integer id);

    @Query(value = "select * from tblcustpackagerel as u where u.custid= :custid and u.expirydate>= :expiryDate order by u.expirydate",nativeQuery = true)
    List<CustomerPackage> findParentCustPackageDetailByExpiryDate(@Param("custid") Integer custid, @Param("expiryDate") LocalDateTime expiryDate);

    @Query(value = "SELECT t.startdate FROM tblcustpackagerel t WHERE t.custid = :customerId ORDER BY t.startdate DESC LIMIT 1", nativeQuery = true)
    Timestamp findStartDateByCustomerId(@Param("customerId") Integer customerId);

    @Query(nativeQuery = true, value = "select * from adoptconvergebss.tblcustpackagerel t where t.custpackageid =:id")
    CustomerPackage findByCustPackId(@Param("id") Integer id);

    @Query(nativeQuery = true, value = "select t.planid from adoptconvergebss.tblcustpackagerel t where t.custpackageid =:id")
    Integer findByPlanId(@Param("id") Integer id);

    @Query(nativeQuery = true , value ="select t.custid from tblcustpackagerel t where t.service =:service")
    List<Integer> findAllCustomerBySevice(@Param("service") String service);

    @Query(value = "select count(t.planid) from tblcustpackagerel t where t.planid =:planid and t.is_delete=false",nativeQuery = true)
    Integer customerPlanBindingIsAlreadyExists(@Param("planid")Integer planid);

    @Query(value = "select distinct t.custid from tblcustpackagerel t where lower(t.service) like lower(concat('%', :service, '%')) and t.is_delete=false", nativeQuery = true)
    List<Integer> customerPackageListByService(@Param("service") String service);


    @Query(value ="select t.expiryDate from CustomerPackage t where t.customers.id =:custid and cast (datediff(t.expiryDate, curdate()) as integer) =:datediff")
    List<LocalDate> getExpirydateBycustomeranddatediff(@Param("custid") Integer custid, @Param("datediff") Integer datediff);

    @Query(value ="select t.plan from CustomerPackage t where t.customers.id =:custid and cast (datediff(t.expiryDate, curdate()) as integer) =:datediff")
    List<PostpaidPlan> getPlanBycustomeranddatediff(@Param("custid") Integer custid, @Param("datediff") Integer datediff);


    @Query(value ="select t.postpaidPlan from DebitDocument t where t.customer.id =:custid  and cast (datediff(curdate(),t.duedate) as integer) =:datediff and t.paymentStatus !='Fully Paid'")
    List<PostpaidPlan> getPostpaidBycustomeranddatediff(@Param("custid") Integer custid, @Param("datediff") Integer datediff);

    @Query(value ="select Date(t.duedate) from DebitDocument t where t.customer.id =:custid  and cast (datediff(curdate(),t.duedate) as integer) =:datediff and t.paymentStatus !='Fully Paid'")
    List<Date> getPostpaidEndDateBycustomeranddatediff(@Param("custid") Integer custid, @Param("datediff") Integer datediff);

    @Query(value ="select t.plan from CustomerPackage t where t.customers.id =:custid and cast (datediff(curdate(),t.startDate) as integer) =:datediff")
    List<PostpaidPlan> getPrepaidBycustomeranddatediff(@Param("custid") Integer custid, @Param("datediff") Integer datediff);

    @Query(value ="select t.startDate from CustomerPackage t where t.customers.id =:custid and cast (datediff(curdate(),t.startDate) as integer) =:datediff")
    List<LocalDate> getPrepaidEndDateBycustomeranddatediff(@Param("custid") Integer custid, @Param("datediff") Integer datediff);

   @Query(value = "select t.service from CustomerPackage t where t.customers.id =:custid and t.endDate =:endDate and t.plan.id =:planid")
    List<String> getServiceNameWithEndDateAndPlanIdForPostpaid(@Param("custid") Integer custid , @Param("endDate") LocalDate endDate , @Param("planid") Integer planid);

    @Query(value = "select t.service from CustomerPackage t where t.customers.id =:custid and t.startDate =:startDate")
    List<String> getServiceNameWithStartDateAndPlanIdForPrepaid(@Param("custid") Integer custid , @Param("startDate") LocalDate startDate);

    @Query(value ="select t.custPackageId from CustomerPackage t where t.customers.id =:custid and cast (datediff(curdate(),t.startDate) as integer) =:datediff")
    List<Long> getPrepaidCustPackageIdBycustomeranddatediff(@Param("custid") Integer custid, @Param("datediff") Integer datediff);

    @Query(value ="select t.custpackrelid from DebitDocument t where t.customer.id =:custid and cast (datediff(curdate(),t.duedate) as integer) =:datediff and t.paymentStatus !='Fully Paid'")
    List<Long> getPostpaidCustPackageIdBycustomeranddatediff(@Param("custid") Integer custid, @Param("datediff") Integer datediff);

    @Query("SELECT t.custPackageId FROM CustomerPackage t JOIN t.customers c " +
            "join DebitDocument  dd on dd.id=t.debitdocid "+
            "WHERE c.id = :custid AND CAST(DATEDIFF(CURDATE(), t.startDate) AS integer) = (:datediff + COALESCE(dd.debitdocGraceDays, 0))")
    List<Long> getPrepaidCustPackageIdBycustomeranddatediffforDeactivate(
            @Param("custid") Integer custid,
            @Param("datediff") Integer datediff);

    @Query(value = "SELECT t.custpackrelid FROM DebitDocument t " +
            "JOIN t.customer c " +
            "WHERE c.id = :custid " +
            "AND FUNCTION('DATEDIFF', FUNCTION('CURDATE'), t.duedate) = (:datediff + COALESCE(t.debitdocGraceDays, 0)) " +
            "AND t.paymentStatus != 'Fully Paid'")
    List<Long> getPostpaidCustPackageIdBycustomeranddatediffDeactivate(
            @Param("custid") Integer custid,
            @Param("datediff") Integer datediff);

    @Query(value = "SELECT CAST(t.duedate AS date) FROM DebitDocument t " +
            "JOIN t.customer c " +
            "WHERE c.id = :custid " +
            "AND FUNCTION('DATEDIFF', FUNCTION('CURDATE'), t.duedate) = (:datediff + COALESCE(t.debitdocGraceDays, 0)) " +
            "AND t.paymentStatus != 'Fully Paid'")
    List<Date> getPostpaidEndDateBycustomeranddatediffDeactivate(
            @Param("custid") Integer custid,
            @Param("datediff") Integer datediff);

    @Query(value= "select t.postpaidPlan " +
            "FROM DebitDocument t " +
            "JOIN t.customer c " +
            "WHERE c.id = :custid " +
            "AND CAST(DATEDIFF(CURDATE(), t.duedate) AS integer) = (:datediff + COALESCE(t.debitdocGraceDays, 0)) " +
            "AND t.paymentStatus != 'Fully Paid'")
    List<PostpaidPlan> getPostpaidBycustomeranddatediffDeactivate(
            @Param("custid") Integer custid,
            @Param("datediff") Integer datediff);

    @Query("SELECT t.startDate FROM CustomerPackage t " +
            "JOIN t.customers c " +
            "join DebitDocument  dd on dd.id=t.debitdocid "+
            "WHERE c.id = :custid AND " +
            "FUNCTION('DATEDIFF', CURRENT_DATE, t.startDate) = (:datediff + COALESCE(dd.debitdocGraceDays, 0))")
    List<LocalDate> getPrepaidEndDateBycustomeranddatediffDeactivate(
            @Param("custid") Integer custid,
            @Param("datediff") Integer datediff);

    @Query(value ="select t.plan from CustomerPackage t JOIN t.customers c join DebitDocument  dd on dd.id=t.debitdocid WHERE c.id = :custid AND CAST(DATEDIFF(CURRENT_DATE, t.startDate) AS integer) = (:datediff + COALESCE(dd.debitdocGraceDays, 0))")
    List<PostpaidPlan> getPrepaidBycustomeranddatediffDeactivate(@Param("custid") Integer custid, @Param("datediff") Integer datediff);

    @Query(value ="select t.custPackageId from CustomerPackage t where t.customers.id =:custid and cast (datediff(t.expiryDate, curdate()) as integer) =:datediff")
    List<Long> getPrepaidCustPackageIdBycustomeranddatediffForAdvance(@Param("custid") Integer custid, @Param("datediff") Integer datediff);




//    @Query(value = "select count(t.planid) > 0 from adoptconvergebss.tblcustpackagerel t where t.planid =:planid and t.is_delete=false and t.MVNOID in :mvnoIds")
//    boolean customerPlanBindingIsAlreadyExists(@Param("planid")Integer planid, @Param("mvnoIds") List mvnoIds);
//
//    @Query("select count(t.planid) > 0 from adoptconvergebss.tblcustpackagerel t where t.planid =:planid and t.is_delete=false and (t.MVNOID = 1 or (t.MVNOID = :mvnoId and t.BUID in :buIds))")
//    boolean customerPlanBindingIsAlreadyExists(@Param("planid")Integer planid, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value ="select DATE(t.duedate) from DebitDocument t where t.customer.id =:custid  and cast (datediff(curdate(),Date(t.duedate)) as integer) =:datediff and t.paymentStatus !='Fully Paid'")
    List<Date> getPostpaidEndDateForPostpaidBycustomeranddatediff(@Param("custid") Integer custid, @Param("datediff") Integer datediff);

    @Query("SELECT new map(p.id as id, p.name as Name, p.offerprice as Price) " +
            "FROM CustomerPackage cp " +
            "JOIN cp.plan p " +
            "WHERE cp.customers.id = :custId")
    List<Map<String, Object>> findPlansByCustomerId(@Param("custId") Integer custId);

    @Query("SELECT DISTINCT new map(p.id as id, p.name as Name, p.offerprice as Price) " +
            "FROM CustomerPackage cp " +
            "JOIN cp.plan p")
    List<Map<String, Object>> findPlans();

    @Query(value = "select t.customers.id, t.custPackageId from CustomerPackage t " +
            "where t.customers.id IN :custid " +
            "and cast(datediff(curdate(), t.startDate) as integer) = :datediff")
    List<Object[]> getPrepaidCustPackageIdBycustomerInanddatediff(
            @Param("custid") List<Integer> custid,
            @Param("datediff") Integer datediff
    );

    @Query(value ="select t.customers.id , t.expiryDate from CustomerPackage t where t.customers.id IN :custids and cast (datediff(t.expiryDate, curdate()) as integer) =:datediff")
    List<Object[]> getExpirydateBycustomerInanddatediff(@Param("custids") List<Integer> custids, @Param("datediff") Integer datediff);

    @Query(value ="select t.customers.id , t.plan.name from CustomerPackage t where t.customers.id" +
            " IN :custids")
    List<Object[]> getPlanBycustomerInanddatediff(@Param("custids") List<Integer> customerIds);

    @Query("SELECT t.customers.id, SUM(t.plan.offerprice) FROM CustomerPackage t " +
            "WHERE t.customers.id IN :custids " +
            "GROUP BY t.customers.id")
    List<Object[]> getAmoutInanddatediff(@Param("custids") List<Integer> customerIds);


//    @Query("SELECT t.customers.id, t.startDate FROM CustomerPackage t " +
//            "WHERE t.customers.id IN :customerIds " +
//            "AND CAST(FUNCTION('DATEDIFF', t.startDate, CURRENT_DATE) AS integer) = :dateDiff")
//    List<Object[]> getStartDateByCustomerIdAndDateDiff(@Param("customerIds") List<Integer> customerIds,
//                                                       @Param("dateDiff") int dateDiff);


    @Query("SELECT t.customers.id, t.startDate FROM CustomerPackage t " +
            "WHERE t.customers.id IN :customerIds")
    List<Object[]> getStartDateByCustomerIdAndDateDiff(@Param("customerIds") List<Integer> customerIds);



    @Query("SELECT t.customers.id, t.endDate FROM CustomerPackage t " +
            "WHERE t.customers.id IN :customerIds ")
    List<Object[]> getEndDateByCustomerIdAndDateDiff(@Param("customerIds") List<Integer> customerIds);
}
