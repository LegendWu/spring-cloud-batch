package com.spring.clould.batch.entity.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 *批量任务状态枚举
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BhJobStatusEnum implements IEnum<String> {
	RUNABLE("RUNABLE", "可执行的"),
	RUNNING("RUNNING", "正在执行"),
	WAITING("WAITING", "等待执行"),
	COMPLETED("COMPLETED", "执行完成"),
	STOPPED("STOPPED", "停止执行"),
    FAILED("FAILED", "执行失败");

    private String value;
    private String desc;

    BhJobStatusEnum(final String value, final String desc) {
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
