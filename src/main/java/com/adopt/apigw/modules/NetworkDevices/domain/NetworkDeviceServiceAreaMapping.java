package com.adopt.apigw.modules.NetworkDevices.domain;

import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblnetworkdevicesservicearearel")
@Data
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class NetworkDeviceServiceAreaMapping {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deviceid", nullable = false, length = 40)
    private Long deviceId;

    @Column(name = "serviceareaid", nullable = false, length = 40)
    private  Integer serviceIdList;

    @Column(name = "created_on", nullable = false, length = 40)
    private LocalDateTime createdOn;

    @Column(name = "lastmodified_on", nullable = false, length = 40)
    private LocalDateTime lastmodifiedOn ;
}
