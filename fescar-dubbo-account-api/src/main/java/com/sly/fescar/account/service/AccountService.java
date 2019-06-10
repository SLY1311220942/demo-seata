package com.sly.fescar.account.service;

import java.util.Map;

/**
 * 账户service
 * 
 * @author sly
 * @time 2019年6月10日
 */
public interface AccountService {

	/**
	 * 修改
	 * 
	 * @param accountId
	 * @return
	 * @author sly
	 * @time 2019年6月10日
	 */
	Map<String, Object> update(String accountId);

}
