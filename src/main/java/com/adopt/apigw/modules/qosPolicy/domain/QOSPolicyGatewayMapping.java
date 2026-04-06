package com.adopt.apigw.modules.qosPolicy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbltqospolicy_gateway_mapping")
public class QOSPolicyGatewayMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String gatewayName;
    @Column(name = "download_speed")
    private String downloadSpeed;
    @Column(name = "upload_speed")
    private String uploadSpeed;
    @Column(name = "base_download_speed")
    private String baseDownloadSpeed;
    @Column(name = "base_upload_speed")
    private String baseUploadSpeed;
    @Column(name = "throttle_download_speed")
    private String throttleDownloadSpeed;
    @Column(name = "throttle_upload_speed")
    private String throttleUploadSpeed;
    @DiffIgnore
    @Column(name = "qos_policy_id")
    private Long qosPolicyId;

}
