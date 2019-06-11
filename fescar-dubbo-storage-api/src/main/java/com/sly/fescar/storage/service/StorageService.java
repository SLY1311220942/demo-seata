package com.sly.fescar.storage.service;

import java.util.Map;

import com.sly.fescar.common.model.storage.Storage;

/**
 * 仓储service
 * 
 * @author sly
 * @time 2019年6月10日
 */
public interface StorageService {

	/**
	 * 新增
	 * 
	 * @param storage
	 * @return
	 * @author sly
	 * @time 2019年6月11日
	 */
	Map<String, Object> insert(Storage storage);

}
