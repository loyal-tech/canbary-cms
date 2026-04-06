package com.adopt.apigw.repository.postpaid;


import com.adopt.apigw.model.postpaid.*;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface PartnerCreditDocRepository extends JpaRepository<PartnerDebitDocument, Integer>, QuerydslPredicateExecutor<PartnerDebitDocument> {

//	@Query(value = "select * from TBLMDebitDocument where lower(name) like '%' :search  '%' order by DebitDocumentID",
//            countQuery = "select count(*) from TBLMDebitDocument where lower(name) like '%' :search '%'",
//            nativeQuery = true)
//    Page<DebitDocument> searchEntity(@Param("search") String searchText, Pageable pageable);

//	List<DebitDocument> findByStatus(String status);

    List<PartnerDebitDocument> findByBillrunid(Integer billRunId);

    @Query("select t from PartnerCreditDocument t where t.isDelete=false")
    List<PartnerDebitDocument> findAll();

    @Query("update PartnerCreditDocument b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);


    List<PartnerDebitDocument> getAllByPartner(Partner partner);



}
