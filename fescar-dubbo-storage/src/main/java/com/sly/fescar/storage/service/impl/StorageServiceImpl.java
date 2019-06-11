package com.sly.fescar.storage.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.sly.fescar.common.model.storage.Storage;
import com.sly.fescar.storage.mapper.StorageMapper;
import com.sly.fescar.storage.service.StorageService;

/**
 * 仓储service实现
 * 
 * @author sly
 * @time 2019年6月10日
 */
@Service
public class StorageServiceImpl implements StorageService {
	
	@Autowired
	private StorageMapper storageMapper;
	
	/**
	 * 新增
	 * 
	 * @param storage
	 * @return
	 * @author sly
	 * @time 2019年6月11日
	 */
	@Override
	public Map<String, Object> insert(Storage storage) {
		storageMapper.insert(storage);
		Map<String, Object> result = new HashMap<>(16);
		result.put("status", 200);
		result.put("message", "新增成功！");
		return result;
	}

}
