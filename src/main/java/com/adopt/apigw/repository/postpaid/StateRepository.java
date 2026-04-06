package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.Country;
import com.adopt.apigw.model.postpaid.State;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface StateRepository extends JpaRepository<State, Integer>, QuerydslPredicateExecutor<State> {

    @Query(value = "select * from TBLMSTATE where lower(name) like '%' :search  '%' order by STATEID AND MVNOID= :MVNOID OR MVNOID IS NULL",
            countQuery = "select count(*) from TBLMSTATE where lower(name) like '%' :search '%' AND MVNOID= :MVNOID OR MVNOID IS NULL",
            nativeQuery = true)
    Page<State> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOID") Integer MVNOID);

    List<State> findAllByStatusAndIsDeletedIsFalseOrderByIdDesc(String status);

    List<State> findAllByCountryAndIsDeletedIsFalseOrderByIdDesc(Country country);


    List<State> findByName(String name);

    @Query(value = "select state.* from tblmstate state\n" +
            "left join tblmcountry country\n" +
            "on country.COUNTRYID = state.countryid\n" +
            "where state.is_deleted = false AND state.MVNOID in :mvnoIds"
            , countQuery = "select count(*) from tblmstate state\n" +
            "left join tblmcountry country\n" +
            "on country.COUNTRYID = state.countryid\n" +
            "where state.is_deleted = false AND state.MVNOID in :mvnoIds"
            , nativeQuery = true)
    Page<State> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tblmstate t where t.is_deleted=false", nativeQuery = true)
    List<State> findAll();

    @Query(value = "select * from tblmstate t where t.is_deleted=false", nativeQuery = true)
    Page<State> findAll(Pageable pageable);

    @Query(value = "select * from tblmstate state \n" +
            "left join tblmcountry country\n" +
            "on country.COUNTRYID = state.countryid\n" +
            "where " + "(state.NAME like '%' :s1 '%' \n" +
            "or state.status=:s3 \n" +
            "or country.NAME like '%' :s2 '%') \n" +
            "and state.is_deleted = false AND state.MVNOID in :MVNOID", nativeQuery = true
            , countQuery = "select count(*) from tblmstate state\n" +
            "left join tblmcountry country\n" +
            "on country.COUNTRYID = state.countryid\n" +
            "where " + " (state.NAME like '%' :s1 '%' \n" +
            "or state.status=:s3 \n" +
            "or country.NAME like '%' :s2 '%') \n" +
            "and state.is_deleted = false")
    Page<State> findAllByNameContainingIgnoreCaseOrCountry_NameContainingIgnoreCaseAndIsDeletedIsFalse(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable, @Param("MVNOID") List MVNOID);

    @Query(value = "select * from tblmstate state \n" +
            "left join tblmcountry country\n" +
            "on country.COUNTRYID = state.countryid\n" +
            "where " + "(state.NAME like '%' :s1 '%' \n" +
            "or state.status=:s3 \n" +
            "or country.NAME like '%' :s2 '%') \n" +
            "and state.is_deleted = false", nativeQuery = true
            , countQuery = "select count(*) from tblmstate state\n" +
            "left join tblmcountry country\n" +
            "on country.COUNTRYID = state.countryid\n" +
            "where " + " (state.NAME like '%' :s1 '%' \n" +
            "or state.status=:s3 \n" +
            "or country.NAME like '%' :s2 '%') \n" +
            "and state.is_deleted = false")
    Page<State> findAllByNameContainingIgnoreCaseOrCountry_NameContainingIgnoreCaseAndIsDeletedIsFalse(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable);

    @Query(value = "select count(*) from tblmstate s where s.NAME=:name and s.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmstate s where s.NAME=:name and s.COUNTRYID =:countryId and s.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyCountryAtSave(@Param("name") String name, @Param("countryId") Integer countryId);

    @Query(value = "select count(*) from tblmstate s where s.NAME=:name and s.STATEID =:id and s.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select count(*) from tblmstate s where s.NAME=:name and s.COUNTRYID =:countryId and s.STATEID =:id and s.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyCountryAtEdit(@Param("name") String name, @Param("countryId") Integer countryId, @Param("id") Integer id);


    @Query(value = "select sum(tbl.tab) from(\n" +
            "select count(*) as tab from tblmcity t2 where t2.STATEID =:id and t2.is_delete =false \n" +
            "union all\n" +
            "select count(*) as tab from tblmpincode t3 where t3.STATEID =:id and t3.is_deleted =false \n" +
            "union all\n" +
            "select count(*) as tab from tblmsubscriberaddressrel t4 where t4.STATEID =:id and t4.is_delete =false \n" +
            "union all \n" +
            "select count(*) as tab from tblmarea t5  where t5.STATEID =:id and t5.is_deleted =false \n" +
            ")tbl",nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);
}
