package com.adopt.apigw.modules.StaffLedgerTransaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffLedgerTransactionRepository extends JpaRepository<StaffLedgerTransactionMapping , Long>, QuerydslPredicateExecutor<StaffLedgerTransactionMapping> {
    List<StaffLedgerTransactionMapping> findAllByTransfferedid(Integer id);
}
