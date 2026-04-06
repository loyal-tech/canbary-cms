package com.adopt.apigw.modules.tickets.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "tblcasedocdetails")
@EntityListeners(AuditableListener.class)
public class CaseDocDetails extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long docId;

    @Column(name = "ticket_id")
    private Long ticketId;
    private String docStatus;
    private String filename;
    private String uniquename;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;




    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return docId;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDelete;
    }
}
