package com.spring.clould.batch.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeparateUtil {
	
	/**
	 * 将一个list均分成n个list,主要通过偏移量来实现的
	 * @param source
	 * @return
	 */
	public static <T> List<List<T>> separateList(List<T> source, int n) {
		List<List<T>> result = new ArrayList<>();
		// (先计算出余数)
		int remaider = source.size() % n;
		// 然后是商
		int number = source.size() / n;
		// 偏移量
		int offset = 0;
		for (int i = 0; i < n; i++) {
			List<T> value;
			if (remaider > 0) {
				value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
				remaider--;
				offset++;
			} else {
				value = source.subList(i * number + offset, (i + 1) * number + offset);
			}
			result.add(value);
		}
		return result;
	}
	
	/**
	 * 将一个list均分成n个map区间,主要通过偏移量来实现的
	 * @param source
	 * @return
	 */
	public static <T> List<Map<String, T>> separateListRange(List<T> source, int n) {
		List<Map<String, T>> result = new ArrayList<>();
		// (先计算出余数)
		int remaider = source.size() % n;
		// 然后是商
		int number = source.size() / n;
		// 偏移量
		int offset = 0;
		for (int i = 0; i < n; i++) {
			Map<String, T> value = new HashMap<String, T>();
			T fromId;
			T toId;
			if (remaider > 0) {
				fromId = source.get(i * number + offset);
				toId = source.get((i + 1) * number + offset);
				remaider--;
				offset++;
			} else {
				fromId = source.get(i * number + offset);
				toId = source.get((i + 1) * number + offset -1);
			}
			value.put("fromId", fromId);
			value.put("toId", toId);
			result.add(value);
		}
		return result;
	}
}
