package com.klaxon.kserver.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klaxon.kserver.entity.dto.LoginAccountDto;
import com.klaxon.kserver.entity.dto.RegisterAccountDto;
import com.klaxon.kserver.entity.dao.Account;
import com.klaxon.kserver.entity.vo.AccountVo;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;
import com.klaxon.kserver.mapper.AccountMapper;
import com.klaxon.kserver.mapperstruct.AccountMapperStruct;
import com.klaxon.kserver.service.IAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


@Service("accountService")
@Slf4j
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AccountMapperStruct accountMapperStruct;

    @Override
    public void addAccount(RegisterAccountDto registerAccountDto) {

        // 用户名重复校验
        Account usernameAccount = accountMapper.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getUsername, registerAccountDto.getUsername()));
        if (!Objects.isNull(usernameAccount)) {
            throw new BizException(BizCodeEnum.ACCOUNT_0030001);
        }
        // 邮箱重复校验
        Account emailAccount = accountMapper.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getEmail, registerAccountDto.getEmail()));
        if (!Objects.isNull(emailAccount)) {
            throw new BizException(BizCodeEnum.ACCOUNT_0030004);
        }

        // md5 加密密码
        MD5 md5 = MD5.create();
        String digestHex = md5.digestHex(registerAccountDto.getPassword());

        Account account = new Account();
        account.setUsername(registerAccountDto.getUsername());
        account.setPassword(digestHex);
        account.setEmail(registerAccountDto.getEmail());
        accountMapper.insert(account);
    }

    @Override
    public AccountVo login(LoginAccountDto loginAccountDto) {
        Account account = accountMapper.selectOne(new LambdaQueryWrapper<Account>().eq(Account::getEmail, loginAccountDto.getEmail()));
        if (Objects.isNull(account)) {
            throw new BizException(BizCodeEnum.ACCOUNT_0030002);
        }
        // 比对密码
        MD5 md5 = MD5.create();
        String digestHex = md5.digestHex(loginAccountDto.getPassword());
        if (!StringUtils.equals(digestHex, account.getPassword())) {
            throw new BizException(BizCodeEnum.ACCOUNT_0030003);
        }
        return accountMapperStruct.entityToVo(account);
    }

    @Override
    public void logout(HttpServletRequest request) {
        request.getSession().invalidate();
    }

}
