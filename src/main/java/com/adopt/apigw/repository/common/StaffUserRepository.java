package com.adopt.apigw.repository.common;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.common.StaffUser;

@Repository
//@JaversSpringDataAuditable
public interface StaffUserRepository extends JpaRepository<StaffUser, Integer>, QuerydslPredicateExecutor<StaffUser> {

    @Query(value = "select * from tblstaffuser where lower(username) like '%' :search '%' order by staffid",
            countQuery = "select count(*) from tblstaffuser where lower(username) like '%' :search  '%'",
            nativeQuery = true)
    Page<StaffUser> searchEntity(@Param("search") String searchText, Pageable pageable);

    @Query(value = "select * from tblstaffuser where (lower(username) like '%'  :search '%' ) and partnerid = :partnerid order by staffid",
            countQuery = "select count(*) from tblstaffuser where (lower(username) like '%' :search  '%') and partnerid= :partnerid",
            nativeQuery = true)
    Page<StaffUser> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("partnerid") Integer partnerid);

    List<StaffUser> findByStatusAndIsDeleteIsFalse(String status);

//    @Query("select new StaffUser(c.id,c.email,c.username,c.mvnoId)   from StaffUser c, CustomerPackage d where "
//            + " c.isDelete =false  and  cast (datediff(d.endDate, curdate()) as integer) = :dateDiff ")
//    List<StaffUser> getDocumentForDunning( @Param(value = "dateDiff") Integer dateDiff);

    @Query(nativeQuery = true, value = "(select t.* , t3.username from tblstaffuser t \n" +
            "inner join tblcustdocdetails t2 \n" +
            "on  t2.CREATEDBYSTAFFID  = t.staffid \n" +
            "join tblcustomers t3 \n" +
            "on t2.cust_id = t3.custid \n"+
            "where t.is_delete = 0  and datediff(t2.ENDDATE  , current_date()))")
    List<StaffUser> getDocumentForDunning(@Param(value = "dateDiff") Integer dateDiff);




    List<StaffUser> findByStatusAndPartneridAndIsDeleteIsFalse(String status, Integer partnerid);

    List<StaffUser> findByUsername(String username);

    StaffUser findByUsernameAndMvnoId(String username, Integer mvnoId);

    List<StaffUser> findByUsernameAndStatusAndIsDeleteIsFalse(String username, String status);

    @Query(value = "select * from tblstaffuser t where t.username=:username And t.is_delete=false And t.sstatus='ACTIVE'", nativeQuery = true)
    StaffUser findUsername(@Param("username") String username);

    @Query(value = "select * from tblstaffuser t where t.username=:username And t.is_delete=false", nativeQuery = true)
    StaffUser findStaffUserByUsername(@Param("username") String username);

    @Query(value = "select t4.* from tblstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
            "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.partnerid = :s1 and t4.MVNOID = :mvnoId", nativeQuery = true, countQuery = "select count(*) from tblstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
            "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.partnerid = :s1 and t4.MVNOID = :mvnoId")
    Page<StaffUser> findByPartneridAndIsDeleteIsFalse(@Param("s1") Integer PartnerId, Pageable pageable, @Param("mvnoId") Integer mvnoId);

    @Query(value = "SELECT * FROM tblstaffuser WHERE username like '%' :searchText '%' ", nativeQuery = true)
    public List<StaffUser> findAllUsername(@Param("searchText") String searchText);

    @Query("select t from StaffUser t where t.isDelete=false")
    List<StaffUser> findAll();

    @Query("update StaffUser t set t.isDelete=true where t.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    @Query(nativeQuery = true, value = "select * from tblstaffuser t \n" +
            "inner join tblstaffrolerel t2 \n" +
            "on t2.roleid = :s1 and t2.staffid  = t.staffid \n" +
            "where t.is_delete = 0 and t.sstatus = 'Active'")
    List<StaffUser> findStaffByRole(@Param("s1") Long s1);

    @Query(nativeQuery = true, value = "select * from tblstaffuser t \n" +
            "inner join tblstaffrolerel t2 \n" +
            "on t2.roleid = :s1 and t2.staffid  = t.staffid \n" +
            "where t.is_delete = 0 and t.sstatus = 'Active' and t.partnerid = :s2")
    List<StaffUser> findStaffByRoleAndPartnerid(@Param("s1") Long s1, @Param("s2") Integer partnerid);


    @Query(value = "select t4.* from tblstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
            "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.MVNOID in :mvnoIds and t4.MVNOID !=1 and t4.lcoid IS NULL"
            , countQuery = "select count(*) from tblstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
            "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.MVNOID in :mvnoIds and t4.MVNOID !=1 and t4.lcoid IS NULL", nativeQuery = true)
    Page<StaffUser> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select t4.* from tblstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
            "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.MVNOID in :mvnoIds and t4.MVNOID !=1 and t4.lcoid=:lcoId"
            , countQuery = "select count(*) from tblstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
            "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.MVNOID in :mvnoIds and t4.MVNOID !=1 and t4.lcoid=:lcoId", nativeQuery = true)
    Page<StaffUser> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds,@Param("lcoId") Integer lcoId);

    @Query(value = "select t4.* from tblstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
            "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "left join \n" +
            "tblstaffbusinessunitrel t5  on t5.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and (t4.MVNOID in :mvnoIds and t5.businessunitid in :buIds) and t4.lcoid IS NULL",
            countQuery = "select count(*) from tblstaffuser t4 \n" +
                    "left join \n" +
                    "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
                    "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
                    "inner join tblroles t3 on t3.roleid =t.roleid \n" +
                    "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
                    "left join \n" +
                    "tblstaffbusinessunitrel t5  on t5.staffid = t4.staffid \n" +
                    "where t4.is_delete = 0 and (t4.MVNOID in :mvnoIds and t5.businessunitid in :buIds) and t4.lcoid IS NULL",
            nativeQuery = true)
    Page<StaffUser> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds, @Param("buIds") List buIds);

    @Query(value = "select t4.* from tblstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
            "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "left join \n" +
            "tblstaffbusinessunitrel t5  on t5.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and (t4.MVNOID in :mvnoIds and t5.businessunitid in :buIds) and t4.lcoid=:lcoId",
            countQuery = "select count(*) from tblstaffuser t4 \n" +
                    "left join \n" +
                    "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
                    "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
                    "inner join tblroles t3 on t3.roleid =t.roleid \n" +
                    "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
                    "left join \n" +
                    "tblstaffbusinessunitrel t5  on t5.staffid = t4.staffid \n" +
                    "where t4.is_delete = 0 and (t4.MVNOID in :mvnoIds and t5.businessunitid in :buIds) and t4.lcoid=:lcoId",
            nativeQuery = true)
    Page<StaffUser> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds, @Param("buIds") List buIds,@Param("lcoId") Integer lcoId);

    @Query(value = "select t4.* from tblstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
            "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.lcoid IS NULL", nativeQuery = true
            , countQuery = "select count(*) from tblstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
            "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.lcoid IS NULL")
    Page<StaffUser> findAll(Pageable pageable);

    @Query(value = "select t4.* from tblstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
            "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.lcoid=:lcoId", nativeQuery = true
            , countQuery = "select count(*) from tblstaffuser t4 \n" +
            "left join \n" +
            "(select t2.staffid, group_concat(t3.rolename) concatname FROM tblstaffuser t2\n" +
            "inner join tblstaffrolerel t  on t2.staffid = t.staffid\n" +
            "inner join tblroles t3 on t3.roleid =t.roleid \n" +
            "group by t2.staffid  )  srn on srn.staffid = t4.staffid \n" +
            "where t4.is_delete = 0 and t4.lcoid=:lcoId")
    Page<StaffUser> findAll(Pageable pageable,@Param("lcoId") Integer lcoId);

    @Query(nativeQuery = true, value = "select * from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and (t.MVNOID in :mvnoId and t.MVNOID != 1) and t.lcoid IS NULL", countQuery = "select count(*) from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0  and (t.MVNOID in :mvnoId and t.MVNOID != 1) and t.lcoid IS NULL")//(t.businessunitid is null or t.businessunitid in :buIds)
    Page<StaffUser> findAllByNameOrEmailOrRole(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("mvnoId") List mvnoId);
    //  Page<StaffUser> findAllByNameOrEmailOrRole(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("mvnoId") List mvnoId, @Param("buIds") List buIds);


    @Query(nativeQuery = true, value = "select * from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and (t.MVNOID in :mvnoId and t.MVNOID != 1) and t.lcoid=:lcoId", countQuery = "select count(*) from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0  and (t.MVNOID in :mvnoId and t.MVNOID != 1) and t.lcoid=:lcoId")//(t.businessunitid is null or t.businessunitid in :buIds)
    Page<StaffUser> findAllByNameOrEmailOrRole(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("mvnoId") List mvnoId,@Param("lcoId") Integer lcoId);

    @Query(nativeQuery = true, value = "select t.* from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join tblstaffbusinessunitrel t5\n" +
            "on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and (t.MVNOID in :mvnoId and t.MVNOID != 1 and t5.businessunitid in :buIds and t.lcoid IS NULL)", countQuery = "select count(*) from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join tblstaffbusinessunitrel t5\n" +
            "on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and (t.MVNOID in :mvnoId and t.MVNOID != 1 and t5.businessunitid in :buIds) and t.lcoid IS NULL")
    Page<StaffUser> findAllByNameOrEmailOrRole(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("mvnoId") List mvnoId,@Param("buIds") List buIds);


    @Query(nativeQuery = true, value = "select t.* from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join tblstaffbusinessunitrel t5\n" +
            "on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and (t.MVNOID in :mvnoId and t.MVNOID != 1 and t5.businessunitid in :buIds and t.lcoid=:lcoId)", countQuery = "select count(*) from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join tblstaffbusinessunitrel t5\n" +
            "on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and (t.MVNOID in :mvnoId and t.MVNOID != 1 and t5.businessunitid in :buIds) and t.lcoid=:lcoId")
    Page<StaffUser> findAllByNameOrEmailOrRole(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("mvnoId") List mvnoId,@Param("buIds") List buIds,@Param("lcoId") Integer lcoId);

    @Query(nativeQuery = true, value = "select t.* from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join \n" +
            "tblstaffbusinessunitrel t5  on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t5.businessunitid in :buIds and t.lcoid IS NULL", countQuery = "select count(*) from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join \n" +
            "tblstaffbusinessunitrel t5  on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t5.businessunitid in :buIds and t.lcoid IS NULL")
    Page<StaffUser> findAllByNameOrEmailOrRoleByPartner(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("s6") Integer s6, @Param("mvnoId") List mvnoId,@Param("buIds") List buIds);

    @Query(nativeQuery = true, value = "select t.* from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join \n" +
            "tblstaffbusinessunitrel t5  on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t5.businessunitid in :buIds and t.lcoid=:lcoId", countQuery = "select count(*) from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "left join \n" +
            "tblstaffbusinessunitrel t5  on t5.staffid = t.staffid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t5.businessunitid in :buIds and t.lcoid=:lcoId")
    Page<StaffUser> findAllByNameOrEmailOrRoleByPartner(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("s6") Integer s6, @Param("mvnoId") List mvnoId,@Param("buIds") List buIds,@Param("lcoId") Integer lcoId);

    @Query(nativeQuery = true, value = "select * from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t.lcoid IS NULL", countQuery = "select count(*) from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t.lcoid IS NULL")
    Page<StaffUser> findAllByNameOrEmailOrRoleByPartner(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("s6") Integer s6, @Param("mvnoId") List mvnoId);

    @Query(nativeQuery = true, value = "select * from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%' " +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t.lcoid=:lcoId", countQuery = "select count(*) from tblstaffuser t \n" +
            "left join tblstaffrolerel t2\n" +
            "on t2.staffid  = t.staffid \n" +
            "left join tblroles t3\n" +
            "on t3.roleid = t2.roleid \n" +
            "where (  t.firstname like '%' :s1 '%' or t.lastname like '%' :s2 '%' or t.email like '%' :s3 '%'" +
            " or t.username like '%' :s4 '%'" +
            " or t3.rolename like '%' :s5 '%')\n" +
            "and t.is_delete = 0 and t.partnerid = :s6 and t.MVNOID in :mvnoId and t.lcoid=:lcoId")
    Page<StaffUser> findAllByNameOrEmailOrRoleByPartner(Pageable pageable, @Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("s6") Integer s6, @Param("mvnoId") List mvnoId,@Param("lcoId") Integer lcoId);


    @Query(value = "select * from tblstaffuser where service_area_id=:id and sstatus='ACTIVE' and is_delete = false",
            countQuery = "select count(*) from tblstaffuser where service_area_id=:id and sstatus='ACTIVE' and is_delete = false",
            nativeQuery = true)
    List<StaffUser> getByServiceAreaId(@Param("id") Integer id);

    @Query(value = "select * from tblstaffuser t where t.is_delete = 0 and t.sstatus = 'ACTIVE' and (t.MVNOID in :MVNOIDS and t.MVNOID != 1)",
            countQuery = "select count(*) tblstaffuser t where t.is_delete = 0 and t.sstatus = 'ACTIVE' and (t.MVNOID in :MVNOIDS and t.MVNOID != 1)",
            nativeQuery = true)
    List<StaffUser> findAllUsername(@Param("MVNOIDS") List MVNOIDS);
    @Query(value = "select * from tblstaffuser t left join tblstaffbusinessunitrel t2 on t.staffid = t2.staffid where t.is_delete = 0 and t.sstatus = 'ACTIVE' and (t.MVNOID in :MVNOIDS and t.MVNOID != 1) and (t2.businessunitid is null or t2.businessunitid in :buIds)",
            countQuery = "select count(*) from tblstaffuser t left join tblstaffbusinessunitrel t2 on t.staffid = t2.staffid where t.is_delete = 0 and t.sstatus = 'ACTIVE' and (t.MVNOID in :MVNOIDS and t.MVNOID != 1) and (t2.businessunitid is null or t2.businessunitid in :buIds)",
            nativeQuery = true)
    List<StaffUser> findAllUsername(@Param("MVNOIDS") List MVNOIDS, @Param("buIds") List buIds);

    List<StaffUser> findByIdAndStatusAndIsDeleteIsFalseAndMvnoIdIn(Integer id, String status, List mvnoIs);

    List<StaffUser> StatusAndIsDeleteIsFalseAndMvnoIdIn(String status, List mvnoIs);

    // List<StaffUser> findBystaffId(List<Integer> staffserviceidList);

    List<StaffUser> findByIdIn(List<Integer> staffserviceidList);
    //List<StaffUser> findByIdAndStatusAndIsDeleteIsFalse(Integer id, String status);
    List<StaffUser> findAllByStaffUserparent(StaffUser staffUser);

    @Query(value = "select count(*) from tblstaffuser c where c.username=:username and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("username") String username);

    @Query(value = "select count(*) from tblstaffuser c where c.username=:username and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("username") String username, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select t.staffid  from tblstaffservicearearel t where t.serviceareaid =:serviceareaid", nativeQuery = true)
    List<Integer> findAllByServiceareaId(@Param("serviceareaid") Integer serviceareaid);

    List<StaffUser> findByIsDeleteIsFalseOrderByIdDesc();

    @Query(value = "select * from tblstaffuser t where partnerid in :partnerids",nativeQuery = true)
    List<StaffUser> getAllStaffUserByPartnerIds(@Param("partnerids") List<Integer> partnerids);

    @Query(value = "select  c.id from StaffUser c where c.team=:teamname")
    List<Integer> findAllByTeamName(@Param("teamname") String teamname );

    @Query(value = "select s.firstname from StaffUser s where s.id=:staffid")
    String findNameById(@Param("staffid") Integer staffid);

    @Query(value = "select CONCAT(firstName, ' ', lastName) from tblstaffuser where staffid= :staffId",nativeQuery = true)
    String findStaffFullNameById(@Param("staffId") Integer staffId);

    @Query(value = "select * from tblstaffuser t where t.parent_staff_id =:parentStaffId", nativeQuery = true)
    List<StaffUser> findAllByParentStaffId(@Param("parentStaffId") Integer parentStaffId);

    @Query(value = "SELECT staffid FROM tblstaffuser t WHERE t.sstatus = 'ACTIVE' AND t.MVNOID IN :mvnoIds", nativeQuery = true)
    List<Long> findAllStaffIdsByMvnoIds(List<Long> mvnoIds);

    @Query(value = "SELECT * FROM tblstaffuser t WHERE t.sstatus = 'ACTIVE' AND t.MVNOID IN :mvnoIds", nativeQuery = true)
    List<StaffUser> findAllStaffByMvnoIds(List<Long> mvnoIds);

    @Query(value = "select staffid from tblstaffuser t where t.mvno_deactivation_flag =true and t.MVNOID=:MVNOID", nativeQuery = true)
    List<Integer> findStaffidByMvnoDeativationFlag(@Param("MVNOID") Integer MVNOID);
    @Query(value = "select * from tblstaffuser t where t.mvno_deactivation_flag =true and t.MVNOID=:MVNOID", nativeQuery = true)
    List<StaffUser> findStaffidListByMvnoDeativationFlag(@Param("MVNOID") Integer MVNOID);
    @Query("SELECT s.staffUserparent FROM StaffUser s WHERE s.id = :staffUserId")
    StaffUser findParentByStaffUserId(@Param("staffUserId") Integer staffUserId);


    @Query(value = "SELECT staffid FROM tblstaffuser t WHERE t.parent_staff_id = :parentStaffId", nativeQuery = true)
    List<Integer> findAllByParentStaffIds(@Param("parentStaffId") Integer parentStaffId);

    @Query(value = "SELECT t.department FROM tblstaffuser t WHERE t.staffid = :staffUserId", nativeQuery = true)
    Integer findByDepartment(@Param("staffUserId") Integer staffUserId);


    @Query(value = "SELECT total_collected, total_transferred, available_amount FROM tblstaffuser WHERE staffid = :id", nativeQuery = true)
    Object[] findAvailableAmountById(@Param("id") Integer id);

    @Query("SELECT sa.id FROM StaffUser t JOIN t.serviceAreaNameList sa WHERE t.id = :staffid")
    List<Long> findServiceAreaIdsByStaffId(@Param("staffid") Integer staffid);

    @Query("select t.username from StaffUser t where t.id= :staffId")
    String findStaffUserNameById(@Param("staffId") Integer staffId);

    @Query(
            value = "SELECT staffid FROM tblstaffuser " +
                    "WHERE is_delete = false " +
                    "AND LOWER(sstatus) = LOWER(:activeStatus) " +
                    "AND LOWER(username) = LOWER(:username) " +
                    "LIMIT 1",
            nativeQuery = true
    )
    Integer findActiveStaffIdByUsername(
            @Param("activeStatus") String activeStatus,
            @Param("username") String username
    );

    @Query(value = "SELECT s.parent_staff_id FROM tblstaffuser s WHERE s.staffid = :staffUserId", nativeQuery = true)
    Optional<Integer> findParentIdByStaffUserId(@Param("staffUserId") Integer staffUserId);

    @Query(value = "SELECT username, password FROM tblstaffuser WHERE staffid = :staffId AND is_delete = false", nativeQuery = true)
    Object[] findUserPassByStaffId(@Param("staffId") Integer staffId);

//    @Query(value =" SELECT DISTINCT s from tblstaffuser s left join tblstaffservicearearel sr on s.id=sr.staffid and " +
//            " join tblteamusermapping  tu on s.id=tu.staffid and tu.team_id = :teamId and sr.serviceareaid in :serviceareaId where (sr.serviceareaid IN (:serviceareaId) or sr.serviceareaid IS NULL )",nativeQuery = true )
//    List<StaffUser>getDistinctStaffByServiceAreaAndTeamId(List<Long> serviceareaId,Integer teamId);

    @Query(value = "SELECT DISTINCT s.* FROM tblstaffuser s JOIN tblteamusermapping tu ON s.staffid = tu.staffid AND tu.team_id = :teamId LEFT JOIN tblstaffservicearearel sr ON s.staffid = sr.staffid WHERE ( sr.serviceareaid IN (:serviceareaId) OR sr.serviceareaid IS NULL)   AND s.is_delete = false AND s.sstatus = 'ACTIVE'; ", nativeQuery = true)
    List<StaffUser> getDistinctStaffByServiceAreaAndTeamId(
            @Param("serviceareaId") List<Long> serviceareaId,
            @Param("teamId") Long teamId
    );


}
