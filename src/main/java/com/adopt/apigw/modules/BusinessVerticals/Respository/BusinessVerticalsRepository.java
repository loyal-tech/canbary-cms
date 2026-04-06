package com.adopt.apigw.modules.BusinessVerticals.Respository;

import com.adopt.apigw.modules.BusinessVerticals.domain.BusinessVerticals;
import com.adopt.apigw.modules.BusinessVerticals.domain.BusinessVerticalsMapping;
import com.adopt.apigw.modules.Region.domain.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BusinessVerticalsRepository extends JpaRepository<BusinessVerticals, Long> , QuerydslPredicateExecutor<BusinessVerticals> {

    @Query(value = "select count(*) from tblmbusinessverticals m where m.vname=:vname and m.is_deleted=false and m.MVNOID in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("vname")String vname, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmbusinessverticals m where m.vname=:vname and m.is_deleted=false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("vname")String vname);

    @Query(value = "select count(*) from tblmbusinessverticals where vname=:vname and bu_verticals_id =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("vname") String vname, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblmbusinessverticals where vname=:vname and bu_verticals_id =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("vname") String vname, @Param("id") Long id);

    @Query(value = "select count(*) from tblmbusinessverticals where bu_verticals_id =:id",nativeQuery = true)
    Integer deleteVerify(@Param("id")Long id);

    @Query(value = "select * from  tblmbusinessverticals t where t.is_deleted = false and MVNOID in :mvnoIds", nativeQuery = true)
    Page<BusinessVerticals> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

//    @Query(value = "select * from  tblmbusinessverticals t where t.is_deleted = false and MVNOID in :mvnoIds", nativeQuery = true)
//    Page<BusinessVerticals> findAll(pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query(value = "SELECT * from tblmbusinessverticals t WHERE t.is_deleted = false"
            , nativeQuery = true
            , countQuery = "SELECT count(*) from tblmbusinessverticals t WHERE t.is_deleted = false")
    Page<BusinessVerticals> findAll(Pageable pageable);

//    findAllByRnameContainingIgnoreCaseAndIsDeletedIsFalse

    BusinessVerticals findByVnameContainingIgnoreCaseAndIsDeletedIsFalse(String rname);

    @Query(value = "select * from tbltbusinessverticalsmapping t where buverticalsid =:id and is_deleted = false", nativeQuery = true)
    List<BusinessVerticalsMapping> findAllByBusinessVerticalId(@Param("id") Long id);

    List<BusinessVerticals> findAllByIdIn(List<Long> result);
}
