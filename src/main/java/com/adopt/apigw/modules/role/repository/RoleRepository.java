package com.adopt.apigw.modules.role.repository;


import com.adopt.apigw.modules.role.domain.Role;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, QuerydslPredicateExecutor<Role> {

    @Query(value = "select * from tblroles where lower(rolename) like '%' || :search || '%' order by roleid",
            countQuery = "select count(*) from tblroles where lower(rolename) like '%' || :search ",
            nativeQuery = true)
    Page<Role> searchEntity(@Param("search") String searchText, Pageable pageable);

    List<Role> findByStatus(String status);

    List<Role> findByStatusAndIdIn(String status, List<Long> roleIds);

    @Query("select t from Role t where t.isDelete=false")
    List<Role> findAll();

    @Query("select t from Role t where t.isDelete=false")
    Page<Role> findAll(Pageable page);

    @Query("select t from Role t where t.isDelete=false and MVNOID in :mvnoIds")
    Page<Role> findAll(Pageable page, @Param("mvnoIds")List mvnoIds);

    void deleteById(Long id);
    
    @Query(value = "select count(*) from tblroles where rolename=:name and is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblroles where rolename=:name and roleid =:id and is_delete=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblroles where rolename=:name and is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblroles where rolename=:name and roleid =:id and is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id);

    List<Role> findAllByRolename(String name);

    @Query(value = "select t from Role t where t.id IN (:roleIds)")
    List<Role> findRolenameByrolrids(List<Long> roleIds);

//    Page<Role> findAllByRolenameContainingAndMvnoIdAndIsDeleteIsFalse(String name, Integer mvnoId, Pageable pageable);
}
