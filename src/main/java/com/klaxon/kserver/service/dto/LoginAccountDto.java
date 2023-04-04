package com.klaxon.kserver.service.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginAccountDto {

	@NotBlank(message = "电子邮箱不能为空")
	private String email;

	@NotBlank(message = "密码不能为空")
	private String password;

}
