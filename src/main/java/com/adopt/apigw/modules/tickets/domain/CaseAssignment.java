package com.adopt.apigw.modules.tickets.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.StaffUser;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "tblcaseassignment")
public class CaseAssignment implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;
    
    @OneToOne
    @JoinColumn(name = "case_id")
    private Case cases;
    
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "assignee_id")
    private StaffUser staffUser;
    
    private LocalDate assignedDate;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return assignmentId;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    public CaseAssignment() {
    }
}
