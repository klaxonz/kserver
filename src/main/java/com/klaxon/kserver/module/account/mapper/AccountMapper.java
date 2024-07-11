package com.klaxon.kserver.module.account.mapper;

import com.klaxon.kserver.module.account.model.entity.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 账号表 Mapper 接口
 * </p>
 *
 * @author klaxonz
 * @since 2024-04-25
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

}
