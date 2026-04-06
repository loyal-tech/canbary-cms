package com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.core.data.IBaseData2;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;


@Data
@Entity
@Table(name = "tblreportproblem")
public class ReportProblem implements IBaseData2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="report_id", nullable = false, length = 40)
    private Long id;
    @Column(name = "phno", nullable = false, length = 40)
    private Long phno;
    @Column(name = "issue", length = 255)
    private String issue;
    @Column(name = "description",length = 255)
    private String desc;

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }
    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    @Override
    public void setBuId(Long buId) {

    }
}
