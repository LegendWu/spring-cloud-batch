package com.spring.clould.batch.entity;

import org.springframework.batch.core.BatchStatus;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.spring.clould.batch.entity.enums.BatchJobStatusEnum;
import com.spring.clould.batch.entity.enums.YesOrNoEnum;

/**
 * Description: 批量任务类
 * Copyright: Copyright (c) 2020
 * @author zhangcuiwu  
 * @date 2020年4月4日  
 * @version 1.0
 */
@SuppressWarnings("serial")
public class BatchJob extends Model<BatchJob> {

	private int id;
	
	private String jobName;

    private String jobGroup;

    private String jobInstanceId;

    private String cron;
    
    private String cronTemplate;

    private String description;

    private BatchJobStatusEnum status;
    
    private YesOrNoEnum isMultiRun;
    
    private int maxRetryTimes;
    
    private int retryTimes;
    
    private int retryAfterFailed;
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName == null ? null : jobName.trim();
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup == null ? null : jobGroup.trim();
    }

    public String getJobInstanceId() {
        return jobInstanceId;
    }

    public void setJobInstanceId(String jobInstanceId) {
        this.jobInstanceId = jobInstanceId == null ? null : jobInstanceId.trim();
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron == null ? null : cron.trim();
    }
    
    public String getCronTemplate() {
		return cronTemplate;
	}

	public void setCronTemplate(String cronTemplate) {
		this.cronTemplate = cronTemplate;
	}

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public BatchJobStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BatchJobStatusEnum status) {
        this.status = status;
    }

	public YesOrNoEnum getIsMultiRun() {
		return isMultiRun;
	}

	public void setIsMultiRun(YesOrNoEnum isMultiRun) {
		this.isMultiRun = isMultiRun;
	}
	
	public int getMaxRetryTimes() {
		return maxRetryTimes;
	}

	public void setMaxRetryTimes(int maxRetryTimes) {
		this.maxRetryTimes = maxRetryTimes;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public int getRetryAfterFailed() {
		return retryAfterFailed;
	}

	public void setRetryAfterFailed(int retryAfterFailed) {
		this.retryAfterFailed = retryAfterFailed;
	}

	public void convertStatus(BatchStatus status) {
		switch (status) {
		case COMPLETED:
			setStatus(BatchJobStatusEnum.COMPLETED);
			break;
		case STARTING:
			setStatus(BatchJobStatusEnum.STARTING);
			break;
		case STARTED:
			setStatus(BatchJobStatusEnum.STARTED);
			break;
		case STOPPING:
			setStatus(BatchJobStatusEnum.STOPPING);
			break;
		case STOPPED:
			setStatus(BatchJobStatusEnum.STOPPED);
			break;
		case FAILED:
			setStatus(BatchJobStatusEnum.FAILED);
			break;
		case ABANDONED:
			setStatus(BatchJobStatusEnum.ABANDONED);
			break;
		case UNKNOWN:
			setStatus(BatchJobStatusEnum.UNKNOWN);
			break;
		default:
			setStatus(BatchJobStatusEnum.UNKNOWN);
			break;
		}
	}
    
}