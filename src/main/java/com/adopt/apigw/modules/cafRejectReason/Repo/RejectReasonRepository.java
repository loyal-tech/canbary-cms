package com.adopt.apigw.modules.cafRejectReason.Repo;

import com.adopt.apigw.modules.cafRejectReason.Entity.RejectReason;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RejectReasonRepository extends JpaRepository<RejectReason, Long> , QuerydslPredicateExecutor<RejectReason> {

    Optional<RejectReason> findByNameAndIsDelete(String name, Boolean isDelete);

    List<RejectReason> findByNameAndMvnoIdAndIsDelete(String name, Integer mvnoId, Boolean isDelete);

    Page<RejectReason> findByNameContainingAndIsDelete(String name, Boolean isDelete, Pageable pageable);

    Page<RejectReason> findByNameContainingAndMvnoIdAndIsDelete(String name, Integer mvnoId, Boolean isDelete, Pageable pageable);

    @Query(value = "select * from adoptconvergebss.tblmcafrejectreason where status = 'Active' and is_delete = false\n", nativeQuery = true)
    List<RejectReason> findAllRejectedReasonsList();
}