package com.sly.fescar.order.service;

import java.util.Map;

import com.sly.fescar.common.model.order.Order;

/**
 * 订单service
 * 
 * @author sly
 * @time 2019年6月10日
 */
public interface OrderService {
	/**
	 * 新增
	 * 
	 * @param order
	 * @return
	 * @author sly
	 * @time 2019年6月10日
	 */
	Map<String, Object> insert(Order order);

}
