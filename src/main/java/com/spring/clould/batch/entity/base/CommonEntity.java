package com.spring.clould.batch.entity.base;

import java.util.List;

public class CommonEntity<T> {
	
	private List<T> list;

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
	
}
