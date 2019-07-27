package com.spring.clould.batch.dao.tables;

import com.spring.clould.batch.model.tables.JobEntity;
import java.util.List;

public interface JobEntityMapper {
    int insert(JobEntity record);

    List<JobEntity> selectAll();
}