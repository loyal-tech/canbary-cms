package com.adopt.apigw.modules.planUpdate.repository;

import com.adopt.apigw.model.postpaid.CustQuotaDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.planUpdate.domain.QuotaDtls;

import java.util.List;

@Repository
public interface QuotaDtlsRepository extends JpaRepository<QuotaDtls, Long>  ,  QuerydslPredicateExecutor<QuotaDtls> {
    public List<QuotaDtls> findAllByCustomersId(Integer id);

}
