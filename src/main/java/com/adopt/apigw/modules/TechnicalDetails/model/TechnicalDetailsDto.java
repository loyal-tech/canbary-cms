package com.adopt.apigw.modules.TechnicalDetails.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.core.dto.IBaseDto2;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class TechnicalDetailsDto implements IBaseDto2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Long distance;
    private Long bandwidth;
    private Long upload_qos;
    private Long download_qos;
    private String link_router_location;
    private String link_port_type;
    private String link_router_ip;
    private String link_port_on_router;
    private String vlanid;
    private String bandwidth_type;
    private String link_router_name;
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }

    @Override
    public Long getBuId() {
        return null;
    }
}
