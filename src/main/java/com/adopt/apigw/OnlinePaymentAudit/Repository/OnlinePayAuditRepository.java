//package com.adopt.apigw.OnlinePaymentAudit.Repository;
//
//import com.adopt.apigw.OnlinePaymentAudit.Entity.OnlinePayAudit;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface OnlinePayAuditRepository extends JpaRepository<OnlinePayAudit,Long> {
//
//    @Query(nativeQuery = true, value ="select * from tblmonlinepayaudit t where t.mvnoid=1 or (t.mvnoid in :MVNOIDS)", countQuery ="select count(*) from tblmonlinepayaudit t where t.mvnoid=1 or (t.mvnoid in :MVNOIDS)" )
//    Page<OnlinePayAudit> findAll(Pageable pageable, @Param("MVNOIDS") List MVNOIDS);
//
//    @Query(nativeQuery = true, value ="select * from tblmonlinepayaudit t where t.mvnoid =1 or (t.mvnoid in :MVNOIDS and t.buid in :buIds)", countQuery ="select count(*) from tblmonlinepayaudit t where t.mvnoid= 1 or(t.mvnoid in :MVNOIDS and t.buid in :buIds ) " )
//    Page<OnlinePayAudit> findAll(Pageable pageable, @Param("MVNOIDS") List MVNOIDS, @Param("buIds") List buIds);
//
//
//    OnlinePayAudit findOnlinePayAuditByReferenceNumber(String referenceNumber);
//
//    List<OnlinePayAudit> findOnlinePayAuditByCustomerId(Integer CustId);
//}
