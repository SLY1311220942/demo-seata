package com.sly.fescar.business.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sly.fescar.account.service.AccountService;
import com.sly.fescar.business.BusinessService;
import com.sly.fescar.order.service.OrderService;
import com.sly.fescar.storage.service.StorageService;

/**
 * 业务service实现
 * 
 * @author sly
 * @time 2019年6月10日
 */
@Service
public class BusinessServiceImpl implements BusinessService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BusinessServiceImpl.class);

	@Reference
	private StorageService storageService;
	@Reference
	private OrderService orderService;
	@Reference
	private AccountService accountService;

	/**
	 * 购买
	 * 
	 * @param accountId
	 * @param orderId
	 * @param storageId
	 * @return
	 * @author sly
	 * @time 2019年6月10日
	 */
	@Override
	public Map<String, Object> purchase(String accountId, String orderId, String storageId) {
		try {
			storageService.update(storageId);
			orderService.insert(orderId);
			accountService.update(accountId);
			Map<String, Object> result = new HashMap<>(16);
			result.put("status", 200);
			result.put("message", "购买成功！");
			return  result;
		} catch (Exception e) {
			LOGGER.error(ExceptionUtils.getStackTrace(e));
			throw new RuntimeException(e);
		}
		
	}

}
