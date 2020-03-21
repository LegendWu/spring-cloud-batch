package com.spring.clould.batch.entity.base;

import java.io.Serializable;

import com.baomidou.mybatisplus.extension.activerecord.Model;

@SuppressWarnings({ "serial", "rawtypes" })
public class SuperEntity<T extends Model> extends Model<T> {

    private Long id;
    
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
