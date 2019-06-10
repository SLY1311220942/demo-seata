package com.sly.fescar.storage.service;

import java.util.Map;

/**
 * 仓储service
 * 
 * @author sly
 * @time 2019年6月10日
 */
public interface StorageService {
	/**
	 * 修改
	 * 
	 * @param storageId
	 * @return
	 * @author sly
	 * @time 2019年6月10日
	 */
	Map<String, Object> update(String storageId);

}
