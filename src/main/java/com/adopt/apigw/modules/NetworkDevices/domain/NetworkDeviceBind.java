package com.adopt.apigw.modules.NetworkDevices.domain;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltnetworkdevicebind")
@EntityListeners(AuditableListener.class)
public class NetworkDeviceBind extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "currentdeviceid")
    private Long currentDeviceId;

    @Column(name = "porttype")
    private String portType;

    @Column(name = "otherdeviceid")
    private Long otherDeviceId;

    @Column(name = "current_device_port")
    private String currentDevicePort;

    @Column(name = "other_device_port")
    private String otherDevicePort;

    @Column(name = "mappingid")
    private Integer mappingId;


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

    public NetworkDeviceBind() {
        super();
    }

    public NetworkDeviceBind(Long deviceId, String portType, Long parentDeviceId) {
        this.currentDeviceId = deviceId;
        this.portType = portType;

    }

    public NetworkDeviceBind(Long deviceId, String portType, Long deviceName, Long parentDeviceId, String inBind, String outBind) {
        this.currentDeviceId = deviceId;
        this.portType = portType;
        this.otherDeviceId =deviceName;

    }
}
