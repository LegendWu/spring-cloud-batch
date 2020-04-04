package com.spring.clould.batch.entity.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 *批量任务状态枚举
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BhJobStatusEnum  implements IEnum<String> {
	COMPLETED("COMPLETED", "执行完成"),
	STARTING("STARTING", "正在执行"),
	STARTED("STARTED", "可执行的"),
	STOPPING("RUNNING", "正在停止"),
	STOPPED("STOPPED", "已经停止"),
	FAILED("FAILED", "执行失败"),
	ABANDONED("ABANDONED", "禁止执行"),
	UNKNOWN("UNKNOWN", "未知状态");

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
