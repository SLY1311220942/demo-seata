package com.sly.fescar.business.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sly.fescar.account.service.AccountService;
import com.sly.fescar.business.BusinessService;
import com.sly.fescar.common.model.account.Account;
import com.sly.fescar.common.model.order.Order;
import com.sly.fescar.common.model.storage.Storage;
import com.sly.fescar.common.utils.CommonUtils;
import com.sly.fescar.common.utils.DateUtils;
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
			System.out.println("accountId:" + accountId);
			System.out.println("orderId:" + orderId);
			System.out.println("storageId:" + storageId);
			
			Storage storage = new Storage();
			storage.setStorageId(CommonUtils.genUUID());
			storage.setStorageName("name");
			storage.setStorageCount(20);
			storage.setRemark("备注");
			storage.setLogicDel("N");
			Order order = new Order();
			order.setOrderId(CommonUtils.genUUID());
			order.setOrderNo("NO" + System.currentTimeMillis());
			order.setOrderDetail("详情");
			order.setCreateTime(DateUtils.formateTime(new Date()));
			order.setRemark("备注");
			order.setLogicDel("N");
			Account account = new Account();
			account.setAccountId(CommonUtils.genUUID());
			account.setAccountName("name");
			account.setAmount(new BigDecimal("100.5"));
			account.setLogicDel("N");
			account.setRemark("备注");
			
			storageService.insert(storage);
			orderService.insert(order);
			accountService.insert(account);
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
