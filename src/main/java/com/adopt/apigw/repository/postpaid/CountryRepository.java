package com.adopt.apigw.repository.postpaid;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.Country;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> , QuerydslPredicateExecutor<Country> {

    @Query(value = "select * from TBLMCOUNTRY where lower(name) like '%' :search  '%' order by COUNTRYID AND MVNOID= :MVNOID OR MVNOID IS NULL",
            countQuery = "select count(*) from TBLMCOUNTRY where lower(name) like '%' :search '%' AND MVNOID= :MVNOID OR MVNOID IS NULL",
            nativeQuery = true)
    Page<Country> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOID") Integer MVNOID);

    List<Country> findByStatusAndIsDeleteIsFalseOrderByIdDesc(String status);

    @Query("select t from Country t where t.isDelete=false")
    List<Country> findAll();

    @Query("select t from Country t where t.isDelete=false")
    Page<Country> findAll(Pageable pageable);

    @Query("select t from Country t where t.isDelete=false and t.mvnoId in :mvnoIds")
    Page<Country> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query("update Country b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    @Query(value = "select sum(tbl.tab) from(\n" +
            "select count(*) as tab from tblmstate t where t.COUNTRYID =:id and t.is_deleted =false\n" +
            "union all\n" +
            "select count(*) as tab from tblmcity t2 where t2.countryid=:id and t2.is_delete =false\n" +
            "union all\n" +
            "select count(*) as tab from tblmpincode t3 where t3.COUNTRYID =:id and t3.is_deleted =false \n" +
            "union all\n" +
            "select count(*) as tab from tblmsubscriberaddressrel t4 where t4.COUNTRYID =:id and t4.is_delete =false\n" +
            "union all \n" +
            "select count(*) as tab from tblmarea t5  where t5.COUNTRYID=:id and t5.is_deleted =false\n" +
            ")tbl",nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);

    Page<Country> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(String name, Pageable pageable);

    Country findByNameAndIsDeleteIsFalse(String countryName);

    Page<Country> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(String name, Pageable pageable, List<Integer> mvnoId);

    @Query(value = "select count(*) from tblmcountry t where t.NAME=:name and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmcountry t where t.NAME=:name and t.COUNTRYID =:id and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id);

    @Query(value = "select count(*) from tblmcountry t where t.NAME=:name and t.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmcountry t where t.NAME=:name and t.COUNTRYID =:id and t.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoIds")List mvnoIds);

    Country findByName(String countryName);
}
