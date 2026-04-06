package com.adopt.apigw.modules.DisconnectSubscriber.domain;

import lombok.Data;

import javax.persistence.*;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tbluser_disconnect")
@EntityListeners(AuditableListener.class)
public class UserDisconnect extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="disuserid")
    private Long id;

    private String remark;
    private String reqType;
    private String username;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userDisconnect")
    private List<UserDisconnectDtl> userDisconnectDtlList = new ArrayList<>();

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted=deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
