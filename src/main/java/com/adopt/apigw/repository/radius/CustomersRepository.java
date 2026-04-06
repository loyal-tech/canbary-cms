package com.adopt.apigw.repository.radius;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.adopt.apigw.modules.ippool.domain.IPPoolDtls;
import com.adopt.apigw.pojo.NewCustPojos.CustFieldsPojo;
import com.adopt.apigw.pojo.NewCustPojos.NewCustPlanMappingPojo;
import com.adopt.apigw.pojo.api.CustomersDetailsPojo;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;

import javax.persistence.QueryHint;
import javax.transaction.Transactional;

@JaversSpringDataAuditable
@Repository
public interface CustomersRepository extends JpaRepository<Customers, Integer>, QuerydslPredicateExecutor<Customers> {

    @Query(value = "select custname from tblcustomers where custid= :customerId",nativeQuery = true)
    String findCustomerName(@Param("customerId") Integer CustomerId);

    @Query(value = "select username from tblcustomers where custid= :Id",nativeQuery = true)
    String findUsernameById(Integer Id);

    @Query(value = "select * from tblcustomers where lower(firstname) like '%'  :search  '%' OR lower(lastname) like '%'  :search  '%' OR lower(username) like '%'  :search  '%'  OR lower(email) like '%'  :search  '%' order by custid",
            countQuery = "select count(*) from tblcustomers where lower(firstname) like '%'  :search  '%' OR lower(lastname) like '%'  :search  '%' OR lower(username) like '%'  :search  '%'  OR lower(email) like '%'  :search  '%' ",
            nativeQuery = true)
    Page<Customers> findCustomers(@Param("search") String searchText, Pageable pageable);

    @Query(value = "select * from tblcustomers where lower(firstname) like '%'  :search  '%' OR lower(lastname) like '%'  :search  '%' OR lower(username) like '%'  :search  '%'  OR lower(email) like '%'  :search  '%' AND MVNOID in :mvnoIds order by custid",
            countQuery = "select count(*) from tblcustomers where lower(firstname) like '%'  :search  '%' OR lower(lastname) like '%'  :search  '%' OR lower(username) like '%'  :search  '%'  OR lower(email) like '%'  :search  '%' AND MVNOID in :mvnoIds",
            nativeQuery = true)
    Page<Customers> findCustomers(@Param("search") String searchText, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tblcustomers where (lower(firstname) like '%'  :search  '%' OR lower(lastname) like '%'  :search  '%' OR lower(username) like '%'  :search  '%'  OR lower(email) like '%'  :search  '%')  AND partnerid= :partnerid order by custid",
            countQuery = "select count(*) from tblcustomers where lower(firstname) like '%'  :search  '%' OR lower(lastname) like '%'  :search  '%' OR lower(username) like '%'  :search  '%'  OR lower(email) like '%'  :search  '%' AND partnerid= :partnerid ",
            nativeQuery = true)
    Page<Customers> findCustomers(@Param("search") String searchText, Pageable pageable, @Param("partnerid") Integer partnerid);

    @Query(value = "select * from tblcustomers where (lower(firstname) like '%'  :search  '%' OR lower(lastname) like '%'  :search  '%' OR lower(username) like '%'  :search  '%'  OR lower(email) like '%'  :search  '%')  AND partnerid= :partnerid AND MVNOID in :mvnoIds order by custid",
            countQuery = "select count(*) from tblcustomers where lower(firstname) like '%'  :search  '%' OR lower(lastname) like '%'  :search  '%' OR lower(username) like '%'  :search  '%'  OR lower(email) like '%'  :search  '%' AND partnerid= :partnerid AND MVNOID in :mvnoIds",
            nativeQuery = true)
    Page<Customers> findCustomers(@Param("search") String searchText, Pageable pageable, @Param("partnerid") Integer partnerid, @Param("mvnoIds") List mvnoIds);

    // Page<Customers> findByUsernameStartingWith(String username, Pageable pageable);

    @Query(value = "select custid ,username ,firstname ,lastname ,email ,created_on  ,lastmodified_on ,cstatus ,last_login_time ,failcount,last_password_change,'' as password from tblcustomers where lower(username) like '%'  :username  '%' order by custid",
            countQuery = "select count(*) from tblcustomers where lower(username) like '%'  :username  '%' order by custid ",
            nativeQuery = true)
    List<Customers> downloadcustomer(@Param("username") String username);

    @Query(value = "select custid ,username ,firstname ,lastname ,email ,created_on  ,lastmodified_on ,cstatus ,last_login_time ,failcount,last_password_change,'' as password from tblcustomers where lower(username) like '%'  :username  '%' AND partnerid= :partnerid order by custid",
            countQuery = "select count(*) from tblcustomers where lower(username) like '%'  :username  '%' AND partnerid= :partnerid order by custid ",
            nativeQuery = true)
    List<Customers> downloadcustomer(@Param("username") String username, @Param("partnerid") Integer partnerid);

    @Query(value = "select * from tblcustomers where custid IN :id", nativeQuery = true)
    List<Customers> getAllCustomersById(@Param("id") List<Integer> id);

    @Query(value = "select * from tblcustomers where DATE(CAST(expirydate as DATETIME)) >= :expirydate1 AND DATE(CAST(expirydate as DATETIME))<= :expirydate2 AND cstatus='active'", nativeQuery = true)
    List<Customers> getAllCustomerByExpiryWithIn(@Param("expirydate1") LocalDate expirydate1, @Param("expirydate2") LocalDate expirydate2);

    @Query(value = "select * from tblcustomers where DATE(CAST(expirydate as DATETIME))< :expirydate1 AND cstatus='active'", nativeQuery = true)
    List<Customers> getAllCustomerByExpiryLessthan(@Param("expirydate1") LocalDate expirydate1);

    @Query(value = "select * from tblcustomers where DATE(CAST(expirydate as DATETIME))> :expirydate1 AND cstatus='active'", nativeQuery = true)
    List<Customers> getAllCustomerByExpiryGreaterthan(@Param("expirydate1") LocalDate expirydate1);

    @Query(value = "select * from tblcustomers where expirydate= :expirydate1", nativeQuery = true)
    List<Customers> getAllCustomerByExpiryEqual(@Param("expirydate1") LocalDate expirydate1);

    @Query(value = "select * from tblcustomers where cstatus='active' AND DATE(CAST(expirydate as DATETIME)) BETWEEN :expirydate1 AND :expirydate2", nativeQuery = true)
    List<Customers> getAllCustomerByExpiryBetween(@Param("expirydate1") LocalDate expirydate1, @Param("expirydate2") LocalDate expirydate2);

    @Query(value = "select * from tblcustomers where servicearea_id =:servicearea_id and network_device_id =:network_device_id and oltslotid =:oltslotid and cstatus='active' and oltportid IN :oltportid", nativeQuery = true)
    List<Customers> getAllCustomerForLocation(@Param("servicearea_id") Integer servicearea_id, @Param("network_device_id") Integer network_device_id, @Param("oltslotid") Integer oltslotid, @Param("oltportid") List<Integer> oltportid);

    @Query(value = "select * from tblcustomers where servicearea_id =:servicearea_id and cstatus='active'", nativeQuery = true)
    List<Customers> getAllCustomerByNetworkDevice(@Param("servicearea_id") Integer servicearea_id);

    @Query(value = "select * from tblcustomers where servicearea_id =:servicearea_id and network_device_id =:network_device_id and cstatus='active'", nativeQuery = true)
    List<Customers> getAllCustomerBySlot(@Param("servicearea_id") Integer servicearea_id, @Param("network_device_id") Integer network_device_id);

    @Query(value = "select * from tblcustomers where expirydate<=:expiry", nativeQuery = true)
    List<Customers> getAllCustomerByPlan(@Param("expiry") String expiry);

    List<Customers> findByUsername(String username);

    List<Customers> findByUsernameAndIsDeletedIsFalse(String username);

    @Query(value = "select * from tblcustomers t where t.username=:username AND t.is_deleted=false AND t.cstatus!='Terminate'", nativeQuery = true)
    Customers findByUserName(@Param("username") String username);

    @Query(value = "select t.custid from tblcustomers t where t.username=:username AND t.is_deleted=false AND t.cstatus!='Terminate' AND t.cstatus ='Active'", nativeQuery = true)
    Integer findCustIdByUserName(@Param("username") String username);

    List<Customers> findByUsernameAndPartner_Id(String username, Integer partnerid);

    @Query(value = "select * from tblcustomers t where t.is_deleted=false and t.partnerid = :s1", nativeQuery = true
            , countQuery = "select count(*) from tblcustomers t where t.is_deleted=false and t.partnerid = :s1")
    Page<Customers> findByPartner_IdAndIsDeletedIsFalse(@Param("s1") Integer PartnerId, Pageable pageable);

