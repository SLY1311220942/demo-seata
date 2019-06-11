package com.sly.fescar.order.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.sly.fescar.common.model.order.Order;
import com.sly.fescar.order.mapper.OrderMapper;
import com.sly.fescar.order.service.OrderService;

/**
 * 订单service实现
 * 
 * @author sly
 * @time 2019年6月10日
 */
@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private OrderMapper orderMapper;

	/**
	 * 新增
	 * 
	 * @param order
	 * @return
	 * @author sly
	 * @time 2019年6月11日
	 */
	@Override
	public Map<String, Object> insert(Order order) {
		orderMapper.insert(order);
		Map<String, Object> result = new HashMap<>(16);
		result.put("status", 200);
		result.put("message", "新增成功！");
		return result;
	}

}
