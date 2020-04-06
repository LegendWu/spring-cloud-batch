package com.spring.clould.batch.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.mapper.CatMapper;
import com.spring.clould.batch.service.ICatService;

/**
 * Description: 测试类数据库操作实现类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月5日  
 * @version 1.0
 */
@Service
public class CatService extends ServiceImpl<CatMapper, Cat> implements ICatService{

}
