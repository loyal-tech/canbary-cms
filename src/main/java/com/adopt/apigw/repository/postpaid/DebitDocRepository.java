package com.adopt.apigw.repository.postpaid;


import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.DebitDocDetails;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.model.postpaid.DebitDocumentSummary;
import com.adopt.apigw.model.postpaid.DebitDocumentTAXRel;
import com.adopt.apigw.pojo.DebitDocDetailDTO;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//@JaversSpringDataAuditable
@Repository
public interface DebitDocRepository extends JpaRepository<DebitDocument, Integer>, QuerydslPredicateExecutor<DebitDocument> {

//	@Query(value = "select * from TBLMDebitDocument where lower(name) like '%' :search  '%' order by DebitDocumentID",
//            countQuery = "select count(*) from TBLMDebitDocument where lower(name) like '%' :search '%'",
//            nativeQuery = true)
//    Page<DebitDocument> searchEntity(@Param("search") String searchText, Pageable pageable);

//	List<DebitDocument> findByStatus(String status);

    List<DebitDocument> findByBillrunid(Integer billRunId);

    @Query("select t from DebitDocument t where t.isDelete=false")
    List<DebitDocument> findAll();

    @Query("update DebitDocument b set b.isDelete=true where b.id=:id")
    @Modifying
    void deleteById(@Param("id") Integer id);

    @Query(value = "SELECT * from tbltdebitdocument t where t.subscriberid = :s1 and t.planid != 0 and t.is_delete = 0 order by debitdocumentid desc", nativeQuery = true)
    List<DebitDocument> getAllByCustomer_Id(@Param("s1") Integer custId);

    @Query(value = "select totaldue from tbltdebitdocument t where t.debitdocumentid =:id", nativeQuery = true)
    Double FindDueAmountFromInvoice(@Param("id") Integer id);

    @Query(value = "select * from tbltdebitdocument t where t.debitdocumentid =:id", nativeQuery = true)
    DebitDocument FindDebitDocsFromInvoice(@Param("id") Integer id);

    List<DebitDocument> findAllByCustomer(Customers customer);

    List<DebitDocument> findAllByCustomerAndBillrunstatusIsNot(Customers customer, String status);
    @Query(value = "SELECT * FROM TBLTDEBITDOCUMENT d WHERE d.subscriberid = :customerId AND d.billrunstatus = :status ORDER BY d.debitdocumentid DESC LIMIT 1", nativeQuery = true)
    DebitDocument findTopByCustomerAndBillrunstatus(@Param("customerId") Integer customerId, @Param("status") String status);
    List<DebitDocument> findAllByCustomerAndBillrunstatusIs(Customers customer, String status);

//    DebitDocument findFirstByOrderByDocnumberDesc(BooleanExpression eq);

    DebitDocument findDebitDocumentByInventoryMappingId(Long inventoryMappingId);

    DebitDocument findByDocnumber(String docnumber);

    List<DebitDocument> findAllByCustpackrelidIn(List<Integer> custpackrelid);

    @Query("select m from DebitDocument m where  m.customer.id=:customerId")
    List<DebitDocument> pendingDebitDocumentList(@Param(value = "customerId") Integer customerid);

    @Query("select m from DebitDocDetails  m where m.debitdocumentid = :debitDocId")
    List<DebitDocDetails> debitDocDetailsByDebitDocId(@Param(value = "debitDocId") Integer debitdocumentid);

    @Query("select new DebitDocumentTAXRel(t.taxlevel ,t.chargeid ,t.taxname ,t.percentage ,sum(t.amount)) from DebitDocumentTAXRel t where t.debitdocumentid = :debitDocId group by t.taxlevel ,t.chargeid ,t.taxname ,t.percentage")
    List<DebitDocumentTAXRel> getAllDebitDocTaxDetails(@Param(value = "debitDocId") Integer debitDocId);


    @Query("select d from DebitDocument d where d.customer.id= :customerId and d.totalamount-d.adjustedAmount >0  and d.paymentStatus!='Fully Paid' and (d.endate < CURRENT_DATE or d.duedate < CURRENT_DATE) ")
    List<DebitDocument> getInvoiceWhereServiceExpiredAndNotClear(@Param(value = "customerId") Integer customerId);

    @Query("select d from DebitDocument d where d.customer.id= :customerId and d.paymentStatus!='Fully Paid' and (d.endate < CURRENT_DATE or d.duedate < CURRENT_DATE) ")
    List<DebitDocument> currentServiceInoviceList(@Param(value = "customerId") Integer customerId);

    List<DebitDocument> findAllById(Integer id);

