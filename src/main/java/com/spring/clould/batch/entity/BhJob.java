package com.spring.clould.batch.entity;

import org.springframework.batch.core.BatchStatus;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.spring.clould.batch.entity.enums.BhJobStatusEnum;
import com.spring.clould.batch.entity.enums.YesOrNoEnum;

/**
 *批量任务表
 * @param <T>
 */
@SuppressWarnings("serial")
public class BhJob extends Model<BhJob> {

	private int id;
	
	private String jobName;

    private String jobGroup;

    private String jobInstanceId;

    private String cron;

    private String description;

    private BhJobStatusEnum status;
    
    private YesOrNoEnum isMultiRun;
    
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public BhJobStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BhJobStatusEnum status) {
        this.status = status;
    }

	public YesOrNoEnum getIsMultiRun() {
		return isMultiRun;
	}

	public void setIsMultiRun(YesOrNoEnum isMultiRun) {
		this.isMultiRun = isMultiRun;
	}
	
	public void convertStatus(BatchStatus status) {
		switch (status) {
		case COMPLETED:
			setStatus(BhJobStatusEnum.COMPLETED);
			break;
		case STARTING:
			setStatus(BhJobStatusEnum.STARTING);
			break;
		case STARTED:
			setStatus(BhJobStatusEnum.STARTED);
			break;
		case STOPPING:
			setStatus(BhJobStatusEnum.STOPPING);
			break;
		case STOPPED:
			setStatus(BhJobStatusEnum.STOPPED);
			break;
		case FAILED:
			setStatus(BhJobStatusEnum.FAILED);
			break;
		case ABANDONED:
			setStatus(BhJobStatusEnum.ABANDONED);
			break;
		case UNKNOWN:
			setStatus(BhJobStatusEnum.UNKNOWN);
			break;
		default:
			setStatus(BhJobStatusEnum.UNKNOWN);
			break;
		}
	}
    
}