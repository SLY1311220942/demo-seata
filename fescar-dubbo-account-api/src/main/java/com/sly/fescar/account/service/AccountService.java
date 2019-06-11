package com.sly.fescar.account.service;

import java.util.Map;

import com.sly.fescar.common.model.account.Account;

/**
 * 账户service
 * 
 * @author sly
 * @time 2019年6月10日
 */
public interface AccountService {

	/**
	 * 新增
	 * 
	 * @param account
	 * @return
	 * @author sly
	 * @time 2019年6月11日
	 */
	Map<String, Object> insert(Account account);

}
