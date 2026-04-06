package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable;

import lombok.Data;

@Data
public class ParentPojo extends Auditable {

    Integer errCode;
    String errMessage;

}
