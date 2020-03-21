package com.spring.clould.batch.entity;

import com.spring.clould.batch.entity.base.SuperEntity;
import com.spring.clould.batch.entity.enums.BhJobStatusEnum;

/**
 *批量任务表
 */
@SuppressWarnings("serial")
public class BhJob extends SuperEntity<BhJob> {

	private String jobName;

    private String jobGroup;

    private String jobInstanceId;

    private String cron;

    private String description;

    private BhJobStatusEnum status;

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
}