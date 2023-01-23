package com.klaxon.kserver.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterAccountDto {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "电子邮箱不能为空")
    private String email;

}
