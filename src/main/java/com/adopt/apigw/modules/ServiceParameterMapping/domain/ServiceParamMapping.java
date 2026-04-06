package com.adopt.apigw.modules.ServiceParameterMapping.domain;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.modules.Region.domain.Region;
import com.adopt.apigw.modules.ServiceParameters.domain.ServiceParameter;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltserviceparamservicemapping")
public class ServiceParamMapping implements IBaseData2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

//    @ManyToMany(targetEntity = PlanService.class)
//    @JoinColumn(name = "serviceid", referencedColumnName = "id", updatable = true, insertable = true)
    @Column(name = "serviceid")
    private Long serviceid;

    @Column(name = "serviceparamid")
    private Long serviceParamId;

    @Column(name = "value")
    private String value;

    @Column(name = "ismandatory")
    private Boolean isMandatory;
    @Column(name = "serviceparamname")
    private  String serviceParamName;
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
