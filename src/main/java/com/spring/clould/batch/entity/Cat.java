package com.spring.clould.batch.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * Description: 测试类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Cat extends Model<Cat> {
	
	private int id;

    private String catname;

    private String catage;

    private String cataddress;

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getCatage() {
        return catage;
    }

    public void setCatage(String catage) {
        this.catage = catage;
    }

    public String getCataddress() {
        return cataddress;
    }

    public void setCataddress(String cataddress) {
        this.cataddress = cataddress;
    }
}