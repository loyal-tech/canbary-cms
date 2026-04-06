package com.adopt.apigw.modules.ippool.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.ippool.domain.IPPool;

import java.util.List;

@Repository
public interface IPPoolRepository extends JpaRepository<IPPool, Long> {


    @Query(value = "select * from tblippool t where t.is_delete  = 0 ", nativeQuery = true
            , countQuery = "select count(*) from tblippool t where t.is_delete  = 0 ")
    Page<IPPool> findAll(Pageable pageable);

    @Query(value = "select * from tblippool t where t.is_delete  = 0 and t.MVNOID in :mvnoIds ", nativeQuery = true
            , countQuery = "select count(*) from tblippool t where t.is_delete  = 0  and t.MVNOID in :mvnoIds")
    Page<IPPool> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tblippool t \n" +
            "where (t.display_name like '%' :s1 '%' or t.pool_type like '%' :s2 '%' or t.pool_category like '%' :s3 '%') \n" +
            "and t.is_delete  = 0"
            , countQuery = "select count(*) from tblippool t \n" +
            "where (t.display_name like '%' :s1 '%' or t.pool_type like '%' :s2 '%' or t.pool_category like '%' :s3 '%') \n" +
            "and t.is_delete  = 0", nativeQuery = true)
    Page<IPPool> findAllByPoolNameContainingIgnoreCaseOrPoolTypeContainingIgnoreCaseOrPoolCategoryContainingIgnoreCaseAndIsDeleteIsFalse(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable);

    @Query(value = "select * from tblippool t \n" +
            "where (t.display_name like '%' :s1 '%' or t.pool_type like '%' :s2 '%' or t.pool_category like '%' :s3 '%') \n" +
            "and t.is_delete  = 0 and m.MVNOID in :mvnoIds"
            , countQuery = "select count(*) from tblippool t \n" +
            "where (t.display_name like '%' :s1 '%' or t.pool_type like '%' :s2 '%' or t.pool_category like '%' :s3 '%') \n" +
            "and t.is_delete  = 0 and m.MVNOID in :mvnoIds", nativeQuery = true)
    Page<IPPool> findAllByPoolNameContainingIgnoreCaseOrPoolTypeContainingIgnoreCaseOrPoolCategoryContainingIgnoreCaseAndIsDeleteIsFalse(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, Pageable pageable, @Param("mvnoIds") List mvnoIds);

    List<IPPool> findAllByDefaultPoolFlagIsTrueAndIsDeleteIsFalse();

    @Query(value = "select count(*) from tblippool m where m.pool_name =:name and m.is_delete=false and m.MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblippool m where m.pool_name=:name and m.pool_id=:id and m.is_delete=false and m.MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblippool m where m.pool_name =:name and m.is_delete=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name);

    @Query(value = "select count(*) from tblippool m where m.pool_name=:name and m.pool_id=:id and m.is_delete=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Integer id);

    @Query(value = "select sum(tbl.tab) from(\n" +
            "select count(*) as tab from tblippooldtls t3  where lower(t3.status) !='Free' and t3.pool_id= 26\n" +
            "union all\n" +
            "select count(*) as tab from tblcustomers t1 where t1.defaultpoolid =26 and t1.is_deleted =false\n" +
            ")tbl",nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);
}
