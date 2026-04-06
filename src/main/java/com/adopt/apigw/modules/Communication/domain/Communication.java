package com.adopt.apigw.modules.Communication.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "communication")
@Component
public class Communication extends Auditable<Integer> implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String subject;
    private String emailBody;
    private String uuid;
    private String destination;
    private String source;
    private String smsMessage;
    private String templateId;
    private String channel;
    private Boolean is_sended;
    private String error;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }

}
