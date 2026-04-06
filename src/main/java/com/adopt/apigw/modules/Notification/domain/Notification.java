package com.adopt.apigw.modules.Notification.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblnotifications")
@EntityListeners(AuditableListener.class)
public class Notification extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private String name;
    private Boolean email_enabled;
    private Boolean sms_enabled;
    private String status;
    private String category;
    private String email_body;
    private String sms_body;
    private String template_id;


    @OneToOne(cascade = CascadeType.ALL, mappedBy = "notification", fetch = FetchType.LAZY,orphanRemoval = true)
    private NotificationConfig notificationConfig;

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
