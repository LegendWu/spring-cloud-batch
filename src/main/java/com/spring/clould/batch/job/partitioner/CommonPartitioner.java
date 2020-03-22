package com.spring.clould.batch.job.partitioner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.mapper.CatMapper;

@Component
public class CommonPartitioner implements Partitioner {

    private static final Logger log = LoggerFactory.getLogger(CommonPartitioner.class);
    
    @Autowired
    CatMapper catMapper;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
    	List<Cat> cats = catMapper.selectList(null);
    	System.out.println("cats size=========================================================="+cats.size());
    	
        log.info("partition  gridsize is " + gridSize);
        Map<String, ExecutionContext> result = new HashMap<>();
        int range = 3;
        int fromId = 1;
        int toId = range;
        for (int i = 1; i <= gridSize; i++) {
            ExecutionContext value = new ExecutionContext();

            log.info("\nStarting : Thread" + i);
            log.info("fromId : " + fromId);
            log.info("toId : " + toId);

            value.putInt("fromId", fromId);
            value.putInt("toId", toId);

            // give each thread a name, thread 1,2,3
            value.putString("name", "Thread" + i);

            result.put("partition" + i, value);

            fromId = toId + 1;
            toId += range;

        }
        return result;
    }
}