package com.adopt.apigw.pojo.api;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthResponsePojo extends ParentPojo {

    private Integer id;

    private String username;

    private String replymessage;

    private String packettype;

    private String clientip;

    private String clientgroup;

    private LocalDateTime createdate;

    private String createDateString;
    private String updateDateString;
}
