package com.sly.fescar.account.service.impl;

import java.util.Map;

import org.apache.dubbo.config.annotation.Service;

import com.sly.fescar.account.service.AccountService;

/**
 * 账户service实现
 * 
 * @author sly
 * @time 2019年6月10日
 */
@Service
public class AccountServiceImpl implements AccountService {

	/**
	 * 修改
	 * 
	 * @param accountId
	 * @return
	 * @author sly
	 * @time 2019年6月10日
	 */
	@Override
	public Map<String, Object> update(String accountId) {

		return null;
	}

}
