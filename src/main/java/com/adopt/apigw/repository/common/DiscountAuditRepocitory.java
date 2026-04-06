package com.adopt.apigw.repository.common;
import com.adopt.apigw.model.postpaid.discountAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountAuditRepocitory extends JpaRepository<discountAudit, Integer>, QuerydslPredicateExecutor<discountAudit> {
}
