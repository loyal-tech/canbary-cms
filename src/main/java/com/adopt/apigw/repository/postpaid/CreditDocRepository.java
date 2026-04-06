package com.adopt.apigw.repository.postpaid;


import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.pojo.NewCustPojos.NewCreditDocPojo;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
@Repository
public interface CreditDocRepository extends JpaRepository<CreditDocument, Integer>, QuerydslPredicateExecutor<CreditDocument> {

//	@Query(value = "select * from TBLMDebitDocument where lower(name) like '%' :search  '%' order by DebitDocumentID",
//            countQuery = "select count(*) from TBLMDebitDocument where lower(name) like '%' :search '%'",
//            nativeQuery = true)
//    Page<DebitDocument> searchEntity(@Param("search") String searchText, Pageable pageable);

//	List<DebitDocument> findByStatus(String status);

    @Query("select t from CreditDocument t where t.isDelete=false")
    List<CreditDocument> findAll();

    @Query(value = "select t.CREDITDOCID from TBLTCREDITDOC t where t.is_delete = false" , nativeQuery = true)
    List<Integer> findAllCreditDocID();

    @Query("update CreditDocument b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    List<CreditDocument> findAllByCustomer(Customers customers);

    List<CreditDocument> getAllByCustomer_IdOrderByIdDesc(Integer custId);

    List<CreditDocument> getAllByCustomer_IdAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseOrderByIdDesc(Integer custId, String payType, String type);

    @Query(value = "SELECT SUM(t.amount) FROM TBLTCREDITDOC t where t.status='approved' AND t.invoiceid = :invoiceid AND t.is_delete=false", nativeQuery = true)
    Double findTotalPaymentAmountByInvoice(@Param("invoiceid") Integer invoiceid);

    //@Query(value = "SELECT SUM(t.adjustedamount) FROM tbltcreditdebitmapping t where t.debitdocumentid = :invoiceid AND t.is_deleted=false",nativeQuery = true)
    @Query(value = "select sum(t2.adjustedamount) from tbltcreditdoc as t left outer join tbltcreditdebitmapping as t2 on t.creditdocid = t2.creditdocid where STATUS ='approved' and t2.debitdocumentid= :invoiceid and t2.is_deleted=false", nativeQuery = true)
    Double findTotalPaymentAmountByInvoices(@Param("invoiceid") Integer invoiceid);

    @Query(nativeQuery = true
            , value = "select * from tbltcreditdoc creditdoc\n" +
            "left join tblcustomers customers\n" +
            "on customers.custid = creditdoc.CUSTID\n" +
            "where (customers.title like '%' :s1 '%' or customers.firstname like '%' :s2 '%' or customers.lastname like '%' :s3 '%' or creditdoc.CREATEDATE like '%' :s4 '%' or creditdoc.LASTMODIFIEDDATE like '%' :s5 '%' or creditdoc.STATUS like '%' :s6 '%')\n" +
            "and creditdoc.is_delete = false AND creditdoc.MVNOID= :MVNOID OR creditdoc.MVNOID IS NULL"
            , countQuery = "select count(*) from tbltcreditdoc creditdoc\n" +
            "left join tblcustomers customers\n" +
            "on customers.custid = creditdoc.CUSTID\n" +
            "where (customers.title like '%' :s1 '%' or customers.firstname like '%' :s2 '%' or customers.lastname like '%' :s3 '%' or creditdoc.CREATEDATE like '%' :s4 '%' or creditdoc.LASTMODIFIEDDATE like '%' :s5 '%' or creditdoc.STATUS like '%' :s6 '%')\n" +
            "and creditdoc.is_delete = false AND creditdoc.MVNOID= :MVNOID OR creditdoc.MVNOID IS NULL")
    Page<CreditDocument> findAllByCustidContainingIgnoreCaseOrCust_NameAndIsDeleteIsFalse(@Param("s1") String s1, @Param("s2") String s2, @Param("s3") String s3, @Param("s4") String s4, @Param("s5") String s5, @Param("s6") String s6, Pageable pageable, @Param("MVNOID") Integer MVNOID);


    @Query(value = "select count(*) from tblcustomercafassignment t where t.staff_id =:s1", nativeQuery = true)
    Long findMinimumApprovalReuqestForPlanByStaff(@Param("s1") Integer id);

    @Query(value = "select * from adoptconvergebss.tbltcreditdoc t where t.CUSTID =:id", nativeQuery = true)
    List<CreditDocument> findAllByCustId(@Param("id") Integer id);

    Optional<CreditDocument> findById(@Param("creditDocId") Integer creditDocId);

    List<CreditDocument> findAllByIdIn(List<Integer> list);

    @Query(value = "select creditdocumentno from TBLTCREDITDOC where UPPER(type) LIKE UPPER('%CREDITNOTE%') AND CREDITDOCID IN(:list)",nativeQuery = true)
    List<String> findAllByIdInAndTypeCreditNote(List<Integer> list);


    @Query("select coalesce( sum(t2.amount ),0) from CreditDocument t2  where t2.id in (select t.creditDocId from CreditDebitDocMapping t where t.debtDocId=:debitDocId)  and t2.paymode=:payMode and t2.invoiceId != null and t2.invoiceId=:debitDocId and t2.status not in ('pending','rejected')")
    Double checkCreditNoteIsAllowedOrNot(@Param(value = "debitDocId") Integer debitDocId, @Param(value = "payMode") String payMode);

    @Query("select  new CreditDocument (c.amount,c.adjustedAmount,c.paymode,c.id,c.amount-c.adjustedAmount,c.referenceno,c.creditdocumentno)  from CreditDocument c where c.customer.id = :customerId and c.amount-c.adjustedAmount > 0 and c.paytype != 'Withdrawal' and c.status != 'pending' and c.status!= 'rejected'")
    List<CreditDocument> getWithdrawPayments(@Param(value = "customerId") Integer customerId, Pageable pageable);

    @Query("select  sum (c.amount-c.adjustedAmount)  from CreditDocument c where c.id in (:creditDocIds)")
    Double totalWithDrawalAmount(List<Integer> creditDocIds);

    @Query(value = "select sum(t.AMOUNT - t.adjustedamount)  from TBLTCREDITDOC t where t.CUSTID = :customerId and t.paytype != 'Withdrawal'", nativeQuery = true)
    Double totalWithDrawAmount(Integer customerId);

    @Query(value = "select coalesce(sum(t.AMOUNT - t.adjustedamount),0)  from TBLTCREDITDOC t where t.CUSTID = :customerId and t.paytype = 'Withdrawal' and t.STATUS = 'pending'", nativeQuery = true)
    Double totalPendingAmount(Integer customerId);

    @Query(nativeQuery = true, value = "SELECT nextvalcreditnote('creditnoteno')")
    String getFuction();

    @Query(nativeQuery = true, value = "SELECT nextvalpayment('paymentno')")
    String getPaymentFuction();

    @Query(nativeQuery = true, value = "SELECT nextvalconnection('connectionno')")
    String getConnectionFuction();

    @Query(value = "select (sum(case when cd.APPROVEDBYSTAFFID is not null and cd.STATUS != 'rejected' and cd.STATUS != 'Fully Adjusted' then cd.amount else 0 end)) as pendingAmt from tbltcreditdoc cd where cd.invoiceid = :id", nativeQuery = true)
    Double findTotalPendingAmountByDebitDocId(Integer id);

    @Query(value = "select (sum(case when cd.APPROVEDBYSTAFFID is not null and cd.STATUS != 'rejected' then cd.amount else 0 end)) as pendingAmt from tbltcreditdoc cd where (cd.invoiceid = :id AND cd.type = 'CREDITNOTE')", nativeQuery = true)
    Double findTotalPendingAmountByDebitDocIdforCN(Integer id);

    @Query(value = "SELECT MAX(m.id) FROM CreditDocument m")
    Integer findlast();

    @Query("SELECT new com.adopt.apigw.pojo.NewCustPojos.NewCreditDocPojo("+
            "c.id, c.paymode, c.paymentdate, c.amount, c.referenceno )"+
            "FROM CreditDocument c where  c.customer.id =:custId ")
    List<NewCreditDocPojo> getAllListByCustId(@Param("custId") Integer custId);

    @Query(value = "SELECT CREDITDOCID, invoiceid FROM tbltcreditdoc WHERE CREDITDOCID IN (:ids) AND invoiceid IS NOT NULL", nativeQuery = true)
    List<Object[]> findInvoiceIdsByCreditDocIds(@Param("ids") List<Integer> ids);

    @Query(value = "SELECT CREDITDOCID, invoiceid FROM tbltcreditdoc WHERE CREDITDOCID IN (:ids)", nativeQuery = true)
    List<Object[]> getInvoiceIdsByCreditDocIds(@Param("ids") List<Long> ids);

    @Query(value = "select c.mvnoId from CreditDocument t left join Customers c on c.id=t.customer.id where t.id=:id")
    Integer getMvnoIdByCreditDocId(@Param("id") Integer id);
}