    DebitDocument findByInventoryMappingId(Long id);


//    @Query("SELECT new com.adopt.apigw.rabbitMq.message.BillGenMessageData(dd.docnumber, concat( c.firstname,c.lastname), c.username, dd.billrunid, c.acctno, chg.name, tx.name, dd.startdate, dd.endate, b.branch_code, " +
//            "bu.bucode, s.iccode, chg.ledgerId, ch.chargeAmount, dd.id,c.servicearea.id)" +
//            " FROM CustPlanMappping cpr JOIN CustomerChargeHistory ch ON ch.custPlanMapppingId = cpr.id " +
//            "JOIN CustomerServiceMapping csm ON csm.id = cpr.custServiceMappingId JOIN PlanService s ON s.id = csm.serviceId JOIN Charge chg ON chg.id = ch.chargeId" +
//            " JOIN DebitDocument dd ON dd.id = cpr.debitdocid JOIN Customers c ON c.id = dd.customer.id JOIN Tax tx ON tx.id = ch.taxId" +
//            " LEFT JOIN Branch b ON b.id = c.branch JOIN BusinessUnit bu ON bu.id = c.buId" +
//            " WHERE dd.id = :debitDocId")
//    List<BillGenMessageData> getBillGenMessageData(@Param(value = "debitDocId") Integer debitDocId);

    List<DebitDocument> findAllByIdIn(List<Integer> debitdocids);

    List<DebitDocument> findAllByBillrunstatusAndIdIn(String billRunStatus,List<Integer> debitdocids);

    List<DebitDocument> findAllByIdInAndBillrunstatusIsNot(List<Integer> debitdocids, String status);

    boolean existsByIdInAndStatus(List<Integer> debitDocids, String status);

    @Query(value = "SELECT COUNT(*) FROM tbltdebitdocument t LEFT JOIN tbltdebitdocumentdetail t2 ON t.debitdocumentid = t2.debitdocumentid " +
            "WHERE t2.chargeid = :chargeId AND t2.chargetype = 'NON_RECURRING' AND t.billrunstatus != 'VOID' AND t.subscriberid = :custId", nativeQuery = true)
    Integer getCount(@Param("chargeId")Integer chargeId,@Param("custId")Integer custId);


     List<DebitDocument> findAllByCustRefNameIn(List<String> cust_ref_name);
    @Query(value = "SELECT * from TBLTDEBITDOCUMENT deb where deb.subscriberid=:custId  and deb.billrunstatus != 'VOID' and deb.payment_status!='Fully Paid' ORDER BY deb.debitdocumentid desc limit 1",nativeQuery = true)
    List<DebitDocument> lastInvoice(Integer custId);
    @Query(value = "SELECT SUM(d.totalamount) " +
            "FROM DebitDocument d " +
            "JOIN Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billdate >=:billDate")
    Double getAmountByMvnoIdAndBillDate(Integer mvnoId, LocalDateTime billDate);

    @Query(value = "SELECT SUM(d.totalamount) " +
            "FROM DebitDocument d " +
            "JOIN Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billrunstatus != 'VOID'")
    Double getAmountByMvnoIdAndStatusIsNotVoid(Integer mvnoId);

//    @Query(value = "SELECT SUM(d.totalamount) \n" +
//            "           FROM tbltdebitdocument  d \n" +
//            "            JOIN tblcustomers  cust on d.subscriberid  = cust.custid  \n" +
//            "            WHERE cust.mvnoId = :mvnoId and d.billrunstatus  != 'VOID' and d. =:duedate",nativeQuery = true)
//    Double getAmountByMvnoIdAndStatusIsNotVoid(Integer mvnoId,LocalDateTime duedate);

    @Query(value = "SELECT SUM(d.totalamount) " +
            "FROM DebitDocument d " +
            "JOIN Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billrunstatus != 'VOID'")
    Double getAmountByMvnoIdAndStatusIsNotVoidAndBillDate(Integer mvnoId);

    @Query(value = "SELECT SUM(d.totalamount)" +
            "FROM DebitDocument d " +
            "JOIN Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId")
    Double getAmountByMvnoId(Integer mvnoId);
    @Query(value = "select d from DebitDocument  d where d.custpackrelid in :cprId and d.totalamount-d.adjustedAmount>0")
    List<DebitDocument> findAllByCustpackrelid(List<Integer> cprId);

    @Query(value = "select d.debitdocumentid   from tbltdebitdocument d join tblcustomers c on c.custid = d.subscriberid where d.debitdocumentnumber = :invoicnumber and c.MVNOID = :mvnoid",  nativeQuery = true)
    Integer findDebitDocumentsIdByDocumentNoAndMvnoId(@Param("invoicnumber") String invoicnumber, @Param("mvnoid")Integer mvnoid);

    @Query(value = "select d.debitdocumentid   from tbltdebitdocument d join tblcustomers c on c.custid = d.subscriberid where d.debitdocumentnumber = :invoicnumber",  nativeQuery = true)
    List<Integer> findDebitDocumentIdsByDocumentNumber(@Param("invoicnumber") String invoicnumber);

