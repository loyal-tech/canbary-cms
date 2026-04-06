package com.adopt.apigw.modules.ServiceParameters.domain;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmserviceparams")
@EntityListeners(AuditableListener.class)
public class ServiceParameter extends Auditable implements IBaseData2 {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name",length = 40)
    private String name;

    @Column(name = "isdelete")
    private Boolean isdelete;

    @Column(name = "field_name")
    private String fieldName;
    @Column(name = "data_type")
    private String dataType;
    @Override
    @JsonIgnore
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    @JsonIgnore
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    @JsonIgnore
    public boolean getDeleteFlag() {
        return false;
    }

    @Override
    @JsonIgnore
    public void setBuId(Long buId) {

    }
}
