package com.adopt.apigw.modules.ippool.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblippool")
@EntityListeners(AuditableListener.class)
public class IPPool extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long poolId;
    private String poolName;
    private String displayName;
    private String poolType;
    private String poolCategory;
    private String ipRange;
    private String netMask;
    private String networkIp;
    private String broadcastIp;
    private String firstHost;
    private String lastHost;
    private Integer totalHost;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isStaticIpPool = false;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean defaultPoolFlag = false;
    private String status;
    private String remark;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    
    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return poolId;
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
