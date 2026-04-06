package com.adopt.apigw.modules.auditLog.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "tblauditlog")
public class AuditLogEntry implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long id;

    @CreationTimestamp
    @Column(name = "auditdate", nullable = false)
    private LocalDate auditDate;

    private String userName;
    private Integer userId;
    private String employeeName;
    private Integer employeeId;
    private String module;
    private String operation;
    private String ipAddress;
    private String remark;
    private Long entityRefId;
    private Integer partnerId;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
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
