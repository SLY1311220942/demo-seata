package com.sly.fescar.order.mapper;

import com.sly.fescar.common.model.order.Order;

/**
 * 订单mapper
 * 
 * @author sly
 * @time 2019年6月11日
 */
public interface OrderMapper {
	/**
	 * 新增
	 * 
	 * @param order
	 * @return
	 * @author sly
	 * @time 2019年6月11日
	 */
	int insert(Order order);
}
