package com.adopt.apigw.modules.Region.repository;

import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Region.domain.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> , QuerydslPredicateExecutor<Region> {

    @Query(value = "select count(*) from tblmregion m where m.rname=:rname and m.is_deleted=false and m.MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("rname")String rname, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmregion m where m.rname=:rname and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("rname")String rname);

    @Query(value = "select count(*) from tblmregion where rname=:rname and region_id =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("rname") String rname, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmregion where rname=:rname and region_id =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("rname") String rname, @Param("id") Long id);

    @Query(value = "select count(*) from tblmregion where region_id =:id",nativeQuery = true)
    Integer deleteVerify(@Param("id")Long id);

    @Query(value = "select count(*) from tbltbusinessverticalsmapping t where t.region_id =:id",nativeQuery = true)
    Integer deleteVerifyForBusinessVertical(@Param("id")Long id);

    @Query(value = "select * from  tblmregion t where t.is_deleted = false and MVNOID in :mvnoIds", nativeQuery = true)
    Page<Region> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query(value = "SELECT * from tblmregion t WHERE t.is_deleted = false"
            , nativeQuery = true
            , countQuery = "SELECT count(*) from tblmregion t WHERE t.is_deleted = false")
    Page<Region> findAll(Pageable pageable);

    Region findByRnameContainingIgnoreCaseAndIsDeletedIsFalse(String rname);

    List<Region> findAllByIdIn(List<Long> result);
}
