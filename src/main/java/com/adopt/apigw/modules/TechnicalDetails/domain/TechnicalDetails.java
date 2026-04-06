package com.adopt.apigw.modules.TechnicalDetails.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblm_lc_gi_technical")
@SQLDelete(sql = "UPDATE tblm_lc_gi_technical SET is_deleted = true WHERE techid=?")
@Where(clause = "is_deleted=false")
@EntityListeners(AuditableListener.class)
public class TechnicalDetails extends Auditable implements IBaseData2<Long> {

    @Id
    @Column(name = "techid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "distance")
    private Long distance;

    @Column(name = "bandwidth")
    private Long bandwidth;

    @Column(name = "upload_qos")
    private Long upload_qos;

    @Column(name = "download_qos")
    private Long download_qos;

    @Column(name = "link_router_location")
    private String link_router_location;

    @Column(name = "link_port_type")
    private String link_port_type;

    @Column(name = "link_router_ip")
    private String link_router_ip;

    @Column(name = "link_port_on_router")
    private String link_port_on_router;

    @Column(name = "vlanid")
    private String vlanid;

    @Column(name = "bandwidth_type")
    private String bandwidth_type;

    @Column(name = "link_router_name")
    private String link_router_name;

    @Column(name = "MVNOID")
    private Integer mvnoId;

    @Column(name = "BUID")
    private Long buid;

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

    @Override
    public void setBuId(Long buid) {

    }
}
