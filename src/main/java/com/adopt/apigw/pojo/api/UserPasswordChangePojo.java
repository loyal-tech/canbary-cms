package com.adopt.apigw.pojo.api;

import javax.validation.constraints.NotNull;

public class UserPasswordChangePojo {

	@NotNull
    private String userName;
	
	//@NotNull
    private String oldPassword;
	
	@NotNull
    private String newPassword;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