    @Query(value = "select * from tblcustomers t where t.is_deleted=false and t.partnerid = :s1 AND t.MVNOID in :mvnoIds", nativeQuery = true
            , countQuery = "select count(*) from tblcustomers t where t.is_deleted=false and t.partnerid = :s1 AND t.MVNOID in :mvnoIds")
    Page<Customers> findByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn(@Param("s1") Integer PartnerId, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    List<Customers> findByPartner_IdAndStatusAndIsDeletedIsFalse(Integer ParnterId, String status);

    List<Customers> findByAcctno(String acctno);

    List<Customers> findByAcctnoAndMvnoId(String acctno, Integer mvnoId);

    List<Customers> findByAcctnoAndPartner_Id(String acctno, Integer partnerid);

    List<Customers> findByStatus(String status);

    @Query(value = "select * from tblcustomers where custid <> :id", nativeQuery = true)
    List<Customers> getAllParentCustomers(@Param("id") Integer id);

    Customers findCustomersByPhone(String phone);

    Customers findCustomersByMobile(String mobile);

    List<Customers> findAllByStatusAndIsDeletedIsFalseOrderByIdDesc(String status);

    Page<Customers> findAllByIsDeletedIsFalseOrderByIdDesc(Pageable pageable);

    Customers findByIdAndIsDeletedIsFalse(Integer id);

    Customers findByIdAndIsDeletedIsFalseAndMvnoIdIn(Integer id, List mvnoIds);
    @Query("select new com.adopt.apigw.pojo.api.CustomersDetailsPojo(c.id,c.username,c.firstname,c.lastname,c.title,c.custname,c.status,c.custtype,c.planMappingList,c.mvnoId,c.parentCustomers.id) from Customers c where c.id =:id and c.isDeleted=false")
    CustomersDetailsPojo findCustomersDetailsByIdAndIsDeletedIsFalse(@Param("id") Integer id);

    @Query("select new com.adopt.apigw.pojo.api.CustomersDetailsPojo(c.id,c.username,c.firstname,c.lastname,c.title,c.custname,c.status,c.custtype,c.planMappingList,c.mvnoId,c.parentCustomers.id) from Customers c where c.id =:id and c.isDeleted=false and c.mvnoId in :mvnoIds")
    CustomersDetailsPojo findCustomerDetailsByIdAndIsDeletedIsFalseAndMvnoIdIn(@Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    List<Customers> findAllByCafnoAndIsDeletedIsFalseOrderByIdDesc(String cafNo);

    List<Customers> findAllByMobileAndIsDeletedIsFalseOrderByIdDesc(String mobile);

    List<Customers> findAllByMobileAndIsDeletedIsFalse(String mobile);

    List<Customers> findAllByUsernameAndIsDeletedIsFalseOrderByIdDesc(String username);

    List<Customers> findAllByEmailAndIsDeletedIsFalseOrderByIdDesc(String email);

    @Query(value = "select * from tblcustomers t where t.is_deleted=false AND t.MVNOID in :mvnoIds", nativeQuery = true
            , countQuery = "select count(*) from tblcustomers t where t.is_deleted=false AND t.MVNOID in :mvnoIds")
    Page<Customers> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);
//
//    @Query(nativeQuery = true, value = "select cust.* from tblcustomers cust \n" +
//            "where (cust.username like '%' :s1 '%' or \n" +
//            "cust.firstname like '%' :s2 '%' or \n" +
//            "cust.email like '%' :s3 '%' or\n" +
//            "cust.accountnumber like '%' :s4 '%' or\n" +
//            "cust.cstatus like '%' :s5 '%' or \n" +
//            "cust.mobile like '%' :s6 '%')  \n" +
//            "and cust.is_deleted = 0 AND cust.MVNOID= :MVNOID", countQuery = "select count(*) from tblcustomers cust \n" +
//            "where (cust.username like '%' :s1 '%' or \n" +
//            "cust.firstname like '%' :s2 '%' or \n" +
//            "cust.email like '%' :s3 '%' or\n" +
//            "cust.accountnumber like '%' :s4 '%' or\n" +
//            "cust.cstatus like '%' :s5 '%' or \n" +
//            "cust.mobile like '%' :s6 '%')  \n" +
//            "and cust.is_deleted = 0 AND cust.MVNOID= :MVNOID")
//    Page<Customers> findAllCustomersBy(@Param("s1") String s1
//            , @Param("s2") String s2
//            , @Param("s3") String s3
//            , @Param("s4") String s4
//            , @Param("s5") String s5
//            , @Param("s6") String s6
//            , Pageable pageable, @Param("MVNOID") Integer MVNOID);
//
//    @Query(nativeQuery = true, value = "select cust.* from tblcustomers cust \n" +
//            "where (cust.username like '%' :s1 '%' or \n" +
//            "cust.firstname like '%' :s2 '%' or \n" +
//            "cust.email like '%' :s3 '%' or\n" +
//            "cust.accountnumber like '%' :s4 '%' or\n" +
//            "cust.cstatus like '%' :s5 '%' or \n" +
//            "cust.mobile like '%' :s6 '%')  \n" +
//            "and cust.is_deleted = 0 and cust.partnerid = :s7 AND cust.MVNOID= :MVNOID", countQuery = "select count(*) from tblcustomers cust \n" +
//            "where (cust.username like '%' :s1 '%' or \n" +
//            "cust.firstname like '%' :s2 '%' or \n" +
//            "cust.email like '%' :s3 '%' or\n" +
//            "cust.accountnumber like '%' :s4 '%' or\n" +
//            "cust.cstatus like '%' :s5 '%' or \n" +
//            "cust.mobile like '%' :s6 '%')  \n" +
//            "and cust.is_deleted = 0 and cust.partnerid = :s7 AND cust.MVNOID= :MVNOID")
//    Page<Customers> findAllCustomersByPartner(@Param("s1") String s1
//            , @Param("s2") String s2
//            , @Param("s3") String s3
//            , @Param("s4") String s4
//            , @Param("s5") String s5
//            , @Param("s6") String s6
//            , @Param("s7") Integer s7
//            , Pageable pageable, @Param("MVNOID") Integer MVNOID);

    @Query(nativeQuery = true, value = "select * from tblcustomers t where t.custid IN (:s1) and t.is_deleted = 0 AND t.MVNOID= :MVNOID"
            , countQuery = "select count(*) from tblcustomers t where t.custid IN (:s1) and t.is_deleted = 0 AND t.MVNOID= :MVNOID")
    Page<Customers> findAllBy(@Param("s1") List<String> idList, Pageable pageable, @Param("MVNOID") Integer MVNOID);

    @Query(value = "select * from tblcustomers where expirydate BETWEEN :expirydate1 AND :expirydate2", nativeQuery = true)
    List<Customers> getAllCustomerByExpiryOnSameDay(@Param("expirydate1") Date expirydate1, @Param("expirydate2") Date expirydate2);

    @Query(value = "SELECT * FROM  tblcustomers t inner join tblcustchargedtls t2 on t2.custid = t.custid and t2.chargeid = 3 INNER JOIN tblipallocationdtls t3 on t2.purchase_entity_id = t3.id INNER JOIN tblippooldtls t4 on t4.allocated_id  = t3.id where t.is_deleted = 0 and t2.enddate= :expirydate1", nativeQuery = true)
    List<Customers> getCustomersIpExpiry(@Param("expirydate1") Date expirydate1);

    @Query(value = "SELECT * FROM  tblcustomers t inner join tblcustpackagerel t2 on t2.custid = t.custid where t.is_deleted = 0 and t2.expirydate= :expirydate1", nativeQuery = true)
    List<Customers> getCustomersPlanExpiry(@Param("expirydate1") Date expirydate1);

    Customers findByServicearea(ServiceArea serviceArea);

    @Query(nativeQuery = true
            , value = "select * from tblcustomers t where t.is_deleted = 0  and t.cstatus = lower('Active') and t.voicesrvtype " +
            "in ('PhoneLine','ShipTrunk') and t.voiceProvision = 0")
    List<Customers> findAllForProvisionInNexGe();

    @Query(nativeQuery = true, value = "select * from tblcustomers t \n" +
            "inner join tblcustpackagerel t2 \n" +
            "on t2.custid = t.custid \n" +
            "where t.is_deleted = 0 and\n" +
            "t2.expirydate is not null and t2.expirydate\n" +
            "between sysdate() and sysdate() + interval 1 day")
    List<Customers> findAllForChangePlanInNexGe();

//    @Query(nativeQuery = true,value = "SELECT * from tblcustomers t where t.is_deleted = 0 AND t.MVNOID= :MVNOID OR t.MVNOID IS NULL and t.partnerid = :s1 and (t.username like '%' :s2 '%' " +
//            "or t.firstname like '%' :s2 '%' or t.mobile like '%' :s2 '%' or t.email like '%' :s2 '%')")
//    List<Customers> findAllByPartner_IdAndIsDeletedIsFalse(@Param("s1") Integer partnerid,@Param("s2") String s, @Param("MVNOID") Integer MVNOID);

    @Query(value = "select count(*) from tblcustomercafassignment t where t.staff_id =:s1 ", nativeQuery = true)
    Long findMinimumApprovalReuqestByStaff(@Param("s1") Integer id);

    @Query("select count(c) > 0 from Customers c where username = :username and is_deleted=0")
    boolean customerUsernameIsAlreadyExists(@Param("username") String username);

    @Query("select count(c) > 0 from Customers c where username = :username and is_deleted=0 and MVNOID in :mvnoIds")
    boolean customerUsernameIsAlreadyExists(@Param("username") String username, @Param("mvnoIds") List mvnoIds);

    @Query("select count(c) > 0 from Customers c where username = :username and is_deleted=0 and MVNOID in :mvnoIds and status != 'Terminate'")
    boolean customerUsernameIsAlreadyExists(@Param("username") String username, @Param("mvnoIds")Long  mvnoId);

    @Query("select count(c) > 0 from Customers c where username = :username and is_deleted=0 and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))")
    boolean customerUsernameIsAlreadyExists(@Param("username") String username, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

  /*  @Query("select count(c) > 0 from Customers c where username = :username and MVNOID= :mvnoId")
    boolean customerUsernameIsAlreadyExists(@Param("username") String username, @Param("mvnoId") Integer mvnoId);
*/
     Optional<Customers>  findById(@Param("customerId") Integer CustomerId);
      Customers  findAllById(@Param("customerId") Integer CustomerId);

     Optional<Customers> findByUsernameAndMvnoId(String username, Integer mvnoId);

    @Query("select count(c) > 0 from Customers c where username = :username and MVNOID= :mvnoId and isDeleted = false")
    boolean customerUsernameIsAlreadyExists(@Param("username") String username, @Param("mvnoId") Integer mvnoId);



    @Query(value = "SELECT * FROM tblcustomers t WHERE t.username = :username AND t.MVNOID = :mvnoId AND t.cstatus <> :status LIMIT 1",
            nativeQuery = true)
    Optional<Customers> findByUsernameAndMvnoIdAndStatusNot(@Param("username") String username,
                                                            @Param("mvnoId") Integer mvnoId,
                                                            @Param("status") String status);
    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN CustomerPackage d ON d.customers.id = c.id " +
            "join DebitDocument db on d.debitdocid=db.id "+
            "WHERE c.dunningType = :dunningType " +
            "AND c.dunningCategory = :dunningCategory " +
            "AND c.dunningSector = :dunningSector " +
            "AND c.status = 'Active' " +
            "AND c.isDeleted = false " +
            "AND c.branch IN (:branchIds) " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND DATEDIFF(CURRENT_DATE, d.startDate) = (:dateDiff + COALESCE(db.debitdocGraceDays, 0))")
    List<Customers> getCustomersForDunningPrepaid(@Param(value = "dunningType") String dunningType, @Param(value = "dunningCategory") String dunningCategory,@Param(value = "dunningSector") String dunningSector, @Param(value = "dateDiff") Integer dateDiff , @Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);

    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN CustomerPackage d ON d.customers.id = c.id " +
            "join DebitDocument db on d.debitdocid=db.id "+
            "WHERE c.dunningType = :dunningType " +
            "AND c.dunningCategory = :dunningCategory " +
            "AND (c.dunningSector = '' OR c.dunningSector IS NULL) " +
            "AND c.status = 'Active' " +
            "AND c.isDeleted = false " +
            "AND c.branch IN (:branchIds) " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND d.custPlanStatus = 'Active' " +
            "AND DATEDIFF(CURRENT_DATE, d.startDate) = (:dateDiff + COALESCE(db.debitdocGraceDays, 0))")
    List<Customers> getCustomersForDunningPrepaid(@Param(value = "dunningType") String dunningType, @Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);
    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN CustomerPackage d ON d.customers.id = c.id " +
            "join DebitDocument db on d.debitdocid=db.id "+
            "WHERE c.dunningSector = :dunningSector " +
            "AND c.dunningCategory = :dunningCategory " +
            "AND (c.dunningType = '' OR c.dunningType IS NULL) " +
            "AND c.status = 'Active' " +
            "AND c.isDeleted = false " +
            "AND c.branch IN (:branchIds) " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND d.custPlanStatus = 'Active' " +
            "AND DATEDIFF(CURRENT_DATE, d.startDate) = (:dateDiff + COALESCE(db.debitdocGraceDays, 0))")
    List<Customers> getCustomersForDunningSectorPrepaid(@Param(value = "dunningSector") String dunningSector, @Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);

    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, " +
            "c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN CustomerPackage d ON d.customers.id = c.id " +
            "join DebitDocument db on d.debitdocid=db.id "+
            "WHERE c.dunningCategory = :dunningCategory " +
            "AND (c.dunningType IS NULL OR c.dunningType = '') " +
            "AND (c.dunningSector IS NULL OR c.dunningSector = '') " +
            "And (c.status='Active')"+
            "AND c.isDeleted = false " +
            "AND c.branch IN (:branchIds) " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND FUNCTION('DATEDIFF', CURRENT_DATE, d.startDate) = (:dateDiff + COALESCE(db.debitdocGraceDays, 0))"
    )
    List<Customers> getCustomersForDunningPrepaid(@Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);

    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, " +
            "c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN DebitDocument d ON d.customer.id = c.id " +
            "WHERE c.dunningType = :dunningType " +
            "AND c.dunningCategory = :dunningCategory " +
            "AND c.dunningSector = :dunningSector " +
            "AND c.status = 'Active' " +
            "AND c.isDeleted = false " +
            "AND c.branch IN (:branchIds) " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND FUNCTION('DATEDIFF', CURRENT_DATE, d.duedate) =  (:dateDiff + COALESCE(d.debitdocGraceDays, 0)) " +
            "AND d.paymentStatus != 'Fully Paid'")
    List<Customers> getCustomersForDunningPostpaid(@Param(value = "dunningType") String dunningType, @Param(value = "dunningCategory") String dunningCategory,@Param(value = "dunningSector") String dunningSector, @Param(value = "dateDiff") Integer dateDiff , @Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);

    @Query("select DISTINCT new Customers(c.id,c.mobile,c.email,c.countryCode,c.username,c.mvnoId,c.status, c.custtype,c.walletbalance,c.buId,c.firstname,c.lastname,c.acctno)   from Customers c, DebitDocument d where  c.dunningType =:dunningType" +
            " and  c.dunningCategory = :dunningCategory and c.dunningSector =''  or c.dunningSector is null and"
            + "  (c.status='Active') and c.isDeleted =false and d.customer.id = c.id and c.branch IN (:branchIds) and c.custtype =:customerPayType and c.isDunningEnable =true and  cast (datediff(curdate(),d.duedate) as integer) = (:dateDiff + COALESCE(d.debitdocGraceDays, 0)) and d.paymentStatus !='Fully Paid'"
            + " AND CURRENT_DATE = DATE_ADD(d.duedate, INTERVAL (:dateDiff + COALESCE(d.debitdocGraceDays, 0)))")
    List<Customers> getCustomersForDunningPostpaid(@Param(value = "dunningType") String dunningType, @Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);

    @Query(value="SELECT DISTINCT \n" +
            "    c.custid, c.mobile, c.email, c.country_code, c.username,\n" +
            "    c.MVNOID, c.cstatus, c.customertype, c.walletbalance, c.BUID,c.firstname,c.lastname,c.accountnumber\n" +
            "FROM tblcustomers c\n" +
            "JOIN TBLTDEBITDOCUMENT d ON d.subscriberid = c.custid\n" +
            "WHERE c.dunning_sector = :dunningSector\n" +
            "  AND c.dunning_category = :dunningCategory\n" +
            "  AND (c.dunning_type = '' OR c.dunning_type IS NULL)\n" +
            "  AND c.cstatus = 'Active'\n" +
            "  AND c.is_deleted = FALSE\n" +
            "  AND c.branchid IN (:branchIds)\n" +
            "  AND c.customertype = :customerPayType\n" +
            "  AND c.is_dunning_enable = TRUE\n" +
            "  AND d.payment_status != 'Fully Paid'\n" +
            "  AND d.duedate IS NOT NULL\n" +
            "  AND DATE(\n" +
            "        DATE_ADD(\n" +
            "            DATE_ADD(d.duedate, INTERVAL COALESCE(d.debitdoc_grace_days, 0) DAY),\n" +
            "            INTERVAL CAST(:dateDiff AS SIGNED) DAY\n" +
            "        )\n" +
            "    ) = CURDATE()\n" +
            "  AND EXISTS (\n" +
            "      SELECT 1\n" +
            "      FROM tbltcustomerservicemapping t\n" +
            "      WHERE t.custid = c.custid\n" +
            "        AND t.status = 'Active'\n" +
            "  )",nativeQuery = true)
    List<Object[]> getCustomersForDunningSectorPostpaid(@Param(value = "dunningSector") String dunningSector, @Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);

    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, " +
            "c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN DebitDocument d ON d.customer.id = c.id " +
            "WHERE c.dunningCategory = :dunningCategory " +
            "AND (c.dunningType = '' OR c.dunningType IS NULL) " +
            "AND (c.dunningSector = '' OR c.dunningSector IS NULL) " +
            "AND c.status = 'Active' " +
            "AND c.isDeleted = false " +
            "AND c.branch IN (:branchIds) " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND FUNCTION('DATEDIFF', CURRENT_DATE, d.duedate) = (:dateDiff + COALESCE(d.debitdocGraceDays, 0))" +
            "AND d.paymentStatus != 'Fully Paid'")
    List<Customers> getCustomersForDunningPostpaid(@Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);

    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN CustomerPackage d ON d.customers.id = c.id " +
            "join DebitDocument db on d.debitdocid=db.id "+
            "WHERE c.dunningType = :dunningType " +
            "AND c.dunningCategory = :dunningCategory " +
            "AND c.dunningSector = :dunningSector " +
            "AND c.status = 'Active' " +
            "AND c.isDeleted = false " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND c.partner.id IN (:partnerIds) " +
            "AND DATEDIFF(CURRENT_DATE, d.startDate) = (:dateDiff + COALESCE(db.debitdocGraceDays, 0))"
    )
    List<Customers> getCustomersForDunningForPartnerPrepaid(@Param(value = "dunningType") String dunningType, @Param(value = "dunningCategory") String dunningCategory,@Param(value = "dunningSector") String dunningSector, @Param(value = "dateDiff") Integer dateDiff , @Param(value = "customerPayType") String customerPayType, @Param("partnerIds") List partnerIds);

    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN CustomerPackage d ON d.customers.id = c.id " +
            "join DebitDocument db on d.debitdocid=db.id "+
            "WHERE c.dunningType = :dunningType " +
            "AND c.dunningCategory = :dunningCategory " +
            "AND (c.dunningSector = '' OR c.dunningSector IS NULL) " +
            "AND c.status = 'Active' " +
            "AND c.isDeleted = false " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND c.partner.id IN (:partnerIds) " +
            "AND DATEDIFF(CURRENT_DATE, d.startDate) =(:dateDiff + COALESCE(db.debitdocGraceDays, 0))"
    )
    List<Customers> getCustomersForDunningForPartnerPrepaid(@Param(value = "dunningType") String dunningType, @Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("partnerIds") List partnerIds);

    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN CustomerPackage d ON d.customers.id = c.id " +
            "join DebitDocument db on d.debitdocid=db.id "+
            "WHERE c.dunningSector = :dunningSector " +
            "AND c.dunningCategory = :dunningCategory " +
            "AND (c.dunningType = '' OR c.dunningType IS NULL) " +
            "AND c.status = 'Active' " +
            "AND c.isDeleted = false " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND c.partner.id IN (:partnerIds) " +
            "AND DATEDIFF(CURRENT_DATE, d.startDate) =(:dateDiff + COALESCE(db.debitdocGraceDays, 0))"
    )
    List<Customers> getCustomersForDunningSectorForPartnerPrepaid(@Param(value = "dunningSector") String dunningSector, @Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("partnerIds") List partnerIds);

    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN CustomerPackage d ON d.customers.id = c.id " +
            "join DebitDocument db on d.debitdocid=db.id "+
            "WHERE c.dunningCategory = :dunningCategory " +
            "AND (c.dunningType = '' OR c.dunningType IS NULL) " +
            "AND (c.dunningSector = '' OR c.dunningSector IS NULL) " +
            "AND c.status = 'Active' " +
            "AND c.isDeleted = false " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND c.partner.id IN (:partnerIds) " +
            "AND DATEDIFF(CURRENT_DATE, d.startDate) =(:dateDiff + COALESCE(db.debitdocGraceDays, 0))"
    )
    List<Customers> getCustomersForDunningForPartnerPrepaid(@Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("partnerIds") List partnerIds);


    @Query("select DISTINCT new Customers(c.id,c.mobile,c.email,c.countryCode,c.username,c.mvnoId , c.status, c.custtype,c.walletbalance,c.buId,c.firstname,c.lastname,c.acctno)   from Customers c, DebitDocument d where  c.dunningType =:dunningType" +
            " and  c.dunningCategory = :dunningCategory and c.dunningSector =:dunningSector and"
            + "  (c.status='Active') and c.isDeleted =false and d.customer.id = c.id and c.custtype =:customerPayType and c.isDunningEnable =true and c.partner.id IN (:partnerIds) and  cast (datediff(curdate(),d.duedate) as integer) = (:dateDiff + COALESCE(d.debitdocGraceDays, 0)) and d.paymentStatus !='Fully Paid'")
    List<Customers> getCustomersForDunningForPartnerPostpaid(@Param(value = "dunningType") String dunningType, @Param(value = "dunningCategory") String dunningCategory,@Param(value = "dunningSector") String dunningSector, @Param(value = "dateDiff") Integer dateDiff , @Param(value = "customerPayType") String customerPayType, @Param("partnerIds") List partnerIds);

    @Query("select DISTINCT new Customers(c.id,c.mobile,c.email,c.countryCode,c.username,c.mvnoId,c.status, c.custtype,c.walletbalance,c.buId,c.firstname,c.lastname,c.acctno)   from Customers c, DebitDocument d where  c.dunningType =:dunningType" +
            " and  c.dunningCategory = :dunningCategory and c.dunningSector =''  or c.dunningSector is null and"
            + "  (c.status='Active') and c.isDeleted =false and d.customer.id = c.id and c.custtype =:customerPayType and c.isDunningEnable =true and c.partner.id IN (:partnerIds) and  cast (datediff(curdate(),d.duedate) as integer) = (:dateDiff + COALESCE(d.debitdocGraceDays, 0))  and d.paymentStatus !='Fully Paid'")
    List<Customers> getCustomersForDunningForPartnerPostpaid(@Param(value = "dunningType") String dunningType, @Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("partnerIds") List partnerIds);

    @Query("select DISTINCT new Customers(c.id,c.mobile,c.email,c.countryCode,c.username,c.mvnoId,c.status, c.custtype,c.walletbalance,c.buId)   from Customers c, DebitDocument d where  c.dunningSector =:dunningSector" +
            " and  c.dunningCategory = :dunningCategory and c.dunningType ='' or c.dunningType is null  and"
            + " (c.status='Active') and  c.isDeleted =false and d.customer.id = c.id and c.custtype =:customerPayType and c.isDunningEnable =true and c.partner.id IN (:partnerIds) and  cast (datediff(curdate(),d.duedate) as integer) = (:dateDiff + COALESCE(d.debitdocGraceDays, 0)) and d.paymentStatus !='Fully Paid'")
    List<Customers> getCustomersForDunningSectorForPartnerPostpaid(@Param(value = "dunningSector") String dunningSector, @Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("partnerIds") List partnerIds);

