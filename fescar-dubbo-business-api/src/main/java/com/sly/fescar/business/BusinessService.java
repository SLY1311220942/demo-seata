package com.sly.fescar.business;

import java.util.Map;

/**
 * 业务service
 * 
 * @author sly
 * @time 2019年6月10日
 */
public interface BusinessService {
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
	Map<String, Object> purchase(String accountId, String orderId, String storageId);

}
