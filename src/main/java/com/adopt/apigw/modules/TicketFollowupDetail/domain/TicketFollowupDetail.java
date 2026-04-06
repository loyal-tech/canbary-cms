package com.adopt.apigw.modules.TicketFollowupDetail.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.adopt.apigw.core.data.IBaseData;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@Entity
@Table(name = "tblticketfollowupdetail")
public class TicketFollowupDetail implements IBaseData<Long> {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticketfollowid")
    private Long id;

    private String remark;
    
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Column(name = "case_id")
    private Long caseId;
    
    @Column(name = "staffid")
    private Integer staffId;
    
    @Column(name = "custid")
    private Integer custId;

    @Column(name = "remark_date", nullable = false)
    private LocalDateTime remarkDate;

    @Transient
    private String caseTitle;
    
    @Transient
    private String staffUserName;
    
    @Transient
    private String customersName;
    
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDelete;
    }
}
