package com.spring.clould.batch.entity.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Description: 是否枚举
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum YesOrNoEnum implements IEnum<String> {
	YES("YES", "是"),
	NO("NO", "否");

    private String value;
    private String desc;

    YesOrNoEnum(final String value, final String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public String getDesc(){
        return this.desc;
    }
}
