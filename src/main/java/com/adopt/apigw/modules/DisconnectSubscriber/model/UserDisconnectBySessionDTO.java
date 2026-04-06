package com.adopt.apigw.modules.DisconnectSubscriber.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserDisconnectBySessionDTO {
    private String remark;
    private String reqType;
    private List<String> sessionId=new ArrayList<>();
}
