package com.klaxon.kserver.service.dto;

import javax.validation.constraints.NotBlank;



public class LoginAccountDto {

	@NotBlank(message = "电子邮箱不能为空")
	private String email;

	@NotBlank(message = "密码不能为空")
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
