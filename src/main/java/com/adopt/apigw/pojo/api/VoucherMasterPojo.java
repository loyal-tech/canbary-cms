package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.postpaid.PostpaidPlan;

import com.adopt.apigw.model.radius.VoucherLinkType;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class VoucherMasterPojo {
    private Integer id;
    private String vcName;
    private Integer vcQty;
    private PostpaidPlan plan;
    private Integer plid;
    private String numeric;
    private String uppercase;
    private String lowercase;
    private Integer voucherlength;
    private Integer vouchervalidity;

    private VoucherLinkType linkType;
    private Double voucherAmount;

}
