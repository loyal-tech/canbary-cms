package com.adopt.apigw.pojo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCAFDTO {

    private String firstName;
    private String email;
    private String custtype;
    private String mobile;
    private Long serviceareaid;
    private Integer planId;
    private String userName;

}
