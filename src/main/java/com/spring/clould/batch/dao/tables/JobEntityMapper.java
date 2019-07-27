package com.spring.clould.batch.dao.tables;

import com.spring.clould.batch.model.tables.JobEntity;
import java.util.List;

public interface JobEntityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(JobEntity record);

    JobEntity selectByPrimaryKey(Integer id);

    List<JobEntity> selectAll();

    int updateByPrimaryKey(JobEntity record);
}