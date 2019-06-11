package com.sly.fescar.account.mapper;

import com.sly.fescar.common.model.account.Account;

/**
 * 账户mapper
 * 
 * @author sly
 * @time 2019年6月11日
 */
public interface AccountMapper {
	/**
	 * 新增
	 * 
	 * @param account
	 * @return
	 * @author sly
	 * @time 2019年6月11日
	 */
	int insert(Account account);
}
