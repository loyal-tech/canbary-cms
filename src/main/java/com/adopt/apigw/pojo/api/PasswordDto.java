package com.adopt.apigw.pojo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Password",description = "This is data transfer object which is used to reset user password")
@Data	
public class PasswordDto {

	@ApiModelProperty(notes = "Name of the user",required=true)
	private String userName;
	@ApiModelProperty(notes = "New password of the user",required=true)
	private String newPassword;
	@ApiModelProperty(notes = "Confirm new password of the user",required=true)
	private String confirmNewPassword;
}
