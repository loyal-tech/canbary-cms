package com.adopt.apigw.repository.postpaid;


import com.adopt.apigw.model.postpaid.Country;
import com.adopt.apigw.model.postpaid.Department;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> , QuerydslPredicateExecutor<Department> {

    @Query(value = "select * from tblmdepartment where lower(name) like '%' :search  '%' order by COUNTRYID AND MVNOID= :MVNOID OR MVNOID IS NULL",
            countQuery = "select count(*) from tblmdepartment where lower(name) like '%' :search '%' AND MVNOID= :MVNOID OR MVNOID IS NULL",
            nativeQuery = true)
    Page<Department> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOID") Integer MVNOID);

    List<Department> findByStatusAndIsDeleteIsFalseOrderByIdDesc(String status);

    List<Department> findAll();

    Page<Department> findAll(Pageable pageable);

    @Query("select t from Department t where t.isDelete=false and t.mvnoId in :mvnoIds")
    Page<Department> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query("update Department b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    Page<Country> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(String name, Pageable pageable);

    Department findByNameAndIsDeleteIsFalse(String countryName);

    Page<Department> findAllByNameContainingIgnoreCaseAndIsDeleteIsFalseAndMvnoIdIn(String name, Pageable pageable, List<Integer> mvnoId);

    @Query(value = "select count(*) from tblmcountry t where t.NAME=:name and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblmcountry t where t.NAME=:name and t.COUNTRYID =:id and t.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id);

    @Query(value = "select count(*) from tblmcountry t where t.NAME=:name and t.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmcountry t where t.NAME=:name and t.COUNTRYID =:id and t.is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Integer id, @Param("mvnoIds")List mvnoIds);

}
