package com.adopt.apigw.modules.partnerdocDetails.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.pojo.api.PartnerPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PartnerdocDTO extends Auditable implements IBaseDto {

    private Long docId;
    private Integer partnerId;
    private String docType;
    private String docSubType;
    private String remark;
    private String mode;
    private String docStatus;
    private String filename;
    private String uniquename;
    private Boolean isDelete = false;
    private String documentNumber;

    @JsonBackReference
    @ApiModelProperty(hidden = true)
    private PartnerPojo partner;

    private Integer mvnoId;

    private LocalDate startDate;

    private LocalDate endDate;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return docId;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }


}
