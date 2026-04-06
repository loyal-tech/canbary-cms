package com.adopt.apigw.modules.linkacceptance.repository;

import com.adopt.apigw.modules.linkacceptance.domain.LinkAcceptance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface LinkAcceptanceRepository extends JpaRepository<LinkAcceptance, Long>, QuerydslPredicateExecutor<LinkAcceptance> {
//    Integer duplicateVerifyAtSave(String circuitname, List mvnoId);
//    Integer duplicateVerifyAtSave(String circuitname);
//    Integer duplicateVerifyAtSaveWithName(String circuitname, List mvnoId);
//    Integer duplicateVerifyAtSaveWithName(String circuitname);
//    Integer duplicateVerifyAtEdit(String circuitname, Long id, List mvnoId);
//    Integer duplicateVerifyAtEdit(String circuitname, Long id);
}