    @Query(value = "SELECT new com.adopt.apigw.pojo.DebitDocDetailDTO(dd.chargetype, dd.totalamount, dd.debitdocdetailid, d.docnumber, cust.username, d.billdate,c.id,c.tax.id) " +
            "FROM com.adopt.apigw.model.postpaid.DebitDocDetails dd " +
            "JOIN DebitDocument d on d.id = dd.debitdocumentid " +
            "JOIN Charge c on c.id = dd.chargeid " +
            "JOIN com.adopt.apigw.model.common.Customers cust ON d.customer.id = cust.id " +
            "WHERE cust.mvnoId = :mvnoId and d.billrunstatus != 'VOID' and dd.mvnodebitdocumentid is null and" +
            " d.startdate >= :fromDate and d.startdate <= :toDate and dd.totalamount > 0  and c.mvnoId = 1 and d.isDelete = 0 ")
    List<DebitDocDetailDTO> getDebitDocDTOByMvnoAndBillDate(Integer mvnoId, LocalDateTime fromDate, LocalDateTime toDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbltdebitdocument e SET e.subscriberid = :custId WHERE e.debitdocumentid = :debitDocId",nativeQuery = true)
    int updateCustomer(@Param("debitDocId") Integer debitDocId,@Param("custId") Integer custId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tbltdebitdocument e SET e.subscriberid = :custId, e.planid=:planId WHERE e.debitdocumentid = :debitDocId",nativeQuery = true)
    int updateCustomer(@Param("debitDocId") Integer debitDocId,@Param("custId") Integer custId,@Param("planId") Integer planId);

    @Query(value = "SELECT  new com.adopt.apigw.model.postpaid.DebitDocumentSummary(d, SUM(d.totalamount) ) " +
            "FROM DebitDocument d " +
            "JOIN Customers cust ON d.customer.id = cust.id " +
            "WHERE  d.customer.id = :custId " +
            "AND d.billrunstatus != 'VOID' " +
            "AND d.paymentStatus != 'Fully Paid' " +
            "AND DATE(d.duedate) = DATE(:mvnoPaymentDueDays) " +
            "GROUP BY d.id " +
            "ORDER BY d.id DESC ")
    List<DebitDocumentSummary >findDebitDocumentAndSum(@Param("custId") Integer custId,
                                                 @Param("mvnoPaymentDueDays") LocalDateTime mvnoPaymentDueDays);

    @Query(value = "select * from tbltdebitdocument t where t.subscriberid =:custId", nativeQuery = true)
    DebitDocument findDebitDocByCustID(@Param("custId") Integer custId);

    @Query(value = "SELECT subscriberid, SUM(tax) FROM tbltdebitdocument " +
            "WHERE subscriberid IN (:customerIds) " +
            "GROUP BY subscriberid",
            nativeQuery = true)
    List<Object[]> getTaxAmountsByCustomerIds(@Param("customerIds") List<Integer> customerIds);


    @Query("SELECT d.customer.id, COALESCE(SUM(d.totaldue), 0) " +
            "FROM DebitDocument d " +
            "WHERE d.customer.id IN :customerIds " +
            "GROUP BY d.customer.id")
    List<Object[]> getTotalDueByCustomerIds(@Param("customerIds") List<Integer> customerIds);


    @Query("SELECT d.customer.id, COALESCE(SUM(d.subtotal), 0) " +
            "FROM DebitDocument d " +
            "WHERE d.customer.id IN :customerIds " +
            "GROUP BY d.customer.id")
    List<Object[]> getSubtotalByCustomerIds(@Param("customerIds") List<Integer> customerIds);

//    @Query("SELECT d.customer.id, d.duedate FROM DebitDocument d " +
//            "WHERE d.customer.id IN :customerIds")
//    List<Object[]> getDueDatesByCustomerIds(@Param("customerIds") List<Integer> customerIds);



    @Query("SELECT d.customer.id, d.duedate FROM DebitDocument d " +
            "WHERE d.customer.id IN :customerIds " +
            "AND FUNCTION('DATEDIFF', d.duedate, CURRENT_DATE) = :dateDiff")
    List<Object[]> getDueDatesByCustomerIds(@Param("customerIds") List<Integer> customerIds,
                                                       @Param("dateDiff") Integer dateDiff);
    @Query("SELECT  d.id FROM DebitDocument d " +
            "WHERE d.customer.id IN :customerIds " +
            "AND FUNCTION('DATEDIFF', d.duedate, CURRENT_DATE) = :dateDiff")
    List<Integer> getInvoiceNumber(@Param("customerIds") List<Integer> customerIds,
                                          @Param("dateDiff") Integer dateDiff);


    @Query(value = "SELECT COUNT(DISTINCT d.debitdocumentid) " +
            "FROM TBLTDEBITDOCUMENT d " +
            "JOIN tbltdebitdocumentdetail dd ON d.debitdocumentid = dd.debitdocumentid " +
            "WHERE d.subscriberid = :subscriberId " +
            "AND dd.planid = :planId " +
            "AND ABS(d.totalamount - d.adjustedamount) <= 0.1", nativeQuery = true)
    long countFullyPaidInvoicesNative(@Param("subscriberId") Integer subscriberId,
                                      @Param("planId") Integer planId);


    @Query(
            value = "SELECT MAX(t.enddate) FROM tbltdebitdocument t WHERE t.subscriberid = :customerId AND t.is_delete = 0",
            nativeQuery = true
    )
    LocalDateTime findLatestEndDateFromDetails(@Param("customerId") Integer customerId);

}
