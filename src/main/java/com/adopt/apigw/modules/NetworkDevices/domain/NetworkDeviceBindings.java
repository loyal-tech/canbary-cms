package com.adopt.apigw.modules.NetworkDevices.domain;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltnetworkdevicebindings")
@EntityListeners(AuditableListener.class)
public class NetworkDeviceBindings extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "deviceid")
    private Long deviceId;

    @Column(name = "porttype")
    private String portType;

    @Column(name = "parentdeviceid")
    private Long parentDeviceId;

    @Column(name = "inbind")
    private String inBind;

    @Column(name = "outbind")
    private String outBind;

    @Override
    public Long getPrimaryKey() {
        return this.id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    public NetworkDeviceBindings() {
        super();
    }

    public NetworkDeviceBindings(Long deviceId, String portType, Long parentDeviceId) {
        this.deviceId = deviceId;
        this.portType = portType;
        this.parentDeviceId = parentDeviceId;
    }

    public NetworkDeviceBindings(Long deviceId, String portType, Long parentDeviceId, String inBind, String outBind) {
        this.deviceId = deviceId;
        this.portType = portType;
        this.parentDeviceId = parentDeviceId;
        this.inBind = inBind;
        this.outBind = outBind;
    }
}
