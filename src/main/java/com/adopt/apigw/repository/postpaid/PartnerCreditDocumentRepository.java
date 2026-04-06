package com.adopt.apigw.repository.postpaid;

import com.adopt.apigw.model.postpaid.PartnerCreditDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartnerCreditDocumentRepository extends JpaRepository<PartnerCreditDocument, Integer>, QuerydslPredicateExecutor<PartnerCreditDocument> {

    List<PartnerCreditDocument> getAllByLcoidAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseOrderByIdDesc(Integer custId, String payType, String type);
}
