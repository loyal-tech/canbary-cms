package com.adopt.apigw.modules.DisconnectSubscriber.model;

import lombok.Data;

@Data
public class UserDisconnectByNameReqDTO {
    private Integer userId;
    private String remark;
    private String reqType;
}
