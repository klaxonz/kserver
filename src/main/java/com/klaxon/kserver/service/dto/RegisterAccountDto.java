package com.klaxon.kserver.service.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RegisterAccountDto {

	@NotBlank(message = "用户名不能为空")
	private String username;

	@NotBlank(message = "密码不能为空")
	private String password;

	@NotBlank(message = "电子邮箱不能为空")
	private String email;

}
