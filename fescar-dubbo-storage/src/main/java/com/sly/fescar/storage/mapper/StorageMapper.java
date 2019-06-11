package com.sly.fescar.storage.mapper;

import com.sly.fescar.common.model.storage.Storage;

/**
 * 仓储mapper
 * 
 * @author sly
 * @time 2019年6月11日
 */
public interface StorageMapper {
	/**
	 * 新增
	 * 
	 * @param storage
	 * @return
	 * @author sly
	 * @time 2019年6月11日
	 */
	int insert(Storage storage);
}
