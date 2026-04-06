package com.adopt.apigw.modules.purchaseDetails.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.adopt.apigw.modules.purchaseDetails.domain.PurchaseDetails;

import java.util.List;

public interface PurchaseDetailsRepo extends JpaRepository<PurchaseDetails, Long> {
    PurchaseDetails findByTransid(String transId);

    @Query(nativeQuery = true, value = "SELECT * from tbl_purchase_details t where t.is_deleted = 0"
            , countQuery = "SELECT count(*) from tbl_purchase_details t where t.is_deleted = 0")
    Page<PurchaseDetails> findAll(Pageable pageable);

    @Query(nativeQuery = true
            , value = "SELECT * from tbl_purchase_details t where t.purchaseid IN (:s1) AND t.is_deleted = 0"
            , countQuery = "SELECT count(*) from tbl_purchase_details t where t.purchaseid IN (:s1) AND t.is_deleted = 0")
    Page<PurchaseDetails> findAllBy(Pageable pageable, @Param("s1") List<String> s1);

}
