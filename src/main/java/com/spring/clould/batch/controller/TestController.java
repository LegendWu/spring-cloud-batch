package com.spring.clould.batch.controller;

import java.util.Date;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.clould.batch.entity.BatchJob;
import com.spring.clould.batch.entity.Cat;
import com.spring.clould.batch.entity.enums.BatchJobStatusEnum;
import com.spring.clould.batch.entity.enums.YesOrNoEnum;
import com.spring.clould.batch.mapper.BatchJobMapper;
import com.spring.clould.batch.mapper.CatMapper;
import com.spring.clould.batch.util.BeanUtil;
import com.spring.clould.batch.util.DateUtil;
import com.spring.clould.batch.util.RedisLockUtil;

/**
 * Description: 测试controller
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@RestController
public class TestController {

	@Autowired
	CatMapper catMapper;
	
	@Autowired
	BatchJobMapper batchJobMapper;
	
	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	RedisLockUtil redisLockUtil;

//	@PostConstruct
	public void initCat() {
		for (int i = 0; i < 10080; i++) {
			Cat cat = new Cat();
			cat.setCatname("cat" + i);
			cat.setCatage(i + "");
			cat.setCataddress("cat address " + i);
			catMapper.insert(cat);
			if (i % 10000 == 0) {
				System.out.println(i);
			}
		}
	}

	@GetMapping("deleteLock")
	public void deleteRedisLock(@RequestParam String key) {
		redisLockUtil.deleleJobLock(key);
		redisLockUtil.deleleJobRetryLock(key);
	}
	
	@GetMapping("runJob")
	public void runJob(@RequestParam Integer id){
		BatchJob job = batchJobMapper.selectById(id);
		// 获取分布式锁
		boolean isLock = redisLockUtil.lockJob(job.getJobName());
		if (isLock) {
			//重新获取一下数据库里的job信息
			job = batchJobMapper.selectById(job.getId());
			//设置任务执行参数
			JobParameters jobParameters = null;
			String jobInstanceId = job.getJobInstanceId();
			//如果任务状态为失败或者正在执行则获取上次执行的实例ID作为参数进行续跑
			if (BatchJobStatusEnum.FAILED.equals(job.getStatus()) 
					|| BatchJobStatusEnum.STARTING.equals(job.getStatus())
					 || BatchJobStatusEnum.UNKNOWN.equals(job.getStatus())) {
				jobParameters = new JobParametersBuilder().addString(jobInstanceId, "datetime").toJobParameters();
			} else {
				if(YesOrNoEnum.YES.equals(job.getIsMultiRun())) {
					//当天可以多次执行
					jobInstanceId = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_TIME_FORMAT_YYYYMMDDHHMISSSSS);
				}else {
					//当天只允许执行一次
					jobInstanceId = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_FORMAT_YYYYMMDD);
				}
				jobParameters = new JobParametersBuilder().addString(jobInstanceId, "datetime").toJobParameters();
			}
			//更新job实例ID及状态
			job.setJobInstanceId(jobInstanceId);
			job.setStatus(BatchJobStatusEnum.STARTING);
			batchJobMapper.updateJobStatusOrInstanceId(job);
			try {
				//执行批量任务
				JobExecution result = jobLauncher.run((org.springframework.batch.core.Job) BeanUtil.getContext().getBean(job.getJobName()), jobParameters);
				if(null != result) {
					//更新任务状态
					job.convertStatus(result.getStatus());
					batchJobMapper.updateJobStatusOrInstanceId(job);
					//任务成功结束删除分布式锁
					if(result.getStatus().equals(BatchStatus.COMPLETED)) {
						redisLockUtil.deleleJobLock(job.getJobName());
					}
				}
			} catch (JobInstanceAlreadyCompleteException e) {
				redisLockUtil.deleleJobLock(job.getJobName());
			} catch (BeansException | JobExecutionAlreadyRunningException | JobRestartException | JobParametersInvalidException e) {
				job.convertStatus(BatchStatus.FAILED);
				batchJobMapper.updateJobStatusOrInstanceId(job);
			} finally {
				redisLockUtil.deleleJobLock(job.getJobName());
				//删除重试锁
				redisLockUtil.deleleJobRetryLock(job.getJobName());
			}
		} else {
			
		}
	}

}
