package com.spring.clould.batch.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * Description: 批量任务类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@SuppressWarnings("serial")
public class BatchJobConfig extends Model<BatchJobConfig> {

	private int id;
	
	private String confCode;

    private String confValue;

    private String confDesc;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getConfCode() {
		return confCode;
	}

	public void setConfCode(String confCode) {
		this.confCode = confCode;
	}

	public String getConfValue() {
		return confValue;
	}

	public void setConfValue(String confValue) {
		this.confValue = confValue;
	}

	public String getConfDesc() {
		return confDesc;
	}

	public void setConfDesc(String confDesc) {
		this.confDesc = confDesc;
	}
    
}