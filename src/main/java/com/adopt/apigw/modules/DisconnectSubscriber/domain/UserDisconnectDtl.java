package com.adopt.apigw.modules.DisconnectSubscriber.domain;

import lombok.Data;

import javax.persistence.*;

import com.adopt.apigw.core.data.IBaseData;

@Data
@Entity
@Table(name = "tbluser_disconnect_details")
public class UserDisconnectDtl implements IBaseData {

    @Id
    @Column(name = "disuserdtlid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionid;
    private String NASIPAddress;
    private String FramedIPAddress;
    @ManyToOne
    @JoinColumn(name = "disuserid")
    private UserDisconnect userDisconnect;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

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
        return isDeleted;
    }
}
