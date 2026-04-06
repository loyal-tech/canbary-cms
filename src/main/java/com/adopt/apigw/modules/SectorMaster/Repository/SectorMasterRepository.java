package com.adopt.apigw.modules.SectorMaster.Repository;

import com.adopt.apigw.modules.Region.domain.Region;
import com.adopt.apigw.modules.SectorMaster.Domain.SectorMaster;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@JaversSpringDataAuditable
@Repository
public interface SectorMasterRepository extends JpaRepository<SectorMaster,Long>, QuerydslPredicateExecutor<SectorMaster> {

    @Query(value = "select count(*) from tblsectormaster m where m.sector_name=:sname and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("sname") String sname);

    @Query(value = "select count(*) from tblsectormaster m where m.sector_name=:sname and m.is_deleted=false and m.MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("sname") String sname, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblsectormaster where sector_name=:name and sector_id =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Long id);

    @Query(value = "select count(*) from tblsectormaster where sector_name=:name and sector_id =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name,@Param("id") Long id,@Param("mvnoIds") List mvnoIds);


    @Query(value = "select * from  tblsectormaster t where t.is_deleted = false and MVNOID in :mvnoIds", nativeQuery = true)
    Page<SectorMaster> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query(value = "SELECT * from tblsectormaster t WHERE t.is_deleted = false"
            , nativeQuery = true
            , countQuery = "SELECT count(*) from tblsectormaster t WHERE t.is_deleted = false")
    Page<SectorMaster> findAll(Pageable pageable);

}
