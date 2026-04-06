package com.adopt.apigw.modules.CaseCustomerDetails.model;


import com.adopt.apigw.core.data.IBaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tblcasecustdetails")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseCustomerDetails implements IBaseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "customer_id")
    Integer customerId;

    @Column(name = "case_number")
    String caseNumber;

    @Column(name = "case_id")
    Integer caseId;

    @Column(name = "case_status")
    String caseStatus;

    @Override
    public Serializable getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