//    @Query("select DISTINCT new Customers(c.id,c.mobile,c.email,c.countryCode,c.username,c.mvnoId,c.status, c.custtype,c.walletbalance,c.buId,c.firstname,c.lastname,c.acctno)   from Customers c, DebitDocument d where " +
//            " c.dunningCategory = :dunningCategory and c.dunningType ='' or c.dunningType is null and c.dunningSector = '' or c.dunningSector is null and"
//            + " (c.status='Active') and  c.isDeleted =false and d.customer.id = c.id and c.custtype =:customerPayType and c.isDunningEnable =true and c.partner.id IN (:partnerIds) and  cast (datediff(curdate(),d.duedate) as integer) = (:dateDiff + COALESCE(d.debitdocGraceDays, 0))  and d.paymentStatus !='Fully Paid'")
//    List<Customers> getCustomersForDunningForPartnerPostpaid(@Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("partnerIds") List partnerIds);

    @Query(value = "SELECT DISTINCT " +
            "c.custid, c.mobile, c.email, c.country_code, c.username, " +
            "c.MVNOID, c.cstatus, c.customertype, c.walletbalance, c.BUID, " +
            "c.firstname, c.lastname, c.accountnumber " +
            "FROM tblcustomers c " +
            "JOIN TBLTDEBITDOCUMENT d ON d.subscriberid = c.custid " +
            "WHERE c.dunning_category = :dunningCategory " +
            "  AND (c.dunning_type = '' OR c.dunning_type IS NULL) " +
            "  AND (c.dunning_sector = '' OR c.dunning_sector IS NULL) " +
            "  AND c.cstatus = 'Active' " +
            "  AND c.is_deleted = FALSE " +
            "  AND c.partnerid IN (:partnerIds) " +
            "  AND c.customertype = :customerPayType " +
            "  AND c.is_dunning_enable = TRUE " +
            "  AND d.payment_status != 'Fully Paid' " +
            "  AND d.duedate IS NOT NULL " +
            "  AND DATE( " +
            "        DATE_ADD( " +
            "            DATE_ADD(d.duedate, INTERVAL COALESCE(d.debitdoc_grace_days, 0) DAY), " +
            "            INTERVAL CAST(:dateDiff AS SIGNED) DAY " +
            "        ) " +
            "    ) = CURDATE() " +
            "  AND EXISTS ( " +
            "      SELECT 1 " +
            "      FROM tbltcustomerservicemapping t " +
            "      WHERE t.custid = c.custid " +
            "        AND t.status = 'Active' " +
            "  )",
            nativeQuery = true)
    List<Object[]> getCustomersForDunningForPartnerPostpaid(@Param("dunningCategory") String dunningCategory, @Param("dateDiff") Integer dateDiff, @Param("customerPayType") String customerPayType, @Param("partnerIds") List<Integer> partnerIds);


    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN CustomerPackage d ON d.customers.id = c.id " +
            "WHERE c.dunningCategory = :dunningCategory " +
            "AND c.dunningSector = :dunningSector " +
            "AND c.dunningType = :dunningType " +
            "AND c.status = 'Active' " +
            "AND c.isDeleted = false " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND c.branch IN (:branchIds) " +
            "AND DATEDIFF(d.expiryDate, CURRENT_DATE) = :dateDiff")
    List<Customers> getAdvanceNotificationForDunning(@Param(value = "dunningType") String dunningType, @Param(value = "dunningSector") String dunningSector , @Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType , @Param("branchIds") List branchIds);

    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c JOIN CustomerPackage d ON d.customers.id = c.id " +
            "WHERE c.dunningCategory = :dunningCategory " +
            "AND (c.dunningSector = '' OR c.dunningSector IS NULL) " +
            "AND c.dunningType = :dunningType " +
            "AND c.status = 'Active' " +
            "AND c.isDeleted = false " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND c.branch IN (:branchIds) " +
            "AND DATEDIFF(d.expiryDate, CURRENT_DATE) = :dateDiff")
    List<Customers> getAdvanceNotificationForDunning(@Param(value = "dunningType") String dunningType ,@Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);

    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN CustomerPackage d ON d.customers.id = c.id " +
            "WHERE c.dunningCategory = :dunningCategory " +
            "AND c.dunningSector = :dunningSector " +
            "AND (c.dunningType = '' OR c.dunningType IS NULL) " +
            "AND c.status = 'Active' " +
            "AND c.isDeleted = false " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND c.branch IN (:branchIds) " +
            "AND DATEDIFF(d.expiryDate, CURRENT_DATE) = :dateDiff")
    List<Customers> getAdvanceNotificationForDunningSector(@Param(value = "dunningSector") String dunningSector ,@Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);

    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            " JOIN CustomerPackage d ON d.customers.id = c.id  "+
            " WHERE c.dunningCategory = :dunningCategory "+
            " AND (c.dunningType IS NULL OR c.dunningType = '') "+
            " AND (c.dunningSector IS NULL OR c.dunningSector = '') "+
            " AND c.status = 'Active' "+
            " AND c.isDeleted = false   " +
            " AND c.custtype = :customerPayType " +
            " AND c.isDunningEnable = true " +
            " AND c.branch IN (:branchIds) " +
            "   AND DATEDIFF(d.expiryDate, CURRENT_DATE) = :dateDiff")
    List<Customers> getAdvanceNotificationForDunning(@Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);

    @Query("select DISTINCT new  Customers(c.id,c.mobile,c.email,c.countryCode,c.username,c.mvnoId,c.status, c.custtype,c.walletbalance,c.buId,c.firstname,c.lastname,c.acctno)   from Customers c, CustomerPackage d where c.dunningCategory = :dunningCategory " +
            "and c.dunningSector =:dunningSector and c.dunningType =:dunningType and"
            + "  (c.status='Active') and c.isDeleted =false and d.customers.id = c.id and c.custtype =:customerPayType and c.isDunningEnable =true and c.partner.id IN (:partnerIds) and  cast (datediff(d.expiryDate, curdate()) as integer) = :dateDiff ")
    List<Customers> getAdvanceNotificationForDunningForPartner(@Param(value = "dunningType") String dunningType, @Param(value = "dunningSector") String dunningSector , @Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("partnerIds") List partnerIds);

    @Query("select DISTINCT new  Customers(c.id,c.mobile,c.email,c.countryCode,c.username,c.mvnoId,c.status, c.custtype,c.walletbalance,c.buId,c.firstname,c.lastname,c.acctno)   from Customers c, CustomerPackage d where c.dunningCategory = :dunningCategory " +
            "and c.dunningSector ='' or c.dunningSector is null and c.dunningType =:dunningType and"
            + "  (c.status='Active') and c.isDeleted =false and d.customers.id = c.id and c.custtype =:customerPayType and c.isDunningEnable =true and c.partner.id IN (:partnerIds) and  cast (datediff(d.expiryDate, curdate()) as integer) = :dateDiff ")
    List<Customers> getAdvanceNotificationForDunningForPartner(@Param(value = "dunningType") String dunningType ,@Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("partnerIds") List partnerIds);

    @Query("select DISTINCT new  Customers(c.id,c.mobile,c.email,c.countryCode,c.username,c.mvnoId,c.status, c.custtype,c.walletbalance,c.buId,c.firstname,c.lastname,c.acctno)   from Customers c, CustomerPackage d where c.dunningCategory = :dunningCategory " +
            "and c.dunningSector =:dunningSector and c.dunningType ='' or c.dunningType is null   and"
            + "  (c.status='Active') and c.isDeleted =false and d.customers.id = c.id and c.custtype =:customerPayType and c.isDunningEnable =true and c.partner.id IN (:partnerIds) and  cast (datediff(d.expiryDate, curdate()) as integer) = :dateDiff ")
    List<Customers> getAdvanceNotificationForDunningSectorForPartner(@Param(value = "dunningSector") String dunningSector ,@Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("partnerIds") List partnerIds);

    @Query("select DISTINCT new  Customers(c.id,c.mobile,c.email,c.countryCode,c.username,c.mvnoId,c.status, c.custtype,c.walletbalance,c.buId,c.firstname,c.lastname,c.acctno)   from Customers c, CustomerPackage d where c.dunningCategory = :dunningCategory " +
            " and c.dunningType ='' or c.dunningSector is null and c.dunningSector ='' or c.dunningSector is null  and"
            + "  (c.status='Active') and c.isDeleted =false and d.customers.id = c.id and c.custtype =:customerPayType and c.isDunningEnable =true and c.partner.id IN (:partnerIds) and  cast (datediff(d.expiryDate, curdate()) as integer) = :dateDiff ")
    List<Customers> getAdvanceNotificationForDunningForPartner(@Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("partnerIds") List partnerIds);





    @Query(value = "select count(*) from tblcustomers t where t.is_deleted=false and t.plangroupid = :planGroupId", nativeQuery = true)
	Integer countByPlanGroupId(@Param("planGroupId")Integer planGroupId);

    @Query(value = "select * from tblcustomers t where t.is_deleted=false AND t.customertype = :type AND t.parentcustid IS NULL", nativeQuery = true)
    List<Customers> findParentCustomerList(@Param("type") String type);

    @Query(value = "select * from tblcustomers t where t.is_deleted=false AND t.customertype = :type AND t.parentcustid IS NULL and MVNOID in :mvnoIds", nativeQuery = true)
    List<Customers> findParentCustomerList(@Param("type") String type, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tblcustomers t where t.is_deleted=false AND t.customertype = :type AND t.parentcustid IS NULL and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
    List<Customers> findParentCustomerList(@Param("type") String type, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tblcustapprove t where t.current_staff =:s1", nativeQuery = true)
    Long findMinimumApprovalReuqestForTerminationByStaff(@Param("s1") String staffUsername);

    @Query(value = "select * from tblcustomers t where t.servicearea_id IN :serviceAreaIds and partnerid != 1 and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
	List<Customers> findByServiceAreaIdIn(@Param("serviceAreaIds") List<Long> serviceAreaIds,@Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

	@Query(value = "select * from tblcustomers t where t.servicearea_id IN :serviceAreaIds and partnerid != 1 and MVNOID in :mvnoIds", nativeQuery = true)
	List<Customers> findByServiceAreaIdIn(@Param("serviceAreaIds") List<Long> serviceAreaIds,@Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblcustomercafassignment t where t.staff_id =:s1 and t.custpackage_id = :s2", nativeQuery = true)
    Long findMinimumApprovalReuqestForChangeCustDiscountByStaff(@Param("s1") Integer id,@Param("s2") Integer custpackage_id);

    List<Customers> findAllByMobileAndStatusAndIsDeletedIsFalseOrderByIdDesc(String mobile,String status);

    List<Customers> findByMobileAndPartner_IdAndStatusAndIsDeletedIsFalse(String mobile,Integer ParnterId, String status);

    List<Customers> findAllByUsernameAndStatusAndIsDeletedIsFalseOrderByIdDesc(String username,String status);

    List<Customers> findByUsernameAndPartner_IdAndStatusAndIsDeletedIsFalse(String username,Integer ParnterId, String status);
    @Query(value = "select * from tblcustomers t where t.is_deleted=false and t.reject_reason_id = :rejectReasonId", nativeQuery = true)
    List<Customers> findByRejectReasonId(@Param("rejectReasonId")Long reject_reason_id);
    @Query(value = "select * from tblcustomers t where t.is_deleted=false and t.reject_reason_id = :rejectSubReasonId", nativeQuery = true)
    List<Customers> findByRejectSubReasonId(@Param("rejectSubReasonId")Long reject_sub_reason_id);

    @Query(value = "select pan from tblcustomers t where t.pan =:pan and t.business_type = 'Retail'",nativeQuery = true)
    List<String> getpanforretail(@Param("pan")String pan);

    @Query(value = "select pan from tblcustomers t where t.pan =:pan and t.business_type = 'Enterprise'",nativeQuery = true)
    List<String> getpanforenterprise(@Param("pan")String pan);

    @Query(value = "select * from tblcustomers t where t.servicearea_id IN :serviceAreaIds", nativeQuery = true)
    List<Integer> findByServiceAreaIds(@Param("serviceAreaIds") List<Integer> serviceAreaIds);


    List<Customers> findAllByParentCustomers(Customers parentId);

    @Query(value = "SELECT t.custid FROM tblcustomers t WHERE t.parentcustid =:parentId", nativeQuery = true)
    List<Integer> findAllByParentId(Integer parentId);

    @Query(value = "select parentcustid from tblcustomers t where parentcustid is not null", nativeQuery = true)
   List<Integer> findAllByPareCustomersList();

//    List<Integer> findAllIdByIsDeletedIsFalseAndStatusIn(List<String> status);

    @Query("select t.id from Customers t where t.status IN :status")
    List<Integer> findAllIdByIsDeletedIsFalseAndStatusIn(@Param("status")List<String> status);

    @Query("select t.id from Customers t where t.status IN :status and custtype!='Postpaid'")
    List<Integer> findAllIdByIsDeletedIsFalseAndStatusInAndCustomerTypeIsNotPostpaid(@Param("status")List<String> status);


    @Query(value = "SELECT t.firstname, t.cstatus FROM adoptconvergebss.tblcustomers t WHERE t.lead_id IN :ids order by lead_id desc", nativeQuery = true)
    List<Map<String, String>> getCustomerCAFStatus(List<Long> ids);

	List<Customers> findByUsernameOrPanOrMobileOrEmailAndIsDeletedIsFalse(String username, String pan,String mobile, String email);

    @Query(value = "select t.id ,t.username ,t.title, t.firstname , t.lastname , t.servicearea.name , t.mobile , t.acctno , t.status, t.nextTeamHierarchyMapping , t.staffId , t.password from Customers t where t.username =:username and t.mvnoId =:mvnoId")
    List<Object[]> findCustomerObjectUsingUsername(String username,Integer mvnoId);

    @Query(value = "select t.id ,t.username, t.password ,t.mobile , t.countryCode , t.email , t.custtype from Customers t where  t.id =:id")
    List<Object[]> findCustomerObjectUsingId(Integer id);

    @Query(value ="SELECT t.username FROM tblcustomers t WHERE t.branchid = :branchId",nativeQuery = true)
    List<String> findUsernameByBranchId(@Param("branchId") Long branchId);

    @Query(value = "select custid from tblcustomers t where t.cstatus = 'active' and mvnoid in :mvnoIds ",nativeQuery = true)
    List<Integer> findAllCustIdsByMvno(List<Integer> mvnoIds);

//    long countByIsDeletedFalseAndParentCustomersIdAndInvoiceTypeAndMvnoIdAndBuIdIn(
//            Long parentId, String invoiceType, Integer mvnoId, Integer mvnoIdFromCurrentStaff, List<Long> buIds);
    @Query(value = "SELECT t.mvnoId from Customers t where t.id= :Id")
    Integer findMvnoIdById(Integer Id);

    List<Customers> findAllByIdIn(List<Integer> custIds);


    @Query(value = "select custid from tblcustomers t where t.mvno_deactivation_flag =true and t.MVNOID=:MVNOID", nativeQuery = true)
    List<Integer> findCustomerIdsbyMvnoDeactivationFlag(@Param("MVNOID") Integer MVNOID);


    @Query("SELECT t.username FROM Customers t WHERE t.id IN :ids")
    List<String> findNamesByIds(@Param("ids") List<Integer> ids);


    @Query("SELECT c FROM Customers c WHERE c.parentCustomers.id = :parentCustomersId AND c.parentExperience = :parentExperience")
    List<Customers> findByParentCustomersIdAndParentExperience(@Param("parentCustomersId") Integer parentCustomersId, @Param("parentExperience") String parentExperience);


    @Query("SELECT DISTINCT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, " +
            "c.custtype, c.walletbalance, c.buId,c.firstname,c.lastname,c.acctno) " +
            "FROM Customers c " +
            "JOIN DebitDocument d ON d.customer.id = c.id " +
            "WHERE c.status = 'Active' " +
            "AND c.branch IN (:branchIds) " +
            "AND c.dunningCategory = :dunningCategory " +
            "AND (c.dunningType IS NULL OR c.dunningType = '') " +
            "AND (c.dunningSector IS NULL OR c.dunningSector = '') " +
            "AND c.isDeleted = false " +
            "AND c.custtype = :customerPayType " +
            "AND c.isDunningEnable = true " +
            "AND FUNCTION('DATEDIFF', d.duedate, CURRENT_DATE) = :dateDiff " +
            "AND d.paymentStatus != 'Fully Paid'")
    List<Customers> getAdvanceNotificationForPostPaidDunning( @Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);
    @Query(value = "SELECT DISTINCT \n" +
            "       c.custid, \n" +
            "       c.mobile, \n" +
            "       c.email, \n" +
            "       c.country_code, \n" +
            "       c.username, \n" +
            "       c.mvnoid, \n" +
            "       c.cstatus, \n" +
            "       c.customertype, \n" +
            "       c.walletbalance, \n" +
            "       c.buid, \n" +
            "       c.firstname, \n" +
            "       c.lastname, \n" +
            "       c.accountnumber \n" +
            "  FROM tblcustomers c \n" +
            " INNER JOIN tbltdebitdocument d \n" +
            "        ON d.subscriberid = c.custid \n" +
            " WHERE c.cstatus           = 'Active' \n" +
            "   AND c.branchid          IN (:branchIds) \n" +
            "   AND c.dunning_category  = :dunningCategory \n" +
            "   AND (c.dunning_type     IS NULL OR c.dunning_type = '') \n" +
            "   AND (c.dunning_sector   IS NULL OR c.dunning_sector = '') \n" +
            "   AND c.is_deleted        = FALSE \n" +
            "   AND c.customertype      = :customerPayType \n" +
            "   AND c.is_dunning_enable = TRUE \n" +
            "   AND d.payment_status   != 'Fully Paid' \n" +
            "   AND d.duedate          IS NOT NULL \n" +
            "   AND DATEDIFF(d.duedate, CURDATE()) = :dateDiff \n" +
            "   AND EXISTS ( \n" +
            "           SELECT 1 \n" +
            "             FROM tbltcustomerservicemapping t \n" +
            "            WHERE t.custid = c.custid \n" +
            "              AND t.status = 'Active' \n" +
            "       )",
            nativeQuery = true)
    List<Object[]> getAdvanceNotificationForPostPaidDunningNative(
            @Param("dunningCategory") String dunningCategory,
            @Param("dateDiff") Integer dateDiff,
            @Param("customerPayType") String customerPayType,
            @Param("branchIds") List<Long> branchIds);



//    @Query("select DISTINCT new Customers(c.id,c.mobile,c.email,c.countryCode,c.username,c.mvnoId,c.status, c.custtype,c.walletbalance,c.buId,c.firstname,c.lastname,c.acctno)   from Customers c, DebitDocument d where " +
//            "c.status='Active' and  c.dunningCategory = :dunningCategory and c.dunningType ='' or c.dunningType is null and c.dunningSector = :dunningsector and"
//            + "  c.isDeleted =false and d.customer.id = c.id and c.custtype =:customerPayType and c.branch IN (:branchIds) and c.isDunningEnable =true and cast (datediff(d.duedate,curdate()) as integer) = :dateDiff  and d.paymentStatus !='Fully Paid'")
//    List<Customers> getAdvanceNotificationForPostPaidDunningDunningSector( @Param(value = "dunningsector") String dunningsector ,@Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);
    @Query(value = "SELECT DISTINCT \n" +
            "       c.custid, \n" +
            "       c.mobile, \n" +
            "       c.email, \n" +
            "       c.country_code, \n" +
            "       c.username, \n" +
            "       c.mvnoid, \n" +
            "       c.cstatus, \n" +
            "       c.customertype, \n" +
            "       c.walletbalance, \n" +
            "       c.buid, \n" +
            "       c.firstname, \n" +
            "       c.lastname, \n" +
            "       c.accountnumber \n" +
            "  FROM tblcustomers c \n" +
            "  JOIN tbltdebitdocument d \n" +
            "    ON d.subscriberid = c.custid \n" +
            " WHERE c.cstatus           = 'Active' \n" +
            "   AND c.dunning_category  = :dunningCategory \n" +
            "   AND (c.dunning_type     = '' OR c.dunning_type IS NULL) \n" +
            "   AND c.dunning_sector    = :dunningsector \n" +
            "   AND c.is_deleted        = FALSE \n" +
            "   AND c.customertype          = :customerPayType \n" +
            "   AND c.branchid          IN (:branchIds) \n" +
            "   AND c.is_dunning_enable = TRUE \n" +
            "   AND DATEDIFF(d.duedate, CURDATE()) = :dateDiff \n" +
            "   AND d.payment_status   != 'Fully Paid' ",
            nativeQuery = true)
    List<Object[]> getAdvanceNotificationForPostPaidDunningDunningSector(
            @Param("dunningsector") String dunningsector,
            @Param("dunningCategory") String dunningCategory,
            @Param("dateDiff") Integer dateDiff,
            @Param("customerPayType") String customerPayType,
            @Param("branchIds") List<Long> branchIds);



    @Query("select DISTINCT new Customers(c.id,c.mobile,c.email,c.countryCode,c.username,c.mvnoId,c.status, c.custtype,c.walletbalance,c.buId,c.firstname,c.lastname,c.acctno)   from Customers c, DebitDocument d where " +
            "c.status='Active' and  c.dunningCategory = :dunningCategory and c.dunningType =:dunningType  and c.dunningSector = :dunningCategory and"
            + "  c.isDeleted =false and d.customer.id = c.id and c.custtype =:customerPayType and c.branch IN (:branchIds) and c.isDunningEnable =true and cast (datediff(d.duedate,curdate()) as integer) = :dateDiff  and d.paymentStatus !='Fully Paid'")
    List<Customers> getAdvanceNotificationForPostPaidDunningDunningType( @Param(value = "dunningType") String dunningType ,@Param(value = "dunningCategory") String dunningCategory, @Param(value = "dateDiff") Integer dateDiff,@Param(value = "customerPayType") String customerPayType, @Param("branchIds") List branchIds);


    @Query("SELECT new Customers(c.id, c.mvnoId, c.buId, c.nextBillDate,c.walletbalance,c.nextQuotaResetDate,c.custtype) " +
            "FROM Customers c " +
            "WHERE c.id = :custId")
    Customers  findById1(@Param("custId") Integer custId);


    @Modifying
    @Query(value = "UPDATE  tblcustomers t SET t.walletbalance=:walletbalance,t.nextBillDate=:nextBillDate where t.custid=:custId", nativeQuery = true)
    void updateWalletAndNextBillDate(@Param("custId") Integer custId,@Param("walletbalance") Double walletbalance, @Param("nextBillDate") LocalDate nextBillDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE  tblcustomers t SET t.nextBillDate=:nextBillDate where t.custid=:custId", nativeQuery = true)
    void updateNextBillDate(@Param("custId") Integer custId, @Param("nextBillDate") LocalDate nextBillDate);

    @Modifying
    @Query(value = "UPDATE  tblcustomers t SET t.nextquotaresetdate=:nextquotaresetdate where t.custid=:custId", nativeQuery = true)
    void updateNextQuotaDate(@Param("custId") Integer custId, @Param("nextquotaresetdate") LocalDate nextquotaresetdate);


    @Modifying
    @Transactional
    @Query(value = "UPDATE  tblcustomers t SET t.walletbalance=:walletbalance,t.nextBillDate=:nextBillDate,nextquotaresetdate=:nextQuotaResetDate where t.custid=:custId", nativeQuery = true)
    void updateWalletAndNextBillDateAndNextQuotaResetDate(@Param("custId") Integer custId,@Param("walletbalance") Double walletbalance, @Param("nextBillDate") LocalDate nextBillDate,@Param("nextQuotaResetDate") LocalDate nextQuotaResetDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE  tblcustomers t SET t.nextBillDate=:nextBillDate,nextquotaresetdate=:nextQuotaResetDate where t.custid=:custId", nativeQuery = true)
    void updateNextBillDate(@Param("custId") Integer custId, @Param("nextBillDate") LocalDate nextBillDate,@Param("nextQuotaResetDate") LocalDate nextQuotaResetDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE  tblcustomers t SET t.nextBillDate=:nextBillDate,nextquotaresetdate=:nextQuotaResetDate,BILLDAY=:billDay where t.custid=:custId", nativeQuery = true)
    void updateNextBillDateAndBilldayAndNextQuotaResetDate(@Param("custId") Integer custId, @Param("nextBillDate") LocalDate nextBillDate,@Param("nextQuotaResetDate") LocalDate nextQuotaResetDate, @Param("billDay") Integer billDay);

    @Query(value = "select t.custid, t.custname, t.username, t.isinvoicestop from tblcustomers t where t.username=:username AND t.is_deleted=false AND t.cstatus ='Active'", nativeQuery = true)
    Object[] findCustomersByUserNameActiveOnly(@Param("username") String username);

    @Query(value = "SELECT c.accountNumber FROM tblcustomers c WHERE c.MVNOID = :mvnoId  order by c.createdate desc limit 1",nativeQuery = true)
    String findLatestAccountNumberByMvnoId(@Param("mvnoId") Integer mvnoId);
    @Query(value = "SELECT t.accountnumber FROM tblcustomers t WHERE t.MVNOID = :mvnoId ORDER BY t.custid DESC LIMIT 1",
            nativeQuery = true)
    String findLatestCustomerByMvnoId(@Param("mvnoId") Integer mvnoId);

    @Query(value = "select c.mobile AS customerMsisdn , c.username AS userName ,c.password AS passWord ," +
            " c.accountnumber AS accountNumber, c.walletbalance AS walletBalance, c.firstname AS firstName," +
            "c.lastname AS lastName, c.cstatus, c.custid AS custId, c.MVNOID AS mvnoId, c.BUID AS buId,"+
            "c.customertype AS custtype from tblcustomers c " +
            "where c.accountnumber =:accountNumber and c.mvnoId = :mvnoId",nativeQuery = true)
    List<Object[]> findCustomersByAccountNumber(@Param("accountNumber") String accountNumber, @Param("mvnoId") Integer mvnoId);

    @Query(value = "SELECT c.mobile FROM tblcustomers c WHERE c.custid=:custid",nativeQuery = true)
    String findMobileNmuber(@Param("custid") Integer custId);
    @Query("SELECT c.id FROM Customers c WHERE c.acctno = :accountNo and c.mvnoId = :mvnoId")
    List<Long> findCustomerIdByAccountNo(@Param("accountNo") String accountNo, @Param("mvnoId") Integer mvnoId);

    @Query(value = "SELECT c.accountnumber AS accountNumber, c.mobile AS customerMsisdn, c.username AS userName, c.email AS email " +
            "FROM tblcustomers c " +
            "WHERE c.custid = :custId", nativeQuery = true)
    List<Object[]> findCustomersById(@Param("custId") Integer custId);

    @Query("SELECT c FROM Customers c WHERE c.acctno = :accountNo and c.mvnoId = :mvnoId")
    Customers findCustomerIdByAccNo(@Param("accountNo") String accountNo, @Param("mvnoId") Integer mvnoId);

    @Modifying
    @Transactional
    @Query("UPDATE Customers c SET c.isDunningActivate = true, c.lastDunningDate = :now WHERE c.id = :customerId")
    void updateDunningStatus(@Param("customerId") Integer customerId, @Param("now") LocalDateTime now);

    @Query("SELECT c.acctno, c.mvnoId FROM Customers c WHERE c.id = :custId")
    Object[] findAccountNoAndMvnoIdByCustId(@Param("custId") Integer custId);


    @Query("SELECT new com.adopt.apigw.pojo.NewCustPojos.CustFieldsPojo( " +
            "c.id, c.custname, c.servicearea.id, c.custtype, c.mobile, c.altmobile, c.phone, c.countryCode, " +
            "c.email, c.organisation, c.businessType, c.title, c.firstname, c.lastname, " +
            "c.status, c.istrialplan,c.plangroup.planGroupId, c.parentCustomers.id, c.isDunningEnable, " +
            "c.isNotificationEnable, c.username, c.popid, c.oltid, c.branch, c.partner.id," +
            "c.currentAssigneeId, c.nextfollowupdate, c.birthDate, c.billableCustomerId, " +
            "c.parentExperience, c.customerType, c.customerSector, " +
            "c.contactperson, c.cafno, c.acctno, c.calendarType, c.billday, c.nextBillDate, " +
            "c.invoiceType, c.firstActivationDate, c.pan, c.fax, c.customerbillingid, " +
            "c.dunningCategory, c.customerSubSector, c.outstanding, c.nextQuotaResetDate, " +
            "c.blockNo, c.walletbalance, c.servicearea.name, c.valleyType, c.latitude, " +
            "c.longitude, c.framedIpBind, c.nasIpAddress, c.nasPort, c.ipPoolNameBind, " +
            "c.vlan_id, c.framedIp, c.framedIpv6Address, c.maxconcurrentsession, " +
            "c.framedroute, c.framedIPNetmask, c.framedIPv6Prefix, c.gatewayIP, " +
            "c.primaryDNS, c.primaryIPv6DNS, c.secondaryDNS, c.secondaryIPv6DNS, " +
            "c.delegatedprefix, c.nasPortId, c.mac_provision, c.mac_auth_enable, " +
            "c.mvnoId, c.buId, c.activationByName, c.createdByName) " +
            "FROM Customers c " +
            "WHERE c.id = :id AND c.isDeleted = false")
    CustFieldsPojo findCustomerDetailsByIdAndIsDeletedIsFalse(@Param("id") Integer id);



    @Query("SELECT new Customers(c.id, c.mvnoId, c.buId, c.nextTeamHierarchyMapping) " +
            "FROM Customers c " +
            "WHERE c.id = :customerId")
   Customers  findByIdLight(@Param("customerId") Integer CustomerId);

    @Query(value = "SELECT t.MVNOID, t.accountnumber FROM tblcustomers t WHERE t.MVNOID in :MVNOID and t.custid = (" +
            "select max(t2.custid) FROM tblcustomers t2 WHERE  t2.MVNOID = t.MVNOID)",
            nativeQuery = true)
    List<Object[]> findLatestCustAccNumByMvnoId(@Param("MVNOID") List<Integer> mvnoId);
    @Query("select count(c) > 1 from Customers c where username = :username and is_deleted=0")
    boolean customerUsernameIsAlreadyExist(@Param("username") String username);
    @Query("select count(c) > 0 from Customers c where username = :username and is_deleted=0 and MVNOID in :mvnoIds")
    boolean customerUsernameIsAlreadyExist(@Param("username") String username, @Param("mvnoIds") List mvnoIds);

    @Query("SELECT new Customers(c.id, c.mobile, c.email, c.countryCode, c.username, c.mvnoId, c.status, c.custtype,c.walletbalance, c.buId) " +
            "FROM Customers c " +
            "WHERE c.id = :customerId")
    Optional<Customers>  findCustomerById(@Param("customerId") Integer customerId);
    @Modifying
    @Transactional
    @Query("UPDATE Customers c SET c.graceDay = :graceDays WHERE c.id = :custId")
    void updateGracedays(Integer custId, Integer graceDays);

    Optional<Customers> findByMvnoIdAndMobileAndStatus(Integer mvnoId , String mobileNumber,String status);

    @Query("SELECT  lcoId FROM Customers c WHERE c.id = :customerId")
    Integer findLcoIdByCustId(Integer customerId);

    @Query("SELECT c.acctno FROM Customers c WHERE c.id = :customerId")
    String findAcctnoById(@Param("customerId") Integer customerId);

    @Query("SELECT c.id, c.username, c.firstname, c.lastname, c.partner.id, c.acctno, c.countryCode," +"c.createdById , c.custtype,c.status "+
            "FROM Customers c WHERE c.id = :customerId")
    Object[] findBasicCustomerInfoById(@Param("customerId") Integer customerId);

    @Query("SELECT c.currency FROM Customers c WHERE c.id = :id")
    Optional<String> findCurrencyByCustomerId(@Param("id") Integer id);

    @Query("select c.mvnoId from Customers c where c.id = :custId")
    Integer getCustomerMvnoIdByCustId(@Param("custId") Integer custId);
    @Query("SELECT  c.status FROM Customers c WHERE c.id = :customerId")
    String findStatusCustId(Integer customerId);

    @Modifying
    @Query("UPDATE Customers c SET c.password = :newPassword WHERE c.username = :username")
    void updatePasswordHistory(
            @Param("newPassword") String newPassword,
            @Param("username") String username);


    @Query("SELECT c FROM Customers c WHERE LTRIM(c.username) = :name")
    List<Customers> findByTrimmedNameIgnoreCase(@Param("name") String name);

    @Query("SELECT  c.custtype FROM Customers c WHERE c.id = :customerId")
    String findCustType(Integer customerId);

    @Query(
            value = "SELECT c.custid " +
                    "FROM tblcustomers c " +
                    "WHERE c.cstatus = 'NewActivation' " +
                    "  AND c.is_deleted = 0 " +
                    "  AND NOT EXISTS ( " +
                    "      SELECT 1 " +
                    "      FROM tblcustpackagerel cpr " +
                    "      JOIN tblmpostpaidplan p " +
                    "        ON p.POSTPAIDPLANID = cpr.planid " +
                    "      WHERE cpr.custid = c.custid " +
                    "        AND cpr.is_delete = 0 " +
                    "        AND p.is_deleted = 0 " +
                    "        AND p.plangroup IN ('Registration','Registration and Renewal','Renew') " +
                    "        AND cpr.enddate >= CURRENT_DATE " +
                    "  )",
            nativeQuery = true
    )
    List<Integer> findCustomersWithAllPlansExpired();

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Customers c " +
            "SET c.status = 'Closed', " +
            "    c.cafApproveStatus = 'Closed', " +
            "    c.remarks = CONCAT('Customer closed due to plan expiry on ', CURRENT_TIMESTAMP) " +
            "WHERE c.id IN :custIds")
    int closeCustomersByIds(@Param("custIds") List<Integer> custIds);


    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            "UPDATE CustPlanMappping c " +
                    "SET c.custPlanStatus = 'STOP', " +
                    "    c.status = 'STOP', " +
                    "    c.stopServiceDate = CURRENT_DATE, " +
                    "    c.serviceHoldDate = CURRENT_TIMESTAMP, " +
                    "    c.remarks = CONCAT('Service stopped due to customer closure on ', CURRENT_TIMESTAMP) " +
                    "WHERE c.customer.id IN :custIds "
    )
    int stopAllPlansByCustomerIds(@Param("custIds") List<Integer> custIds);





    @Query(value = "SELECT COUNT(*) FROM tblcustomers t WHERE t.username = :username AND t.parentcustid IS NOT NULL", nativeQuery = true)
    BigInteger checkChildUserByUsername(@Param("username") String username);

   Boolean existsByUsernameAndParentCustomersIsNotNull( String username);

    @Query(value = "select username from tblcustomers where email= :email limit 1",nativeQuery = true)
    Optional<String> findCustomerNameByEmail(@Param("email") String email);
    @Query(value = "select username from tblcustomers where mobile= :phone limit 1",nativeQuery = true)
    Optional<String> findCustomerNameByPhone(@Param("phone") String phone);
}
