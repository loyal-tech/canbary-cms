package com.adopt.apigw.modules.mvnoDocDetails.repository;

import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.modules.mvnoDocDetails.domain.MvnoDocDetails;
import com.adopt.apigw.modules.mvnoDocDetails.model.MvnoDocDetailsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MvnoDocDetailsRepository extends JpaRepository<MvnoDocDetails, Long> {


    @Query("SELECT new com.adopt.apigw.modules.mvnoDocDetails.model.MvnoDocDetailsDTO(m.docId, m.mvno.id, m.docType, m.docSubType, m.mode, m.remark, m.docStatus, m.filename, m.uniquename, m.isDelete, m.startDate, m.endDate, m.nextTeamHierarchyMappingId, m.nextStaff,m.uniquename) FROM MvnoDocDetails m WHERE m.isDelete = false AND m.mvno.id = :mvnoId")
    List<MvnoDocDetailsDTO> findAllByMvnoIdAndIsDeleteFalse(Long mvnoId);


    @Query(value = "SELECT t.*\n" +
            "FROM tblmvnodocdetails t\n" +
            "JOIN (\n" +
            "    SELECT d.mvnoId, MAX(d.ENDDATE) AS max_enddate \n" +
            "    FROM tblmvnodocdetails d\n" +
            "    WHERE d.doc_status = 'verified' \n" +
            "      AND d.is_delete = 0\n" +
            "    GROUP BY d.mvnoId \n" +
            ") latest \n" +
            "ON t.mvnoId = latest.mvnoId \n" +
            "AND t.ENDDATE = latest.max_enddate \n" +
            "AND t.doc_status = 'verified' \n" +
            "AND t.is_delete = 0",
            nativeQuery = true)
    List<MvnoDocDetails> getMvnoDocumentForDunning(@Param(value = "dateDiff") Integer dateDiff);


}
