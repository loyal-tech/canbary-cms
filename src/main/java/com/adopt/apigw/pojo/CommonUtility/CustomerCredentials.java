package com.adopt.apigw.pojo.CommonUtility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCredentials {

    private String customerUserName;

    private String customerPassword;

    private String deviceSerialNumber;
}
