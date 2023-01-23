package com.klaxon.kserver.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginAccountDto {

    @NotBlank(message = "电子邮箱不能为空")
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;

}
