package com.adopt.apigw.modules.Reseller.module;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Password",description = "This is data transfer object for password which is used to update reseller password")
public class ResellerChangePasswordDto
{
	@ApiModelProperty(notes = "Name of the user",required=true)
	private String username;
	@ApiModelProperty(notes = "New password of the user",required=true)
	private String newPassword;
	@ApiModelProperty(notes = "Confirm new password of the user",required=true)
	private String confirmNewPassword;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}
	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}
}

