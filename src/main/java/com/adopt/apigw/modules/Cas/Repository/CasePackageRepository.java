package com.adopt.apigw.modules.Cas.Repository;

import com.adopt.apigw.modules.Cas.Domain.CasMaster;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@JaversSpringDataAuditable
public interface CasePackageRepository extends JpaRepository<CasMaster,Long>, QuerydslPredicateExecutor<CasMaster> {

    @Query(value = "select count(*) from tbltcasmaster t where t.casname=:casname and t.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("casname") String casname);

    @Query(value = "select count(*) from tbltcasmaster t where t.casname=:casname and t.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("casname") String casname, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tbltcasmaster t where t.casname=:casname and t.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds))",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("casname") String casname, @Param("mvnoIds") Integer mvnoIds, @Param("buIds")List buIds);

    @Query(value = "select count(*) from tbltcasmaster t where t.casname=:casname and t.id=:casid and t.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("casname") String casname, @Param("casid") Integer casid);

    @Query(value = "select count(*) from tbltcasmaster t where t.casname=:casname and t.id=:casid and t.is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("casname") String casname, @Param("casid") Integer casid,@Param("mvnoIds")  List mvnoIds);

    @Query(value = "select count(*) from tbltcasmaster t where t.casname=:casname and t.id=:casid and t.is_deleted=false and (MVNOID = 1 or (MVNOID = :mvnoIds and BUID in :buIds))", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("casname") String casname,@Param("casid") Integer casid, @Param("mvnoIds") Integer mvnoId, @Param("buIds") List buIds);



    @Query(value = "select * from tbltcasmaster t where t.is_deleted = false and MVNOID in :mvnoIds",nativeQuery = true)
    Page<CasMaster> findAll(Pageable pageable, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select * from tbltcasmaster t where t.is_deleted = false and (MVNOID = 1 or (MVNOID = :mvnoId and BUID in :buIds))",nativeQuery = true)
    Page<CasMaster> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    @Query(value = "select count(*) from tbltcasmaster where id =:id",nativeQuery = true)
    Integer deleteVerify(@Param("id")Long id);

    Page<CasMaster> findAllByIsDeletedIsFalse(Pageable pageable);
    Page<CasMaster> findAllByIsDeletedIsFalseAndMvnoIdIn(List<Integer> mvnoId, Pageable pageable);
    Page<CasMaster> findAllByIsDeletedIsFalseAndMvnoIdInAndAndBuIdIn(List<Integer> mvnoId, List<Long> buIds, Pageable pageable);
}
