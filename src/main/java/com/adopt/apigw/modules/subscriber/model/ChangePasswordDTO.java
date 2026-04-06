package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private Integer id;
    private String oldPassword;

    private String newPassword;
}
