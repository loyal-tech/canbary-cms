package com.adopt.apigw.modules.qosPolicy.model;

import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicyGatewayMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class QOSPolicyDTO extends Auditable implements IBaseDto2 {

    private Long id;
    @NotNull(message = "Please enter name")
    private String name;
    @NotNull(message = "Please enter description")
    private String description;
    @NotNull(message = "Please enter basepolicyname")
    private String basepolicyname;
    private String thpolicyname;
    private String baseparam1;
    private String baseparam2;
    private String baseparam3;
    private String thparam1;
    private String thparam2;
    private String thparam3;
    private Boolean isDeleted = false;

    private Integer mvnoId;
    private String mvnoName;
    private Long buId;
    List<QOSPolicyGatewayMapping> qosPolicyGatewayMappingList;

    private String type;

    private Integer displayId;
    private String displayName;

    private String qosspeed;
    private String upstreamprofileuid;
    private String downstreamprofileuid;
    private String upstreamprofileName;
    private String downstreamprofileName;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

}
