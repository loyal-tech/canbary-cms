package com.adopt.apigw.pojo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Customer Update Password", description = "This is data transfer object for customer which is used to update customer data")
public class UpdatePasswordResetDto {

    private String username;

    private String password;

    private Integer id;

    private Long mvnoId;
}
