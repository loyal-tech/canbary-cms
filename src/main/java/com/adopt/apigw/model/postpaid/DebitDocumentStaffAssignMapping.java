package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmdebitdocassignstaffmapping")
@EntityListeners(AuditableListener.class)
public class DebitDocumentStaffAssignMapping extends Auditable<DebitDocumentStaffAssignMapping> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "debit_doc_id")
    private Integer debitDocId;

    private Integer staffId;
}
