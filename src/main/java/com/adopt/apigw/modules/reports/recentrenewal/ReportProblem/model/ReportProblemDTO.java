package com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.springframework.data.annotation.Id;

import com.adopt.apigw.core.dto.IBaseDto2;

import lombok.Data;

@Data
public class ReportProblemDTO implements IBaseDto2{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long phno;
    private String desc;
    private String issue;
    private List<String> issue_list = new ArrayList<>();
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }
    @Override
    public Long getBuId() {
        return null;
    }
}
