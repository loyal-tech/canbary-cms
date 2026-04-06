package com.adopt.apigw.modules.partnerdocDetails.repository;

import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.modules.partnerdocDetails.domain.PartnerdocDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerDocdetailsRepository extends JpaRepository<PartnerdocDetails,Long> {


    List<PartnerdocDetails> findAllByPartner_idAndIsDeleteIsFalse(Integer partnerId);
    @Query(value = "select count(*) from tblpartnerdetails where PARTNERID =:partnerId",nativeQuery = true)
    Integer deleteVerify(@Param("partnerId") Integer partnerId);

    @Query(nativeQuery = true, value = "(select t.* from tblpartnerdetails t \n" +
            "inner join tblstaffuser t2 \n" +
            "on  t.CREATEDBYSTAFFID  = t2.staffid \n" +
            "where t.is_delete = 0 and t.doc_status = 'pending' and datediff(current_date(),t.ENDDATE) =:dateDiff)")
    List<PartnerdocDetails> getDocumentForPartnerDunning(@Param(value = "dateDiff") Integer dateDiff);

    @Query(nativeQuery = true, value = "(select t.* from tblpartnerdetails t \n" +
            "inner join tblstaffuser t2 \n" +
            "on  t.CREATEDBYSTAFFID  = t2.staffid \n" +
            "where t.is_delete = 0 and t.ENDDATE IS NULL and t.doc_status = 'pending' and datediff(current_date(),t.CREATEDATE) =:dateDiff)")
    List<PartnerdocDetails> getDocumentForPartnerDunningForCreatedate(@Param(value = "dateDiff") Integer dateDiff);



}
