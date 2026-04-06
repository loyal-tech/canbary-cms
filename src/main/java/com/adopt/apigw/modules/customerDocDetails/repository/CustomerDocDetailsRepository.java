package com.adopt.apigw.modules.customerDocDetails.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;

import java.util.List;

@Repository
public interface CustomerDocDetailsRepository extends JpaRepository<CustomerDocDetails, Long>, QuerydslPredicateExecutor<CustomerDocDetails> {
    //public List<CustomerDocDetails> findAllByDocStatus(String status);
    List<CustomerDocDetails> findAllByIsDeleteFalse();
    List<CustomerDocDetails> findAllByCustomer_idAndIsDeleteIsFalse(Integer custid);

    @Query(value = "select count(*) from tblcustdocdetails where cust_id =:custId",nativeQuery = true)
    Integer deleteVerify(@Param("custId") Integer custId);

    @Query(nativeQuery = true, value = "select * from tblcustdocdetails where CREATEDBYSTAFFID =:staffid")
    CustomerDocDetails getDocumentByStaffId(@Param("staffid") Integer staffid);

    @Query(nativeQuery = true, value = "SELECT t.*\n" +
            "FROM tblcustdocdetails t\n" +
            "INNER JOIN tblstaffuser t2 ON t.CREATEDBYSTAFFID = t2.staffid\n" +
            "INNER JOIN tblcustomers t3 ON t3.custid = t.cust_id \n" +
            "LEFT JOIN tblmbranch t4 ON t3.branchid = t4.branchid\n" +
            "WHERE \n" +
            " (t.doc_status = 'pending' and t.is_delete = 0 and DATEDIFF(CURRENT_DATE(), t.ENDDATE) = :dateDiff)\n" +
            "  or \n" +
            " (t.doc_status = 'pending' and t.is_delete = 0 and DATEDIFF(CURRENT_DATE(), t.ENDDATE) = (:dateDiff + CAST(t4.dunning_days AS UNSIGNED)))")
    List<CustomerDocDetails> getDocumentForDunning(@Param(value = "dateDiff") Integer dateDiff);

    @Query(nativeQuery = true , value = "(select t.* from tblcustdocdetails t \n" +
            "inner join tblstaffuser t2 \n" +
            "on  t.CREATEDBYSTAFFID  = t2.staffid \n" +
            "where t.is_delete = 0)")
    List<CustomerDocDetails> getDocumentForDunningCust();
    
}
