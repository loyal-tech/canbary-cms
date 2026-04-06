package com.adopt.apigw.modules.MvnoDiscountManagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MvnoDiscountMappingRepository extends JpaRepository<MvnoDiscountMapping, Long>, QuerydslPredicateExecutor<MvnoDiscountMapping> {

    List<MvnoDiscountMapping> findAllByMvnoId(Long mvnoId);

    void deleteAllByMvnoId(Long mvnoId);

}
