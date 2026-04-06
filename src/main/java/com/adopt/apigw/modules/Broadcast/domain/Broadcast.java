package com.adopt.apigw.modules.Broadcast.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "tblbroadcast")
@EntityListeners(AuditableListener.class)
public class Broadcast extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "broadcast_id")
    private Long id;

    private String type;
    private String priority;
    private Long templateid;
    private String emailsubject;
    private String custstatus;
    private String custcondition;
    private Long planid;
    private Long serviceareaid;
    private Long networkdeviceid;
    private Long slotid;
    private String body;
    private String status;
    private Long customer_id;
    private String expiry_condition;
    private LocalDate expirydate1;
    private LocalDate expirydate2;
    private Integer expirywithin;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "broadcast")
    private Set<BroadcastPorts> broadcastPortsList;

    @Column(columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;
    
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
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
