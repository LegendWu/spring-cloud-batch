package com.spring.clould.batch.entity.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 *批量任务状态枚举
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
