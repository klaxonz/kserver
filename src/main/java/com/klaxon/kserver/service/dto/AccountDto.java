package com.klaxon.kserver.service.dto;

import com.klaxon.kserver.mapper.model.Account;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountDto extends Account {
    private static final long serialVersionUID = -7593619371233522034L;
}
