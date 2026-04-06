package com.adopt.apigw.pojo.api;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ForgotPwdPojo {
	 @NotNull
	    private String username;

}
