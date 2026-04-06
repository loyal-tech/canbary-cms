package com.adopt.apigw.modules.Template.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltemplate")
public class Template implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, length = 40)
    private Long id;

    @Column(name="template_name", nullable = false, length = 40)
    private String name;

    @Column(name="template_type", nullable = false, length = 6)
    private String type;

    @Column(name = "status",nullable = false, length = 1)
    private String status;

    @Column(name = "template_file", nullable = false)
    private String file;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
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

}
