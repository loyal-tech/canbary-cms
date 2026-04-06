package com.adopt.apigw.repository.postpaid;


import io.swagger.models.auth.In;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.model.postpaid.DunningRule;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface DunningRuleRepository extends JpaRepository<DunningRule, Integer> {

    @Query(value = "select * from tbldunningrules where lower(name) like '%' :search  '%' order by druleid",
            countQuery = "select count(*) from tbldunningrules where lower(name) like '%' :search '%'",
            nativeQuery = true)
    Page<DunningRule> searchEntity(@Param("search") String searchText, Pageable pageable);

    @Query(value = "select * from tbldunningrules where lower(name) like '%' :search  '%' order by druleid and MVNOID in :mvnoIds",
            countQuery = "select count(*) from tbldunningrules where lower(name) like '%' :search '%' and MVNOID in :mvnoIds",
            nativeQuery = true)
    Page<DunningRule> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    List<DunningRule> findByStatus(String status);

    @Query("select t from DunningRule t where t.isDelete=false")
    List<DunningRule> findAll();

    @Query("select t from DunningRule t where t.isDelete=false and t.status = 'Y'")
    List<DunningRule> findAllByStatus();


    @Query("update DunningRule b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    @Query(value = "select * from tbldunningrules dunningrule \n" +
            "where dunningrule.is_delete = false AND lcoid IS NULL"
            , countQuery = "select count(*) from tbldunningrules dunningrule \n" +
            "left join tbldunruleaction dunruleaction \n" +
            "on dunruleaction.druleid = dunningrule.druleid \n" +
            "where dunningrule.is_delete = false AND lcoid IS NULL"
            , nativeQuery = true)
    Page<DunningRule> findAll(Pageable pageable);

    @Query(value = "select * from tbldunningrules dunningrule \n" +
            "where dunningrule.is_delete = false AND dunningrule.MVNOID in :mvnoIds AND lcoid IS NULL"
            , countQuery = "select count(*) from tbldunningrules dunningrule \n" +
            "left join tbldunruleaction dunruleaction \n" +
            "on dunruleaction.druleid = dunningrule.druleid \n" +
            "where dunningrule.is_delete = false AND dunningrule.MVNOID in :mvnoIds AND lcoid IS NULL"
            , nativeQuery = true)
    Page<DunningRule> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);



    @Query(value = "select * from tbldunningrules dunningrule \n" +
            "where dunningrule.is_delete = false AND lcoid=:lcoId"
            , countQuery = "select count(*) from tbldunningrules dunningrule \n" +
            "left join tbldunruleaction dunruleaction \n" +
            "on dunruleaction.druleid = dunningrule.druleid \n" +
            "where dunningrule.is_delete = false AND lcoid=:lcoId"
            , nativeQuery = true)
    Page<DunningRule> findAll(Pageable pageable,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from tbldunningrules dunningrule \n" +
            "where dunningrule.is_delete = false AND dunningrule.MVNOID in :mvnoIds AND lcoid=:lcoId"
            , countQuery = "select count(*) from tbldunningrules dunningrule \n" +
            "left join tbldunruleaction dunruleaction \n" +
            "on dunruleaction.druleid = dunningrule.druleid \n" +
            "where dunningrule.is_delete = false AND dunningrule.MVNOID in :mvnoIds AND lcoid=:lcoId"
            , nativeQuery = true)
    Page<DunningRule> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds,@Param("lcoId") Integer lcoId);




    @Query(value = "select * from tbldunningrules dunningrule \n" +
            "where " + "(dunningrule.name like '%' :s1 '%' \n" +
            "or dunningrule.creditclass like '%' :s2 '%') \n" +
            "and dunningrule.is_delete = false AND dunningrule.MVNOID in :mvnoIds and dunningrule.lcoid IS NULL", nativeQuery = true
            , countQuery = "select count(*) from tbldunningrules dunningrule \n" +
            "left join tbldunruleaction dunruleaction \n" +
            "on dunruleaction.druleid = dunningrule.druleid \n" +
            "where " + "(dunningrule.name like '%' :s1 '%' \n" +
            "or dunningrule.creditclass like '%' :s2 '%') \n" +
            "and dunningrule.is_delete = false AND dunningrule.MVNOID in :mvnoIds and dunningrule.lcoid IS NULL")
    Page<DunningRule> findAllByNameOrCreditClassContainingIgnoreCaseAndIsDeletedIsFalse(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tbldunningrules dunningrule \n" +
            "where " + "(dunningrule.name like '%' :s1 '%' \n" +
            "or dunningrule.creditclass like '%' :s2 '%') \n" +
            "and dunningrule.is_delete = false and dunningrule.lcoid IS NULL", nativeQuery = true
            , countQuery = "select count(*) from tbldunningrules dunningrule \n" +
            "left join tbldunruleaction dunruleaction \n" +
            "on dunruleaction.druleid = dunningrule.druleid \n" +
            "where " + "(dunningrule.name like '%' :s1 '%' \n" +
            "or dunningrule.creditclass like '%' :s2 '%') \n" +
            "and dunningrule.is_delete = false and dunningrule.lcoid IS NULL")
    Page<DunningRule> findAllByNameOrCreditClassContainingIgnoreCaseAndIsDeletedIsFalse(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable);


    @Query(value = "select * from tbldunningrules dunningrule \n" +
            "where " + "(dunningrule.name like '%' :s1 '%' \n" +
            "or dunningrule.creditclass like '%' :s2 '%') \n" +
            "and dunningrule.is_delete = false AND dunningrule.MVNOID in :mvnoIds and dunningrule.lcoid=:lcoId", nativeQuery = true
            , countQuery = "select count(*) from tbldunningrules dunningrule \n" +
            "left join tbldunruleaction dunruleaction \n" +
            "on dunruleaction.druleid = dunningrule.druleid \n" +
            "where " + "(dunningrule.name like '%' :s1 '%' \n" +
            "or dunningrule.creditclass like '%' :s2 '%') \n" +
            "and dunningrule.is_delete = false AND dunningrule.MVNOID in :mvnoIds and dunningrule.lcoid=:lcoId")
    Page<DunningRule> findAllByNameOrCreditClassContainingIgnoreCaseAndIsDeletedIsFalse(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoIds") List mvnoIds,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from tbldunningrules dunningrule \n" +
            "where " + "(dunningrule.name like '%' :s1 '%' \n" +
            "or dunningrule.creditclass like '%' :s2 '%') \n" +
            "and dunningrule.is_delete = false and dunningrule.lcoid=:lcoId", nativeQuery = true
            , countQuery = "select count(*) from tbldunningrules dunningrule \n" +
            "left join tbldunruleaction dunruleaction \n" +
            "on dunruleaction.druleid = dunningrule.druleid \n" +
            "where " + "(dunningrule.name like '%' :s1 '%' \n" +
            "or dunningrule.creditclass like '%' :s2 '%') \n" +
            "and dunningrule.is_delete = false and dunningrule.lcoid=:lcoId")
    Page<DunningRule> findAllByNameOrCreditClassContainingIgnoreCaseAndIsDeletedIsFalse(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable,@Param("lcoId") Integer lcoId);

    @Query(value = "select count(*) from tbldunningrules c where c.name=:name and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

//    @Query(value = "select count(*) from tbldunningrules c where c.name=:name and c.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
//    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);
//
//    @Query(value = "select count(*) from tbldunningrules c where c.name=:name and c.druleid =:id and c.is_delete=false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))", nativeQuery = true)
//    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tbldunningrules c where c.name=:name and c.druleid =:id and c.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tbldunningrules c where c.name=:name and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tbldunningrules c where c.name=:name and c.druleid =:id and c.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);
}
