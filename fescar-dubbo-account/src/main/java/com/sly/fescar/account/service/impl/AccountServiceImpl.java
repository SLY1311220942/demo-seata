package com.sly.fescar.account.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.sly.fescar.account.mapper.AccountMapper;
import com.sly.fescar.account.service.AccountService;
import com.sly.fescar.common.model.account.Account;

import io.seata.core.context.RootContext;

/**
 * 账户service实现
 * 
 * @author sly
 * @time 2019年6月10日
 */
@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired
	private AccountMapper accountMapper;

	/**
	 * 新增
	 * 
	 * @param account
	 * @return
	 * @author sly
	 * @time 2019年6月11日
	 */
	@Override
	public Map<String, Object> insert(Account account) {
		System.out.println("全局事务id:" + RootContext.getXID());
		
		accountMapper.insert(account);
		Map<String, Object> result = new HashMap<>(16);
		result.put("status", 200);
		result.put("message", "新增成功！");
		return result;
	}

}
