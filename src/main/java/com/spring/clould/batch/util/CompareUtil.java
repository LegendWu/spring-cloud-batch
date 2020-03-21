package com.spring.clould.batch.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompareUtil {

	/**
	 * 比较两个map是否一致
	 * 
	 * @param map1
	 * @param map2
	 * @param excludeFields
	 * @return
	 */
	public static boolean isMapDifferent(Map<String, Object> map1, Map<String, Object> map2, List<String> excludeFields) {
		Set<String> keys = map1.keySet();
		for (String key : keys) {
			// 只比较字符串类型的字段数据
			if (!excludeFields.contains(key) 
					&& map1.get(key) instanceof String 
					&& map2.get(key) instanceof String
					&& !map2.get(key).equals(map1.get(key))) {
				return true;
			}
		}
		return false;
	}
}
